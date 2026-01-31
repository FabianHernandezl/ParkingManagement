package view;

import Controller.ClientController;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import model.entities.Client;

public class ClientViewInternal extends JInternalFrame {

    private final ClientController clientController = new ClientController();

    private JTextField txtId, txtPhone, txtName;
    private JCheckBox chkPreferential;
    private JTable table;
    private DefaultTableModel model;

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

        JLabel title = new JLabel("👤 Cliente");
        title.setFont(UITheme.TITLE_FONT);
        title.setBounds(10, 10, 200, 25);
        formPanel.add(title);

        JLabel lblId = new JLabel("ID:");
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

        chkPreferential = new JCheckBox("Preferencial");
        chkPreferential.setBounds(10, 160, 150, 25);
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
        model = new DefaultTableModel(
                new String[]{"ID", "Nombre", "Teléfono", "Preferencial"}, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        UITheme.styleTable(table);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                boolean pref = table.getValueAt(row, 3).toString().equals("Sí");
                c.setBackground(pref ? new Color(210, 235, 210) : Color.WHITE);

                if (isSelected) {
                    c.setBackground(UITheme.SECONDARY);
                    c.setForeground(Color.WHITE);
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(300, 20, 440, 480);
        scroll.setBorder(UITheme.panelBorder());
        add(scroll);
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
                c.isIsPreferential() ? "Sí" : "No"
            });
        }
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        if (row == -1) {
            return;
        }

        txtId.setText(model.getValueAt(row, 0).toString());
        txtName.setText(model.getValueAt(row, 1).toString());
        txtPhone.setText(model.getValueAt(row, 2).toString());
        chkPreferential.setSelected(model.getValueAt(row, 3).equals("Sí"));
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtPhone.setText("");
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
        JOptionPane.showMessageDialog(this,
                clientController.registerClient(
                        txtId.getText(), txtName.getText(),
                        txtPhone.getText(), chkPreferential.isSelected()));
        loadTable();
        clearForm();
    }

    private void updateClient() {
        JOptionPane.showMessageDialog(this,
                clientController.updateClient(
                        txtId.getText(), txtName.getText(),
                        txtPhone.getText(), chkPreferential.isSelected()));
        loadTable();
        clearForm();
    }

    private void deleteClient() {
        JOptionPane.showMessageDialog(this,
                clientController.deleteClient(txtId.getText()));
        loadTable();
        clearForm();
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
