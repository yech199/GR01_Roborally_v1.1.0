package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

import java.util.ArrayList;

public class PushPanel extends FieldAction {
    private Heading heading;
    //private int pushPanelAmount = 0;
    private int[] pushPanelLabel;

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    @Override
    public boolean doAction(GameController gameController, Space space) {
        if (space.getActions().size() > 0) {
            Space neighbour = space.board.getNeighbour(space, heading);
            Player player = space.getPlayer();
            int step = gameController.board.getStep();

            System.out.println(pushPanelLabel[1]);
            System.out.println(pushPanelLabel[3]);

            if(pushPanelLabel[0] == step || pushPanelLabel[1] == step) {
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
