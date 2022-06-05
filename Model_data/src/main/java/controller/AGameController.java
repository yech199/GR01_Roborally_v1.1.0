package controller;

import model.*;
import model.boardElements.Antenna;
import model.boardElements.RebootToken;
import org.jetbrains.annotations.NotNull;

abstract public class AGameController {
    final public Board board;
    protected Antenna antenna;
    protected Space antennaSpace;
    protected RebootToken rebootToken;
    protected Space rebootTokenSpace;

    public AGameController(@NotNull Board board) {
        this.board = board;

        // Finds antenna if it exists
        for (var x : board.getSpaces()) {
            if (antenna != null) break;
            for (var y : x) {
                if (antenna != null) break;
                for (var action : y.getActions()) {
                    if (action instanceof Antenna antenna) {
                        this.antenna = antenna;
                        this.antennaSpace = y;
                        break;
                    }
                }
            }
        }

        // Finds reboot if it exists
        for (var x : board.getSpaces()) {
            if (rebootToken != null) break;
            for (var y : x) {
                if (rebootToken != null) break;
                for (var action : y.getActions()) {
                    if (action instanceof RebootToken rebootToken) {
                        this.rebootToken = rebootToken;
                        this.rebootTokenSpace = y;
                        break;
                    }
                }
            }
        }
    }

    public abstract void moveCurrentPlayerToSpace(@NotNull Space space);

    public abstract void startProgrammingPhase();

    public abstract void finishProgrammingPhase();

    public abstract void executePrograms();

    public abstract void executeStep();

    public abstract void executeCommandOptionAndContinue(Command option);

    public abstract void moveForward(@NotNull Player player, Heading playerHeading);

    public abstract void moveXForward(@NotNull Player player, int moves);

    public abstract void turnRight(@NotNull Player player);

    public abstract void turnLeft(@NotNull Player player);

    public abstract boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target);

    public abstract void reboot(Player player);

    public abstract void Winner(Player player);
}
