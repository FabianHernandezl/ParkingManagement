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

    public static void main(String[] args) {
        showMainMenu();
    }

    public static void showMainMenu() {

        int option = -1;

        while (option != 0) {

            try {
                option = Integer.parseInt(
                        JOptionPane.showInputDialog(
                                "PARKING MANAGEMENT\n\n"
                                + "0. Salir\n"
                                + "1. Clientes\n"
                                + "2. Vehículos\n"
                                + "3. Ingreso de vehículo (Tiquete)\n"
                                + "4. Parqueos\n"
                        )
                );
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Debe ingresar un número válido");
                option = -1;
            }

            switch (option) {

                case 0 ->
                    JOptionPane.showMessageDialog(null, "¡Hasta pronto!");

                case 1 ->
                    new ClientView().showClientMenu();

                case 2 ->
                    new VehicleView().showVehicleMenu();

                case 3 ->
                    new TicketView().registerVehicleAndGenerateTicket();

                case 4 ->
                    new ParkingLotView().showParkingLotsMenu();

                default ->
                    JOptionPane.showMessageDialog(null, "Opción inválida");
            }
        }

    }
}
