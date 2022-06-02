package dtu.compute.RoborallyAPI;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
public class BoardService implements IBoardService {

    private Board board;

    Gson gson = new Gson();
    DataFile dataFile = new DataFile("test.json");

    public BoardService() throws IOException {
        //loadBoard();
    }

    private void loadBoard() throws IOException {
        String jsonString = dataFile.load();
        board = gson.fromJson(jsonString, new TypeToken<Board>(){}.getType());
    }

    @Override
    public Board getBoardByName() {
        board = new Board();
        board.name = "Test";
        return board;
    }
}
