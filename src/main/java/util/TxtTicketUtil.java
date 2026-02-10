package util;

import java.io.FileWriter;
import java.io.IOException;

public class TxtTicketUtil {

    public static String generarTicketTXT(
            String cliente,
            String vehiculo,
            String placa,
            String espacio,
            double monto
    ) {

        String ruta = "ticket_parqueo.txt";

        try (FileWriter writer = new FileWriter(ruta)) {

            writer.write("TICKET DE PARQUEO\n");
            writer.write("========================\n");
            writer.write("Cliente: " + cliente + "\n");
            writer.write("Vehículo: " + vehiculo + "\n");
            writer.write("Placa: " + placa + "\n");
            writer.write("Espacio: " + espacio + "\n");
            writer.write("Monto a pagar: ₡" + monto + "\n");
            writer.write("========================\n");
            writer.write("Gracias por su visita\n");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return ruta;
    }
}
