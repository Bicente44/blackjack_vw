/*
 * Program created by Vincent Welbourne
 * vincent.vw04@gmail.com
 * 
 */

/**
 * This class ..
 * 
 * @author Vincent Wrlbourne
 */
public class BjWork {
	
	// Global Declarations
	public final String MENU_OPTIONS = "Select an option for your next move (1-4)."
			+ "1. Hit"
			+ "2. Stand"
			+ "3. Double"
			+ "4. Split";
	
	
	
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
		 * 2. Method to read Values
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
	 * (Hit, Stand, Double and Split) --Eventually add surrender
	 * 
	 */
	public void gameOptions() {
		System.out.println(MENU_OPTIONS);
		int option = 0;
		
		switch(option) {
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
				break;
			
		}
		
		
		return;
	}
	
}


