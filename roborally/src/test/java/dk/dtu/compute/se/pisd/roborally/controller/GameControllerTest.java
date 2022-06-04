package dk.dtu.compute.se.pisd.roborally.controller;

import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import roborally.controller.GameController;

class GameControllerTest {

    private final int TEST_WIDTH = 8;
    private final int TEST_HEIGHT = 8;
    private final int TEST_CHECKPOINTAMOUNT = 8;

    private GameController gameController;

    @BeforeEach
    void setUp() {
        Board board = new Board(TEST_WIDTH, TEST_HEIGHT, TEST_CHECKPOINTAMOUNT);
        gameController = new GameController(board);
        for (int i = 0; i < 6; i++) {
            Player player = new Player(board, null, "Player " + i + 1);
            player.setName("Player " + (i + 1));
            board.addPlayer(player);
            player.setSpace(board.getSpace(i, i));
            // Player 0: 0 % 4 = 0      Player 1: 1 % 4 = 1     Player 2: 2 % 4 = 2
            // Player 3: 3 % 4 = 3      Player 4: 4 % 4 = 0     Player 5: 5 % 4 = 1
            // 0 = SOUTH    1 = WEST    2 = NORTH   3 = EAST
            player.setHeading(Heading.values()[i % Heading.values().length]);
        }
        board.setCurrentPlayer(board.getPlayer(0));
    }

    @AfterEach
    void tearDown() {
        gameController = null;
    }

    @Test
    void moveCurrentPlayerToSpace() {
        Board board = gameController.board;
        Player player1 = board.getPlayer(0);
        Player player2 = board.getPlayer(1);

        gameController.moveCurrentPlayerToSpace(board.getSpace(0, 4));

        Assertions.assertEquals(player1, board.getSpace(0, 4).getPlayer(), "Player " + player1.getName() + " should be Space (0,4)!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
        Assertions.assertEquals(player2, board.getCurrentPlayer(), "Current player should be " + player2.getName() + "!");
    }

    @Test
    void testStartProgrammingPhase() {
        Board board = gameController.board;

        gameController.startProgrammingPhase();

        Assertions.assertEquals(Phase.PROGRAMMING, board.getPhase(), "It should be in PROGRAMMING Phase!");
        Assertions.assertEquals(board.getPlayer(0), board.getCurrentPlayer(), "Player " + board.getCurrentPlayer().getName() + " should be the current player!");
        Assertions.assertEquals(0, board.getStep(), "Step should be zero!");
        Assertions.assertEquals(6, board.getPlayersNumber(), "There should be 6 players!");
    }

    @Test
    void testfinishProgrammingPhase() {
        Board board = gameController.board;

        gameController.finishProgrammingPhase();

        Assertions.assertEquals(Phase.ACTIVATION, board.getPhase(), "It should be in ACTIVATION Phase!");
        Assertions.assertEquals(board.getPlayer(0), board.getCurrentPlayer(), "Player "
                + board.getCurrentPlayer().getName() + " should be the current player!");
        Assertions.assertEquals(0, board.getStep(), "Step should be zero!");

    }

    @Test
    void testExecuteProgramsAndStep() {
        Board board = gameController.board;
        board.setPhase(Phase.ACTIVATION);

        gameController.executePrograms();

        Assertions.assertFalse(board.isStepMode(), "Stepmode should be false!");
        Assertions.assertEquals(Phase.PROGRAMMING, board.getPhase(), "It should be in PROGRAMMING Phase!");

        board.setPhase(Phase.ACTIVATION);
        gameController.executeStep();

        Assertions.assertTrue(board.isStepMode(), "Stepmode should be true!");
        Assertions.assertEquals(Phase.ACTIVATION, board.getPhase(), "It should be in ACTIVATION Phase!");
        // Step is zero because this method uses the function startProgrammingPhase() at the end
        Assertions.assertEquals(0, board.getStep(), "It should be step 0!");
    }

    @Test
    void testOPTION_LEFT_RIGHT() {
        Board board = gameController.board;
        board.setPhase(Phase.ACTIVATION);
        Player player = board.getPlayer(0);
        CommandCard card = new CommandCard(Command.OPTION_LEFT_RIGHT);
        player.getProgramField(board.getStep()).setCard(card);

        gameController.executePrograms();

        Assertions.assertEquals(Phase.PLAYER_INTERACTION, board.getPhase(), "It should be in PLAYER_INTERACTION Phase!");
    }

    @Test
    void testCommandCard() {
        Board board = gameController.board;
        board.setPhase(Phase.ACTIVATION);
        Player player = board.getPlayer(0);
        CommandCard card = new CommandCard(Command.FORWARD);
        player.getProgramField(board.getStep()).setCard(card);

        gameController.executePrograms();

        Assertions.assertEquals(player, board.getSpace(0, 1).getPlayer(), "Player " + player.getName() + " should be Space (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, player.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
        Assertions.assertEquals(Phase.PROGRAMMING, board.getPhase(), "It should be in PROGRAMMING Phase!");

        board.setPhase(Phase.ACTIVATION);
        player.getProgramField(board.getStep()).setCard(new CommandCard(Command.RIGHT));
        gameController.executePrograms();

        Assertions.assertEquals(player, board.getSpace(0, 1).getPlayer(), "Player "
                + player.getName() + " should be Space (0,1)!");
        Assertions.assertEquals(Heading.WEST, player.getHeading(), "Player 0 should be heading WEST!");
        Assertions.assertEquals(Phase.PROGRAMMING, board.getPhase(), "It should be in PROGRAMMING Phase!");

        board.setPhase(Phase.ACTIVATION);
        player.getProgramField(board.getStep()).setCard(new CommandCard(Command.LEFT));
        gameController.executePrograms();

        Assertions.assertEquals(player, board.getSpace(0, 1).getPlayer(), "Player "
                + player.getName() + " should be Space (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, player.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertEquals(Phase.PROGRAMMING, board.getPhase(), "It should be in PROGRAMMING Phase!");

        board.setPhase(Phase.ACTIVATION);
        player.getProgramField(board.getStep()).setCard(new CommandCard(Command.FAST_FORWARD));
        gameController.executePrograms();

        Assertions.assertEquals(player, board.getSpace(0, 3).getPlayer(), "Player " + player.getName() + " should be Space (0,2)!");
        Assertions.assertEquals(Heading.SOUTH, player.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
        Assertions.assertEquals(Phase.PROGRAMMING, board.getPhase(), "It should be in PROGRAMMING Phase!");
    }
    @Test
    void testExecuteCommandOptionAndContinue() {
        Board board = gameController.board;
        Player player = board.getPlayer(0);

        gameController.executeCommandOptionAndContinue(Command.FORWARD);

        Assertions.assertEquals(player, board.getSpace(0, 1).getPlayer(), "Player " + player.getName()
                + " should be Space (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, player.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
        Assertions.assertEquals(Phase.PROGRAMMING, board.getPhase(), "It should be in PROGRAMMING Phase!");

        gameController.executeCommandOptionAndContinue(Command.RIGHT);

        Assertions.assertEquals(player, board.getSpace(0, 1).getPlayer(), "Player "
                + player.getName() + " should be Space (0,1)!");
        Assertions.assertEquals(Heading.WEST, player.getHeading(), "Player 0 should be heading WEST!");
        Assertions.assertEquals(Phase.PROGRAMMING, board.getPhase(), "It should be in PROGRAMMING Phase!");

        gameController.executeCommandOptionAndContinue(Command.LEFT);

        Assertions.assertEquals(player, board.getSpace(0, 1).getPlayer(), "Player "
                + player.getName() + " should be Space (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, player.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertEquals(Phase.PROGRAMMING, board.getPhase(), "It should be in PROGRAMMING Phase!");

        gameController.executeCommandOptionAndContinue(Command.FAST_FORWARD);

        Assertions.assertEquals(player, board.getSpace(0, 3).getPlayer(), "Player "
                + player.getName() + " should be Space (0,3)!");
        Assertions.assertEquals(Heading.SOUTH, player.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
        Assertions.assertEquals(Phase.PROGRAMMING, board.getPhase(), "It should be in PROGRAMMING Phase!");

        board.setCurrentPlayer(board.getPlayer(5));
        player = board.getPlayer(5);
        Assertions.assertEquals(player, board.getSpace(5, 5).getPlayer(), "Player " + player.getName() + " should be Space (0,2)!");
        Assertions.assertEquals(Heading.WEST, player.getHeading(), "Player 5 should be heading WEST!");

        gameController.executeCommandOptionAndContinue(Command.FAST_FORWARD);

        Assertions.assertEquals(player, board.getSpace(3, 5).getPlayer(), "Player "
                + player.getName() + " should be Space (3,5)!");
        Assertions.assertEquals(Heading.WEST, player.getHeading(), "Player 0 should be heading WEST!");
        Assertions.assertNull(board.getSpace(5, 5).getPlayer(), "Space (0,0) should be empty!");
        Assertions.assertEquals(Phase.PROGRAMMING, board.getPhase(), "It should be in PROGRAMMING Phase!");
        Assertions.assertEquals(board.getPlayer(0), board.getCurrentPlayer(), "The current Player should be: "
                + player.getName());
    }

    @Test
    void moveForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.moveForward(current);

        Assertions.assertEquals(current, board.getSpace(0, 1).getPlayer(), "Player " + current.getName() + " should be Space (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }

    @Test
    void moveFastForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.moveXForward(current, 2);

        Assertions.assertEquals(current, board.getSpace(0, 2).getPlayer(), "Player " + current.getName() + " should be Space (0,2)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }

    @Test
    void moveRightOrLeft() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.turnLeft(current);
        gameController.moveForward(current);

        Assertions.assertEquals(current, board.getSpace(1, 0).getPlayer(), "Player " + current.getName() + " should be Space (1,0)!");
        Assertions.assertEquals(Heading.EAST, current.getHeading(), "Player 0 should be heading EAST!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");

        gameController.turnRight(current);
        gameController.moveForward(current);

        Assertions.assertEquals(current, board.getSpace(1, 1).getPlayer(), "Player " + current.getName() + " should be Space (1,1)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(1, 0).getPlayer(), "Space (1,0) should be empty!");
    }
}