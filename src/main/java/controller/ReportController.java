package controller;

import model.entities.Ticket;
import model.entities.ParkingLot;
import model.entities.IngresoReportRow;
import model.entities.TipoVehiculoReportRow;
import model.data.IngresoReportPDF;
import model.data.TipoVehiculoReportPDF;
import util.TxtTicketUtil;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ReportController {

    private final TicketController ticketController = TicketController.getInstance();
    private final ParkingLotController parkingLotController = new ParkingLotController();

    /**
     * Reporte 1: Ingresos por parqueo en un rango de fechas
     */
    public List<IngresoReportRow> generarReporteIngresos(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<IngresoReportRow> reportRows = new ArrayList<>();
        List<Ticket> ticketsCerrados = ticketController.getAllTicketsFromTxt();

        // Filtrar por fecha
        List<Ticket> ticketsFiltrados = ticketsCerrados.stream()
                .filter(t -> t.getExitTime() != null)
                .filter(t -> !t.getExitTime().isBefore(fechaInicio) && !t.getExitTime().isAfter(fechaFin))
                .collect(Collectors.toList());

        // Agrupar por parqueo
        Map<String, List<Ticket>> ticketsPorParqueo = ticketsFiltrados.stream()
                .filter(t -> t.getParkingLot() != null)
                .collect(Collectors.groupingBy(t -> t.getParkingLot().getName()));

        for (Map.Entry<String, List<Ticket>> entry : ticketsPorParqueo.entrySet()) {
            String nombreParqueo = entry.getKey();
            List<Ticket> tickets = entry.getValue();

            int cantidadVehiculos = tickets.size();
            double totalRecaudado = tickets.stream().mapToDouble(Ticket::getTotal).sum();

            reportRows.add(new IngresoReportRow(
                    nombreParqueo,
                    cantidadVehiculos,
                    totalRecaudado,
                    fechaInicio,
                    fechaFin
            ));
        }

        // Ordenar por mayor recaudación
        reportRows.sort((a, b) -> Double.compare(b.getTotalRecaudado(), a.getTotalRecaudado()));

        return reportRows;
    }

    /**
     * Genera y exporta a PDF el reporte de ingresos
     */
    public void generarPDFIngresos(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<IngresoReportRow> reportRows = generarReporteIngresos(fechaInicio, fechaFin);
        IngresoReportPDF.generate(reportRows, fechaInicio, fechaFin);
    }

    /**
     * Reporte 2: Tipo de vehículo más frecuente
     */
    public List<TipoVehiculoReportRow> generarReporteTipoVehiculo(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<Ticket> ticketsCerrados = ticketController.getAllTicketsFromTxt();

        // Filtrar por fecha
        List<Ticket> ticketsFiltrados = ticketsCerrados.stream()
                .filter(t -> t.getExitTime() != null)
                .filter(t -> !t.getExitTime().isBefore(fechaInicio) && !t.getExitTime().isAfter(fechaFin))
                .collect(Collectors.toList());

        Map<String, Integer> contadorPorTipo = new HashMap<>();
        contadorPorTipo.put("Autos", 0);
        contadorPorTipo.put("Motos", 0);
        contadorPorTipo.put("Bicicletas", 0);
        contadorPorTipo.put("Pesados", 0);

        for (Ticket ticket : ticketsFiltrados) {
            if (ticket.getVehicle() != null && ticket.getVehicle().getVehicleType() != null) {
                String tipo = ticket.getVehicle().getVehicleType().getDescription().toLowerCase();

                if (tipo.contains("auto") || tipo.contains("automóvil") || tipo.contains("carro")) {
                    contadorPorTipo.put("Autos", contadorPorTipo.get("Autos") + 1);
                } else if (tipo.contains("moto") || tipo.contains("motocicleta")) {
                    contadorPorTipo.put("Motos", contadorPorTipo.get("Motos") + 1);
                } else if (tipo.contains("bici") || tipo.contains("bicicleta")) {
                    contadorPorTipo.put("Bicicletas", contadorPorTipo.get("Bicicletas") + 1);
                } else if (tipo.contains("camión") || tipo.contains("pesado") || tipo.contains("truck")) {
                    contadorPorTipo.put("Pesados", contadorPorTipo.get("Pesados") + 1);
                }
            }
        }

        int totalVehiculos = ticketsFiltrados.size();
        List<TipoVehiculoReportRow> reportRows = new ArrayList<>();

        if (contadorPorTipo.get("Autos") > 0) {
            reportRows.add(new TipoVehiculoReportRow("Autos", contadorPorTipo.get("Autos"), totalVehiculos, fechaInicio, fechaFin));
        }
        if (contadorPorTipo.get("Motos") > 0) {
            reportRows.add(new TipoVehiculoReportRow("Motos", contadorPorTipo.get("Motos"), totalVehiculos, fechaInicio, fechaFin));
        }
        if (contadorPorTipo.get("Bicicletas") > 0) {
            reportRows.add(new TipoVehiculoReportRow("Bicicletas", contadorPorTipo.get("Bicicletas"), totalVehiculos, fechaInicio, fechaFin));
        }
        if (contadorPorTipo.get("Pesados") > 0) {
            reportRows.add(new TipoVehiculoReportRow("Pesados", contadorPorTipo.get("Pesados"), totalVehiculos, fechaInicio, fechaFin));
        }

        // Ordenar por mayor cantidad
        reportRows.sort((a, b) -> b.getCantidad() - a.getCantidad());

        return reportRows;
    }

    /**
     * Genera y exporta a PDF el reporte de tipo de vehículo
     */
    public void generarPDFTipoVehiculo(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<TipoVehiculoReportRow> reportRows = generarReporteTipoVehiculo(fechaInicio, fechaFin);
        int totalVehiculos = reportRows.stream().mapToInt(TipoVehiculoReportRow::getCantidad).sum();
        TipoVehiculoReportPDF.generate(reportRows, fechaInicio, fechaFin, totalVehiculos);
    }

    /**
     * Obtener resumen general
     */
    public String generarResumenGeneral(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<Ticket> tickets = ticketController.getAllTicketsFromTxt().stream()
                .filter(t -> t.getExitTime() != null)
                .filter(t -> !t.getExitTime().isBefore(fechaInicio) && !t.getExitTime().isAfter(fechaFin))
                .collect(Collectors.toList());

        int totalVehiculos = tickets.size();
        double totalIngresos = tickets.stream().mapToDouble(Ticket::getTotal).sum();

        return String.format(
                "RESUMEN GENERAL\n"
                + "================\n"
                + "Período: %s - %s\n"
                + "Total vehículos: %d\n"
                + "Total ingresos: ₡%.2f\n"
                + "Promedio por vehículo: ₡%.2f",
                fechaInicio.toLocalDate().toString(),
                fechaFin.toLocalDate().toString(),
                totalVehiculos,
                totalIngresos,
                totalVehiculos > 0 ? totalIngresos / totalVehiculos : 0
        );
    }
}
