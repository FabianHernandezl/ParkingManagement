package view;

import model.entities.Space;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SpacePanel extends JPanel {

    private Space space;
    private boolean selected = false;
    private SpaceView parent;
    private JLabel lblTitle;
    private JLabel lblStatus;

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

        updateView();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parent.setSelectedPanel(SpacePanel.this);
            }
        });
    }

    public void updateView() {
        if (space.isSpaceTaken()) {
            setBackground(new Color(244, 67, 54)); // rojo suave
            lblStatus.setText("🚗 Ocupado");
        } else if (space.isDisabilityAdaptation()) {
            setBackground(new Color(3, 169, 244)); // azul
            lblStatus.setText("♿ Preferencial");
        } else {
            setBackground(new Color(76, 175, 80)); // verde
            lblStatus.setText("✔ Disponible");
        }
        setOpaque(true);
        updateTooltip();
    }

    public void updateTooltip() {
        if (space.isSpaceTaken() && space.getVehicle() != null) {
            setToolTipText("<html><pre>" + space.getVehicle().toString() + "</pre></html>");
        } else {
            setToolTipText("Espacio disponible");
        }
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

}
