package dtu.compute.RoborallyAPI;

import fileaccess.SaveBoard;
import model.Board;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ServerController {

    @Autowired
    private IGameService gameService;

    @GetMapping("/game")
    public ResponseEntity<String> getListOfGames() {
        return ResponseEntity.ok().body(gameService.getListOfGames());
    }

    @PostMapping("/game/{boardname}")
    public ResponseEntity<String> createGameFromBoard(@PathVariable String boardname, @RequestBody int numOfPlayers) {
        return ResponseEntity.ok().body(gameService.createGame(boardname, numOfPlayers));
    }

    @GetMapping("/game/{id}")
    public ResponseEntity<String> getGameState(@PathVariable int id) {
        Board board = gameService.getGameById(id);
        return ResponseEntity.ok().body(SaveBoard.serializeBoard(board));
    }

    @PutMapping("/game/{id}")
    public ResponseEntity<String> updateGameState(@PathVariable int id, @RequestBody String gameData) {
        gameService.updateGame(id, gameData);
        return ResponseEntity.ok().body("OK");
    }
    
    @GetMapping("/board")
    public ResponseEntity<String> getListOfBoards() {
        return ResponseEntity.ok().body(gameService.getListOfBoards());
    }

    @GetMapping("/board/{name}")
    public ResponseEntity<String> getBoardState(@PathVariable String name) {
        return ResponseEntity.ok().body(gameService.getBoardState(name));
    }
}
