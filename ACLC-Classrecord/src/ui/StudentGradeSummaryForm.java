package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.print.PrinterException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import dao.AssessmentDao;
import dao.StudentDao;
import dao.SubjectDao;
import model.Assessment;
import model.GradingSeason;
import model.ScoreResult;
import model.Student;
import model.Subject;
import model.User;
import service.GradeComputer;
import util.StyleConstants;

public class StudentGradeSummaryForm extends JFrame {

    private JComboBox<String> sectionBox;
    private JList<Student> studentList;
    private DefaultListModel<Student> studentListModel;
    private JTable summaryTable;
    private AssessmentDao assessmentDao;
    private GradeComputer gradeComputer;

    public StudentGradeSummaryForm(User currentUser) {
        assessmentDao = new AssessmentDao();
        gradeComputer = new GradeComputer();

        setTitle("ACLC Class Record \u2014 Student Grade Summary");
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        add(createHeaderPanel(currentUser), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        populateSectionDropdown();
    }

    public StudentGradeSummaryForm(User currentUser, Student preselected) {
        this(currentUser);
        selectStudent(preselected);
    }

    private JPanel createHeaderPanel(User currentUser) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(StyleConstants.HEADER_BORDER);
        panel.setBackground(StyleConstants.WHITE);

        JLabel titleLabel = new JLabel("Student Grade Summary");
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

        panel.add(createStudentSidebar(), BorderLayout.WEST);
        panel.add(createTablePanel(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStudentSidebar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(260, 0));

        TitledBorder titledBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(StyleConstants.BORDER_COLOR, 1),
            "Students", TitledBorder.LEFT, TitledBorder.TOP,
            StyleConstants.SMALL_BOLD_FONT, StyleConstants.TEXT_SECONDARY);

        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 10, 5, 0), titledBorder));

        panel.add(createSectionFilter(), BorderLayout.NORTH);
        panel.add(createStudentList(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSectionFilter() {
        JPanel panel = new JPanel(new BorderLayout(
            StyleConstants.GRID_H_GAP, StyleConstants.GRID_V_GAP));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.setBackground(StyleConstants.WHITE);

        JLabel sectionLabel = new JLabel("Course/Section:");
        sectionLabel.setFont(StyleConstants.BODY_FONT);

        sectionBox = new JComboBox<>();
        sectionBox.setFont(StyleConstants.BODY_FONT);
        sectionBox.addActionListener(e -> filterStudentsBySection());

        panel.add(sectionLabel, BorderLayout.NORTH);
        panel.add(sectionBox, BorderLayout.CENTER);

        return panel;
    }

    private JScrollPane createStudentList() {
        studentListModel = new DefaultListModel<>();
        studentList = new JList<>(studentListModel);
        studentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentList.setFont(StyleConstants.BODY_FONT);
        studentList.setFixedCellHeight(30);
        studentList.setCellRenderer(createStudentListRenderer());
        studentList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                refreshSummary();
            }
        });

        JScrollPane scrollPane = new JScrollPane(studentList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        return scrollPane;
    }

    private DefaultListCellRenderer createStudentListRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                Component cell = super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

                if (isSelected) {
                    cell.setBackground(StyleConstants.PRIMARY);
                    cell.setForeground(StyleConstants.WHITE);
                } else {
                    cell.setBackground(index % 2 == 0
                        ? StyleConstants.WHITE : StyleConstants.TABLE_ROW_ALT);
                    cell.setForeground(StyleConstants.TEXT_PRIMARY);
                }

                setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
                return cell;
            }
        };
    }

    private JPanel createTablePanel() {
        summaryTable = createSummaryTable();

        JScrollPane scrollPane = new JScrollPane(summaryTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel panel = new JPanel(new BorderLayout());

        TitledBorder titledBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(StyleConstants.BORDER_COLOR, 1),
            "Grade Summary", TitledBorder.LEFT, TitledBorder.TOP,
            StyleConstants.SMALL_BOLD_FONT, StyleConstants.TEXT_SECONDARY);

        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 0, 5, 10), titledBorder));

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JTable createSummaryTable() {
        String[] columns = {"Subject Code", "Subject Name", "Prelim", "Midterm",
                            "Pre-Final", "Final", "Final Grade", "Remarks"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(StyleConstants.TABLE_ROW_HEIGHT);
        table.setFont(StyleConstants.BODY_FONT);
        table.setGridColor(StyleConstants.BORDER_COLOR);
        table.setDefaultRenderer(Object.class, createGradeRenderer());
        styleTableHeader(table);

        return table;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panel.setBorder(StyleConstants.BUTTON_BORDER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        JButton printButton = new JButton("Print");
        printButton.addActionListener(e -> handlePrint());
        buttons.add(printButton);

        panel.add(createButtonGroup("Export", buttons));

        return panel;
    }

    private JPanel createButtonGroup(String title, JPanel buttons) {
        JPanel panel = new JPanel(new BorderLayout());

        TitledBorder titledBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(StyleConstants.BORDER_COLOR, 1),
            title, TitledBorder.LEFT, TitledBorder.TOP,
            StyleConstants.SMALL_BOLD_FONT, StyleConstants.TEXT_SECONDARY);

        panel.setBorder(BorderFactory.createCompoundBorder(
            titledBorder, BorderFactory.createEmptyBorder(5, 5, 8, 5)));

        panel.add(buttons, BorderLayout.CENTER);
        return panel;
    }

    private void populateSectionDropdown() {
        List<String> sections = new StudentDao().getAllSections();
        sectionBox.addItem("All Sections");
        for (String section : sections) {
            sectionBox.addItem(section);
        }
        filterStudentsBySection();
    }

    private void filterStudentsBySection() {
        studentListModel.clear();
        String selected = (String) sectionBox.getSelectedItem();
        List<Student> students = new StudentDao().getAll();

        for (Student student : students) {
            if ("All Sections".equals(selected) || student.getCourseSection().equals(selected)) {
                studentListModel.addElement(student);
            }
        }

        if (!studentListModel.isEmpty()) {
            studentList.setSelectedIndex(0);
        }
    }

    private void selectStudent(Student preselected) {
        sectionBox.setSelectedItem(preselected.getCourseSection());

        for (int i = 0; i < studentListModel.size(); i++) {
            Student item = studentListModel.getElementAt(i);
            if (item.getStudentId().equals(preselected.getStudentId())) {
                studentList.setSelectedIndex(i);
                studentList.ensureIndexIsVisible(i);
                return;
            }
        }
    }

    private void refreshSummary() {
        Student selected = studentList.getSelectedValue();
        if (selected == null) {
            clearTable();
            return;
        }

        List<Assessment> assessments = assessmentDao.getByStudent(selected.getStudentId());
        populateSummaryTable(assessments);
    }

    private void populateSummaryTable(List<Assessment> assessments) {
        DefaultTableModel model = (DefaultTableModel) summaryTable.getModel();
        model.setRowCount(0);

        Map<Integer, String[]> subjectInfo = buildSubjectInfoMap();
        Map<Integer, Map<GradingSeason, List<Assessment>>> grouped =
            groupBySubject(assessments);

        for (Map.Entry<Integer, Map<GradingSeason, List<Assessment>>> entry : grouped.entrySet()) {
            int subjectId = entry.getKey();
            Map<GradingSeason, List<Assessment>> seasonMap = entry.getValue();

            String[] info = subjectInfo.getOrDefault(subjectId,
                new String[]{String.valueOf(subjectId), "Unknown"});

            ScoreResult weightedResult = gradeComputer.computeFinalGrade(seasonMap);

            model.addRow(new Object[]{
                info[0],
                info[1],
                formatSeasonAverage(seasonMap, GradingSeason.PRELIM),
                formatSeasonAverage(seasonMap, GradingSeason.MIDTERM),
                formatSeasonAverage(seasonMap, GradingSeason.PRE_FINAL),
                formatSeasonAverage(seasonMap, GradingSeason.FINAL),
                String.format("%.2f", weightedResult.getFinalGrade()),
                weightedResult.getRemarks()
            });
        }
    }

    private String formatSeasonAverage(Map<GradingSeason, List<Assessment>> seasonMap,
                                       GradingSeason season) {
        ScoreResult result = gradeComputer.computeAverage(seasonMap.get(season));
        return String.format("%.2f", result.getFinalGrade());
    }

    private Map<Integer, Map<GradingSeason, List<Assessment>>> groupBySubject(
            List<Assessment> assessments) {
        Map<Integer, Map<GradingSeason, List<Assessment>>> grouped = new LinkedHashMap<>();

        for (Assessment a : assessments) {
            int subjectId = a.getSubjectId();

            Map<GradingSeason, List<Assessment>> seasonMap = grouped.get(subjectId);
            if (seasonMap == null) {
                seasonMap = new HashMap<>();
                grouped.put(subjectId, seasonMap);
            }

            List<Assessment> list = seasonMap.get(a.getSeason());
            if (list == null) {
                list = new ArrayList<>();
                seasonMap.put(a.getSeason(), list);
            }
            list.add(a);
        }

        return grouped;
    }

    private Map<Integer, String[]> buildSubjectInfoMap() {
        Map<Integer, String[]> map = new HashMap<>();
        for (Subject subject : new SubjectDao().getAll()) {
            map.put(subject.getSubjectId(),
                new String[]{subject.getSubjectCode(), subject.getSubjectName()});
        }
        return map;
    }

    private void clearTable() {
        DefaultTableModel model = (DefaultTableModel) summaryTable.getModel();
        model.setRowCount(0);
    }

    private void handlePrint() {
        if (summaryTable.getRowCount() == 0) {
            showError("No data to print.");
            return;
        }

        Student selected = studentList.getSelectedValue();
        String studentName = (selected != null) ? selected.toString() : "Unknown";

        try {
            MessageFormat header = new MessageFormat("Grade Summary — " + studentName);
            MessageFormat footer = new MessageFormat("Page {0}");
            summaryTable.print(JTable.PrintMode.FIT_WIDTH, header, footer);
        } catch (PrinterException ex) {
            showError("Printing failed: " + ex.getMessage());
        }
    }

    private void handleBack(User currentUser) {
        new DashboardForm(currentUser).setVisible(true);
        dispose();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void styleTableHeader(JTable table) {
        JTableHeader header = table.getTableHeader();
        header.setBackground(StyleConstants.TABLE_HEADER_BG);
        header.setForeground(StyleConstants.TABLE_HEADER_FG);
        header.setFont(StyleConstants.TABLE_HEADER_FONT);
    }

    private DefaultTableCellRenderer createGradeRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(
                    t, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    cell.setBackground(row % 2 == 0
                        ? StyleConstants.WHITE : StyleConstants.TABLE_ROW_ALT);

                    int remarksCol = 7;
                    int finalGradeCol = 6;
                    String remarks = (String) t.getModel().getValueAt(row, remarksCol);

                    if (column == remarksCol || column == finalGradeCol) {
                        if ("PASSED".equals(remarks)) {
                            cell.setForeground(StyleConstants.SUCCESS);
                        } else {
                            cell.setForeground(StyleConstants.DANGER);
                        }
                    } else {
                        cell.setForeground(StyleConstants.TEXT_PRIMARY);
                    }
                }
                return cell;
            }
        };
    }
}
