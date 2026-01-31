package view;

import model.data.VehicleData;
import model.entities.ParkingLot;
import model.entities.Space;
import model.entities.Vehicle;
import Controller.ParkingLotController;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class SpaceView extends JInternalFrame {

    private ParkingLot parkingLot;
    private ParkingLotPanel parkingLotPanel;
    private SpacePanel selectedPanel;
    private AdminMenu parent;

    public SpaceView(ParkingLot parkingLot, AdminMenu parent) {
        this.parkingLot = parkingLot;
        this.parent = parent;

        setTitle("Espacios - " + parkingLot.getName());
        setClosable(true);
        setSize(800, 500);
        setLayout(new BorderLayout());

        List<Space> spaces = Arrays.asList(parkingLot.getSpaces());

        parkingLotPanel = new ParkingLotPanel(spaces, this);
        add(parkingLotPanel, BorderLayout.CENTER);

        add(createButtonsPanel(), BorderLayout.SOUTH);
    }

    // ================= BOTONES =================
    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel();

        JButton btnOccupy = new JButton("Ocupar");
        JButton btnRelease = new JButton("Liberar");
        JButton btnChangeParking = new JButton("Cambiar parqueo");

        btnOccupy.addActionListener(e -> occupySpace());
        btnRelease.addActionListener(e -> releaseSpace());

        btnChangeParking.addActionListener(e -> {
            ParkingLotController controller = new ParkingLotController();
            parent.openInternalFrame(
                    new SelectParkingLotView(controller.getAllParkingLots(), parent)
            );
            dispose(); // cerrar esta vista
        });

        panel.add(btnOccupy);
        panel.add(btnRelease);
        panel.add(btnChangeParking);

        return panel;
    }

    // ================= LÓGICA =================
    private void occupySpace() {
        if (selectedPanel == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un espacio");
            return;
        }

        Space space = selectedPanel.getSpace();

        if (space.isSpaceTaken()) {
            JOptionPane.showMessageDialog(this, "El espacio ya está ocupado");
            return;
        }

        VehicleData vehicleData = new VehicleData();
        List<Vehicle> vehicles = vehicleData.getAllVehicles();

        Vehicle selectedVehicle = (Vehicle) JOptionPane.showInputDialog(
                this,
                "Seleccione un vehículo",
                "Vehículos",
                JOptionPane.QUESTION_MESSAGE,
                null,
                vehicles.toArray(),
                null
        );

        if (selectedVehicle != null) {
            space.setVehicle(selectedVehicle);
            space.setClient(
                    selectedVehicle.getClients().isEmpty()
                    ? null
                    : selectedVehicle.getClients().get(0)
            );
            space.setSpaceTaken(true);
            space.setAvailable(false);
            space.setDisabilityAdaptation(selectedVehicle.hasPreferentialClient());

            refreshPanel();
        }
    }

    private void releaseSpace() {
        if (selectedPanel == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un espacio");
            return;
        }

        Space space = selectedPanel.getSpace();

        if (!space.isSpaceTaken()) {
            JOptionPane.showMessageDialog(this, "El espacio ya está libre");
            return;
        }

        space.setVehicle(null);
        space.setClient(null);
        space.setSpaceTaken(false);
        space.setAvailable(true);
        space.setDisabilityAdaptation(false);

        refreshPanel();
    }

    // ================= SELECCIÓN =================
    public void setSelectedPanel(SpacePanel panel) {
        if (selectedPanel != null) {
            selectedPanel.setSelected(false);
        }
        selectedPanel = panel;
        selectedPanel.setSelected(true);
    }

    // ================= REFRESH =================
    private void refreshPanel() {
        if (selectedPanel != null) {
            Color target;

            if (selectedPanel.getSpace().isSpaceTaken()) {
                target = new Color(244, 67, 54);
            } else if (selectedPanel.getSpace().isDisabilityAdaptation()) {
                target = new Color(3, 169, 244);
            } else {
                target = new Color(76, 175, 80);
            }

            selectedPanel.animateChange(target);
            selectedPanel.updateView();
            parkingLotPanel.repaint();
        }
    }

}
