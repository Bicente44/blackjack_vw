/*
 * Program created by Vincent Welbourne
 * vincent.vw04@gmail.com
 *
 */

/**
 * Player object
 */
public abstract class Player {
    private final int PLAYER_ID;
    private final String playerName;
    private double money;
    private int wins;
    private int losses;
    private int cardTotal;

    /**
     * Player constructor
     */
    protected Player(int PLAYER_ID, String playerName, int cardTotal, double money, int wins, int losses) {
        this.PLAYER_ID = PLAYER_ID;
        this.playerName = playerName;
        this.money = money;
        this.wins = wins;
        this.losses = losses;
        this.cardTotal = cardTotal;
    }

    /**
     *
     * @return
     */
    public int getPlayerID() {
        return PLAYER_ID;
    }

    /**
     *
     * @return
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     *
     * @return
     */
    public double getMoney() {
        return money;
    }
    /**
     *
     * @param money
     */
    public void setMoney(double money) {
        this.money = money;
    }

    /**
     *
     * @return player wins
     */
    public int getWins() {
        return wins;
    }
    /**
     * addWin
     */
    public void addWin() {
        this.wins++;
    }

    /**
     * getLosses
     * @return player losses
     */
    public int getLosses() {
        return losses;
    }
    /**
     * addLoss
     */
    public void addLoss() {
        this.losses++;
    }

    // Hand management

    /**
     *
     * @return
     */
    public int getCardTotal() {
        return cardTotal;
    }
    /**
     *
     * @param cardTotal
     */
    public void setCardTotal(int cardTotal) {
        this.cardTotal = cardTotal;
    }

    /**
     * Nested human player class.
     * May change to seperate file later if complications arise.
     */
    public static class HumanPlayer extends Player {
        public HumanPlayer(int playerID, String playerName, int cardTotal, double money, int wins, int losses) {
            super(playerID, playerName, cardTotal, money, wins, losses);
        }
    }
    /**
     * Nested Dealer class, this is the dealer object will use its card total and player name (dealer)
     */
    public static class Dealer extends Player {
        public Dealer(int playerID, String playerName, int cardTotal, double money, int wins, int losses) {
            super(0, "Dealer", cardTotal, money, wins, losses);
        }
    }
}