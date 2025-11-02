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

    public static class Diamonds extends Card {
        public Diamonds(String rank, int value) {
            super(value, "Diamonds", rank);
        }
    }

    public static class Hearts extends Card {
        public Hearts(String rank, int value) {
            super(value, "Hearts", rank);
        }
    }

    public static class Spades extends Card {
        public Spades(String rank, int value) {
            super(value, "Spades", rank);
        }
    }

    public static class Clubs extends Card {
        public Clubs(String rank, int value) {
            super(value, "Clubs", rank);
        }
    }


    /**
     * LinkedList deck containing all cards.
     */
    private final LinkedList<Card> deck;

    /**
     * Assigning each card its value
     *
     * @param numOfDecks number of decks chosen by the user.
     */
    public Deck(int numOfDecks) {
        deck = new LinkedList<>();

        for (int i = 0; i < numOfDecks; i++) {
            /*
             * Card suit array.
             */
             final String[] SUITS = {"Clubs", "Spades", "Diamonds", "Hearts"}; // 4

            for (String suit : SUITS) {
                /*
                 * Card rank array.
                 */
                final String[] RANKS = {"Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King"}; // 13
                for (String rank : RANKS) {
                    int value = getValue(rank);
                    switch (suit) {
                        case "Clubs":
                            deck.add(new Clubs(rank, value));
                            break;
                        case "Spades":
                            deck.add(new Spades(rank, value));
                            break;
                        case "Diamonds":
                            deck.add(new Diamonds(rank, value));
                            break;
                        case "Hearts":
                            deck.add(new Hearts(rank, value));
                            break;
                        default:
                            // shouldn't happen
                            break;
                    }
                }
            }
        }
    }

    /**
     * Takes the rank and converts it to its numerical value
     * @param rank of card
     * @return value of card
     */
    private static int getValue(String rank) {
        switch (rank) {
            case "Ace":
                return 11;
            case "Jack", "Queen", "King":
                return 10;
            default:
                try {
                    return Integer.parseInt(rank);
                } catch (NumberFormatException e) {
                    return 0;
                }
        }
    }

    /**
     * Returns the full deck as a LinkedList of Card objects.
     * @return deck
     */
    public LinkedList<Card> getDeck() {
        return deck;
    }
}
