package model.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import model.entities.Client;
import model.entities.Vehicle;

/**
 *
 * @author fabian
 */
public class VehicleData {

    private ArrayList<Vehicle> vehicleDB;

    private static final String FILE_PATH_JSON = "data/vehicles.json";
    private static final String FILE_PATH_TXT = "data/vehicles.txt";

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
            saveToTxt();
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
                saveToTxt();
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
                saveToTxt();
            }
        }

        return deleted;
    }

    // ===================== JSON =====================
    private void saveToFile() {
        try (Writer writer = new FileWriter(FILE_PATH_JSON)) {
            gson.toJson(vehicleDB, writer);
        } catch (IOException e) {
            System.out.println("Error guardando vehículos en JSON");
        }
    }

    private ArrayList<Vehicle> loadFromFile() {
        try (Reader reader = new FileReader(FILE_PATH_JSON)) {
            Type listType = new TypeToken<ArrayList<Vehicle>>() {
            }.getType();
            ArrayList<Vehicle> data = gson.fromJson(reader, listType);
            return data != null ? data : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    // ===================== TXT =====================
    private void saveToTxt() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH_TXT))) {

            // Encabezado del archivo
            writer.write("========================================");
            writer.newLine();
            writer.write("      REGISTRO DE VEHÍCULOS");
            writer.newLine();
            writer.write("========================================");
            writer.newLine();
            writer.write("Total de vehículos: " + vehicleDB.size());
            writer.newLine();
            writer.write("========================================");
            writer.newLine();
            writer.newLine();

            // Si no hay vehículos
            if (vehicleDB.isEmpty()) {
                writer.write("No hay vehículos registrados.");
                writer.newLine();
            } else {
                // Escribir cada vehículo
                int contador = 1;
                for (Vehicle vehicle : vehicleDB) {

                    writer.write("VEHÍCULO #" + contador);
                    writer.newLine();
                    writer.write("----------------------------------------");
                    writer.newLine();

                    // Placa
                    writer.write("Placa:        "
                            + (vehicle.getPlate() != null ? vehicle.getPlate() : "No definida"));
                    writer.newLine();

                    // Marca
                    writer.write("Marca:        "
                            + (vehicle.getBrand() != null ? vehicle.getBrand() : "No definida"));
                    writer.newLine();

                    // Modelo
                    writer.write("Modelo:       "
                            + (vehicle.getModel() != null ? vehicle.getModel() : "No definido"));
                    writer.newLine();

                    // Color
                    writer.write("Color:        "
                            + (vehicle.getColor() != null ? vehicle.getColor() : "No definido"));
                    writer.newLine();

                    // Tipo de vehículo
                    writer.write("Tipo:         "
                            + (vehicle.getVehicleType() != null
                            ? vehicle.getVehicleType().getDescription() : "No definido"));
                    writer.newLine();

                    // Clientes asociados
                    writer.write("Clientes:     ");
                    if (vehicle.getClients() != null && !vehicle.getClients().isEmpty()) {
                        writer.newLine();
                        for (Client client : vehicle.getClients()) {
                            if (client != null) {
                                writer.write("              - "
                                        + (client.getName() != null ? client.getName() : "Sin nombre")
                                        + " (ID: "
                                        + (client.getId() != null ? client.getId() : "N/A") + ")");
                                writer.newLine();
                            }
                        }
                    } else {
                        writer.write("No hay clientes asociados");
                        writer.newLine();
                    }

                    writer.write("----------------------------------------");
                    writer.newLine();
                    writer.newLine();

                    contador++;
                }
            }

            // Pie de página
            writer.write("========================================");
            writer.newLine();
            writer.write("Fin del registro");
            writer.newLine();
            writer.write("========================================");

        } catch (IOException e) {
            System.out.println("Error guardando vehículos en TXT: " + e.getMessage());
        }
    }
}
