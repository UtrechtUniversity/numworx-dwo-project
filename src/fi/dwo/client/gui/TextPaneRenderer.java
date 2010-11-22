/**
 * 
 */
package fi.dwo.client.gui;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.table.TableCellRenderer;

public class TextPaneRenderer extends JTextPane implements TableCellRenderer {

	TextPaneRenderer() {
		super();
		setFont(GuiConstants.NORMAL_TEXT);
		setEditable(false);
	}

	public Component getTableCellRendererComponent(JTable table,
			Object value, boolean selected, boolean hasFocus, int row,
			int column) {
		if(selected)
			setBackground(table.getSelectionBackground());
		else
			setBackground(table.getBackground());
		setCell(value);
		return this;
	}

	protected void setCell(Object value) {
		setText(value.toString());
	}
	
}