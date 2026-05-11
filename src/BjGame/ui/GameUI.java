package BjGame.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Full blackjack game table screen.
 */
public class GameUI {

    private final DealerArea        dealer = new DealerArea();
    private final List<PlayerSeat>  seats  = new ArrayList<>();

    private Button hitBtn;
    private Button standBtn;
    private Button doubleBtn;
    private Button splitBtn;

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

    /**
     * Register a player seat. Call this for every player before build().
     * @param isLocal true for the player on this machine (brighter name label)
     */
    public PlayerSeat addSeat(String name, boolean isLocal) {
        PlayerSeat seat = new PlayerSeat(name, isLocal);
        seats.add(seat);
        return seat;
    }

    public DealerArea getDealer() { return dealer; }

    // ── Action control ────────────────────────────────────────────────────────

    /** Enable or disable all four action buttons at once. */
    public void setActionsEnabled(boolean enabled) {
        hitBtn.setDisable(!enabled);
        standBtn.setDisable(!enabled);
        doubleBtn.setDisable(!enabled);
        splitBtn.setDisable(!enabled);
    }

    public void setSplitEnabled(boolean enabled)  { splitBtn.setDisable(!enabled);  }
    public void setDoubleEnabled(boolean enabled) { doubleBtn.setDisable(!enabled); }

    // ── Build ─────────────────────────────────────────────────────────────────

    public Region build() {
        StackPane root = new StackPane();
        root.getStyleClass().add("game-table");

        VBox layout = new VBox(0);
        layout.setAlignment(Pos.CENTER);

        // Header
        layout.getChildren().add(buildHeader());

        // Dealer
        layout.getChildren().add(dealer.build());

        // Felt spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        layout.getChildren().add(spacer);

        // Player seats
        layout.getChildren().add(buildSeatRow());

        // Action bar
        layout.getChildren().add(buildActionBar());

        root.getChildren().add(layout);
        return root;
    }

    // ── Layout sections ───────────────────────────────────────────────────────

    private Region buildHeader() {
        Text suits = new Text("♠   ♥   ♦   ♣");
        suits.getStyleClass().add("table-suits");

        HBox header = new HBox(suits);
        header.setAlignment(Pos.CENTER);
        header.getStyleClass().add("table-header");
        header.setPadding(new Insets(10, 0, 6, 0));
        return header;
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

        // Spacer pushes ? to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bar = new HBox(10, hitBtn, standBtn, doubleBtn, splitBtn, spacer, helpBtn);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.getStyleClass().add("action-bar");
        bar.setPadding(new Insets(12, 24, 16, 24));

        setActionsEnabled(false); // disabled until it's the player's turn

        return bar;
    }

    private Button btn(String label, String styleClass) {
        Button b = new Button(label);
        b.getStyleClass().add("action-btn");
        b.getStyleClass().add(styleClass);
        return b;
    }

    public PlayerSeat getSeat(int index) {
        return seats.get(index);
    }
}