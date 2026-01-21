/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.data;

import java.util.ArrayList;
import model.entity.Client;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;

/**
 *
 * @author Jimena
 */
public class ClientData {

    private static final String FILE_PATH = "src/main/resources/clients.json";
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

    /*
    Add new clients
     */
    public boolean addClient(Client client) {

        boolean result = true;

        if (client == null || findClientById(client.getId()) != null) {

            result = false;

        }

        clients.add(client);
        saveClients();

        return result;
    }

    /*
    Returns all registered clients
     */
    public ArrayList<Client> getAllClients() {
        return clients;
    }

    /*
    Finds a client by id
     */
    public Client findClientById(String id) {

        for (Client client : clients) {

            if (client.getId().equals(id)) {

                return client;

            }

        }

        return null;
    }

    /*
    Loads clients from JSON file
     */
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

    /*
    Saves clients to JSON file
     */
    private void saveClients() {

        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(clients, writer);
        } catch (Exception e) {
            System.out.println("Error saving clients: " + e.getMessage());
        }

    }

}
