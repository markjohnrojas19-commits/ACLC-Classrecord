package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JButton;
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

import dao.StudentDao;
import model.Student;
import util.StyleConstants;

public class StudentImportPreviewForm extends JDialog {

    private List<Student> validStudents;
    private List<String> errorMessages;
    private JTable previewTable;
    private Runnable onImportComplete;

    public StudentImportPreviewForm(JFrame parent, List<Student> validStudents,
            List<String> errorMessages, Runnable onImportComplete) {
        super(parent, "Import Preview", true);
        this.validStudents = validStudents;
        this.errorMessages = errorMessages;
        this.onImportComplete = onImportComplete;

        setSize(900, 500);
        setLocationRelativeTo(parent);

        add(createSummaryLabel(), BorderLayout.NORTH);
        add(createPreviewTable(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JLabel createSummaryLabel() {
        String text = "  Valid: " + validStudents.size()
            + "  |  Errors: " + errorMessages.size();
        JLabel label = new JLabel(text);
        label.setFont(StyleConstants.SMALL_BOLD_FONT);
        label.setBorder(StyleConstants.HEADER_BORDER);
        return label;
    }

    private JScrollPane createPreviewTable() {
        String[] columns = {"Status", "Student ID", "First Name", "Last Name",
            "Course", "Year Level", "Section", "Gender"};

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        addValidRows(model);
        addErrorRows(model);

        previewTable = new JTable(model);
        previewTable.setRowHeight(StyleConstants.TABLE_ROW_HEIGHT);
        previewTable.setFont(StyleConstants.BODY_FONT);
        previewTable.setGridColor(StyleConstants.BORDER_COLOR);
        previewTable.setDefaultRenderer(Object.class, createStatusRenderer());
        styleTableHeader(previewTable);

        return new JScrollPane(previewTable);
    }

    private void addValidRows(DefaultTableModel model) {
        for (Student student : validStudents) {
            model.addRow(new Object[]{
                "Valid",
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

    private void addErrorRows(DefaultTableModel model) {
        for (String error : errorMessages) {
            model.addRow(new Object[]{
                "Error", error, "", "", "", "", "", ""
            });
        }
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER,
            StyleConstants.BUTTON_GAP, StyleConstants.BUTTON_GAP));
        panel.setBorder(StyleConstants.BUTTON_BORDER);

        JButton importButton = new JButton("Import All Valid (" + validStudents.size() + ")");
        JButton cancelButton = new JButton("Cancel");

        importButton.addActionListener(e -> handleImport());
        cancelButton.addActionListener(e -> dispose());

        importButton.setEnabled(!validStudents.isEmpty());

        panel.add(importButton);
        panel.add(cancelButton);

        return panel;
    }

    private void handleImport() {
        StudentDao studentDao = new StudentDao();
        int imported = 0;
        int duplicates = 0;

        for (Student student : validStudents) {
            if (studentDao.add(student)) {
                imported++;
            } else {
                duplicates++;
            }
        }

        showImportResult(imported, duplicates);
        onImportComplete.run();
        dispose();
    }

    private void showImportResult(int imported, int duplicates) {
        String message = "Imported: " + imported + " students.";
        if (duplicates > 0) {
            message += "\nSkipped: " + duplicates + " (duplicate Student IDs).";
        }
        if (!errorMessages.isEmpty()) {
            message += "\nErrors: " + errorMessages.size() + " rows had invalid data.";
        }
        JOptionPane.showMessageDialog(this, message,
            "Import Complete", JOptionPane.INFORMATION_MESSAGE);
    }

    private DefaultTableCellRenderer createStatusRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(
                    t, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    String status = (String) t.getModel().getValueAt(row, 0);
                    cell.setBackground(getRowColor(status));
                }
                return cell;
            }
        };
    }

    private Color getRowColor(String status) {
        if ("Error".equals(status)) {
            return new Color(255, 230, 230);
        }
        return new Color(230, 255, 230);
    }

    private void styleTableHeader(JTable targetTable) {
        JTableHeader header = targetTable.getTableHeader();
        header.setBackground(StyleConstants.TABLE_HEADER_BG);
        header.setForeground(StyleConstants.TABLE_HEADER_FG);
        header.setFont(StyleConstants.TABLE_HEADER_FONT);
    }
}
