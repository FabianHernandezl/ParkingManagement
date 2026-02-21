package view;

import controller.ParkingLotController;
import controller.SpaceController;
import controller.TicketController;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.*;
import model.data.SpaceData;
import model.entities.ParkingLot;
import model.entities.Space;
import model.entities.Ticket;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Internal frame that displays and manages parking spaces of a selected
 * ParkingLot.
 *
 * Allows: - Viewing all spaces in grid format - Selecting a space - Viewing
 * vehicle information (double click) - Registering vehicle exit
 */
public class SpaceView extends JInternalFrame {

    private ParkingLot parkingLot;
    private AdminMenu parent;
    private JPanel spacesContainer;
    private SpacePanel selectedPanel;

    private ParkingLotController parkingLotController = new ParkingLotController();
    private SpaceController spaceController = new SpaceController();
    private TicketController ticketController = TicketController.getInstance();
    private SpaceData sd = new SpaceData();

    public SpaceView(ParkingLot parkingLot, AdminMenu parent) {
        this.parkingLot = parkingLot;
        this.parent = parent;

        setTitle("Espacios - " + (parkingLot != null ? parkingLot.getName() : ""));
        setClosable(true);
        setSize(900, 600);
        setLayout(new BorderLayout(10, 10));

        initUI();
        loadSpaces();
    }

    private void initUI() {

        getContentPane().setBackground(new Color(245, 247, 250));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblTitle = new JLabel(
                "Gestión de Espacios - " + parkingLot.getName());
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton btnRefresh = createModernButton(
                "Actualizar",
                new Color(52, 152, 219)
        );

        JButton btnChangeParking = createModernButton(
                "Cambiar",
                new Color(155, 89, 182)
        );

        JButton btnRegisterExit = createModernButton(
                "Registrar Salida",
                new Color(231, 76, 60)
        );

        btnRefresh.addActionListener(e -> loadSpaces());

        btnChangeParking.addActionListener(e -> {
            parent.openInternalFrame(new SelectParkingLotView(parent));
            dispose();
        });

        btnRegisterExit.addActionListener(e -> handleRegisterExit());

        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnChangeParking);
        buttonPanel.add(btnRegisterExit);

        header.add(lblTitle, BorderLayout.WEST);
        header.add(buttonPanel, BorderLayout.EAST);

        spacesContainer = new JPanel(new GridLayout(0, 6, 12, 12));
        spacesContainer.setBackground(new Color(245, 247, 250));
        spacesContainer.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JScrollPane scrollPane = new JScrollPane(spacesContainer);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(245, 247, 250));

        add(header, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadSpaces() {

        spacesContainer.removeAll();

        if (parkingLot == null) {
            return;
        }

        Space[] spaces = spaceController.getSpacesByParkingLot(parkingLot.getId());

        for (Space space : spaces) {
            if (space == null) {
                continue;
            }

            SpacePanel panel = new SpacePanel(space, this);

            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {

                    setSelectedPanel(panel);

                    if (e.getClickCount() == 2) {

                        Space sp = panel.getSpace();

                        if (!sp.isSpaceTaken()) {
                            JOptionPane.showMessageDialog(
                                    SpaceView.this,
                                    "Este espacio está libre",
                                    "Información",
                                    JOptionPane.INFORMATION_MESSAGE
                            );
                            return;
                        }

                        Ticket ticket = ticketController
                                .getActiveTickets()
                                .stream()
                                .filter(t -> t.getSpace() != null
                                && t.getSpace().getId() == sp.getId())
                                .findFirst()
                                .orElse(null);

                        if (ticket == null) {
                            JOptionPane.showMessageDialog(
                                    SpaceView.this,
                                    "No se encontró ticket activo",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE
                            );
                            return;
                        }

                        new VehicleInfoDialog(parent, ticket).setVisible(true);
                    }
                }
            });

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

    private void animateExit(SpacePanel panel, Runnable onFinish) {

        Timer timer = new Timer(150, null);
        final int[] count = {0};

        timer.addActionListener(e -> {

            if (count[0] % 2 == 0) {
                panel.setBackground(new Color(46, 204, 113));
            } else {
                panel.updateView();
            }

            count[0]++;

            if (count[0] == 6) {
                timer.stop();
                onFinish.run();
            }
        });

        timer.start();
    }

    /**
     * Maneja el evento de registro de salida del vehículo seleccionado
     */
    private void handleRegisterExit() {

        // Validar que haya un espacio seleccionado
        if (selectedPanel == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Debe seleccionar un espacio ocupado",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Space space = selectedPanel.getSpace();

        // Validar que el espacio esté ocupado
        if (!space.isSpaceTaken()) {
            JOptionPane.showMessageDialog(
                    this,
                    "El espacio seleccionado está libre",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Buscar el ticket activo para este espacio
        Ticket ticket = ticketController
                .getActiveTickets()
                .stream()
                .filter(t -> t.getSpace() != null
                && t.getSpace().getId() == space.getId())
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

        // Mostrar información del vehículo antes de confirmar
        int option = JOptionPane.showConfirmDialog(
                this,
                "Vehículo: " + ticket.getVehicle().getPlate() + "\n"
                + "Hora de ingreso: " + ticket.getEntryTime() + "\n\n"
                + "¿Desea registrar la salida de este vehículo?",
                "Confirmar salida",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            // Registrar la salida en TicketController (esto genera el ticket de salida automáticamente)
            double totalPagado = ticketController.registerExit(ticket);

            // Liberar el espacio
            boolean released = spaceController.releaseSpace(space.getId());

            if (!released) {
                JOptionPane.showMessageDialog(
                        this,
                        "Error al liberar el espacio",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Actualizar el estado del espacio localmente
            space.setSpaceTaken(false);
            space.setVehicle(null);

            // Animar y actualizar la vista
            animateExit(selectedPanel, () -> {
                selectedPanel.updateView();

                // Mostrar mensaje de éxito con el total pagado
                JOptionPane.showMessageDialog(
                        this,
                        "Salida registrada correctamente\n"
                        + "Total pagado: ₡" + String.format("%.2f", totalPagado) + "\n"
                        + "Ticket de salida generado en data/ticket_" + ticket.getId() + ".txt",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // Opcional: abrir el ticket automáticamente
                int abrirTicket = JOptionPane.showConfirmDialog(
                        this,
                        "¿Desea abrir el ticket de salida?",
                        "Abrir ticket",
                        JOptionPane.YES_NO_OPTION
                );

                if (abrirTicket == JOptionPane.YES_OPTION) {
                    try {
                        java.awt.Desktop.getDesktop().open(new java.io.File("data/ticket_" + ticket.getId() + ".txt"));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(
                                this,
                                "No se pudo abrir el ticket: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            });

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al registrar la salida: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }
    }

    private JButton createModernButton(String text, Color color) {

        JButton button = new JButton(text);

        button.setFocusPainted(false);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));

        button.setPreferredSize(new Dimension(150, 35));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });

        return button;
    }
}
