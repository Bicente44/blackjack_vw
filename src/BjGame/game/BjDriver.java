package BjGame.game;/*
 * Program created by Vincent Welbourne
 * vincent.vw04@gmail.com
 * Code description: This is a recreation of the famous card BjGame.game called 'Blackjack' I have implemented my own
 * variation and twist of the rules for this program for more information you may contact me through my email or
 * for more information on the BjGame.game go to help section.
 *
 */

import BjGame.Debug;
import BjGame.shared.Player;
import BjGame.ui.*;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Scanner;

/**
 * This class works as the launch menu of the Blackjack card BjGame.game
 *
 * @author Vincent Welbourne
 */
public class BjDriver extends Application {
    public final static int START_CASH = 500;
    private StackPane root;
    private String loggedInUsername = null;
    GameController gameController = new GameController();


    @Override
    public void start(Stage stage) {
        root = new StackPane();
        root.getStylesheets().add("/css/style.css");

        StackPane contentPane = new StackPane();

        showHomeScreen(contentPane);

        BorderPane layout = new BorderPane();
        layout.setCenter(contentPane);
        layout.setBottom(new BottomBar(root).build());

        root.getChildren().add(layout);

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Blackjack - Vincent Welbourne");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    @Override
    public void stop() {
        Debug.println("Closing app...");
        Debug.println("Program created by Vincent Welbourne");
    }

    private void showHomeScreen(StackPane contentPane) {
        HostConnectUI homeScreen = new HostConnectUI();

        homeScreen.setOnLogin(() -> {
            String username = homeScreen.usernameField.getText();
            Debug.println("Login attempt: " + username);
            // TODO authenticate player
        });
        homeScreen.setOnGuest(() -> {
            loggedInUsername = "Guest";
            Debug.println("Continuing as guest");
            // TODO create a guest Player
        });
        homeScreen.setOnHost(() -> {
            Debug.println("Host selected");
            showHostUI(contentPane);
        });
        homeScreen.setOnJoin(() -> {
            Debug.println("Join selected");
            showJoinUI(contentPane);
        });
        homeScreen.setOnLogout(() -> {
            loggedInUsername = null;
            Debug.println("Logged out");
        });
        contentPane.getChildren().setAll(homeScreen.build());
    }

    private void showHostUI(StackPane contentPane) {
        HostUI hostUI = new HostUI();

        // TODO start up the game server on a daemon and run players on a separate thread

        hostUI.setOnStart(() -> {
            Debug.println("Starting game as host...");
            showGameTable(contentPane);
        });

        hostUI.setOnExit(() -> showHomeScreen(contentPane));

        contentPane.getChildren().setAll(hostUI.build());
    }

    private void showJoinUI(StackPane contentPane) {
        ConnectUI connectUI = new ConnectUI();

        connectUI.setOnConnect(() -> {
            String ip = connectUI.getHostAddress();
            if (ip.isEmpty()) {
                Debug.println("No IP entered");
                return;
            }
            Debug.println("Connecting to: " + ip);
            // TODO show the lobby ui excluding management permissions
        });

        connectUI.setOnExit(() -> showHomeScreen(contentPane));

        contentPane.getChildren().setAll(connectUI.build());
    }

    private void showGameTable(StackPane contentPane) {
        Debug.println("Showing game table...");

        // Test add players:
        gameController.gameSession.players.clear();
        gameController.gameSession.players.add(new Player.Dealer("Dealer", "", 0));          // index 0 — dealer slot
        gameController.gameSession.players.add(new Player.HumanPlayer("Vincent", "", START_CASH)); // index 1
        gameController.gameSession.players.add(new Player.HumanPlayer("Alice",   "", START_CASH)); // index 2
        loggedInUsername = "Vincent";

        GameUI table = new GameUI();

        // Add seats for each non-dealer player
        for (int i = 1; i < gameController.gameSession.players.size(); i++) {
            Player p = gameController.gameSession.players.get(i);
            boolean isLocal = p.getPlayerName().equals(loggedInUsername);
            table.addSeat(p.getPlayerName(), isLocal);
        }

        table.setOnHit(()    -> gameController.hit());
        table.setOnStand(()  -> gameController.stand());
        table.setOnDouble(() -> gameController.doubleDown());
        table.setOnHelp(()   -> { /* show help overlay */ });

        gameController.setUI(table);
        gameController.startRound();

        contentPane.getChildren().setAll(table.build());
    }

}