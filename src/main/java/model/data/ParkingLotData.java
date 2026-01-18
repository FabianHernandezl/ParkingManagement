package model.data;

import java.util.ArrayList;
import model.entity.ParkingLot;
import model.entity.Space;
import model.entity.Vehicle;

public class ParkingLotData {

    public int registerVehicleInParkingLot(Vehicle vehicle, ParkingLot parkingLot) {

        // Validaciones básicas
        if (parkingLot == null || vehicle == null) {
            return 0;
        }

        Space[] spaces = parkingLot.getSpaces();

        // 🚨 ESTE ERA EL PROBLEMA
        if (spaces == null || spaces.length == 0) {
            System.out.println("El parqueo no tiene espacios inicializados");
            return 0;
        }

        ArrayList<Vehicle> vehiclesInParkingLot = parkingLot.getVehicles();

        if (vehiclesInParkingLot == null) {
            vehiclesInParkingLot = new ArrayList<>();
            parkingLot.setVehicles(vehiclesInParkingLot);
        }

        boolean hasDisability = vehicle.getClient().isIsPreferential();
        int vehicleTypeId = vehicle.getVehicleType().getId();

        for (Space space : spaces) {

            if (space == null || space.isSpaceTaken()) {
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

            // ✅ Registrar vehículo
            vehiclesInParkingLot.add(vehicle);
            space.setSpaceTaken(true);

            return space.getId(); // ← retorno inmediato
        }

        // No se encontró espacio
        return 0;
    }
}
