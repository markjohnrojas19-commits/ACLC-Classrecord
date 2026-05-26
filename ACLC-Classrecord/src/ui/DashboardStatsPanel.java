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
    private JLabel enrolledLabel;
    private JLabel passedLabel;
    private JLabel failedLabel;
    private JLabel attendanceLabel;

    public DashboardStatsPanel() {
        this.dashboardDao = new DashboardDao();

        setLayout(new GridLayout(3, 2, 20, 20));
        setBorder(StyleConstants.STATS_BORDER);
        setBackground(StyleConstants.WHITE);

        studentsLabel = createStatLabel("Total Students", "0");
        subjectsLabel = createStatLabel("Total Subjects", "0");
        enrolledLabel = createStatLabel("Enrolled", "0");
        passedLabel = createStatLabel("Passed", "0");
        failedLabel = createStatLabel("Failed", "0");
        attendanceLabel = createStatLabel("Today's Attendance", "0 / 0");

        add(studentsLabel);
        add(subjectsLabel);
        add(enrolledLabel);
        add(passedLabel);
        add(failedLabel);
        add(attendanceLabel);
    }

    public void refresh() {
        updateLabel(studentsLabel, "Total Students", dashboardDao.countStudents());
        updateLabel(subjectsLabel, "Total Subjects", dashboardDao.countSubjects());
        updateLabel(enrolledLabel, "Enrolled", dashboardDao.countEnrolled());
        updatePassedLabel(dashboardDao.countPassed());
        updateFailedLabel(dashboardDao.countFailed());
        updateAttendanceLabel();
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

    private void updateAttendanceLabel() {
        int present = dashboardDao.countTodayPresent();
        int total = dashboardDao.countTodayTotal();

        if (present < 0 || total < 0) {
            attendanceLabel.setText("Today's Attendance: Error");
            return;
        }

        attendanceLabel.setText("Today's Attendance: " + present + " / " + total);

        if (total > 0) {
            attendanceLabel.setForeground(StyleConstants.PRIMARY);
        }
    }

    private JLabel createStatLabel(String title, String value) {
        JLabel label = new JLabel(title + ": " + value, SwingConstants.CENTER);
        label.setFont(StyleConstants.BODY_FONT);
        label.setForeground(StyleConstants.TEXT_PRIMARY);
        return label;
    }
}
