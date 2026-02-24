package controller;

import java.util.ArrayList;
import model.data.ParkingLotData;
import model.entities.Administrator;
import model.entities.Clerk;
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

        // Asignar la referencia del parqueo a cada espacio
        for (Space space : spaces) {
            space.setParkingLot(parkingLot);
        }

        parkingLot.setSpaces(spaces);

        ParkingLot saved = parkingLotData.addParkingLot(parkingLot);

        // 🔥 GUARDAR INMEDIATAMENTE
        parkingLotData.saveParkingLots();
        parkingLotData.saveParkingLotsAsTxt();

        return saved;
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
            int truck,
            int bicycle) {

        Space[] spaces = new Space[total];
        int spaceId = 1;
        int index = 0;

        // Preferenciales (Automóvil)
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
                    new VehicleType(3, "Camión", 4, 8.0f));
        }

        // Bicicletas
        for (int i = 0; i < bicycle; i++) {
            spaces[index++] = new Space(spaceId++, false, false,
                    new VehicleType(4, "Bicicleta", 2, 1.5f));
        }

        // Lo restante → Autos normales
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

            // 🔥 Si NO es preferencial → NO puede usar espacio adaptado
            if (!hasDisability && space.isDisabilityAdaptation()) {
                continue;
            }

            // 🔥 Si SÍ es preferencial → SOLO puede usar adaptado
            if (hasDisability && !space.isDisabilityAdaptation()) {
                continue;
            }

            // Validar tipo de vehículo
            if (space.getVehicleType() != null
                    && space.getVehicleType().getId() != vehicleTypeId) {
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
        System.out.println("--- removeVehicleFromParkingLot ---");
        System.out.println("Vehículo: " + (vehicle != null ? vehicle.getPlate() : "null"));
        System.out.println("Parqueo: " + (parkingLot != null ? parkingLot.getName() : "null"));

        parkingLotData.removeVehicleFromParkingLot(vehicle, parkingLot);

        parkingLotData.saveParkingLots();
        parkingLotData.saveParkingLotsAsTxt();
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

            // 🔥 FORZAR GUARDADO DESPUÉS DE ACTUALIZAR
            parkingLotData.saveParkingLots();
            parkingLotData.saveParkingLotsAsTxt();

            return "El parqueo fue actualizado correctamente";
        } else {
            return "El parqueo no se actualizó porque no se encontró en la base de datos.";
        }
    }

    public String removeParkingLot(int id) {

        ParkingLot lot = parkingLotData.findParkingLotById(id);

        if (lot == null) {
            return "El parqueo no se encontró en la base de datos.";
        }

        // 1. Verificar si hay vehículos actualmente estacionados
        if (lot.getSpaces() != null) {
            for (Space space : lot.getSpaces()) {
                if (space.isSpaceTaken()) {
                    return "❌ No se puede eliminar el parqueo \"" + lot.getName() + "\".\n"
                            + "Aún hay vehículos estacionados en él.\n"
                            + "Primero registre la salida de todos los vehículos.";
                }
            }
        }

        // 2. Eliminar tarifas asociadas
        ParkingRateController rateController = new ParkingRateController();
        rateController.deleteAllRatesByParkingLot(id);

        // 3. Desasignar el parqueo de operarios
        ClerkController clerkController = new ClerkController();
        for (Clerk clerk : clerkController.getAllClerks()) {
            if (clerk.getParkingLot() != null) {
                boolean modified = clerk.getParkingLot()
                        .removeIf(p -> p.getId() == id);
                if (modified) {
                    clerkController.updateClerk(clerk);
                }
            }
        }

        // 4. Desasignar el parqueo de administradores
        AdministratorController adminController = new AdministratorController();
        for (Administrator admin : adminController.getAllAdministrators()) {
            if (admin.getParkingLot() != null) {
                boolean modified = admin.getParkingLot()
                        .removeIf(p -> p.getId() == id);
                if (modified) {
                    adminController.updateAdministrator(admin);
                }
            }
        }

        // 5. Eliminar el parqueo
        parkingLotData.deleteParkingLot(lot);
        parkingLotData.saveParkingLots();
        parkingLotData.saveParkingLotsAsTxt();

        return "✅ Parqueo \"" + lot.getName() + "\" eliminado correctamente.\n"
                + "Se eliminaron sus tarifas y se desasignó de operarios y administradores.";
    }

    // ========== MÉTODOS DE BÚSQUEDA DE ESPACIOS ==========
    /**
     * Busca un espacio por su ID en todos los parqueos
     */
    public Space findSpaceById(int spaceId) {
        ArrayList<ParkingLot> allParkingLots = getAllParkingLots();
        for (ParkingLot parkingLot : allParkingLots) {
            for (Space space : parkingLot.getSpaces()) {
                if (space.getId() == spaceId) {
                    space.setParkingLot(parkingLot);
                    return space;
                }
            }
        }
        return null;
    }

    /**
     * Busca un espacio por su número en un parqueo específico
     */
    public Space findSpaceByNumber(int parkingLotId, int spaceNumber) {
        ParkingLot parkingLot = findParkingLotById(parkingLotId);
        if (parkingLot != null) {
            for (Space space : parkingLot.getSpaces()) {
                if (space.getId() == spaceNumber) {
                    space.setParkingLot(parkingLot);
                    return space;
                }
            }
        }
        return null;
    }

    /**
     * Busca un espacio por su ID y el ID del parqueo
     */
    public Space findSpaceByParkingLotAndSpaceId(int parkingLotId, int spaceId) {
        ParkingLot parkingLot = findParkingLotById(parkingLotId);
        if (parkingLot != null) {
            for (Space space : parkingLot.getSpaces()) {
                if (space.getId() == spaceId) {
                    space.setParkingLot(parkingLot);
                    return space;
                }
            }
        }
        return null;
    }

    /**
     * Obtiene el nombre del parqueo para un espacio específico
     */
    public String getParkingLotNameBySpaceId(int spaceId) {
        Space space = findSpaceById(spaceId);
        if (space != null && space.getParkingLot() != null) {
            return space.getParkingLot().getName();
        }
        return "N/A";
    }
}
