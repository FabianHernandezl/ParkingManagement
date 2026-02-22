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
import model.entities.Clerk;
import model.entities.ParkingLot;

/**
 *
 * @author FAMILIA
 */
public class ClerkData {

    private ArrayList<Clerk> clerks = new ArrayList<>();

    private static final String FILE_PATH = "data/clerks.json";
    private static final String FILE_PATH_TXT = "data/clerks.txt";

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public ClerkData() {
        File folder = new File("data");
        if (!folder.exists()) {
            folder.mkdir();
        }
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
            saveClerksToTxt();
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
                saveClerksToTxt();
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
            saveClerksToTxt();
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

    // ===================== TXT =====================
    private void saveClerksToTxt() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH_TXT))) {

            // Encabezado
            writer.write("========================================");
            writer.newLine();
            writer.write("        REGISTRO DE EMPLEADOS");
            writer.newLine();
            writer.write("========================================");
            writer.newLine();
            writer.write("Total de empleados: " + clerks.size());
            writer.newLine();
            writer.write("========================================");
            writer.newLine();
            writer.newLine();

            // Si no hay empleados
            if (clerks.isEmpty()) {
                writer.write("No hay empleados registrados.");
                writer.newLine();
            } else {

                int contador = 1;
                for (Clerk clerk : clerks) {

                    writer.write("EMPLEADO #" + contador);
                    writer.newLine();
                    writer.write("----------------------------------------");
                    writer.newLine();

                    // Datos heredados de User
                    writer.write("ID:             " + (clerk.getId() != null ? clerk.getId() : "No definido"));
                    writer.newLine();

                    writer.write("Nombre:         " + (clerk.getName() != null ? clerk.getName() : "No definido"));
                    writer.newLine();

                    writer.write("Usuario:        " + (clerk.getUsername() != null ? clerk.getUsername() : "No definido"));
                    writer.newLine();

                    // Datos propios de Clerk
                    writer.write("Código empleado:" + clerk.getEmployeeCode());
                    writer.newLine();

                    writer.write("Horario:        "
                            + (clerk.getShedule() != null ? clerk.getShedule() : "No definido"));
                    writer.newLine();

                    writer.write("Edad:           " + clerk.getAge());
                    writer.newLine();

                    // Parqueos asignados
                    writer.write("Parqueos:       ");
                    if (clerk.getParkingLot() != null && !clerk.getParkingLot().isEmpty()) {
                        writer.newLine();
                        for (ParkingLot parking : clerk.getParkingLot()) {
                            if (parking != null) {
                                writer.write("                 - "
                                        + (parking.getName() != null ? parking.getName() : "Sin nombre")
                                        + " (ID: "
                                        + parking.getId() + ")");
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
            System.out.println("Error guardando empleados en TXT: " + e.getMessage());
        }
    }

}
