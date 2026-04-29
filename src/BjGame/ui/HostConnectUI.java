package BjGame.ui;

import BjGame.Debug;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.awt.Desktop;
import java.net.URI;


public class HostConnectUI {
    private static final String BICENTE = "Vincent Welbourne";
    private static final String GITHUB_HANDLE  = "bicente44";
    private static final String GITHUB_URL     = "https://github.com/Bicente44";
    private static final String WEBSITE_LABEL  = "bicente_website";
    private static final String WEBSITE_URL    = "https://bicente44.github.io/bicente_website";

    public Region build() {
        VBox card = new VBox(30,
                buildHeader(),
                buildDivider(),
                buildButtons(),
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

    private Region buildHeader() {
        Text suits = new Text("♠   ♥   ♦   ♣");
        suits.getStyleClass().add("header-suits");

        Text title = new Text("BLACKJACK");
        title.getStyleClass().add("header-title");

        Text subtitle = new Text("— The House Always Wins —");
        subtitle.getStyleClass().add("header-subtitle");

        VBox header = new VBox(6, suits, title, subtitle);
        header.setAlignment(Pos.CENTER);
        header.getStyleClass().add("header-box");
        return header;
    }

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

    private Region buildButtons() {
        Button hostBtn = new Button("HOST GAME");
        hostBtn.getStyleClass().addAll("lobby-btn", "host-btn");
        // TODO: hostBtn.setOnAction(e -> {});

        Button joinBtn = new Button("JOIN GAME");
        joinBtn.getStyleClass().addAll("lobby-btn", "join-btn");
        // TODO: joinBtn.setOnAction(e -> {});

        HBox buttons = new HBox(14, hostBtn, joinBtn);
        buttons.setAlignment(Pos.CENTER);
        buttons.getStyleClass().add("button-row");
        return buttons;
    }

    private Region buildFooter() {
        Text name = new Text(BICENTE);
        name.getStyleClass().add("footer-name");

        Text bullet = new Text("·");
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