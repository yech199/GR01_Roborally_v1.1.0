package dtu.compute.RoborallyAPI;

import dtu.compute.RoborallyAPI.Model.Board;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GameService implements IGameService {

    private Board board;
    private String gameData;

    DataFile dataFile = new DataFile("test.json");

    public GameService() throws IOException {
        loadGame();
    }

    private void loadGame() throws IOException {
        gameData = dataFile.load();
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
