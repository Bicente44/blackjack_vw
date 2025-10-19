/*
 * Program created by Vincent Welbourne
 * vincent.vw04@gmail.com
 *
 */
public abstract class Card {
    /**
     * The value of the card in the deck
     * ex: 10 of spades = 10, king of hearts = 10.
     */
    private final int value;		// Worth of the card

    /**
     * The suit of the card in the deck
     * ex: clubs, diamonds...
     */
    private final String suit;	// Clubs, Diamonds etc..

    /**
     * The rank of the card in the deck
     * ex: ace, 2, king...
     */
    private final String rank; 	// Ace, 2, king etc..

    /**
     * Card constructor. Sets the fields of the requirements of what makes a card.
     * @param value Value of the actual card..
     * @param suit The suit of the card ex: Clubs, Diamonds..
     * @param rank The rank of the card ex: Ace, 2, King..
     */
    protected Card(int value, String suit, String rank) {
        this.value = value;
        this.suit = suit;
        this.rank = rank;
    }

    /**
     * Getter for Value
     * @return value
     */
    public int getValue() {
        return value;
    }

    /**
     * Getter for Suit
     * @return Suit
     */
    public String getSuit() {
        return suit;
    }

    /**
     * Getter for Rank
     * @return Rank
     */
    public String getRank() {
        return rank;
    }

    /**
     * To get the string name of the card (Ace of Hearts)
     * @return String
     */
    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}
