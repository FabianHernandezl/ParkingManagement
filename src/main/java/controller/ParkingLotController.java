package controller;

import java.util.ArrayList;
import java.util.List;
import model.data.ParkingLotData;
import model.entities.ParkingLot;
import model.entities.Vehicle;
import model.entities.Space;
import model.entities.VehicleType;
import model.entities.Ticket;
import controller.TicketController;

public class ParkingLotController {

    private ParkingLotData parkingLotData = new ParkingLotData();

    // Registrar parqueo simple o con espacios opcionales
    public ParkingLot registerParkingLot(
            String name,
            int totalSpaces,
            int disabledSpaces,
            int preferentialSpaces,
            int motorcycleSpaces,
            int truckSpaces, int bicycleSpaces) {

        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setId(parkingLotData.findLastIdNumberOfParkingLot() + 1);
        parkingLot.setName(name);
        parkingLot.setNumberOfSpaces(totalSpaces);

        Space[] spaces = createSpacesForParkingLot(
                totalSpaces,
                preferentialSpaces,
                motorcycleSpaces,
                truckSpaces, bicycleSpaces
        );

        // Asignar la referencia del parqueo a cada espacio
        for (Space space : spaces) {
            space.setParkingLot(parkingLot);
        }

        parkingLot.setSpaces(spaces);

        ParkingLot saved = parkingLotData.addParkingLot(parkingLot);

        // 🔥 GUARDAR INMEDIATAMENTE
        parkingLotData.saveParkingLots();
        parkingLotData.saveParkingLotsAsTxt();

        return saved;
    }

    public ParkingLot registerParkingLot(String name,
            int totalSpaces,
            int disabledSpaces,
            int preferentialSpaces,
            int motorcycleSpaces) {

        return registerParkingLot(
                name,
                totalSpaces,
                disabledSpaces,
                preferentialSpaces,
                motorcycleSpaces,
                0, // truck
                0 // bicycle
        );
    }

    private Space[] createSpacesForParkingLot(
            int total,
            int preferential,
            int motorcycle,
            int truck,
            int bicycle) {

        Space[] spaces = new Space[total];
        int spaceId = 1;
        int index = 0;

        // Preferenciales (Automóvil)
        for (int i = 0; i < preferential; i++) {
            spaces[index++] = new Space(spaceId++, true, false,
                    new VehicleType(1, "Automóvil", 4, 5.0f));
        }

        // Motocicletas
        for (int i = 0; i < motorcycle; i++) {
            spaces[index++] = new Space(spaceId++, false, false,
                    new VehicleType(2, "Motocicleta", 2, 2.5f));
        }
        // Camiones
        for (int i = 0; i < truck; i++) {
            spaces[index++] = new Space(spaceId++, false, false,
                    new VehicleType(3, "Camión", 4, 8.0f));
        }

        // Bicicletas
        for (int i = 0; i < bicycle; i++) {
            spaces[index++] = new Space(spaceId++, false, false,
                    new VehicleType(4, "Bicicleta", 2, 1.5f));
        }

        // Lo restante → Autos normales
        while (index < total) {
            spaces[index++] = new Space(spaceId++, false, false,
                    new VehicleType(1, "Automóvil", 4, 5.0f));
        }

        return spaces;
    }

    /**
     * 🔥 VERSIÓN CORREGIDA - Asigna toda la información del vehículo
     */
    public int registerVehicleInParkingLot(Vehicle vehicle, ParkingLot parkingLot) {
        System.out.println("--- registerVehicleInParkingLot ---");
        System.out.println("Vehículo: " + (vehicle != null ? vehicle.getPlate() : "null"));
        System.out.println("Parqueo: " + (parkingLot != null ? parkingLot.getName() : "null"));

        if (vehicle == null || parkingLot == null || parkingLot.getSpaces() == null) {
            System.out.println("❌ Datos inválidos");
            return 0;
        }

        boolean hasDisability = vehicle.hasPreferentialClient();
        int vehicleTypeId = vehicle.getVehicleType().getId();

        for (Space space : parkingLot.getSpaces()) {

            if (space.isSpaceTaken()) {
                continue;
            }

            // 🔥 Si NO es preferencial → NO puede usar espacio adaptado
            if (!hasDisability && space.isDisabilityAdaptation()) {
                continue;
            }

            // 🔥 Si SÍ es preferencial → SOLO puede usar adaptado
            if (hasDisability && !space.isDisabilityAdaptation()) {
                continue;
            }

            // Validar tipo de vehículo
            if (space.getVehicleType() != null
                    && space.getVehicleType().getId() != vehicleTypeId) {
                continue;
            }

            System.out.println("✅ Espacio SELECCIONADO: " + space.getId());

            // 🔥 ASIGNAR TODA LA INFORMACIÓN
            space.setSpaceTaken(true);
            space.setVehicle(vehicle);
            space.setVehicleType(vehicle.getVehicleType()); // ¡Línea CRUCIAL!

            System.out.println("  Vehículo asignado: " + vehicle.getPlate());
            System.out.println("  Tipo asignado: " + vehicle.getVehicleType().getDescription());

            parkingLotData.updateParkingLot(parkingLot);
            parkingLotData.saveParkingLots();
            parkingLotData.saveParkingLotsAsTxt();

            return space.getId();
        }

        System.out.println("❌ No se encontró espacio disponible");
        return 0;
    }

    /**
     * 🔥 NUEVO MÉTODO - Repara espacios ocupados desde tickets activos
     */
    public void repararEspaciosDesdeTickets() {
        System.out.println("\n=== REPARANDO ESPACIOS DESDE TICKETS ===");
        TicketController ticketController = TicketController.getInstance();

        // Obtener tickets activos
        List<Ticket> ticketsActivos = ticketController.getActiveTickets();
        System.out.println("Tickets activos encontrados: " + ticketsActivos.size());

        int reparados = 0;

        for (Ticket ticket : ticketsActivos) {
            if (ticket.getSpace() == null || ticket.getVehicle() == null) {
                continue;
            }

            int spaceId = ticket.getSpace().getId();

            // Buscar el espacio en todos los parqueos
            for (ParkingLot lot : getAllParkingLots()) {
                boolean encontrado = false;
                for (Space space : lot.getSpaces()) {
                    if (space.getId() == spaceId && space.isSpaceTaken()) {
                        // Reparar el espacio con información del ticket
                        if (space.getVehicle() == null) {
                            space.setVehicle(ticket.getVehicle());
                            reparados++;
                        }
                        if (space.getVehicleType() == null && ticket.getVehicle().getVehicleType() != null) {
                            space.setVehicleType(ticket.getVehicle().getVehicleType());
                            reparados++;
                        }

                        System.out.println("  Reparado espacio " + spaceId
                                + " en parqueo " + lot.getName()
                                + " - Vehículo: " + ticket.getVehicle().getPlate()
                                + " - Tipo: " + ticket.getVehicle().getVehicleType().getDescription());

                        parkingLotData.updateParkingLot(lot);
                        encontrado = true;
                        break;
                    }
                }
                if (encontrado) {
                    break;
                }
            }
        }

        if (reparados > 0) {
            parkingLotData.saveParkingLots();
            System.out.println("✅ Reparación completada: " + reparados + " espacios reparados");
        } else {
            System.out.println("✅ No fue necesario reparar espacios");
        }
        System.out.println("========================================\n");
    }

    public void removeVehicleFromParkingLot(Vehicle vehicle, ParkingLot parkingLot) {
        System.out.println("--- removeVehicleFromParkingLot ---");
        System.out.println("Vehículo: " + (vehicle != null ? vehicle.getPlate() : "null"));
        System.out.println("Parqueo: " + (parkingLot != null ? parkingLot.getName() : "null"));

        parkingLotData.removeVehicleFromParkingLot(vehicle, parkingLot);

        // 🔥 FORZAR GUARDADO DESPUÉS DE REMOVER
        parkingLotData.saveParkingLots();
        parkingLotData.saveParkingLotsAsTxt();
    }

    public ParkingLot findParkingLotById(int id) {
        return parkingLotData.findParkingLotById(id);
    }

    public ParkingLot findParkingLotByName(String name) {
        return parkingLotData.findParkingLotByName(name);
    }

    public ArrayList<ParkingLot> getAllParkingLots() {
        return parkingLotData.getAllParkingLots();
    }

    public String updateParkingLot(int id, ParkingLot newParkingLot) {
        if (parkingLotData.findParkingLotById(id) != null) {
            parkingLotData.updateParkingLot(newParkingLot);

            // 🔥 FORZAR GUARDADO DESPUÉS DE ACTUALIZAR
            parkingLotData.saveParkingLots();
            parkingLotData.saveParkingLotsAsTxt();

            return "El parqueo fue actualizado correctamente";
        } else {
            return "El parqueo no se actualizó porque no se encontró en la base de datos.";
        }
    }

    public String removeParkingLot(int id) {
        ParkingLot lot = parkingLotData.findParkingLotById(id);
        if (lot != null) {
            parkingLotData.deleteParkingLot(lot);

            // 🔥 FORZAR GUARDADO DESPUÉS DE ELIMINAR
            parkingLotData.saveParkingLots();
            parkingLotData.saveParkingLotsAsTxt();

            return "El parqueo se eliminó correctamente";
        } else {
            return "El parqueo no se eliminó porque no se encontró en la base de datos.";
        }
    }

    // ========== MÉTODOS DE BÚSQUEDA DE ESPACIOS ==========
    /**
     * Busca un espacio por su ID en todos los parqueos
     */
    public Space findSpaceById(int spaceId) {
        ArrayList<ParkingLot> allParkingLots = getAllParkingLots();
        for (ParkingLot parkingLot : allParkingLots) {
            for (Space space : parkingLot.getSpaces()) {
                if (space.getId() == spaceId) {
                    space.setParkingLot(parkingLot);
                    return space;
                }
            }
        }
        return null;
    }

    /**
     * Busca un espacio por su número en un parqueo específico
     */
    public Space findSpaceByNumber(int parkingLotId, int spaceNumber) {
        ParkingLot parkingLot = findParkingLotById(parkingLotId);
        if (parkingLot != null) {
            for (Space space : parkingLot.getSpaces()) {
                if (space.getId() == spaceNumber) {
                    space.setParkingLot(parkingLot);
                    return space;
                }
            }
        }
        return null;
    }

    /**
     * Busca un espacio por su ID y el ID del parqueo
     */
    public Space findSpaceByParkingLotAndSpaceId(int parkingLotId, int spaceId) {
        ParkingLot parkingLot = findParkingLotById(parkingLotId);
        if (parkingLot != null) {
            for (Space space : parkingLot.getSpaces()) {
                if (space.getId() == spaceId) {
                    space.setParkingLot(parkingLot);
                    return space;
                }
            }
        }
        return null;
    }

    /**
     * Obtiene el nombre del parqueo para un espacio específico
     */
    public String getParkingLotNameBySpaceId(int spaceId) {
        Space space = findSpaceById(spaceId);
        if (space != null && space.getParkingLot() != null) {
            return space.getParkingLot().getName();
        }
        return "N/A";
    }
}
