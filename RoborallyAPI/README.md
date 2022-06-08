# API documentation

The API is REST, using JSON for serialization.
Request are made using HTTP requests. When the server is running locally
all request starts with `http://localhost:8080/`.

## Game State
Game state refers to the data of the game at a given time. That includes
player position, cards and registers for each player, space positions and 
space actions, the phase of the game etc.

## Board
The server can hold board templates which games can be created from.

`GET /board` returns a list of board names located on the server

`GET /board/{boardname}` returns a specific board' configuration

## Game
### Get list of active games
`GET /games` returs a list of all the active games on the server that 
are not full.

### Create game
`POST /game/{boardname}` Creates a game on the server based of
a board template located on the server. The POST request body contains the
number of players in the game.

**POST request body:**
```JSON
4
```
The response contains the state of the game.

### Update game
`PUT /game/{id}` will update the game on the server 
as with the Game State in the body of the PUT request.

### Get Game
`GET /game/{id}` wil return the state of the game where the game is `id`.

### Join Game
Players can join a game.

`POST /game/join/{id}` will return the state of the game to the player.

Players can include their player name in the POST request

### Leave Game
`DELETE /game/{id}` will remove a player from the game

### Play cards
A player can choose to finish their programming phase and submit
their programming cards to the server.


