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
package controller;

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
                for (int j = 0; j < Globals.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Globals.NO_CARDS; j++) {
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
        if (register >= 0 && register < Globals.NO_REGISTERS) {
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
            for (int j = 0; j < Globals.NO_REGISTERS; j++) {
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

    private void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer();

        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Globals.NO_REGISTERS) {
                if (!currentPlayer.isRebooted) {
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
                }
                if (currentPlayer.isRebooted && board.getStep() == Globals.NO_REGISTERS - 1)
                    currentPlayer.isRebooted = false;

                // Next Player
                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                }
                else {
                    step++;
                    if (step < Globals.NO_REGISTERS) {
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
                case FORWARD -> cardController.moveForward(player, player.getHeading());
                case FAST_FORWARD -> cardController.moveXForward(player, 2);
                case MOVE_3 -> cardController.moveXForward(player, 3);
                case RIGHT -> cardController.turnRight(player);
                case LEFT -> cardController.turnLeft(player);
                case U_TURN -> cardController.uTurn(player);
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
            if (step < Globals.NO_REGISTERS) {
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
        player.isRebooted = true;
        if (rebootTokenSpace.getPlayer() != null) {
            cardController.moveForward(rebootTokenSpace.getPlayer(), rebootToken.getHeading());
        }
        rebootToken.doAction(this, rebootTokenSpace);
        rebootTokenSpace.setPlayer(player);
    }
}
