package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import model.entities.Client;
import model.entities.Space;
import model.entities.Vehicle;

public class ParkingSpacesPanel extends JPanel {

    private Space[] spaces;
    private SpaceInfoPanel infoPanel;

    private Image carImage;

    private int carX = -40;
    private Space animatingSpace = null;
    private Timer timer;

    public ParkingSpacesPanel(Space[] spaces, SpaceInfoPanel infoPanel) {
        this.spaces = spaces;
        this.infoPanel = infoPanel;

        ImageIcon icon = new ImageIcon(
                "C:\\Users\\jimen\\OneDrive\\Escritorio\\UCR\\Desarrollo 2\\proyecto\\ParkingManagement\\src\\main\\resources\\car.jpg"
        );
        carImage = icon.getImage();

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                detectHover(e.getX(), e.getY());
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int x = 50;
        int y = 50;

        for (Space space : spaces) {

            g.setColor(space.isSpaceTaken() ? Color.RED : Color.GREEN);
            g.fillRect(x, y, 100, 60);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, 100, 60);
            g.drawString("P" + space.getId(), x + 40, y + 35);

            if (space == animatingSpace) {
                g.drawImage(carImage, carX, y + 10, 40, 25, null);
            }

            if (space.isSpaceTaken() && animatingSpace == null) {
                g.drawImage(carImage, x + 30, y + 10, 40, 25, null);
            }

            x += 130;
        }
    }

    private void detectHover(int mx, int my) {
        int x = 50;
        int y = 50;

        for (Space space : spaces) {
            if (mx >= x && mx <= x + 100 && my >= y && my <= y + 60) {
                infoPanel.showSpaceInfo(space);
                return;
            }
            x += 130;
        }
        infoPanel.showSpaceInfo(null);
    }

    private void handleClick(int mx, int my) {
        int x = 50;
        int y = 50;

        for (Space space : spaces) {
            if (mx >= x && mx <= x + 100 && my >= y && my <= y + 60) {

                if (!space.isSpaceTaken()) {

                    space.setSpaceTaken(true);
                    space.setClient(new Client("199233", "Jimena Calvo", "7788997", false));
                    space.setVehicle(new Vehicle("988908", "Beige", "Toyota", "2001"));

                    animatingSpace = space;
                    animateCarEntry(x);
                } 
                else {
                    animateCarExit(space, x);
                }
                return;
            }
            x += 130;
        }
    }

    private void animateCarEntry(int targetX) {
        carX = -40;

        timer = new Timer(20, e -> {
            carX += 5;
            repaint();

            if (carX >= targetX + 30) {
                timer.stop();
                animatingSpace = null;
                repaint();
            }
        });
        timer.start();
    }

    private void animateCarExit(Space space, int startX) {
        animatingSpace = space;
        carX = startX + 30;

        timer = new Timer(20, e -> {
            carX += 5;
            repaint();

            if (carX > getWidth()) {
                timer.stop();
                space.setSpaceTaken(false);
                space.setClient(null);
                space.setVehicle(null);
                animatingSpace = null;
                repaint();
            }
        });
        timer.start();
    }
}
