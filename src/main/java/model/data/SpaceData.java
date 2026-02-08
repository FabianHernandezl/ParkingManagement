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
            return data != null ? data : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private void saveParkings(ArrayList<ParkingLot> parkings) {
        try (Writer writer = new FileWriter(PARKING_FILE)) {
            gson.toJson(parkings, writer);
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
}
