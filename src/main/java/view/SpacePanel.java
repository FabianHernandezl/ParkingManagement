package view;

import model.entities.Space;
import model.entities.Vehicle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class SpacePanel extends JPanel {

    private Space space;
    private boolean selected = false;
    private SpaceView parent;

    private JLabel lblTitle;
    private JLabel lblStatus;
    private JLabel lblVehicleIcon;

    private boolean hover = false;

    public SpacePanel(Space space, SpaceView parent) {
        this.space = space;
        this.parent = parent;

        setPreferredSize(new Dimension(150, 100));
        setLayout(new BorderLayout(5, 5));
        setOpaque(false); // 🔥 IMPORTANTE para bordes redondeados
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblTitle = new JLabel("Espacio " + space.getId(), SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(Color.WHITE);

        lblStatus = new JLabel("", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(Color.WHITE);

        lblVehicleIcon = new JLabel("", SwingConstants.CENTER);
        lblVehicleIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        lblVehicleIcon.setVisible(false);

        add(lblVehicleIcon, BorderLayout.NORTH);
        add(lblTitle, BorderLayout.CENTER);
        add(lblStatus, BorderLayout.SOUTH);

        updateView();

        // Click
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parent.setSelectedPanel(SpacePanel.this);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                repaint();
            }
        });
    }

    // 🎨 DIBUJO PERSONALIZADO (Tarjeta con curvas y sombra)
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = 25;

        // 🔥 Sombra
        g2.setColor(new Color(0, 0, 0, 40));
        g2.fillRoundRect(6, 6, getWidth() - 12, getHeight() - 12, arc, arc);

        // 🔥 Color principal
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, arc, arc);

        // 🔥 Hover efecto
        if (hover) {
            g2.setColor(new Color(255, 255, 255, 40));
            g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, arc, arc);
        }

        // 🔥 Selección borde elegante
        if (selected) {
            g2.setStroke(new BasicStroke(3));
            g2.setColor(new Color(255, 215, 0));
            g2.drawRoundRect(1, 1, getWidth() - 8, getHeight() - 8, arc, arc);
        }

        g2.dispose();

        super.paintComponent(g);
    }

    public void updateView() {

        String statusText;
        Color bgColor;

        String vehicleTypeDesc = (space.getVehicleType() != null)
                ? space.getVehicleType().getDescription()
                : "Tipo desconocido";

        if (space.isSpaceTaken() && space.getVehicle() != null) {
            bgColor = new Color(244, 67, 54);
            statusText = "Ocupado";
            lblVehicleIcon.setText(space.getVehicle().getIcon());
            lblVehicleIcon.setVisible(true);
            animateVehicleIn();
        } else if (space.isDisabilityAdaptation()) {
            bgColor = new Color(3, 169, 244);
            statusText = "♿ Preferencial";
            lblVehicleIcon.setVisible(false);
        } else {
            bgColor = new Color(76, 175, 80);
            statusText = "Disponible";
            lblVehicleIcon.setVisible(false);
            animateVehicleOut();
        }

        setBackground(bgColor);

        lblStatus.setText("<html>" + vehicleTypeDesc + "<br>" + statusText + "</html>");

        updateTooltip();
        repaint();
    }

    public void updateTooltip() {
        String tooltip = "<html>";
        tooltip += "<b>Espacio #" + space.getId() + "</b><br>";

        String vehicleTypeDesc = (space.getVehicleType() != null)
                ? space.getVehicleType().getDescription()
                : "Tipo desconocido";

        tooltip += vehicleTypeDesc + "<br>";

        if (space.isSpaceTaken() && space.getVehicle() != null) {
            Vehicle v = space.getVehicle();
            tooltip += "<hr>";
            tooltip += "<b>Vehículo:</b><br>";
            tooltip += "Placa: " + v.getPlate() + "<br>";
            tooltip += "Modelo: " + v.getModel() + "<br>";
            if (space.getClient() != null) {
                tooltip += "Cliente: " + space.getClient().getName() + "<br>";
            }
        } else {
            tooltip += "<i>Libre</i>";
        }

        tooltip += "</html>";
        setToolTipText(tooltip);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        repaint();
    }

    public Space getSpace() {
        return space;
    }

    void animateVehicleIn() {
        lblVehicleIcon.setVisible(true);
        lblVehicleIcon.setForeground(new Color(0, 0, 0, 0));

        Timer timer = new Timer(20, null);
        final int[] alpha = {0};

        timer.addActionListener(e -> {
            alpha[0] += 20;
            lblVehicleIcon.setForeground(
                    new Color(0, 0, 0, Math.min(alpha[0], 255))
            );

            if (alpha[0] >= 255) {
                timer.stop();
            }
        });

        timer.start();
    }

    private void animateVehicleOut() {
        if (!lblVehicleIcon.isVisible()) {
            return;
        }

        Timer timer = new Timer(20, null);
        final int[] alpha = {255};

        timer.addActionListener(e -> {
            alpha[0] -= 20;
            lblVehicleIcon.setForeground(
                    new Color(0, 0, 0, Math.max(alpha[0], 0))
            );

            if (alpha[0] <= 0) {
                lblVehicleIcon.setVisible(false);
                timer.stop();
            }
        });

        timer.start();
    }

    public void setVehicleIcon(Vehicle vehicle) {
        lblVehicleIcon.setText(vehicle.getIcon());
    }
}
