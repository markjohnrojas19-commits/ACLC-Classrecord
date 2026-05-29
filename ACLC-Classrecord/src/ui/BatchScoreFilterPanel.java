package ui;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.GradingSeason;
import model.Subject;
import util.StyleConstants;

public class BatchScoreFilterPanel extends JPanel {

    private JComboBox<Subject> subjectBox;
    private JComboBox<String> sectionBox;
    private JComboBox<String> seasonBox;
    private JTextField assessmentNameField;
    private JButton loadButton;

    public BatchScoreFilterPanel() {
        setLayout(new GridLayout(2, 6, StyleConstants.GRID_H_GAP, StyleConstants.GRID_V_GAP));
        setBorder(StyleConstants.INPUT_BORDER);

        subjectBox = new JComboBox<>();
        sectionBox = new JComboBox<>();
        seasonBox = createSeasonBox();
        assessmentNameField = new JTextField();
        loadButton = new JButton("Load Students");

        add(new JLabel("Subject:"));
        add(subjectBox);
        add(new JLabel("Section:"));
        add(sectionBox);
        add(new JLabel(""));
        add(new JLabel(""));

        add(new JLabel("Season:"));
        add(seasonBox);
        add(new JLabel("Assessment:"));
        add(assessmentNameField);
        add(new JLabel(""));
        add(loadButton);
    }

    public Subject getSelectedSubject() {
        return (Subject) subjectBox.getSelectedItem();
    }

    public String getSelectedSection() {
        return (String) sectionBox.getSelectedItem();
    }

    public GradingSeason getSelectedSeason() {
        String selected = (String) seasonBox.getSelectedItem();
        return GradingSeason.fromDbValue(selected);
    }

    public String getAssessmentName() {
        return assessmentNameField.getText().trim();
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

    public void addSeasonListener(ActionListener listener) {
        seasonBox.addActionListener(listener);
    }

    public void addLoadListener(ActionListener listener) {
        loadButton.addActionListener(listener);
    }

    private JComboBox<String> createSeasonBox() {
        JComboBox<String> box = new JComboBox<>();
        for (GradingSeason season : GradingSeason.values()) {
            box.addItem(season.toDisplayName());
        }
        return box;
    }
}
