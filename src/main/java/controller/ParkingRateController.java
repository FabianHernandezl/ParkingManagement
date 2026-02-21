package controller;

import model.data.ParkingRateData;
import model.entities.ParkingLot;
import model.entities.ParkingRate;
import java.util.ArrayList;
import java.util.List;

public class ParkingRateController {

    private final ParkingRateData rateData;

    public ParkingRateController() {
        rateData = new ParkingRateData();
    }

    public String insertParkingRate(ParkingRate rate) {
        if (!validatePrices(rate)) {
            return "❌ Todos los precios deben ser mayores a 0";
        }
        return rateData.insertParkingRate(rate);
    }

    public ArrayList<ParkingRate> getAllParkingRates() {
        return rateData.getAllParkingRates();
    }

    public ArrayList<ParkingRate> getParkingRatesByParkingLot(ParkingLot parkingLot) {
        return rateData.getParkingRatesByParkingLot(parkingLot);
    }

    public ParkingRate getParkingRateByParkingLotAndType(int parkingLotId, String vehicleType) {
        ArrayList<ParkingRate> rates = rateData.getAllParkingRates();

        // Normalizar el tipo buscado
        String tipoBuscado = normalizarTipo(vehicleType);

        for (ParkingRate rate : rates) {
            if (rate.getParkingLotId() == parkingLotId) {
                String tipoRate = normalizarTipo(rate.getVehicleType());
                if (tipoRate.equals(tipoBuscado)) {
                    return rate;
                }
            }
        }
        return null;
    }

    private String normalizarTipo(String tipo) {
        if (tipo == null) {
            return "";
        }

        if (tipo.equalsIgnoreCase("Automóvil")
                || tipo.equalsIgnoreCase("Auto")
                || tipo.equalsIgnoreCase("Carro")) {
            return "CARRO";
        }
        if (tipo.equalsIgnoreCase("Motocicleta")
                || tipo.equalsIgnoreCase("Moto")) {
            return "MOTO";
        }
        if (tipo.equalsIgnoreCase("Camión")
                || tipo.equalsIgnoreCase("Camion")
                || tipo.equalsIgnoreCase("Pesado")) {
            return "CAMION";
        }

        return tipo.toUpperCase();
    }

    public String updateParkingRate(ParkingRate rate) {
        if (!validatePrices(rate)) {
            return "❌ Todos los precios deben ser mayores a 0";
        }
        return rateData.updateParkingRate(rate) ? "✅ Tarifa actualizada" : "❌ Error al actualizar";
    }

    public String deleteParkingRate(int parkingLotId, String vehicleType) {
        return rateData.deleteParkingRate(parkingLotId, vehicleType)
                ? "✅ Tarifa eliminada" : "❌ No se encontró la tarifa";
    }

    private boolean validatePrices(ParkingRate rate) {
        return rate.getHourPrice() > 0
                && rate.getHalfHourPrice() > 0
                && rate.getDayPrice() > 0
                && rate.getWeekPrice() > 0
                && rate.getMonthPrice() > 0
                && rate.getYearPrice() > 0;
    }

    public ArrayList<String> getAvailableVehicleTypes() {
        ArrayList<String> types = new ArrayList<>();
        types.add("Carro");
        types.add("Moto");
        types.add("Camión");
        types.add("Bicicleta");
        return types;
    }

    public boolean parkingLotHasRates(int parkingLotId) {
        List<ParkingRate> rates = getAllParkingRates();
        for (ParkingRate rate : rates) {
            if (rate.getParkingLotId() == parkingLotId) {
                return true;
            }
        }
        return false;
    }

    public String getParkingLotRatesStatus(int parkingLotId) {
        List<ParkingRate> rates = getAllParkingRates();
        StringBuilder sb = new StringBuilder();
        for (ParkingRate rate : rates) {
            if (rate.getParkingLotId() == parkingLotId) {
                sb.append("  - ").append(rate.getVehicleType())
                        .append(": ₡").append(rate.getHourPrice())
                        .append("/hora\n");
            }
        }
        return sb.toString();
    }

    // 🔥 Método de depuración
    public void debugTarifas() {
        System.out.println("=== DEBUG TARIFAS ===");
        ArrayList<ParkingRate> todas = getAllParkingRates();
        System.out.println("Total tarifas en sistema: " + todas.size());

        for (ParkingRate rate : todas) {
            System.out.println("Tarifa - Parqueo ID: " + rate.getParkingLotId()
                    + ", Tipo: " + rate.getVehicleType()
                    + ", Precio: " + rate.getHourPrice());
        }
        System.out.println("=====================");
    }
}
