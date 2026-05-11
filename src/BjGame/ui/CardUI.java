package BjGame.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * A single playing card drawn entirely in JavaFX.
 */
public class CardUI {

    public static final double W = 64;
    public static final double H = 90;

    private final String rank;
    private final String suit;
    private boolean faceDown = false;

    public CardUI(String rank, String suit) {
        this.rank = rank;
        this.suit = suit;
    }

    /** Fluent — mark as face-down before calling build(). */
    public CardUI faceDown() {
        this.faceDown = true;
        return this;
    }

    public Region build() {
        return faceDown ? buildBack() : buildFace();
    }

    // ── Face ──────────────────────────────────────────────────────────────────

    private Region buildFace() {
        String colorClass = isRed() ? "card-red" : "card-black";

        // Top-left: rank + suit small
        Text topRank = styledText(rank, "card-rank", colorClass);
        Text topSuit = styledText(suit, "card-suit-small", colorClass);
        VBox topLeft = new VBox(0, topRank, topSuit);
        topLeft.setAlignment(Pos.TOP_LEFT);
        StackPane.setAlignment(topLeft, Pos.TOP_LEFT);

        // Center: large suit
        Text suitBig = styledText(suit, "card-suit-big", colorClass);
        StackPane.setAlignment(suitBig, Pos.CENTER);

        // Bottom-right: mirrored rank + suit
        Text botRank = styledText(rank, "card-rank", colorClass);
        Text botSuit = styledText(suit, "card-suit-small", colorClass);
        VBox botRight = new VBox(0, botSuit, botRank);
        botRight.setAlignment(Pos.BOTTOM_RIGHT);
        botRight.setRotate(180);
        StackPane.setAlignment(botRight, Pos.BOTTOM_RIGHT);

        StackPane card = card("card-face");
        card.setPadding(new Insets(4, 5, 4, 5));
        card.getChildren().addAll(topLeft, suitBig, botRight);
        return card;
    }

    // ── Back ──────────────────────────────────────────────────────────────────

    private Region buildBack() {
        // Gold diamond pattern on dark navy
        Text pattern = new Text("✦");
        pattern.getStyleClass().add("card-back-pattern");

        StackPane card = card("card-back");
        card.getChildren().add(pattern);
        return card;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private StackPane card(String styleClass) {
        StackPane pane = new StackPane();
        pane.getStyleClass().add(styleClass);
        pane.setPrefSize(W, H);
        pane.setMinSize(W, H);
        pane.setMaxSize(W, H);
        return pane;
    }

    private Text styledText(String content, String... styles) {
        Text t = new Text(content);
        t.getStyleClass().addAll(styles);
        return t;
    }

    private boolean isRed() {
        return "♥".equals(suit) || "♦".equals(suit);
    }
}
