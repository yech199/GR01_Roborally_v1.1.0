package dtu.compute.RoborallyAPI;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fileaccess.IOUtil;
import fileaccess.LoadBoard;
import fileaccess.SaveBoard;
import model.Board;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameService implements IGameService {

    // A game is a board with a set gameId and saved player cards/registers
    ArrayList<Board> games = new ArrayList<>();
    ArrayList<Board> boards = new ArrayList<>();
    int id = 1;

    public GameService() {
        // Initialize board templates on server
        List<String> boardNames = IOUtil.getBoardFileNames();
            for (String boardName : boardNames) {
                Board board = LoadBoard.newBoard(boardName, 6);
                boards.add(board);
            }
    }

    @Override
    public Board getGameById(int id) {
        return findGame(id);
    }

    @Override
    public void updateGame(int id, String gameState) {
        Board game = findGame(id);
        int i = games.indexOf(game);
        game = LoadBoard.loadGameState(gameState);
        games.set(i, game);
    }

    @Override
    public String createGame(String boardName, int numOfPlayers) {
        Board board = findBoard(boardName);
        Board game = LoadBoard.newBoardState(SaveBoard.serializeBoard(board), id, numOfPlayers);
        games.add(game);
        id++;
        return SaveBoard.serializeBoard(game);
    }

    @Override
    public String getListOfGames() {
        List<Integer> listOfGames = new ArrayList<>();
        List<String> listOfBoardNames = new ArrayList<>();
        games.forEach(game -> listOfGames.add(game.getGameId()));
        games.forEach(game -> listOfBoardNames.add(game.getBoardName()));

        JsonObject jsonObj = new JsonObject();
        // array to JsonArray
        JsonArray jsonArray1 = new Gson().toJsonTree(listOfGames).getAsJsonArray();

        JsonArray jsonArray2 = new Gson().toJsonTree(listOfBoardNames).getAsJsonArray();
        jsonObj.add("gameId", jsonArray1);
        jsonObj.add("boardNames", jsonArray2);

        return new Gson().toJson(jsonObj);
    }

    @Override
    public String getListOfBoards() {
        List<String> listOfBoards = new ArrayList<>();
        // Get a list of board names
        boards.forEach(board -> listOfBoards.add(board.getBoardName()));
        return new Gson().toJson(listOfBoards);
    }

    @Override
    public String getBoardState(String boardName) {
        for (Board board : boards) {
            if (board.getBoardName().equals(boardName)) {
                return SaveBoard.serializeBoard(board);
            }
        }
        return null;
    }

    @Override
    public String joinGame(int id) {
        return null;
    }

    private Board findGame(int id) {
        for (Board game : games) {
            if (game.getGameId() == id) {
                return game;
            }
        }
        return null;
    }
    private Board findBoard(String boardName) {
        for (Board board : boards) {
            if (board.getBoardName().equals(boardName)) {
                return board;
            }
        }
        return null;
    }
}
