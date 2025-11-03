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

    /**
     * Method to draw a card from the static deck list in BjWork
     * @return Card drawn
     */
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
     * Check for blackjack
     */
    public static void checkBlackjack() {
        int numPlayers = BjWork.players.size();

        if (BjWork.hands.size() != BjWork.players.size()) {
            throw new IllegalStateException("hands and players lists must be same length");
        }
        if (BjWork.bjStand.size() != numPlayers) {
            BjWork.bjStand.clear();
            for (int i = 0; i < numPlayers; i++) BjWork.bjStand.add(false);
        }
        BjUtilities dealerHand = BjWork.hands.get(0);
        boolean dealerBJ = (dealerHand.getCards().size() == 2 && dealerHand.getTotal() == 21);

        if (dealerBJ) {
            System.out.println("\nDealer has blackjack, all players stand.");
            // force every player to stand
            for (int i = 1; i < numPlayers; i++) {
                BjWork.bjStand.set(i, true);
            }
            // Mark the round as finished and resolve
            BjWork.roundOver = true;
            BjWork.dealerPlay();
            return;
        }
        for (int i = 1; i < numPlayers; i++) {
            BjUtilities ph = BjWork.hands.get(i);
            boolean playerBJ = (ph.getCards().size() == 2 && ph.getTotal() == 21);
            BjWork.bjStand.set(i, playerBJ);
            if (playerBJ) {
                System.out.println(BjWork.players.get(i).getPlayerName() + " has Blackjack and will stand.");
            }
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
