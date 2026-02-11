/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import model.entities.Administrator;
import model.entities.Clerk;
import model.entities.Client;
import model.entities.ParkingLot;
import model.entities.Vehicle;

/**
 *
 * @author FAMILIA
 */
public class AdministratorData {

    private ArrayList<Administrator> administrators;

    private static final String FILE_PATH = "data/administrators.json";
    private static final String FILE_PATH_TXT = "data/administrators.txt";

    ParkingLotData parkingLotData = new ParkingLotData();

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public AdministratorData() {

        File folder = new File("data");
        if (!folder.exists()) {
            folder.mkdir();
        }
        administrators = loadAdministrators();
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
                saveAdministratorsToTxt();
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
            saveAdministratorsToTxt();
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

    // ===================== TXT =====================
    private void saveAdministratorsToTxt() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH_TXT))) {

            // Encabezado
            writer.write("========================================");
            writer.newLine();
            writer.write("     REGISTRO DE ADMINISTRADORES");
            writer.newLine();
            writer.write("========================================");
            writer.newLine();
            writer.write("Total de administradores: " + administrators.size());
            writer.newLine();
            writer.write("========================================");
            writer.newLine();
            writer.newLine();

            // Si no hay administradores
            if (administrators.isEmpty()) {
                writer.write("No hay administradores registrados.");
                writer.newLine();
            } else {

                int contador = 1;
                for (Administrator admin : administrators) {

                    writer.write("ADMINISTRADOR #" + contador);
                    writer.newLine();
                    writer.write("----------------------------------------");
                    writer.newLine();

                    // Datos heredados de User
                    writer.write("ID:            " + (admin.getId() != null ? admin.getId() : "No definido"));
                    writer.newLine();

                    writer.write("Nombre:        " + (admin.getName() != null ? admin.getName() : "No definido"));
                    writer.newLine();

                    writer.write("Usuario:       " + (admin.getUsername() != null ? admin.getUsername() : "No definido"));
                    writer.newLine();

                    // Datos propios de Administrator
                    writer.write("Número Admin:  " + admin.getAdminNumber());
                    writer.newLine();

                    // Parqueos asignados
                    writer.write("Parqueos:      ");
                    if (admin.getParkingLot() != null && !admin.getParkingLot().isEmpty()) {
                        writer.newLine();
                        for (ParkingLot parking : admin.getParkingLot()) {
                            if (parking != null) {
                                writer.write("                - "
                                        + (parking.getName() != null ? parking.getName() : "Sin nombre"));
                                        
                                writer.newLine();
                            }
                        }
                    } else {
                        writer.write("No tiene parqueos asignados");
                        writer.newLine();
                    }

                    writer.write("----------------------------------------");
                    writer.newLine();
                    writer.newLine();

                    contador++;
                }
            }

            // Pie
            writer.write("========================================");
            writer.newLine();
            writer.write("Fin del registro");
            writer.newLine();
            writer.write("========================================");

        } catch (IOException e) {
            System.out.println("Error guardando administradores en TXT: " + e.getMessage());
        }
    }

}
