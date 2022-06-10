package roborally.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import fileaccess.LoadBoard;
import fileaccess.SaveBoard;
import model.Board;
import model.Player;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Used to handle responses and JSON files and communication to the server
 *
 * @author Mads Sørensen (S215805)
 * @author Mark Nielsen
 */
public class GameClient {

    ClientController clientController;
    String MyIP;
    public String playerName;
    public int gameId;

    public GameClient() {
        try {
            MyIP = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        System.out.println("Your IP-Address is: " + MyIP);
        clientController = new ClientController(MyIP);
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

    /**
     * Returns a list of games
     * @return ArrayList of list of games
     */
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

    /**
     * Get list of boardNames
     * @return
     */
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

    public Board getBoardState(int gameId) {
        String json = clientController.getBoardState(gameId);
        return LoadBoard.loadGameState(json);
    }

    public String leaveGame(int id, String playerName) {
        return clientController.leaveGame(id, playerName);
    }

    public String joinGame(int id, String playerName) {
        this.playerName = playerName.replaceAll(" ", "");
        String result = clientController.joinGame(id, playerName.replaceAll(" ", ""));
        if (!Objects.equals(result, "Game Full")) {
            gameId = Integer.parseInt(result);
        }
        return result;
    }

    public void setTargetIP(String newIP){
        clientController.setTargetIP(newIP);
    }
}
