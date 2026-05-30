package ui;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

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

        setLayout(new GridLayout(3, 2, 15, 15));
        setBorder(StyleConstants.STATS_BORDER);
        setBackground(StyleConstants.WHITE);

        studentsLabel = createStatLabel("Total Students", "0");
        subjectsLabel = createStatLabel("Total Subjects", "0");
        enrolledLabel = createStatLabel("Enrolled", "0");
        passedLabel = createStatLabel("Passed", "0");
        failedLabel = createStatLabel("Failed", "0");
        attendanceLabel = createStatLabel("Today's Attendance", "0 / 0");

        add(createStatPanel(studentsLabel));
        add(createStatPanel(subjectsLabel));
        add(createStatPanel(enrolledLabel));
        add(createStatPanel(passedLabel));
        add(createStatPanel(failedLabel));
        add(createStatPanel(attendanceLabel));
    }

    public void refresh() {
        updateLabel(studentsLabel, "Total Students", dashboardDao.countStudents());
        updateLabel(subjectsLabel, "Total Subjects", dashboardDao.countSubjects());
        updateLabel(enrolledLabel, "Enrolled", dashboardDao.countEnrolled());
        updatePassedLabel(dashboardDao.countPassed());
        updateFailedLabel(dashboardDao.countFailed());
        updateAttendanceLabel();
    }

    private JPanel createStatPanel(JLabel label) {
        JPanel panel = new JPanel(new GridLayout(1, 1));
        panel.setBackground(StyleConstants.WHITE);

        Border outline = BorderFactory.createLineBorder(StyleConstants.BORDER_COLOR, 1);
        Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        panel.setBorder(BorderFactory.createCompoundBorder(outline, padding));

        panel.add(label);
        return panel;
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
        int sectionsMarked = dashboardDao.countTodaySectionsMarked();
        int totalSections = dashboardDao.countTotalEnrolledSections();
        int present = dashboardDao.countTodayPresent();
        int total = dashboardDao.countTodayTotal();

        if (sectionsMarked < 0 || totalSections < 0 || present < 0 || total < 0) {
            attendanceLabel.setText("Today's Attendance: Error");
            return;
        }

        String display = String.format("Today: %d/%d sections (%d/%d present)",
            sectionsMarked, totalSections, present, total);
        attendanceLabel.setText(display);

        if (sectionsMarked >= totalSections && totalSections > 0) {
            attendanceLabel.setForeground(StyleConstants.SUCCESS);
        } else if (sectionsMarked > 0) {
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
