package model.BoardElements;

import controller.AGameController;
import model.Space;

public class Laser extends FieldAction {
    @Override
    public boolean doAction(AGameController gameController, Space space) {
        return false;
    }
}
