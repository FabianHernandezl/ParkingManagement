/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.WindowConstants;

/**
 *
 * @author jimen
 */
public class ClerkMenu extends JFrame {

    private HomeDesktop desktop;

    public ClerkMenu() {

        super("Menú Operario de Parqueos");

        desktop = new HomeDesktop(); // Call the HomeDesktop
        this.add(desktop);

        this.setSize(1000, 700);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(true);

        createMenuBar();
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
            openInternalFrame(new TicketViewInternal());
        });

       //--------------Sign Out----------
        JButton comebackMenu = new JButton("Salir");
        menuBar.add(comebackMenu);

        comebackMenu.addActionListener(e -> {
            openJFrame(new LoginWindow());
            
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
