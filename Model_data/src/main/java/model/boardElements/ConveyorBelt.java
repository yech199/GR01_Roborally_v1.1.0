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

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    @Override
    public void doAction(@NotNull AGameController gameController, @NotNull Space space) {
        if (space.getActions().size() > 0) {
            ConveyorBelt conveyorBelt = (ConveyorBelt) space.getActions().get(0);
            Player player = space.getPlayer();

            // If there is a player on the field
            if (player != null) {
                gameController.moveForward(player, conveyorBelt.heading);

                if (this.color == ConveyorBeltColor.BLUE) {
                    if (player.getSpace().getActions().size() > 0) {
                        for (SpaceElement s : player.getSpace().getActions()) {
                            if (s instanceof Pit)
                                return;
                        }
                    }
                    gameController.moveForward(player, conveyorBelt.heading);
                }
            }

            if (player.getSpace().getActions().size() > 0) {
                for (SpaceElement s : player.getSpace().getActions()) {
                    if (!(s instanceof ConveyorBelt))
                        s.doAction(gameController, player.getSpace());
                }
            }
        }
    }
}