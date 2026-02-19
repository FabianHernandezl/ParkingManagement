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

                String vehicleType = space.isSpaceTaken()
                        ? space.getVehicleType().getDescription()
                        : "-";

                String plate = space.isSpaceTaken()
                        ? space.getVehicle().getPlate()
                        : "-";

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
