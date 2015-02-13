package fi.dwo.dwojapplet.gui;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.BorderFactory;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.Document;

import fi.dwo.client.domain.LessonGroup;


public class MultiLineTableCellRenderer extends JTextArea implements TableCellRenderer {

	
	private final DefaultTableCellRenderer adaptee =
	      new DefaultTableCellRenderer();

	MultiLineTableCellRenderer() {
		super();
		initialize();
		this.setAlignmentY(SwingConstants.CENTER);
	}

	private void initialize() {
	    setLineWrap(true);
	    setWrapStyleWord(true);
	}

	MultiLineTableCellRenderer(int rows, int columns) {
		super(rows, columns);
		initialize();
	}

	public Dimension getPreferredSize() {
		Dimension preferredSize = super.getPreferredSize();
		Insets insets = getInsets();
		// 3 lines: 
		int height = insets.top + insets.bottom + getRows() * getRowHeight();
		preferredSize.height = Math.min(height, preferredSize.height);
		return preferredSize;
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		if(value instanceof LessonGroup)
		{
			value  = ((LessonGroup) value).getName();
		}
		
		adaptee.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		setForeground(adaptee.getForeground());
	    setBackground(adaptee.getBackground());
	    setBackground(new Color(230,230,230));//
	    //setBorder(adaptee.getBorder());
	    setFont(adaptee.getFont());
	    String text = adaptee.getText();
		setRows(text.indexOf('\n')>0 || text.length() > 15 ? 3: 1);
		setColumns(text.indexOf('\n')>0 ? 20 : text.length());
		Insets insets = getInsets();
	    setSize(getColumns()*getColumnWidth()+insets.left + insets.right,getRows()*getRowHeight()+insets.bottom+insets.top);
	    setText(text);
		return this;
	}


}
