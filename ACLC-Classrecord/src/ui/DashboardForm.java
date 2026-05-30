package ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.User;
import util.StyleConstants;

public class DashboardForm extends JFrame {

    private User currentUser;
    private DashboardStatsPanel statsPanel;

    public DashboardForm(User user) {
        this.currentUser = user;
        this.statsPanel = new DashboardStatsPanel();

        setTitle("ACLC Class Record — Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(statsPanel, BorderLayout.CENTER);
        add(createNavigationPanel(), BorderLayout.SOUTH);

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

    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 5,
            StyleConstants.BUTTON_GAP, StyleConstants.BUTTON_GAP));
        panel.setBorder(StyleConstants.BUTTON_BORDER);

        JButton studentsButton = new JButton("Students");
        JButton subjectsButton = new JButton("Subjects");
        JButton enrollmentButton = new JButton("Enrollment");
        JButton gradesButton = new JButton("Grades");
        JButton attendanceButton = new JButton("Attendance");

        studentsButton.addActionListener(e -> openStudentForm());
        subjectsButton.addActionListener(e -> openSubjectForm());
        enrollmentButton.addActionListener(e -> openEnrollmentForm());
        gradesButton.addActionListener(e -> openGradeForm());
        attendanceButton.addActionListener(e -> openAttendanceForm());

        panel.add(studentsButton);
        panel.add(subjectsButton);
        panel.add(enrollmentButton);
        panel.add(gradesButton);
        panel.add(attendanceButton);

        return panel;
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
