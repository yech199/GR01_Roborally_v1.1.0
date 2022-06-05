package roborally.client;

public interface IGameService {

    String getGameById(int id);
    void updateGame(int id, String gameData);
    String getGame();
    int startGame();
    String getListOfGames();
    String getListOfBoards();
    String getBoard(String boardName);
    String joinGame(int id);

}

