package Controller;

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

        Space available = findAvailableSpace(client, vehicle);

        if (available == null) {
            return "Error: No hay espacios disponibles para este vehículo/cliente";
        }

        ClientData clientData = new ClientData();
        Client existingClient = clientData.findClientById(client.getId());

        if (existingClient == null) {
            clientData.addClient(client);
        } else {
            client = existingClient;
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
        String vehicleType = vehicle.getVehicleType().getDescription().toLowerCase();

        for (ParkingLot lot : parkingLots) {
            if (lot.getSpaces() == null) {
                continue;
            }

            for (Space s : lot.getSpaces()) {
                if (s == null || s.isSpaceTaken() || s.getVehicleType() == null) {
                    continue;
                }

                String spaceType = s.getVehicleType().getDescription().toLowerCase();

                // ESPACIOS PARA DISCAPACIDAD
                if (s.isDisabilityAdaptation()) {
                    if (client.isIsPreferential()) {
                        // Solo clientes preferenciales pueden usar este espacio
                        return s;
                    } else {
                        continue; // ignorar este espacio para clientes normales
                    }
                }

                // ESPACIOS PARA MOTOCICLETA
                if (spaceType.contains("motocicleta") && vehicleType.contains("motocicleta")) {
                    return s;
                }

                // ESPACIOS NORMALES PARA CARROS Y CAMIONES
                if (!spaceType.contains("motocicleta") && !vehicleType.contains("motocicleta")) {

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

                    return parkingLotData.updateParkingLot(parkingLot);
                }
            }
        }
        return false;
    }

    public boolean releaseSpace(int id) {
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
                    return updateSpaceInParkingLot(s);
                }
            }
        }
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
}
