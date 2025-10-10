/*
 * Program created by Vincent Welbourne
 * vincent.vw04@gmail.com
 * Code description: This is a recreation of the famous card game called 'Blackjack' i have implemented my own
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
    private static boolean LOOP = true;

    /**
     * This is where the game starts you get options from start game, help and exit.
     * Checks if its the first game or if out of cards to initialize a deck and or
     * shuffle.
     *
     * @param args no commandline arguments
     */
    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);
        int roundsPlayed = 0;

        System.out.println("Welcome to BlackJack\n" + "Would you like to play a new hand?\n");
        do {
            try {

                System.out.println("Options are: 1. (Yes), 2. (Help), 3. (No, exit).");
                int option = keyboard.nextInt();

                switch (option) {
                    // 1. Call to BjWork class and start a new hand
                    case 1:
                        if (roundsPlayed == 0) { // Initialize decks if its first round
                            Shuffle.shuffle(roundsPlayed);
                        } /*
                         * else if (out of cards) {
                         * Shuffle.shuffle(roundsPlayed);
                         * cards == 0;
                         * }
                         */

                        // TODO: add game logic so call onto BjWork System.out.println();
                        roundsPlayed++;
                        break;
                    // 2. Help menu
                    case 2:
                        System.out.println("");// TODO: Give basic menu help and make it give an option for further
                        // instruction like how to play and basic strategy
                        // CASE 1 how to play
                        // CASE 2 bj basic strategy sheet
                        break;
                    // 3. Exit Program
                    case 3:
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
        System.out.println("You played a total of: " + roundsPlayed + " round(s)!");
        if (roundsPlayed > 35)
            System.out.println(".. wow you might have a problem take a break..");
        System.out.println("");

        System.out.println("Thank you for using my program!");
        // Have a print that tell the user "You played: "#" hands!
        System.out.println("Program created by Vincent Welbourne");

        keyboard.close();
        System.out.close();
    }
}