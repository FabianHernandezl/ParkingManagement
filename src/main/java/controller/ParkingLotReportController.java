package controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import model.data.IngresoReportExcel;
import model.data.ParkingLotReportPDF;
import model.entities.ParkingLot;
import model.entities.Space;
import model.entities.ParkingLotReportRow;
import model.data.ParkingLotReportExcelAll;
import model.data.TipoVehiculoReportExcel;
import model.entities.IngresoReportRow;
import model.entities.TipoVehiculoReportRow;

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

                String vehicleType = "-";
                if (space.isSpaceTaken()) {
                    if (space.getVehicleType() != null) {
                        vehicleType = space.getVehicleType().getDescription();
                    } else {
                        vehicleType = "Sin tipo";
                    }
                }

                String plate = "-";
                if (space.isSpaceTaken()) {
                    if (space.getVehicle() != null) {
                        plate = space.getVehicle().getPlate();
                    } else {
                        plate = "Sin placa";
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

    public void generateGeneralExcelReport() {

        ArrayList<ParkingLot> parkingLots
                = parkingLotController.getAllParkingLots();

        if (parkingLots.isEmpty()) {
            System.out.println("No hay parqueos registrados");
            return;
        }

        ParkingLotReportExcelAll.generate(parkingLots);
    }

    public void generarExcelIngresos(LocalDateTime inicio, LocalDateTime fin) {

        List<IngresoReportRow> data = obtenerDatosIngresos(inicio, fin);

        IngresoReportExcel.generate(data, inicio, fin);
    }

    public void generarExcelTipoVehiculo(LocalDateTime inicio, LocalDateTime fin) {

        List<TipoVehiculoReportRow> data = obtenerDatosTipoVehiculo(inicio, fin);

        int total = data.stream()
                .mapToInt(TipoVehiculoReportRow::getCantidad)
                .sum();

        TipoVehiculoReportExcel.generate(data, inicio, fin, total);
    }

  
    private List<IngresoReportRow> obtenerDatosIngresos(
            LocalDateTime inicio, LocalDateTime fin) {

        List<IngresoReportRow> lista = new ArrayList<>();

        ArrayList<ParkingLot> parkingLots
                = parkingLotController.getAllParkingLots();

        for (ParkingLot lot : parkingLots) {

            int cantidad = 0;
            double total = 0;

            for (Space space : lot.getSpaces()) {

                if (space.isSpaceTaken()
                        && space.getEntryTime() != null) {

                    LocalDateTime entry = space.getEntryTime()
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();

                    if (entry.isAfter(inicio)
                            && entry.isBefore(fin)) {

                        cantidad++;

                        if (space.getVehicleType() != null) {
                            total += space.getVehicleType().getFee();
                        }
                    }
                }
            }

            if (cantidad > 0) {
                lista.add(new IngresoReportRow(
                        lot.getName(),
                        cantidad,
                        total,
                        inicio,
                        fin
                ));
            }
        }

        return lista;
    }

    private List<TipoVehiculoReportRow> obtenerDatosTipoVehiculo(
            LocalDateTime inicio, LocalDateTime fin) {

        List<TipoVehiculoReportRow> lista = new ArrayList<>();

        int autos = 0;
        int motos = 0;
        int bicicletas = 0;
        int pesados = 0;

        ArrayList<ParkingLot> parkingLots
                = parkingLotController.getAllParkingLots();

        for (ParkingLot lot : parkingLots) {
            for (Space space : lot.getSpaces()) {

                if (space.isSpaceTaken()
                        && space.getEntryTime() != null
                        && space.getVehicleType() != null) {

                    LocalDateTime entry = space.getEntryTime()
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();

                    if (entry.isAfter(inicio)
                            && entry.isBefore(fin)) {

                        String tipo = space.getVehicleType()
                                .getDescription()
                                .toLowerCase();

                        if (tipo.contains("auto")) {
                            autos++;
                        } else if (tipo.contains("moto")) {
                            motos++;
                        } else if (tipo.contains("bici")) {
                            bicicletas++;
                        } else {
                            pesados++;
                        }
                    }
                }
            }
        }

        int total = autos + motos + bicicletas + pesados;

        if (total > 0) {
            lista.add(new TipoVehiculoReportRow("Autos", autos,
                    autos * 100.0 / total));
            lista.add(new TipoVehiculoReportRow("Motos", motos,
                    motos * 100.0 / total));
            lista.add(new TipoVehiculoReportRow("Bicicletas", bicicletas,
                    bicicletas * 100.0 / total));
            lista.add(new TipoVehiculoReportRow("Pesados", pesados,
                    pesados * 100.0 / total));
        }

        return lista;
    }

    // =========================
// 🔹 DEVOLVER DATOS INGRESOS 
// =========================
    public List<IngresoReportRow> generarReporteIngresos(
            LocalDateTime inicio, LocalDateTime fin) {

        return obtenerDatosIngresos(inicio, fin);
    }

// =========================
// 🔹 DEVOLVER DATOS TIPO VEHICULO 
// =========================
    public List<TipoVehiculoReportRow> generarReporteTipoVehiculo(
            LocalDateTime inicio, LocalDateTime fin) {

        return obtenerDatosTipoVehiculo(inicio, fin);
    }
}
