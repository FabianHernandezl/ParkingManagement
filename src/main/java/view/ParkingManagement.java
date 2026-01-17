package view;

import Controller.ClientController;
import javax.swing.JOptionPane;
import model.data.ClientData;
import model.entity.Client;

/**
 *
 * @author fabia
 */
public class ParkingManagement {

    static ClientController clientController = new ClientController();

    public static void main(String[] args) {
        showClientMenu();
    }

    public static void showClientMenu() {

        int option = 1;

        while (option != 0) {

            option = Integer.parseInt(JOptionPane.showInputDialog(
                    "CLIENT MANAGEMENT\n\n"
                    + "0. Salir\n"
                    + "1. Registrar cliente\n"
                    + "2. Mostrar clientes\n"
                    + "3. Consultar cliente por ID"
            )
            );

            switch (option) {

                case 0 ->
                    JOptionPane.showMessageDialog(null, "¡Hasta pronto!");

                case 1 ->
                    insertClient();

                case 2 ->
                    showAllClients();

                case 3 ->
                    consultClient();

                default -> {

                }
            }

        }
    }

    private static void insertClient() {

        String ID = JOptionPane.showInputDialog("Ingrese el id del cliente");
        String name = JOptionPane.showInputDialog("Ingres el nombre del cliente");
        String phone = JOptionPane.showInputDialog("Ingrese el numero de telefono del cliente");
        boolean preferential = JOptionPane.showConfirmDialog(null,
                "El cliente es preferencial",
                "Preferencial",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;

        String result = clientController.registerClient(ID, name, phone, preferential);
        JOptionPane.showMessageDialog(null, result);

    }

    private static void showAllClients() {

        if (clientController.getAllClients().isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay clientes registrados");
            return;
        }

        String clientsInfo = "Lista de clientes\n\n";

        for (Client c : clientController.getAllClients()) {
            clientsInfo += c + "\n";
        }

        JOptionPane.showMessageDialog(null, clientsInfo);

    }

    private static void consultClient() {

        String id = JOptionPane.showInputDialog("Ingrese el id del cliente:");

        Client client = clientController.findClientById(id);

        if (client == null) {
            JOptionPane.showMessageDialog(null, "Cliente no encontrado");
        } else {
            JOptionPane.showMessageDialog(null, client.toString());
        }
    }

}
