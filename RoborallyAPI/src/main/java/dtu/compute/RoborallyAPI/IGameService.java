package dtu.compute.RoborallyAPI;

import model.Board;

public interface IGameService {

    Board getGameById(int id);
    void updateGame(int id, String gameState);
    Board createGame(String boardname);
    String getListOfGames();
    String getListOfBoards();
    String getBoardState(String boardName);
    Board getBoard(String boardName);
    String joinGame(int id);

}
