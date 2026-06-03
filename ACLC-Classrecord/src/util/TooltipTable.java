package util;

import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.TableModel;

public class TooltipTable extends JTable {

    public TooltipTable(TableModel model) {
        super(model);
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        int row = rowAtPoint(event.getPoint());
        int col = columnAtPoint(event.getPoint());
        if (row < 0 || col < 0) {
            return null;
        }
        return formatCellText(getValueAt(row, col));
    }

    private String formatCellText(Object value) {
        if (value == null) {
            return null;
        }
        String text = value.toString().trim();
        if (text.isEmpty()) {
            return null;
        }
        return text;
    }
}
