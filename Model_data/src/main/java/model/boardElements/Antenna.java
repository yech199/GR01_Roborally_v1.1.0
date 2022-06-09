package model.boardElements;

import controller.AGameController;
import model.Heading;
import model.Player;
import model.Space;
import util.Tuple;

import java.util.ArrayList;
import java.util.List;

public class Antenna implements SpaceElement {
    private Heading heading;

    /**
     * Calculates the order of the turn of the players
     *
     * @param gameController the gameController of the respective game
     * @param space          the space this action should be executed for
     */
    @Override
    public void doAction(AGameController gameController, Space space) {
        List<Player> players = gameController.board.getPlayers();
        // Practically a Triple containing a Player, the distance between a player and the antenna
        // and the angle between the player and the antenna (depends on which way the antenna is looking)
        List<Tuple<Player, Tuple<Integer, Double>>> distAndAngles = new ArrayList<>();

        int antenna_x = space.x;
        int antenna_y = space.y;

        // Calculates the angle between the antenna and a point in the direction of the header
        var antennaAngle = switch (this.heading) {
            case NORTH -> new Tuple<>(antenna_x, antenna_y + 1);
            case EAST -> new Tuple<>(antenna_x + 1, antenna_y);
            case SOUTH -> new Tuple<>(antenna_x, antenna_y - 1);
            case WEST -> new Tuple<>(antenna_x - 1, antenna_y);
        };
        double headingAngle = Math.toDegrees(Math.atan2(antennaAngle.right() - antenna_y, antennaAngle.left() - antenna_x));
        if (headingAngle < 0) {
            headingAngle += 360;
        }


        for (Player player : players) {
            var playerSpace = player.getSpace();
            int player_x = playerSpace.x;
            int player_y = playerSpace.y;

            // Calculates the distance between the antenna and the player (in steps)
            int distance = Math.abs(player_x - antenna_x) + Math.abs(player_y - antenna_y);

            // Calculates the angle between the antenna and a player
            double angle = Math.toDegrees(Math.atan2(player_y - antenna_y, player_x - antenna_x));
            if (angle < 0) {
                angle += 360;
            }

            angle -= headingAngle;

            distAndAngles.add(new Tuple<>(player, new Tuple<>(distance, angle)));
        }

        // Sort on distance. IF distance is the same, sort on angles
        distAndAngles.sort((o1, o2) -> {
            int dist1 = o1.right().left();
            int dist2 = o2.right().left();
            double angle1 = o1.right().right();
            double angle2 = o2.right().right();

            int result = Integer.compare(dist1, dist2);
            if (result == 0) return Double.compare(angle1, angle2);
            return result;
        });

        // Set new player order
        for (int i = 0; i < players.size(); i++) {
            players.set(i, distAndAngles.get(i).left());
        }
        gameController.board.setPlayers(players);
    }

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }
}
