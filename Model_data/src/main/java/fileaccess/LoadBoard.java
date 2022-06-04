package fileaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fileaccess.model.BoardTemplate;
import fileaccess.model.PlayerTemplate;
import fileaccess.model.SpaceTemplate;
import model.*;
import model.boardElements.SpaceElement;

import java.util.ArrayList;
import java.util.List;

public class LoadBoard {
    private static boolean loadedBoard = false;

    private static void loadSpaces(BoardTemplate template, Board board) {
        for (SpaceTemplate spaceTemplate : template.spaces) {
            Space space = board.getSpace(spaceTemplate.x, spaceTemplate.y);
            if (space != null) {
                space.getActions().addAll(spaceTemplate.actions);
                space.getWalls().addAll(spaceTemplate.walls);
            }
        }
    }
    private static void loadPlayers(BoardTemplate template, Board board) {
        // Loading players
        for (PlayerTemplate player : template.players) {
            Player newPlayer = new Player(board, player.color, player.name);
            newPlayer.setSpace(board.getSpace(player.spaceX, player.spaceY));
            newPlayer.setHeading(Heading.valueOf(player.heading));

            // -----------------------------SAVED GAME-------------------------------------
            newPlayer.setCards(loadCards(player, newPlayer));
            newPlayer.setProgram(loadRegisters(player, newPlayer));

            board.addPlayer(newPlayer);
        }
    }
    private static CommandCardField[] loadCards(PlayerTemplate player, Player newPlayer) {
        CommandCardField[] newCards = new CommandCardField[player.cards.size()];
        for(int i = 0; i < player.cards.size(); i++) {
            CommandCardField commandCardField = new CommandCardField(newPlayer);
            String command = player.cards.get(i).command;

            if (!command.equals("")) {
                CommandCard commandCard = new CommandCard(Command.valueOf(command));

                // Set the CommandCardField card
                commandCardField.setCard(commandCard);
            }

            newCards[i] = commandCardField;

        }
        return newCards;
    }
    private static CommandCardField[] loadRegisters(PlayerTemplate player, Player newPlayer) {
        CommandCardField[] newRegisters = new CommandCardField[player.registers.size()];
        for(int i = 0; i < player.registers.size(); i++) {
            CommandCardField commandCardField = new CommandCardField(newPlayer);
            String command = player.registers.get(i).command;

            if (!command.equals("")) {
                CommandCard commandCard = new CommandCard(Command.valueOf(command));

                // Set the CommandCardField card
                commandCardField.setCard(commandCard);
            }

            newRegisters[i] = commandCardField;

        }
        return newRegisters;
    }

    /**
     * Deserialize a json string with the state of the game and returns a board.
     *
     * @return board object with the game state
     */
    private static Board deserializeGame(String jsonGameState, String gameName) {
        // In simple cases, we can create a Gson object with new Gson():
        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(SpaceElement.class, new Adapter<SpaceElement>());
        Gson gson = simpleBuilder.create();

        BoardTemplate template = gson.fromJson(jsonGameState, BoardTemplate.class);

        Board board = new Board(template.width, template.height, template.checkPointAmount, gameName);

        loadSpaces(template, board);
        loadPlayers(template, board);

        // -----------------------------SAVED GAME-------------------------------------
        // load board state values into board from templates
        board.setCurrentPlayer(board.getPlayer(template.currentPlayer));
        board.setPhase(Phase.valueOf(template.phase));
        board.setStep(template.step);

        return board;
    }

    private static Board deserializeBoard(String jsonGameState, String gameName, ArrayList<String> playernames) {
        // In simple cases, we can create a Gson object with new Gson():
        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(SpaceElement.class, new Adapter<SpaceElement>());
        Gson gson = simpleBuilder.create();

        BoardTemplate template = gson.fromJson(jsonGameState, BoardTemplate.class);

        Board board = new Board(template.width, template.height, template.checkPointAmount, gameName);

        loadSpaces(template, board);

        for(int i = 0; i < playernames.size(); i++) {
            Player player = new Player(board, template.players.get(i).color, playernames.get(i));
            player.setSpace(board.getSpace(template.players.get(i).spaceX, template.players.get(i).spaceY));
            player.setHeading(Heading.valueOf(template.players.get(i).heading));
            board.addPlayer(player);
        }
        return board;
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
            loadedBoard = true;
            return deserializeGame(gameState, gameName);
        }
        return new Board(8, 8, 1);
    }

    /**
     * Create a new game by loading a gameboard from the predefined board configurations in resource folder
     *
     * @param boardName name of the game board
     * @return the new board
     */
    public static Board newGame(String boardName, ArrayList<String> playernames) {
        String gameState = IOUtil.readGame(boardName, false);
        return deserializeBoard(gameState, boardName, playernames);
    }

    public static boolean getLoadedBoard() {
        return loadedBoard;
    }
}
