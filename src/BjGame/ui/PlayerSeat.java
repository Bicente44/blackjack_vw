package BjGame.ui;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * One player's seat at the table.
 *
 * ── Bet phase ────────────────────────────────────────────────────────────────
 *   showBetMode(true)    active player   → chip +/- controls, timer running
 *   showBetMode(false)   others          → "Waiting…" text, no controls
 *   getCurrentBet()      read anytime    → 0 if player passed / no money
 *   confirmBet()         backend call    → fires onBetConfirmed early
 *   setOnBetConfirmed()  wire before startTimer → called on timer expiry
 *
 * ── Play phase ───────────────────────────────────────────────────────────────
 *   showPlayMode()
 *   addCard / addHiddenCard / revealCard
 *   setActive / startTimer(seconds, false) / stopTimer
 *   setTotal / setBet / showNotification / clearHand
 */
public class PlayerSeat {

    private static final int[]  ADD_CHIPS    = {5, 10, 25, 50, 100};
    private static final int[]  REMOVE_CHIPS = {5, 10, 25};
    private static final double TIMER_R      = 18;
    private static final double TIMER_PAD    = 4;
    private static final double TIMER_SIZE   = (TIMER_R + TIMER_PAD) * 2;

    private final String  playerName;
    private final boolean isLocal;
    private double        playerMoney = 500;

    // ── Bet state ─────────────────────────────────────────────────────────────
    private int      currentBet       = 0;
    private Runnable onBetConfirmed   = () -> {};

    // ── Node refs ─────────────────────────────────────────────────────────────
    private StackPane swapZone;
    private Label     betDisplayLabel;
    private HBox      cardRow;
    private Label     totalLabel;
    private Label     betLabel;
    private Label     notificationLabel;
    private StackPane timerRing;
    private Arc       timerArc;
    private Text      timerText;
    private VBox      seatBox;
    private Timeline  activeTimer;

    public PlayerSeat(String playerName, boolean isLocal) {
        this.playerName = playerName;
        this.isLocal    = isLocal;
    }

    public void setPlayerMoney(double money) { this.playerMoney = money; }

    // ── Build ─────────────────────────────────────────────────────────────────

    public Region build() {
        notificationLabel = buildNotificationLabel();

        swapZone = new StackPane();
        swapZone.setMinHeight(CardUI.H + 10);
        swapZone.setAlignment(Pos.CENTER);
        swapZone.getChildren().setAll(buildCardRow());

        timerRing = buildTimerRing();

        Label nameLabel = new Label(playerName);
        nameLabel.getStyleClass().add(isLocal ? "seat-name-local" : "seat-name");

        totalLabel = new Label("");
        totalLabel.getStyleClass().add("total-badge");
        totalLabel.setVisible(false);

        betLabel = new Label("$0");
        betLabel.getStyleClass().add("seat-bet");

        HBox infoRow = new HBox(8, totalLabel, betLabel);
        infoRow.setAlignment(Pos.CENTER);

        seatBox = new VBox(6,
                notificationLabel,
                swapZone,
                nameLabel,
                infoRow,
                timerRing
        );
        seatBox.setAlignment(Pos.CENTER);
        seatBox.getStyleClass().add("player-seat");
        seatBox.setPadding(new Insets(10, 14, 10, 14));

        return seatBox;
    }

    // ── Mode switching ────────────────────────────────────────────────────────

    /**
     * Switch to bet phase.
     *
     * @param isActive true  = this player's turn — shows chip controls + timer
     *                 false = waiting for their turn — shows status text only
     */
    public void showBetMode(boolean isActive) {
        currentBet = 0;
        seatBox.getStyleClass().remove("player-seat-active");

        if (isActive) {
            swapZone.getChildren().setAll(buildActiveBetWidget());
            swapZone.setOpacity(1.0);
            seatBox.getStyleClass().add("player-seat-active");
        } else {
            swapZone.getChildren().setAll(buildWaitingWidget());
            swapZone.setOpacity(1.0);
        }
    }

    /** Switch back to play phase — restores card row. */
    public void showPlayMode() {
        cardRow = buildCardRow();
        swapZone.getChildren().setAll(cardRow);
        swapZone.setOpacity(1.0);
        seatBox.getStyleClass().remove("player-seat-active");
        stopTimer();
    }

    // ── Bet widgets ───────────────────────────────────────────────────────────

    /** Full interactive chip controls — only shown to the active better. */
    private Region buildActiveBetWidget() {
        betDisplayLabel = new Label("$0");
        betDisplayLabel.getStyleClass().add("bet-amount");

        // Balance hint
        Label balanceHint = new Label("of $" + (int) playerMoney);
        balanceHint.getStyleClass().add("bet-balance-hint");

        HBox amountRow = new HBox(6, betDisplayLabel, balanceHint);
        amountRow.setAlignment(Pos.CENTER);

        // Add chips row
        HBox addRow = new HBox(4);
        addRow.setAlignment(Pos.CENTER);
        for (int v : ADD_CHIPS) {
            addRow.getChildren().add(chipBtn("+" + v, v, true));
        }

        // Remove chips row
        HBox removeRow = new HBox(4);
        removeRow.setAlignment(Pos.CENTER);
        for (int v : REMOVE_CHIPS) {
            removeRow.getChildren().add(chipBtn("-" + v, -v, false));
        }

        Button clearBtn = new Button("CLEAR");
        clearBtn.getStyleClass().add("bet-clear-btn");
        clearBtn.setOnAction(e -> applyBetDelta(-currentBet));

        VBox widget = new VBox(6, amountRow, addRow, removeRow, clearBtn);
        widget.setAlignment(Pos.CENTER);
        widget.setPadding(new Insets(4, 0, 4, 0));
        return widget;
    }

    /**
     * Shown on all non-active seats during the bet phase.
     * Once a player's bet is confirmed their amount is shown here; before it's their
     * turn they see "Waiting…".
     */
    private Region buildWaitingWidget() {
        Label waiting = new Label("Waiting…");
        waiting.getStyleClass().add("seat-waiting-label");

        VBox box = new VBox(waiting);
        box.setAlignment(Pos.CENTER);
        box.setMinHeight(CardUI.H + 10);
        return box;
    }

    /**
     * Called on a seat after its bet is locked in — replaces "Waiting…"
     * with the confirmed amount so the table can see everyone's bet at a glance.
     */
    public void showConfirmedBet(int amount) {
        Label confirmed = new Label("$" + amount);
        confirmed.getStyleClass().add("seat-confirmed-bet");

        Label sub = new Label("bet placed");
        sub.getStyleClass().add("seat-waiting-label");

        VBox box = new VBox(4, confirmed, sub);
        box.setAlignment(Pos.CENTER);
        box.setMinHeight(CardUI.H + 10);
        swapZone.getChildren().setAll(box);
    }

    // ── Chip logic ────────────────────────────────────────────────────────────

    private Button chipBtn(String label, int delta, boolean isAdd) {
        Button b = new Button(label);
        b.getStyleClass().add(isAdd ? "chip-btn-add" : "chip-btn-remove");
        b.setOnAction(e -> applyBetDelta(delta));
        return b;
    }

    private void applyBetDelta(int delta) {
        int max    = (int) playerMoney;
        currentBet = Math.max(0, Math.min(currentBet + delta, max));
        if (betDisplayLabel != null) betDisplayLabel.setText("$" + currentBet);
    }

    // ── Public bet API ────────────────────────────────────────────────────────

    /** Current bet amount. Returns 0 if player cleared or never bet. */
    public int getCurrentBet() { return currentBet; }

    /**
     * Wire before calling startTimer so the controller knows when to advance.
     * Fires on: timer expiry (auto) or confirmBet() (manual early advance).
     */
    public void setOnBetConfirmed(Runnable r) { onBetConfirmed = r; }

    /**
     * Manually fire the bet confirmation — call from controller to force-advance
     * (e.g. host skips remaining time, or intermission timer hits zero globally).
     */
    public void confirmBet() { onBetConfirmed.run(); }

    // ── Play phase public API ─────────────────────────────────────────────────

    public void addCard(String rank, String suit) {
        if (cardRow != null)
            cardRow.getChildren().add(new CardUI(rank, suit).build());
    }

    public void addHiddenCard() {
        if (cardRow != null)
            cardRow.getChildren().add(new CardUI("", "").faceDown().build());
    }

    public void revealCard(int index, String rank, String suit) {
        if (cardRow != null && index >= 0 && index < cardRow.getChildren().size())
            cardRow.getChildren().set(index, new CardUI(rank, suit).build());
    }

    public void setTotal(int total, boolean busted) {
        totalLabel.setText(String.valueOf(total));
        totalLabel.setVisible(true);
        totalLabel.getStyleClass().removeAll("total-badge-bust", "total-badge-blackjack");
        if (busted)           totalLabel.getStyleClass().add("total-badge-bust");
        else if (total == 21) totalLabel.getStyleClass().add("total-badge-blackjack");
    }

    public void setBet(int amount) { betLabel.setText("$" + amount); }

    public void setActive(boolean active) {
        if (active) {
            seatBox.getStyleClass().add("player-seat-active");
        } else {
            seatBox.getStyleClass().remove("player-seat-active");
            stopTimer();
        }
        timerRing.setVisible(active);
        timerRing.setManaged(active);
    }

    /**
     * @param seconds       countdown duration
     * @param fireCallback  true during bet phase (timer expiry confirms bet),
     *                      false during play phase (visual only)
     */
    public void startTimer(int seconds, boolean fireCallback) {
        stopTimer();
        timerRing.setVisible(true);
        timerRing.setManaged(true);
        timerArc.setLength(360);
        timerText.setText(String.valueOf(seconds));

        activeTimer = new Timeline(
                new KeyFrame(Duration.seconds(seconds),
                        new KeyValue(timerArc.lengthProperty(), 0))
        );
        if (fireCallback) activeTimer.setOnFinished(e -> onBetConfirmed.run());

        Timeline textTicker = new Timeline();
        for (int i = 0; i <= seconds; i++) {
            final int remaining = seconds - i;
            textTicker.getKeyFrames().add(
                    new KeyFrame(Duration.seconds(i),
                            e -> timerText.setText(String.valueOf(remaining)))
            );
        }
        activeTimer.play();
        textTicker.play();
    }

    public void stopTimer() {
        if (activeTimer != null) { activeTimer.stop(); activeTimer = null; }
        timerRing.setVisible(false);
        timerRing.setManaged(false);
    }

    public void showNotification(String message) {
        notificationLabel.getStyleClass().removeAll(
                "seat-notification-win", "seat-notification-bust",
                "seat-notification-blackjack", "seat-notification-push");
        switch (message.toUpperCase()) {
            case "BLACKJACK" -> notificationLabel.getStyleClass().add("seat-notification-blackjack");
            case "BUST"      -> notificationLabel.getStyleClass().add("seat-notification-bust");
            case "LOSE"      -> notificationLabel.getStyleClass().add("seat-notification-bust");
            case "WIN"       -> notificationLabel.getStyleClass().add("seat-notification-win");
            case "PUSH"      -> notificationLabel.getStyleClass().add("seat-notification-push");
        }
        notificationLabel.setText(message);
        notificationLabel.setOpacity(1.0);
        notificationLabel.setVisible(true);
        notificationLabel.setManaged(true);

        FadeTransition fade = new FadeTransition(Duration.seconds(1.2), notificationLabel);
        fade.setDelay(Duration.seconds(2.2));
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> {
            notificationLabel.setVisible(false);
            notificationLabel.setManaged(false);
        });
        fade.play();
    }

    public void clearHand() {
        if (cardRow != null) cardRow.getChildren().clear();
        totalLabel.setVisible(false);
        totalLabel.getStyleClass().removeAll("total-badge-bust", "total-badge-blackjack");
        currentBet = 0;
        stopTimer();
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private HBox buildCardRow() {
        HBox row = new HBox(6);
        row.setAlignment(Pos.BOTTOM_CENTER);
        row.setMinHeight(CardUI.H + 10);
        cardRow = row;
        return row;
    }

    private Label buildNotificationLabel() {
        Label lbl = new Label("");
        lbl.getStyleClass().add("seat-notification");
        lbl.setVisible(false);
        lbl.setManaged(false);
        return lbl;
    }

    private StackPane buildTimerRing() {
        double cx = TIMER_R + TIMER_PAD;
        double cy = TIMER_R + TIMER_PAD;

        Arc bgArc = new Arc(cx, cy, TIMER_R, TIMER_R, 0, 360);
        bgArc.setType(ArcType.OPEN);
        bgArc.setFill(Color.TRANSPARENT);
        bgArc.setStroke(Color.web("#1a2e44"));
        bgArc.setStrokeWidth(3);

        timerArc = new Arc(cx, cy, TIMER_R, TIMER_R, 90, 360);
        timerArc.setType(ArcType.OPEN);
        timerArc.setFill(Color.TRANSPARENT);
        timerArc.setStroke(Color.web("#D4AF37"));
        timerArc.setStrokeWidth(3);
        timerArc.setStrokeLineCap(StrokeLineCap.ROUND);

        Pane arcPane = new Pane(bgArc, timerArc);
        arcPane.setPrefSize(TIMER_SIZE, TIMER_SIZE);
        arcPane.setMinSize(TIMER_SIZE, TIMER_SIZE);
        arcPane.setMaxSize(TIMER_SIZE, TIMER_SIZE);

        timerText = new Text("30");
        timerText.getStyleClass().add("timer-text");

        StackPane ring = new StackPane(arcPane, timerText);
        ring.setPrefSize(TIMER_SIZE, TIMER_SIZE);
        ring.setMinSize(TIMER_SIZE, TIMER_SIZE);
        ring.setMaxSize(TIMER_SIZE, TIMER_SIZE);
        ring.setVisible(false);
        ring.setManaged(false);
        return ring;
    }
}