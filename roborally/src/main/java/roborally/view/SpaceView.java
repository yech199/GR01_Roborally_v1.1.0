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
package roborally.view;

import designpatterns.observer.Subject;
import model.boardElements.*;
import model.Heading;
import model.Player;
import model.Space;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
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
        else if (space.getActions().size() > 0 && space.getActions().get(0) instanceof PushPanel pushPanel) {
            this.tileAngle = switch (pushPanel.getHeading()) {
                case SOUTH -> 0;
                case WEST -> 90;
                case NORTH -> 180;
                case EAST -> 270;
            };
            this.setStyle(pushPanel.pushPanelStart + pushPanel.pushPanelInsert + pushPanel.pushPanelEnd +
                    SPACE_HEIGHT + " " + SPACE_WIDTH + "; -fx-rotate: " + tileAngle + ";");
        }
        else if(space.getActions().size() > 0 && space.getActions().get(0) instanceof Pit pit) {
            this.setStyle("-fx-background-image: url('graphics/Pit.png'); -fx-background-size: " +
                    SPACE_HEIGHT + " " + SPACE_WIDTH + ";");
        }
        else if(space.getActions().size() > 0 && space.getActions().get(0) instanceof Checkpoint checkpoint) {
            this.setStyle("-fx-background-image: url('graphics/Checkpoint"+ checkpoint.getCheckpointNumber() +".png'); -fx-background-size: " +
                    SPACE_HEIGHT + " " + SPACE_WIDTH + ";");
        }
        else if(space.getActions().size() > 0 && space.getActions().get(0) instanceof RebootToken rebootToken) {
            this.tileAngle = switch (rebootToken.getHeading()) {
                case SOUTH -> 0;
                case WEST -> 90;
                case NORTH -> 180;
                case EAST -> 270;
            };
            this.setStyle("-fx-background-image: url('graphics/RespawnToken.png'); -fx-background-size: " +
                    SPACE_HEIGHT + " " + SPACE_WIDTH + "; -fx-rotate: " + tileAngle + ";");
        }

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    private void updatePlayer() {
        this.getChildren().clear();

        Player player = space.getPlayer();
        if (!this.space.board.getPlayers().contains(player)) {
            return;
        }


        if (player != null) {
            //Polygon arrow = new Polygon(0.0, 0.0, 10.0, 20.0, 20.0, 0.0);
            ImagePattern playerImage = new ImagePattern(new Image("graphics/robots/" + player.getColor() + ".png"));
            Rectangle rectangle = new Rectangle(0.0, 0.0, SPACE_WIDTH, SPACE_HEIGHT);
            try {
                //arrow.setFill(Color.valueOf(player.getColor()));
                rectangle.setFill(playerImage);
            } catch (Exception e) {
                rectangle.setFill(Color.MEDIUMPURPLE);
            }

            int angle = switch (player.getHeading()) {
                case NORTH -> 0;
                case EAST -> 90;
                case SOUTH -> 180;
                case WEST -> 270;
            };

            // rectangle.setRotate((90 * player.getHeading().ordinal()) % 360);
            rectangle.setRotate(angle - this.tileAngle);
            rectangle.toFront();
            this.getChildren().add(rectangle);
        }
    }

    /**
     * Draws the walls on the gameboard.
     */
    private void updateWalls() {
        ImagePattern wall = new ImagePattern(new Image("graphics/Wall.png"));

        for (Heading wallHeading : this.space.getWalls()) {
            Rectangle rectangle =
                    new Rectangle(0.0, 0.0, SPACE_WIDTH, SPACE_HEIGHT);
            rectangle.setFill(wall);

            int angle = switch (wallHeading) {
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

        updatePlayer();

        if (subject == this.space) {
            updateWalls();
        }
    }
}
