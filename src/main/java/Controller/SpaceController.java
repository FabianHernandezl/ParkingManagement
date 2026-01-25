/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import java.util.ArrayList;
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
        String message = "";

        if (updated) {
            message = "Espacio actualizado correctamente";
        }
        message = "No se pudo actualizar el espacio";
        return message;
    }

    public String deleteSpace(int id) {

        boolean deleted = spaceData.deleteSpace(id);
        String message = "";

        if (deleted) {
            message = "Espacio eliminado correctamente";
        }
        message = "No se pudo eliminar el espacio";
        return message;
    }

    public boolean occupySpace(int id, Client client, Vehicle vehicle) {

        Space space = spaceData.findSpaceById(id);
        boolean result = false;

        if (space != null && !space.isSpaceTaken()) {

            space.setSpaceTaken(true);
            space.setClient(client);
            space.setVehicle(vehicle);
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
            spaceData.updateSpace(space);
            result = true;

        }

        return result;
    }

}
