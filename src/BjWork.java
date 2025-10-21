/*
 * Program created by Vincent Welbourne
 * vincent.vw04@gmail.com
 *
 */

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * This class ..
 *
 * @author Vincent Wrlbourne
 */
public class BjWork {

    public static List<Card> deck;
    public final String MENU_ACTIONS = "Select an option for your next move (1-4)."
            + "1. Hit"
            + "2. Stand"
            + "3. Double"
            + "4. Split";
    Scanner keyboard = new Scanner(System.in);

    /*
     * These are all of the tasks broken down into modules:
     *
     * 1. Deal cards (in order; 1p, 1d, 1p, 1d hidden) p=player, d=dealer
     * 2. Method to read Values
     * 3. Case Options to Hit, Stand, Double(Implement $ later), Split(Hide this option 4now)
     * 4. Check if bust after every card draw (Cannot hit if have 21 or greater game lost)
     * 5.
     * TODO: Check after every card dealt if deck empty, if so shuffle and remake deck
     */

    /**
     *
     * @return
     */
    public static List<Card> game() {
        if (deck == null) {
            System.out.println("Deck not initialized or empty, creating and shuffling now.");
            deck = Shuffle.shuffle();
        } else if (deck.size() < 4) {
            deck = Shuffle.shuffle();
        }


        System.out.println("Drawing cards..\n");
        Card dealt = deck.remove(0);
        System.out.println(BjDriver.playerName + ": " + dealt);
        dealt = deck.remove(0);
        System.out.println("Dealer: " + dealt);
        dealt = deck.remove(0);
        System.out.println(BjDriver.playerName + ": " + dealt);
        dealt = deck.remove(0);
        System.out.println("Dealer: ?? ");
        deckcheck();
        return deck;
    }

    public boolean readValue() {

        return true;
    }

    /**
     * Method used to call when you want to get playing options,
     * (Hit, Stand, Double and Split) --Eventually add surrender and insurance
     *
     */
    public void gameActions() {
        System.out.println(MENU_ACTIONS);
        do {
            try {
                System.out.println("Actions are: 1. (Hit), 2. (Stand), 3. (Double), 4. (Split).");
                int action = keyboard.nextInt();
                switch (action) {
//		Hit
                    case 1:
                        break;
//		Stand
                    case 2:
                        break;
//		Double
                    case 3:
                        break;
//		Split
                    case 4:
                        break;
                    default:
                        System.out.println("Invalid please pick a number between (1 - 4)\n");
                        break;

                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number!");
                keyboard.nextLine();
            }
        } while (true);
    }

    /**
     * Method to check if the deck is empty to set the 'nocards' variable as true
     */
    public static void deckcheck() {
        if (deck.isEmpty()) {
            BjDriver.noCards = true; // signal there are no cards
        }
    }
}