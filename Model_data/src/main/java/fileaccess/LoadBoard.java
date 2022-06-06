package fileaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fileaccess.model.BoardTemplate;
import fileaccess.model.CommandCardFieldTemplate;
import fileaccess.model.PlayerTemplate;
import fileaccess.model.SpaceTemplate;
import model.*;
import model.boardElements.SpaceElement;

import java.util.ArrayList;

public class LoadBoard {

    private static void loadSpaces(BoardTemplate template, Board board) {
        for (SpaceTemplate spaceTemplate : template.spaces) {
            Space space = board.getSpace(spaceTemplate.x, spaceTemplate.y);
            if (space != null) {
                space.getActions().addAll(spaceTemplate.actions);
                space.getWalls().addAll(spaceTemplate.walls);
            }
        }
    }
    /**
     * Only used when loading a saved game. Not a new game
     */
    private static void loadPlayers(BoardTemplate template, Board board) {
        // Loading players
        for (PlayerTemplate player : template.players) {
            Player newPlayer = new Player(board, player.color, player.name);
            newPlayer.setSpace(board.getSpace(player.spaceX, player.spaceY));
            newPlayer.setHeading(Heading.valueOf(player.heading));

            newPlayer.setCards(loadCommandCardFields(player.cards, newPlayer));
            newPlayer.setProgram(loadCommandCardFields(player.registers, newPlayer));

            newPlayer.activePlayer = true;
            board.addPlayer(newPlayer);
        }
    }

    private static CommandCardField[] loadCommandCardFields(ArrayList<CommandCardFieldTemplate> commandCardFields, Player newPlayer) {
        int i = 0;
        CommandCardField[] newCards = new CommandCardField[commandCardFields.size()];
        for (CommandCardFieldTemplate commandCardFieldTemplate : commandCardFields) {
            CommandCardField commandCardField = new CommandCardField(newPlayer);
            String command = commandCardFieldTemplate.command;

            // If the card is not empty we create a commandCard
            if (!command.equals("")) {
                CommandCard commandCard = new CommandCard(Command.valueOf(command));
                commandCardField.setCard(commandCard);
            }
            newCards[i] = commandCardField;
            i++;
        }
        return newCards;
    }

    /**
     * Deserialize a json string with the state of the game and returns a board.
     *
     * @return board object with the game state
     */
    private static Board deserializeGame(String jsonGameState) {
        // In simple cases, we can create a Gson object with new Gson():
        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(SpaceElement.class, new Adapter<SpaceElement>());
        Gson gson = simpleBuilder.create();

        BoardTemplate template = gson.fromJson(jsonGameState, BoardTemplate.class);

        // load board state values into board from templates
        Board board = new Board(template.width, template.height, template.checkPointAmount, template.boardName);
        loadSpaces(template, board);
        loadPlayers(template, board);
        board.setCurrentPlayer(board.getPlayer(template.currentPlayer));
        board.setPhase(Phase.valueOf(template.phase));
        board.setStep(template.step);
        board.setGameId(template.gameId);

        return board;
    }

    private static Board deserializeBoard(String jsonGameState, int numberOfPlayers) {
        // In simple cases, we can create a Gson object with new Gson():
        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(SpaceElement.class, new Adapter<SpaceElement>());
        Gson gson = simpleBuilder.create();

        BoardTemplate template = gson.fromJson(jsonGameState, BoardTemplate.class);

        Board board = new Board(template.width, template.height, template.checkPointAmount, template.boardName);

        loadSpaces(template, board);

        int playerNo = 1;
        for(int i = 0; i < numberOfPlayers; i++) {
            Player player = new Player(board, template.players.get(i).color, "Player " + playerNo);
            player.setSpace(board.getSpace(template.players.get(i).spaceX, template.players.get(i).spaceY));
            player.setHeading(Heading.valueOf(template.players.get(i).heading));
            board.addPlayer(player);
            playerNo++;
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
            return deserializeGame(gameState);
        }
        return new Board(8, 8, 1);
    }

    /**
     * Load a board object given a json string
     */
    public static Board loadGameState(String jsonGameState) {
        try {
            return deserializeGame(jsonGameState);
        } catch (Exception e) {
            System.out.println("Loading of game state failed");
            return new Board(8, 8, 1);
        }
    }

    /**
     * Create a new game by loading a gameboard from the predefined board configurations in resource folder
     *
     * @param boardName name of the game board
     * @return the new board
     */
    public static Board newBoard(String boardName, int numberOfPlayers) {
        String gameState = IOUtil.readGame(boardName, false);
        return deserializeBoard(gameState, numberOfPlayers);
    }

    public static Board newBoardState(String jsonBoardState, int gameId, int numberOfPlayers) {
        Board board = deserializeBoard(jsonBoardState, numberOfPlayers);
        board.setGameId(gameId);
        return board;
    }
}
