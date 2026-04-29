package BjGame.game;

import BjGame.Debug;
import BjGame.shared.Card;
import BjGame.shared.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * HandManager class is used as the utilities for GameSession in relation to each hand.
 */
public class HandManager {
    private List<Card> cards = new ArrayList<>();
    private int cardTotal = 0;
    private int aceCount = 0;

    public HandManager() {
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

    /**
     * Adds card to a hand
     *
     * @param card
     */
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
     * Method for the hit action to draw a card for the player used in 'Hit' and 'Double'
     * @param p BjGame.shared.Player
     * @param draw Card drawn
     * @return true for bust else false
     */
    public boolean hitAction(Player p, Card draw) {
        this.addCard(draw);
        p.setCardTotal(this.getTotal());
        Debug.print(p.getPlayerName() + " drew: " + draw + " (total: " + this.getTotal() + ")");
        if (this.getTotal() > 21) {
            Debug.print(p.getPlayerName() + " busted!");
            return true;
        }
        return false;
    }

    /**
     * Attempt to double the player's bet, perform a single hit, and force stand.
     * @param p the BjGame.shared.Player to double for
     * @return Boolean.TRUE if busted, FALSE if not busted, null if double was not allowed.
     */
    public Boolean doubleAction(Player p, Card doubleCard) {
        if (p == null) return null;
        double currentBet = p.getBet();
        p.adjustMoney(-currentBet);
        p.setBet(currentBet * 2);
        Debug.print(p.getPlayerName() + " doubles to $" + p.getBet() + ". Remaining: $" + (int)p.getMoney());
        return this.hitAction(p, doubleCard);
    }

    /**
     * Clear hand and bets for reuse between rounds.
     */
    public void clear() {
        cards.clear();
        cardTotal = 0;
        aceCount = 0;
    }

    /**
     * Returns the count of Aces in a hand
     *
     * @return Count of Aces
     */
    public int getAceCount() {
        return aceCount;
    }

    @Override
    public String toString() {
        return cards.toString() + " total = " + cardTotal;
    }
}
