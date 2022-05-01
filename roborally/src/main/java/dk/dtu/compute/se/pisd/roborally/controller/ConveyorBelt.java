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
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class ConveyorBelt extends FieldAction {

    public enum ConveyorBeltColor {
        green,
        blue
    }

    private ConveyorBeltColor color;

    private Heading heading;

    public ConveyorBeltColor getConveyorBeltColor() {
        return color;
    }

    public void setConveyorBeltColor(ConveyorBeltColor color) {
        this.color = color;
    }

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        if (space.getActions().size() > 0) {
            ConveyorBelt action = (ConveyorBelt) space.getActions().get(0);

            Player player = space.getPlayer();

            if (player != null) {
                // Move in direction of player
                Heading heading = player.getHeading();
                //player.setHeading(action.heading);
                if (color == ConveyorBeltColor.green) {
                    gameController.moveForward(player);
                } else {
                    gameController.fastForward(player, 2);
                }

                player.setHeading(heading);
            } else {
                // No player on space
                return false;
            }


            // Set Variables
            /*Heading heading = player.getSpace().conveyorDirection;
            Space target = board.getNeighbour(player.getSpace(), heading);

            // Green: Move once
            if (player.getSpace().isGreenConveyor) {
                if (!target.isWall && target.getPlayer() == null) {
                    target.setPlayer(player);
                }
            }
            // Blue: Move Twice
            if (player.getSpace().isBlueConveyor) {
                for (int i = 0; i < 2; i++) {
                    heading = player.getSpace().conveyorDirection;
                    target = board.getNeighbour(player.getSpace(), heading);
                    if (!target.isWall && target.getPlayer() == null) {
                        target.setPlayer(player);
                    }
                }
            }
        }
        return false;*/
        }
        return true;
    }
}