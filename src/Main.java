/**
 * Main entry point of the program, launches client/server
 *
 * @author Vincent Welbourne
 */
public class Main {
    public static void main(String[] args) {
        boolean headless = args.length > 0 && args[0].equals("--server");

        if (headless) {
            System.out.println("Headless server mode, not yet implemented.");
        } else {
            game.BjDriver.launch(args);
        }
    }
}