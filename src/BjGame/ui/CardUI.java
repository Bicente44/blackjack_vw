package BjGame.ui;

import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * A single playing card drawn with absolute coordinates on a Pane.
 * Avoids StackPane alignment/padding overlap issues.
 *
 *   new CardView("A", "♠").build()           // face-up
 *   new CardView("", "").faceDown().build()   // face-down
 */
public class CardUI {

    public static final double W = 62;
    public static final double H = 88;

    private final String rank;
    private final String suit;
    private boolean faceDown = false;

    public CardUI(String rank, String suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public CardUI faceDown() {
        this.faceDown = true;
        return this;
    }

    public Region build() {
        return faceDown ? buildBack() : buildFace();
    }

    // ── Face-up ───────────────────────────────────────────────────────────────

    private Region buildFace() {
        Pane pane = basePane("#f5f0e0", "#c8c0a0", false);
        Color color = isRed() ? Color.web("#b22222") : Color.web("#1a1a1a");

        // Top-left: rank
        Text topRank = text(rank, 10, 13, Font.font("Georgia", FontWeight.BOLD, 13), color);

        // Top-left: suit small (below rank)
        Text topSuit = text(suit, 10, 25, Font.font("Georgia", FontWeight.NORMAL, 11), color);

        // Center: large suit
        Text bigSuit = text(suit, W / 2, H / 2 + 10,
                Font.font("Georgia", FontWeight.NORMAL, 28), color);
        bigSuit.setX(bigSuit.getX() - 9); // rough center offset for suit glyphs

        // Bottom-right: rank (rotated 180 — simulated by mirroring coords)
        Text botRank = text(rank, W - 10, H - 14, Font.font("Georgia", FontWeight.BOLD, 13), color);
        botRank.setRotate(180);

        Text botSuit = text(suit, W - 10, H - 3, Font.font("Georgia", FontWeight.NORMAL, 11), color);
        botSuit.setRotate(180);

        pane.getChildren().addAll(topRank, topSuit, bigSuit, botRank, botSuit);
        return pane;
    }

    // ── Face-down ─────────────────────────────────────────────────────────────

    private Region buildBack() {
        Pane pane = basePane("#0f1e30", "#D4AF37", true);

        Text diamond = new Text("✦");
        diamond.setFont(Font.font("Georgia", FontWeight.NORMAL, 24));
        diamond.setFill(Color.web("#D4AF37"));
        diamond.setOpacity(0.35);
        // Center the glyph manually
        diamond.setX(W / 2 - 10);
        diamond.setY(H / 2 + 8);

        pane.getChildren().add(diamond);
        return pane;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Pane basePane(String fill, String stroke, boolean thickBorder) {
        Pane pane = new Pane();
        pane.setPrefSize(W, H);
        pane.setMinSize(W, H);
        pane.setMaxSize(W, H);

        Rectangle bg = new Rectangle(W, H);
        bg.setArcWidth(8);
        bg.setArcHeight(8);
        bg.setFill(Color.web(fill));
        bg.setStroke(Color.web(stroke));
        bg.setStrokeWidth(thickBorder ? 1.5 : 0.8);

        // Drop shadow via a slightly-offset dark rect underneath
        Rectangle shadow = new Rectangle(W, H);
        shadow.setArcWidth(8);
        shadow.setArcHeight(8);
        shadow.setFill(Color.rgb(0, 0, 0, 0.35));
        shadow.setTranslateX(2);
        shadow.setTranslateY(2);

        pane.getChildren().addAll(shadow, bg);
        return pane;
    }

    /** Place a Text node at absolute (x, y) within a Pane. */
    private Text text(String content, double x, double y, Font font, Color fill) {
        Text t = new Text(content);
        t.setFont(font);
        t.setFill(fill);
        t.setX(x);
        t.setY(y);
        return t;
    }

    private boolean isRed() {
        return "♥".equals(suit) || "♦".equals(suit);
    }
}