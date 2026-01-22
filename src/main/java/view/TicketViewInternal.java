/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import controller.TicketController;
import controller.VehicleController;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import model.entities.Ticket;
import model.entities.Vehicle;

/**
 *
 * @author jimen
 */
public class TicketViewInternal extends JInternalFrame {

    private final VehicleController vehicleController = new VehicleController();
    private final TicketController ticketController = new TicketController();

    public TicketViewInternal() {
        super("Gestión de Clientes", true, true, true, true);
        setSize(700, 400);
        setLocation(20, 20);

        ClientViewInternal panel = new ClientViewInternal();
        this.add(panel.getContentPane());
    }

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
