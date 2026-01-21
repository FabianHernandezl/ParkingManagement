/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import controller.TicketController;
import controller.VehicleController;
import javax.swing.JOptionPane;
import model.entity.Ticket;
import model.entity.Vehicle;

/**
 *
 * @author jimen
 */
public class TicketView {

    private final VehicleController vehicleController = new VehicleController();
    private final TicketController ticketController = new TicketController();

    // ===================== TIQUETE =====================
    public void registerVehicleAndGenerateTicket() {

        String plate = JOptionPane.showInputDialog("Ingrese la placa del vehículo");

        if (plate == null || plate.isBlank()) {
            JOptionPane.showMessageDialog(null, "Placa inválida");
            return;
        }

        Vehicle vehicle = vehicleController.findVehicleByPlate(plate);

        if (vehicle == null) {
            JOptionPane.showMessageDialog(null, "Vehículo no encontrado");
            return;
        }

        int spaceId = vehicleController.registerVehicleInParking(vehicle);

        if (spaceId <= 0) {
            JOptionPane.showMessageDialog(null, "No hay espacios disponibles");
            return;
        }

        Ticket ticket = ticketController.generateEntryTicket(vehicle, spaceId);
        JOptionPane.showMessageDialog(null, ticket.toString());
    }
}
