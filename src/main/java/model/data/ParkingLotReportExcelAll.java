package model.data;

import model.entities.ParkingLot;
import model.entities.Space;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFChart;

import org.apache.poi.xddf.usermodel.chart.*;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ParkingLotReportExcelAll {

    public static void generate(ArrayList<ParkingLot> parkingLots) {

        File folder = new File("reports");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String filePath = "reports/reporte_general_parqueos.xlsx";

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {

            for (ParkingLot parkingLot : parkingLots) {

                // Nombre seguro para la hoja
                String safeName = parkingLot.getName()
                        .replace(" ", "_")
                        .replaceAll("[^a-zA-Z0-9_]", "");

                if (safeName.length() > 31) {
                    safeName = safeName.substring(0, 31); // Excel límite
                }

                XSSFSheet sheet = workbook.createSheet(safeName);

                int rowIndex = 0;

                // ================================
                // Fecha y datos generales
                // ================================
                String fechaHora = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

                Row row1 = sheet.createRow(rowIndex++);
                row1.createCell(0).setCellValue("Parqueo:");
                row1.createCell(1).setCellValue(parkingLot.getName());

                Row row2 = sheet.createRow(rowIndex++);
                row2.createCell(0).setCellValue("Fecha:");
                row2.createCell(1).setCellValue(fechaHora);

                rowIndex++;

                // ================================
                // Cálculos
                // ================================
                int total = parkingLot.getNumberOfSpaces();
                int ocupados = 0;

                for (Space space : parkingLot.getSpaces()) {
                    if (space.isSpaceTaken()) {
                        ocupados++;
                    }
                }

                int disponibles = total - ocupados;

                // ================================
                // Tabla resumen
                // ================================
                Row header = sheet.createRow(rowIndex++);
                header.createCell(0).setCellValue("Estado");
                header.createCell(1).setCellValue("Cantidad");

                int startDataRow = rowIndex;

                Row r1 = sheet.createRow(rowIndex++);
                r1.createCell(0).setCellValue("Ocupados");
                r1.createCell(1).setCellValue(ocupados);

                Row r2 = sheet.createRow(rowIndex++);
                r2.createCell(0).setCellValue("Disponibles");
                r2.createCell(1).setCellValue(disponibles);

                int endDataRow = rowIndex - 1;

                // ================================
                // Crear gráfico
                // ================================
                XSSFDrawing drawing = sheet.createDrawingPatriarch();

                XSSFClientAnchor anchor = drawing.createAnchor(
                        0, 0, 0, 0,
                        3, startDataRow,
                        10, startDataRow + 15
                );

                XSSFChart chart = drawing.createChart(anchor);
                chart.setTitleText("Ocupación del Parqueo");
                chart.setTitleOverlay(false);

                XDDFChartLegend legend = chart.getOrAddLegend();
                legend.setPosition(LegendPosition.RIGHT);

                XDDFDataSource<String> categories
                        = XDDFDataSourcesFactory.fromStringCellRange(
                                sheet,
                                new CellRangeAddress(startDataRow, endDataRow, 0, 0)
                        );

                XDDFNumericalDataSource<Double> values
                        = XDDFDataSourcesFactory.fromNumericCellRange(
                                sheet,
                                new CellRangeAddress(startDataRow, endDataRow, 1, 1)
                        );

                XDDFChartData data = chart.createData(ChartTypes.PIE, null, null);
                data.addSeries(categories, values);

                chart.plot(data);

                sheet.autoSizeColumn(0);
                sheet.autoSizeColumn(1);
            }

            FileOutputStream fileOut = new FileOutputStream(filePath);
            workbook.write(fileOut);
            fileOut.close();

            System.out.println("✅ Reporte Excel general generado correctamente");
            System.out.println("📁 Archivo: " + filePath);

        } catch (Exception e) {
            System.out.println("❌ Error generando Excel: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
