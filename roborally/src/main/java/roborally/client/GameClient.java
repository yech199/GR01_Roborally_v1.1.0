package roborally.client;

// import fileaccess.LoadSaveBoard;
import com.google.gson.Gson;
import fileaccess.LoadBoard;
import fileaccess.SaveBoard;
import model.Board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameClient {

    ClientController clientController;

    public GameClient() {
        clientController = new ClientController();
    }

    public Board createGame(String boardName, int numOfPlayers) {
        return LoadBoard.loadGameState(clientController.createGame(boardName, numOfPlayers));
    }

    public Board getGameState(int id) {
        String json = clientController.getGameById(id);
        return LoadBoard.loadGameState(json);
    }

    public void setGameState(int id, String jsonGameState) {
        clientController.updateGame(id, jsonGameState);
    }

    public void setGameState(Board board) {
        clientController.updateGame(board.getGameId(), SaveBoard.serializeBoard(board));
    }

    public List<String> getListOfGames() {
        Gson gson = new Gson();
        String games = clientController.getListOfGames();
        int[] id = gson.fromJson(games, int[].class);
        List<String> gameIDs = new ArrayList<>();
        for (int j : id) {
            gameIDs.add(String.valueOf(j));
        }
        return gameIDs;
    }

    public List<String> getListOfBoards() {
        Gson gson = new Gson();
        String boards = clientController.getListOfBoards();
        String[] boardNames = gson.fromJson(boards, String[].class);
        return new ArrayList<>(Arrays.asList(boardNames));
    }

    public Board getBoardState(String boardName, int gameId, int numOfPlayers) {
        String json = clientController.getBoardState(boardName);
        return LoadBoard.newBoardState(json, gameId, numOfPlayers);
    }
}
