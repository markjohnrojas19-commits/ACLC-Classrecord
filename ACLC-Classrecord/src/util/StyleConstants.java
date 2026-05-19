package util;

import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

public final class StyleConstants {

    public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 18);
    public static final Font BODY_FONT = new Font("SansSerif", Font.PLAIN, 16);
    public static final Font SMALL_BOLD_FONT = new Font("SansSerif", Font.BOLD, 13);

    public static final Border HEADER_BORDER = BorderFactory.createEmptyBorder(15, 20, 5, 20);
    public static final Border INPUT_BORDER = BorderFactory.createEmptyBorder(15, 20, 10, 20);
    public static final Border BUTTON_BORDER = BorderFactory.createEmptyBorder(5, 20, 15, 20);
    public static final Border TABLE_BORDER = BorderFactory.createEmptyBorder(5, 20, 5, 20);
    public static final Border STATS_BORDER = BorderFactory.createEmptyBorder(30, 40, 30, 40);

    public static final int GRID_H_GAP = 10;
    public static final int GRID_V_GAP = 8;
    public static final int BUTTON_GAP = 10;

    private StyleConstants() {
    }
}
