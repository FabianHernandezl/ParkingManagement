/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import java.util.ArrayList;
import java.util.Date;
import model.data.SpaceData;
import model.entities.Client;
import model.entities.Space;
import model.entities.Vehicle;

/**
 *
 * @author jimen
 */
public class SpaceController {

    private SpaceData spaceData;

    public SpaceController() {
        spaceData = new SpaceData();
    }

    public String registerSpace(int id, boolean disability, boolean taken) {
        Space space = new Space();
        space.setId(id);
        space.setDisabilityAdaptation(disability);
        space.setSpaceTaken(taken);
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

    public boolean occupySpace(int id, Client client, Vehicle vehicle) {
        Space space = spaceData.findSpaceById(id);
        boolean result = false;

        if (space != null && !space.isSpaceTaken()) {
            // Validar compatibilidad básica
            if (space.isDisabilityAdaptation() && !client.isIsPreferential()) {
                return false;
            }

            space.setSpaceTaken(true);
            space.setClient(client);
            space.setVehicle(vehicle);
            space.setEntryTime(new Date());
            spaceData.updateSpace(space);
            result = true;
        }

        return result;
    }

    public boolean releaseSpace(int id) {
        Space space = spaceData.findSpaceById(id);
        boolean result = false;

        if (space != null && space.isSpaceTaken()) {
            space.setSpaceTaken(false);
            space.setClient(null);
            space.setVehicle(null);
            space.setEntryTime(null);
            spaceData.updateSpace(space);
            result = true;
        }

        return result;
    }

    // Método para calcular tarifa
    public double calculateFee(Space space) {
        if (space.getEntryTime() == null || space.getVehicle() == null
                || space.getVehicle().getVehicleType() == null) {
            return 0.0;
        }

        long diff = System.currentTimeMillis() - space.getEntryTime().getTime();
        long hours = Math.max(1, diff / (1000 * 60 * 60));
        float hourlyRate = space.getVehicle().getVehicleType().getFee();

        return hours * hourlyRate;
    }
}
