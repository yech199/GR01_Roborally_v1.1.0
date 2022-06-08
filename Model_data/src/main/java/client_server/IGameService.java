package client_server;

public interface IGameService {

    String getGameById(int id);
    void updateGame(int id, String gameData);
    String createGame(String boardName, int numOfPlayers);
    String getListOfGames();
    String getListOfBoards();
    String getBoardState(String boardName);
    String joinGame(int id, String playername);
    String leaveGame(int id, String playername);
    String playCards(int id, String playername, String playerData);
}

