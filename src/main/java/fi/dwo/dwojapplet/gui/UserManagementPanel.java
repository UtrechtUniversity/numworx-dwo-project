package fi.dwo.dwojapplet.gui;

import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.commons.exceptions.LoginException;
import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.exceptions.RegisterException;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.SchoolAdmin;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.DwoIF;
import fi.dwo.dwojapplet.domain.School;
import fi.dwo.dwojapplet.domain.SchoolClass;
import fi.dwo.dwojapplet.domain.SchoolGroup;
import fi.dwo.dwojapplet.domain.Teacher;
import fi.dwo.dwojapplet.domain.User;
import fi.dwo.dwojapplet.persistence.PersistenceFacade;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractCellEditor;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

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
