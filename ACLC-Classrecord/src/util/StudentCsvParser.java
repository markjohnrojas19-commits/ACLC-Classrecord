package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.Student;

public class StudentCsvParser {

    private static final int EXPECTED_COLUMNS = 7;
    private static final int MIN_YEAR = 1;
    private static final int MAX_YEAR = 4;

    private List<Student> validStudents;
    private List<String> errorMessages;

    public StudentCsvParser() {
        validStudents = new ArrayList<>();
        errorMessages = new ArrayList<>();
    }

    public void parse(File file) {
        validStudents.clear();
        errorMessages.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            skipHeader(reader);
            parseDataRows(reader);
        } catch (IOException e) {
            errorMessages.add("Failed to read file: " + e.getMessage());
        }
    }

    public List<Student> getValidStudents() {
        return validStudents;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public boolean hasErrors() {
        return !errorMessages.isEmpty();
    }

    private void skipHeader(BufferedReader reader) throws IOException {
        reader.readLine();
    }

    private void parseDataRows(BufferedReader reader) throws IOException {
        String line;
        int rowNumber = 1;

        while ((line = reader.readLine()) != null) {
            rowNumber++;
            parseSingleRow(line.trim(), rowNumber);
        }
    }

    private void parseSingleRow(String line, int rowNumber) {
        if (line.isEmpty()) {
            return;
        }

        String[] columns = line.split(",", -1);

        if (columns.length != EXPECTED_COLUMNS) {
            errorMessages.add("Row " + rowNumber + ": Expected "
                + EXPECTED_COLUMNS + " columns, found " + columns.length);
            return;
        }

        String[] trimmed = trimColumns(columns);

        String emptyError = findEmptyColumn(trimmed, rowNumber);
        if (emptyError != null) {
            errorMessages.add(emptyError);
            return;
        }

        String yearError = validateYearLevel(trimmed[4], rowNumber);
        if (yearError != null) {
            errorMessages.add(yearError);
            return;
        }

        String genderError = validateGender(trimmed[6], rowNumber);
        if (genderError != null) {
            errorMessages.add(genderError);
            return;
        }

        Student student = buildStudent(trimmed);
        validStudents.add(student);
    }

    private String[] trimColumns(String[] columns) {
        String[] trimmed = new String[columns.length];
        for (int i = 0; i < columns.length; i++) {
            trimmed[i] = columns[i].trim();
        }
        return trimmed;
    }

    private String findEmptyColumn(String[] columns, int rowNumber) {
        String[] names = {"Student ID", "First Name", "Last Name",
            "Course", "Year Level", "Section", "Gender"};

        for (int i = 0; i < columns.length; i++) {
            if (columns[i].isEmpty()) {
                return "Row " + rowNumber + ": " + names[i] + " is empty";
            }
        }
        return null;
    }

    private String validateYearLevel(String value, int rowNumber) {
        try {
            int year = Integer.parseInt(value);
            if (year < MIN_YEAR || year > MAX_YEAR) {
                return "Row " + rowNumber + ": Year level must be between "
                    + MIN_YEAR + " and " + MAX_YEAR + ", got " + year;
            }
            return null;
        } catch (NumberFormatException e) {
            return "Row " + rowNumber + ": Year level must be a number, got '"
                + value + "'";
        }
    }

    private String validateGender(String value, int rowNumber) {
        if (!value.equalsIgnoreCase("Male") && !value.equalsIgnoreCase("Female")) {
            return "Row " + rowNumber + ": Gender must be 'Male' or 'Female', got '"
                + value + "'";
        }
        return null;
    }

    private Student buildStudent(String[] columns) {
        String gender = capitalizeGender(columns[6]);

        return new Student(
            columns[0],
            columns[1],
            columns[2],
            columns[3],
            Integer.parseInt(columns[4]),
            columns[5],
            gender
        );
    }

    private String capitalizeGender(String value) {
        return value.substring(0, 1).toUpperCase()
            + value.substring(1).toLowerCase();
    }
}
