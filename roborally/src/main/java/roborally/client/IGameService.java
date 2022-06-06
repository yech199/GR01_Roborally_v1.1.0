package roborally.client;

public interface IGameService {

    String getGameById(int id);
    void updateGame(int id, String gameData);
    int createGame(String boardName);
    String getListOfGames();
    String getListOfBoards();
    String getBoard(String boardName);
    String joinGame(int id);

}

