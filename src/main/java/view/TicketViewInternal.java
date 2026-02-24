package view;

import controller.TicketController;
import controller.ParkingLotController;
import model.entities.Clerk;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import model.entities.Ticket;
import model.entities.ParkingLot;
import model.entities.Space;
import util.TxtTicketUtil;

public class TicketViewInternal extends JInternalFrame {

    private final TicketController ticketController = TicketController.getInstance();
    private final ParkingLotController parkingLotController = new ParkingLotController();
    private Clerk loggedClerk;

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnRefresh;
    private JLabel lblStatus;
    private JTextArea detailArea;

    // 🔍 Componentes del buscador
    private JTextField searchField;
    private JComboBox<String> searchFilter;

    private static final DateTimeFormatter SHORT_FORMAT
            = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");

    private static final DateTimeFormatter FULL_FORMAT
            = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public TicketViewInternal() {
        this(null); // Admin ve todos los tickets
    }

    public TicketViewInternal(Clerk clerk) {

        super("Monitoreo de Tickets", true, true, true, true);

        this.loggedClerk = clerk;

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

        // 🔍 Panel de búsqueda
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        searchPanel.setBackground(topPanel.getBackground());

        searchField = new JTextField(15);
        searchField.setToolTipText("Buscar...");

        searchFilter = new JComboBox<>(new String[]{
            "Placa", "ID", "Parqueo", "Tipo", "Estado"
        });

        JButton btnSearch = new JButton("Buscar");
        btnSearch.setBackground(new Color(0, 153, 76));
        btnSearch.setForeground(Color.WHITE);

        JButton btnClear = new JButton("Limpiar");
        btnClear.setBackground(new Color(102, 102, 102));
        btnClear.setForeground(Color.WHITE);

        searchPanel.add(new JLabel("🔍"));
        searchPanel.add(searchFilter);
        searchPanel.add(searchField);
        searchPanel.add(btnSearch);
        searchPanel.add(btnClear);

        // Botón refresh
        btnRefresh = new JButton("Actualizar");
        btnRefresh.setBackground(new Color(0, 102, 204));
        btnRefresh.setForeground(Color.WHITE);

        lblStatus = new JLabel("Cargando tickets...");

        btnRefresh.addActionListener(e -> loadTickets());
        btnSearch.addActionListener(e -> searchTickets());
        btnClear.addActionListener(e -> {
            searchField.setText("");
            loadTickets();
        });

        // Búsqueda en tiempo real (opcional)
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (searchField.getText().isEmpty()) {
                    loadTickets();
                } else {
                    searchTickets();
                }
            }
        });

        topPanel.add(searchPanel);
        topPanel.add(Box.createHorizontalStrut(20));
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

            if (loggedClerk != null && loggedClerk.getParkingLot() != null) {

                boolean perteneceAlClerk = false;
                int parkingLotId = ticket.getParkingLot() != null
                        ? ticket.getParkingLot().getId() : 0;
                String parkingLotName = obtenerNombreParqueo(ticket);

                for (ParkingLot pl : loggedClerk.getParkingLot()) {
                    // Comparar por ID primero, si no está disponible comparar por nombre
                    boolean matchById = parkingLotId > 0 && pl.getId() == parkingLotId;
                    boolean matchByName = pl.getName() != null
                            && pl.getName().equalsIgnoreCase(parkingLotName);

                    if (matchById || matchByName) {
                        perteneceAlClerk = true;
                        break;
                    }
                }

                if (!perteneceAlClerk) {
                    continue;
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

    // 🔍 Método para buscar tickets
    private void searchTickets() {
        String searchText = searchField.getText().toLowerCase().trim();
        if (searchText.isEmpty()) {
            loadTickets();
            return;
        }

        String filter = searchFilter.getSelectedItem().toString();

        // Obtener todos los tickets
        List<Ticket> allTickets = ticketController.getTodosLosTicketsConParqueo();
        List<Ticket> filteredTickets = new ArrayList<>();

        for (Ticket ticket : allTickets) {

            // Aplicar filtro de clerk primero
            if (loggedClerk != null && loggedClerk.getParkingLot() != null) {
                boolean perteneceAlClerk = false;
                int parkingLotId = ticket.getParkingLot() != null ? ticket.getParkingLot().getId() : 0;
                String parkingLotName = obtenerNombreParqueo(ticket);

                for (ParkingLot pl : loggedClerk.getParkingLot()) {
                    boolean matchById = parkingLotId > 0 && pl.getId() == parkingLotId;
                    boolean matchByName = pl.getName() != null && pl.getName().equalsIgnoreCase(parkingLotName);
                    if (matchById || matchByName) {
                        perteneceAlClerk = true;
                        break;
                    }
                }
                if (!perteneceAlClerk) {
                    continue;
                }
            }

            // Aplicar filtro de búsqueda
            boolean matches = false;

            switch (filter) {
                case "Placa":
                    String placa = ticket.getVehicle() != null ? ticket.getVehicle().getPlate() : "";
                    matches = placa.toLowerCase().contains(searchText);
                    break;
                case "ID":
                    matches = String.valueOf(ticket.getId()).contains(searchText);
                    break;
                case "Parqueo":
                    String parqueo = obtenerNombreParqueo(ticket);
                    matches = parqueo.toLowerCase().contains(searchText);
                    break;
                case "Tipo":
                    String tipo = ticket.getVehicle() != null && ticket.getVehicle().getVehicleType() != null
                            ? ticket.getVehicle().getVehicleType().getDescription() : "";
                    matches = tipo.toLowerCase().contains(searchText);
                    break;
                case "Estado":
                    String estado = ticket.getExitTime() == null ? "activo" : "cerrado";
                    matches = estado.contains(searchText);
                    break;
            }

            if (matches) {
                filteredTickets.add(ticket);
            }
        }

        // Mostrar resultados
        tableModel.setRowCount(0);
        int countActive = 0;

        for (Ticket ticket : filteredTickets) {
            String nombreParqueo = obtenerNombreParqueo(ticket);
            String placa = ticket.getVehicle() != null ? ticket.getVehicle().getPlate() : "N/A";
            String tipo = (ticket.getVehicle() != null && ticket.getVehicle().getVehicleType() != null)
                    ? ticket.getVehicle().getVehicleType().getDescription() : "N/A";
            String espacio = ticket.getSpace() != null ? "Esp. " + ticket.getSpace().getId() : "No asignado";
            String horaEntrada = ticket.getEntryTime() != null ? ticket.getEntryTime().format(SHORT_FORMAT) : "N/A";
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

        lblStatus.setText("Resultados: " + filteredTickets.size() + " tickets | Activos: " + countActive);
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
