# blackjack\_vw

## Project info
- This project is entirely made with Java
- Creator: Vincent Welbourne, vincent.vw04@gmail.com

## Planned tasks

- TERM BREAK REFACTOR:
    - [ ] Separate server & client code, essentially choose host or join, for host you create server
  on a daemon and join that server on a separate thread. If host leaves they will still be hosting
  and can choose to further close the server.
    - [ ] Object serialization, send player and deck data through the stream. (server serves to client still tho)
    - [ ] Add JFX & Gradle


- Next important steps program wide
    - [ ] Improve bet entry system with timer
    - [ ] Fix game start initialize deck (shared.Player 1 and rounds == 0 should access game start)
    - [ ] Set minimum table bets with card initialization phase

- game.GameSession System
    - [ ] Make the ability to Split

- Driver
    - [ ] Separate from Main, launches other threads (depending on what option server or client).
    - [ ] Complete basic strategy sheet

- Multiplayer Features
    - [ ] Make player timeout feature
    - [ ] Make a way of creating multiple players (At the moment I create a single instance, id need a loop).
    - [ ] ^ With this also need a way of detecting when a player leaves mid-game, of after the game to remove them.

## Learning curve / important for the future
- This program is very complicated to make multiplayer the way it stands, I'll have to refactor
  the start and end to be per user and not whoever joins and such, another problem is the initialization
  it's where anybody that joins gets to initialize, also fix rounds played as it's global and not per player