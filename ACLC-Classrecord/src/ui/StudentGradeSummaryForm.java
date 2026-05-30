package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
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
    private JComboBox<Student> studentBox;
    private JTable summaryTable;
    private AssessmentDao assessmentDao;
    private GradeComputer gradeComputer;

    public StudentGradeSummaryForm(User currentUser) {
        assessmentDao = new AssessmentDao();
        gradeComputer = new GradeComputer();

        setTitle("ACLC Class Record \u2014 Student Grade Summary");
        setSize(1050, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        add(createHeaderPanel(currentUser), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        populateStudentDropdown();
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

        panel.add(createStudentSelector(), BorderLayout.NORTH);
        panel.add(createTablePanel(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStudentSelector() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT,
            StyleConstants.GRID_H_GAP, StyleConstants.GRID_V_GAP));
        panel.setBorder(StyleConstants.INPUT_BORDER);
        panel.setBackground(StyleConstants.WHITE);

        JLabel sectionLabel = new JLabel("Course/Section:");
        sectionLabel.setFont(StyleConstants.BODY_FONT);

        sectionBox = new JComboBox<>();
        sectionBox.setFont(StyleConstants.BODY_FONT);
        sectionBox.addActionListener(e -> filterStudentsBySection());

        JLabel studentLabel = new JLabel("Student:");
        studentLabel.setFont(StyleConstants.BODY_FONT);

        studentBox = new JComboBox<>();
        studentBox.setFont(StyleConstants.BODY_FONT);
        studentBox.addActionListener(e -> refreshSummary());

        panel.add(sectionLabel);
        panel.add(sectionBox);
        panel.add(studentLabel);
        panel.add(studentBox);

        return panel;
    }

    private JScrollPane createTablePanel() {
        summaryTable = createSummaryTable();

        JScrollPane scrollPane = new JScrollPane(summaryTable);
        scrollPane.setBorder(StyleConstants.TABLE_BORDER);

        return scrollPane;
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
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER,
            StyleConstants.BUTTON_GAP, StyleConstants.BUTTON_GAP));
        panel.setBorder(StyleConstants.BUTTON_BORDER);

        JButton printButton = new JButton("Print");
        JButton exportButton = new JButton("Export CSV");

        printButton.addActionListener(e -> handlePrint());
        exportButton.addActionListener(e -> handleExportCsv());

        panel.add(printButton);
        panel.add(exportButton);

        return panel;
    }

    private void populateStudentDropdown() {
        List<String> sections = new StudentDao().getAllSections();
        sectionBox.addItem("All Sections");
        for (String section : sections) {
            sectionBox.addItem(section);
        }
        filterStudentsBySection();
    }

    private void filterStudentsBySection() {
        studentBox.removeAllItems();
        String selected = (String) sectionBox.getSelectedItem();
        List<Student> students = new StudentDao().getAll();

        for (Student student : students) {
            if ("All Sections".equals(selected) || student.getCourseSection().equals(selected)) {
                studentBox.addItem(student);
            }
        }
    }

    private void selectStudent(Student preselected) {
        sectionBox.setSelectedItem(preselected.getCourseSection());

        for (int i = 0; i < studentBox.getItemCount(); i++) {
            Student item = studentBox.getItemAt(i);
            if (item.getStudentId().equals(preselected.getStudentId())) {
                studentBox.setSelectedIndex(i);
                return;
            }
        }
    }

    private void refreshSummary() {
        Student selected = (Student) studentBox.getSelectedItem();
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

        Student selected = (Student) studentBox.getSelectedItem();
        String studentName = (selected != null) ? selected.toString() : "Unknown";

        try {
            MessageFormat header = new MessageFormat("Grade Summary — " + studentName);
            MessageFormat footer = new MessageFormat("Page {0}");
            summaryTable.print(JTable.PrintMode.FIT_WIDTH, header, footer);
        } catch (PrinterException ex) {
            showError("Printing failed: " + ex.getMessage());
        }
    }

    private void handleExportCsv() {
        if (summaryTable.getRowCount() == 0) {
            showError("No data to export.");
            return;
        }

        File file = chooseExportFile();
        if (file == null) {
            return;
        }

        writeTableToCsv(file);
    }

    private File chooseExportFile() {
        Student selected = (Student) studentBox.getSelectedItem();
        String studentId = (selected != null) ? selected.getStudentId() : "unknown";
        String defaultName = "grade_summary_" + studentId + ".csv";

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export Grade Summary to CSV");
        chooser.setSelectedFile(new File(defaultName));

        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        File file = chooser.getSelectedFile();
        if (!file.getName().endsWith(".csv")) {
            file = new File(file.getAbsolutePath() + ".csv");
        }
        return file;
    }

    private void writeTableToCsv(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            DefaultTableModel model = (DefaultTableModel) summaryTable.getModel();

            writeCsvRow(writer, getColumnHeaders(model));

            for (int row = 0; row < model.getRowCount(); row++) {
                writeCsvRow(writer, getRowValues(model, row));
            }

            JOptionPane.showMessageDialog(this,
                "Exported " + model.getRowCount() + " rows to:\n" + file.getAbsolutePath(),
                "Export Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            showError("Export failed: " + ex.getMessage());
        }
    }

    private String[] getColumnHeaders(DefaultTableModel model) {
        String[] headers = new String[model.getColumnCount()];
        for (int col = 0; col < model.getColumnCount(); col++) {
            headers[col] = model.getColumnName(col);
        }
        return headers;
    }

    private String[] getRowValues(DefaultTableModel model, int row) {
        String[] values = new String[model.getColumnCount()];
        for (int col = 0; col < model.getColumnCount(); col++) {
            Object value = model.getValueAt(row, col);
            values[col] = (value == null) ? "" : value.toString();
        }
        return values;
    }

    private void writeCsvRow(FileWriter writer, String[] values) throws IOException {
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                writer.write(",");
            }
            writer.write(escapeCsv(values[i]));
        }
        writer.write("\n");
    }

    private String escapeCsv(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
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
