package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import dao.AttendanceDao;
import dao.EnrollmentDao;
import dao.StudentDao;
import dao.SubjectDao;
import model.Attendance;
import model.AttendanceStatus;
import model.Student;
import model.Subject;
import model.User;
import util.StyleConstants;

public class AttendanceForm extends JFrame {

    private AttendanceFilterPanel filterPanel;
    private AttendanceDao attendanceDao;
    private EnrollmentDao enrollmentDao;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<Student> currentStudents;

    public AttendanceForm(User currentUser) {
        attendanceDao = new AttendanceDao();
        enrollmentDao = new EnrollmentDao();
        currentStudents = new ArrayList<>();

        setTitle("ACLC Class Record \u2014 Attendance");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        add(createHeaderPanel(currentUser), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        populateSubjects();
        attachFilterListeners();
    }

    private JPanel createHeaderPanel(User currentUser) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(StyleConstants.HEADER_BORDER);
        panel.setBackground(StyleConstants.WHITE);

        JLabel titleLabel = new JLabel("Attendance Management");
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

        filterPanel = new AttendanceFilterPanel();
        createTable();

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private void createTable() {
        String[] columns = {"Student ID", "Name", "Status"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(StyleConstants.TABLE_ROW_HEIGHT);
        table.setFont(StyleConstants.BODY_FONT);
        table.setGridColor(StyleConstants.BORDER_COLOR);
        table.setDefaultRenderer(Object.class, createAlternatingRenderer());
        styleTableHeader(table);

        JComboBox<String> statusEditor = createStatusComboBox();
        table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(statusEditor));
    }

    private JComboBox<String> createStatusComboBox() {
        JComboBox<String> box = new JComboBox<>();
        for (AttendanceStatus status : AttendanceStatus.values()) {
            box.addItem(status.toDisplayName());
        }
        return box;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER,
            StyleConstants.BUTTON_GAP, StyleConstants.BUTTON_GAP));
        panel.setBorder(StyleConstants.BUTTON_BORDER);

        JButton saveButton = new JButton("Save");
        JButton markAllPresentButton = new JButton("Mark All Present");

        saveButton.addActionListener(e -> handleSave());
        markAllPresentButton.addActionListener(e -> handleMarkAllPresent());

        panel.add(saveButton);
        panel.add(markAllPresentButton);

        return panel;
    }

    private void populateSubjects() {
        List<Subject> subjects = new SubjectDao().getAll();
        filterPanel.populateSubjects(subjects);
    }

    private void attachFilterListeners() {
        filterPanel.addSubjectListener(e -> refreshSections());
        filterPanel.addSectionListener(e -> refreshDatesAndTable());
        filterPanel.addDateListener(e -> refreshTable());
    }

    private void refreshSections() {
        Subject subject = filterPanel.getSelectedSubject();
        if (subject == null) {
            filterPanel.populateSections(new ArrayList<>());
            return;
        }

        List<String> sections = enrollmentDao.getSectionsBySubject(subject.getSubjectId());
        filterPanel.populateSections(sections);
    }

    private void refreshDatesAndTable() {
        refreshDates();
        refreshTable();
    }

    private void refreshDates() {
        Subject subject = filterPanel.getSelectedSubject();
        String section = filterPanel.getSelectedSection();

        if (subject == null || section == null) {
            filterPanel.populateDates(new ArrayList<>());
            return;
        }

        List<LocalDate> dates = attendanceDao.getDatesBySubjectAndSection(
            subject.getSubjectId(), section);
        filterPanel.populateDates(dates);
    }

    private void refreshTable() {
        if (filterPanel.isDateRangeMode()) {
            refreshTableForRange();
        } else {
            refreshTableForSingleDate();
        }
    }

    private void refreshTableForSingleDate() {
        Subject subject = filterPanel.getSelectedSubject();
        String section = filterPanel.getSelectedSection();
        LocalDate date = filterPanel.getStartDate();

        if (subject == null || section == null || date == null) {
            clearTable();
            return;
        }

        currentStudents = enrollmentDao.getStudentsBySubjectAndSection(
            subject.getSubjectId(), section);
        Map<String, AttendanceStatus> existingRecords = loadExistingAttendance(
            subject.getSubjectId(), date);

        applySingleDateModel();

        for (Student student : currentStudents) {
            AttendanceStatus status = existingRecords.getOrDefault(
                student.getStudentId(), AttendanceStatus.PRESENT);

            tableModel.addRow(new Object[]{
                student.getStudentId(),
                student.getFirstname() + " " + student.getLastname(),
                status.toDisplayName()
            });
        }
    }

    private void refreshTableForRange() {
        Subject subject = filterPanel.getSelectedSubject();
        String section = filterPanel.getSelectedSection();
        LocalDate startDate = filterPanel.getStartDate();
        LocalDate endDate = filterPanel.getEndDate();

        if (subject == null || section == null || startDate == null || endDate == null) {
            clearTable();
            return;
        }

        List<Attendance> records = attendanceDao.getBySubjectSectionAndDateRange(
            subject.getSubjectId(), section, startDate, endDate);
        Map<String, String> studentNames = buildStudentNameMap();

        applyRangeModel();

        for (Attendance record : records) {
            String name = studentNames.getOrDefault(record.getStudentId(),
                record.getStudentId());

            tableModel.addRow(new Object[]{
                record.getStudentId(),
                name,
                record.getDate().toString(),
                record.getStatus().toDisplayName()
            });
        }

        currentStudents.clear();
    }

    private void clearTable() {
        tableModel.setRowCount(0);
        currentStudents.clear();
    }

    private void applySingleDateModel() {
        String[] columns = {"Student ID", "Name", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };
        table.setModel(tableModel);

        JComboBox<String> statusEditor = createStatusComboBox();
        table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(statusEditor));
    }

    private void applyRangeModel() {
        String[] columns = {"Student ID", "Name", "Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setModel(tableModel);
    }

    private Map<String, String> buildStudentNameMap() {
        Map<String, String> map = new HashMap<>();
        for (Student student : new StudentDao().getAll()) {
            map.put(student.getStudentId(),
                student.getFirstname() + " " + student.getLastname());
        }
        return map;
    }

    private Map<String, AttendanceStatus> loadExistingAttendance(int subjectId, LocalDate date) {
        Map<String, AttendanceStatus> map = new HashMap<>();
        List<Attendance> records = attendanceDao.getBySubjectAndDate(subjectId, date);

        for (Attendance record : records) {
            map.put(record.getStudentId(), record.getStatus());
        }

        return map;
    }

    private void handleSave() {
        if (filterPanel.isDateRangeMode()) {
            showError("Cannot save in date range view. Use a single date to mark attendance.");
            return;
        }

        Subject subject = filterPanel.getSelectedSubject();
        LocalDate date = filterPanel.getStartDate();

        if (subject == null) {
            showError("Please select a subject.");
            return;
        }
        if (date == null) {
            showError("Please enter a valid date (yyyy-mm-dd).");
            return;
        }
        if (currentStudents.isEmpty()) {
            showError("No students to save attendance for.");
            return;
        }

        stopCellEditing();
        int saved = saveAllRows(subject.getSubjectId(), date);
        showSaveResult(saved);
    }

    private int saveAllRows(int subjectId, LocalDate date) {
        int saved = 0;

        for (int row = 0; row < tableModel.getRowCount(); row++) {
            String statusText = (String) tableModel.getValueAt(row, 2);
            AttendanceStatus status = AttendanceStatus.fromDbValue(statusText);
            String studentId = currentStudents.get(row).getStudentId();

            Attendance attendance = new Attendance(0, studentId, subjectId, date, status);

            if (attendanceDao.saveOrUpdate(attendance)) {
                saved++;
            }
        }

        return saved;
    }

    private void stopCellEditing() {
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }
    }

    private void showSaveResult(int saved) {
        String message = "Attendance saved for " + saved + " of " + currentStudents.size() + " students.";
        JOptionPane.showMessageDialog(this, message,
            "Attendance Saved", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleMarkAllPresent() {
        if (filterPanel.isDateRangeMode()) {
            showError("Cannot modify attendance in date range view. Use a single date.");
            return;
        }
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            tableModel.setValueAt(AttendanceStatus.PRESENT.toDisplayName(), row, 2);
        }
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
