package view;

import Controller.ParkingLotController;
import model.entities.ParkingLot;
import model.entities.Space;
import model.entities.VehicleType;
import model.data.ParkingLotData;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import model.entities.Vehicle;

public class ParkingLotViewInternal extends JInternalFrame {

    private final ParkingLotController parkingLotController = new ParkingLotController();
    private final ParkingLotData parkingLotData = new ParkingLotData();

    private JTextField txtId, txtName, txtNumberOfSpaces;
    private JTable table;
    private DefaultTableModel model;

    private VehicleType carType, motorcycleType, truckType;
    private JButton btnSave, btnUpdate, btnDelete, btnClear, btnViewDetails, btnChangeFees;

    private JLabel lblCarFee, lblMotoFee, lblTruckFee;

    public ParkingLotViewInternal() {
        super("Gestión de Parqueos", true, true, true, true);
        setSize(800, 600);
        setLayout(null);
        setVisible(true);

        initializeVehicleTypes();
        initComponents();
        loadTable();
        generateNextId();
    }

    private void initializeVehicleTypes() {
        carType = new VehicleType(1, "Automóvil", 4, 5.0f);
        motorcycleType = new VehicleType(2, "Motocicleta", 2, 2.5f);
        truckType = new VehicleType(3, "Camión", 4, 6.5f);
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

        // Botones
        btnSave = createButton("Guardar Nuevo", x, y, 140, 35, new Color(46, 125, 50));
        btnClear = createButton("Limpiar", x + 150, y, 90, 35, new Color(120, 144, 156));
        y += 45;
        btnUpdate = createButton("Actualizar", x, y, 140, 35, new Color(21, 101, 192));
        btnUpdate.setEnabled(false);
        btnDelete = createButton("Eliminar", x + 150, y, 90, 35, new Color(198, 40, 40));
        btnDelete.setEnabled(false);
        y += 45;
        // Botón Ver Detalles
        btnViewDetails = createButton("Ver Detalles", x, y, 240, 35, new Color(123, 31, 162));
        btnViewDetails.setEnabled(false); // se habilitará cuando se seleccione un parqueo
        y += 45;

        btnViewDetails.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un parqueo primero.");
                return;
            }

            // Obtener el parking lot seleccionado
            int id = Integer.parseInt(model.getValueAt(row, 0).toString());
            ParkingLot selectedLot = parkingLotData.getAllParkingLots()
                    .stream().filter(l -> l.getId() == id)
                    .findFirst().orElse(null);

            if (selectedLot == null) {
                JOptionPane.showMessageDialog(this, "No se encontró el parqueo seleccionado.");
                return;
            }

            // Construir el mensaje con los detalles
            StringBuilder details = new StringBuilder();
            details.append("Parqueo: ").append(selectedLot.getName()).append("\n");
            details.append("Número de espacios: ").append(selectedLot.getNumberOfSpaces()).append("\n");
            details.append("Espacios ocupados: ").append(
                    (int) java.util.Arrays.stream(selectedLot.getSpaces())
                            .filter(Space::isSpaceTaken)
                            .count()
            ).append("\n");
            details.append("Espacios disponibles: ").append(
                    (int) java.util.Arrays.stream(selectedLot.getSpaces())
                            .filter(s -> !s.isSpaceTaken())
                            .count()
            ).append("\n\n");

            details.append("Detalles de cada espacio:\n");
            for (Space s : selectedLot.getSpaces()) {
                details.append("Espacio ").append(s.getId())
                        .append(s.isDisabilityAdaptation() ? " (Discapacitado)" : "")
                        .append(": ")
                        .append(s.isSpaceTaken() ? "Ocupado" : "Libre");
                if (s.isSpaceTaken() && s.getVehicle() != null) {
                    details.append(" - Vehículo: ").append(s.getVehicle().getPlate())
                            .append(" (").append(s.getVehicle().getVehicleType().getDescription()).append(")");
                }
                details.append("\n");
            }

            JOptionPane.showMessageDialog(this, details.toString(), "Detalles del Parqueo", JOptionPane.INFORMATION_MESSAGE);
        });

        btnChangeFees = new JButton("Cambiar Tarifas");
        btnChangeFees.setBounds(x, y + 90, 240, 35);
        btnChangeFees.setBackground(new Color(245, 124, 0));
        btnChangeFees.setForeground(Color.WHITE);
        btnChangeFees.setFont(new Font("Arial", Font.BOLD, 12));
        btnChangeFees.addActionListener(e -> feesVehicleTypes());
        add(btnChangeFees);

        // Tabla
        model = new DefaultTableModel(new String[]{"ID", "Nombre", "Espacios", "Ocupados", "Disponibles", "Estado"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Colorear columna Estado
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus,
                    int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 5) { // Estado
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

        // Listeners
        btnSave.addActionListener(e -> saveParkingLot());
        btnUpdate.addActionListener(e -> updateParkingLot());
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
        int nextId = parkingLotData.findLastIdNumberOfParkingLot() + 1;
        txtId.setText(String.valueOf(nextId));
    }

    private void loadTable() {
        model.setRowCount(0);
        ArrayList<ParkingLot> parkingLots = parkingLotData.getAllParkingLots();
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
                    : (available <= lot.getNumberOfSpaces() * 0.1 ? "CASI LLENO" : "DISPONIBLE");
            model.addRow(new Object[]{lot.getId(), lot.getName(), lot.getNumberOfSpaces(), occupied, available, status});
        }
    }

    private void saveParkingLot() {
        try {
            int totalSpaces = Integer.parseInt(txtNumberOfSpaces.getText().trim());
            SpaceConfiguration config = askForSpaceConfiguration(totalSpaces);
            if (config == null) {
                return;
            }

            ParkingLot lot = createParkingLotFromConfig(config);
            parkingLotData.addParkingLot(lot);
            loadTable();
            clearForm();
            generateNextId();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void updateParkingLot() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un parqueo para actualizar.");
            return;
        }

        try {
            int id = Integer.parseInt(txtId.getText().trim());
            String name = txtName.getText().trim();
            int totalSpaces = Integer.parseInt(txtNumberOfSpaces.getText().trim());

            // Opcional: permitir modificar la distribución de espacios
            SpaceConfiguration config = askForSpaceConfiguration(totalSpaces);
            if (config == null) {
                return;
            }

            // Crear nuevos espacios según la configuración
            Space[] spaces = new Space[totalSpaces];
            int idx = 0;
            for (int i = 0; i < config.getDisabledSpaces(); i++) {
                spaces[idx++] = new Space(idx, true, false, truckType);
            }
            for (int i = 0; i < config.getMotorcycleSpaces(); i++) {
                spaces[idx++] = new Space(idx, false, false, motorcycleType);
            }
            for (int i = 0; i < config.getStandardSpaces(); i++) {
                spaces[idx++] = new Space(idx, false, false, carType);
            }

            // Obtener la lista de vehículos existentes para no perder la info
            ParkingLot oldLot = parkingLotData.getAllParkingLots()
                    .stream().filter(l -> l.getId() == id).findFirst().orElse(null);

            ArrayList<Vehicle> vehicles = oldLot != null ? oldLot.getVehicles() : new ArrayList<>();

            // Crear el nuevo objeto ParkingLot
            ParkingLot updatedLot = new ParkingLot(id, name, totalSpaces, vehicles, spaces);

            // Llamar al Controller
            String message = parkingLotController.updateParkingLot(id, updatedLot);
            JOptionPane.showMessageDialog(this, message);

            // Recargar tabla y limpiar formulario
            loadTable();
            clearForm();
            generateNextId();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Verifique que todos los campos estén correctamente llenos.");
        }
    }

    private void deleteParkingLot() {
        int row = table.getSelectedRow();
        if (row == -1) {
            return; // No hay selección
        }
        // Obtener el objeto ParkingLot seleccionado
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        ParkingLot selectedLot = parkingLotData.getAllParkingLots()
                .stream()
                .filter(lot -> lot.getId() == id)
                .findFirst()
                .orElse(null);

        if (selectedLot == null) {
            JOptionPane.showMessageDialog(this, "Parqueo no encontrado.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar parqueo seleccionado?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean deleted = parkingLotData.deleteParkingLot(selectedLot); // Usamos tu método
            if (deleted) {
                JOptionPane.showMessageDialog(this, "Parqueo eliminado correctamente.");
                loadTable();
                clearForm();
                generateNextId();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar el parqueo.");
            }
        }
    }

    private void clearForm() {
        txtName.setText("");
        txtNumberOfSpaces.setText("");
        txtId.setText("");
        table.clearSelection();
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        btnViewDetails.setEnabled(false);
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

    private SpaceConfiguration askForSpaceConfiguration(int totalSpaces) {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        JSpinner disabledSpinner = new JSpinner(new SpinnerNumberModel(Math.max(1, totalSpaces / 10), 0, totalSpaces, 1));
        JSpinner motorcycleSpinner = new JSpinner(new SpinnerNumberModel(Math.max(1, totalSpaces / 5), 0, totalSpaces, 1));
        JLabel lblStandard = new JLabel();
        disabledSpinner.addChangeListener(e -> updateStandardLabel(disabledSpinner, motorcycleSpinner, lblStandard, totalSpaces));
        motorcycleSpinner.addChangeListener(e -> updateStandardLabel(disabledSpinner, motorcycleSpinner, lblStandard, totalSpaces));
        updateStandardLabel(disabledSpinner, motorcycleSpinner, lblStandard, totalSpaces);

        panel.add(new JLabel("Total: " + totalSpaces));
        panel.add(new JLabel());
        panel.add(new JLabel("Espacios discapacitados:"));
        panel.add(disabledSpinner);
        panel.add(new JLabel("Espacios motocicletas:"));
        panel.add(motorcycleSpinner);
        panel.add(new JLabel("Espacios estándar:"));
        panel.add(lblStandard);

        int result = JOptionPane.showConfirmDialog(this, panel, "Configurar Distribución de Espacios", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int disabled = (int) disabledSpinner.getValue();
            int moto = (int) motorcycleSpinner.getValue();
            int standard = totalSpaces - disabled - moto;
            if (standard < 0) {
                JOptionPane.showMessageDialog(this, "Error: suma excede total");
                return null;
            }
            return new SpaceConfiguration(disabled, moto, standard);
        }
        return null;
    }

    private void updateStandardLabel(JSpinner dis, JSpinner moto, JLabel lbl, int total) {
        int standard = total - (int) dis.getValue() - (int) moto.getValue();
        lbl.setText(String.valueOf(standard));
        lbl.setForeground(standard >= 0 ? Color.BLACK : Color.RED);
    }

    private ParkingLot createParkingLotFromConfig(SpaceConfiguration config) {
        int id = Integer.parseInt(txtId.getText().trim());
        String name = txtName.getText().trim();
        int total = config.getTotalSpaces();
        Space[] spaces = new Space[total];
        int idx = 0;
        for (int i = 0; i < config.getDisabledSpaces(); i++) {
            spaces[idx++] = new Space(idx, true, false, truckType);
        }
        for (int i = 0; i < config.getMotorcycleSpaces(); i++) {
            spaces[idx++] = new Space(idx, false, false, motorcycleType);
        }
        for (int i = 0; i < config.getStandardSpaces(); i++) {
            spaces[idx++] = new Space(idx, false, false, carType);
        }
        return new ParkingLot(id, name, total, new ArrayList<>(), spaces);
    }

    private static class SpaceConfiguration {

        private int disabledSpaces, motorcycleSpaces, standardSpaces;

        public SpaceConfiguration(int d, int m, int s) {
            disabledSpaces = d;
            motorcycleSpaces = m;
            standardSpaces = s;
        }

        public int getDisabledSpaces() {
            return disabledSpaces;
        }

        public int getMotorcycleSpaces() {
            return motorcycleSpaces;
        }

        public int getStandardSpaces() {
            return standardSpaces;
        }

        public int getTotalSpaces() {
            return disabledSpaces + motorcycleSpaces + standardSpaces;
        }
    }

    private void feesVehicleTypes() {
        // Crear un panel con 3 filas: Automóvil, Motocicleta, Camión
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        // Campos de texto con tarifas actuales
        JTextField carField = new JTextField(String.valueOf(carType.getFee()));
        JTextField motoField = new JTextField(String.valueOf(motorcycleType.getFee()));
        JTextField truckField = new JTextField(String.valueOf(truckType.getFee()));

        panel.add(new JLabel("Automóviles: "));
        panel.add(carField);
        panel.add(new JLabel("Motocicletas: "));
        panel.add(motoField);
        panel.add(new JLabel("Camiones: "));
        panel.add(truckField);

        // Mostrar diálogo
        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Cambiar tarifas",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        // Si el usuario presiona OK
        if (result == JOptionPane.OK_OPTION) {
            try {
                float carFee = Float.parseFloat(carField.getText());
                float motoFee = Float.parseFloat(motoField.getText());
                float truckFee = Float.parseFloat(truckField.getText());

                // Validar que todas sean positivas
                if (carFee < 0 || motoFee < 0 || truckFee < 0) {
                    throw new NumberFormatException();
                }

                // Actualizar tarifas
                carType.setFee(carFee);
                motorcycleType.setFee(motoFee);
                truckType.setFee(truckFee);

                // Actualizar etiquetas en el panel
                updateFeeLabels();

                JOptionPane.showMessageDialog(this,
                        "Tarifas actualizadas correctamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Por favor ingrese tarifas válidas y positivas",
                        "Error de Formato",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateFeeLabels() {
        lblCarFee.setText("Tarifa Auto: " + carType.getFee());
        lblMotoFee.setText("Tarifa Moto: " + motorcycleType.getFee());
        lblTruckFee.setText("Tarifa Camión: " + truckType.getFee());
    }

}
