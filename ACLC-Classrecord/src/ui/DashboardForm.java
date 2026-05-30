package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import dao.SemesterDao;
import model.Semester;
import model.User;
import util.ActiveSemester;
import util.StyleConstants;

public class DashboardForm extends JFrame {

    private User currentUser;
    private DashboardStatsPanel statsPanel;
    private SemesterDao semesterDao;

    public DashboardForm(User user) {
        this.currentUser = user;
        this.statsPanel = new DashboardStatsPanel();
        this.semesterDao = new SemesterDao();

        setTitle("ACLC Class Record \u2014 Dashboard");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        topPanel.add(createSemesterPanel(), BorderLayout.CENTER);
        topPanel.add(createNavigationPanel(), BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(statsPanel, BorderLayout.CENTER);

        statsPanel.refresh();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(StyleConstants.HEADER_BORDER);
        panel.setBackground(StyleConstants.WHITE);

        JLabel welcomeLabel = new JLabel(
            "ACLC Class Record — " + currentUser.getUsername());
        welcomeLabel.setFont(StyleConstants.TITLE_FONT);
        welcomeLabel.setForeground(StyleConstants.PRIMARY);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> handleLogout());

        panel.add(welcomeLabel, BorderLayout.WEST);
        panel.add(logoutButton, BorderLayout.EAST);

        return panel;
    }

    private JPanel createSemesterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panel.setBackground(StyleConstants.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel label = new JLabel("Semester:");
        label.setFont(StyleConstants.SMALL_BOLD_FONT);

        JComboBox<Semester> semesterBox = new JComboBox<>();
        populateSemesterBox(semesterBox);
        semesterBox.setFont(StyleConstants.BODY_FONT);

        semesterBox.addActionListener(e -> {
            Semester selected = (Semester) semesterBox.getSelectedItem();
            if (selected != null) {
                switchSemester(selected);
            }
        });

        JButton newSemesterButton = new JButton("New Semester");
        newSemesterButton.addActionListener(e -> handleNewSemester(semesterBox));

        JButton deleteSemesterButton = new JButton("Delete Semester");
        deleteSemesterButton.addActionListener(e -> handleDeleteSemester(semesterBox));

        panel.add(label);
        panel.add(semesterBox);
        panel.add(newSemesterButton);
        panel.add(deleteSemesterButton);

        return panel;
    }

    private void populateSemesterBox(JComboBox<Semester> semesterBox) {
        List<Semester> semesters = semesterDao.getAll();
        int activeId = ActiveSemester.getId();

        for (Semester semester : semesters) {
            semesterBox.addItem(semester);
            if (semester.getSemesterId() == activeId) {
                semesterBox.setSelectedItem(semester);
            }
        }
    }

    private void switchSemester(Semester selected) {
        if (selected.getSemesterId() == ActiveSemester.getId()) {
            return;
        }
        ActiveSemester.setId(selected.getSemesterId());
        semesterDao.setActive(selected.getSemesterId());
        statsPanel.refresh();
    }

    private void handleNewSemester(JComboBox<Semester> semesterBox) {
        String schoolYear = JOptionPane.showInputDialog(this,
            "School Year (e.g., 2025-2026):", "New Semester",
            JOptionPane.QUESTION_MESSAGE);

        if (schoolYear == null || schoolYear.trim().isEmpty()) {
            return;
        }

        String[] options = {"1st Semester", "2nd Semester"};
        int choice = JOptionPane.showOptionDialog(this,
            "Which semester?", "New Semester",
            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, options, options[0]);

        if (choice < 0) {
            return;
        }

        Semester newSemester = new Semester(0, schoolYear.trim(), choice + 1);
        if (semesterDao.add(newSemester)) {
            java.awt.event.ActionListener[] listeners = semesterBox.getActionListeners();
            for (java.awt.event.ActionListener l : listeners) {
                semesterBox.removeActionListener(l);
            }
            semesterBox.removeAllItems();
            populateSemesterBox(semesterBox);
            for (java.awt.event.ActionListener l : listeners) {
                semesterBox.addActionListener(l);
            }
            JOptionPane.showMessageDialog(this,
                "Semester added: " + newSemester.toDisplayName(),
                "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to add semester. It may already exist.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteSemester(JComboBox<Semester> semesterBox) {
        Semester selected = (Semester) semesterBox.getSelectedItem();
        if (selected == null) {
            return;
        }

        if (semesterDao.count() <= 1) {
            JOptionPane.showMessageDialog(this,
                "Cannot delete the only semester.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete \"" + selected.toDisplayName() + "\"?\n\n"
            + "This will permanently delete ALL enrollments, grades,\n"
            + "and attendance records for this semester.",
            "Delete Semester", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        boolean wasActive = (selected.getSemesterId() == ActiveSemester.getId());

        if (semesterDao.delete(selected.getSemesterId())) {
            java.awt.event.ActionListener[] listeners = semesterBox.getActionListeners();
            for (java.awt.event.ActionListener l : listeners) {
                semesterBox.removeActionListener(l);
            }
            semesterBox.removeAllItems();
            populateSemesterBox(semesterBox);
            for (java.awt.event.ActionListener l : listeners) {
                semesterBox.addActionListener(l);
            }

            if (wasActive) {
                Semester first = (Semester) semesterBox.getItemAt(0);
                if (first != null) {
                    semesterBox.setSelectedItem(first);
                    ActiveSemester.setId(first.getSemesterId());
                    semesterDao.setActive(first.getSemesterId());
                }
            }

            statsPanel.refresh();
            JOptionPane.showMessageDialog(this,
                "Semester deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to delete semester.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        TitledBorder titledBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(StyleConstants.BORDER_COLOR, 1),
            "Navigation", TitledBorder.LEFT, TitledBorder.TOP,
            StyleConstants.SMALL_BOLD_FONT, StyleConstants.TEXT_SECONDARY);

        panel.setBorder(BorderFactory.createCompoundBorder(
            StyleConstants.BUTTON_BORDER,
            BorderFactory.createCompoundBorder(
                titledBorder,
                BorderFactory.createEmptyBorder(5, 5, 8, 5))));

        panel.add(createNavButton("Students", "Manage records", e -> openStudentForm()));
        panel.add(createNavButton("Subjects", "Manage courses", e -> openSubjectForm()));
        panel.add(createNavButton("Enrollment", "Enroll students", e -> openEnrollmentForm()));
        panel.add(createNavButton("Grades", "View grades", e -> openGradeForm()));
        panel.add(createNavButton("Attendance", "Mark attendance", e -> openAttendanceForm()));

        return panel;
    }

    private JButton createNavButton(String title, String description,
                                     java.awt.event.ActionListener action) {
        String html = "<html><center><b>" + title + "</b><br>"
            + "<span style='font-size:9px;color:#6c757d;'>" + description + "</span>"
            + "</center></html>";

        JButton button = new JButton(html);
        button.setPreferredSize(new Dimension(135, 50));
        button.addActionListener(action);

        return button;
    }

    private void openSubjectForm() {
        new SubjectForm(currentUser).setVisible(true);
        dispose();
    }

    private void openGradeForm() {
        new GradeForm(currentUser).setVisible(true);
        dispose();
    }

    private void openEnrollmentForm() {
        new EnrollmentForm(currentUser).setVisible(true);
        dispose();
    }

    private void openAttendanceForm() {
        new AttendanceForm(currentUser).setVisible(true);
        dispose();
    }

    private void openStudentForm() {
        new StudentForm(currentUser).setVisible(true);
        dispose();
    }

    private void handleLogout() {
        new LoginForm().setVisible(true);
        dispose();
    }
}
