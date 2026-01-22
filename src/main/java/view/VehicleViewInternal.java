/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import controller.VehicleController;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import model.entities.Client;
import model.entities.Vehicle;
import model.entities.VehicleType;

/**
 *
 * @author Jimena
 */
public class VehicleViewInternal extends JInternalFrame {

    public VehicleViewInternal() {
        super("Gestión de Clientes", true, true, true, true);
        setSize(700, 400);
        setLocation(20, 20);

        ClientViewInternal panel = new ClientViewInternal();
        this.add(panel.getContentPane());
    }

    private final VehicleController vehicleController = new VehicleController();
    private final ClientViewInternal clientView = new ClientViewInternal();

    

    public void showVehicleMenu() {

        int option = -1;

        while (option != 0) {

            option = Integer.parseInt(JOptionPane.showInputDialog(
                    "VEHÍCULOS\n\n"
                    + "0. Volver\n"
                    + "1. Registrar vehículo\n"
                    + "2. Mostrar vehículos\n"
                    + "3. Actualizar vehículo\n"
                    + "4. Eliminar vehículo"
            ));

            switch (option) {
                case 1 ->
                    insertVehicle();
                case 2 ->
                    showAllVehicles();
                case 3 ->
                    updateVehicle();
                case 4 ->
                    deleteVehicle();
            }
        }
    }

    private void insertVehicle() {

        Vehicle vehicle = new Vehicle();

        vehicle.setPlate(JOptionPane.showInputDialog("Placa"));
        vehicle.setBrand(JOptionPane.showInputDialog("Marca"));
        vehicle.setModel(JOptionPane.showInputDialog("Modelo"));
        vehicle.setColor(JOptionPane.showInputDialog("Color"));

        VehicleType type = selectVehicleType();
        if (type == null) {
            return;
        }

        vehicle.setVehicleType(type);

        boolean addMore = true;

        while (addMore) {

            Client client = clientView.selectOrCreateClient();

            if (client != null) {
                vehicle.addClient(client);
            }

            addMore = JOptionPane.showConfirmDialog(
                    null, "¿Agregar otro cliente?",
                    "Clientes", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
        }

        if (vehicle.getClients().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe tener al menos un cliente");
            return;
        }

        JOptionPane.showMessageDialog(null,
                vehicleController.insertVehicle(vehicle));
    }

    private void showAllVehicles() {

        if (vehicleController.getAllVehicles().isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay vehículos");
            return;
        }

        String info = "VEHÍCULOS\n\n";
        for (Vehicle v : vehicleController.getAllVehicles()) {
            info += v + "\n";
        }

        JOptionPane.showMessageDialog(null, info);
    }

    private void updateVehicle() {

        String plate = JOptionPane.showInputDialog("Ingrese la placa del vehículo a actualizar");
        Vehicle existing = vehicleController.findVehicleByPlate(plate);

        if (existing == null) {
            JOptionPane.showMessageDialog(null, "Vehículo no encontrado");
            return;
        }

        String brand = JOptionPane.showInputDialog("Nueva marca", existing.getBrand());
        String model = JOptionPane.showInputDialog("Nuevo modelo", existing.getModel());
        String color = JOptionPane.showInputDialog("Nuevo color", existing.getColor());

        VehicleType type = selectVehicleType();
        if (type == null) {
            JOptionPane.showMessageDialog(null, "Tipo de vehículo inválido");
            return;
        }

        Vehicle updated = new Vehicle();
        updated.setPlate(plate);
        updated.setBrand(brand);
        updated.setModel(model);
        updated.setColor(color);
        updated.setVehicleType(type);
        updated.setClients(existing.getClients());

        String result = vehicleController.updateVehicle(updated);
        JOptionPane.showMessageDialog(null, result);
    }

    private void deleteVehicle() {

        String plate = JOptionPane.showInputDialog("Ingrese la placa del vehículo a eliminar");

        int confirm = JOptionPane.showConfirmDialog(
                null,
                "¿Está seguro de eliminar el vehículo?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String result = vehicleController.deleteVehicle(plate);
        JOptionPane.showMessageDialog(null, result);
    }

    private static VehicleType selectVehicleType() {

        int option = Integer.parseInt(JOptionPane.showInputDialog(
                "Seleccione el tipo de vehículo:\n"
                + "1. Carro\n"
                + "2. Moto\n"
                + "3. Camión"
        ));

        VehicleType type = new VehicleType();

        switch (option) {
            case 1 -> {
                type.setId(1);
                type.setDescription("Carro");
            }
            case 2 -> {
                type.setId(2);
                type.setDescription("Moto");
            }
            case 3 -> {
                type.setId(3);
                type.setDescription("Camión");
            }
            default -> {
                JOptionPane.showMessageDialog(null, "Opción inválida");
                return null;
            }
        }

        return type;
    }

}
