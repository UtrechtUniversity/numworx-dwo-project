package fi.dwo.dwojapplet.gui;

import java.awt.Component;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import fi.dwo.dwojapplet.domain.School;
import fi.dwo.dwojapplet.domain.SchoolClass;
import fi.dwo.dwojapplet.domain.User;

class SchoolClassTableRenderer extends JComboBox implements TableCellRenderer {

    private final DefaultTableCellRenderer NULL = new DefaultTableCellRenderer();
    private Vector items;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        Component component = NULL.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (!table.getModel().isCellEditable(row, column)) {
            return component;
        }
        setForeground(NULL.getForeground());
        setBackground(NULL.getBackground());
        setBorder(NULL.getBorder());
        setFont(NULL.getFont());

        setSelectedItem(value);

        return this;
    }

    SchoolClassTableRenderer(Vector items) {
        super(items);
        this.items = items;

    }

    Vector getItems() {
        return items;
    }

    SchoolClassTableRenderer(School school) {
        this(toVector(school));
    }

    SchoolClassTableRenderer(User user) {
        this(toVector(user));
    }

    private static Vector toVector(School school) {
        SchoolClass[] classes = school.getClassList();
        return toVector(classes);
    }

    private static Vector toVector(User teacher) {
        return toVector(teacher.getSchool());
    }

    private static Vector toVector(SchoolClass[] classes) {
        Vector v = new Vector();
        v.add(null);
        for (int i = 0; i < classes.length; i++) {
            v.add(classes[i]);
        }
        return v;
    }

}
