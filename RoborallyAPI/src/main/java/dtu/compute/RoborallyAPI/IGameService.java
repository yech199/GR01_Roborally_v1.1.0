package dtu.compute.RoborallyAPI;

public interface IGameService {

    String getGameById(int id);
    void updateGame(int id, String gameData);
    String getGame();
    int startGame();
    String getListOfGames();
    String joinGame(int id);

}
