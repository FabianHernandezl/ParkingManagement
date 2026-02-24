package model.data;

import model.entities.TipoVehiculoReportRow;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TipoVehiculoReportExcel {

    public static void generate(List<TipoVehiculoReportRow> reportData,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            int totalVehiculos) {

        try {

            File folder = new File("reports");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filePath = "reports/reporte_tipo_vehiculo_" + timestamp + ".xlsx";

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Tipo Vehículo");

            int rowIndex = 0;

            // ======= TÍTULO =======
            Row titleRow = sheet.createRow(rowIndex++);
            titleRow.createCell(0).setCellValue("REPORTE DE TIPO DE VEHÍCULO");

            rowIndex++;

            // ======= TOTAL GENERAL =======
            Row totalRowHeader = sheet.createRow(rowIndex++);
            totalRowHeader.createCell(0).setCellValue("Total vehículos en período:");
            totalRowHeader.createCell(1).setCellValue(totalVehiculos);

            rowIndex++;

            // ======= ENCABEZADOS =======
            Row headerRow = sheet.createRow(rowIndex++);
            headerRow.createCell(0).setCellValue("Tipo Vehículo");
            headerRow.createCell(1).setCellValue("Cantidad");
            headerRow.createCell(2).setCellValue("Porcentaje (%)");

            // ======= DATOS =======
            for (TipoVehiculoReportRow row : reportData) {
                Row dataRow = sheet.createRow(rowIndex++);
                dataRow.createCell(0).setCellValue(row.getTipoVehiculo());
                dataRow.createCell(1).setCellValue(row.getCantidad());
                dataRow.createCell(2).setCellValue(row.getPorcentaje());
            }

            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i);
            }

            FileOutputStream fileOut = new FileOutputStream(filePath);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();

            System.out.println("✅ Excel de tipo de vehículo generado: " + filePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
