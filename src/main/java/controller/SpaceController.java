package controller;

import java.util.ArrayList;
import java.util.Date;
import model.data.ClientData;
import model.data.SpaceData;
import model.data.VehicleData;
import model.data.ParkingLotData;
import model.entities.Client;
import model.entities.Space;
import model.entities.Vehicle;
import model.entities.VehicleType;
import model.entities.ParkingLot;

/**
 * Handles the business logic related to parking spaces. Manages assignment,
 * release, and fee calculation.
 */
public class SpaceController {

    private SpaceData spaceData;
    private ParkingLotData parkingLotData;

    public SpaceController() {
        spaceData = new SpaceData();
        parkingLotData = new ParkingLotData();
    }

    // ================= REGISTRAR ESPACIO =================
    public String registerSpace(int id, boolean disability, boolean taken, String vehicleTypeDesc) {
        Space space = new Space();
        space.setId(id);
        space.setDisabilityAdaptation(disability);
        space.setSpaceTaken(taken);

        VehicleType type = new VehicleType();
        type.setDescription(vehicleTypeDesc);
        space.setVehicleType(type);

        return spaceData.insertSpace(space);
    }

    public ArrayList<Space> getAllSpaces() {
        ArrayList<Space> allSpaces = new ArrayList<>();
        ArrayList<ParkingLot> parkingLots = parkingLotData.getAllParkingLots();

        for (ParkingLot parkingLot : parkingLots) {
            if (parkingLot.getSpaces() != null) {
                for (Space space : parkingLot.getSpaces()) {
                    if (space != null) {
                        allSpaces.add(space);
                    }
                }
            }
        }

        return allSpaces;
    }

    // ================= OCUPAR ESPACIO AUTOMÁTICO =================
    public String occupySpace(Client client, Vehicle vehicle) {

        if (client == null || vehicle == null) {
            return "Error: Cliente o vehículo inválido.";
        }

        ClientData clientData = new ClientData();
        Client existingClient = clientData.findClientById(client.getId());

        if (existingClient == null) {
            clientData.addClient(client);
        } else {
            client = existingClient;
        }

        // 🔥 BUSCAR ESPACIO DESPUÉS de tener cliente correcto
        Space available = findAvailableSpace(client, vehicle);

        if (available == null) {
            return "Error: No hay espacios disponibles para este vehículo/cliente";
        }

        VehicleData vehicleData = new VehicleData();
        Vehicle existingVehicle = vehicleData.findVehicleByPlate(vehicle.getPlate());

        if (existingVehicle == null) {
            vehicle.addClient(client);
            vehicleData.insertVehicle(vehicle);
        } else {
            existingVehicle.addClient(client);
            vehicleData.updateVehicle(existingVehicle);
            vehicle = existingVehicle;
        }

        available.setSpaceTaken(true);
        available.setClient(client);
        available.setVehicle(vehicle);
        available.setEntryTime(new Date());

        boolean updated = updateSpaceInParkingLot(available);

        return updated ? "OK: Espacio asignado" : "Error crítico: No se pudo actualizar el parqueo.";
    }

    // ================= BUSCAR ESPACIO DISPONIBLE =================
    private Space findAvailableSpace(Client client, Vehicle vehicle) {

        ArrayList<ParkingLot> parkingLots = parkingLotData.getAllParkingLots();

        String vehicleType = vehicle.getVehicleType()
                .getDescription()
                .toLowerCase()
                .trim();

        for (ParkingLot lot : parkingLots) {

            if (lot.getSpaces() == null) {
                continue;
            }

            for (Space s : lot.getSpaces()) {

                if (s == null || s.isSpaceTaken()) {
                    continue;
                }

                // ================= CLIENTE PREFERENCIAL =================
                if (client.isIsPreferential()) {

                    if (s.isDisabilityAdaptation()) {
                        return s;
                    }

                    continue;
                }

                // ================= CLIENTE NORMAL =================
                if (s.isDisabilityAdaptation()) {
                    continue;
                }

                if (s.getVehicleType() == null) {
                    continue;
                }

                String spaceType = s.getVehicleType()
                        .getDescription()
                        .toLowerCase()
                        .trim();

                if (spaceType.equals(vehicleType)) {
                    return s;
                }
            }
        }

        return null;
    }

    // ================= ACTUALIZAR ESPACIO EN PARQUEO =================
    private boolean updateSpaceInParkingLot(Space updatedSpace) {

        ArrayList<ParkingLot> parkingLots = parkingLotData.getAllParkingLots();

        for (ParkingLot parkingLot : parkingLots) {

            if (parkingLot.getSpaces() == null) {
                continue;
            }

            Space[] spaces = parkingLot.getSpaces();

            for (int i = 0; i < spaces.length; i++) {

                if (spaces[i] != null && spaces[i].getId() == updatedSpace.getId()) {

                    spaces[i].setSpaceTaken(updatedSpace.isSpaceTaken());
                    spaces[i].setClient(updatedSpace.getClient());
                    spaces[i].setVehicle(updatedSpace.getVehicle());
                    spaces[i].setEntryTime(updatedSpace.getEntryTime());
                    spaces[i].setDisabilityAdaptation(updatedSpace.isDisabilityAdaptation());
                    spaces[i].setVehicleType(updatedSpace.getVehicleType());
                    spaces[i].setParkingLot(updatedSpace.getParkingLot());

                    // 🔥 updateParkingLot ya guarda internamente
                    boolean updated = parkingLotData.updateParkingLot(parkingLot);

                    if (updated) {
                        // Guardado adicional por si acaso (no está mal)
                        parkingLotData.saveParkingLots();
                        parkingLotData.saveParkingLotsAsTxt();
                    }

                    return updated;
                }
            }
        }

        return false;
    }

    /**
     * 🔥 VERSIÓN CORREGIDA - Libera espacio y guarda
     */
    public boolean releaseSpace(int id) {
        System.out.println("=== SpaceController.releaseSpace ===");
        System.out.println("Liberando espacio ID: " + id);

        for (ParkingLot lot : parkingLotData.getAllParkingLots()) {
            if (lot.getSpaces() == null) {
                continue;
            }

            for (Space s : lot.getSpaces()) {
                if (s != null && s.getId() == id && s.isSpaceTaken()) {

                    s.setSpaceTaken(false);
                    s.setClient(null);
                    s.setVehicle(null);
                    s.setEntryTime(null);

                    boolean updated = updateSpaceInParkingLot(s);

                    if (updated) {
                        System.out.println("✅ Espacio " + id + " liberado permanentemente");
                        return true;
                    } else {
                        System.out.println("❌ Error al guardar liberación del espacio " + id);
                        return false;
                    }
                }
            }
        }

        System.out.println("No se encontró espacio ocupado con ID: " + id);
        return false;
    }

    public double calculateFee(Space space) {
        if (space.getEntryTime() == null || space.getVehicle() == null
                || space.getVehicle().getVehicleType() == null) {
            return 0.0;
        }

        long diff = System.currentTimeMillis() - space.getEntryTime().getTime();
        long hours = Math.max(1, diff / (1000 * 60 * 60));
        float rate = space.getVehicle().getVehicleType().getFee();

        return hours * rate;
    }

    public Space[] getSpacesByParkingLot(int parkingLotId) {
        ArrayList<ParkingLot> allParkings = parkingLotData.getAllParkingLots();

        for (ParkingLot lot : allParkings) {
            if (lot.getId() == parkingLotId && lot.getSpaces() != null) {
                return lot.getSpaces();
            }
        }
        return new Space[0];
    }

}
