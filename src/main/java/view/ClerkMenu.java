/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.WindowConstants;
import model.entities.Clerk;

/**
 *
 * @author jimen
 */
public class ClerkMenu extends JFrame {

    private LoginWindow loginWindow;
    private final HomeDesktop desktop;
    private static File lockFile;
    private static FileChannel lockChannel;
    private static FileLock lock;
    private Clerk loggedClerk;

    public ClerkMenu(LoginWindow loginWindow, Clerk clerk) {

        super("Menú Operario de Parqueos");
        this.loginWindow = loginWindow;
        this.loggedClerk = clerk;

        desktop = new HomeDesktop(); // Call the HomeDesktop
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

        JMenuItem entryTicket = new JMenuItem("Ingreso de vehículo");
        ticketMenu.add(entryTicket);

        entryTicket.addActionListener(e -> {
            openInternalFrame(new TicketViewInternal(loggedClerk));
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

        menuBar.updateUI();

    }

    private void openInternalFrame(JInternalFrame frame) {
        desktop.add(frame);
        frame.setVisible(true);
    }

    private void openJFrame(LoginWindow loginWindow) {
        this.setVisible(false);
        loginWindow.setVisible(true);
    }

}
