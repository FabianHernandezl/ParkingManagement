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
import model.dto.ParkingLotDTO;
import model.dto.SpaceDTO;
import model.converter.ParkingLotConverter;

public class ParkingLotData {

    private static final String JSON_FILE_PATH = "data/parkinglots.json";
    private static final String TXT_FILE_PATH = "data/parkinglots.txt";

    private ArrayList<ParkingLot> parkingLots;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ParkingLotData() {
        parkingLots = loadParkingLots();
    }

    private ArrayList<ParkingLot> loadParkingLots() {
        File file = new File(JSON_FILE_PATH);
        if (!file.exists()) {
            System.out.println("DEBUG: Archivo JSON no existe, iniciando con lista vacía");
            return new ArrayList<>();
        }

        try (FileReader reader = new FileReader(JSON_FILE_PATH)) {
            // Leer DTOs en lugar de entidades directamente
            Type listType = new TypeToken<ArrayList<ParkingLotDTO>>() {
            }.getType();
            ArrayList<ParkingLotDTO> dtoList = gson.fromJson(reader, listType);

            if (dtoList == null) {
                return new ArrayList<>();
            }

            // Convertir DTOs a entidades
            ArrayList<ParkingLot> loadedParkingLots = new ArrayList<>(ParkingLotConverter.fromDTOList(dtoList));

            System.out.println("DEBUG: Cargando parking lots desde JSON");
            for (ParkingLot lot : loadedParkingLots) {
                int totalSpaces = (lot.getSpaces() != null) ? lot.getSpaces().length : 0;
                System.out.println("DEBUG: Parking: " + lot.getName() + " | Espacios: " + totalSpaces);
            }

            return loadedParkingLots;

        } catch (Exception e) {
            System.out.println("DEBUG: Error al cargar parking lots: " + e.getMessage());
            e.printStackTrace();
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
            // Convertir entidades a DTOs antes de guardar
            ArrayList<ParkingLotDTO> dtoList = new ArrayList<>(ParkingLotConverter.toDTOList(parkingLots));
            gson.toJson(dtoList, writer);
            System.out.println("DEBUG: Guardado de parking lots exitoso. Total: " + parkingLots.size());
        } catch (Exception e) {
            System.out.println("DEBUG: Error al guardar parking lots JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveParkingLotsAsTxt() {
        try (PrintWriter writer = new PrintWriter(new File(TXT_FILE_PATH))) {
            for (ParkingLot lot : parkingLots) {
                int totalSpaces = (lot.getSpaces() != null) ? lot.getSpaces().length : 0;
                writer.println("ID: " + lot.getId() + " | Name: " + lot.getName() + " | Spaces: " + totalSpaces);
            }
            System.out.println("DEBUG: Guardado de parking lots TXT exitoso");
        } catch (IOException e) {
            System.out.println("DEBUG: Error al guardar parking lots TXT: " + e.getMessage());
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
