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
        if (spaces == null) {
            return;
        }

        int x = 50;
        int y = 50;
        int spacesPerRow = Math.max(1, (getWidth() - 100) / 130);
        int spaceCount = 0;

        for (Space space : spaces) {
            if (space != null && mx >= x && mx <= x + 100 && my >= y && my <= y + 60) {

                if (!space.isSpaceTaken()) {
                    // Mostrar diálogo para asignar cliente y vehículo
                    showAssignDialog(space);

                } else {
                    // Mostrar opciones para liberar espacio o ver detalles
                    showOccupiedSpaceOptions(space);
                }
                return;
            }

            spaceCount++;
            x += 130;
            if (spaceCount % spacesPerRow == 0) {
                x = 50;
                y += 80;
            }
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

    private void showAssignDialog(Space space) {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);

        if (parentWindow instanceof JFrame) {
            AssignSpaceDialog dialog = new AssignSpaceDialog((JFrame) parentWindow, space, controller);
            dialog.setVisible(true);

            if (dialog.wasAssigned()) {
                // 1. Buscamos el espacio actualizado en el origen de datos (JSON)
                Space updatedSpace = controller.findSpaceById(space.getId());

                if (updatedSpace != null) {
                    // 2. Actualizamos nuestra referencia en el arreglo local Space[]
                    for (int i = 0; i < spaces.length; i++) {
                        if (spaces[i] != null && spaces[i].getId() == space.getId()) {
                            spaces[i] = updatedSpace; // Ahora tiene el Client y Vehicle
                            break;
                        }
                    }

                    // 3. Disparamos la animación
                    animatingSpace = updatedSpace;
                    animateCarEntry(getSpacePositionX(updatedSpace));

                    // 4. Refrescamos la UI
                    infoPanel.showSpaceInfo(updatedSpace);
                    repaint();
                }
            }
        }
    }

    private int getSpacePositionX(Space space) {
        // Calcular posición X del espacio en el panel
        if (spaces == null) {
            return 50;
        }

        for (int i = 0; i < spaces.length; i++) {
            if (spaces[i] != null && spaces[i].getId() == space.getId()) {
                int spacesPerRow = Math.max(1, (getWidth() - 100) / 130);
                int col = i % spacesPerRow;
                return 50 + (col * 130) + 30; // +30 para centrar el carro
            }
        }
        return 50;
    }

    private void showOccupiedSpaceOptions(Space space) {
        if (space.getClient() == null || space.getVehicle() == null) {
            JOptionPane.showMessageDialog(this,
                    "Este espacio está marcado como ocupado pero no tiene información de cliente/vehículo.",
                    "Error de datos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object[] options = {"Ver Detalles", "Liberar Espacio", "Cancelar"};

        int choice = JOptionPane.showOptionDialog(this,
                "Espacio #" + space.getId() + " está ocupado\n"
                + "Cliente: " + space.getClient().getName() + "\n"
                + "Vehículo: " + space.getVehicle().getPlate(),
                "Espacio Ocupado",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);

        switch (choice) {
            case 0: // Ver Detalles
                showSpaceDetails(space);
                break;
            case 1: // Liberar Espacio
                liberateSpace(space);
                break;
            // Cancelar no hace nada
        }
    }

    private void showSpaceDetails(Space space) {
        StringBuilder details = new StringBuilder();
        details.append("DETALLES DEL ESPACIO\n");
        details.append("══════════════════════\n\n");
        details.append("Espacio #").append(space.getId()).append("\n");
        details.append("Tipo: ").append(space.getVehicleType().getDescription()).append("\n");
        details.append("Accesibilidad: ").append(space.isDisabilityAdaptation() ? "Adaptado para discapacidad" : "Estándar").append("\n");
        details.append("Estado: OCUPADO\n\n");

        if (space.getClient() != null) {
            details.append("CLIENTE:\n");
            details.append("────────\n");
            details.append("• ID: ").append(space.getClient().getId()).append("\n");
            details.append("• Nombre: ").append(space.getClient().getName()).append("\n");
            details.append("• Teléfono: ").append(space.getClient().getPhone()).append("\n");
            details.append("• Preferencial: ").append(space.getClient().isIsPreferential() ? "Sí" : "No").append("\n\n");
        }

        if (space.getVehicle() != null) {
            details.append("VEHÍCULO:\n");
            details.append("─────────\n");
            details.append("• Placa: ").append(space.getVehicle().getPlate()).append("\n");
            details.append("• Marca: ").append(space.getVehicle().getBrand()).append("\n");
            details.append("• Modelo: ").append(space.getVehicle().getModel()).append("\n");
            details.append("• Color: ").append(space.getVehicle().getColor()).append("\n");
            details.append("• Tipo: ").append(space.getVehicle().getVehicleType().getDescription()).append("\n\n");
        }

        if (space.getEntryTime() != null) {
            details.append("INFORMACIÓN DE ESTACIONAMIENTO:\n");
            details.append("───────────────────────────────\n");
            details.append("• Hora de entrada: ").append(space.getEntryTime()).append("\n");

            // Calcular tiempo transcurrido
            long diff = System.currentTimeMillis() - space.getEntryTime().getTime();
            long hours = diff / (1000 * 60 * 60);
            long minutes = (diff % (1000 * 60 * 60)) / (1000 * 60);

            details.append("• Tiempo transcurrido: ").append(hours).append("h ").append(minutes).append("m\n");

            // Calcular tarifa estimada
            if (space.getVehicle() != null && space.getVehicle().getVehicleType() != null) {
                float hourlyRate = space.getVehicle().getVehicleType().getFee();
                float estimatedFee = Math.max(1, hours) * hourlyRate;
                details.append("• Tarifa estimada: $").append(String.format("%.2f", estimatedFee)).append("\n");
            }
        }

        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setBackground(new Color(248, 248, 248));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(this, scrollPane,
                "Detalles Espacio #" + space.getId(),
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void liberateSpace(Space space) {
        String clientName = space.getClient() != null ? space.getClient().getName() : "Desconocido";
        String vehiclePlate = space.getVehicle() != null ? space.getVehicle().getPlate() : "Desconocida";

        // Calcular tarifa si hay hora de entrada
        String feeMessage = "";
        if (space.getEntryTime() != null && space.getVehicle() != null
                && space.getVehicle().getVehicleType() != null) {

            long diff = System.currentTimeMillis() - space.getEntryTime().getTime();
            long hours = Math.max(1, diff / (1000 * 60 * 60));
            float hourlyRate = space.getVehicle().getVehicleType().getFee();
            float totalFee = hours * hourlyRate;

            feeMessage = "\n\nTarifa a cobrar: $" + String.format("%.2f", totalFee)
                    + "\n(" + hours + " hora(s) × $" + hourlyRate + ")";
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Liberar espacio #" + space.getId() + "?\n\n"
                + "Cliente: " + clientName + "\n"
                + "Vehículo: " + vehiclePlate + feeMessage,
                "Confirmar Liberación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = controller.releaseSpace(space.getId());

            if (success) {
                // Animar salida del vehículo
                animatingSpace = space;
                animateCarExit(space, getSpacePositionX(space));

                JOptionPane.showMessageDialog(this,
                        "Espacio liberado exitosamente"
                        + (feeMessage.isEmpty() ? "" : "\nTarifa cobrada: " + feeMessage.substring(feeMessage.indexOf("$"))),
                        "Liberación Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);

                // Actualizar la visualización después de la animación
                Timer timer = new Timer(1000, e -> {
                    Space updatedSpace = controller.findSpaceById(space.getId());
                    if (updatedSpace != null) {
                        for (int i = 0; i < spaces.length; i++) {
                            if (spaces[i].getId() == space.getId()) {
                                spaces[i] = updatedSpace;
                                break;
                            }
                        }
                        repaint();
                        infoPanel.showSpaceInfo(updatedSpace);
                    }
                });
                timer.setRepeats(false);
                timer.start();

            } else {
                JOptionPane.showMessageDialog(this,
                        "Error al liberar el espacio",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
