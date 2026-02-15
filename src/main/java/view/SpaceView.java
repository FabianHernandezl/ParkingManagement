package view;

import Controller.ParkingLotController;
import Controller.SpaceController;
import Controller.TicketController;
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

                        TicketController ticketController = TicketController.getInstance();

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

    private void handleRegisterExit() {

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

        if (!space.isSpaceTaken()) {
            JOptionPane.showMessageDialog(
                    this,
                    "El espacio seleccionado está libre",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        TicketController ticketController = TicketController.getInstance();

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
                    "No se encontró ticket activo",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        new VehicleInfoDialog(parent, ticket).setVisible(true);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro que desea registrar la salida del vehículo?",
                "Confirmar salida",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        animateExit(selectedPanel, () -> {

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

            // 🔥 Actualizamos solo el modelo local
            space.setSpaceTaken(false);
            space.setVehicle(null);

            // 🔥 Actualizamos solo el panel
            selectedPanel.updateView();

            JOptionPane.showMessageDialog(
                    this,
                    "Salida registrada correctamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });
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
