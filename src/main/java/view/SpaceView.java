package view;

import Controller.SpaceController;
import model.entities.Space;
import model.entities.Vehicle;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import model.data.VehicleData;
import model.entities.Client;

public class SpaceView extends JInternalFrame {

    private JComboBox<Space> cmbSpaces;
    private JLabel lblStatus;
    private JLabel lblEntryTime;
    private JTextArea txtVehicleInfo;
    private JComboBox<Vehicle> cmbVehicles;
    private JButton btnOccupy;
    private VehicleData vehicleData;

    private JButton btnRelease;

    private SpaceController spaceController;

    public SpaceView() {
        super("Espacios de Parqueo", true, true, true, true);
        spaceController = new SpaceController();
        vehicleData = new VehicleData();

        initComponents();
        loadSpaces();
        loadVehicles();

    }

    private void initComponents() {
        setSize(650, 400);
        setLayout(new BorderLayout(10, 10));

        // ---------- PANEL SUPERIOR ----------
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Espacio:"));

        cmbSpaces = new JComboBox<>();
        cmbSpaces.setPreferredSize(new Dimension(150, 25));
        cmbSpaces.addActionListener(e -> showSpaceInfo());
        topPanel.add(cmbSpaces);

        add(topPanel, BorderLayout.NORTH);

        // ---------- PANEL CENTRAL ----------
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        lblStatus = new JLabel("Estado: ");
        lblEntryTime = new JLabel("Hora de entrada: ");

        centerPanel.add(lblStatus);
        centerPanel.add(lblEntryTime);

        add(centerPanel, BorderLayout.CENTER);

        // ---------- PANEL VEHÍCULO ----------
        txtVehicleInfo = new JTextArea();
        txtVehicleInfo.setEditable(false);
        txtVehicleInfo.setLineWrap(true);
        txtVehicleInfo.setWrapStyleWord(true);
        txtVehicleInfo.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollVehicle = new JScrollPane(txtVehicleInfo);
        scrollVehicle.setBorder(
                BorderFactory.createTitledBorder("Datos del Vehículo"));

        add(scrollVehicle, BorderLayout.EAST);

        // ---------- PANEL INFERIOR ----------
        JPanel bottomPanel = new JPanel();

        btnRelease = new JButton("Liberar espacio");
        btnRelease.addActionListener(e -> releaseSpace());

        bottomPanel.add(btnRelease);

        add(bottomPanel, BorderLayout.SOUTH);

        // ---------- PANEL OCUPAR ----------
        JPanel occupyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        occupyPanel.setBorder(
                BorderFactory.createTitledBorder("Ocupar espacio")
        );

        occupyPanel.add(new JLabel("Vehículo:"));

        cmbVehicles = new JComboBox<>();
        cmbVehicles.setPreferredSize(new Dimension(200, 25));
        occupyPanel.add(cmbVehicles);

        btnOccupy = new JButton("Ocupar espacio");
        btnOccupy.addActionListener(e -> occupySpace());
        occupyPanel.add(btnOccupy);

        add(occupyPanel, BorderLayout.WEST);

    }

    private void loadSpaces() {
        cmbSpaces.removeAllItems();
        ArrayList<Space> spaces = spaceController.getAllSpaces();

        for (Space s : spaces) {
            cmbSpaces.addItem(s);
        }

        if (!spaces.isEmpty()) {
            cmbSpaces.setSelectedIndex(0);
        }

        showSpaceInfo();
    }

    private void showSpaceInfo() {
        Space space = (Space) cmbSpaces.getSelectedItem();
        if (space == null) {
            return;
        }

        lblStatus.setText(
                "Estado: " + (space.isSpaceTaken() ? "Ocupado" : "Libre")
        );

        if (space.isSpaceTaken() && space.getEntryTime() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            lblEntryTime.setText(
                    "Hora de entrada: " + sdf.format(space.getEntryTime())
            );
        } else {
            lblEntryTime.setText("Hora de entrada: —");
        }

        // 🔥 AQUÍ ESTÁ LA CLAVE
        if (space.isSpaceTaken() && space.getVehicle() != null) {
            Vehicle vehicle = space.getVehicle();

            // Muestra TODO lo que viene del JSON
            txtVehicleInfo.setText(vehicle.toString());

        } else {
            txtVehicleInfo.setText("El espacio se encuentra libre.");
        }
    }

    private void releaseSpace() {
        Space space = (Space) cmbSpaces.getSelectedItem();
        if (space == null) {
            return;
        }

        if (!space.isSpaceTaken()) {
            JOptionPane.showMessageDialog(
                    this,
                    "El espacio ya está libre",
                    "Aviso",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Desea liberar el espacio?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = spaceController.releaseSpace(space.getId());

            JOptionPane.showMessageDialog(
                    this,
                    ok ? "Espacio liberado correctamente"
                            : "Error al liberar el espacio"
            );

            loadSpaces();
        }
    }

    private void occupySpace() {
        Space space = (Space) cmbSpaces.getSelectedItem();
        Vehicle vehicle = (Vehicle) cmbVehicles.getSelectedItem();

        if (space == null || vehicle == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar espacio y vehículo");
            return;
        }

        if (space.isSpaceTaken()) {
            JOptionPane.showMessageDialog(this, "El espacio ya está ocupado");
            return;
        }

        // Tomamos el cliente del vehículo (YA VIENE DEL JSON)
        Client client = vehicle.getClients().isEmpty()
                ? null
                : vehicle.getClients().get(0);

        if (client == null) {
            JOptionPane.showMessageDialog(this, "El vehículo no tiene cliente asociado");
            return;
        }

        String result = spaceController.occupySpace(
                space.getId(),
                client,
                vehicle
        );

        JOptionPane.showMessageDialog(this, result);
        loadSpaces();
    }

    private void loadVehicles() {
        cmbVehicles.removeAllItems();
        ArrayList<Vehicle> vehicles = vehicleData.getAllVehicles();

        for (Vehicle v : vehicles) {
            cmbVehicles.addItem(v);
        }
    }

}
