package model.dto;

import java.util.List;

public class ParkingLotDTO {

    private int id;
    private String name;
    private int numberOfSpaces;
    private List<SpaceDTO> spaces;

    public ParkingLotDTO() {
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

    public List<SpaceDTO> getSpaces() {
        return spaces;
    }

    public void setSpaces(List<SpaceDTO> spaces) {
        this.spaces = spaces;
    }
}
