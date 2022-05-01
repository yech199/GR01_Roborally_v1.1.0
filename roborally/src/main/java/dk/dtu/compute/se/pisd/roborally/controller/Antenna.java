package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.util.Tuple;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Antenna extends FieldAction {
    private Heading heading;

    @Override
    public boolean doAction(GameController gameController, Space space) {
        if (space.getActions().size() > 0) {
            List<Player> players = gameController.board.getPlayers();
            List<Tuple<Player, Double>> distances = new ArrayList<>();

            int antenna_x = space.x;
            int antenna_y = space.y;

            for (Player player : players) {
                var playerSpace = player.getSpace();
                int player_x = playerSpace.x;
                int player_y = playerSpace.y;

                double distance = Math.sqrt(Math.pow(player_x - antenna_x, 2) + Math.pow(player_y - antenna_y, 2));
                distances.add(new Tuple<>(player, distance));
            }

            distances.sort(Comparator.comparing(Tuple::right));

            double prev_dist = 0;
            for (var tuple : distances) {
                if (prev_dist == tuple.right()) {

                }
            }




        }




        return false;
    }

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }
}
