package model.entities;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Entidad Ticket Representa el ingreso y salida de un vehículo del parqueo
 */
public class Ticket {

    private int id;
    private Vehicle vehicle;
    private Space space;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime; // ← debe iniciar en null
    private double total;

    public Ticket() {
        this.entryTime = LocalDateTime.now();
        this.exitTime = null; // 🔑 CLAVE
        this.total = 0.0;
    }

    // ================= GETTERS & SETTERS =================
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    // ================= LÓGICA DEL NEGOCIO =================
    /**
     * Calcula el total a pagar según el tiempo y el tipo de vehículo
     * NO modifica el atributo total
     */
    public double calculateTotal() {
        if (entryTime == null || exitTime == null || vehicle == null || vehicle.getVehicleType() == null) {
            return 0;
        }

        long hours = Duration.between(entryTime, exitTime).toHours();
        if (hours == 0) {
            hours = 1; // mínimo 1 hora
        }

        return hours * vehicle.getVehicleType().getFee();
    }

    // ================= REPRESENTACIÓN =================
    @Override
    public String toString() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        return """
               ==============================
                     TIQUETE DE PARQUEO
               ==============================
               Tiquete #: %d
               Placa: %s
               Tipo de vehículo: %s
               Espacio asignado: %d
               Hora de ingreso: %s
               Hora de salida: %s
               Total a pagar: ₡%.2f
               ==============================
               """.formatted(
                id,
                vehicle != null ? vehicle.getPlate() : "N/A",
                vehicle != null && vehicle.getVehicleType() != null ? vehicle.getVehicleType().getDescription() : "N/A",
                space != null ? space.getId() : 0,
                entryTime != null ? entryTime.format(format) : "—",
                (exitTime != null ? exitTime.format(format) : "—"),
                total
        );
    }
}