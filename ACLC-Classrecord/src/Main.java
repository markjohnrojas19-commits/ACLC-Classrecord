import javax.swing.SwingUtilities;

import ui.LoginForm;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
