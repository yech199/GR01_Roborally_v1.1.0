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

import designpatterns.Subject;
import controller.GameController;
import model.Board;
import model.Player;
import javafx.scene.control.TabPane;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class PlayersView extends TabPane implements ViewObserver {

    private Board board;

    private PlayerView[] playerViews;


    // TODO Need to figure out what to do with server buttons
    /*private VBox buttonPanel;

    private Button finishButton;
    private Button executeButton;
    private Button stepButton;*/

    public PlayersView(GameController gameController) {
        board = gameController.board;

        this.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

        playerViews = new PlayerView[board.getPlayersNumber()];
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            playerViews[i] = new PlayerView(gameController, board.getPlayer(i));
            this.getTabs().add(playerViews[i]);
        }

        // XXX  the following buttons should actually not be on the tabs of the individual
        //      players, but on the PlayersView (view for all players). This should be
        //      refactored.

        /*finishButton = new Button("Finish Programming");
        finishButton.setOnAction(e -> gameController.finishProgrammingPhase());

        executeButton = new Button("Execute Program");
        executeButton.setOnAction(e -> gameController.executePrograms());

        stepButton = new Button("Execute Current Register");
        stepButton.setOnAction(e -> gameController.executeStep());

        buttonPanel = new VBox(finishButton, executeButton, stepButton);
        buttonPanel.setAlignment(Pos.CENTER_LEFT);
        buttonPanel.setSpacing(3.0);

        this.getChildren().add(buttonPanel);*/

        board.attach(this);
        update(board);
    }

    @Override
    public void updateView(Subject subject) {
        if (subject == board) {
            Player current = board.getCurrentPlayer();
            if (current == null) current = new Player(board, "", "a");
            this.getSelectionModel().select(board.getPlayerNumber(current));
        }
    }
}
