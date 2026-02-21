package view;

import model.entities.Space;
import model.entities.Vehicle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Custom visual component representing a single parking space.
 *
 * Displays: - Space ID - Status (Available / Occupied / Preferential) - Vehicle
 * icon (if occupied)
 *
 * Includes: - Rounded card design - Hover effect - Selection highlight -
 * Entry/exit animations
 */
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
        setOpaque(false);
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

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = 25;

        g2.setColor(new Color(0, 0, 0, 40));
        g2.fillRoundRect(6, 6, getWidth() - 12, getHeight() - 12, arc, arc);

        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, arc, arc);

        if (hover) {
            g2.setColor(new Color(255, 255, 255, 40));
            g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, arc, arc);
        }

        if (selected) {
            g2.setStroke(new BasicStroke(3));
            g2.setColor(new Color(255, 215, 0));
            g2.drawRoundRect(1, 1, getWidth() - 8, getHeight() - 8, arc, arc);
        }

        g2.dispose();

        super.paintComponent(g);
    }

    public void updateView() {
        System.out.println("🔄 SpacePanel.updateView() - Espacio " + space.getId()
                + ", Ocupado: " + space.isSpaceTaken()
                + ", Vehículo: " + (space.getVehicle() != null ? space.getVehicle().getPlate() : "null"));

        String statusText;
        Color bgColor;
        String vehicleTypeDesc = "Tipo desconocido";

        if (space.getVehicleType() != null) {
            vehicleTypeDesc = space.getVehicleType().getDescription();
        }

        // 🔥 PRIORIDAD 1: Espacio ocupado - SIEMPRE rojo
        if (space.isSpaceTaken()) {
            bgColor = new Color(244, 67, 54); // Rojo
            statusText = "Ocupado";

            // Si hay vehículo, mostrar su ícono
            if (space.getVehicle() != null) {
                lblVehicleIcon.setText(space.getVehicle().getIcon());
                lblVehicleIcon.setVisible(true);
                animateVehicleIn();
            } else {
                // Si no hay vehículo pero está ocupado, mostrar ícono genérico
                lblVehicleIcon.setText("🚗");
                lblVehicleIcon.setVisible(true);
            }

            // 🔥 PRIORIDAD 2: Espacio preferencial
        } else if (space.isDisabilityAdaptation()) {
            bgColor = new Color(3, 169, 244); // Azul
            statusText = "♿ Preferencial";
            lblVehicleIcon.setVisible(false);

            // 🔥 PRIORIDAD 3: Espacio disponible
        } else {
            bgColor = new Color(76, 175, 80); // Verde
            statusText = "Disponible";
            lblVehicleIcon.setVisible(false);
        }

        // Actualizar colores y texto
        setBackground(bgColor);
        lblStatus.setText("<html>" + vehicleTypeDesc + "<br>" + statusText + "</html>");

        updateTooltip();

        // Forzar repaint inmediato
        revalidate();
        repaint();
    }

    public void updateTooltip() {
        String tooltip = "<html>";
        tooltip += "<b>Espacio #" + space.getId() + "</b><br>";

        String vehicleTypeDesc = (space.getVehicleType() != null)
                ? space.getVehicleType().getDescription()
                : "Tipo desconocido";

        tooltip += vehicleTypeDesc + "<br>";

        if (space.isSpaceTaken()) {
            tooltip += "<hr>";
            tooltip += "<b>Estado: OCUPADO</b><br>";
            if (space.getVehicle() != null) {
                Vehicle v = space.getVehicle();
                tooltip += "Placa: " + v.getPlate() + "<br>";
                tooltip += "Modelo: " + v.getModel() + "<br>";
            } else {
                tooltip += "<i>Sin información de vehículo</i><br>";
            }
            if (space.getClient() != null) {
                tooltip += "Cliente: " + space.getClient().getName() + "<br>";
            }
        } else if (space.isDisabilityAdaptation()) {
            tooltip += "<i>Espacio preferencial - Libre</i>";
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
