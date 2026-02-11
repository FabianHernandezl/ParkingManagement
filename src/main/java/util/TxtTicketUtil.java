package util;

import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import model.entities.Ticket;

public class TxtTicketUtil {

    public static String generarTicketTXT(
            String cliente,
            String vehiculo,
            String placa,
            String espacio,
            double monto,
            int ticketId
    ) {
        String ruta = "data/ticket_" + ticketId + ".txt";

        try (FileWriter writer = new FileWriter(ruta)) {
            writer.write("==============================\n");
            writer.write("        TICKET DE PARQUEO\n");
            writer.write("==============================\n");
            writer.write("Ticket #: " + ticketId + "\n");
            writer.write("Cliente: " + cliente + "\n");
            writer.write("Vehículo: " + vehiculo + "\n");
            writer.write("Placa: " + placa + "\n");
            writer.write("Espacio: " + espacio + "\n");
            writer.write("Monto a pagar: ₡" + monto + "\n");
            writer.write("==============================\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ruta;
    }
}
