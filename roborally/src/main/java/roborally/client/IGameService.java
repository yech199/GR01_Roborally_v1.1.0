package roborally.client;

public interface IGameService {

    String getGameById(int id);
    void updateGame(int id, String gameData);
    String createGame(String boardName, int numOfPlayers);
    String getListOfGames();
    String getListOfBoards();
    String getBoardState(String boardName);
    String joinGame(int id, String playername);
    void leaveGame(int id, String playername);
    String playCards(int id, String playerData);

}

