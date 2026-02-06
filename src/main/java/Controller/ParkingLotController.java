/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import java.util.ArrayList;
import model.data.ParkingLotData;
import model.entities.ParkingLot;
import model.entities.Vehicle;
import model.entities.Space;
import model.entities.VehicleType;
import model.entities.Ticket;

/**
 *
 * @author FAMILIA
 */
public class ParkingLotController {

    private ParkingLotData parkingLotData = new ParkingLotData();
    private TicketController ticketController = TicketController.getInstance();

    public ParkingLot registerParkingLot(String name, int numberOfSpaces, int disabledSpaces, int motorcycleSpaces) {

        // Crear el parqueo
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setName(name);
        parkingLot.setNumberOfSpaces(numberOfSpaces);

        // Crear espacios automáticamente
        Space[] spaces = createSpacesForParkingLot(numberOfSpaces, disabledSpaces, motorcycleSpaces);
        parkingLot.setSpaces(spaces);

        // Guardar espacios individualmente en SpaceData
        SpaceController spaceController = new SpaceController();
        for (Space space : spaces) {
            spaceController.registerSpace(space.getId(),
                    space.isDisabilityAdaptation(),
                    space.isSpaceTaken());
        }

        return parkingLotData.addParkingLot(parkingLot);
    }

    private Space[] createSpacesForParkingLot(int total, int disabled, int motorcycle) {
        Space[] spaces = new Space[total];
        int spaceId = 1;

        // Espacios para discapacidad
        for (int i = 0; i < disabled; i++) {
            spaces[i] = new Space(spaceId++, true, false,
                    new VehicleType(3, "Discapacitado", 4, 0.0f));
        }

        // Espacios para motocicletas
        for (int i = 0; i < motorcycle; i++) {
            spaces[disabled + i] = new Space(spaceId++, false, false,
                    new VehicleType(2, "Motocicleta", 2, 2.5f));
        }

        // Espacios estándar
        for (int i = disabled + motorcycle; i < total; i++) {
            spaces[i] = new Space(spaceId++, false, false,
                    new VehicleType(1, "Automóvil", 4, 5.0f));
        }

        return spaces;
    }

    public int registerVehicleInParkingLot(Vehicle vehicle, ParkingLot parkingLot) {
        System.out.println("\n=== PARKINGLOTCONTROLLER: Registrando vehículo ===");

        if (vehicle == null || parkingLot == null || parkingLot.getSpaces() == null) {
            return 0;
        }

        for (Space space : parkingLot.getSpaces()) {
            if (!space.isSpaceTaken()) {

                // Marcar espacio como ocupado
                space.setSpaceTaken(true);
                space.setVehicle(vehicle);

                // Guardar cambio en Data
                parkingLotData.updateParkingLot(parkingLot);

                System.out.println("✅ Espacio asignado: " + space.getId());

                // 🎟 Crear ticket
                Ticket ticket = ticketController.generateEntryTicket(vehicle, space);

                if (ticket != null) {
                    System.out.println("🎟 Ticket creado ID: " + ticket.getId());
                }

                return space.getId();
            }
        }

        System.out.println("⚠️ No hay espacios disponibles");
        return 0;
    }

    public void removeVehicleFromParkingLot(Vehicle vehicle, ParkingLot parkingLot) {
        parkingLotData.removeVehicleFromParkingLot(vehicle, parkingLot);
    }

    public ParkingLot findParkingLotById(int id) {
        return parkingLotData.findParkingLotById(id);
    }

    public ArrayList<ParkingLot> getAllParkingLots() {
        return parkingLotData.getAllParkingLots();
    }

    public String updateParkingLot(int id, ParkingLot newParkingLot) {
        String result = "";
        if (parkingLotData.findParkingLotById(id) != null) { //rev this creo que es !=
            parkingLotData.updateParkingLot(newParkingLot);
            result = "El parqueo fue actualizado correctamente";
        } else {
            result = "El parqueo no se actualizó porque no se encontró en la base de datos.";
        }
        return result;
    }

    public String removeParkingLot(ParkingLot parkingLot) { //será mejor hacerlo por id? 
        String result = "";
        if (parkingLotData.findParkingLotById(parkingLot.getId()) != null) {
            parkingLotData.deleteParkingLot(parkingLot);
            result = "El parqueo se eliminó ";
        } else {
            result = "El parqueo no se eliminó porque no se encontró en la base de datos.";
        }
        return result;
    }
}
