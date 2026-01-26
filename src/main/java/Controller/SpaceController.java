package Controller;

import java.util.ArrayList;
import java.util.Date;
import model.data.ClientData;
import model.data.SpaceData;
import model.data.VehicleData;
import model.data.ParkingLotData;  // AÑADIR ESTO
import model.entities.Client;
import model.entities.Space;
import model.entities.Vehicle;
import model.entities.ParkingLot;  // AÑADIR ESTO

/**
 * Controlador para la gestión de espacios de parqueo. Se comunica con SpaceData
 * para persistencia en el archivo de parqueos.
 */
public class SpaceController {

    private SpaceData spaceData;
    private ParkingLotData parkingLotData;  // NUEVO: para acceder a parqueos

    public SpaceController() {
        spaceData = new SpaceData();
        parkingLotData = new ParkingLotData();  // Inicializar
    }

    public String registerSpace(int id, boolean disability, boolean taken) {
        Space space = new Space();
        space.setId(id);
        space.setDisabilityAdaptation(disability);
        space.setSpaceTaken(taken);
        return spaceData.insertSpace(space);
    }

    // MODIFICAR ESTE MÉTODO para obtener espacios desde los parqueos
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

    public Space findSpaceById(int id) {
        return spaceData.findSpaceById(id);
    }

    public String updateSpace(Space space) {
        boolean updated = spaceData.updateSpace(space);
        return updated ? "Espacio actualizado correctamente" : "No se pudo actualizar el espacio";
    }

    public String deleteSpace(int id) {
        boolean deleted = spaceData.deleteSpace(id);
        return deleted ? "Espacio eliminado correctamente" : "No se pudo eliminar el espacio";
    }

    /**
     * Método principal para asignar un vehículo a un espacio.
     *
     * @return "OK" si tuvo éxito, o un mensaje descriptivo del error.
     */
    public String occupySpace(int id, Client client, Vehicle vehicle) {
        System.out.println("=== DEBUG OCCUPY SPACE ===");
        System.out.println("Espacio ID: " + id);
        System.out.println("Cliente recibido: " + (client != null ? client.toString() : "null"));
        System.out.println("Vehículo recibido: " + (vehicle != null ? vehicle.toString() : "null"));

        // PRIMERO buscar en todos los parqueos
        Space space = findSpaceInAllParkingLots(id);

        if (space == null) {
            System.out.println("DEBUG: Espacio no encontrado en ningún parqueo");
            return "Error: El espacio #" + id + " no existe en la base de datos.";
        }

        System.out.println("DEBUG: Espacio encontrado en parqueo - Ocupado: " + space.isSpaceTaken());

        // Resto del código permanece igual...
        if (space.isSpaceTaken()) {
            System.out.println("DEBUG: Espacio ya ocupado");
            return "Error: El espacio ya se encuentra ocupado.";
        }

        if (space.isDisabilityAdaptation() && !client.isIsPreferential()) {
            System.out.println("DEBUG: Violación Ley 7600");
            return "Error: Este espacio es exclusivo para personas con discapacidad (Ley 7600).";
        }

        ClientData clientData = new ClientData();
        VehicleData vehicleData = new VehicleData();

        Client existingClient = clientData.findClientById(client.getId());
        if (existingClient == null) {
            System.out.println("DEBUG: Cliente no encontrado, registrando nuevo");
            clientData.addClient(client);
        } else {
            System.out.println("DEBUG: Cliente ya existe en base de datos");
        }

        Vehicle existingVehicle = vehicleData.findVehicleByPlate(vehicle.getPlate());
        if (existingVehicle == null) {
            System.out.println("DEBUG: Vehículo no encontrado, registrando nuevo");
            vehicle.addClient(client);
            vehicleData.insertVehicle(vehicle);
        } else {
            System.out.println("DEBUG: Vehículo ya existe en base de datos");
            existingVehicle.addClient(client);
            vehicleData.updateVehicle(existingVehicle);
            vehicle = existingVehicle;
        }

        space.setSpaceTaken(true);
        space.setClient(client);
        space.setVehicle(vehicle);
        space.setEntryTime(new Date());

        System.out.println("DEBUG: Space configurado:");
        System.out.println("  - Ocupado: " + space.isSpaceTaken());
        System.out.println("  - Cliente: " + (space.getClient() != null ? space.getClient().getName() : "null"));
        System.out.println("  - Vehículo: " + (space.getVehicle() != null ? space.getVehicle().getPlate() : "null"));
        System.out.println("  - Hora entrada: " + space.getEntryTime());

        // Aquí necesitamos actualizar el espacio en el parqueo correspondiente
        boolean updated = updateSpaceInParkingLot(space);

        System.out.println("DEBUG: updateSpaceInParkingLot resultó: " + updated);
        System.out.println("=== FIN DEBUG OCCUPY SPACE ===\n");

        return updated ? "OK" : "Error crítico: No se pudo actualizar el archivo de datos.";
    }

    // NUEVO MÉTODO: Buscar espacio en todos los parqueos
    private Space findSpaceInAllParkingLots(int spaceId) {
        ArrayList<ParkingLot> parkingLots = parkingLotData.getAllParkingLots();

        for (ParkingLot parkingLot : parkingLots) {
            if (parkingLot.getSpaces() != null) {
                for (Space space : parkingLot.getSpaces()) {
                    if (space != null && space.getId() == spaceId) {
                        return space;
                    }
                }
            }
        }
        return null;
    }

    // NUEVO MÉTODO: Actualizar espacio en el parqueo correspondiente
    private boolean updateSpaceInParkingLot(Space updatedSpace) {
        ArrayList<ParkingLot> parkingLots = parkingLotData.getAllParkingLots();

        for (ParkingLot parkingLot : parkingLots) {
            if (parkingLot.getSpaces() != null) {
                Space[] spaces = parkingLot.getSpaces();
                for (int i = 0; i < spaces.length; i++) {
                    if (spaces[i] != null && spaces[i].getId() == updatedSpace.getId()) {
                        // Actualizar el espacio
                        spaces[i] = updatedSpace;
                        // Guardar cambios en el parqueo
                        return parkingLotData.updateParkingLot(parkingLot);
                    }
                }
            }
        }
        return false;
    }

    /**
     * Libera un espacio y limpia los datos del cliente y vehículo.
     */
    public boolean releaseSpace(int id) {
        Space space = findSpaceInAllParkingLots(id);
        if (space != null && space.isSpaceTaken()) {
            space.setSpaceTaken(false);
            space.setClient(null);
            space.setVehicle(null);
            space.setEntryTime(null);
            return updateSpaceInParkingLot(space);
        }
        return false;
    }

    /**
     * Calcula la tarifa basada en el tipo de vehículo y el tiempo transcurrido.
     */
    public double calculateFee(Space space) {
        if (space.getEntryTime() == null || space.getVehicle() == null
                || space.getVehicle().getVehicleType() == null) {
            return 0.0;
        }

        // Diferencia en milisegundos
        long diff = System.currentTimeMillis() - space.getEntryTime().getTime();

        // Convertir a horas (mínimo 1 hora)
        long hours = Math.max(1, diff / (1000 * 60 * 60));
        float hourlyRate = space.getVehicle().getVehicleType().getFee();

        return hours * hourlyRate;
    }
}
