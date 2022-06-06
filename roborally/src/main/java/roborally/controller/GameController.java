/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package roborally.controller;

import controller.AGameController;
import model.*;
import model.boardElements.*;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class GameController extends AGameController {
    public GameController(@NotNull Board board) {
        super(board);
    }

    /**
     * This is just some dummy controller operation to make a simple move to see something
     * happening on the board. This method should eventually be deleted!
     *
     * @param space the space to which the current player should move
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space) {
        // method should be implemented by the students:
        //   - the current player should be moved to the given space
        //     (if it is free()
        //   - and the current player should be set to the player
        //     following the current player
        //   - the counter of moves in the game should be increased by one
        //     if the player is moved

        if (space != null && space.board == board) {
            Player currentPlayer = board.getCurrentPlayer();
            if (currentPlayer != null && space.getPlayer() == null) {
                currentPlayer.setSpace(space);
                int playerNumber = (board.getPlayerNumber(currentPlayer) + 1) % board.getPlayersNumber();
                board.setCurrentPlayer(board.getPlayer(playerNumber));
            }
        }
    }

    /**
     * Generates a fixed number of holders for the cards the player chooses
     * and autogenerate a number of cards the player can choose from.
     */
    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
    }

    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    private void continuePrograms() {
        do {
            if (antenna != null && board.getPlayerNumber(board.getCurrentPlayer()) == 0) {
                antenna.doAction(this, antennaSpace);
                board.setCurrentPlayer(board.getPlayer(0));
            }
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode() && board.getWinner() == null);
    }

    // XXX: V2
    private void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    if (card.command == Command.OPTION_LEFT_RIGHT) {
                        board.setPhase(Phase.PLAYER_INTERACTION);
                        return;
                    }
                    Command command = card.command;
                    executeCommand(currentPlayer, command);
                }

                //doFieldEffect(currentPlayer); Implement field effects in their own classes extending FieldAction
                // executing the actions on the space a player moves to
                Space space = currentPlayer.getSpace();
                for (SpaceElement action : space.getActions()) {
                    action.doAction(this, space);
                }

                // Next Player
                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                }
                else {
                    step++;
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                    }
                    else {
                        startProgrammingPhase();
                    }
                }
            }
            else {
                // this should not happen
                assert false;
            }
        }
        else {
            // this should not happen
            assert false;
        }
    }

    // Executes Commands of cards
    private void executeCommand(@NotNull Player player, Command command) {
        if (player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).

            switch (command) {
                case FORWARD -> this.moveForward(player, player.getHeading());
                case RIGHT -> this.turnRight(player);
                case LEFT -> this.turnLeft(player);
                case FAST_FORWARD -> this.moveXForward(player, 2);
                default -> {
                }
                // DO NOTHING (for now)
            }
        }
    }

    public void executeCommandOptionAndContinue(Command option) {
        board.setPhase(Phase.ACTIVATION);
        Player currentPlayer = board.getCurrentPlayer();
        executeCommand(currentPlayer, option);
        int step = board.getStep();

        int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
        if (nextPlayerNumber < board.getPlayersNumber()) {
            board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
        }
        else {
            step++;
            if (step < Player.NO_REGISTERS) {
                makeProgramFieldsVisible(step);
                board.setStep(step);
                board.setCurrentPlayer(board.getPlayer(0));
            }
            else {
                startProgrammingPhase();
            }
        }
        continuePrograms();
    }

    /**
     * Moves the player in the direction of the heading if possible
     * @param player being moved
     * @param moveDirection of the player or of the spaceElement moving you
     */
    public void moveForward(@NotNull Player player, Heading moveDirection) {
        try {
            // Heading moveDirection = player.getHeading();
            Space target = board.getNeighbour(player.getSpace(), moveDirection);

            if (player.getSpace().getActions().size() > 0) {
                for (SpaceElement space : player.getSpace().getActions()) {
                    if (space instanceof PushPanel pushPanel && player.getHeading() == pushPanel.getHeading().next().next()
                            && target == board.getNeighbour(player.getSpace(), player.getHeading())) {
                        throw new ImpossibleMoveException(player, player.getSpace(), player.getHeading());
                    }
                }
            }

            // Target out of board. You cannot move out of the board or into an antenna (you can only be pushed out)
            if (target == null || target == this.antennaSpace)
                throw new ImpossibleMoveException(player, player.getSpace(), moveDirection);

            // If the target contains another player
            else if (target.getPlayer() != null) {
                boolean isValid = checkIfMoveToTargetWithPlayerIsValid(player, target);
                if (!isValid) throw new ImpossibleMoveException(player, player.getSpace(), moveDirection);
            }

            else {
                if (player.getSpace().hasWallPointing(moveDirection) || target.hasWallPointing(moveDirection.next().next())) {
                    throw new ImpossibleMoveException(player, player.getSpace(), moveDirection);
                }
            }
            // Free? Then move player
            target.setPlayer(player);
        } catch (ImpossibleMoveException e) {
            System.out.println("Move impossible");
        }

    }

    /**
     *
     * @param player who wants to push another player
     * @param target the space of the player being pushed
     * @return Whether the push is a valid move
     * @throws ImpossibleMoveException if the move isn't a valid move
     */
    private boolean checkIfMoveToTargetWithPlayerIsValid(@NotNull Player player, Space target) throws ImpossibleMoveException {
        Heading pushDirection = player.getHeading();
        Player targetPlayer = target.getPlayer();
        Space tmpTarget = board.getNeighbour(targetPlayer.getSpace(), pushDirection);

        if (targetPlayer.getSpace().getActions().size() > 0) {
            for (SpaceElement space : targetPlayer.getSpace().getActions()) {
                if (space instanceof PushPanel pushPanel) {
                    if (player.getHeading() == pushPanel.getHeading().next().next()
                            && tmpTarget == board.getNeighbour(target, player.getHeading()))
                        throw new ImpossibleMoveException(targetPlayer, targetPlayer.getSpace(), pushDirection);
                    pushDirection = pushPanel.getHeading();
                }
            }
        }

        boolean isValid = true;

        if (tmpTarget == null) {
            reboot(targetPlayer);
            return true;
        }
        else if (target.hasWallPointing(pushDirection) || tmpTarget.hasWallPointing(pushDirection.next().next())
                || tmpTarget == this.antennaSpace) {
            return false;
        }

        else if (tmpTarget.getPlayer() != null) {
            isValid = checkIfMoveToTargetWithPlayerIsValid(player, tmpTarget);
        }

        // Moves the pushed player(s) recursively
        if (isValid) {
            tmpTarget.setPlayer(targetPlayer);

            if (tmpTarget.getActions().size() > 0) {
                for (SpaceElement space : tmpTarget.getActions()) {
                    if (!(space instanceof ConveyorBelt) && !(space instanceof Checkpoint)
                            && !(space instanceof PushPanel)) {
                        space.doAction(this, targetPlayer.getSpace());
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
        if (player.board == board) {
            player.setHeading(player.getHeading().next());
        }
    }

    public void turnLeft(@NotNull Player player) {
        if (player.board == board) {
            player.setHeading(player.getHeading().prev());
        }
    }

    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
        if (sourceCard != null && targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            return true;
        }
        else {
            return false;
        }
    }

    public void reboot(Player player) {
        // int checkpoint = player.getCheckPoints();
        // if (checkpoint == 1) {
        //     player.setSpace(board.getSpace(0, 0));
        // }
        // else {
        //     for (int x = 0; x < board.getSpaces().length; x++) {
        //         for (int y = 0; y < board.getSpaces()[0].length; y++) {
        //             if (board.getSpace(x, y).checkpointNumber == checkpoint - 1) {
        //                 player.setSpace(board.getSpace(x, y));
        //             }
        //         }
        //     }
        // }
        
        rebootTokenSpace.setPlayer(player);
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
