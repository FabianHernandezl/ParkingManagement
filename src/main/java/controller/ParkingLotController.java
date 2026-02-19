package controller;

import java.util.ArrayList;
import model.data.ParkingLotData;
import model.entities.ParkingLot;
import model.entities.Vehicle;
import model.entities.Space;
import model.entities.VehicleType;

public class ParkingLotController {

    private ParkingLotData parkingLotData = new ParkingLotData();

    // Registrar parqueo simple o con espacios opcionales
    public ParkingLot registerParkingLot(
            String name,
            int totalSpaces,
            int disabledSpaces,
            int preferentialSpaces,
            int motorcycleSpaces,
            int truckSpaces, int bicycleSpaces) {

        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setId(parkingLotData.findLastIdNumberOfParkingLot() + 1);
        parkingLot.setName(name);
        parkingLot.setNumberOfSpaces(totalSpaces);

        Space[] spaces = createSpacesForParkingLot(
                totalSpaces,
                preferentialSpaces,
                motorcycleSpaces,
                truckSpaces, bicycleSpaces
        );

        parkingLot.setSpaces(spaces);

        return parkingLotData.addParkingLot(parkingLot);
    }

    public ParkingLot registerParkingLot(String name,
            int totalSpaces,
            int disabledSpaces,
            int preferentialSpaces,
            int motorcycleSpaces) {

        return registerParkingLot(
                name,
                totalSpaces,
                disabledSpaces,
                preferentialSpaces,
                motorcycleSpaces,
                0, // truck
                0 // bicycle
        );
    }

    private Space[] createSpacesForParkingLot(
            int total,
            int preferential,
            int motorcycle,
            int truck, int bicycle) {

        Space[] spaces = new Space[total];
        int spaceId = 1;
        int index = 0;

        // Preferenciales
        for (int i = 0; i < preferential; i++) {
            spaces[index++] = new Space(spaceId++, true, false,
                    new VehicleType(1, "Automóvil", 4, 5.0f));
        }

        // Motocicletas
        for (int i = 0; i < motorcycle; i++) {
            spaces[index++] = new Space(spaceId++, false, false,
                    new VehicleType(2, "Motocicleta", 2, 2.5f));
        }

        // Camiones
        for (int i = 0; i < truck; i++) {
            spaces[index++] = new Space(spaceId++, false, false,
                    new VehicleType(4, "Camión", 6, 8.0f));
        }
        // Bicicletas
        for (int i = 0; i < bicycle; i++) {
            spaces[index++] = new Space(spaceId++, false, false,
                    new VehicleType(5, "Bicicleta", 2, 1.5f));
        }

        // Lo que quede → Autos normales
        while (index < total) {
            spaces[index++] = new Space(spaceId++, false, false,
                    new VehicleType(1, "Automóvil", 4, 5.0f));
        }

        return spaces;
    }

    public int registerVehicleInParkingLot(Vehicle vehicle, ParkingLot parkingLot) {
        if (vehicle == null || parkingLot == null || parkingLot.getSpaces() == null) {
            return 0;
        }

        boolean hasDisability = vehicle.hasPreferentialClient();
        int vehicleTypeId = vehicle.getVehicleType().getId();

        for (Space space : parkingLot.getSpaces()) {
            if (space.isSpaceTaken()) {
                continue;
            }
            if (hasDisability && !space.isDisabilityAdaptation()) {
                continue;
            }
            if (space.getVehicleType() != null && space.getVehicleType().getId() != vehicleTypeId) {
                continue;
            }

            space.setSpaceTaken(true);
            space.setVehicle(vehicle);
            parkingLotData.updateParkingLot(parkingLot);
            return space.getId();
        }

        return 0;
    }

    public void removeVehicleFromParkingLot(Vehicle vehicle, ParkingLot parkingLot) {
        parkingLotData.removeVehicleFromParkingLot(vehicle, parkingLot);
    }

    public ParkingLot findParkingLotById(int id) {
        return parkingLotData.findParkingLotById(id);
    }

    public ParkingLot findParkingLotByName(String name) {
        return parkingLotData.findParkingLotByName(name);
    }

    public ArrayList<ParkingLot> getAllParkingLots() {
        return parkingLotData.getAllParkingLots();
    }

    public String updateParkingLot(int id, ParkingLot newParkingLot) {
        if (parkingLotData.findParkingLotById(id) != null) {
            parkingLotData.updateParkingLot(newParkingLot);
            return "El parqueo fue actualizado correctamente";
        } else {
            return "El parqueo no se actualizó porque no se encontró en la base de datos.";
        }
    }

    public String removeParkingLot(int id) {
        ParkingLot lot = parkingLotData.findParkingLotById(id);
        if (lot != null) {
            parkingLotData.deleteParkingLot(lot);
            return "El parqueo se eliminó correctamente";
        } else {
            return "El parqueo no se eliminó porque no se encontró en la base de datos.";
        }
    }

}
