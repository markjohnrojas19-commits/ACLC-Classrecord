package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import dao.GradeDao;
import dao.StudentDao;
import dao.SubjectDao;
import model.Grade;
import model.GradeRecord;
import model.ScoreResult;
import model.Student;
import model.Subject;
import model.User;
import service.GradeComputer;

public class GradeForm extends JFrame {

    private GradeInputPanel inputPanel;
    private JTable gradeTable;
    private GradeDao gradeDao;
    private GradeComputer gradeComputer;
    private JTextField searchField;
    private List<GradeRecord> currentRecords;

    public GradeForm(User currentUser) {
        gradeDao = new GradeDao();
        gradeComputer = new GradeComputer();
        currentRecords = new ArrayList<>();

        setTitle("ACLC Class Record — Grade Management");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        add(createHeaderPanel(currentUser), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        populateDropdowns();
        setupAutoCompute();
        refreshTable();
    }

    private JPanel createHeaderPanel(User currentUser) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));

        JLabel titleLabel = new JLabel("Grade Management");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        JButton backButton = new JButton("Back to Dashboard");
        backButton.addActionListener(e -> handleBack(currentUser));

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(backButton, BorderLayout.EAST);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        inputPanel = new GradeInputPanel();

        gradeTable = createGradeTable();
        JScrollPane scrollPane = new JScrollPane(gradeTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JTable createGradeTable() {
        String[] columns = {"ID", "Student", "Subject", "Quiz", "Assignment",
                            "Exam", "Final Grade", "Remarks"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> loadSelectedGrade());

        return table;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 20, 15, 20));

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

    private void setupAutoCompute() {
        inputPanel.addScoreChangeListener(this::computeAndDisplay);
    }

    private void computeAndDisplay() {
        double quiz = inputPanel.getQuiz();
        double assignment = inputPanel.getAssignment();
        double exam = inputPanel.getExam();

        ScoreResult result = gradeComputer.compute(quiz, assignment, exam);
        inputPanel.updateResult(result.getFinalGrade(), result.getRemarks());
    }

    private void handleAdd() {
        if (inputPanel.hasNoSelections()) {
            showError("Please select a student and subject.");
            return;
        }

        if (inputPanel.hasEmptyScores()) {
            showError("Please fill in all score fields.");
            return;
        }

        Grade grade = buildGradeFromInput(0);
        ScoreResult result = computeResult();

        if (gradeDao.add(grade, result)) {
            refreshTable();
            inputPanel.clear();
        } else {
            showError("Failed to add grade. This student-subject combination may already exist.");
        }
    }

    private void handleEdit() {
        if (gradeTable.getSelectedRow() == -1) {
            showError("Please select a grade to edit.");
            return;
        }

        if (inputPanel.hasEmptyScores()) {
            showError("Please fill in all score fields.");
            return;
        }

        int gradeId = (int) gradeTable.getValueAt(gradeTable.getSelectedRow(), 0);
        Grade grade = buildGradeFromInput(gradeId);
        ScoreResult result = computeResult();

        if (gradeDao.update(grade, result)) {
            refreshTable();
            inputPanel.clear();
        } else {
            showError("Failed to update grade.");
        }
    }

    private void handleDelete() {
        int selectedRow = gradeTable.getSelectedRow();

        if (selectedRow == -1) {
            showError("Please select a grade to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete this grade record?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int gradeId = (int) gradeTable.getValueAt(selectedRow, 0);
            gradeDao.delete(gradeId);
            refreshTable();
            inputPanel.clear();
        }
    }

    private void handleSearch() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            refreshTable();
            return;
        }

        List<GradeRecord> results = gradeDao.search(keyword);
        populateTable(results);
    }

    private Grade buildGradeFromInput(int gradeId) {
        Student student = inputPanel.getSelectedStudent();
        Subject subject = inputPanel.getSelectedSubject();

        return new Grade(gradeId, student.getStudentId(), subject.getSubjectId(),
                         inputPanel.getQuiz(), inputPanel.getAssignment(), inputPanel.getExam());
    }

    private ScoreResult computeResult() {
        return gradeComputer.compute(
            inputPanel.getQuiz(), inputPanel.getAssignment(), inputPanel.getExam());
    }

    private void refreshTable() {
        List<GradeRecord> records = gradeDao.getAll();
        populateTable(records);
    }

    private void populateTable(List<GradeRecord> records) {
        currentRecords = records;
        Map<String, String> studentNames = buildStudentNameMap();
        Map<Integer, String> subjectNames = buildSubjectNameMap();

        DefaultTableModel model = (DefaultTableModel) gradeTable.getModel();
        model.setRowCount(0);

        for (GradeRecord record : records) {
            Grade grade = record.getGrade();
            ScoreResult result = record.getScoreResult();

            String studentDisplay = studentNames.getOrDefault(grade.getStudentId(), grade.getStudentId());
            String subjectDisplay = subjectNames.getOrDefault(grade.getSubjectId(), String.valueOf(grade.getSubjectId()));

            model.addRow(new Object[]{
                grade.getGradeId(),
                studentDisplay,
                subjectDisplay,
                grade.getQuiz(),
                grade.getAssignment(),
                grade.getExam(),
                result.getFinalGrade(),
                result.getRemarks()
            });
        }
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

    private void loadSelectedGrade() {
        int selectedRow = gradeTable.getSelectedRow();

        if (selectedRow == -1) {
            return;
        }

        GradeRecord record = currentRecords.get(selectedRow);
        Grade grade = record.getGrade();

        inputPanel.selectStudent(grade.getStudentId());
        inputPanel.selectSubject(grade.getSubjectId());
        inputPanel.setScores(grade.getQuiz(), grade.getAssignment(), grade.getExam());
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
}
