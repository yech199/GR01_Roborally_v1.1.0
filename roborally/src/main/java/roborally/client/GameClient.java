package roborally.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import fileaccess.LoadBoard;
import fileaccess.SaveBoard;
import model.Board;
import model.Player;

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

    public Board getPlayerStateByGameId(int id, String playerName) {
        String json = clientController.getPlayerStateByGameId(id, playerName);
        return LoadBoard.loadGameState(json);
    }

    public String createGame(String boardName, int numOfPlayers) {
        String gameId = clientController.createGame(boardName, numOfPlayers);
        this.gameId = Integer.parseInt(gameId);
        return gameId;
    }

    public ArrayList<String> getListOfGames() {
        Gson gson = new Gson();
        String games = clientController.getListOfGames();
        JsonObject data = gson.fromJson(games, JsonObject.class);
        ArrayList<String> result = new ArrayList<>();

        if (data != null) {
            int[] gameID = gson.fromJson(data.get("gameId"), int[].class);
            String[] boardNames = gson.fromJson(data.get("boardNames"), String[].class);
            int[] activePlayers = gson.fromJson(data.get("activePlayers"), int[].class);
            int[] totalPlayers = gson.fromJson(data.get("maxNumberOfPlayers"), int[].class);


            for (int i = 0; i < gameID.length && i < boardNames.length; i++) {
                if (activePlayers[i] != totalPlayers[i]) {
                    result.add("Name: " + boardNames[i].concat(" | Id: " + String.valueOf(gameID[i]).concat(" | Active: " + String.valueOf(activePlayers[i]))));
                } else {
                    result.add("Name: " + boardNames[i].concat(" | Id: " + String.valueOf(gameID[i]).concat(" | Active: ").concat("FULL!")));
                }
            }
        }
        return result;
    }

    public List<String> getListOfBoards() {
        Gson gson = new Gson();
        String boards = clientController.getListOfBoards();
        String[] boardNames = gson.fromJson(boards, String[].class);
        if (boardNames[0] != null) {
            return new ArrayList<>(Arrays.asList(boardNames));
        } else {
            return null;
        }
    }

    public String joinGame(int id, String playerName) {
        this.playerName = playerName.replaceAll(" ", "");
        String result = clientController.joinGame(id, playerName.replaceAll(" ", ""));
        if (!Objects.equals(result, "Game Full")) {
            gameId = Integer.parseInt(result);
        }
        return result;
    }

    public String leaveGame(int id, String playerName) {
        return clientController.leaveGame(id, playerName);
    }

    public void setPlayerState(int id, Player player) {
        String playerData = SaveBoard.serializePlayer(player);
        clientController.setPlayerState(id, playerName, playerData);
    }
}
