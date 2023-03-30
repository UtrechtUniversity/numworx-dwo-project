package fi.dwo.dwojapplet.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import nl.uu.fi.dwo.rest.dom.entities.ValidUserFieldsChecker;

/**
 *
 * @author G.A.J. van der Plas
 */
class InputCellRendererEmail extends DefaultTableCellRenderer {
    
    private static final long serialVersionUID = 1L;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JComponent component = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        int rows = table.getRowCount();
        if (row == rows-1 ||
            value != null && ValidUserFieldsChecker.isValidEmail(value.toString())) {
            component.setBackground(Color.WHITE);
            component.setToolTipText(null);
        } else {
            component.setBackground(new Color(255, 128, 128));
            component.setToolTipText("Invalid email");
        }
        return component;
    }
    
}
