package BjGame;

/**
 * BjGame.Main entry point of the program, launches BjGame.client/BjGame.server
 *
 * @author Vincent Welbourne
 */
public class Main {
    public static void main(String[] args) {
        boolean headless = args.length > 0 && args[0].equals("--BjGame.server");
        if (headless) {
            Debug.println("Headless BjGame.server mode, not yet implemented.");
        } else {
            BjGame.game.BjDriver.launch(args);
        }
    }
}