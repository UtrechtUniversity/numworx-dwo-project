package fi.dwo.client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import sun.awt.VerticalBagLayout;

public class HeaderPanel extends JLabel
{
	
	private JLabel label;
	private boolean scalable;
	private boolean scale;
	private Font origFont;
	private static final int MARGIN = 40;
	public HeaderPanel(String string) {
		super(string.trim()); 
	    Font f = GuiConstants.HEADER_TEXT;
	    final boolean ibg = GuiConstants.GUI_IMAGE_BG;
		if(ibg)
	    	f = new Font(f.getName(), f.getStyle(), 26);
		setFont(f);
		origFont = f;
	    setOpaque(!ibg);
	    setHorizontalAlignment(ibg?SwingConstants.LEFT : SwingConstants.CENTER);
	    setVerticalAlignment(ibg?SwingConstants.BOTTOM: SwingConstants.CENTER);
	    setBackground(GuiConstants.MAIN_BACKGROUND);
	    if (!ibg)
	    	setBorder(BorderFactory.createLineBorder(Color.BLACK));
	    
	}

	private void scale() {
		scale = scalable;
	}

	public HeaderPanel(String description, boolean b) {
		this(description);
		this.scalable = b;
		scale();
	}

	/* (non-Javadoc)
	 * @see java.awt.Component#resize(java.awt.Dimension)
	 */
	public void resize(Dimension d) {
		// TODO Auto-generated method stub
		super.resize(d);
		scale();
	}

	/* (non-Javadoc)
	 * @see java.awt.Component#resize(int, int)
	 */
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		super.resize(width, height);
		scale();
	}

	/* (non-Javadoc)
	 * @see java.awt.Component#setBounds(int, int, int, int)
	 */
	public void setBounds(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		super.setBounds(x, y, width, height);
		scale();
	}

	/* (non-Javadoc)
	 * @see java.awt.Component#setBounds(java.awt.Rectangle)
	 */
	public void setBounds(Rectangle r) {
		super.setBounds(r);
		scale();
	}


	/* (non-Javadoc)
	 * @see java.awt.Component#setSize(java.awt.Dimension)
	 */
	public void setSize(Dimension d) {
		// TODO Auto-generated method stub
		super.setSize(d);
		scale();
	}

	/* (non-Javadoc)
	 * @see java.awt.Component#setSize(int, int)
	 */
	public void setSize(int width, int height) {
		// TODO Auto-generated method stub
		super.setSize(width, height);
		scale();
	}

	
	public void paint(Graphics g)
	{
		if(scale)
		{
			int width = getWidth();
			Insets inset = getInsets();
			width -= inset.left + inset.right;
			width -= MARGIN;
			Icon icon = getIcon();
			if(icon != null)
			{
				width -= getIconTextGap();
				width -= icon.getIconWidth();
			}
			String text = getText();
			Font f = origFont;
			while(g.getFontMetrics(f).stringWidth(text) > width)
			{
				f = new Font(f.getName(), f.getStyle(), f.getSize()-1);
			}
			setFont(f);
			scale = false;
		}
		super.paint(g);
	}
	
	
}
