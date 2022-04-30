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
package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static dk.dtu.compute.se.pisd.roborally.model.Heading.*;
import static dk.dtu.compute.se.pisd.roborally.model.Phase.INITIALISATION;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Board extends Subject {

    public final int width;

    public final int height;

    public final String boardName;

    private Integer gameId;

    private final Space[][] spaces;

    private final List<Player> players = new ArrayList<>();

    private Player current;

    private Phase phase = INITIALISATION;

    private int step = 0;

    public int checkPointAmount; // How many checkpoint are there in total

    private final int wallAmount = 3;

    private boolean stepMode;

    public Board(int width, int height, @NotNull String boardName) {
        this.boardName = boardName;
        this.width = width;
        this.height = height;
        spaces = new Space[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Space space = new Space(this, x, y);
                spaces[x][y] = space;
            }
        }
        this.stepMode = false;
        /*setupCheckpoints(checkPointAmount);
        setupWalls(wallAmount);
        setupGears(wallAmount);
        setupConveyor(3);
        setupPushPanels(3);*/
    }

    public Board(int width, int height) {
        this(width, height, "defaultboard");
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        if (this.gameId == null) {
            this.gameId = gameId;
        }
        else {
            if (!this.gameId.equals(gameId)) {
                throw new IllegalStateException("A game with a set id may not be assigned a new id!");
            }
        }
    }

    public Space getSpace(int x, int y) {
        if (x >= 0 && x < width &&
                y >= 0 && y < height) {
            return spaces[x][y];
        }
        else {
            return null;
        }
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
        }
        else {
            return null;
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
        }
        else {
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
        switch (heading) {
            case SOUTH:
                y = (y + 1) % height;
                break;
            case WEST:
                x = (x + width - 1) % width;
                break;
            case NORTH:
                y = (y + height - 1) % height;
                break;
            case EAST:
                x = (x + 1) % width;
                break;
        }

        return getSpace(x, y);
    }

    public int getCheckPointAmount() {
        return checkPointAmount;
    }

    public String getStatusMessage() {
        // this is actually a view aspect, but for making assignment V1 easy for
        // the students, this method gives a string representation of the current
        // status of the game

        // XXX: V2 changed the status so that it shows the phase, the player and the step
        return "Phase: " + getPhase().name() +
                ", Player = " + getCurrentPlayer().getName() +
                ", Step: " + getStep();
    }

    /*public void setupCheckpoints(int checkPointAmount) {

        int x = 2;
        int y = 2;
        // Create checkpoints
        /*
         * Added dummy checkpoints (2,2), (2,4) & (2,6)
         */
        /*switch (checkPointAmount) {
            case 3:
                spaces[x][y + 4].checkpointNumber = 3;
            case 2:
                spaces[x][y + 2].checkpointNumber = 2;
            case 1:
                spaces[x][y].checkpointNumber = 1;
                break;
        }
    }

    public void setupWalls(int wallAmount) {
        int x = 4;
        int y = 1;
        // create walls at (2,1) (2,3) (2,5)
        switch (wallAmount)
        {
            case 3:
                spaces[x][y + 5].isWall = true;
            case 2:
                spaces[x][y + 2].isWall = true;
            case 1:
                spaces[x][y].isWall = true;
                break;
        }
    }
    public void setupPushPanels(int pushPanelAmount) {
        int x = 5;
        int y = 1;
        // create push panels at (5,1) (5,3) (5,5)
        switch (pushPanelAmount)
        {
            case 3:
                spaces[x][y + 2].isPushPanel = true;
                boolean[] values1 = {false, true, false, true, false};
                spaces[x][y + 2].setPushPanelLabel(values1);
                spaces[x][y + 2].pushPanelDirection = SOUTH;
            case 2:
                spaces[x + 1][y + 1].isPushPanel = true;
                boolean[] values2 = {false, false, false, true, true};
                spaces[x + 1][y + 1].setPushPanelLabel(values2);
                spaces[x + 1][y + 1].pushPanelDirection = EAST;
            case 1:
                spaces[x + 2][y + 3].isPushPanel = true;
                boolean[] values3 = {true, false, false, false, true};
                spaces[x + 2][y + 3].setPushPanelLabel(values3);
                spaces[x + 2][y + 3].pushPanelDirection = WEST;
                break;
        }
    }

    public void setupGears(int gearAmount) {
        int x = 6;
        int y = 1;
        // create walls at (2,1) (2,3) (2,5)
        switch (wallAmount)
        {
            case 3:
                spaces[x][y].isGear = true;
                spaces[x][y].gearDirection = true;
            case 2:
                spaces[x][y + 2].isGear = true;
                spaces[x][y + 2].gearDirection = false;
            case 1:
                spaces[x][y + 4].isGear = true;
                spaces[x][y + 4].gearDirection = true;
                break;
        }
    }

    public void setupConveyor(int wallAmount) {
        int x = 3;
        int y = 1;
        // create walls at (2,1) (2,3) (2,5)
        switch (wallAmount)
        {
            case 3:
                spaces[x][y + 4].isGreenConveyor = true;
                spaces[x][y + 4].conveyorDirection = EAST;
            case 2:
                spaces[x + 1][y + 4].isGreenConveyor = true;
                spaces[x + 1][y + 4].conveyorDirection = EAST;
            case 1:
                spaces[x + 2][y + 4].isGreenConveyor = true;
                spaces[x + 2][y + 4].conveyorDirection = EAST;
                break;
        }
    }*/

    public int getSpaceAmount() {
        return spaces.length;
    }
}
