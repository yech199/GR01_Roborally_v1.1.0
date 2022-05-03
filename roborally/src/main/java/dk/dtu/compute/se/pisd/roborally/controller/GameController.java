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
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class GameController {
    final public Board board;
    private Antenna antenna;
    private Space antennaSpace;

    public GameController(@NotNull Board board) {
        this.board = board;

        // Finds antenna if it exists
        for (var x : board.getSpaces()) {
            if (antenna != null) break;
            for (var y : x) {
                if (antenna != null) break;
                for (var action : y.getActions()) {
                    if (action instanceof Antenna antenna) {
                        this.antenna = antenna;
                        this.antennaSpace = y;
                        break;
                    }
                }
            }
        }
    }

    /**
     * This is just some dummy controller operation to make a simple move to see something
     * happening on the board. This method should eventually be deleted!
     *
     * @param space the space to which the current player should move
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space) {
        // TODO Assignment V1: method should be implemented by the students:
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

    // XXX: V2
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    // XXX: V2
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    // XXX: V2
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    // XXX: V2
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    // XXX: V2
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    // XXX: V2
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    // XXX: V2
    private void continuePrograms() {
        do {
            if (antenna != null && board.getPlayerNumber(board.getCurrentPlayer()) == 0) {
                antenna.doAction(this, antennaSpace);
                board.setCurrentPlayer(board.getPlayer(0));
            }
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
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
                    //doFieldEffect(currentPlayer); Implement field effects in their own classes extending FieldAction
                    // executing the actions on the space a player moves to
                    Space space = currentPlayer.getSpace();
                    for (FieldAction action : space.getActions()) {
                        action.doAction(this, space);
                    }

                    //Check winner
                    if (currentPlayer.isWinner()) {
                        Winner(currentPlayer);
                    }
                }
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
        if (player != null && player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).

            switch (command) {
                case FORWARD -> this.moveForward(player);
                case RIGHT -> this.turnRight(player);
                case LEFT -> this.turnLeft(player);
                case FAST_FORWARD -> this.fastForward(player, 2);
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

    // TODO: V2
    public void moveForward(@NotNull Player player) {
        Space space = player.getSpace();

        if (player.board == board && space != null) {
            Heading heading = player.getHeading();

            Space target = board.getNeighbour(space, heading);
            if (target != null) {
                List<Heading> walls = target.getWalls();
                if (!walls.contains(heading)) {
                    if (target.getPlayer() == null) {
                        // Move player
                        target.setPlayer(player);
                    }
                    else {
                        // Push other Player
                        board.getNeighbour(target, heading).setPlayer(target.getPlayer());
                        target.setPlayer(player);
                    }
                }
            }
        }
    }

    // TODO: V2
    public void fastForward(@NotNull Player player, int moves) {
        for (int i = 0; i < moves; i++) {
            moveForward(player);
        }
    }

    // TODO: V2
    public void turnRight(@NotNull Player player) {
        if (player.board == board) {
            player.setHeading(player.getHeading().next());
        }
    }

    // TODO: V2
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

    public void reboot(Player player, Board board) {
        int checkpoint = player.getCheckPoints();

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (checkpoint == 1) {
                    player.setSpace(board.getSpace(0, 0));
                }
                else {
                    if (board.getSpace(x, y).checkpointNumber == checkpoint - 1) {
                        player.setSpace(board.getSpace(x, y));

                    }
                }
            }
        }
    }

    public void Winner(Player player) {
        // Player has won
        System.out.println(player.getName() + " har vundet");
        JOptionPane.showMessageDialog(null, player.getName()
                + " har vundet", "InfoBox: " + player.getName() + " har vundet", JOptionPane.INFORMATION_MESSAGE);
        Platform.exit();
    }

    /*public void doFieldEffect (Player player){

        // Check if player is on top of checkpoint
        /*if (player.getSpace().checkpointNumber == player.getNextCheckPoint()) {
            player.setNextCheckPoint(player.getNextCheckPoint() + 1);
        }

        // Check if player is on a push panel
        if (player.getSpace().isPushPanel) {
            Heading heading = player.getSpace().pushPanelDirection;
            Space target = board.getNeighbour(player.getSpace(), heading);

            if (!target.isWall && target.getPlayer() == null && player.getSpace().getPushPanelLabel()[board.getStep()]) {
                target.setPlayer(player);
            }
        }

    }*/

}
