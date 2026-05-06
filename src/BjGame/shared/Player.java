package BjGame.shared;/*
 * Program created by Vincent Welbourne
 * vincent.vw04@gmail.com
 *
 */

import java.io.Serializable;
import java.util.UUID;

/**
 * BjGame.shared.Player object
 */
public abstract class Player implements Serializable {
    private static final long serialVersion = 1L;
    private final String PLAYER_ID;
    private String playerName;
    private String password;
    private double money;
    private int wins;
    private int losses;
    private int cardTotal;
    private double bet;

    /**
     * BjGame.shared.Player constructor
     */
    protected Player(String playerName, String password, double money) {
        this.PLAYER_ID = UUID.randomUUID().toString();
        this.playerName = playerName;
        this.password = password;
        this.money = money;
    }

    /**
     *
     * @return
     */
    public String getPlayerID() {
        return PLAYER_ID;
    }

    public String getPlayerName() {
        return playerName;
    }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public double getMoney() {
        return money;
    }
    public void setMoney(double money) {
        this.money = money;
    }

    public double getBet() {
        return bet;
    }
    public void setBet(double bet) {
        this.bet = bet;
    }

    public void adjustMoney(double x) {
        this.money += x;
    }

    public int getWins() {
        return wins;
    }
    public void addWin() {
        this.wins++;
    }

    public int getLosses() {
        return losses;
    }
    public void addLoss() {
        this.losses++;
    }

    public int getCardTotal() {
        return cardTotal;
    }
    public void setCardTotal(int cardTotal) {
        this.cardTotal = cardTotal;
    }

    /**
     * Nested human player class.
     * May change to seperate file later if complications arise.
     */
    public static class HumanPlayer extends Player {
        public HumanPlayer(String playerName, String password, double money) {
            super(playerName, password, money);
        }
    }

    /**
     * Nested Dealer class, this is the dealer object will use its card total and player name (dealer)
     */
    public static class Dealer extends Player {
        public Dealer(String playerName, String password, double money) {
            super("Dealer", "password", 100000);
        }
    }
}