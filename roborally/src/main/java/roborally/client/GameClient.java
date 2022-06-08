package roborally.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import fileaccess.LoadBoard;
import fileaccess.SaveBoard;
import model.Board;
import roborally.StartRoboRally;

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

    public void setPlayerState(int id, String playerName, String playerData) {
        clientController.playCards(id, playerData, playerName);
    }

    public void setGameState(int id, String jsonGameState) {
        clientController.updateGame(id, jsonGameState);
    }

    public void setGameState(Board board) {
        clientController.updateGame(board.getGameId(), SaveBoard.serializeBoard(board));
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
            // TODO Add boardname in some smart way
            if(activePlayers[i] != totalPlayers[i]) {
                result.add("Name: " + boardNames[i].concat(" | Id: " + String.valueOf(gameID[i]).concat(" | Active: "
                        + String.valueOf(activePlayers[i]))));
            } else {
                result.add("Name: " + boardNames[i].concat(" | Id: " + String.valueOf(gameID[i]).concat(" | Active: "
                        + "FULL!")));
            }
        }
        return result;
    }

    public ArrayList<String> getFormattedListOfGameIDs() {
        /*ArrayList<String> games = getListOfGames();
        ArrayList<String> result = new ArrayList<>();
        String[] id = new String[2];
        result.forEach((s -> {
            id[0] = s.split(",")[0];
            result.add(id[0]);
        }));
        return result;*/
        return null;
    }

    public ArrayList<String> getFormattedListOfBoardNames() {
        /*ArrayList<String> games = getListOfGames();
        ArrayList<String> result = new ArrayList<>();
        String[] names = new String[2];
        result.forEach((s -> {
            names[0] = s.split(",")[0];
            result.add(names[0]);
        }));
        return result;*/
        return null;
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

    public String leaveGame(int id, String playerName) {
        return clientController.leaveGame(id, playerName);
    }
    public Board joinGame(int id, String playerName) {
        String json = clientController.joinGame(id, playerName);
        return LoadBoard.loadGameState(json);
    }
}
