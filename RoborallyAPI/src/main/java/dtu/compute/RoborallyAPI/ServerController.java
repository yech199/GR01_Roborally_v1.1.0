package dtu.compute.RoborallyAPI;

import client_server.IGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ServerController {

    @Autowired
    private IGameService gameService;

    // Returns the game state of a game
    @GetMapping("/game/{id}/{playername}")
    public ResponseEntity<String> getPlayerState(@PathVariable int id, @PathVariable String playername) {
        return ResponseEntity.ok().body(gameService.getGameById(id, playername));
    }

    // Updates the game state of a game
    @PutMapping("/game/{id}/{playername}/save")
    public ResponseEntity<String> updateGameState(@PathVariable int id, @PathVariable String playername, @RequestBody String playerData) {
        gameService.updateGame(id, playername, playerData);
        return ResponseEntity.ok().body("OK");
    }

    // Creates a new game on the server, and returns the state of that game to the client
    @PostMapping("/game/{boardname}/{numOfPlayers}")
    public ResponseEntity<String> createGameFromBoard(@PathVariable String boardname, @PathVariable int numOfPlayers) {
        return ResponseEntity.ok().body(gameService.createGame(boardname, numOfPlayers));
    }

    // Return list of active games on the server
    @GetMapping("/game")
    public ResponseEntity<String> getListOfGames() {
        return ResponseEntity.ok().body(gameService.getListOfGames());
    }

    // Return list of board templates
    @GetMapping("/board")
    public ResponseEntity<String> getListOfBoards() {
        return ResponseEntity.ok().body(gameService.getListOfBoards());
    }

    // Returns a boards configuration/state
    @GetMapping("/board/{gameId}")
    public ResponseEntity<String> getBoardState(@PathVariable int gameId) {
        return ResponseEntity.ok().body(gameService.getBoardState(gameId));
    }

    // Add player to game players
    @PostMapping("/game/{id}")
    public ResponseEntity<String> joinGame(@PathVariable int id, @RequestBody String playerName) {
        String result = gameService.joinGame(id, playerName);
        if (result.equals("Game not found")) return ResponseEntity.badRequest().body("Game not found");
        if (result.equals("Game Full")) return ResponseEntity.badRequest().body("Game Full");
        return ResponseEntity.ok().body(result);
    }

    @DeleteMapping("/game/{id}/{playerName}")
    public ResponseEntity<String> leaveGame(@PathVariable int id, @PathVariable String playerName) {
        String result = gameService.leaveGame(id, playerName);
        if (result.equals("Game not found")) return ResponseEntity.badRequest().body("Game not found");
        if (result.equals("Game removed")) return ResponseEntity.ok().body("Game removed");
        return ResponseEntity.ok().body("ok");
    }

    // Updates the player state
    @PutMapping("game/{id}/{playerName}")
    public ResponseEntity<String> setPlayerState(@PathVariable int id, @PathVariable String playerName, @RequestBody String playerData) {
        String result = gameService.setPlayerState(id, playerName, playerData);
        if(result.equals("Game not found")) return ResponseEntity.badRequest().body("Game not found");
        return ResponseEntity.ok().body(result);
    }
}
