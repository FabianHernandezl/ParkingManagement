/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import javax.swing.JInternalFrame;
import controller.AdministratorController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import model.data.AdministratorData;
import model.data.ParkingLotData;
import model.entities.Administrator;
import model.entities.ParkingLot;
/**
 *
 * @author camila
 */
public class AdminView extends JInternalFrame{

    private final AdministratorController administratorController = new AdministratorController();
    private JTextField txtId;
    private JTextField txtName;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtAdminNumber;
    private JList<ParkingLot> lstParkingLots;
    private DefaultListModel<ParkingLot> parkingLotModel;
    private JTable table;
    private DefaultTableModel model;
    private JButton btnUpdate;
    private JButton btnDelete;

    

    public AdminView() {
        super("Gestión de Administradores", true, true, true, true);


        setSize(900, 600);
        setLayout(null);
        setVisible(true);

        // Columna 1: Campos de entrada
        int x = 30;
        int y = 20;
        int labelWidth = 140;
        int fieldWidth = 200;
        int verticalSpacing = 30;

        // ID (Generado automáticamente)
        JLabel lblId = new JLabel("ID Administrador:");
        lblId.setBounds(x, y, labelWidth, 25);
        lblId.setForeground(Color.BLUE);
        add(lblId);

        txtId = new JTextField();
        txtId.setBounds(x + labelWidth, y, fieldWidth, 25);
        txtId.setEditable(false);
        txtId.setBackground(new Color(240, 240, 240));
        add(txtId);
        y += verticalSpacing;

        // Número de Administrador (Generado automáticamente)
        JLabel lblAdminNumber = new JLabel("Número Administrador:");
        lblAdminNumber.setBounds(x, y, labelWidth, 25);
        lblAdminNumber.setForeground(Color.BLUE);
        add(lblAdminNumber);

        txtAdminNumber = new JTextField();
        txtAdminNumber.setBounds(x + labelWidth, y, fieldWidth, 25);
        txtAdminNumber.setEditable(false);
        txtAdminNumber.setBackground(new Color(240, 240, 240));
        add(txtAdminNumber);
        y += verticalSpacing;

        // Nombre
        JLabel lblName = new JLabel("Nombre Completo:");
        lblName.setBounds(x, y, labelWidth, 25);
        add(lblName);

        txtName = new JTextField();
        txtName.setBounds(x + labelWidth, y, fieldWidth, 25);
        add(txtName);
        y += verticalSpacing;

        // Username
        JLabel lblUsername = new JLabel("Usuario:");
        lblUsername.setBounds(x, y, labelWidth, 25);
        add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(x + labelWidth, y, fieldWidth, 25);
        add(txtUsername);
        y += verticalSpacing;

        // Password
        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setBounds(x, y, labelWidth, 25);
        add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(x + labelWidth, y, fieldWidth, 25);
        add(txtPassword);
        y += verticalSpacing;

        // Parqueo/s Asignado/s
        JLabel lblParkingLot = new JLabel("Parqueo/s Asignado/s:");
        lblParkingLot.setBounds(x, y, labelWidth, 25);
        add(lblParkingLot);
        y += 25;

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
                    setText(pl.getName() + " - " + pl.getNumberOfSpaces() + " espacios");
                } else if (value == null) {
                    setText("Sin asignar");
                }
                
                return this;
            }
        });
        
        // Crear JScrollPane para el JList
        JScrollPane scrollPanePL = new JScrollPane(lstParkingLots);
        scrollPanePL.setBounds(x + labelWidth, y, fieldWidth, 80);
        add(scrollPanePL);
        y += 90;

        // Etiqueta informativa
        JLabel lblInfo = new JLabel("Seleccione varios con Ctrl+Click o Shift+Click");
        lblInfo.setBounds(x + labelWidth, y, fieldWidth, 20);
        lblInfo.setFont(new Font("Arial", Font.ITALIC, 10));
        lblInfo.setForeground(Color.GRAY);
        add(lblInfo);
        y += 25;

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

        // Cargar los parking lots en el JList
        loadParkingLots();

        // Tabla
        model = new DefaultTableModel(new String[]{
            "ID", "Nombre", "Usuario", "Número Admin", "Parqueo/s"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);

        table.getColumnModel().getColumn(0).setPreferredWidth(100);   // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(150);   // Nombre
        table.getColumnModel().getColumn(2).setPreferredWidth(120);   // Usuario
        table.getColumnModel().getColumn(3).setPreferredWidth(80);    // Número Admin
        table.getColumnModel().getColumn(4).setPreferredWidth(200);   // Parqueo/s

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(400, 20, 450, 500);
        add(scrollPane);

        // Cargar datos iniciales
        loadTable();

        // Generar IDs para nuevo registro
        generateNextIds();

        // Listeners
        btnSave.addActionListener(e -> saveAdministrator());
        btnUpdate.addActionListener(e -> updateAdministrator());
        btnDelete.addActionListener(e -> deleteAdministrator());
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
     * Genera el próximo ID y número de administrador automáticamente
     */
    private void generateNextIds() {
        try {
            int nextIdNumber = administratorController.findLastAdminNumber()+ 1;
            String nextId = String.format("ADM%04d", nextIdNumber);
            txtId.setText(nextId);

            int nextAdminNumber = administratorController.findLastAdminNumber() + 1;
            txtAdminNumber.setText(String.valueOf(nextAdminNumber));

        } catch (Exception e) {
            txtId.setText("ADM0001");
            txtAdminNumber.setText("1001");
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

    private void loadTable() {
        model.setRowCount(0);
        try {
            ArrayList<Administrator> administrators = administratorController.getAllAdministrators();

            for (Administrator admin : administrators) {
                ArrayList<ParkingLot> parkingLots = admin.getParkingLot();
                
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
                    admin.getId(),
                    admin.getName(),
                    admin.getUsername(),
                    admin.getAdminNumber(),
                    parkingLotNames
                });
            }
        } catch (Exception e) {
            System.err.println("Error cargando tabla: " + e.getMessage());
        }
    }

    private void saveAdministrator() {
        try {
            if (!validateRequiredFields()) {
                return;
            }

            String id = txtId.getText().trim();
            if (administratorController.findAdministratorById(id) != null) {
                JOptionPane.showMessageDialog(this,
                        "Error: Ya existe un administrador con el ID: " + id + "\n"
                        + "Por favor, presione 'Limpiar' para generar un nuevo ID.",
                        "ID Duplicado",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String username = txtUsername.getText().trim();
            if (administratorController.findAdminByUsername(username) != null) {
                JOptionPane.showMessageDialog(this,
                        "Error: Ya existe un administrador con el usuario: " + username,
                        "Usuario Duplicado",
                        JOptionPane.ERROR_MESSAGE);
                txtUsername.requestFocus();
                return;
            }

            Administrator admin = createAdministratorFromForm();
            Administrator adminToInsert = administratorController.addAdministrator(admin);

            if (adminToInsert != null) {
                JOptionPane.showMessageDialog(this,
                        "Administrador registrado exitosamente\n"
                        + "ID: " + admin.getId() + "\n"
                        + "Número: " + admin.getAdminNumber(),
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);

                loadTable();
                clearForm();
                generateNextIds();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error al guardar el administrador",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Número de administrador debe ser un número válido",
                    "Error de Formato",
                    JOptionPane.ERROR_MESSAGE);
            txtAdminNumber.requestFocus();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error inesperado: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateAdministrator() {
        try {
            String id = txtId.getText().trim();

            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Seleccione un administrador de la tabla primero",
                        "Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Administrator existingAdmin = administratorController.findAdministratorById(id);
            if (existingAdmin == null) {
                JOptionPane.showMessageDialog(this,
                        "No existe un administrador con el ID: " + id,
                        "Administrador No Encontrado",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!validateRequiredFields()) {
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de actualizar los datos del administrador?\n"
                    + "ID: " + id + "\n"
                    + "Número: " + txtAdminNumber.getText(),
                    "Confirmar Actualización",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                Administrator updatedAdmin = createAdministratorFromForm();
                boolean success = administratorController.updateAdministrator(updatedAdmin);

                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Administrador actualizado exitosamente",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);

                    loadTable();
                    clearForm();
                    generateNextIds();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Error al actualizar el administrador",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Número de administrador debe ser un número válido",
                    "Error de Formato",
                    JOptionPane.ERROR_MESSAGE);
            txtAdminNumber.requestFocus();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error inesperado: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteAdministrator() {
        String id = txtId.getText().trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un administrador de la tabla primero",
                    "Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = txtName.getText().trim();
        String displayName = name.isEmpty() ? id : name;

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar al administrador?\n\n"
                + "ID: " + id + "\n"
                + "Nombre: " + displayName + "\n"
                + "\nEsta acción no se puede deshacer.",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = administratorController.removeAdministrator(id);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Administrador eliminado exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);

                loadTable();
                clearForm();
                generateNextIds();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error al eliminar el administrador",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Administrator createAdministratorFromForm() throws NumberFormatException {
        int adminNumber = Integer.parseInt(txtAdminNumber.getText().trim());
        
        // Obtener los parking lots seleccionados del JList
        ArrayList<ParkingLot> selectedParkingLots = new ArrayList<>(lstParkingLots.getSelectedValuesList());
        
        // Si solo se seleccionó "Sin asignar" (null), lista vacía
        if (selectedParkingLots.size() == 1 && selectedParkingLots.get(0) == null) {
            selectedParkingLots.clear();
        }

        return new Administrator(
                adminNumber,
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

        if (txtAdminNumber.getText().trim().isEmpty()) {
            errors.append("• Número de administrador es obligatorio\n");
        } else {
            try {
                Integer.parseInt(txtAdminNumber.getText().trim());
            } catch (NumberFormatException e) {
                errors.append("• Número de administrador debe ser un número válido\n");
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
            Administrator admin = administratorController.findAdministratorById(id);

            if (admin != null) {
                txtId.setText(admin.getId());
                txtAdminNumber.setText(String.valueOf(admin.getAdminNumber()));
                txtName.setText(admin.getName());
                txtUsername.setText(admin.getUsername());
                txtPassword.setText(admin.getPassword());

                // Seleccionar los parking lots en el JList
                ArrayList<ParkingLot> parkingLots = admin.getParkingLot();
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
                txtAdminNumber.setEditable(false);
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
        txtAdminNumber.setText("");
        lstParkingLots.clearSelection();

        txtId.setEditable(false);
        txtAdminNumber.setEditable(false);

        table.clearSelection();

        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);

        txtName.requestFocus();
    }
}

