package controller;

import java.util.ArrayList;
import model.data.ParkingLotData;
import model.data.VehicleData;
import model.entities.Vehicle;
import model.entities.Client;
import model.entities.ParkingLot;
import controller.ParkingLotController;
import controller.TicketController;
import model.entities.ParkingAssignment;
import model.entities.Space;
import model.entities.Ticket;

public class VehicleController {

    private ParkingLotData parkingLotData = new ParkingLotData();
    private ParkingLotController parkingLotController = new ParkingLotController();
    private ParkingLot parkingLot = new ParkingLot(); // parqueo único
    private TicketController ticketController = TicketController.getInstance();

    VehicleData vehicleData = new VehicleData();

    public String insertVehicle(Vehicle vehicle, ParkingLot selectedParkingLot) {

        if (vehicle == null || selectedParkingLot == null) {
            return "Vehículo o parqueo inválido";
        }

        // Verificar si algún cliente ya tiene vehículo
        for (Client c : vehicle.getClients()) {
            if (vehicleData.findVehicle(c) != null) {
                return "No se insertó el vehículo, el cliente ya tiene un vehículo registrado";
            }
        }

        // 🚨 PRIMERO intentar parquear
        ParkingAssignment assignment
                = registerVehicleInParking(vehicle, selectedParkingLot);

        if (assignment == null) {
            return "❌ No hay espacios disponibles para este tipo de vehículo";
        }

        // ✅ Si hay espacio, ahora sí guardar
        String result = vehicleData.insertVehicle(vehicle);

        return result
                + "\n✅ Parqueo: " + assignment.getParkingLot().getName()
                + "\n📍 Espacio: " + assignment.getSpace().getId();
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

        for (Client c : vehicle.getClients()) {
            System.out.println("Cliente: " + c.getName()
                    + " | Preferencial: " + c.isIsPreferential());
        }

        if (vehicle == null || parkingLot == null) {
            return null;
        }

        // DEBUG: Verificar el parqueo
        System.out.println("=== DEBUG REGISTRO VEHÍCULO ===");
        System.out.println("Parqueo: " + parkingLot.getName() + " (ID: " + parkingLot.getId() + ")");

        // Verificar si el parqueo tiene espacios
        if (parkingLot.getSpaces() == null) {
            System.out.println("❌ ERROR: El parqueo no tiene espacios inicializados");
            return null;
        }

        System.out.println("Total espacios: " + parkingLot.getSpaces().length);

        int espacioId = parkingLotController
                .registerVehicleInParkingLot(vehicle, parkingLot);

        System.out.println("Espacio asignado por controller: " + espacioId);

        if (espacioId <= 0) {
            System.out.println("❌ No se asignó espacio");
            return null;
        }

        // Buscar el espacio en el arreglo
        Space space = null;
        for (Space s : parkingLot.getSpaces()) {
            if (s != null && s.getId() == espacioId) {
                space = s;
                System.out.println("✅ Espacio encontrado: " + space.getId());
                break;
            }
        }

        if (space == null) {
            System.out.println("❌ ERROR: No se encontró el espacio " + espacioId + " en el parqueo " + parkingLot.getName());
            return null;
        }

        // Asegurar que el espacio tiene el parkingLot
        space.setParkingLot(parkingLot);

        // Usar el método que recibe parkingLotId y spaceId
        Ticket ticket = ticketController.generateEntryTicket(vehicle, parkingLot.getId(), space.getId());

        if (ticket == null) {
            System.out.println("❌ ERROR: No se pudo generar el ticket");
            return null;
        }

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

    /**
     * Elimina un vehículo y también su ticket activo si existe
     *
     * @param plate Placa del vehículo a eliminar
     * @return Mensaje con el resultado de la operación
     * @throws Exception Si ocurre algún error durante el proceso
     */
    public String deleteVehicle(String plate) throws Exception {
        if (plate == null || plate.trim().isEmpty()) {
            throw new IllegalArgumentException("Placa inválida");
        }

        // 1. Buscar el vehículo por su placa
        Vehicle vehicle = findVehicleByPlate(plate);

        if (vehicle == null) {
            throw new Exception("No se encontró un vehículo con la placa: " + plate);
        }

        // 2. Buscar si tiene un ticket activo
        Ticket activeTicket = null;
        for (Ticket ticket : ticketController.getActiveTickets()) {
            if (ticket.getVehicle() != null
                    && ticket.getVehicle().getPlate() != null
                    && ticket.getVehicle().getPlate().equalsIgnoreCase(plate)) {
                activeTicket = ticket;
                break;
            }
        }

        // 3. Si hay ticket activo, cerrarlo (registrar salida)
        double totalCobrado = 0;
        if (activeTicket != null) {
            totalCobrado = ticketController.registerExit(activeTicket);
        }

        // 4. Liberar el espacio en el parqueo (si estaba estacionado)
        for (ParkingLot pl : parkingLotController.getAllParkingLots()) {
            if (pl.getVehicles() != null && pl.getVehicles().contains(vehicle)) {
                parkingLotController.removeVehicleFromParkingLot(vehicle, pl);
                break;
            }
        }

        // 5. Eliminar el vehículo de la base de datos
        boolean deleted = vehicleData.deleteVehicle(plate);

        if (!deleted) {
            throw new Exception("No se pudo eliminar el vehículo de la base de datos");
        }

        // Construir mensaje de éxito
        String mensaje = "Vehículo eliminado correctamente";
        if (activeTicket != null) {
            mensaje += " y su ticket ha sido cerrado. Total cobrado: ₡" + String.format("%.2f", totalCobrado);
        }

        return mensaje;
    }

    /**
     * Versión simplificada que solo elimina el vehículo (sin ticket)
     *
     * @param plate Placa del vehículo a eliminar
     * @return Mensaje con el resultado de la operación
     * @throws Exception Si ocurre algún error durante el proceso
     */
    public String deleteVehicleOnly(String plate) throws Exception {
        if (plate == null || plate.trim().isEmpty()) {
            throw new IllegalArgumentException("Placa inválida");
        }

        boolean deleted = vehicleData.deleteVehicle(plate);

        if (!deleted) {
            throw new Exception("No se pudo eliminar el vehículo");
        }

        return "Vehículo eliminado correctamente";
    }
}
