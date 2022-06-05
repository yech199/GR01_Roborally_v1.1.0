package model.boardElements;

import controller.AGameController;
import model.Heading;
import model.Space;

public class Gear extends SpaceElement {
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
                case LEFT -> {
                    gameController.turnLeft(space.getPlayer());
                }
                case RIGHT -> {
                    gameController.turnRight(space.getPlayer());
                }
            }
        }
    }
}
