package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import java.text.MessageFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
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
import util.GradeConstants;
import util.StyleConstants;

public class GradeForm extends JFrame {

    private User currentUser;
    private GradeFilterPanel filterPanel;
    private JTabbedPane seasonTabs;
    private AssessmentDao assessmentDao;
    private GradeComputer gradeComputer;
    private Map<GradingSeason, List<Assessment>> seasonRecords;

    public GradeForm(User currentUser) {
        this.currentUser = currentUser;
        assessmentDao = new AssessmentDao();
        gradeComputer = new GradeComputer();
        seasonRecords = new HashMap<>();

        setTitle("ACLC Class Record \u2014 Grades");
        setSize(1050, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        populateSectionFilter();
        refreshAllTabs();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(StyleConstants.HEADER_BORDER);
        panel.setBackground(StyleConstants.WHITE);

        JLabel titleLabel = new JLabel("Grades");
        titleLabel.setFont(StyleConstants.TITLE_FONT);
        titleLabel.setForeground(StyleConstants.PRIMARY);

        JButton backButton = new JButton("Back to Dashboard");
        backButton.addActionListener(e -> handleBack());

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(backButton, BorderLayout.EAST);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        filterPanel = createFilterPanel();
        seasonTabs = createSeasonTabs();

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(seasonTabs, BorderLayout.CENTER);

        return panel;
    }

    private GradeFilterPanel createFilterPanel() {
        GradeFilterPanel panel = new GradeFilterPanel();
        panel.addSectionListener(e -> refreshAllTabs());
        panel.addStatusListener(e -> refreshAllTabs());
        panel.addSearchListener(e -> refreshWithSearch());
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
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(StyleConstants.TABLE_ROW_HEIGHT);
        table.setFont(StyleConstants.BODY_FONT);
        table.setGridColor(StyleConstants.BORDER_COLOR);
        table.setDefaultRenderer(Object.class, createFinalGradeRenderer());
        styleTableHeader(table);

        return table;
    }

    private JTable createAssessmentTable() {
        String[] columns = {"ID", "Student", "Subject", "Assessment", "Score", "Date"};
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
        table.setDefaultRenderer(Object.class, createGroupedRenderer());
        styleTableHeader(table);

        return table;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER,
            StyleConstants.BUTTON_GAP, StyleConstants.BUTTON_GAP));
        panel.setBorder(StyleConstants.BUTTON_BORDER);

        JButton enterScoresButton = new JButton("Enter Scores");
        JButton printButton = new JButton("Print");
        JButton exportButton = new JButton("Export CSV");

        enterScoresButton.addActionListener(e -> openBatchScoreEntry());
        printButton.addActionListener(e -> handlePrint());
        exportButton.addActionListener(e -> handleExportCsv());

        panel.add(enterScoresButton);
        panel.add(printButton);
        panel.add(exportButton);

        return panel;
    }

    private void openBatchScoreEntry() {
        new BatchScoreEntryForm(currentUser).setVisible(true);
        dispose();
    }

    private void refreshWithSearch() {
        String keyword = filterPanel.getSearchKeyword();

        if (keyword.isEmpty()) {
            refreshAllTabs();
            return;
        }

        List<Assessment> results = assessmentDao.search(keyword);
        populateAllTabs(results);
    }

    private void handlePrint() {
        int tabIndex = seasonTabs.getSelectedIndex();
        String tabTitle = seasonTabs.getTitleAt(tabIndex);
        JTable table = getTableForTab(tabIndex);

        try {
            MessageFormat header = new MessageFormat("ACLC Class Record — " + tabTitle);
            MessageFormat footer = new MessageFormat("Page {0}");
            table.print(JTable.PrintMode.FIT_WIDTH, header, footer);
        } catch (PrinterException ex) {
            showError("Printing failed: " + ex.getMessage());
        }
    }

    private void handleExportCsv() {
        JTable table = getTableForTab(seasonTabs.getSelectedIndex());
        if (table.getRowCount() == 0) {
            showError("No data to export.");
            return;
        }

        File file = chooseExportFile();
        if (file == null) {
            return;
        }

        writeTableToCsv(table, file);
    }

    private File chooseExportFile() {
        String tabTitle = seasonTabs.getTitleAt(seasonTabs.getSelectedIndex());
        String defaultName = "grades_" + tabTitle.replace(" ", "_").toLowerCase() + ".csv";

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export Grades to CSV");
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

    private void writeTableToCsv(JTable table, File file) {
        try (FileWriter writer = new FileWriter(file)) {
            DefaultTableModel model = (DefaultTableModel) table.getModel();

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

    private void populateSectionFilter() {
        List<String> sections = new StudentDao().getAllSections();
        filterPanel.populateSections(sections);
    }

    private void refreshAllTabs() {
        List<Assessment> allAssessments = assessmentDao.getAll();
        populateAllTabs(allAssessments);
    }

    private void populateAllTabs(List<Assessment> assessments) {
        Map<String, String> studentNames = buildStudentNameMap();
        Map<Integer, String> subjectNames = buildSubjectNameMap();
        assessments = filterBySection(assessments);

        for (int i = 0; i < GradingSeason.values().length; i++) {
            GradingSeason season = GradingSeason.values()[i];
            List<Assessment> filtered = filterBySeason(assessments, season);
            filtered = filterByStatus(filtered);
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

        Map<String, List<Assessment>> grouped = groupByStudentSubjectFlat(assessments);

        for (List<Assessment> group : grouped.values()) {
            addGroupRows(model, group, studentNames, subjectNames);
        }

        updateSeasonAverage(tabIndex, assessments);
    }

    private void addGroupRows(DefaultTableModel model, List<Assessment> group,
                              Map<String, String> studentNames,
                              Map<Integer, String> subjectNames) {
        for (int i = 0; i < group.size(); i++) {
            Assessment assessment = group.get(i);
            boolean isFirstRow = (i == 0);

            String studentDisplay = isFirstRow
                ? studentNames.getOrDefault(assessment.getStudentId(), assessment.getStudentId())
                : "";
            String subjectDisplay = isFirstRow
                ? subjectNames.getOrDefault(assessment.getSubjectId(),
                    String.valueOf(assessment.getSubjectId()))
                : "";

            model.addRow(new Object[]{
                assessment.getAssessmentId(),
                studentDisplay,
                subjectDisplay,
                assessment.getAssessmentName(),
                formatScoreDisplay(assessment),
                assessment.getDate() != null ? assessment.getDate().toString() : ""
            });
        }
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

            if (!matchesStatusFilter(weightedResult)) {
                continue;
            }

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

    private String formatScoreDisplay(Assessment assessment) {
        return String.format("%.0f/%.0f (%.1f%%)",
            assessment.getScore(), assessment.getTotalItems(), assessment.getPercentage());
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

    private boolean matchesStatusFilter(ScoreResult result) {
        if (filterPanel.isAllResults()) {
            return true;
        }
        boolean passed = "PASSED".equals(result.getRemarks());
        return passed == filterPanel.isPassedOnly();
    }

    private List<Assessment> filterByStatus(List<Assessment> assessments) {
        if (filterPanel.isAllResults()) {
            return assessments;
        }

        boolean keepPassed = filterPanel.isPassedOnly();
        Map<String, List<Assessment>> groups = groupByStudentSubjectFlat(assessments);
        List<Assessment> filtered = new ArrayList<>();

        for (List<Assessment> group : groups.values()) {
            ScoreResult result = gradeComputer.computeAverage(group);
            boolean passed = "PASSED".equals(result.getRemarks());
            if (passed == keepPassed) {
                filtered.addAll(group);
            }
        }

        return filtered;
    }

    private Map<String, List<Assessment>> groupByStudentSubjectFlat(List<Assessment> assessments) {
        Map<String, List<Assessment>> groups = new LinkedHashMap<>();

        for (Assessment a : assessments) {
            String key = a.getStudentId() + "|" + a.getSubjectId();
            List<Assessment> list = groups.get(key);
            if (list == null) {
                list = new ArrayList<>();
                groups.put(key, list);
            }
            list.add(a);
        }

        return groups;
    }

    private List<Assessment> filterBySection(List<Assessment> assessments) {
        if (filterPanel.isAllSections()) {
            return assessments;
        }

        String section = filterPanel.getSelectedSection();
        Map<String, String> sectionMap = buildStudentSectionMap();

        List<Assessment> filtered = new ArrayList<>();
        for (Assessment assessment : assessments) {
            String studentSection = sectionMap.get(assessment.getStudentId());
            if (section.equals(studentSection)) {
                filtered.add(assessment);
            }
        }
        return filtered;
    }

    private Map<String, String> buildStudentSectionMap() {
        Map<String, String> map = new HashMap<>();
        for (Student student : new StudentDao().getAll()) {
            map.put(student.getStudentId(), student.getCourseSection());
        }
        return map;
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

    private void handleBack() {
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

    private DefaultTableCellRenderer createGroupedRenderer() {
        return new DefaultTableCellRenderer() {
            private final Border groupTopBorder = BorderFactory.createMatteBorder(
                2, 0, 0, 0, StyleConstants.BORDER_COLOR);
            private final Border noBorder = BorderFactory.createEmptyBorder();

            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(
                    t, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    cell.setBackground(isGroupFirstRow(t, row)
                        ? StyleConstants.WHITE : StyleConstants.TABLE_ROW_ALT);
                }

                setBorder(isGroupFirstRow(t, row) && row > 0
                    ? groupTopBorder : noBorder);

                return cell;
            }

            private boolean isGroupFirstRow(JTable t, int row) {
                Object student = t.getModel().getValueAt(row, 1);
                return student != null && !student.toString().isEmpty();
            }
        };
    }
}
