package dk.dtu.compute.se.pisd.roborally.client;

public interface IGameService {

    public String getGameById(int id);
    public void updateGame(String gameData);
    public String getGame();

}
