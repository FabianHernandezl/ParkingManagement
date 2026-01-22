package controller;

import model.entities.Ticket;
import model.entities.Vehicle;

public class TicketController {

    public Ticket generateEntryTicket(Vehicle vehicle, int spaceId) {
        return new Ticket(vehicle, spaceId);
    }
}
