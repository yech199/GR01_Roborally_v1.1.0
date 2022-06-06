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

import designpatterns.observer.Observer;
import designpatterns.observer.Subject;
import fileaccess.IOUtil;
import fileaccess.LoadBoard;
import fileaccess.SaveBoard;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import model.Board;
import model.Phase;
import org.jetbrains.annotations.NotNull;
import roborally.RoboRally;
import roborally.client.GameClient;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class AppController implements Observer {
    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);

    final private RoboRally roboRally;
    private GameController gameController;
    private final GameClient client = new GameClient();

    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }

    public void newGame() {
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        dialog.setTitle("Player number");
        dialog.setHeaderText("Select number of players");
        Optional<Integer> result = dialog.showAndWait();

        int numberOfPlayers;

        if (result.isPresent()) {
            if (gameController != null) {
                // The UI should not allow this, but in case this happens anyway.
                // TODO give the user the option to save the game or abort this operation!
                if (!stopGame()) {
                    return;
                }

            }
            numberOfPlayers = result.get();

            final List<String> BOARD_NAMES;
            try {
                BOARD_NAMES = IOUtil.getBoardFileNames();
            } catch (NullPointerException e) {
                // If this happens, then the player then can't start a game to make a savegame.
                System.out.println("Could not find Resource folder or there is not any board templates available");
                return;
            }

            ChoiceDialog<String> dialogL = new ChoiceDialog<>(BOARD_NAMES.get(0), BOARD_NAMES);
            dialogL.setTitle("Select board");
            dialogL.setHeaderText("Select a board to load");
            Optional<String> resultS = dialogL.showAndWait();

            Board board;

            if (resultS.isPresent()) {
                board = LoadBoard.newBoard(resultS.get(), numberOfPlayers);
                // Sets number of players here!
                choosePlayerNames(numberOfPlayers, board);
            }
            else {
                board = LoadBoard.newBoard(null, numberOfPlayers);
            }

            setupGameController(board);
        }
    }

    private void choosePlayerNames(int numberOfPlayers, Board board) {
        for (int i = 0; i < numberOfPlayers; i++) {
            TextInputDialog name = new TextInputDialog(board.getPlayer(i).getName());
            name.setTitle("Player name");
            name.setHeaderText("Write the name of the player");
            name.setContentText("Name: ");
            Optional<String> resultName = name.showAndWait();

            if (resultName.isPresent()) {
                board.getPlayer(i).setName(resultName.get());
            }
        }
    }

    public void createServerGame() {
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        dialog.setTitle("Player number");
        dialog.setHeaderText("Select number of players");
        Optional<Integer> selectedNumOfPlayers = dialog.showAndWait();
        int numOfPlayers;
        if (selectedNumOfPlayers.isPresent()) {
            numOfPlayers = selectedNumOfPlayers.get();

            List<String> games = client.getListOfBoards();

            ChoiceDialog<String> dialogL = new ChoiceDialog<>(games.get(0), games);
            dialogL.setTitle("Select board");
            dialogL.setHeaderText("Select a board to load");
            Optional<String> selectedBoard = dialogL.showAndWait();

            Board board;
            if (selectedBoard.isPresent()) {
                board = client.createGame(selectedBoard.get(), numOfPlayers);
                // Sets number of players here!
                choosePlayerNames(numOfPlayers, board);
                client.setGameState(board);
            }
            else {
                board = LoadBoard.newBoard(null, numOfPlayers);
            }

            Alert alert = new Alert(AlertType.CONFIRMATION, "Game created succesfully. Your game ID is: " + board.getGameId(), ButtonType.OK);
            alert.showAndWait();

            setupGameController(board);
        }
    }

    public void saveServerGame() {
        String msg;
        try {
            int id = gameController.board.getGameId();
            String json = SaveBoard.serializeBoard(gameController.board);
            client.setGameState(id, json);
            msg = "Succesfully saved game to server";
        } catch (Exception e) {
            e.printStackTrace();
            msg = "Failed to save game to server";
        }
        Alert alert = new Alert(AlertType.CONFIRMATION, msg, ButtonType.OK);
        alert.showAndWait();
    }

    public void joinGame() {
        // List all available games
        List<String> games = client.getListOfGames();

        ChoiceDialog<String> dialogL = new ChoiceDialog<>(games.get(0), games);
        dialogL.setTitle("Join Game");
        dialogL.setHeaderText("Select a game to join");
        Optional<String> selectedGame = dialogL.showAndWait();

        Board board;
        if (selectedGame.isPresent()) {
            // Join the selected game
            int gameId = Integer.parseInt(selectedGame.get());
            board = client.getGameState(gameId);
        } else {
            board = LoadBoard.newBoard(null, 6);
        }
        setupGameController(board);
    }

    public void saveGame() {
        TextInputDialog dialogS = new TextInputDialog();
        dialogS.setTitle("SAVE GAME");
        dialogS.setHeaderText("Enter a name for your game save");

        final Optional<String> resultS = dialogS.showAndWait();

        if (resultS.isPresent()) {
            String saveName = resultS.get();
            SaveBoard.saveGame(gameController.board, saveName);
        }
    }

    public void loadGame() {
        if (gameController == null) {

            final List<String> BOARD_NAMES;
            try {
                BOARD_NAMES = IOUtil.getSaveGameFiles();
            } catch (NullPointerException e) {
                System.out.println("Could not find Resource folder or there is not any save games available");
                return;
            }

            ChoiceDialog<String> dialogL = new ChoiceDialog<>(BOARD_NAMES.get(0), BOARD_NAMES);
            dialogL.setTitle("Load game");
            dialogL.setHeaderText("Select a savegame to load");
            Optional<String> result = dialogL.showAndWait();

            if (result.isPresent()) {
                String boardName = result.get();
                Board board = LoadBoard.loadGame(boardName, true);
                setupGameController(board);
            }
            else {
                // TODO: The UI should not allow this, but in case this happens anyway.
                //  give the user the option to save the game or abort this operation!
            }
        }
    }

    private void setupGameController(Board board) {
        board.attach(this);
        gameController = new GameController(board);

        // If game is new (eg. not loaded), then we set up the programming phase. Else we skip it.
        if (board.getPhase() == Phase.INITIALISATION) {
            gameController.startProgrammingPhase();
        }
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
            // If there is a winner, don't save game. Just exit program
            if (gameController.board.getWinner() != null) {
                System.out.println(gameController.board.getWinner().getName() + " har vundet");

                JOptionPane.showMessageDialog(null, gameController.board.getWinner().getName()
                        + " har vundet", "InfoBox: " + gameController.board.getWinner().getName()
                        + " har vundet", JOptionPane.INFORMATION_MESSAGE);

                gameController = null;
                roboRally.createBoardView(null);
            }

            else {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Exit RoboRally?");
                alert.setContentText("Are you sure you want to exit RoboRally?");
                Optional<ButtonType> result = alert.showAndWait();

                if (result.isEmpty() || result.get() != ButtonType.OK) {
                    return; // return without exiting the application
                }
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
        if (gameController != null) {
            if (gameController.board.getWinner() != null) exit();
        }
    }

}
