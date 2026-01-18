package model.data;

import java.util.ArrayList;
import model.entity.ParkingLot;

/**
 *
 * @author fabian
 */
public class ParkingLotData {

    //Descomentar una vez que estén las clases que faltan
    /*
    public int registerVehicleInParkingLot(Vehicle vehicle, ParkingLot parkingLot) {

        ArrayList<Vehicle> vehiclesInParkingLot = parkingLot.getVehicles();
        Space[] spaces = parkingLot.getSpaces();
        int spaceId = 0;

        boolean hasDisability = vehicle.getCustomer().isDisabilityPresented();
        int vehicleTypeId = vehicle.getVehicleType().getId();

        for (Space space : spaces) {

            if (space.isSpaceTaken()) {

                
                //Salta el resto del ciclo actual
                /Pasa inmediatamente a la siguiente iteración
                 
                continue;
            }

            if (space.getVehicleType().getId() != vehicleTypeId) {
                continue;
            }

            if (hasDisability && !space.isDisabilityAdaptation()) {
                continue;
            }

            if (!hasDisability && space.isDisabilityAdaptation()) {
                continue;
            }

            // Registrar vehículo
            vehiclesInParkingLot.add(vehicle);
            space.setSpaceTaken(true);
            spaceId = space.getId();
            break;
        }

        return spaceId;
    }
     */
}
