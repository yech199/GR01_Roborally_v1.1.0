package model.boardElements;

import controller.AGameController;
import model.Space;

public class Gear implements SpaceElement {
    public enum Direction {
        LEFT,
        RIGHT
    }

    // Assigned by Json file
    private Direction direction;

    public Direction getDirection() {
        return direction;
    }

    @Override
    public void doAction(AGameController gameController, Space space) {
        if (space.getActions().size() > 0) {
            switch (this.direction) {
                case LEFT -> gameController.cardController.turnLeft(space.getPlayer());
                case RIGHT -> gameController.cardController.turnRight(space.getPlayer());
            }
        }
    }
}
