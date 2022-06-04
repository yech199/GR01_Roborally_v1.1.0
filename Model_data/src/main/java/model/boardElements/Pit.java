package model.boardElements;

import controller.AGameController;
import model.Player;
import model.Space;
import model.Board;

public class Pit extends SpaceElement {

    @Override
    public void doAction(AGameController gameController, Space space) {
        if (space.getActions().size() > 0) {
            gameController.reboot(space.getPlayer());
        }
    }
}
