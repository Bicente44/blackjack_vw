/*
 * Program created by Vincent Welbourne
 * vincent.vw04@gmail.com
 *
 */

/**
 * Player object
 */
public abstract class Player {
    String playerName;
    Double money;


    protected Player(String playerName, Double money) {
        this.playerName = playerName;
        this.money = money;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Double getMoney() {
        return money;
    }
}
