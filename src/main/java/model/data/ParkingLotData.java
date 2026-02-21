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
            System.out.println("Archivo JSON no existe, iniciando vacío");
            return new ArrayList<>();
        }

        try (FileReader reader = new FileReader(JSON_FILE_PATH)) {
            // Leer DTOs en lugar de entidades
            Type listType = new TypeToken<ArrayList<ParkingLotDTO>>() {
            }.getType();
            ArrayList<ParkingLotDTO> dtoList = gson.fromJson(reader, listType);

            if (dtoList == null) {
                return new ArrayList<>();
            }

            // Convertir DTOs a entidades
            ArrayList<ParkingLot> loaded = new ArrayList<>(ParkingLotConverter.fromDTOList(dtoList));

            System.out.println("DEBUG: Cargados " + loaded.size() + " parqueos desde JSON");

            // 🔥 Verificar estados de espacios
            for (ParkingLot lot : loaded) {
                int ocupados = 0;
                for (Space s : lot.getSpaces()) {
                    if (s != null && s.isSpaceTaken()) {
                        ocupados++;
                    }
                }
                System.out.println("  Parqueo " + lot.getName() + ": " + ocupados + " espacios ocupados de " + lot.getSpaces().length);
            }

            return loaded;

        } catch (Exception e) {
            System.out.println("Error al cargar parking lots: " + e.getMessage());
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

    /**
     * 🔥 GUARDAR EN JSON - Versión mejorada con verificación
     */
    public void saveParkingLots() {
        try {
            // Asegurar que el directorio existe
            new File("data").mkdirs();

            try (FileWriter writer = new FileWriter(JSON_FILE_PATH)) {
                // Convertir entidades a DTOs antes de guardar
                ArrayList<ParkingLotDTO> dtoList = new ArrayList<>(ParkingLotConverter.toDTOList(parkingLots));
                gson.toJson(dtoList, writer);
                writer.flush();

                System.out.println("DEBUG: Guardados " + parkingLots.size() + " parqueos en JSON");

                // 🔥 Verificar que se guardó correctamente
                File savedFile = new File(JSON_FILE_PATH);
                if (savedFile.exists()) {
                    System.out.println("  Archivo JSON tamaño: " + savedFile.length() + " bytes");
                }
            }
        } catch (Exception e) {
            System.out.println("Error al guardar parking lots JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 🔥 GUARDAR EN TXT - Versión mejorada
     */
    public void saveParkingLotsAsTxt() {
        try {
            // Asegurar que el directorio existe
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

                    int totalSpaces = (lot.getSpaces() != null) ? lot.getSpaces().length : 0;
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

                System.out.println("DEBUG: Guardado de parking lots TXT exitoso");
            }
        } catch (IOException e) {
            System.out.println("DEBUG: Error al guardar parking lots TXT: " + e.getMessage());
            e.printStackTrace();
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
        System.out.println("--- ParkingLotData.removeVehicleFromParkingLot ---");
        System.out.println("Vehículo: " + (vehicle != null ? vehicle.getPlate() : "null"));
        System.out.println("Parqueo: " + (parkingLot != null ? parkingLot.getName() : "null"));

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
                    System.out.println("  Espacio " + spaces[i].getId() + " liberado");
                }
                break;
            }
        }

        parkingLot.setSpaces(spaces);
        parkingLot.setVehicles(vehiclesInParkingLot);

        // 🔥 FORZAR GUARDADO
        saveParkingLots();
        saveParkingLotsAsTxt();
    }

    /**
     * 🔥 VERSIÓN CORREGIDA - Actualiza y guarda inmediatamente
     */
    public boolean updateParkingLot(ParkingLot updatedParkingLot) {
        System.out.println("--- ParkingLotData.updateParkingLot ---");
        System.out.println("ID: " + (updatedParkingLot != null ? updatedParkingLot.getId() : "null"));

        boolean updated = false;

        if (updatedParkingLot != null && updatedParkingLot.getId() != 0) {
            ParkingLot existing = findParkingLotById(updatedParkingLot.getId());
            if (existing != null) {
                System.out.println("  Parqueo encontrado: " + existing.getName());

                if (updatedParkingLot.getName() != null) {
                    existing.setName(updatedParkingLot.getName());
                }

                if (updatedParkingLot.getNumberOfSpaces() != 0) {
                    existing.setNumberOfSpaces(updatedParkingLot.getNumberOfSpaces());
                }

                if (updatedParkingLot.getSpaces() != null) {
                    existing.setSpaces(updatedParkingLot.getSpaces());

                    // Contar espacios ocupados para debug
                    int ocupados = 0;
                    for (Space s : existing.getSpaces()) {
                        if (s != null && s.isSpaceTaken()) {
                            ocupados++;
                        }
                    }
                    System.out.println("  Espacios actualizados: " + existing.getSpaces().length
                            + " total, " + ocupados + " ocupados");
                }

                if (updatedParkingLot.getVehicles() != null) {
                    existing.setVehicles(updatedParkingLot.getVehicles());
                    System.out.println("  Vehículos actualizados: " + existing.getVehicles().size());
                }

                updated = true;

                // 🔥 FORZAR GUARDADO INMEDIATO
                saveParkingLots();
                saveParkingLotsAsTxt();

                System.out.println("  ✅ Parqueo actualizado y guardado");
            } else {
                System.out.println("  ❌ Parqueo no encontrado en memoria");
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

                // 🔥 FORZAR GUARDADO
                saveParkingLots();
                saveParkingLotsAsTxt();

                System.out.println("Parqueo " + parkingLot.getName() + " eliminado");
            }
        }

        return deleted;
    }
}
