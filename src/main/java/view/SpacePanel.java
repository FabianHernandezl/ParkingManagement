package view;

import model.entities.Space;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SpacePanel extends JPanel {

    private final Space space;
    private boolean selected = false;

    public SpacePanel(Space space, SpaceView parent) {
        this.space = space;

        setPreferredSize(new Dimension(120, 80));
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        setLayout(new BorderLayout());

        JLabel lbl = new JLabel("Espacio " + space.getId(), SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        add(lbl, BorderLayout.CENTER);

        updateColor();
        updateTooltip();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                // Selecciona visualmente el espacio
                parent.setSelectedPanel(SpacePanel.this);

                // 👉 SI EL ESPACIO ESTÁ OCUPADO, MOSTRAR VEHÍCULO
                if (space.isSpaceTaken() && space.getVehicle() != null) {
                    JOptionPane.showMessageDialog(
                            SpacePanel.this,
                            space.getVehicle().toString(),
                            "Información del Vehículo",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
        });

    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        setBorder(BorderFactory.createLineBorder(
                selected ? Color.YELLOW : Color.BLACK,
                selected ? 4 : 2
        ));
    }

    public void updateColor() {
        if (space.isSpaceTaken()) {
            setBackground(Color.RED);
        } else if (space.isDisabilityAdaptation()) {
            setBackground(Color.CYAN);
        } else {
            setBackground(Color.GREEN);
        }
        setOpaque(true);
    }

    public void updateTooltip() {
        if (space.isSpaceTaken() && space.getVehicle() != null) {
            setToolTipText("<html>" + space.getVehicle().toString().replace("\n", "<br>") + "</html>");
        } else {
            setToolTipText("Espacio disponible");
        }
    }

    public Space getSpace() {
        return space;
    }
}
