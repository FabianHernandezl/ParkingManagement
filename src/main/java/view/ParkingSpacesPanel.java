package view;

import Controller.SpaceController;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import model.entities.Client;
import model.entities.Space;
import model.entities.Vehicle;

public class ParkingSpacesPanel extends JPanel {

    private Space[] spaces;
    private SpaceInfoPanel infoPanel;
    private SpaceController controller;
    private Image carImage;
    private int carX = -40;
    private Space animatingSpace = null;
    private Timer timer;
    private int parkingLotId; // Nuevo campo para identificar el parqueo

    public ParkingSpacesPanel(Space[] spaces, SpaceInfoPanel infoPanel, SpaceController controller) {
        this.spaces = spaces;
        this.infoPanel = infoPanel;
        this.controller = controller;

        ImageIcon icon = new ImageIcon(getClass().getResource("/car.jpg"));
        carImage = icon.getImage();

        setBackground(new Color(245, 245, 245));

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

    public void setSpaces(Space[] spaces) {
        this.spaces = spaces;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (spaces == null || spaces.length == 0) {
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.setColor(Color.GRAY);
            g.drawString("No hay espacios disponibles", getWidth() / 2 - 100, getHeight() / 2);
            return;
        }

        int x = 50;
        int y = 50;
        int spacesPerRow = Math.max(1, (getWidth() - 100) / 130);
        int spaceCount = 0;

        for (Space space : spaces) {
            // Dibujar el espacio
            if (space != null) {
                // Color según tipo y estado
                if (space.isSpaceTaken()) {
                    g.setColor(Color.RED);
                } else if (space.isDisabilityAdaptation()) {
                    g.setColor(new Color(135, 206, 250)); // Azul claro para discapacitados
                } else if (space.getVehicleType().getDescription().equals("Motocicleta")) {
                    g.setColor(new Color(255, 218, 185)); // Melocotón para motocicletas
                } else {
                    g.setColor(Color.GREEN);
                }

                g.fillRect(x, y, 100, 60);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, 100, 60);

                // Texto del espacio
                g.setFont(new Font("Arial", Font.BOLD, 12));
                g.drawString("P" + space.getId(), x + 40, y + 25);

                // Indicadores especiales
                if (space.isDisabilityAdaptation()) {
                    g.setColor(Color.BLUE);
                    g.drawString("♿", x + 45, y + 45);
                } else if (space.getVehicleType().getDescription().equals("Motocicleta")) {
                    g.setColor(Color.ORANGE);
                    g.drawString("🏍", x + 45, y + 45);
                }

                // Dibujar vehículo si está ocupado o en animación
                if (space == animatingSpace) {
                    g.drawImage(carImage, carX, y + 10, 40, 25, null);
                } else if (space.isSpaceTaken()) {
                    g.drawImage(carImage, x + 30, y + 10, 40, 25, null);
                }
            }

            spaceCount++;
            x += 130;

            // Nueva fila si llegamos al límite
            if (spaceCount % spacesPerRow == 0) {
                x = 50;
                y += 80;
            }
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
                    Client client = new Client("199233", "Jimena Calvo", "7788997", false);
                    Vehicle vehicle = new Vehicle("988908", "Beige", "Toyota", "2001");

                    boolean success = controller.occupySpace(space.getId(), client, vehicle);
                    if (success) {
                        animatingSpace = space;
                        animateCarEntry(x);
                    }

                } else {
                    boolean success = controller.releaseSpace(space.getId());
                    if (success) {
                        animatingSpace = space;
                        animateCarExit(space, x);
                    }
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
        carX = startX + 30;

        timer = new Timer(20, e -> {
            carX += 5;
            repaint();

            if (carX > getWidth()) {
                timer.stop();
                animatingSpace = null;
                repaint();
            }
        });
        timer.start();
    }
}
