package model.boardElements;

import controller.AGameController;
import model.Heading;
import model.Space;

public class PushPanel extends SpaceElement {
    private Heading heading;
    private int[] pushPanelLabel;

    public Heading getHeading() {
        return heading;
    }

    @Override
    public void doAction(AGameController gameController, Space space) {
        if (space.getActions().size() > 0) {

            // Determines what indexes push panels should activate on
            int n1 = 0, n2 = 1;
            boolean firstFound = false;

            for (int i = 0; i < 5; i++) {
                if (firstFound) {
                    if (pushPanelLabel[i] == 0) {
                        n2++;
                    }
                    else {
                        if (n1 == n2) {
                            n2++;
                        }
                        // Break when both have been found
                        break;
                    }
                }
                else if (pushPanelLabel[i] == 0) {
                    n1++;
                    n2++;
                }
                else {
                    firstFound = true;
                }
            }
            
            if ((gameController.board.getStep() == n1 || gameController.board.getStep() == n2) && space.getPlayer() != null) {
                gameController.moveForward(space.getPlayer(), this.heading);
            }
        }
    }

    public int[] getPushPanelLabels() {
        int[] registers = new int[]{0, 0};
        int registerIndex = 0;
        for (int i = 0; i < pushPanelLabel.length; i++) {
            if (pushPanelLabel[i] == 1) {
                registers[registerIndex] = i + 1;
                registerIndex++;
            }
        }
        return registers;
    }
}
