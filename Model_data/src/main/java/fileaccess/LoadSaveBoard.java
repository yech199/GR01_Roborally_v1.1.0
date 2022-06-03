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
package fileaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fileaccess.*;
import fileaccess.model.BoardTemplate;
import fileaccess.model.CommandCardFieldTemplate;
import fileaccess.model.PlayerTemplate;
import fileaccess.model.SpaceTemplate;
import model.boardElements.FieldAction;
import model.*;

import java.util.ArrayList;

/**
 * This class is responsible for reading(load) and writing(save) game state to and from json files.
 */
public class LoadSaveBoard {

    private static boolean loadedBoard = false;

    /**
     * Deserialize a json string with the state of the game and returns a board.
     *
     * @return board object with the game state
     */

    private static void loadCards(BoardTemplate template, PlayerTemplate player, CommandCardField[] newCards, Player newPlayer) {
        // Load all cards from JSON file
        for (int i = 0; i < newCards.length; i++) {
            int j = template.players.indexOf(player);

            // Get the CommandCardField JSON data
            CommandCardFieldTemplate commandCardFieldTemplate = template.players.get(j).cards.get(i);

            // Set the game CommandCardField to the template
            CommandCardField commandCardField = new CommandCardField(newPlayer);

            String command = commandCardFieldTemplate.command;

            // If command is empty in json, we just make a null value TODO make random card?
            if (!command.equals("")) {
                Command commandType = Command.valueOf(command);
                // Get the command from the template and make a new commandCard that we use in the game
                CommandCard commandCard = new CommandCard(commandType);
                // Set the CommandCardField card
                commandCardField.setCard(commandCard);
            }
            commandCardField.setVisible(commandCardFieldTemplate.visible);
            newCards[i] = commandCardField;
        }
    }
    private static void loadRegisters(BoardTemplate template, PlayerTemplate player, CommandCardField[] newRegisters, Player newPlayer) {
        // Load all registers from JSON file
        for (int i = 0; i < newRegisters.length; i++) {
            int j = template.players.indexOf(player);

            // Get the CommandCardField JSON data
            CommandCardFieldTemplate commandCardFieldTemplate = template.players.get(j).registers.get(i);

            // Set the game CommandCardField to the template
            CommandCardField commandCardField = new CommandCardField(newPlayer);

            String command = commandCardFieldTemplate.command;

            if (!command.equals("")) {
                // Get the command from the template and make a new commandCard that we use in the game
                CommandCard commandCard = new CommandCard(Command.valueOf(command));
                // Set the CommandCardField card
                commandCardField.setCard(commandCard);
            }
            newRegisters[i] = commandCardField;
        }
    }

    private static Board deserialize(String jsonGameState, String gameName, boolean saveGame) {
        // In simple cases, we can create a Gson object with new Gson():
        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>());
        Gson gson = simpleBuilder.create();

        BoardTemplate template = gson.fromJson(jsonGameState, BoardTemplate.class);

        Board board = new Board(template.width, template.height, gameName);

        // Loading spaces
        for (SpaceTemplate spaceTemplate : template.spaces) {
            Space space = board.getSpace(spaceTemplate.x, spaceTemplate.y);
            if (space != null) {
                space.getActions().addAll(spaceTemplate.actions);
                space.getWalls().addAll(spaceTemplate.walls);
            }
        }

        // Loading players
        for (PlayerTemplate player : template.players) {

            Player newPlayer = new Player(board, player.color, player.name);
            newPlayer.setSpace(board.getSpace(player.spaceX, player.spaceY));
            newPlayer.setHeading(Heading.valueOf(player.heading));

            CommandCardField[] newCards = new CommandCardField[player.cards.size()];
            CommandCardField[] newRegisters = new CommandCardField[player.registers.size()];

            // If we have a new game, we don't need to do card and register setup
            if (!saveGame) {
                board.addPlayer(newPlayer);
                continue;
            }

            loadCards(template, player, newCards, newPlayer);
            loadRegisters(template, player, newRegisters, newPlayer);

            newPlayer.setCards(newCards);
            newPlayer.setProgram(newRegisters);
            loadedBoard = true;

            board.addPlayer(newPlayer);
        }

        if (!saveGame) return board;
        board.setCurrentPlayer(board.getPlayer(template.currentPlayer));
        board.setPhase(Phase.valueOf(template.phase));
        board.setStep(template.step);

        return board;
    }

    /**
     * Serialize a board with the game state to a json string
     *
     * @param board the board to be serialized
     * @return json string of game state
     */
    private static String serialize(Board board) {
        // Set up the board template
        BoardTemplate template = new BoardTemplate();
        template.width = board.width;
        template.height = board.height;

        // Add all spaces
        for (int i = 0; i < board.width; i++) {
            for (int j = 0; j < board.height; j++) {
                Space space = board.getSpace(i, j);
                if (!space.getWalls().isEmpty() || !space.getActions().isEmpty()) {
                    SpaceTemplate spaceTemplate = new SpaceTemplate();
                    spaceTemplate.x = space.x;
                    spaceTemplate.y = space.y;
                    spaceTemplate.actions.addAll(space.getActions());
                    spaceTemplate.walls.addAll(space.getWalls());
                    template.spaces.add(spaceTemplate);
                }
            }
        }

        for (Player player : board.getPlayers()) {
            PlayerTemplate playerTemplate = new PlayerTemplate();
            Space space = player.getSpace();
            playerTemplate.spaceX = space.x;
            playerTemplate.spaceY = space.y;
            playerTemplate.heading = String.valueOf(player.getHeading());
            playerTemplate.color = player.getColor();
            playerTemplate.name = player.getName();

            // Save cards
            ArrayList<CommandCardFieldTemplate> cards = new ArrayList<>();
            for (CommandCardField commandCardField : player.getCards()) {
                CommandCardFieldTemplate cardFieldTemplate = new CommandCardFieldTemplate();

                if (commandCardField.getCard() == null) {
                    cardFieldTemplate.command = "";
                    cardFieldTemplate.visible = true;
                } else {
                    cardFieldTemplate.command = String.valueOf(commandCardField.getCard().command);
                    cardFieldTemplate.visible = commandCardField.isVisible();
                }

                // Add to card template
                cards.add(cardFieldTemplate);
            }

            // Save registers
            ArrayList<CommandCardFieldTemplate> registers = new ArrayList<>();
            for (CommandCardField commandCardField : player.getProgram()) {
                CommandCardFieldTemplate cardFieldTemplate = new CommandCardFieldTemplate();

                if (commandCardField.getCard() == null) {
                    cardFieldTemplate.command = "";
                    cardFieldTemplate.visible = true;
                } else {
                    cardFieldTemplate.command = String.valueOf(commandCardField.getCard().command);
                    cardFieldTemplate.visible = commandCardField.isVisible();
                }

                // Add to card template
                registers.add(cardFieldTemplate);
            }

            playerTemplate.registers = registers;
            playerTemplate.cards = cards;

            template.players.add(playerTemplate);

        }
        template.currentPlayer = board.getPlayerNumber(board.getCurrentPlayer());
        template.step = board.getStep();
        template.phase = String.valueOf(board.getPhase());

        // Saving the board template using GSON
        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>()).
                setPrettyPrinting();
        Gson gson = simpleBuilder.create();

        return gson.toJson(template, template.getClass());
    }

    /**
     * Load the given board from a json file.
     *
     * @param gameName name of the game board
     * @return the new board
     */
    public static Board loadGame(String gameName, boolean saveGame) {
        String gameState = IOUtil.readGame(gameName, saveGame);
        if (gameState != null) {
            return deserialize(gameState, gameName, saveGame);
        }
        return new Board(8, 8);
    }

    /**
     * Create a new game by loading a gameboard from the predefined board configurations in resource folder
     *
     * @param gameName name of the game board
     * @return the new board
     */
    public static Board newGame(String gameName) {
        return loadGame(gameName, false);
    }

    /**
     * Save a game in json format to the resource/savegames folder
     *
     * @param board    the board to be saved
     * @param gameName name of the game board
     */
    public static void saveGame(Board board, String gameName) {
        String json = serialize(board);
        IOUtil.writeGame(gameName, json);
    }

    public static boolean getLoadedBoard() {
        return loadedBoard;
    }
}
