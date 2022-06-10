package roborally.controller;

import javafx.application.Platform;
import model.Phase;

import java.util.TimerTask;

/**
 * Updater periodically runs this task and calls the method to update view in a seperate thread
 * At the moment this doesn't work properly with the view and is therefore disabled.
 * @author Mads Sørensen (S215805)
 */
public class Updater extends TimerTask {

    public static AppController appController;

    @Override
    public void run() {
        if (appController.getAppState() == AppController.AppState.SERVER_GAME) {
            if(appController.getGameController().board.getPhase() == Phase.PROGRAMMING) {
                System.out.println("Update");
                try {
                    Platform.runLater(appController::updateServerView);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Failed to update");
                }
            }
        }
    }
}
