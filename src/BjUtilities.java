/*
 * Program created by Vincent Welbourne
 * vincent.vw04@gmail.com
 *
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Hand class is used as the utilities for BjWork in relation to each hand.
 * TODO Will probably modify and add the rest of the utilities to free up BjWork
 */
public class BjUtilities {
    private List<Card> cards = new ArrayList<>();
    private int cardTotal = 0;
    private int aceCount = 0;

    public BjUtilities() {
        this.cards = new ArrayList<>();
        this.cardTotal = 0;
        this.aceCount = 0;
    }

    public List<Card> getCards() {
        return cards;
    }
    public int getTotal() {
        return cardTotal;
    }

    public void addCard(Card card) {
        if (card == null) return;
        cards.add(card);
        cardTotal += card.getValue();
        if ("Ace".equalsIgnoreCase(card.getRank())) aceCount++;

        // If over 21 and any aces counted as 11, turn them into 1
        while (cardTotal > 21 && aceCount > 0) {
            cardTotal -= 10;
            aceCount--;
        }
    }

    public static synchronized Card drawCard() {
        if (BjWork.deck == null || BjWork.deck.isEmpty()) {
            System.out.println("Deck empty, reshuffling.");
            BjWork.deck = new LinkedList<>(Shuffle.shuffle());
        }
        return BjWork.deck.remove(0);
    }

    /**
     * Method to deal with the first and second pass at game start
     */
    public static void playerPass(){
        if (BjWork.hands.size() != BjWork.players.size()) {
            throw new IllegalStateException("hands and players lists must be same length");
        }
        int numPlayers = BjWork.players.size();
        for (int i = 1; i < numPlayers; i++) {
            Card c = BjUtilities.drawCard();
            BjWork.hands.get(i).addCard(c);
            System.out.println(BjWork.players.get(i).getPlayerName() + ": " + c);
        }
    }

    /**
     * Clear hand for reuse between rounds.
     */
    public void clear() {
        cards.clear();
        cardTotal = 0;
        aceCount = 0;
    }

    public int getAceCount() {
        return aceCount;
    }

    @Override
    public String toString() {
        return cards.toString() + " total = " + cardTotal;
    }
}
