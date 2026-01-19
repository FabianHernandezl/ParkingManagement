package model.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
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

    private static final String FILE_PATH = "data/vehicles.json";
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public VehicleData() {

        File folder = new File("data");
        if (!folder.exists()) {
            folder.mkdir();
        }

        vehicleDB = loadFromFile();
    }

    public String insertVehicle(Vehicle vehicle) {

        String message = "Error: vehículo inválido";

        if (vehicle != null && findVehicleByPlate(vehicle.getPlate()) == null) {
            vehicleDB.add(vehicle);
            saveToFile();
            message = "Vehículo insertado correctamente";
        }

        return message;
    }

    public ArrayList<Vehicle> getAllVehicles() {
        return new ArrayList<>(vehicleDB);
    }

    public Vehicle findVehicle(Client client) {

        Vehicle foundVehicle = null;

        if (client != null) {
            for (Vehicle vehicle : vehicleDB) {

                if (vehicle.getClients() != null) {
                    for (Client c : vehicle.getClients()) {

                        if (c != null && c.getId() != null
                                && c.getId().equals(client.getId())) {
                            foundVehicle = vehicle;
                            break;
                        }
                    }
                }

                if (foundVehicle != null) {
                    break;
                }
            }
        }

        return foundVehicle;
    }

    public Vehicle findVehicleByPlate(String plate) {

        Vehicle foundVehicle = null;

        if (plate != null && !plate.isBlank()) {
            for (Vehicle vehicle : vehicleDB) {

                if (vehicle != null && vehicle.getPlate() != null) {
                    if (vehicle.getPlate().equalsIgnoreCase(plate)) {
                        foundVehicle = vehicle;
                        break;
                    }
                }
            }
        }

        return foundVehicle;
    }

    public boolean updateVehicle(Vehicle updatedVehicle) {

        boolean updated = false;

        if (updatedVehicle != null && updatedVehicle.getPlate() != null) {

            Vehicle existing = findVehicleByPlate(updatedVehicle.getPlate());

            if (existing != null) {

                if (updatedVehicle.getBrand() != null) {
                    existing.setBrand(updatedVehicle.getBrand());
                }

                if (updatedVehicle.getModel() != null) {
                    existing.setModel(updatedVehicle.getModel());
                }

                if (updatedVehicle.getColor() != null) {
                    existing.setColor(updatedVehicle.getColor());
                }

                if (updatedVehicle.getVehicleType() != null) {
                    existing.setVehicleType(updatedVehicle.getVehicleType());
                }

                updated = true;
                saveToFile();
            }
        }

        return updated;
    }

    public boolean deleteVehicle(String plate) {

        boolean deleted = false;

        if (plate != null && !plate.isBlank()) {

            Vehicle vehicle = findVehicleByPlate(plate);

            if (vehicle != null) {
                vehicleDB.remove(vehicle);
                deleted = true;
                saveToFile();
            }
        }

        return deleted;
    }

    // ===================== JSON =====================
    private void saveToFile() {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(vehicleDB, writer);
        } catch (IOException e) {
            System.out.println("Error guardando vehículos");
        }
    }

    private ArrayList<Vehicle> loadFromFile() {
        try (Reader reader = new FileReader(FILE_PATH)) {
            Type listType = new TypeToken<ArrayList<Vehicle>>() {
            }.getType();
            ArrayList<Vehicle> data = gson.fromJson(reader, listType);
            return data != null ? data : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
}
