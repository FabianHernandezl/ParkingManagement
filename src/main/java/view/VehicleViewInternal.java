package view;

import Controller.ClientController;
import Controller.ParkingLotController;
import controller.VehicleController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import model.entities.Client;
import model.entities.Vehicle;
import model.entities.VehicleType;
import model.entities.ParkingLot;

import java.util.ArrayList;

public class VehicleViewInternal extends JInternalFrame {

    private final VehicleController controller = new VehicleController();
    private final ClientController clientController = new ClientController();
    private final ParkingLotController parkingController = new ParkingLotController();

    private JTextField txtPlate, txtBrand, txtModel, txtColor;
    private JComboBox<String> cmbType;
    private JComboBox<ParkingLot> cmbParking;

    private JTable table;
    private DefaultTableModel model;

    private JButton btnSave, btnUpdate, btnDelete, btnClear, btnRemoveClient;

    private ArrayList<Client> selectedClients = new ArrayList<>();
    private DefaultListModel<String> clientListModel = new DefaultListModel<>();
    private JList<String> listClients;

    // Buscador tipo Google
    private JTextField txtSearchClient;
    private JPopupMenu popupClients;
    private JList<Client> listSuggestions;

    public VehicleViewInternal() {
        super("Gestión de Vehículos", true, true, true, true);
        setSize(900, 650);
        setLayout(null);
        getContentPane().setBackground(UITheme.BACKGROUND);

        initForm();
        initTable();
        setupEvents();
        loadTable();
        loadParkings();
    }

    // ================= FORMULARIO =================
    private void initForm() {

        JPanel panel = new JPanel(null);
        panel.setBounds(20, 20, 320, 600);
        panel.setBackground(UITheme.PANEL_BG);
        panel.setBorder(UITheme.panelBorder());
        add(panel);

        JLabel title = new JLabel("🚗 Vehículo");
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
        cmbType = new JComboBox<>(new String[]{"Carro", "Moto", "Camión"});
        cmbType.setBounds(100, y, 200, 25);
        panel.add(cmbType);

        // ===== PARQUEO =====
        y += 35;
        panel.add(label("Parqueo:", y));
        cmbParking = new JComboBox<>();
        cmbParking.setBounds(100, y, 200, 25);
        panel.add(cmbParking);

        // ===== BUSCAR CLIENTE =====
        JLabel lblSearch = new JLabel("Buscar cliente:");
        lblSearch.setBounds(10, y + 40, 150, 25);
        lblSearch.setFont(UITheme.LABEL_FONT);
        panel.add(lblSearch);

        txtSearchClient = new JTextField();
        txtSearchClient.setBounds(10, y + 65, 290, 25);
        panel.add(txtSearchClient);

        // ===== CLIENTES ASIGNADOS =====
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

        btnSave = action(panel, "Guardar", UITheme.SUCCESS, y + 245);
        btnClear = action(panel, "Limpiar", UITheme.SECONDARY, y + 285);
        btnUpdate = action(panel, "Actualizar", UITheme.PRIMARY, y + 325);
        btnDelete = action(panel, "Eliminar", UITheme.DANGER, y + 365);

        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);

        // ===== POPUP DE SUGERENCIAS =====
        popupClients = new JPopupMenu();
        listSuggestions = new JList<>();
        listSuggestions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        popupClients.add(new JScrollPane(listSuggestions));
    }

    // ================= TABLA =================
    private void initTable() {

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
        sp.setBounds(360, 20, 510, 600);
        sp.setBorder(UITheme.panelBorder());
        add(sp);
    }

    // ================= EVENTOS =================
    private void setupEvents() {

        btnSave.addActionListener(e -> save());
        btnUpdate.addActionListener(e -> update());
        btnDelete.addActionListener(e -> delete());
        btnClear.addActionListener(e -> clear());

        btnRemoveClient.addActionListener(e -> {
            int index = listClients.getSelectedIndex();
            if (index != -1) {
                selectedClients.remove(index);
                clientListModel.remove(index);
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

        // ===== BUSCADOR TIPO GOOGLE =====
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
                popupClients.show(txtSearchClient, 0, txtSearchClient.getHeight());
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

        listSuggestions.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Client selected = listSuggestions.getSelectedValue();
                if (selected != null && !selectedClients.contains(selected)) {
                    selectedClients.add(selected);
                    clientListModel.addElement(selected.toString());
                    txtSearchClient.setText("");
                    popupClients.setVisible(false);
                }
            }
        });
    }

    // ================= CRUD =================
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

    private void loadParkings() {
        cmbParking.removeAllItems();
        for (ParkingLot p : parkingController.getAllParkingLots()) {
            cmbParking.addItem(p);
        }
    }

    private void fillForm() {

        int r = table.getSelectedRow();
        if (r == -1) {
            return;
        }

        txtPlate.setText(model.getValueAt(r, 0).toString());
        txtBrand.setText(model.getValueAt(r, 1).toString());
        txtModel.setText(model.getValueAt(r, 2).toString());
        txtColor.setText(model.getValueAt(r, 3).toString());
        cmbType.setSelectedItem(model.getValueAt(r, 4));
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

        Vehicle v = build();
        ParkingLot parking = (ParkingLot) cmbParking.getSelectedItem();

        if (parking == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un parqueo");
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                controller.insertVehicle(v, parking)
        );

        loadTable();
        clear();
    }

    private void update() {
        Vehicle v = build();
        JOptionPane.showMessageDialog(this, controller.updateVehicle(v));
        loadTable();
        clear();
    }

    private void delete() {
        JOptionPane.showMessageDialog(this,
                controller.deleteVehicle(txtPlate.getText()));
        loadTable();
        clear();
    }

    private Vehicle build() {

        Vehicle v = new Vehicle();
        v.setPlate(txtPlate.getText());
        v.setBrand(txtBrand.getText());
        v.setModel(txtModel.getText());
        v.setColor(txtColor.getText());

        VehicleType t = new VehicleType();
        t.setDescription(cmbType.getSelectedItem().toString());
        v.setVehicleType(t);

        v.setClients(new ArrayList<>(selectedClients));

        return v;
    }

    // ================= HELPERS =================
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

    private JButton action(JPanel p, String text, java.awt.Color c, int y) {
        JButton b = new JButton(text);
        b.setBounds(10, y, 290, 30);
        UITheme.styleButton(b, c);
        p.add(b);
        return b;
    }
}
