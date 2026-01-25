package view;

import Controller.ClientController;
import controller.VehicleController;
import Controller.SpaceController;
import model.entities.Client;
import model.entities.Space;
import model.entities.Vehicle;
import model.entities.VehicleType;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;

public class AssignSpaceDialog extends JDialog {

    private Space space;
    private SpaceController spaceController;
    private ClientController clientController;
    private VehicleController vehicleController;

    private JComboBox<String> clientCombo;
    private JComboBox<String> vehicleCombo;
    private JTextField txtPlate;
    private JTextField txtColor;
    private JTextField txtBrand;
    private JTextField txtModel;
    private JComboBox<String> vehicleTypeCombo;
    private JButton btnAssign;
    private JButton btnCancel;
    private JButton btnNewClient;
    private JButton btnNewVehicle;

    private ArrayList<Client> clientsList;
    private ArrayList<Vehicle> vehiclesList;

    private boolean assigned = false;

    public AssignSpaceDialog(JFrame parent, Space space, SpaceController spaceController) {
        super(parent, "Asignar Espacio #" + space.getId(), true);
        this.space = space;
        this.spaceController = spaceController;
        this.clientController = new ClientController();
        this.vehicleController = new VehicleController();

        setSize(600, 500);
        setLocationRelativeTo(parent);
        initComponents();
        loadClients();
        loadVehicles();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Información del espacio
        JPanel spacePanel = createSpaceInfoPanel();
        mainPanel.add(spacePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Selección de cliente
        JPanel clientPanel = createClientPanel();
        mainPanel.add(clientPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Selección/registro de vehículo
        JPanel vehiclePanel = createVehiclePanel();
        mainPanel.add(vehiclePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        add(mainPanel, BorderLayout.CENTER);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btnAssign = new JButton("Asignar Espacio");
        btnAssign.setBackground(new Color(46, 125, 50));
        btnAssign.setForeground(Color.WHITE);
        btnAssign.setFont(new Font("Arial", Font.BOLD, 12));
        btnAssign.addActionListener(e -> assignSpace());

        btnCancel = new JButton("Cancelar");
        btnCancel.setBackground(new Color(120, 144, 156));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnAssign);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createSpaceInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Información del Espacio"));
        panel.setBackground(new Color(240, 248, 255));

        panel.add(new JLabel("Espacio #:"));
        panel.add(new JLabel(String.valueOf(space.getId())));
        panel.add(new JLabel("Tipo:"));
        panel.add(new JLabel(space.getVehicleType().getDescription()));

        if (space.isDisabilityAdaptation()) {
            JLabel lblAccessibility = new JLabel("♿ Accesible para discapacitados");
            lblAccessibility.setForeground(Color.BLUE);
            lblAccessibility.setFont(new Font("Arial", Font.BOLD, 11));
            panel.add(new JLabel("Accesibilidad:"));
            panel.add(lblAccessibility);
        }

        return panel;
    }

    private JPanel createClientPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Seleccionar Cliente"));

        // Panel superior con combo y botón
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        topPanel.add(new JLabel("Cliente:"));

        clientCombo = new JComboBox<>();
        clientCombo.setPreferredSize(new Dimension(250, 25));
        topPanel.add(clientCombo);

        btnNewClient = new JButton("Nuevo Cliente");
        btnNewClient.addActionListener(e -> createNewClient());
        topPanel.add(btnNewClient);

        panel.add(topPanel);

        // Panel de información del cliente seleccionado
        JPanel infoPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        infoPanel.setBorder(BorderFactory.createEtchedBorder());

        JLabel lblClientId = new JLabel("ID: -");
        JLabel lblClientName = new JLabel("Nombre: -");
        JLabel lblClientStatus = new JLabel("Preferencial: -");

        lblClientId.setFont(new Font("Arial", Font.PLAIN, 11));
        lblClientName.setFont(new Font("Arial", Font.PLAIN, 11));
        lblClientStatus.setFont(new Font("Arial", Font.PLAIN, 11));

        infoPanel.add(lblClientId);
        infoPanel.add(lblClientName);
        infoPanel.add(lblClientStatus);

        // Actualizar información cuando se selecciona un cliente
        clientCombo.addActionListener(e -> {
            int index = clientCombo.getSelectedIndex();
            if (index >= 0 && index < clientsList.size()) {
                Client selected = clientsList.get(index);
                lblClientId.setText("ID: " + selected.getId());
                lblClientName.setText("Nombre: " + selected.getName());
                lblClientStatus.setText("Preferencial: " + (selected.isIsPreferential() ? "Sí" : "No"));
            }
        });

        panel.add(infoPanel);

        return panel;
    }

    private JPanel createVehiclePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Vehículo"));

        // Panel para seleccionar vehículo existente
        JPanel selectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        selectPanel.add(new JLabel("Vehículo existente:"));

        vehicleCombo = new JComboBox<>();
        vehicleCombo.setPreferredSize(new Dimension(200, 25));
        selectPanel.add(vehicleCombo);

        btnNewVehicle = new JButton("Nuevo Vehículo");
        btnNewVehicle.addActionListener(e -> toggleNewVehicleForm());
        selectPanel.add(btnNewVehicle);

        panel.add(selectPanel);

        // Panel para registrar nuevo vehículo (inicialmente oculto)
        JPanel newVehiclePanel = createNewVehicleForm();
        newVehiclePanel.setVisible(false);
        panel.add(newVehiclePanel);

        return panel;
    }

    private JPanel createNewVehicleForm() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Registrar Nuevo Vehículo"));

        panel.add(new JLabel("Placa:"));
        txtPlate = new JTextField();
        panel.add(txtPlate);

        panel.add(new JLabel("Marca:"));
        txtBrand = new JTextField();
        panel.add(txtBrand);

        panel.add(new JLabel("Modelo:"));
        txtModel = new JTextField();
        panel.add(txtModel);

        panel.add(new JLabel("Color:"));
        txtColor = new JTextField();
        panel.add(txtColor);

        panel.add(new JLabel("Tipo:"));
        vehicleTypeCombo = new JComboBox<>(new String[]{"Carro", "Moto", "Camión"});
        panel.add(vehicleTypeCombo);

        return panel;
    }

    private void loadClients() {
        clientsList = clientController.getAllClients();
        clientCombo.removeAllItems();

        for (Client client : clientsList) {
            clientCombo.addItem(client.getId() + " - " + client.getName());
        }

        if (clientsList.isEmpty()) {
            clientCombo.addItem("No hay clientes registrados");
            clientCombo.setEnabled(false);
        }
    }

    private void loadVehicles() {
        vehiclesList = vehicleController.getAllVehicles();
        vehicleCombo.removeAllItems();

        for (Vehicle vehicle : vehiclesList) {
            vehicleCombo.addItem(vehicle.getPlate() + " - " + vehicle.getBrand());
        }

        if (vehiclesList.isEmpty()) {
            vehicleCombo.addItem("No hay vehículos registrados");
        }
    }

    private void createNewClient() {
        // Usar la ventana existente de gestión de clientes
        ClientViewInternal clientView = new ClientViewInternal();
        JDesktopPane desktopPane = (JDesktopPane) SwingUtilities.getAncestorOfClass(JDesktopPane.class, this);

        if (desktopPane != null) {
            desktopPane.add(clientView);
            clientView.setVisible(true);

            // Esperar a que se cree el cliente
            // Podrías implementar un listener o verificar periódicamente
            JOptionPane.showMessageDialog(this,
                    "Complete el formulario de cliente y presione 'Guardar Nuevo'",
                    "Crear Nuevo Cliente",
                    JOptionPane.INFORMATION_MESSAGE);

            // Recargar lista de clientes después de un tiempo
            Timer timer = new Timer(2000, e -> {
                loadClients();
                if (!clientsList.isEmpty()) {
                    clientCombo.setSelectedIndex(clientsList.size() - 1);
                }
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    private void toggleNewVehicleForm() {
        Component[] components = ((JPanel) vehicleCombo.getParent().getParent()).getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel && ((JPanel) comp).getBorder() != null
                    && ((JPanel) comp).getBorder().toString().contains("Registrar Nuevo Vehículo")) {
                comp.setVisible(!comp.isVisible());
                vehicleCombo.setEnabled(!comp.isVisible());
                btnNewVehicle.setText(comp.isVisible() ? "Usar existente" : "Nuevo Vehículo");
                break;
            }
        }
    }

    private void assignSpace() {
        try {
            // Validar que se haya seleccionado un cliente
            int clientIndex = clientCombo.getSelectedIndex();
            if (clientIndex < 0 || clientIndex >= clientsList.size()) {
                JOptionPane.showMessageDialog(this,
                        "Debe seleccionar un cliente válido",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Client selectedClient = clientsList.get(clientIndex);

            // Obtener o crear vehículo
            Vehicle vehicle;
            boolean usingNewVehicle = false;

            // Verificar si se está usando el formulario de nuevo vehículo
            Component[] components = ((JPanel) vehicleCombo.getParent().getParent()).getComponents();
            JPanel newVehiclePanel = null;
            for (Component comp : components) {
                if (comp instanceof JPanel && comp.isVisible()
                        && ((JPanel) comp).getBorder() != null
                        && ((JPanel) comp).getBorder().toString().contains("Registrar Nuevo Vehículo")) {
                    newVehiclePanel = (JPanel) comp;
                    break;
                }
            }

            if (newVehiclePanel != null && newVehiclePanel.isVisible()) {
                // Crear nuevo vehículo
                String plate = txtPlate.getText().trim();
                String color = txtColor.getText().trim();
                String brand = txtBrand.getText().trim();
                String model = txtModel.getText().trim();
                String typeStr = (String) vehicleTypeCombo.getSelectedItem();

                // Validar campos
                if (plate.isEmpty() || color.isEmpty() || brand.isEmpty() || model.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Complete todos los campos del vehículo",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Verificar si la placa ya existe
                if (vehicleController.findVehicleByPlate(plate) != null) {
                    JOptionPane.showMessageDialog(this,
                            "Ya existe un vehículo con la placa: " + plate,
                            "Placa Duplicada",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Crear VehicleType
                VehicleType vehicleType = new VehicleType();
                vehicleType.setDescription(typeStr);

                // Crear vehículo
                vehicle = new Vehicle(plate, color, brand, model, selectedClient, vehicleType);

                // Registrar vehículo
                String result = vehicleController.insertVehicle(vehicle);
                if (!result.contains("éxito") && !result.contains("exito")) {
                    JOptionPane.showMessageDialog(this,
                            "Error al registrar vehículo: " + result,
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                usingNewVehicle = true;
            } else {
                // Usar vehículo existente
                int vehicleIndex = vehicleCombo.getSelectedIndex();
                if (vehicleIndex < 0 || vehicleIndex >= vehiclesList.size()) {
                    JOptionPane.showMessageDialog(this,
                            "Debe seleccionar un vehículo válido",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                vehicle = vehiclesList.get(vehicleIndex);
            }

            // Validar compatibilidad del vehículo con el espacio
            if (!isVehicleCompatibleWithSpace(vehicle, space, selectedClient)) {
                String message = "El vehículo no es compatible con este espacio.\n\n";

                if (space.isDisabilityAdaptation() && !selectedClient.isIsPreferential()) {
                    message += "Este espacio es para clientes con discapacidad.\n";
                    message += "El cliente seleccionado no es preferencial.";
                } else if (!space.getVehicleType().getDescription().equals(vehicle.getVehicleType().getDescription())) {
                    message += "Tipo requerido: " + space.getVehicleType().getDescription() + "\n";
                    message += "Tipo del vehículo: " + vehicle.getVehicleType().getDescription();
                }

                JOptionPane.showMessageDialog(this, message,
                        "Incompatibilidad", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Confirmar asignación
            String confirmMessage = "¿Confirmar asignación?\n\n"
                    + "Espacio: #" + space.getId() + "\n"
                    + "Cliente: " + selectedClient.getName() + "\n"
                    + "Vehículo: " + vehicle.getPlate() + " - " + vehicle.getBrand() + "\n"
                    + (usingNewVehicle ? "(Vehículo nuevo registrado)" : "(Vehículo existente)");

            int confirm = JOptionPane.showConfirmDialog(this,
                    confirmMessage,
                    "Confirmar Asignación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                // Asignar espacio usando el SpaceController
                boolean success = spaceController.occupySpace(space.getId(), selectedClient, vehicle);

                if (success) {
                    // Registrar hora de entrada
                    space.setEntryTime(new Date());

                    JOptionPane.showMessageDialog(this,
                            "¡Espacio asignado exitosamente!\n\n"
                            + "Cliente: " + selectedClient.getName() + "\n"
                            + "Vehículo: " + vehicle.getPlate() + "\n"
                            + "Hora de entrada: " + new Date(),
                            "Asignación Exitosa",
                            JOptionPane.INFORMATION_MESSAGE);

                    assigned = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Error al asignar el espacio. Puede que ya esté ocupado.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error inesperado: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private boolean isVehicleCompatibleWithSpace(Vehicle vehicle, Space space, Client client) {
        // Validar tipo de vehículo
        if (vehicle.getVehicleType() == null || space.getVehicleType() == null) {
            return false;
        }

        String vehicleType = vehicle.getVehicleType().getDescription();
        String spaceType = space.getVehicleType().getDescription();

        // Mapeo de tipos compatibles
        if (!vehicleType.equals(spaceType)) {
            // Si es espacio para discapacitados, solo debe aceptar "Carro" con cliente preferencial
            if (space.isDisabilityAdaptation()) {
                return vehicleType.equals("Carro") && client.isIsPreferential();
            }
            return false;
        }

        // Validar discapacidad
        if (space.isDisabilityAdaptation() && !client.isIsPreferential()) {
            return false;
        }

        return true;
    }

    public boolean wasAssigned() {
        return assigned;
    }
}
