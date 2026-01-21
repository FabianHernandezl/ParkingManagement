/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import Controller.ParkingLotController;
import static java.lang.Boolean.parseBoolean;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import model.entity.ParkingLot;
import model.entity.Space;
import model.entity.Vehicle;
import model.entity.VehicleType;

/**
 *
 * @author Camila
 */
public class ParkingLotView {

    static ParkingLotController parkingLotController = new ParkingLotController();

    static void showParkingLotsMenu() {

        int choice = 1;
        while (choice != 0) {

            choice = Integer.parseInt(JOptionPane.showInputDialog(
                    "-----------Menú Parqueos------------\n"
                    + "Seleccione una opción: \n"
                    + " 1) Añadir parqueo\n"
                    + " 2) Mostrar todos los parqueos\n"
                    + " 3) Buscar un parqueo\n"
                    + " 4) Actualizar parqueo\n"
                    + " 5) Borrar parqueo\n"
                    + " 0) Regresar\n"
            ));
            switch (choice) {
                case 0 -> {
                    JOptionPane.showMessageDialog(null, "Saliendo de administrador de Parqueos...");
                }
                case 1 -> {
                    insertParkingLot();
                }
                case 2 -> {
                    showAllParkingLots();
                }
                case 3 -> {
                    consultParkingLot();
                }
                case 4 -> {
                    updateParkingLot();
                }
                case 5 -> {
                    removeParkingLot();
                }
                default -> {

                }
            }//switch
        }//while     

    }

    private static void insertParkingLot() {
        String name = JOptionPane.showInputDialog("Ingrese el nombre del parqueo");
        int numberOfSpaces = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el número de espacios que tiene el parqueo"));
        int numberOfSpacesWithDisabiltyAdaptation = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el número de espacios designados para personas con discapacidad"));

        Space[] spaces = new Space[numberOfSpaces];
        spaces = configureSpaces(spaces, numberOfSpacesWithDisabiltyAdaptation);
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setName(name);
        parkingLot.setNumberOfSpaces(numberOfSpaces);
        parkingLot.setVehicles(new ArrayList<Vehicle>());

        parkingLotController.registerParkingLot(parkingLot);

    }

    private static Space[] configureSpaces(Space[] spaces, int numberOfSpacesWithDisabilityAdaptation) {
        if (numberOfSpacesWithDisabilityAdaptation <= spaces.length) {
            for (int i = 0; i < numberOfSpacesWithDisabilityAdaptation; i++) {
                Space space = new Space();

                space.setId(i);
                space.setDisabilityAdaptation(true);
                space.setVehicleType(configureVehicleTypeOfSpaces(i, true)); //para personas con discapacidad

                spaces[i] = space;
            }

            for (int i = numberOfSpacesWithDisabilityAdaptation; i < spaces.length; i++) {
                Space space = new Space();

                space.setId(i);
                space.setDisabilityAdaptation(false);
                space.setVehicleType(configureVehicleTypeOfSpaces(i, false));//no para personas con discapacidad

                spaces[i] = space;
            }

        } else {

            JOptionPane.showMessageDialog(null, "El número de espacios seleccionados sobrepasa el máximo configurado para este parqueo");
        }

        return spaces;
    }

    private static VehicleType configureVehicleTypeOfSpaces(int position, boolean disabilityPresented) {
        String[] types = {"Tipos de vehículo", "1)moto", "2)liviano", "3)pesado", "4)bicicleta", "5)otro"};
        byte[] tires = {0, 2, 4, 8, 12, -1};

        String allTypes = "";
        for (String type : types) {

            allTypes += type + "\n";
        }
        VehicleType vehicleType = new VehicleType();

        byte typeNumber;
        typeNumber = Byte.parseByte(JOptionPane.showInputDialog(allTypes + "\n" + "Ingrese el número del tipo de vehículo del espacio # " + position + " ¿Discapacidad?=" + (disabilityPresented ? "Sí" : "No")));
        vehicleType.setId(typeNumber);
        vehicleType.setDescription(types[typeNumber]);
        vehicleType.setNumberOfTires(tires[typeNumber]);

        return vehicleType;
    }

    //consultar parkings
    private static void showAllParkingLots() {

        if (parkingLotController.getAllParkingLots().isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay parqueos registrados. ");
            return;
        }

        String info = "Lista de Parqueos\n\n";

        for (ParkingLot c : parkingLotController.getAllParkingLots()) {
            info += c + "\n";
        }

        JOptionPane.showMessageDialog(null, info);
    }

    private static void consultParkingLot() {

        int id = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el ID del parqueo:"));
        ParkingLot parkingLot = parkingLotController.findParkingLotById(id);

        if (parkingLot == null) {
            JOptionPane.showMessageDialog(null, "No se encontró el parqueo");
        } else {
            JOptionPane.showMessageDialog(null, parkingLot.toString());
        }
    }

    /*
     private int id;
    private String name;
    private int numberOfSpaces;
    private ArrayList<Vehicle> vehicles;
    private Space[] spaces; */
    private static void updateParkingLot() {
        int originalId = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el id del parqueo que desea actualizar: "));

       // String newId = JOptionPane.showInputDialog("Ingrese el nuevo número de id del parqueo: ");
        String name = JOptionPane.showInputDialog("Ingrese el nuevo nombre del parqueo: ");
        int numberOfSpaces = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el nuevo número de espacios: "));
        int numberOfSpacesWithDisabiltyAdaptation = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el número de espacios designados para personas con discapacidad"));    
        
        Space[] spaces = new Space[numberOfSpaces];
        spaces = configureSpaces(spaces, numberOfSpacesWithDisabiltyAdaptation);
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setName(name);
        parkingLot.setNumberOfSpaces(numberOfSpaces);
        parkingLot.setVehicles(new ArrayList<Vehicle>());
        
        
        JOptionPane.showMessageDialog(null,
                parkingLotController.updateParkingLot(originalId, parkingLot));
    }

    private static void removeParkingLot() {
        String id = JOptionPane.showInputDialog("Ingrese el ID del parqueo que desea eliminar: ");

        ParkingLot parkingLotToRemove = new ParkingLot(); //ask si se puede hacer con el id

        JOptionPane.showMessageDialog(null,
                parkingLotController.removeParkingLot(parkingLotToRemove));
    }
}
