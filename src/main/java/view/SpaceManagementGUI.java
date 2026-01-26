package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import Controller.SpaceController;
import Controller.ClientController;  // AÑADIR ESTO
import controller.VehicleController;
import model.entities.Space;
import model.entities.Client;
import model.entities.Vehicle;
import model.entities.VehicleType;

public class SpaceManagementGUI extends JFrame {

    private SpaceController spaceController;
    private VehicleController vehicleController;  // AÑADIR ESTO
    private ClientController clientController;    // AÑADIR ESTO
    private JTable spacesTable;
    private DefaultTableModel tableModel;
    private JButton occupyBtn, releaseBtn, refreshBtn, calculateFeeBtn;
    private JComboBox<String> parkingLotCombo;
    private ArrayList<model.entities.ParkingLot> parkingLots;

    public SpaceManagementGUI() {
        spaceController = new SpaceController();
        vehicleController = new VehicleController();  // Inicializar
        clientController = new ClientController();    // Inicializar
        initializeUI();
        loadParkingLots();
        loadSpacesData();
    }

    private void initializeUI() {
        setTitle("Gestión de Espacios de Parqueo");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 650);
        setLayout(new BorderLayout());

        // Panel superior con filtros
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel parkingLabel = new JLabel("Seleccionar Parqueo:");
        parkingLotCombo = new JComboBox<>();
        parkingLotCombo.addActionListener(e -> loadSpacesData());

        refreshBtn = new JButton("Actualizar");
        occupyBtn = new JButton("Ocupar Espacio");
        releaseBtn = new JButton("Liberar Espacio");
        calculateFeeBtn = new JButton("Calcular Tarifa");

        refreshBtn.addActionListener(e -> {
            loadParkingLots();
            loadSpacesData();
        });
        occupyBtn.addActionListener(e -> showOccupySpaceDialog());
        releaseBtn.addActionListener(e -> releaseSelectedSpace());
        calculateFeeBtn.addActionListener(e -> calculateFeeForSelectedSpace());

        topPanel.add(parkingLabel);
        topPanel.add(parkingLotCombo);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(refreshBtn);
        topPanel.add(occupyBtn);
        topPanel.add(releaseBtn);
        topPanel.add(calculateFeeBtn);
        add(topPanel, BorderLayout.NORTH);

        // Tabla de espacios
        String[] columnNames = {"ID", "Estado", "Tipo", "Accesibilidad",
            "Cliente", "Vehículo", "Hora Entrada", "Parqueo"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        spacesTable = new JTable(tableModel);
        spacesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        spacesTable.setRowHeight(25);

        // Personalizar ancho de columnas
        spacesTable.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        spacesTable.getColumnModel().getColumn(1).setPreferredWidth(80);   // Estado
        spacesTable.getColumnModel().getColumn(2).setPreferredWidth(100);  // Tipo
        spacesTable.getColumnModel().getColumn(3).setPreferredWidth(100);  // Accesibilidad
        spacesTable.getColumnModel().getColumn(4).setPreferredWidth(120);  // Cliente
        spacesTable.getColumnModel().getColumn(5).setPreferredWidth(100);  // Vehículo
        spacesTable.getColumnModel().getColumn(6).setPreferredWidth(150);  // Hora Entrada
        spacesTable.getColumnModel().getColumn(7).setPreferredWidth(120);  // Parqueo

        JScrollPane scrollPane = new JScrollPane(spacesTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Espacios Disponibles"));
        add(scrollPane, BorderLayout.CENTER);

        // Panel de información
        JPanel infoPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Estadísticas"));
        infoPanel.setBackground(new Color(240, 240, 240));

        JLabel totalLabel = new JLabel("Total: 0", SwingConstants.CENTER);
        JLabel occupiedLabel = new JLabel("Ocupados: 0", SwingConstants.CENTER);
        JLabel freeLabel = new JLabel("Disponibles: 0", SwingConstants.CENTER);
        JLabel accessibleLabel = new JLabel("Accesibles: 0", SwingConstants.CENTER);

        totalLabel.setFont(new Font("Arial", Font.BOLD, 12));
        occupiedLabel.setFont(new Font("Arial", Font.BOLD, 12));
        freeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        accessibleLabel.setFont(new Font("Arial", Font.BOLD, 12));

        infoPanel.add(totalLabel);
        infoPanel.add(occupiedLabel);
        infoPanel.add(freeLabel);
        infoPanel.add(accessibleLabel);
        add(infoPanel, BorderLayout.SOUTH);
    }

    private void loadParkingLots() {
        // Necesitarías un controlador o método para obtener los parqueos
        parkingLotCombo.removeAllItems();
        parkingLotCombo.addItem("Todos los parqueos");

        // Aquí deberías obtener la lista de parqueos
        // Ejemplo: parkingLots = parkingLotController.getAllParkingLots();
        // Por ahora, usaré un ejemplo
        try {
            Controller.ParkingLotController plc = new Controller.ParkingLotController();
            parkingLots = plc.getAllParkingLots();
            for (model.entities.ParkingLot pl : parkingLots) {
                parkingLotCombo.addItem(pl.getName() + " (ID: " + pl.getId() + ")");
            }
        } catch (Exception e) {
            System.out.println("Error cargando parqueos: " + e.getMessage());
        }
    }

    private void loadSpacesData() {
        tableModel.setRowCount(0);
        ArrayList<Space> allSpaces = spaceController.getAllSpaces();

        int total = 0;
        int occupied = 0;
        int free = 0;
        int accessible = 0;

        String selectedParkingLot = (String) parkingLotCombo.getSelectedItem();

        for (Space space : allSpaces) {
            // Filtrar por parqueo si se seleccionó uno específico
            if (selectedParkingLot != null && !selectedParkingLot.equals("Todos los parqueos")) {
                // Aquí necesitarías obtener el parqueo al que pertenece el espacio
                // Por ahora, mostraré todos
            }

            String status = space.isSpaceTaken() ? "🔴 OCUPADO" : "🟢 LIBRE";
            String type = space.getVehicleType() != null
                    ? space.getVehicleType().getDescription() : "N/A";
            String accessibility = space.isDisabilityAdaptation() ? "♿ SÍ" : "NO";
            String client = space.getClient() != null
                    ? space.getClient().getName() : "";
            String vehicle = space.getVehicle() != null
                    ? space.getVehicle().getPlate() : "";
            String entryTime = space.getEntryTime() != null
                    ? space.getEntryTime().toString() : "";
            String parkingLotName = "Parqueo #" + space.getId() / 100; // Ejemplo

            // Solo para estadísticas
            total++;
            if (space.isSpaceTaken()) {
                occupied++;
            } else {
                free++;
            }
            if (space.isDisabilityAdaptation()) {
                accessible++;
            }

            tableModel.addRow(new Object[]{
                space.getId(), status, type, accessibility,
                client, vehicle, entryTime, parkingLotName
            });
        }

        updateStatistics(total, occupied, free, accessible);
    }

    private void updateStatistics(int total, int occupied, int free, int accessible) {
        JPanel infoPanel = (JPanel) ((BorderLayout) getContentPane().getLayout()).getLayoutComponent(BorderLayout.SOUTH);
        Component[] comps = infoPanel.getComponents();

        ((JLabel) comps[0]).setText("Total: " + total);
        ((JLabel) comps[1]).setText("Ocupados: " + occupied);
        ((JLabel) comps[2]).setText("Disponibles: " + free);
        ((JLabel) comps[3]).setText("Accesibles: " + accessible);

        // Cambiar colores según disponibilidad
        if (free == 0) {
            ((JLabel) comps[2]).setForeground(Color.RED);
        } else if (free <= total * 0.1) {
            ((JLabel) comps[2]).setForeground(Color.ORANGE);
        } else {
            ((JLabel) comps[2]).setForeground(new Color(0, 150, 0));
        }
    }

    private void showOccupySpaceDialog() {
        JDialog dialog = new JDialog(this, "Ocupar Espacio", true);
        dialog.setSize(500, 600);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Componentes del formulario
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("ID del Espacio:"), gbc);

        gbc.gridx = 1;
        JTextField spaceIdField = new JTextField(15);
        formPanel.add(spaceIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("ID del Cliente:"), gbc);

        gbc.gridx = 1;
        JTextField clientIdField = new JTextField(15);
        formPanel.add(clientIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Nombre del Cliente:"), gbc);

        gbc.gridx = 1;
        JTextField clientNameField = new JTextField(15);
        formPanel.add(clientNameField, gbc);

        // Botón para buscar cliente existente
        gbc.gridx = 2;
        JButton searchClientBtn = new JButton("Buscar");
        searchClientBtn.addActionListener(e -> {
            String id = clientIdField.getText().trim();
            if (!id.isEmpty()) {
                Client existingClient = clientController.findClientById(id);
                if (existingClient != null) {
                    clientNameField.setText(existingClient.getName());
                    JOptionPane.showMessageDialog(dialog,
                            "Cliente encontrado: " + existingClient.getName(),
                            "Cliente Encontrado", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Cliente no encontrado. Puede registrar uno nuevo.",
                            "Cliente No Encontrado", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        formPanel.add(searchClientBtn, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Teléfono:"), gbc);

        gbc.gridx = 1;
        JTextField clientPhoneField = new JTextField(15);
        formPanel.add(clientPhoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Persona con Discapacidad:"), gbc);

        gbc.gridx = 1;
        JCheckBox preferentialCheck = new JCheckBox();
        formPanel.add(preferentialCheck, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Placa del Vehículo:"), gbc);

        gbc.gridx = 1;
        JTextField plateField = new JTextField(15);
        formPanel.add(plateField, gbc);

        // Botón para buscar vehículo existente
        gbc.gridx = 2;
        JButton searchVehicleBtn = new JButton("Buscar");
        searchVehicleBtn.addActionListener(e -> {
            String plate = plateField.getText().trim();
            if (!plate.isEmpty()) {
                Vehicle existingVehicle = vehicleController.findVehicleByPlate(plate);
                if (existingVehicle != null) {
                    JOptionPane.showMessageDialog(dialog,
                            "Vehículo encontrado: " + existingVehicle.getBrand()
                            + " " + existingVehicle.getModel(),
                            "Vehículo Encontrado", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Vehículo no encontrado. Debe registrar uno nuevo.",
                            "Vehículo No Encontrado", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        formPanel.add(searchVehicleBtn, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Marca:"), gbc);

        gbc.gridx = 1;
        JTextField brandField = new JTextField(15);
        formPanel.add(brandField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(new JLabel("Modelo:"), gbc);

        gbc.gridx = 1;
        JTextField modelField = new JTextField(15);
        formPanel.add(modelField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        formPanel.add(new JLabel("Tipo de Vehículo:"), gbc);

        gbc.gridx = 1;
        String[] vehicleTypes = {"Automóvil", "Motocicleta", "Camión"};
        JComboBox<String> typeCombo = new JComboBox<>(vehicleTypes);
        formPanel.add(typeCombo, gbc);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton saveBtn = new JButton("Guardar");
        JButton cancelBtn = new JButton("Cancelar");

        saveBtn.addActionListener(e -> {
            try {
                // Validar campos
                if (spaceIdField.getText().trim().isEmpty()
                        || clientIdField.getText().trim().isEmpty()
                        || clientNameField.getText().trim().isEmpty()
                        || plateField.getText().trim().isEmpty()) {

                    JOptionPane.showMessageDialog(dialog,
                            "Por favor complete todos los campos requeridos",
                            "Validación", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int spaceId = Integer.parseInt(spaceIdField.getText().trim());

                // Crear cliente
                Client client = new Client(
                        clientIdField.getText().trim(),
                        clientNameField.getText().trim(),
                        clientPhoneField.getText().trim(),
                        preferentialCheck.isSelected()
                );

                // Crear tipo de vehículo basado en selección
                int typeId = typeCombo.getSelectedIndex() + 1;
                float fee = 0.0f;
                String description = (String) typeCombo.getSelectedItem();

                // Asignar tarifas según tipo
                switch (typeId) {
                    case 1: // Automóvil
                        fee = 5.0f;
                        break;
                    case 2: // Motocicleta
                        fee = 2.5f;
                        break;
                    case 3: // Camión
                        fee = 6.5f;
                        break;
                }

                VehicleType vehicleType = new VehicleType(
                        typeId,
                        description,
                        typeId == 2 ? 2 : 4, // Motos tienen 2 llantas, otros 4
                        fee
                );

                // Crear vehículo
                Vehicle vehicle = new Vehicle(
                        plateField.getText().trim(),
                        "N/A", // color por defecto
                        brandField.getText().trim(),
                        modelField.getText().trim(),
                        client,
                        vehicleType
                );

                // Ocupar espacio
                String result = spaceController.occupySpace(spaceId, client, vehicle);

                if (result.equals("OK")) {
                    JOptionPane.showMessageDialog(dialog,
                            "✅ Espacio ocupado exitosamente!\n"
                            + "Espacio: #" + spaceId + "\n"
                            + "Cliente: " + client.getName() + "\n"
                            + "Vehículo: " + vehicle.getPlate(),
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);

                    dialog.dispose();
                    loadSpacesData(); // Refrescar tabla

                    // También registrar en el JSON de vehículos
                    vehicleController.insertVehicle(vehicle);

                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "❌ Error: " + result,
                            "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "ID de espacio debe ser un número válido",
                        "Error de Formato", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error inesperado: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void releaseSelectedSpace() {
        int selectedRow = spacesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione un espacio de la tabla",
                    "Selección Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int spaceId = (int) tableModel.getValueAt(selectedRow, 0);
        String clientName = (String) tableModel.getValueAt(selectedRow, 4);
        String vehiclePlate = (String) tableModel.getValueAt(selectedRow, 5);

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de liberar este espacio?\n\n"
                + "Espacio: #" + spaceId + "\n"
                + "Cliente: " + (clientName.isEmpty() ? "N/A" : clientName) + "\n"
                + "Vehículo: " + (vehiclePlate.isEmpty() ? "N/A" : vehiclePlate),
                "Confirmar Liberación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean released = spaceController.releaseSpace(spaceId);
            if (released) {
                JOptionPane.showMessageDialog(this,
                        "✅ Espacio liberado exitosamente",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                loadSpacesData();
            } else {
                JOptionPane.showMessageDialog(this,
                        "❌ Error al liberar el espacio",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void calculateFeeForSelectedSpace() {
        int selectedRow = spacesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione un espacio de la tabla",
                    "Selección Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int spaceId = (int) tableModel.getValueAt(selectedRow, 0);
        Space space = spaceController.findSpaceById(spaceId);

        if (space == null || !space.isSpaceTaken()) {
            JOptionPane.showMessageDialog(this,
                    "Este espacio no está ocupado",
                    "Espacio Libre", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        double fee = spaceController.calculateFee(space);
        String message = String.format(
                "📋 TICKET DE SALIDA\n\n"
                + "Espacio: #%d\n"
                + "Cliente: %s\n"
                + "Vehículo: %s\n"
                + "Tipo: %s\n"
                + "Hora Entrada: %s\n\n"
                + "💰 TOTAL A PAGAR: $%.2f\n\n"
                + "¿Desea generar el ticket de salida?",
                spaceId,
                space.getClient() != null ? space.getClient().getName() : "N/A",
                space.getVehicle() != null ? space.getVehicle().getPlate() : "N/A",
                space.getVehicle() != null && space.getVehicle().getVehicleType() != null
                ? space.getVehicle().getVehicleType().getDescription() : "N/A",
                space.getEntryTime() != null ? space.getEntryTime().toString() : "N/A",
                fee
        );

        int option = JOptionPane.showConfirmDialog(this,
                message,
                "Cálculo de Tarifa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (option == JOptionPane.YES_OPTION) {
            // Aquí podrías llamar a un método para generar ticket de salida
            boolean released = spaceController.releaseSpace(spaceId);
            if (released) {
                JOptionPane.showMessageDialog(this,
                        "✅ Ticket generado y espacio liberado",
                        "Proceso Completado", JOptionPane.INFORMATION_MESSAGE);
                loadSpacesData();
            }
        }
    }

  
}
