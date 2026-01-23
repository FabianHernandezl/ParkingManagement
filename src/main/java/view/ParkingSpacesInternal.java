package view;

import javax.swing.*;
import model.entities.Space;


public class ParkingSpacesInternal extends JInternalFrame {

    public ParkingSpacesInternal() {
        super("Mapa de Parqueo", true, true, true, true);
        setSize(900, 400);
        setLayout(new java.awt.BorderLayout());

        Space[] spaces = new Space[5];
        for (int i = 0; i < spaces.length; i++) {
            spaces[i] = new Space();
            spaces[i].setId(i + 1);
        }

        SpaceInfoPanel infoPanel = new SpaceInfoPanel();
        ParkingSpacesPanel spacesPanel = new ParkingSpacesPanel(spaces, infoPanel);

        add(infoPanel, java.awt.BorderLayout.WEST);
        add(spacesPanel, java.awt.BorderLayout.CENTER);

        setVisible(true);
    }
}
