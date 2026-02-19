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
        return rateData.getParkingRateByParkingLotAndType(parkingLotId, vehicleType);
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
        return types;
    }

    // En ParkingRateController.java
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
}
