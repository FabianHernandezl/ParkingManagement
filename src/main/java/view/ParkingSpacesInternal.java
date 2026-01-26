package view;

import Controller.ParkingLotController;
import Controller.SpaceController;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;
import model.entities.ParkingLot;
import model.entities.Space;

public class ParkingSpacesInternal extends JInternalFrame {

    private ParkingLotController parkingLotController;
    private JComboBox<String> parkingLotCombo;
    private ParkingSpacesPanel spacesPanel;
    private SpaceInfoPanel infoPanel;

    public ParkingSpacesInternal() {
        super("Mapa de Parqueo", true, true, true, true);
        setSize(1000, 500);

        parkingLotController = new ParkingLotController();

        initComponents();
        loadParkingLots();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Panel superior para selección de parqueo
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Seleccionar Parqueo:"));

        parkingLotCombo = new JComboBox<>();
        parkingLotCombo.setPreferredSize(new Dimension(200, 30));
        parkingLotCombo.addActionListener(e -> loadSpacesForParkingLot());
        topPanel.add(parkingLotCombo);

        add(topPanel, BorderLayout.NORTH);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 0));

        infoPanel = new SpaceInfoPanel();
        infoPanel.setPreferredSize(new Dimension(200, 0));
        mainPanel.add(infoPanel, BorderLayout.WEST);

        spacesPanel = new ParkingSpacesPanel(null, infoPanel, new SpaceController());
        mainPanel.add(spacesPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Panel inferior para estadísticas
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(new JLabel("Seleccione un parqueo para ver los espacios"));
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadParkingLots() {
        parkingLotCombo.removeAllItems();
        ArrayList<ParkingLot> parkingLots = parkingLotController.getAllParkingLots();

        for (ParkingLot pl : parkingLots) {
            parkingLotCombo.addItem(pl.getId() + " - " + pl.getName());
        }

        if (parkingLots.isEmpty()) {
            parkingLotCombo.addItem("No hay parqueos registrados");
            parkingLotCombo.setEnabled(false);
        }
    }

    private void loadSpacesForParkingLot() {
        int selectedIndex = parkingLotCombo.getSelectedIndex();
        if (selectedIndex >= 0 && parkingLotCombo.isEnabled()) {
            String selected = (String) parkingLotCombo.getSelectedItem();
            int parkingLotId = Integer.parseInt(selected.split(" - ")[0]);

            // IMPORTANTE: Asegúrate de que findParkingLotById vuelva a leer el JSON
            ParkingLot parkingLot = parkingLotController.findParkingLotById(parkingLotId);

            if (parkingLot != null) {
                // Pasamos el arreglo directo (Space[])
                spacesPanel.setSpaces(parkingLot.getSpaces());
                // Forzamos al panel a redibujarse
                spacesPanel.revalidate();
                spacesPanel.repaint();
            }
        }
    }
}
