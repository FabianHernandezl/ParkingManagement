package Controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import model.data.TicketData;
import model.entities.Space;
import model.entities.Ticket;
import model.entities.Vehicle;

/**
 * Controlador de tickets
 * Mantiene tickets activos y permite registrar entradas y salidas
 */
public class TicketController {

    private static final TicketData ticketData = new TicketData();
    private static int ticketIdCounter = 1;

    // Singleton para garantizar una sola instancia
    private static TicketController instance;

    private TicketController() { 
        // Inicializar contador basado en tickets existentes
        ArrayList<Ticket> allTickets = ticketData.getAllTickets();
        if (!allTickets.isEmpty()) {
            int maxId = 0;
            for (Ticket t : allTickets) {
                if (t.getId() > maxId) {
                    maxId = t.getId();
                }
            }
            ticketIdCounter = maxId + 1;
            System.out.println("TicketController: Último ID=" + maxId + ", próximo ID=" + ticketIdCounter);
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
        System.out.println("\n=== GENERANDO TICKET DE ENTRADA ===");
        System.out.println("Vehículo: " + (vehicle != null ? vehicle.getPlate() : "null"));
        System.out.println("Espacio: " + (space != null ? space.getId() : "null"));
        
        if (vehicle == null || space == null) {
            System.out.println("❌ ERROR: Vehículo o espacio nulo");
            return null;
        }

        // Solo se permite un ticket activo por vehículo
        Ticket existing = ticketData.findActiveTicketByVehicle(vehicle);
        if (existing != null) {
            System.out.println("❌ ERROR: Vehículo " + vehicle.getPlate() + 
                             " ya tiene un ticket activo #" + existing.getId());
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
        
        System.out.println("✅ TICKET CREADO EXITOSAMENTE");
        System.out.println("ID: " + ticket.getId());
        System.out.println("Placa: " + ticket.getVehicle().getPlate());
        System.out.println("Espacio: " + ticket.getSpace().getId());
        System.out.println("Hora entrada: " + ticket.getEntryTime());
        System.out.println("=====================\n");
        
        return ticket;
    }

    /**
     * Registra la salida de un vehículo y calcula el total
     */
    public double registerExit(Ticket ticket) {
        System.out.println("\n=== REGISTRANDO SALIDA ===");
        
        if (ticket == null) {
            System.out.println("❌ ERROR: Ticket nulo");
            return 0;
        }
        
        if (ticket.getExitTime() != null) {
            System.out.println("❌ ERROR: Ticket ya tiene hora de salida");
            return 0;
        }

        ticket.setExitTime(LocalDateTime.now());
        double total = ticket.calculateTotal();
        ticket.setTotal(total);
        
        // Actualizar el ticket en la base de datos
        ticketData.updateTicket(ticket);
        
        System.out.println("✅ SALIDA REGISTRADA");
        System.out.println("Ticket #" + ticket.getId());
        System.out.println("Vehículo: " + ticket.getVehicle().getPlate());
        System.out.println("Hora entrada: " + ticket.getEntryTime());
        System.out.println("Hora salida: " + ticket.getExitTime());
        System.out.println("Total: ₡" + total);
        System.out.println("=====================\n");
        
        return total;
    }

    /**
     * Devuelve todos los tickets activos
     */
    public ArrayList<Ticket> getActiveTickets() {
        return ticketData.getActiveTickets();
    }

    /**
     * Busca un ticket por ID
     */
    public Ticket findTicketById(int id) {
        for (Ticket ticket : ticketData.getAllTickets()) {
            if (ticket.getId() == id) {
                return ticket;
            }
        }
        return null;
    }
    
    /**
     * Devuelve todos los tickets (activos e inactivos)
     */
    public ArrayList<Ticket> getAllTickets() {
        return ticketData.getAllTickets();
    }
}