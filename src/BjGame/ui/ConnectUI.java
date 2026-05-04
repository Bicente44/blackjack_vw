package BjGame.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

/**
 * Join screen — player enters a host IP to connect to a session.
 *
 *  ┌──────────────────────────────────┐
 *  │  ♠   ♥   ♦   ♣                  │
 *  │  JOIN GAME                       │
 *  │  ──────  ✦  ──────               │
 *  │  CONNECT TO HOST IP              │
 *  │  [ 192.168.x.x _____________ ]  │
 *  │  ──────  ✦  ──────               │
 *  │  [ CONNECT ]  [ EXIT ]          │
 *  └──────────────────────────────────┘
 *
 *  Wire callbacks via setOn*() before calling build().
 *  Read the entered IP via getHostAddress().
 */
public class ConnectUI {

    // ── Exposed field ─────────────────────────────────────────────────────────
    public TextField ipField;

    // ── Callbacks ─────────────────────────────────────────────────────────────
    private Runnable onConnect = () -> {};
    private Runnable onExit    = () -> {};

    public void setOnConnect(Runnable r) { onConnect = r; }
    public void setOnExit(Runnable r)    { onExit    = r; }

    /** Returns whatever the player typed in the IP field. */
    public String getHostAddress() {
        return ipField == null ? "" : ipField.getText().trim();
    }

    // ─────────────────────────────────────────────────────────────────────────

    public Region build() {
        VBox card = new VBox(28,
                buildTitle(),
                buildDivider(),
                buildIpInput(),
                buildDivider(),
                buildActions()
        );
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("lobby-card");
        card.setPadding(new Insets(52, 60, 44, 60));
        card.setMaxWidth(460);

        StackPane root = new StackPane(card);
        root.getStyleClass().add("root-pane");
        root.setAlignment(Pos.CENTER);
        return root;
    }

    // ── Title ─────────────────────────────────────────────────────────────────

    private Region buildTitle() {
        Text suits = new Text("♠   ♥   ♦   ♣");
        suits.getStyleClass().add("header-suits");

        Text title = new Text("JOIN GAME");
        title.getStyleClass().add("panel-title");

        VBox box = new VBox(6, suits, title);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    // ── IP input ──────────────────────────────────────────────────────────────

    private Region buildIpInput() {
        Text label = new Text("CONNECT TO HOST IP");
        label.getStyleClass().add("field-label");

        ipField = new TextField();
        ipField.setPromptText("192.168.x.x");
        ipField.getStyleClass().add("auth-input");
        ipField.setMaxWidth(Double.MAX_VALUE);

        // Enter key triggers connect
        ipField.setOnAction(e -> onConnect.run());

        VBox block = new VBox(8, label, ipField);
        block.setAlignment(Pos.CENTER_LEFT);
        block.setMaxWidth(Double.MAX_VALUE);
        return block;
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    private Region buildActions() {
        Button connectBtn = new Button("CONNECT");
        connectBtn.getStyleClass().addAll("lobby-btn", "host-btn");
        connectBtn.setOnAction(e -> onConnect.run());

        Button exitBtn = new Button("EXIT");
        exitBtn.getStyleClass().addAll("lobby-btn", "exit-btn");
        exitBtn.setOnAction(e -> onExit.run());

        HBox actions = new HBox(14, connectBtn, exitBtn);
        actions.setAlignment(Pos.CENTER);
        actions.getStyleClass().add("button-row");
        return actions;
    }

    // ── Divider ───────────────────────────────────────────────────────────────

    private Region buildDivider() {
        Line left  = makeLine();
        Line right = makeLine();
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