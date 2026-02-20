package controller;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import model.data.TicketData;
import model.entities.ParkingLot;
import model.entities.ParkingRate;
import model.entities.Space;
import model.entities.Ticket;
import model.entities.Vehicle;
import util.TxtTicketUtil;

public class TicketController {

    private static final TicketData ticketData = new TicketData();
    private final ParkingRateController rateController;
    private final ParkingLotController parkingLotController;

    private static int ticketIdCounter = 1;
    private static TicketController instance;

    private TicketController() {
        this.rateController = new ParkingRateController();
        this.parkingLotController = new ParkingLotController();

        // Cargar tickets activos al iniciar el controlador
        cargarTicketsActivosAlIniciar();
        initializeIdCounter();
    }

    public static TicketController getInstance() {
        if (instance == null) {
            instance = new TicketController();
        }
        return instance;
    }

    /**
     * Carga los tickets activos desde el archivo al iniciar el programa
     */
    private void cargarTicketsActivosAlIniciar() {
        List<Ticket> ticketsActivos = TxtTicketUtil.cargarTicketsActivos();
        for (Ticket ticket : ticketsActivos) {
            Ticket existente = ticketData.findActiveTicketByVehicle(ticket.getVehicle());
            if (existente == null) {
                // Al cargar, intentamos reparar las referencias
                repararReferenciaTicket(ticket);
                ticketData.insertTicket(ticket);
            }
        }
        System.out.println("Tickets activos cargados: " + ticketsActivos.size());
    }

    /**
     * Busca un espacio por su ID y el ID del parqueo
     *
     * @param parkingLotId ID del parqueo
     * @param spaceId ID del espacio
     * @return El espacio encontrado o null
     */
    public Space findSpaceByParkingLotAndSpaceId(int parkingLotId, int spaceId) {
        ParkingLot parkingLot = parkingLotController.findParkingLotById(parkingLotId);
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
     * Busca el parqueo al que pertenece un espacio
     *
     * @param spaceId ID del espacio
     * @return El nombre del parqueo o "N/A" si no se encuentra
     */
    public String buscarNombreParqueoPorEspacio(int spaceId) {
        // Obtener todos los parqueos
        ArrayList<ParkingLot> todosLosParqueos = parkingLotController.getAllParkingLots();

        // Buscar el espacio en cada parqueo
        for (ParkingLot parqueo : todosLosParqueos) {
            for (Space espacio : parqueo.getSpaces()) {
                if (espacio.getId() == spaceId) {
                    return parqueo.getName();
                }
            }
        }

        return "N/A";
    }

    /**
     * Repara la referencia de un ticket buscando el parqueo correcto
     *
     * @param ticket El ticket a reparar
     */
    public void repararReferenciaTicket(Ticket ticket) {
        if (ticket == null || ticket.getSpace() == null) {
            return;
        }

        int spaceId = ticket.getSpace().getId();

        // Si el ticket ya tiene parkingLot, intentar buscar el espacio en ese parqueo
        if (ticket.getParkingLot() != null && ticket.getParkingLot().getId() > 0) {
            Space space = findSpaceByParkingLotAndSpaceId(ticket.getParkingLot().getId(), spaceId);
            if (space != null) {
                ticket.setSpace(space);
                ticket.setParkingLot(space.getParkingLot());
                return;
            }
        }

        // Si no, buscar en todos los parqueos
        String nombreParqueo = buscarNombreParqueoPorEspacio(spaceId);

        if (!"N/A".equals(nombreParqueo)) {
            // Crear un objeto ParkingLot con al menos el nombre
            ParkingLot parkingLot = new ParkingLot();
            parkingLot.setName(nombreParqueo);

            // Buscar también el ID si es posible
            for (ParkingLot pl : parkingLotController.getAllParkingLots()) {
                if (pl.getName().equals(nombreParqueo)) {
                    parkingLot.setId(pl.getId());
                    // Buscar el espacio específico en este parqueo
                    Space space = findSpaceByParkingLotAndSpaceId(pl.getId(), spaceId);
                    if (space != null) {
                        ticket.setSpace(space);
                    }
                    break;
                }
            }

            ticket.setParkingLot(parkingLot);
        }
    }

    /**
     * Versión simplificada para la vista - solo devuelve el nombre
     */
    public String getNombreParqueoForTicket(Ticket ticket) {
        if (ticket == null) {
            return "N/A";
        }

        // Si ya tiene parkingLot, intentar validar que sea correcto
        if (ticket.getParkingLot() != null && ticket.getParkingLot().getId() > 0 && ticket.getSpace() != null) {
            Space space = findSpaceByParkingLotAndSpaceId(ticket.getParkingLot().getId(), ticket.getSpace().getId());
            if (space != null) {
                return space.getParkingLot().getName();
            }
        }

        // Si no, buscar por espacio
        if (ticket.getSpace() != null) {
            return buscarNombreParqueoPorEspacio(ticket.getSpace().getId());
        }

        return "N/A";
    }

    /**
     * Obtiene todos los tickets con sus nombres de parqueo reparados
     */
    public List<Ticket> getTodosLosTicketsConParqueo() {
        List<Ticket> todos = new ArrayList<>();

        // Tickets activos
        List<Ticket> activos = getActiveTickets();
        for (Ticket t : activos) {
            repararReferenciaTicket(t);
            todos.add(t);
        }

        // Tickets históricos
        List<Ticket> historicos = TxtTicketUtil.leerTicketsTXT();
        for (Ticket t : historicos) {
            repararReferenciaTicket(t);
            todos.add(t);
        }

        return todos;
    }

    /**
     * Inicializa el contador de IDs basado en los tickets existentes.
     */
    private void initializeIdCounter() {
        List<Ticket> allTickets = ticketData.getAllTickets();
        if (!allTickets.isEmpty()) {
            int maxId = allTickets.stream()
                    .mapToInt(Ticket::getId)
                    .max()
                    .orElse(0);

            List<Ticket> historicalTickets = TxtTicketUtil.leerTicketsTXT();
            if (historicalTickets != null && !historicalTickets.isEmpty()) {
                int maxHistoricalId = historicalTickets.stream()
                        .mapToInt(Ticket::getId)
                        .max()
                        .orElse(0);
                maxId = Math.max(maxId, maxHistoricalId);
            }

            ticketIdCounter = maxId + 1;
        }
    }

    /**
     * Genera un ticket de entrada para un vehículo (VERSIÓN PRINCIPAL
     * CORREGIDA)
     *
     * @param vehicle Vehículo que ingresa
     * @param parkingLotId ID del parqueo
     * @param spaceId ID del espacio
     * @return Ticket generado o null si hay error
     */
    public Ticket generateEntryTicket(Vehicle vehicle, int parkingLotId, int spaceId) {

        if (vehicle == null) {
            System.out.println("ERROR: vehicle es null");
            return null;
        }

        // Buscar el espacio específico en el parqueo indicado
        Space space = findSpaceByParkingLotAndSpaceId(parkingLotId, spaceId);

        if (space == null) {
            System.out.println("ERROR: No se encontró el espacio " + spaceId + " en el parqueo " + parkingLotId);
            return null;
        }

        // Verificar si ya tiene ticket activo
        Ticket existing = ticketData.findActiveTicketByVehicle(vehicle);
        if (existing != null) {
            System.out.println("Ya existe ticket activo para este vehículo");
            return null;
        }

        Ticket ticket = new Ticket();
        ticket.setId(ticketIdCounter++);
        ticket.setVehicle(vehicle);
        ticket.setSpace(space);
        ticket.setParkingLot(space.getParkingLot()); // Usar el parkingLot del espacio
        ticket.setEntryTime(LocalDateTime.now());
        ticket.setExitTime(null);
        ticket.setTotal(0.0);

        System.out.println("=== TICKET CREADO CORRECTAMENTE ===");
        System.out.println("Ticket ID: " + ticket.getId());
        System.out.println("Parqueo: " + space.getParkingLot().getName() + " (ID: " + space.getParkingLot().getId() + ")");
        System.out.println("Espacio: " + space.getId());

        boolean inserted = ticketData.insertTicket(ticket);

        if (!inserted) {
            System.out.println("ERROR: No se pudo insertar el ticket");
            return null;
        }

        guardarTicketsActivos();
        appendToRegistro(ticket, "ENTRADA");

        return ticket;
    }

    /**
     * Versión anterior para compatibilidad (ahora usa la nueva)
     */
    public Ticket generateEntryTicket(Vehicle vehicle, Space space) {
        if (vehicle == null || space == null) {
            return null;
        }

        // Si el espacio tiene parkingLot, usarlo
        if (space.getParkingLot() != null) {
            return generateEntryTicket(vehicle, space.getParkingLot().getId(), space.getId());
        }

        // Si no, buscar el espacio en todos los parqueos
        for (ParkingLot pl : parkingLotController.getAllParkingLots()) {
            for (Space s : pl.getSpaces()) {
                if (s.getId() == space.getId()) {
                    return generateEntryTicket(vehicle, pl.getId(), s.getId());
                }
            }
        }

        System.out.println("ERROR: No se pudo encontrar el parqueo para el espacio " + space.getId());
        return null;
    }

    /**
     * Guarda todos los tickets activos en el archivo
     */
    private void guardarTicketsActivos() {
        List<Ticket> todosLosTickets = ticketData.getAllTickets();
        TxtTicketUtil.guardarTicketsActivos(todosLosTickets);
    }

    /**
     * Registra la salida de un vehículo y calcula el total.
     */
    public double registerExit(Ticket ticket) {

        if (ticket == null || ticket.getExitTime() != null) {
            return 0;
        }

        // Reparar referencia antes de procesar salida
        repararReferenciaTicket(ticket);

        // Asignar hora de salida
        ticket.setExitTime(LocalDateTime.now());

        double total = calculateTotal(ticket);

        if (total <= 0) {
            return 0;
        }

        ticket.setTotal(total);

        boolean updated = ticketData.updateTicket(ticket);
        if (!updated) {
            return 0;
        }

        eliminarTicketActivo(ticket.getId());
        appendToRegistro(ticket, "SALIDA");

        // Obtener nombre del parqueo de manera segura
        String parkingName = getNombreParqueoForTicket(ticket);
        String vehicleType = "Desconocido";
        String plate = "N/A";
        String spaceId = "N/A";

        if (ticket.getSpace() != null) {
            spaceId = String.valueOf(ticket.getSpace().getId());
        }

        if (ticket.getVehicle() != null) {
            plate = ticket.getVehicle().getPlate();

            if (ticket.getVehicle().getVehicleType() != null) {
                vehicleType = ticket.getVehicle()
                        .getVehicleType()
                        .getDescription();
            }
        }

        TxtTicketUtil.generarTicketTXT(
                parkingName,
                vehicleType,
                plate,
                spaceId,
                total,
                ticket.getId()
        );

        return total;
    }

    /**
     * Elimina un ticket de los activos
     */
    private void eliminarTicketActivo(int ticketId) {
        TxtTicketUtil.eliminarTicketActivo(ticketId);
    }

    /**
     * Agrega información del ticket al archivo histórico.
     */
    private void appendToRegistro(Ticket ticket, String tipo) {

        String ruta = "data/tickets_registro.txt";

        try (FileWriter writer = new FileWriter(ruta, true)) {

            writer.write("===== TICKET " + tipo + " =====\n");
            writer.write(ticket.toString());
            writer.write("\n\n");

        } catch (IOException e) {
            System.err.println("Error al escribir en registro histórico");
        }
    }

    /**
     * Retorna los tickets activos.
     */
    public List<Ticket> getActiveTickets() {
        return ticketData.getActiveTickets();
    }

    /**
     * Busca un ticket por ID.
     */
    public Ticket findTicketById(int id) {
        Ticket ticket = ticketData.getAllTickets()
                .stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .orElse(null);

        if (ticket == null) {
            List<Ticket> historicalTickets = TxtTicketUtil.leerTicketsTXT();
            if (historicalTickets != null) {
                ticket = historicalTickets.stream()
                        .filter(t -> t.getId() == id)
                        .findFirst()
                        .orElse(null);
            }
        }

        // Reparar referencia si es necesario
        if (ticket != null) {
            repararReferenciaTicket(ticket);
        }

        return ticket;
    }

    /**
     * Retorna todos los tickets en memoria.
     */
    public List<Ticket> getAllTickets() {
        return ticketData.getAllTickets();
    }

    /**
     * Retorna todos los tickets almacenados en el archivo TXT histórico.
     */
    public List<Ticket> getAllTicketsFromTxt() {
        List<Ticket> ticketsFromTxt = TxtTicketUtil.leerTicketsTXT();
        return ticketsFromTxt != null ? ticketsFromTxt : List.of();
    }

    /**
     * Calcula el total usando las tarifas del sistema con tiempo de cortesía
     */
    private double calculateTotal(Ticket ticket) {
        System.out.println("--- calculateTotal ---");

        // Validaciones básicas
        if (ticket.getEntryTime() == null) {
            System.out.println("  EntryTime es null");
            return 0;
        }
        if (ticket.getExitTime() == null) {
            System.out.println("  ExitTime es null");
            return 0;
        }
        if (ticket.getSpace() == null) {
            System.out.println("  Space es null");
            return 0;
        }
        if (ticket.getSpace().getParkingLot() == null) {
            System.out.println("  Space.ParkingLot es null");
            return 0;
        }
        if (ticket.getVehicle() == null) {
            System.out.println("  Vehicle es null");
            return 0;
        }
        if (ticket.getVehicle().getVehicleType() == null) {
            System.out.println("  VehicleType es null");
            return 0;
        }

        // Calcular tiempo transcurrido
        long minutes = java.time.Duration
                .between(ticket.getEntryTime(), ticket.getExitTime())
                .toMinutes();

        System.out.println("  Minutos transcurridos: " + minutes);

        if (minutes <= 0) {
            System.out.println("  Minutos <= 0, no se cobra");
            return 0;
        }

        // TIEMPO DE CORTESÍA: 15 minutos (puedes ajustar este valor)
        final int TIEMPO_CORTESIA = 15;

        if (minutes <= TIEMPO_CORTESIA) {
            System.out.println("  Tiempo de cortesía (" + TIEMPO_CORTESIA + " min) - No se cobra");
            return 0;
        }

        // Restar el tiempo de cortesía para el cálculo
        long minutosACobrar = minutes - TIEMPO_CORTESIA;
        double horas = Math.ceil(minutosACobrar / 60.0);
        System.out.println("  Minutos a cobrar: " + minutosACobrar);
        System.out.println("  Horas a cobrar: " + horas);

        int parkingLotId = ticket.getSpace().getParkingLot().getId();
        String vehicleType = ticket.getVehicle().getVehicleType().getDescription();

        System.out.println("  parkingLotId: " + parkingLotId);
        System.out.println("  vehicleType: " + vehicleType);

        // OBTENER TARIFA DEL SISTEMA (no valores quemados)
        ParkingRate rate = rateController
                .getParkingRateByParkingLotAndType(parkingLotId, vehicleType);

        if (rate == null) {
            System.out.println("  ❌ ERROR: No hay tarifa configurada para este parqueo y tipo de vehículo");
            System.out.println("  Por favor, configure las tarifas en la sección de Tarifas");
            return 0; // No se puede cobrar sin tarifas
        }

        double precioPorHora = rate.getHourPrice();
        System.out.println("  Tarifa encontrada: ₡" + precioPorHora + " por hora");

        double total = horas * precioPorHora;
        System.out.println("  TOTAL CALCULADO: ₡" + total);

        return total;
    }
}
