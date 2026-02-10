/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.data;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import java.io.File;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import model.entities.ParkingLot;
import model.entities.Space;
import model.entities.Vehicle;

/**
 *
 * @author jimen
 */
public class ParkingLotReportPDF {

    public static void generate(ParkingLot parkingLot) {

        // Crear carpeta si no existe
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

            String fechaHora = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

            Paragraph title = new Paragraph("Reporte de Ocupación del Parqueo")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setFontSize(16);

            document.add(title);
            document.add(new Paragraph("Nombre del parqueo: " + parkingLot.getName()));
            document.add(new Paragraph("Fecha y hora del reporte: " + fechaHora));
            document.add(new Paragraph(" "));

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

            document.add(new Paragraph("Total de espacios: " + total));
            document.add(new Paragraph("Espacios ocupados: " + ocupados));
            document.add(new Paragraph("Espacios disponibles: " + disponibles));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Ocupación por tipo de vehículo:").setBold());

            for (String tipo : ocupacionPorTipo.keySet()) {
                document.add(new Paragraph("- " + tipo + ": " + ocupacionPorTipo.get(tipo)));
            }

            document.close();

            System.out.println("✅ Reporte generado: " + filePath);

        } catch (Exception e) {
            System.out.println("❌ Error generando reporte PDF: " + e.getMessage());
        }
    }

}
