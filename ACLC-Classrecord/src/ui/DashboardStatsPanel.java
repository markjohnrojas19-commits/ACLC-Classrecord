package ui;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import dao.DashboardDao;
import util.StyleConstants;

public class DashboardStatsPanel extends JPanel {

    private DashboardDao dashboardDao;
    private JLabel studentsLabel;
    private JLabel subjectsLabel;
    private JLabel passedLabel;
    private JLabel failedLabel;

    public DashboardStatsPanel() {
        this.dashboardDao = new DashboardDao();

        setLayout(new GridLayout(2, 2, 20, 20));
        setBorder(StyleConstants.STATS_BORDER);
        setBackground(StyleConstants.WHITE);

        studentsLabel = createStatLabel("Total Students", "0");
        subjectsLabel = createStatLabel("Total Subjects", "0");
        passedLabel = createStatLabel("Passed", "0");
        failedLabel = createStatLabel("Failed", "0");

        add(studentsLabel);
        add(subjectsLabel);
        add(passedLabel);
        add(failedLabel);
    }

    public void refresh() {
        updateLabel(studentsLabel, "Total Students", dashboardDao.countStudents());
        updateLabel(subjectsLabel, "Total Subjects", dashboardDao.countSubjects());
        updatePassedLabel(dashboardDao.countPassed());
        updateFailedLabel(dashboardDao.countFailed());
    }

    private void updateLabel(JLabel label, String title, int count) {
        String display = (count < 0) ? "Error" : String.valueOf(count);
        label.setText(title + ": " + display);
    }

    private void updatePassedLabel(int count) {
        updateLabel(passedLabel, "Passed", count);
        if (count > 0) {
            passedLabel.setForeground(StyleConstants.SUCCESS);
        }
    }

    private void updateFailedLabel(int count) {
        updateLabel(failedLabel, "Failed", count);
        if (count > 0) {
            failedLabel.setForeground(StyleConstants.DANGER);
        }
    }

    private JLabel createStatLabel(String title, String value) {
        JLabel label = new JLabel(title + ": " + value, SwingConstants.CENTER);
        label.setFont(StyleConstants.BODY_FONT);
        label.setForeground(StyleConstants.TEXT_PRIMARY);
        return label;
    }
}
