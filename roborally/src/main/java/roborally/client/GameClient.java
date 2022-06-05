package roborally.client;

// import fileaccess.LoadSaveBoard;
import com.google.gson.Gson;
import fileaccess.LoadBoard;
import model.Board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameClient {

    ClientController data;

    public GameClient() {
        data = new ClientController();
    }

    public int createGame() {
        return data.startGame();
    }

    public Board getGame(String gameName) {
        String json = data.getGame();
        return LoadBoard.loadGameState(json, gameName);
    }

    public void setGame(int id, String jsonGameState) {
        data.updateGame(id, jsonGameState);
    }

    public List<String> getListOfGames() {
        Gson gson = new Gson();
        String games = data.getListOfGames();
        int[] id = gson.fromJson(games, int[].class);
        List<String> gameIDs = new ArrayList<>();
        for (int j : id) {
            gameIDs.add(String.valueOf(j));
        }
        return gameIDs;
    }

    public List<String> getListOfBoards() {
        Gson gson = new Gson();
        String boards = data.getListOfBoards();
        String[] boardNames = gson.fromJson(boards, String[].class);
        return new ArrayList<>(Arrays.asList(boardNames));
    }

    public Board getBoard(String boardName, int numOfPlayers) {
        String json = data.getBoard(boardName);
        return LoadBoard.newBoardState(json, boardName, numOfPlayers);
    }
}
