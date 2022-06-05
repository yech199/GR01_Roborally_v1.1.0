package dtu.compute.RoborallyAPI.Model;

public class Game {
    private int id;
    private transient String gameState;
    private int amountOfPlayers;

    public Game(int id) {
        this.id = id;
        this.amountOfPlayers = 1;
    }

    public void addPlayer(){
        amountOfPlayers++;
    }

    public void removePlayer() {amountOfPlayers--;}

    public int getAmountOfPlayers() {
        return amountOfPlayers;
    }

    public String getGameState() {
        return gameState;
    }

    public void setGameState(String gameState) {
        this.gameState = gameState;
    }

    public int getId(){
        return id;
    }
}
