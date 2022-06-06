package model.boardElements;

import controller.AGameController;
import model.Heading;
import model.Space;

public class RebootToken extends SpaceElement {
    private Heading heading;

    @Override
    public void doAction(AGameController gameController, Space space) {
    }

    public Heading getHeading() {
        return heading;
    }
}
