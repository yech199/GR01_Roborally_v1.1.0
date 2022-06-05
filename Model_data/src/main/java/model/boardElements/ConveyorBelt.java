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
package model.boardElements;

import controller.AGameController;
import model.Heading;
import model.Player;
import model.Space;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class ConveyorBelt extends SpaceElement {

    public enum ConveyorBeltColor {
        GREEN,
        BLUE
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
    public void doAction(@NotNull AGameController gameController, @NotNull Space space) {
        if (space.getActions().size() > 0) {

            ConveyorBelt action = (ConveyorBelt) space.getActions().get(0);

            // Get Player on field
            Player player = space.getPlayer();

            // If there is a player on the field
            if (player != null) {
                // Remember the players heading, then change it to conveyors direction
                Heading playerHeading = player.getHeading();
                player.setHeading(action.heading);

                // Move the player
                    gameController.moveForward(player);
                if (color == ConveyorBeltColor.GREEN) {
                } else {
                    // Move player, then update direction, then move again
                    gameController.moveForward(player);
                    player.setHeading(action.heading);
                    gameController.moveForward(player);
                }
                // Reset the heading of the player
                player.setHeading(playerHeading);
            } else {
                // No player on space
            }
        }
    }
}