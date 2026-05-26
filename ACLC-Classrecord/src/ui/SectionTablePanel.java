package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import model.Student;
import util.StyleConstants;

public class SectionTablePanel extends JPanel {

    private JTable table;

    public SectionTablePanel() {
        setLayout(new BorderLayout());
        setBorder(StyleConstants.SECTION_BORDER);
        setBackground(StyleConstants.WHITE);
        table = createTable();
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public JTable getTable() {
        return table;
    }

    public int getSelectedRow() {
        return table.getSelectedRow();
    }

    public Object getValueAt(int row, int column) {
        return table.getValueAt(row, column);
    }

    public void populate(List<Student> students) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        for (Student student : students) {
            model.addRow(new Object[]{
                student.getStudentId(),
                student.getFirstname(),
                student.getLastname(),
                student.getCourse(),
                student.getYearLevel(),
                student.getSection(),
                student.getGender()
            });
        }
    }

    private JTable createTable() {
        String[] columns = {"Student ID", "First Name", "Last Name",
                            "Course", "Year", "Section", "Gender"};

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable newTable = new JTable(model);
        newTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        newTable.setRowHeight(StyleConstants.TABLE_ROW_HEIGHT);
        newTable.setFont(StyleConstants.BODY_FONT);
        newTable.setGridColor(StyleConstants.BORDER_COLOR);
        newTable.setDefaultRenderer(Object.class, createAlternatingRenderer());
        styleTableHeader(newTable);

        return newTable;
    }

    private void styleTableHeader(JTable targetTable) {
        JTableHeader header = targetTable.getTableHeader();
        header.setBackground(StyleConstants.TABLE_HEADER_BG);
        header.setForeground(StyleConstants.TABLE_HEADER_FG);
        header.setFont(StyleConstants.TABLE_HEADER_FONT);
    }

    private DefaultTableCellRenderer createAlternatingRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(
                    t, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    cell.setBackground(row % 2 == 0
                        ? StyleConstants.WHITE : StyleConstants.TABLE_ROW_ALT);
                }
                return cell;
            }
        };
    }
}
