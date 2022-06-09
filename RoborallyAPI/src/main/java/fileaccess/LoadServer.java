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

public class LoadServer {

    private static void loadSpaces(BoardTemplate template, Board board) {
        for (SpaceTemplate spaceTemplate : template.spaces) {
            Space space = board.getSpace(spaceTemplate.x, spaceTemplate.y);
            if (space != null) {
                space.getActions().addAll(spaceTemplate.actions);
                space.getWalls().addAll(spaceTemplate.walls);
            }
        }
    }

    public static void loadPlayers(List<PlayerTemplate> players, Board board) {
        // Loading players
        for (PlayerTemplate player : players) {
            Player newPlayer = new Player(board, player.color, player.name);
            newPlayer.setSpace(board.getSpace(player.spaceX, player.spaceY));
            newPlayer.setHeading(Heading.valueOf(player.heading));

            newPlayer.setCards(loadCommandCardFields(player.cards, newPlayer));
            newPlayer.setRegisters(loadCommandCardFields(player.registers, newPlayer));

            // If phase is INITIALISATION, check for a template player that new player can replace.
            // Else, check replace the same player with the new data
            if (board.getPhase() == Phase.INITIALISATION) {
                newPlayer.activePlayer = player.active;
                board.getRobot().ifPresent(integer -> board.getPlayers().set(integer, newPlayer));
            } else {
                for (int i = 0; i < board.getPlayers().size(); i++) {
                    if(board.getPlayers().get(i).getName().equals(newPlayer.getName())) {
                        board.getPlayers().set(i, newPlayer);
                    }
                }
            }
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

    private static BoardTemplate deserialize(String jsonGameState) {
        // In simple cases, we can create a Gson object with new Gson():
        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(SpaceElement.class, new Adapter<SpaceElement>());
        Gson gson = simpleBuilder.create();

        return gson.fromJson(jsonGameState, BoardTemplate.class);
    }

    public static Board loadGame(String jsonState) {
        BoardTemplate template = deserialize(jsonState);

        // load board state values into board from templates
        Board board = new Board(template.width, template.height, template.checkPointAmount, template.boardName);

        loadSpaces(template, board);
        loadPlayers(template.players, board);

        board.setCurrentPlayer(board.getPlayer(template.currentPlayer));
        board.setPhase(Phase.valueOf(template.phase));
        board.setStep(template.step);
        board.setGameId(template.gameId);
        board.setMaxAmountOfPlayers(template.maxNumberOfPlayers);

        // Count active players in game
        AtomicInteger i = new AtomicInteger();
        board.getPlayers().forEach((player) -> {
            if (player.activePlayer) i.getAndIncrement();
        });
        board.amountOfActivePlayers = i.get();

        return board;
    }

    public static Board createBoard(String boardState, int numOfPlayers) {
        BoardTemplate template = deserialize(boardState);

        // load board state values into board from templates
        Board board = new Board(template.width, template.height, template.checkPointAmount, template.boardName);

        loadSpaces(template, board);
        loadPlayers(template.players, board);

        board.setCurrentPlayer(board.getPlayer(template.currentPlayer));
        board.setPhase(Phase.valueOf(template.phase));
        board.setStep(template.step);

        board.setMaxAmountOfPlayers(numOfPlayers);
        // Overwrite
        int playerNo = 1;
        for(int i = 0; i < numOfPlayers; i++) {
            Player player = new Player(board, template.players.get(i).color, "Player " + playerNo);
            player.setSpace(board.getSpace(template.players.get(i).spaceX, template.players.get(i).spaceY));
            player.setHeading(Heading.valueOf(template.players.get(i).heading));
            board.addPlayer(player);
            playerNo++;
        }
        return board;
    }

    public static Board loadBoard(String boardName, int numOfPlayers) {
        String json = IOUtil.readGame(boardName, false);
        return createBoard(json, numOfPlayers);
    }

    public static void deserializePlayer(PlayerTemplate player, Board board) {
        Player newPlayer = new Player(board, player.color, player.name);
        newPlayer.setSpace(board.getSpace(player.spaceX, player.spaceY));
        newPlayer.setHeading(Heading.valueOf(player.heading));

        newPlayer.setCards(loadCommandCardFields(player.cards, newPlayer));
        newPlayer.setRegisters(loadCommandCardFields(player.registers, newPlayer));

        // If phase is INITIALISATION, check for a template player that new player can replace.
        // Else, check replace the same player with the new data
        if (board.getPhase() == Phase.INITIALISATION) {
            newPlayer.activePlayer = player.active;
            board.getRobot().ifPresent(integer -> board.getPlayers().set(integer, newPlayer));
        } else {
            for (int i = 0; i < board.getPlayers().size(); i++) {
                if(board.getPlayers().get(i).getName().equals(newPlayer.getName())) {
                    board.getPlayers().set(i, newPlayer);
                }
            }
        }
    }
}
