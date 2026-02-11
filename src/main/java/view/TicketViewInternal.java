package view;

import Controller.TicketController;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import model.entities.Ticket;
import model.entities.Vehicle;
import model.entities.VehicleType;
import model.entities.Space;
import util.TxtTicketUtil;

public class TicketViewInternal extends JInternalFrame {

    private final TicketController ticketController = TicketController.getInstance();
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnRefresh;
    private JButton btnRegisterExit;
    private JLabel lblStatus;

    public TicketViewInternal() {
        super("Tickets Activos", true, true, true, true);

        setSize(900, 500);
        setLayout(new BorderLayout());
        setVisible(true);

        // Panel superior con botones
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnRefresh = new JButton("Actualizar");
        btnRefresh.setBackground(new Color(0, 102, 204)); // Azul
        btnRefresh.setForeground(Color.WHITE);
        btnRegisterExit = new JButton("Registrar Salida");
        btnRegisterExit.setBackground(new Color(204, 0, 0)); // Rojo
        btnRegisterExit.setForeground(Color.WHITE);

        lblStatus = new JLabel("Cargando tickets...");

        topPanel.add(btnRefresh);
        topPanel.add(btnRegisterExit);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(lblStatus);

        add(topPanel, BorderLayout.NORTH);

        // Tabla de tickets
        String[] columnNames = {
            "ID", "Placa", "Tipo", "Espacio",
            "Hora Ingreso", "Tiempo Transcurrido", "Estado"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return String.class;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Renderizador para cambiar el color según estado
        table.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String estado = table.getModel().getValueAt(row, 6).toString();
                if (estado.equals("CERRADO")) {
                    c.setForeground(Color.GRAY); // Cerrados en gris
                } else {
                    c.setForeground(Color.BLACK); // Activos en negro
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Panel inferior para detalles
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JTextArea detailArea = new JTextArea(6, 80);
        detailArea.setEditable(false);
        detailArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        bottomPanel.add(new JLabel("Detalles del ticket seleccionado:"), BorderLayout.NORTH);
        bottomPanel.add(new JScrollPane(detailArea), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Cargar datos iniciales
        loadTickets();

        // Event Listeners
        btnRefresh.addActionListener((ActionEvent e) -> loadTickets());
        btnRegisterExit.addActionListener((ActionEvent e) -> registerExit());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateDetailArea(detailArea);
            }
        });

        // Auto-refresh cada 30 segundos
        Timer refreshTimer = new Timer(30000, e -> loadTickets());
        refreshTimer.start();
    }

    private void loadTickets() {
        try {
            tableModel.setRowCount(0);

            ArrayList<Ticket> activeTickets = ticketController.getActiveTickets();
            ArrayList<Ticket> txtTickets = TxtTicketUtil.leerTicketsTXT();

            ArrayList<Ticket> allTickets = new ArrayList<>();
            allTickets.addAll(activeTickets);
            allTickets.addAll(txtTickets);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
            int countActive = 0;

            for (Ticket ticket : allTickets) {
                String placa = "N/A";
                String tipo = "N/A";
                String espacioInfo = "No asignado";

                if (ticket.getVehicle() != null) {
                    placa = ticket.getVehicle().getPlate() != null ? ticket.getVehicle().getPlate() : "N/A";
                    if (ticket.getVehicle().getVehicleType() != null) {
                        tipo = ticket.getVehicle().getVehicleType().getDescription();
                    }
                }

                if (ticket.getSpace() != null) {
                    espacioInfo = "Esp. " + ticket.getSpace().getId();
                }

                String horaEntrada = ticket.getEntryTime() != null
                        ? ticket.getEntryTime().format(formatter)
                        : "N/A";

                long horas = ticket.getEntryTime() != null
                        ? java.time.Duration.between(ticket.getEntryTime(),
                                java.time.LocalDateTime.now()).toHours()
                        : 0;
                long minutos = ticket.getEntryTime() != null
                        ? java.time.Duration.between(ticket.getEntryTime(),
                                java.time.LocalDateTime.now()).toMinutes() % 60
                        : 0;
                String tiempo = String.format("%d:%02d", horas, minutos);

                String estado = ticket.getExitTime() == null ? "ACTIVO" : "CERRADO";
                if (estado.equals("ACTIVO")) {
                    countActive++;
                }

                tableModel.addRow(new Object[]{
                    ticket.getId(),
                    placa,
                    tipo,
                    espacioInfo,
                    horaEntrada,
                    tiempo,
                    estado
                });
            }

            lblStatus.setText("Tickets activos: " + countActive);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error cargando tickets: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateDetailArea(JTextArea detailArea) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int ticketId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
            Ticket ticket = ticketController.findTicketById(ticketId);

            if (ticket == null) {
                for (Ticket t : TxtTicketUtil.leerTicketsTXT()) {
                    if (t.getId() == ticketId) {
                        ticket = t;
                        break;
                    }
                }
            }

            if (ticket != null) {
                StringBuilder details = new StringBuilder();
                details.append(String.format("ID Ticket: %d\n", ticket.getId()));
                details.append(String.format("Placa: %s\n",
                        ticket.getVehicle() != null ? ticket.getVehicle().getPlate() : "N/A"));
                details.append(String.format("Tipo: %s\n",
                        ticket.getVehicle() != null && ticket.getVehicle().getVehicleType() != null
                        ? ticket.getVehicle().getVehicleType().getDescription() : "N/A"));
                details.append("Espacio: " + (ticket.getSpace() != null ? ticket.getSpace().getId() : "No asignado") + "\n");
                details.append(String.format("Hora Ingreso: %s\n",
                        ticket.getEntryTime() != null ? ticket.getEntryTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) : "N/A"));
                details.append(String.format("Hora Salida: %s\n",
                        ticket.getExitTime() != null ? ticket.getExitTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) : "No registrado"));
                details.append(String.format("Total Pagado: ₡%.2f\n", ticket.getTotal()));
                detailArea.setText(details.toString());
            }
        }
    }

    private void registerExit() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione un ticket de la tabla",
                    "Selección requerida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int ticketId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        Ticket ticket = ticketController.findTicketById(ticketId);

        if (ticket == null) {
            JOptionPane.showMessageDialog(this,
                    "Este ticket ya fue cerrado o no existe en memoria",
                    "Ticket no disponible",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (ticket.getExitTime() != null) {
            JOptionPane.showMessageDialog(this,
                    "Este ticket ya tiene registrada una salida",
                    "Ticket ya cerrado",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String placa = ticket.getVehicle() != null ? ticket.getVehicle().getPlate() : "Desconocido";
        int espacio = ticket.getSpace() != null ? ticket.getSpace().getId() : 0;

        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("¿Registrar salida del vehículo?\n\n"
                        + "Placa: %s\n"
                        + "Espacio: %d\n"
                        + "Hora entrada: %s",
                        placa, espacio,
                        ticket.getEntryTime().format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"))),
                "Confirmar salida",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            double total = ticketController.registerExit(ticket);

            JOptionPane.showMessageDialog(this,
                    String.format("Salida registrada exitosamente\n\n"
                            + "Total a pagar: ₡%.2f\n"
                            + "Ticket #%d cerrado",
                            total, ticket.getId()),
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

            loadTickets();
        }
    }
}
