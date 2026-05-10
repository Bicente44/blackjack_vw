# blackjack\_vw

## Project info
- This project is entirely made with Java
- Creator: Vincent Welbourne, vincent.vw04@gmail.com

## Prerequisites
- Java 21 Installed

## Planned tasks

- TERM BREAK REFACTOR:
    - [ ] Separate server & client code, essentially choose host or join, for host you create server
  on a daemon and join that server on a separate thread. If host leaves they will still be hosting
  and optionally choose to further close the server.
    - [ ] Object serialization, send player and deck data through the stream. (server serves to client still tho)


- Core game features to add (Priority order)
    - [ ] Add timer to betting
    - [ ] Set minimum table bets with card initialization phase
    - [ ] Make the ability to Split
    - [ ] Add a strategy sheet UI
    - [ ] About, will be an overlay popup with text info, x to leave overlay fully or back to go back to settings


- Extra features or ideas
  - [ ] Specific names get administrative privs (admin, cheaterpumpkineater) or another way
  - [ ] Button that gives suggestion based on basic strategy (Tip/Advice button?)