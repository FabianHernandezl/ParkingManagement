package view;

import model.entities.Ticket;
import model.entities.Vehicle;
import model.entities.Space;
import model.entities.Client;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class VehicleInfoDialog extends JDialog {

    public VehicleInfoDialog(JFrame parent, Ticket ticket) {
        super(parent, "Información del Vehículo", true);
        setSize(400, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // ================= VEHÍCULO =================
        mainPanel.add(new JLabel("=== Información del Vehículo ==="));
        mainPanel.add(Box.createVerticalStrut(10));

        Vehicle vehicle = ticket.getVehicle();

        if (vehicle != null) {
            mainPanel.add(new JLabel("Placa: " + vehicle.getPlate()));
            mainPanel.add(new JLabel("Tipo: "
                    + (vehicle.getVehicleType() != null
                    ? vehicle.getVehicleType().getDescription()
                    : "N/A")));
        } else {
            mainPanel.add(new JLabel("Vehículo no disponible"));
        }

        mainPanel.add(Box.createVerticalStrut(15));

        // ================= CLIENTE =================
        mainPanel.add(new JLabel("=== Información del Cliente ==="));
        mainPanel.add(Box.createVerticalStrut(10));

        Client client = null;

        Space space = ticket.getSpace();
        if (space != null) {
            client = space.getClient();
        }

        if (client != null) {
            mainPanel.add(new JLabel("Nombre: " + client.getName()));
            mainPanel.add(new JLabel("Cédula: " + client.getId()));
            mainPanel.add(new JLabel("Teléfono: " + client.getPhone()));
            mainPanel.add(new JLabel("Email: " + client.getEmail()));
        } else {
            mainPanel.add(new JLabel("Cliente no disponible"));
        }

        mainPanel.add(Box.createVerticalStrut(15));

        // ================= TICKET =================
        mainPanel.add(new JLabel("=== Información del Ticket ==="));
        mainPanel.add(Box.createVerticalStrut(10));

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

        mainPanel.add(Box.createVerticalStrut(20));

        JButton closeButton = new JButton("Cerrar");
        closeButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
