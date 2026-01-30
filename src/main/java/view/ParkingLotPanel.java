package view;

import model.entities.Space;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ParkingLotPanel extends JPanel {

    public ParkingLotPanel(List<Space> spaces, SpaceView parent) {
        setLayout(new GridLayout(0, 4, 10, 10));

        for (Space space : spaces) {
            add(new SpacePanel(space, parent));
        }
    }
}
