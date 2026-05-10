package BjGame.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 *  Settings UI
 */
public class SettingsUI {

    private Runnable onAbout = () -> {};
    public void setOnAbout(Runnable r) { onAbout = r; }

    private Slider masterSlider;
    private Slider musicSlider;
    private Slider sfxSlider;
    private ComboBox<String> themeDropdown;
    private ComboBox<String> cardBackDropdown;
    private CheckBox fullscreenToggle;
    private ComboBox<String> languageDropdown;

    public double  getMasterVolume() { return masterSlider.getValue(); }
    public double  getMusicVolume() { return musicSlider.getValue(); }
    public double  getSfxVolume() { return sfxSlider.getValue(); }
    public String  getTheme() { return themeDropdown.getValue(); }
    public String  getCardBack() { return cardBackDropdown.getValue(); }
    public boolean isFullscreen() { return fullscreenToggle.isSelected(); }
    public String  getLanguage() { return languageDropdown.getValue(); }

    /**
     * Returns Settings UI content for within Overlay.
     */
    public Node[] buildContent() {
        return new Node[] {
                buildAudioSection(),
                buildSectionDivider(),
                buildVisualSection(),
                buildSectionDivider(),
                buildLanguageSection(),
                buildSectionDivider(),
                buildAboutRow()
        };
    }

    private Region buildAudioSection() {
        masterSlider = makeSlider(80);
        musicSlider = makeSlider(60);
        sfxSlider = makeSlider(100);

        VBox section = new VBox(14,
                sectionLabel("AUDIO"),
                sliderRow("Master", masterSlider),
                sliderRow("Music", musicSlider),
                sliderRow("SFX", sfxSlider)
        );
        section.setPadding(new Insets(20, 0, 20, 0));
        return section;
    }

    private Slider makeSlider(double defaultValue) {
        Slider s = new Slider(0, 100, defaultValue);
        s.getStyleClass().add("settings-slider");
        s.setMaxWidth(Double.MAX_VALUE);
        s.setShowTickMarks(false);
        s.setShowTickLabels(false);
        return s;
    }

    private HBox sliderRow(String labelText, Slider slider) {
        Label label = new Label(labelText);
        label.getStyleClass().add("settings-row-label");
        label.setMinWidth(58);

        Label pct = new Label((int) slider.getValue() + "%");
        pct.getStyleClass().add("settings-row-value");
        pct.setMinWidth(36);
        pct.setAlignment(Pos.CENTER_RIGHT);
        slider.valueProperty().addListener((obs, o, n) -> pct.setText(n.intValue() + "%"));

        HBox.setHgrow(slider, Priority.ALWAYS);
        HBox row = new HBox(12, label, slider, pct);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private Region buildVisualSection() {
        themeDropdown = makeDropdown("Classic");
        cardBackDropdown = makeDropdown("Default");
        fullscreenToggle = new CheckBox();
        fullscreenToggle.getStyleClass().add("settings-checkbox");
        fullscreenToggle.setSelected(false);

        VBox section = new VBox(14,
                sectionLabel("VISUAL"),
                dropdownRow("Theme", themeDropdown),
                dropdownRow("Card Back", cardBackDropdown),
                checkboxRow("Fullscreen", fullscreenToggle)
        );
        section.setPadding(new Insets(20, 0, 20, 0));
        return section;
    }

    private Region buildLanguageSection() {
        languageDropdown = makeDropdown("English", "Français");

        VBox section = new VBox(14,
                sectionLabel("LANGUAGE"),
                dropdownRow("Language", languageDropdown)
        );
        section.setPadding(new Insets(20, 0, 20, 0));
        return section;
    }

    private Region buildAboutRow() {
        Button aboutBtn = new Button("ABOUT");
        aboutBtn.getStyleClass().add("lobby-btn");
        aboutBtn.getStyleClass().add("join-btn");
        aboutBtn.setMaxWidth(Double.MAX_VALUE);
        aboutBtn.setOnAction(e -> onAbout.run());

        HBox row = new HBox(aboutBtn);
        row.setAlignment(Pos.CENTER);
        return row;
    }

    // Helpers

    private Text sectionLabel(String text) {
        Text t = new Text(text);
        t.getStyleClass().add("settings-section-label");
        return t;
    }

    private ComboBox<String> makeDropdown(String... options) {
        ComboBox<String> cb = new ComboBox<>();
        cb.getItems().addAll(options);
        cb.setValue(options[0]);
        cb.getStyleClass().add("settings-dropdown");
        cb.setMaxWidth(Double.MAX_VALUE);
        return cb;
    }

    private HBox dropdownRow(String labelText, ComboBox<String> dropdown) {
        Label label = new Label(labelText);
        label.getStyleClass().add("settings-row-label");
        label.setMinWidth(90);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        dropdown.setMaxWidth(180);
        HBox row = new HBox(12, label, spacer, dropdown);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private HBox checkboxRow(String labelText, CheckBox checkbox) {
        Label label = new Label(labelText);
        label.getStyleClass().add("settings-row-label");
        label.setMinWidth(90);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(12, label, spacer, checkbox);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private Region buildSectionDivider() {
        Region line = new Region();
        line.getStyleClass().add("section-divider");
        line.setMaxWidth(Double.MAX_VALUE);
        line.setPrefHeight(1);
        return line;
    }
}