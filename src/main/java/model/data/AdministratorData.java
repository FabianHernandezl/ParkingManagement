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
import model.entities.Administrator;
import model.entities.Clerk;
import model.entities.ParkingLot;

/**
 *
 * @author FAMILIA
 */
public class AdministratorData {

    private static final String FILE_PATH = "data/administrators.json";
    private ArrayList<Administrator> administrators;
    ParkingLotData parkingLotData = new ParkingLotData();

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public AdministratorData() {
    administrators = loadAdministrators();

    if (administrators.isEmpty()) {
        ArrayList<ParkingLot> parkingLots = parkingLotData.getAllParkingLots();
        administrators.add(new Administrator(1001, parkingLots, "ID-FAB", "Fabián", "admin_fabian", "fabian123"));
        administrators.add(new Administrator(1002, parkingLots, "ID-ZAY", "Zaylin", "admin_zailyn", "zailyn456"));
        administrators.add(new Administrator(1003, parkingLots, "ID-JIM", "Jimena", "admin_jimena", "jimena789"));
        administrators.add(new Administrator(1004, parkingLots, "ID-CAM", "Camila", "admin_camila", "camila012"));
        saveAdministrators();
    }
}
  public ArrayList<Administrator> getAllAdministrators() {
        return new ArrayList<>(administrators); // Retorna copia para evitar modificación externa
    }

    /*
    Loads clients from JSON file
     */
    private ArrayList<Administrator> loadAdministrators() {

        
        try (FileReader reader = new FileReader(FILE_PATH)) {

            Type listType = new TypeToken<ArrayList<Administrator>>() {
            }.getType();
            ArrayList<Administrator> loadedParkingLots = gson.fromJson(reader, listType);

            return (loadedParkingLots != null) ? loadedParkingLots : new ArrayList<>();

        } catch (Exception e) {

            return new ArrayList<>();
        }
    }

    /*
    Add new admin
     */
    public Administrator addAdministrator(Administrator admin) {
        Administrator adminToReturn = null;

        if (admin != null && findAdministratorById(admin.getId()) == null) {
            administrators.add(admin);
            saveAdministrators();
            adminToReturn = admin;
        }

        return adminToReturn;
    }

    /*
    Saves clients to JSON file
     */
    private void saveAdministrators() {

        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(administrators, writer);
        } catch (Exception e) {
            System.out.println("Error saving admins: " + e.getMessage());
        }

    }

    /*
    Finds a admin by id
     */
    public Administrator findAdministratorById(String id) {
        Administrator adminToReturn = null;
        
        for (Administrator admin : administrators) {
            if (admin.getId().equals(id)) {
                adminToReturn = admin;
            }
        }
        return adminToReturn;
    }

    /*
    Finds a admin by username 
     */
    public Administrator findAdminByUsername(String username) {
        Administrator adminToReturn = null;

        for (Administrator admin : administrators) {
            if (admin.getUsername().equals(username)) {
                adminToReturn = admin;
            }
        }
        return adminToReturn;
    }

    /*
    Updates an existing admin
     */
    public boolean updateAdministrator(Administrator updatedAdministrator) {
        for (int i = 0; i < administrators.size(); i++) {
            if (administrators.get(i).getId().equals(updatedAdministrator.getId())) {
                administrators.set(i, updatedAdministrator);
                saveAdministrators();
                return true;
            }
        }
        return false;
    }

    /*
    Removes a admin by id
     */
    public boolean removeAdministrator(String id) {
        Administrator admin = findAdministratorById(id);
        if (admin != null) {
            administrators.remove(admin);
            saveAdministrators();
            return true;
        }
        return false;
    }

    /*
    Authenticates a admin 
     */
    public Administrator authenticateAdmin(String username, String password) {
        Administrator clerk = findAdminByUsername(username);
        if (clerk != null && clerk.getPassword().equals(password)) {
            return clerk;
        }
        return null;
    }

    public int findLastIdNumberOfAdmins() {
        int maxId = 0;
        for (Administrator admin : administrators) {
            String id = admin.getId();
            if (id.startsWith("ID") || id.startsWith("ADM")) {
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

    public int findLastAdminNumber() {
        int maxCode = 1000; // Código inicial
        for (Administrator admin : administrators) {
            if (admin.getAdminNumber() > maxCode) {
                maxCode = admin.getAdminNumber();
            }
        }
        return maxCode;
    }

    /*
    Get number of registered clerks
     */
    public int getAdminsCount() {
        return administrators.size();
    }

}
