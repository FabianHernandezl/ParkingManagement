package model.entity;

/**
 *
 * @author fabian
 */
public class VehicleType {

    private int id;
    private String description;
    private int numberOfTires;
    private float fee;

    public VehicleType() {

    }

    public VehicleType(int id, String description, int numberOfTires, float fee) {

        this.id = id;
        this.description = description;
        this.numberOfTires = numberOfTires;
        this.fee = fee;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNumberOfTires() {
        return numberOfTires;
    }

    public void setNumberOfTires(byte numberOfTires) {
        this.numberOfTires = numberOfTires;
    }

    public float getFee() {
        return fee;
    }

    public void setFee(float fee) {
        this.fee = fee;
    }

    @Override
    public String toString() {
        return "VehicleType{" + "id=" + id + ", description=" + description + ", numberOfTires=" + numberOfTires + ", fee=" + fee + '}';
    }

}
