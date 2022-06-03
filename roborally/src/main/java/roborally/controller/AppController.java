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

import designpatterns.observer.Subject;
import designpatterns.observer.Observer;

import roborally.RoboRally;

import roborally.util.ResourcesUtil;
import roborally.fileaccess.LoadSaveBoard;
import model.Board;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class AppController implements Observer {
    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);

    final private RoboRally roboRally;
    private GameController gameController;

    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }

    public void newGame() {
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        dialog.setTitle("Player number");
        dialog.setHeaderText("Select number of players");
        Optional<Integer> result = dialog.showAndWait();

        if (result.isPresent()) {
            if (gameController != null) {
                // The UI should not allow this, but in case this happens anyway.
                // give the user the option to save the game or abort this operation!
                if (!stopGame()) {
                    return;
                }
            }

            List<String> BOARD_NAMES = ResourcesUtil.getBoardFileNames();

            ChoiceDialog<String> dialogL = new ChoiceDialog<>(BOARD_NAMES.get(0), BOARD_NAMES);
            dialogL.setTitle("Select board");
            dialogL.setHeaderText("Select a board to load");
            Optional<String> resultS = dialogL.showAndWait();

            Board board;

            if (resultS.isPresent()) {
                board = LoadSaveBoard.newGame(resultS.get());
                for (int i = 0; i < board.getPlayersNumber(); i++) {
                    TextInputDialog name = new TextInputDialog(board.getPlayer(i).getName());
                    name.setTitle("Player name");
                    name.setHeaderText("Write the name of the player");
                    name.setContentText("Name: ");
                    Optional<String> resultName = name.showAndWait();

                    if (resultName.isPresent()) {
                        board.getPlayer(i).setName(resultName.get());
                    }
                }
            } else {
                board = LoadSaveBoard.newGame(null);
            }

            setupGameController(board);
        }
    }

    public void saveGame() {
        TextInputDialog dialogS = new TextInputDialog();
        dialogS.setTitle("SAVE GAME");
        dialogS.setHeaderText("Enter a name for your game save");

        final Optional<String> resultS = dialogS.showAndWait();

        if (resultS.isPresent()) {
            String saveName = resultS.get();
            LoadSaveBoard.saveGame(gameController.board, saveName);
        }
    }

    public void loadGame() {
        if (gameController == null) {

            final List<String> BOARD_NAMES = ResourcesUtil.getSaveGameFiles();

            ChoiceDialog<String> dialogL = new ChoiceDialog<>(BOARD_NAMES.get(0), BOARD_NAMES);
            dialogL.setTitle("Load game");
            dialogL.setHeaderText("Select a savegame to load");
            Optional<String> result = dialogL.showAndWait();

            if (result.isPresent()) {
                String boardname = result.get();
                Board board = LoadSaveBoard.loadGame(boardname, true);
                setupGameController(board);
            }  else {
                // The UI should not allow this, but in case this happens anyway.
                // give the user the option to save the game or abort this operation!
                return;
            }
        }
    }

    private void setupGameController(Board board) {
        gameController = new GameController(board);
        board.setCurrentPlayer(board.getPlayer(0));
        gameController.startProgrammingPhase();
        roboRally.createBoardView(gameController);
    }

    /**
     * Stop playing the current game, giving the user the option to save
     * the game or to cancel stopping the game. The method returns true
     * if the game was successfully stopped (with or without saving the
     * game); returns false, if the current game was not stopped. In case
     * there is no current game, false is returned.
     *
     * @return true if the current game was stopped, false otherwise
     */
    public boolean stopGame() {
        if (gameController != null) {

            // here we save the game (without asking the user).
            saveGame();

            gameController = null;
            roboRally.createBoardView(null);
            return true;
        }
        return false;
    }

    public void exit() {
        if (gameController != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Exit RoboRally?");
            alert.setContentText("Are you sure you want to exit RoboRally?");
            Optional<ButtonType> result = alert.showAndWait();

            if (!result.isPresent() || result.get() != ButtonType.OK) {
                return; // return without exiting the application
            }
        }

        // If the user did not cancel, the RoboRally application will exit
        // after the option to save the game
        if (gameController == null || stopGame()) {
            Platform.exit();
        }
    }

    public boolean isGameRunning() {
        return gameController != null;
    }

    @Override
    public void update(Subject subject) {
        // XXX do nothing for now
    }

}