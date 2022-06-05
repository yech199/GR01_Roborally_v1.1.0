package dtu.compute.RoborallyAPI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ServerController {

    @Autowired
    private IGameService gameService;

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
    public ResponseEntity<String> setGame(@PathVariable int id, @RequestBody String gameData) {
        gameService.updateGame(id, gameData);
        return ResponseEntity.ok().body("OK");
    }
}
