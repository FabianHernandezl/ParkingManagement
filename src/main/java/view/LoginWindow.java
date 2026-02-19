package view;

import controller.AdministratorController;
import controller.ClerkController;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import javax.swing.*;
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

    private static File lockFile;
    private static FileChannel lockChannel;
    private static FileLock lock;

    public LoginWindow() {

        UIManager.put("OptionPane.yesButtonText", "Sí");
        UIManager.put("OptionPane.noButtonText", "No");
        UIManager.put("OptionPane.cancelButtonText", "Cancelar");
        UIManager.put("OptionPane.okButtonText", "Aceptar");

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
                releaseLock();
                System.exit(0);
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

    private void initComponents() {
        // ------------------ Colores basados en el logo ------------------
        Color azulLogo = new Color(46, 94, 154);     // #2E5E9A
        Color verdeAguaLogo = new Color(62, 193, 169); // #3EC1A9
        Color moradoLogo = new Color(140, 84, 160);  // #8C54A0

        // ------------------ Fondo degradado ------------------
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();

                GradientPaint gp1 = new GradientPaint(0, 0, azulLogo, 0, h / 2f, verdeAguaLogo);
                GradientPaint gp2 = new GradientPaint(0, h / 2f, verdeAguaLogo, 0, h, moradoLogo);

                g2d.setPaint(gp1);
                g2d.fillRect(0, 0, w, h / 2);
                g2d.setPaint(gp2);
                g2d.fillRect(0, h / 2, w, h / 2);
            }
        };
        mainPanel.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1;
        gbc.weighty = 0;

        // ------------------ Título ------------------
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel lblSubtitle = new JLabel("Sistema de Gestión de Parqueos", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 20));
        lblSubtitle.setForeground(Color.WHITE);
        titlePanel.add(lblSubtitle, BorderLayout.SOUTH);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        contentPanel.add(titlePanel, gbc);

        // ------------------ Card login ------------------
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

        JLabel lblFormTitle = new JLabel("INICIAR SESIÓN", SwingConstants.CENTER);
        lblFormTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblFormTitle.setForeground(azulLogo);
        cardGbc.gridx = 0;
        cardGbc.gridy = 0;
        cardGbc.gridwidth = 2;
        loginCard.add(lblFormTitle, cardGbc);

        JPanel separator = new JPanel();
        separator.setBackground(verdeAguaLogo);
        separator.setPreferredSize(new Dimension(100, 2));
        cardGbc.gridy = 1;
        cardGbc.insets = new Insets(5, 0, 15, 0);
        loginCard.add(separator, cardGbc);

        JLabel lblUsername = new JLabel("Usuario:");
        lblUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        lblUsername.setForeground(new Color(80, 80, 80));
        cardGbc.gridx = 0;
        cardGbc.gridy = 2;
        cardGbc.gridwidth = 1;
        cardGbc.anchor = GridBagConstraints.WEST;
        loginCard.add(lblUsername, cardGbc);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(180, 180, 180), 1),
                new EmptyBorder(8, 10, 8, 10)
        ));
        txtUsername.setPreferredSize(new Dimension(250, 35));
        txtUsername.setText("Ingrese su usuario");
        txtUsername.setForeground(Color.GRAY);

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

        // ------------------ Botones ------------------
        var normalBorder = BorderFactory.createCompoundBorder(
                new LineBorder(azulLogo, 1),
                new EmptyBorder(12, 25, 12, 25)
        );
        var hoverBorder = BorderFactory.createCompoundBorder(
                new LineBorder(verdeAguaLogo, 1),
                new EmptyBorder(12, 25, 12, 25)
        );

        btnSignIn = new JButton("INGRESAR AL SISTEMA");
        btnSignIn.setFont(new Font("Arial", Font.BOLD, 14));
        btnSignIn.setBackground(verdeAguaLogo);
        btnSignIn.setForeground(Color.WHITE);
        btnSignIn.setBorder(normalBorder);
        btnSignIn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSignIn.setFocusPainted(false);
        btnSignIn.addActionListener(this);

        btnSignIn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnSignIn.setBackground(new Color(72, 214, 186));
                btnSignIn.setBorder(hoverBorder);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnSignIn.setBackground(verdeAguaLogo);
                btnSignIn.setBorder(normalBorder);
            }
        });

        btnCancel = new JButton("CANCELAR");
        btnCancel.setFont(new Font("Arial", Font.BOLD, 13));
        btnCancel.setBackground(moradoLogo);
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setBorder(normalBorder);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.setFocusPainted(false);
        btnCancel.addActionListener(this);

        btnCancel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnCancel.setBackground(new Color(160, 100, 180));
                btnCancel.setBorder(hoverBorder);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnCancel.setBackground(moradoLogo);
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

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.NORTH;
        contentPanel.add(loginCard, gbc);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);
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

            ArrayList<ParkingLot> parkingLots = new ArrayList<>();
            User userAuthenticatedClerk = clerkController.searchUser(
                    new Clerk(1, "", 0, null, "3", "", username, password));
            User userAuthenticatedAdmin = administratorController.searchUser(
                    new Administrator(1, parkingLots, "1", null, username, password));

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
            releaseLock();
            System.exit(0);
        }
    }

    private boolean verificarPassword(String almacenado, String ingresado) {
        return almacenado != null && almacenado.equals(ingresado);
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
