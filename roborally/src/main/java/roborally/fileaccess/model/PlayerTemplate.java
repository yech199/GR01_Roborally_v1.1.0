package roborally.fileaccess.model;

import java.util.ArrayList;

public class PlayerTemplate {
    public String name;
    public String color;

    public int spaceX;
    public int spaceY;
    public String heading;

    public ArrayList<CommandCardFieldTemplate> playerCards = new ArrayList<>();

}