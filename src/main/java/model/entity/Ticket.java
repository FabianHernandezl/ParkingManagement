package model.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ticket {

    private static int counter = 1;

    private int id;
    private Vehicle vehicle;
    private int spaceId;
    private LocalDateTime entryTime;

    public Ticket(Vehicle vehicle, int spaceId) {
        this.id = counter++;
        this.vehicle = vehicle;
        this.spaceId = spaceId;
        this.entryTime = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public int getSpaceId() {
        return spaceId;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    @Override
    public String toString() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        return "==============================\n"
                + "        TIQUETE DE INGRESO     \n"
                + "==============================\n"
                + "Tiquete #: " + id + "\n"
                //+ "Placa: " + vehicle.getPlate() + "\n"
                // + "Tipo de vehículo: " + vehicle.getVehicleType().getName() + "\n"
                + "Espacio asignado: " + spaceId + "\n"
                + "Hora de ingreso: " + entryTime.format(format) + "\n"
                + "------------------------------\n"
                + "Conserve este tiquete\n"
                + "==============================";
    }
}
