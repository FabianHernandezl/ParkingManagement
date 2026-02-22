/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.util.ArrayList;
import java.util.List;

import model.data.ParkingLotReportPDF;
import model.entities.ParkingLot;
import model.entities.Space;
import model.entities.ParkingLotReportRow;

public class ParkingLotReportController {

    private ParkingLotController parkingLotController
            = new ParkingLotController();

    // =========================
    // 🔹 PDF: UN parqueo
    // =========================
    public void generateOccupationReport(int parkingLotId) {

        ParkingLot parkingLot
                = parkingLotController.findParkingLotById(parkingLotId);

        if (parkingLot == null) {
            System.out.println("Parqueo no encontrado");
            return;
        }

        ParkingLotReportPDF.generate(parkingLot);
    }

    // =========================
    // 🔹 PDF: TODOS los parqueos
    // =========================
    public void generateOccupationReportForAll() {

        ArrayList<ParkingLot> parkingLots
                = parkingLotController.getAllParkingLots();

        if (parkingLots.isEmpty()) {
            System.out.println("No hay parqueos registrados");
            return;
        }

        for (ParkingLot parkingLot : parkingLots) {
            ParkingLotReportPDF.generate(parkingLot);
        }

        System.out.println("✅ Todos los reportes fueron generados");
    }

    // =========================
    // 🔹 DATOS PARA LA TABLA (UI)
    // =========================
    public List<ParkingLotReportRow> getOccupationReportRows() {

        List<ParkingLotReportRow> rows = new ArrayList<>();

        ArrayList<ParkingLot> parkingLots
                = parkingLotController.getAllParkingLots();

        for (ParkingLot parkingLot : parkingLots) {

            for (Space space : parkingLot.getSpaces()) {

                String status = space.isSpaceTaken()
                        ? "Ocupado"
                        : "Disponible";

                // 🔥 Validación para vehicleType
                String vehicleType = "-";
                if (space.isSpaceTaken()) {
                    if (space.getVehicleType() != null) {
                        vehicleType = space.getVehicleType().getDescription();
                    } else {
                        vehicleType = "Sin tipo";
                        System.out.println("DEBUG: Espacio " + space.getId()
                                + " ocupado pero sin tipo de vehículo");
                    }
                }

                // 🔥 Validación para plate
                String plate = "-";
                if (space.isSpaceTaken()) {
                    if (space.getVehicle() != null) {
                        plate = space.getVehicle().getPlate();
                    } else {
                        plate = "Sin placa";
                        System.out.println("DEBUG: Espacio " + space.getId()
                                + " ocupado pero sin vehículo");
                    }
                }

                ParkingLotReportRow row = new ParkingLotReportRow(
                        parkingLot.getName(),
                        space.getId(),
                        status,
                        vehicleType,
                        plate
                );

                rows.add(row);
            }
        }

        return rows;
    }
}
