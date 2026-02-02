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
    private JTextField txtSearchVehicle;
    private JPopupMenu popupVehicles;
    private JList<Vehicle> listVehicleSuggestions;

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

        if (vehicles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay vehículos registrados");
            return;
        }
        Vehicle selectedVehicle = showVehicleSearchDialog();

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

            selectedPanel.setVehicleIcon(selectedVehicle);
            selectedPanel.updateView();
        }

        // ===== VISTA (ANIMACIÓN) =====
        selectedPanel.setVehicleIcon(selectedVehicle);
        selectedPanel.updateView();
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

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Desea liberar este espacio?",
                "Confirmar liberación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // animar salida del carro primero
        selectedPanel.animateVehicleIn();

        Timer delay = new Timer(400, e -> {
            space.setVehicle(null);
            space.setClient(null);
            space.setSpaceTaken(false);
            space.setAvailable(true);
            space.setDisabilityAdaptation(false);

            refreshPanel();
        });
        delay.setRepeats(false);
        delay.start();
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

    private Vehicle showVehicleSearchDialog() {

        VehicleData vehicleData = new VehicleData();
        List<Vehicle> vehicles = vehicleData.getAllVehicles();

        if (vehicles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay vehículos registrados");
            return null;
        }

        JDialog dialog = new JDialog((Frame) null, "Buscar vehículo", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        txtSearchVehicle = new JTextField();
        dialog.add(txtSearchVehicle, BorderLayout.NORTH);

        DefaultListModel<Vehicle> model = new DefaultListModel<>();
        listVehicleSuggestions = new JList<>(model);
        dialog.add(new JScrollPane(listVehicleSuggestions), BorderLayout.CENTER);

        txtSearchVehicle.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {

            private void search() {
                String text = txtSearchVehicle.getText().toLowerCase();
                model.clear();

                for (Vehicle v : vehicles) {
                    if (v.getPlate().toLowerCase().contains(text)
                            || v.getBrand().toLowerCase().contains(text)
                            || v.getModel().toLowerCase().contains(text)) {
                        model.addElement(v);
                    }
                }
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                search();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                search();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                search();
            }
        });

        final Vehicle[] selected = new Vehicle[1];

        listVehicleSuggestions.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selected[0] = listVehicleSuggestions.getSelectedValue();
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
        return selected[0];
    }

}
