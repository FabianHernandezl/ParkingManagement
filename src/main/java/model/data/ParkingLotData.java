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
            return new ArrayList<>();
        }

        try (FileReader reader = new FileReader(JSON_FILE_PATH)) {

            Type listType = new TypeToken<ArrayList<ParkingLotDTO>>() {
            }.getType();
            ArrayList<ParkingLotDTO> dtoList = gson.fromJson(reader, listType);

            if (dtoList == null) {
                return new ArrayList<>();
            }

            return new ArrayList<>(ParkingLotConverter.fromDTOList(dtoList));

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

    public void saveParkingLots() {
        try {
            new File("data").mkdirs();

            try (FileWriter writer = new FileWriter(JSON_FILE_PATH)) {
                ArrayList<ParkingLotDTO> dtoList
                        = new ArrayList<>(ParkingLotConverter.toDTOList(parkingLots));
                gson.toJson(dtoList, writer);
                writer.flush();
            }

        } catch (Exception e) {
            // Error silencioso (puedes reemplazar por Logger si deseas)
        }
    }

    public void saveParkingLotsAsTxt() {
        try {
            new File("data").mkdirs();

            try (PrintWriter writer = new PrintWriter(new File(TXT_FILE_PATH))) {

                writer.println("========================================");
                writer.println("      REGISTRO DE PARQUEOS");
                writer.println("========================================");
                writer.println("Total de parqueos: " + parkingLots.size());
                writer.println("========================================");

                for (ParkingLot lot : parkingLots) {
                    writer.println();
                    writer.println("PARQUEO #" + lot.getId());
                    writer.println("----------------------------------------");
                    writer.println("Nombre: " + lot.getName());

                    int totalSpaces = (lot.getSpaces() != null)
                            ? lot.getSpaces().length : 0;

                    int ocupados = 0;

                    if (lot.getSpaces() != null) {
                        for (Space s : lot.getSpaces()) {
                            if (s != null && s.isSpaceTaken()) {
                                ocupados++;
                            }
                        }
                    }

                    writer.println("Espacios totales: " + totalSpaces);
                    writer.println("Espacios ocupados: " + ocupados);
                    writer.println("Espacios disponibles: " + (totalSpaces - ocupados));
                    writer.println("----------------------------------------");
                }

                writer.println();
                writer.println("========================================");
                writer.println("Fin del registro");
                writer.println("========================================");
            }

        } catch (IOException e) {
            // Error silencioso
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

            ParkingLot parkingLotToDelete
                    = findParkingLotById(parkingLot.getId());

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
