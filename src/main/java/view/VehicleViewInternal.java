package view;

import controller.VehicleController;
import java.awt.Font;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.entities.Client;
import model.entities.Vehicle;
import model.entities.VehicleType;
import java.util.ArrayList;

public class VehicleViewInternal extends JInternalFrame {

    private final VehicleController controller = new VehicleController();

    private JTextField txtPlate, txtBrand, txtModel, txtColor;
    private JComboBox<String> cmbType;
    private JTable table;
    private DefaultTableModel model;

    private JButton btnSave, btnUpdate, btnDelete, btnClear, btnAddClient, btnRemoveClient;

    private ArrayList<Client> selectedClients = new ArrayList<>();
    private DefaultListModel<String> clientListModel = new DefaultListModel<>();
    private JList<String> listClients;

    public VehicleViewInternal() {
        super("Gestión de Vehículos", true, true, true, true);
        setSize(900, 620);
        setLayout(null);
        getContentPane().setBackground(UITheme.BACKGROUND);

        initForm();
        initTable();
        setupEvents();
        loadTable();
    }

    private void initForm() {
        JPanel panel = new JPanel(null);
        panel.setBounds(20, 20, 320, 560);
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

        JLabel lblClients = new JLabel("Clientes:");
        lblClients.setBounds(10, y + 40, 100, 25);
        lblClients.setFont(UITheme.LABEL_FONT);
        panel.add(lblClients);

        listClients = new JList<>(clientListModel);
        JScrollPane sp = new JScrollPane(listClients);
        sp.setBounds(10, y + 65, 290, 70);
        panel.add(sp);

        btnAddClient = new JButton("Agregar");
        btnAddClient.setBounds(10, y + 145, 140, 30);
        UITheme.styleButton(btnAddClient, UITheme.SUCCESS);
        panel.add(btnAddClient);

        btnRemoveClient = new JButton("Quitar");
        btnRemoveClient.setBounds(160, y + 145, 140, 30);
        UITheme.styleButton(btnRemoveClient, UITheme.DANGER);
        panel.add(btnRemoveClient);

        btnSave = action(panel, "Guardar", UITheme.SUCCESS, y + 190);
        btnClear = action(panel, "Limpiar", UITheme.SECONDARY, y + 230);
        btnUpdate = action(panel, "Actualizar", UITheme.PRIMARY, y + 270);
        btnDelete = action(panel, "Eliminar", UITheme.DANGER, y + 310);

        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
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

    private JButton action(JPanel p, String text, java.awt.Color c, int y) {
        JButton b = new JButton(text);
        b.setBounds(10, y, 290, 30);
        UITheme.styleButton(b, c);
        p.add(b);
        return b;
    }

    private void initTable() {
        model = new DefaultTableModel(
                new String[]{"Placa", "Marca", "Modelo", "Color", "Tipo"}, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        UITheme.styleTable(table);

        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(360, 20, 510, 560);
        sp.setBorder(UITheme.panelBorder());
        add(sp);
    }

    private void setupEvents() {
        btnSave.addActionListener(e -> save());
        btnUpdate.addActionListener(e -> update());
        btnDelete.addActionListener(e -> delete());
        btnClear.addActionListener(e -> clear());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillForm();
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
                txtPlate.setEnabled(false);
            }
        });
    }

    private void loadTable() {
        model.setRowCount(0);
        for (Vehicle v : controller.getAllVehicles()) {
            model.addRow(new Object[]{
                v.getPlate(), v.getBrand(), v.getModel(),
                v.getColor(),
                v.getVehicleType().getDescription()
            });
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
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    private void save() {
        Vehicle v = build();
        JOptionPane.showMessageDialog(this, controller.insertVehicle(v));
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
        return v;
    }
}
