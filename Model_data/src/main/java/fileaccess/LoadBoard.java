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
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class loads/deserialize different configurations of the board and game
 */
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
     * Load each player by copying Only used when loading a saved game. Not a new game
     */
    public static void loadPlayers(List<PlayerTemplate> players, Board board) {
        // Loading players
        int i = 0;
        for (PlayerTemplate playerTemplate : players) {
            Player newPlayer = new Player(board, playerTemplate.color, playerTemplate.name);
            newPlayer.setSpace(board.getSpace(playerTemplate.spaceX, playerTemplate.spaceY));
            newPlayer.setHeading(Heading.valueOf(playerTemplate.heading));

            newPlayer.setCards(loadCommandCardFields(playerTemplate.cards, newPlayer, 9));
            newPlayer.setRegisters(loadCommandCardFields(playerTemplate.registers, newPlayer, 5));

            if (board.getPhase() == Phase.INITIALISATION) {
                board.addPlayer(newPlayer);
            } else {
                board.getPlayers().set(i, newPlayer);
            }
            i++;
        }
    }

    private static CommandCardField[] loadCommandCardFields(ArrayList<CommandCardFieldTemplate> commandCardFields, Player newPlayer, int size) {
        int i = 0;
        CommandCardField[] newCards = new CommandCardField[size];
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
        if (commandCardFields.size() == 0) {
            for (int j = 0; j < size; j++) {
                CommandCardField commandField = new CommandCardField(newPlayer);
                newCards[i] = commandField;
            }
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
        loadPlayers(template.players, board);
        board.setCurrentPlayer(board.getPlayer(template.currentPlayer));
        board.setPhase(Phase.valueOf(template.phase));
        board.setStep(template.step);
        board.setGameId(template.gameId);
        board.setMaxAmountOfPlayers(template.maxNumberOfPlayers);
        board.amountOfActivePlayers = template.activePlayers;

        return board;
    }

    public static Board deserializeBoard(String jsonGameState, int numOfPlayers) {
        // In simple cases, we can create a Gson object with new Gson():
        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(SpaceElement.class, new Adapter<SpaceElement>());
        Gson gson = simpleBuilder.create();

        BoardTemplate template = gson.fromJson(jsonGameState, BoardTemplate.class);

        Board board = new Board(template.width, template.height, template.checkPointAmount, template.boardName);

        loadSpaces(template, board);

        if (board.getPhase() == Phase.INITIALISATION) {
            int playerNo = 1;
            for (int i = 0; i < numOfPlayers; i++) {
                Player player = new Player(board, template.players.get(i).color, "Player " + playerNo);
                player.setSpace(board.getSpace(template.players.get(i).spaceX, template.players.get(i).spaceY));
                player.setHeading(Heading.valueOf(template.players.get(i).heading));
                board.addPlayer(player);
                playerNo++;
            }
            AtomicInteger i = new AtomicInteger();
            board.getPlayers().forEach((player) -> {
                if (player.active) i.getAndIncrement();
            });
            board.amountOfActivePlayers = i.get();
        }
        board.setMaxAmountOfPlayers(numOfPlayers);

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
        //System.out.println("File: "+jsonGameState);
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
    public static Board newBoard(String boardName, int numOfPlayers) {
        String gameState = IOUtil.readGame(boardName, false);
        return deserializeBoard(gameState, numOfPlayers);
    }

}
