# Roborally
This is a Roborally board game implemented in Java and JavaFX.

The game can be played by 2-6 players. They can either play locally
on the sam computer or play multiplayer, where they can join a server
on different clients to play(limited game features).

## Features
### Space Actions:
- Checkpoints
- Conveyor Belts
- Pit
- Priority Antenna
- Push Panel
- Reboot Token
- Gear

### Programming cards
- Programming cards
  - Move forward (1, 2, 3)
  - Turn right
  - Turn left
  - U-turn
  - Left or Right
- Special programming cards (Not implemented)
- Damage cards (Not implemented)

### Local game mode
- Fully functional game with the above spaces and programming cards implemented
- Read JSON from files
- Write JSON to files
- Serialize board's to JSON
- Deserialize of JSON to Board's
- Save game state locally
- Load board from file
- Load game from file (load an older savegame and start from there)

### Online Multiplayer mode

Implemented using RESTful archictecture. A player can:
- View list of board templates on the server
- Create a game from a board template. Game is in lobby-state before all players have joined. In lobby-state cards are not dealt and a player has to wait for everyone to join.
- View list of all games
- Join games that are not full
- Play programming cards (cards with options are not supported yet!), and have the logic executed on the server (step-mode not supported yet!).
- Get game state and update view accordingly
- Not see other players cards
- Seperate Thread HTTP Requests to Server
- Play on the server with a person on another computer
- Try to enter unaccaptable names and be prompted to try again

### Youtube Video Demo
https://youtu.be/epLNwWvihjE