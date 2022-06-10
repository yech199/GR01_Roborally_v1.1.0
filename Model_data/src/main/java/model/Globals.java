package model;

import java.util.Arrays;
import java.util.List;

public class Globals {
    // Used by Player
    public static final int NO_REGISTERS = 5;
    public static final int NO_CARDS = 9;
    public static final int MAX_NO_PLAYERS = 6;

    // Used by AppController
    public static final List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);

    // Used by SpaceView
    public static final int SPACE_HEIGHT = 60; // 75;
    public static final int SPACE_WIDTH = 60; // 75;

    // Used by CardFieldView
    public static final int CARDFIELD_WIDTH = 65;
    public static final int CARDFIELD_HEIGHT = 100;
}
