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
    @GetMapping("/game/{id}/{playerName}")
    public ResponseEntity<String> getGameState(@PathVariable int id, @PathVariable String playerName) {
        return ResponseEntity.ok().body(gameService.getGameById(id, playerName));
    }

    // Updates the game state of a game
    @PutMapping("/game/{id}/{playerName}/save")
    public ResponseEntity<String> updateGameState(@PathVariable int id, @PathVariable String playerName, @RequestBody String playerState) {
        gameService.updateGame(id, playerName, playerState);
        return ResponseEntity.ok().body("OK");
    }

    // Creates a new game on the server, and returns the state of that game to the client
    @PostMapping("/game/{boardName}/{numOfPlayers}")
    public ResponseEntity<String> createGameFromBoard(@PathVariable String boardName, @PathVariable int numOfPlayers) {
        return ResponseEntity.ok().body(gameService.createGame(boardName, numOfPlayers));
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
    @PostMapping("/game/join/{id}")
    public ResponseEntity<String> joinGame(@PathVariable int id, @RequestBody String playerName) {
        String result = gameService.joinGame(id, playerName);
        if(result.equals("Game not found")) return ResponseEntity.badRequest().body("Game not found");
        else if(result.equals("Game Full")) return ResponseEntity.badRequest().body("Game Full");
        return ResponseEntity.ok().body(result);
    }

    @DeleteMapping("/game/{id}/{playerName}")
    public ResponseEntity<String> leaveGame(@PathVariable int id, @PathVariable String playerName) {
        String result = gameService.leaveGame(id, playerName);
        if(result.equals("Game not found")) return ResponseEntity.badRequest().body("Game not found");
        if(result.equals("Game removed")) return ResponseEntity.ok().body("Game removed");
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
