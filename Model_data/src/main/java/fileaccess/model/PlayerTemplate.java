package fileaccess.model;

import fileaccess.model.CommandCardFieldTemplate;

import java.util.ArrayList;

public class PlayerTemplate {
    public String name;
    public String color;

    public int spaceX;
    public int spaceY;
    public String heading;

    public ArrayList<CommandCardFieldTemplate> playerCards = new ArrayList<>();
    public ArrayList<CommandCardFieldTemplate> playerProgram = new ArrayList<>();


}