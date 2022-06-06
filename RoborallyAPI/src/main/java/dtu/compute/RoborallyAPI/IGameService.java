package dtu.compute.RoborallyAPI;

import model.Board;

public interface IGameService {

    Board getGameById(int id);
    void updateGame(int id, String gameState);
    String createGame(String boardname, int numOfPlayers);
    String getListOfGames();
    String getListOfBoards();
    String getBoardState(String boardName);
    String joinGame(int id);

}
