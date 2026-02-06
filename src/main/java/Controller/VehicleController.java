package controller;

import java.util.ArrayList;
import model.data.ParkingLotData;
import model.data.VehicleData;
import model.entities.Vehicle;
import model.entities.Client;
import model.entities.ParkingLot;
import Controller.ParkingLotController;
import Controller.TicketController;
import model.entities.Space;

public class VehicleController {

    private ParkingLotData parkingLotData = new ParkingLotData();
    private ParkingLotController parkingLotController = new ParkingLotController();
    private ParkingLot parkingLot = new ParkingLot(); // parqueo único
    private TicketController ticketController = TicketController.getInstance();

    VehicleData vehicleData = new VehicleData();

    public String insertVehicle(Vehicle vehicle) {
        System.out.println("\n=== VEHICLECONTROLLER: Insertando vehículo ===");
        System.out.println("Placa: " + (vehicle != null ? vehicle.getPlate() : "null"));

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

                // Intentar parquear el vehículo después de insertarlo
                System.out.println("Intentando parquear el vehículo...");
                int espacioAsignado = registerVehicleInParking(vehicle);

                if (espacioAsignado > 0) {
                    result += "\n✅ Vehículo parqueado en espacio: " + espacioAsignado;
                } else {
                    result += "\n⚠️ Vehículo NO pudo ser parqueado (sin espacios disponibles)";
                }
            } else {
                result = "No se insertó el vehículo, uno de los clientes ya tiene un vehículo registrado";
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

    public int registerVehicleInParking(Vehicle vehicle) {
        System.out.println("\n=== VEHICLECONTROLLER: Registrando en parking ===");
        System.out.println("Vehículo: " + vehicle.getPlate());
        System.out.println("Tipo: " + (vehicle.getVehicleType() != null
                ? vehicle.getVehicleType().getDescription() : "NULL"));

        // Obtener parqueos disponibles
        ArrayList<ParkingLot> parqueos = parkingLotData.getAllParkingLots();
        System.out.println("Parqueos disponibles: " + parqueos.size());

        if (parqueos.isEmpty()) {
            System.out.println("❌ ERROR: No hay parqueos creados");
            return 0;
        }

        // Usar el primer parqueo disponible
        ParkingLot parkingLot = parqueos.get(0);
        System.out.println("Usando parqueo: " + parkingLot.getName());
        System.out.println("Espacios en parqueo: " + parkingLot.getNumberOfSpaces());

        // Registrar el vehículo en el parqueo (esto creará automáticamente el ticket)
        int espacio = parkingLotController.registerVehicleInParkingLot(vehicle, parkingLot);
        System.out.println("Espacio asignado: " + espacio);

        if (espacio <= 0) {
            System.out.println("❌ No se pudo asignar espacio");
            return 0;
        }

        // 🔍 Obtener el Space usando TU ParkingLot
        Space space = null;
        for (Space s : parkingLot.getSpaces()) {
            if (s.getId() == espacio) {
                space = s;
                break;
            }
        }

        if (space == null) {
            System.out.println("❌ ERROR: Space no encontrado");
            return 0;
        }

        // 🎟️ AQUÍ SE GENERA EL TICKET
        ticketController.generateEntryTicket(vehicle, space);

        System.out.println("✅ Ticket generado correctamente");

        return espacio;
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
