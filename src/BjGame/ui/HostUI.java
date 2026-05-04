package BjGame.ui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

public class HostUI {

    // Hardcoded for now — swap with real network lookup later
    private static final String HOST_IP = "192.168.1.42";

    // ── Callbacks ─────────────────────────────────────────────────────────────
    private Runnable onStart = () -> {};
    private Runnable onExit  = () -> {};

    public void setOnStart(Runnable r) { onStart = r; }
    public void setOnExit(Runnable r)  { onExit  = r; }

    // ── Player model ──────────────────────────────────────────────────────────

    public static class Player {
        private final String name;
        private final String status;

        public Player(String name, String status) {
            this.name   = name;
            this.status = status;
        }

        public String getName()   { return name; }
        public String getStatus() { return status; }
    }

    private final ObservableList<Player> players = FXCollections.observableArrayList();

    /** Add / remove players at runtime via this list. */
    public ObservableList<Player> getPlayers() { return players; }

    // ─────────────────────────────────────────────────────────────────────────

    public Region build() {
        VBox card = new VBox(24,
                buildTitle(),
                buildDivider(),
                buildIpBlock(),
                buildDivider(),
                buildPlayerTable(),
                buildDivider(),
                buildActions()
        );
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("lobby-card");
        card.setPadding(new Insets(44, 52, 40, 52));
        card.setMaxWidth(480);
        card.setMinWidth(380);

        StackPane root = new StackPane(card);
        root.getStyleClass().add("root-pane");
        root.setAlignment(Pos.CENTER);
        return root;
    }

    // ── Title ─────────────────────────────────────────────────────────────────

    private Region buildTitle() {
        Text suits = new Text("♠   ♥   ♦   ♣");
        suits.getStyleClass().add("header-suits");

        Text title = new Text("HOST LOBBY");
        title.getStyleClass().add("panel-title");

        VBox box = new VBox(6, suits, title);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    // ── IP block ──────────────────────────────────────────────────────────────

    private Region buildIpBlock() {
        Text label = new Text("HOST IP");
        label.getStyleClass().add("field-label");

        Text ipText = new Text(HOST_IP);
        ipText.getStyleClass().add("ip-value");

        Text copyIcon = new Text("⎘");
        copyIcon.getStyleClass().add("copy-icon");

        HBox ipChip = new HBox(10, ipText, copyIcon);
        ipChip.setAlignment(Pos.CENTER_LEFT);
        ipChip.getStyleClass().add("ip-chip");
        ipChip.setPadding(new Insets(10, 16, 10, 16));

        ipChip.setOnMouseClicked(e -> {
            ClipboardContent content = new ClipboardContent();
            content.putString(HOST_IP);
            Clipboard.getSystemClipboard().setContent(content);
            copyIcon.setText("✓");
            copyIcon.getStyleClass().add("copy-icon-done");
            new Thread(() -> {
                try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
                Platform.runLater(() -> {
                    copyIcon.setText("⎘");
                    copyIcon.getStyleClass().remove("copy-icon-done");
                });
            }).start();
        });

        VBox block = new VBox(8, label, ipChip);
        block.setAlignment(Pos.CENTER_LEFT);
        block.setMaxWidth(Double.MAX_VALUE);
        return block;
    }

    // ── Player table ──────────────────────────────────────────────────────────

    private Region buildPlayerTable() {
        Text label = new Text("CONNECTED PLAYERS");
        label.getStyleClass().add("field-label");

        TableColumn<Player, String> nameCol = new TableColumn<>("Player");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.getStyleClass().add("table-col");
        nameCol.setResizable(false);

        TableColumn<Player, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.getStyleClass().add("table-col");
        statusCol.setResizable(false);

        TableView<Player> table = new TableView<>(players);
        // Add columns individually to avoid unchecked varargs warning
        table.getColumns().add(nameCol);
        table.getColumns().add(statusCol);
        table.getStyleClass().add("player-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("Waiting for players…"));
        table.setMaxWidth(Double.MAX_VALUE);
        table.setPrefHeight(160);
        table.setFocusTraversable(false);

        // Bind column widths to the table (not tableView field — that's package-private)
        nameCol.prefWidthProperty().bind(table.widthProperty().multiply(0.65));
        statusCol.prefWidthProperty().bind(table.widthProperty().multiply(0.34));

        VBox block = new VBox(8, label, table);
        block.setAlignment(Pos.CENTER_LEFT);
        block.setMaxWidth(Double.MAX_VALUE);
        return block;
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    private Region buildActions() {
        Button startBtn = new Button("START GAME");
        startBtn.getStyleClass().add("lobby-btn");
        startBtn.getStyleClass().add("host-btn");
        startBtn.setOnAction(e -> onStart.run());

        Button exitBtn = new Button("EXIT");
        exitBtn.getStyleClass().add("lobby-btn");
        exitBtn.getStyleClass().add("exit-btn");
        exitBtn.setOnAction(e -> onExit.run());

        HBox actions = new HBox(14, startBtn, exitBtn);
        actions.setAlignment(Pos.CENTER);
        actions.getStyleClass().add("button-row");
        return actions;
    }

    // ── Divider ───────────────────────────────────────────────────────────────

    private Region buildDivider() {
        Line left    = makeLine();
        Line right   = makeLine();
        Text diamond = new Text("✦");
        diamond.getStyleClass().add("divider-diamond");
        HBox box = new HBox(10, left, diamond, right);
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("divider-box");
        return box;
    }

    private Line makeLine() {
        Line line = new Line(0, 0, 100, 0);
        line.getStyleClass().add("divider-line");
        return line;
    }
}