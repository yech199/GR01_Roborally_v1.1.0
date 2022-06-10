package fileaccess.model;

import java.util.ArrayList;

/**
 * Templates are used for serialization
 * @author Mark Nielsen
 */
public class PlayerTemplate {
    public String name;
    public String color;

    public boolean active;
    public int spaceX;
    public int spaceY;
    public String heading;

    public ArrayList<CommandCardFieldTemplate> registers = new ArrayList<>();
    public ArrayList<CommandCardFieldTemplate> cards = new ArrayList<>();
}