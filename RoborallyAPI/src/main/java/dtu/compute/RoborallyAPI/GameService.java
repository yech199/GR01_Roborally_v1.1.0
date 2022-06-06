package dtu.compute.RoborallyAPI;

import com.google.gson.Gson;
import fileaccess.IOUtil;
import fileaccess.LoadBoard;
import fileaccess.SaveBoard;
import model.Board;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameService implements IGameService {

    ArrayList<Board> games = new ArrayList<>();
    ArrayList<Board> boards = new ArrayList<>();
    int id = 1;

    public GameService() {
        List<String> boardNames = IOUtil.getBoardFileNames();
            for (String boardName : boardNames) {
                Board board = LoadBoard.newBoard(boardName, 6);
                boards.add(board);
            }
    }

    @Override
    public Board getGameById(int id) {
        for (Board game : games) {
            if (game.getGameId() == id) {
                return game;
            }
        }
        return null;
    }

    @Override
    public void updateGame(int id, String gameState) {
        int i = 0;
        for (Board game : games) {
            if (game.getGameId() == id) {
                // TODO Expore if it's good enough to serialize->deserialize, or a better copy-board-method can be found
                Board newGame = LoadBoard.loadGameState(gameState);
                games.set(i, newGame);
                i++;
            }
        }
    }

    @Override
    public Board createGame(String boardName) {
        for (Board board : boards) {
            if (board.getBoardName().equals(boardName)) {
                Board game = LoadBoard.newBoardState(SaveBoard.serializeBoard(board), id, 6);
                games.add(game);
                id++;
                return game;
            }
        }
        // TODO Add game not found Exeption
        return null;
    }

    @Override
    public String getListOfGames() {
        List<Integer> listOfGames = new ArrayList<>();
        // Get a list of game IDs
        games.forEach(game -> listOfGames.add(game.getGameId()));
        return new Gson().toJson(listOfGames);
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
    public Board getBoard(String boardName) {
        for (Board board : boards) {
            if (board.getBoardName().equals(boardName)) {
                return board;
            }
        }
        return null;
    }

    @Override
    public String joinGame(int id) {
        return null;
    }
}
