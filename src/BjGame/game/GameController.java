package BjGame.game;

import BjGame.Debug;
import BjGame.shared.Card;
import BjGame.shared.Player;
import BjGame.ui.GameUI;
import BjGame.ui.PlayerSeat;

import java.util.*;

/**
 * This class is the Blackjack game controller class, it is what composes the game structure and brings it all together.
 * It's essentially the controller logic that connects UI to models.
 *
 * @author Vincent Wrlbourne
 */
public class GameController {
    GameSession gameSession = new GameSession();
    private GameUI gameUI;
    private int currentPlayerIndex = 1;

    public void setUI(GameUI gameUI) {
        this.gameUI = gameUI;
    }

    /**
     * Method to draw a card from the static deck list in BjWork
     * @return BjGame.shared.Card drawn
     */
    public synchronized Card drawCard() {
        if (gameSession.deck == null || gameSession.deck.isEmpty()) {
            Debug.println("BjGame.shared.Deck empty, reshuffling.");
            gameSession.deck = Shuffle.shuffle(gameSession.numOfDecks);
        }
        return gameSession.deck.remove(0);
    }

    /**
     * Check for blackjack & handles for each player including dealer
     */
    public void checkBlackjack() {
        int numPlayers = gameSession.players.size();

        if (gameSession.hands.size() != gameSession.players.size()) {
            throw new IllegalStateException("hands and players lists must be same length");
        }
        if (gameSession.bjStand.size() != numPlayers) {
            gameSession.bjStand.clear();
            for (int i = 0; i < numPlayers; i++) gameSession.bjStand.add(false);
        }
        HandManager dealerHand = gameSession.hands.get(0);
        boolean dealerBJ = (dealerHand.getCards().size() == 2 && dealerHand.getTotal() == 21);

        if (dealerBJ) {
            Debug.println("\nDealer has blackjack, all players stand.");
            // force every player to stand
            for (int i = 1; i < numPlayers; i++) {
                gameSession.bjStand.set(i, true);
            }
            // Mark the round as finished and resolve
            gameSession.roundOver = true;
            dealerPlay();
            return;
        }
        for (int i = 1; i < numPlayers; i++) {
            HandManager ph = gameSession.hands.get(i);
            boolean playerBJ = (ph.getCards().size() == 2 && ph.getTotal() == 21);
            gameSession.bjStand.set(i, playerBJ);
            if (playerBJ) {
                Debug.println(gameSession.players.get(i).getPlayerName() + " has Blackjack and will stand.");
            }
        }
    }

    /**
     * Method to deal with the first and second pass at BjGame.game start
     */
    public void playerPass(){
        if (gameSession.hands.size() != gameSession.players.size()) {
            throw new IllegalStateException("hands and players lists must be same length");
        }
        int numPlayers = gameSession.players.size();
        for (int i = 1; i < numPlayers; i++) {
            Card c = drawCard();
            gameSession.hands.get(i).addCard(c);
            Debug.println(gameSession.players.get(i).getPlayerName() + ": " + c);
        }
    }

    public void startRound() {
        if (gameSession.deck == null || gameSession.deck.size() < (gameSession.players.size() * 2) + 2) {
            gameSession.deck = Shuffle.shuffle(gameSession.numOfDecks);
        }
        gameStart();
    }

    /**
     * Starts BjGame.game after bets are placed allows users to interact and place bets
     */
    public void gameStart() {
        for (HandManager h : gameSession.hands) h.clear();
        gameSession.roundOver = false;
        gameSession.bjStand.clear();
        for (int i = 0; i < gameSession.players.size(); i++) gameSession.bjStand.add(false);

        playerPass();

        gameSession.dealerUp = drawCard();
        gameSession.hands.get(0).addCard(gameSession.dealerUp);

        playerPass();

        Card dealerHidden = drawCard();
        gameSession.hands.get(0).addCard(dealerHidden);

        for (int i = 0; i < gameSession.players.size(); i++) {
            gameSession.players.get(i).setCardTotal(gameSession.hands.get(i).getTotal());
        }

        if (gameUI != null) {
            gameUI.getDealer().clearHand();
            Card up = gameSession.dealerUp;
            gameUI.getDealer().addCard(up.getRank(), up.getSuit());
            gameUI.getDealer().addHiddenCard();

            for (int i = 1; i < gameSession.players.size(); i++) {
                PlayerSeat seat = gameUI.getSeat(i - 1);
                seat.clearHand();
                for (Card c : gameSession.hands.get(i).getCards()) {
                    seat.addCard(c.getRank(), c.getSuit());
                }
                seat.setTotal(gameSession.hands.get(i).getTotal(), false);
            }
        }
        checkBlackjack();

        if (!gameSession.roundOver) {
            currentPlayerIndex = 1;
            playerTurn();
        }
    }

    /**
     * Method used to call when you want to get playing options,
     * (Hit, Stand, Double and Split) --Eventually add surrender and insurance
     *
     */
    public void playerTurn() {
        while (currentPlayerIndex < gameSession.players.size()) {
        boolean standing = gameSession.bjStand.size() > currentPlayerIndex
                && gameSession.bjStand.get(currentPlayerIndex);
        if (!standing) break;
        currentPlayerIndex++;
    }

        if (currentPlayerIndex >= gameSession.players.size()) {
        // All players done
        if (gameUI != null) gameUI.setActionsEnabled(false);
        dealerPlay();
        return;
    }

    // Activate this player's seat
        if (gameUI != null) {
            gameUI.setActionsEnabled(true);
        PlayerSeat seat = gameUI.getSeat(currentPlayerIndex - 1);
        seat.setActive(true);
        seat.startTimer(30);
    }
}

    public void hit() {
        Player p = gameSession.players.get(currentPlayerIndex);
        HandManager hand = gameSession.hands.get(currentPlayerIndex);
        Card draw = drawCard();
        boolean busted = hand.hitAction(p, draw);
        Debug.println(p.getPlayerName() + " hits: " + draw + " total: " + hand.getTotal());

        if (gameUI != null) {
            PlayerSeat seat = gameUI.getSeat(currentPlayerIndex - 1);
            seat.addCard(draw.getRank(), draw.getSuit());
            seat.setTotal(hand.getTotal(), busted);
            if (busted) {
                seat.showNotification("BUST");
                seat.setActive(false);
            }
        }

        if (busted) {
            currentPlayerIndex++;
            playerTurn();
        }
    }

    public void stand() {
        Player p = gameSession.players.get(currentPlayerIndex);
        Debug.println(p.getPlayerName() + " stands with " + gameSession.hands.get(currentPlayerIndex).getTotal());
        gameSession.bjStand.set(currentPlayerIndex, true);

        if (gameUI != null) {
            gameUI.getSeat(currentPlayerIndex - 1).setActive(false);
            gameUI.getSeat(currentPlayerIndex - 1).showNotification("STAND");
        }

        currentPlayerIndex++;
        playerTurn();
    }

    public void doubleDown() {
        Player p = gameSession.players.get(currentPlayerIndex);
        HandManager hand = gameSession.hands.get(currentPlayerIndex);

        if (p.getBet() <= 0 || p.getMoney() < p.getBet()) {
            Debug.println("Cannot double — invalid bet state.");
            return;
        }

        Card draw = drawCard();
        boolean busted = hand.doubleAction(p, draw);
        Debug.println(p.getPlayerName() + " doubles. Drew: " + draw);

        if (gameUI != null) {
            PlayerSeat seat = gameUI.getSeat(currentPlayerIndex - 1);
            seat.addCard(draw.getRank(), draw.getSuit());
            seat.setTotal(hand.getTotal(), busted);
            seat.setBet((int) p.getBet());
            seat.setActive(false);
            if (busted) seat.showNotification("BUST");
        }

        currentPlayerIndex++;
        playerTurn();
    }

    /**
     * Once all players finish their move, dealer reveals hidden card and acts.
     */
    public void dealerPlay() {
        HandManager dealerHand = gameSession.hands.get(0);
        Card hidden = dealerHand.getCards().get(1);

        if (gameUI != null) {
            gameUI.getDealer().revealHiddenCard(hidden.getRank(), hidden.getSuit());
        }
        Debug.println("Dealer reveals: " + hidden + " total: " + dealerHand.getTotal());

        while (dealerHand.getTotal() < 17) {
            Card draw = drawCard();
            dealerHand.addCard(draw);
            Debug.println("Dealer draws: " + draw + " total: " + dealerHand.getTotal());
            if (gameUI != null) gameUI.getDealer().addCard(draw.getRank(), draw.getSuit());
        }

        if (gameUI != null) gameUI.getDealer().setTotal(dealerHand.getTotal());
        gameSession.players.get(0).setCardTotal(dealerHand.getTotal());
        endGameResults();
    }

    /**
     * Compares dealer hand to each player hand and assigns wins/losses/pushes.
     * This method assumes dealer is gameSession.players.get(0) and hands[0] is dealer hand.
     */
    private void endGameResults() {
        Player dealer = gameSession.players.get(0);
        HandManager dealerHand = gameSession.hands.get(0);
        int dealerTotal = dealerHand.getTotal();
        boolean dealerBlackjack = (dealerHand.getCards().size() == 2 && dealerTotal == 21);

        for (int i = 1; i < gameSession.players.size() && i < gameSession.hands.size(); i++) {
            Player p = gameSession.players.get(i);
            HandManager h = gameSession.hands.get(i);
            int playerTotal = h.getTotal();
            double bet = p.getBet(); // amount already deducted at bet time (placeBet)

            Debug.print(p.getPlayerName() + " (" + playerTotal + ") vs Dealer (" + dealerTotal + "): ");

            boolean playerBlackjack = (h.getCards().size() == 2 && playerTotal == 21);

            if (playerTotal > 21) {
                // player busted dealer wins
                Debug.println(p.getPlayerName() + " busted. Dealer wins.");
                dealer.addWin();
                p.addLoss();
            } else if (playerBlackjack && dealerBlackjack) {
                // Both have natural blackjack, push
                Debug.println(p.getPlayerName() + " and Dealer have Blackjack, bets returned.");
                p.adjustMoney(bet);
            } else if (playerBlackjack) {
                // BjGame.shared.Player natural blackjack
                Debug.println(p.getPlayerName() + " has Blackjack! Pays 3:2.");
                p.addWin();
                dealer.addLoss();
                double payout = Math.round(bet * 2.5); // round to nearest whole dollar
                p.adjustMoney(payout);
            } else if (dealerBlackjack) {
                // Dealer natural blackjack
                Debug.println("Dealer has Blackjack. " + p.getPlayerName() + " loses.");
                dealer.addWin();
                p.addLoss();
            } else if (dealerTotal > 21) {
                // dealer busted, player wins
                Debug.println("Dealer busted. " + p.getPlayerName() + " wins.");
                p.addWin();
                dealer.addLoss();
                p.adjustMoney(bet * 2); // return bet + winnings
            } else if (playerTotal > dealerTotal) {
                Debug.println(p.getPlayerName() + " wins.");
                p.addWin();
                dealer.addLoss();
                p.adjustMoney(bet * 2);
            } else if (playerTotal < dealerTotal) {
                Debug.println("Dealer wins.");
                dealer.addWin();
                p.addLoss();
            } else {
                Debug.println("Push. Bets are returned.");
                p.adjustMoney(bet);
            }
            // reset player's bet for next round
            p.setBet(0);
        }
        // Clear totals for next round
        for (Player p : gameSession.players) p.setCardTotal(0);
    }
}