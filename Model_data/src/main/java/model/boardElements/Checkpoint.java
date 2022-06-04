package model.boardElements;

import controller.AGameController;
import model.Player;
import model.Space;

public class Checkpoint extends FieldAction {

    private int checkpointNumber;

    public void setCheckpointNumber(int checkpointNumber) {
        this.checkpointNumber = checkpointNumber;
    }

    public int getCheckpointNumber() {
        return checkpointNumber;
    }

    @Override
    public boolean doAction(AGameController gameController, Space space) {
        if (space.getActions().size() > 0) {
            Checkpoint checkpoint = (Checkpoint) space.getActions().get(0);

            Player player = space.getPlayer();

            if (player != null && player.getCheckPoints()+1 == checkpoint.checkpointNumber) {
                player.nextCheckPoint();
                return true;
            }
        }

        return false;
    }
}
