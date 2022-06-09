package roborally.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import fileaccess.LoadBoard;
import fileaccess.SaveBoard;
import model.Board;
import model.Player;

import javax.print.attribute.HashDocAttributeSet;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GameClient {

    ClientController clientController;
    public String playerName;
    public int gameId;

    public GameClient() {
        clientController = new ClientController();
    }

    public String createGame(String boardName, int numOfPlayers) {
        String gameId = clientController.createGame(boardName, numOfPlayers);
        this.gameId = Integer.parseInt(gameId);
        return gameId;
    }

    public Board getGameState(int id, String playerName) {
        String json = clientController.getGameById(id, playerName);
        return LoadBoard.loadGameState(json);
    }

    public void setPlayerState(int gameId, Player player) {
        String playerData = SaveBoard.serializePlayer(player);
        clientController.setPlayerState(gameId, playerName, playerData);
    }

    public void setGameState(int id, String jsonGameState) {
        clientController.updateGame(id, playerName, jsonGameState);
    }

    public ArrayList<String> getListOfGames() {
        Gson gson = new Gson();
        String games = clientController.getListOfGames();
        JsonObject data = gson.fromJson(games, JsonObject.class);

        int[] gameID = gson.fromJson(data.get("gameId"), int[].class);
        String[] boardNames = gson.fromJson(data.get("boardNames"), String[].class);
        int[] activePlayers = gson.fromJson(data.get("activePlayers"), int[].class);
        int[] totalPlayers = gson.fromJson(data.get("maxNumberOfPlayers"), int[].class);

        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < gameID.length && i < boardNames.length; i++) {
            if (activePlayers[i] != totalPlayers[i]) {
                result.add("Name: " + boardNames[i].concat(" | Id: " + String.valueOf(gameID[i]).concat(" | Active: " + String.valueOf(activePlayers[i]))));
            } else {
                result.add("Name: " + boardNames[i].concat(" | Id: " + String.valueOf(gameID[i]).concat(" | Active: ").concat("FULL!")));
            }
        }
        return result;
    }

    public List<String> getListOfBoards() {
        Gson gson = new Gson();
        String boards = clientController.getListOfBoards();
        String[] boardNames = gson.fromJson(boards, String[].class);
        return new ArrayList<>(Arrays.asList(boardNames));
    }

    public Board getBoardState(int gameId) {
        String json = clientController.getBoardState(gameId);
        return LoadBoard.loadGameState(json);
    }

    public String leaveGame(int id, String playerName) {
        return clientController.leaveGame(id, playerName);
    }

    public String joinGame(int id, String playerName) {
        this.playerName = playerName;
        String result = clientController.joinGame(id, playerName);
        gameId = Integer.parseInt(result);

        return result;
    }
}
