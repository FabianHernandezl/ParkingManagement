package util;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import model.entities.Ticket;

public class TxtTicketUtil {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Genera un ticket individual en TXT
     */
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

    /**
     * Lee todos los tickets históricos desde los archivos TXT en la carpeta
     * "data"
     */
    public static ArrayList<Ticket> leerTicketsTXT() {
        ArrayList<Ticket> tickets = new ArrayList<>();
        File folder = new File("data");
        if (!folder.exists() || !folder.isDirectory()) {
            return tickets;
        }

        File[] files = folder.listFiles((dir, name) -> name.startsWith("ticket_") && name.endsWith(".txt"));
        if (files == null) {
            return tickets;
        }

        for (File file : files) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                Ticket ticket = new Ticket();
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.startsWith("Ticket #:")) {
                        ticket.setId(Integer.parseInt(line.split(":")[1].trim()));
                    } else if (line.startsWith("Placa:")) {
                        ticket.setVehicle(new model.entities.Vehicle());
                        ticket.getVehicle().setPlate(line.split(":")[1].trim());
                    } else if (line.startsWith("Espacio:")) {
                        ticket.setSpace(new model.entities.Space());
                        ticket.getSpace().setId(Integer.parseInt(line.split(":")[1].trim()));
                    } else if (line.startsWith("Monto a pagar:")) {
                        ticket.setTotal(Double.parseDouble(line.split("₡")[1].trim()));
                        ticket.setExitTime(LocalDateTime.now()); // marcamos como cerrado
                    }
                }
                tickets.add(ticket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return tickets;
    }
}
