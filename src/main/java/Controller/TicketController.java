package controller;

import model.entity.Ticket;

import model.entity.Vehicle;

public class TicketController {

    public Ticket generateEntryTicket(Vehicle vehicle, int spaceId) {
        return new Ticket(vehicle, spaceId);
    }
}
