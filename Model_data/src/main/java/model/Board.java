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
package model;

import designpatterns.Subject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static model.Phase.INITIALISATION;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class Board extends Subject {

    private Integer gameId;

    public final String boardName;

    // maxAmount on THIS board
    public Integer maxAmountOfPlayers;

    public final int width;

    public final int height;

    // How many checkpoint are there in total
    public final int totalNoOfCheckpoints;

    private Phase phase = INITIALISATION;

    // zero indexed
    private int step = 0;

    private Player current;

    public Integer amountOfActivePlayers = 0;

    private boolean stepMode;

    private Player winner = null;

    private final Space[][] spaces;

    private final List<Player> players = new ArrayList<>();

    public Board(int width, int height, int totalNoOfCheckpoints, @NotNull String boardName) {
        this.boardName = boardName;
        this.width = width;
        this.height = height;
        this.totalNoOfCheckpoints = totalNoOfCheckpoints;
        spaces = new Space[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Space space = new Space(this, x, y);
                spaces[x][y] = space;
            }
        }
        this.stepMode = false;
    }

    public Board(int width, int height, int totalNoOfCheckpoints) {
        this(width, height, totalNoOfCheckpoints, "defaultboard");
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        if (this.gameId == null) {
            this.gameId = gameId;
        } else {
            if (!this.gameId.equals(gameId)) {
                throw new IllegalStateException("A game with a set id may not be assigned a new id!");
            }
        }
    }

    public Space getSpace(int x, int y) {
        if (x >= 0 && x < width &&
                y >= 0 && y < height) {
            return spaces[x][y];
        } else {
            return null;
        }
    }

    public Space[][] getSpaces() {
        return spaces;
    }

    public int getPlayersNumber() {
        return players.size();
    }

    public void addPlayer(@NotNull Player player) {
        if (player.board == this && !players.contains(player)) {
            players.add(player);
            notifyChange();
        }
    }

    public Player getPlayer(int i) {
        if (i >= 0 && i < players.size()) {
            return players.get(i);
        } else {
            return null;
        }
    }

    public Player getPlayer(String playerName) {
        for (Player player : players) {
            if (player.getName().equals(playerName)) return player;
        }
        return null;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> newPlayerList) {
        for (int i = 0; i < players.size(); i++) {
            this.players.set(i, newPlayerList.get(i));
        }
    }

    public Player getCurrentPlayer() {
        return current;
    }

    public void setCurrentPlayer(Player player) {
        if (player != this.current && players.contains(player)) {
            this.current = player;
            notifyChange();
        }
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        if (phase != this.phase) {
            this.phase = phase;
            notifyChange();
        }
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        if (step != this.step) {
            this.step = step;
            notifyChange();
        }
    }

    public boolean isStepMode() {
        return stepMode;
    }

    public void setStepMode(boolean stepMode) {
        if (stepMode != this.stepMode) {
            this.stepMode = stepMode;
            notifyChange();
        }
    }

    public int getPlayerNumber(@NotNull Player player) {
        if (player.board == this) {
            return players.indexOf(player);
        } else {
            return -1;
        }
    }

    /**
     * Returns the neighbour of the given space of the board in the given heading.
     * The neighbour is returned only, if it can be reached from the given space
     * (no walls or obstacles in either of the involved spaces); otherwise,
     * null will be returned.
     *
     * @param space   the space for which the neighbour should be computed
     * @param heading the heading of the neighbour
     * @return the space in the given direction; null if there is no (reachable) neighbour
     */
    public Space getNeighbour(@NotNull Space space, @NotNull Heading heading) {
        int x = space.x;
        int y = space.y;

        // Update Placing/Check for collision
        switch (heading) {
            case SOUTH -> y = (y + 1); //% height;
            case WEST -> x = (x - 1); //% width;
            case NORTH -> y = (y - 1); //% height;
            case EAST -> x = (x + 1); // % width;
        }

        // Check for out of bounds
        if (y > height || y < 0 && x > width || x < 0) {
            return null;
        } else {
            // Moved within board
            return getSpace(x, y);
        }
    }

    public String getBoardName() {
        return boardName;
    }

    /*public String getStatusMessage() {
        // this is actually a view aspect, but for making assignment V1 easy for
        // the students, this method gives a string representation of the current
        // status of the game

        // XXX: V2 changed the status so that it shows the phase, the player and the step
        return "Phase: " + getPhase().name() +
                ", Player = " + getCurrentPlayer().getName() +
                ", Step: " + getStep();
    }*/

    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) {
        if (this.winner == null && winner.isWinner())
            this.winner = winner;
        notifyChange();
    }

    public int getAmountOfActivePlayers() {
        return amountOfActivePlayers;
    }
    
    public void setMaxAmountOfPlayers(int maxAmountOfPlayers) {
        if (this.maxAmountOfPlayers == null) {
            this.maxAmountOfPlayers = maxAmountOfPlayers;
        } else {
            if (!this.maxAmountOfPlayers.equals(maxAmountOfPlayers)) {
                throw new IllegalStateException("A game with a set maxAmoundOfPlayers may not be assigned a new id!");
            }
        }
    }

    /**
     * Return the index of a free robot
     */
    public Optional<Integer> getRobot() {
        for (Player player : players) {
            if (!player.active) {
                return Optional.of(players.indexOf(player));
            }
        }
        return Optional.empty();
    }

    public void setRobot(Player player) {
        amountOfActivePlayers++;
        Optional<Integer> freeIndex = getRobot();
        freeIndex.ifPresent(integer -> players.set(integer, player));
    }

    public void removeRobot(Player player, int i) {
        amountOfActivePlayers--;
        players.set(i, player);
    }

    public void updateView() {
        notifyChange();
    }
}
