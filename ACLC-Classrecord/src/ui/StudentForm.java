package ui;

import java.awt.BorderLayout;
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
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import dao.StudentDao;
import model.Student;
import model.User;
import util.StyleConstants;

public class StudentForm extends JFrame {

    private StudentInputPanel inputPanel;
    private JTabbedPane sectionTabs;
    private StudentDao studentDao;
    private JTextField searchField;
    private User currentUser;

    public StudentForm(User currentUser) {
        this.currentUser = currentUser;
        studentDao = new StudentDao();

        setTitle("ACLC Class Record \u2014 Student Management");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                handleBack(currentUser);
            }
        });
        setLocationRelativeTo(null);

        add(createHeaderPanel(currentUser), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        refreshTabs();
    }

    private JPanel createHeaderPanel(User currentUser) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(StyleConstants.HEADER_BORDER);
        panel.setBackground(StyleConstants.WHITE);

        JLabel titleLabel = new JLabel("Student Management");
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

        inputPanel = new StudentInputPanel();

        sectionTabs = new JTabbedPane();
        sectionTabs.setFont(StyleConstants.TAB_FONT);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(sectionTabs, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER,
            StyleConstants.BUTTON_GAP, StyleConstants.BUTTON_GAP));
        panel.setBorder(StyleConstants.BUTTON_BORDER);

        JButton addButton = new JButton("Add");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");
        JButton viewGradesButton = new JButton("View Grades");

        addButton.addActionListener(e -> handleAdd());
        editButton.addActionListener(e -> handleEdit());
        deleteButton.addActionListener(e -> handleDelete());
        clearButton.addActionListener(e -> inputPanel.clear());
        viewGradesButton.addActionListener(e -> handleViewGrades());

        searchField = new JTextField(15);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> handleSearch());

        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(clearButton);
        panel.add(viewGradesButton);
        panel.add(searchField);
        panel.add(searchButton);

        return panel;
    }

    private void refreshTabs() {
        List<Student> allStudents = studentDao.getAll();
        buildSectionTabs(allStudents);
    }

    private void buildSectionTabs(List<Student> students) {
        sectionTabs.removeAll();

        SectionTablePanel allPanel = createSectionPanel();
        allPanel.populate(students);
        sectionTabs.addTab("All", allPanel);

        List<String> sections = extractSections(students);

        for (String section : sections) {
            SectionTablePanel sectionPanel = createSectionPanel();
            sectionPanel.populate(filterBySection(students, section));
            sectionTabs.addTab("Section " + section, sectionPanel);
        }
    }

    private SectionTablePanel createSectionPanel() {
        SectionTablePanel panel = new SectionTablePanel();
        panel.getTable().getSelectionModel()
            .addListSelectionListener(e -> loadSelectedFromActiveTab());
        return panel;
    }

    private List<String> extractSections(List<Student> students) {
        Set<String> sections = new LinkedHashSet<>();
        for (Student student : students) {
            sections.add(student.getSection());
        }
        return new ArrayList<>(sections);
    }

    private List<Student> filterBySection(List<Student> students, String section) {
        List<Student> filtered = new ArrayList<>();
        for (Student student : students) {
            if (student.getSection().equals(section)) {
                filtered.add(student);
            }
        }
        return filtered;
    }

    private SectionTablePanel getActivePanel() {
        return (SectionTablePanel) sectionTabs.getSelectedComponent();
    }

    private void loadSelectedFromActiveTab() {
        SectionTablePanel activePanel = getActivePanel();
        if (activePanel == null) {
            return;
        }

        int selectedRow = activePanel.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        Student student = extractStudentFromRow(activePanel, selectedRow);
        inputPanel.fromStudent(student);
        inputPanel.lockStudentId();
    }

    private Student extractStudentFromRow(SectionTablePanel panel, int row) {
        return new Student(
            (String) panel.getValueAt(row, 0),
            (String) panel.getValueAt(row, 1),
            (String) panel.getValueAt(row, 2),
            (String) panel.getValueAt(row, 3),
            (int) panel.getValueAt(row, 4),
            (String) panel.getValueAt(row, 5),
            (String) panel.getValueAt(row, 6)
        );
    }

    private void handleAdd() {
        if (inputPanel.hasEmptyFields()) {
            showError("Please fill in all fields.");
            return;
        }

        if (inputPanel.hasInvalidYearLevel()) {
            showError("Year level must be between 1 and 4.");
            return;
        }

        Student student = inputPanel.toStudent();

        if (studentDao.add(student)) {
            refreshTabs();
            inputPanel.clear();
            showSuccess("Student added successfully.");
        } else {
            showError("Failed to add student. The Student ID may already exist.");
        }
    }

    private void handleEdit() {
        SectionTablePanel activePanel = getActivePanel();
        if (activePanel == null || activePanel.getSelectedRow() == -1) {
            showError("Please select a student to edit.");
            return;
        }

        if (inputPanel.hasEmptyFields()) {
            showError("Please fill in all fields.");
            return;
        }

        if (inputPanel.hasInvalidYearLevel()) {
            showError("Year level must be between 1 and 4.");
            return;
        }

        Student student = inputPanel.toStudent();

        if (studentDao.update(student)) {
            refreshTabs();
            inputPanel.clear();
            showSuccess("Student updated successfully.");
        } else {
            showError("Failed to update student.");
        }
    }

    private void handleDelete() {
        SectionTablePanel activePanel = getActivePanel();
        if (activePanel == null || activePanel.getSelectedRow() == -1) {
            showError("Please select a student to delete.");
            return;
        }

        String studentId = (String) activePanel.getValueAt(activePanel.getSelectedRow(), 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete student " + studentId + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            studentDao.delete(studentId);
            refreshTabs();
            inputPanel.clear();
        }
    }

    private void handleSearch() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            refreshTabs();
            return;
        }

        List<Student> results = studentDao.search(keyword);
        buildSectionTabs(results);
    }

    private void handleViewGrades() {
        SectionTablePanel activePanel = getActivePanel();
        if (activePanel == null || activePanel.getSelectedRow() == -1) {
            showError("Please select a student to view grades.");
            return;
        }

        Student student = extractStudentFromRow(activePanel, activePanel.getSelectedRow());
        new StudentGradeSummaryForm(currentUser, student).setVisible(true);
        dispose();
    }

    private void handleBack(User currentUser) {
        if (inputPanel.hasChanges() && !confirmDiscard()) {
            return;
        }
        new DashboardForm(currentUser).setVisible(true);
        dispose();
    }

    private boolean confirmDiscard() {
        int choice = JOptionPane.showConfirmDialog(this,
            "You have unsaved changes. Discard and go back?",
            "Unsaved Changes", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        return choice == JOptionPane.YES_OPTION;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}
