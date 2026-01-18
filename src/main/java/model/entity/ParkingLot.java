
package model.entity;

import java.util.ArrayList;

/**
 *
 * @author cami
 */
public class ParkingLot {
    private int id;
    private String name;
    private int numberOfSpaces;
    
   // private ArrayList<Vehicle> vehicles;
   // private Space[] spaces;

    public ParkingLot() {
    }

    public ParkingLot(int id, String name, int numberOfSpaces) {
        this.id = id;
        this.name = name;
        this.numberOfSpaces = numberOfSpaces;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfSpaces() {
        return numberOfSpaces;
    }

    public void setNumberOfSpaces(int numberOfSpaces) {
        this.numberOfSpaces = numberOfSpaces;
    }
    
    
}
