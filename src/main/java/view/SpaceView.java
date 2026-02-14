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
}
