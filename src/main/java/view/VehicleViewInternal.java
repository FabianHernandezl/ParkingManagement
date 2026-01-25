package view;

import controller.VehicleController;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.entities.Client;
import model.entities.Vehicle;
import model.entities.VehicleType;

public class VehicleViewInternal extends JInternalFrame {

    private final VehicleController vehicleController = new VehicleController();

    private JTextField txtPlate;
    private JTextField txtBrand;
    private JTextField txtModel;
    private JTextField txtColor;
    private JComboBox<String> cmbType;

    private JTable table;
    private DefaultTableModel model;

    private Client selectedClient; // solo para creación

    public VehicleViewInternal() {
        super("Gestión de Vehículos", true, true, true, true);

        setSize(820, 500);
        setLayout(null);
        setVisible(true);

        // ===== FORM =====
        JLabel lblPlate = new JLabel("Placa:");
        lblPlate.setBounds(30, 20, 80, 25);
        add(lblPlate);

        txtPlate = new JTextField();
        txtPlate.setBounds(120, 20, 160, 25);
        add(txtPlate);

        JLabel lblBrand = new JLabel("Marca:");
        lblBrand.setBounds(30, 60, 80, 25);
        add(lblBrand);

        txtBrand = new JTextField();
        txtBrand.setBounds(120, 60, 160, 25);
        add(txtBrand);

        JLabel lblModel = new JLabel("Modelo:");
        lblModel.setBounds(30, 100, 80, 25);
        add(lblModel);

        txtModel = new JTextField();
        txtModel.setBounds(120, 100, 160, 25);
        add(txtModel);

        JLabel lblColor = new JLabel("Color:");
        lblColor.setBounds(30, 140, 80, 25);
        add(lblColor);

        txtColor = new JTextField();
        txtColor.setBounds(120, 140, 160, 25);
        add(txtColor);

        JLabel lblType = new JLabel("Tipo:");
        lblType.setBounds(30, 180, 80, 25);
        add(lblType);

        cmbType = new JComboBox<>(new String[]{"Carro", "Moto", "Camión"});
        cmbType.setBounds(120, 180, 160, 25);
        add(cmbType);

        JButton btnSelectClient = new JButton("Asignar Cliente");
        btnSelectClient.setBounds(30, 220, 250, 30);
        add(btnSelectClient);

        JButton btnSave = new JButton("Guardar");
        btnSave.setBounds(30, 270, 115, 30);
        add(btnSave);

        JButton btnClear = new JButton("Limpiar");
        btnClear.setBounds(165, 270, 115, 30);
        add(btnClear);

        JButton btnUpdate = new JButton("Actualizar");
        btnUpdate.setBounds(30, 310, 115, 30);
        btnUpdate.setEnabled(false);
        add(btnUpdate);

        JButton btnDelete = new JButton("Eliminar");
        btnDelete.setBounds(165, 310, 115, 30);
        btnDelete.setEnabled(false);
        add(btnDelete);

        // ===== TABLE =====
        model = new DefaultTableModel(
                new String[]{"Placa", "Marca", "Modelo", "Color", "Tipo"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(320, 20, 470, 400);
        add(scrollPane);

        loadTable();

        // ===== EVENTS =====
        btnSelectClient.addActionListener((ActionEvent e) -> selectClient());
        btnSave.addActionListener(e -> saveVehicle());
        btnUpdate.addActionListener(e -> updateVehicle());
        btnDelete.addActionListener(e -> deleteVehicle());
        btnClear.addActionListener(e -> clearForm());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFormFromTable();
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
                txtPlate.setEnabled(false); // placa NO se edita
            }
        });
    }

    // ================= LOGIC =================
    private void loadTable() {
        model.setRowCount(0);
        for (Vehicle v : vehicleController.getAllVehicles()) {
            model.addRow(new Object[]{
                v.getPlate(),
                v.getBrand(),
                v.getModel(),
                v.getColor(),
                v.getVehicleType() != null ? v.getVehicleType().getDescription() : ""
            });
        }
    }

    private void saveVehicle() {
        if (!validateForm(true)) {
            return;
        }

        Vehicle v = buildVehicle();
        String result = vehicleController.insertVehicle(v);

        JOptionPane.showMessageDialog(this, result);
        loadTable();
        clearForm();
    }

    private void updateVehicle() {
        if (!validateForm(false)) {
            return;
        }

        Vehicle v = new Vehicle();
        v.setPlate(txtPlate.getText().trim());
        v.setBrand(txtBrand.getText().trim());
        v.setModel(txtModel.getText().trim());
        v.setColor(txtColor.getText().trim());
        v.setVehicleType(buildVehicleType());

        String result = vehicleController.updateVehicle(v);
        JOptionPane.showMessageDialog(this, result);

        loadTable();
        clearForm();
    }

    private void deleteVehicle() {
        String plate = txtPlate.getText().trim();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar vehículo con placa: " + plate + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(
                    this,
                    vehicleController.deleteVehicle(plate)
            );
            loadTable();
            clearForm();
        }
    }

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row == -1) {
            return;
        }

        txtPlate.setText(model.getValueAt(row, 0).toString());
        txtBrand.setText(model.getValueAt(row, 1).toString());
        txtModel.setText(model.getValueAt(row, 2).toString());
        txtColor.setText(model.getValueAt(row, 3).toString());
        cmbType.setSelectedItem(model.getValueAt(row, 4).toString());
    }

    private void clearForm() {
        txtPlate.setText("");
        txtBrand.setText("");
        txtModel.setText("");
        txtColor.setText("");
        cmbType.setSelectedIndex(0);
        selectedClient = null;

        txtPlate.setEnabled(true);
        table.clearSelection();
    }

    private void selectClient() {

        ClientViewInternal cv = new ClientViewInternal();

        // NO mostrar la ventana de una vez
        // solo se mostrará si el cliente no existe
        getDesktopPane().add(cv);

        Client c = cv.selectOrCreateClient(getDesktopPane());

        // Si el cliente no existía, esperar a que se cree
        if (c == null) {
            c = cv.getCreatedClient();
        }

        if (c != null) {
            selectedClient = c;
            JOptionPane.showMessageDialog(this,
                    "Cliente asignado: " + c.getName());
        }
    }

    private boolean validateForm(boolean creating) {
        if (txtPlate.getText().trim().isEmpty()
                || txtBrand.getText().trim().isEmpty()
                || txtModel.getText().trim().isEmpty()
                || txtColor.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Todos los campos son obligatorios",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (creating && selectedClient == null) {
            JOptionPane.showMessageDialog(this,
                    "Debe asignar al menos un cliente al vehículo",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private Vehicle buildVehicle() {
        Vehicle v = new Vehicle();
        v.setPlate(txtPlate.getText().trim());
        v.setBrand(txtBrand.getText().trim());
        v.setModel(txtModel.getText().trim());
        v.setColor(txtColor.getText().trim());
        v.setVehicleType(buildVehicleType());
        v.addClient(selectedClient);
        return v;
    }

    private VehicleType buildVehicleType() {
        VehicleType type = new VehicleType();
        type.setDescription(cmbType.getSelectedItem().toString());
        return type;
    }
}
