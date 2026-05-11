package BjGame.game;/*
 * Program created by Vincent Welbourne
 * vincent.vw04@gmail.com
 *
 */

import BjGame.Debug;
import BjGame.shared.Card;
import BjGame.shared.Deck;

import java.util.Collections;
import java.util.List;

/**
 * This class takes care of mixing the cards, and of how many decks are in use
 * for the BjGame.game.
 *
 * @author Vincent Welbourne
 */
public class Shuffle {

    /**
     * Builds and shuffles a deck of the given number of standard 52-card decks.
     * @param numOfDecks number of decks to use (1-8)
     * @return shuffled list of cards
     */
    public static List<Card> shuffle(int numOfDecks) {
        Deck deck = new Deck(numOfDecks);
        List<Card> deckList = deck.getDeck();
        Collections.shuffle(deckList);
        Debug.println("Deck shuffled (" + numOfDecks + " deck(s)).");
        return deckList;
    }
}