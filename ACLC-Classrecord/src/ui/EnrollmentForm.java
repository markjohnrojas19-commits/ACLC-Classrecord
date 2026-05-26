package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import dao.EnrollmentDao;
import dao.StudentDao;
import dao.SubjectDao;
import model.Student;
import model.Subject;
import model.User;
import util.StyleConstants;

public class EnrollmentForm extends JFrame {

    private EnrollmentFilterPanel filterPanel;
    private EnrollmentDao enrollmentDao;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<Student> currentStudents;

    public EnrollmentForm(User currentUser) {
        enrollmentDao = new EnrollmentDao();
        currentStudents = new ArrayList<>();

        setTitle("ACLC Class Record \u2014 Enrollment");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        add(createHeaderPanel(currentUser), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        populateDropdowns();
        filterPanel.addFilterListener(e -> refreshTable());
    }

    private JPanel createHeaderPanel(User currentUser) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(StyleConstants.HEADER_BORDER);
        panel.setBackground(StyleConstants.WHITE);

        JLabel titleLabel = new JLabel("Enrollment Management");
        titleLabel.setFont(StyleConstants.TITLE_FONT);
        titleLabel.setForeground(StyleConstants.PRIMARY);

        JButton backButton = new JButton("Back to Dashboard");
        backButton.addActionListener(e -> handleBack(currentUser));

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(backButton, BorderLayout.EAST);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        filterPanel = new EnrollmentFilterPanel();
        createTable();

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private void createTable() {
        String[] columns = {"Enroll", "Student ID", "Name", "Course", "Year"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) {
                    return Boolean.class;
                }
                return Object.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(StyleConstants.TABLE_ROW_HEIGHT);
        table.setFont(StyleConstants.BODY_FONT);
        table.setGridColor(StyleConstants.BORDER_COLOR);
        table.getColumnModel().getColumn(0).setMaxWidth(60);
        table.setDefaultRenderer(Object.class, createAlternatingRenderer());
        styleTableHeader(table);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER,
            StyleConstants.BUTTON_GAP, StyleConstants.BUTTON_GAP));
        panel.setBorder(StyleConstants.BUTTON_BORDER);

        JButton saveButton = new JButton("Save");
        JButton enrollAllButton = new JButton("Enroll All");
        JButton unenrollAllButton = new JButton("Unenroll All");

        saveButton.addActionListener(e -> handleSave());
        enrollAllButton.addActionListener(e -> handleEnrollAll());
        unenrollAllButton.addActionListener(e -> handleUnenrollAll());

        panel.add(saveButton);
        panel.add(enrollAllButton);
        panel.add(unenrollAllButton);

        return panel;
    }

    private void populateDropdowns() {
        List<Subject> subjects = new SubjectDao().getAll();
        List<String> sections = extractSections(new StudentDao().getAll());

        filterPanel.populateSubjects(subjects);
        filterPanel.populateSections(sections);
    }

    private List<String> extractSections(List<Student> students) {
        Set<String> sections = new LinkedHashSet<>();
        for (Student student : students) {
            sections.add(student.getSection());
        }
        return new ArrayList<>(sections);
    }

    private void refreshTable() {
        Subject subject = filterPanel.getSelectedSubject();
        String section = filterPanel.getSelectedSection();

        if (subject == null || section == null) {
            tableModel.setRowCount(0);
            currentStudents.clear();
            return;
        }

        currentStudents = getStudentsBySection(section);
        tableModel.setRowCount(0);

        for (Student student : currentStudents) {
            boolean enrolled = enrollmentDao.isEnrolled(
                student.getStudentId(), subject.getSubjectId());

            tableModel.addRow(new Object[]{
                enrolled,
                student.getStudentId(),
                student.getFirstname() + " " + student.getLastname(),
                student.getCourse(),
                student.getYearLevel()
            });
        }
    }

    private List<Student> getStudentsBySection(String section) {
        List<Student> filtered = new ArrayList<>();
        for (Student student : new StudentDao().getAll()) {
            if (student.getSection().equals(section)) {
                filtered.add(student);
            }
        }
        return filtered;
    }

    private void handleSave() {
        Subject subject = filterPanel.getSelectedSubject();
        if (subject == null) {
            showError("Please select a subject.");
            return;
        }

        int enrolled = 0;
        int unenrolled = 0;

        for (int row = 0; row < tableModel.getRowCount(); row++) {
            boolean checked = (boolean) tableModel.getValueAt(row, 0);
            String studentId = currentStudents.get(row).getStudentId();
            boolean currentlyEnrolled = enrollmentDao.isEnrolled(studentId, subject.getSubjectId());

            if (checked && !currentlyEnrolled) {
                enrollmentDao.enroll(studentId, subject.getSubjectId());
                enrolled++;
            } else if (!checked && currentlyEnrolled) {
                enrollmentDao.unenrollByStudentAndSubject(studentId, subject.getSubjectId());
                unenrolled++;
            }
        }

        showResult(enrolled, unenrolled);
        refreshTable();
    }

    private void handleEnrollAll() {
        setAllCheckboxes(true);
    }

    private void handleUnenrollAll() {
        setAllCheckboxes(false);
    }

    private void setAllCheckboxes(boolean value) {
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            tableModel.setValueAt(value, row, 0);
        }
    }

    private void showResult(int enrolled, int unenrolled) {
        if (enrolled == 0 && unenrolled == 0) {
            JOptionPane.showMessageDialog(this, "No changes to save.",
                "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String message = "Enrolled: " + enrolled + ", Unenrolled: " + unenrolled;
        JOptionPane.showMessageDialog(this, message,
            "Enrollment Updated", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleBack(User currentUser) {
        new DashboardForm(currentUser).setVisible(true);
        dispose();
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
