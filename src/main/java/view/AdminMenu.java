/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import controller.ParkingLotController;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.WindowConstants;
import model.entities.Administrator;
import java.awt.Font;
import java.awt.Color;
import javax.swing.Box;
import javax.swing.JLabel;

/**
 *
 * @author FAMILIA
 */
public class AdminMenu extends JFrame {

    private LoginWindow loginWindow;
    private HomeDesktop desktop;
    private ParkingLotController parkingLotController = new ParkingLotController();
    private static File lockFile;
    private static FileChannel lockChannel;
    private static FileLock lock;
    private Administrator loggedAdmin;

    public AdminMenu(LoginWindow loginWindow, Administrator admin) {

        super("Menú Administrador de Parqueos");
        this.loginWindow = loginWindow;
        this.loggedAdmin = admin;

        desktop = new HomeDesktop();
        this.add(desktop);

        this.setSize(1000, 700);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setResizable(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                releaseLock();
                System.exit(0);
            }
        });

        createMenuBar();
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

    private void createMenuBar() {

        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        ///------------FUNCIONES OPERADOR ------------
        // ---------- CLIENTES ----------
        JMenu clientMenu = new JMenu("Clientes");
        menuBar.add(clientMenu);

        JMenuItem manageClients = new JMenuItem("Gestionar clientes");
        clientMenu.add(manageClients);

        manageClients.addActionListener((ActionEvent e) -> {
            openInternalFrame(new ClientViewInternal());
        });
        // ---------- Vehículos ----------
        JMenu vehicleMenu = new JMenu("Vehículos");
        menuBar.add(vehicleMenu);

        JMenuItem manageVehicles = new JMenuItem("Gestionar vehículos");
        vehicleMenu.add(manageVehicles);

        manageVehicles.addActionListener(e -> {
            openInternalFrame(new VehicleViewInternal());
        });

        //-------------------Tiquete----------
        JMenu ticketMenu = new JMenu("Tiquetes");
        menuBar.add(ticketMenu);

        JMenuItem entryTicket = new JMenuItem("Gestionar tiquetes");
        ticketMenu.add(entryTicket);

        entryTicket.addActionListener(e -> {
            openInternalFrame(new TicketViewInternal());
        });

        ///------------FIN FUNCIONES OPERADOR ------------
        //---------Parqueos--------
        JMenu parkingMenu = new JMenu("Parqueos");
        menuBar.add(parkingMenu);

        JMenuItem manageParkingLots = new JMenuItem("Gestionar parqueos");
        parkingMenu.add(manageParkingLots);

        manageParkingLots.addActionListener(e -> {
            openInternalFrame(new ParkingLotViewInternal(this));
        });

        menuBar.updateUI();

        // ---------- SPACES ----------
        JMenu spacesMenu = new JMenu("Espacios");
        menuBar.add(spacesMenu);

        JMenuItem manageSpaces = new JMenuItem("Gestionar Espacios");
        spacesMenu.add(manageSpaces);

        manageSpaces.addActionListener(e -> {
            openInternalFrame(new SelectParkingLotView(this));
        });

        //--------------TARIFAS ----------
        JMenu feeMenu = new JMenu("Tarifas");
        menuBar.add(feeMenu);

        JMenuItem fees = new JMenuItem("Gestionar Tarifas");
        feeMenu.add(fees);

        fees.addActionListener(e -> {
            openInternalFrame(new ParkingRateViewInternal());
        });

        // ---------- OPERARIOS - users ----------
        JMenu clerksMenu = new JMenu("Operarios");
        menuBar.add(clerksMenu);

        JMenuItem manageClerks = new JMenuItem("Gestionar Operarios");
        clerksMenu.add(manageClerks);

        manageClerks.addActionListener(e -> {
            openInternalFrame(new ClerksView()); //crud de clerks
        });
        //--------------Administrador ----------
        JMenu adminMenu = new JMenu("Administradores");
        menuBar.add(adminMenu);

        JMenuItem admins = new JMenuItem("Gestionar Administradores");
        adminMenu.add(admins);

        admins.addActionListener(e -> {
            openInternalFrame(new AdminView());
        });
        //-------------- REPORTES ----------
        JMenu reportsMenu = new JMenu("Reportes");
        menuBar.add(reportsMenu);

        JMenuItem reportsCenter = new JMenuItem("Centro de Reportes");
        reportsMenu.add(reportsCenter);

        reportsCenter.addActionListener(e -> {
            openInternalFrame(new ReportsCenterInternal());
        });

        //--------------Sign Out---------- 
        JButton comebackMenu = new JButton("Salir");
        menuBar.add(comebackMenu);

        comebackMenu.addActionListener(e -> {

            int option = javax.swing.JOptionPane.showConfirmDialog(
                    this,
                    "¿Desea cerrar sesión y volver al login?",
                    "Cerrar sesión",
                    javax.swing.JOptionPane.YES_NO_OPTION
            );

            if (option == javax.swing.JOptionPane.YES_OPTION) {
                this.dispose();              // cerramos AdminMenu
                loginWindow.setVisible(true); // volvemos al login original
            }
        });

        // ---------- Etiqueta de bienvenida ----------
        String parqueosAsignados = "Todos los parqueos";
        if (loggedAdmin.getParkingLot() != null
                && !loggedAdmin.getParkingLot().isEmpty()) {
            parqueosAsignados = loggedAdmin.getParkingLot()
                    .stream()
                    .map(p -> p.getName())
                    .collect(java.util.stream.Collectors.joining(", "));
        }

        JLabel lblWelcome = new JLabel(loggedAdmin.getName()
                + " | Administrador | " + parqueosAsignados + " ");
        lblWelcome.setFont(new Font("Arial", Font.PLAIN, 12));
        lblWelcome.setForeground(Color.DARK_GRAY);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(lblWelcome);

        menuBar.updateUI();
    }

    void openInternalFrame(JInternalFrame frame) {
        desktop.add(frame);
        frame.setVisible(true);

    }

    private void openJFrame(LoginWindow loginWindow) {
        this.setVisible(false);
        loginWindow.setVisible(true);
    }

    public void refreshParkingCombos() {
        for (JInternalFrame frame : desktop.getAllFrames()) {
            if (frame instanceof VehicleViewInternal) {
                ((VehicleViewInternal) frame).refreshTable();
            }
            if (frame instanceof ParkingRateViewInternal) {
                ((ParkingRateViewInternal) frame).loadParkingLots();
            }
        }
    }
}
