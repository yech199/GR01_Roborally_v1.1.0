package roborally.client;

// import fileaccess.LoadSaveBoard;
import com.google.gson.Gson;
import fileaccess.LoadBoard;
import model.Board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameClient {

    ClientController clientController;

    public GameClient() {
        clientController = new ClientController();
    }

    public int createGame() {
        return clientController.startGame();
    }

    public Board getGameState(String gameName) {
        String json = clientController.getGame();
        return LoadBoard.loadGameState(json, gameName);
    }

    public void setGameState(int id, String jsonGameState) {
        clientController.updateGame(id, jsonGameState);
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

    public Board getBoardState(String boardName, int numOfPlayers) {
        String json = clientController.getBoard(boardName);
        return LoadBoard.newBoardState(json, boardName, numOfPlayers);
    }
}
