/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import Controller.ParkingLotReportController;
import model.entities.ParkingLotReportRow;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.table.DefaultTableCellRenderer;

public class ReportsCenterInternal extends JInternalFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> parkingFilter;
    private ParkingLotReportController controller;

    private List<ParkingLotReportRow> allRows;

    public ReportsCenterInternal() {
        super("Centro de Reportes", true, true, true, true);
        setSize(900, 500);
        setLayout(new BorderLayout());

        controller = new ParkingLotReportController();

        // ================== PANEL SUPERIOR ==================
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.setBackground(UITheme.PANEL_BG);

        JLabel titleLabel = new JLabel("Reporte de Ocupación de Parqueos");
        titleLabel.setFont(UITheme.TITLE_FONT);

        // ===== FILTRO =====
        parkingFilter = new JComboBox<>();
        parkingFilter.addItem("Todos");
        parkingFilter.addActionListener(e -> applyFilter());

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setBackground(UITheme.PANEL_BG);
        leftPanel.add(titleLabel);
        leftPanel.add(new JLabel("Parqueo:"));
        leftPanel.add(parkingFilter);

        // ===== BOTONES =====
        JButton refreshBtn = new JButton("Refrescar");
        UITheme.styleButton(refreshBtn, UITheme.SECONDARY);
        refreshBtn.addActionListener(e -> loadTableData());

        JButton generatePdfBtn = new JButton("Generar PDF");
        UITheme.styleButton(generatePdfBtn, UITheme.PRIMARY);
        generatePdfBtn.addActionListener(e -> {
            controller.generateOccupationReportForAll();
            JOptionPane.showMessageDialog(this,
                    "Reportes PDF generados correctamente");
        });

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setBackground(UITheme.PANEL_BG);
        buttonsPanel.add(refreshBtn);
        buttonsPanel.add(generatePdfBtn);

        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(buttonsPanel, BorderLayout.EAST);

        // ================== TABLA ==================
        tableModel = new DefaultTableModel(
                new Object[]{
                    "Parqueo",
                    "Espacio",
                    "Estado",
                    "Tipo Vehículo",
                    "Placa"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        UITheme.styleTable(table);
        applyRowColors();

        JScrollPane scrollPane = new JScrollPane(table);

        // ================== ADD ==================
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        loadTableData();
    }

    // ================== CARGAR DATOS ==================
    private void loadTableData() {
        tableModel.setRowCount(0);
        parkingFilter.removeAllItems();
        parkingFilter.addItem("Todos");

        allRows = controller.getOccupationReportRows();

        // Llenar combo de parqueos
        allRows.stream()
                .map(ParkingLotReportRow::getParkingLotName)
                .distinct()
                .forEach(parkingFilter::addItem);

        // Llenar tabla
        for (ParkingLotReportRow row : allRows) {
            tableModel.addRow(new Object[]{
                row.getParkingLotName(),
                row.getSpaceNumber(),
                row.getStatus(),
                row.getVehicleType(),
                row.getPlate()
            });
        }
    }

    // ================== FILTRO ==================
    private void applyFilter() {

        Object selectedObj = parkingFilter.getSelectedItem();

        // 🔒 Protección contra null (MUY IMPORTANTE)
        if (selectedObj == null || allRows == null) {
            return;
        }

        String selected = selectedObj.toString();

        tableModel.setRowCount(0);

        List<ParkingLotReportRow> filtered = selected.equals("Todos")
                ? allRows
                : allRows.stream()
                        .filter(r -> r.getParkingLotName().equals(selected))
                        .collect(Collectors.toList());

        for (ParkingLotReportRow row : filtered) {
            tableModel.addRow(new Object[]{
                row.getParkingLotName(),
                row.getSpaceNumber(),
                row.getStatus(),
                row.getVehicleType(),
                row.getPlate()
            });
        }
    }

    private void applyRowColors() {

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (isSelected) {
                    c.setBackground(UITheme.SECONDARY);
                    c.setForeground(Color.WHITE);
                    return c;
                }

                String estado = table.getValueAt(row, 2).toString(); // Estado
                String tipo = table.getValueAt(row, 3).toString();   // Tipo vehículo

                // 🔴 OCUPADO
                if (estado.equalsIgnoreCase("Ocupado")) {
                    c.setBackground(new Color(255, 230, 230));
                    c.setForeground(UITheme.DANGER);

                    // 🟨 PREFERENCIAL / DISCAPACIDAD
                } else if (tipo.equalsIgnoreCase("Discapacitado")) {
                    c.setBackground(new Color(255, 249, 196));
                    c.setForeground(new Color(130, 119, 23));

                    // 🟢 DISPONIBLE
                } else {
                    c.setBackground(new Color(232, 245, 233));
                    c.setForeground(new Color(27, 94, 32));
                }

                return c;
            }
        });
    }

}
