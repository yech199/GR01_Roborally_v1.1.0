package model.boardElements;

import controller.AGameController;
import model.Heading;
import model.Space;

public class RebootToken extends SpaceElement {
    private Heading heading;

    @Override
    public void doAction(AGameController gameController, Space space) {
    //    TODO: If more than one stands on this they push in the direction of the token
    }

    public Heading getHeading() {
        return heading;
    }
}
