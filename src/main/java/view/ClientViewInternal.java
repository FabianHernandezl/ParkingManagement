package view;

import controller.ClientController;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import model.entities.Client;

/**
 * Internal window for managing clients.
 *
 * Provides functionality to: - Register new clients - Update existing clients -
 * Delete clients - Display all clients in a table - Open TXT report file
 *
 * Uses ClientController to handle business logic.
 */
public class ClientViewInternal extends JInternalFrame {

    private final ClientController clientController = new ClientController();

    private JTextField txtId, txtPhone, txtName, txtEmail;
    private JCheckBox chkPreferential;
    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtSearch;

    private JButton btnSave, btnUpdate, btnDelete, btnClear, btnReport;

    public ClientViewInternal() {
        super("Gestión de Clientes", true, true, true, true);
        setSize(760, 560);
        setLayout(null);
        getContentPane().setBackground(UITheme.BACKGROUND);

        initInputs();
        initButtons();
        initTable();
        setupEvents();
        loadTable();
    }

    private void initInputs() {
        JPanel formPanel = new JPanel(null);
        formPanel.setBounds(20, 20, 260, 300);
        formPanel.setBackground(UITheme.PANEL_BG);
        formPanel.setBorder(UITheme.panelBorder());
        add(formPanel);

        JLabel title = new JLabel("Cliente");
        title.setFont(UITheme.TITLE_FONT);
        title.setBounds(10, 10, 200, 25);
        formPanel.add(title);

        JLabel lblId = new JLabel("Cedula:");
        lblId.setFont(UITheme.LABEL_FONT);
        lblId.setBounds(10, 50, 80, 25);
        formPanel.add(lblId);

        txtId = new JTextField();
        txtId.setBounds(90, 50, 150, 25);
        formPanel.add(txtId);

        JLabel lblName = new JLabel("Nombre:");
        lblName.setFont(UITheme.LABEL_FONT);
        lblName.setBounds(10, 85, 80, 25);
        formPanel.add(lblName);

        txtName = new JTextField();
        txtName.setBounds(90, 85, 150, 25);
        formPanel.add(txtName);

        JLabel lblPhone = new JLabel("Teléfono:");
        lblPhone.setFont(UITheme.LABEL_FONT);
        lblPhone.setBounds(10, 120, 80, 25);
        formPanel.add(lblPhone);

        txtPhone = new JTextField();
        txtPhone.setBounds(90, 120, 150, 25);
        formPanel.add(txtPhone);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(UITheme.LABEL_FONT);
        lblEmail.setBounds(10, 155, 80, 25);
        formPanel.add(lblEmail);

        txtEmail = new JTextField();
        txtEmail.setBounds(90, 155, 150, 25);
        formPanel.add(txtEmail);

        chkPreferential = new JCheckBox("Preferencial");
        chkPreferential.setBounds(10, 195, 150, 25);
        chkPreferential.setBackground(UITheme.PANEL_BG);
        formPanel.add(chkPreferential);
    }

    private void initButtons() {
        btnSave = new JButton("Guardar");
        btnSave.setBounds(30, 340, 110, 30);
        UITheme.styleButton(btnSave, UITheme.SUCCESS);
        add(btnSave);

        btnClear = new JButton("Limpiar");
        btnClear.setBounds(150, 340, 110, 30);
        UITheme.styleButton(btnClear, UITheme.SECONDARY);
        add(btnClear);

        btnUpdate = new JButton("Actualizar");
        btnUpdate.setBounds(30, 380, 110, 30);
        UITheme.styleButton(btnUpdate, UITheme.PRIMARY);
        btnUpdate.setEnabled(false);
        add(btnUpdate);

        btnDelete = new JButton("Eliminar");
        btnDelete.setBounds(150, 380, 110, 30);
        UITheme.styleButton(btnDelete, UITheme.DANGER);
        btnDelete.setEnabled(false);
        add(btnDelete);

        btnReport = new JButton("Abrir TXT");
        btnReport.setBounds(30, 420, 230, 35);
        UITheme.styleButton(btnReport, UITheme.WARNING);
        add(btnReport);
    }

    private void initTable() {

        JLabel lblSearch = new JLabel("Buscar:");
        lblSearch.setFont(UITheme.LABEL_FONT);
        lblSearch.setBounds(300, 5, 80, 20);
        add(lblSearch);

        txtSearch = new JTextField();
        txtSearch.setBounds(360, 0, 200, 25);
        add(txtSearch);

        model = new DefaultTableModel(
                new String[]{"Cedula", "Nombre", "Teléfono", "Email", "Preferencial"}, 0) {

            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        UITheme.styleTable(table);
        table.setRowHeight(28);

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                JLabel cell = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                cell.setOpaque(true);
                cell.setForeground(Color.BLACK);

                int modelRow = table.convertRowIndexToModel(row);
                boolean pref = model.getValueAt(modelRow, 4).toString().equals("Sí");

                if (isSelected) {
                    cell.setBackground(UITheme.PRIMARY);
                    cell.setForeground(Color.WHITE);
                } else {
                    cell.setBackground(pref ? new Color(220, 240, 220) : Color.WHITE);
                }

                return cell;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(300, 30, 440, 470);
        scroll.setBorder(UITheme.panelBorder());
        add(scroll);

        setupSearch();
    }

    private void setupSearch() {
        //Filtra por la primera y segunda columna
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {

            private void filter() {
                String text = txtSearch.getText().trim();

                if (text.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 0, 1));
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                filter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filter();
            }
        });
    }

    private void setupEvents() {

        btnSave.addActionListener(e -> saveClient());
        btnUpdate.addActionListener(e -> updateClient());
        btnDelete.addActionListener(e -> deleteClient());
        btnClear.addActionListener(e -> clearForm());
        btnReport.addActionListener(e -> openReport());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillForm();
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
            }
        });
    }

    private void loadTable() {
        model.setRowCount(0);
        for (Client c : clientController.getAllClients()) {
            model.addRow(new Object[]{
                c.getId(),
                c.getName(),
                c.getPhone(),
                c.getEmail(),
                c.isIsPreferential() ? "Sí" : "No"
            });
        }
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        if (row == -1) {
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);

        txtId.setText(model.getValueAt(modelRow, 0).toString());
        txtName.setText(model.getValueAt(modelRow, 1).toString());
        txtPhone.setText(model.getValueAt(modelRow, 2).toString());
        txtEmail.setText(model.getValueAt(modelRow, 3).toString());
        chkPreferential.setSelected(model.getValueAt(modelRow, 4).equals("Sí"));
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        chkPreferential.setSelected(false);
        table.clearSelection();
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    private void saveClient() {

        if (txtId.getText().isEmpty() || txtName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos");
            return;
        }

        if (!txtEmail.getText().isEmpty() && !txtEmail.getText().contains("@")) {
            JOptionPane.showMessageDialog(this, "Email inválido. Debe contener '@'");
            return;
        }

        String response = clientController.registerClient(
                txtId.getText(),
                txtName.getText(),
                txtPhone.getText(),
                chkPreferential.isSelected(),
                txtEmail.getText()
        );

        if (response.toLowerCase().contains("existe")) {

            int option = JOptionPane.showOptionDialog(
                    this,
                    response + "\n¿Desea cancelar el registro?",
                    "Cliente existente",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    new String[]{"Aceptar", "Cancelar"},
                    "Cancelar"
            );

            if (option == 0) {
                clearForm();
            }

        } else {

            JOptionPane.showMessageDialog(this, response);
            loadTable();
            clearForm();
        }
    }

    private void updateClient() {

        if (!txtEmail.getText().isEmpty() && !txtEmail.getText().contains("@")) {
            JOptionPane.showMessageDialog(this, "Email inválido. Debe contener '@'");
            return;
        }

        JOptionPane.showMessageDialog(this,
                clientController.updateClient(
                        txtId.getText(),
                        txtName.getText(),
                        txtPhone.getText(),
                        chkPreferential.isSelected(),
                        txtEmail.getText()));

        loadTable();
        clearForm();
    }

    private void deleteClient() {

        int option = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar este cliente?\nEsta acción no se puede deshacer.",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (option == JOptionPane.YES_OPTION) {

            JOptionPane.showMessageDialog(this,
                    clientController.deleteClient(txtId.getText()));

            loadTable();
            clearForm();
        }
    }

    private void openReport() {

        try {
            File file = new File("data/clients.txt");
            if (file.exists()) {
                Desktop.getDesktop().open(file);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "No se pudo abrir el archivo");
        }
    }
}
