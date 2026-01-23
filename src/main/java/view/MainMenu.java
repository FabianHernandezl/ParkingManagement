/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import java.awt.event.ActionEvent;
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
public class MainMenu extends JFrame {

    private HomeDesktop desktop;

    public MainMenu() {

        super("Parking Management System");

        desktop = new HomeDesktop(); // 👈 AQUÍ SE LLAMA
        this.add(desktop);

        this.setSize(1000, 700);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(false);

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

        JMenu parkingMenu = new JMenu("Parqueos");
        menuBar.add(parkingMenu);

        JMenuItem manageParkingLots = new JMenuItem("Gestionar parqueos");
        parkingMenu.add(manageParkingLots);

        manageParkingLots.addActionListener(e -> {
            openInternalFrame(new ParkingLotViewInternal());
        });

        menuBar.updateUI();

        JMenuItem parkingSpacesItem = new JMenuItem("Ver Espacios");
        parkingMenu.add(parkingSpacesItem);

        parkingSpacesItem.addActionListener(e -> {
            desktop.add(new ParkingSpacesInternal());
        });

    }

    private void openInternalFrame(JInternalFrame frame) {
        desktop.add(frame);
        frame.setVisible(true);
    }

}
