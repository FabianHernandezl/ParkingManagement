package view;

import Controller.ParkingLotController;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.*;
import model.entities.ParkingLot;
import model.entities.Space;

public class SpaceView extends JInternalFrame {

    private ParkingLot parkingLot;
    private AdminMenu parent;
    private JPanel spacesContainer;
    private SpacePanel selectedPanel;

    private ParkingLotController parkingLotController = new ParkingLotController();

    public SpaceView(ParkingLot parkingLot, AdminMenu parent) {
        this.parkingLot = parkingLot;
        this.parent = parent;

        setTitle("Espacios - " + parkingLot.getName());
        setClosable(true);
        setSize(900, 600);
        setLayout(new BorderLayout(10, 10));

        initUI();
        loadSpaces();
    }

    private void initUI() {

        // 🔹 Panel superior
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel lblTitle = new JLabel("Parqueo: " + parkingLot.getName());
        lblTitle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));

        JButton btnRefresh = new JButton("Actualizar");
        btnRefresh.addActionListener(e -> refreshParkingLot());

        topPanel.add(lblTitle);
        topPanel.add(btnRefresh);

        // 🔹 Contenedor de espacios
        spacesContainer = new JPanel();
        spacesContainer.setLayout(new GridLayout(0, 5, 15, 15));
        spacesContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(spacesContainer);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    // 🔄 Carga inicial de espacios
    private void loadSpaces() {
        spacesContainer.removeAll();

        if (parkingLot.getSpaces() != null) {
            for (Space space : parkingLot.getSpaces()) {
                SpacePanel panel = new SpacePanel(space, this);
                spacesContainer.add(panel);
            }
        }

        spacesContainer.revalidate();
        spacesContainer.repaint();
    }

    // 🔄 Refresca desde DATA (JSON / memoria)
    private void refreshParkingLot() {

        ParkingLot updated = parkingLotController.getParkingLotById(parkingLot.getId());

        if (updated != null) {
            this.parkingLot = updated;
            loadSpaces();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo actualizar el parqueo",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // 🔹 Manejo de selección visual
    public void setSelectedPanel(SpacePanel panel) {

        if (selectedPanel != null) {
            selectedPanel.setSelected(false);
        }

        selectedPanel = panel;

        if (selectedPanel != null) {
            selectedPanel.setSelected(true);
        }
    }

    public SpacePanel getSelectedPanel() {
        return selectedPanel;
    }
}
