/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.data;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import model.entities.IngresoReportRow;

public class IngresoReportPDF {

    public static void generate(List<IngresoReportRow> reportData,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {

        File folder = new File("reports");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filePath = "reports/reporte_ingresos_" + timestamp + ".pdf";

        try {

            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // =============================
            // Fecha y hora
            // =============================
            String fechaHora = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

            // =============================
            // Título
            // =============================
            Paragraph title = new Paragraph("REPORTE DE INGRESOS POR PARQUEO")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20)
                    .setBold()
                    .setFontColor(ColorConstants.WHITE)
                    .setBackgroundColor(ColorConstants.DARK_GRAY)
                    .setPadding(10);

            document.add(title);
            document.add(new Paragraph(" "));

            // =============================
            // Información general
            // =============================
            document.add(new Paragraph("Generado por: Sistema de Gestión de Parqueos").setBold());
            document.add(new Paragraph("Fecha y hora: " + fechaHora)
                    .setFontColor(ColorConstants.GRAY));

            // =============================
            // Rango de fechas
            // =============================
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            document.add(new Paragraph(" ")
                    .setBorderBottom(new SolidBorder(ColorConstants.GRAY, 1)));
            document.add(new Paragraph("Período analizado: "
                    + fechaInicio.format(formatter) + " - " + fechaFin.format(formatter))
                    .setBold()
                    .setFontSize(12));
            document.add(new Paragraph(" ")
                    .setBorderBottom(new SolidBorder(ColorConstants.GRAY, 1)));

            document.add(new Paragraph(" "));

            // =============================
            // Totales generales
            // =============================
            int totalVehiculosGeneral = reportData.stream()
                    .mapToInt(IngresoReportRow::getCantidadVehiculos)
                    .sum();
            double totalIngresosGeneral = reportData.stream()
                    .mapToDouble(IngresoReportRow::getTotalRecaudado)
                    .sum();

            Table resumenTable = new Table(UnitValue.createPercentArray(new float[]{3, 2}))
                    .useAllAvailableWidth();

            resumenTable.addHeaderCell(new Cell()
                    .add(new Paragraph("Concepto General").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            resumenTable.addHeaderCell(new Cell()
                    .add(new Paragraph("Valor").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));

            resumenTable.addCell("Total vehículos atendidos");
            resumenTable.addCell(String.valueOf(totalVehiculosGeneral));

            resumenTable.addCell("Total recaudado");
            resumenTable.addCell(String.format("₡%,.2f", totalIngresosGeneral));

            resumenTable.addCell("Promedio por vehículo");
            resumenTable.addCell(totalVehiculosGeneral > 0
                    ? String.format("₡%,.2f", totalIngresosGeneral / totalVehiculosGeneral)
                    : "₡0.00");

            document.add(resumenTable);
            document.add(new Paragraph(" "));

            // =============================
            // Tabla detalle por parqueo
            // =============================
            Paragraph subtitle = new Paragraph("Detalle por Parqueo")
                    .setBold()
                    .setFontSize(14);
            document.add(subtitle);
            document.add(new Paragraph(" "));

            Table table = new Table(UnitValue.createPercentArray(new float[]{3, 2, 3, 2}))
                    .useAllAvailableWidth();

            // Encabezados
            table.addHeaderCell(new Cell()
                    .add(new Paragraph("Parqueo").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell()
                    .add(new Paragraph("Vehículos").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell()
                    .add(new Paragraph("Total Recaudado").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell()
                    .add(new Paragraph("Promedio").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));

            // Datos
            for (IngresoReportRow row : reportData) {
                table.addCell(row.getParkingLotName());
                table.addCell(String.valueOf(row.getCantidadVehiculos()));
                table.addCell(String.format("₡%,.2f", row.getTotalRecaudado()));
                table.addCell(String.format("₡%,.2f", row.getPromedioPorVehiculo()));
            }

            document.add(table);

            // =============================
            // Gráfico de barras simple (texto)
            // =============================
            document.add(new Paragraph(" "));
            Paragraph chartTitle = new Paragraph("Distribución de Ingresos")
                    .setBold()
                    .setFontSize(14);
            document.add(chartTitle);
            document.add(new Paragraph(" "));

            double maxIngreso = reportData.stream()
                    .mapToDouble(IngresoReportRow::getTotalRecaudado)
                    .max()
                    .orElse(1);

            for (IngresoReportRow row : reportData) {
                double porcentaje = (row.getTotalRecaudado() / maxIngreso) * 100;
                int barras = (int) (porcentaje / 10);

                StringBuilder barra = new StringBuilder();
                for (int i = 0; i < barras; i++) {
                    barra.append("█");
                }

                Paragraph barParagraph = new Paragraph(
                        String.format("%-20s [%-20s] ₡%,.2f (%d vehículos)",
                                row.getParkingLotName(),
                                barra.toString(),
                                row.getTotalRecaudado(),
                                row.getCantidadVehiculos()))
                        .setFontSize(10);

                document.add(barParagraph);
            }

            document.close();

            System.out.println("✅ Reporte de ingresos generado correctamente: " + filePath);

        } catch (Exception e) {
            System.out.println("❌ Error generando reporte PDF de ingresos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
