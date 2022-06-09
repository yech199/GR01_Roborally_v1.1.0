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

public class SaveServer {

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

    private static ArrayList<PlayerTemplate> savePlayers(List<Player> players, Phase phase, Player newPlayer) {
        ArrayList<PlayerTemplate> playerTemplates = new ArrayList<>();
        // Save state of player
        for (Player player : players) {
            PlayerTemplate playerTemplate = new PlayerTemplate();
            Space space = player.getSpace();
            playerTemplate.spaceX = space.x;
            playerTemplate.spaceY = space.y;
            playerTemplate.heading = String.valueOf(player.getHeading());
            playerTemplate.color = player.getColor();
            playerTemplate.name = player.getName();

            // If Phase is INITIALISATION then we don't save cards. We also only save card if it's the player
            if (!phase.equals(Phase.INITIALISATION) && newPlayer.equals(player)) {
                playerTemplate.cards = saveCommandCardFields(player.getCards());
                playerTemplate.registers = saveCommandCardFields(player.getRegisters());
            }
            playerTemplate.active = player.activePlayer;
            playerTemplates.add(playerTemplate);
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

    private static String serialize(BoardTemplate template) {
        // Saving the board template using GSON
        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(SpaceElement.class, new Adapter<SpaceElement>()).
                setPrettyPrinting();
        Gson gson = simpleBuilder.create();

        return gson.toJson(template, template.getClass());
    }

    public static String serializePlayerState(Board board, Player newPlayer) {
        // Set up the board template by copying values one by one
        BoardTemplate template = new BoardTemplate();
        template.width = board.width;
        template.height = board.height;

        template.spaces = saveSpaces(board);
        template.players = savePlayers(board.getPlayers(), board.getPhase(), newPlayer);

        if (board.getCurrentPlayer() == null) template.currentPlayer = 0;
        else template.currentPlayer = board.getPlayerNumber(board.getCurrentPlayer());

        template.step = board.getStep();
        template.phase = String.valueOf(board.getPhase());
        template.boardName = board.getBoardName();
        template.checkPointAmount = board.totalNoOfCheckpoints;
        template.maxNumberOfPlayers = board.maxAmountOfPlayers;

        // Count active players in game
        AtomicInteger i = new AtomicInteger();
        board.getPlayers().forEach((p) -> {
            if (p.activePlayer) i.getAndIncrement();
        });
        template.activePlayers = i.get();

        if (board.getGameId() != null) template.gameId = board.getGameId();

        return serialize(template);
    }

}
