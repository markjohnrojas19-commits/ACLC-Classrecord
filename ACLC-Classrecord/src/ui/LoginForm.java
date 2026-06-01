package ui;

import java.awt.BorderLayout;
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

import dao.UserDao;
import model.User;

public class LoginForm extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginForm() {
        setTitle("ACLC Class Record — Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        add(createFieldsPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createFieldsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 10, 40));

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        loginButton = new JButton("Login");
        loginButton.addActionListener(e -> handleLogin());

        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.addActionListener(e -> handleChangePassword());

        panel.add(loginButton);
        panel.add(changePasswordButton);

        return panel;
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
