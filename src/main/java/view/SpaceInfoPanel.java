/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import javax.swing.*;
import java.awt.*;
import model.entities.Space;

/**
 *
 * @author jimen
 */
public class SpaceInfoPanel extends JPanel {

    private JLabel lblTitle = new JLabel("INFORMACIÓN DEL ESPACIO");
    private JLabel lblSpaceId = new JLabel("Espacio #: ");
    private JLabel lblStatus = new JLabel("Estado: ");
    private JLabel lblType = new JLabel("Tipo: ");
    private JLabel lblAccessibility = new JLabel("Accesibilidad: ");
    private JLabel lblClient = new JLabel("Cliente: ");
    private JLabel lblVehicle = new JLabel("Vehículo: ");
    private JLabel lblTime = new JLabel("Tiempo: ");

    public SpaceInfoPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(240, 240, 240));

        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(new Color(0, 102, 204));
        lblTitle.setAlignmentX(CENTER_ALIGNMENT);

        add(lblTitle);
        add(Box.createRigidArea(new Dimension(0, 20)));

        add(createInfoLabel("ID:", lblSpaceId));
        add(createInfoLabel("Estado:", lblStatus));
        add(createInfoLabel("Tipo:", lblType));
        add(createInfoLabel("Accesibilidad:", lblAccessibility));
        add(Box.createRigidArea(new Dimension(0, 15)));
        add(createInfoLabel("Cliente:", lblClient));
        add(createInfoLabel("Vehículo:", lblVehicle));
        add(createInfoLabel("Tiempo:", lblTime));
    }

    private JPanel createInfoLabel(String label, JLabel valueLabel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 240, 240));

        JLabel labelLbl = new JLabel(label);
        labelLbl.setFont(new Font("Arial", Font.BOLD, 12));

        valueLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        panel.add(labelLbl, BorderLayout.WEST);
        panel.add(valueLabel, BorderLayout.CENTER);
        panel.setMaximumSize(new Dimension(200, 25));

        return panel;
    }

    public void showSpaceInfo(Space space) {
        if (space == null) {
            clearInfo();
            return;
        }

        lblSpaceId.setText(String.valueOf(space.getId()));
        lblStatus.setText(space.isSpaceTaken() ? "OCUPADO" : "DISPONIBLE");
        lblType.setText(space.getVehicleType().getDescription());
        lblAccessibility.setText(space.isDisabilityAdaptation() ? "ADAPTADO" : "ESTÁNDAR");

        if (space.isSpaceTaken() && space.getClient() != null && space.getVehicle() != null) {
            lblClient.setText(space.getClient().getName());
            lblVehicle.setText(space.getVehicle().getPlate() + " - " + space.getVehicle().getBrand());
            lblTime.setText("Calculando...");
        } else {
            lblClient.setText("No asignado");
            lblVehicle.setText("No asignado");
            lblTime.setText("N/A");
        }
    }

    private void clearInfo() {
        lblSpaceId.setText("");
        lblStatus.setText("");
        lblType.setText("");
        lblAccessibility.setText("");
        lblClient.setText("");
        lblVehicle.setText("");
        lblTime.setText("");
    }

}
