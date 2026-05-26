package ui;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.Subject;
import util.StyleConstants;

public class AttendanceFilterPanel extends JPanel {

    private JComboBox<Subject> subjectBox;
    private JComboBox<String> sectionBox;
    private JTextField dateField;

    public AttendanceFilterPanel() {
        setLayout(new GridLayout(1, 6, StyleConstants.GRID_H_GAP, StyleConstants.GRID_V_GAP));
        setBorder(StyleConstants.INPUT_BORDER);

        subjectBox = new JComboBox<>();
        sectionBox = new JComboBox<>();
        dateField = new JTextField(LocalDate.now().toString());

        add(new JLabel("Subject:"));
        add(subjectBox);
        add(new JLabel("Section:"));
        add(sectionBox);
        add(new JLabel("Date (yyyy-mm-dd):"));
        add(dateField);
    }

    public Subject getSelectedSubject() {
        return (Subject) subjectBox.getSelectedItem();
    }

    public String getSelectedSection() {
        return (String) sectionBox.getSelectedItem();
    }

    public LocalDate getSelectedDate() {
        try {
            return LocalDate.parse(dateField.getText().trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public void populateSubjects(List<Subject> subjects) {
        subjectBox.removeAllItems();
        for (Subject subject : subjects) {
            subjectBox.addItem(subject);
        }
    }

    public void populateSections(List<String> sections) {
        sectionBox.removeAllItems();
        for (String section : sections) {
            sectionBox.addItem(section);
        }
    }

    public void addSubjectListener(ActionListener listener) {
        subjectBox.addActionListener(listener);
    }

    public void addSectionListener(ActionListener listener) {
        sectionBox.addActionListener(listener);
    }

    public void addDateListener(ActionListener listener) {
        dateField.addActionListener(listener);
    }
}
