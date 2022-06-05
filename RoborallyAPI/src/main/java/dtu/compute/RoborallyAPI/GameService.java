package dtu.compute.RoborallyAPI;

import com.google.gson.Gson;
import dtu.compute.RoborallyAPI.Model.Game;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;

@Service
public class GameService implements IGameService {

    ArrayList<Game> games = new ArrayList<>();
    int id = 0;
    //private String gameData;

    //DataFile dataFile = new DataFile("defaultboard.json");

    public void getGamesList() {

    }

    @Override
    public String getGameById(int id) {
        return games.get(id).getGameState();
    }

    @Override
    public void updateGame(int id, String gameData) {
        games.get(id).setGameState(gameData);
    }

    @Override
    public String getGame() {
        //return gameData;
        return null;
    }

    @Override
    public int startGame() {
        games.add(new Game(id));
        int gameId = id;
        id++;
        return gameId;
    }

    @Override
    public String getListOfGames() {
        Gson gson = new Gson();
        ArrayList<Integer> ListOfGames = new ArrayList<>();
        // Get a list of game IDs
        games.forEach(e -> ListOfGames.add(e.getId()));
        return gson.toJson(ListOfGames);
    }

    @Override
    public String joinGame(int id) {
        return null;
    }
}
