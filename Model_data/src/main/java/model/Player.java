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

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class Player extends Subject {
    private String name;
    private String color;

    private Space space;
    private Heading heading;

    private int gatheredCheckpoints = 0;

    private CommandCardField[] cards;
    private CommandCardField[] registers;

    public boolean isRebooted = false;

    public boolean active;

    final public Board board;

    public Player(@NotNull Board board, String color, @NotNull String name) {
        this.board = board;
        this.name = name;
        this.color = color;
        this.space = null;

        registers = new CommandCardField[Globals.NO_REGISTERS];
        for (int i = 0; i < registers.length; i++) {
            registers[i] = new CommandCardField(this);
        }

        cards = new CommandCardField[Globals.NO_CARDS];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = new CommandCardField(this);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.equals(this.name)) {
            this.name = name;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
        notifyChange();
        if (space != null) {
            space.playerChanged();
        }
    }

    public boolean isWinner() {
        return board.totalNoOfCheckpoints == this.gatheredCheckpoints;
    }

    public int getGatheredCheckpoints() {
        return gatheredCheckpoints;
    }

    public void addCheckPoint() {
        gatheredCheckpoints++;
    }

    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        Space oldSpace = this.space;
        if (space != oldSpace &&
                (space == null || space.board == this.board)) {
            this.space = space;
            if (oldSpace != null) {
                oldSpace.setPlayer(null);
            }
            if (space != null) {
                space.setPlayer(this);
            }
            notifyChange();
        }
    }

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(@NotNull Heading heading) {
        if (heading != this.heading) {
            this.heading = heading;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    public void setCards(@NotNull CommandCardField[] cards) {
        this.cards = cards;
    }

    public CommandCardField[] getCards() {
        return cards;
    }

    public void setRegisters(@NotNull CommandCardField[] registers) {
        this.registers = registers;
    }

    public CommandCardField[] getRegisters() {
        return registers;
    }

    public CommandCardField getProgramField(int i) {
        return registers[i];
    }

    public CommandCardField getCardField(int i) {
        return cards[i];
    }
}
