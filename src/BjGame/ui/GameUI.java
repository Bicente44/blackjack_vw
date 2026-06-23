package BjGame.ui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * Full blackjack game table.
 *
 * Phase flow:
 *   showIntermission(seconds)           → countdown banner, all seats in bet mode (dimmed)
 *   startPlayerBetTurn(index, seconds)  → activates one seat for betting + its timer
 *   showPlayMode()                      → hides intermission, shows action bar, seats show card rows
 *
 * Dealer and player seats are driven via:
 *   getDealer()     → DealerArea
 *   getSeats()      → List<PlayerSeat>
 */
public class GameUI {

    // ── Intermission timer constants ──────────────────────────────────────────
    private static final double INTER_TIMER_R   = 22;
    private static final double INTER_TIMER_PAD = 4;
    private static final double INTER_TIMER_SIZE = (INTER_TIMER_R + INTER_TIMER_PAD) * 2;

    private final DealerArea       dealer = new DealerArea();
    private final List<PlayerSeat> seats  = new ArrayList<>();

    // ── Action buttons ────────────────────────────────────────────────────────
    private Button hitBtn;
    private Button standBtn;
    private Button doubleBtn;
    private Button splitBtn;

    // ── Intermission banner nodes ─────────────────────────────────────────────
    private Region    intermissionBanner;
    private Arc       interTimerArc;
    private Text      interTimerText;
    private Timeline  interTimeline;
    private Label     intermissionLabel;

    // ── Swappable bottom zone: intermission banner ↔ action bar ──────────────
    private StackPane bottomZone;

    // ── Callbacks ─────────────────────────────────────────────────────────────
    private Runnable onHit    = () -> {};
    private Runnable onStand  = () -> {};
    private Runnable onDouble = () -> {};
    private Runnable onSplit  = () -> {};
    private Runnable onHelp   = () -> {};

    public void setOnHit(Runnable r)    { onHit    = r; }
    public void setOnStand(Runnable r)  { onStand  = r; }
    public void setOnDouble(Runnable r) { onDouble = r; }
    public void setOnSplit(Runnable r)  { onSplit  = r; }
    public void setOnHelp(Runnable r)   { onHelp   = r; }

    // ── Seat management ───────────────────────────────────────────────────────

    public PlayerSeat addSeat(String name, boolean isLocal, double money) {
        PlayerSeat seat = new PlayerSeat(name, isLocal);
        seat.setPlayerMoney(money);
        seats.add(seat);
        return seat;
    }

    public DealerArea       getDealer() { return dealer; }
    public List<PlayerSeat> getSeats()  { return seats; }

    // ── Action bar control ────────────────────────────────────────────────────

    public void setActionsEnabled(boolean enabled) {
        hitBtn.setDisable(!enabled);
        standBtn.setDisable(!enabled);
        doubleBtn.setDisable(!enabled);
        splitBtn.setDisable(!enabled);
    }

    public void setSplitEnabled(boolean enabled)  { splitBtn.setDisable(!enabled); }
    public void setDoubleEnabled(boolean enabled) { doubleBtn.setDisable(!enabled); }

    // ── Phase control ─────────────────────────────────────────────────────────

    /**
     * Show the intermission banner with a countdown timer in the bottom zone.
     * All seats are put into bet mode (dimmed — not yet anyone's turn).
     * Your loop then calls startPlayerBetTurn() one by one.
     *
     * @param seconds total intermission window before auto-advancing
     * @param onEnd   called when the timer reaches zero (use to force-start round)
     */
    public void showIntermission(int seconds, Runnable onEnd) {
        // All seats → bet mode, dimmed
        for (PlayerSeat seat : seats) {
            seat.showBetMode(false);
        }

        intermissionLabel.setText("PLACE YOUR BETS");
        interTimerArc.setLength(360);
        interTimerText.setText(String.valueOf(seconds));

        bottomZone.getChildren().setAll(intermissionBanner);

        if (interTimeline != null) interTimeline.stop();

        interTimeline = new Timeline(
                new KeyFrame(Duration.seconds(seconds),
                        new KeyValue(interTimerArc.lengthProperty(), 0))
        );
        interTimeline.setOnFinished(e -> onEnd.run());

        Timeline textTicker = new Timeline();
        for (int i = 0; i <= seconds; i++) {
            final int remaining = seconds - i;
            textTicker.getKeyFrames().add(
                    new KeyFrame(Duration.seconds(i),
                            e -> interTimerText.setText(String.valueOf(remaining)))
            );
        }
        interTimeline.play();
        textTicker.play();
    }

    /**
     * Activate one seat for betting — called in sequence by your bet loop.
     * The seat's timer fires onBetConfirmed when the player confirms or time runs out.
     *
     * @param seatIndex index into getSeats()
     * @param seconds   how long this player has to bet
     */
    public void startPlayerBetTurn(int seatIndex, int seconds) {
        // De-activate all, then activate the chosen one
        for (int i = 0; i < seats.size(); i++) {
            seats.get(i).showBetMode(i == seatIndex);
        }
        seats.get(seatIndex).startTimer(seconds, true);
    }

    /**
     * End intermission — hides banner, restores action bar, all seats flip to card/play mode.
     */
    public void showPlayMode() {
        if (interTimeline != null) { interTimeline.stop(); interTimeline = null; }
        for (PlayerSeat seat : seats) {
            seat.showPlayMode();
        }
        setActionsEnabled(false);
        bottomZone.getChildren().setAll(buildActionBar());
    }

    // ── Build ─────────────────────────────────────────────────────────────────

    public Region build() {
        StackPane root = new StackPane();
        root.getStyleClass().add("game-table");

        // Pre-build action bar and intermission banner so swaps are instant
        Region actionBar = buildActionBar();
        intermissionBanner = buildIntermissionBanner();

        bottomZone = new StackPane(actionBar);
        bottomZone.setAlignment(Pos.CENTER);

        VBox layout = new VBox(0,
                buildHeader(),
                dealer.build(),
                buildFelt(),
                buildSeatRow(),
                bottomZone
        );
        layout.setAlignment(Pos.CENTER);

        root.getChildren().add(layout);
        return root;
    }

    // ── Layout sections ───────────────────────────────────────────────────────

    private Region buildHeader() {
        Text suits = new Text("♠   ♥   ♦   ♣");
        suits.getStyleClass().add("table-suits");
        HBox h = new HBox(suits);
        h.setAlignment(Pos.CENTER);
        h.getStyleClass().add("table-header");
        h.setPadding(new Insets(10, 0, 6, 0));
        return h;
    }

    private Region buildFelt() {
        Region felt = new Region();
        VBox.setVgrow(felt, Priority.ALWAYS);
        return felt;
    }

    private Region buildSeatRow() {
        HBox row = new HBox(12);
        row.setAlignment(Pos.BOTTOM_CENTER);
        row.setPadding(new Insets(0, 24, 4, 24));
        for (PlayerSeat seat : seats) {
            Region r = seat.build();
            HBox.setHgrow(r, Priority.NEVER);
            row.getChildren().add(r);
        }
        return row;
    }

    // ── Intermission banner ───────────────────────────────────────────────────

    private Region buildIntermissionBanner() {
        intermissionLabel = new Label("PLACE YOUR BETS");
        intermissionLabel.getStyleClass().add("intermission-label");

        // Timer ring
        double cx = INTER_TIMER_R + INTER_TIMER_PAD;
        double cy = INTER_TIMER_R + INTER_TIMER_PAD;

        Arc bgArc = new Arc(cx, cy, INTER_TIMER_R, INTER_TIMER_R, 0, 360);
        bgArc.setType(ArcType.OPEN);
        bgArc.setFill(Color.TRANSPARENT);
        bgArc.setStroke(Color.web("#1a2e44"));
        bgArc.setStrokeWidth(3);

        interTimerArc = new Arc(cx, cy, INTER_TIMER_R, INTER_TIMER_R, 90, 360);
        interTimerArc.setType(ArcType.OPEN);
        interTimerArc.setFill(Color.TRANSPARENT);
        interTimerArc.setStroke(Color.web("#D4AF37"));
        interTimerArc.setStrokeWidth(3);
        interTimerArc.setStrokeLineCap(StrokeLineCap.ROUND);

        Pane arcPane = new Pane(bgArc, interTimerArc);
        arcPane.setPrefSize(INTER_TIMER_SIZE, INTER_TIMER_SIZE);
        arcPane.setMinSize(INTER_TIMER_SIZE, INTER_TIMER_SIZE);
        arcPane.setMaxSize(INTER_TIMER_SIZE, INTER_TIMER_SIZE);

        interTimerText = new Text("30");
        interTimerText.getStyleClass().add("timer-text");

        StackPane ring = new StackPane(arcPane, interTimerText);
        ring.setPrefSize(INTER_TIMER_SIZE, INTER_TIMER_SIZE);
        ring.setMinSize(INTER_TIMER_SIZE, INTER_TIMER_SIZE);
        ring.setMaxSize(INTER_TIMER_SIZE, INTER_TIMER_SIZE);

        HBox banner = new HBox(16, ring, intermissionLabel);
        banner.setAlignment(Pos.CENTER_LEFT);
        banner.getStyleClass().add("intermission-bar");
        banner.setPadding(new Insets(12, 24, 16, 24));
        return banner;
    }

    // ── Action bar ────────────────────────────────────────────────────────────

    private Region buildActionBar() {
        hitBtn    = btn("HIT",    "action-btn-primary");
        standBtn  = btn("STAND",  "action-btn-primary");
        doubleBtn = btn("DOUBLE", "action-btn-secondary");
        splitBtn  = btn("SPLIT",  "action-btn-secondary");
        Button helpBtn = btn("?", "action-btn-help");

        hitBtn.setOnAction(e    -> onHit.run());
        standBtn.setOnAction(e  -> onStand.run());
        doubleBtn.setOnAction(e -> onDouble.run());
        splitBtn.setOnAction(e  -> onSplit.run());
        helpBtn.setOnAction(e   -> onHelp.run());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bar = new HBox(10, hitBtn, standBtn, doubleBtn, splitBtn, spacer, helpBtn);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.getStyleClass().add("action-bar");
        bar.setPadding(new Insets(12, 24, 16, 24));

        setActionsEnabled(false);
        return bar;
    }

    private Button btn(String label, String styleClass) {
        Button b = new Button(label);
        b.getStyleClass().add("action-btn");
        b.getStyleClass().add(styleClass);
        return b;
    }
}