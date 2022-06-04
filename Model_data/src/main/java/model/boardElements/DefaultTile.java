package model.boardElements;

import controller.AGameController;
import model.Space;

public class DefaultTile extends SpaceElement {
    public boolean isRebootToken = false;

    @Override
    public void doAction(AGameController gameController, Space space) {
    }
}
