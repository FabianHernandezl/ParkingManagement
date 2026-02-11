package view;

import Controller.ParkingLotController;
import Controller.SpaceController;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.*;
import model.entities.ParkingLot;
import model.entities.Space;

public class SpaceView extends JInternalFrame {

    private ParkingLot parkingLot;
    private AdminMenu parent;
    private JPanel spacesContainer;
    private SpacePanel selectedPanel;
    private ParkingLotController parkingLotController = new ParkingLotController();

    private SpaceController spaceController = new SpaceController();

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
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel lblTitle = new JLabel("Parqueo: " + (parkingLot != null ? parkingLot.getName() : ""));
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JButton btnRefresh = new JButton("Actualizar");
        btnRefresh.addActionListener(e -> loadSpaces());

        JButton btnChangeParking = new JButton("Cambiar Parqueo");
        btnChangeParking.addActionListener(e -> {
            // Obtener todos los parking lots desde el controlador
            parent.openInternalFrame(new SelectParkingLotView(
                    parkingLotController.getAllParkingLots(),
                    parent
            ));
            this.dispose();
        });

        JButton btnReleaseSpace = new JButton("Liberar Espacio");
        btnReleaseSpace.addActionListener(e -> {
            SpacePanel selected = getSelectedPanel();
            if (selected == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Seleccione un espacio para liberar",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            boolean success = spaceController.releaseSpace(selected.getSpace().getId());
            if (success) {
                JOptionPane.showMessageDialog(
                        this,
                        "El espacio #" + selected.getSpace().getId() + " ha sido liberado",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE
                );
                loadSpaces();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo liberar el espacio",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        topPanel.add(lblTitle);
        topPanel.add(btnRefresh);
        topPanel.add(btnChangeParking);
        topPanel.add(btnReleaseSpace);

        spacesContainer = new JPanel();
        spacesContainer.setLayout(new GridLayout(0, 5, 15, 15));
        spacesContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(spacesContainer);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadSpaces() {
        spacesContainer.removeAll();

        if (parkingLot == null) {
            return;
        }

        Space[] spaces = parkingLot.getSpaces();
        if (spaces != null) {
            for (Space space : spaces) {
                SpacePanel panel = new SpacePanel(space, this);
                spacesContainer.add(panel);
            }
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
}
