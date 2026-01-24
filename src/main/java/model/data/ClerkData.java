/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import model.entities.Clerk;
import model.entities.ParkingLot;

/**
 *
 * @author FAMILIA
 */
public class ClerkData {

    private static final String FILE_PATH = "data/clerks.json";
    private ArrayList<Clerk> clerks = new ArrayList<>();

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public ClerkData() {
        clerks = loadClerks();

    }

    /*
    Loads Clerks from JSON file
     */
    private ArrayList<Clerk> loadClerks() {

        try (FileReader reader = new FileReader(FILE_PATH)) {

            Type listType = new TypeToken<ArrayList<Clerk>>() {
            }.getType();
            ArrayList<Clerk> loadedParkingLots = gson.fromJson(reader, listType);

            return (loadedParkingLots != null) ? loadedParkingLots : new ArrayList<>();

        } catch (Exception e) {

            return new ArrayList<>();
        }
    }

    /*
    Add new clerk
     */
    public Clerk addClerk(Clerk clerk) {
        Clerk clerkToReturn = null;

        if (clerk != null && findClerkById(clerk.getId()) == null) {
            clerks.add(clerk);
            saveClerks();
            clerkToReturn = clerk;
        }

        return clerkToReturn;
    }

    /*
    Returns all registered clerks
     */
    public ArrayList<Clerk> getAllClerks() {
        return new ArrayList<>(clerks); // Retorna copia para evitar modificación externa
    }

    /*
    Finds a clerk by id
     */
    public Clerk findClerkById(String id) { // Asumiendo que Clerk tiene ID String
        for (Clerk clerk : clerks) {
            if (clerk.getId().equals(id)) {
                return clerk;
            }
        }
        return null;
    }

    /*
    Finds a clerk by username (método  útil para login)
     */
    public Clerk findClerkByUsername(String username) {
        for (Clerk clerk : clerks) {
            if (clerk.getUsername().equals(username)) {
                return clerk;
            }
        }
        return null;
    }

    /*
    Updates an existing clerk
     */
    public boolean updateClerk(Clerk updatedClerk) {
        for (int i = 0; i < clerks.size(); i++) {
            if (clerks.get(i).getId().equals(updatedClerk.getId())) {
                clerks.set(i, updatedClerk);
                saveClerks();
                return true;
            }
        }
        return false;
    }

    /*
    Removes a clerk by id
     */
    public boolean removeClerk(String id) {
        Clerk clerk = findClerkById(id);
        if (clerk != null) {
            clerks.remove(clerk);
            saveClerks();
            return true;
        }
        return false;
    }

    /*
    Authenticates a clerk (método adicional útil)
     */
    public Clerk authenticateClerk(String username, String password) {
        Clerk clerk = findClerkByUsername(username);
        if (clerk != null && clerk.getPassword().equals(password)) {
            return clerk;
        }
        return null;
    }

    /*
    Saves clerks to JSON file
     */
    private void saveClerks() {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(clerks, writer);
        } catch (Exception e) {
            System.out.println("Error saving clerks: " + e.getMessage());
        }
    }

    public int findLastIdNumberOfClerk() {
        int maxId = 0;
        for (Clerk clerk : clerks) {
            String id = clerk.getId();
            if (id.startsWith("CLK")) {
                try {
                    int idNum = Integer.parseInt(id.substring(3));
                    if (idNum > maxId) {
                        maxId = idNum;
                    }
                } catch (NumberFormatException e) {
                    // Ignorar IDs con formato incorrecto
                }
            }
        }
        return maxId;
    }

    public int findLastEmployeeCode() {
        int maxCode = 1000; // Código inicial
        for (Clerk clerk : clerks) {
            if (clerk.getEmployeeCode() > maxCode) {
                maxCode = clerk.getEmployeeCode();
            }
        }
        return maxCode;
    }

    /*
    Get number of registered clerks
     */
    public int getClerkCount() {
        return clerks.size();
    }

}
