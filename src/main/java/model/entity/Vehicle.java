package model.entity;

/**
 *
 * @author fabian
 */
public class Vehicle {

    private String plate;
    private String color;
    private String brand;
    private String model;
    private Client client;
    private VehicleType vehicleType;

    public Vehicle() {

    }

    public Vehicle(String plate, String color, String brand, String model, Client client, VehicleType vehicleType) {
        this.plate = plate;
        this.color = color;
        this.brand = brand;
        this.model = model;
        this.client = client;
        this.vehicleType = vehicleType;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    @Override
    public String toString() {
        return "Vehicle{" + "plate=" + plate + ", color=" + color + ", brand=" + brand + ", model=" + model + ", customer=" + client + ", vehicleType=" + vehicleType + '}';
    }

}
