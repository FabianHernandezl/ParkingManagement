package view;

import model.entities.Ticket;
import model.entities.Vehicle;
import model.entities.Space;
import model.entities.Client;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
 * Modal dialog that displays detailed information about: - Vehicle - Client -
 * Ticket
 *
 * Information is retrieved from the provided Ticket object.
 */
public class VehicleInfoDialog extends JDialog {

    public VehicleInfoDialog(JFrame parent, Ticket ticket) {
        super(parent, "Información del Vehículo", true);
        setSize(420, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // ================= VEHÍCULO =================
        JLabel vehicleTitle = new JLabel("=== Información del Vehículo ===");
        vehicleTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mainPanel.add(vehicleTitle);
        mainPanel.add(Box.createVerticalStrut(10));

        Vehicle vehicle = ticket.getVehicle();

        if (vehicle != null) {
            mainPanel.add(new JLabel("Placa: " + vehicle.getPlate()));
            mainPanel.add(new JLabel("Modelo: " + vehicle.getModel()));
            mainPanel.add(new JLabel("Tipo: "
                    + (vehicle.getVehicleType() != null
                    ? vehicle.getVehicleType().getDescription()
                    : "N/A")));
        } else {
            mainPanel.add(new JLabel("Vehículo no disponible"));
        }

        mainPanel.add(Box.createVerticalStrut(20));

        // ================= CLIENTE =================
        JLabel clientTitle = new JLabel("=== Información del Cliente ===");
        clientTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mainPanel.add(clientTitle);
        mainPanel.add(Box.createVerticalStrut(10));

        Client client = null;

        // 🔥 Obtener cliente desde el VEHÍCULO (más seguro que desde el espacio)
        if (vehicle != null
                && vehicle.getClients() != null
                && !vehicle.getClients().isEmpty()) {

            client = vehicle.getClients().get(0);
        }

        if (client != null) {
            mainPanel.add(new JLabel("Nombre: " + client.getName()));
            mainPanel.add(new JLabel("Cédula: " + client.getId()));
            mainPanel.add(new JLabel("Teléfono: " + client.getPhone()));
            mainPanel.add(new JLabel("Email: " + client.getEmail()));
            mainPanel.add(new JLabel("Preferencial: "
                    + (client.isIsPreferential() ? "Sí" : "No")));
        } else {
            mainPanel.add(new JLabel("Cliente no disponible"));
        }

        mainPanel.add(Box.createVerticalStrut(20));

        // ================= TICKET =================
        JLabel ticketTitle = new JLabel("=== Información del Ticket ===");
        ticketTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mainPanel.add(ticketTitle);
        mainPanel.add(Box.createVerticalStrut(10));

        Space space = ticket.getSpace();

        mainPanel.add(new JLabel("Espacio: "
                + (space != null ? space.getId() : "N/A")));

        mainPanel.add(new JLabel("Entrada: "
                + (ticket.getEntryTime() != null
                ? ticket.getEntryTime().format(formatter)
                : "—")));

        mainPanel.add(new JLabel("Salida: "
                + (ticket.getExitTime() != null
                ? ticket.getExitTime().format(formatter)
                : "Activo")));

        mainPanel.add(new JLabel("Total: ₡"
                + String.format("%.2f", ticket.getTotal())));

        mainPanel.add(Box.createVerticalStrut(25));

        JButton closeButton = new JButton("Cerrar");
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
