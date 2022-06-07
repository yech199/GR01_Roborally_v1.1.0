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
import fileaccess.model.BoardTemplate;
import fileaccess.model.CommandCardFieldTemplate;
import fileaccess.model.PlayerTemplate;
import fileaccess.model.SpaceTemplate;
import model.boardElements.SpaceElement;
import model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for reading(load) and writing(save) game state to and from json files.
 */
public class SaveBoard {

    private static ArrayList<SpaceTemplate> saveSpaces(Board board) {
        ArrayList<SpaceTemplate> spaceTemplates = new ArrayList<>();
        // Add all spaces
        // TODO: Make sure all the needed attributes are saved. e.g. the heading and color of ConveyorBelt
        for (int i = 0; i < board.width; i++) {
            for (int j = 0; j < board.height; j++) {
                Space space = board.getSpace(i, j);
                if (!space.getWalls().isEmpty() || !space.getActions().isEmpty()) {
                    SpaceTemplate spaceTemplate = new SpaceTemplate();
                    spaceTemplate.x = space.x;
                    spaceTemplate.y = space.y;
                    spaceTemplate.actions.addAll(space.getActions());
                    spaceTemplate.walls.addAll(space.getWalls());
                    spaceTemplates.add(spaceTemplate);
                }
            }
        }
        return spaceTemplates;
    }

    private static ArrayList<PlayerTemplate> savePlayers(List<Player> players) {
        int playerCounter = 0;
        ArrayList<PlayerTemplate> playerTemplates = new ArrayList<>();
        // Save state of player
        for (Player player : players) {
            if (playerCounter > players.size()) break;
            PlayerTemplate playerTemplate = new PlayerTemplate();
            Space space = player.getSpace();
            playerTemplate.spaceX = space.x;
            playerTemplate.spaceY = space.y;
            playerTemplate.heading = String.valueOf(player.getHeading());
            playerTemplate.color = player.getColor();
            playerTemplate.name = player.getName();

            playerTemplate.registers = saveCommandCardFields(player.getProgram());
            playerTemplate.cards = saveCommandCardFields(player.getCards());
            playerTemplate.active = player.activePlayer;

            playerTemplates.add(playerTemplate);
            playerCounter++;
        }
        return playerTemplates;
    }

    private static ArrayList<CommandCardFieldTemplate> saveCommandCardFields(CommandCardField[] commandCardFields) {
        ArrayList<CommandCardFieldTemplate> newCards = new ArrayList<>();
        for (CommandCardField commandCardField : commandCardFields) {
            CommandCardFieldTemplate cardFieldTemplate = new CommandCardFieldTemplate();

            if (commandCardField.getCard() == null) {
                cardFieldTemplate.command = "";
                cardFieldTemplate.visible = true;
            } else {
                cardFieldTemplate.command = String.valueOf(commandCardField.getCard().command);
                cardFieldTemplate.visible = commandCardField.isVisible();
            }

            // Add to card template
            newCards.add(cardFieldTemplate);
        }
        return newCards;
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

        template.spaces = saveSpaces(board);
        template.players = savePlayers(board.getPlayers());

        if (board.getCurrentPlayer() == null) template.currentPlayer = 0;
        else template.currentPlayer = board.getPlayerNumber(board.getCurrentPlayer());

        template.step = board.getStep();
        template.phase = String.valueOf(board.getPhase());
        template.boardName = board.getBoardName();
        template.checkPointAmount = board.totalNoOfCheckpoints;
        template.maxNumberOfPlayers = board.maxAmountOfPlayers;
        template.activePlayers = board.amountOfActivePlayers;

        if (board.getGameId() != null) template.gameId = board.getGameId();

        // Saving the board template using GSON
        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(SpaceElement.class, new Adapter<SpaceElement>()).
                setPrettyPrinting();
        Gson gson = simpleBuilder.create();

        return gson.toJson(template, template.getClass());
    }

    /**
     * Save a game in json format to the resource/savedgames folder
     *
     * @param board    the board to be saved
     * @param gameName name of the game board
     */
     public static void saveGame(Board board, String gameName) {
        String json = serialize(board);
        IOUtil.writeGame(gameName, json);
    }

    public static String serializeBoard(Board board) {
        return serialize(board);
    }

    public static String serializePlayer(Player player) {
        PlayerTemplate playerTemplate = new PlayerTemplate();
        Space space = player.getSpace();
        playerTemplate.spaceX = space.x;
        playerTemplate.spaceY = space.y;
        playerTemplate.heading = String.valueOf(player.getHeading());
        playerTemplate.color = player.getColor();
        playerTemplate.name = player.getName();
        playerTemplate.active = player.activePlayer;

        playerTemplate.registers = saveCommandCardFields(player.getProgram());
        playerTemplate.cards = saveCommandCardFields(player.getCards());

        // Saving the board template using GSON
        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(SpaceElement.class, new Adapter<SpaceElement>()).
                setPrettyPrinting();
        Gson gson = simpleBuilder.create();

        return gson.toJson(playerTemplate, playerTemplate.getClass());
    }
}