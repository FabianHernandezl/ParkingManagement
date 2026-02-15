package model.data;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.borders.SolidBorder;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import model.entities.ParkingLot;
import model.entities.Space;

public class ParkingLotReportPDF {

    public static void generate(ParkingLot parkingLot) {

        File folder = new File("reports");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String safeName = parkingLot.getName()
                .replace(" ", "_")
                .replaceAll("[^a-zA-Z0-9_]", "");

        String filePath = "reports/reporte_parqueo_"
                + parkingLot.getId() + "_" + safeName + ".pdf";

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
            Paragraph title = new Paragraph("REPORTE DE OCUPACIÓN")
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
            document.add(new Paragraph("Parqueo: " + parkingLot.getName()).setBold());
            document.add(new Paragraph("Generado por: Sistema de Gestión de Parqueos"));
            document.add(new Paragraph("Fecha y hora: " + fechaHora)
                    .setFontColor(ColorConstants.GRAY));

            document.add(new Paragraph(" ")
                    .setBorderBottom(new SolidBorder(ColorConstants.GRAY, 1)));

            document.add(new Paragraph(" "));

            // =============================
            // Cálculos
            // =============================
            int total = parkingLot.getNumberOfSpaces();
            int ocupados = 0;

            HashMap<String, Integer> ocupacionPorTipo = new HashMap<>();

            for (Space space : parkingLot.getSpaces()) {
                if (space.isSpaceTaken()) {
                    ocupados++;
                    String tipo = space.getVehicleType().getDescription();
                    ocupacionPorTipo.put(tipo,
                            ocupacionPorTipo.getOrDefault(tipo, 0) + 1);
                }
            }

            int disponibles = total - ocupados;

            double porcentaje = total == 0 ? 0 : (ocupados * 100.0) / total;

            // =============================
            // Color según ocupación
            // =============================
            DeviceRgb colorEstado;

            if (porcentaje <= 40) {
                colorEstado = new DeviceRgb(76, 175, 80); // Verde
            } else if (porcentaje <= 70) {
                colorEstado = new DeviceRgb(255, 193, 7); // Amarillo
            } else {
                colorEstado = new DeviceRgb(244, 67, 54); // Rojo
            }

            // =============================
            // Tabla resumen
            // =============================
            Table table = new Table(UnitValue.createPercentArray(new float[]{3, 2}))
                    .useAllAvailableWidth();

            table.addHeaderCell(new Cell()
                    .add(new Paragraph("Concepto").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));

            table.addHeaderCell(new Cell()
                    .add(new Paragraph("Cantidad").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));

            table.addCell("Total de espacios");
            table.addCell(String.valueOf(total));

            table.addCell("Espacios ocupados");
            table.addCell(String.valueOf(ocupados));

            table.addCell("Espacios disponibles");
            table.addCell(String.valueOf(disponibles));

            table.addCell("Porcentaje de ocupación");
            table.addCell(new Cell()
                    .add(new Paragraph(String.format("%.2f %%", porcentaje))
                            .setBold()
                            .setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(colorEstado));

            document.add(table);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Ocupación por tipo de vehículo")
                    .setBold()
                    .setFontSize(14));

            document.add(new Paragraph(" "));

            // =============================
            // Tabla por tipo
            // =============================
            Table tableTipos = new Table(UnitValue.createPercentArray(new float[]{3, 2}))
                    .useAllAvailableWidth();

            tableTipos.addHeaderCell(new Cell()
                    .add(new Paragraph("Tipo de vehículo").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));

            tableTipos.addHeaderCell(new Cell()
                    .add(new Paragraph("Cantidad").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));

            for (String tipo : ocupacionPorTipo.keySet()) {
                tableTipos.addCell(tipo);
                tableTipos.addCell(String.valueOf(ocupacionPorTipo.get(tipo)));
            }

            document.add(tableTipos);

            document.close();

            System.out.println("✅ Reporte generado correctamente: " + filePath);

        } catch (Exception e) {
            System.out.println("❌ Error generando reporte PDF: " + e.getMessage());
        }
    }
}
