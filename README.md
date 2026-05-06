# blackjack\_vw

## Project info
- This project is entirely made with Java
- Creator: Vincent Welbourne, vincent.vw04@gmail.com

## Planned tasks

- TERM BREAK REFACTOR:
    - [ ] Separate server & client code, essentially choose host or join, for host you create server
  on a daemon and join that BjGame.server on a separate thread. If host leaves they will still be hosting
  and optionally choose to further close the server.
    - [ ] Object serialization, send player and deck data through the stream. (server serves to client still tho)
    - [x] Add JFX & Gradle


- Core game features to add
    - [ ] Add timer to betting
    - [ ] Set minimum table bets with card initialization phase
    - [ ] Make the ability to Split
    - [ ] Add a strategy sheet UI
