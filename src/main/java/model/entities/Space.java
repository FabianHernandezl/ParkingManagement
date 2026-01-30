package model.entities;

import java.util.Date;

/**
 *
 * @author fabian
 */
public class Space {

    private int id;
    private boolean disabilityAdaptation;
    private boolean spaceTaken;
    private VehicleType vehicleType;
    private Date entryTime;
    private boolean available;

    private Client client;
    private Vehicle vehicle;

    public Space(int id, boolean disabilityAdaptation, boolean spaceTaken, VehicleType vehicleType) {
        this.id = id;
        this.disabilityAdaptation = disabilityAdaptation;
        this.spaceTaken = spaceTaken;
        this.vehicleType = vehicleType;

    }

    public Space(int id, boolean disabilityAdaptation, boolean spaceTaken, VehicleType vehicleType, Date entryTime, Client client, Vehicle vehicle, boolean available) {
        this.id = id;
        this.disabilityAdaptation = disabilityAdaptation;
        this.spaceTaken = spaceTaken;
        this.vehicleType = vehicleType;
        this.entryTime = entryTime;
        this.client = client;
        this.vehicle = vehicle;
        this.available = available;
    }

    public Space() {

    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the disabilityAdaptation
     */
    public boolean isDisabilityAdaptation() {
        return disabilityAdaptation;
    }

    /**
     * @param disabilityAdaptation the disabilityAdaptation to set
     */
    public void setDisabilityAdaptation(boolean disabilityAdaptation) {
        this.disabilityAdaptation = disabilityAdaptation;
    }

    /**
     * @return the spaceTaken
     */
    public boolean isSpaceTaken() {
        return spaceTaken;
    }

    /**
     * @param spaceTaken the spaceTaken to set
     */
    public void setSpaceTaken(boolean spaceTaken) {
        this.spaceTaken = spaceTaken;
    }

    /**
     * @return the vehicleType
     */
    public VehicleType getVehicleType() {
        return vehicleType;
    }

    /**
     * @param vehicleType the vehicleType to set
     */
    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Date getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(Date entryTime) {
        this.entryTime = entryTime;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "Espacio #" + id + (spaceTaken ? " (Ocupado)" : " (Libre)");
    }

}
