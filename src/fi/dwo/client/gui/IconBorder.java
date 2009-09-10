package fi.dwo.client.gui;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.border.Border;


public class IconBorder implements Border {

	IconBorder(Icon icon, int orientation) {
		super();
		this.icon = icon;
		this.orientation = orientation;
	}

	IconBorder(Icon icon) {
		this.icon = icon;
	}

	IconBorder() {
		
	}
	
	
	private Icon icon;
	public static final int TOP = 0;
	public static final int RIGHT = 1;
	
	private int  orientation;
	private int  padding = 6;
	static private Insets NULL = new Insets(1, 1, 1, 1);

	public Insets getBorderInsets(Component c) {
		if(icon == null)
			return NULL;
		switch(orientation) {
		default:
		case TOP:
			return new Insets(padding*2+getIconHeight(), 5, 5, 5);
		case RIGHT:
			return new Insets(1, 1, 1, padding*2+getIconWidth());
		}
	}

	public boolean isBorderOpaque() {
		return false;
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width,
			int height) {
		switch(orientation)
		{
			default:
			case TOP:
				x += (width - getIconWidth())/2;
				y += padding;
				break;
			case RIGHT:
				x += width - getIconWidth() - padding;
				y += (height - getIconHeight())/2;
				break;
				
		}
		if(icon != null)
			icon.paintIcon(c, g, x, y);
		g = g.create();
		g.setColor(new Color(230,230,230));
		g.drawRect(0, 0, width-1, height-1);
		g.dispose();
	}

	private int getIconHeight() {
		if(icon != null)
			return icon.getIconHeight();
		return 0;
	}

	private int getIconWidth() {
		if(icon != null)
			return icon.getIconWidth();
		return 0;
	}

	Icon getIcon() {
		return icon;
	}

	void setIcon(Icon icon) {
		this.icon = icon;
	}

	int getOrientation() {
		return orientation;
	}

	void setOrientation(int orientation) {
		this.orientation = orientation;
	}

}
