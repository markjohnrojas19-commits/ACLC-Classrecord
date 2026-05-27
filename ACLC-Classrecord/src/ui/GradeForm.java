package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
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

public class GradeForm extends JFrame {

    private AssessmentInputPanel inputPanel;
    private JTabbedPane seasonTabs;
    private AssessmentDao assessmentDao;
    private GradeComputer gradeComputer;
    private JTextField searchField;
    private Map<GradingSeason, List<Assessment>> seasonRecords;

    public GradeForm(User currentUser) {
        assessmentDao = new AssessmentDao();
        gradeComputer = new GradeComputer();
        seasonRecords = new HashMap<>();

        setTitle("ACLC Class Record \u2014 Grade Management");
        setSize(1050, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        add(createHeaderPanel(currentUser), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        populateDropdowns();
        refreshAllTabs();
    }

    private JPanel createHeaderPanel(User currentUser) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(StyleConstants.HEADER_BORDER);
        panel.setBackground(StyleConstants.WHITE);

        JLabel titleLabel = new JLabel("Grade Management");
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

        inputPanel = new AssessmentInputPanel();
        seasonTabs = createSeasonTabs();

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(seasonTabs, BorderLayout.CENTER);

        return panel;
    }

    private JTabbedPane createSeasonTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(StyleConstants.TAB_FONT);

        for (GradingSeason season : GradingSeason.values()) {
            JPanel tabContent = createSeasonTabContent(season);
            tabs.addTab(season.toDisplayName(), tabContent);
        }

        tabs.addTab("Final Grade", createFinalGradeTabContent());

        return tabs;
    }

    private JPanel createSeasonTabContent(GradingSeason season) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(StyleConstants.WHITE);

        JTable table = createAssessmentTable();
        table.getSelectionModel().addListSelectionListener(e -> loadSelectedAssessment());

        JLabel averageLabel = new JLabel("Season Average: \u2014");
        averageLabel.setFont(StyleConstants.SMALL_BOLD_FONT);
        averageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        averageLabel.setOpaque(true);
        averageLabel.setBackground(StyleConstants.SEASON_AVERAGE_BG);
        averageLabel.setForeground(StyleConstants.TEXT_PRIMARY);

        JPanel averagePanel = new JPanel(new BorderLayout());
        averagePanel.setBorder(StyleConstants.SECTION_BORDER);
        averagePanel.add(averageLabel, BorderLayout.CENTER);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(averagePanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createFinalGradeTabContent() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(StyleConstants.WHITE);

        JTable table = createFinalGradeTable();
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private JTable createFinalGradeTable() {
        String[] columns = {"Student", "Subject", "Prelim", "Midterm",
                            "Pre-Final", "Final", "Final Grade", "Remarks"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(StyleConstants.TABLE_ROW_HEIGHT);
        table.setFont(StyleConstants.BODY_FONT);
        table.setGridColor(StyleConstants.BORDER_COLOR);
        table.setDefaultRenderer(Object.class, createFinalGradeRenderer());
        styleTableHeader(table);

        return table;
    }

    private JTable createAssessmentTable() {
        String[] columns = {"ID", "Student", "Subject", "Assessment", "Score"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(StyleConstants.TABLE_ROW_HEIGHT);
        table.setFont(StyleConstants.BODY_FONT);
        table.setGridColor(StyleConstants.BORDER_COLOR);
        table.setDefaultRenderer(Object.class, createAlternatingRenderer());
        styleTableHeader(table);

        return table;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER,
            StyleConstants.BUTTON_GAP, StyleConstants.BUTTON_GAP));
        panel.setBorder(StyleConstants.BUTTON_BORDER);

        JButton addButton = new JButton("Add");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");

        addButton.addActionListener(e -> handleAdd());
        editButton.addActionListener(e -> handleEdit());
        deleteButton.addActionListener(e -> handleDelete());
        clearButton.addActionListener(e -> inputPanel.clear());

        searchField = new JTextField(15);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> handleSearch());

        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(clearButton);
        panel.add(searchField);
        panel.add(searchButton);

        return panel;
    }

    private void handleAdd() {
        if (inputPanel.hasNoSelections()) {
            showError("Please select a student and subject.");
            return;
        }
        if (inputPanel.hasEmptyFields()) {
            showError("Please fill in the assessment name and score.");
            return;
        }
        if (inputPanel.hasInvalidScore()) {
            showError("Score must be a number between 0 and 100.");
            return;
        }

        Assessment assessment = buildAssessmentFromInput(0);

        if (assessmentDao.add(assessment)) {
            refreshAllTabs();
            inputPanel.clear();
        } else {
            showError("Failed to add. This assessment may already exist for this student/subject/season.");
        }
    }

    private void handleEdit() {
        Assessment selected = getSelectedAssessment();
        if (selected == null) {
            showError("Please select an assessment to edit.");
            return;
        }
        if (inputPanel.hasEmptyFields()) {
            showError("Please fill in the assessment name and score.");
            return;
        }
        if (inputPanel.hasInvalidScore()) {
            showError("Score must be a number between 0 and 100.");
            return;
        }

        Assessment assessment = buildAssessmentFromInput(selected.getAssessmentId());

        if (assessmentDao.update(assessment)) {
            refreshAllTabs();
            inputPanel.clear();
        } else {
            showError("Failed to update assessment.");
        }
    }

    private void handleDelete() {
        Assessment selected = getSelectedAssessment();
        if (selected == null) {
            showError("Please select an assessment to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete this assessment record?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            assessmentDao.delete(selected.getAssessmentId());
            refreshAllTabs();
            inputPanel.clear();
        }
    }

    private void handleSearch() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            refreshAllTabs();
            return;
        }

        List<Assessment> results = assessmentDao.search(keyword);
        populateAllTabs(results);
    }

    private Assessment buildAssessmentFromInput(int assessmentId) {
        Student student = inputPanel.getSelectedStudent();
        Subject subject = inputPanel.getSelectedSubject();

        return new Assessment(
            assessmentId,
            student.getStudentId(),
            subject.getSubjectId(),
            inputPanel.getSelectedSeason(),
            inputPanel.getAssessmentName(),
            inputPanel.getScore()
        );
    }

    private Assessment getSelectedAssessment() {
        int tabIndex = seasonTabs.getSelectedIndex();
        if (tabIndex >= GradingSeason.values().length) {
            return null;
        }
        GradingSeason season = GradingSeason.values()[tabIndex];
        JTable table = getTableForTab(tabIndex);

        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            return null;
        }

        List<Assessment> records = seasonRecords.getOrDefault(season, new ArrayList<>());
        if (selectedRow >= records.size()) {
            return null;
        }
        return records.get(selectedRow);
    }

    private void loadSelectedAssessment() {
        Assessment selected = getSelectedAssessment();
        if (selected == null) {
            return;
        }
        inputPanel.fromAssessment(selected);
    }

    private void refreshAllTabs() {
        List<Assessment> allAssessments = assessmentDao.getAll();
        populateAllTabs(allAssessments);
    }

    private void populateAllTabs(List<Assessment> assessments) {
        Map<String, String> studentNames = buildStudentNameMap();
        Map<Integer, String> subjectNames = buildSubjectNameMap();

        for (int i = 0; i < GradingSeason.values().length; i++) {
            GradingSeason season = GradingSeason.values()[i];
            List<Assessment> filtered = filterBySeason(assessments, season);
            seasonRecords.put(season, filtered);
            populateSeasonTab(i, filtered, studentNames, subjectNames);
        }

        populateFinalGradeTab(assessments, studentNames, subjectNames);
    }

    private void populateSeasonTab(int tabIndex, List<Assessment> assessments,
                                   Map<String, String> studentNames,
                                   Map<Integer, String> subjectNames) {
        JTable table = getTableForTab(tabIndex);
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        for (Assessment assessment : assessments) {
            String studentDisplay = studentNames.getOrDefault(
                assessment.getStudentId(), assessment.getStudentId());
            String subjectDisplay = subjectNames.getOrDefault(
                assessment.getSubjectId(), String.valueOf(assessment.getSubjectId()));

            model.addRow(new Object[]{
                assessment.getAssessmentId(),
                studentDisplay,
                subjectDisplay,
                assessment.getAssessmentName(),
                assessment.getScore()
            });
        }

        updateSeasonAverage(tabIndex, assessments);
    }

    private void updateSeasonAverage(int tabIndex, List<Assessment> assessments) {
        JLabel averageLabel = getAverageLabelForTab(tabIndex);

        if (assessments.isEmpty()) {
            averageLabel.setText("Season Average: \u2014");
            averageLabel.setForeground(StyleConstants.TEXT_SECONDARY);
            return;
        }

        ScoreResult result = gradeComputer.computeAverage(assessments);
        String display = String.format("Season Average: %.2f \u2014 %s",
            result.getFinalGrade(), result.getRemarks());
        averageLabel.setText(display);

        if ("PASSED".equals(result.getRemarks())) {
            averageLabel.setForeground(StyleConstants.SUCCESS);
        } else {
            averageLabel.setForeground(StyleConstants.DANGER);
        }
    }

    private void populateFinalGradeTab(List<Assessment> assessments,
                                       Map<String, String> studentNames,
                                       Map<Integer, String> subjectNames) {
        JTable table = getTableForTab(GradingSeason.values().length);
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        Map<String, Map<GradingSeason, List<Assessment>>> grouped =
            groupByStudentSubject(assessments);

        for (Map.Entry<String, Map<GradingSeason, List<Assessment>>> entry : grouped.entrySet()) {
            String key = entry.getKey();
            Map<GradingSeason, List<Assessment>> seasonMap = entry.getValue();

            String studentId = key.split("\\|")[0];
            int subjectId = Integer.parseInt(key.split("\\|")[1]);

            String studentDisplay = studentNames.getOrDefault(studentId, studentId);
            String subjectDisplay = subjectNames.getOrDefault(subjectId,
                String.valueOf(subjectId));

            ScoreResult weightedResult = gradeComputer.computeFinalGrade(seasonMap);

            model.addRow(new Object[]{
                studentDisplay,
                subjectDisplay,
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

    private Map<String, Map<GradingSeason, List<Assessment>>> groupByStudentSubject(
            List<Assessment> assessments) {
        Map<String, Map<GradingSeason, List<Assessment>>> grouped = new LinkedHashMap<>();

        for (Assessment a : assessments) {
            String key = a.getStudentId() + "|" + a.getSubjectId();

            Map<GradingSeason, List<Assessment>> seasonMap = grouped.get(key);
            if (seasonMap == null) {
                seasonMap = new HashMap<>();
                grouped.put(key, seasonMap);
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

    private JTable getTableForTab(int tabIndex) {
        JPanel tabPanel = (JPanel) seasonTabs.getComponentAt(tabIndex);
        JScrollPane scrollPane = (JScrollPane) tabPanel.getComponent(0);
        return (JTable) scrollPane.getViewport().getView();
    }

    private JLabel getAverageLabelForTab(int tabIndex) {
        JPanel tabPanel = (JPanel) seasonTabs.getComponentAt(tabIndex);
        JPanel averagePanel = (JPanel) tabPanel.getComponent(1);
        return (JLabel) averagePanel.getComponent(0);
    }

    private List<Assessment> filterBySeason(List<Assessment> assessments, GradingSeason season) {
        List<Assessment> filtered = new ArrayList<>();
        for (Assessment assessment : assessments) {
            if (assessment.getSeason() == season) {
                filtered.add(assessment);
            }
        }
        return filtered;
    }

    private Map<String, String> buildStudentNameMap() {
        Map<String, String> map = new HashMap<>();
        for (Student student : new StudentDao().getAll()) {
            map.put(student.getStudentId(), student.toString());
        }
        return map;
    }

    private Map<Integer, String> buildSubjectNameMap() {
        Map<Integer, String> map = new HashMap<>();
        for (Subject subject : new SubjectDao().getAll()) {
            map.put(subject.getSubjectId(), subject.toString());
        }
        return map;
    }

    private void populateDropdowns() {
        inputPanel.populateStudents(new StudentDao().getAll());
        inputPanel.populateSubjects(new SubjectDao().getAll());
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

    private DefaultTableCellRenderer createFinalGradeRenderer() {
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
