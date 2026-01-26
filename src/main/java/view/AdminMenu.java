/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.WindowConstants;

/**
 *
 * @author FAMILIA
 */
public class AdminMenu extends JFrame {

    private HomeDesktop desktop;

    public AdminMenu() {

        super("Menú Administrador de Parqueos");

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
            openInternalFrame(new ParkingLotViewInternal());
        });

        menuBar.updateUI();

        // ---------- SPACES ----------
        JMenu spacesMenu = new JMenu("Espacios");
        menuBar.add(spacesMenu);

        JMenuItem manageSpaces = new JMenuItem("Gestionar Espacios");
        spacesMenu.add(manageSpaces);

        JMenuItem parkingSpacesItem = new JMenuItem("Ver Espacios");
        spacesMenu.add(parkingSpacesItem);

        parkingSpacesItem.addActionListener(e -> {
            desktop.add(new ParkingSpacesInternal());
        });
        manageSpaces.addActionListener((ActionEvent e) -> {
            openInternalFrame(new ParkingSpacesInternal());
        });
        // ---------- OPERARIOS - users ----------
        JMenu clerksMenu = new JMenu("Operarios");
        menuBar.add(clerksMenu);

        JMenuItem manageClerks = new JMenuItem("Gestionar Operarios");
        clerksMenu.add(manageClerks);

        manageClerks.addActionListener(e -> {
            openInternalFrame(new ClerksView()); //crud de clerks
        });

        //--------------TARIFAS ----------
        JMenu feeMenu = new JMenu("Tarifas");
        menuBar.add(feeMenu);

        JMenuItem fees = new JMenuItem("Gestionar Tarifas");
        feeMenu.add(fees);

        fees.addActionListener(e -> {
            openInternalFrame(new TicketViewInternal());//TO DO crud de Tarifas
        });

        //--------------REPORTS ---------- TO DO 
        JMenu reportsMenu = new JMenu("Reportes");
        menuBar.add(reportsMenu);

        JMenuItem report1 = new JMenuItem("Generar Reporte 1");
        reportsMenu.add(report1);

        report1.addActionListener(e -> {
            openInternalFrame(new TicketViewInternal());
        });

        JMenuItem report2 = new JMenuItem("Generar Reporte 2");
        reportsMenu.add(report2);
        report2.addActionListener(e -> {
            openInternalFrame(new TicketViewInternal());
        });

        JMenuItem report3 = new JMenuItem("Generar Reporte 3");
        reportsMenu.add(report3);
        report3.addActionListener(e -> {
            openInternalFrame(new TicketViewInternal());
        });

        //--------------Sign Out---------- 
        JButton comebackMenu = new JButton("Salir");
        menuBar.add(comebackMenu);

        comebackMenu.addActionListener(e -> {
            openJFrame(new LoginWindow());
            
        });
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
