package dtu.compute.RoborallyAPI;

import client_server.IGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ServerController {

    @Autowired
    private IGameService gameService;

    /**
     * Return list of active games on the server
     * @return JSON string of all games
     * @author Mads Sørensen
     */
    @GetMapping("/game")
    public ResponseEntity<String> getListOfGames() {
        return ResponseEntity.ok().body(gameService.getListOfGames());
    }

    /**
     * Creates a new game on the server, and returns the state of that game to the client
     * @param boardname
     * @param numOfPlayers
     * @return gameId
     * @aythor Mads Sørensen
     */
    @PostMapping("/game/{boardname}/{numOfPlayers}")
    public ResponseEntity<String> createGameFromBoard(@PathVariable String boardname, @PathVariable int numOfPlayers) {
        return ResponseEntity.ok().body(gameService.createGame(boardname, numOfPlayers));
    }

    /**
     * Returns the game state of a game
     * @param id
     * @param playername
     * @return Response code
     * @author Mads Sørensen
     */
    @GetMapping("/game/{id}/{playername}")
    public ResponseEntity<String> getPlayerState(@PathVariable int id, @PathVariable String playername) {
        return ResponseEntity.ok().body(gameService.getGameById(id, playername));
    }

    /**
     * Updates the game state of a game
     * @param id
     * @param playername
     * @param playerData
     * @return Response code
     * @author Mads Sørensen
     */
    @PutMapping("/game/{id}/{playername}/save")
    public ResponseEntity<String> updateGameState(@PathVariable int id, @PathVariable String playername, @RequestBody String playerData) {
        gameService.updateGame(id, playername, playerData);
        return ResponseEntity.ok().body("OK");
    }

    /**
     * Delete a player from the game
     * @param id
     * @param playerName
     * @return Response code
     * @author Mads Sørensen
     */
    @DeleteMapping("/game/{id}/{playerName}")
    public ResponseEntity<String> leaveGame(@PathVariable int id, @PathVariable String playerName) {
        String result = gameService.leaveGame(id, playerName);
        if (result.equals("Game not found")) return ResponseEntity.badRequest().body("Game not found");
        if (result.equals("Game removed")) return ResponseEntity.ok().body("Game removed");
        return ResponseEntity.ok().body("ok");
    }

    /**
     * Add player to game players
     * @author Mads Sørensen
     */
    @PostMapping("/game/join/{id}")
    public ResponseEntity<String> joinGame(@PathVariable int id, @RequestBody String playerName) {
        String result = gameService.joinGame(id, playerName);
        if (result.equals("Game not found")) return ResponseEntity.badRequest().body("Game not found");
        if (result.equals("Game Full")) return ResponseEntity.badRequest().body("Game Full");
        return ResponseEntity.ok().body(result);
    }

    /**
     * Return list of board templates
     * @author Mark Nielsen
     */
    @GetMapping("/board")
    public ResponseEntity<String> getListOfBoards() {
        return ResponseEntity.ok().body(gameService.getListOfBoards());
    }

    /**
     * Return list of board templates
     * @author Mads Sørensen
     */
    @GetMapping("/board/{gameId}")
    public ResponseEntity<String> getBoardState(@PathVariable int gameId) {
        return ResponseEntity.ok().body(gameService.getBoardState(gameId));
    }

    /**
     * Updates the player state
     * @author Mads Sørensen
     */
    @PutMapping("game/{id}/{playername}")
    public ResponseEntity<String> setPlayerCards(@PathVariable int id, @PathVariable String playername, @RequestBody String playerData) {
        String result = gameService.setPlayerState(id, playername, playerData);
        if (result.equals("Game not found")) return ResponseEntity.badRequest().body("Game not found");
        return ResponseEntity.ok().body(result);
    }
}
