package BjGame.ui;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
 */
public class PlayerSeat {

    private static final double TIMER_R    = 18;
    private static final double TIMER_PAD  = 4;
    private static final double TIMER_SIZE = (TIMER_R + TIMER_PAD) * 2;

    private final String  playerName;
    private final boolean isLocal;

    // ── Live node references ──────────────────────────────────────────────────
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

    // ── Build ─────────────────────────────────────────────────────────────────

    public Region build() {
        notificationLabel = buildNotificationLabel();
        cardRow           = buildCardRow();
        timerRing         = buildTimerRing();

        Label nameLabel = new Label(playerName);
        nameLabel.getStyleClass().add(isLocal ? "seat-name-local" : "seat-name");

        totalLabel = new Label("0");
        totalLabel.getStyleClass().add("total-badge");
        totalLabel.setVisible(false);

        betLabel = new Label("$0");
        betLabel.getStyleClass().add("seat-bet");

        HBox infoRow = new HBox(8, totalLabel, betLabel);
        infoRow.setAlignment(Pos.CENTER);

        seatBox = new VBox(6,
                notificationLabel,
                cardRow,
                nameLabel,
                infoRow,
                timerRing
        );
        seatBox.setAlignment(Pos.CENTER);
        seatBox.getStyleClass().add("player-seat");
        seatBox.setPadding(new Insets(12, 16, 12, 16));

        return seatBox;
    }

    // ── Sub-builders ──────────────────────────────────────────────────────────

    private Label buildNotificationLabel() {
        Label lbl = new Label("");
        lbl.getStyleClass().add("seat-notification");
        lbl.setVisible(false);
        lbl.setManaged(false);
        return lbl;
    }

    private HBox buildCardRow() {
        HBox row = new HBox(6);
        row.setAlignment(Pos.BOTTOM_CENTER);
        row.setMinHeight(CardUI.H + 10);
        return row;
    }

    private StackPane buildTimerRing() {
        double cx = TIMER_R + TIMER_PAD;
        double cy = TIMER_R + TIMER_PAD;

        // Background ring
        Arc bgArc = new Arc(cx, cy, TIMER_R, TIMER_R, 0, 360);
        bgArc.setType(ArcType.OPEN);
        bgArc.setFill(Color.TRANSPARENT);
        bgArc.setStroke(Color.web("#1a2e44"));
        bgArc.setStrokeWidth(3);

        // Countdown arc (gold, animates from 360→0)
        timerArc = new Arc(cx, cy, TIMER_R, TIMER_R, 90, 360);
        timerArc.setType(ArcType.OPEN);
        timerArc.setFill(Color.TRANSPARENT);
        timerArc.setStroke(Color.web("#D4AF37"));
        timerArc.setStrokeWidth(3);
        timerArc.setStrokeLineCap(StrokeLineCap.ROUND);

        // Absolute-positioned pane so arc coordinates are correct
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

    // ── Public API ────────────────────────────────────────────────────────────

    /** Add a face-up card to this hand. */
    public void addCard(String rank, String suit) {
        cardRow.getChildren().add(new CardUI(rank, suit).build());
    }

    /** Add a face-down (hidden) card placeholder. */
    public void addHiddenCard() {
        cardRow.getChildren().add(new CardUI("", "").faceDown().build());
    }

    /** Replace the face-down card at the given index with a revealed card. */
    public void revealCard(int index, String rank, String suit) {
        if (index >= 0 && index < cardRow.getChildren().size()) {
            cardRow.getChildren().set(index, new CardUI(rank, suit).build());
        }
    }

    /** Update the hand total badge. Pass busted=true to colour it red. */
    public void setTotal(int total, boolean busted) {
        totalLabel.setText(String.valueOf(total));
        totalLabel.setVisible(true);
        totalLabel.getStyleClass().removeAll("total-badge-bust", "total-badge-blackjack");
        if (busted)       totalLabel.getStyleClass().add("total-badge-bust");
        else if (total == 21) totalLabel.getStyleClass().add("total-badge-blackjack");
    }

    /** Update the bet label. */
    public void setBet(int amount) {
        betLabel.setText("$" + amount);
    }

    /**
     * Highlight/de-highlight this seat as the active player.
     * Showing active also reveals the timer ring; hiding removes it.
     */
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

    /** Start the visual countdown timer. Call setActive(true) first. */
    public void startTimer(int seconds) {
        stopTimer();
        timerArc.setLength(360);
        timerText.setText(String.valueOf(seconds));

        // Arc sweeps from 360 → 0 over the full duration
        activeTimer = new Timeline(
                new KeyFrame(Duration.seconds(seconds),
                        new KeyValue(timerArc.lengthProperty(), 0))
        );

        // Separate timeline ticks the text label every second
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

    /** Stop and reset the timer without hiding the ring. */
    public void stopTimer() {
        if (activeTimer != null) {
            activeTimer.stop();
            activeTimer = null;
        }
    }

    /**
     * Show a floating notification above this seat's cards.
     * Common values: "BLACKJACK", "BUST", "WIN", "PUSH", "STAND"
     * Fades out automatically after ~3.5 s.
     */
    public void showNotification(String message) {
        notificationLabel.getStyleClass().removeAll(
                "seat-notification-win",
                "seat-notification-bust",
                "seat-notification-blackjack",
                "seat-notification-push"
        );
        switch (message.toUpperCase()) {
            case "BLACKJACK" -> notificationLabel.getStyleClass().add("seat-notification-blackjack");
            case "BUST"      -> notificationLabel.getStyleClass().add("seat-notification-bust");
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

    /** Clear all cards and reset visual state for a new round. */
    public void clearHand() {
        cardRow.getChildren().clear();
        totalLabel.setVisible(false);
        totalLabel.getStyleClass().removeAll("total-badge-bust", "total-badge-blackjack");
        stopTimer();
    }
}