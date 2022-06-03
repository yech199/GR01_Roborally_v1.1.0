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
package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.*;
import dk.dtu.compute.se.pisd.roborally.model.*;

import java.util.ArrayList;

/**
 * This class is responsible for reading(load) and writing(save) game state to and from json files.
 * @author Mads Sørensen (s215805)
 */
public class LoadSaveBoard {

    /**
     * Deserialize a json string with the state of the game and returns a board.
     * @param jsonGameState a json string of game state
     * @param gameName name of the save game
     * @return board object with the game state
     */
    private static Board deserialize(String jsonGameState, String gameName) {
        // In simple cases, we can create a Gson object with new Gson():
        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>());
        Gson gson = simpleBuilder.create();

        BoardTemplate template = gson.fromJson(jsonGameState, BoardTemplate.class);

        Board board = new Board(template.width, template.height, gameName);

        // TODO: New games dont have state for registers, phase etc.

        // TODO: Add loading of phases and other game state elements.
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
            board.addPlayer(newPlayer);
            newPlayer.setSpace(board.getSpace(player.spaceX, player.spaceY));
            newPlayer.setHeading(Heading.valueOf(player.heading));

            CommandCardField[] newCards = new CommandCardField[player.playerCards.size()];
            CommandCardField[] newProgram = new CommandCardField[player.playerCards.size()];

            for (int i = 0; i < newCards.length; i++) {
                // TODO: Load player cards
                // Sets CommandCardField
                CommandCardFieldTemplate commandCardFieldTemplate = template.players.get(newPlayer.hashCode()).playerCards.get(i);
                CommandCardField commandCardField = template.players.get(newPlayer.hashCode()).playerCards.get(i).commandCardField;

                // Sets CommandCard to commandCardField
                CommandCard commandCard = new CommandCard(commandCardFieldTemplate.command);
                commandCardField.setCard(commandCard);
            }

            for (int i = 0; i < newProgram.length; i++) {
                // TODO: Load player registers

            }

            newPlayer.setCards(newCards);
            newPlayer.setProgram(newProgram);
        }

        // Set current player
        board.setCurrentPlayer(board.getPlayer(template.currentPlayer));

        return board;
    }

    /**
     * Serialize a board with the game state to a json string
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

        // TODO: set up players in template
        for (Player player : board.getPlayers()) {
            PlayerTemplate playerTemplate = new PlayerTemplate();
            Space space = player.getSpace();
            playerTemplate.spaceX = space.x;
            playerTemplate.spaceY = space.y;
            playerTemplate.heading = String.valueOf(player.getHeading());
            playerTemplate.color = player.getColor();
            playerTemplate.name = player.getName();

            ArrayList<CommandCardFieldTemplate> cards = new ArrayList<>();

            // TODO Setup cards by loading array of cards into playertemplate
            for (CommandCardField commandCardField : player.getCards()) {

                CommandCardFieldTemplate cardFieldTemplate = new CommandCardFieldTemplate();
                CommandCardTemplate cardTemplate = new CommandCardTemplate();
                CommandTemplate commandTemplate = new CommandTemplate();

                commandTemplate.command = commandCardField.getCard().command;
                cardTemplate.commandCard = commandTemplate;
                cardFieldTemplate.commandCard = cardTemplate;
                cardFieldTemplate.visible = commandCardField.isVisible();

                // Add to card template
                cards.add(cardFieldTemplate);

            }

            ArrayList<CommandCardFieldTemplate> program = new ArrayList<>();
            // TODO Setup program by loading array of cards into playertemplate

            playerTemplate.playerCards = cards;

            template.players.add(playerTemplate);

        }
        template.currentPlayer = board.getPlayerNumber(board.getCurrentPlayer());

        // Saving the board template using GSON
        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>()).
                setPrettyPrinting();
        Gson gson = simpleBuilder.create();

        return gson.toJson(template, template.getClass());
    }

    /**
     * Load the given board from a json file.
     * @param gameName name of the game board
     * @return the new board
     */
    public static Board loadGame(String gameName, boolean saveGame) {
        String gameState = IOUtil.readGame(gameName, saveGame);

        if (gameState != null) {
            return deserialize(gameState, gameName);
        }
        return new Board(8, 8);
    }

    /**
     * Create a new game by loading a gameboard from the predefined board configurations in resource folder
     * @param gameName name of the game board
     * @return the new board
     */
    public static Board newGame(String gameName) {
        return loadGame(gameName, false);
    }

    /**
     * Save a game in json format to the resource/savegames folder
     * @param board the board to be saved
     * @param gameName name of the game board
     */
    public static void saveGame(Board board, String gameName) {
        String json = serialize(board);
        IOUtil.writeGame(gameName, json);
    }
}
