/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import Controller.ClientController;
import javax.swing.JOptionPane;
import model.entity.Client;

/**
 *
 * @author Jimena
 */
public class ClientView {

    private final ClientController clientController = new ClientController();

    public void showClientMenu() {

        int option = -1;

        while (option != 0) {

            option = Integer.parseInt(JOptionPane.showInputDialog(
                    "CLIENTES\n\n"
                    + "0. Volver\n"
                    + "1. Registrar cliente\n"
                    + "2. Mostrar clientes\n"
                    + "3. Consultar cliente por ID"
            ));

            switch (option) {
                case 1 ->
                    insertClient();
                case 2 ->
                    showAllClients();
                case 3 ->
                    consultClient();
            }
        }

    }

    private Client insertClient() {

        String id = JOptionPane.showInputDialog("ID");
        String name = JOptionPane.showInputDialog("Nombre");
        String phone = JOptionPane.showInputDialog("Teléfono");

        boolean preferential = JOptionPane.showConfirmDialog(
                null, "¿Preferencial?", "Cliente",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;

        String result = clientController.registerClient(id, name, phone, preferential);
        JOptionPane.showMessageDialog(null, result);

        return clientController.findClientById(id);
    }

    private void showAllClients() {

        if (clientController.getAllClients().isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay clientes");
            return;
        }

        String info = "CLIENTES\n\n";
        for (Client c : clientController.getAllClients()) {
            info += c + "\n";
        }

        JOptionPane.showMessageDialog(null, info);
    }

    private void consultClient() {

        String id = JOptionPane.showInputDialog("ID del cliente");
        Client c = clientController.findClientById(id);

        JOptionPane.showMessageDialog(null,
                c == null ? "Cliente no encontrado" : c.toString());
    }

    public Client selectOrCreateClient() {

        String id = JOptionPane.showInputDialog("ID del cliente");
        Client existing = clientController.findClientById(id);

        if (existing != null) {
            JOptionPane.showMessageDialog(null, "Cliente encontrado:\n" + existing);
            return existing;
        }

        int option = JOptionPane.showConfirmDialog(
                null, "Cliente no existe ¿Crearlo?",
                "Cliente", JOptionPane.YES_NO_OPTION);

        if (option != JOptionPane.YES_OPTION) {
            return null;
        }

        return insertClient();
    }

}
