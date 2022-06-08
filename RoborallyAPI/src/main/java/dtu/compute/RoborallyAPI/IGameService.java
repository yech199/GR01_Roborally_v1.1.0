package dtu.compute.RoborallyAPI;

import model.Board;

public interface IGameService {

    String getGameById(int id);
    void updateGame(int id, String gameState);
    String createGame(String boardname, int numOfPlayers);
    String getListOfGames();
    String getListOfBoards();
    String getBoardState(String boardName);
    String leaveGame(int id, String playername);
    String joinGame(int id, String playerName);
    String playCards(int id, String playername, String playerData);

}
