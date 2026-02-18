package view;

import Controller.ParkingLotController;
import model.entities.ParkingLot;
import model.entities.Space;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class ParkingLotViewInternal extends JInternalFrame {

    private final ParkingLotController parkingLotController = new ParkingLotController();

    private JTextField txtId, txtName, txtNumberOfSpaces;
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtDisabled;
    private JTextField txtMotorcycle;
    private JTextField txtTruck;
    private JTextField txtPreferential;

    private JButton btnSave, btnUpdate, btnDelete, btnClear, btnViewDetails;

    public ParkingLotViewInternal() {
        super("Gestión de Parqueos", true, true, true, true);
        setSize(920, 640);
        setLayout(null);
        getContentPane().setBackground(UITheme.BACKGROUND);
        setVisible(true);

        initComponents();
        loadTable();
        generateNextId();
    }

    private void initComponents() {

        int x = 30, y = 20, labelWidth = 150, fieldWidth = 120, spacing = 35;

        //this is for better design and uniformity in the view
        JPanel panel = new JPanel(null);
        panel.setBounds(x, 20, 320, 580);
        panel.setBackground(UITheme.PANEL_BG);
        panel.setBorder(UITheme.panelBorder());
        add(panel);

        JLabel lblId = new JLabel("ID Parqueo:");
        lblId.setBounds(x, y, labelWidth, 25);
        panel.add(lblId);

        txtId = new JTextField();
        txtId.setBounds(x + labelWidth + 10, y, fieldWidth, 25);
        txtId.setEditable(false);
        panel.add(txtId);
        y += spacing;

        JLabel lblName = new JLabel("Nombre del Parqueo:");
        lblName.setBounds(x, y, labelWidth, 25);
        panel.add(lblName);

        txtName = new JTextField();
        txtName.setBounds(x + labelWidth + 10, y, fieldWidth, 25);
        panel.add(txtName);
        y += spacing;

        JLabel lblNumber = new JLabel("Número de Espacios:");
        lblNumber.setBounds(x, y, labelWidth, 25);
        panel.add(lblNumber);

        txtNumberOfSpaces = new JTextField();
        txtNumberOfSpaces.setBounds(x + labelWidth + 10, y, fieldWidth, 25);
        panel.add(txtNumberOfSpaces);
        y += spacing;

        JLabel lblPreferential = new JLabel("Espacios Preferenciales:");
        lblPreferential.setBounds(x, y, labelWidth, 25);
        panel.add(lblPreferential);

        txtPreferential = new JTextField();
        txtPreferential.setBounds(x + labelWidth + 10, y, fieldWidth, 25);
        panel.add(txtPreferential);
        y += spacing;

        JLabel lblMotorcycle = new JLabel("Espacios Motocicleta:");
        lblMotorcycle.setBounds(x, y, labelWidth, 25);
        panel.add(lblMotorcycle);

        txtMotorcycle = new JTextField();
        txtMotorcycle.setBounds(x + labelWidth + 10, y, fieldWidth, 25);
        panel.add(txtMotorcycle);
        y += spacing;

        JLabel lblTruck = new JLabel("Espacios Camión:");
        lblTruck.setBounds(x, y, labelWidth, 25);
        panel.add(lblTruck);

        txtTruck = new JTextField();
        txtTruck.setBounds(x + labelWidth + 10, y, fieldWidth, 25);
        panel.add(txtTruck);
        y += spacing;

        btnSave = createButton("Guardar Nuevo", x, y, 140, 35, new Color(46, 125, 50));
        btnClear = createButton("Limpiar", x + 150, y, 90, 35, new Color(120, 144, 156));
        y += 45;

        btnUpdate = createButton("Actualizar", x, y, 140, 35, new Color(21, 101, 192));
        btnUpdate.setEnabled(false);

        btnDelete = createButton("Eliminar", x + 150, y, 90, 35, new Color(198, 40, 40));
        btnDelete.setEnabled(false);
        y += 45;

        btnViewDetails = createButton("Ver Detalles", x, y, 240, 35, new Color(123, 31, 162));
        btnViewDetails.setEnabled(false);

        model = new DefaultTableModel(
                new String[]{"ID", "Nombre", "Espacios", "Ocupados", "Disponibles", "Estado"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        UITheme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus,
                    int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (column == 5) {
                    String estado = value.toString();
                    switch (estado) {
                        case "LLENO":
                            c.setForeground(Color.RED);
                            break;
                        case "CASI LLENO":
                            c.setForeground(Color.ORANGE);
                            break;
                        default:
                            c.setForeground(new Color(46, 125, 50));
                            break;
                    }
                } else {
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });

        btnSave.addActionListener(e -> saveParkingLot());
        btnDelete.addActionListener(e -> deleteParkingLot());

        btnClear.addActionListener(e -> {
            clearForm();
            generateNextId();
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                fillFormFromTable();
            }
        });
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(360, 20, 530, 520);
        sp.setBorder(UITheme.panelBorder());
        add(sp);

        btnViewDetails.addActionListener(e -> viewDetails());
        panel.add(btnSave);
        panel.add(btnDelete);
        panel.add(btnUpdate);
        panel.add(btnClear);
        panel.add(btnViewDetails);

    }

    private JButton createButton(String text, int x, int y, int w, int h, Color color) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, w, h);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        add(btn);
        return btn;
    }

    private void generateNextId() {
        ArrayList<ParkingLot> list = parkingLotController.getAllParkingLots();
        int maxId = list.stream().mapToInt(ParkingLot::getId).max().orElse(0);
        txtId.setText(String.valueOf(maxId + 1));
    }

    private void loadTable() {
        model.setRowCount(0);

        ArrayList<ParkingLot> parkingLots = parkingLotController.getAllParkingLots();

        for (ParkingLot lot : parkingLots) {

            int occupied = 0;

            if (lot.getSpaces() != null) {
                for (Space s : lot.getSpaces()) {
                    if (s.isSpaceTaken()) {
                        occupied++;
                    }
                }
            }

            int available = lot.getNumberOfSpaces() - occupied;

            String status = available == 0 ? "LLENO"
                    : (available <= lot.getNumberOfSpaces() * 0.1
                    ? "CASI LLENO" : "DISPONIBLE");

            model.addRow(new Object[]{
                lot.getId(),
                lot.getName(),
                lot.getNumberOfSpaces(),
                occupied,
                available,
                status
            });
        }
    }

    private void saveParkingLot() {
        try {

            String name = txtName.getText().trim();
            int totalSpaces = Integer.parseInt(txtNumberOfSpaces.getText().trim());
            int preferential = Integer.parseInt(txtPreferential.getText().trim());
            int motorcycle = Integer.parseInt(txtMotorcycle.getText().trim());
            int truck = Integer.parseInt(txtTruck.getText().trim());

            int sum = preferential + motorcycle + truck;

            if (sum > totalSpaces) {
                JOptionPane.showMessageDialog(this,
                        "La suma de espacios no puede ser mayor al total");
                return;
            }

            parkingLotController.registerParkingLot(
                    name,
                    totalSpaces,
                    preferential,
                    motorcycle,
                    truck
            );

            JOptionPane.showMessageDialog(this,
                    "Parqueo registrado correctamente");

            loadTable();
            clearForm();
            generateNextId();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error: Verifique los datos ingresados");
        }
    }

    private void deleteParkingLot() {

        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un parqueo primero");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro que desea eliminar este parqueo?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        int id = Integer.parseInt(model.getValueAt(row, 0).toString());

        String message = parkingLotController.removeParkingLot(id);

        JOptionPane.showMessageDialog(this, message);

        loadTable();
        clearForm();
        generateNextId();
    }

    private void viewDetails() {

        int row = table.getSelectedRow();
        if (row == -1) {
            return;
        }

        int id = Integer.parseInt(model.getValueAt(row, 0).toString());

        ParkingLot lot = parkingLotController.findParkingLotById(id);

        if (lot == null) {
            return;
        }

        StringBuilder details = new StringBuilder();

        details.append("Parqueo: ").append(lot.getName()).append("\n");
        details.append("Espacios Totales: ").append(lot.getNumberOfSpaces()).append("\n\n");

        if (lot.getSpaces() != null) {
            for (Space s : lot.getSpaces()) {
                details.append("Espacio ")
                        .append(s.getId())
                        .append(" - ")
                        .append(s.isSpaceTaken() ? "Ocupado" : "Libre")
                        .append("\n");
            }
        }

        //this is for scroll
        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(350, 250));

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "Detalles del Parqueo",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void clearForm() {
        txtName.setText("");
        txtNumberOfSpaces.setText("");
        txtId.setText("");
        table.clearSelection();
        btnDelete.setEnabled(false);
        btnViewDetails.setEnabled(false);
    }

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        txtId.setText(model.getValueAt(row, 0).toString());
        txtName.setText(model.getValueAt(row, 1).toString());
        txtNumberOfSpaces.setText(model.getValueAt(row, 2).toString());
        btnDelete.setEnabled(true);
        btnViewDetails.setEnabled(true);
    }
}
