package view;

import Controller.AdministratorController;
import Controller.ClerkController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import model.data.ParkingLotData;
import model.entities.Administrator;
import model.entities.Clerk;
import model.entities.ParkingLot;
import model.entities.User;

public class LoginWindow extends JFrame implements ActionListener {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnSignIn;
    private JButton btnCancel;
    private JPanel mainPanel;

    ClerkController clerkController;
    AdministratorController administratorController;
    ParkingLotData parkingLotController;
    
    // Variables para evitar múltiples ejecuciones
    private static File lockFile;
    private static FileChannel lockChannel;
    private static FileLock lock;

    public LoginWindow() {

        UIManager.put("OptionPane.yesButtonText", "Sí");
        UIManager.put("OptionPane.noButtonText", "No");
        UIManager.put("OptionPane.cancelButtonText", "Cancelar");
        UIManager.put("OptionPane.okButtonText", "Aceptar");

        //Verificar si ya existe otra instancia
        if (!acquireLock()) {
            JOptionPane.showMessageDialog(
                    null,
                    "El sistema ya se encuentra en ejecución.",
                    "Sistema en ejecución",
                    JOptionPane.WARNING_MESSAGE
            );
            System.exit(0);
            return;
        }

        setTitle("Sistema Parqueos InnovaSoft");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                releaseLock();
            }
        });

        clerkController = new ClerkController();
        administratorController = new AdministratorController();

        initComponents();

        setSize(500, 550);
        setMinimumSize(new Dimension(500, 550));
        setMaximumSize(new Dimension(500, 550));
        setLocationRelativeTo(null);

        setVisible(true);
    }

    // Obtener lock
    private boolean acquireLock() {
        try {
            String tempDir = System.getProperty("java.io.tmpdir");
            String userName = System.getProperty("user.name");

            lockFile = new File(tempDir, "innosofpark_" + userName + ".lock");
            lockChannel = new RandomAccessFile(lockFile, "rw").getChannel();
            lock = lockChannel.tryLock();

            return lock != null;
        } catch (Exception e) {
            return false;
        }
    }

    // Liberar lock
    private void releaseLock() {
        try {
            if (lock != null && lock.isValid()) {
                lock.release();
            }
            if (lockChannel != null && lockChannel.isOpen()) {
                lockChannel.close();
            }
            if (lockFile != null && lockFile.exists()) {
                lockFile.deleteOnExit();
            }
        } catch (Exception e) {
        }
    }

    private void confirmExit() {
        int option = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro que desea salir del sistema?",
                "Confirmar salida",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (option == JOptionPane.YES_OPTION) {
            releaseLock();
            dispose();
            System.exit(0);
        }
    }

    private void initComponents() {
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(32, 58, 96);  // Azul oscuro
                Color color2 = new Color(142, 68, 173); // Morado 
                GradientPaint gradient = new GradientPaint(
                        0, 0, color1,
                        getWidth(), getHeight(), color2
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());

        // Panel de contenido central
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1;
        gbc.weighty = 0;

        // Logo/Título
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel lblSubtitle = new JLabel("Sistema de Gestión de Parqueos", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 20));
        lblSubtitle.setForeground(new Color(200, 230, 255));

        titlePanel.add(lblSubtitle, BorderLayout.SOUTH);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        contentPanel.add(titlePanel, gbc);

        // Panel de login (card)
        JPanel loginCard = new JPanel(new GridBagLayout());
        loginCard.setBackground(Color.WHITE);
        loginCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 220), 1, true),
                new EmptyBorder(25, 25, 25, 25)
        ));

        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.fill = GridBagConstraints.HORIZONTAL;
        cardGbc.insets = new Insets(8, 0, 8, 0);
        cardGbc.weightx = 1;
        cardGbc.weighty = 0;

        // Título del formulario
        JLabel lblFormTitle = new JLabel("INICIAR SESIÓN", SwingConstants.CENTER);
        lblFormTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblFormTitle.setForeground(new Color(32, 58, 96));
        cardGbc.gridx = 0;
        cardGbc.gridy = 0;
        cardGbc.gridwidth = 2;
        loginCard.add(lblFormTitle, cardGbc);

        // Separador
        JPanel separator = new JPanel();
        separator.setBackground(new Color(22, 160, 133));
        separator.setPreferredSize(new Dimension(100, 2));
        cardGbc.gridy = 1;
        cardGbc.insets = new Insets(5, 0, 15, 0);
        loginCard.add(separator, cardGbc);

        // Campo Usuario
        cardGbc.insets = new Insets(5, 0, 5, 0);
        cardGbc.gridwidth = 1;

        JLabel lblUsername = new JLabel("Usuario:");
        lblUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        lblUsername.setForeground(new Color(80, 80, 80));
        cardGbc.gridx = 0;
        cardGbc.gridy = 2;
        cardGbc.anchor = GridBagConstraints.WEST;
        loginCard.add(lblUsername, cardGbc);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(180, 180, 180), 1),
                new EmptyBorder(8, 10, 8, 10)
        ));
        txtUsername.setPreferredSize(new Dimension(250, 35));

        // Placeholder effect
        txtUsername.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtUsername.getText().equals("Ingrese su usuario")) {
                    txtUsername.setText("");
                    txtUsername.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtUsername.getText().isEmpty()) {
                    txtUsername.setText("Ingrese su usuario");
                    txtUsername.setForeground(Color.GRAY);
                }
            }
        });

        cardGbc.gridx = 0;
        cardGbc.gridy = 3;
        cardGbc.gridwidth = 2;
        loginCard.add(txtUsername, cardGbc);

        // Campo Contraseña
        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        lblPassword.setForeground(new Color(80, 80, 80));
        cardGbc.gridx = 0;
        cardGbc.gridy = 4;
        cardGbc.gridwidth = 1;
        loginCard.add(lblPassword, cardGbc);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(180, 180, 180), 1),
                new EmptyBorder(8, 10, 8, 10)
        ));
        txtPassword.setPreferredSize(new Dimension(250, 35));
        cardGbc.gridx = 0;
        cardGbc.gridy = 5;
        cardGbc.gridwidth = 2;
        loginCard.add(txtPassword, cardGbc);

        // Bordes fijos para el botón 
        var normalBorder = BorderFactory.createCompoundBorder(
                new LineBorder(new Color(20, 140, 120), 1),
                new EmptyBorder(12, 25, 12, 25)
        );

        var hoverBorder = BorderFactory.createCompoundBorder(
                new LineBorder(new Color(24, 168, 140), 1),
                new EmptyBorder(12, 25, 12, 25)
        );

        // Botón de inicio de sesión
        btnSignIn = new JButton("INGRESAR AL SISTEMA");
        btnSignIn.setFont(new Font("Arial", Font.BOLD, 14));
        btnSignIn.setBackground(new Color(142, 68, 173));
        btnSignIn.setForeground(Color.WHITE);
        btnSignIn.setBorder(normalBorder);
        btnSignIn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSignIn.setFocusPainted(false);
        btnSignIn.addActionListener(this);

        Dimension btnSize = new Dimension(260, 45);
        btnSignIn.setPreferredSize(btnSize);
        btnSignIn.setMinimumSize(btnSize);
        btnSignIn.setMaximumSize(btnSize);

        // Efecto hover
        btnSignIn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnSignIn.setBackground(new Color(155, 89, 182));
                btnSignIn.setBorder(hoverBorder);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnSignIn.setBackground(new Color(142, 68, 173));
                btnSignIn.setBorder(normalBorder);
            }
        });

        // Botón cancelar
        btnCancel = new JButton("CANCELAR");
        btnCancel.setFont(new Font("Arial", Font.BOLD, 13));
        btnCancel.setBackground(new Color(231, 76, 60)); // rojo elegante
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setBorder(normalBorder);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.setFocusPainted(false);
        btnCancel.addActionListener(this);

        Dimension btnCancelSize = new Dimension(260, 40);
        btnCancel.setPreferredSize(btnCancelSize);
        btnCancel.setMinimumSize(btnCancelSize);
        btnCancel.setMaximumSize(btnCancelSize);

        // Hover cancelar
        btnCancel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnCancel.setBackground(new Color(192, 57, 43));
                btnCancel.setBorder(hoverBorder);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnCancel.setBackground(new Color(231, 76, 60));
                btnCancel.setBorder(normalBorder);
            }
        });

        cardGbc.gridx = 0;
        cardGbc.gridy = 6;
        cardGbc.gridwidth = 2;
        cardGbc.insets = new Insets(20, 0, 0, 0);
        loginCard.add(btnSignIn, cardGbc);

        cardGbc.gridy = 7;
        cardGbc.insets = new Insets(10, 0, 0, 0);
        loginCard.add(btnCancel, cardGbc);

        JLabel lblHelp = new JLabel("<html><center>¿Problemas para ingresar?<br>Contacte al administrador del sistema</center></html>",
                SwingConstants.CENTER);
        lblHelp.setFont(new Font("Arial", Font.ITALIC, 11));
        lblHelp.setForeground(new Color(120, 120, 120));
        cardGbc.gridy = 7;
        cardGbc.insets = new Insets(15, 0, 0, 0);
        loginCard.add(lblHelp, cardGbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.NORTH;
        contentPanel.add(loginCard, gbc);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);

        txtUsername.setText("Ingrese su usuario");
        txtUsername.setForeground(Color.GRAY);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSignIn) {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();

            if (username.equals("Ingrese su usuario") || username.isEmpty()) {
                showErrorMessage("Debe ingresar un nombre de usuario");
                return;
            }

            if (password.isEmpty()) {
                showErrorMessage("Debe ingresar una contraseña");
                return;
            }

           ArrayList<ParkingLot> parkingLots = new  ArrayList<ParkingLot>();
            User userAuthenticatedClerk = clerkController.searchUser(
                    new Clerk(1, "", 0, null, "3", "", username, password));
            User userAuthenticatedAdmin = administratorController.searchUser(
                    new Administrator(1,  parkingLots, "1", null, username, password));

            if (userAuthenticatedClerk == null && userAuthenticatedAdmin == null) {
                showErrorMessage("Usuario no encontrado en el sistema");
                return;
            }

            boolean autenticado = false;

            Administrator admin = administratorController.findAdminByUsername(username);
            if (admin != null && verificarPassword(admin.getPassword(), password)) {
                new AdminMenu(this);
                setVisible(false);
                autenticado = true;
            }

            if (!autenticado) {
                Clerk clerk = clerkController.findClerkByUsername(username);
                if (clerk != null && verificarPassword(clerk.getPassword(), password)) {
                    new ClerkMenu(this);
                    setVisible(false);
                }
            }
        } else if (e.getSource() == btnCancel) {
            confirmExit();
        }
    }

    private boolean verificarPassword(String almacenado, String ingresado) {
        return almacenado != null && almacenado.equals(ingresado);
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
