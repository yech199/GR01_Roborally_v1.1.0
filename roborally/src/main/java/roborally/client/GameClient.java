package roborally.client;

// import fileaccess.LoadSaveBoard;
import fileaccess.LoadBoard;
import model.Board;

public class GameClient {

    ClientController data;

    public GameClient() {
        data = new ClientController();
    }

    public int createGame() {
        return data.startGame();
    }

    public Board getGame(String gameName) {
        String json = data.getGame();
        return LoadBoard.loadGameState(json, gameName);
    }

    public void setGame(int id, String jsonGameState) {
        data.updateGame(id, jsonGameState);
    }
}
