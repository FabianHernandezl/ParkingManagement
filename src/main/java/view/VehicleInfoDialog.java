/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import java.awt.*;
import javax.swing.*;
import model.entities.Ticket;

import javax.swing.JDialog;

/**
 *
 * @author jimen
 */
public class VehicleInfoDialog extends JDialog {

    public VehicleInfoDialog(JFrame parent, Ticket ticket) {
        super(parent, "Información del Vehículo", true);

        setSize(400, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ===== VEHÍCULO =====
        JLabel lblVehicleTitle = new JLabel("🚗 Datos del Vehículo");
        lblVehicleTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblVehicleTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        mainPanel.add(lblVehicleTitle);
        mainPanel.add(Box.createVerticalStrut(10));

        mainPanel.add(new JLabel("Placa: " + ticket.getVehicle().getPlate()));
        mainPanel.add(new JLabel("Marca: " + ticket.getVehicle().getBrand()));
        mainPanel.add(new JLabel("Modelo: " + ticket.getVehicle().getModel()));
        mainPanel.add(new JLabel("Color: " + ticket.getVehicle().getColor()));

        mainPanel.add(Box.createVerticalStrut(20));

        // ===== CLIENTE =====
        JLabel lblClientTitle = new JLabel("👤 Datos del Cliente");
        lblClientTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblClientTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        mainPanel.add(lblClientTitle);
        mainPanel.add(Box.createVerticalStrut(10));

        if (ticket.getSpace() != null && ticket.getSpace().getClient() != null) {

            mainPanel.add(new JLabel("Nombre: "
                    + ticket.getSpace().getClient().getName()));

            mainPanel.add(new JLabel("Cédula: "
                    + ticket.getSpace().getClient().getId()));

            mainPanel.add(new JLabel("Teléfono: "
                    + ticket.getSpace().getClient().getPhone()));
            mainPanel.add(new JLabel("Email: " + ticket.getSpace().getClient().getEmail()));

        } else {

            mainPanel.add(new JLabel("Cliente no disponible"));

        }

        // ===== TICKET =====
        JLabel lblTicketTitle = new JLabel("🎟 Información del Ticket");
        lblTicketTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTicketTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        mainPanel.add(lblTicketTitle);
        mainPanel.add(Box.createVerticalStrut(10));

        mainPanel.add(new JLabel("Hora Entrada: " + ticket.getEntryTime()));

        JButton btnClose = new JButton("Cerrar");
        btnClose.addActionListener(e -> dispose());

        add(mainPanel, BorderLayout.CENTER);
        add(btnClose, BorderLayout.SOUTH);
    }

}
