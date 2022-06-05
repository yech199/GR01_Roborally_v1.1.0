package model.boardElements;

import controller.AGameController;
import model.CommandCard;
import model.Heading;
import model.Player;
import model.Space;

public class PushPanel extends SpaceElement {
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
    public void doAction(AGameController gameController, Space space) {
        if (space.getActions().size() > 0) {
            Space neighbour = space.board.getNeighbour(space, heading);
            Player player = space.getPlayer();

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

            int step = gameController.board.getStep();
            // Get the current card from the current program field
            // and check if placed in one of the activation registers for push panel
            if(step == n1 || step == n2) {
                CommandCard card = gameController.board.getCurrentPlayer().getProgramField(step).getCard();
                if (card != null) {
                    System.out.println(card.getName());
                    if (player != null && neighbour != null) {
                        Heading playerHeading = player.getHeading();
                        player.setHeading(heading);
                        gameController.moveForward(player);
                        player.setHeading(playerHeading);
                    }
                }
            }
        }
    }
}
