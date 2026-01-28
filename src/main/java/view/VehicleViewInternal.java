package view;

import controller.VehicleController;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.entities.Client;
import model.entities.Vehicle;
import model.entities.VehicleType;

public class VehicleViewInternal extends JInternalFrame {

    private final VehicleController vehicleController = new VehicleController();

    // ===== COLORES DEL TEMA =====
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);      // Azul principal
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);    // Azul claro
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);      // Verde
    private final Color DANGER_COLOR = new Color(231, 76, 60);        // Rojo
    private final Color WARNING_COLOR = new Color(241, 196, 15);      // Amarillo
    private final Color DARK_COLOR = new Color(44, 62, 80);           // Gris oscuro
    private final Color LIGHT_BG = new Color(236, 240, 241);          // Fondo claro
    private final Color WHITE = Color.WHITE;

    // ===== CAMPOS DEL FORMULARIO =====
    private JTextField txtPlate;
    private JTextField txtBrand;
    private JTextField txtModel;
    private JTextField txtColor;
    private JComboBox<String> cmbType;

    // ===== BÚSQUEDA =====
    private JTextField txtSearch;
    private JButton btnSearch;
    private JButton btnClearSearch;

    // ===== TABLA =====
    private JTable table;
    private DefaultTableModel model;

    // ===== BOTONES =====
    private JButton btnSave;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnAddClient;
    private JButton btnRemoveClient;

    // ===== GESTIÓN DE CLIENTES =====
    private ArrayList<Client> selectedClients; // múltiples clientes
    private JList<String> listClients;
    private DefaultListModel<String> clientListModel;

    public VehicleViewInternal() {
        super("Gestión de Vehículos", true, true, true, true);

        setSize(900, 620);
        setLayout(null);

        // Fondo general
        getContentPane().setBackground(LIGHT_BG);

        // Inicializar lista de clientes
        selectedClients = new ArrayList<>();
        clientListModel = new DefaultListModel<>();

        initComponents();
        loadTable();

        setVisible(true);
    }

    private void initComponents() {
        // ===== PANEL DE BÚSQUEDA =====
        createSearchPanel();

        // ===== PANEL DE FORMULARIO (incluye botones) =====
        createFormPanel();

        // ===== TABLA =====
        createTable();

        // ===== EVENTOS =====
        setupEvents();
    }

    private void createSearchPanel() {
        // Panel contenedor de búsqueda
        JPanel searchPanel = new JPanel();
        searchPanel.setBounds(30, 20, 820, 60);
        searchPanel.setLayout(null);
        searchPanel.setBackground(WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        add(searchPanel);

        // Título
        JLabel lblSearchTitle = new JLabel("🔍 Búsqueda de Vehículos");
        lblSearchTitle.setBounds(10, 5, 250, 25);
        lblSearchTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSearchTitle.setForeground(DARK_COLOR);
        searchPanel.add(lblSearchTitle);

        // Campo de búsqueda
        JLabel lblSearch = new JLabel("Placa:");
        lblSearch.setBounds(10, 30, 60, 25);
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchPanel.add(lblSearch);

        txtSearch = new JTextField();
        txtSearch.setBounds(70, 30, 200, 25);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchPanel.add(txtSearch);

        // Botón buscar
        btnSearch = new JButton("Buscar");
        btnSearch.setBounds(280, 30, 100, 25);
        styleButton(btnSearch, PRIMARY_COLOR);
        searchPanel.add(btnSearch);

        // Botón limpiar búsqueda
        btnClearSearch = new JButton("Mostrar Todos");
        btnClearSearch.setBounds(390, 30, 130, 25);
        styleButton(btnClearSearch, SECONDARY_COLOR);
        searchPanel.add(btnClearSearch);

        // Label de resultados
        JLabel lblInfo = new JLabel("💡 Busca por placa exacta o usa 'Mostrar Todos'");
        lblInfo.setBounds(530, 30, 280, 25);
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblInfo.setForeground(DARK_COLOR);
        searchPanel.add(lblInfo);
    }

    private void createFormPanel() {
        // Panel contenedor del formulario
        JPanel formPanel = new JPanel();
        formPanel.setBounds(30, 100, 320, 480);
        formPanel.setLayout(null);
        formPanel.setBackground(WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        add(formPanel);

        // Título del formulario
        JLabel lblFormTitle = new JLabel("📝 Datos del Vehículo");
        lblFormTitle.setBounds(10, 10, 280, 25);
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFormTitle.setForeground(DARK_COLOR);
        formPanel.add(lblFormTitle);

        // Campo: Placa
        JLabel lblPlate = new JLabel("Placa:");
        lblPlate.setBounds(10, 50, 80, 25);
        lblPlate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(lblPlate);

        txtPlate = new JTextField();
        txtPlate.setBounds(100, 50, 200, 25);
        txtPlate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(txtPlate);

        // Campo: Marca
        JLabel lblBrand = new JLabel("Marca:");
        lblBrand.setBounds(10, 85, 80, 25);
        lblBrand.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(lblBrand);

        txtBrand = new JTextField();
        txtBrand.setBounds(100, 85, 200, 25);
        txtBrand.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(txtBrand);

        // Campo: Modelo
        JLabel lblModel = new JLabel("Modelo:");
        lblModel.setBounds(10, 120, 80, 25);
        lblModel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(lblModel);

        txtModel = new JTextField();
        txtModel.setBounds(100, 120, 200, 25);
        txtModel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(txtModel);

        // Campo: Color
        JLabel lblColor = new JLabel("Color:");
        lblColor.setBounds(10, 155, 80, 25);
        lblColor.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(lblColor);

        txtColor = new JTextField();
        txtColor.setBounds(100, 155, 200, 25);
        txtColor.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(txtColor);

        // Campo: Tipo
        JLabel lblType = new JLabel("Tipo:");
        lblType.setBounds(10, 190, 80, 25);
        lblType.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(lblType);

        cmbType = new JComboBox<>(new String[]{"Carro", "Moto", "Camión"});
        cmbType.setBounds(100, 190, 200, 25);
        cmbType.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(cmbType);

        // ===== SECCIÓN DE CLIENTES =====
        JLabel lblClientsTitle = new JLabel("👥 Clientes Asignados:");
        lblClientsTitle.setBounds(10, 225, 200, 25);
        lblClientsTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblClientsTitle.setForeground(DARK_COLOR);
        formPanel.add(lblClientsTitle);

        // Lista de clientes
        listClients = new JList<>(clientListModel);
        listClients.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        listClients.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollClients = new JScrollPane(listClients);
        scrollClients.setBounds(10, 250, 290, 60);
        scrollClients.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        formPanel.add(scrollClients);

        // Botones de gestión de clientes
        btnAddClient = new JButton("➕ Agregar Cliente");
        btnAddClient.setBounds(10, 315, 140, 30);
        styleButton(btnAddClient, SUCCESS_COLOR);
        formPanel.add(btnAddClient);

        btnRemoveClient = new JButton("➖ Quitar Cliente");
        btnRemoveClient.setBounds(160, 315, 140, 30);
        styleButton(btnRemoveClient, DANGER_COLOR);
        formPanel.add(btnRemoveClient);

        // Separador visual
        JSeparator separator = new JSeparator();
        separator.setBounds(10, 355, 290, 2);
        separator.setForeground(PRIMARY_COLOR);
        formPanel.add(separator);

        // Título de acciones
        JLabel lblActions = new JLabel("⚡ Acciones");
        lblActions.setBounds(10, 365, 280, 25);
        lblActions.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblActions.setForeground(DARK_COLOR);
        formPanel.add(lblActions);

        // Botón: Guardar
        btnSave = new JButton("💾 Guardar");
        btnSave.setBounds(10, 395, 140, 35);
        styleButton(btnSave, SUCCESS_COLOR);
        formPanel.add(btnSave);

        // Botón: Limpiar
        btnClear = new JButton("🔄 Limpiar");
        btnClear.setBounds(160, 395, 140, 35);
        styleButton(btnClear, SECONDARY_COLOR);
        formPanel.add(btnClear);

        // Botón: Actualizar
        btnUpdate = new JButton("✏️ Actualizar");
        btnUpdate.setBounds(10, 435, 140, 35);
        styleButton(btnUpdate, PRIMARY_COLOR);
        btnUpdate.setEnabled(false);
        formPanel.add(btnUpdate);

        // Botón: Eliminar
        btnDelete = new JButton("🗑️ Eliminar");
        btnDelete.setBounds(160, 435, 140, 35);
        styleButton(btnDelete, DANGER_COLOR);
        btnDelete.setEnabled(false);
        formPanel.add(btnDelete);
    }

    private void createTable() {
        // Modelo de la tabla
        model = new DefaultTableModel(
                new String[]{"Placa", "Marca", "Modelo", "Color", "Tipo"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(WHITE);
        table.setSelectionBackground(SECONDARY_COLOR);
        table.setSelectionForeground(WHITE);
        table.setGridColor(new Color(189, 195, 199));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(370, 100, 480, 480);
        scrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2));
        add(scrollPane);
    }

    private void setupEvents() {
        // Evento: Búsqueda
        btnSearch.addActionListener(e -> searchVehicle());

        // Búsqueda al presionar Enter
        txtSearch.addActionListener(e -> searchVehicle());

        // Evento: Limpiar búsqueda
        btnClearSearch.addActionListener(e -> {
            txtSearch.setText("");
            loadTable();
        });

        // Evento: Agregar Cliente
        btnAddClient.addActionListener(e -> addClient());

        // Evento: Quitar Cliente
        btnRemoveClient.addActionListener(e -> removeClient());

        // Evento: Guardar
        btnSave.addActionListener(e -> saveVehicle());

        // Evento: Actualizar
        btnUpdate.addActionListener(e -> updateVehicle());

        // Evento: Eliminar
        btnDelete.addActionListener(e -> deleteVehicle());

        // Evento: Limpiar formulario
        btnClear.addActionListener(e -> clearForm());

        // Evento: Selección de fila en tabla
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFormFromTable();
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
                txtPlate.setEnabled(false); // placa NO se edita
            }
        });
    }

    // ================= GESTIÓN DE CLIENTES =================
    private void addClient() {
        ClientViewInternal cv = new ClientViewInternal();

        // NO agregar aquí - selectOrCreateClient lo hace si es necesario
        // getDesktopPane().add(cv);  ← ELIMINAR ESTA LÍNEA
        Client c = cv.selectOrCreateClient(getDesktopPane());

        // Si el cliente no existía, esperar a que se cree
        if (c == null) {
            c = cv.getCreatedClient();
        }

        if (c != null) {
            // Verificar que no esté ya en la lista
            boolean exists = false;
            for (Client existing : selectedClients) {
                if (existing.getId().equals(c.getId())) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                selectedClients.add(c);
                clientListModel.addElement(c.getName() + " (ID: " + c.getId() + ")");
                JOptionPane.showMessageDialog(this,
                        "✅ Cliente agregado: " + c.getName(),
                        "Cliente agregado",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "⚠️ Este cliente ya está asignado al vehículo",
                        "Cliente duplicado",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void removeClient() {
        int selectedIndex = listClients.getSelectedIndex();

        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un cliente de la lista para quitar",
                    "Sin selección",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Client removedClient = selectedClients.get(selectedIndex);

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Quitar a " + removedClient.getName() + " de este vehículo?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            selectedClients.remove(selectedIndex);
            clientListModel.remove(selectedIndex);
            JOptionPane.showMessageDialog(this,
                    "Cliente removido de la lista",
                    "Cliente removido",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ================= LÓGICA DE BÚSQUEDA =================
    private void searchVehicle() {
        String plate = txtSearch.getText().trim();

        if (plate.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor ingrese una placa para buscar",
                    "Búsqueda vacía",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Vehicle vehicle = vehicleController.findVehicleByPlate(plate);

        if (vehicle != null) {
            // Limpiar tabla y mostrar solo el vehículo encontrado
            model.setRowCount(0);
            model.addRow(new Object[]{
                vehicle.getPlate(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getColor(),
                vehicle.getVehicleType() != null ? vehicle.getVehicleType().getDescription() : ""
            });

            // Seleccionar la fila automáticamente
            table.setRowSelectionInterval(0, 0);

            JOptionPane.showMessageDialog(this,
                    "✅ Vehículo encontrado: " + vehicle.getPlate(),
                    "Búsqueda exitosa",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            model.setRowCount(0);
            JOptionPane.showMessageDialog(this,
                    "❌ No se encontró ningún vehículo con la placa: " + plate,
                    "Sin resultados",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    // ================= LÓGICA CRUD =================
    private void loadTable() {
        model.setRowCount(0);
        for (Vehicle v : vehicleController.getAllVehicles()) {
            model.addRow(new Object[]{
                v.getPlate(),
                v.getBrand(),
                v.getModel(),
                v.getColor(),
                v.getVehicleType() != null ? v.getVehicleType().getDescription() : ""
            });
        }
    }

    private void saveVehicle() {
        if (!validateForm(true)) {
            return;
        }

        Vehicle v = buildVehicle();
        String result = vehicleController.insertVehicle(v);

        JOptionPane.showMessageDialog(this, result);
        loadTable();
        clearForm();
        txtSearch.setText(""); // Limpiar búsqueda también
    }

    private void updateVehicle() {
        if (!validateForm(false)) {
            return;
        }

        Vehicle v = new Vehicle();
        v.setPlate(txtPlate.getText().trim());
        v.setBrand(txtBrand.getText().trim());
        v.setModel(txtModel.getText().trim());
        v.setColor(txtColor.getText().trim());
        v.setVehicleType(buildVehicleType());

        String result = vehicleController.updateVehicle(v);
        JOptionPane.showMessageDialog(this, result);

        loadTable();
        clearForm();
        txtSearch.setText("");
    }

    private void deleteVehicle() {
        String plate = txtPlate.getText().trim();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar vehículo con placa: " + plate + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(
                    this,
                    vehicleController.deleteVehicle(plate)
            );
            loadTable();
            clearForm();
            txtSearch.setText("");
        }
    }

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row == -1) {
            return;
        }

        String plate = model.getValueAt(row, 0).toString();
        txtPlate.setText(plate);
        txtBrand.setText(model.getValueAt(row, 1).toString());
        txtModel.setText(model.getValueAt(row, 2).toString());
        txtColor.setText(model.getValueAt(row, 3).toString());
        cmbType.setSelectedItem(model.getValueAt(row, 4).toString());

        // Cargar los clientes del vehículo
        Vehicle vehicle = vehicleController.findVehicleByPlate(plate);
        if (vehicle != null && vehicle.getClients() != null) {
            selectedClients.clear();
            clientListModel.clear();

            for (Client client : vehicle.getClients()) {
                selectedClients.add(client);
                clientListModel.addElement(client.getName() + " (ID: " + client.getId() + ")");
            }
        }
    }

    private void clearForm() {
        txtPlate.setText("");
        txtBrand.setText("");
        txtModel.setText("");
        txtColor.setText("");
        cmbType.setSelectedIndex(0);

        // Limpiar lista de clientes
        selectedClients.clear();
        clientListModel.clear();

        txtPlate.setEnabled(true);
        table.clearSelection();

        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    private boolean validateForm(boolean creating) {
        if (txtPlate.getText().trim().isEmpty()
                || txtBrand.getText().trim().isEmpty()
                || txtModel.getText().trim().isEmpty()
                || txtColor.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Todos los campos son obligatorios",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (creating && selectedClients.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Debe asignar al menos un cliente al vehículo",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private Vehicle buildVehicle() {
        Vehicle v = new Vehicle();
        v.setPlate(txtPlate.getText().trim());
        v.setBrand(txtBrand.getText().trim());
        v.setModel(txtModel.getText().trim());
        v.setColor(txtColor.getText().trim());
        v.setVehicleType(buildVehicleType());

        // Agregar todos los clientes seleccionados
        for (Client client : selectedClients) {
            v.addClient(client);
        }

        return v;
    }

    private VehicleType buildVehicleType() {
        VehicleType type = new VehicleType();
        type.setDescription(cmbType.getSelectedItem().toString());
        return type;
    }

    // ================= ESTILIZACIÓN =================
    private void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        // Efecto hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }
}
