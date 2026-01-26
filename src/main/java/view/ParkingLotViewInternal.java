/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import Controller.ParkingLotController;
import java.awt.Color;
import static java.lang.Boolean.parseBoolean;
import java.util.ArrayList;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import model.data.ParkingLotData;
import model.entities.ParkingLot;
import model.entities.Space;
import model.entities.Vehicle;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import model.entities.VehicleType;

/**
 *
 * @author Camila
 */
public class ParkingLotViewInternal extends JInternalFrame {

    private final ParkingLotController parkingLotController = new ParkingLotController();
    private JTextField txtId;
    private JTextField txtName;
    private JTextField txtNumberOfSpaces;
    private JTable table;
    private DefaultTableModel model;

    // Instancia de ParkingLotData para manejar datos persistentes
    private ParkingLotData parkingLotData;

    // Tipos de vehículo predefinidos -  Carro, Camión o Moto
    private VehicleType carType;
    private VehicleType motorcycleType;
    private VehicleType truckType;

    // Referencias a botones para poder habilitar/deshabilitarlos
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnViewDetails;
    private JButton changeFees;
    
    //Panel de tarifas
    private JPanel feePanel;
    private JLabel lblCarFee;
    private JLabel lblBikeFee;
    private JLabel lblDisabledFee;

    public ParkingLotViewInternal() {
        super("Gestión de Parqueos", true, true, true, true);

        //Inicializar VehiclesType
        initializeVehicleTypes();

        // Inicializar ParkingLotData
        parkingLotData = new ParkingLotData();

        setSize(800, 600);
        setLayout(null);
        setVisible(true);

        // Columna 1: Campos de entrada
        int x = 30;
        int y = 20;
        int labelWidth = 150;
        int fieldWidth = 200;
        int verticalSpacing = 35;

        // ID (Generado automáticamente)
        JLabel lblId = new JLabel("ID Parqueo:");
        lblId.setBounds(x, y, labelWidth, 25);
        lblId.setForeground(new Color(0, 102, 204));
        add(lblId);

        txtId = new JTextField();
        txtId.setBounds(x + labelWidth + 10, y, fieldWidth, 25);
        txtId.setEditable(false);
        txtId.setBackground(new Color(240, 240, 240));
        txtId.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        add(txtId);
        y += verticalSpacing;

        // Nombre del Parqueo
        JLabel lblName = new JLabel("Nombre del Parqueo:");
        lblName.setBounds(x, y, labelWidth, 25);
        add(lblName);

        txtName = new JTextField();
        txtName.setBounds(x + labelWidth + 10, y, fieldWidth, 25);
        txtName.setToolTipText("Ingrese el nombre del parqueo (ej: Parqueo Central)");
        txtName.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        add(txtName);
        y += verticalSpacing;

        // Número de Espacios
        JLabel lblNumberOfSpaces = new JLabel("Número de Espacios:");
        lblNumberOfSpaces.setBounds(x, y, labelWidth, 25);
        add(lblNumberOfSpaces);

        txtNumberOfSpaces = new JTextField();
        txtNumberOfSpaces.setBounds(x + labelWidth + 10, y, fieldWidth, 25);
        txtNumberOfSpaces.setToolTipText("Ingrese el número total de espacios disponibles (1-1000)");
        txtNumberOfSpaces.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        add(txtNumberOfSpaces);
        y += verticalSpacing + 10;

        // Panel de información de tarifas
        feePanel = new JPanel();
        feePanel.setLayout(new GridLayout(3, 1, 5, 5));
        feePanel.setBounds(x, y, 350, 80);
        feePanel.setBorder(BorderFactory.createTitledBorder("Tarifas por Hora"));
        feePanel.setBackground(new Color(245, 245, 245));

        lblCarFee = new JLabel(" Automóviles: $" + String.format("%.2f", carType.getFee()));
        lblCarFee.setForeground(new Color(0, 100, 0));
        feePanel.add(lblCarFee);

        lblBikeFee = new JLabel(" Motocicletas: $" + String.format("%.2f", motorcycleType.getFee()));
        lblBikeFee.setForeground(new Color(0, 100, 0));
        feePanel.add(lblBikeFee);

        lblDisabledFee = new JLabel(" Camiones: $" + String.format("%.2f", truckType.getFee()));
        lblDisabledFee.setForeground(new Color(0, 100, 0));
        feePanel.add(lblDisabledFee);

        add(feePanel);
        y += 90;

        // Botones
        JButton btnSave = new JButton("Guardar Nuevo");
        btnSave.setBounds(x, y, 140, 35);
        btnSave.setBackground(new Color(46, 125, 50));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Arial", Font.BOLD, 12));
        btnSave.setToolTipText("Crear un nuevo parqueo");
        add(btnSave);

        JButton btnClear = new JButton("Limpiar");
        btnClear.setBounds(x + 150, y, 90, 35);
        btnClear.setBackground(new Color(120, 144, 156));
        btnClear.setForeground(Color.WHITE);
        btnClear.setToolTipText("Limpiar formulario");
        add(btnClear);
        y += 45;

        btnUpdate = new JButton("Actualizar");
        btnUpdate.setBounds(x, y, 140, 35);
        btnUpdate.setBackground(new Color(21, 101, 192));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFont(new Font("Arial", Font.BOLD, 12));
        btnUpdate.setEnabled(false);
        btnUpdate.setToolTipText("Actualizar parqueo seleccionado");
        add(btnUpdate);

        btnDelete = new JButton("Eliminar");
        btnDelete.setBounds(x + 150, y, 90, 35);
        btnDelete.setBackground(new Color(198, 40, 40));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setEnabled(false);
        btnDelete.setToolTipText("Eliminar parqueo seleccionado");
        add(btnDelete);
        y += 45;

        btnViewDetails = new JButton("Ver Detalles");
        btnViewDetails.setBounds(x, y, 240, 35);
        btnViewDetails.setBackground(new Color(123, 31, 162));
        btnViewDetails.setForeground(Color.WHITE);
        btnViewDetails.setEnabled(false);
        btnViewDetails.addActionListener(e -> viewParkingLotDetails());
        add(btnViewDetails);

        /* Botón para configurar espacios especiales
        JButton btnConfigure = new JButton("Configurar Espacios");
        btnConfigure.setBounds(x, y + 45, 240, 30);
        btnConfigure.setBackground(new Color(245, 124, 0));
        btnConfigure.setForeground(Color.WHITE);
        btnConfigure.addActionListener(e -> showConfigurationDialog());
        btnConfigure.setToolTipText("Configurar distribución de espacios especiales");
        add(btnConfigure);*/
        // Botón para configurar tarifas
        changeFees = new JButton("Cambiar Tarifas");
        changeFees.setBounds(x, y + 45, 240, 30);
        changeFees.setBackground(new Color(245, 124, 0));
        changeFees.setForeground(Color.WHITE);
        changeFees.addActionListener(e -> feesVehicleTypes());
        changeFees.setToolTipText("Configurar tarifas por tipo de Vehículo");
        add(changeFees);

        // Tabla
        model = new DefaultTableModel(new String[]{
            "ID", "Nombre", "Espacios", "Ocupados", "Disponibles", "Estado"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(66, 66, 66));
        table.getTableHeader().setForeground(Color.WHITE);

        // Personalizar ancho de columnas
        table.getColumnModel().getColumn(0).setPreferredWidth(60);   // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(150);  // Nombre
        table.getColumnModel().getColumn(2).setPreferredWidth(80);   // Espacios
        table.getColumnModel().getColumn(3).setPreferredWidth(80);   // Ocupados
        table.getColumnModel().getColumn(4).setPreferredWidth(90);   // Disponibles
        table.getColumnModel().getColumn(5).setPreferredWidth(100);  // Estado

        // Aplicar renderizador personalizado a la columna de estado
        table.getColumnModel().getColumn(5).setCellRenderer(new StatusCellRenderer());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(400, 20, 370, 500);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        add(scrollPane);

        // Cargar datos iniciales
        loadTable();

        // Generar ID para nuevo registro
        generateNextId();

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
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
                btnViewDetails.setEnabled(true);
            }
        });
    }

    /**
     * Inicializa los tipos de vehículo
     */
    private void initializeVehicleTypes() {
        carType = new VehicleType(1, "Automóvil", 4, 5.0f);
        motorcycleType = new VehicleType(2, "Motocicleta", 2, 2.5f);
        truckType = new VehicleType(3, "Camión", 4, 6.5f);
    }

    /**
     * Settear fees de los tipos de vehículo
     */
    private void feesVehicleTypes() {
        int option;
        float carFee, motoFee, truckFee;

        option = Integer.parseInt(JOptionPane.showInputDialog("Selecciona el tipo de vehículo a modificar: \n"
                + "1. Carros\n"
                + "2. Motocicletas\n"
                + "3. Camiones\n"));

        switch (option) {
            case 1:
                carFee = Float.parseFloat(JOptionPane.showInputDialog("Nueva tarifa para carros: "));
                carType.setFee(carFee);
                updateFeeLabels();
                break;
            case 2:
                motoFee = Float.parseFloat(JOptionPane.showInputDialog("Nueva tarifa para motocicletas: "));
                motorcycleType.setFee(motoFee);
                updateFeeLabels();
                break;
            case 3:
                truckFee = Float.parseFloat(JOptionPane.showInputDialog("Nueva tarifa para camiones: "));
                truckType.setFee(truckFee);
                updateFeeLabels();
                break;

            default:
                JOptionPane.showInputDialog("Selecciona una opción válida.");

        }

    }

    // Método para actualizar las etiquetas
    public void updateFeeLabels() {
        lblCarFee.setText(" Automóviles: $" + String.format("%.2f", carType.getFee()));
        lblBikeFee.setText(" Motocicletas: $" + String.format("%.2f", motorcycleType.getFee()));
        lblDisabledFee.setText(" Camiones: $" + String.format("%.2f", truckType.getFee()));

        // Forzar revalidación y repintado
        feePanel.revalidate();
        feePanel.repaint();
    }

    /**
     * Genera el próximo ID automáticamente
     */
    private void generateNextId() {
        try {
            int nextIdNumber = parkingLotData.findLastIdNumberOfParkingLot() + 1;
            txtId.setText(String.valueOf(nextIdNumber));
        } catch (Exception e) {
            txtId.setText("1");
            System.err.println("Error generando ID: " + e.getMessage());
        }
    }

    /**
     * Carga los datos en la tabla
     */
    private void loadTable() {
        model.setRowCount(0);
        try {
            ArrayList<ParkingLot> parkingLots = parkingLotData.getAllParkingLots();

            for (ParkingLot parkingLot : parkingLots) {
                int espaciosTotales = parkingLot.getNumberOfSpaces();
                int espaciosOcupados = (parkingLot.getVehicles() != null) ? parkingLot.getVehicles().size() : 0;
                int espaciosDisponibles = espaciosTotales - espaciosOcupados;

                // Determinar estado
                String estado = calculateStatus(espaciosTotales, espaciosOcupados, espaciosDisponibles);

                model.addRow(new Object[]{
                    String.valueOf(parkingLot.getId()),
                    parkingLot.getName(),
                    String.valueOf(espaciosTotales),
                    String.valueOf(espaciosOcupados),
                    String.valueOf(espaciosDisponibles),
                    estado
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error cargando datos: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Calcula el estado del parqueo
     */
    private String calculateStatus(int total, int ocupados, int disponibles) {
        if (disponibles == 0) {
            return "LLENO";
        } else if (disponibles <= total * 0.1) { // Menos del 10% disponibles
            return "CASI LLENO";
        } else if (ocupados == 0) {
            return "VACÍO";
        } else if (disponibles <= total * 0.3) { // Menos del 30% disponibles
            return "OCUPADO";
        } else {
            return "DISPONIBLE";
        }
    }

    /**
     * Guarda un nuevo parqueo
     */
    private void saveParkingLot() {
        try {
            // Validar campos requeridos
            if (!validateRequiredFields()) {
                return;
            }

            // Verificar si el ID ya existe
            int id = Integer.parseInt(txtId.getText().trim());
            if (parkingLotData.findParkingLotById(id) != null) {
                JOptionPane.showMessageDialog(this,
                        "Error: Ya existe un parqueo con el ID: " + id + "\n"
                        + "Presione 'Limpiar' para generar un nuevo ID automático.",
                        "ID Duplicado",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Verificar si el nombre ya existe
            String name = txtName.getText().trim();
            if (parkingLotData.findParkingLotByName(name) != null) {
                JOptionPane.showMessageDialog(this,
                        "Error: Ya existe un parqueo con el nombre: '" + name + "'",
                        "Nombre Duplicado",
                        JOptionPane.ERROR_MESSAGE);
                txtName.requestFocus();
                return;
            }

            // Pedir configuración de distribución de espacios
            int numberOfSpaces = Integer.parseInt(txtNumberOfSpaces.getText().trim());
            SpaceConfiguration config = askForSpaceConfiguration(numberOfSpaces);
            if (config == null) {
                return; // Usuario canceló
            }

            // Crear objeto ParkingLot
            ParkingLot parkingLot = createParkingLotFromForm(config);

            // Guardar en ParkingLotData
            ParkingLot success = parkingLotData.addParkingLot(parkingLot);

            if (success != null) {
                JOptionPane.showMessageDialog(this,
                        "Parqueo registrado exitosamente\n\n"
                        + "ID: " + parkingLot.getId() + "\n"
                        + "Nombre: " + parkingLot.getName() + "\n"
                        + "Espacios totales: " + parkingLot.getNumberOfSpaces() + "\n"
                        + "• Carro liviano: " + config.getStandardSpaces() + "\n"
                        + "• Motocicletas: " + config.getMotorcycleSpaces() + "\n"
                        + "• Camiones: " + config.getDisabledSpaces(),
                        "Registro Exitoso",
                        JOptionPane.INFORMATION_MESSAGE);

                loadTable();
                clearForm();
                generateNextId();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error al guardar el parqueo",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Número de espacios debe ser un número válido",
                    "Error de Formato",
                    JOptionPane.ERROR_MESSAGE);
            txtNumberOfSpaces.requestFocus();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "❌ Error inesperado: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Actualiza un parqueo existente
     */
    private void updateParkingLot() {
        try {
            int id = Integer.parseInt(txtId.getText().trim());

            // Verificar que el parqueo existe
            ParkingLot existingParkingLot = parkingLotData.findParkingLotById(id);
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

            // Verificar si el nuevo nombre ya existe (excluyendo el actual)
            String newName = txtName.getText().trim();
            ParkingLot parkingLotWithSameName = parkingLotData.findParkingLotByName(newName);
            if (parkingLotWithSameName != null && parkingLotWithSameName.getId() != id) {
                JOptionPane.showMessageDialog(this,
                        "Error: Ya existe otro parqueo con el nombre: '" + newName + "'",
                        "Nombre Duplicado",
                        JOptionPane.ERROR_MESSAGE);
                txtName.requestFocus();
                return;
            }

            // Verificar que no se reduzcan espacios por debajo de vehículos ocupados
            int newNumberOfSpaces = Integer.parseInt(txtNumberOfSpaces.getText().trim());
            int currentVehicles = (existingParkingLot.getVehicles() != null)
                    ? existingParkingLot.getVehicles().size() : 0;

            if (newNumberOfSpaces < currentVehicles) {
                JOptionPane.showMessageDialog(this,
                        "❌ No se pueden reducir los espacios a " + newNumberOfSpaces
                        + "\nActualmente hay " + currentVehicles + " vehículo(s) estacionado(s)."
                        + "\n\nMínimo requerido: " + currentVehicles + " espacios.",
                        "Espacios Insuficientes",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Confirmar actualización del parqueo?\n\n"
                    + "ID: " + id + "\n"
                    + "Nombre: " + newName + "\n"
                    + "Espacios: " + newNumberOfSpaces + "\n\n"
                    + "Esta acción actualizará la configuración de espacios.",
                    "Confirmar Actualización",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                // Pedir nueva configuración si cambia el número de espacios
                SpaceConfiguration config = null;
                if (newNumberOfSpaces != existingParkingLot.getNumberOfSpaces()) {
                    config = askForSpaceConfiguration(newNumberOfSpaces);
                    if (config == null) {
                        return;
                    }
                }

                ParkingLot updatedParkingLot = updateExistingParkingLot(existingParkingLot, config);
                boolean success = parkingLotData.updateParkingLot(updatedParkingLot);

                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Parqueo actualizado exitosamente",
                            "Actualización Exitosa",
                            JOptionPane.INFORMATION_MESSAGE);

                    loadTable();
                    clearForm();
                    generateNextId();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Error al actualizar el parqueo",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Número de espacios debe ser un número válido",
                    "Error de Formato",
                    JOptionPane.ERROR_MESSAGE);
            txtNumberOfSpaces.requestFocus();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error inesperado: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Elimina un parqueo
     */
    private void deleteParkingLot() {
        try {
            int id = Integer.parseInt(txtId.getText().trim());

            // Obtener parqueo para mostrar información
            ParkingLot parkingLot = parkingLotData.findParkingLotById(id);
            if (parkingLot == null) {
                JOptionPane.showMessageDialog(this,
                        "No existe un parqueo con el ID: " + id,
                        "Parqueo No Encontrado",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Verificar si hay vehículos estacionados
            int vehiclesCount = (parkingLot.getVehicles() != null) ? parkingLot.getVehicles().size() : 0;

            String warningMessage = " ¿ELIMINAR PARQUEO?\n\n"
                    + "ID: " + id + "\n"
                    + "Nombre: " + parkingLot.getName() + "\n"
                    + "Espacios: " + parkingLot.getNumberOfSpaces();

            if (vehiclesCount > 0) {
                warningMessage += "\n\n️ ¡ADVERTENCIA CRÍTICA!\n"
                        + "Hay " + vehiclesCount + " vehículo(s) estacionado(s).\n"
                        + "La eliminación también eliminará estos registros.";
            }

            warningMessage += "\n\nEsta acción NO se puede deshacer.";

            int confirm = JOptionPane.showConfirmDialog(this,
                    warningMessage,
                    "Confirmar Eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = parkingLotData.deleteParkingLot(parkingLot);

                if (success) {
                    JOptionPane.showMessageDialog(this,
                            " Parqueo eliminado exitosamente",
                            "Eliminación Exitosa",
                            JOptionPane.INFORMATION_MESSAGE);

                    loadTable();
                    clearForm();
                    generateNextId();
                } else {
                    JOptionPane.showMessageDialog(this,
                            " Error al eliminar el parqueo",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    " ID inválido",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Pide configuración de distribución de espacios
     */
    private SpaceConfiguration askForSpaceConfiguration(int totalSpaces) {
        // Panel personalizado para configuración
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTotal = new JLabel("Espacios totales: " + totalSpaces);
        lblTotal.setFont(new Font("Arial", Font.BOLD, 12));
        lblTotal.setForeground(new Color(0, 102, 204));

        JSpinner disabledSpinner = new JSpinner(new SpinnerNumberModel(
                Math.max(1, (int) (totalSpaces * 0.1)), 0, totalSpaces, 1));

        JSpinner motorcycleSpinner = new JSpinner(new SpinnerNumberModel(
                Math.max(1, (int) (totalSpaces * 0.15)), 0, totalSpaces, 1));

        JLabel lblStandard = new JLabel("Espacios carro estándar: ");
        lblStandard.setFont(new Font("Arial", Font.PLAIN, 11));

        // Calcular espacios estándar automáticamente
        JLabel lblStandardValue = new JLabel();
        lblStandardValue.setFont(new Font("Arial", Font.BOLD, 11));

        // Actualizar valor cuando cambien los spinners
        disabledSpinner.addChangeListener(e -> {
            int disabled = (int) disabledSpinner.getValue();
            int motorcycle = (int) motorcycleSpinner.getValue();
            int standard = totalSpaces - disabled - motorcycle;
            lblStandardValue.setText(String.valueOf(standard));
            lblStandardValue.setForeground(standard >= 0 ? Color.BLACK : Color.RED);
        });

        motorcycleSpinner.addChangeListener(e -> {
            int disabled = (int) disabledSpinner.getValue();
            int motorcycle = (int) motorcycleSpinner.getValue();
            int standard = totalSpaces - disabled - motorcycle;
            lblStandardValue.setText(String.valueOf(standard));
            lblStandardValue.setForeground(standard >= 0 ? Color.BLACK : Color.RED);
        });

        // Inicializar valor
        int initialDisabled = (int) disabledSpinner.getValue();
        int initialMotorcycle = (int) motorcycleSpinner.getValue();
        int initialStandard = totalSpaces - initialDisabled - initialMotorcycle;
        lblStandardValue.setText(String.valueOf(initialStandard));

        panel.add(lblTotal);
        panel.add(new JLabel("")); // Espacio vacío
        panel.add(new JLabel("Espacios para camiones:"));
        panel.add(disabledSpinner);
        panel.add(new JLabel("Espacios para motocicletas:"));
        panel.add(motorcycleSpinner);
        panel.add(lblStandard);
        panel.add(lblStandardValue);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Configurar Distribución de Espacios",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            int disabledSpaces = (int) disabledSpinner.getValue();
            int motorcycleSpaces = (int) motorcycleSpinner.getValue();
            int standardSpaces = totalSpaces - disabledSpaces - motorcycleSpaces;

            // Validar que no exceda el total
            if (standardSpaces < 0) {
                JOptionPane.showMessageDialog(this,
                        " Error: La suma de espacios especiales ("
                        + (disabledSpaces + motorcycleSpaces)
                        + ") excede el total (" + totalSpaces + ")",
                        "Error de Configuración",
                        JOptionPane.ERROR_MESSAGE);
                return null;
            }

            return new SpaceConfiguration(disabledSpaces, motorcycleSpaces, standardSpaces);
        }

        return null;
    }

    /**
     * Crea un nuevo parqueo con la configuración especificada
     */
    private ParkingLot createParkingLotFromForm(SpaceConfiguration config) {
        int id = Integer.parseInt(txtId.getText().trim());
        String name = txtName.getText().trim();
        int numberOfSpaces = config.getTotalSpaces();

        ArrayList<Vehicle> vehicles = new ArrayList<>();
        Space[] spaces = new Space[numberOfSpaces];

        // Crear espacios según configuración
        int spaceIndex = 0;

        // Espacios para camión
        for (int i = 0; i < config.getDisabledSpaces(); i++) {
            spaces[spaceIndex] = new Space(spaceIndex + 1, true, false, truckType);
            spaceIndex++;
        }

        // Espacios para motocicletas
        for (int i = 0; i < config.getMotorcycleSpaces(); i++) {
            spaces[spaceIndex] = new Space(spaceIndex + 1, false, false, motorcycleType);
            spaceIndex++;
        }

        // Espacios estándar
        for (int i = 0; i < config.getStandardSpaces(); i++) {
            spaces[spaceIndex] = new Space(spaceIndex + 1, false, false, carType);
            spaceIndex++;
        }

        return new ParkingLot(id, name, numberOfSpaces, vehicles, spaces);
    }

    /**
     * Actualiza un parqueo existente
     */
    private ParkingLot updateExistingParkingLot(ParkingLot existing, SpaceConfiguration config) {
        int newNumberOfSpaces = Integer.parseInt(txtNumberOfSpaces.getText().trim());

        // Si no hay nueva configuración, mantener la existente
        if (config == null) {
            existing.setName(txtName.getText().trim());
            existing.setNumberOfSpaces(newNumberOfSpaces);
            return existing;
        }

        // Crear nuevo array de espacios
        Space[] newSpaces = new Space[newNumberOfSpaces];

        // Copiar espacios existentes si es posible
        Space[] oldSpaces = existing.getSpaces();
        int copyLength = Math.min(oldSpaces.length, newNumberOfSpaces);
        for (int i = 0; i < copyLength; i++) {
            newSpaces[i] = oldSpaces[i];
        }

        // Si hay nuevos espacios, crearlos según configuración
        if (newNumberOfSpaces > oldSpaces.length) {
            int spaceIndex = oldSpaces.length;

            // Determinar cuántos de cada tipo agregar proporcionalmente
            int totalNewSpaces = newNumberOfSpaces - oldSpaces.length;
            int newDisabled = (int) (totalNewSpaces * (config.getDisabledSpaces() / (double) config.getTotalSpaces()));
            int newMotorcycle = (int) (totalNewSpaces * (config.getMotorcycleSpaces() / (double) config.getTotalSpaces()));

            for (int i = 0; i < newDisabled && spaceIndex < newNumberOfSpaces; i++) {
                newSpaces[spaceIndex] = new Space(spaceIndex + 1, true, false, truckType);
                spaceIndex++;
            }

            for (int i = 0; i < newMotorcycle && spaceIndex < newNumberOfSpaces; i++) {
                newSpaces[spaceIndex] = new Space(spaceIndex + 1, false, false, motorcycleType);
                spaceIndex++;
            }

            // El resto son estándar
            while (spaceIndex < newNumberOfSpaces) {
                newSpaces[spaceIndex] = new Space(spaceIndex + 1, false, false, carType);
                spaceIndex++;
            }
        }

        existing.setName(txtName.getText().trim());
        existing.setNumberOfSpaces(newNumberOfSpaces);
        existing.setSpaces(newSpaces);

        return existing;
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

    /**
     * Llena el formulario con datos de la tabla seleccionada
     */
    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row == -1) {
            return;
        }

        try {
            int id = Integer.parseInt(model.getValueAt(row, 0).toString());
            ParkingLot parkingLot = parkingLotData.findParkingLotById(id);

            if (parkingLot != null) {
                txtId.setText(String.valueOf(parkingLot.getId()));
                txtName.setText(parkingLot.getName());
                txtNumberOfSpaces.setText(String.valueOf(parkingLot.getNumberOfSpaces()));
            }
        } catch (Exception e) {
            System.err.println("Error al llenar formulario: " + e.getMessage());
        }
    }

    /**
     * Limpia el formulario
     */
    private void clearForm() {
        txtName.setText("");
        txtNumberOfSpaces.setText("");

        table.clearSelection();

        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        btnViewDetails.setEnabled(false);

        txtName.requestFocus();
    }

    /**
     * Muestra detalles del parqueo seleccionado
     */
    private void viewParkingLotDetails() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un parqueo de la tabla primero",
                    "Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = Integer.parseInt(model.getValueAt(row, 0).toString());
            ParkingLot parkingLot = parkingLotData.findParkingLotById(id);

            if (parkingLot != null) {
                showParkingLotDetailsDialog(parkingLot);
            }
        } catch (Exception e) {
            System.err.println("Error mostrando detalles: " + e.getMessage());
        }
    }

    /**
     * Muestra diálogo con detalles del parqueo
     */
    private void showParkingLotDetailsDialog(ParkingLot parkingLot) {
        StringBuilder details = new StringBuilder();
        details.append("DETALLES DEL PARQUEO\n");
        details.append("══════════════════════\n\n");
        details.append("ID: ").append(parkingLot.getId()).append("\n");
        details.append("Nombre: ").append(parkingLot.getName()).append("\n");
        details.append("Espacios Totales: ").append(parkingLot.getNumberOfSpaces()).append("\n");

        int vehiclesCount = (parkingLot.getVehicles() != null) ? parkingLot.getVehicles().size() : 0;
        details.append("Vehículos Estacionados: ").append(vehiclesCount).append("\n");
        details.append("Espacios Disponibles: ").append(parkingLot.getNumberOfSpaces() - vehiclesCount).append("\n");

        // Contar tipos de espacios
        if (parkingLot.getSpaces() != null) {
            int disabledSpaces = 0;
            int motorcycleSpaces = 0;
            int occupiedSpaces = 0;

            for (Space space : parkingLot.getSpaces()) {
                if (space != null) {
                    if (space.isDisabilityAdaptation()) {
                        disabledSpaces++;
                    }
                    if (space.getVehicleType().getDescription().equals("Motocicleta")) {
                        motorcycleSpaces++;
                    }
                    if (space.isSpaceTaken()) {
                        occupiedSpaces++;
                    }
                }
            }

            details.append("\nDISTRIBUCIÓN DE ESPACIOS:\n");
            details.append("─────────────────────────\n");
            details.append("• Para camiones: ").append(disabledSpaces).append("\n");
            details.append("• Para motocicletas: ").append(motorcycleSpaces).append("\n");
            details.append("• Carros estándar: ").append(parkingLot.getSpaces().length - disabledSpaces - motorcycleSpaces).append("\n");
            details.append("• Ocupados: ").append(occupiedSpaces).append("\n");
            details.append("• Libres: ").append(parkingLot.getSpaces().length - occupiedSpaces).append("\n");
        }

        // Mostrar vehículos si hay
        if (parkingLot.getVehicles() != null && !parkingLot.getVehicles().isEmpty()) {
            details.append("\nVEHÍCULOS ESTACIONADOS:\n");
            details.append("──────────────────────\n");
            for (Vehicle vehicle : parkingLot.getVehicles()) {
                details.append("• ").append(vehicle.getPlate())
                        .append(" - ").append(vehicle.getVehicleType()).append("\n");
                //.append(" - Hora entrada: ").append(vehicle.getEntryTime())
            }
        }

        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setBackground(new Color(248, 248, 248));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(this, scrollPane,
                "Detalles: " + parkingLot.getName(),
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Muestra diálogo de configuración
     */
    private void showConfigurationDialog() {
        JOptionPane.showMessageDialog(this,
                "CONFIGURACIÓN ACTUAL\n\n"
                + "Tarifas por hora:\n"
                + "• Automóviles: $" + String.format("%.2f", carType.getFee()) + "\n"
                + "• Motocicletas: $" + String.format("%.2f", motorcycleType.getFee()) + "\n"
                + "• Camiones: $" + String.format("%.2f", truckType.getFee()) + "\n\n"
                + "Distribución predeterminada:\n"
                + "• 10% espacios para camiones\n"
                + "• 15% espacios para motocicletas\n"
                + "• 75% espacios estándar\n\n"
                + "La distribución se ajustará automáticamente al crear un nuevo parqueo.",
                "Configuración del Sistema",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Clase para configuración de espacios
     */
    class SpaceConfiguration {

        private int truckSpaces;
        private int motorcycleSpaces;
        private int standardSpaces;

        public SpaceConfiguration(int truckSpaces, int motorcycleSpaces, int standardSpaces) {
            this.truckSpaces = truckSpaces;
            this.motorcycleSpaces = motorcycleSpaces;
            this.standardSpaces = standardSpaces;
        }

        public int getDisabledSpaces() {
            return truckSpaces;
        }

        public int getMotorcycleSpaces() {
            return motorcycleSpaces;
        }

        public int getStandardSpaces() {
            return standardSpaces;
        }

        public int getTotalSpaces() {
            return truckSpaces + motorcycleSpaces + standardSpaces;
        }
    }

    /**
     * Renderer personalizado para la columna de estado
     */
    private class StatusCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(table, value,
                    isSelected, hasFocus, row, column);

            JLabel label = (JLabel) c;
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setOpaque(true);
            label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

            if (value != null) {
                String status = value.toString();

                // Resetear estilo primero
                label.setFont(table.getFont());

                switch (status) {
                    case "LLENO":
                        label.setBackground(new Color(255, 205, 210)); // Rojo claro
                        label.setForeground(new Color(198, 40, 40));   // Rojo oscuro
                        label.setFont(label.getFont().deriveFont(Font.BOLD));
                        label.setBorder(BorderFactory.createLineBorder(new Color(198, 40, 40), 1));
                        break;
                    case "CASI LLENO":
                        label.setBackground(new Color(255, 245, 157)); // Amarillo claro
                        label.setForeground(new Color(245, 124, 0));   // Naranja
                        label.setFont(label.getFont().deriveFont(Font.BOLD));
                        label.setBorder(BorderFactory.createLineBorder(new Color(245, 124, 0), 1));
                        break;
                    case "VACÍO":
                        label.setBackground(new Color(200, 230, 201)); // Verde claro
                        label.setForeground(new Color(46, 125, 50));   // Verde oscuro
                        label.setBorder(BorderFactory.createLineBorder(new Color(46, 125, 50), 1));
                        break;
                    case "OCUPADO":
                        label.setBackground(new Color(255, 224, 178)); // Naranja claro
                        label.setForeground(new Color(245, 124, 0));   // Naranja
                        label.setBorder(BorderFactory.createLineBorder(new Color(245, 124, 0), 1));
                        break;
                    case "DISPONIBLE":
                        label.setBackground(new Color(227, 242, 253)); // Azul claro
                        label.setForeground(new Color(21, 101, 192));  // Azul
                        label.setBorder(BorderFactory.createLineBorder(new Color(21, 101, 192), 1));
                        break;
                    default:
                        if (isSelected) {
                            label.setBackground(table.getSelectionBackground());
                            label.setForeground(table.getSelectionForeground());
                        } else {
                            label.setBackground(table.getBackground());
                            label.setForeground(table.getForeground());
                        }
                }
            }

            return label;
        }
    }
}
