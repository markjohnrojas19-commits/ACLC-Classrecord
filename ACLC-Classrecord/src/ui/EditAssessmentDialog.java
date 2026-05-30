package ui;

import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import dao.AssessmentDao;
import model.Assessment;
import util.StyleConstants;

public class EditAssessmentDialog extends JDialog {

    private JTextField scoreField;
    private JTextField totalItemsField;
    private JTextField dateField;

    private Assessment assessment;
    private Runnable onSaveComplete;

    public EditAssessmentDialog(JFrame parent, Assessment assessment, Runnable onSaveComplete) {
        super(parent, "Edit Assessment", true);
        this.assessment = assessment;
        this.onSaveComplete = onSaveComplete;

        setSize(350, 200);
        setLocationRelativeTo(parent);
        setResizable(false);

        add(createFormPanel());
        populateFields();

        setVisible(true);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2,
            StyleConstants.GRID_H_GAP, StyleConstants.GRID_V_GAP));
        panel.setBorder(StyleConstants.INPUT_BORDER);

        scoreField = new JTextField();
        totalItemsField = new JTextField();
        dateField = new JTextField();

        javax.swing.JButton saveButton = new javax.swing.JButton("Save");
        saveButton.addActionListener(e -> handleSave());

        panel.add(new JLabel("Score:"));
        panel.add(scoreField);
        panel.add(new JLabel("Total Items:"));
        panel.add(totalItemsField);
        panel.add(new JLabel("Date (yyyy-MM-dd):"));
        panel.add(dateField);
        panel.add(new JLabel());
        panel.add(saveButton);

        return panel;
    }

    private void populateFields() {
        scoreField.setText(String.format("%.0f", assessment.getScore()));
        totalItemsField.setText(String.format("%.0f", assessment.getTotalItems()));
        dateField.setText(assessment.getDate() != null ? assessment.getDate().toString() : "");
    }

    private void handleSave() {
        double score = parseScore();
        if (score < 0) {
            return;
        }

        double totalItems = parseTotalItems();
        if (totalItems < 0) {
            return;
        }

        if (score > totalItems) {
            showError("Score cannot exceed total items.");
            return;
        }

        LocalDate date = parseDate();
        if (date == null && !dateField.getText().trim().isEmpty()) {
            return;
        }

        updateAssessment(score, totalItems, date);
    }

    private double parseScore() {
        try {
            double score = Double.parseDouble(scoreField.getText().trim());
            if (score < 0) {
                showError("Score must be 0 or greater.");
                return -1;
            }
            return score;
        } catch (NumberFormatException e) {
            showError("Score must be a number.");
            return -1;
        }
    }

    private double parseTotalItems() {
        try {
            double totalItems = Double.parseDouble(totalItemsField.getText().trim());
            if (totalItems <= 0) {
                showError("Total items must be greater than 0.");
                return -1;
            }
            return totalItems;
        } catch (NumberFormatException e) {
            showError("Total items must be a number.");
            return -1;
        }
    }

    private LocalDate parseDate() {
        String text = dateField.getText().trim();
        if (text.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            showError("Invalid date format. Use yyyy-MM-dd.");
            return null;
        }
    }

    private void updateAssessment(double score, double totalItems, LocalDate date) {
        assessment.setScore(score);
        assessment.setTotalItems(totalItems);
        assessment.setDate(date);

        if (new AssessmentDao().update(assessment)) {
            onSaveComplete.run();
            dispose();
        } else {
            showError("Failed to update assessment.");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
