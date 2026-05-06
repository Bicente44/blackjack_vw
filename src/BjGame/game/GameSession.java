package BjGame.game;

import BjGame.shared.Card;
import BjGame.shared.Player;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class GameSession {
    public List<Card> deck;
    public static List<Player> players;
    public static List<HandManager> hands;
    public Card dealerUp;
    public List<Boolean> bjStand;
    public boolean roundOver;

    public GameSession() {
        this.players  = new ArrayList<>();
        this.hands    = new ArrayList<>();
        this.bjStand  = new ArrayList<>();
        this.roundOver = false;
    }
}
