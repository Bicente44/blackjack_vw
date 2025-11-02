/*
 * Program created by Vincent Welbourne
 * vincent.vw04@gmail.com
 *
 */

import java.util.Collections;
import java.util.List;

/**
 * This class takes care of mixing the cards, and of how many decks are in use
 * for the game.
 *
 * @author Vincent Welbourne
 */
public class Shuffle {
    public static int numOfDecks;

    /**
     *
     * The actual shuffle object and method
     *
     * @return null
     */
    public static List<Card> shuffle() {

        if (BjDriver.roundsPlayed == 0) {
            System.out.println("How many decks would you like to play with?");
            do {
                try {
                    System.out.print("You may choose a number from 1-8.\n" + "> ");
                    numOfDecks = BjDriver.keyboard.nextInt();
                    break;
                } catch (Exception e) {
                    System.out.println("Please enter a valid number!");
                    BjDriver.keyboard.nextLine();
                }
            } while (true);
        }
        Deck deck = new Deck(numOfDecks);
        List<Card> deckList = deck.getDeck();
        BjDriver.noCards = false;

        // shuffle the deck
        Collections.shuffle(deckList);
        System.out.println("Deck shuffled...");


        return deckList;

    }
}