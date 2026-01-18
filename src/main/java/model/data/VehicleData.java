package model.data;

import java.util.ArrayList;
import model.entity.Client;
import model.entity.Vehicle;

/**
 *
 * @author fabian
 */
public class VehicleData {

    // Simulación de la base de datos
    private ArrayList<Vehicle> vehicleDB;

    public VehicleData() {
        vehicleDB = new ArrayList<>();
    }

    public String insertVehicle(Vehicle vehicle) {

        String message = "Error: vehículo inválido";

        if (vehicle != null) {
            vehicleDB.add(vehicle);
            message = "Vehículo insertado correctamente";
        }

        return message;
    }

    public ArrayList<Vehicle> getAllVehicles() {

        ArrayList<Vehicle> vehicles = vehicleDB;

        return vehicles;
    }

    public Vehicle findVehicle(Client client) {

        Vehicle foundVehicle = null;

        for (Vehicle vehicle : vehicleDB) {
            if (vehicle.getClient() != null
                    && vehicle.getClient().getId().equals(client.getId())) {
                foundVehicle = vehicle;
                break;
            }
        }

        return foundVehicle;
    }
}
