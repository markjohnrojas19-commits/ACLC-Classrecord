package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import dao.DashboardDao;
import model.SubjectStats;
import util.StyleConstants;
import util.TooltipTable;

public class DashboardStatsPanel extends JPanel {

    private DashboardDao dashboardDao;
    private JLabel studentsValue;
    private JLabel subjectsValue;
    private JLabel attendanceValue;
    private DefaultTableModel subjectStatsModel;

    public DashboardStatsPanel() {
        this.dashboardDao = new DashboardDao();

        setLayout(new BorderLayout(0, 15));
        setBackground(StyleConstants.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        studentsValue = new JLabel("0");
        subjectsValue = new JLabel("0");
        attendanceValue = new JLabel("0 / 0");

        add(createStatCardsPanel(), BorderLayout.NORTH);
        add(createSubjectStatsPanel(), BorderLayout.CENTER);

        attendanceValue.setFont(StyleConstants.BODY_FONT);
    }

    private JPanel createStatCardsPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(StyleConstants.WHITE);

        TitledBorder titledBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(StyleConstants.BORDER_COLOR, 1),
            "Dashboard Overview", TitledBorder.LEFT, TitledBorder.TOP,
            StyleConstants.SMALL_BOLD_FONT, StyleConstants.TEXT_SECONDARY);

        wrapper.setBorder(BorderFactory.createCompoundBorder(
            titledBorder, BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JPanel cards = new JPanel(new GridLayout(1, 3, 15, 15));
        cards.setBackground(StyleConstants.WHITE);

        cards.add(createStatCard("/icons/students.png", studentsValue, "Total Students"));
        cards.add(createStatCard("/icons/subjects.png", subjectsValue, "Total Subjects"));
        cards.add(createStatCard("/icons/attendance.png", attendanceValue, "Today's Attendance"));

        wrapper.add(cards, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel createSubjectStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(StyleConstants.WHITE);

        String[] columns = {"Subject", "Name", "Enrolled", "Passed", "Failed"};
        subjectStatsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new TooltipTable(subjectStatsModel);
        table.setRowHeight(StyleConstants.TABLE_ROW_HEIGHT);
        table.setFont(StyleConstants.BODY_FONT);
        table.setGridColor(StyleConstants.BORDER_COLOR);
        table.setDefaultRenderer(Object.class, createSubjectStatsRenderer());
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        table.getColumnModel().getColumn(0).setPreferredWidth(120);
        table.getColumnModel().getColumn(0).setMaxWidth(150);
        table.getColumnModel().getColumn(1).setPreferredWidth(250);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(2).setMaxWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setMaxWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setMaxWidth(100);
        styleTableHeader(table);

        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(StyleConstants.BORDER_COLOR, 1),
            "Per-Subject Statistics", TitledBorder.LEFT, TitledBorder.TOP,
            StyleConstants.SMALL_BOLD_FONT, StyleConstants.TEXT_SECONDARY);

        panel.setBorder(BorderFactory.createCompoundBorder(
            border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        panel.add(tableScroll, BorderLayout.CENTER);

        return panel;
    }

    public void refresh() {
        updateCount(studentsValue, dashboardDao.countStudents());
        updateCount(subjectsValue, dashboardDao.countSubjects());
        updateAttendanceLabel();
        refreshSubjectStats();
    }

    private void refreshSubjectStats() {
        subjectStatsModel.setRowCount(0);
        List<SubjectStats> statsList = dashboardDao.getPerSubjectStats();

        for (SubjectStats stats : statsList) {
            subjectStatsModel.addRow(new Object[]{
                stats.getSubjectCode(),
                stats.getSubjectName(),
                stats.getEnrolled(),
                stats.getPassed(),
                stats.getFailed()
            });
        }
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

    private DefaultTableCellRenderer createSubjectStatsRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(
                    t, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    cell.setBackground(row % 2 == 0
                        ? StyleConstants.WHITE : StyleConstants.TABLE_ROW_ALT);
                    cell.setForeground(StyleConstants.TEXT_PRIMARY);

                    if (column == 3) {
                        cell.setForeground(StyleConstants.SUCCESS);
                    } else if (column == 4) {
                        int failed = (Integer) t.getModel().getValueAt(row, 4);
                        if (failed > 0) {
                            cell.setForeground(StyleConstants.DANGER);
                        }
                    }
                }
                return cell;
            }
        };
    }

    private void styleTableHeader(JTable table) {
        JTableHeader header = table.getTableHeader();
        header.setBackground(StyleConstants.TABLE_HEADER_BG);
        header.setForeground(StyleConstants.TABLE_HEADER_FG);
        header.setFont(StyleConstants.TABLE_HEADER_FONT);
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
