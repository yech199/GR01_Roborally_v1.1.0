package fileaccess.templates;

import java.util.ArrayList;

public class PlayerTemplate {
    public String name;
    public String color;

    public boolean active;
    public int playerId;
    public int spaceX;
    public int spaceY;
    public String heading;

    public ArrayList<CommandCardFieldTemplate> registers = new ArrayList<>();
    public ArrayList<CommandCardFieldTemplate> cards = new ArrayList<>();
}