/*
 * Program created by Vincent Welbourne
 * vincent.vw04@gmail.com
 *
 */
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * This class takes care of mixing the cards, and of how many decks are in use
 * for the game.
 *
 * @author Vincent Welbourne
 */
public class Shuffle {
    /**
     *
     * The actual shuffle object and method
     *
     * @param roundsPlayed
     * @return null
     */
    public static List<String> shuffle(int roundsPlayed) {
        Scanner keyboard = new Scanner(System.in);
        int numOfDecks = 1;

        if (roundsPlayed == 0) {

            System.out.println("How many decks would you like to play with?");
            do {
                try {
                    System.out.println("You may choose a number from 1-8.");
                    numOfDecks = keyboard.nextInt();

                    break;
                } catch (Exception e) {
                    System.out.println("Please enter a valid number!");
                    keyboard.nextLine();
                }
            } while (true);
        }

        Deck deck = new Deck(numOfDecks);
        List<String> deckList = deck.getDeck();

        // shuffle the deck
        Collections.shuffle(deckList);
        keyboard.close();
        return deckList;

    }
}
