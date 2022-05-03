package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;

public class Gear extends FieldAction {
    public enum Direction {
        LEFT,
        RIGHT
    }

    private Direction direction;

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public boolean doAction(GameController gameController, Space space) {
        if (space.getActions().size() > 0) {
            switch (getDirection()) {
                case LEFT -> {
                    Heading current = space.getPlayer().getHeading();
                    space.getPlayer().setHeading(current.prev());
                }
                case RIGHT -> {
                    Heading curr = space.getPlayer().getHeading();
                    space.getPlayer().setHeading(curr.next());
                }
            }
            return true;
        }
        return false;
    }
}
