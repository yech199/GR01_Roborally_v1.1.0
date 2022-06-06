package roborally.client;

public interface IGameService {

    String getGameById(int id);
    void updateGame(int id, String gameData);
    String createGame(String boardName, int numOfPlayers);
    String getListOfGames();
    String getListOfBoards();
    String getBoard(String boardName);
    String joinGame(int id);

}

