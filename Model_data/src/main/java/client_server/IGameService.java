package client_server;

public interface IGameService {

    String getGameById(int id, String playerName);

    String updateGame(int id, String playerName, String playerState);

    String createGame(String boardName, int numOfPlayers);

    String getListOfGames();

    String getListOfBoards();

    String getBoardState(int gameId);

    String joinGame(int id, String playerName);

    String leaveGame(int id, String playerName);

    String setPlayerState(int id, String playerName, String playerData);
}

