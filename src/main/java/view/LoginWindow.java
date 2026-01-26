/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import Controller.AdministratorController;
import Controller.ClerkController;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import model.entities.Administrator;
import model.entities.Clerk;
import model.entities.ParkingLot;
import model.entities.User;

/**
 *
 * @author FAMILIA
 */
public class LoginWindow extends JFrame implements ActionListener {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnSignIn;

    ClerkController clerkController;
    AdministratorController administratorController;

    public LoginWindow() {
        //this are atributos del JFrame por encapsulamiento
        setTitle("Login Sistema de Parqueos de InnovaSoft");
        setSize(600, 300);//largo x ancho
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //cerrar todo el programa 
        setLocationRelativeTo(null);
        setResizable(false);
         
        clerkController = new ClerkController(); //buena practica en POO
        administratorController = new AdministratorController();

        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        //  Título
        JLabel lblTitle = new JLabel("Bienvenido/a", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));

        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
              
        //  Panel de formulario
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        // Username row
        JPanel usernamePanel = new JPanel(new BorderLayout(10, 0));
        JLabel lblUsername = new JLabel("Nombre de usuario:");
        txtUsername = new JTextField(15);
        
        
        usernamePanel.add(lblUsername, BorderLayout.WEST);
        usernamePanel.add(txtUsername, BorderLayout.CENTER);

        // Password row
        JPanel passwordPanel = new JPanel(new BorderLayout(10, 0));
        JLabel lblPassword = new JLabel("Contraseña:");
        txtPassword = new JPasswordField(15);
        txtPassword.setSize(100, 100);
        passwordPanel.add(lblPassword, BorderLayout.WEST);
        passwordPanel.add(txtPassword, BorderLayout.CENTER);

        formPanel.add(usernamePanel);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(passwordPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));

        btnSignIn = new JButton("Iniciar sesión");
        btnSignIn.addActionListener(this);
        buttonPanel.add(btnSignIn);

        mainPanel.add(lblTitle, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnSignIn) {

            String username = txtUsername.getText();
            String password = new String(txtPassword.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Se requiere nombre de usuario y contraseña.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            } else {

                //Review this-ask prof
                User userAuthenticatedClerk = clerkController.searchUser(new Clerk(1, "", 0, null, "3", "", username, password));
                User userAuthenticatedAdmin = administratorController.searchUser(new Administrator(1, new ParkingLot(), "1", null, username, password));

                if (userAuthenticatedClerk == null && userAuthenticatedAdmin == null) {
                    JOptionPane.showMessageDialog(
                            this,
                            "El usuario no existe.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                } else {

                    boolean autenticado = false;

                    // Verificar como administrador
                    Administrator admin = administratorController.findAdminByUsername(username);
                    if (admin != null && verificarPassword(admin.getPassword(), password)) {
                        new AdminMenu();
                        setVisible(false);
                        autenticado = true;
                    }

                    // Verificar como clerk
                    if (!autenticado) {
                        Clerk clerk = clerkController.findClerkByUsername(username);
                        if (clerk != null && verificarPassword(clerk.getPassword(), password)) {
                            new ClerkMenu();
                            setVisible(false);
                            autenticado = true;
                        }
                    }

                    // Si no se autenticó en ninguno
                    if (!autenticado) {
                        JOptionPane.showMessageDialog(
                                this,
                                "Credenciales inválidas", // Mensaje genérico por seguridad
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }

                }
            }
        }

    }

    private boolean verificarPassword(String passwordAlmacenado, String passwordIngresado) {
        // Verificar que no sean nulos
        if (passwordAlmacenado == null || passwordIngresado == null) {
            return false;
        }
        return passwordAlmacenado.equals(passwordIngresado);
    }
}
