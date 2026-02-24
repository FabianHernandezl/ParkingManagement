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

/**
 * Handles data access and persistence of Space objects. Spaces are stored
 * inside ParkingLot objects in a JSON file.
 */
public class SpaceData {

    // Path to the JSON file that stores parking lot data
    private static final String PARKING_FILE = "data/parkinglots.json";
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
        }

        return found;
    }

    private ArrayList<ParkingLot> loadParkings() {
        try (Reader reader = new FileReader(PARKING_FILE)) {
            Type listType = new TypeToken<ArrayList<ParkingLot>>() {
            }.getType();
            ArrayList<ParkingLot> data = gson.fromJson(reader, listType);
            return (data != null) ? data : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private void saveParkings(ArrayList<ParkingLot> parkings) {
        try (Writer writer = new FileWriter(PARKING_FILE)) {
            gson.toJson(parkings, writer);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    public String insertSpace(Space space) {
        return "Operación no permitida: Los espacios son fijos por parqueo.";
    }

    public boolean deleteSpace(int id) {
        return false;
    }
}
