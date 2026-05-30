package ui;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.Subject;
import util.StyleConstants;

public class AttendanceFilterPanel extends JPanel {

    private JComboBox<Subject> subjectBox;
    private JComboBox<String> sectionBox;
    private JComboBox<String> startDateBox;
    private JComboBox<String> endDateBox;

    public AttendanceFilterPanel() {
        setLayout(new GridLayout(2, 4, StyleConstants.GRID_H_GAP, StyleConstants.GRID_V_GAP));
        setBorder(StyleConstants.INPUT_BORDER);

        subjectBox = new JComboBox<>();
        sectionBox = new JComboBox<>();
        startDateBox = createEditableDateBox(LocalDate.now().toString());
        endDateBox = createEditableDateBox("");

        add(new JLabel("Subject:"));
        add(subjectBox);
        add(new JLabel("Course/Section:"));
        add(sectionBox);
        add(new JLabel("From:"));
        add(startDateBox);
        add(new JLabel("To (optional):"));
        add(endDateBox);
    }

    private JComboBox<String> createEditableDateBox(String defaultText) {
        JComboBox<String> box = new JComboBox<>();
        box.setEditable(true);
        box.getEditor().setItem(defaultText);
        return box;
    }

    public Subject getSelectedSubject() {
        return (Subject) subjectBox.getSelectedItem();
    }

    public String getSelectedSection() {
        return (String) sectionBox.getSelectedItem();
    }

    public LocalDate getStartDate() {
        return parseDate(getDateText(startDateBox));
    }

    public LocalDate getEndDate() {
        return parseDate(getDateText(endDateBox));
    }

    public boolean isDateRangeMode() {
        LocalDate start = getStartDate();
        LocalDate end = getEndDate();
        return start != null && end != null && !start.equals(end);
    }

    private String getDateText(JComboBox<String> box) {
        Object item = box.getEditor().getItem();
        return item == null ? "" : item.toString();
    }

    private LocalDate parseDate(String text) {
        try {
            String trimmed = text.trim();
            if (trimmed.isEmpty()) {
                return null;
            }
            return LocalDate.parse(trimmed);
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

    public void populateDates(List<LocalDate> dates) {
        String currentStart = getDateText(startDateBox);
        String currentEnd = getDateText(endDateBox);

        startDateBox.removeAllItems();
        endDateBox.removeAllItems();

        for (LocalDate date : dates) {
            String dateStr = date.toString();
            startDateBox.addItem(dateStr);
            endDateBox.addItem(dateStr);
        }

        startDateBox.getEditor().setItem(currentStart);
        endDateBox.getEditor().setItem(currentEnd);
    }

    public void addSubjectListener(ActionListener listener) {
        subjectBox.addActionListener(listener);
    }

    public void addSectionListener(ActionListener listener) {
        sectionBox.addActionListener(listener);
    }

    public void addDateListener(ActionListener listener) {
        startDateBox.addActionListener(listener);
        endDateBox.addActionListener(listener);
    }
}
