package ui;

import java.awt.GridLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.Student;
import util.StyleConstants;

public class StudentInputPanel extends JPanel {

    private JTextField studentIdField;
    private JTextField firstnameField;
    private JTextField lastnameField;
    private JTextField courseField;
    private JTextField yearLevelField;
    private JComboBox<String> sectionBox;
    private JComboBox<String> genderBox;

    private static final String[] SECTION_OPTIONS = {"", "A", "B", "C", "D", "E"};

    public StudentInputPanel() {
        setLayout(new GridLayout(4, 4, StyleConstants.GRID_H_GAP, StyleConstants.GRID_V_GAP));
        setBorder(StyleConstants.INPUT_BORDER);
        createFields();
        addFieldsToPanel();
    }

    public Student toStudent() {
        return new Student(
            studentIdField.getText().trim(),
            firstnameField.getText().trim(),
            lastnameField.getText().trim(),
            courseField.getText().trim(),
            parseYearLevel(),
            getSelectedSection(),
            (String) genderBox.getSelectedItem()
        );
    }

    public void fromStudent(Student student) {
        studentIdField.setText(student.getStudentId());
        firstnameField.setText(student.getFirstname());
        lastnameField.setText(student.getLastname());
        courseField.setText(student.getCourse());
        yearLevelField.setText(String.valueOf(student.getYearLevel()));
        sectionBox.setSelectedItem(student.getSection());
        genderBox.setSelectedItem(student.getGender());
    }

    public void clear() {
        studentIdField.setText("");
        firstnameField.setText("");
        lastnameField.setText("");
        courseField.setText("");
        yearLevelField.setText("");
        sectionBox.setSelectedIndex(0);
        genderBox.setSelectedIndex(0);
        studentIdField.setEditable(true);
    }

    public void lockStudentId() {
        studentIdField.setEditable(false);
    }

    public boolean hasChanges() {
        return !studentIdField.getText().trim().isEmpty()
            || !firstnameField.getText().trim().isEmpty()
            || !lastnameField.getText().trim().isEmpty()
            || !courseField.getText().trim().isEmpty()
            || !yearLevelField.getText().trim().isEmpty()
            || !getSelectedSection().isEmpty();
    }

    public boolean hasEmptyFields() {
        return studentIdField.getText().trim().isEmpty()
            || firstnameField.getText().trim().isEmpty()
            || lastnameField.getText().trim().isEmpty()
            || courseField.getText().trim().isEmpty()
            || yearLevelField.getText().trim().isEmpty()
            || getSelectedSection().isEmpty();
    }

    public boolean hasInvalidYearLevel() {
        int year = parseYearLevel();
        return year < 1 || year > 4;
    }

    private void createFields() {
        studentIdField = new JTextField();
        firstnameField = new JTextField();
        lastnameField = new JTextField();
        courseField = new JTextField();
        yearLevelField = new JTextField();
        sectionBox = new JComboBox<>(SECTION_OPTIONS);
        sectionBox.setEditable(true);
        genderBox = new JComboBox<>(new String[]{"Male", "Female"});
    }

    private void addFieldsToPanel() {
        add(new JLabel("Student ID:"));
        add(studentIdField);
        add(new JLabel("First Name:"));
        add(firstnameField);

        add(new JLabel("Last Name:"));
        add(lastnameField);
        add(new JLabel("Course:"));
        add(courseField);

        add(new JLabel("Year Level:"));
        add(yearLevelField);
        add(new JLabel("Section:"));
        add(sectionBox);

        add(new JLabel("Gender:"));
        add(genderBox);
        add(new JLabel());
        add(new JLabel());
    }

    private String getSelectedSection() {
        Object item = sectionBox.getSelectedItem();
        return item != null ? item.toString().trim() : "";
    }

    private int parseYearLevel() {
        try {
            return Integer.parseInt(yearLevelField.getText().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
