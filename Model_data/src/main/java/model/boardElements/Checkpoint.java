package model.boardElements;

import controller.AGameController;
import model.Player;
import model.Space;

public class Checkpoint extends SpaceElement {
    private int checkpointNumber;

    @Override
    public void doAction(AGameController gameController, Space space) {
        if (space.getPlayer() != null && space.getPlayer().getGatheredCheckpoints() == this.checkpointNumber - 1
                && gameController.board.getStep() == Player.NO_REGISTERS - 1) {
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
