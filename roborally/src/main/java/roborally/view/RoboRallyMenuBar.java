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

import roborally.controller.AppController;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class RoboRallyMenuBar extends MenuBar {

    private AppController appController;

    private Menu localMenu;
    private Menu serverMenu;

    private MenuItem saveGame;

    private MenuItem update;

    private MenuItem newGame;
    private MenuItem saveGame;
    private MenuItem serverGame;
    private MenuItem saveServerGame;
    private MenuItem joinGame;
    private MenuItem loadGame;
    private MenuItem stopGame;
    private MenuItem leaveGame;
    private MenuItem exitApp;

    public RoboRallyMenuBar(AppController appController) {
        this.appController = appController;

        /**
         * LOCAL ACTIONS MENU
         */

        localMenu = new Menu("Local");
        this.getMenus().add(localMenu);

        newGame = new MenuItem("New Game");
        newGame.setOnAction(e -> this.appController.newGame());
        localMenu.getItems().add(newGame);

        stopGame = new MenuItem("Stop Game");
        stopGame.setOnAction(e -> this.appController.stopGame());
        localMenu.getItems().add(stopGame);

        saveGame = new MenuItem("Save Game");
        saveGame.setOnAction(e -> this.appController.saveGame());
        localMenu.getItems().add(saveGame);

        loadGame = new MenuItem("Load Game");
        loadGame.setOnAction(e -> this.appController.loadGame());
        localMenu.getItems().add(loadGame);

        exitApp = new MenuItem("Exit");
        exitApp.setOnAction(e -> this.appController.exit());
        localMenu.getItems().add(exitApp);

        localMenu.setOnShowing(e -> update());
        localMenu.setOnShown(e -> this.updateBounds());


        /**
         * SERVER ACTIONS MENU
         */
        serverMenu = new Menu("Server");
        this.getMenus().add(serverMenu);

        serverGame = new MenuItem("Create Server Game");
        serverGame.setOnAction(e -> this.appController.createServerGame());
        serverMenu.getItems().add(serverGame);

        joinGame = new MenuItem("Join Game");
        joinGame.setOnAction(e -> this.appController.joinGame());
        serverMenu.getItems().add(joinGame);

        saveServerGame = new MenuItem("Save Server game");
        saveServerGame.setOnAction(e -> this.appController.saveServerGame());
        serverMenu.getItems().add(saveServerGame);

        leaveGame = new MenuItem("Leave game");
        leaveGame.setOnAction(e -> this.appController.leaveGame());
        serverMenu.getItems().add(leaveGame);

        exitApp = new MenuItem("Exit");
        exitApp.setOnAction(e -> this.appController.exit());
        serverMenu.getItems().add(exitApp);

        update = new MenuItem("Update");
        update.setOnAction(e -> this.appController.updateServerGame());
        serverMenu.getItems().add(update);

        serverMenu.setOnShowing(e -> update());
        serverMenu.setOnShown(e -> this.updateBounds());


        update();
    }

    public void update() {
        // TODO Hide server menu when local game is running and vice versa
        if (appController.isGameRunning()) {
            newGame.setVisible(false);
            stopGame.setVisible(true);
            saveGame.setVisible(true);
            loadGame.setVisible(false);
            //leaveGame.setVisible(true);
            saveServerGame.setVisible(true);
        }
        else {
            newGame.setVisible(true);
            stopGame.setVisible(false);
            saveGame.setVisible(false);
            loadGame.setVisible(true);
            //leaveGame.setVisible(false);
            //saveServerGame.setVisible(false);
        }
    }

}
