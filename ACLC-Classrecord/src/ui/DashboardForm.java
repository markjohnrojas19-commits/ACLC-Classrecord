package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import model.User;
import util.StyleConstants;

public class DashboardForm extends JFrame {

    private User currentUser;
    private DashboardStatsPanel statsPanel;

    public DashboardForm(User user) {
        this.currentUser = user;
        this.statsPanel = new DashboardStatsPanel();

        setTitle("ACLC Class Record — Dashboard");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(createHeaderPanel(), BorderLayout.NORTH);
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
