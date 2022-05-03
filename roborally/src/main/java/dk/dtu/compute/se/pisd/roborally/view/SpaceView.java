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
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.Antenna;
import dk.dtu.compute.se.pisd.roborally.controller.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.controller.Gear;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class SpaceView extends StackPane implements ViewObserver {
    public int tileAngle = 0;
    final public static int SPACE_HEIGHT = 60; // 75;
    final public static int SPACE_WIDTH = 60; // 75;

    public final Space space;

    public SpaceView(@NotNull Space space) {
        this.space = space;

        // XXX the following styling should better be done with styles
        this.setPrefWidth(SPACE_WIDTH);
        this.setMinWidth(SPACE_WIDTH);
        this.setMaxWidth(SPACE_WIDTH);

        this.setPrefHeight(SPACE_HEIGHT);
        this.setMinHeight(SPACE_HEIGHT);
        this.setMaxHeight(SPACE_HEIGHT);

        this.setStyle("-fx-background-image: url('graphics/DefaultTile.png'); -fx-background-size: "
                + SPACE_HEIGHT + " " + SPACE_WIDTH + ";");

        if (space.getActions().size() > 0 && space.getActions().get(0) instanceof ConveyorBelt conveyorBelt) {
            if (conveyorBelt.getConveyorBeltColor() == ConveyorBelt.ConveyorBeltColor.green) {
                this.tileAngle = switch (conveyorBelt.getHeading()) {
                    case SOUTH -> 0;
                    case WEST -> 90;
                    case NORTH -> 180;
                    case EAST -> 270;
                };
                this.setStyle("-fx-background-image: url('graphics/ConveyerBelt_Green.png'); -fx-background-size: " +
                        SPACE_HEIGHT + " " + SPACE_WIDTH + "; -fx-rotate: " + tileAngle + ";");
            } else {
                this.tileAngle = switch (conveyorBelt.getHeading()) {
                    case SOUTH -> 0;
                    case WEST -> 90;
                    case NORTH -> 180;
                    case EAST -> 270;
                };
                this.setStyle("-fx-background-image: url('graphics/ConveyorBelt_Blue.png'); -fx-background-size: " +
                        SPACE_HEIGHT + " " + SPACE_WIDTH + "; -fx-rotate: " + tileAngle + ";");
            }

        } else if (space.getActions().size() > 0 && space.getActions().get(0) instanceof Gear gear) {
            switch (gear.getDirection()) {
                case LEFT -> this.setStyle("-fx-background-image: url('graphics/GearLeft.PNG'); -fx-background-size: " +
                        SPACE_HEIGHT + " " + SPACE_WIDTH + ";");
                case RIGHT -> this.setStyle("-fx-background-image: url('graphics/GearRight.PNG'); -fx-background-size: " +
                        SPACE_HEIGHT + " " + SPACE_WIDTH + ";");
            }
        }
        else if (space.getActions().size() > 0 && space.getActions().get(0) instanceof Antenna antenna) {
            this.tileAngle = switch (antenna.getHeading()) {
                case NORTH -> 0;
                case EAST -> 90;
                case WEST -> 180;
                case SOUTH -> 270;
            };
            this.setStyle("-fx-background-image: url('graphics/Antenna.png'); -fx-background-size: " +
                    SPACE_HEIGHT + " " + SPACE_WIDTH + "; -fx-rotate: " + tileAngle + ";");
        }
        // updatePlayer();

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    private void updatePlayer() {
        this.getChildren().clear();

        Player player = space.getPlayer();
        if (player != null) {
            Polygon arrow = new Polygon(0.0, 0.0,
                    10.0, 20.0,
                    20.0, 0.0);
            try {
                arrow.setFill(Color.valueOf(player.getColor()));
            } catch (Exception e) {
                arrow.setFill(Color.MEDIUMPURPLE);
            }

            arrow.setRotate((90 * player.getHeading().ordinal()) % 360);
            this.getChildren().add(arrow);
        }
    }

    /**
     * Draws the walls on the gameboard.
     */
    private void updateWalls() {
        ImagePattern wall2 = new ImagePattern(new Image("graphics/Wall.png"));

        for (Heading wall : space.getWalls()) {
            Rectangle rectangle =
                    new Rectangle(0.0, 0.0, SPACE_WIDTH, SPACE_HEIGHT);
            rectangle.setFill(wall2);

            int angle = switch (wall) {
                case SOUTH -> 0;
                case WEST -> 90;
                case NORTH -> 180;
                case EAST -> -90;
            };
            rectangle.setRotate(angle - this.tileAngle);
            rectangle.toFront();
            this.getChildren().add(rectangle);
        }
    }

    @Override
    public void updateView(Subject subject) {
        this.getChildren().clear();

        if (subject == this.space) {
            updatePlayer();
            updateWalls();
        }
    }
}
