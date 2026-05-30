package ui;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import util.StyleConstants;

public class GradeFilterPanel extends JPanel {

    private JComboBox<String> sectionBox;
    private JComboBox<String> statusBox;
    private JTextField searchField;

    public GradeFilterPanel() {
        setLayout(new GridLayout(1, 6, StyleConstants.GRID_H_GAP, StyleConstants.GRID_V_GAP));
        setBorder(StyleConstants.INPUT_BORDER);

        sectionBox = new JComboBox<>();
        statusBox = new JComboBox<>(new String[]{"All Results", "Passed Only", "Failed Only"});
        searchField = new JTextField();

        add(new JLabel("Course/Section:"));
        add(sectionBox);
        add(new JLabel("Status:"));
        add(statusBox);
        add(new JLabel("Search:"));
        add(searchField);
    }

    public String getSelectedSection() {
        return (String) sectionBox.getSelectedItem();
    }

    public boolean isAllSections() {
        return "All Sections".equals(getSelectedSection());
    }

    public String getSelectedStatus() {
        return (String) statusBox.getSelectedItem();
    }

    public boolean isAllResults() {
        return "All Results".equals(getSelectedStatus());
    }

    public boolean isPassedOnly() {
        return "Passed Only".equals(getSelectedStatus());
    }

    public String getSearchKeyword() {
        return searchField.getText().trim();
    }

    public void populateSections(List<String> sections) {
        sectionBox.removeAllItems();
        sectionBox.addItem("All Sections");
        for (String section : sections) {
            sectionBox.addItem(section);
        }
    }

    public void addSectionListener(ActionListener listener) {
        sectionBox.addActionListener(listener);
    }

    public void addStatusListener(ActionListener listener) {
        statusBox.addActionListener(listener);
    }

    public void addSearchListener(ActionListener listener) {
        searchField.addActionListener(listener);
    }
}
