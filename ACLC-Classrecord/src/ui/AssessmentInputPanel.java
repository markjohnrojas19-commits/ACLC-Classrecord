package ui;

import java.awt.GridLayout;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.Assessment;
import model.GradingSeason;
import model.Student;
import model.Subject;
import util.GradeConstants;
import util.StyleConstants;

public class AssessmentInputPanel extends JPanel {

    private JComboBox<Student> studentBox;
    private JComboBox<Subject> subjectBox;
    private JComboBox<String> seasonBox;
    private JTextField assessmentNameField;
    private JTextField scoreField;
    private JLabel resultLabel;

    public AssessmentInputPanel() {
        setLayout(new GridLayout(3, 4, StyleConstants.GRID_H_GAP, StyleConstants.GRID_V_GAP));
        setBorder(StyleConstants.INPUT_BORDER);
        createFields();
        addFieldsToPanel();
    }

    public Student getSelectedStudent() {
        return (Student) studentBox.getSelectedItem();
    }

    public Subject getSelectedSubject() {
        return (Subject) subjectBox.getSelectedItem();
    }

    public GradingSeason getSelectedSeason() {
        String selected = (String) seasonBox.getSelectedItem();
        return GradingSeason.fromDbValue(selected);
    }

    public String getAssessmentName() {
        return assessmentNameField.getText().trim();
    }

    public double getScore() {
        return parseDouble(scoreField);
    }

    public void populateStudents(List<Student> students) {
        studentBox.removeAllItems();
        for (Student student : students) {
            studentBox.addItem(student);
        }
    }

    public void populateSubjects(List<Subject> subjects) {
        subjectBox.removeAllItems();
        for (Subject subject : subjects) {
            subjectBox.addItem(subject);
        }
    }

    public void fromAssessment(Assessment assessment) {
        selectStudent(assessment.getStudentId());
        selectSubject(assessment.getSubjectId());
        seasonBox.setSelectedItem(assessment.getSeason().toDisplayName());
        assessmentNameField.setText(assessment.getAssessmentName());
        scoreField.setText(String.valueOf(assessment.getScore()));
    }

    public void clear() {
        assessmentNameField.setText("");
        scoreField.setText("");
        resultLabel.setText("\u2014");
        resetDropdowns();
    }

    public void updateResult(String text) {
        resultLabel.setText(text);
    }

    public boolean hasNoSelections() {
        return studentBox.getSelectedItem() == null
            || subjectBox.getSelectedItem() == null;
    }

    public boolean hasEmptyFields() {
        return assessmentNameField.getText().trim().isEmpty()
            || scoreField.getText().trim().isEmpty();
    }

    public boolean hasInvalidScore() {
        try {
            double value = Double.parseDouble(scoreField.getText().trim());
            return value < GradeConstants.MIN_SCORE || value > GradeConstants.MAX_SCORE;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    private void selectStudent(String studentId) {
        for (int i = 0; i < studentBox.getItemCount(); i++) {
            if (studentBox.getItemAt(i).getStudentId().equals(studentId)) {
                studentBox.setSelectedIndex(i);
                return;
            }
        }
    }

    private void selectSubject(int subjectId) {
        for (int i = 0; i < subjectBox.getItemCount(); i++) {
            if (subjectBox.getItemAt(i).getSubjectId() == subjectId) {
                subjectBox.setSelectedIndex(i);
                return;
            }
        }
    }

    private void resetDropdowns() {
        if (studentBox.getItemCount() > 0) {
            studentBox.setSelectedIndex(0);
        }
        if (subjectBox.getItemCount() > 0) {
            subjectBox.setSelectedIndex(0);
        }
        seasonBox.setSelectedIndex(0);
    }

    private void createFields() {
        studentBox = new JComboBox<>();
        subjectBox = new JComboBox<>();
        seasonBox = createSeasonBox();
        assessmentNameField = new JTextField();
        scoreField = new JTextField();
        resultLabel = new JLabel("\u2014");
        resultLabel.setFont(StyleConstants.SMALL_BOLD_FONT);
    }

    private JComboBox<String> createSeasonBox() {
        JComboBox<String> box = new JComboBox<>();
        for (GradingSeason season : GradingSeason.values()) {
            box.addItem(season.toDisplayName());
        }
        return box;
    }

    private void addFieldsToPanel() {
        add(new JLabel("Student:"));
        add(studentBox);
        add(new JLabel("Subject:"));
        add(subjectBox);

        add(new JLabel("Season:"));
        add(seasonBox);
        add(new JLabel("Assessment:"));
        add(assessmentNameField);

        add(new JLabel("Score (0-100):"));
        add(scoreField);
        add(new JLabel("Avg:"));
        add(resultLabel);
    }

    private double parseDouble(JTextField field) {
        try {
            return Double.parseDouble(field.getText().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
