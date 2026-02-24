package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import model.data.ParkingLotData;
import model.entities.Clerk;
import model.entities.ParkingLot;

/**
 *
 * @author Zai
 */
public class SelectParkingLotViewClerk extends JInternalFrame {

    private JComboBox<ParkingLot> cmbParking;
    private ClerkMenu parent;
    private Clerk loggedClerk;

    public SelectParkingLotViewClerk(ClerkMenu parent, Clerk clerk) {
        this.parent = parent;
        this.loggedClerk = clerk;

        setTitle("Seleccionar Parqueo");
        setClosable(true);
        setSize(500, 250);
        setLayout(new BorderLayout(10, 10));

        initUI();
        refreshParkingLots();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("Seleccione el parqueo que desea ver");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setAlignmentX(CENTER_ALIGNMENT);

        cmbParking = new JComboBox<>();
        cmbParking.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cmbParking.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        cmbParking.setPreferredSize(new Dimension(400, 40));

        JButton btnOpen = new JButton("Ver Espacios");
        btnOpen.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnOpen.setPreferredSize(new Dimension(200, 40));
        btnOpen.setAlignmentX(CENTER_ALIGNMENT);
        btnOpen.addActionListener(e -> openSpaces());

        mainPanel.add(lblTitle);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(cmbParking);
        mainPanel.add(Box.createVerticalStrut(25));
        mainPanel.add(btnOpen);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void openSpaces() {
        ParkingLot selected = (ParkingLot) cmbParking.getSelectedItem();
        if (selected != null) {
            parent.openInternalFrame(new SpaceView(selected, parent));
            dispose();
        }
    }

    public void refreshParkingLots() {
        cmbParking.removeAllItems();

        List<ParkingLot> all = new ParkingLotData().getAllParkingLots();

        // Filtrar solo los parqueos asignados al clerk
        if (loggedClerk != null && loggedClerk.getParkingLot() != null) {
            all = all.stream()
                    .filter(p -> loggedClerk.getParkingLot().stream()
                    .anyMatch(assigned -> assigned.getId() == p.getId()
                    || (assigned.getName() != null
                    && assigned.getName().equalsIgnoreCase(p.getName()))))
                    .collect(Collectors.toList());
        }

        for (ParkingLot p : all) {
            cmbParking.addItem(p);
        }
    }
}
