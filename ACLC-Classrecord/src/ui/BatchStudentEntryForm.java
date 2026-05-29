package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import dao.StudentDao;
import model.Student;
import util.StyleConstants;

public class BatchStudentEntryForm extends JDialog {

    private JTable table;
    private DefaultTableModel tableModel;
    private Runnable onSaveComplete;

    private static final int INITIAL_ROWS = 10;

    public BatchStudentEntryForm(JFrame parent, Runnable onSaveComplete) {
        super(parent, "Add Multiple Students", true);
        this.onSaveComplete = onSaveComplete;

        setSize(950, 550);
        setLocationRelativeTo(parent);

        add(createHeaderLabel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JLabel createHeaderLabel() {
        JLabel label = new JLabel("  Fill in student details. Empty rows will be skipped.");
        label.setFont(StyleConstants.SMALL_BOLD_FONT);
        label.setBorder(StyleConstants.HEADER_BORDER);
        return label;
    }

    private JScrollPane createTablePanel() {
        String[] columns = {"Student ID", "First Name", "Last Name",
            "Course", "Year Level", "Section", "Gender"};

        tableModel = new DefaultTableModel(columns, INITIAL_ROWS);
        table = new JTable(tableModel);
        table.setRowHeight(StyleConstants.TABLE_ROW_HEIGHT);
        table.setFont(StyleConstants.BODY_FONT);
        table.setGridColor(StyleConstants.BORDER_COLOR);
        table.setDefaultRenderer(Object.class, createAlternatingRenderer());
        styleTableHeader(table);
        setupGenderColumn();

        return new JScrollPane(table);
    }

    private void setupGenderColumn() {
        JComboBox<String> genderBox = new JComboBox<>(
            new String[]{"", "Male", "Female"});
        TableColumn genderColumn = table.getColumnModel().getColumn(6);
        genderColumn.setCellEditor(new DefaultCellEditor(genderBox));
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER,
            StyleConstants.BUTTON_GAP, StyleConstants.BUTTON_GAP));
        panel.setBorder(StyleConstants.BUTTON_BORDER);

        JButton addRowButton = new JButton("Add 5 Rows");
        JButton saveButton = new JButton("Save All");
        JButton cancelButton = new JButton("Cancel");

        addRowButton.addActionListener(e -> addEmptyRows());
        saveButton.addActionListener(e -> handleSaveAll());
        cancelButton.addActionListener(e -> dispose());

        panel.add(addRowButton);
        panel.add(saveButton);
        panel.add(cancelButton);

        return panel;
    }

    private void addEmptyRows() {
        for (int i = 0; i < 5; i++) {
            tableModel.addRow(new Object[]{"", "", "", "", "", "", ""});
        }
    }

    private void handleSaveAll() {
        stopCellEditing();

        List<Student> students = collectValidRows();
        if (students.isEmpty()) {
            showError("No valid student rows to save.");
            return;
        }

        saveStudents(students);
    }

    private List<Student> collectValidRows() {
        List<Student> students = new ArrayList<>();

        for (int row = 0; row < tableModel.getRowCount(); row++) {
            if (isRowEmpty(row)) {
                continue;
            }

            if (isRowIncomplete(row)) {
                showError("Row " + (row + 1) + ": All fields must be filled.");
                return new ArrayList<>();
            }

            String yearError = validateYearLevel(row);
            if (yearError != null) {
                showError("Row " + (row + 1) + ": " + yearError);
                return new ArrayList<>();
            }

            students.add(buildStudentFromRow(row));
        }

        return students;
    }

    private boolean isRowEmpty(int row) {
        for (int col = 0; col < tableModel.getColumnCount(); col++) {
            String value = getCellText(row, col);
            if (!value.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean isRowIncomplete(int row) {
        for (int col = 0; col < tableModel.getColumnCount(); col++) {
            String value = getCellText(row, col);
            if (value.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private String validateYearLevel(int row) {
        String value = getCellText(row, 4);
        try {
            int year = Integer.parseInt(value);
            if (year < 1 || year > 4) {
                return "Year level must be between 1 and 4.";
            }
            return null;
        } catch (NumberFormatException e) {
            return "Year level must be a number.";
        }
    }

    private Student buildStudentFromRow(int row) {
        return new Student(
            getCellText(row, 0),
            getCellText(row, 1),
            getCellText(row, 2),
            getCellText(row, 3),
            Integer.parseInt(getCellText(row, 4)),
            getCellText(row, 5),
            getCellText(row, 6)
        );
    }

    private String getCellText(int row, int col) {
        Object value = tableModel.getValueAt(row, col);
        return value != null ? value.toString().trim() : "";
    }

    private void saveStudents(List<Student> students) {
        StudentDao studentDao = new StudentDao();
        int saved = 0;
        int duplicates = 0;

        for (Student student : students) {
            if (studentDao.add(student)) {
                saved++;
            } else {
                duplicates++;
            }
        }

        showSaveResult(saved, duplicates);
        onSaveComplete.run();
        dispose();
    }

    private void showSaveResult(int saved, int duplicates) {
        String message = "Saved: " + saved + " students.";
        if (duplicates > 0) {
            message += "\nSkipped: " + duplicates + " (duplicate Student IDs).";
        }
        JOptionPane.showMessageDialog(this, message,
            "Batch Save Complete", JOptionPane.INFORMATION_MESSAGE);
    }

    private void stopCellEditing() {
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
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
