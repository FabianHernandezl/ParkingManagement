/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import Controller.AdministratorController;
import Controller.ClerkController;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
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

    private HomeDesktop desktop;//background desktop

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnSignIn;

    ClerkController clerkController;
    AdministratorController administratorController;

    public LoginWindow() {
        //this are atributos del JFrame por encapsulamiento
        setTitle("Login");
        setSize(400, 350);//largo x ancho
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //cerrar todo el programa 
        setLocationRelativeTo(null);
        setVisible(true);

        clerkController = new ClerkController(); //buena practica en POO
        administratorController = new AdministratorController();

        initComponents();
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

    /*
    public static void main(String[] args) {
        new LoginWindow().setVisible(true);
    }*/

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

                    if (administratorController.findClerkByUsername(username) != null) {
                        new AdminMenu(); //call main page Admin

                    } else {
                        JOptionPane.showMessageDialog(
                                this,
                                "El operador no existe.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );

                        if (administratorController.findClerkByUsername(username) != null) {
                            new ClerkMenu(); //call main page Clerk
                        } else {
                            JOptionPane.showMessageDialog(
                                    this,
                                    "El administrador no existe.",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE
                            );

                        }
                    }

                }
            }
        }

    }
}
