/*
 * Program created by Vincent Welbourne
 * vincent.vw04@gmail.com
 *
 */

import java.util.*;

/**
 * This class ..
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
    public static List<Card> deck;
    public static List<Player> players = new ArrayList<>();
    public static List<Hand> hands = new ArrayList<>();

    /**
     *
     * @return
     */
    public static List<Card> game() {
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
        // Deal initial cards
        gameStart();

        if (!players.isEmpty()) {
            int humanIndex = 1;
            playerTurn(humanIndex);
        } else {
            System.out.println("Debug ahh, player list is broken, ");
        }
        for (Hand h : hands) h.clear();
        for (Player p : players) p.setCardTotal(0);

        return deck;
    }

    public boolean readValue() {

        return true;
    }

    public static void gameStart() {
        for (Hand h : hands) h.clear();

        int numPlayers = players.size(); // including dealer at index 0

        // First pass: deal one card to each player
        for (int i = 1; i < numPlayers; i++) {
            Card c = drawCard();
            hands.get(i).addCard(c);
            System.out.println(players.get(i).getPlayerName() + ": " + c);
        }

        // Dealer upcard (index 0)
        Card dealerUp = drawCard();
        hands.get(0).addCard(dealerUp);
        System.out.println("Dealer: " + dealerUp);

        // Second pass: deal second card to each player
        for (int i = 1; i < numPlayers; i++) {
            Card c = drawCard();
            hands.get(i).addCard(c);
            System.out.println(players.get(i).getPlayerName() + ": " + c);
        }
        // Dealer hidden card (index 0)
        Card dealerHidden = drawCard();
        hands.get(0).addCard(dealerHidden);
        System.out.println("Dealer: ?? (hidden)");

        // Update totals on Player objects
        for (int i = 0; i < numPlayers; i++) {
            players.get(i).setCardTotal(hands.get(i).getTotal());
        }

        // TODO Show dealer total
    }

    /**
     * Method used to call when you want to get playing options,
     * (Hit, Stand, Double and Split) --Eventually add surrender and insurance
     *
     */
    public static void playerTurn(int playerIndex) {
        Player p = players.get(playerIndex);
        Hand hand = hands.get(playerIndex);
        Hand dealerHand = hands.get(0);
        boolean exitHand = false;

        while (!exitHand) {
            System.out.println("\n" + p.getPlayerName() + " total: " + hand.getTotal());
            // TODO Here when player gets 21 skip this and wait for other players/just skip to win screen
            System.out.print(MENU_ACTIONS);
            try {
                int action = BjDriver.keyboard.nextInt();
                switch (action) {
//		Hit
                    case 1:
                        Card draw = drawCard();
                        hand.addCard(draw);
                        p.setCardTotal(hand.getTotal());
                        System.out.println(p.getPlayerName() + " drew: " + draw + " (total: " + hand.getTotal() + ")");
                        if (hand.getTotal() > 21) {
                            System.out.println(p.getPlayerName() + " busted!");
                            exitHand = true;
                        }
                        break;
//		Stand
                    case 2:
                        System.out.println(p.getPlayerName() + " stands with " + hand.getTotal());
                        // For now run dealer
                        dealerPlay();
                        exitHand = true;
                        break;
//		Double
                    /*case 3:
                        break;
//		Split
                    case 4:
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

    private static void dealerPlay() {
        Player dealer = players.get(0);
        Hand dealerHand = hands.get(0);

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

    public static Card drawCard() {
        if (deck == null || deck.isEmpty()) {
            System.out.println("Deck empty, reshuffling.");
            deck = new LinkedList<>(Shuffle.shuffle());
        }
        return deck.remove(0);
    }

    /**
     * Compares dealer hand to each player hand and assigns wins/losses/pushes.
     * This method assumes dealer is players.get(0) and hands[0] is dealer hand.
     */
    private static void endGameResults() {
        Player dealer = players.get(0);
        Hand dealerHand = hands.get(0);
        int dealerTotal = dealerHand.getTotal();

        for (int i = 1; i < players.size() && i < hands.size(); i++) {
            Player p = players.get(i);
            Hand h = hands.get(i);
            int playerTotal = h.getTotal();

            System.out.print(p.getPlayerName() + " (" + playerTotal + ") vs Dealer (" + dealerTotal + "): ");

            if (playerTotal > 21) {
                // player already busted — dealer wins
                System.out.println("Player busted. Dealer wins.");
                dealer.addWin();
                p.addLoss();
            } else if (dealerTotal > 21) {
                // dealer busted, player wins
                System.out.println("Dealer busted. Player wins.");
                p.addWin();
                dealer.addLoss();
            } else if (playerTotal > dealerTotal) {
                System.out.println("Player wins.");
                p.addWin();
                dealer.addLoss();
            } else if (playerTotal < dealerTotal) {
                System.out.println("Dealer wins.");
                dealer.addWin();
                p.addLoss();
            } else {
                System.out.println("Push. Bets are returned.");
                // no changes to wins/losses for push
            }
        }

        // Clear totals for next round
        for (Player p : players) p.setCardTotal(0);
    }
}