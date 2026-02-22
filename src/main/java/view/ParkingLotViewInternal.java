package view;

import controller.ParkingLotController;
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
    private JTextField txtMotorcycle;
    private JTextField txtTruck;
    private JTextField txtPreferential;
    private JTextField txtBicycle;

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

        JLabel lblBicyle = new JLabel("Espacios Bicicleta");
        lblBicyle.setBounds(x, y, labelWidth, 25);
        panel.add(lblBicyle);

        txtBicycle = new JTextField();
        txtBicycle.setBounds(x + labelWidth + 10, y, fieldWidth, 25);
        panel.add(txtBicycle);
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
        panel.add(btnSave);
        panel.add(btnDelete);
        panel.add(btnUpdate);
        panel.add(btnClear);
        panel.add(btnViewDetails);

        btnSave.addActionListener(e -> saveParkingLot());
        btnDelete.addActionListener(e -> deleteParkingLot());
        btnUpdate.addActionListener(e -> updateParkingLot());
        btnViewDetails.addActionListener(e -> viewDetails());
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

            int total = lot.getSpaces() != null ? lot.getSpaces().length : 0;
            int available = total - occupied;

            String status = available == 0 ? "LLENO"
                    : (available <= total * 0.1
                            ? "CASI LLENO" : "DISPONIBLE");

            model.addRow(new Object[]{
                lot.getId(),
                lot.getName(),
                total,
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
            int bicycle = Integer.parseInt(txtBicycle.getText().trim());

            int sum = preferential + motorcycle + truck + bicycle;
            if (sum > totalSpaces) {
                JOptionPane.showMessageDialog(this,
                        "La suma de espacios no puede ser mayor al total");
                return;
            }

            parkingLotController.registerParkingLot(
                    name,
                    totalSpaces,
                    0,
                    preferential,
                    motorcycle,
                    truck,
                    bicycle
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

    /**
     * Actualiza un parqueo existente
     */
    private void updateParkingLot() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un parqueo primero");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro que desea actualizar este parqueo?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        String newName = txtName.getText();
        int newNumberOfSpaces = Integer.parseInt(txtNumberOfSpaces.getText());
        // Verificar que el parqueo existe
        ParkingLot existingParkingLot = parkingLotController.findParkingLotById(id);
        if (existingParkingLot == null) {
            JOptionPane.showMessageDialog(this,
                    "No existe un parqueo con el ID: " + id,
                    "Parqueo No Encontrado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar campos requeridos
        if (!validateRequiredFields()) {
            return;
        }

        ParkingLot updateParkingLot = new ParkingLot();
        updateParkingLot.setId(id);
        updateParkingLot.setName(newName);
        updateParkingLot.setNumberOfSpaces(newNumberOfSpaces);
        updateParkingLot.setVehicles(existingParkingLot.getVehicles());

        String message = parkingLotController.updateParkingLot(id, updateParkingLot);
        JOptionPane.showMessageDialog(this, message);

        loadTable();
        clearForm();
        generateNextId();
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

    /**
     * Valida los campos del formulario
     */
    private boolean validateRequiredFields() {
        StringBuilder errors = new StringBuilder();

        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            errors.append("• Nombre del parqueo es obligatorio\n");
        } else if (name.length() < 3) {
            errors.append("• Nombre debe tener al menos 3 caracteres\n");
        }

        String spacesText = txtNumberOfSpaces.getText().trim();
        if (spacesText.isEmpty()) {
            errors.append("• Número de espacios es obligatorio\n");
        } else {
            try {
                int spaces = Integer.parseInt(spacesText);
                if (spaces <= 0) {
                    errors.append("• Número de espacios debe ser mayor a 0\n");
                } else if (spaces > 1000) {
                    errors.append("• Número de espacios no puede exceder 1000\n");
                }
            } catch (NumberFormatException e) {
                errors.append("• Número de espacios debe ser un número válido\n");
            }
        }

        if (errors.length() > 0) {
            JOptionPane.showMessageDialog(this,
                    "Por favor corrija los siguientes errores:\n\n" + errors.toString(),
                    "Validación Fallida",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    private void clearForm() {
        txtName.setText("");
        txtNumberOfSpaces.setText("");
        txtId.setText("");
        table.clearSelection();
        btnDelete.setEnabled(false);
        btnUpdate.setEnabled(false);
        btnViewDetails.setEnabled(false);
        txtMotorcycle.setText("");
        txtTruck.setText("");
        txtPreferential.setText("");
        txtBicycle.setText("");
    }

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        txtId.setText(model.getValueAt(row, 0).toString());
        txtName.setText(model.getValueAt(row, 1).toString());
        txtNumberOfSpaces.setText(model.getValueAt(row, 2).toString());
        btnUpdate.setEnabled(true);
        btnDelete.setEnabled(true);
        btnViewDetails.setEnabled(true);
    }
}
