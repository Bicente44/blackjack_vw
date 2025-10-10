/*
 * Program created by Vincent Welbourne
 * vincent.vw04@gmail.com
 *
 */
import java.util.LinkedList;

/**
 *
 * This Class is the deck of cards array and object.
 * A deck consists of 4 arrays with the types: Clubs, Spades, Diamonds, Hearts.
 *
 * @author Vincent Welbourne
 */
public class Deck {




    public class Diamonds extends Card {
        private String suit;	// Clubs, Diamonds etc..
        private int value;		// Worth of the card
        private String rank; 	// Ace, 2, king etc..
    }

    public class Hearts extends Card {
        private String suit;	// Clubs, Diamonds etc..
        private int value;		// Worth of the card
        private String rank; 	// Ace, 2, king etc..
    }

    public class Spades extends Card {
        private String suit;	// Clubs, Diamonds etc..
        private int value;		// Worth of the card
        private String rank; 	// Ace, 2, king etc..
    }

    public class Clubs extends Card {
        private String suit;	// Clubs, Diamonds etc..
        private int value;		// Worth of the card
        private String rank; 	// Ace, 2, king etc..
    }


    /**
     * Card suit array.
     */
    private final String[] SUITS = {"Clubs", "Spades", "Diamonds", "Hearts"}; // 4
    /**
     * Card rank array.
     */
    private final String[] RANKS = {"Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King"}; // 13
    /**
     * LinkedList deck containing all cards.
     */
    private LinkedList<String> deck;

    /**
     * Assigning each card its value
     *
     * @param numOfDecks
     */
    public Deck(int numOfDecks) {
        deck = new LinkedList<>(); // making the array the proper size for the amount of decks

        for (int i = 0; i < numOfDecks; i++) {
            for (String suit : SUITS) {
                for (String rank : RANKS) {
                    deck.add(rank + " of " + suit);
                }
            }
        }
    }
    /**
     * Returns the full deck as a LinkedList.
     *
     * @return
     */
    public LinkedList<String> getDeck() {
        return deck;
    }
}
