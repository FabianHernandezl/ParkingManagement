package view;

import controller.TicketController;
import controller.ParkingLotController;
import model.entities.Clerk;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import model.entities.Ticket;
import model.entities.ParkingLot;
import model.entities.Space;
import util.TxtTicketUtil;

public class TicketViewInternal extends JInternalFrame {

    private final TicketController ticketController = TicketController.getInstance();
    private final ParkingLotController parkingLotController = new ParkingLotController();
    private Clerk loggedClerk; // 🔥 NUEVO

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnRefresh;
    private JLabel lblStatus;
    private JTextArea detailArea;

    private static final DateTimeFormatter SHORT_FORMAT
            = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");

    private static final DateTimeFormatter FULL_FORMAT
            = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public TicketViewInternal() {
        this(null); // Admin ve todos los tickets
    }

    public TicketViewInternal(Clerk clerk) {

        super("Monitoreo de Tickets", true, true, true, true);

        this.loggedClerk = clerk; // 🔥 NUEVO

        setSize(900, 500);
        setLayout(new BorderLayout());

        initTopPanel();
        initTable();
        initBottomPanel();

        loadTickets();
        setVisible(true);

        new Timer(30000, e -> loadTickets()).start();
    }

    private void initTopPanel() {

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        btnRefresh = new JButton("Actualizar");
        btnRefresh.setBackground(new Color(0, 102, 204));
        btnRefresh.setForeground(Color.WHITE);

        lblStatus = new JLabel("Cargando tickets...");

        btnRefresh.addActionListener(e -> loadTickets());

        topPanel.add(btnRefresh);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(lblStatus);

        add(topPanel, BorderLayout.NORTH);
    }

    private void initTable() {

        String[] columnNames = {
            "ID", "Placa", "Tipo", "Parqueo", "Espacio",
            "Hora Ingreso", "Tiempo Transcurrido", "Estado"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value,
                    boolean isSelected, boolean hasFocus,
                    int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                String estado = table.getModel().getValueAt(row, 7).toString();
                c.setForeground("CERRADO".equals(estado) ? Color.GRAY : Color.BLACK);

                return c;
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateDetailArea();
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void initBottomPanel() {

        detailArea = new JTextArea(6, 80);
        detailArea.setEditable(false);
        detailArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(new JLabel("Detalles del ticket seleccionado:"), BorderLayout.NORTH);
        bottomPanel.add(new JScrollPane(detailArea), BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadTickets() {
        tableModel.setRowCount(0);

        // Usar el método que repara las referencias
        List<Ticket> allTickets = ticketController.getTodosLosTicketsConParqueo();

        int countActive = 0;

        for (Ticket ticket : allTickets) {

            String nombreParqueo = obtenerNombreParqueo(ticket);

// 🔥 FILTRO PARA CLERK CON LISTA DE PARQUEOS
            if (loggedClerk != null && loggedClerk.getParkingLot() != null) {

                boolean perteneceAlClerk = false;

                for (ParkingLot pl : loggedClerk.getParkingLot()) {
                    if (pl.getName().equals(nombreParqueo)) {
                        perteneceAlClerk = true;
                        break;
                    }
                }

                if (!perteneceAlClerk) {
                    continue; // No pertenece a ninguno de sus parqueos
                }
            }

            String placa = ticket.getVehicle() != null
                    ? ticket.getVehicle().getPlate() : "N/A";
            String tipo = (ticket.getVehicle() != null
                    && ticket.getVehicle().getVehicleType() != null)
                    ? ticket.getVehicle().getVehicleType().getDescription() : "N/A";
            String espacio = ticket.getSpace() != null
                    ? "Esp. " + ticket.getSpace().getId() : "No asignado";
            String horaEntrada = ticket.getEntryTime() != null
                    ? ticket.getEntryTime().format(SHORT_FORMAT) : "N/A";
            String tiempo = calculateElapsedTime(ticket);
            String estado = ticket.getExitTime() == null ? "ACTIVO" : "CERRADO";

            if ("ACTIVO".equals(estado)) {
                countActive++;
            }

            tableModel.addRow(new Object[]{
                ticket.getId(),
                placa,
                tipo,
                nombreParqueo,
                espacio,
                horaEntrada,
                tiempo,
                estado
            });
        }

        lblStatus.setText("Tickets activos: " + countActive);
    }

    /**
     * Obtiene el nombre del parqueo buscando en todas las fuentes posibles
     */
    private String obtenerNombreParqueo(Ticket ticket) {
        if (ticket == null) {
            return "N/A";
        }

        // Usar el método del controller que maneja la lógica compleja
        return ticketController.getNombreParqueoForTicket(ticket);
    }

    private String obtenerPlaca(Ticket ticket) {
        return ticket.getVehicle() != null ? ticket.getVehicle().getPlate() : "N/A";
    }

    private String obtenerTipoVehiculo(Ticket ticket) {
        if (ticket.getVehicle() != null && ticket.getVehicle().getVehicleType() != null) {
            return ticket.getVehicle().getVehicleType().getDescription();
        }
        return "N/A";
    }

    private String obtenerEspacio(Ticket ticket) {
        return ticket.getSpace() != null ? "Esp. " + ticket.getSpace().getId() : "No asignado";
    }

    private String obtenerHoraEntrada(Ticket ticket) {
        return ticket.getEntryTime() != null ? ticket.getEntryTime().format(SHORT_FORMAT) : "N/A";
    }

    private String calculateElapsedTime(Ticket ticket) {

        if (ticket.getEntryTime() == null || ticket.getExitTime() != null) {
            return "0:00";
        }

        Duration duration = Duration.between(
                ticket.getEntryTime(),
                LocalDateTime.now()
        );

        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;

        return String.format("%d:%02d", hours, minutes);
    }

    private void updateDetailArea() {

        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }

        int ticketId = Integer.parseInt(
                tableModel.getValueAt(selectedRow, 0).toString()
        );

        Ticket ticket = ticketController.findTicketById(ticketId);

        if (ticket == null) {
            return;
        }

        StringBuilder details = new StringBuilder();
        details.append("ID Ticket: ").append(ticket.getId()).append("\n");
        details.append("Parqueo: ").append(obtenerNombreParqueo(ticket)).append("\n");
        details.append("Placa: ").append(obtenerPlaca(ticket)).append("\n");
        details.append("Tipo: ").append(obtenerTipoVehiculo(ticket)).append("\n");
        details.append("Espacio: ").append(obtenerEspacio(ticket)).append("\n");
        details.append("Hora Ingreso: ").append(obtenerHoraEntradaCompleta(ticket)).append("\n");
        details.append("Hora Salida: ").append(obtenerHoraSalida(ticket)).append("\n");
        details.append(String.format("Total Pagado: ₡%.2f\n", ticket.getTotal()));

        detailArea.setText(details.toString());
    }

    private String obtenerHoraEntradaCompleta(Ticket ticket) {
        return ticket.getEntryTime() != null
                ? ticket.getEntryTime().format(FULL_FORMAT) : "N/A";
    }

    private String obtenerHoraSalida(Ticket ticket) {
        return ticket.getExitTime() != null
                ? ticket.getExitTime().format(FULL_FORMAT) : "No registrado";
    }
}
