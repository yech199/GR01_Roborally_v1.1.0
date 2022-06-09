package model.boardElements;

import controller.AGameController;
import model.Space;

/**
 * This class is used for spaces containing only walls, no SpaceElements
 */
public class DefaultTile implements SpaceElement {
    @Override
    public void doAction(AGameController gameController, Space space) {
    }
}
