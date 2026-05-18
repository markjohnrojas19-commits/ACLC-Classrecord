package ui;

import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import model.Student;
import model.Subject;

public class GradeInputPanel extends JPanel {

    private JComboBox<Student> studentBox;
    private JComboBox<Subject> subjectBox;
    private JTextField quizField;
    private JTextField assignmentField;
    private JTextField examField;
    private JLabel resultLabel;

    public GradeInputPanel() {
        setLayout(new GridLayout(3, 4, 10, 8));
        setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        createFields();
        addFieldsToPanel();
    }

    public Student getSelectedStudent() {
        return (Student) studentBox.getSelectedItem();
    }

    public Subject getSelectedSubject() {
        return (Subject) subjectBox.getSelectedItem();
    }

    public double getQuiz() {
        return parseDouble(quizField);
    }

    public double getAssignment() {
        return parseDouble(assignmentField);
    }

    public double getExam() {
        return parseDouble(examField);
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

    public void clear() {
        quizField.setText("");
        assignmentField.setText("");
        examField.setText("");
        resultLabel.setText("—");
        if (studentBox.getItemCount() > 0) {
            studentBox.setSelectedIndex(0);
        }
        if (subjectBox.getItemCount() > 0) {
            subjectBox.setSelectedIndex(0);
        }
    }

    public boolean hasEmptyScores() {
        return quizField.getText().trim().isEmpty()
            || assignmentField.getText().trim().isEmpty()
            || examField.getText().trim().isEmpty();
    }

    public boolean hasNoSelections() {
        return studentBox.getSelectedItem() == null
            || subjectBox.getSelectedItem() == null;
    }

    public void updateResult(double finalGrade, String remarks) {
        resultLabel.setText(String.format("%.2f — %s", finalGrade, remarks));
    }

    public void setScores(double quiz, double assignment, double exam) {
        quizField.setText(String.valueOf(quiz));
        assignmentField.setText(String.valueOf(assignment));
        examField.setText(String.valueOf(exam));
    }

    public void selectStudent(String studentId) {
        for (int i = 0; i < studentBox.getItemCount(); i++) {
            if (studentBox.getItemAt(i).getStudentId().equals(studentId)) {
                studentBox.setSelectedIndex(i);
                return;
            }
        }
    }

    public void selectSubject(int subjectId) {
        for (int i = 0; i < subjectBox.getItemCount(); i++) {
            if (subjectBox.getItemAt(i).getSubjectId() == subjectId) {
                subjectBox.setSelectedIndex(i);
                return;
            }
        }
    }

    public void addScoreChangeListener(Runnable listener) {
        DocumentListener docListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { listener.run(); }
            @Override
            public void removeUpdate(DocumentEvent e) { listener.run(); }
            @Override
            public void changedUpdate(DocumentEvent e) { listener.run(); }
        };

        quizField.getDocument().addDocumentListener(docListener);
        assignmentField.getDocument().addDocumentListener(docListener);
        examField.getDocument().addDocumentListener(docListener);
    }

    private void createFields() {
        studentBox = new JComboBox<>();
        subjectBox = new JComboBox<>();
        quizField = new JTextField();
        assignmentField = new JTextField();
        examField = new JTextField();
        resultLabel = new JLabel("—");
        resultLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
    }

    private void addFieldsToPanel() {
        add(new JLabel("Student:"));
        add(studentBox);
        add(new JLabel("Subject:"));
        add(subjectBox);

        add(new JLabel("Quiz:"));
        add(quizField);
        add(new JLabel("Assignment:"));
        add(assignmentField);

        add(new JLabel("Exam:"));
        add(examField);
        add(new JLabel("Result:"));
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
