package game;

import shared.Card;
import shared.Player;

import java.util.*;

/**
 * This class is the Blackjack work class, it is what composes the game and brings it all together.
 * Utilizes the utility methods in BjUtilities.
 *
 * @author Vincent Wrlbourne
 */
public class GameSession {
    public static final String MENU_ACTIONS = "Select an option for your next move (1-4).\n"
            + "1. Hit\n"
            + "2. Stand\n"
            + "3. Double\n"
            + "4. Split\n" +
            "> ";
    public List<Card> deck;
    public List<Player> players;
    public List<HandManager> hands;
    public Card dealerUp;
    public List<Boolean> bjStand;
    public boolean roundOver;
    HandManager handManager = new HandManager();

    public GameSession() {
        this.players = new ArrayList<>();
        this.hands   = new ArrayList<>();
        this.bjStand = new ArrayList<>();
        this.roundOver = false;
    }

    /**
     * Method to draw a card from the static deck list in BjWork
     * @return shared.Card drawn
     */
    public synchronized Card drawCard() {
        if (deck == null || deck.isEmpty()) {
            System.out.println("shared.Deck empty, reshuffling.");
            deck = new LinkedList<>(Shuffle.shuffle());
        }
        return deck.remove(0);
    }

    /**
     * Check for blackjack & handles for each player including dealer
     */
    public void checkBlackjack() {
        int numPlayers = players.size();

        if (hands.size() != players.size()) {
            throw new IllegalStateException("hands and players lists must be same length");
        }
        if (bjStand.size() != numPlayers) {
            bjStand.clear();
            for (int i = 0; i < numPlayers; i++) bjStand.add(false);
        }
        HandManager dealerHand = hands.get(0);
        boolean dealerBJ = (dealerHand.getCards().size() == 2 && dealerHand.getTotal() == 21);

        if (dealerBJ) {
            System.out.println("\nDealer has blackjack, all players stand.");
            // force every player to stand
            for (int i = 1; i < numPlayers; i++) {
                bjStand.set(i, true);
            }
            // Mark the round as finished and resolve
            roundOver = true;
            dealerPlay();
            return;
        }
        for (int i = 1; i < numPlayers; i++) {
            HandManager ph = hands.get(i);
            boolean playerBJ = (ph.getCards().size() == 2 && ph.getTotal() == 21);
            bjStand.set(i, playerBJ);
            if (playerBJ) {
                System.out.println(players.get(i).getPlayerName() + " has Blackjack and will stand.");
            }
        }
    }

    /**
     * Allows a p player place a bet
     *
     * @param p Player
     */
    public void placeBet(Player p) {
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
     * Method to deal with the first and second pass at game start
     */
    public void playerPass(){
        if (hands.size() != players.size()) {
            throw new IllegalStateException("hands and players lists must be same length");
        }
        int numPlayers = players.size();
        for (int i = 1; i < numPlayers; i++) {
            Card c = drawCard();
            hands.get(i).addCard(c);
            System.out.println(players.get(i).getPlayerName() + ": " + c);
        }
    }

    /**
     * Handles players, starts a game and does cleanup for next round
     */
    public void game() {
        if (deck == null) {
            System.out.println("shared.Deck not initialized or empty, creating and shuffling now.");
            deck = Shuffle.shuffle();
            // Calculate player list size plus dealer card amount to see if game may start or reshuffle
        } else if (deck.size() < (players.size() * 2) + 2) {
            deck = Shuffle.shuffle();
        }
        if (players.isEmpty() || hands.size() != players.size()) {
            System.out.println("Debug ahh, player list is broken");
        }

        // ALL PLAYERS PLACE BETS HERE
        if (players.size() > 1) {
            for (int i = 1; i < players.size(); i++) {
                Player p = players.get(i);
                placeBet(p);
            }
        }

        // Deal initial cards
        gameStart();

        // Check for round end with dealer or player blackjack(s)
        if (!roundOver) {
            for (int i = 1; i < players.size(); i++) {
                if (roundOver) break;
                playerTurn(i);
            }
            if (!roundOver) {
                dealerPlay();
            }
        }
        for (HandManager h : hands) h.clear();
        for (Player p : players) p.setCardTotal(0);
    }

    /**
     * Starts game after bets are placed allows users to interact and place bets
     */
    public void gameStart() {
        // Clear for game start
        for (HandManager h : hands) h.clear();
        roundOver = false;
        bjStand.clear();
        for (int i = 0; i < players.size(); i++) bjStand.add(false);

        int numPlayers = players.size(); // including dealer at index 0

        // First pass: deal one card to each player
        playerPass();

        // TODO? Could make a method for dealer pass in the future...
        // Dealer upcard
        dealerUp = drawCard();
        hands.get(0).addCard(dealerUp);
        System.out.println("Dealer: " + dealerUp);

        // Second pass: deal second card to each player
        playerPass();

        // Dealer hidden card
        Card dealerHidden = drawCard();
        hands.get(0).addCard(dealerHidden);
        System.out.println("Dealer: ?? (hidden)");

        // Update totals on shared.Player objects
        for (int i = 0; i < numPlayers; i++) {
            players.get(i).setCardTotal(hands.get(i).getTotal());
        }
        // Check for blackjack to prevent further bets
        checkBlackjack();
    }

    /**
     * Method used to call when you want to get playing options,
     * (Hit, Stand, Double and Split) --Eventually add surrender and insurance
     *
     */
    public void playerTurn(int playerIndex) {
        int action = 0;
        Player p = players.get(playerIndex);
        HandManager hand = hands.get(playerIndex);
        boolean exitHand = false;

        if (roundOver) {
            return;
        }

        boolean playerStand = (bjStand.size() > playerIndex && bjStand.get(playerIndex));
        if (playerStand) {
            return;
        }

        while (!exitHand && !roundOver) {
            System.out.print("\nDealer upcard: " + dealerUp.getValue());
            System.out.println("\n" + p.getPlayerName() + " total: " + hand.getTotal());
            System.out.print(MENU_ACTIONS);
            try {
                action = BjDriver.keyboard.nextInt();
                switch (action) {
//		Hit
                    case 1:
                        // Check if bust, else continue
                        if (hand.hitAction(p, drawCard())) {
                            exitHand = true;
                        }
                        break;
//		Stand
                    case 2:
                        System.out.println(p.getPlayerName() + " stands with " + hand.getTotal() + "\n");
                        exitHand = true;
                        break;
//		Double
                    case 3:
                        if (p.getBet() <= 0) {
                            System.out.println(p.getPlayerName() + " has no active bet to double.");
                            break;
                        }
                        if (p.getMoney() < p.getBet()) {
                            System.out.println(p.getPlayerName() + " cannot double — insufficient funds.");
                            break;
                        }
                        Card doubleCard = drawCard();
                        Boolean dblResult = hand.doubleAction(p,doubleCard);
                        if (dblResult == null) {
                            break;
                        } else {
                            // Double performed force exit
                            exitHand = true;
                        }
                        break;
//		Split
                    /*case 4:
                        break;

                     */
                    default:
                        System.out.println("Invalid option, please pick a number between (1 - 2)\n");
                        break;

                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number!");
                BjDriver.keyboard.nextLine();
            }
        }
    }

    /**
     * Once all players finish their move, dealer reveals hidden card and acts.
     */
    public void dealerPlay() {
        Player dealer = players.get(0);
        HandManager dealerHand = hands.get(0);

        // Reveal hidden card
        if (dealerHand.getCards().size() > 1) {
            System.out.println("Dealer reveals hidden card: " + dealerHand.getCards().get(1));
        } else {
            System.out.println("Dealer has no hidden card, Error??.");
        }
        System.out.println("Dealer total: " + dealerHand.getTotal());

        while (dealerHand.getTotal() < 17) {
            Card draw = drawCard();
            dealerHand.addCard(draw);
            System.out.println("Dealer draws: " + draw + " (total: " + dealerHand.getTotal() + ")");
        }

        dealer.setCardTotal(dealerHand.getTotal());
        System.out.println("Dealer final total: " + dealerHand.getTotal());

        endGameResults();
    }

    /**
     * Compares dealer hand to each player hand and assigns wins/losses/pushes.
     * This method assumes dealer is players.get(0) and hands[0] is dealer hand.
     */
    private void endGameResults() {
        Player dealer = players.get(0);
        HandManager dealerHand = hands.get(0);
        int dealerTotal = dealerHand.getTotal();
        boolean dealerBlackjack = (dealerHand.getCards().size() == 2 && dealerTotal == 21);

        for (int i = 1; i < players.size() && i < hands.size(); i++) {
            Player p = players.get(i);
            HandManager h = hands.get(i);
            int playerTotal = h.getTotal();
            double bet = p.getBet(); // amount already deducted at bet time (placeBet)

            System.out.print(p.getPlayerName() + " (" + playerTotal + ") vs Dealer (" + dealerTotal + "): ");

            boolean playerBlackjack = (h.getCards().size() == 2 && playerTotal == 21);

            if (playerTotal > 21) {
                // player busted dealer wins
                System.out.println(p.getPlayerName() + " busted. Dealer wins.");
                dealer.addWin();
                p.addLoss();
            } else if (playerBlackjack && dealerBlackjack) {
                // Both have natural blackjack, push
                System.out.println(p.getPlayerName() + " and Dealer have Blackjack, bets returned.");
                p.adjustMoney(bet);
            } else if (playerBlackjack) {
                // shared.Player natural blackjack
                System.out.println(p.getPlayerName() + " has Blackjack! Pays 3:2.");
                p.addWin();
                dealer.addLoss();
                double payout = Math.round(bet * 2.5); // round to nearest whole dollar
                p.adjustMoney(payout);
            } else if (dealerBlackjack) {
                // Dealer natural blackjack
                System.out.println("Dealer has Blackjack. " + p.getPlayerName() + " loses.");
                dealer.addWin();
                p.addLoss();
            } else if (dealerTotal > 21) {
                // dealer busted, player wins
                System.out.println("Dealer busted. " + p.getPlayerName() + " wins.");
                p.addWin();
                dealer.addLoss();
                p.adjustMoney(bet * 2); // return bet + winnings
            } else if (playerTotal > dealerTotal) {
                System.out.println(p.getPlayerName() + " wins.");
                p.addWin();
                dealer.addLoss();
                p.adjustMoney(bet * 2);
            } else if (playerTotal < dealerTotal) {
                System.out.println("Dealer wins.");
                dealer.addWin();
                p.addLoss();
            } else {
                System.out.println("Push. Bets are returned.");
                p.adjustMoney(bet);
            }
            // reset player's bet for next round
            p.setBet(0);
        }
        // Clear totals for next round
        for (Player p : players) p.setCardTotal(0);
    }
}