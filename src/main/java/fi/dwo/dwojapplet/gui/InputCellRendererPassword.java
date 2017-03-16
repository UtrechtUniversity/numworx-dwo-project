package fi.dwo.dwojapplet.gui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import nl.uu.fi.dwo.rest.dom.entities.ValidUserFieldsChecker;

/**
 *
 * @author G.A.J. van der Plas
 */
class InputCellRendererPassword extends DefaultTableCellRenderer {
    
    private static final long serialVersionUID = 1L;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel component = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (value != null && ValidUserFieldsChecker.isValidPassword(value.toString())) {
            component.setBackground(Color.WHITE);
            component.setToolTipText("test");
        } else {
            component.setBackground(new Color(255, 128, 128));
        }
        return component;
    }
    
}
