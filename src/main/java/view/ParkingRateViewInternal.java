package view;

import model.data.ParkingLotData;
import model.entities.ParkingLot;
import model.entities.Space;
import model.entities.VehicleType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;

public class ParkingRateViewInternal extends JInternalFrame {

    private JComboBox<ParkingLot> cmbParkingLot;
    private JTable tblRates;
    private DefaultTableModel tableModel;

    private ParkingLotData parkingLotData = new ParkingLotData();
    private ParkingLot selectedParkingLot;

    public ParkingRateViewInternal() {
        super("Configuración de Tarifas", true, true, true, true);
        setSize(600, 400);
        setLayout(null);

        initComponents();
        loadParkingLots();
    }

    private void initComponents() {

        JLabel lblParking = new JLabel("Parqueo:");
        lblParking.setBounds(20, 20, 100, 25);
        add(lblParking);

        cmbParkingLot = new JComboBox<>();
        cmbParkingLot.setBounds(120, 20, 300, 25);
        cmbParkingLot.addActionListener(e -> loadRates());
        add(cmbParkingLot);

        tableModel = new DefaultTableModel(
                new Object[]{"Tipo de Vehículo", "Tarifa por Hora"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1; // solo la tarifa es editable
            }
        };

        tblRates = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(tblRates);
        scroll.setBounds(20, 70, 540, 220);
        add(scroll);

        JButton btnSave = new JButton("Guardar Tarifas");
        btnSave.setBounds(380, 310, 180, 30);
        btnSave.addActionListener(e -> saveRates());
        add(btnSave);
    }

    private void loadParkingLots() {
        cmbParkingLot.removeAllItems();
        for (ParkingLot p : parkingLotData.getAllParkingLots()) {
            cmbParkingLot.addItem(p);
        }
    }

    private void loadRates() {
        tableModel.setRowCount(0);

        selectedParkingLot = (ParkingLot) cmbParkingLot.getSelectedItem();
        if (selectedParkingLot == null) return;

        Map<Integer, VehicleType> vehicleTypes = new HashMap<>();

        for (Space s : selectedParkingLot.getSpaces()) {
            VehicleType vt = s.getVehicleType();
            vehicleTypes.putIfAbsent(vt.getId(), vt);
        }

        for (VehicleType vt : vehicleTypes.values()) {
            tableModel.addRow(new Object[]{
                    vt.getDescription(),
                    vt.getFee()
            });
        }
    }

    private void saveRates() {
        if (selectedParkingLot == null) return;

        try {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String desc = tableModel.getValueAt(i, 0).toString();
                float newFee = Float.parseFloat(tableModel.getValueAt(i, 1).toString());

                for (Space s : selectedParkingLot.getSpaces()) {
                    VehicleType vt = s.getVehicleType();
                    if (vt.getDescription().equals(desc)) {
                        vt.setFee(newFee);
                    }
                }
            }

            parkingLotData.updateParkingLot(selectedParkingLot);

            JOptionPane.showMessageDialog(this,
                    "Tarifas actualizadas correctamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Las tarifas deben ser numéricas",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
