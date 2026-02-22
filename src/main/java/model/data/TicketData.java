package model.data;

import java.util.ArrayList;
import java.util.List;
import model.entities.Ticket;
import model.entities.Vehicle;

/**
 * Simula la base de datos de tickets en memoria.
 */
public class TicketData {

    // Simulación de base de datos en memoria
    private static final List<Ticket> tickets = new ArrayList<>();

    /**
     * Inserta un nuevo ticket en el sistema.
     */
    public boolean insertTicket(Ticket ticket) {
        if (ticket == null) {
            return false;
        }

        tickets.add(ticket);
        return true;
    }

    /**
     * Retorna todos los tickets (copia para evitar modificación externa).
     */
    public List<Ticket> getAllTickets() {
        return new ArrayList<>(tickets);
    }

    /**
     * Busca un ticket activo (sin hora de salida) por vehículo.
     */
    public Ticket findActiveTicketByVehicle(Vehicle vehicle) {
        if (vehicle == null || vehicle.getPlate() == null) {
            return null;
        }

        for (Ticket ticket : tickets) {
            if (ticket.getVehicle() != null
                    && ticket.getVehicle().getPlate() != null
                    && ticket.getVehicle().getPlate().equalsIgnoreCase(vehicle.getPlate())
                    && ticket.getExitTime() == null) {
                return ticket;
            }
        }
        return null;
    }

    /**
     * Obtiene todos los tickets activos (sin hora de salida).
     */
    public List<Ticket> getActiveTickets() {
        List<Ticket> activeTickets = new ArrayList<>();

        for (Ticket ticket : tickets) {
            if (ticket.getExitTime() == null) {
                activeTickets.add(ticket);
            }
        }

        return activeTickets;
    }

    /**
     * Actualiza un ticket existente.
     */
    public boolean updateTicket(Ticket updatedTicket) {
        if (updatedTicket == null) {
            return false;
        }

        for (int i = 0; i < tickets.size(); i++) {
            if (tickets.get(i).getId() == updatedTicket.getId()) {
                tickets.set(i, updatedTicket);
                return true;
            }
        }

        return false;
    }

    /**
     * Elimina todos los tickets (útil para pruebas).
     */
    public void clearAll() {
        tickets.clear();
    }
}
