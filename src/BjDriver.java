
/*
 * Program created by Vincent Welbourne
 * Code description:
 * 
 * 
 */
//import javax.swing.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class BjDriver {
	private static boolean LOOP = true;

	public static void main(String[] args) {
		Scanner keyboard = new Scanner(System.in);
		int roundsPlayed = 0;

		System.out.println("Welcome to BlackJack\n" + "Would you like to play a new hand?\n");
		do {
			try {

				System.out.println("Options are: 1. (Yes), 2. (Help), 3. (No, exit).");
				int option = keyboard.nextInt();

				switch (option) {
				// 1. Call to BjWork class and start a new hand
				case 1:
					if (roundsPlayed == 0) { //Initialize decks if its first round
						Shuffle.shuffle(roundsPlayed);
					} /*
						 * else if (out of cards) { Shuffle.shuffle(roundsPlayed); } */

					// TODO: add game logic so call onto BjWork System.out.println();
					roundsPlayed++;
					break;
				// 2. Help menu
				case 2:
					System.out.println("");// TODO: Give basic menu help and make it give an option for further
											// instruction like how to play and basic strategy
					// CASE 1 how to play
					// CASE 2 bj basic strategy
					break;
				// 3. Exit Program
				case 3:
					LOOP = false;
					break;
				default:
					System.out.println("Invalid number, options are from 1-3!");
				}

			} catch (InputMismatchException e) {
				System.out.println("Please enter a valid number!");
				keyboard.nextLine();
			}
		} while (LOOP);
		System.out.println("You played a total of: " + roundsPlayed + " round(s)!");
		if (roundsPlayed > 35)
			System.out.println(".. wow you might have a problem take a break..");
		System.out.println("");

		System.out.println("Thank you for using my program!");
		// Have a print that tell the user "You played: "#" hands!
		System.out.println("Program created by Vincent Welbourne");

		keyboard.close();
		System.out.close();
	}
}

/*
 * //Welcome to blackjack int startResponse =
 * JOptionPane.showConfirmDialog(null,
 * "Welcome to Blackjack! Would you like to play a new hand?", "Game Master",
 * JOptionPane.YES_NO_OPTION);
 * 
 * //Player input is no if (startResponse != JOptionPane.YES_OPTION) {
 * JOptionPane.showMessageDialog(null, "Closing the program..."); return; }
 */