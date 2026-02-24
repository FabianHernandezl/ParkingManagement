package model.data;

import model.entities.IngresoReportRow;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class IngresoReportExcel {

    public static void generate(List<IngresoReportRow> reportData,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {

        try {

            File folder = new File("reports");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filePath = "reports/reporte_ingresos_" + timestamp + ".xlsx";

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Ingresos");

            int rowIndex = 0;

            // ======= TÍTULO =======
            Row titleRow = sheet.createRow(rowIndex++);
            titleRow.createCell(0).setCellValue("REPORTE DE INGRESOS POR PARQUEO");

            rowIndex++;

            // ======= ENCABEZADOS =======
            Row headerRow = sheet.createRow(rowIndex++);
            headerRow.createCell(0).setCellValue("Parqueo");
            headerRow.createCell(1).setCellValue("Vehículos");
            headerRow.createCell(2).setCellValue("Total Recaudado");
            headerRow.createCell(3).setCellValue("Promedio");

            // ======= DATOS =======
            for (IngresoReportRow row : reportData) {
                Row dataRow = sheet.createRow(rowIndex++);
                dataRow.createCell(0).setCellValue(row.getParkingLotName());
                dataRow.createCell(1).setCellValue(row.getCantidadVehiculos());
                dataRow.createCell(2).setCellValue(row.getTotalRecaudado());
                dataRow.createCell(3).setCellValue(row.getPromedioPorVehiculo());
            }

            // ======= TOTALES =======
            rowIndex++;
            Row totalRow = sheet.createRow(rowIndex);

            int totalVehiculos = reportData.stream()
                    .mapToInt(IngresoReportRow::getCantidadVehiculos)
                    .sum();

            double totalIngresos = reportData.stream()
                    .mapToDouble(IngresoReportRow::getTotalRecaudado)
                    .sum();

            totalRow.createCell(0).setCellValue("TOTAL GENERAL");
            totalRow.createCell(1).setCellValue(totalVehiculos);
            totalRow.createCell(2).setCellValue(totalIngresos);

            // Ajustar tamaño columnas
            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }

            FileOutputStream fileOut = new FileOutputStream(filePath);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();

            System.out.println("✅ Excel de ingresos generado: " + filePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
