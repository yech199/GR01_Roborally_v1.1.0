package dtu.compute.RoborallyAPI.Model;

public class Board {
    String name;
    String boardState;

    public Board(String name) {
        this.name = name;
    }

    public void setGameState(String jsonBoardState) {
        this.boardState = jsonBoardState;
    }

    public String getBoardState() {
        return boardState;
    }
    public String getName() {
        return name;
    }
}
