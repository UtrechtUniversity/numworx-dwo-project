package fi.dwo.dwojapplet.gui;

import java.awt.Component;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class ImageRenderer extends JLabel implements TableCellRenderer {

	private ImageIcon icon = new ImageIcon();
	
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		if(value == null) 
			setIcon(null);
		else
		{
			icon.setImage((Image) value);
			setIcon(icon);
			setHorizontalAlignment(CENTER);
		}
		setOpaque(true);
		if(isSelected)
		{
			setBackground(table.getSelectionBackground());
		} else {
			setBackground(table.getBackground());
		}
		return this;
	}

}
