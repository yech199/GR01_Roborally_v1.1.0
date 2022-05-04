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
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.BoardTemplate;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.PlayerTemplate;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.SpaceTemplate;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class LoadSaveNewBoard {

    final private static List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");

    private static final String BOARDSFOLDER = "boards";

    private static final String SAVEFOLDER = "savegames";
    private static final String DEFAULTBOARD = "defaultboard";
    private static final String JSON_EXT = "json";


    public static Board loadBoard(String boardname) {
        if (boardname == null) {
            boardname = DEFAULTBOARD;
        }

        ClassLoader classLoader = LoadSaveNewBoard.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(SAVEFOLDER + "/" + boardname + "." + JSON_EXT);

        if (inputStream == null) {
            // TODO these constants should be defined somewhere
            return new Board(8, 8);
        }

        // In simple cases, we can create a Gson object with new Gson():
        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>());
        Gson gson = simpleBuilder.create();

        Board result;
        // FileReader fileReader = null;
        JsonReader reader = null;
        try {

            reader = gson.newJsonReader(new InputStreamReader(inputStream));
            BoardTemplate template = gson.fromJson(reader, BoardTemplate.class);

            result = new Board(template.width, template.height, boardname);

            // TODO: Add loading of phase, stepmode and step
            // Loading spaces
            for (SpaceTemplate spaceTemplate : template.spaces) {
                Space space = result.getSpace(spaceTemplate.x, spaceTemplate.y);
                if (space != null) {
                    space.getActions().addAll(spaceTemplate.actions);
                    space.getWalls().addAll(spaceTemplate.walls);
                }
            }

            // Loading players
            for (PlayerTemplate player : template.players) {
                Player newPlayer = new Player(result, player.color, player.name);
                result.addPlayer(newPlayer);
                newPlayer.setSpace(result.getSpace(player.spaceX, player.spaceY));
                newPlayer.setHeading(Heading.valueOf(player.heading));
            }

            // Set current player
            result.setCurrentPlayer(result.getPlayer(template.currentPlayer));

            reader.close();
            return result;
        } catch (IOException e1) {
            if (reader != null) {
                try {
                    reader.close();
                    inputStream = null;
                } catch (IOException e2) {
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e2) {
                }
            }
        }
        return null;
    }

    public static void saveBoard(Board board, String name) {
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
            template.players.add(playerTemplate);
        }
        template.currentPlayer = board.getPlayerNumber(board.getCurrentPlayer());

        ClassLoader classLoader = LoadSaveNewBoard.class.getClassLoader();
        // TODO: this is not very defensive, and will result in a NullPointerException
        //       when the folder "resources" does not exist! But, it does not need
        //       the file "simpleCards.json" to exist!
        String filename =
                classLoader.getResource(SAVEFOLDER).getPath() + "/" + name + "." + JSON_EXT;

        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>()).
                setPrettyPrinting();
        Gson gson = simpleBuilder.create();

        FileWriter fileWriter = null;
        JsonWriter writer = null;
        try {
            fileWriter = new FileWriter(filename);
            writer = gson.newJsonWriter(fileWriter);
            gson.toJson(template, template.getClass(), writer);
            writer.close();
        } catch (IOException e1) {
            if (writer != null) {
                try {
                    writer.close();
                    fileWriter = null;
                } catch (IOException e2) {
                }
            }
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e2) {
                }
            }
        }
    }

    public static Board newBoard(int numOfPlayers, String boardname) {
        Board newBoard;

        if (boardname == null) {
            boardname = DEFAULTBOARD;
        }

        ClassLoader classLoader = LoadSaveNewBoard.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(BOARDSFOLDER + "/" + boardname + "." + JSON_EXT);

        if (inputStream == null) {
            return new Board(8, 8);
        }

        GsonBuilder simpleBuilder = new GsonBuilder().registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>());
        Gson gson = simpleBuilder.create();

        try {

            JsonReader reader = gson.newJsonReader(new InputStreamReader(inputStream));
            BoardTemplate template = gson.fromJson(reader, BoardTemplate.class);

            newBoard = new Board(template.width, template.height);

            for (SpaceTemplate spaceTemplate : template.spaces) {
                Space space = newBoard.getSpace(spaceTemplate.x, spaceTemplate.y);
                if (space != null) {
                    space.getActions().addAll(spaceTemplate.actions);
                    space.getWalls().addAll(spaceTemplate.walls);
                    space.setPlayer(null);
                }
            }

            for (int i = 0; i < numOfPlayers; i++) {
                Player player = new Player(newBoard, PLAYER_COLORS.get(i), "Player " + (i + 1));
                newBoard.addPlayer(player);
                player.setSpace(newBoard.getSpace(i % newBoard.width, i));
            }

            reader.close();

        } catch (Exception e) {
            System.out.println("Failed loading board");
            newBoard = new Board(8, 8);
        }
        return newBoard;
    }

}
