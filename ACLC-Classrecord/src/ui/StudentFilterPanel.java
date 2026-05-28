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

public class StudentFilterPanel extends JPanel {

    private JComboBox<Object> subjectBox;
    private JTextField searchField;

    public StudentFilterPanel() {
        setLayout(new GridLayout(1, 4, StyleConstants.GRID_H_GAP, StyleConstants.GRID_V_GAP));
        setBorder(StyleConstants.INPUT_BORDER);

        subjectBox = new JComboBox<>();
        searchField = new JTextField();

        add(new JLabel("Enrolled in:"));
        add(subjectBox);
        add(new JLabel("Search:"));
        add(searchField);
    }

    public Subject getSelectedSubject() {
        Object selected = subjectBox.getSelectedItem();
        if (selected instanceof Subject) {
            return (Subject) selected;
        }
        return null;
    }

    public boolean isAllSubjects() {
        return getSelectedSubject() == null;
    }

    public String getSearchKeyword() {
        return searchField.getText().trim();
    }

    public void populateSubjects(List<Subject> subjects) {
        subjectBox.removeAllItems();
        subjectBox.addItem("All Subjects");
        for (Subject subject : subjects) {
            subjectBox.addItem(subject);
        }
    }

    public void addSubjectListener(ActionListener listener) {
        subjectBox.addActionListener(listener);
    }

    public void addSearchListener(ActionListener listener) {
        searchField.addActionListener(listener);
    }
}
