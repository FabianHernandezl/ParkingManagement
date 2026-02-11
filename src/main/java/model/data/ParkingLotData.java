package model.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import model.entities.ParkingLot;
import model.entities.Space;
import model.entities.Vehicle;

public class ParkingLotData {

    private static final String JSON_FILE_PATH = "data/parkinglots.json";
    private static final String TXT_FILE_PATH = "data/parkinglots.txt";

    private ArrayList<ParkingLot> parkingLots;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ParkingLotData() {
        parkingLots = loadParkingLots();
    }

    private ArrayList<ParkingLot> loadParkingLots() {
        try (FileReader reader = new FileReader(JSON_FILE_PATH)) {
            Type listType = new TypeToken<ArrayList<ParkingLot>>() {
            }.getType();
            ArrayList<ParkingLot> loadedParkingLots = gson.fromJson(reader, listType);
            return (loadedParkingLots != null) ? loadedParkingLots : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public ParkingLot addParkingLot(ParkingLot parkingLot) {
        if (parkingLot == null || findParkingLotById(parkingLot.getId()) != null) {
            return parkingLot;
        }

        parkingLots.add(parkingLot);
        saveParkingLots();
        saveParkingLotsAsTxt();
        return parkingLot;
    }

    public ArrayList<ParkingLot> getAllParkingLots() {
        return parkingLots;
    }

    public ParkingLot findParkingLotById(int id) {
        for (ParkingLot parkingLot : parkingLots) {
            if (parkingLot.getId() == id) {
                return parkingLot;
            }
        }
        return null;
    }

    public ParkingLot findParkingLotByName(String name) {
        for (ParkingLot parkingLot : parkingLots) {
            if (parkingLot.getName().equalsIgnoreCase(name)) {
                return parkingLot;
            }
        }
        return null;
    }

    private void saveParkingLots() {
        try (FileWriter writer = new FileWriter(JSON_FILE_PATH)) {
            gson.toJson(parkingLots, writer);
        } catch (Exception e) {
            System.out.println("Error saving parking lots JSON: " + e.getMessage());
        }
    }

    private void saveParkingLotsAsTxt() {
        try (PrintWriter writer = new PrintWriter(new File(TXT_FILE_PATH))) {
            for (ParkingLot lot : parkingLots) {
                writer.println("ID: " + lot.getId() + " | Name: " + lot.getName() + " | Spaces: " + lot.getNumberOfSpaces());
            }
        } catch (IOException e) {
            System.out.println("Error saving parking lots TXT: " + e.getMessage());
        }
    }

    public int findLastIdNumberOfParkingLot() {
        int maxId = 0;
        for (ParkingLot parkingLot : parkingLots) {
            if (parkingLot.getId() > maxId) {
                maxId = parkingLot.getId();
            }
        }
        return maxId;
    }

    public void removeVehicleFromParkingLot(Vehicle vehicle, ParkingLot parkingLot) {
        if (parkingLot == null || vehicle == null) {
            return;
        }

        ArrayList<Vehicle> vehiclesInParkingLot = parkingLot.getVehicles();
        Space[] spaces = parkingLot.getSpaces();

        for (int i = 0; i < vehiclesInParkingLot.size(); i++) {
            if (vehiclesInParkingLot.get(i) == vehicle) {
                vehiclesInParkingLot.remove(vehicle);
                if (i < spaces.length) {
                    spaces[i].setSpaceTaken(false);
                }
                break;
            }
        }

        parkingLot.setSpaces(spaces);
        parkingLot.setVehicles(vehiclesInParkingLot);
        saveParkingLots();
        saveParkingLotsAsTxt();
    }

    public boolean updateParkingLot(ParkingLot updatedParkingLot) {
        boolean updated = false;

        if (updatedParkingLot != null && updatedParkingLot.getId() != 0) {
            ParkingLot existing = findParkingLotById(updatedParkingLot.getId());
            if (existing != null) {
                if (updatedParkingLot.getName() != null) {
                    existing.setName(updatedParkingLot.getName());
                }
                if (updatedParkingLot.getNumberOfSpaces() != 0) {
                    existing.setNumberOfSpaces(updatedParkingLot.getNumberOfSpaces());
                }
                if (updatedParkingLot.getSpaces() != null) {
                    existing.setSpaces(updatedParkingLot.getSpaces());
                }
                if (updatedParkingLot.getVehicles() != null) {
                    existing.setVehicles(updatedParkingLot.getVehicles());
                }
                updated = true;
                saveParkingLots();
                saveParkingLotsAsTxt();
            }
        }

        return updated;
    }

    public boolean deleteParkingLot(ParkingLot parkingLot) {
        boolean deleted = false;

        if (parkingLot != null && parkingLot.getId() != 0) {
            ParkingLot parkingLotToDelete = findParkingLotById(parkingLot.getId());
            if (parkingLotToDelete != null) {
                parkingLots.remove(parkingLotToDelete);
                deleted = true;
                saveParkingLots();
                saveParkingLotsAsTxt();
            }
        }

        return deleted;
    }
}
