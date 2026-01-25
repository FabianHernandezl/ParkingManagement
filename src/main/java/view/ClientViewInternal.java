package view;

import Controller.ClientController;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import model.entities.Client;

public class ClientViewInternal extends JInternalFrame {

    private final ClientController clientController = new ClientController();
    private Client createdClient; // cliente creado recientemente
    private JTextField txtId;
    private JTextField txtPhone;
    private JTextField txtName;
    private JCheckBox chkPreferential;
    private JTable table;
    private DefaultTableModel model;

    public ClientViewInternal() {
        super("Gestion de Clientes", true, true, true, true);

        setSize(700, 500);
        setLayout(null);
        setVisible(true);

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

        chkPreferential = new JCheckBox("Preferencial");
        chkPreferential.setBounds(30, 140, 150, 30);
        add(chkPreferential);

        JButton btnSave = new JButton("Guardar Nuevo");
        btnSave.setBounds(30, 190, 140, 30);
        add(btnSave);

        JButton btnClear = new JButton("Limpiar");
        btnClear.setBounds(180, 190, 90, 30);
        add(btnClear);

        JButton btnUpdate = new JButton("Actualizar");
        btnUpdate.setBounds(30, 230, 140, 30);
        btnUpdate.setEnabled(false); // Inicialmente deshabilitado
        add(btnUpdate);

        JButton btnDelete = new JButton("Eliminar");
        btnDelete.setBounds(180, 230, 90, 30);
        btnDelete.setEnabled(false); // Inicialmente deshabilitado
        add(btnDelete);

        model = new DefaultTableModel(new String[]{"ID", "Nombre", "Teléfono", "Preferencial"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);

        table.getColumnModel().getColumn(0).setPreferredWidth(80);  // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Nombre
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Teléfono
        table.getColumnModel().getColumn(3).setPreferredWidth(80);  // Preferencial

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(300, 20, 370, 400);
        add(scrollPane);

        loadTable();

        btnSave.addActionListener((ActionEvent e) -> saveClient());
        btnUpdate.addActionListener((ActionEvent e) -> updateClient());
        btnDelete.addActionListener((ActionEvent e) -> deleteClient());
        btnClear.addActionListener((ActionEvent e) -> clearForm());

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
                client.isIsPreferential()
            });
        }
    }

    private void saveClient() {
        String id = txtId.getText().trim();
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        boolean pref = chkPreferential.isSelected();

        if (id.isEmpty() || name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Todos los campos son obligatorios (ID, Nombre, Teléfono)",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String result = clientController.registerClient(id, name, phone, pref);
        JOptionPane.showMessageDialog(this, result);

        // guardar referencia del cliente creado
        createdClient = clientController.findClientById(id);

        // cerrar ventana si fue creada desde otro módulo 
        this.dispose();

        loadTable();
        clearForm();
    }

    private void updateClient() {
        String id = txtId.getText().trim();
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        boolean pref = chkPreferential.isSelected();

        if (id.isEmpty() || name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Todos los campos son obligatorios para actualizar",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Client existingClient = clientController.findClientById(id);
        if (existingClient == null) {
            JOptionPane.showMessageDialog(this,
                    "No existe un cliente con el ID: " + id + "\nUse el botón 'Guardar Nuevo' para crear uno.",
                    "Cliente No Encontrado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de actualizar los datos del cliente?\nID: " + id,
                "Confirmar Actualización",
                JOptionPane.YES_NO_OPTION);

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
            JOptionPane.showMessageDialog(this,
                    "Seleccione un cliente de la tabla primero",
                    "Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar al cliente?\nID: " + id,
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            String result = clientController.deleteClient(id);
            JOptionPane.showMessageDialog(this, result);
            loadTable();
            clearForm();
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

        Object prefValue = model.getValueAt(row, 3);
        if (prefValue instanceof Boolean) {
            chkPreferential.setSelected((Boolean) prefValue);
        } else if (prefValue instanceof String) {
            chkPreferential.setSelected(Boolean.parseBoolean((String) prefValue));
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtPhone.setText("");
        chkPreferential.setSelected(false);

        table.clearSelection();

        for (java.awt.Component comp : getContentPane().getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                if (btn.getText().equals("Actualizar") || btn.getText().equals("Eliminar")) {
                    btn.setEnabled(false);
                }
            }
        }

        txtId.requestFocus();
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
            // Si existe, mostrarlo en el formulario
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

                // Mostrar la ventana SOLO si aún no está visible
                if (!this.isVisible()) {
                    if (desktop != null) {
                        desktop.add(this);
                    }
                    this.setVisible(true);
                }

                // Traer la ventana al frente
                try {
                    this.setSelected(true);
                    this.toFront();
                } catch (java.beans.PropertyVetoException e) {
                    // ignorar
                }

                // Preparar formulario
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
