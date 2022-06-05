package dtu.compute.RoborallyAPI;

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

    @PostMapping("/game")
    public ResponseEntity<Integer> startGame() {
        int gameId = gameService.startGame();
        return ResponseEntity.ok().body(gameId);
    }

    @GetMapping("/game/{id}")
    public ResponseEntity<String> getGameState(@PathVariable int id) {
        return ResponseEntity.ok().body(gameService.getGameById(id));
    }

    @PutMapping("/game/{id}")
    public ResponseEntity<String> setGameState(@PathVariable int id, @RequestBody String gameData) {
        gameService.updateGame(id, gameData);
        return ResponseEntity.ok().body("OK");
    }
    
    @GetMapping("/board")
    public ResponseEntity<String> getListOfBoards() {
        return ResponseEntity.ok().body(gameService.getListOfBoards());
    }

    @GetMapping("/board/{name}")
    public ResponseEntity<String> getBoard(@PathVariable String name) {
        return ResponseEntity.ok().body(gameService.getBoard(name));
    }
}
