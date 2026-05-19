package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

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

import dao.StudentDao;
import model.Student;
import model.User;
import util.StyleConstants;

public class StudentForm extends JFrame {

    private StudentInputPanel inputPanel;
    private JTable studentTable;
    private StudentDao studentDao;
    private JTextField searchField;

    public StudentForm(User currentUser) {
        studentDao = new StudentDao();

        setTitle("ACLC Class Record — Student Management");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        add(createHeaderPanel(currentUser), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        refreshTable();
    }

    private JPanel createHeaderPanel(User currentUser) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(StyleConstants.HEADER_BORDER);

        JLabel titleLabel = new JLabel("Student Management");
        titleLabel.setFont(StyleConstants.TITLE_FONT);

        JButton backButton = new JButton("Back to Dashboard");
        backButton.addActionListener(e -> handleBack(currentUser));

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(backButton, BorderLayout.EAST);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        inputPanel = new StudentInputPanel();

        studentTable = createStudentTable();
        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(StyleConstants.TABLE_BORDER);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JTable createStudentTable() {
        String[] columns = {"Student ID", "First Name", "Last Name", "Course", "Year", "Section", "Gender"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> loadSelectedStudent());

        return table;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, StyleConstants.BUTTON_GAP, StyleConstants.BUTTON_GAP));
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
            refreshTable();
            inputPanel.clear();
        } else {
            showError("Failed to add student. The Student ID may already exist.");
        }
    }

    private void handleEdit() {
        if (studentTable.getSelectedRow() == -1) {
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
            refreshTable();
            inputPanel.clear();
        } else {
            showError("Failed to update student.");
        }
    }

    private void handleDelete() {
        int selectedRow = studentTable.getSelectedRow();

        if (selectedRow == -1) {
            showError("Please select a student to delete.");
            return;
        }

        String studentId = (String) studentTable.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete student " + studentId + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            studentDao.delete(studentId);
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

        List<Student> results = studentDao.search(keyword);
        populateTable(results);
    }

    private void refreshTable() {
        List<Student> students = studentDao.getAll();
        populateTable(students);
    }

    private void populateTable(List<Student> students) {
        DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
        model.setRowCount(0);

        for (Student student : students) {
            model.addRow(new Object[]{
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

    private void loadSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();

        if (selectedRow == -1) {
            return;
        }

        Student student = new Student(
            (String) studentTable.getValueAt(selectedRow, 0),
            (String) studentTable.getValueAt(selectedRow, 1),
            (String) studentTable.getValueAt(selectedRow, 2),
            (String) studentTable.getValueAt(selectedRow, 3),
            (int) studentTable.getValueAt(selectedRow, 4),
            (String) studentTable.getValueAt(selectedRow, 5),
            (String) studentTable.getValueAt(selectedRow, 6)
        );

        inputPanel.fromStudent(student);
        inputPanel.lockStudentId();
    }

    private void handleBack(User currentUser) {
        new DashboardForm(currentUser).setVisible(true);
        dispose();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
