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

import controller.GameController;
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
import model.Globals;
import model.Phase;
import org.jetbrains.annotations.NotNull;
import roborally.RoboRally;
import roborally.client.GameClient;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class AppController implements Observer {
    private final List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);
    private final RoboRally roboRally;
    private final GameClient client = new GameClient();

    private GameController gameController;
    private AppState appState = AppState.UNDECIDED;

    public enum AppState {
        LOCAL_GAME,
        SERVER_GAME,
        UNDECIDED
    }

    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }

    /**
     * @return whether it was possible to create a new game or not
     */
    public boolean newGame() {
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
                    return false;
                }

            }
            numberOfPlayers = result.get();

            final List<String> BOARD_NAMES;
            try {
                BOARD_NAMES = IOUtil.getBoardFileNames();
            } catch (NullPointerException e) {
                // If this happens, then the player then can't start a game to make a savegame.
                System.out.println("Could not find Resource folder or there is not any board templates available");
                return false;
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
            } else {
                board = LoadBoard.newBoard(null, numberOfPlayers);
            }

            setupGameController(board);
        }
        appState = AppState.LOCAL_GAME;
        return true;
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

    public boolean createServerGame() {
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        dialog.setTitle("Player number");
        dialog.setHeaderText("Select number of players");
        Optional<Integer> selectedNumOfPlayers = dialog.showAndWait();
        int numOfPlayers;

        if (selectedNumOfPlayers.isPresent()) {
            numOfPlayers = selectedNumOfPlayers.get();

            List<String> games = client.getListOfBoards();

            TextInputDialog name = new TextInputDialog();
            name.setTitle("Player name");
            name.setHeaderText("Write the name of your player");
            name.setContentText("Name: ");
            Optional<String> resultName = name.showAndWait();

            client.playerName = "Player";
            if (!resultName.isEmpty()) {
                client.playerName = resultName.get();
            } else {
                Alert alert = new Alert(AlertType.INFORMATION, "Returning to main menu...", ButtonType.OK);
                alert.showAndWait();
                return false;
            }

            ChoiceDialog<String> dialogL = new ChoiceDialog<>(games.get(0), games);
            dialogL.setTitle("Select board");
            dialogL.setHeaderText("Select a board to load");
            Optional<String> selectedBoard = dialogL.showAndWait();

            Board board;
            String result;
            AlertType alertType;
            if (selectedBoard.isPresent()) {
                String gameId = client.createGame(selectedBoard.get(), numOfPlayers);
                result = client.joinGame(Integer.parseInt(gameId), client.playerName);
                board = client.getGameState(Integer.parseInt(gameId), client.playerName);
                alertType = AlertType.CONFIRMATION;
            } else { // Should not happen
                return false;
                //board = LoadBoard.newBoard(null, numOfPlayers); // boardName = Null, means loading of default board
                //result = "OK";
                //alertType = AlertType.WARNING;
            }

            // Give Alert to player showing
            Alert alert;
            alert = new Alert(alertType, "Game created succesfully. Your game ID is: " + board.getGameId(), ButtonType.OK);
            alert.showAndWait();


            appState = AppState.SERVER_GAME;
            setupGameController(board);
            return true;
        }
        return false;
    }

    public void saveServerGame() {
        appState = AppState.UNDECIDED;
        String msg;
        int id = client.gameId;
        //String json = SaveBoard.serializePlayer(gameController.board.getPlayer(client.playerName));
        //String playerJson = SaveBoard.serializePlayer(gameController.board.getPlayer(playerName));
        client.setPlayerState(id, gameController.board.getPlayer(client.playerName));
    }

    public void leaveServerGame() {
        // TODO add functionality so that a player can leave the game
        Alert alert = new Alert(AlertType.INFORMATION, "Do you want to leave the game?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> resultS = alert.showAndWait();

        if (resultS.get().equals(ButtonType.YES)) {
            // Optional<String> resultName = name.showAndWait();
            String playerName = client.playerName;

            if (playerName != null) {
                //playerName = resultName.get();
                int id = gameController.board.getGameId();
                String result = client.leaveGame(id, playerName);

                Alert confirmation;
                if (result.equals("ok")) {
                    confirmation = new Alert(AlertType.CONFIRMATION, "You have left the game", ButtonType.OK);
                    stopGame();
                    appState = AppState.UNDECIDED;

                    gameController = null;
                } else if (result.equals("Game removed")) {
                    confirmation = new Alert(AlertType.CONFIRMATION, "You have left the game and since no other players were left, the game has been deleted", ButtonType.OK);
                    confirmation.showAndWait();
                    stopGame();
                    appState = AppState.UNDECIDED;
                    gameController = null;
                } else {
                    confirmation = new Alert(AlertType.ERROR, "Something went wrong", ButtonType.CLOSE);
                    stopGame();
                    appState = AppState.UNDECIDED;
                    gameController = null;
                }

            }
        }
    }

    public boolean joinGame() {
        appState = AppState.SERVER_GAME;
        TextInputDialog name = new TextInputDialog();
        name.setTitle("Player name");
        name.setHeaderText("Write the name of your player");
        name.setContentText("Name: ");

        Optional<String> resultName = name.showAndWait();

        String playerName = "Player";
        if (resultName.isPresent()) {
            playerName = resultName.get();
        }

        // List all available games
        try {
            ArrayList<String> listOfGames = client.getListOfGames();

            ChoiceDialog<String> dialogL = new ChoiceDialog<>(listOfGames.get(0), listOfGames);
            dialogL.setTitle("Join Game");
            dialogL.setHeaderText("Select a game to join");
            Optional<String> selectedGame = dialogL.showAndWait();

            String numbers = selectedGame.get();
            ArrayList<String> extraction = new ArrayList<>();
            for (int i = 0; i < numbers.length(); i++) {
                if (numbers.charAt(i) == '|') {
                    for (int j = i; j < i + 12; j++) {
                        extraction.add(String.valueOf(numbers.charAt(j)));
                    }
                    break;
                }
            }
            String[] extractionArray = extraction.toArray(new String[0]);
            String clean = Arrays.toString(extractionArray);

            String result = clean.replaceAll("\\D+", "");
            String resultResponse = null;
            Board board;
            if (selectedGame.isPresent()) {
                // Join the selected game
                int gameId = Integer.parseInt(result);
                // TODO Error check
                resultResponse = client.joinGame(gameId, playerName);
                board = client.getGameState(gameId, client.playerName);
            } else {
                board = LoadBoard.newBoard(null, Globals.MAX_NO_PLAYERS);
            }
            if(resultResponse.equals("Game Full")) {
                Alert alert = new Alert(AlertType.INFORMATION, "GAME IS FULL.", ButtonType.OK);
                alert.showAndWait();
                appState = AppState.UNDECIDED;
                return false;
            }
            setupGameController(board);
            appState = AppState.SERVER_GAME;
        } catch (IndexOutOfBoundsException e) {
            Alert alert = new Alert(AlertType.INFORMATION, "No active games.", ButtonType.OK);
            alert.showAndWait();
            appState = AppState.UNDECIDED;
            return false;
        }
        return true;
    }

    public void submitPlayerCards() {
        int id = client.gameId;
        client.setPlayerState(id, gameController.board.getPlayer(client.playerName));
    }

    public void saveGame() {
        TextInputDialog dialogS = new TextInputDialog();
        dialogS.setTitle("SAVE GAME");
        dialogS.setHeaderText("Enter a name for your game save");

        final Optional<String> resultS = dialogS.showAndWait();

        if (resultS.isPresent()) {
            String saveName = resultS.get();
            SaveBoard.saveGame(gameController.board, saveName);
            appState = AppState.UNDECIDED;
            stopGame();
        } else {
            Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to exit RoboRally without saving?", ButtonType.YES, ButtonType.NO);
            alert.setTitle("Exit RoboRally without saving?");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.YES) {
                appState = AppState.UNDECIDED;
                stopGame();
            }
        }
    }

    public boolean loadGame() {
        if (gameController == null) {

            final List<String> BOARD_NAMES;
            try {
                BOARD_NAMES = IOUtil.getSaveGameFiles();
            } catch (NullPointerException e) {
                System.out.println("Could not find Resource folder or there is not any save games available");
                return false;
            }

            ChoiceDialog<String> dialogL = new ChoiceDialog<>(BOARD_NAMES.get(0), BOARD_NAMES);
            dialogL.setTitle("Load game");
            dialogL.setHeaderText("Select a saved game to load");
            Optional<String> result = dialogL.showAndWait();

            if (result.isPresent()) {
                String boardName = result.get();
                Board board = LoadBoard.loadGame(boardName, true);
                setupGameController(board);
            } else {
                // TODO: The UI should not allow this, but in case this happens anyway.
                //  give the user the option to save the game or abort this operation!
                return false;
            }
        }
        appState = AppState.LOCAL_GAME;
        return true;
    }

    private void setupGameController(Board board) {
        board.attach(this);
        gameController = new GameController(board);

        // If game is new (eg. not loaded), then we set up the programming phase. Else we skip it.
        if (board.getPhase() == Phase.INITIALISATION && appState != AppState.SERVER_GAME) {
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
            if (appState != AppState.UNDECIDED) {
                if (appState == AppState.LOCAL_GAME) {
                    saveGame();
                } else {
                    saveServerGame();
                }
                if (appState != AppState.UNDECIDED)
                    return false;
            }

            gameController = null;
            roboRally.createBoardView(null);
            appState = AppState.UNDECIDED;
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
            } else {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Exit RoboRally?");
                alert.setContentText("Are you sure you want to exit RoboRally?");
                Optional<ButtonType> result = alert.showAndWait();

                // Cancel
                if (result.isEmpty() || result.get() != ButtonType.OK) {
                    return; // return without exiting the application
                } else {
                    // Sure
                    try {
                        if (appState == AppState.SERVER_GAME) {
                            client.leaveGame(gameController.board.getGameId(), client.playerName);
                        }
                    } catch (NullPointerException e) {
                        System.out.println("Could Not Exit");
                    }

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

    public boolean updateServerView() {
        int gameId = client.gameId;
        Board board = client.getGameState(gameId, client.playerName);
        if (board != gameController.board) {
            setupGameController(board);
            return true;
        }
        return false;
    }

    public AppState getAppState() {
        return appState;
    }
}
