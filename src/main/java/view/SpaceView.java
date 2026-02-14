package view;

import Controller.ParkingLotController;
import Controller.SpaceController;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.*;
import model.data.SpaceData;
import model.entities.ParkingLot;
import model.entities.Space;
import Controller.TicketController;
import model.entities.Ticket;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SpaceView extends JInternalFrame {

    private ParkingLot parkingLot;
    private AdminMenu parent;
    private JPanel spacesContainer;
    private SpacePanel selectedPanel;
    private ParkingLotController parkingLotController = new ParkingLotController();
    private SpaceController spaceController = new SpaceController();
    private SpaceData sd = new SpaceData();

    public SpaceView(ParkingLot parkingLot, AdminMenu parent) {
        this.parkingLot = parkingLot;
        this.parent = parent;

        sd.debugPrintAllSpaces();

        setTitle("Espacios - " + (parkingLot != null ? parkingLot.getName() : ""));
        setClosable(true);
        setSize(900, 600);
        setLayout(new BorderLayout(10, 10));

        initUI();
        loadSpaces();
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel lblTitle = new JLabel("Parqueo: " + (parkingLot != null ? parkingLot.getName() : ""));
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JButton btnRefresh = new JButton("Actualizar");
        btnRefresh.addActionListener(e -> loadSpaces());

        JButton btnChangeParking = new JButton("Cambiar Parqueo");
        btnChangeParking.addActionListener(e -> {
            parent.openInternalFrame(new SelectParkingLotView(parent));

            this.dispose();
        });

        JButton btnRegisterExit = new JButton("Registrar Salida");
        btnRegisterExit.addActionListener(e -> {

            SpacePanel selected = getSelectedPanel();

            if (selected == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Seleccione un espacio",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            Space space = selected.getSpace();

            if (!space.isSpaceTaken()) {
                JOptionPane.showMessageDialog(
                        this,
                        "El espacio no está ocupado",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // Buscar ticket activo por vehículo
            TicketController ticketController = TicketController.getInstance();
            Ticket ticket = ticketController
                    .getActiveTickets()
                    .stream()
                    .filter(t -> t.getSpace() != null && t.getSpace().getId() == space.getId())
                    .findFirst()
                    .orElse(null);

            if (ticket == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "No se encontró ticket activo para este espacio",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "¿Registrar salida del vehículo con placa "
                    + ticket.getVehicle().getPlate() + "?",
                    "Confirmar salida",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {

                double total = ticketController.registerExit(ticket);

                animateExit(selectedPanel, () -> {

                    spaceController.releaseSpace(space.getId());

                    JOptionPane.showMessageDialog(
                            this,
                            "Salida registrada correctamente\n\n"
                            + "Total a pagar: ₡" + total,
                            "Salida exitosa",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    loadSpaces();
                });

            }
        });

        styleButton(btnRefresh, new Color(52, 152, 219));       // Azul
        styleButton(btnChangeParking, new Color(155, 89, 182)); // Morado
        styleButton(btnRegisterExit, new Color(231, 76, 60));   // Rojo elegante

        topPanel.add(lblTitle);
        topPanel.add(btnRefresh);
        topPanel.add(btnChangeParking);
        topPanel.add(btnRegisterExit);

        spacesContainer = new JPanel();
        spacesContainer.setLayout(new GridLayout(0, 5, 15, 15));
        spacesContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(spacesContainer);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void styleButton(JButton button, Color bgColor) {

        button.setFocusPainted(false);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }

    private void loadSpaces() {
        spacesContainer.removeAll();

        if (parkingLot == null) {
            return;
        }

        Space[] spaces = spaceController.getSpacesByParkingLot(parkingLot.getId());

        System.out.println("DEBUG: Cargando espacios para parqueo: " + parkingLot.getName());
        System.out.println("DEBUG: Espacios totales en el arreglo: " + spaces.length);

        for (int i = 0; i < spaces.length; i++) {
            Space space = spaces[i];
            if (space == null) {
                System.out.println("DEBUG: Espacio en posición " + i + " es null");
                continue;
            }
            System.out.println("DEBUG: Espacio #" + space.getId() + " | Ocupado: " + space.isSpaceTaken());
            SpacePanel panel = new SpacePanel(space, this);
            spacesContainer.add(panel);
        }

        spacesContainer.revalidate();
        spacesContainer.repaint();
    }

    public void setSelectedPanel(SpacePanel panel) {
        if (selectedPanel != null) {
            selectedPanel.setSelected(false);
        }
        selectedPanel = panel;
        if (selectedPanel != null) {
            selectedPanel.setSelected(true);
        }
    }

    public SpacePanel getSelectedPanel() {
        return selectedPanel;
    }

    public void markSpaceOccupied(int spaceId) {
        String success = spaceController.occupySpace(
                selectedPanel.getSpace().getClient(),
                selectedPanel.getSpace().getVehicle()
        );
        if (!success.equals("OK: Espacio asignado")) {
            JOptionPane.showMessageDialog(this,
                    success,
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
        }
        loadSpaces();
    }

    private void animateExit(SpacePanel panel, Runnable onFinish) {

        Timer timer = new Timer(150, null);
        final int[] count = {0};

        timer.addActionListener(e -> {

            if (count[0] % 2 == 0) {
                panel.setBackground(new Color(46, 204, 113)); // Verde
            } else {
                panel.setBackground(Color.WHITE);
            }

            count[0]++;

            if (count[0] == 6) { // 3 parpadeos
                timer.stop();
                onFinish.run();
            }
        });

        timer.start();
    }

    public void showVehicleInfo(SpacePanel panel) {

        Space space = panel.getSpace();

        if (!space.isSpaceTaken()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Este espacio está libre",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        TicketController ticketController = TicketController.getInstance();

        Ticket ticket = ticketController
                .getActiveTickets()
                .stream()
                .filter(t -> t.getSpace() != null && t.getSpace().getId() == space.getId())
                .findFirst()
                .orElse(null);

        if (ticket == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se encontró ticket activo",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        new VehicleInfoDialog(parent, ticket).setVisible(true);
    }

}
