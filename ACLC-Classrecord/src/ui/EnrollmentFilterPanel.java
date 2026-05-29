package ui;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.Subject;
import util.StyleConstants;

public class EnrollmentFilterPanel extends JPanel {

    private JComboBox<Subject> subjectBox;
    private JComboBox<String> sectionBox;

    public EnrollmentFilterPanel() {
        setLayout(new GridLayout(1, 4, StyleConstants.GRID_H_GAP, StyleConstants.GRID_V_GAP));
        setBorder(StyleConstants.INPUT_BORDER);

        subjectBox = new JComboBox<>();
        sectionBox = new JComboBox<>();

        add(new JLabel("Subject:"));
        add(subjectBox);
        add(new JLabel("Section:"));
        add(sectionBox);
    }

    public Subject getSelectedSubject() {
        return (Subject) subjectBox.getSelectedItem();
    }

    public String getSelectedSection() {
        return (String) sectionBox.getSelectedItem();
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
}
