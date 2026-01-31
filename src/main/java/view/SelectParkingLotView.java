/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import model.entities.ParkingLot;

/**
 *
 * @author jimen
 */
public class SelectParkingLotView extends JInternalFrame {

    private JComboBox<ParkingLot> cmbParking;
    private List<ParkingLot> parkingLots;
    private AdminMenu parent;

    public SelectParkingLotView(List<ParkingLot> parkingLots, AdminMenu parent) {
        this.parkingLots = parkingLots;
        this.parent = parent;

        setTitle("Seleccionar Parqueo");
        setClosable(true);
        setSize(400, 200);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));

        cmbParking = new JComboBox<>();
        for (ParkingLot p : parkingLots) {
            cmbParking.addItem(p);
        }

        JButton btnOpen = new JButton("Ver Espacios");
        btnOpen.addActionListener(e -> openSpaces());

        panel.add(cmbParking);
        panel.add(btnOpen);

        add(panel, BorderLayout.CENTER);
    }

    private void openSpaces() {
        ParkingLot selected = (ParkingLot) cmbParking.getSelectedItem();
        if (selected != null) {
            parent.openInternalFrame(new SpaceView(selected, parent));

            dispose();
        }
    }
}
