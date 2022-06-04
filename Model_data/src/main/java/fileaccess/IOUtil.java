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

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import model.boardElements.SpaceElement;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * A utility class reading strings from resources and arbitrary input streams.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class IOUtil {

    private static final String BOARDSFOLDER = "boards";
    private static final String SAVEFOLDER = "savegames";

    private static final String DEFAULTBOARD = "defaultboard";
    private static final String JSON_EXT = "json";

    /**
     * Reads a string from some InputStream. The solution is based
     * on google's Guava and a solution from Baeldung:
     * https://www.baeldung.com/convert-input-stream-to-string#guava
     *
     * @param inputStream the input stream
     * @return the string of the input stream
     */
    public static String readString(InputStream inputStream) {

        ByteSource byteSource = new ByteSource() {
            @Override
            public InputStream openStream() throws IOException {
                return inputStream;
            }
        };

        try {
            return byteSource.asCharSource(Charsets.UTF_8).read();
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * Returns a string from a resource of the project. This method is implemented
     * in such a way that resource can be read when the project is deployed in
     * a jar file.
     *
     * @param relativeResourcePath the relative path to the resource
     * @return the string contents of the resource
     */
    public static String readResource(String relativeResourcePath) {
        ClassLoader classLoader = IOUtil.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(relativeResourcePath);
        return IOUtil.readString(inputStream);
    }

    public static String getResourceFile(String folderName, String fileName, String ext) throws NullPointerException {
        ClassLoader classLoader = SaveBoard.class.getClassLoader();
        return classLoader.getResource(folderName).getPath() + "/" + fileName + "." + ext;
    }

    /**
     * Write the game state from a json string to a json file.
     * @param gameName Name of the file
     * @param jsonGameState json string of the game state
     */
    public static void writeGame(String gameName, String jsonGameState) {
        String filename;
        try {
             filename = getResourceFile(SAVEFOLDER, gameName, JSON_EXT);
        } catch (NullPointerException e) {
            System.out.println("Could not find save games folder");
            // TODO Expore option to create a save game folder for the user?
            return;
        }

        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(SpaceElement.class, new Adapter<SpaceElement>()).
                setPrettyPrinting();
        Gson gson = simpleBuilder.create();

        FileWriter fileWriter = null;
        JsonWriter writer = null;

        try {
            fileWriter = new FileWriter(filename);
            writer = gson.newJsonWriter(fileWriter);

            writer.jsonValue(jsonGameState);
            writer.flush();
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
                } catch (IOException e2) {}
            }
        }

    }

    /**
     * Read game state from a json file.
     * The method can handle if the we read from a save game or board file.
     * @param gameName name of game
     * @return json string of the game state
     */
    public static String readGame(String gameName, boolean savedGame) {
        if (gameName == null) {
            gameName = DEFAULTBOARD;
        }

        // If we load a new game, we load from predefined boards in boards folder.
        String resourcePath;
        if (savedGame)  {
             resourcePath = SAVEFOLDER + "/" + gameName + "." + JSON_EXT;
        } else { // else we load from an save in savesfolder
            resourcePath = BOARDSFOLDER + "/" + gameName + "." + JSON_EXT;
        }

        ClassLoader classLoader = SaveBoard.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(resourcePath);

        try {
            String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return json;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
