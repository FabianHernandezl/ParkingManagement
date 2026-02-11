package model.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import model.entities.ParkingLot;
import model.entities.ParkingRate;

public class ParkingRateData {

    private ArrayList<ParkingRate> rateDB;

    private static final String FILE_PATH_JSON = "data/parking_rates.json";
    private static final String FILE_PATH_TXT = "data/parking_rates.txt";

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public ParkingRateData() {
        File folder = new File("data");
        if (!folder.exists()) {
            folder.mkdir();
        }
        rateDB = loadFromFile();
    }

    // ===================== CRUD =====================
    public String insertParkingRate(ParkingRate rate) {
        String message = "Error: tarifa inválida";

        if (rate != null && getParkingRateByParkingLotAndType(rate.getParkingLotId(), rate.getVehicleType()) == null) {
            rateDB.add(rate);
            saveToFile();
            saveToTxt();
            message = "✅ Tarifa registrada para " + rate.getVehicleType() + " en " + rate.getParkingLotName();
        } else {
            message = "❌ Ya existe una tarifa para " + rate.getVehicleType() + " en este parqueo";
        }

        return message;
    }

    public ArrayList<ParkingRate> getAllParkingRates() {
        return new ArrayList<>(rateDB);
    }

    public ArrayList<ParkingRate> getParkingRatesByParkingLot(ParkingLot parkingLot) {
        ArrayList<ParkingRate> ratesForParkingLot = new ArrayList<>();

        if (parkingLot != null) {
            for (ParkingRate rate : rateDB) {
                if (rate.getParkingLotId() == parkingLot.getId()) {
                    ratesForParkingLot.add(rate);
                }
            }
        }

        return ratesForParkingLot;
    }

    public ParkingRate getParkingRateByParkingLotAndType(int parkingLotId, String vehicleType) {
        for (ParkingRate rate : rateDB) {
            if (rate.getParkingLotId() == parkingLotId && rate.getVehicleType().equals(vehicleType)) {
                return rate;
            }
        }
        return null;
    }

    public boolean updateParkingRate(ParkingRate updatedRate) {
        boolean updated = false;

        if (updatedRate != null) {
            ParkingRate existing = getParkingRateByParkingLotAndType(
                    updatedRate.getParkingLotId(),
                    updatedRate.getVehicleType()
            );

            if (existing != null) {
                existing.setHourPrice(updatedRate.getHourPrice());
                existing.setHalfHourPrice(updatedRate.getHalfHourPrice());
                existing.setDayPrice(updatedRate.getDayPrice());
                existing.setWeekPrice(updatedRate.getWeekPrice());
                existing.setMonthPrice(updatedRate.getMonthPrice());
                existing.setYearPrice(updatedRate.getYearPrice());

                updated = true;
                saveToFile();
                saveToTxt();
            }
        }

        return updated;
    }

    public boolean deleteParkingRate(int parkingLotId, String vehicleType) {
        boolean deleted = false;

        ParkingRate rate = getParkingRateByParkingLotAndType(parkingLotId, vehicleType);

        if (rate != null) {
            rateDB.remove(rate);
            deleted = true;
            saveToFile();
            saveToTxt();
        }

        return deleted;
    }

    // ===================== JSON =====================
    private void saveToFile() {
        try (Writer writer = new FileWriter(FILE_PATH_JSON)) {
            gson.toJson(rateDB, writer);
        } catch (IOException e) {
            System.out.println("Error guardando tarifas en JSON");
        }
    }

    private ArrayList<ParkingRate> loadFromFile() {
        try (Reader reader = new FileReader(FILE_PATH_JSON)) {
            Type listType = new TypeToken<ArrayList<ParkingRate>>() {
            }.getType();
            ArrayList<ParkingRate> data = gson.fromJson(reader, listType);
            return data != null ? data : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    // ===================== TXT =====================
    private void saveToTxt() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH_TXT))) {

            writer.write("========================================");
            writer.newLine();
            writer.write("      REGISTRO DE TARIFAS");
            writer.newLine();
            writer.write("========================================");
            writer.newLine();
            writer.write("Total de tarifas: " + rateDB.size());
            writer.newLine();
            writer.write("========================================");
            writer.newLine();
            writer.newLine();

            if (rateDB.isEmpty()) {
                writer.write("No hay tarifas registradas.");
                writer.newLine();
            } else {
                // Agrupar por parqueo para mejor lectura
                int contador = 1;
                int currentParkingId = -1;

                for (ParkingRate rate : rateDB) {

                    // Encabezado de parqueo cuando cambia
                    if (currentParkingId != rate.getParkingLotId()) {
                        if (currentParkingId != -1) {
                            writer.write("----------------------------------------");
                            writer.newLine();
                            writer.newLine();
                        }
                        writer.write("PARQUEO: " + rate.getParkingLotName());
                        writer.newLine();
                        writer.write("ID: " + rate.getParkingLotId());
                        writer.newLine();
                        writer.write("========================================");
                        writer.newLine();
                        currentParkingId = rate.getParkingLotId();
                        contador = 1;
                    }

                    writer.write("TARIFA #" + contador + " - " + rate.getVehicleType());
                    writer.newLine();
                    writer.write("----------------------------------------");
                    writer.newLine();
                    writer.write("Hora:        ₡" + String.format("%,.2f", rate.getHourPrice()));
                    writer.newLine();
                    writer.write("Media hora:  ₡" + String.format("%,.2f", rate.getHalfHourPrice()));
                    writer.newLine();
                    writer.write("Día:         ₡" + String.format("%,.2f", rate.getDayPrice()));
                    writer.newLine();
                    writer.write("Semana:      ₡" + String.format("%,.2f", rate.getWeekPrice()));
                    writer.newLine();
                    writer.write("Mes:         ₡" + String.format("%,.2f", rate.getMonthPrice()));
                    writer.newLine();
                    writer.write("Año:         ₡" + String.format("%,.2f", rate.getYearPrice()));
                    writer.newLine();
                    writer.write("----------------------------------------");
                    writer.newLine();
                    writer.newLine();

                    contador++;
                }
            }

            writer.write("========================================");
            writer.newLine();
            writer.write("Fin del registro");
            writer.newLine();
            writer.write("========================================");

        } catch (IOException e) {
            System.out.println("Error guardando tarifas en TXT: " + e.getMessage());
        }
    }
}
