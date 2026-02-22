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

import model.entities.TipoVehiculoReportRow;

public class TipoVehiculoReportPDF {

    public static void generate(List<TipoVehiculoReportRow> reportData,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            int totalVehiculos) {

        File folder = new File("reports");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filePath = "reports/reporte_tipo_vehiculo_" + timestamp + ".pdf";

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
            Paragraph title = new Paragraph("REPORTE DE TIPO DE VEHÍCULO MÁS FRECUENTE")
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
            document.add(new Paragraph("Total vehículos en el período: " + totalVehiculos)
                    .setBold()
                    .setFontSize(14));
            document.add(new Paragraph(" "));

            // =============================
            // Tabla de distribución
            // =============================
            Table table = new Table(UnitValue.createPercentArray(new float[]{3, 2, 2}))
                    .useAllAvailableWidth();

            // Encabezados
            table.addHeaderCell(new Cell()
                    .add(new Paragraph("Tipo de Vehículo").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell()
                    .add(new Paragraph("Cantidad").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell()
                    .add(new Paragraph("Porcentaje").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));

            // Colores para cada tipo
            DeviceRgb colorAuto = new DeviceRgb(33, 150, 243);    // Azul
            DeviceRgb colorMoto = new DeviceRgb(76, 175, 80);     // Verde
            DeviceRgb colorBici = new DeviceRgb(255, 193, 7);     // Amarillo
            DeviceRgb colorPesado = new DeviceRgb(156, 39, 176);  // Morado

            // Datos
            for (TipoVehiculoReportRow row : reportData) {
                DeviceRgb color;
                switch (row.getTipoVehiculo().toLowerCase()) {
                    case "autos":
                    case "automóvil":
                    case "carro":
                        color = colorAuto;
                        break;
                    case "motos":
                    case "motocicleta":
                        color = colorMoto;
                        break;
                    case "bicicletas":
                        color = colorBici;
                        break;
                    case "pesados":
                    case "camión":
                        color = colorPesado;
                        break;
                    default:
                        color = new DeviceRgb(158, 158, 158); // Gris
                }

                table.addCell(new Cell()
                        .add(new Paragraph(row.getTipoVehiculo()).setBold())
                        .setBackgroundColor(color)
                        .setFontColor(ColorConstants.WHITE));

                table.addCell(new Cell()
                        .add(new Paragraph(String.valueOf(row.getCantidad())))
                        .setBackgroundColor(color)
                        .setFontColor(ColorConstants.WHITE));

                table.addCell(new Cell()
                        .add(new Paragraph(String.format("%.2f %%", row.getPorcentaje())))
                        .setBackgroundColor(color)
                        .setFontColor(ColorConstants.WHITE));
            }

            document.add(table);

            // =============================
            // Gráfico de barras
            // =============================
            document.add(new Paragraph(" "));
            Paragraph chartTitle = new Paragraph("Distribución Porcentual")
                    .setBold()
                    .setFontSize(14);
            document.add(chartTitle);
            document.add(new Paragraph(" "));

            for (TipoVehiculoReportRow row : reportData) {
                int barras = (int) (row.getPorcentaje() / 5); // 5% por barra

                StringBuilder barra = new StringBuilder();
                for (int i = 0; i < barras; i++) {
                    barra.append("█");
                }

                Paragraph barParagraph = new Paragraph(
                        String.format("%-15s [%-20s] %5.2f%% (%d vehículos)",
                                row.getTipoVehiculo(),
                                barra.toString(),
                                row.getPorcentaje(),
                                row.getCantidad()))
                        .setFontSize(10);

                document.add(barParagraph);
            }

            // =============================
            // Análisis
            // =============================
            document.add(new Paragraph(" "));
            Paragraph analysisTitle = new Paragraph("Análisis de Demanda")
                    .setBold()
                    .setFontSize(14);
            document.add(analysisTitle);
            document.add(new Paragraph(" "));

            // Encontrar el tipo más frecuente
            TipoVehiculoReportRow maxRow = reportData.stream()
                    .max((a, b) -> Double.compare(a.getPorcentaje(), b.getPorcentaje()))
                    .orElse(null);

            if (maxRow != null && maxRow.getPorcentaje() > 0) {
                document.add(new Paragraph("✓ El tipo de vehículo más frecuente es: "
                        + maxRow.getTipoVehiculo().toUpperCase()
                        + " con un " + String.format("%.2f", maxRow.getPorcentaje()) + "% de ocupación"));
            }

            // Recomendaciones
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Recomendaciones:").setBold());

            for (TipoVehiculoReportRow row : reportData) {
                if (row.getPorcentaje() > 50) {
                    document.add(new Paragraph("  • La alta demanda de " + row.getTipoVehiculo()
                            + " sugiere considerar más espacios para este tipo."));
                } else if (row.getPorcentaje() < 10 && row.getCantidad() > 0) {
                    document.add(new Paragraph("  • La baja demanda de " + row.getTipoVehiculo()
                            + " podría reasignarse a otros tipos."));
                }
            }

            document.close();

            System.out.println("✅ Reporte de tipo de vehículo generado correctamente: " + filePath);

        } catch (Exception e) {
            System.out.println("❌ Error generando reporte PDF de tipo de vehículo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
