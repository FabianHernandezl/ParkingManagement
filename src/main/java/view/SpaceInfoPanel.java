/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import model.entities.Space;

/**
 *
 * @author jimen
 */
public class SpaceInfoPanel extends JPanel {
    
    private JLabel lblTitle = new JLabel("Información del espacio");
    private JLabel lblClient = new JLabel("Cliente: ");
    private JLabel lblVehicle = new JLabel("Vehiculo: ");
    
    public SpaceInfoPanel() {
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(lblTitle);
        add(lblClient);
        add(lblVehicle);
        
    }
    
    public void showSpaceInfo(Space space) {
        
        if (space == null || !space.isSpaceTaken()) {
            
            lblClient.setText("Cliente: ");
            lblVehicle.setText("Vehículo: ");
            return;
            
        }
        
        lblClient.setText("Cliente: " + space.getClient().getName());
        lblVehicle.setText("Vehiculos: " + space.getVehicle().getPlate());
        
    }
    
}
