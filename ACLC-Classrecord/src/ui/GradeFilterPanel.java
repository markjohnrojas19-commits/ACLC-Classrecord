package ui;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.Subject;
import util.StyleConstants;

public class GradeFilterPanel extends JPanel {

    private JComboBox<String> subjectBox;
    private JComboBox<String> sectionBox;
    private JComboBox<String> statusBox;
    private JTextField searchField;

    public GradeFilterPanel() {
        setLayout(new GridLayout(2, 4, StyleConstants.GRID_H_GAP, StyleConstants.GRID_V_GAP));
        setBorder(StyleConstants.INPUT_BORDER);

        subjectBox = new JComboBox<>();
        sectionBox = new JComboBox<>();
        statusBox = new JComboBox<>(new String[]{"All Results", "Passed Only", "Failed Only"});
        searchField = new JTextField();

        add(new JLabel("Subject:"));
        add(subjectBox);
        add(new JLabel("Course/Section:"));
        add(sectionBox);
        add(new JLabel("Status:"));
        add(statusBox);
        add(new JLabel("Search:"));
        add(searchField);
    }

    public String getSelectedSubject() {
        return (String) subjectBox.getSelectedItem();
    }

    public boolean isAllSubjects() {
        return "All Subjects".equals(getSelectedSubject());
    }

    public String getSelectedSection() {
        return (String) sectionBox.getSelectedItem();
    }

    public boolean isAllSections() {
        return "All Sections".equals(getSelectedSection());
    }

    public String getSelectedStatus() {
        return (String) statusBox.getSelectedItem();
    }

    public boolean isAllResults() {
        return "All Results".equals(getSelectedStatus());
    }

    public boolean isPassedOnly() {
        return "Passed Only".equals(getSelectedStatus());
    }

    public String getSearchKeyword() {
        return searchField.getText().trim();
    }

    public void populateSubjects(List<Subject> subjects) {
        subjectBox.removeAllItems();
        subjectBox.addItem("All Subjects");
        for (Subject subject : subjects) {
            subjectBox.addItem(subject.toString());
        }
    }

    public void populateSections(List<String> sections) {
        sectionBox.removeAllItems();
        sectionBox.addItem("All Sections");
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

    public void addStatusListener(ActionListener listener) {
        statusBox.addActionListener(listener);
    }

    public void addSearchListener(ActionListener listener) {
        searchField.addActionListener(listener);
    }
}
