/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

/**
 *
 * @author jimen
 */
public class HomeDesktop extends JDesktopPane {

    private ImageIcon backgroundImagen;

    public HomeDesktop() {

        this.setSize(800, 700);

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Dimension size = getSize();

        ImageIcon icon = new ImageIcon(
                getClass().getResource("/background_program.png")
        );

        g.drawImage(icon.getImage(), 0, 0, size.width, size.height, this);
    }
}
