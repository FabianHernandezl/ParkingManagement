package view;

import Controller.ClientController;
import controller.TicketController;
import controller.VehicleController;
import javax.swing.JOptionPane;
import model.entity.Client;
import model.entity.Ticket;
import model.entity.Vehicle;
import model.entity.VehicleType;

public class ParkingManagement {

    // Controllers
    static ClientController clientController = new ClientController();
    static VehicleController vehicleController = new VehicleController();
    static TicketController ticketController = new TicketController();

    public static void main(String[] args) {
        showMainMenu();
    }

    // ===================== MENÚ PRINCIPAL =====================
    public static void showMainMenu() {

        int option = -1;

        while (option != 0) {

            try {
                option = Integer.parseInt(JOptionPane.showInputDialog(
                        "PARKING MANAGEMENT\n\n"
                        + "0. Salir\n"
                        + "1. Registrar cliente\n"
                        + "2. Mostrar clientes\n"
                        + "3. Consultar cliente por ID\n"
                        + "4. Registrar vehículo\n"
                        + "5. Ingresar vehículo (generar tiquete)\n"
                        + "6. Mostrar vehículos\n"
                        + "7. Actualizar vehículo\n"
                        + "8. Eliminar vehículo\n"
                        + "9. Parqueos\n"
                ));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Debe ingresar un número válido");
                option = -1;
            }

            switch (option) {
                case 0 ->
                    JOptionPane.showMessageDialog(null, "¡Hasta pronto!");

                case 1 ->
                    insertClient();

                case 2 ->
                    showAllClients();

                case 3 ->
                    consultClient();

                case 4 ->
                    insertVehicle();

                case 5 ->
                    registerVehicleAndGenerateTicket();

                case 6 ->
                    showAllVehicles();

                case 7 ->
                    updateVehicle();

                case 8 ->
                    deleteVehicle();
                case 9 ->
                    //manage ParkingLots -CRUD
                    showParkingLotsMenu();

            }
        }
    }

    // ===================== CLIENTES =====================
    private static Client insertClient() {

        String id = JOptionPane.showInputDialog("Ingrese el ID del cliente");
        String name = JOptionPane.showInputDialog("Ingrese el nombre del cliente");
        String phone = JOptionPane.showInputDialog("Ingrese el número de teléfono");

        boolean preferential = JOptionPane.showConfirmDialog(
                null,
                "¿El cliente es preferencial?",
                "Preferencial",
                JOptionPane.YES_NO_OPTION
        ) == JOptionPane.YES_OPTION;

        String result = clientController.registerClient(id, name, phone, preferential);
        JOptionPane.showMessageDialog(null, result);

        return clientController.findClientById(id);
    }

    private static void showAllClients() {

        if (clientController.getAllClients().isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay clientes registrados");
            return;
        }

        String info = "LISTA DE CLIENTES\n\n";

        for (Client c : clientController.getAllClients()) {
            info += c + "\n";
        }

        JOptionPane.showMessageDialog(null, info);
    }

    private static void consultClient() {

        String id = JOptionPane.showInputDialog("Ingrese el ID del cliente");
        Client client = clientController.findClientById(id);

        if (client == null) {
            JOptionPane.showMessageDialog(null, "Cliente no encontrado");
        } else {
            JOptionPane.showMessageDialog(null, client.toString());
        }
    }

    // ===================== VEHÍCULOS =====================
    private static void insertVehicle() {

        String plate = JOptionPane.showInputDialog("Ingrese la placa del vehículo");
        String brand = JOptionPane.showInputDialog("Ingrese la marca del vehículo");
        String model = JOptionPane.showInputDialog("Ingrese el modelo del vehículo");
        String color = JOptionPane.showInputDialog("Ingrese el color del vehículo");

        VehicleType vehicleType = selectVehicleType();

        if (vehicleType == null) {
            JOptionPane.showMessageDialog(null, "No se pudo registrar el vehículo");
            return;
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setPlate(plate);
        vehicle.setBrand(brand);
        vehicle.setModel(model);
        vehicle.setColor(color);
        vehicle.setVehicleType(vehicleType);

        boolean addMoreClients = true;

        while (addMoreClients) {

            Client client = insertClient();

            if (client != null) {
                vehicle.addClient(client);
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo crear el cliente");
            }

            addMoreClients = JOptionPane.showConfirmDialog(
                    null,
                    "¿Desea agregar otro cliente a este vehículo?",
                    "Clientes",
                    JOptionPane.YES_NO_OPTION
            ) == JOptionPane.YES_OPTION;
        }

        if (vehicle.getClients().isEmpty()) {
            JOptionPane.showMessageDialog(null, "El vehículo debe tener al menos un cliente");
            return;
        }

        String result = vehicleController.insertVehicle(vehicle);
        JOptionPane.showMessageDialog(null, result);
    }

    private static void showAllVehicles() {

        if (vehicleController.getAllVehicles().isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay vehículos registrados");
            return;
        }

        String info = "LISTA DE VEHÍCULOS\n\n";

        for (Vehicle v : vehicleController.getAllVehicles()) {
            info += v + "\n";
        }

        JOptionPane.showMessageDialog(null, info);
    }

    private static void updateVehicle() {

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

    private static void deleteVehicle() {

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

    // ===================== TIQUETE =====================
    private static void registerVehicleAndGenerateTicket() {

        String plate = JOptionPane.showInputDialog("Ingrese la placa del vehículo");

        Vehicle vehicle = vehicleController.findVehicleByPlate(plate);

        if (vehicle == null) {
            JOptionPane.showMessageDialog(null, "Vehículo no encontrado");
            return;
        }

        int spaceId = vehicleController.registerVehicleInParking(vehicle);

        if (spaceId == 0) {
            JOptionPane.showMessageDialog(null, "No hay espacios disponibles");
            return;
        }

        Ticket ticket = ticketController.generateEntryTicket(vehicle, spaceId);
        JOptionPane.showMessageDialog(null, ticket.toString());
    }

    // ===================== TIPO DE VEHÍCULO =====================
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
     // ===================== PARKING LOT =====================
    public static void showParkingLotsMenu() {

        ParkingLotView menu = new ParkingLotView();
        menu.showParkingLotsMenu();
    }

 

}
