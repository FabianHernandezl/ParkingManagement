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

    private JButton btnSave, btnUpdate, btnDelete, btnClear, btnViewDetails;

    public ParkingLotViewInternal() {
        super("Gestión de Parqueos", true, true, true, true);
        setSize(800, 600);
        setLayout(null);
        setVisible(true);

        initComponents();
        loadTable();
        generateNextId();
    }

    private void initComponents() {

        int x = 30, y = 20, labelWidth = 150, fieldWidth = 200, spacing = 35;

        JLabel lblId = new JLabel("ID Parqueo:");
        lblId.setBounds(x, y, labelWidth, 25);
        add(lblId);

        txtId = new JTextField();
        txtId.setBounds(x + labelWidth + 10, y, fieldWidth, 25);
        txtId.setEditable(false);
        add(txtId);
        y += spacing;

        JLabel lblName = new JLabel("Nombre del Parqueo:");
        lblName.setBounds(x, y, labelWidth, 25);
        add(lblName);

        txtName = new JTextField();
        txtName.setBounds(x + labelWidth + 10, y, fieldWidth, 25);
        add(txtName);
        y += spacing;

        JLabel lblNumber = new JLabel("Número de Espacios:");
        lblNumber.setBounds(x, y, labelWidth, 25);
        add(lblNumber);

        txtNumberOfSpaces = new JTextField();
        txtNumberOfSpaces.setBounds(x + labelWidth + 10, y, fieldWidth, 25);
        add(txtNumberOfSpaces);
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

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(400, 20, 370, 500);
        add(scrollPane);

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

        btnViewDetails.addActionListener(e -> viewDetails());
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

            int disabled = totalSpaces / 10;
            int motorcycle = totalSpaces / 5;

            parkingLotController.registerParkingLot(
                    name,
                    totalSpaces,
                    disabled,
                    motorcycle
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

        JOptionPane.showMessageDialog(this,
                details.toString(),
                "Detalles del Parqueo",
                JOptionPane.INFORMATION_MESSAGE);
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
