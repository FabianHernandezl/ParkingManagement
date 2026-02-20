/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import controller.ParkingLotReportController;
import controller.ReportController;
import model.entities.ParkingLotReportRow;
import model.entities.IngresoReportRow;
import model.entities.TipoVehiculoReportRow;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.awt.Desktop;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ReportsCenterInternal extends JInternalFrame {

    private JTable table;
    private DefaultTableModel tableModel;

    private JComboBox<String> reportTypeFilter;
    private JComboBox<String> dynamicFilter;

    private ParkingLotReportController controller;
    private ReportController reportController;
    private List<ParkingLotReportRow> allRows;

    // Componentes para selección de fechas
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;
    private JSpinner startTimeSpinner;
    private JSpinner endTimeSpinner;

    public ReportsCenterInternal() {
        super("Centro de Reportes", true, true, true, true);
        setSize(1000, 600);
        setLayout(new BorderLayout());

        controller = new ParkingLotReportController();
        reportController = new ReportController();

        // ================== PANEL SUPERIOR ==================
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.setBackground(UITheme.PANEL_BG);

        JLabel titleLabel = new JLabel("Centro de Reportes");
        titleLabel.setFont(UITheme.TITLE_FONT);

        // Panel de filtros principales
        JPanel filtersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filtersPanel.setBackground(UITheme.PANEL_BG);

        reportTypeFilter = new JComboBox<>();
        reportTypeFilter.addItem("Ocupación por Parqueo");
        reportTypeFilter.addItem("Ingresos por Parqueo");
        reportTypeFilter.addItem("Tipo de Vehículo");
        reportTypeFilter.addItem("Tarifas");
        reportTypeFilter.addItem("Vehículos Estacionados");
        reportTypeFilter.addActionListener(e -> {
            loadTableData(); // Esto carga los datos y actualiza el filtro
            updateTableColumns();
        });

        dynamicFilter = new JComboBox<>();
        dynamicFilter.addItem("Todos");
        dynamicFilter.addActionListener(e -> applyFilter());

        filtersPanel.add(titleLabel);
        filtersPanel.add(new JLabel("Tipo de Reporte:"));
        filtersPanel.add(reportTypeFilter);
        filtersPanel.add(new JLabel("Filtro:"));
        filtersPanel.add(dynamicFilter);

        // ================== PANEL DE FECHAS ==================
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        datePanel.setBackground(UITheme.PANEL_BG);
        datePanel.setBorder(BorderFactory.createTitledBorder("Rango de Fechas"));

        // Spinners para fecha
        SpinnerDateModel startDateModel = new SpinnerDateModel();
        startDateSpinner = new JSpinner(startDateModel);
        startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "dd/MM/yyyy"));
        startDateSpinner.setValue(new java.util.Date());

        SpinnerDateModel endDateModel = new SpinnerDateModel();
        endDateSpinner = new JSpinner(endDateModel);
        endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "dd/MM/yyyy"));
        endDateSpinner.setValue(new java.util.Date());

        // Spinners para hora
        SpinnerDateModel startTimeModel = new SpinnerDateModel();
        startTimeSpinner = new JSpinner(startTimeModel);
        startTimeSpinner.setEditor(new JSpinner.DateEditor(startTimeSpinner, "HH:mm"));
        startTimeSpinner.setValue(java.sql.Time.valueOf("00:00:00"));

        SpinnerDateModel endTimeModel = new SpinnerDateModel();
        endTimeSpinner = new JSpinner(endTimeModel);
        endTimeSpinner.setEditor(new JSpinner.DateEditor(endTimeSpinner, "HH:mm"));
        endTimeSpinner.setValue(java.sql.Time.valueOf("23:59:00"));

        datePanel.add(new JLabel("Desde:"));
        datePanel.add(startDateSpinner);
        datePanel.add(new JLabel("Hora:"));
        datePanel.add(startTimeSpinner);
        datePanel.add(new JLabel("Hasta:"));
        datePanel.add(endDateSpinner);
        datePanel.add(new JLabel("Hora:"));
        datePanel.add(endTimeSpinner);

        // ================== PANEL DE BOTONES ==================
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setBackground(UITheme.PANEL_BG);

        JButton refreshBtn = new JButton("Refrescar");
        UITheme.styleButton(refreshBtn, UITheme.SECONDARY);
        refreshBtn.addActionListener(e -> loadTableData());

        JButton generatePdfBtn = new JButton("Generar PDF");
        UITheme.styleButton(generatePdfBtn, UITheme.PRIMARY);
        generatePdfBtn.addActionListener(e -> generatePDF());

        JButton generateExcelBtn = new JButton("Generar Excel");
        UITheme.styleButton(generateExcelBtn, UITheme.SUCCESS);
        generateExcelBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Funcionalidad en desarrollo",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        buttonsPanel.add(refreshBtn);
        buttonsPanel.add(generatePdfBtn);
        buttonsPanel.add(generateExcelBtn);

        // Ensamblar panel superior
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(UITheme.PANEL_BG);
        northPanel.add(filtersPanel, BorderLayout.NORTH);
        northPanel.add(datePanel, BorderLayout.CENTER);
        northPanel.add(buttonsPanel, BorderLayout.SOUTH);

        // ================== TABLA ==================
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        updateTableColumns();

        table = new JTable(tableModel);
        UITheme.styleTable(table);
        applyRowColors();

        JScrollPane scrollPane = new JScrollPane(table);

        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        loadTableData();
    }

    /**
     * Actualiza las columnas de la tabla según el reporte seleccionado
     */
    private void updateTableColumns() {
        String selectedReport = reportTypeFilter.getSelectedItem().toString();
        tableModel.setColumnCount(0);

        switch (selectedReport) {
            case "Ocupación por Parqueo":
                tableModel.setColumnIdentifiers(new Object[]{
                    "Parqueo", "Espacio", "Estado", "Tipo Vehículo", "Placa"
                });
                break;

            case "Ingresos por Parqueo":
                tableModel.setColumnIdentifiers(new Object[]{
                    "Parqueo", "Vehículos", "Total Recaudado", "Promedio", "Período"
                });
                break;

            case "Tipo de Vehículo":
                tableModel.setColumnIdentifiers(new Object[]{
                    "Tipo Vehículo", "Cantidad", "Porcentaje", "Período"
                });
                break;

            default:
                tableModel.setColumnIdentifiers(new Object[]{
                    "Información"
                });
        }
    }

    /**
     * Convierte los valores de los spinners a LocalDateTime
     */
    private LocalDateTime getStartDateTime() {
        java.util.Date date = (java.util.Date) startDateSpinner.getValue();
        java.util.Date time = (java.util.Date) startTimeSpinner.getValue();

        LocalDate localDate = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        LocalTime localTime = time.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime();

        return LocalDateTime.of(localDate, localTime);
    }

    private LocalDateTime getEndDateTime() {
        java.util.Date date = (java.util.Date) endDateSpinner.getValue();
        java.util.Date time = (java.util.Date) endTimeSpinner.getValue();

        LocalDate localDate = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        LocalTime localTime = time.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime();

        return LocalDateTime.of(localDate, localTime);
    }

    // ================== CARGAR DATOS ==================
    private void loadTableData() {
        tableModel.setRowCount(0);
        updateTableColumns();

        String selectedReport = reportTypeFilter.getSelectedItem().toString();

        switch (selectedReport) {
            case "Ocupación por Parqueo":
                allRows = controller.getOccupationReportRows();
                for (ParkingLotReportRow row : allRows) {
                    tableModel.addRow(new Object[]{
                        row.getParkingLotName(),
                        row.getSpaceNumber(),
                        row.getStatus(),
                        row.getVehicleType(),
                        row.getPlate()
                    });
                }
                break;

            case "Ingresos por Parqueo":
                LocalDateTime inicio = getStartDateTime();
                LocalDateTime fin = getEndDateTime();

                List<IngresoReportRow> ingresos = reportController.generarReporteIngresos(inicio, fin);

                for (IngresoReportRow row : ingresos) {
                    tableModel.addRow(new Object[]{
                        row.getParkingLotName(),
                        row.getCantidadVehiculos(),
                        String.format("₡%,.2f", row.getTotalRecaudado()),
                        String.format("₡%,.2f", row.getPromedioPorVehiculo()),
                        row.getFechaInicio() + " - " + row.getFechaFin()
                    });
                }
                break;

            case "Tipo de Vehículo":
                LocalDateTime inicioTipo = getStartDateTime();
                LocalDateTime finTipo = getEndDateTime();

                List<TipoVehiculoReportRow> tipos = reportController.generarReporteTipoVehiculo(inicioTipo, finTipo);

                for (TipoVehiculoReportRow row : tipos) {
                    tableModel.addRow(new Object[]{
                        row.getTipoVehiculo(),
                        row.getCantidad(),
                        String.format("%.2f %%", row.getPorcentaje()),
                        row.getFechaInicio() + " - " + row.getFechaFin()
                    });
                }
                break;

            case "Tarifas":
                tableModel.addRow(new Object[]{"Funcionalidad en desarrollo"});
                break;

            case "Vehículos Estacionados":
                tableModel.addRow(new Object[]{"Funcionalidad en desarrollo"});
                break;
        }

        // Actualizar filtro dinámico SIN recargar datos
        updateDynamicFilterOnly();
    }

    // ================== ACTUALIZAR FILTRO (SIN RECARGAR) ==================
    private void updateDynamicFilterOnly() {
        dynamicFilter.removeAllItems();
        dynamicFilter.addItem("Todos");

        String selectedReport = reportTypeFilter.getSelectedItem().toString();

        switch (selectedReport) {
            case "Ocupación por Parqueo":
                if (allRows != null) {
                    allRows.stream()
                            .map(ParkingLotReportRow::getParkingLotName)
                            .distinct()
                            .forEach(dynamicFilter::addItem);
                }
                break;

            case "Ingresos por Parqueo":
                // Para ingresos, obtener datos sin recargar la tabla
                LocalDateTime inicio = getStartDateTime();
                LocalDateTime fin = getEndDateTime();
                List<IngresoReportRow> ingresos = reportController.generarReporteIngresos(inicio, fin);
                ingresos.stream()
                        .map(IngresoReportRow::getParkingLotName)
                        .distinct()
                        .forEach(dynamicFilter::addItem);
                break;

            case "Tipo de Vehículo":
                dynamicFilter.addItem("Autos");
                dynamicFilter.addItem("Motos");
                dynamicFilter.addItem("Bicicletas");
                dynamicFilter.addItem("Pesados");
                break;
        }
    }

    // ================== APLICAR FILTRO ==================
    private void applyFilter() {
        Object selectedObj = dynamicFilter.getSelectedItem();
        if (selectedObj == null) {
            return;
        }

        String selected = selectedObj.toString();

        if (selected.equals("Todos")) {
            loadTableData(); // Solo aquí se recargan los datos
            return;
        }

        String selectedReport = reportTypeFilter.getSelectedItem().toString();
        tableModel.setRowCount(0);

        switch (selectedReport) {
            case "Ocupación por Parqueo":
                if (allRows != null) {
                    List<ParkingLotReportRow> filtered = allRows.stream()
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
                break;

            case "Ingresos por Parqueo":
                LocalDateTime inicio = getStartDateTime();
                LocalDateTime fin = getEndDateTime();

                List<IngresoReportRow> ingresos = reportController.generarReporteIngresos(inicio, fin);
                List<IngresoReportRow> filtered = ingresos.stream()
                        .filter(r -> r.getParkingLotName().equals(selected))
                        .collect(Collectors.toList());

                for (IngresoReportRow row : filtered) {
                    tableModel.addRow(new Object[]{
                        row.getParkingLotName(),
                        row.getCantidadVehiculos(),
                        String.format("₡%,.2f", row.getTotalRecaudado()),
                        String.format("₡%,.2f", row.getPromedioPorVehiculo()),
                        row.getFechaInicio() + " - " + row.getFechaFin()
                    });
                }
                break;

            case "Tipo de Vehículo":
                LocalDateTime inicioTipo = getStartDateTime();
                LocalDateTime finTipo = getEndDateTime();

                List<TipoVehiculoReportRow> tipos = reportController.generarReporteTipoVehiculo(inicioTipo, finTipo);
                List<TipoVehiculoReportRow> filteredTipos = tipos.stream()
                        .filter(r -> r.getTipoVehiculo().equals(selected))
                        .collect(Collectors.toList());

                for (TipoVehiculoReportRow row : filteredTipos) {
                    tableModel.addRow(new Object[]{
                        row.getTipoVehiculo(),
                        row.getCantidad(),
                        String.format("%.2f %%", row.getPorcentaje()),
                        row.getFechaInicio() + " - " + row.getFechaFin()
                    });
                }
                break;
        }
    }

    // ================== GENERAR PDF ==================
    private void generatePDF() {
        String selectedReport = reportTypeFilter.getSelectedItem().toString();
        LocalDateTime inicio = getStartDateTime();
        LocalDateTime fin = getEndDateTime();

        switch (selectedReport) {
            case "Ocupación por Parqueo":
                controller.generateOccupationReportForAll();
                JOptionPane.showMessageDialog(this,
                        "✅ Reporte de Ocupación generado correctamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                break;

            case "Ingresos por Parqueo":
                reportController.generarPDFIngresos(inicio, fin);
                JOptionPane.showMessageDialog(this,
                        "✅ Reporte de Ingresos generado correctamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                break;

            case "Tipo de Vehículo":
                reportController.generarPDFTipoVehiculo(inicio, fin);
                JOptionPane.showMessageDialog(this,
                        "✅ Reporte de Tipo de Vehículo generado correctamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                break;

            default:
                JOptionPane.showMessageDialog(this,
                        "❌ Reporte no disponible por el momento",
                        "Información",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
        }

        // Abrir carpeta automáticamente
        try {
            File reportsFolder = new File("reports");
            if (!reportsFolder.exists()) {
                reportsFolder.mkdirs();
            }
            Desktop.getDesktop().open(reportsFolder);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo abrir la carpeta automáticamente",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
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

                String selectedReport = reportTypeFilter.getSelectedItem().toString();

                switch (selectedReport) {
                    case "Ocupación por Parqueo":
                        if (table.getColumnCount() >= 5) {
                            String estado = table.getValueAt(row, 2) != null ? table.getValueAt(row, 2).toString() : "";
                            String tipo = table.getValueAt(row, 3) != null ? table.getValueAt(row, 3).toString() : "";

                            if (estado.equalsIgnoreCase("Ocupado")) {
                                c.setBackground(new Color(255, 230, 230));
                                c.setForeground(UITheme.DANGER);
                            } else if (tipo.equalsIgnoreCase("Discapacitado")) {
                                c.setBackground(new Color(255, 249, 196));
                                c.setForeground(new Color(130, 119, 23));
                            } else {
                                c.setBackground(new Color(232, 245, 233));
                                c.setForeground(new Color(27, 94, 32));
                            }
                        }
                        break;

                    case "Ingresos por Parqueo":
                        // Colores para ingresos (alternar)
                        if (row % 2 == 0) {
                            c.setBackground(new Color(240, 248, 255));
                        } else {
                            c.setBackground(Color.WHITE);
                        }
                        c.setForeground(Color.BLACK);
                        break;

                    case "Tipo de Vehículo":
                        // Colores por tipo de vehículo
                        if (table.getValueAt(row, 0) != null) {
                            String tipo = table.getValueAt(row, 0).toString();
                            switch (tipo) {
                                case "Autos":
                                    c.setBackground(new Color(227, 242, 253)); // Azul claro
                                    break;
                                case "Motos":
                                    c.setBackground(new Color(232, 245, 233)); // Verde claro
                                    break;
                                case "Bicicletas":
                                    c.setBackground(new Color(255, 249, 196)); // Amarillo claro
                                    break;
                                case "Pesados":
                                    c.setBackground(new Color(243, 229, 245)); // Morado claro
                                    break;
                            }
                        }
                        c.setForeground(Color.BLACK);
                        break;

                    default:
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                }

                return c;
            }
        });
    }
}
