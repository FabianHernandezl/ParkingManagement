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
    private JTextField txtPlate, txtColor, txtBrand, txtModel;
    private JComboBox<String> vehicleTypeCombo;
    private JButton btnAssign, btnCancel, btnNewClient, btnNewVehicle;

    private ArrayList<Client> clientsList;
    private ArrayList<Vehicle> vehiclesList;
    private boolean assigned = false;

    public AssignSpaceDialog(JFrame parent, Space space, SpaceController spaceController) {
        super(parent, "Asignar Espacio #" + space.getId(), true);
        this.space = space;
        this.spaceController = spaceController;
        this.clientController = new ClientController();
        this.vehicleController = new VehicleController();

        setSize(600, 550);
        setLocationRelativeTo(parent);
        initComponents();
        loadClients();
        loadVehicles();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(createSpaceInfoPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(createClientPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(createVehiclePanel());

        add(mainPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAssign = new JButton("Asignar Espacio");
        btnAssign.setBackground(new Color(46, 125, 50));
        btnAssign.setForeground(Color.WHITE);
        btnAssign.setFont(new Font("Arial", Font.BOLD, 12));
        btnAssign.addActionListener(e -> assignSpace());

        btnCancel = new JButton("Cancelar");
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnAssign);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createSpaceInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Información del Espacio"));
        panel.setBackground(new Color(240, 248, 255));

        panel.add(new JLabel("Espacio #:"));
        panel.add(new JLabel(String.valueOf(space.getId())));
        panel.add(new JLabel("Tipo Requerido:"));
        panel.add(new JLabel(space.getVehicleType().getDescription()));

        panel.add(new JLabel("Accesibilidad:"));
        String accText = space.isDisabilityAdaptation() ? "♿ Ley 7600 (Preferencial)" : "Estándar";
        JLabel lblAcc = new JLabel(accText);
        if (space.isDisabilityAdaptation()) {
            lblAcc.setForeground(Color.BLUE);
        }
        panel.add(lblAcc);

        return panel;
    }

    private JPanel createClientPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Seleccionar Cliente"));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        clientCombo = new JComboBox<>();
        clientCombo.setPreferredSize(new Dimension(300, 25));
        btnNewClient = new JButton("+");
        btnNewClient.setToolTipText("Nuevo Cliente");
        btnNewClient.addActionListener(e -> createNewClient());

        topPanel.add(new JLabel("Cliente:"));
        topPanel.add(clientCombo);
        topPanel.add(btnNewClient);
        panel.add(topPanel);

        return panel;
    }

    private JPanel createVehiclePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Vehículo"));

        JPanel selectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        vehicleCombo = new JComboBox<>();
        vehicleCombo.setPreferredSize(new Dimension(250, 25));
        btnNewVehicle = new JButton("Nuevo Vehículo");
        btnNewVehicle.addActionListener(e -> toggleNewVehicleForm());

        selectPanel.add(new JLabel("Existente:"));
        selectPanel.add(vehicleCombo);
        selectPanel.add(btnNewVehicle);
        panel.add(selectPanel);

        panel.add(createNewVehicleForm());
        return panel;
    }

    private JPanel newVehicleForm;

    private JPanel createNewVehicleForm() {
        newVehicleForm = new JPanel(new GridLayout(5, 2, 5, 5));
        newVehicleForm.setBorder(BorderFactory.createTitledBorder("Datos del Nuevo Vehículo"));
        newVehicleForm.setVisible(false);

        newVehicleForm.add(new JLabel("Placa:"));
        txtPlate = new JTextField();
        newVehicleForm.add(txtPlate);
        newVehicleForm.add(new JLabel("Marca:"));
        txtBrand = new JTextField();
        newVehicleForm.add(txtBrand);
        newVehicleForm.add(new JLabel("Modelo:"));
        txtModel = new JTextField();
        newVehicleForm.add(txtModel);
        newVehicleForm.add(new JLabel("Color:"));
        txtColor = new JTextField();
        newVehicleForm.add(txtColor);
        newVehicleForm.add(new JLabel("Tipo:"));
        vehicleTypeCombo = new JComboBox<>(new String[]{"Carro", "Motocicleta", "Camión"});
        newVehicleForm.add(vehicleTypeCombo);

        return newVehicleForm;
    }

    private void loadClients() {
        clientsList = clientController.getAllClients();
        clientCombo.removeAllItems();
        for (Client c : clientsList) {
            clientCombo.addItem(c.getId() + " - " + c.getName());
        }
    }

    private void loadVehicles() {
        vehiclesList = vehicleController.getAllVehicles();
        vehicleCombo.removeAllItems();
        for (Vehicle v : vehiclesList) {
            vehicleCombo.addItem(v.getPlate() + " - " + v.getBrand());
        }
    }

    private void toggleNewVehicleForm() {
        boolean isVisible = newVehicleForm.isVisible();
        newVehicleForm.setVisible(!isVisible);
        vehicleCombo.setEnabled(isVisible);
        btnNewVehicle.setText(!isVisible ? "Cancelar Nuevo" : "Nuevo Vehículo");
        revalidate();
    }

    // --- LÓGICA DE ASIGNACIÓN CORREGIDA ---
    private void assignSpace() {
        try {
            // 1. Validar Cliente seleccionado
            int clientIndex = clientCombo.getSelectedIndex();
            if (clientIndex < 0) {
                showError("Debe seleccionar un cliente.");
                return;
            }
            Client selectedClient = clientsList.get(clientIndex);

            // 2. Obtener Vehículo (Nuevo o Existente)
            Vehicle selectedVehicle = null;
            if (newVehicleForm.isVisible()) {
                selectedVehicle = createNewVehicleFromForm(selectedClient);
                if (selectedVehicle == null) {
                    return; // Error ya mostrado en el método
                }
            } else {
                int vIndex = vehicleCombo.getSelectedIndex();
                if (vIndex < 0) {
                    showError("Debe seleccionar un vehículo.");
                    return;
                }
                selectedVehicle = vehiclesList.get(vIndex);
            }

            // 3. Validar Compatibilidad (Aquí estaba el fallo de mensajes mentirosos)
            if (!isVehicleCompatibleWithSpace(selectedVehicle, space, selectedClient)) {
                // El mensaje detallado ahora se maneja dentro de la validación o aquí
                return;
            }

            // 4. Confirmación final
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Asignar espacio #" + space.getId() + " a " + selectedVehicle.getPlate() + "?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // LLAMADA AL CONTROLADOR
                String result = spaceController.occupySpace(space.getId(), selectedClient, selectedVehicle);

                // --- Busca esta parte en tu método assignSpace() ---
                if (result.equals("OK")) {
                    // Sincronizamos el objeto local con los nuevos datos
                    Space updated = spaceController.findSpaceById(space.getId());
                    if (updated != null) {
                        this.space.setSpaceTaken(true);
                        this.space.setClient(selectedClient);
                        this.space.setVehicle(selectedVehicle);
                        this.space.setEntryTime(updated.getEntryTime());
                    }

                    JOptionPane.showMessageDialog(this, "¡Espacio asignado exitosamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    assigned = true;
                    dispose();
                }
            }

        } catch (Exception e) {
            showError("Error inesperado: " + e.getMessage());
        }
    }

    private Vehicle createNewVehicleFromForm(Client owner) {
        String plate = txtPlate.getText().trim();
        if (plate.isEmpty()) {
            showError("La placa es obligatoria.");
            return null;
        }

        // Verificar si la placa ya existe para evitar errores de BD
        if (vehicleController.findVehicleByPlate(plate) != null) {
            showError("La placa " + plate + " ya está registrada en el sistema.");
            return null;
        }

        VehicleType type = new VehicleType();
        type.setDescription((String) vehicleTypeCombo.getSelectedItem());

        Vehicle v = new Vehicle(plate, txtColor.getText(), txtBrand.getText(), txtModel.getText(), owner, type);
        String res = vehicleController.insertVehicle(v);

        if (res.toLowerCase().contains("exito") || res.toLowerCase().contains("éxito")) {
            return v;
        } else {
            showError("No se pudo registrar el vehículo: " + res);
            return null;
        }
    }

    private boolean isVehicleCompatibleWithSpace(Vehicle v, Space s, Client c) {
        String vType = v.getVehicleType().getDescription().toLowerCase().replace("ó", "o").trim();
        String sType = s.getVehicleType().getDescription().toLowerCase().replace("ó", "o").trim();

        // Normalizar nombres (Moto = Motocicleta, Carro = Automovil)
        if (vType.equals("motocicleta")) {
            vType = "moto";
        }
        if (sType.equals("motocicleta")) {
            sType = "moto";
        }
        if (vType.equals("automovil")) {
            vType = "carro";
        }
        if (sType.equals("automovil")) {
            sType = "carro";
        }

        // Validar Tipo
        if (!vType.equals(sType)) {
            showError("Incompatibilidad de TIPO:\nEspacio para: " + s.getVehicleType().getDescription()
                    + "\nVehículo es: " + v.getVehicleType().getDescription());
            return false;
        }

        // Validar Ley 7600 (Discapacidad)
        if (s.isDisabilityAdaptation() && !c.isIsPreferential()) {
            showError("Espacio RESTRINGIDO:\nEste espacio es exclusivo para clientes preferenciales (Ley 7600).");
            return false;
        }

        return true;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Atención", JOptionPane.WARNING_MESSAGE);
    }

    private void createNewClient() {
        JOptionPane.showMessageDialog(this, "Abra la pestaña 'Clientes' para registrar uno nuevo.");
    }

    public boolean wasAssigned() {
        return assigned;
    }
}
