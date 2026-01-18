
package model.entity;

/**
 *
 * @author fabian
 */
public class Space {

    private int id;
    private boolean disabilityAdaptation;
    private boolean spaceTaken;
    private VehicleType vehicleType;

    public Space(int id, boolean disabilityAdaptation, boolean spaceTaken, VehicleType vehicleType) {
        this.id = id;
        this.disabilityAdaptation = disabilityAdaptation;
        this.spaceTaken = spaceTaken;
        this.vehicleType = vehicleType;
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
    
    
    

}
