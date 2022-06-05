package dtu.compute.RoborallyAPI;

import com.google.gson.Gson;
import dtu.compute.RoborallyAPI.Model.Board;
import dtu.compute.RoborallyAPI.Model.Game;
import fileaccess.IOUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GameService implements IGameService {

    ArrayList<Game> games = new ArrayList<>();
    ArrayList<Board> boards = new ArrayList<>();
    int id = 0;

    public GameService() {
        List<String> boardNames = IOUtil.getBoardFileNames();
        for (String boardName : boardNames) {
            Board board = new Board(boardName);
            boards.add(board);
            board.setGameState(IOUtil.readGame(boardName, false));
        }
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
    public int startGame() {
        games.add(new Game(id));
        int gameId = id;
        id++;
        return gameId;
    }

    @Override
    public String getListOfGames() {
        List<Integer> listOfGames = new ArrayList<>();
        // Get a list of game IDs
        games.forEach(game -> listOfGames.add(game.getId()));
        return new Gson().toJson(listOfGames);
    }

    @Override
    public String getListOfBoards() {
        List<String> listOfBoards = new ArrayList<>();
        // Get a list of board names
        boards.forEach(board -> listOfBoards.add(board.getName()));
        return new Gson().toJson(listOfBoards);
    }

    @Override
    public String getBoard(String boardName) {
        for (Board board : boards) {
            if (board.getName().equals(boardName)) {
                return board.getBoardState();
            }
        }
        return null;
    }

    @Override
    public String joinGame(int id) {
        return null;
    }
}
