package Controller;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import model.data.TicketData;
import model.entities.Space;
import model.entities.Ticket;
import model.entities.Vehicle;
import util.TxtTicketUtil;

public class TicketController {

    private static final TicketData ticketData = new TicketData();
    private static int ticketIdCounter = 1;

    private static TicketController instance;

    private TicketController() {
        // Inicializar contador basado en tickets existentes
        ArrayList<Ticket> allTickets = ticketData.getAllTickets();
        if (!allTickets.isEmpty()) {
            int maxId = allTickets.stream().mapToInt(Ticket::getId).max().orElse(0);
            ticketIdCounter = maxId + 1;
        }
    }

    public static TicketController getInstance() {
        if (instance == null) {
            instance = new TicketController();
        }
        return instance;
    }

    /**
     * Genera un ticket de entrada para un vehículo
     */
    public Ticket generateEntryTicket(Vehicle vehicle, Space space) {
        if (vehicle == null || space == null) {
            return null;
        }

        Ticket existing = ticketData.findActiveTicketByVehicle(vehicle);
        if (existing != null) {
            return null;
        }

        Ticket ticket = new Ticket();
        ticket.setId(ticketIdCounter++);
        ticket.setVehicle(vehicle);
        ticket.setSpace(space);
        ticket.setEntryTime(LocalDateTime.now());
        ticket.setExitTime(null);
        ticket.setTotal(0.0);

        ticketData.insertTicket(ticket);

        // =================== REGISTRO HISTÓRICO ===================
        appendToRegistro(ticket, "ENTRADA");
        // ==========================================================

        System.out.println("✅ TICKET DE ENTRADA CREADO: ID " + ticket.getId());
        return ticket;
    }

    /**
     * Registra la salida de un vehículo, calcula total y genera ticket
     * individual
     */
    public double registerExit(Ticket ticket) {
        if (ticket == null || ticket.getExitTime() != null) {
            return 0;
        }

        ticket.setExitTime(LocalDateTime.now());
        double total = ticket.calculateTotal();
        ticket.setTotal(total);

        ticketData.updateTicket(ticket);

        // =================== REGISTRO HISTÓRICO ===================
        appendToRegistro(ticket, "SALIDA");
        // ==========================================================

        // =================== TICKET INDIVIDUAL ====================
        TxtTicketUtil.generarTicketTXT(
                ticket.getVehicle().getPlate(),
                ticket.getVehicle().getVehicleType().getDescription(),
                ticket.getVehicle().getPlate(),
                String.valueOf(ticket.getSpace().getId()),
                total,
                ticket.getId()
        );
        // ==========================================================

        System.out.println("✅ SALIDA REGISTRADA: Ticket ID " + ticket.getId());
        return total;
    }

    /**
     * Agrega información de ticket al archivo histórico tickets_registro.txt
     */
    private void appendToRegistro(Ticket ticket, String tipo) {
        String ruta = "data/tickets_registro.txt";
        try (FileWriter writer = new FileWriter(ruta, true)) { // append
            writer.write("TICKET " + tipo + "\n");
            writer.write(ticket.toString());
            writer.write("\n\n"); // separador
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retorna los tickets activos (sin salida)
     */
    public ArrayList<Ticket> getActiveTickets() {
        return ticketData.getActiveTickets();
    }

    /**
     * Busca un ticket por ID
     */
    public Ticket findTicketById(int id) {
        return ticketData.getAllTickets().stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Retorna todos los tickets en memoria
     */
    public ArrayList<Ticket> getAllTickets() {
        return ticketData.getAllTickets();
    }

    /**
     * Retorna todos los tickets leídos desde el archivo TXT histórico
     */
    public ArrayList<Ticket> getAllTicketsFromTxt() {
        ArrayList<Ticket> ticketsFromTxt = TxtTicketUtil.leerTicketsTXT();
        if (ticketsFromTxt == null) {
            ticketsFromTxt = new ArrayList<>();
        }
        return ticketsFromTxt;
    }
}
