package BjGame.game;/*
 * Program created by Vincent Welbourne
 * vincent.vw04@gmail.com
 * Code description: This is a recreation of the famous card BjGame.game called 'Blackjack' I have implemented my own
 * variation and twist of the rules for this program for more information you may contact me through my email or
 * for more information on the BjGame.game go to help section.
 *
 */

import BjGame.Debug;
import BjGame.ui.ConnectUI;
import BjGame.ui.HostConnectUI;

import BjGame.ui.HostUI;
import javafx.application.Application;
import javafx.scene.Scene;
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
    private GameController gameSession;
    private StackPane root;
    public static Scanner keyboard = new Scanner(System.in);
    public static int roundsPlayed = 0;
    public static boolean noCards = true;
    private String loggedInUsername = null;


    @Override
    public void start(Stage stage) {
        root = new StackPane();
        root.getStylesheets().add("/css/style.css");

        showHomeScreen();

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

    private void showHomeScreen() {
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
            showHostUI();
        });
        homeScreen.setOnJoin(() -> {
            Debug.println("Join selected");
            showJoinUI();
        });
        homeScreen.setOnLogout(() -> {
            loggedInUsername = null;
            Debug.println("Logged out");
        });
        root.getChildren().setAll(homeScreen.build());
    }

    private void showHostUI() {
        HostUI hostUI = new HostUI();

        // TODO start up the game server on a daemon and run players on a separate thread

        hostUI.setOnStart(() -> {
            Debug.println("Starting game as host...");
            // TODO showGameTable();
        });

        hostUI.setOnExit(() -> showHomeScreen());

        root.getChildren().setAll(hostUI.build());
    }

    private void showJoinUI() {
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

        connectUI.setOnExit(() -> showHomeScreen());

        root.getChildren().setAll(connectUI.build());
    }
}