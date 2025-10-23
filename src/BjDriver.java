/*
 * Program created by Vincent Welbourne
 * vincent.vw04@gmail.com
 * Code description: This is a recreation of the famous card game called 'Blackjack' I have implemented my own
 * variation and twist of the rules for this program for more information you may contact me through my email or
 * for more information on the game go to help section.
 *
 */

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * This class works as the main menu of the Blackjack card game
 *
 * @author Vincent Welbourne
 */
public class BjDriver {
    public static Scanner keyboard = new Scanner(System.in);
    private static boolean LOOP = true;
    public static int roundsPlayed = 0;
    public static boolean noCards = true;
    public static String playerName = "Player";
    public final static int START_CASH = 500;
    public static int PLAYER_ID = 1;

    /**
     * This is where the game starts you get options from start game, help and exit.
     * Checks if it's the first game or if out of cards to initialize a deck and or
     * shuffle.
     *
     * @param args no commandline arguments
     */
    public static void main(String[] args) {

        Player dealer = new Player.Dealer(0, "Dealer", 0, 0.0, 0, 0);
        BjWork.players.add(dealer);
        BjWork.hands.add(new Hand());

        System.out.println("Welcome to BlackJack\n");
        System.out.print("Enter your name (Optional)\n" + "> ");
        playerName = keyboard.nextLine();
        if (playerName.isEmpty()) {
            playerName = "Player"+PLAYER_ID;
        }
        //TODO: Eventually make this capable for each player that joins the game (For now just the only 1 player that joins)
        Player player = new Player.HumanPlayer(PLAYER_ID, playerName, 0, START_CASH, 0, 0);
        BjWork.players.add(player);
        BjWork.hands.add(new Hand());
        do {
            try {
                System.out.print("Would you like to start new a hand?\n" + "Options are: 1. (Yes), 2. (Help), 3. (No, exit).\n" + "> ");
                int option = keyboard.nextInt();
                keyboard.nextLine();

                switch (option) {

                    // 1. Call to BjWork class and start a new hand
                    case 1:
                        // Initialize decks if its first round or if you have no cards
                        if (roundsPlayed == 0 || noCards) {
                            BjWork.deck = Shuffle.shuffle();
                            BjWork.game();
                        } else {
                            BjWork.game();
                        }
                        roundsPlayed++;
                        break;
                    // 2. Help menu
                    case 2:
                        boolean inHelp = true;
                        while (inHelp) {
                            System.out.print("Enter '1' to learn how to play, Basic strategy sheet '2' or 3 to exit help menu.\n" + "> ");
                            try {
                                int helpOption = keyboard.nextInt();
                                keyboard.nextLine();
                                switch (helpOption) {
                                    case 1:
                                        System.out.println("Blackjack is a simple card game where your only opponent is the dealer.\n" +
                                                "Your goal is to beat the dealer, you can do this by simply having more cards then the dealer, or if the dealer 'busts'\n" +
                                                "'bust' in Blackjack means you have greater then 21 cards, you may also lose this way.\n" +
                                                "The point of the game is to have a total card value closest to 21 without busting or greater then the dealer\n" +
                                                "You may lose if you 'bust' and the dealer has more cards then you.\n" +
                                                "If you have a tie in card value its a 'Stand', it means you get to play again and keep your bets\n" +
                                                "The dealer MUST stand at 17 and up, you may draw as many cards as you desire.\n" +
                                                "Hit = draw a card\n" +
                                                "Stand = you wish to no longer draw cards and see the hidden dealer card\n" +
                                                "Double = you double your placed bet and you have to draw 1 last card\n");
                                        break;
                                    case 2:
                                        System.out.println("i might actually write it down one day..\n" +
                                                "for now you just get the link:\n" +
                                                "https://www.blackjackapprenticeship.com/blackjack-strategy-charts/\n");
                                        break;
                                    case 3:
                                        System.out.println("Exiting help menu..\n");
                                        inHelp = false;
                                        break;
                                    default:
                                        System.out.println("Invalid number, options are from 1-3!");
                                }
                            } catch (InputMismatchException ime) {
                                System.out.println("Please enter a valid number");
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
                        System.out.println("Invalid number, options are from 1-3!");
                }

            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number!");
                keyboard.nextLine();
            }
        } while (LOOP);
        if (roundsPlayed > 35) {
            System.out.println(".. wow you might have a problem you should take a break..");
        }
        System.out.println("You played: " + roundsPlayed + " hands!\n");
        /*
         * TODO: Show each player win/loss stats
         * System.out.println("You won: " + roundsPlayed + " hands\n");
         * System.out.println("You lost: " + roundsPlayed + " hands\n");
         */

        System.out.println("Thank you for using my program!");
        System.out.println("Program created by Vincent Welbourne");

        keyboard.close();
    }
}