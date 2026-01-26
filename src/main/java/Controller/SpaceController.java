package Controller;

import java.util.ArrayList;
import java.util.Date;
import model.data.SpaceData;
import model.entities.Client;
import model.entities.Space;
import model.entities.Vehicle;

/**
 * Controlador para la gestión de espacios de parqueo. Se comunica con SpaceData
 * para persistencia en el archivo de parqueos.
 */
public class SpaceController {

    private SpaceData spaceData;

    public SpaceController() {
        // SpaceData ahora maneja la lógica de buscar dentro de la estructura de Parkings
        spaceData = new SpaceData();
    }

    public String registerSpace(int id, boolean disability, boolean taken) {
        Space space = new Space();
        space.setId(id);
        space.setDisabilityAdaptation(disability);
        space.setSpaceTaken(taken);
        // Nota: Asegúrate de que insertSpace en SpaceData soporte la estructura de Parkings
        return spaceData.insertSpace(space);
    }

    public ArrayList<Space> getAllSpaces() {
        return spaceData.getAllSpaces();
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
        Space space = spaceData.findSpaceById(id);

        // 1. Validar existencia
        if (space == null) {
            return "Error: El espacio #" + id + " no existe en la base de datos de parqueos.";
        }

        // 2. Validar disponibilidad
        if (space.isSpaceTaken()) {
            return "Error: El espacio ya se encuentra ocupado.";
        }

        // 3. Validar Ley 7600 (Accesibilidad)
        if (space.isDisabilityAdaptation() && !client.isIsPreferential()) {
            return "Error: Este espacio es exclusivo para personas con discapacidad (Ley 7600).";
        }

        // 4. Sincronizar datos del objeto
        space.setSpaceTaken(true);
        space.setClient(client);
        space.setVehicle(vehicle);
        space.setEntryTime(new Date());

        // 5. Persistir cambios a través de SpaceData (que actualiza el JSON de Parkings)
        boolean updated = spaceData.updateSpace(space);

        return updated ? "OK" : "Error crítico: No se pudo actualizar el archivo de datos.";
    }

    /**
     * Libera un espacio y limpia los datos del cliente y vehículo.
     */
    public boolean releaseSpace(int id) {
        Space space = spaceData.findSpaceById(id);
        if (space != null && space.isSpaceTaken()) {
            space.setSpaceTaken(false);
            space.setClient(null);
            space.setVehicle(null);
            space.setEntryTime(null);
            return spaceData.updateSpace(space);
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
