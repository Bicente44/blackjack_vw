package BjGame;

import java.lang.Runnable;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Debugging throughout the blackjack program made easy with debug mode enabled
 *
 * @author Vincent Welbourne (Heavily inspired by Benjamin Macleod)
 */
public class Debug {
    static private Boolean enabled = true;
    static private ArrayList<Runnable> onEnabled = new ArrayList();
    static private ArrayList<Runnable> onDisabled = new ArrayList();
    static private ArrayList<Consumer<String>> onMessage = new ArrayList();
    static private ArrayList<String> messages = new ArrayList();

    // Enable debug:
    /**
     * Method to set debug status
     *
     * @param e Boolean to disable or enable debugging
     */
    static public void setEnabled(Boolean e) {
        if (e == enabled) return;
        enabled = e;
        runEnabledCallback();
    }

    /**
     * Used to check if debug is enabled
     *
     * @return true if debug enabled, else it isn't
     */
    static public boolean isEnabled() { return enabled; }

    static public void onEnabled(Runnable call) { onEnabled.add(call); }

    static public void onDisabled(Runnable call) { onDisabled.add(call); }

    static private void runEnabledCallback() {
        if (enabled) {
            for (Runnable call: onEnabled) {
                call.run();
            }
        }
        else {
            for (Runnable call: onDisabled) {
                call.run();
            }
        }
    }

    // Print:
    /**
     * BjGame.Debug print rather than sys out, gets rid of clutter in code and clean way to find issues
     *
     * @param message A string message to output in a print
     */
    static public void print(String message) {
        messages.add(message);
        System.out.println(message);
        runMessageCallback(message);
    }

    static public void onMessage(Consumer<String> call) { onMessage.add(call); }

    static private void runMessageCallback(String message) {
        for (Consumer<String> call: onMessage) {
            call.accept(message);
        }
    }

}