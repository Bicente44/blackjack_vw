package BjGame.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Dealer's card area.
 */
public class DealerArea {

    private HBox  cardRow;
    private Label totalLabel;
    private int   hiddenIndex = -1;

    public Region build() {
        Text label = new Text("DEALER");
        label.getStyleClass().add("dealer-label");

        cardRow = new HBox(8);
        cardRow.setAlignment(Pos.CENTER);
        cardRow.setMinHeight(CardUI.H + 10);

        totalLabel = new Label("");
        totalLabel.getStyleClass().add("total-badge");
        totalLabel.setVisible(false);

        VBox area = new VBox(10, label, cardRow, totalLabel);
        area.setAlignment(Pos.CENTER);
        area.getStyleClass().add("dealer-area");
        area.setPadding(new Insets(20, 32, 20, 32));
        return area;
    }

    // ── Public API ────────────────────────────────────────────────────────────

    public void addCard(String rank, String suit) {
        cardRow.getChildren().add(new CardUI(rank, suit).build());
    }

    /** Add the hole card face-down. Call revealHiddenCard() later. */
    public void addHiddenCard() {
        hiddenIndex = cardRow.getChildren().size();
        cardRow.getChildren().add(new CardUI("", "").faceDown().build());
    }

    /** Flip the hole card to show the real rank/suit. */
    public void revealHiddenCard(String rank, String suit) {
        if (hiddenIndex >= 0 && hiddenIndex < cardRow.getChildren().size()) {
            cardRow.getChildren().set(hiddenIndex, new CardUI(rank, suit).build());
            hiddenIndex = -1;
        }
    }

    public void setTotal(int total) {
        totalLabel.setText(String.valueOf(total));
        totalLabel.setVisible(true);
    }

    public void clearHand() {
        cardRow.getChildren().clear();
        totalLabel.setVisible(false);
        hiddenIndex = -1;
    }
}