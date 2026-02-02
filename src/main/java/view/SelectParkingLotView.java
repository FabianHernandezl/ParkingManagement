package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import javax.swing.*;
import model.entities.ParkingLot;

public class SelectParkingLotView extends JInternalFrame {

    private JComboBox<ParkingLot> cmbParking;
    private List<ParkingLot> parkingLots;
    private AdminMenu parent;

    public SelectParkingLotView(List<ParkingLot> parkingLots, AdminMenu parent) {
        this.parkingLots = parkingLots;
        this.parent = parent;

        setTitle("Seleccionar Parqueo");
        setClosable(true);
        setSize(500, 250);
        setLayout(new BorderLayout(10, 10));

        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("Seleccione el parqueo que desea ver");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setAlignmentX(CENTER_ALIGNMENT);

        cmbParking = new JComboBox<>();
        for (ParkingLot p : parkingLots) {
            cmbParking.addItem(p);
        }

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
}
