/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import controller.ClerkController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
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
    private JTextField txtSearch;
    private JList<ParkingLot> lstParkingLots;
    private DefaultListModel<ParkingLot> parkingLotModel; // Model for JList
    private JTable table;
    private DefaultTableModel model;
    private JButton btnUpdate;
    private JButton btnDelete;

    private TableRowSorter<DefaultTableModel> sorter;

    public ClerksView() {
        super("Gestión de Operarios de Parqueos", true, true, true, true);
        
        setSize(1000, 700);
        setLayout(null);  
        initComponents();
        loadTable();
        loadTable();
        setVisible(true);
    }

    private void initComponents() {

        //for cute format
        JPanel panel = new JPanel(null);
        panel.setBounds(30, 20, 380, 590);
        panel.setBackground(UITheme.PANEL_BG);
        panel.setBorder(UITheme.panelBorder());
        add(panel);

        // Column 1: entry fields
        int x = 30;
        int y = 20;
        int labelWidth = 120;
        int fieldWidth = 180;
        int verticalSpacing = 30;

        // ID (Generado automáticamente)
        JLabel lblId = new JLabel("ID Operario:");
        lblId.setBounds(x, y, labelWidth, 25);
        lblId.setForeground(Color.BLUE);
        panel.add(lblId);

        txtId = new JTextField();
        txtId.setBounds(x + labelWidth, y, fieldWidth, 20);
        txtId.setEditable(false);
        txtId.setBackground(new Color(240, 240, 240));
        panel.add(txtId);
        y += verticalSpacing;

        // Código Empleado (Generado automáticamente)
        JLabel lblEmployeeCode = new JLabel("Código Empleado:");
        lblEmployeeCode.setBounds(x, y, labelWidth, 25);
        lblEmployeeCode.setForeground(Color.BLUE);
        panel.add(lblEmployeeCode);

        txtEmployeeCode = new JTextField();
        txtEmployeeCode.setBounds(x + labelWidth + 5, y, fieldWidth, 20);
        txtEmployeeCode.setEditable(false);
        txtEmployeeCode.setBackground(new Color(240, 240, 240));
        panel.add(txtEmployeeCode);
        y += verticalSpacing;

        // Nombre
        JLabel lblName = new JLabel("Nombre Completo:");
        lblName.setBounds(x, y, labelWidth, 25);
        panel.add(lblName);

        txtName = new JTextField();
        txtName.setBounds(x + labelWidth + 10, y, fieldWidth, 25);
        panel.add(txtName);
        y += verticalSpacing;

        // Username
        JLabel lblUsername = new JLabel("Usuario:");
        lblUsername.setBounds(x, y, labelWidth, 25);
        panel.add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(x + labelWidth + 10, y, fieldWidth, 25);
        panel.add(txtUsername);
        y += verticalSpacing;

        // Password
        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setBounds(x, y, labelWidth, 25);
        panel.add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(x + labelWidth + 10, y, fieldWidth, 25);
        panel.add(txtPassword);
        y += verticalSpacing;

        // Horario
        JLabel lblSchedule = new JLabel("Horario:");
        lblSchedule.setBounds(x, y, labelWidth, 25);
        panel.add(lblSchedule);

        cmbSchedule = new JComboBox<>();
        loadSchedules();
        cmbSchedule.setBounds(x + labelWidth + 10, y, fieldWidth, 25);
        panel.add(cmbSchedule);
        y += verticalSpacing;

        // Edad
        JLabel lblAge = new JLabel("Edad:");
        lblAge.setBounds(x, y, labelWidth, 25);
        panel.add(lblAge);

        txtAge = new JTextField();
        txtAge.setBounds(x + labelWidth + 10, y, fieldWidth, 25);
        panel.add(txtAge);
        y += verticalSpacing;

        // Parqueo/s Asignado/s - AHORA CON JLIST
        JLabel lblParkingLot = new JLabel("Parqueo/s Asignado/s:");
        lblParkingLot.setBounds(x, y, labelWidth, 25);
        panel.add(lblParkingLot);
        y += 25; // Espacio extra para la etiqueta

        // Inicializar el JList con DefaultListModel
        parkingLotModel = new DefaultListModel<>();
        lstParkingLots = new JList<>(parkingLotModel);
        lstParkingLots.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Configurar renderer para mostrar mejor los ParkingLot
        lstParkingLots.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof ParkingLot) {
                    ParkingLot pl = (ParkingLot) value;
                    setText(pl.getName() + " - " + pl.getNumberOfSpaces());
                } else if (value == null) {
                    setText("Sin asignar");
                }

                return this;
            }
        });

        // Crear JScrollPane para el JList con posición y tamaño definidos
        JScrollPane scrollPanePL = new JScrollPane(lstParkingLots);
        scrollPanePL.setBounds(x + labelWidth + 10, y, fieldWidth, 100); // ALTURA AUMENTADA
        panel.add(scrollPanePL);
        y += 110; // Espacio después del JList

        // Etiqueta informativa
        JLabel lblInfo = new JLabel("Seleccione varios con Ctrl+Click o Shift+Click");
        lblInfo.setBounds(x + labelWidth + 10, y, fieldWidth, 20);
        lblInfo.setFont(new Font("Arial", Font.ITALIC, 10));
        lblInfo.setForeground(Color.GRAY);
        panel.add(lblInfo);
        y += 25;

        // Botones
        JButton btnSave = new JButton("Guardar Nuevo");
        btnSave.setBounds(x, y, 140, 30);
        btnSave.setBackground(new Color(76, 175, 80));
        btnSave.setForeground(Color.WHITE);
        panel.add(btnSave);

        JButton btnClear = new JButton("Limpiar");
        btnClear.setBounds(x + 150, y, 90, 30);
        btnClear.setBackground(new Color(158, 158, 158));
        btnClear.setForeground(Color.WHITE);
        panel.add(btnClear);
        y += 40;

        btnUpdate = new JButton("Actualizar");
        btnUpdate.setBounds(x, y, 140, 30);
        btnUpdate.setBackground(new Color(33, 150, 243));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setEnabled(false);
        panel.add(btnUpdate);

        btnDelete = new JButton("Eliminar");
        btnDelete.setBounds(x + 150, y, 90, 30);
        btnDelete.setBackground(new Color(244, 67, 54));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setEnabled(false);
        panel.add(btnDelete);

        // Cargar los parking lots en el JList
        loadParkingLots();

        // Tabla
        model = new DefaultTableModel(new String[]{
            "ID", "Nombre", "Usuario", "Código", "Horario", "Edad", "Parqueo/s"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        UITheme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(28);

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        table.getColumnModel().getColumn(0).setPreferredWidth(80);   // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(120);  // Nombre
        table.getColumnModel().getColumn(2).setPreferredWidth(100);  // Usuario
        table.getColumnModel().getColumn(3).setPreferredWidth(70);   // Código Empleado
        table.getColumnModel().getColumn(4).setPreferredWidth(100);  // Horario
        table.getColumnModel().getColumn(5).setPreferredWidth(50);   // Edad
        table.getColumnModel().getColumn(6).setPreferredWidth(150);  // Parqueo/s

        //Panel for the table
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(420, 80, 530, 520);
        scrollPane.setBorder(UITheme.panelBorder());
        add(scrollPane);

        // Cargar datos iniciales
        loadTable();

        // Generar IDs para nuevo registro
        generateNextIds();

        //for the search
        initSearch();
        setupSearch();
        
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
            int nextIdNumber = clerkController.findLastIdNumberOfClerk() + 1;
            String nextId = String.format("CLK%04d", nextIdNumber);
            txtId.setText(nextId);

            int nextEmployeeCode = clerkController.findLastEmployeeCode() + 1;
            txtEmployeeCode.setText(String.valueOf(nextEmployeeCode));

        } catch (Exception e) {
            txtId.setText("CLK0001");
            txtEmployeeCode.setText("1001");
            System.err.println("Error generando IDs: " + e.getMessage());
        }
    }

    private void loadParkingLots() {
        try {
            parkingLotModel.clear();

            // Agregar opción "Sin asignar" como primer elemento
            parkingLotModel.addElement(null);

            ParkingLotData parkingLotData = new ParkingLotData();
            ArrayList<ParkingLot> parkingLots = parkingLotData.getAllParkingLots();

            for (ParkingLot parkingLot : parkingLots) {
                parkingLotModel.addElement(parkingLot);
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
            cmbSchedule.addItem(null);

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
            ArrayList<Clerk> clerks = clerkController.getAllClerks();

            for (Clerk clerk : clerks) {
                ArrayList<ParkingLot> parkingLots = clerk.getParkingLot();

                // Formatear nombres de parking lots para mostrar en tabla
                String parkingLotNames;
                if (parkingLots != null && !parkingLots.isEmpty()) {
                    ArrayList<String> nombres = new ArrayList<>();
                    for (ParkingLot pl : parkingLots) {
                        nombres.add(pl.getName());
                    }
                    parkingLotNames = String.join(", ", nombres);
                } else {
                    parkingLotNames = "Sin asignar";
                }

                model.addRow(new Object[]{
                    clerk.getId(),
                    clerk.getName(),
                    clerk.getUsername(),
                    clerk.getEmployeeCode(),
                    clerk.getShedule(),
                    clerk.getAge(),
                    parkingLotNames
                });
            }
        } catch (Exception e) {
            System.err.println("Error cargando tabla: " + e.getMessage());
        }

    }

    private void initSearch() {
        JPanel searchPanel = new JPanel(null);
        searchPanel.setBounds(420, 20, 530, 50);
        searchPanel.setBackground(UITheme.PANEL_BG);
        searchPanel.setBorder(UITheme.panelBorder());
        add(searchPanel);

        JLabel lblSearchVehicle = new JLabel("Buscar:");
        lblSearchVehicle.setBounds(10, 10, 150, 25);
        lblSearchVehicle.setFont(UITheme.LABEL_FONT);
        searchPanel.add(lblSearchVehicle);

        txtSearch = new JTextField();
        txtSearch.setBounds(130, 10, 335, 25);
        searchPanel.add(txtSearch);

       
    }

    private void setupSearch() {
        // filter by 1 and 2 column
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {

            private void filter() {
                String text = txtSearch.getText().trim();

                if (text.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 0, 1));
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                filter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filter();
            }
        });
    }

    private void saveClerk() {
        try {
            if (!validateRequiredFields()) {
                return;
            }

            String id = txtId.getText().trim();
            if (clerkController.findClerkById(id) != null) {
                JOptionPane.showMessageDialog(this,
                        "Error: Ya existe un operario con el ID: " + id + "\n"
                        + "Por favor, presione 'Nuevo Operario' para generar un nuevo ID.",
                        "ID Duplicado",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String username = txtUsername.getText().trim();
            if (clerkController.findClerkByUsername(username) != null) {
                JOptionPane.showMessageDialog(this,
                        "Error: Ya existe un operario con el usuario: " + username,
                        "Usuario Duplicado",
                        JOptionPane.ERROR_MESSAGE);
                txtUsername.requestFocus();
                return;
            }

            Clerk clerk = createClerkFromForm();
            Clerk clerkToInsert = clerkController.addClerk(clerk);

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

            Clerk existingClerk = clerkController.findClerkById(id);
            if (existingClerk == null) {
                JOptionPane.showMessageDialog(this,
                        "No existe un operario con el ID: " + id,
                        "Operario No Encontrado",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

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
                boolean success = clerkController.updateClerk(updatedClerk);

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
            boolean success = clerkController.removeClerk(id);

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

        // Obtener los parking lots seleccionados del JList
        ArrayList<ParkingLot> selectedParkingLots = new ArrayList<>(lstParkingLots.getSelectedValuesList());

        // Si solo se seleccionó "Sin asignar" (null), lista vacía
        if (selectedParkingLots.size() == 1 && selectedParkingLots.get(0) == null) {
            selectedParkingLots.clear();
        }

        String schedule = (String) cmbSchedule.getSelectedItem();

        return new Clerk(
                employeeCode,
                schedule,
                age,
                selectedParkingLots,
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

        if (cmbSchedule.getSelectedItem() == null) {
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
            String id = model.getValueAt(row, 0).toString();
            Clerk clerk = clerkController.findClerkById(id);

            if (clerk != null) {
                txtId.setText(clerk.getId());
                txtEmployeeCode.setText(String.valueOf(clerk.getEmployeeCode()));
                txtName.setText(clerk.getName());
                txtUsername.setText(clerk.getUsername());
                txtPassword.setText(clerk.getPassword());
                txtAge.setText(String.valueOf(clerk.getAge()));

                // Seleccionar el horario en el combo
                String schedule = clerk.getShedule();
                if (schedule != null) {
                    cmbSchedule.setSelectedItem(schedule);
                } else {
                    cmbSchedule.setSelectedIndex(0);
                }

                // Seleccionar los parking lots en el JList
                ArrayList<ParkingLot> parkingLots = clerk.getParkingLot();
                if (parkingLots != null && !parkingLots.isEmpty()) {
                    // Buscar los índices de los parking lots en el modelo
                    ArrayList<Integer> indices = new ArrayList<>();
                    for (ParkingLot pl : parkingLots) {
                        for (int i = 0; i < parkingLotModel.getSize(); i++) {
                            ParkingLot item = parkingLotModel.getElementAt(i);
                            if (item != null && item.equals(pl)) {
                                indices.add(i);
                                break;
                            }
                        }
                    }

                    // Convertir a array y seleccionar
                    if (!indices.isEmpty()) {
                        int[] indicesArray = new int[indices.size()];
                        for (int i = 0; i < indices.size(); i++) {
                            indicesArray[i] = indices.get(i);
                        }
                        lstParkingLots.setSelectedIndices(indicesArray);
                    } else {
                        lstParkingLots.clearSelection();
                    }
                } else {
                    // Si no tiene parking lots asignados, seleccionar "Sin asignar" (índice 0)
                    lstParkingLots.setSelectedIndex(0);
                }

                txtId.setEditable(false);
                txtEmployeeCode.setEditable(false);
            }
        } catch (Exception e) {
            System.err.println("Error al llenar formulario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearForm() {
        txtName.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        cmbSchedule.setSelectedIndex(0);
        txtAge.setText("");
        lstParkingLots.clearSelection(); // Limpiar selección del JList

        txtId.setEditable(false);
        txtEmployeeCode.setEditable(false);

        table.clearSelection();

        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);

        txtName.requestFocus();
    }

}
