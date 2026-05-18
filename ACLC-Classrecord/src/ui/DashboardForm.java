package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import model.User;

public class DashboardForm extends JFrame {

    private User currentUser;

    public DashboardForm(User user) {
        this.currentUser = user;

        setTitle("ACLC Class Record — Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createStatsPanel(), BorderLayout.CENTER);
        add(createNavigationPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername() + "!");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> handleLogout());

        panel.add(welcomeLabel, BorderLayout.WEST);
        panel.add(logoutButton, BorderLayout.EAST);

        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        panel.add(createStatLabel("Total Students", "0"));
        panel.add(createStatLabel("Total Subjects", "0"));
        panel.add(createStatLabel("Passed", "0"));
        panel.add(createStatLabel("Failed", "0"));

        return panel;
    }

    private JLabel createStatLabel(String title, String value) {
        JLabel label = new JLabel(title + ": " + value, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
        return label;
    }

    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 30, 10));

        JButton studentsButton = new JButton("Students");
        JButton subjectsButton = new JButton("Subjects");
        JButton gradesButton = new JButton("Grades");

        studentsButton.addActionListener(e -> openStudentForm());
        subjectsButton.addActionListener(e -> openSubjectForm());
        // TODO: Wire Grades button in later milestone

        panel.add(studentsButton);
        panel.add(subjectsButton);
        panel.add(gradesButton);

        return panel;
    }

    private void openSubjectForm() {
        new SubjectForm(currentUser).setVisible(true);
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
