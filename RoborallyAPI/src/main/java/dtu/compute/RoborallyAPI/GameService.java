package dtu.compute.RoborallyAPI;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dtu.compute.RoborallyAPI.Model.Board;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GameService implements IGameService {

    private Board board;
    private String gameData;

    Gson gson = new Gson();
    DataFile dataFile = new DataFile("test.json");

    public GameService() throws IOException {
        //loadBoard();
    }

    private void loadBoard() throws IOException {
        String jsonString = dataFile.load();
        board = gson.fromJson(jsonString, new TypeToken<Board>(){}.getType());
    }

    @Override
    public String getGameById(int id) {
        // TODO: Implement get game by ID
        return null;
    }

    @Override
    public void updateGame(String gameData) {
        this.gameData = gameData;
    }

    @Override
    public String getGame() {
        return gameData;
    }
}
