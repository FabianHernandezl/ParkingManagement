package view;

import controller.ClientController;
import controller.ParkingLotController;
import controller.ParkingRateController;
import controller.VehicleController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import model.entities.Client;
import model.entities.Vehicle;
import model.entities.VehicleType;
import model.entities.ParkingLot;

import java.awt.Cursor;
import java.awt.Font;
import java.util.ArrayList;

public class VehicleViewInternal extends JInternalFrame {

    private final VehicleController controller = new VehicleController();
    private final ClientController clientController = new ClientController();
    private final ParkingLotController parkingController = new ParkingLotController();
    private final ParkingRateController rateController = new ParkingRateController();

    private JTextField txtPlate, txtBrand, txtModel, txtColor;
    private JComboBox<String> cmbType;
    private JComboBox<ParkingLot> cmbParking;

    private JTable table;
    private DefaultTableModel model;

    private JButton btnSave, btnUpdate, btnDelete, btnClear, btnRemoveClient, btnClearSearch;

    private ArrayList<Client> selectedClients = new ArrayList<>();
    private DefaultListModel<String> clientListModel = new DefaultListModel<>();
    private JList<String> listClients;

    private JTextField txtSearchClient;
    private JPopupMenu popupClients;
    private JList<Client> listSuggestions;

    private JTextField txtSearchVehicle;
    private JPopupMenu popupVehicles;
    private JList<Vehicle> listVehicleSuggestions;

    public VehicleViewInternal() {
        super("Gestión de Vehículos", true, true, true, true);
        setSize(920, 640);
        setLayout(null);
        getContentPane().setBackground(UITheme.BACKGROUND);

        initForm();
        initTable();
        setupEvents();
        loadTable();
        loadParkings();
    }

    private void initForm() {

        JPanel panel = new JPanel(null);
        panel.setBounds(20, 20, 320, 580);
        panel.setBackground(UITheme.PANEL_BG);
        panel.setBorder(UITheme.panelBorder());
        add(panel);

        JLabel title = new JLabel("Vehículo");
        title.setFont(UITheme.TITLE_FONT);
        title.setBounds(10, 10, 200, 25);
        panel.add(title);

        int y = 50;

        panel.add(label("Placa:", y));
        txtPlate = field(panel, y);

        y += 35;
        panel.add(label("Marca:", y));
        txtBrand = field(panel, y);

        y += 35;
        panel.add(label("Modelo:", y));
        txtModel = field(panel, y);

        y += 35;
        panel.add(label("Color:", y));
        txtColor = field(panel, y);

        y += 35;
        panel.add(label("Tipo:", y));
        cmbType = new JComboBox<>(new String[]{"Carro", "Motocicleta", "Camión", "Bicicleta"});
        cmbType.setBounds(100, y, 200, 25);
        panel.add(cmbType);

        y += 35;
        panel.add(label("Parqueo:", y));
        cmbParking = new JComboBox<>();
        cmbParking.setBounds(100, y, 200, 25);
        panel.add(cmbParking);

        JLabel lblSearch = new JLabel("Buscar cliente:");
        lblSearch.setBounds(10, y + 40, 150, 25);
        lblSearch.setFont(UITheme.LABEL_FONT);
        panel.add(lblSearch);

        txtSearchClient = new JTextField();
        txtSearchClient.setBounds(10, y + 65, 290, 25);
        panel.add(txtSearchClient);

        JLabel lblClients = new JLabel("Clientes asignados:");
        lblClients.setBounds(10, y + 100, 150, 25);
        lblClients.setFont(UITheme.LABEL_FONT);
        panel.add(lblClients);

        listClients = new JList<>(clientListModel);
        JScrollPane spClients = new JScrollPane(listClients);
        spClients.setBounds(10, y + 125, 290, 70);
        panel.add(spClients);

        btnRemoveClient = new JButton("Quitar cliente");
        btnRemoveClient.setBounds(10, y + 205, 290, 30);
        UITheme.styleButton(btnRemoveClient, UITheme.DANGER);
        panel.add(btnRemoveClient);

        int btnY = y + 250;
        int btnWidth = 140;
        int btnHeight = 40;
        int gap = 10;

        btnSave = actionGrid(panel, "Guardar", UITheme.SUCCESS, 10, btnY, btnWidth, btnHeight);
        btnClear = actionGrid(panel, "Limpiar", UITheme.SECONDARY, 10 + btnWidth + gap, btnY, btnWidth, btnHeight);
        btnUpdate = actionGrid(panel, "Actualizar", UITheme.PRIMARY, 10, btnY + btnHeight + gap, btnWidth, btnHeight);
        btnDelete = actionGrid(panel, "Eliminar", UITheme.DANGER, 10 + btnWidth + gap, btnY + btnHeight + gap, btnWidth, btnHeight);

        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);

        popupClients = new JPopupMenu();
        popupClients.setFocusable(false);
        listSuggestions = new JList<>();
        listSuggestions.setFocusable(false);
        listSuggestions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollSuggestions = new JScrollPane(listSuggestions);
        scrollSuggestions.setFocusable(false);
        popupClients.add(scrollSuggestions);
    }

    private void initTable() {

        JPanel searchPanel = new JPanel(null);
        searchPanel.setBounds(360, 20, 530, 50);
        searchPanel.setBackground(UITheme.PANEL_BG);
        searchPanel.setBorder(UITheme.panelBorder());
        add(searchPanel);

        JLabel lblSearchVehicle = new JLabel("Buscar vehículo:");
        lblSearchVehicle.setBounds(10, 10, 150, 25);
        lblSearchVehicle.setFont(UITheme.LABEL_FONT);
        searchPanel.add(lblSearchVehicle);

        txtSearchVehicle = new JTextField();
        txtSearchVehicle.setBounds(130, 10, 335, 25);
        searchPanel.add(txtSearchVehicle);

        btnClearSearch = new JButton("X");
        btnClearSearch.setBounds(475, 10, 45, 25);
        btnClearSearch.setFont(new Font("Arial", Font.BOLD, 14));
        btnClearSearch.setFocusPainted(false);
        btnClearSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClearSearch.setToolTipText("Limpiar búsqueda");
        btnClearSearch.setBackground(new java.awt.Color(231, 76, 60));
        btnClearSearch.setForeground(java.awt.Color.WHITE);
        btnClearSearch.setBorderPainted(false);
        searchPanel.add(btnClearSearch);

        model = new DefaultTableModel(
                new String[]{"Placa", "Marca", "Modelo", "Color", "Tipo"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        UITheme.styleTable(table);

        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(360, 80, 530, 520);
        sp.setBorder(UITheme.panelBorder());
        add(sp);

        popupVehicles = new JPopupMenu();
        popupVehicles.setFocusable(false);
        listVehicleSuggestions = new JList<>();
        listVehicleSuggestions.setFocusable(false);
        listVehicleSuggestions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollVehicleSuggestions = new JScrollPane(listVehicleSuggestions);
        scrollVehicleSuggestions.setFocusable(false);
        popupVehicles.add(scrollVehicleSuggestions);
    }

    private void setupEvents() {

        btnSave.addActionListener(e -> save());
        btnUpdate.addActionListener(e -> update());
        btnDelete.addActionListener(e -> delete());
        btnClear.addActionListener(e -> clear());

        btnRemoveClient.addActionListener(e -> {
            int index = listClients.getSelectedIndex();
            if (index != -1) {
                Client removed = selectedClients.remove(index);
                clientListModel.remove(index);
                JOptionPane.showMessageDialog(
                        this,
                        "Cliente " + removed.getName() + " removido correctamente",
                        "Cliente eliminado",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Seleccione un cliente de la lista para quitar",
                        "Atención",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillForm();
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
                txtPlate.setEnabled(false);
            }
        });

        txtSearchClient.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {

            private void search() {

                String text = txtSearchClient.getText().trim().toLowerCase();

                if (text.isEmpty()) {
                    popupClients.setVisible(false);
                    return;
                }

                ArrayList<Client> results = new ArrayList<>();

                for (Client c : clientController.getAllClients()) {
                    if (String.valueOf(c.getId()).contains(text)
                            || c.getName().toLowerCase().contains(text)) {
                        results.add(c);
                    }
                }

                if (results.isEmpty()) {
                    popupClients.setVisible(false);
                    return;
                }

                listSuggestions.setListData(results.toArray(new Client[0]));

                if (!popupClients.isVisible()) {
                    SwingUtilities.invokeLater(() -> {
                        popupClients.show(txtSearchClient, 0, txtSearchClient.getHeight());
                        txtSearchClient.requestFocusInWindow();
                    });
                }
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                SwingUtilities.invokeLater(() -> search());
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                SwingUtilities.invokeLater(() -> search());
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                SwingUtilities.invokeLater(() -> search());
            }
        });

        listSuggestions.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                Client selected = listSuggestions.getSelectedValue();
                if (selected != null && !selectedClients.contains(selected)) {
                    selectedClients.add(selected);
                    clientListModel.addElement(selected.toString());
                    txtSearchClient.setText("");
                    popupClients.setVisible(false);
                    txtSearchClient.requestFocusInWindow();
                }
            }
        });

        btnClearSearch.addActionListener(e -> {
            txtSearchVehicle.setText("");
            popupVehicles.setVisible(false);
            loadTable();
            table.clearSelection();
        });

        txtSearchVehicle.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {

            private void searchVehicle() {

                String text = txtSearchVehicle.getText().trim().toLowerCase();

                if (text.isEmpty()) {
                    popupVehicles.setVisible(false);
                    loadTable();
                    return;
                }

                ArrayList<Vehicle> results = new ArrayList<>();

                for (Vehicle v : controller.getAllVehicles()) {
                    if (v.getPlate().toLowerCase().contains(text)
                            || v.getBrand().toLowerCase().contains(text)
                            || v.getModel().toLowerCase().contains(text)) {
                        results.add(v);
                    }
                }

                model.setRowCount(0);
                for (Vehicle v : results) {
                    model.addRow(new Object[]{
                        v.getPlate(),
                        v.getBrand(),
                        v.getModel(),
                        v.getColor(),
                        v.getVehicleType().getDescription()
                    });
                }

                if (!results.isEmpty()) {
                    listVehicleSuggestions.setListData(results.toArray(new Vehicle[0]));

                    if (!popupVehicles.isVisible()) {
                        popupVehicles.show(txtSearchVehicle, 0, txtSearchVehicle.getHeight());
                    }
                } else {
                    popupVehicles.setVisible(false);
                }
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                SwingUtilities.invokeLater(() -> searchVehicle());
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                SwingUtilities.invokeLater(() -> searchVehicle());
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                SwingUtilities.invokeLater(() -> searchVehicle());
            }
        });

        listVehicleSuggestions.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                Vehicle selected = listVehicleSuggestions.getSelectedValue();
                if (selected != null) {
                    for (int i = 0; i < model.getRowCount(); i++) {
                        if (model.getValueAt(i, 0).equals(selected.getPlate())) {
                            table.setRowSelectionInterval(i, i);
                            table.scrollRectToVisible(table.getCellRect(i, 0, true));
                            break;
                        }
                    }
                    popupVehicles.setVisible(false);
                    SwingUtilities.invokeLater(() -> txtSearchVehicle.requestFocusInWindow());
                }
            }
        });
    }

    private void loadTable() {
        model.setRowCount(0);
        for (Vehicle v : controller.getAllVehicles()) {
            model.addRow(new Object[]{
                v.getPlate(),
                v.getBrand(),
                v.getModel(),
                v.getColor(),
                v.getVehicleType().getDescription()
            });
        }
    }

    /**
     * Carga los parqueos en el combo box, filtrando solo aquellos que tienen
     * tarifas configuradas
     */
    private void loadParkings() {
        cmbParking.removeAllItems();

        int countWithRates = 0;

        for (ParkingLot p : parkingController.getAllParkingLots()) {
            // Verificar si el parqueo tiene tarifas
            if (rateController.parkingLotHasRates(p.getId())) {
                cmbParking.addItem(p);
                countWithRates++;
            } else {
                System.out.println("Parqueo " + p.getName() + " (ID: " + p.getId()
                        + ") no tiene tarifas - omitido del combo");
            }
        }

        if (cmbParking.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "⚠️ No hay parqueos con tarifas configuradas.\n\n"
                    + "Para poder estacionar vehículos, debe:\n"
                    + "1. Ir a la sección de Tarifas\n"
                    + "2. Configurar al menos una tarifa para algún parqueo\n"
                    + "3. Los parqueos sin tarifas no están disponibles para estacionar",
                    "Parqueos no disponibles",
                    JOptionPane.WARNING_MESSAGE);

            // Deshabilitar botón de guardar si no hay parqueos disponibles
            btnSave.setEnabled(false);
            btnSave.setToolTipText("No hay parqueos con tarifas disponibles");
        } else {
            btnSave.setEnabled(true);
            btnSave.setToolTipText("Guardar vehículo");
            System.out.println("Parqueos con tarifas cargados: " + countWithRates);
        }
    }

    private void fillForm() {

        int r = table.getSelectedRow();
        if (r == -1) {
            return;
        }

        String plate = model.getValueAt(r, 0).toString();

        txtPlate.setText(plate);
        txtBrand.setText(model.getValueAt(r, 1).toString());
        txtModel.setText(model.getValueAt(r, 2).toString());
        txtColor.setText(model.getValueAt(r, 3).toString());
        cmbType.setSelectedItem(model.getValueAt(r, 4));

        loadClientsForVehicle(plate);
    }

    private void loadClientsForVehicle(String plate) {
        selectedClients.clear();
        clientListModel.clear();

        Vehicle vehicle = controller.findVehicleByPlate(plate);

        if (vehicle != null && vehicle.getClients() != null) {
            for (Client client : vehicle.getClients()) {
                selectedClients.add(client);
                clientListModel.addElement(client.toString());
            }
        }
    }

    private void clear() {

        txtPlate.setText("");
        txtBrand.setText("");
        txtModel.setText("");
        txtColor.setText("");
        cmbType.setSelectedIndex(0);
        txtPlate.setEnabled(true);
        table.clearSelection();

        selectedClients.clear();
        clientListModel.clear();
        txtSearchClient.setText("");

        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    private void save() {

        // Verificar que haya parqueos disponibles
        if (cmbParking.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "No hay parqueos con tarifas disponibles.\n"
                    + "Configure tarifas en la sección correspondiente.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Vehicle v = build();
        ParkingLot parking = (ParkingLot) cmbParking.getSelectedItem();

        if (parking == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un parqueo");
            return;
        }

        // Validación adicional: verificar que el parqueo seleccionado tenga tarifas
        if (!rateController.parkingLotHasRates(parking.getId())) {
            JOptionPane.showMessageDialog(this,
                    "El parqueo seleccionado ya no tiene tarifas configuradas.\n"
                    + "Por favor, seleccione otro parqueo o configure las tarifas.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);

            // Recargar la lista de parqueos para actualizar
            loadParkings();
            return;
        }

        String resultado = controller.insertVehicle(v, parking);

        // Verificar si el resultado contiene algún mensaje de error específico
        if (resultado.contains("❌") || resultado.contains("No hay espacios")) {
            JOptionPane.showMessageDialog(this, resultado, "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, resultado, "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }

        loadTable();
        clear();

        // Refrescar lista de parqueos en el combo (por si cambió algo)
        loadParkings();
    }

    private void update() {
        Vehicle v = build();
        JOptionPane.showMessageDialog(this, controller.updateVehicle(v));
        loadTable();
        clear();
    }

    /**
     * Elimina el vehículo seleccionado y maneja las excepciones
     */
    private void delete() {
        String placa = txtPlate.getText();

        if (placa == null || placa.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay vehículo seleccionado para eliminar",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirmar eliminación
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar el vehículo con placa " + placa + "?\n"
                + "Esto también cerrará su ticket activo si existe.",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            String resultado = controller.deleteVehicle(placa);
            JOptionPane.showMessageDialog(this,
                    resultado,
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            loadTable();
            clear();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    "Error de validación: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al eliminar el vehículo: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Para depuración
        }
    }

    private Vehicle build() {

        Vehicle v = new Vehicle();
        v.setPlate(txtPlate.getText());
        v.setBrand(txtBrand.getText());
        v.setModel(txtModel.getText());
        v.setColor(txtColor.getText());

        String selected = cmbType.getSelectedItem().toString();

        VehicleType t;

        switch (selected) {
            case "Carro":
                t = new VehicleType(1, "Automóvil", 4, 5.0f);
                break;

            case "Motocicleta":
                t = new VehicleType(2, "Motocicleta", 2, 2.5f);
                break;

            case "Camión":
                t = new VehicleType(3, "Camión", 4, 8.0f);
                break;

            case "Bicicleta":
                t = new VehicleType(4, "Bicicleta", 2, 1.5f);
                break;

            default:
                t = new VehicleType(1, "Automóvil", 4, 5.0f);
        }

        v.setVehicleType(t);
        v.setClients(new ArrayList<>(selectedClients));

        return v;
    }

    private JLabel label(String text, int y) {
        JLabel l = new JLabel(text);
        l.setBounds(10, y, 80, 25);
        l.setFont(UITheme.LABEL_FONT);
        return l;
    }

    private JTextField field(JPanel p, int y) {
        JTextField t = new JTextField();
        t.setBounds(100, y, 200, 25);
        p.add(t);
        return t;
    }

    private JButton actionGrid(JPanel p, String text, java.awt.Color c, int x, int y, int width, int height) {
        JButton b = new JButton(text);
        b.setBounds(x, y, width, height);
        UITheme.styleButton(b, c);
        p.add(b);
        return b;
    }

    public void refreshTable() {
        loadTable();
        loadParkings(); // También refrescar la lista de parqueos
    }
}
