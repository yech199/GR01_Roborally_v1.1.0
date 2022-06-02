package dtu.compute.RoborallyAPI;

import dtu.compute.RoborallyAPI.Model.Board;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {

    @Autowired
    private IBoardService boardService;

    @GetMapping("/board")
    public ResponseEntity<Board> getBoardByName() {
        Board board = boardService.getBoardByName();
        return ResponseEntity.ok().body(board);
    }

}
