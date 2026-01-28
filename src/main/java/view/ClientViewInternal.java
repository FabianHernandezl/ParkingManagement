package view;

import Controller.ClientController;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import model.entities.Client;

/**
 *
 * @author Jimena
 */
public class ClientViewInternal extends JInternalFrame {

    private final ClientController clientController = new ClientController();
    private JTextField txtId, txtPhone, txtName;
    private JCheckBox chkPreferential;
    private JTable table;
    private DefaultTableModel model;
    private Client createdClient;

    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnSave;
    private JButton btnClear;
    private JButton btnReport;

    public ClientViewInternal() {
        super("Gestión de Clientes", true, true, true, true);
        setSize(750, 550);
        setLayout(null);
        getContentPane().setBackground(new Color(245, 245, 245));

        initInputs();

        initButtons();

        initTable();

        setupEvents();

        loadTable();
    }

    private void initInputs() {
        JLabel lblId = new JLabel("ID:");
        lblId.setBounds(30, 20, 80, 25);
        add(lblId);

        txtId = new JTextField();
        txtId.setBounds(120, 20, 150, 25);
        add(txtId);

        JLabel lblName = new JLabel("Nombre:");
        lblName.setBounds(30, 60, 80, 25);
        add(lblName);

        txtName = new JTextField();
        txtName.setBounds(120, 60, 150, 25);
        add(txtName);

        JLabel lblPhone = new JLabel("Teléfono:");
        lblPhone.setBounds(30, 100, 80, 25);
        add(lblPhone);

        txtPhone = new JTextField();
        txtPhone.setBounds(120, 100, 150, 25);
        add(txtPhone);

        chkPreferential = new JCheckBox("Cliente Preferencial");
        chkPreferential.setBounds(30, 140, 200, 30);
        chkPreferential.setOpaque(false);
        add(chkPreferential);
    }

    private void initButtons() {
        btnSave = new JButton("Guardar Nuevo");
        btnSave.setBounds(30, 190, 140, 30);
        btnSave.setBackground(new Color(40, 167, 69));
        btnSave.setForeground(Color.WHITE);
        add(btnSave);

        btnClear = new JButton("Limpiar");
        btnClear.setBounds(180, 190, 90, 30);
        btnClear.setBackground(new Color(108, 117, 125));
        btnClear.setForeground(Color.WHITE);
        add(btnClear);

        btnUpdate = new JButton("Actualizar");
        btnUpdate.setBounds(30, 230, 140, 30);
        btnUpdate.setBackground(new Color(0, 123, 255));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setEnabled(false);
        add(btnUpdate);

        btnDelete = new JButton("Eliminar");
        btnDelete.setBounds(180, 230, 90, 30);
        btnDelete.setBackground(new Color(220, 53, 69));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setEnabled(false);
        add(btnDelete);

        btnReport = new JButton("Abrir Reporte TXT");
        btnReport.setBounds(30, 280, 240, 35);
        btnReport.setBackground(new Color(255, 193, 7));
        add(btnReport);
    }

    private void initTable() {
        model = new DefaultTableModel(new String[]{"ID", "Nombre", "Teléfono", "Preferencial"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);

        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(52, 73, 94));
        table.getTableHeader().setForeground(Color.WHITE);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                String estado = table.getValueAt(row, 3).toString();

                if (estado.equals("Sí")) {
                    c.setBackground(new Color(210, 235, 210));
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }

                if (isSelected) {
                    c.setBackground(new Color(184, 218, 255));
                }

                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(300, 20, 410, 450);
        add(scrollPane);
    }

    private void setupEvents() {
        btnSave.addActionListener((ActionEvent e) -> saveClient());
        btnUpdate.addActionListener((ActionEvent e) -> updateClient());
        btnDelete.addActionListener((ActionEvent e) -> deleteClient());
        btnClear.addActionListener((ActionEvent e) -> clearForm());
        btnReport.addActionListener((ActionEvent e) -> openTextReport());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFormFromTable();
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
            }
        });
    }

    private void loadTable() {
        model.setRowCount(0);
        for (Client client : clientController.getAllClients()) {
            model.addRow(new Object[]{
                client.getId(),
                client.getName(),
                client.getPhone(),
                client.isIsPreferential() ? "Sí" : "No"
            });
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtPhone.setText("");
        chkPreferential.setSelected(false);
        table.clearSelection();
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        txtId.requestFocus();
    }

    private void openTextReport() {
        try {
            File file = new File("data/clients.txt");
            if (file.exists()) {
                Desktop.getDesktop().open(file);
            } else {
                JOptionPane.showMessageDialog(this, "El reporte no existe todavía.");
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al abrir archivo.");
        }
    }

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row == -1) {
            return;
        }
        txtId.setText(model.getValueAt(row, 0).toString());
        txtName.setText(model.getValueAt(row, 1).toString());
        txtPhone.setText(model.getValueAt(row, 2).toString());
        chkPreferential.setSelected(model.getValueAt(row, 3).toString().equals("Sí"));
    }

    private void saveClient() {
        String id = txtId.getText().trim();
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        boolean pref = chkPreferential.isSelected();
        if (id.isEmpty() || name.isEmpty()) {
            return;
        }

        String res = clientController.registerClient(id, name, phone, pref);
        JOptionPane.showMessageDialog(this, res);
        loadTable();
        clearForm();
    }

    private void updateClient() {
        String id = txtId.getText().trim();
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        boolean pref = chkPreferential.isSelected();

        if (id.isEmpty()) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "¿Desea actualizar al cliente " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String result = clientController.updateClient(id, name, phone, pref);
            JOptionPane.showMessageDialog(this, result);
            loadTable();
            clearForm();
        }
    }

    private void deleteClient() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar cliente " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            String result = clientController.deleteClient(id);
            JOptionPane.showMessageDialog(this, result);
            loadTable();
            clearForm();
        }
    }

    public Client selectOrCreateClient(JDesktopPane desktop) {

        String id = JOptionPane.showInputDialog(
                this,
                "Ingrese el ID del cliente:",
                "Buscar Cliente",
                JOptionPane.QUESTION_MESSAGE
        );

        if (id == null || id.trim().isEmpty()) {

            return null;

        }

        Client client = clientController.findClientById(id.trim());

        if (client != null) {

            txtId.setText(client.getId());

            txtName.setText(client.getName());

            txtPhone.setText(client.getPhone());

            chkPreferential.setSelected(client.isIsPreferential());

            JOptionPane.showMessageDialog(this,
                    "Cliente encontrado:\n\n"
                    + "ID: " + client.getId() + "\n"
                    + "Nombre: " + client.getName() + "\n"
                    + "Teléfono: " + client.getPhone() + "\n"
                    + "Preferencial: " + (client.isIsPreferential() ? "Sí" : "No"),
                    "Cliente Encontrado",
                    JOptionPane.INFORMATION_MESSAGE);

            for (java.awt.Component comp : getContentPane().getComponents()) {

                if (comp instanceof JButton) {

                    JButton btn = (JButton) comp;

                    if (btn.getText().equals("Actualizar") || btn.getText().equals("Eliminar")) {

                        btn.setEnabled(true);

                    }

                }

            }

            return client;

        } else {

            int option = JOptionPane.showConfirmDialog(this,
                    "No existe un cliente con ID: " + id + "\n¿Desea crearlo ahora?",
                    "Cliente No Encontrado",
                    JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.YES_OPTION) {

                if (!this.isVisible()) {

                    if (desktop != null) {

                        desktop.add(this);

                    }

                    this.setVisible(true);

                }

                try {

                    this.setSelected(true);

                    this.toFront();

                } catch (java.beans.PropertyVetoException e) {

                }

                txtId.setText(id);

                txtName.requestFocus();

                JOptionPane.showMessageDialog(this,
                        "Complete los datos del cliente y presione 'Guardar Nuevo'",
                        "Crear Nuevo Cliente",
                        JOptionPane.INFORMATION_MESSAGE);

            }

            return null;

        }

    }

    public Client getCreatedClient() {

        return createdClient;

    }

}
