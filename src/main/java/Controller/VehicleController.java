package Controller;

import java.util.ArrayList;
import model.data.ParkingLotData;
import model.data.VehicleData;
import model.entities.Vehicle;
import model.entities.Client;
import model.entities.ParkingLot;
import Controller.ParkingLotController;
import Controller.TicketController;
import model.entities.ParkingAssignment;
import model.entities.Space;

public class VehicleController {

    private ParkingLotData parkingLotData = new ParkingLotData();
    private ParkingLotController parkingLotController = new ParkingLotController();
    private ParkingLot parkingLot = new ParkingLot(); // parqueo único
    private TicketController ticketController = TicketController.getInstance();

    VehicleData vehicleData = new VehicleData();

    public String insertVehicle(Vehicle vehicle, ParkingLot selectedParkingLot) {

        System.out.println("\n=== VEHICLECONTROLLER: Insertando vehículo ===");
        System.out.println("Placa: " + (vehicle != null ? vehicle.getPlate() : "null"));

        String result;
        boolean clientHasVehicle = false;

        if (vehicle != null && vehicle.getClients() != null) {

            for (Client c : vehicle.getClients()) {
                if (vehicleData.findVehicle(c) != null) {
                    clientHasVehicle = true;
                    break;
                }
            }

            if (!clientHasVehicle) {

                result = vehicleData.insertVehicle(vehicle);

                System.out.println("Intentando parquear el vehículo...");
                ParkingAssignment assignment
                        = registerVehicleInParking(vehicle, selectedParkingLot);

                if (assignment != null) {
                    result += "\n✅ Parqueo: " + assignment.getParkingLot().getName()
                            + "\n📍 Espacio: " + assignment.getSpace().getId();
                } else {
                    result += "\n⚠️ Vehículo NO pudo ser parqueado";
                }

            } else {
                result = "No se insertó el vehículo, el cliente ya tiene un vehículo registrado";
            }

        } else {
            result = "Vehículo inválido";
        }

        System.out.println("Resultado final: " + result);
        return result;
    }

    public ArrayList<Vehicle> getAllVehicles() {
        return vehicleData.getAllVehicles();
    }

    public Vehicle findVehicleByCustomer(Client client) {
        return vehicleData.findVehicle(client);
    }

    public Vehicle findVehicleByPlate(String plate) {
        return vehicleData.findVehicleByPlate(plate);
    }

    public ParkingAssignment registerVehicleInParking(Vehicle vehicle, ParkingLot parkingLot) {

        if (vehicle == null || parkingLot == null) {
            return null;
        }

        int espacioId = parkingLotController
                .registerVehicleInParkingLot(vehicle, parkingLot);

        if (espacioId <= 0) {
            return null;
        }

        Space space = null;
        for (Space s : parkingLot.getSpaces()) {
            if (s.getId() == espacioId) {
                space = s;
                break;
            }
        }

        if (space == null) {
            return null;
        }

        ticketController.generateEntryTicket(vehicle, space);

        return new ParkingAssignment(parkingLot, space);
    }

    public String updateVehicle(Vehicle vehicle) {

        String result = "No se pudo actualizar el vehículo";

        if (vehicle != null) {
            boolean updated = vehicleData.updateVehicle(vehicle);

            if (updated) {
                result = "Vehículo actualizado correctamente";
            }
        }

        return result;
    }

    public String deleteVehicle(String plate) {

        String result = "No se pudo eliminar el vehículo";

        if (plate != null) {
            boolean deleted = vehicleData.deleteVehicle(plate);

            if (deleted) {
                result = "Vehículo eliminado correctamente";
            }
        }

        return result;
    }

}
