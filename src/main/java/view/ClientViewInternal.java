package view;

import Controller.ClientController;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import model.entities.Client;

/**
 *
 * @author Jimena
 */
public class ClientViewInternal extends JInternalFrame {

    private final ClientController clientController = new ClientController();

    private JTextField txtId;
    private JTextField txtPhone;
    private JTextField txtName;
    private JCheckBox chkPreferential;
    private JTable table;
    private DefaultTableModel model;

    public ClientViewInternal() {
        super("Gestion de Clientes", true, true, true, true);

        setSize(600, 450);
        setLayout(null);  // Layout absoluto
        setVisible(true);

        // Etiquetas y campos de texto
        JLabel lblId = new JLabel("ID");
        lblId.setBounds(30, 20, 80, 25);
        add(lblId);

        txtId = new JTextField();
        txtId.setBounds(120, 20, 150, 25);
        add(txtId);

        JLabel lblName = new JLabel("Nombre");
        lblName.setBounds(30, 60, 80, 25);
        add(lblName);

        txtName = new JTextField();
        txtName.setBounds(120, 60, 150, 25);
        add(txtName);

        JLabel lblPhone = new JLabel("Teléfono");
        lblPhone.setBounds(30, 100, 80, 25);
        add(lblPhone);

        txtPhone = new JTextField();
        txtPhone.setBounds(120, 100, 150, 25);
        add(txtPhone);

        // Checkbox Preferencial
        chkPreferential = new JCheckBox("Preferencial");
        chkPreferential.setBounds(30, 140, 150, 30);
        add(chkPreferential);

        // Botones
        JButton btnSave = new JButton("Guardar");
        btnSave.setBounds(30, 190, 110, 30);
        add(btnSave);

        JButton btnDelete = new JButton("Eliminar");
        btnDelete.setBounds(150, 190, 110, 30);
        add(btnDelete);

        // Tabla
        model = new DefaultTableModel(new String[]{"ID", "Nombre", "Teléfono", "Preferencial"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(300, 20, 260, 350);
        add(scrollPane);

        // Cargar datos iniciales
        loadTable();

        // Listeners
        btnSave.addActionListener((ActionEvent e) -> saveClient());
        btnDelete.addActionListener((ActionEvent e) -> deleteClient());
        table.getSelectionModel().addListSelectionListener(e -> fillForm());
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
        String id = txtId.getText();
        String name = txtName.getText();
        String phone = txtPhone.getText();
        boolean pref = chkPreferential.isSelected();

        if (id.isEmpty() || name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (clientController.findClientById(id) == null) {
            JOptionPane.showMessageDialog(this, clientController.registerClient(id, name, phone, pref));
        } else {
            JOptionPane.showMessageDialog(this, clientController.updateClient(id, name, phone, pref));
        }

        loadTable();
        clearForm();
    }

    private void deleteClient() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente para eliminar", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = model.getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar este cliente?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, clientController.deleteClient(id));
            loadTable();
            clearForm();
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

        // Manejar el valor booleano de la columna 3
        Object prefValue = model.getValueAt(row, 3);
        if (prefValue != null) {
            if (prefValue instanceof Boolean) {
                chkPreferential.setSelected((Boolean) prefValue);
            } else if (prefValue instanceof String) {
                chkPreferential.setSelected(Boolean.parseBoolean((String) prefValue));
            }
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtPhone.setText("");
        chkPreferential.setSelected(false);
    }

    public Client selectOrCreateClient(JDesktopPane desktop) {
        String id = JOptionPane.showInputDialog(
                null,
                "Ingrese el ID del cliente",
                "Cliente",
                JOptionPane.QUESTION_MESSAGE
        );

        if (id == null || id.isEmpty()) {
            return null;
        }

        Client existing = clientController.findClientById(id);

        if (existing != null) {
            JOptionPane.showMessageDialog(
                    null,
                    "Cliente encontrado:\n\n" + existing
            );
            return existing;
        }

        int option = JOptionPane.showConfirmDialog(
                null,
                "El cliente no existe.\n¿Desea crearlo?",
                "Cliente",
                JOptionPane.YES_NO_OPTION
        );

        if (option != JOptionPane.YES_OPTION) {
            return null;
        }

        // Centrar y mostrar la ventana
        ClientViewInternal view = new ClientViewInternal();
        desktop.add(view);
        view.setLocation(
                (desktop.getWidth() - view.getWidth()) / 2,
                (desktop.getHeight() - view.getHeight()) / 2
        );
        view.toFront();
        view.setVisible(true);

        JOptionPane.showMessageDialog(
                null,
                "Cree el cliente y luego vuelva a seleccionarlo."
        );

        return null;
    }
}
