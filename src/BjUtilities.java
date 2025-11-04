/*
 * Program created by Vincent Welbourne
 * vincent.vw04@gmail.com
 *
 */

import java.util.ArrayList;
import java.util.InputMismatchException;
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
     * Method for the hit action to draw a card for the player used in 'Hit' and 'Double'
     * @param p Player
     * @return true for bust else false
     */
    public boolean hitAction(Player p) {
        Card draw = BjUtilities.drawCard();
        this.addCard(draw);
        p.setCardTotal(this.getTotal());
        System.out.println(p.getPlayerName() + " drew: " + draw + " (total: " + this.getTotal() + ")");
        if (this.getTotal() > 21) {
            System.out.println(p.getPlayerName() + " busted!");
            return true;
        }
        return false;
    }

    /**
     * Attempt to double the player's bet, perform a single hit, and force stand.
     * @param p the Player to double for
     * @return Boolean.TRUE if busted, FALSE if not busted, null if double was not allowed.
     */
    public Boolean doubleAction(Player p) {
        if (p == null) return null;
        double currentBet = p.getBet();
        if (currentBet <= 0) {
            System.out.println(p.getPlayerName() + " has no active bet to double.");
            return null;
        }
        if (p.getMoney() < currentBet) {
            System.out.println(p.getPlayerName() + " cannot double — insufficient funds.");
            return null;
        }
        p.adjustMoney(-currentBet);
        p.setBet(currentBet * 2);
        System.out.println(p.getPlayerName() + " doubles to $" + p.getBet() + ". Remaining: $" + (int)p.getMoney());
        // Double forces a stand, exitHand = true regardless of busted
        return this.hitAction(p);
    }

    public static void placeBet(Player p) {
        java.util.Scanner keyboard = BjDriver.keyboard;
        final int MIN_BET = 5; // TODO CHANGE LATER TO BE SET ON INITIALIZATION
        int bet = 0;

        if (p == null) return;
        // If player has no money, skip them
        if (p.getMoney() <= 0) {
            System.out.println(p.getPlayerName() + " has no money, bet is 0.");
            p.setBet(0);
            return;
        }
        while (true) {
            System.out.print(p.getPlayerName() + " - You have $" + (int)p.getMoney() + ". Enter bet (min $" + MIN_BET + "): ");
            try {
                bet = keyboard.nextInt();
                // Validate
                if (bet < MIN_BET) {
                    System.out.println("Bet must be at least $" + MIN_BET + ".");
                    continue;
                }
                if (bet > (int)p.getMoney()) {
                    System.out.println("Insufficient funds. You only have $" + (int)p.getMoney() + ".");
                    continue;
                }
                // valid bet
                break;
            } catch (InputMismatchException e) {
                System.out.println("Please enter a whole number for the bet.");
                keyboard.nextLine();
            }
        }
        // store bet and deduct from player's money now
        p.setBet(bet);
        p.adjustMoney(-bet);
        System.out.println(p.getPlayerName() + " placed $" + bet + ". Remaining: $" + (int)p.getMoney());
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
     * Clear hand and bets for reuse between rounds.
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
