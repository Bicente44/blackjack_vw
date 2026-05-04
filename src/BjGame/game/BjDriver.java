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
import BjGame.ui.ConnectUI;
import BjGame.ui.HostConnectUI;

import BjGame.ui.HostUI;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * This class works as the launch menu of the Blackjack card BjGame.game
 *
 * @author Vincent Welbourne
 */
public class BjDriver extends Application {
    public final static int START_CASH = 500;
    private GameSession gameSession;
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

    /**
     * This is where the BjGame.game starts you get options from start BjGame.game, help and exit.
     * Checks if it's the first BjGame.game or if out of cards to initialize a deck and or
     * shuffle.
     *
     * @param args no commandline arguments
     */
    /*public static void launch(String[] args) {

        Player dealer = new Player.Dealer(0, "Dealer", 0, 0.0, 0, 0, 0.0);
        GameSession gameSession = new GameSession();
        gameSession.players.add(dealer);
        gameSession.hands.add(new HandManager());

        Debug.println("\nWelcome to BlackJack");
        Debug.print("Enter your name (Optional)\n" + "> ");
        playerName = keyboard.nextLine();
        if (playerName.isEmpty()) {
            playerName = "BjGame.shared.Player"+PLAYER_ID;
        }
        //TODO Eventually make this capable for each player that joins the BjGame.game (For now just the only 1 player that joins)
        Player player = new Player.HumanPlayer(PLAYER_ID, playerName, 0, START_CASH, 0, 0, 0.0);
        gameSession.players.add(player);
        gameSession.hands.add(new HandManager());
        do {
            try {
                Debug.print("\nWould you like to start new a hand?\n" + "Options are: 1. (Yes), 2. (Help), 3. (No, exit).\n" + "> ");
                int option = keyboard.nextInt();
                keyboard.nextLine();

                switch (option) {

                    // 1. Call to BjWork class and start a new hand
                    case 1:
                        // Initialize decks if its first round or if you have no cards
                        if (roundsPlayed == 0 || noCards) {
                            gameSession.deck = Shuffle.shuffle();
                            // TODO SET MINIMUM BET
                            gameSession.game();
                        } else {
                            gameSession.game();
                        }
                        roundsPlayed++;
                        break;
                    // 2. Help menu
                    case 2:
                        boolean inHelp = true;
                        while (inHelp) {
                            Debug.print("Enter '1' to learn how to play, Basic strategy sheet '2' or 3 to exit help menu.\n" + "> ");
                            try {
                                int helpOption = keyboard.nextInt();
                                keyboard.nextLine();
                                switch (helpOption) {
                                    case 1:
                                        Debug.println("Blackjack is a simple card BjGame.game where your only opponent is the dealer.\n" +
                                                "Your goal is to beat the dealer, you can do this by simply having more cards then the dealer, or if the dealer 'busts'\n" +
                                                "'bust' in Blackjack means you have greater then 21 cards, you may also lose this way.\n" +
                                                "The point of the BjGame.game is to have a total card value closest to 21 without busting or greater then the dealer\n" +
                                                "You may lose if you 'bust' and the dealer has more cards then you.\n" +
                                                "If you have a tie in card value its a 'Stand', it means you get to play again and keep your bets\n" +
                                                "The dealer MUST stand at 17 and up, you may draw as many cards as you desire.\n" +
                                                "Hit = draw a card\n" +
                                                "Stand = you wish to no longer draw cards and see the hidden dealer card\n" +
                                                "Double = you double your placed bet and you have to draw 1 last card\n");
                                        break;
                                    case 2:
                                        Debug.println("i might actually write it down one day..\n" +
                                                "for now you just get the link:\n" +
                                                "https://www.blackjackapprenticeship.com/blackjack-strategy-charts/\n");
                                        break;
                                    case 3:
                                        Debug.println("Exiting help menu..\n");
                                        inHelp = false;
                                        break;
                                    default:
                                        Debug.println("Invalid number, options are from 1-3!");
                                }
                            } catch (InputMismatchException ime) {
                                Debug.println("Please enter a valid number");
                                keyboard.nextLine();
                            }
                        }
                        break;
                    // 3. Exit Program
                    case 3:
                        // TODO: remove player that chose to leave
                        // TODO: check if theres any more players, if not proceed and close the program
                        LOOP = false;
                        break;
                    default:
                        Debug.println("Invalid number, options are from 1-3!");
                }

            } catch (InputMismatchException e) {
                Debug.println("Please enter a valid number!");
                keyboard.nextLine();
            }
        } while (LOOP);

        // TODO how the heck do i eventually make it for the player that left not everyone
        Debug.println("BjGame.shared.Player stats:");
        System.out.printf("%-15s %10s %8s %8s %10s %12s %8s%n",
                "Name", "Money", "Wins", "Losses", "Total", "WinRate", "Net");
        Debug.println("-------------------------------------------------------------------------------");
        for (int i = 1; i < gameSession.players.size(); i++) {
            Player p = gameSession.players.get(i);
            String name = p.getPlayerName();
            double money = p.getMoney();
            int wins = p.getWins();
            int losses = p.getLosses();
            int total = wins + losses;
            String winRate;
            if (total == 0) {
                winRate = "N/A";
            } else {
                double rate = (wins * 100.0) / total;
                winRate = String.format("%.1f%%", rate);
            }
            double net = money - START_CASH;

            System.out.printf("%-18s %5.2f$ %5d %8d %11d %12s %10.2f$%n",
                    name, money, wins, losses, total, winRate, net);
        }
        Debug.println("-------------------------------------------------------------------------------\n");

        Debug.println("Thank you for using my program!");
        Debug.println("Program created by Vincent Welbourne");

        keyboard.close();
    }
    */
}