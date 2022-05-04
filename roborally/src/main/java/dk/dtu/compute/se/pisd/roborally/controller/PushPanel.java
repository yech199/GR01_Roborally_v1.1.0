package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

import java.util.ArrayList;

public class PushPanel extends FieldAction {
    private Heading heading;
    private ArrayList<Integer> executeNumbers;

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    public void setExecuteNumbers(ArrayList<Integer> executeNumbers) {
        this.executeNumbers = executeNumbers;
    }

    public ArrayList<Integer> getExecuteNumbers() {
        return executeNumbers;
    }

    @Override
    public boolean doAction(GameController gameController, Space space) {
        if (space.getActions().size() > 0) {
            Space neighbour = space.board.getNeighbour(space, heading);
            Player player = space.getPlayer();
            if(executeNumbers.contains(gameController.board.getStep() + 1)) {
                if (player != null && neighbour != null) {
                    Heading playerHeading = player.getHeading();
                    player.setHeading(heading);
                    gameController.moveForward(player);
                    player.setHeading(playerHeading);
                }
            }
            return true;
        }
        return false;
    }
}
