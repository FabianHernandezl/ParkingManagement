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
import model.entities.ParkingLot;

/**
 *
 * @author FAMILIA
 */
public class AdministratorData {

    private static final String FILE_PATH = "data/administrators.json";
    private ArrayList<Administrator> administrators;
    static int adminId = 0;

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public AdministratorData() {
        administrators = loadAdmins();

    }

    // devuelve todos los Administrators de la lista que simula la base de datos.
    public ArrayList<Administrator> getAllAdministrators() {
        
        ParkingLot parkingLot = new ParkingLot();

        administrators.add(new Administrator(1001, parkingLot, "ID-FAB", "Fabián", "admin_fabian", "fabian123"));
        administrators.add(new Administrator(1002, parkingLot, "ID-ZAY", "Zaylin", "admin_zaylin", "zaylin456"));
        administrators.add(new Administrator(1003, parkingLot, "ID-JIM", "Jimena", "admin_jimena", "jimena789"));
        administrators.add(new Administrator(1004, parkingLot, "ID-CAM", "Camila", "admin_camila", "camila012"));

        return administrators;
    }

    /*
    Loads clients from JSON file
     */
    private ArrayList<Administrator> loadAdmins() {

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
    Saves clients to JSON file
     */
    private void saveParkingLots() {

        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(administrators, writer);
        } catch (Exception e) {
            System.out.println("Error saving admins: " + e.getMessage());
        }

    }
    
      /*
    Finds a clerk by username (método adicional útil)
     */
    public Administrator findClerkByUsername(String username) {
        for (Administrator admin : administrators) {
            if (admin.getUsername().equals(username)) {
                return admin;
            }
        }
        return null;
    }

}
