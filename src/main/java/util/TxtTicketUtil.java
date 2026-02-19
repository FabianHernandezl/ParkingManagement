package util;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import model.entities.Ticket;
import model.entities.ParkingLot;
import model.entities.Space;
import model.entities.Vehicle;
import model.entities.VehicleType;

public class TxtTicketUtil {

    private static final DateTimeFormatter DATE_FORMAT
            = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private static final String ACTIVE_TICKETS_FILE = "data/tickets_activos.txt";

    /**
     * Genera un ticket individual en TXT (para tickets cerrados)
     */
    public static String generarTicketTXT(
            String parkingName,
            String vehicleType,
            String plate,
            String spaceId,
            double total,
            int ticketId
    ) {

        String ruta = "data/ticket_" + ticketId + ".txt";

        try (FileWriter writer = new FileWriter(ruta)) {

            writer.write("==============================\n");
            writer.write("      TIQUETE DE PARQUEO\n");
            writer.write("==============================\n");
            writer.write("Tiquete #: " + ticketId + "\n");
            writer.write("Parqueo: " + parkingName + "\n");
            writer.write("Placa: " + plate + "\n");
            writer.write("Tipo de vehículo: " + vehicleType + "\n");
            writer.write("Espacio asignado: " + spaceId + "\n");
            writer.write("Fecha de salida: "
                    + LocalDateTime.now().format(DATE_FORMAT) + "\n");
            writer.write("Total a pagar: ₡"
                    + String.format("%.2f", total) + "\n");
            writer.write("==============================\n");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return ruta;
    }

    /**
     * Guarda la lista de tickets activos en un archivo
     */
    public static void guardarTicketsActivos(List<Ticket> tickets) {
        try {
            new File("data").mkdirs();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(ACTIVE_TICKETS_FILE))) {
                for (Ticket ticket : tickets) {
                    if (ticket.getExitTime() == null) { // Solo activos
                        writer.write("===== INICIO TICKET ACTIVO =====\n");
                        writer.write("ID:" + ticket.getId() + "\n");

                        // Guardar información del parqueo
                        if (ticket.getParkingLot() != null) {
                            writer.write("PARKING_ID:" + ticket.getParkingLot().getId() + "\n");
                            writer.write("PARKING_NOMBRE:" + ticket.getParkingLot().getName() + "\n");
                        }

                        // Guardar información del vehículo
                        if (ticket.getVehicle() != null) {
                            writer.write("VEHICULO_PLACA:" + ticket.getVehicle().getPlate() + "\n");
                            if (ticket.getVehicle().getVehicleType() != null) {
                                writer.write("VEHICULO_TIPO:"
                                        + ticket.getVehicle().getVehicleType().getDescription() + "\n");
                            }
                        }

                        // Guardar información del espacio
                        if (ticket.getSpace() != null) {
                            writer.write("ESPACIO_ID:" + ticket.getSpace().getId() + "\n");
                        }

                        // Guardar hora de entrada
                        if (ticket.getEntryTime() != null) {
                            writer.write("HORA_ENTRADA:"
                                    + ticket.getEntryTime().format(DATE_FORMAT) + "\n");
                        }

                        writer.write("===== FIN TICKET ACTIVO =====\n\n");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al guardar tickets activos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Carga los tickets activos desde el archivo
     */
    public static List<Ticket> cargarTicketsActivos() {
        List<Ticket> tickets = new ArrayList<>();
        File file = new File(ACTIVE_TICKETS_FILE);

        if (!file.exists()) {
            return tickets;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            Ticket ticketActual = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.equals("===== INICIO TICKET ACTIVO =====")) {
                    ticketActual = new Ticket();
                } else if (line.equals("===== FIN TICKET ACTIVO =====")) {
                    if (ticketActual != null) {
                        tickets.add(ticketActual);
                        ticketActual = null;
                    }
                } else if (ticketActual != null) {
                    if (line.startsWith("ID:")) {
                        ticketActual.setId(Integer.parseInt(line.substring(3)));
                    } else if (line.startsWith("PARKING_ID:")) {
                        if (ticketActual.getParkingLot() == null) {
                            ticketActual.setParkingLot(new ParkingLot());
                        }
                        ticketActual.getParkingLot().setId(Integer.parseInt(line.substring(11)));
                    } else if (line.startsWith("PARKING_NOMBRE:")) {
                        if (ticketActual.getParkingLot() == null) {
                            ticketActual.setParkingLot(new ParkingLot());
                        }
                        ticketActual.getParkingLot().setName(line.substring(15));
                    } else if (line.startsWith("VEHICULO_PLACA:")) {
                        if (ticketActual.getVehicle() == null) {
                            ticketActual.setVehicle(new Vehicle());
                        }
                        ticketActual.getVehicle().setPlate(line.substring(15));
                    } else if (line.startsWith("VEHICULO_TIPO:")) {
                        if (ticketActual.getVehicle() != null) {
                            VehicleType tipo = new VehicleType();
                            tipo.setDescription(line.substring(14));
                            ticketActual.getVehicle().setVehicleType(tipo);
                        }
                    } else if (line.startsWith("ESPACIO_ID:")) {
                        if (ticketActual.getSpace() == null) {
                            ticketActual.setSpace(new Space());
                        }
                        ticketActual.getSpace().setId(Integer.parseInt(line.substring(11)));
                    } else if (line.startsWith("HORA_ENTRADA:")) {
                        String fechaStr = line.substring(13);
                        ticketActual.setEntryTime(LocalDateTime.parse(fechaStr, DATE_FORMAT));
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Error al cargar tickets activos: " + e.getMessage());
            e.printStackTrace();
        }

        return tickets;
    }

    /**
     * Elimina un ticket de los activos (cuando se cierra)
     */
    public static void eliminarTicketActivo(int ticketId) {
        List<Ticket> activos = cargarTicketsActivos();
        activos.removeIf(t -> t.getId() == ticketId);
        guardarTicketsActivos(activos);
    }

    /**
     * Lee todos los tickets históricos desde los archivos TXT
     */
    public static ArrayList<Ticket> leerTicketsTXT() {
        ArrayList<Ticket> tickets = new ArrayList<>();
        File folder = new File("data");

        if (!folder.exists() || !folder.isDirectory()) {
            return tickets;
        }

        File[] files = folder.listFiles((dir, name)
                -> name.startsWith("ticket_") && name.endsWith(".txt"));

        if (files == null) {
            return tickets;
        }

        for (File file : files) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                Ticket ticket = new Ticket();
                String line;

                while ((line = br.readLine()) != null) {
                    line = line.trim();

                    if (line.startsWith("Tiquete #:")) {
                        ticket.setId(Integer.parseInt(line.split(":")[1].trim()));

                    } else if (line.startsWith("Placa:")) {
                        if (ticket.getVehicle() == null) {
                            ticket.setVehicle(new Vehicle());
                        }
                        ticket.getVehicle().setPlate(line.split(":")[1].trim());

                    } else if (line.startsWith("Parqueo:")) {
                        if (ticket.getParkingLot() == null) {
                            ticket.setParkingLot(new ParkingLot());
                        }
                        ticket.getParkingLot().setName(line.split(":")[1].trim());

                    } else if (line.startsWith("Espacio asignado:")) {
                        if (ticket.getSpace() == null) {
                            ticket.setSpace(new Space());
                        }
                        ticket.getSpace().setId(
                                Integer.parseInt(line.split(":")[1].trim())
                        );

                    } else if (line.startsWith("Tipo de vehículo:")) {
                        if (ticket.getVehicle() != null && ticket.getVehicle().getVehicleType() == null) {
                            VehicleType tipo = new VehicleType();
                            tipo.setDescription(line.split(":")[1].trim());
                            ticket.getVehicle().setVehicleType(tipo);
                        }

                    } else if (line.startsWith("Total a pagar:")) {
                        String totalStr = line.split("₡")[1].trim();
                        ticket.setTotal(Double.parseDouble(totalStr));

                    } else if (line.startsWith("Fecha de salida:")) {
                        String fechaStr = line.split(":")[1].trim() + ":"
                                + line.split(":")[2].trim() + ":"
                                + line.split(":")[3].trim();
                        ticket.setExitTime(LocalDateTime.parse(fechaStr, DATE_FORMAT));
                    }
                }

                if (ticket.getId() > 0) {
                    tickets.add(ticket);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return tickets;
    }
}
