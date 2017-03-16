package fi.dwo.dwojapplet.gui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import nl.uu.fi.dwo.rest.dom.entities.ValidUserFieldsChecker;

/**
 *
 * @author G.A.J. van der Plas
 */
class InputCellRendererUsername extends DefaultTableCellRenderer {
    
    private static final long serialVersionUID = 1L;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (value != null && ValidUserFieldsChecker.isValidUserName(value.toString())) {
            component.setBackground(Color.WHITE);
        } else {
            component.setBackground(new Color(255, 128, 128));
        }
        return component;
    }
    
}
