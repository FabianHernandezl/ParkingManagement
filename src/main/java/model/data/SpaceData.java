package model.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import model.entities.ParkingLot;
import model.entities.Space;

public class SpaceData {

    private static final String PARKING_FILE = "data/parkings.json";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public SpaceData() {
        File folder = new File("data");
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    public Space findSpaceById(int id) {
        ArrayList<ParkingLot> parkings = loadParkings();
        for (ParkingLot p : parkings) {
            if (p.getSpaces() != null) {
                for (Space s : p.getSpaces()) {
                    if (s != null && s.getId() == id) {
                        return s;
                    }
                }
            }
        }
        return null;
    }

    public ArrayList<Space> getAllSpaces() {
        ArrayList<Space> allSpaces = new ArrayList<>();
        ArrayList<ParkingLot> parkings = loadParkings();
        for (ParkingLot p : parkings) {
            if (p.getSpaces() != null) {
                allSpaces.addAll(Arrays.asList(p.getSpaces()));
            }
        }
        return allSpaces;
    }

    public boolean updateSpace(Space updatedSpace) {
        ArrayList<ParkingLot> parkings = loadParkings();
        boolean found = false;

        System.out.println("DEBUG: updateSpace llamado para espacio #" + updatedSpace.getId());
        System.out.println("DEBUG: Ocupado: " + updatedSpace.isSpaceTaken());
        System.out.println("DEBUG: Cliente: " + (updatedSpace.getClient() != null ? updatedSpace.getClient().getName() : "null"));
        System.out.println("DEBUG: Vehículo: " + (updatedSpace.getVehicle() != null ? updatedSpace.getVehicle().getPlate() : "null"));

        for (ParkingLot p : parkings) {
            if (p.getSpaces() != null) {
                Space[] spacesArray = p.getSpaces();
                for (int i = 0; i < spacesArray.length; i++) {
                    if (spacesArray[i] != null && spacesArray[i].getId() == updatedSpace.getId()) {
                        spacesArray[i].setSpaceTaken(updatedSpace.isSpaceTaken());
                        spacesArray[i].setClient(updatedSpace.getClient());
                        spacesArray[i].setVehicle(updatedSpace.getVehicle());
                        spacesArray[i].setEntryTime(updatedSpace.getEntryTime());
                        spacesArray[i].setDisabilityAdaptation(updatedSpace.isDisabilityAdaptation());
                        spacesArray[i].setVehicleType(updatedSpace.getVehicleType());

                        found = true;
                        System.out.println("DEBUG: Espacio actualizado en el arreglo");
                        break;
                    }
                }
            }
            if (found) {
                break;
            }
        }

        if (found) {
            saveParkings(parkings);
            System.out.println("DEBUG: JSON guardado exitosamente");
        } else {
            System.out.println("DEBUG: No se encontró el espacio para actualizar");
        }

        return found;
    }

    private ArrayList<ParkingLot> loadParkings() {
        try (Reader reader = new FileReader(PARKING_FILE)) {
            Type listType = new TypeToken<ArrayList<ParkingLot>>() {
            }.getType();
            ArrayList<ParkingLot> data = gson.fromJson(reader, listType);
            if (data == null) {
                data = new ArrayList<>();
            }

            System.out.println("DEBUG: Cargando parqueos desde JSON");
            for (ParkingLot p : data) {
                int total = (p.getSpaces() != null) ? p.getSpaces().length : 0;
                System.out.println("DEBUG: Parqueo: " + p.getName() + " | Espacios cargados: " + total);
            }
            return data;
        } catch (IOException e) {
            System.out.println("DEBUG: Error al leer JSON: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void saveParkings(ArrayList<ParkingLot> parkings) {
        try (Writer writer = new FileWriter(PARKING_FILE)) {
            gson.toJson(parkings, writer);
            System.out.println("DEBUG: Guardado de parqueos con " + parkings.size() + " parqueos exitoso");
        } catch (IOException e) {
            System.err.println("Error al guardar: " + e.getMessage());
        }
    }

    public String insertSpace(Space space) {
        return "Operación no permitida: Los espacios son fijos por parqueo.";
    }

    public boolean deleteSpace(int id) {
        return false;
    }

    public void debugPrintAllSpaces() {
        ArrayList<ParkingLot> parkings = loadParkings();
        System.out.println("===== DEBUG: Revisando todos los espacios =====");

        for (ParkingLot p : parkings) {
            System.out.println("Parqueo: " + p.getName());
            if (p.getSpaces() == null) {
                System.out.println("  ⚠ Espacios nulos (getSpaces() == null)");
                continue;
            }

            for (int i = 0; i < p.getSpaces().length; i++) {
                Space s = p.getSpaces()[i];
                if (s == null) {
                    System.out.println("  ⚠ Espacio #" + i + " es NULL");
                } else {
                    System.out.println("  Espacio ID: " + s.getId()
                            + " | Ocupado: " + s.isSpaceTaken()
                            + " | Cliente: " + (s.getClient() != null ? s.getClient().getName() : "null")
                            + " | Vehículo: " + (s.getVehicle() != null ? s.getVehicle().getPlate() : "null")
                            + " | Adaptación discapacidad: " + s.isDisabilityAdaptation());
                }
            }
        }
        System.out.println("===== FIN DEBUG =====");
    }

}
