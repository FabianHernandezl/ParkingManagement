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
                vehicleData.insertVehicle(vehicle);
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

    // ===================== NUEVOS MÉTODOS =====================
    public Vehicle findVehicleByPlate(String plate) {

        Vehicle foundVehicle = null;

        for (Vehicle v : vehicleData.getAllVehicles()) {
            if (v.getPlate().equalsIgnoreCase(plate)) {
                foundVehicle = v;
                break;
            }
        }

        return foundVehicle;
    }

    public int registerVehicleInParking(Vehicle vehicle) {

        return parkingLotData.registerVehicleInParkingLot(vehicle, parkingLot);
    }
}
