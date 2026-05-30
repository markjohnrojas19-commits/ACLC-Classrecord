package ui;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import dao.DashboardDao;
import util.StyleConstants;

public class DashboardStatsPanel extends JPanel {

    private DashboardDao dashboardDao;
    private JLabel studentsValue;
    private JLabel subjectsValue;
    private JLabel attendanceValue;

    public DashboardStatsPanel() {
        this.dashboardDao = new DashboardDao();

        setLayout(new GridLayout(1, 3, 15, 15));
        setBackground(StyleConstants.WHITE);

        TitledBorder titledBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(StyleConstants.BORDER_COLOR, 1),
            "Dashboard Overview", TitledBorder.LEFT, TitledBorder.TOP,
            StyleConstants.SMALL_BOLD_FONT, StyleConstants.TEXT_SECONDARY);

        setBorder(BorderFactory.createCompoundBorder(
            StyleConstants.STATS_BORDER,
            BorderFactory.createCompoundBorder(
                titledBorder,
                BorderFactory.createEmptyBorder(10, 10, 10, 10))));

        studentsValue = new JLabel("0");
        subjectsValue = new JLabel("0");
        attendanceValue = new JLabel("0 / 0");

        add(createStatCard("/icons/students.png", studentsValue, "Total Students"));
        add(createStatCard("/icons/subjects.png", subjectsValue, "Total Subjects"));
        add(createStatCard("/icons/attendance.png", attendanceValue, "Today's Attendance"));

        attendanceValue.setFont(StyleConstants.BODY_FONT);
    }

    public void refresh() {
        updateCount(studentsValue, dashboardDao.countStudents());
        updateCount(subjectsValue, dashboardDao.countSubjects());
        updateAttendanceLabel();
    }

    private JPanel createStatCard(String iconPath, JLabel valueLabel, String title) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(StyleConstants.WHITE);

        Border outline = BorderFactory.createLineBorder(StyleConstants.BORDER_COLOR, 1);
        Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        card.setBorder(BorderFactory.createCompoundBorder(outline, padding));

        JLabel iconLabel = createIconLabel(iconPath);
        styleValueLabel(valueLabel);
        JLabel titleLabel = createTitleLabel(title);

        card.add(Box.createVerticalGlue());
        card.add(iconLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(2));
        card.add(titleLabel);
        card.add(Box.createVerticalGlue());

        return card;
    }

    private JLabel createIconLabel(String iconPath) {
        ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
        JLabel label = new JLabel(icon);
        label.setAlignmentX(CENTER_ALIGNMENT);
        return label;
    }

    private void styleValueLabel(JLabel label) {
        label.setFont(StyleConstants.TITLE_FONT);
        label.setForeground(StyleConstants.TEXT_PRIMARY);
        label.setAlignmentX(CENTER_ALIGNMENT);
        label.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private JLabel createTitleLabel(String title) {
        JLabel label = new JLabel(title);
        label.setFont(StyleConstants.SMALL_BOLD_FONT);
        label.setForeground(StyleConstants.TEXT_SECONDARY);
        label.setAlignmentX(CENTER_ALIGNMENT);
        return label;
    }

    private void updateCount(JLabel label, int count) {
        label.setText((count < 0) ? "Error" : String.valueOf(count));
    }

    private void updateAttendanceLabel() {
        int sectionsMarked = dashboardDao.countTodaySectionsMarked();
        int totalSections = dashboardDao.countTotalEnrolledSections();
        int present = dashboardDao.countTodayPresent();
        int total = dashboardDao.countTodayTotal();

        if (sectionsMarked < 0 || totalSections < 0 || present < 0 || total < 0) {
            attendanceValue.setText("Error");
            return;
        }

        String display = String.format("<html><center>%d/%d sections<br>(%d/%d present)</center></html>",
            sectionsMarked, totalSections, present, total);
        attendanceValue.setText(display);

        if (sectionsMarked >= totalSections && totalSections > 0) {
            attendanceValue.setForeground(StyleConstants.SUCCESS);
        } else if (sectionsMarked > 0) {
            attendanceValue.setForeground(StyleConstants.PRIMARY);
        }
    }
}
