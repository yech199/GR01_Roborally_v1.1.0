package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.CommandCard;
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

            // Used to determine what indexes push panels should activate on
            int n1 = 0, n2 = 1;
            boolean firstFound = false, secFound = false;
            for(int i = 0; i < 5; i++) {
                if(firstFound) {
                    if (pushPanelLabel[i] == 0 && !secFound) {
                        n2++;
                    } else {
                        if (n1 == n2) {
                            n2++;
                        }
                        secFound = true;
                    }
                }
                if (pushPanelLabel[i] == 0 && !firstFound) {
                    n1++;
                    n2++;
                } else {
                    firstFound = true;
                }
            }
            // Get the current card from the current program field
            // and check if placed in one of the activation registers for push panel
            CommandCard card = gameController.board.getCurrentPlayer().getProgramField(step).getCard();
            if(pushPanelLabel[step] == n1 || pushPanelLabel[step] == n2) {
                if (card != null) {
                    if (player != null && neighbour != null) {
                        Heading playerHeading = player.getHeading();
                        player.setHeading(heading);
                        gameController.moveForward(player);
                        player.setHeading(playerHeading);
                    }
                }
            }
            return true;
        }

        return false;
    }
}
