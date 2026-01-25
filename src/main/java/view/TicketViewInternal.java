package view;

import Controller.TicketController;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.format.DateTimeFormatter;
import model.entities.Ticket;
import model.entities.VehicleType;
import model.entities.Vehicle;
import model.entities.Space;

public class TicketViewInternal extends JInternalFrame {
    
    private final TicketController ticketController = TicketController.getInstance();
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnRefresh;
    private JButton btnRegisterExit;
    private JButton btnTestTicket;
    private JLabel lblStatus;
    
    public TicketViewInternal() {
        super("Tickets Activos", true, true, true, true);
        
        setSize(900, 500);
        setLayout(new BorderLayout());
        setVisible(true);
        
        // Panel superior con botones
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnRefresh = new JButton("Actualizar");
        btnRegisterExit = new JButton("Registrar Salida");
        btnTestTicket = new JButton("Crear Ticket Prueba");
        lblStatus = new JLabel("Cargando tickets...");
        
        topPanel.add(btnRefresh);
        topPanel.add(btnRegisterExit);
        topPanel.add(btnTestTicket);
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
        
        // Hacer la tabla más ancha
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(70);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);
        table.getColumnModel().getColumn(5).setPreferredWidth(120);
        table.getColumnModel().getColumn(6).setPreferredWidth(80);
        
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
        btnRefresh.addActionListener((ActionEvent e) -> {
            loadTickets();
        });
        
        btnRegisterExit.addActionListener((ActionEvent e) -> {
            registerExit();
        });
        
        btnTestTicket.addActionListener((ActionEvent e) -> {
            createTestTicket();
        });
        
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
        System.out.println("=== Cargando tickets activos desde la vista ===");
        
        try {
            tableModel.setRowCount(0);
            var activeTickets = ticketController.getActiveTickets();
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
            
            for (Ticket ticket : activeTickets) {
                String placa = "N/A";
                String tipo = "N/A";
                int espacio = 0;
                
                if (ticket.getVehicle() != null) {
                    placa = ticket.getVehicle().getPlate();
                    if (ticket.getVehicle().getVehicleType() != null) {
                        tipo = ticket.getVehicle().getVehicleType().getDescription();
                    }
                }
                
                if (ticket.getSpace() != null) {
                    espacio = ticket.getSpace().getId();
                }
                
                String horaEntrada = ticket.getEntryTime().format(formatter);
                
                // Calcular tiempo transcurrido
                long horas = java.time.Duration.between(ticket.getEntryTime(), 
                    java.time.LocalDateTime.now()).toHours();
                long minutos = java.time.Duration.between(ticket.getEntryTime(), 
                    java.time.LocalDateTime.now()).toMinutes() % 60;
                String tiempo = String.format("%d:%02d", horas, minutos);
                
                String estado = ticket.getExitTime() == null ? "ACTIVO" : "CERRADO";
                
                tableModel.addRow(new Object[]{
                    ticket.getId(),
                    placa,
                    tipo,
                    espacio,
                    horaEntrada,
                    tiempo,
                    estado
                });
            }
            
            lblStatus.setText("Tickets activos: " + activeTickets.size());
            
            if (activeTickets.isEmpty()) {
                System.out.println("No hay tickets activos para mostrar");
            }
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
            
            if (ticket != null) {
                StringBuilder details = new StringBuilder();
                details.append(String.format("ID Ticket: %d\n", ticket.getId()));
                details.append(String.format("Placa: %s\n", 
                    ticket.getVehicle() != null ? ticket.getVehicle().getPlate() : "N/A"));
                details.append(String.format("Tipo: %s\n", 
                    ticket.getVehicle() != null && ticket.getVehicle().getVehicleType() != null ? 
                    ticket.getVehicle().getVehicleType().getDescription() : "N/A"));
                details.append(String.format("Espacio: %d\n", 
                    ticket.getSpace() != null ? ticket.getSpace().getId() : 0));
                details.append(String.format("Hora Ingreso: %s\n", 
                    ticket.getEntryTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));
                
                if (ticket.getExitTime() != null) {
                    details.append(String.format("Hora Salida: %s\n", 
                        ticket.getExitTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));
                    details.append(String.format("Total Pagado: ₡%.2f\n", ticket.getTotal()));
                } else {
                    long horas = java.time.Duration.between(ticket.getEntryTime(), 
                        java.time.LocalDateTime.now()).toHours();
                    long minutos = java.time.Duration.between(ticket.getEntryTime(), 
                        java.time.LocalDateTime.now()).toMinutes() % 60;
                    details.append(String.format("Tiempo transcurrido: %d horas, %d minutos\n", horas, minutos));
                    
                    // Calcular tarifa estimada
                    if (ticket.getVehicle() != null && ticket.getVehicle().getVehicleType() != null) {
                        double tarifa = ticket.getVehicle().getVehicleType().getFee();
                        double estimado = horas * tarifa;
                        if (horas == 0) estimado = tarifa; // Mínimo 1 hora
                        details.append(String.format("Tarifa estimada: ₡%.2f\n", estimado));
                    }
                }
                
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
                "No se encontró el ticket seleccionado",
                "Error",
                JOptionPane.ERROR_MESSAGE);
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
            String.format("¿Registrar salida del vehículo?\n\n" +
                         "Placa: %s\n" +
                         "Espacio: %d\n" +
                         "Hora entrada: %s",
                         placa, espacio,
                         ticket.getEntryTime().format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"))),
            "Confirmar salida",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            double total = ticketController.registerExit(ticket);
            
            JOptionPane.showMessageDialog(this,
                String.format("Salida registrada exitosamente\n\n" +
                            "Total a pagar: ₡%.2f\n" +
                            "Ticket #%d cerrado",
                            total, ticket.getId()),
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Actualizar la tabla
            loadTickets();
        }
    }
    
    private void createTestTicket() {
        try {
            System.out.println("\n=== CREANDO TICKET DE PRUEBA ===");
            
            // Crear vehículo de prueba
            Vehicle testVehicle = new Vehicle();
            testVehicle.setPlate("TEST" + System.currentTimeMillis() % 1000);
            testVehicle.setColor("Rojo");
            testVehicle.setBrand("Toyota");
            testVehicle.setModel("Corolla");
            
            VehicleType vt = new VehicleType();
            vt.setId(1);
            vt.setDescription("Carro");
            vt.setFee((float) 1000.0);
            testVehicle.setVehicleType(vt);
            
            System.out.println("Vehículo creado: " + testVehicle.getPlate());
            
            // Crear espacio de prueba
            Space testSpace = new Space();
            testSpace.setId(999);
            testSpace.setSpaceTaken(false);
            testSpace.setVehicleType(vt);
            
            System.out.println("Espacio creado: ID " + testSpace.getId());
            
            // Crear ticket
            System.out.println("Llamando a TicketController...");
            Ticket testTicket = ticketController.generateEntryTicket(testVehicle, testSpace);
            
            if (testTicket != null) {
                System.out.println("✅ Ticket creado exitosamente!");
                
                JOptionPane.showMessageDialog(this,
                    "✅ Ticket de prueba creado exitosamente\n\n" +
                    "ID: " + testTicket.getId() + "\n" +
                    "Placa: " + testVehicle.getPlate() + "\n" +
                    "Espacio: " + testSpace.getId(),
                    "Prueba exitosa",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Recargar la tabla
                loadTickets();
            } else {
                System.out.println("❌ No se pudo crear ticket de prueba");
                
                JOptionPane.showMessageDialog(this,
                    "❌ No se pudo crear ticket de prueba",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "❌ Excepción al crear ticket:\n" + ex.getMessage(),
                "Excepción",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}