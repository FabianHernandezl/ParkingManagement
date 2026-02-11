package model.entities;

import java.util.ArrayList;

/**
 *
 * @author cami
 */
public class ParkingLot {

    private int id;
    private String name;
    private int numberOfSpaces;

    private ArrayList<Vehicle> vehicles;
    private Space[] spaces;

    public ParkingLot() {
    }

    public ParkingLot(int id, String name, int numberOfSpaces, ArrayList<Vehicle> vehicles, Space[] spaces) {
        this.id = id;
        this.name = name;
        this.numberOfSpaces = numberOfSpaces;
        this.vehicles = vehicles;
        this.spaces = spaces;
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

    public ArrayList<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(ArrayList<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public Space[] getSpaces() {
        return spaces;
    }

    public void setSpaces(Space[] spaces) {
        this.spaces = spaces;
    }

    @Override
    public String toString() {
        return " ID: " + id
                + " | " + name
                + " | Número de espacios: " + numberOfSpaces
                + " | Vehiculos: " + vehicles
                + " | Espacios: " + spaces;
    }

    public int getOccupiedSpaces() {
        int count = 0;
        if (spaces != null) {
            for (Space s : spaces) {
                if (s.isSpaceTaken()) {
                    count++;
                }
            }
        }
        return count;
    }
}
