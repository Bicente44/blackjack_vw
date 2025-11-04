# blackjack\_vw

## Project info
- This project is entirely made with Java
- Creator: Vincent Welbourne, vincent.vw04@gmail.com

## Planned tasks

 - Next important steps program wide
    - [x] Show player stats at end of program
    - [ ] Improve bet entry system with timer
    - [ ] Fix game start initialize deck (Player 1 and rounds == 0 should access game start)
    - [ ] Set minimum table bets with card initialization phase
 
 - BjWork System
    - [ ] Make the ability to Split
    - [ ] Auto stand when hit up to 21 ??

 - Driver
    - [ ] Complete basic strategy sheet

 - Multiplayer Features
    - [ ] Make player timeout feature
    - [ ] Make a way of creating multiple players (At the moment I create a single instance, id need a loop).
    - [ ] ^ With this also need a way of detecting when a player leaves mid-game, of after the game to remove them.

## Learning curve / important for the future
- This program is very complicated to make multiplayer the way it stands, I'll have to refactor
the start and end to be per user and not whoever joins and such, another problem is the initialization
it's where anybody that joins gets to initialize, also fix rounds played as it's global and not per player