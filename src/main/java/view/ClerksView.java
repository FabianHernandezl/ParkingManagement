/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import Controller.ClerkController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import model.data.ClerkData;
import model.data.ParkingLotData;
import model.entities.Clerk;
import model.entities.ParkingLot;

public class ClerksView extends JInternalFrame {

    private final ClerkController clerkController = new ClerkController();
    private JTextField txtId;
    private JTextField txtName;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtEmployeeCode;
    private JComboBox<String> cmbSchedule;
    private JTextField txtAge;
    private JComboBox<ParkingLot> cmbParkingLot;
    private JTable table;
    private DefaultTableModel model;
    private JButton btnUpdate;
    private JButton btnDelete;

    // Instancia de ClerkData para manejar datos persistentes
    private ClerkData clerkData;

    public ClerksView() {
        super("Gestión de Operarios de Parqueos", true, true, true, true);

        // Inicializar ClerkData
        clerkData = new ClerkData();

        setSize(800, 600);
        setLayout(null);
        setVisible(true);

        // Columna 1: Campos de entrada
        int x = 30;
        int y = 20;
        int labelWidth = 120;
        int fieldWidth = 180;
        int verticalSpacing = 30;

        // ID (Generado automáticamente)
        JLabel lblId = new JLabel("ID Operario:");
        lblId.setBounds(x, y, labelWidth, 25);
        lblId.setForeground(Color.BLUE);
        add(lblId);

        txtId = new JTextField();
        txtId.setBounds(x + labelWidth, y, fieldWidth, 20);
        txtId.setEditable(false); // No editable porque se genera automáticamente
        txtId.setBackground(new Color(240, 240, 240));
        add(txtId);
        y += verticalSpacing;

        // Código Empleado (Generado automáticamente)
        JLabel lblEmployeeCode = new JLabel("Código Empleado:");
        lblEmployeeCode.setBounds(x, y, labelWidth, 25);
        lblEmployeeCode.setForeground(Color.BLUE);
        add(lblEmployeeCode);

        txtEmployeeCode = new JTextField();
        txtEmployeeCode.setBounds(x + labelWidth + 5, y, fieldWidth, 20);
        txtEmployeeCode.setEditable(false); // No editable porque se genera automáticamente
        txtEmployeeCode.setBackground(new Color(240, 240, 240));
        add(txtEmployeeCode);
        y += verticalSpacing;

        // Nombre
        JLabel lblName = new JLabel("Nombre Completo:");
        lblName.setBounds(x, y, labelWidth, 25);
        add(lblName);

        txtName = new JTextField();
        txtName.setBounds(x + labelWidth + 10, y, fieldWidth, 25);
        add(txtName);
        y += verticalSpacing;

        // Username
        JLabel lblUsername = new JLabel("Usuario:");
        lblUsername.setBounds(x, y, labelWidth, 25);
        add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(x + labelWidth + 10, y, fieldWidth, 25);
        add(txtUsername);
        y += verticalSpacing;

        // Password
        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setBounds(x, y, labelWidth, 25);
        add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(x + labelWidth + 10, y, fieldWidth, 25);
        add(txtPassword);
        y += verticalSpacing;

        // Horario
        JLabel lblSchedule = new JLabel("Horario:");
        lblSchedule.setBounds(x, y, labelWidth, 25);
        add(lblSchedule);

        cmbSchedule = new JComboBox<>();
        loadSchedules();
        cmbSchedule.setBounds(x + labelWidth + 10, y, fieldWidth, 25);
        add(cmbSchedule);
        y += verticalSpacing;

        // Edad
        JLabel lblAge = new JLabel("Edad:");
        lblAge.setBounds(x, y, labelWidth, 25);
        add(lblAge);

        txtAge = new JTextField();
        txtAge.setBounds(x + labelWidth + 10, y, fieldWidth, 25);
        add(txtAge);
        y += verticalSpacing;

        // Parqueo Asignado
        JLabel lblParkingLot = new JLabel("Parqueo Asignado:");
        lblParkingLot.setBounds(x, y, labelWidth, 25);
        add(lblParkingLot);

        cmbParkingLot = new JComboBox<>();
        loadParkingLots();
        cmbParkingLot.setBounds(x + labelWidth + 10, y, fieldWidth, 25);
        add(cmbParkingLot);
        y += verticalSpacing;

        // Botones
        JButton btnSave = new JButton("Guardar Nuevo");
        btnSave.setBounds(x, y, 140, 30);
        btnSave.setBackground(new Color(76, 175, 80));
        btnSave.setForeground(Color.WHITE);
        add(btnSave);

        JButton btnClear = new JButton("Limpiar");
        btnClear.setBounds(x + 150, y, 90, 30);
        btnClear.setBackground(new Color(158, 158, 158));
        btnClear.setForeground(Color.WHITE);
        add(btnClear);
        y += 40;

        btnUpdate = new JButton("Actualizar");
        btnUpdate.setBounds(x, y, 140, 30);
        btnUpdate.setBackground(new Color(33, 150, 243));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setEnabled(false);
        add(btnUpdate);

        btnDelete = new JButton("Eliminar");
        btnDelete.setBounds(x + 150, y, 90, 30);
        btnDelete.setBackground(new Color(244, 67, 54));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setEnabled(false);
        add(btnDelete);

        // Tabla
        model = new DefaultTableModel(new String[]{
            "ID", "Nombre", "Usuario", "Código", "Horario", "Edad", "Parqueo"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);

        table.getColumnModel().getColumn(0).setPreferredWidth(80);   // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(120);  // Nombre
        table.getColumnModel().getColumn(2).setPreferredWidth(100);  // Usuario
        table.getColumnModel().getColumn(3).setPreferredWidth(70);   // Código
        table.getColumnModel().getColumn(4).setPreferredWidth(100);  // Horario
        table.getColumnModel().getColumn(5).setPreferredWidth(50);   // Edad
        table.getColumnModel().getColumn(6).setPreferredWidth(120);  // Parqueo

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(350, 20, 420, 500);
        add(scrollPane);

        // Cargar datos iniciales
        loadTable();

        // Generar IDs para nuevo registro
        generateNextIds();

        // Listeners
        btnSave.addActionListener(e -> saveClerk());
        btnUpdate.addActionListener(e -> updateClerk());
        btnDelete.addActionListener(e -> deleteClerk());
        btnClear.addActionListener(e -> {
            clearForm();
            generateNextIds();
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                fillFormFromTable();
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
            }
        });
    }

    /**
     * Genera el próximo ID y código de empleado automáticamente
     */
    private void generateNextIds() {
        try {
            // Generar próximo ID
            int nextIdNumber = clerkData.findLastIdNumberOfClerk() + 1;
            String nextId = String.format("CLK%04d", nextIdNumber);
            txtId.setText(nextId);

            // Generar próximo código de empleado
            int nextEmployeeCode = clerkData.findLastEmployeeCode() + 1;
            txtEmployeeCode.setText(String.valueOf(nextEmployeeCode));

        } catch (Exception e) {
            // Valores por defecto si hay error
            txtId.setText("CLK0001");
            txtEmployeeCode.setText("1001");
            System.err.println("Error generando IDs: " + e.getMessage());
        }
    }

    private void loadParkingLots() {
        try {
            //método para obtener todos los parking lots
            ParkingLotData parkingLotData = new ParkingLotData();
            ArrayList<ParkingLot> parkingLots = parkingLotData.getAllParkingLots();

            cmbParkingLot.removeAllItems();
            cmbParkingLot.addItem(null); // Opción vacía para "Sin asignar"

            for (ParkingLot parkingLot : parkingLots) {
                cmbParkingLot.addItem(parkingLot);
            }
        } catch (Exception e) {
            System.err.println("Error cargando parqueos: " + e.getMessage());
        }
    }

    private void loadSchedules() {
        ArrayList<String> schedules = new ArrayList<>() {
            {
                add("Jornada Diurna");
                add("Jornada Nocturna");
                add("Medio Tiempo(Mañana)");
                add("Medio Tiempo(Tarde)");
            }
        };

        try {

            cmbSchedule.removeAllItems();
            cmbSchedule.addItem(null); // Opción vacía para "Sin asignar"

            for (String schedule : schedules) {
                cmbSchedule.addItem(schedule);
            }
        } catch (Exception e) {
            System.err.println("Error cargando horarios: " + e.getMessage());
        }
    }

    private void loadTable() {
        model.setRowCount(0);
        try {
            ArrayList<Clerk> clerks = clerkData.getAllClerks();

            for (Clerk clerk : clerks) {
                ParkingLot parkingLot = clerk.getParkingLot();
                String parkingLotName = (parkingLot != null) ? parkingLot.getName() : "Sin asignar";

                model.addRow(new Object[]{
                    clerk.getId(),
                    clerk.getName(),
                    clerk.getUsername(),
                    clerk.getEmployeeCode(),
                    clerk.getShedule(),
                    clerk.getAge(),
                    parkingLotName
                });
            }
        } catch (Exception e) {
            System.err.println("Error cargando tabla: " + e.getMessage());
        }
    }

    private void saveClerk() {
        try {
            // Validar campos requeridos
            if (!validateRequiredFields()) {
                return;
            }

            // Verificar si el ID ya existe
            String id = txtId.getText().trim();
            if (clerkData.findClerkById(id) != null) {
                JOptionPane.showMessageDialog(this,
                        "Error: Ya existe un operario con el ID: " + id + "\n"
                        + "Por favor, presione 'Nuevo Operario' para generar un nuevo ID.",
                        "ID Duplicado",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Verificar si el usuario ya existe
            String username = txtUsername.getText().trim();
            if (clerkData.findClerkByUsername(username) != null) {
                JOptionPane.showMessageDialog(this,
                        "Error: Ya existe un operario con el usuario: " + username,
                        "Usuario Duplicado",
                        JOptionPane.ERROR_MESSAGE);
                txtUsername.requestFocus();
                return;
            }

            // Crear objeto Clerk
            Clerk clerk = createClerkFromForm();

            // Guardar en ClerkData
            Clerk clerkToInsert = clerkData.addClerk(clerk);

            if (clerkToInsert != null) {
                JOptionPane.showMessageDialog(this,
                        "Operario registrado exitosamente\n"
                        + "ID: " + clerk.getId() + "\n"
                        + "Código: " + clerk.getEmployeeCode(),
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);

                loadTable();
                clearForm();
                generateNextIds();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error al guardar el operario",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Edad debe ser un número válido",
                    "Error de Formato",
                    JOptionPane.ERROR_MESSAGE);
            txtAge.requestFocus();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error inesperado: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateClerk() {
        try {
            String id = txtId.getText().trim();

            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Seleccione un operario de la tabla primero",
                        "Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Verificar que el operario existe
            Clerk existingClerk = clerkData.findClerkById(id);
            if (existingClerk == null) {
                JOptionPane.showMessageDialog(this,
                        "No existe un operario con el ID: " + id,
                        "Operario No Encontrado",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validar campos requeridos
            if (!validateRequiredFields()) {
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de actualizar los datos del operario?\n"
                    + "ID: " + id + "\n"
                    + "Código: " + txtEmployeeCode.getText(),
                    "Confirmar Actualización",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                Clerk updatedClerk = createClerkFromForm();
                boolean success = clerkData.updateClerk(updatedClerk);

                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Operario actualizado exitosamente",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);

                    loadTable();
                    clearForm();
                    generateNextIds();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Error al actualizar el operario",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Edad debe ser un número válido",
                    "Error de Formato",
                    JOptionPane.ERROR_MESSAGE);
            txtAge.requestFocus();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error inesperado: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteClerk() {
        String id = txtId.getText().trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un operario de la tabla primero",
                    "Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtener nombre para mostrar en confirmación
        String name = txtName.getText().trim();
        String displayName = name.isEmpty() ? id : name;

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar al operario?\n\n"
                + "ID: " + id + "\n"
                + "Nombre: " + displayName + "\n"
                + "\nEsta acción no se puede deshacer.",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = clerkData.removeClerk(id);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Operario eliminado exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);

                loadTable();
                clearForm();
                generateNextIds();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error al eliminar el operario",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Clerk createClerkFromForm() throws NumberFormatException {
        int employeeCode = Integer.parseInt(txtEmployeeCode.getText().trim());
        int age = Integer.parseInt(txtAge.getText().trim());
        ParkingLot parkingLot = (ParkingLot) cmbParkingLot.getSelectedItem();
        String schedule = (String) cmbSchedule.getSelectedItem();

        return new Clerk(
                employeeCode,
                schedule,
                age,
                parkingLot,
                txtId.getText().trim(),
                txtName.getText().trim(),
                txtUsername.getText().trim(),
                new String(txtPassword.getPassword())
        );
    }

    private boolean validateRequiredFields() {
        StringBuilder errors = new StringBuilder();

        if (txtName.getText().trim().isEmpty()) {
            errors.append("• Nombre es obligatorio\n");
        }

        if (txtUsername.getText().trim().isEmpty()) {
            errors.append("• Usuario es obligatorio\n");
        }

        if (txtPassword.getPassword().length == 0) {
            errors.append("• Contraseña es obligatoria\n");
        }

        if (cmbSchedule.getSelectedItem().equals(null)) {
            errors.append("• Horario es obligatorio\n");
        }

        if (txtAge.getText().trim().isEmpty()) {
            errors.append("• Edad es obligatoria\n");
        } else {
            try {
                int age = Integer.parseInt(txtAge.getText().trim());
                if (age < 18 || age > 70) {
                    errors.append("• Edad debe estar entre 18 y 70 años\n");
                }
            } catch (NumberFormatException e) {
                errors.append("• Edad debe ser un número válido\n");
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

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row == -1) {
            return;
        }

        try {
            // Obtener datos de la fila seleccionada
            String id = model.getValueAt(row, 0).toString();
            Clerk clerk = clerkData.findClerkById(id);

            if (clerk != null) {
                txtId.setText(clerk.getId());
                txtEmployeeCode.setText(String.valueOf(clerk.getEmployeeCode()));
                txtName.setText(clerk.getName());
                txtUsername.setText(clerk.getUsername());
                txtPassword.setText(clerk.getPassword());
                txtAge.setText(String.valueOf(clerk.getAge()));

                // Seleccionar el horario en el combo
                String schedule = (clerk.getShedule());
                if (schedule != null) {
                    cmbSchedule.setSelectedItem(schedule);
                } else {
                    cmbSchedule.setSelectedIndex(0);
                }

                // Seleccionar el parking lot en el combo
                ParkingLot parkingLot = clerk.getParkingLot();
                if (parkingLot != null) {
                    cmbParkingLot.setSelectedItem(parkingLot);
                } else {
                    cmbParkingLot.setSelectedIndex(0);
                }

                // Hacer el ID y código editables para actualización
                txtId.setEditable(false); // ID no se puede cambiar
                txtEmployeeCode.setEditable(false); // Código no se puede cambiar
            }
        } catch (Exception e) {
            System.err.println("Error al llenar formulario: " + e.getMessage());
        }
    }

    private void clearForm() {
        txtName.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        cmbSchedule.setSelectedIndex(0);
        txtAge.setText("");
        cmbParkingLot.setSelectedIndex(0);

        // Restaurar campos no editables
        txtId.setEditable(false);
        txtEmployeeCode.setEditable(false);

        table.clearSelection();

        // Deshabilitar botones de actualizar y eliminar
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);

        txtName.requestFocus();
    }

}
