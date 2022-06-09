package dtu.compute.RoborallyAPI;

import client_server.IGameService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import controller.GameController;
import fileaccess.IOUtil;
import fileaccess.LoadBoard;
import fileaccess.LoadServer;
import fileaccess.SaveServer;
import fileaccess.model.PlayerTemplate;
import model.Board;
import model.Globals;
import model.Player;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static fileaccess.SaveBoard.serializeBoard;

@Service
public class GameService implements IGameService {

    // A game is a board with a set gameId and saved player cards/registers
    ArrayList<GameController> activeGames = new ArrayList<>();
    ArrayList<Board> modelBoards = new ArrayList<>();

    ArrayList<PlayerTemplate> playerData = new ArrayList<>();

    int id = 1;

    public GameService() {
        // Initialize board templates on server
        List<String> boardNames = IOUtil.getBoardFileNames();
        for (String boardName : boardNames) {
            Board board = LoadServer.loadBoard(boardName, Globals.MAX_NO_PLAYERS);
            modelBoards.add(board);
        }
    }

    @Override
    public String getGameById(int id, String playerName) {
        Board boardGame = findGameBoard(id);
        if (boardGame == null) return "Game not found";
        Player player = boardGame.getPlayer(playerName);
        if (player == null) return "Player not found";
        return SaveServer.serializePlayerState(boardGame, player);
    }

    @Override
    public String updateGame(int id, String playername, String playerState) {
        GameController game = findGame(id);
        if(game == null) return "Game not found";
        LoadServer.deserializePlayer(new Gson().fromJson(playerState, PlayerTemplate.class), game.board);
        return "OK";
    }

    @Override
    public String createGame(String boardName, int numOfPlayers) {
        Board board = findBoard(boardName);
        if (board == null) return "Board not found";
        String boardJson = SaveServer.serializePlayerState(board, board.getPlayer(0));
        Board game = LoadServer.createBoard(boardJson, numOfPlayers);
        GameController gameController = new GameController(game);
        int gameId = id;
        game.setGameId(gameId);
        id++;
        activeGames.add(gameController);
        return String.valueOf(gameId);
    }

    @Override
    public String getListOfGames() {
        List<Integer> listOfGames = new ArrayList<>();
        List<String> listOfBoardNames = new ArrayList<>();
        List<Integer> listOfActivePlayers = new ArrayList<>();
        List<Integer> listOfTotalPlayers = new ArrayList<>();

        // Get a list of game IDs
        activeGames.forEach(game -> listOfGames.add(game.board.getGameId()));
        activeGames.forEach(game -> listOfBoardNames.add(game.board.getBoardName()));
        activeGames.forEach(game -> listOfActivePlayers.add(game.board.amountOfActivePlayers));
        activeGames.forEach(game -> listOfTotalPlayers.add(game.board.maxAmountOfPlayers));

        // https://www.tutorialspoint.com/how-to-convert-java-array-or-arraylist-to-jsonarray-using-gson-in-java
        JsonObject jsonObj = new JsonObject();
        // ArrayList to JsonArray
        JsonArray jsonArray1 = new Gson().toJsonTree(listOfGames).getAsJsonArray();
        JsonArray jsonArray2 = new Gson().toJsonTree(listOfBoardNames).getAsJsonArray();
        JsonArray jsonArray3 = new Gson().toJsonTree(listOfActivePlayers).getAsJsonArray();
        JsonArray jsonArray4 = new Gson().toJsonTree(listOfTotalPlayers).getAsJsonArray();

        jsonObj.add("gameId", jsonArray1);
        jsonObj.add("boardNames", jsonArray2);
        jsonObj.add("activePlayers", jsonArray3);
        jsonObj.add("maxNumberOfPlayers", jsonArray4);

        return new Gson().toJson(jsonObj);
    }

    @Override
    public String getListOfBoards() {
        List<String> listOfBoards = new ArrayList<>();
        // Get a list of board names
        modelBoards.forEach(board -> listOfBoards.add(board.getBoardName()));
        return new Gson().toJson(listOfBoards);
    }

    @Override
    public String getBoardState(int gameId) {
        for (GameController game : activeGames) {
            if (game.board.getGameId().equals(gameId)) {
                return serializeBoard(game.board);
            }
        }
        return null;
    }

    @Override
    public String joinGame(int id, String playerName) {
        GameController game = findGame(id);
        Board gameBoard = game.board;

        if(game == null) return "Game not found";
        if(gameBoard.getPlayers().contains(gameBoard.getPlayer(playerName))) return "Player with same name already joined";
        if(gameBoard.getAmountOfActivePlayers() >= gameBoard.maxAmountOfPlayers) return "Game Full";

        gameBoard.getRobot().ifPresent(freePlayerIndex -> {
            Player template = gameBoard.getPlayer(freePlayerIndex);
            gameBoard.getPlayers().set(freePlayerIndex, template);
            String color = template.getColor();

            // Add new player and replace dummy player
            Player player = new Player(game.board, color, playerName);
            player.setSpace(template.getSpace());
            player.setHeading(template.getHeading());
            player.active = true;
            gameBoard.setRobot(player);

            if(gameBoard.amountOfActivePlayers.equals(gameBoard.maxAmountOfPlayers)) {
                game.startProgrammingPhase();
            }
        });
        return String.valueOf(gameBoard.getGameId());
    }

    @Override
    public String leaveGame(int id, String playerName) {
        GameController game = findGame(id);
        Board board = game.board;

        if(board == null) return "Game not found";
        if(board.amountOfActivePlayers == 1) {
            activeGames.remove(game);
            return "Game removed";
        }
        Player player = board.getPlayer(playerName);

        int i  = board.getPlayers().indexOf(player);

        // Add new player and replace dummy player
        Player dummy = new Player(board, player.getColor(), "Player " + (i+1));
        dummy.setSpace(player.getSpace());
        dummy.setHeading(player.getHeading());
        dummy.active = false;
        dummy.setCards(player.getCards());
        dummy.setRegisters(player.getRegisters());

        board.removeRobot(dummy, i);

        return "ok";
    }

    @Override
    public String setPlayerState(int id, String playerName, String playerData) {
        GameController game = findGame(id);
        if(game == null) return "Game not found";
        if(!game.board.getPlayers().contains(game.board.getPlayer(playerName))) return "Player not found";
        for(PlayerTemplate player : this.playerData) {
            if (player.name.equals(playerName)) return "Player already submitted";
        }
        Board gameBoard = game.board;
        // Save the JSON player data for execution
        PlayerTemplate player = new Gson().fromJson(playerData, PlayerTemplate.class);
        this.playerData.add(player);
        // If all player data have been recieved, load the state into gameBoard and finish Programming Phase
        if (this.playerData.size() >= gameBoard.maxAmountOfPlayers) {
            LoadBoard.loadPlayers(this.playerData, gameBoard);
            int i = 0;
            while(this.playerData.size() != 0) {
                this.playerData.remove(this.playerData.get(i));
            }
            game.finishProgrammingPhase();
            game.executePrograms();
        }
        return "ok";
    }

    public GameController findGame(int id) {
        for (GameController game : activeGames) {
            if (game.board.getGameId() == id) {
                return game;
            }
        }
        return null;
    }

    public Board findGameBoard(int id) {
        for (GameController game : activeGames) {
            if (game.board.getGameId() == id) {
                return game.board;
            }
        }
        return null;
    }

    private Board findBoard(String boardName) {
        for (Board board : modelBoards) {
            if (board.getBoardName().equals(boardName)) {
                return board;
            }
        }
        return null;
    }

}
