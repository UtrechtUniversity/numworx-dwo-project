/**
 * Copyrighted Feb 2, 2016
 */
package fi.dwo.dwojapplet.gui.domutils;

import fi.dwo.rest.dom.entities.DomUser;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import static javax.swing.SwingConstants.CENTER;

/**
 * List cell renderer to render DomUser
 *
 * @author G.A.J. van der Plas
 */
public class DomUserListCellRenderer extends JLabel
        implements ListCellRenderer {

    public DomUserListCellRenderer() {
        setOpaque(true);
        setHorizontalAlignment(LEFT);
        setVerticalAlignment(CENTER);
    }

    /*
         * This method finds the image and text corresponding
         * to the selected value and returns the label, set up
         * to display the text and image.
     */
    @Override
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        if (value instanceof DomUser) {
            DomUser u = (DomUser) value;
            setText(u.getUniqueDisplayName());
            setFont(list.getFont());
        } else if(value == null){
            setText("");
        }else {
            setText("Object of unsupported type");
        }

        return this;
    }

}
