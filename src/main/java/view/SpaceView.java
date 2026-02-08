package view;

import Controller.ParkingLotController;
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

    public SpaceView(ParkingLot parkingLot, AdminMenu parent) {
        this.parkingLot = parkingLot;
        this.parent = parent;

        setTitle("Espacios - " + parkingLot.getName());
        setClosable(true);
        setSize(900, 600);
        setLayout(new BorderLayout(10, 10));

        initUI();
        loadSpaces();
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel lblTitle = new JLabel("Parqueo: " + parkingLot.getName());
        lblTitle.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 18));

        JButton btnRefresh = new JButton("Actualizar");
        btnRefresh.addActionListener(e -> loadSpaces());

        JButton btnChangeParking = new JButton("Cambiar Parqueo");
        btnChangeParking.addActionListener(e -> {
            parent.openInternalFrame(new SelectParkingLotView(
                    parkingLotController.getAllParkingLots(), 
                    parent
            ));
            this.dispose(); 
        });

        topPanel.add(lblTitle);
        topPanel.add(btnRefresh);
        topPanel.add(btnChangeParking); 

        spacesContainer = new JPanel();
        spacesContainer.setLayout(new GridLayout(0, 5, 15, 15));
        spacesContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(spacesContainer);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    
    public void loadSpaces() {
        spacesContainer.removeAll();

        ParkingLot fresh = parkingLotController.getParkingLotById(parkingLot.getId());

        if (fresh != null) {
            this.parkingLot = fresh;

            if (fresh.getSpaces() != null) {
                for (Space space : fresh.getSpaces()) {
                    SpacePanel panel = new SpacePanel(space, this);
                    spacesContainer.add(panel);
                }
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
        ParkingLot fresh = parkingLotController.getParkingLotById(parkingLot.getId());

        if (fresh == null || fresh.getSpaces() == null) {
            return;
        }

        for (Space s : fresh.getSpaces()) {
            if (s != null && s.getId() == spaceId) {
                if (s.isSpaceTaken()) {
                    JOptionPane.showMessageDialog(
                            this,
                            "⚠️ El espacio #" + spaceId + " ya está ocupado",
                            "Espacio ocupado",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }
                s.setSpaceTaken(true);
                break;
            }
        }

        this.parkingLot = fresh;
        loadSpaces();
    }

    public void refreshSpaces() {
        if (parkingLot == null) {
            return;
        }

        ParkingLot updated = parkingLotController.getParkingLotById(parkingLot.getId());

        if (updated != null) {
            this.parkingLot = updated;

            // recargar la UI
            loadSpaces();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo actualizar el parqueo",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

}
