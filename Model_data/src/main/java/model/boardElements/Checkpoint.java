package model.boardElements;

import controller.AGameController;
import model.Globals;
import model.Space;

public class Checkpoint implements SpaceElement {
    private int checkpointNumber;

    @Override
    public void doAction(AGameController gameController, Space space) {
        // In order to complete a checkpoint, you must be on it at the end of a register
        if (space.getPlayer() != null && space.getPlayer().getGatheredCheckpoints() == this.checkpointNumber - 1
                && gameController.board.getStep() == Globals.NO_REGISTERS - 1) {
            space.getPlayer().addCheckPoint();

            System.out.println(space.getPlayer().getName() + " has gathered "
                    + space.getPlayer().getGatheredCheckpoints() + " checkpoints out of "
                    + gameController.board.totalNoOfCheckpoints + " checkpoint.");

            System.out.println("This is after register " + (gameController.board.getStep() + 1));
        }

        if (space.getPlayer().getGatheredCheckpoints() == gameController.board.totalNoOfCheckpoints
                && gameController.board.getWinner() == null) {
            gameController.board.setWinner(space.getPlayer());
        }
    }

    public int getCheckpointNumber() {
        return checkpointNumber;
    }
}
