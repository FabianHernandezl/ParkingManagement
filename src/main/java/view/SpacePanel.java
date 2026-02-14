package view;

import model.entities.Space;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import model.entities.Vehicle;

public class SpacePanel extends JPanel {

    private Space space;
    private boolean selected = false;
    private SpaceView parent;
    private JLabel lblTitle;
    private JLabel lblStatus;
    private JLabel lblVehicleIcon;

    public SpacePanel(Space space, SpaceView parent) {
        this.space = space;
        this.parent = parent;

        setPreferredSize(new Dimension(140, 90));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblTitle = new JLabel("Espacio " + space.getId(), SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));

        lblStatus = new JLabel("", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        add(lblTitle, BorderLayout.CENTER);
        add(lblStatus, BorderLayout.SOUTH);

        lblVehicleIcon = new JLabel("", SwingConstants.CENTER);
        lblVehicleIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        lblVehicleIcon.setVisible(false);

        add(lblVehicleIcon, BorderLayout.NORTH);

        updateView();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parent.setSelectedPanel(SpacePanel.this);
            }
        });

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    parent.showVehicleInfo(SpacePanel.this);
                }
            }
        });

    }

    public void updateView() {

        String statusText = "";
        Color bgColor = Color.LIGHT_GRAY;

        String vehicleTypeDesc = (space.getVehicleType() != null)
                ? space.getVehicleType().getDescription()
                : "Tipo desconocido";

        if (space.isSpaceTaken() && space.getVehicle() != null) {
            bgColor = new Color(244, 67, 54); // rojo
            statusText = "Ocupado";
            lblVehicleIcon.setText(space.getVehicle().getIcon());
            lblVehicleIcon.setVisible(true);
            animateVehicleIn();
        } else if (space.isDisabilityAdaptation()) {
            bgColor = new Color(3, 169, 244); // azul
            statusText = "♿ Preferencial";
            lblVehicleIcon.setVisible(false);
        } else {
            bgColor = new Color(76, 175, 80); // verde
            statusText = "Disponible";
            lblVehicleIcon.setVisible(false);
            animateVehicleOut();
        }

        setBackground(bgColor);
        lblStatus.setText("<html>" + vehicleTypeDesc + "<br>" + statusText + "</html>");

        setOpaque(true);
        updateTooltip();
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
        setBorder(
                selected
                        ? BorderFactory.createLineBorder(Color.YELLOW, 4)
                        : BorderFactory.createLineBorder(Color.DARK_GRAY, 2)
        );
    }

    public Space getSpace() {
        return space;
    }

    public void animateChange(Color targetColor) {
        Color start = getBackground();

        Timer timer = new Timer(15, null);
        final int[] step = {0};
        final int maxSteps = 20;

        timer.addActionListener(e -> {
            float ratio = step[0] / (float) maxSteps;

            int r = (int) (start.getRed() + ratio * (targetColor.getRed() - start.getRed()));
            int g = (int) (start.getGreen() + ratio * (targetColor.getGreen() - start.getGreen()));
            int b = (int) (start.getBlue() + ratio * (targetColor.getBlue() - start.getBlue()));

            setBackground(new Color(r, g, b));
            repaint();

            step[0]++;
            if (step[0] > maxSteps) {
                timer.stop();
            }
        });

        timer.start();
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
