package BjGame.game;

import BjGame.Debug;
import BjGame.shared.Card;
import BjGame.shared.Player;
import BjGame.ui.GameUI;
import BjGame.ui.PlayerSeat;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.util.*;

/**
 * Blackjack game controller — connects UI to game models and drives the round loop.
 *
 * Round cycle:
 *   startRound() → startBettingPhase() → [per-seat bets] → gameStart() → playerTurn()
 *                → dealerPlay() → endGameResults() → PauseTransition → startRound()
 *
 * @author Vincent Welbourne
 */
public class GameController {

    GameSession gameSession = new GameSession();
    private GameUI gameUI;
    private int currentPlayerIndex = 1;

    public void setUI(GameUI gameUI) {
        this.gameUI = gameUI;
    }

    // ── Card draw ─────────────────────────────────────────────────────────────

    public synchronized Card drawCard() {
        if (gameSession.deck == null || gameSession.deck.isEmpty()) {
            Debug.println("Deck empty, reshuffling.");
            gameSession.deck = Shuffle.shuffle(gameSession.numOfDecks);
        }
        return gameSession.deck.remove(0);
    }

    // ── Player management ─────────────────────────────────────────────────────

    public void addPlayer(Player p) {
        gameSession.players.add(p);
        gameSession.hands.add(new HandManager());
    }

    public void clearPlayers() {
        gameSession.players.clear();
        gameSession.hands.clear();
    }

    // ── Round entry point ─────────────────────────────────────────────────────

    /**
     * Entry point for every round. Checks the deck then starts the betting phase.
     * Called once manually to begin the game, then automatically by endGameResults().
     */
    public void startRound() {
        if (gameSession.deck == null ||
                gameSession.deck.size() < (gameSession.players.size() * 2) + 2) {
            gameSession.deck = Shuffle.shuffle(gameSession.numOfDecks);
        }
        startBettingPhase();
    }

    // ── Betting phase ─────────────────────────────────────────────────────────

    /**
     * Shows the "PLACE YOUR BETS" intermission banner and sequences through each
     * seat one at a time. Once all players have bet, transitions to play mode and deals.
     */
    private void startBettingPhase() {
        if (gameUI == null) {
            // No UI attached (headless / test) — skip straight to dealing
            gameStart();
            return;
        }

        // Clear any leftover bets from the previous round on the model side
        for (int i = 1; i < gameSession.players.size(); i++) {
            gameSession.players.get(i).setBet(0);
        }

        // The global intermission timer is a visual backdrop and safety net.
        // showPlayMode() (called when all per-player bets are done) stops it automatically,
        // so it only fires if the per-player sequence stalls for some reason.
        gameUI.showIntermission(120, () -> {
            Debug.println("Global bet timer expired — forcing round start.");
            gameUI.showPlayMode();
            gameStart();
        });

        startBetTurnForPlayer(0);
    }

    /**
     * Activates one seat for betting (chip controls + countdown timer).
     * When the player confirms or the timer expires, the bet is recorded and the
     * next seat is activated. After the last seat, flips to play mode and deals.
     *
     * @param seatIndex index into gameUI.getSeats() (0-based, dealer is not a seat)
     */
    private void startBetTurnForPlayer(int seatIndex) {
        if (gameUI == null) return;
        List<PlayerSeat> seats = gameUI.getSeats();

        if (seatIndex >= seats.size()) {
            // All players have bet — cancel global timer and start dealing
            gameUI.showPlayMode();   // also stops the intermission timeline
            gameStart();
            return;
        }

        PlayerSeat seat   = seats.get(seatIndex);
        // players[0] is always the dealer, so player at seatIndex is players[seatIndex + 1]
        Player     player = gameSession.players.get(seatIndex + 1);

        // Sync the seat's money so the chip cap and balance hint reflect current balance
        seat.setPlayerMoney(player.getMoney());

        seat.setOnBetConfirmed(() -> {
            int betAmount = seat.getCurrentBet();

            // Apply a $5 minimum if the player can afford it; use whatever they have otherwise
            if (betAmount < 5) {
                betAmount = (int) Math.min(5, player.getMoney());
            }
            double actualBet = Math.min(betAmount, player.getMoney());

            if (actualBet > 0) {
                player.adjustMoney(-actualBet);
                player.setBet(actualBet);
            }

            seat.stopTimer();

            // Advance FIRST: startBetTurnForPlayer calls startPlayerBetTurn internally,
            // which runs showBetMode(false) on this seat, overwriting it with "Waiting...".
            startBetTurnForPlayer(seatIndex + 1);

            // Then restore the confirmed label, overriding the "Waiting..." that was just set.
            seat.showConfirmedBet((int) actualBet);
        });

        // startPlayerBetTurn activates the chip widget and starts the per-player timer.
        // The callback above must be wired before this call so it's ready when the timer fires.
        gameUI.startPlayerBetTurn(seatIndex, 20);
    }

    // ── Deal ──────────────────────────────────────────────────────────────────

    /**
     * Called after all bets are placed and seats are in play mode.
     * Clears hands, deals two passes, updates the UI, then checks for blackjack.
     */
    public void gameStart() {
        for (HandManager h : gameSession.hands) h.clear();
        gameSession.roundOver = false;
        gameSession.bjStand.clear();
        for (int i = 0; i < gameSession.players.size(); i++) gameSession.bjStand.add(false);

        playerPass();

        gameSession.dealerUp = drawCard();
        gameSession.hands.get(0).addCard(gameSession.dealerUp);

        playerPass();

        Card dealerHidden = drawCard();
        gameSession.hands.get(0).addCard(dealerHidden);

        for (int i = 0; i < gameSession.players.size(); i++) {
            gameSession.players.get(i).setCardTotal(gameSession.hands.get(i).getTotal());
        }

        if (gameUI != null) {
            gameUI.getDealer().clearHand();
            Card up = gameSession.dealerUp;
            gameUI.getDealer().addCard(up.getRank(), up.getSuit());
            gameUI.getDealer().addHiddenCard();

            for (int i = 1; i < gameSession.players.size(); i++) {
                Player     p    = gameSession.players.get(i);
                PlayerSeat seat = gameUI.getSeats().get(i - 1);
                seat.clearHand();
                for (Card c : gameSession.hands.get(i).getCards()) {
                    seat.addCard(c.getRank(), c.getSuit());
                }
                seat.setTotal(gameSession.hands.get(i).getTotal(), false);
                // Show the locked-in bet in the seat's info row during play
                seat.setBet((int) p.getBet());
            }
        }

        checkBlackjack();

        if (!gameSession.roundOver) {
            currentPlayerIndex = 1;
            playerTurn();
        }
    }

    /** Deals one card to every non-dealer player. Called twice at deal time. */
    public void playerPass() {
        if (gameSession.hands.size() != gameSession.players.size()) {
            throw new IllegalStateException("hands and players lists must be same length");
        }
        for (int i = 1; i < gameSession.players.size(); i++) {
            Card c = drawCard();
            gameSession.hands.get(i).addCard(c);
            Debug.println(gameSession.players.get(i).getPlayerName() + ": " + c);
        }
    }

    // ── Blackjack check ───────────────────────────────────────────────────────

    /**
     * Runs immediately after the deal.
     * Dealer BJ → all players forced to stand, round goes straight to endGameResults.
     * Player BJ → they auto-stand and get a visual notification.
     */
    public void checkBlackjack() {
        int numPlayers = gameSession.players.size();

        if (gameSession.hands.size() != gameSession.players.size()) {
            throw new IllegalStateException("hands and players lists must be same length");
        }
        if (gameSession.bjStand.size() != numPlayers) {
            gameSession.bjStand.clear();
            for (int i = 0; i < numPlayers; i++) gameSession.bjStand.add(false);
        }

        HandManager dealerHand = gameSession.hands.get(0);
        boolean     dealerBJ   = (dealerHand.getCards().size() == 2 && dealerHand.getTotal() == 21);

        if (dealerBJ) {
            Debug.println("\nDealer has blackjack — all players stand.");
            for (int i = 1; i < numPlayers; i++) gameSession.bjStand.set(i, true);
            gameSession.roundOver = true;
            dealerPlay();
            return;
        }

        for (int i = 1; i < numPlayers; i++) {
            HandManager ph       = gameSession.hands.get(i);
            boolean     playerBJ = (ph.getCards().size() == 2 && ph.getTotal() == 21);
            gameSession.bjStand.set(i, playerBJ);
            if (playerBJ) {
                Debug.println(gameSession.players.get(i).getPlayerName() + " has Blackjack.");
                if (gameUI != null) {
                    gameUI.getSeats().get(i - 1).showNotification("BLACKJACK");
                }
            }
        }
    }

    // ── Player actions ────────────────────────────────────────────────────────

    /**
     * Advances to the next player who hasn't stood yet.
     * Sets an auto-stand callback on the seat timer so the round never stalls
     * if a player goes idle.
     */
    public void playerTurn() {
        // Skip anyone already standing (blackjack or prior stand)
        while (currentPlayerIndex < gameSession.players.size()) {
            boolean standing = gameSession.bjStand.size() > currentPlayerIndex
                    && gameSession.bjStand.get(currentPlayerIndex);
            if (!standing) break;
            currentPlayerIndex++;
        }

        if (currentPlayerIndex >= gameSession.players.size()) {
            if (gameUI != null) gameUI.setActionsEnabled(false);
            dealerPlay();
            return;
        }

        if (gameUI != null) {
            gameUI.setActionsEnabled(true);
            PlayerSeat seat = gameUI.getSeats().get(currentPlayerIndex - 1);
            seat.setActive(true);
            // Reuse the bet-confirmed callback slot: if the 30-second turn timer
            // runs out, auto-stand this player rather than freezing the game.
            seat.setOnBetConfirmed(this::stand);
            seat.startTimer(30, true);
        }
    }

    public void hit() {
        Player      p      = gameSession.players.get(currentPlayerIndex);
        HandManager hand   = gameSession.hands.get(currentPlayerIndex);
        Card        draw   = drawCard();
        boolean     busted = hand.hitAction(p, draw);
        Debug.println(p.getPlayerName() + " hits: " + draw + " total: " + hand.getTotal());

        if (gameUI != null) {
            PlayerSeat seat = gameUI.getSeats().get(currentPlayerIndex - 1);
            seat.addCard(draw.getRank(), draw.getSuit());
            seat.setTotal(hand.getTotal(), busted);
            if (busted) {
                seat.showNotification("BUST");
                seat.setActive(false); // also stops the turn timer
            }
        }

        if (busted) {
            currentPlayerIndex++;
            playerTurn();
        }
    }

    public void stand() {
        Player p = gameSession.players.get(currentPlayerIndex);
        Debug.println(p.getPlayerName() + " stands with "
                + gameSession.hands.get(currentPlayerIndex).getTotal());
        gameSession.bjStand.set(currentPlayerIndex, true);

        if (gameUI != null) {
            PlayerSeat seat = gameUI.getSeats().get(currentPlayerIndex - 1);
            seat.setActive(false); // stops the turn timer
            seat.showNotification("STAND");
        }

        currentPlayerIndex++;
        playerTurn();
    }

    public void doubleDown() {
        Player      p    = gameSession.players.get(currentPlayerIndex);
        HandManager hand = gameSession.hands.get(currentPlayerIndex);

        if (p.getBet() <= 0 || p.getMoney() < p.getBet()) {
            Debug.println("Cannot double — invalid bet state.");
            return;
        }

        Card    draw   = drawCard();
        boolean busted = hand.doubleAction(p, draw);
        Debug.println(p.getPlayerName() + " doubles. Drew: " + draw);

        if (gameUI != null) {
            PlayerSeat seat = gameUI.getSeats().get(currentPlayerIndex - 1);
            seat.addCard(draw.getRank(), draw.getSuit());
            seat.setTotal(hand.getTotal(), busted);
            seat.setBet((int) p.getBet());
            seat.setActive(false); // stops the turn timer
            if (busted) seat.showNotification("BUST");
        }

        currentPlayerIndex++;
        playerTurn();
    }

    // ── Dealer play ───────────────────────────────────────────────────────────

    /**
     * Reveals the hole card, draws until 17+, then resolves the round.
     */
    public void dealerPlay() {
        HandManager dealerHand = gameSession.hands.get(0);
        Card        hidden     = dealerHand.getCards().get(1);

        if (gameUI != null) {
            gameUI.getDealer().revealHiddenCard(hidden.getRank(), hidden.getSuit());
        }
        Debug.println("Dealer reveals: " + hidden + " total: " + dealerHand.getTotal());

        while (dealerHand.getTotal() < 17) {
            Card draw = drawCard();
            dealerHand.addCard(draw);
            Debug.println("Dealer draws: " + draw + " total: " + dealerHand.getTotal());
            if (gameUI != null) gameUI.getDealer().addCard(draw.getRank(), draw.getSuit());
        }

        if (gameUI != null) gameUI.getDealer().setTotal(dealerHand.getTotal());
        gameSession.players.get(0).setCardTotal(dealerHand.getTotal());

        endGameResults();
    }

    // ── Results & round loop ──────────────────────────────────────────────────

    /**
     * Compares each player's hand to the dealer's, pays out or sweeps, shows
     * per-seat result notifications, updates money displays, then schedules the
     * next round after a short pause.
     */
    private void endGameResults() {
        Player      dealer      = gameSession.players.get(0);
        HandManager dealerHand  = gameSession.hands.get(0);
        int         dealerTotal = dealerHand.getTotal();
        boolean     dealerBJ   = (dealerHand.getCards().size() == 2 && dealerTotal == 21);

        for (int i = 1; i < gameSession.players.size() && i < gameSession.hands.size(); i++) {
            Player      p           = gameSession.players.get(i);
            HandManager h           = gameSession.hands.get(i);
            int         playerTotal = h.getTotal();
            double      bet         = p.getBet();
            boolean     playerBJ   = (h.getCards().size() == 2 && playerTotal == 21);
            String      notification;

            Debug.print(p.getPlayerName() + " (" + playerTotal
                    + ") vs Dealer (" + dealerTotal + "): ");

            if (playerTotal > 21) {
                Debug.println(p.getPlayerName() + " busted — dealer wins.");
                dealer.addWin();
                p.addLoss();
                notification = "LOSE";

            } else if (playerBJ && dealerBJ) {
                Debug.println(p.getPlayerName() + " and Dealer both have Blackjack — push.");
                p.adjustMoney(bet);
                notification = "PUSH";

            } else if (playerBJ) {
                Debug.println(p.getPlayerName() + " Blackjack! Pays 3:2.");
                p.addWin();
                dealer.addLoss();
                p.adjustMoney(Math.round(bet * 2.5));
                notification = "BLACKJACK";

            } else if (dealerBJ) {
                Debug.println("Dealer Blackjack — " + p.getPlayerName() + " loses.");
                dealer.addWin();
                p.addLoss();
                notification = "LOSE";

            } else if (dealerTotal > 21) {
                Debug.println("Dealer busted — " + p.getPlayerName() + " wins.");
                p.addWin();
                dealer.addLoss();
                p.adjustMoney(bet * 2);
                notification = "WIN";

            } else if (playerTotal > dealerTotal) {
                Debug.println(p.getPlayerName() + " wins.");
                p.addWin();
                dealer.addLoss();
                p.adjustMoney(bet * 2);
                notification = "WIN";

            } else if (playerTotal < dealerTotal) {
                Debug.println(p.getPlayerName() + " loses.");
                dealer.addWin();
                p.addLoss();
                notification = "LOSE";

            } else {
                Debug.println("Push — bets returned to " + p.getPlayerName() + ".");
                p.adjustMoney(bet);
                notification = "PUSH";
            }

            p.setBet(0); // clear model bet; next round's startBettingPhase resets UI side too

            if (gameUI != null) {
                PlayerSeat seat = gameUI.getSeats().get(i - 1);
                seat.showNotification(notification);
                seat.setPlayerMoney(p.getMoney()); // keeps bet-widget cap accurate next round
                seat.setBet(0);                    // clears the bet label in the info row
            }
        }

        for (Player p : gameSession.players) p.setCardTotal(0);

        // Give players a moment to read results before the next betting phase begins
        if (gameUI != null) {
            PauseTransition pause = new PauseTransition(Duration.seconds(3.5));
            pause.setOnFinished(e -> startRound());
            pause.play();
        }
    }
}