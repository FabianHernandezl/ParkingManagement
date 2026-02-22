package view;

import controller.ParkingLotController;
import controller.ParkingRateController;
import model.entities.ParkingLot;
import model.entities.ParkingRate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class ParkingRateViewInternal extends JInternalFrame {

    private final ParkingRateController rateController = new ParkingRateController();
    private final ParkingLotController parkingController = new ParkingLotController();

    private JComboBox<ParkingLot> cmbParkingLot;
    private JComboBox<String> cmbVehicleType;
    private JTextField txtHour, txtHalfHour, txtDay, txtWeek, txtMonth, txtYear;
    private JTable table;
    private DefaultTableModel model;

    private JButton btnSave, btnUpdate, btnDelete, btnClear, btnLoadRates;
    private ParkingLot currentParkingLot;

    public ParkingRateViewInternal() {
        super("Gestión de Tarifas por Parqueo", true, true, true, true);
        setSize(920, 640);
        setLayout(null);
        getContentPane().setBackground(UITheme.BACKGROUND);

        initForm();
        initTable();
        loadParkingLots();    // Primero cargar datos
        loadVehicleTypes();   // Luego cargar tipos
        setupEvents();

        if (cmbParkingLot.getItemCount() > 0) {
            currentParkingLot = (ParkingLot) cmbParkingLot.getSelectedItem();
            setFormEnabled(true);
            loadTableForParkingLot(currentParkingLot);
        } else {
            setFormEnabled(false);
        }
    }

    private void initForm() {
        JPanel panel = new JPanel(null);
        panel.setBounds(20, 20, 320, 580);
        panel.setBackground(UITheme.PANEL_BG);
        panel.setBorder(UITheme.panelBorder());
        add(panel);

        JLabel title = new JLabel("💰 Tarifas por Parqueo");
        title.setFont(UITheme.TITLE_FONT);
        title.setBounds(10, 10, 250, 25);
        panel.add(title);

        int y = 50;

        // Combo de parqueo
        panel.add(label("Parqueo:", y));
        cmbParkingLot = new JComboBox<>();
        cmbParkingLot.setBounds(100, y, 200, 25);
        panel.add(cmbParkingLot);

        y += 35;

        // Botón cargar tarifas
        btnLoadRates = new JButton("Cargar Tarifas");
        btnLoadRates.setBounds(100, y, 200, 30);
        UITheme.styleButton(btnLoadRates, UITheme.PRIMARY);
        panel.add(btnLoadRates);

        y += 45;

        // Combo de tipo de vehículo
        panel.add(label("Tipo:", y));
        cmbVehicleType = new JComboBox<>();
        cmbVehicleType.setBounds(100, y, 200, 25);
        panel.add(cmbVehicleType);

        y += 40;

        // Campos de precios
        panel.add(label("Hora:", y));
        txtHour = field(panel, y);

        y += 35;
        panel.add(label("Media hora:", y));
        txtHalfHour = field(panel, y);

        y += 35;
        panel.add(label("Día:", y));
        txtDay = field(panel, y);

        y += 35;
        panel.add(label("Semana:", y));
        txtWeek = field(panel, y);

        y += 35;
        panel.add(label("Mes:", y));
        txtMonth = field(panel, y);

        y += 35;
        panel.add(label("Año:", y));
        txtYear = field(panel, y);

        // Botones en grilla 2x2
        int btnY = y + 50;
        int btnWidth = 140;
        int btnHeight = 40;
        int gap = 10;

        btnSave = actionGrid(panel, "Guardar", UITheme.SUCCESS, 10, btnY, btnWidth, btnHeight);
        btnClear = actionGrid(panel, "Limpiar", UITheme.SECONDARY, 10 + btnWidth + gap, btnY, btnWidth, btnHeight);
        btnUpdate = actionGrid(panel, "Actualizar", UITheme.PRIMARY, 10, btnY + btnHeight + gap, btnWidth, btnHeight);
        btnDelete = actionGrid(panel, "Eliminar", UITheme.DANGER, 10 + btnWidth + gap, btnY + btnHeight + gap, btnWidth, btnHeight);

        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        setFormEnabled(false);
    }

    private void initTable() {
        model = new DefaultTableModel(
                new String[]{"Parqueo", "Tipo", "Hora", "Media Hora", "Día", "Semana", "Mes", "Año"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        UITheme.styleTable(table);

        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(2).setPreferredWidth(70);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(70);
        table.getColumnModel().getColumn(5).setPreferredWidth(80);
        table.getColumnModel().getColumn(6).setPreferredWidth(70);
        table.getColumnModel().getColumn(7).setPreferredWidth(70);

        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(360, 20, 530, 580);
        sp.setBorder(UITheme.panelBorder());
        add(sp);
    }

    private void setupEvents() {
        btnSave.addActionListener(e -> save());
        btnUpdate.addActionListener(e -> update());
        btnDelete.addActionListener(e -> delete());
        btnClear.addActionListener(e -> clear());

        btnLoadRates.addActionListener(e -> {
            currentParkingLot = (ParkingLot) cmbParkingLot.getSelectedItem();
            if (currentParkingLot != null) {
                loadTableForParkingLot(currentParkingLot);
                setFormEnabled(true);
                clearForm();
            }
        });

        cmbParkingLot.addActionListener(e -> {
            currentParkingLot = (ParkingLot) cmbParkingLot.getSelectedItem();
            if (currentParkingLot != null) {
                setFormEnabled(true);
                clearForm();
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                fillForm();
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
                cmbVehicleType.setEnabled(false);
                cmbParkingLot.setEnabled(false);
            }
        });
    }

    private void loadParkingLots() {
        cmbParkingLot.removeAllItems();
        for (ParkingLot p : parkingController.getAllParkingLots()) {
            cmbParkingLot.addItem(p);
        }
    }

    private void loadVehicleTypes() {
        cmbVehicleType.removeAllItems();
        for (String type : rateController.getAvailableVehicleTypes()) {
            cmbVehicleType.addItem(type);
        }
    }

    private void loadTableForParkingLot(ParkingLot parkingLot) {
        model.setRowCount(0);
        ArrayList<ParkingRate> rates = rateController.getParkingRatesByParkingLot(parkingLot);

        for (ParkingRate rate : rates) {
            model.addRow(new Object[]{
                rate.getParkingLotName(),
                rate.getVehicleType(),
                String.format("₡%.2f", rate.getHourPrice()),
                String.format("₡%.2f", rate.getHalfHourPrice()),
                String.format("₡%.2f", rate.getDayPrice()),
                String.format("₡%.2f", rate.getWeekPrice()),
                String.format("₡%.2f", rate.getMonthPrice()),
                String.format("₡%.2f", rate.getYearPrice())
            });
        }
    }

    private void fillForm() {
        int r = table.getSelectedRow();
        if (r == -1) {
            return;
        }

        String parkingName = model.getValueAt(r, 0).toString();
        String vehicleType = model.getValueAt(r, 1).toString();

        // Buscar el ParkingLot por nombre
        for (int i = 0; i < cmbParkingLot.getItemCount(); i++) {
            ParkingLot p = cmbParkingLot.getItemAt(i);
            if (p.getName().equals(parkingName)) {
                cmbParkingLot.setSelectedItem(p);
                currentParkingLot = p;
                break;
            }
        }

        cmbVehicleType.setSelectedItem(vehicleType);

        ParkingRate rate = rateController.getParkingRateByParkingLotAndType(
                currentParkingLot.getId(), vehicleType);

        if (rate != null) {
            txtHour.setText(String.valueOf(rate.getHourPrice()));
            txtHalfHour.setText(String.valueOf(rate.getHalfHourPrice()));
            txtDay.setText(String.valueOf(rate.getDayPrice()));
            txtWeek.setText(String.valueOf(rate.getWeekPrice()));
            txtMonth.setText(String.valueOf(rate.getMonthPrice()));
            txtYear.setText(String.valueOf(rate.getYearPrice()));
        }
    }

    private void clear() {
        clearForm();
        table.clearSelection();
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        cmbParkingLot.setEnabled(true);
    }

    private void clearForm() {
        txtHour.setText("");
        txtHalfHour.setText("");
        txtDay.setText("");
        txtWeek.setText("");
        txtMonth.setText("");
        txtYear.setText("");

        if (cmbVehicleType.getItemCount() > 0) {
            cmbVehicleType.setSelectedIndex(0);
            cmbVehicleType.setEnabled(true);
        }
    }

    private void save() {
        if (currentParkingLot == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un parqueo");
            return;
        }

        ParkingRate rate = build();
        if (rate != null) {
            String result = rateController.insertParkingRate(rate);
            JOptionPane.showMessageDialog(this, result);
            loadTableForParkingLot(currentParkingLot);
            clear();
        }
    }

    private void update() {
        if (currentParkingLot == null) {
            JOptionPane.showMessageDialog(this, "No hay tarifa seleccionada");
            return;
        }

        ParkingRate rate = build();
        if (rate != null) {
            String result = rateController.updateParkingRate(rate);
            JOptionPane.showMessageDialog(this, result);
            loadTableForParkingLot(currentParkingLot);
            clear();
        }
    }

    private void delete() {
        if (currentParkingLot == null || cmbVehicleType.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "No hay tarifa seleccionada");
            return;
        }

        String vehicleType = cmbVehicleType.getSelectedItem().toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar tarifa para " + vehicleType + "?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            String result = rateController.deleteParkingRate(currentParkingLot.getId(), vehicleType);
            JOptionPane.showMessageDialog(this, result);
            loadTableForParkingLot(currentParkingLot);
            clear();
        }
    }

    private ParkingRate build() {
        try {
            if (currentParkingLot == null) {
                return null;
            }
            if (cmbVehicleType.getSelectedItem() == null) {
                return null;
            }

            String vehicleType = cmbVehicleType.getSelectedItem().toString();

            double hour = Double.parseDouble(txtHour.getText().trim());
            double halfHour = Double.parseDouble(txtHalfHour.getText().trim());
            double day = Double.parseDouble(txtDay.getText().trim());
            double week = Double.parseDouble(txtWeek.getText().trim());
            double month = Double.parseDouble(txtMonth.getText().trim());
            double year = Double.parseDouble(txtYear.getText().trim());

            if (hour <= 0 || halfHour <= 0 || day <= 0 || week <= 0 || month <= 0 || year <= 0) {
                JOptionPane.showMessageDialog(this, "Los precios deben ser > 0");
                return null;
            }

            return new ParkingRate(
                    currentParkingLot.getId(),
                    currentParkingLot.getName(),
                    vehicleType,
                    hour, halfHour, day, week, month, year
            );

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese valores numéricos");
            return null;
        }
    }

    private void setFormEnabled(boolean enabled) {
        cmbVehicleType.setEnabled(enabled);
        txtHour.setEnabled(enabled);
        txtHalfHour.setEnabled(enabled);
        txtDay.setEnabled(enabled);
        txtWeek.setEnabled(enabled);
        txtMonth.setEnabled(enabled);
        txtYear.setEnabled(enabled);
        btnSave.setEnabled(enabled);
        btnClear.setEnabled(enabled);
        btnLoadRates.setEnabled(enabled);
    }

    // ============= MÉTODOS HELPER (IGUAL A VehicleViewInternal) =============
    private JLabel label(String text, int y) {
        JLabel l = new JLabel(text);
        l.setBounds(10, y, 80, 25);
        l.setFont(UITheme.LABEL_FONT);
        return l;
    }

    private JTextField field(JPanel p, int y) {
        JTextField t = new JTextField();
        t.setBounds(100, y, 200, 25);
        p.add(t);
        return t;
    }

    private JButton actionGrid(JPanel p, String text, Color c, int x, int y, int width, int height) {
        JButton b = new JButton(text);
        b.setBounds(x, y, width, height);
        UITheme.styleButton(b, c);
        p.add(b);
        return b;
    }
}
