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
    private MenuItem finishProgramming;

    public RoboRallyMenuBar(AppController appController) {
        this.appController = appController;

        // ---------------------------------LOCAL ACTIONS MENU-------------------------------
        localMenu = new Menu("Local");
        this.getMenus().add(localMenu);

        newGame = new MenuItem("New Game");
        newGame.setOnAction(e -> {
            boolean isSuccessful = this.appController.newGame();
            if (isSuccessful) update();
        });
        localMenu.getItems().add(newGame);

        stopGame = new MenuItem("Stop Game");
        stopGame.setOnAction(e -> {
            boolean isSuccessful = this.appController.stopGame();
            if (isSuccessful) update();
        });
        localMenu.getItems().add(stopGame);

        saveGame = new MenuItem("Save Game");
        saveGame.setOnAction(e -> {
            this.appController.saveGame();
            update();
        });
        localMenu.getItems().add(saveGame);

        loadGame = new MenuItem("Load Game");
        loadGame.setOnAction(e -> {
            boolean isSuccessful = this.appController.loadGame();
            if (isSuccessful) update();

        });
        localMenu.getItems().add(loadGame);

        exitApp = new MenuItem("Exit");
        exitApp.setOnAction(e -> this.appController.exit());
        localMenu.getItems().add(exitApp);

        localMenu.setOnShowing(e -> update());
        localMenu.setOnShown(e -> this.updateBounds());


        // ---------------------------------SERVER ACTIONS MENU-------------------------------
        serverMenu = new Menu("Server");
        this.getMenus().add(serverMenu);

        serverGame = new MenuItem("Create Server Game");
        serverGame.setOnAction(e -> {
            boolean isSuccessful = this.appController.createServerGame();
            if (isSuccessful) update();
        });
        serverMenu.getItems().add(serverGame);

        joinGame = new MenuItem("Join Game");
        joinGame.setOnAction(e -> {
            boolean isSuccessful = this.appController.joinGame();
            if (isSuccessful) update();
        });
        serverMenu.getItems().add(joinGame);

        saveServerGame = new MenuItem("Save Server game");
        saveServerGame.setOnAction(e -> {
            this.appController.saveServerGame();
            update();
        });
        serverMenu.getItems().add(saveServerGame);

        leaveGame = new MenuItem("Leave game");
        leaveGame.setOnAction(e -> {
            this.appController.leaveServerGame();
            update();
        });
        serverMenu.getItems().add(leaveGame);

        exitApp = new MenuItem("Exit");
        exitApp.setOnAction(e -> this.appController.exit());
        serverMenu.getItems().add(exitApp);

        update = new MenuItem("Refresh View");
        update.setOnAction(e -> this.appController.updateServerView());
        serverMenu.getItems().add(update);

        finishProgramming = new MenuItem("Finish Programming");
        finishProgramming.setOnAction(e -> this.appController.submitPlayerCards());
        serverMenu.getItems().add(finishProgramming);

        serverMenu.setOnShowing(e -> update());
        serverMenu.setOnShown(e -> this.updateBounds());

        update();
    }

    public void update() {
        switch (appController.getAppState()) {
            case UNDECIDED -> {
                serverMenu.setVisible(true);
                localMenu.setVisible(true);

                newGame.setVisible(true);
                stopGame.setVisible(false);
                saveGame.setVisible(false);
                loadGame.setVisible(true);

                serverGame.setVisible(true);
                joinGame.setVisible(true);
                saveServerGame.setVisible(false);
                leaveGame.setVisible(false);
                update.setVisible(false);
            }
            case LOCAL_GAME -> {
                serverMenu.setVisible(false);

                newGame.setVisible(false);
                stopGame.setVisible(true);
                saveGame.setVisible(true);
                loadGame.setVisible(false);
            }
            case SERVER_GAME -> {
                localMenu.setVisible(false);

                serverGame.setVisible(false);
                joinGame.setVisible(false);
                saveServerGame.setVisible(true);
                leaveGame.setVisible(true);
                update.setVisible(true);
            }
        }
    }
}
