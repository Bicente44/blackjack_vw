package BjGame.ui;

import BjGame.Debug;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.awt.Desktop;
import java.net.URI;

public class HostConnectUI {

    private static final String BICENTE       = "Vincent Welbourne";
    private static final String GITHUB_HANDLE = "bicente44";
    private static final String GITHUB_URL    = "https://github.com/Bicente44";
    private static final String WEBSITE_LABEL = "bicente_website";
    private static final String WEBSITE_URL   = "https://bicente44.github.io/bicente_website";

    // ── Swappable zone ────────────────────────────────────────────────────────
    private final StackPane swapZone = new StackPane();

    // ── Exposed fields ────────────────────────────────────────────────────────
    public TextField     usernameField;
    public PasswordField passwordField;

    // ── Callbacks — wire these before calling build() ─────────────────────────
    private Runnable onLogin    = () -> {};
    private Runnable onGuest    = () -> {};
    private Runnable onHost     = () -> {};
    private Runnable onJoin     = () -> {};
    private Runnable onLogout = () -> {};

    public void setOnLogin(Runnable r) { onLogin = r; }
    public void setOnGuest(Runnable r) { onGuest = r; }
    public void setOnHost(Runnable r)  { onHost  = r; }
    public void setOnJoin(Runnable r)  { onJoin  = r; }
    public void setOnLogout(Runnable r) { onLogout = r; }

    // ─────────────────────────────────────────────────────────────────────────

    public Region build() {
        swapZone.getChildren().setAll(buildAuth());
        swapZone.setAlignment(Pos.CENTER);

        VBox card = new VBox(28,
                buildHeader(),
                buildDivider(),
                swapZone,
                buildDivider(),
                buildFooter()
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

    // ── Auth panel (state 1) ──────────────────────────────────────────────────

    private Region buildAuth() {
        usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.getStyleClass().add("auth-input");
        usernameField.setMaxWidth(Double.MAX_VALUE);

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("auth-input");
        passwordField.setMaxWidth(Double.MAX_VALUE);

        Button loginBtn = new Button("LOGIN");
        loginBtn.getStyleClass().addAll("lobby-btn", "join-btn");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setOnAction(e -> {
            onLogin.run();
            showHostJoin();
        });

        // Allow Enter key on password field to trigger login
        passwordField.setOnAction(e -> loginBtn.fire());

        Hyperlink guestLink = new Hyperlink("Continue as Guest");
        guestLink.getStyleClass().add("guest-link");
        guestLink.setOnAction(e -> {
            onGuest.run();
            showHostJoin();
        });

        VBox auth = new VBox(10, usernameField, passwordField, loginBtn, guestLink);
        auth.setAlignment(Pos.CENTER);
        auth.getStyleClass().add("auth-block");
        auth.setMaxWidth(Double.MAX_VALUE);
        return auth;
    }

    // ── Host / Join panel (state 2) ───────────────────────────────────────────

    private Region buildHostJoin() {
        Button hostBtn = new Button("HOST GAME");
        hostBtn.getStyleClass().addAll("lobby-btn", "host-btn");
        hostBtn.setOnAction(e -> onHost.run());

        Button joinBtn = new Button("JOIN GAME");
        joinBtn.getStyleClass().addAll("lobby-btn", "join-btn");
        joinBtn.setOnAction(e -> onJoin.run());

        HBox buttons = new HBox(14, hostBtn, joinBtn);
        buttons.setAlignment(Pos.CENTER);
        buttons.getStyleClass().add("button-row");

        Hyperlink logoutLink = new Hyperlink("Logout");
        logoutLink.getStyleClass().add("logout-link");
        logoutLink.setOnAction(e -> {
            onLogout.run();
            showAuth();
        });

        VBox zone = new VBox(10, buttons, logoutLink);
        zone.setAlignment(Pos.CENTER);
        return zone;
    }

    /** Swaps auth panel out, host/join panel in. */
    private void showHostJoin() {
        swapZone.getChildren().setAll(buildHostJoin());
    }

    private void showAuth() {
        swapZone.getChildren().setAll(buildAuth());
    }

    // ── Header ────────────────────────────────────────────────────────────────

    private Region buildHeader() {
        Text suits    = new Text("♠   ♥   ♦   ♣");
        Text title    = new Text("BLACKJACK");
        Text subtitle = new Text("— The House Always Wins —");

        suits.getStyleClass().add("header-suits");
        title.getStyleClass().add("header-title");
        subtitle.getStyleClass().add("header-subtitle");

        VBox header = new VBox(6, suits, title, subtitle);
        header.setAlignment(Pos.CENTER);
        header.getStyleClass().add("header-box");
        return header;
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

    // ── Footer ────────────────────────────────────────────────────────────────

    private Region buildFooter() {
        Text name   = new Text(BICENTE);
        Text bullet = new Text("·");
        name.getStyleClass().add("footer-name");
        bullet.getStyleClass().add("footer-bullet");

        Hyperlink github  = makeLink("⌥  " + GITHUB_HANDLE, GITHUB_URL);
        Hyperlink website = makeLink("⊕  " + WEBSITE_LABEL,  WEBSITE_URL);

        HBox links = new HBox(18, github, bullet, website);
        links.setAlignment(Pos.CENTER);

        VBox footer = new VBox(6, name, links);
        footer.setAlignment(Pos.CENTER);
        footer.getStyleClass().add("footer-box");
        return footer;
    }

    private Hyperlink makeLink(String label, String url) {
        Hyperlink hl = new Hyperlink(label);
        hl.getStyleClass().add("footer-link");
        hl.setOnAction(e -> {
            try {
                if (Desktop.isDesktopSupported())
                    Desktop.getDesktop().browse(new URI(url));
            } catch (Exception ex) {
                Debug.println(ex.getMessage());
            }
        });
        return hl;
    }
}