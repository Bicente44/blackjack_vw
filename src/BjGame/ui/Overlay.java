package BjGame.ui;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Allows for popup over main content, Used with an overlay screen like SettingsUI.
 */
public class Overlay {

    private final String title;
    private final List<Node> contentNodes = new ArrayList<>();
    private Consumer<Region> onCloseLogic = node -> {};

    public Overlay(String title) {
        this.title = title;
    }

    public Overlay addContent(Node... nodes) {
        contentNodes.addAll(Arrays.asList(nodes));
        return this;
    }

    public Overlay onClose(Consumer<Region> onCloseLogic) {
        this.onCloseLogic = onCloseLogic;
        return this;
    }

    public Region build() {
        StackPane container = new StackPane();
        container.getStyleClass().add("overlay-container");

        Region dimmer = new Region();
        dimmer.getStyleClass().add("overlay-dimmer");
        dimmer.setOnMouseClicked(e -> onCloseLogic.accept(container));

        VBox dialog = new VBox(0);
        dialog.getStyleClass().add("overlay-dialog");
        dialog.setAlignment(Pos.TOP_CENTER);

        dialog.getChildren().add(buildHeader(container));

        VBox scrollContent = new VBox(14);
        scrollContent.setPadding(new Insets(16, 20, 20, 20));
        scrollContent.getChildren().addAll(contentNodes);

        ScrollPane scroll = new ScrollPane(scrollContent);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.getStyleClass().add("overlay-scroll");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        dialog.getChildren().add(scroll);

        dialog.setMinWidth(380);
        dialog.maxWidthProperty().bind(
                Bindings.min(container.widthProperty().multiply(0.88), 520));
        dialog.maxHeightProperty().bind(
                Bindings.min(container.heightProperty().multiply(0.88), 560));

        dialog.setOnMouseClicked(e -> e.consume());

        container.getChildren().addAll(dimmer, dialog);
        return container;
    }

    private HBox buildHeader(StackPane container) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("overlay-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button closeBtn = new Button("✕");
        closeBtn.getStyleClass().add("overlay-close-btn");
        closeBtn.setOnAction(e -> onCloseLogic.accept(container));

        HBox header = new HBox(10, titleLabel, spacer, closeBtn);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("overlay-header");
        return header;
    }
}
