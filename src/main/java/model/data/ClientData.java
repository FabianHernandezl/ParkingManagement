package model.data;

import java.util.ArrayList;
import model.entities.Client;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Type;

public class ClientData {

    private static final String FILE_PATH = "data/clients.json";
    private static final String TXT_FILE_PATH = "data/clients.txt";

    private ArrayList<Client> clients;

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public ClientData() {
        File folder = new File("data");
        if (!folder.exists()) {
            folder.mkdir();
        }
        clients = loadClients();
    }

    // ================= CRUD =================
    public boolean addClient(Client client) {
        if (client == null || findClientById(client.getId()) != null) {
            return false;
        }
        clients.add(client);
        saveClients();
        return true;
    }

    public ArrayList<Client> getAllClients() {
        return clients;
    }

    public Client findClientById(String id) {
        for (Client client : clients) {
            if (client.getId().equals(id)) {
                return client;
            }
        }
        return null;
    }

    public boolean update(Client clientUpdate) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getId().equals(clientUpdate.getId())) {
                clients.set(i, clientUpdate);
                saveClients();
                return true;
            }
        }
        return false;
    }

    public boolean delete(String id, VehicleData vehicleData) {
        Client client = findClientById(id);

        if (client == null) {
            return false;
        }

        if (vehicleData.findVehicle(client) != null) {
            System.out.println("No se puede eliminar: El cliente tiene vehículos asociados.");
            return false;
        }

        clients.remove(client);
        saveClients();
        return true;
    }

    // ================= LOAD / SAVE =================
    private ArrayList<Client> loadClients() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type listType = new TypeToken<ArrayList<Client>>() {
            }.getType();
            ArrayList<Client> loadedClients = gson.fromJson(reader, listType);
            return (loadedClients != null) ? loadedClients : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void saveClients() {
        // ===== JSON =====
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(clients, writer);
        } catch (Exception e) {
            System.out.println("Error saving JSON: " + e.getMessage());
        }

        // ===== TXT =====
        try (FileWriter txtWriter = new FileWriter(TXT_FILE_PATH); PrintWriter printWriter = new PrintWriter(txtWriter)) {

            printWriter.println("========================================");
            printWriter.println("          REGISTRO DE CLIENTES          ");
            printWriter.println("========================================");
            printWriter.printf("Total de clientes: %d%n", clients.size());
            printWriter.println("========================================");
            printWriter.println();

            int contador = 1;
            for (Client client : clients) {
                printWriter.printf("CLIENTE #%d%n", contador++);
                printWriter.println("----------------------------------------");
                printWriter.printf("%-14s %s%n", "ID:", client.getId());
                printWriter.printf("%-14s %s%n", "Nombre:", client.getName());
                printWriter.printf("%-14s %s%n", "Teléfono:", client.getPhone());
                printWriter.printf("%-14s %s%n", "Email:",
                        client.getEmail() == null ? "No registrado" : client.getEmail());

                String esPreferencial = client.isIsPreferential() ? "Sí" : "No";
                printWriter.printf("%-14s %s%n", "Preferencial:", esPreferencial);

                printWriter.println("----------------------------------------");
                printWriter.println();
            }

            printWriter.println("========================================");
            printWriter.println("            Fin del registro            ");
            printWriter.println("========================================");

        } catch (Exception e) {
            System.out.println("Error saving TXT: " + e.getMessage());
        }
    }
}
