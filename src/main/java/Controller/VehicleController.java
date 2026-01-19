package controller;

import java.util.ArrayList;
import model.data.ParkingLotData;
import model.data.VehicleData;
import model.entity.Vehicle;
import model.entity.Client;
import model.entity.ParkingLot;

public class VehicleController {

    private ParkingLotData parkingLotData = new ParkingLotData();
    private ParkingLot parkingLot = new ParkingLot(); // parqueo único

    VehicleData vehicleData = new VehicleData();

    public String insertVehicle(Vehicle vehicle) {

        String result = "Vehículo insertado con éxito";
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
            } else {
                result = "No se insertó el vehículo, uno de los clientes ya tiene un vehículo registrado";
            }

        } else {
            result = "Vehículo inválido";
        }

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

    public int registerVehicleInParking(Vehicle vehicle) {
        return parkingLotData.registerVehicleInParkingLot(vehicle, parkingLot);
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
