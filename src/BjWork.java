/*
 * Program created by Vincent Welbourne
 * vincent.vw04@gmail.com
 *
 */

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * This class ..
 *
 * @author Vincent Wrlbourne
 */
public class BjWork {

    // Global Declarations
    public final String MENU_ACTIONS = "Select an option for your next move (1-4)."
            + "1. Hit"
            + "2. Stand"
            + "3. Double"
            + "4. Split";
    Scanner keyboard = new Scanner(System.in);



    /**
     *
     * @return null
     */
    public static String game() {
        System.out.println("Drawing cards..\n");
        /*
         * These are all of the tasks broken down into modules:
         *
         * 1. Deal cards (in order; 1p, 1d, 1p, 1d hidden) p=player, d=dealer
         */





        /* 2. Method to read Values
         * 3. Case Options to Hit, Stand, Double(Implement $ later), Split(Hide this option 4now)
         * 4. Check if bust after every card draw (Cannot hit if have 21 or greater game lost)
         * 5.
         *
         */

        return null;
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
                switch(action) {
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
}