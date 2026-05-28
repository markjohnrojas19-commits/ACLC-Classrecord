package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import dao.SubjectDao;
import model.Subject;
import model.User;
import util.StyleConstants;

public class SubjectForm extends JFrame {

    private JTextField subjectCodeField;
    private JTextField subjectNameField;
    private JTable subjectTable;
    private SubjectDao subjectDao;
    private JTextField searchField;

    public SubjectForm(User currentUser) {
        subjectDao = new SubjectDao();

        setTitle("ACLC Class Record — Subject Management");
        setSize(900, 600);
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

        refreshTable();
    }

    private JPanel createHeaderPanel(User currentUser) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(StyleConstants.HEADER_BORDER);

        JLabel titleLabel = new JLabel("Subject Management");
        titleLabel.setFont(StyleConstants.TITLE_FONT);

        JButton backButton = new JButton("Back to Dashboard");
        backButton.addActionListener(e -> handleBack(currentUser));

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(backButton, BorderLayout.EAST);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        panel.add(createInputPanel(), BorderLayout.NORTH);
        panel.add(createTableScrollPane(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, StyleConstants.GRID_H_GAP, StyleConstants.GRID_V_GAP));
        panel.setBorder(StyleConstants.INPUT_BORDER);

        subjectCodeField = new JTextField();
        subjectNameField = new JTextField();

        panel.add(new JLabel("Subject Code:"));
        panel.add(subjectCodeField);
        panel.add(new JLabel("Subject Name:"));
        panel.add(subjectNameField);

        return panel;
    }

    private JScrollPane createTableScrollPane() {
        subjectTable = createSubjectTable();
        JScrollPane scrollPane = new JScrollPane(subjectTable);
        scrollPane.setBorder(StyleConstants.TABLE_BORDER);
        return scrollPane;
    }

    private JTable createSubjectTable() {
        String[] columns = {"ID", "Subject Code", "Subject Name"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> loadSelectedSubject());

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
        clearButton.addActionListener(e -> clearFields());

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
        if (hasEmptyFields()) {
            showError("Please fill in all fields.");
            return;
        }

        Subject subject = new Subject(0, subjectCodeField.getText().trim(), subjectNameField.getText().trim());

        if (subjectDao.add(subject)) {
            refreshTable();
            clearFields();
            showSuccess("Subject added successfully.");
        } else {
            showError("Failed to add subject. The subject code may already exist.");
        }
    }

    private void handleEdit() {
        if (subjectTable.getSelectedRow() == -1) {
            showError("Please select a subject to edit.");
            return;
        }

        if (hasEmptyFields()) {
            showError("Please fill in all fields.");
            return;
        }

        int subjectId = (int) subjectTable.getValueAt(subjectTable.getSelectedRow(), 0);
        Subject subject = new Subject(subjectId, subjectCodeField.getText().trim(), subjectNameField.getText().trim());

        if (subjectDao.update(subject)) {
            refreshTable();
            clearFields();
            showSuccess("Subject updated successfully.");
        } else {
            showError("Failed to update subject.");
        }
    }

    private void handleDelete() {
        int selectedRow = subjectTable.getSelectedRow();

        if (selectedRow == -1) {
            showError("Please select a subject to delete.");
            return;
        }

        String subjectCode = (String) subjectTable.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete subject " + subjectCode + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int subjectId = (int) subjectTable.getValueAt(selectedRow, 0);
            subjectDao.delete(subjectId);
            refreshTable();
            clearFields();
        }
    }

    private void handleSearch() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            refreshTable();
            return;
        }

        List<Subject> results = subjectDao.search(keyword);
        populateTable(results);
    }

    private boolean hasEmptyFields() {
        return subjectCodeField.getText().trim().isEmpty()
            || subjectNameField.getText().trim().isEmpty();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void refreshTable() {
        List<Subject> subjects = subjectDao.getAll();
        populateTable(subjects);
    }

    private void populateTable(List<Subject> subjects) {
        DefaultTableModel model = (DefaultTableModel) subjectTable.getModel();
        model.setRowCount(0);

        for (Subject subject : subjects) {
            model.addRow(new Object[]{
                subject.getSubjectId(),
                subject.getSubjectCode(),
                subject.getSubjectName()
            });
        }
    }

    private void loadSelectedSubject() {
        int selectedRow = subjectTable.getSelectedRow();

        if (selectedRow == -1) {
            return;
        }

        subjectCodeField.setText((String) subjectTable.getValueAt(selectedRow, 1));
        subjectNameField.setText((String) subjectTable.getValueAt(selectedRow, 2));
    }

    private void clearFields() {
        subjectCodeField.setText("");
        subjectNameField.setText("");
        subjectTable.clearSelection();
    }

    private void handleBack(User currentUser) {
        if (hasUnsavedChanges() && !confirmDiscard()) {
            return;
        }
        new DashboardForm(currentUser).setVisible(true);
        dispose();
    }

    private boolean hasUnsavedChanges() {
        return !subjectCodeField.getText().trim().isEmpty()
            || !subjectNameField.getText().trim().isEmpty();
    }

    private boolean confirmDiscard() {
        int choice = JOptionPane.showConfirmDialog(this,
            "You have unsaved changes. Discard and go back?",
            "Unsaved Changes", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        return choice == JOptionPane.YES_OPTION;
    }
}
