package model.data;

import java.util.ArrayList;
import model.entities.Ticket;
import model.entities.Vehicle;

/**
 * Simula la base de datos de tickets
 */
public class TicketData {

    private static ArrayList<Ticket> tickets = new ArrayList<>();

    public void insertTicket(Ticket ticket) {
        if (ticket == null) {
            System.out.println("ERROR: Intento de insertar ticket nulo");
            return;
        }
        
        System.out.println("=== INSERTANDO TICKET ===");
        System.out.println("ID: " + ticket.getId());
        System.out.println("Placa: " + (ticket.getVehicle() != null ? ticket.getVehicle().getPlate() : "null"));
        System.out.println("Espacio: " + (ticket.getSpace() != null ? ticket.getSpace().getId() : "null"));
        
        tickets.add(ticket);
        System.out.println("✅ Ticket insertado. Total tickets: " + tickets.size());
    }

    public ArrayList<Ticket> getAllTickets() {
        return tickets;
    }

    public Ticket findActiveTicketByVehicle(Vehicle vehicle) {
        if (vehicle == null || vehicle.getPlate() == null) {
            System.out.println("ERROR: Vehículo o placa nula en findActiveTicketByVehicle");
            return null;
        }

        System.out.println("Buscando ticket activo para placa: " + vehicle.getPlate());
        
        for (Ticket ticket : tickets) {
            Vehicle v = ticket.getVehicle();
            if (v != null
                && v.getPlate() != null
                && v.getPlate().equalsIgnoreCase(vehicle.getPlate())
                && ticket.getExitTime() == null) {
                System.out.println("  ✅ Encontrado ticket #" + ticket.getId());
                return ticket;
            }
        }
        System.out.println("  ❌ No se encontró ticket activo para placa: " + vehicle.getPlate());
        return null;
    }

    public ArrayList<Ticket> getActiveTickets() {
        ArrayList<Ticket> active = new ArrayList<>();

        System.out.println("=== Buscando tickets activos ===");
        System.out.println("Total tickets en sistema: " + tickets.size());
        
        for (Ticket ticket : tickets) {
            System.out.println("  Revisando ticket #" + ticket.getId() + 
                             " - Placa: " + (ticket.getVehicle() != null ? ticket.getVehicle().getPlate() : "null") +
                             " - ExitTime: " + ticket.getExitTime());
            
            if (ticket.getExitTime() == null) {
                active.add(ticket);
                System.out.println("    ✅ Ticket activo");
            }
        }

        System.out.println("Tickets activos encontrados: " + active.size());
        return active;
    }
    
    // Método adicional para actualizar un ticket
    public boolean updateTicket(Ticket updatedTicket) {
        for (int i = 0; i < tickets.size(); i++) {
            if (tickets.get(i).getId() == updatedTicket.getId()) {
                tickets.set(i, updatedTicket);
                System.out.println("✅ Ticket #" + updatedTicket.getId() + " actualizado");
                return true;
            }
        }
        System.out.println("❌ No se encontró ticket #" + updatedTicket.getId() + " para actualizar");
        return false;
    }
}