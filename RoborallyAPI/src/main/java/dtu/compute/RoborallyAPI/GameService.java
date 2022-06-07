package dtu.compute.RoborallyAPI;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fileaccess.IOUtil;
import fileaccess.LoadBoard;
import fileaccess.SaveBoard;
import model.Board;
import model.Player;
import org.springframework.stereotype.Service;

import java.awt.*;
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
        if (board == null) return "Board not found";
        // TODO we serialize to deserialize again. Find better way
        Board game = LoadBoard.newBoardState(SaveBoard.serializeBoard(board), id, numOfPlayers);
        id++;
        games.add(game);
        return SaveBoard.serializeBoard(game);
    }

    @Override
    public String getListOfGames() {
        List<Integer> listOfGames = new ArrayList<>();
        List<String> listOfBoardNames = new ArrayList<>();

        // Get a list of game IDs
        games.forEach(game -> listOfGames.add(game.getGameId()));
        games.forEach(game -> listOfBoardNames.add(game.getBoardName()));

        // https://www.tutorialspoint.com/how-to-convert-java-array-or-arraylist-to-jsonarray-using-gson-in-java
        JsonObject jsonObj = new JsonObject();
        // array to JsonArray
        JsonArray jsonArray1 = new Gson().toJsonTree(listOfGames).getAsJsonArray();
        // ArrayList to JsonArray
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
    public String joinGame(int id, String playerName) {
        Board game = findGame(id);
        if(game == null) return "Game not found";
        if(game.getAmountOfActivePlayers() >= game.maxAmountOfPlayers) return "Game Full";
        Player template = game.getPlayer(game.getRobot());
        String color = template.getColor();

        // Add new player and replace dummy player
        Player player = new Player(game, color, playerName);
        player.setSpace(template.getSpace());
        player.setHeading(template.getHeading());
        player.activePlayer = true;
        game.setRobot(player);

        return SaveBoard.serializeBoard(game);
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
