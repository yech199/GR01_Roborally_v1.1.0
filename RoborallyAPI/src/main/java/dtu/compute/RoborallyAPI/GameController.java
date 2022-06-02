package dtu.compute.RoborallyAPI;

import dtu.compute.RoborallyAPI.Model.Board;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {

    @Autowired
    private IGameService gameService;

    @GetMapping("/game")
    public ResponseEntity<String> getGame() {
        return ResponseEntity.ok().body(gameService.getGame());
    }

    @PostMapping("/game")
    public ResponseEntity<String> setGame(@RequestBody String gameData) {
        gameService.updateGame(gameData);
        return ResponseEntity.ok().body("OK");
    }

}
