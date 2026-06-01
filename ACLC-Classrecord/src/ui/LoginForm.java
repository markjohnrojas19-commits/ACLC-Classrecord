package ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import dao.UserDao;
import model.User;
import util.StyleConstants;

public class LoginForm extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginForm() {
        setTitle("ACLC Class Record — Login");
        setSize(420, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createFieldsPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(StyleConstants.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel titleLabel = new JLabel("ACLC Class Record", SwingConstants.CENTER);
        titleLabel.setFont(StyleConstants.TITLE_FONT);
        titleLabel.setForeground(StyleConstants.PRIMARY);

        JLabel subtitleLabel = new JLabel("Sign in to continue", SwingConstants.CENTER);
        subtitleLabel.setFont(StyleConstants.BODY_FONT);
        subtitleLabel.setForeground(StyleConstants.TEXT_SECONDARY);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(subtitleLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createFieldsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2,
            StyleConstants.GRID_H_GAP, StyleConstants.GRID_V_GAP));
        panel.setBackground(StyleConstants.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 40, 10, 40));

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        usernameField.setFont(StyleConstants.BODY_FONT);
        passwordField.setFont(StyleConstants.BODY_FONT);

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        usernameLabel.setFont(StyleConstants.BODY_FONT);
        passwordLabel.setFont(StyleConstants.BODY_FONT);

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(StyleConstants.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 40, 15, 40));

        loginButton = createStyledLoginButton();

        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.setFont(StyleConstants.SMALL_BOLD_FONT);
        changePasswordButton.setForeground(StyleConstants.PRIMARY);
        changePasswordButton.setBorderPainted(false);
        changePasswordButton.setContentAreaFilled(false);
        changePasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        changePasswordButton.addActionListener(e -> handleChangePassword());

        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        linkPanel.setBackground(StyleConstants.WHITE);
        linkPanel.add(changePasswordButton);

        panel.add(loginButton, BorderLayout.NORTH);
        panel.add(linkPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JButton createStyledLoginButton() {
        JButton button = new JButton("Login");
        button.setFont(StyleConstants.SMALL_BOLD_FONT);
        button.setBackground(StyleConstants.PRIMARY);
        button.setForeground(StyleConstants.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(0, 36));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> handleLogin());
        return button;
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }

        User user = new UserDao().authenticate(username, password);

        if (user == null) {
            showError("Invalid username or password.");
            return;
        }

        onLoginSuccess(user);
    }

    private void onLoginSuccess(User user) {
        new DashboardForm(user).setVisible(true);
        dispose();
    }

    private void handleChangePassword() {
        String username = usernameField.getText().trim();
        String currentPassword = new String(passwordField.getPassword());

        if (username.isEmpty() || currentPassword.isEmpty()) {
            showError("Please enter your username and current password first.");
            return;
        }

        UserDao userDao = new UserDao();
        User user = userDao.authenticate(username, currentPassword);

        if (user == null) {
            showError("Invalid username or password.");
            return;
        }

        String newPassword = showPasswordInput("Enter new password:");
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return;
        }

        String confirmPassword = showPasswordInput("Confirm new password:");
        if (confirmPassword == null) {
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }

        if (userDao.updatePassword(user.getUserId(), newPassword)) {
            JOptionPane.showMessageDialog(this, "Password changed successfully.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            passwordField.setText("");
        } else {
            showError("Failed to change password.");
        }
    }

    private String showPasswordInput(String message) {
        return JOptionPane.showInputDialog(this, message,
            "Change Password", JOptionPane.PLAIN_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Login Error", JOptionPane.ERROR_MESSAGE);
    }
}
