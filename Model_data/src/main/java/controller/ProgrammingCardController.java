package controller;

import controller.AGameController;
import model.*;
import model.boardElements.*;
import org.jetbrains.annotations.NotNull;

public class ProgrammingCardController {
    AGameController gameController;

    public ProgrammingCardController(AGameController gameController) {
        this.gameController = gameController;
    }

    /**
     * Moves the player in the direction of the heading if possible
     *
     * @param player        being moved
     * @param moveDirection of the player or of the spaceElement moving you
     */
    public void moveForward(@NotNull Player player, Heading moveDirection) {
        try {
            // Heading moveDirection = player.getHeading();
            Space target = gameController.board.getNeighbour(player.getSpace(), moveDirection);

            if (player.getSpace().getActions().size() > 0) {
                for (SpaceElement space : player.getSpace().getActions()) {

                    // If the player is standing ON a push panel, and is trying to walk into the wall throw exception
                    if (space instanceof PushPanel pushPanel && moveDirection == pushPanel.getHeading().next().next()
                            && target == gameController.board.getNeighbour(player.getSpace(), moveDirection)) {
                        throw new ImpossibleMoveException(player, player.getSpace(), moveDirection);
                    }
                }
            }

            // Target out of board. You cannot move out of the board or into an antenna (you can only be pushed out)
            if (target == null || target == gameController.antennaSpace)
                throw new ImpossibleMoveException(player, player.getSpace(), moveDirection);

                // If the target contains another player
            else if (target.getPlayer() != null) {
                boolean isValid = checkIfMoveToTargetWithPlayerIsValid(player, target);
                if (!isValid) throw new ImpossibleMoveException(player, player.getSpace(), moveDirection);
            } else {
                if (player.getSpace().hasWallPointing(moveDirection) || target.hasWallPointing(moveDirection.next().next())) {
                    throw new ImpossibleMoveException(player, player.getSpace(), moveDirection);
                }

                // If the player is trying to walk onto a push panel THROUGH a wall throw exception
                if (target.getActions().size() > 0) {
                    for (SpaceElement space : target.getActions()) {
                        if (space instanceof PushPanel pushPanel && moveDirection == pushPanel.getHeading()) {
                            throw new ImpossibleMoveException(player, player.getSpace(), moveDirection);
                        }
                    }
                }
            }
            // Free? Then move player
            target.setPlayer(player);

            if (target.getActions().size() > 0) {
                for (SpaceElement space : target.getActions()) {
                    if (space instanceof Pit pit) {
                        pit.doAction(gameController, target);
                    }
                }
            }

        } catch (ImpossibleMoveException e) {
            if (gameController.rebootTokenSpace.getPlayer() != null) {
                moveForward(player, moveDirection.next());
            }
            System.out.println("Move impossible");
        }

    }

    /**
     * @param player who wants to push another player
     * @param target the space of the player being pushed
     * @return Whether the push is a valid move
     * @throws ImpossibleMoveException if the move isn't a valid move
     */
    private boolean checkIfMoveToTargetWithPlayerIsValid(@NotNull Player player, Space target) throws ImpossibleMoveException {
        Heading pushDirection = player.getHeading();
        Player targetPlayer = target.getPlayer();
        Space tmpTarget = gameController.board.getNeighbour(targetPlayer.getSpace(), pushDirection);

        if (targetPlayer.getSpace().getActions().size() > 0) {
            for (SpaceElement space : targetPlayer.getSpace().getActions()) {
                // If the player being pushed is standing ON a push panel, and is being pushed into a "wall" throw exception
                if (space instanceof PushPanel pushPanel) {
                    if (player.getHeading() == pushPanel.getHeading().next().next()
                            && tmpTarget == gameController.board.getNeighbour(target, player.getHeading()))
                        throw new ImpossibleMoveException(targetPlayer, targetPlayer.getSpace(), pushDirection);
                    pushDirection = pushPanel.getHeading();
                }
            }
        }

        boolean isValid = true;

        // Check for out of board
        if (tmpTarget == null) {
            gameController.reboot(targetPlayer);
            return true;
        }
        // Check for walls and antenna
        else if (target.hasWallPointing(pushDirection) || tmpTarget.hasWallPointing(pushDirection.next().next())
                || tmpTarget == gameController.antennaSpace) {
            return false;
        }
        // Check for player
        else if (tmpTarget.getPlayer() != null) {
            isValid = checkIfMoveToTargetWithPlayerIsValid(player, tmpTarget);
        }
        // Check for push panels
        else if (tmpTarget.getActions().size() > 0) {
            for (SpaceElement space : tmpTarget.getActions()) {
                if (space instanceof PushPanel pushPanel && player.getHeading() == pushPanel.getHeading()) {
                    throw new ImpossibleMoveException(player, player.getSpace(), player.getHeading());
                }
            }
        }

        // Moves the pushed player(s) recursively
        if (isValid) {
            tmpTarget.setPlayer(targetPlayer);

            // Only do action for the player which was placed onto a new space if that space isn't
            // a conveyor belt, a checkpoint or a push panel
            if (tmpTarget.getActions().size() > 0) {
                for (SpaceElement space : tmpTarget.getActions()) {
                    if (!(space instanceof ConveyorBelt) && !(space instanceof Checkpoint)
                            && !(space instanceof PushPanel)) {
                        space.doAction(gameController, targetPlayer.getSpace());
                    }
                }
            }
        }
        return true;
    }

    public void moveXForward(@NotNull Player player, int moves) {
        for (int i = 0; i < moves; i++) {
            if (player.getSpace().getActions().size() > 0) {
                for (SpaceElement space : player.getSpace().getActions()) {
                    if (space instanceof Pit)
                        return;
                }
            }

            moveForward(player, player.getHeading());
        }
    }

    public void turnRight(@NotNull Player player) {
        if (player.board == gameController.board) {
            player.setHeading(player.getHeading().next());
        }
    }

    public void turnLeft(@NotNull Player player) {
        if (player.board == gameController.board) {
            player.setHeading(player.getHeading().prev());
        }
    }

    public void uTurn(@NotNull Player player) {
        if (player.board == gameController.board) {
            player.setHeading(player.getHeading().next().next());
        }
    }

    class ImpossibleMoveException extends Exception {

        private Player player;
        private Space space;
        private Heading heading;

        public ImpossibleMoveException(Player player, Space space, Heading heading) {
            super("Move impossible");
            System.out.println(player.getName() + " tried to move " + heading + " from " + space.toString());
            this.player = player;
            this.space = space;
            this.heading = heading;
        }
    }
}
