package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.model.Board;

public class Pit extends FieldAction {

    @Override
    public boolean doAction(GameController gameController, Space space) {
        if (space.getActions().size() > 0) {
            Board board = gameController.board;
            Player player = space.getPlayer();

            gameController.reboot(player, board);

            return true;
        }
        return false;
    }

}
