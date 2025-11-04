/*
 * Program created by Vincent Welbourne
 * vincent.vw04@gmail.com
 *
 */

import java.util.*;

/**
 * This class is the Blackjack work class, it is what composes the game and brings it all together.
 * Utilizes the utility methods in BjUtilities.
 *
 * @author Vincent Wrlbourne
 */
public class BjWork {
    public static final String MENU_ACTIONS = "Select an option for your next move (1-4).\n"
            + "1. Hit\n"
            + "2. Stand\n"
            + "3. Double\n"
            + "4. Split\n" +
            "> ";
    public static List<Card> deck = null;
    public static List<Player> players = new ArrayList<>();
    public static List<BjUtilities> hands = new ArrayList<>();
    public static Card dealerUp;
    public static List<Boolean> bjStand = new ArrayList<>();
    public static boolean roundOver = false;

    /**
     *
     */
    public static void game() {
        if (deck == null) {
            System.out.println("Deck not initialized or empty, creating and shuffling now.");
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
                BjUtilities.placeBet(p);
            }
        }

        // Deal initial cards
        gameStart();

        // Check for round end with dealer or player blackjack(s)
        if (!BjWork.roundOver) {
            for (int i = 1; i < players.size(); i++) {
                if (BjWork.roundOver) break;
                playerTurn(i);
            }
            if (!BjWork.roundOver) {
                dealerPlay();
            }
        }
        for (BjUtilities h : hands) h.clear();
        for (Player p : players) p.setCardTotal(0);
    }

    public static void gameStart() {
        // Clear for game start
        for (BjUtilities h : hands) h.clear();
        roundOver = false;
        bjStand.clear();
        for (int i = 0; i < players.size(); i++) bjStand.add(false);

        int numPlayers = players.size(); // including dealer at index 0

        // First pass: deal one card to each player
        BjUtilities.playerPass();

        // TODO? Could make a method for dealer pass in the future...
        // Dealer upcard
        dealerUp = BjUtilities.drawCard();
        hands.get(0).addCard(dealerUp);
        System.out.println("Dealer: " + dealerUp);

        // Second pass: deal second card to each player
        BjUtilities.playerPass();

        // Dealer hidden card
        Card dealerHidden = BjUtilities.drawCard();
        hands.get(0).addCard(dealerHidden);
        System.out.println("Dealer: ?? (hidden)");

        // Update totals on Player objects
        for (int i = 0; i < numPlayers; i++) {
            players.get(i).setCardTotal(hands.get(i).getTotal());
        }
        // Check for blackjack to prevent further bets
        BjUtilities.checkBlackjack();
    }

    /**
     * Method used to call when you want to get playing options,
     * (Hit, Stand, Double and Split) --Eventually add surrender and insurance
     *
     */
    public static void playerTurn(int playerIndex) {
        int action = 0;
        Player p = players.get(playerIndex);
        BjUtilities hand = hands.get(playerIndex);
        boolean exitHand = false;

        if (BjWork.roundOver) {
            return;
        }

        boolean playerStand = (BjWork.bjStand.size() > playerIndex && BjWork.bjStand.get(playerIndex));
        if (playerStand) {
            return;
        }

        while (!exitHand && !BjWork.roundOver) {
            System.out.print("\nDealer upcard: " + dealerUp.getValue());
            System.out.println("\n" + p.getPlayerName() + " total: " + hand.getTotal());
            System.out.print(MENU_ACTIONS);
            try {
                action = BjDriver.keyboard.nextInt();
                switch (action) {
//		Hit
                    case 1:
                        // Check if bust, else continue
                        if (hand.hitAction(p)) {
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
                        Boolean dblResult = hand.doubleAction(p);
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

    public static void dealerPlay() {
        Player dealer = players.get(0);
        BjUtilities dealerHand = hands.get(0);

        // Reveal hidden card
        if (dealerHand.getCards().size() > 1) {
            System.out.println("Dealer reveals hidden card: " + dealerHand.getCards().get(1));
        } else {
            System.out.println("Dealer has no hidden card, Error??.");
        }
        System.out.println("Dealer total: " + dealerHand.getTotal());

        while (dealerHand.getTotal() < 17) {
            Card draw = BjUtilities.drawCard();
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
    private static void endGameResults() {
        Player dealer = players.get(0);
        BjUtilities dealerHand = hands.get(0);
        int dealerTotal = dealerHand.getTotal();
        boolean dealerBlackjack = (dealerHand.getCards().size() == 2 && dealerTotal == 21);

        for (int i = 1; i < players.size() && i < hands.size(); i++) {
            Player p = players.get(i);
            BjUtilities h = hands.get(i);
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
                // Player natural blackjack
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