/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.border.Border;

/**
 *
 * @author jimen
 */
public class UITheme {

    public static final Color PRIMARY = new Color(41, 128, 185);
    public static final Color SECONDARY = new Color(52, 152, 219);
    public static final Color SUCCESS = new Color(46, 204, 113);
    public static final Color DANGER = new Color(231, 76, 60);
    public static final Color WARNING = new Color(241, 196, 15);

    public static final Color BACKGROUND = new Color(236, 240, 241);
    public static final Color PANEL_BG = Color.WHITE;
    public static final Color DARK = new Color(44, 62, 80);
    public static final Color BORDER = new Color(189, 195, 199);

    // ===== FUENTES =====
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    // ===== BORDES =====
    public static Border panelBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );
    }

    // ===== BOTONES =====
    public static void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
    }

    // ===== TABLAS =====
    public static void styleTable(JTable table) {
        table.setFont(TABLE_FONT);
        table.setRowHeight(25);
        table.getTableHeader().setFont(BUTTON_FONT);
        table.getTableHeader().setBackground(PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(SECONDARY);
        table.setSelectionForeground(Color.WHITE);

    }
}
