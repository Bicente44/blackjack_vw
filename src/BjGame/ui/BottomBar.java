package BjGame.ui;

import BjGame.Debug;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

public record BottomBar(StackPane root) {

    public Region build() {
        Button settingsBtn = iconButton("⚙");
        settingsBtn.setOnAction(e -> showSettings());

        HBox bar = new HBox(8, settingsBtn);
        bar.setAlignment(Pos.CENTER_RIGHT);
        bar.setPadding(new Insets(8, 14, 10, 14));
        bar.getStyleClass().add("bottom-bar");
        return bar;
    }

    private void showSettings() {
        SettingsUI settings = new SettingsUI();
        settings.setOnAbout(() -> Debug.println("About clicked"));

        Region overlay = new Overlay("Settings")
                .addContent(settings.buildContent())
                .onClose(node -> root.getChildren().remove(node))
                .build();

        root.getChildren().add(overlay);
    }

    private Button iconButton(String glyph) {
        Button btn = new Button(glyph);
        btn.getStyleClass().add("bottombar-btn");
        return btn;
    }
}
