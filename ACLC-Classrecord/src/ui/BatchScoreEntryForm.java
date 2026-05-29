package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import dao.AssessmentDao;
import dao.EnrollmentDao;
import dao.SubjectDao;
import model.Assessment;
import model.GradingSeason;
import model.Student;
import model.Subject;
import model.User;
import util.GradeConstants;
import util.StyleConstants;

public class BatchScoreEntryForm extends JFrame {

    private BatchScoreFilterPanel filterPanel;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<Student> currentStudents;
    private EnrollmentDao enrollmentDao;
    private AssessmentDao assessmentDao;

    public BatchScoreEntryForm(User currentUser) {
        enrollmentDao = new EnrollmentDao();
        assessmentDao = new AssessmentDao();
        currentStudents = new ArrayList<>();

        setTitle("ACLC Class Record \u2014 Batch Score Entry");
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

        JLabel titleLabel = new JLabel("Batch Score Entry");
        titleLabel.setFont(StyleConstants.TITLE_FONT);
        titleLabel.setForeground(StyleConstants.PRIMARY);

        JButton backButton = new JButton("Back to Grades");
        backButton.addActionListener(e -> handleBack(currentUser));

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(backButton, BorderLayout.EAST);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        filterPanel = new BatchScoreFilterPanel();
        createTable();

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private void createTable() {
        String[] columns = {"Student ID", "Name", "Score"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(StyleConstants.TABLE_ROW_HEIGHT);
        table.setFont(StyleConstants.BODY_FONT);
        table.setGridColor(StyleConstants.BORDER_COLOR);
        table.setDefaultRenderer(Object.class, createAlternatingRenderer());
        styleTableHeader(table);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER,
            StyleConstants.BUTTON_GAP, StyleConstants.BUTTON_GAP));
        panel.setBorder(StyleConstants.BUTTON_BORDER);

        JButton saveButton = new JButton("Save All");
        JButton deleteButton = new JButton("Delete Selected");

        saveButton.addActionListener(e -> handleSaveAll());
        deleteButton.addActionListener(e -> handleDeleteSelected());

        panel.add(saveButton);
        panel.add(deleteButton);

        return panel;
    }

    private void populateSubjects() {
        List<Subject> subjects = new SubjectDao().getAll();
        filterPanel.populateSubjects(subjects);
    }

    private void attachFilterListeners() {
        filterPanel.addSubjectListener(e -> refreshSections());
        filterPanel.addSectionListener(e -> refreshTable());
        filterPanel.addSeasonListener(e -> refreshTable());
        filterPanel.addLoadListener(e -> refreshTable());
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

    private void refreshTable() {
        Subject subject = filterPanel.getSelectedSubject();
        String section = filterPanel.getSelectedSection();
        String assessmentName = filterPanel.getAssessmentName();

        if (subject == null || section == null || assessmentName.isEmpty()) {
            tableModel.setRowCount(0);
            currentStudents.clear();
            return;
        }

        currentStudents = enrollmentDao.getStudentsBySubjectAndSection(
            subject.getSubjectId(), section);
        Map<String, Double> existingScores = loadExistingScores(
            subject.getSubjectId(), filterPanel.getSelectedSeason(), assessmentName);

        tableModel.setRowCount(0);

        for (Student student : currentStudents) {
            Double score = existingScores.get(student.getStudentId());
            String scoreText = (score != null) ? String.valueOf(score) : "";

            tableModel.addRow(new Object[]{
                student.getStudentId(),
                student.getFirstname() + " " + student.getLastname(),
                scoreText
            });
        }
    }

    private Map<String, Double> loadExistingScores(int subjectId,
            GradingSeason season, String assessmentName) {
        Map<String, Double> map = new HashMap<>();
        List<Assessment> existing = assessmentDao.getBySeason(season);

        for (Assessment a : existing) {
            if (a.getSubjectId() == subjectId
                    && a.getAssessmentName().equals(assessmentName)) {
                map.put(a.getStudentId(), a.getScore());
            }
        }

        return map;
    }

    private void handleSaveAll() {
        Subject subject = filterPanel.getSelectedSubject();
        String assessmentName = filterPanel.getAssessmentName();
        GradingSeason season = filterPanel.getSelectedSeason();

        if (subject == null) {
            showError("Please select a subject.");
            return;
        }
        if (assessmentName.isEmpty()) {
            showError("Please enter an assessment name.");
            return;
        }
        if (currentStudents.isEmpty()) {
            showError("No students to save scores for.");
            return;
        }

        stopCellEditing();

        int saved = 0;
        int skipped = 0;

        for (int row = 0; row < tableModel.getRowCount(); row++) {
            String scoreText = String.valueOf(tableModel.getValueAt(row, 2)).trim();

            if (scoreText.isEmpty()) {
                skipped++;
                continue;
            }

            if (!isValidScore(scoreText)) {
                showError("Invalid score at row " + (row + 1)
                    + ". Score must be a number between 0 and 100.");
                return;
            }

            double score = Double.parseDouble(scoreText);
            String studentId = currentStudents.get(row).getStudentId();

            Assessment assessment = new Assessment(
                0, studentId, subject.getSubjectId(), season, assessmentName, score);

            if (assessmentDao.saveOrUpdate(assessment)) {
                saved++;
            }
        }

        showSaveResult(saved, skipped);
        refreshTable();
    }

    private void handleDeleteSelected() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a student row to delete.");
            return;
        }

        Subject subject = filterPanel.getSelectedSubject();
        String assessmentName = filterPanel.getAssessmentName();
        GradingSeason season = filterPanel.getSelectedSeason();

        if (subject == null || assessmentName.isEmpty()) {
            showError("Please select a subject and enter an assessment name.");
            return;
        }

        String studentId = currentStudents.get(selectedRow).getStudentId();
        int assessmentId = findAssessmentId(studentId, subject.getSubjectId(),
            season, assessmentName);

        if (assessmentId == -1) {
            showError("No saved score to delete for this student.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete this score?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            assessmentDao.delete(assessmentId);
            refreshTable();
        }
    }

    private int findAssessmentId(String studentId, int subjectId,
            GradingSeason season, String assessmentName) {
        List<Assessment> existing = assessmentDao.getBySeason(season);

        for (Assessment a : existing) {
            if (a.getStudentId().equals(studentId)
                    && a.getSubjectId() == subjectId
                    && a.getAssessmentName().equals(assessmentName)) {
                return a.getAssessmentId();
            }
        }
        return -1;
    }

    private boolean isValidScore(String text) {
        try {
            double value = Double.parseDouble(text);
            return value >= GradeConstants.MIN_SCORE && value <= GradeConstants.MAX_SCORE;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void stopCellEditing() {
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }
    }

    private void showSaveResult(int saved, int skipped) {
        String message = "Saved: " + saved + " scores.";
        if (skipped > 0) {
            message += "\nSkipped: " + skipped + " (empty scores).";
        }
        JOptionPane.showMessageDialog(this, message,
            "Batch Save Complete", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleBack(User currentUser) {
        new GradeForm(currentUser).setVisible(true);
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
