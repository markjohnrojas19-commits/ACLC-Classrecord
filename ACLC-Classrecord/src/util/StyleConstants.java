package util;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

public final class StyleConstants {

    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font SMALL_BOLD_FONT = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font TABLE_HEADER_FONT = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font TAB_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    public static final Color PRIMARY = new Color(47, 84, 150);
    public static final Color PRIMARY_LIGHT = new Color(92, 138, 210);
    public static final Color SURFACE = new Color(248, 249, 250);
    public static final Color SURFACE_ALT = new Color(241, 243, 245);
    public static final Color BORDER_COLOR = new Color(206, 212, 218);
    public static final Color TEXT_PRIMARY = new Color(33, 37, 41);
    public static final Color TEXT_SECONDARY = new Color(108, 117, 125);
    public static final Color WHITE = new Color(255, 255, 255);
    public static final Color SUCCESS = new Color(25, 135, 84);
    public static final Color DANGER = new Color(220, 53, 69);
    public static final Color TABLE_HEADER_BG = new Color(52, 58, 64);
    public static final Color TABLE_HEADER_FG = new Color(255, 255, 255);
    public static final Color TABLE_ROW_ALT = new Color(248, 249, 250);
    public static final Color SEASON_AVERAGE_BG = new Color(232, 240, 254);

    public static final Border HEADER_BORDER = BorderFactory.createEmptyBorder(10, 20, 10, 20);
    public static final Border INPUT_BORDER = BorderFactory.createEmptyBorder(10, 20, 10, 20);
    public static final Border BUTTON_BORDER = BorderFactory.createEmptyBorder(10, 20, 10, 20);
    public static final Border TABLE_BORDER = BorderFactory.createEmptyBorder(5, 20, 5, 20);
    public static final Border STATS_BORDER = BorderFactory.createEmptyBorder(30, 40, 30, 40);
    public static final Border SECTION_BORDER = BorderFactory.createEmptyBorder(8, 8, 8, 8);

    public static final int GRID_H_GAP = 10;
    public static final int GRID_V_GAP = 8;
    public static final int BUTTON_GAP = 10;
    public static final int TABLE_ROW_HEIGHT = 28;

    private StyleConstants() {
    }
}
