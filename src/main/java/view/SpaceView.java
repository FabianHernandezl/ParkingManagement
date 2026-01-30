package view;

import model.data.VehicleData;
import model.entities.Space;
import model.entities.Vehicle;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SpaceView extends JInternalFrame {

    private List<Space> spaces;
    private ParkingLotPanel parkingLotPanel;
    private SpacePanel selectedPanel;

    public SpaceView() {
        setTitle("Gestión de Espacios");
        setClosable(true);
        setSize(800, 500);
        setLayout(new BorderLayout());

        spaces = createSpacesFromVehicles();

        parkingLotPanel = new ParkingLotPanel(spaces, this);
        add(parkingLotPanel, BorderLayout.CENTER);

        add(createButtonsPanel(), BorderLayout.SOUTH);
    }

    // ================= BOTONES =================
    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel();

        JButton btnOccupy = new JButton("Ocupar");
        JButton btnRelease = new JButton("Liberar");

        btnOccupy.addActionListener(e -> occupySpace());
        btnRelease.addActionListener(e -> releaseSpace());

        panel.add(btnOccupy);
        panel.add(btnRelease);

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

            selectedPanel.updateColor();
            selectedPanel.updateTooltip();
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

        selectedPanel.updateColor();
        selectedPanel.updateTooltip();
    }

    // ================= SELECCIÓN =================
    public void setSelectedPanel(SpacePanel panel) {
        if (selectedPanel != null) {
            selectedPanel.setSelected(false);
        }
        selectedPanel = panel;
        selectedPanel.setSelected(true);
    }

    // ================= DATOS =================
    private List<Space> createSpacesFromVehicles() {
        List<Space> list = new ArrayList<>();

        VehicleData vehicleData = new VehicleData();
        List<Vehicle> vehicles = vehicleData.getAllVehicles();

        int id = 1;

        for (Vehicle v : vehicles) {
            Space space = new Space();
            space.setId(id++);
            space.setVehicle(v);
            space.setClient(v.getClients().isEmpty() ? null : v.getClients().get(0));
            space.setSpaceTaken(true);
            space.setAvailable(false);
            space.setDisabilityAdaptation(v.hasPreferentialClient());
            list.add(space);
        }

        while (list.size() < 12) {
            Space space = new Space();
            space.setId(id++);
            space.setSpaceTaken(false);
            space.setAvailable(true);
            space.setDisabilityAdaptation(false);
            list.add(space);
        }

        return list;
    }
}
