/**
 * Copyrighted Mar 7, 2016
 */
package fi.dwo.dwojapplet.gui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author G.A.J. van der Plas
 */
public class NewSingleSchoolStudentsTeacherTableColorRenderer extends JLabel implements TableCellRenderer  {
 
    public NewSingleSchoolStudentsTeacherTableColorRenderer() {
        setOpaque(true); //MUST do this for background to show up.
    }
 
    public Component getTableCellRendererComponent(
                            JTable table, Object value,
                            boolean isSelected, boolean hasFocus,
                            int row, int column) {
 
                if(column==0){
                    setBorder(new MatteBorder(1, 1, 1, 0, Color.LIGHT_GRAY) );
                }else if(column==6){
                    setBorder(new MatteBorder(1, 0, 1, 1, Color.LIGHT_GRAY) );
                }else{
                    setBorder(new MatteBorder(1, 0, 1, 0, Color.LIGHT_GRAY) );
                }
                //setBackground(Color.LIGHT_GRAY);
                //setForeground(Color.BLACK);
        //}
        return this;
    }
}    
