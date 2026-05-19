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
    private JTextField sectionField;
    private JComboBox<String> genderBox;

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
            sectionField.getText().trim(),
            (String) genderBox.getSelectedItem()
        );
    }

    public void fromStudent(Student student) {
        studentIdField.setText(student.getStudentId());
        firstnameField.setText(student.getFirstname());
        lastnameField.setText(student.getLastname());
        courseField.setText(student.getCourse());
        yearLevelField.setText(String.valueOf(student.getYearLevel()));
        sectionField.setText(student.getSection());
        genderBox.setSelectedItem(student.getGender());
    }

    public void clear() {
        studentIdField.setText("");
        firstnameField.setText("");
        lastnameField.setText("");
        courseField.setText("");
        yearLevelField.setText("");
        sectionField.setText("");
        genderBox.setSelectedIndex(0);
        studentIdField.setEditable(true);
    }

    public void lockStudentId() {
        studentIdField.setEditable(false);
    }

    public boolean hasEmptyFields() {
        return studentIdField.getText().trim().isEmpty()
            || firstnameField.getText().trim().isEmpty()
            || lastnameField.getText().trim().isEmpty()
            || courseField.getText().trim().isEmpty()
            || yearLevelField.getText().trim().isEmpty()
            || sectionField.getText().trim().isEmpty();
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
        sectionField = new JTextField();
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
        add(sectionField);

        add(new JLabel("Gender:"));
        add(genderBox);
        add(new JLabel());
        add(new JLabel());
    }

    private int parseYearLevel() {
        try {
            return Integer.parseInt(yearLevelField.getText().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
