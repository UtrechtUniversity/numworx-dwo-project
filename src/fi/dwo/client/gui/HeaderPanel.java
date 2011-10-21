package fi.dwo.client.gui;

import java.awt.BorderLayout;
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
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


public class HeaderPanel extends JPanel
{
	
	private JLabel label;
	private JComponent buttonBox;
	private boolean scalable;
	private boolean scale;
	private Font origFont;
	private static final int MARGIN = 40;
	public HeaderPanel(String string) {
		super(new BorderLayout());
		label = new JLabel(string.trim());
		add(label, BorderLayout.CENTER);
		
	    Font f = GuiConstants.HEADER_TEXT;
	    final boolean ibg = GuiConstants.GUI_IMAGE_BG;
	    if(ibg)
	    	f = new Font(f.getName(), f.getStyle(), 20);
		label.setFont(f);
		origFont = f;
	    setOpaque(!ibg);
	    label.setHorizontalAlignment(ibg?SwingConstants.LEFT : SwingConstants.CENTER);
	    label.setVerticalAlignment(ibg?SwingConstants.BOTTOM: SwingConstants.CENTER);
	    setBackground(GuiConstants.MAIN_BACKGROUND);
	    setForeground(GuiConstants.HEADER_COLOR);
	    if (!ibg)
	    	setBorder(MainPanel.createNBorder());
	    setButtonBox( createButtonBox() );
	}

	protected JComponent createButtonBox() {
		Box box = Box.createHorizontalBox();
//		box.add(new JButton("Stop bewerken"));
//		box.add(Box.createHorizontalStrut(4));
//		box.add(new JButton("Opslaan"));
//		box.add(Box.createHorizontalStrut(5));
//		box.add(new JButton("Preview"));
		box.setBorder(BorderFactory.createEmptyBorder(38, 0, 0, 0));
		return box;
//		return null;
	}

	Dimension lastdim = new Dimension();
	private void scale() {
		if(scalable && !lastdim.equals(getSize()))
		{ 	scale = scalable;
			getSize(lastdim);
		}
	}

	public HeaderPanel(String description, boolean b) {
		this(description);
		this.scalable = b;
	    setButtonBox( createButtonBox() );
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
			int width = label.getWidth();
			int height = label.getHeight();
//			Insets inset = getInsets();
//			width -= inset.left + inset.right;
//			height -= inset.top + inset.bottom;
			width -= MARGIN;
			Icon icon = label.getIcon();
			if(icon != null)
			{
				width -= label.getIconTextGap();
				width -= icon.getIconWidth();
			}
			String text = label.getText();
			Font f = origFont;
			while(g.getFontMetrics(f).stringWidth(text) > width || g.getFontMetrics(f).getHeight() > height)
			{
				f = new Font(f.getName(), f.getStyle(), f.getSize()-1);
			}
			setFont(f);
			scale = false;
		}
		super.paint(g);
	}

	/**
	 * @return the buttonBox
	 */
	JComponent getButtonBox() {
		return buttonBox;
	}

	/**
	 * @param box the buttonBox to set
	 */
	void setButtonBox(JComponent box) {
		if(buttonBox != null)
			remove(buttonBox);
		buttonBox = box;
		if(buttonBox != null)
			add(buttonBox, BorderLayout.EAST);
		invalidate();
	}

	/**
	 * @param alignment
	 * @see javax.swing.JLabel#setHorizontalAlignment(int)
	 */
	public void setHorizontalAlignment(int alignment) {
		label.setHorizontalAlignment(alignment);
	}

	/**
	 * @param icon
	 * @see javax.swing.JLabel#setIcon(javax.swing.Icon)
	 */
	public void setIcon(Icon icon) {
		label.setIcon(icon);
	}

	/**
	 * @param iconTextGap
	 * @see javax.swing.JLabel#setIconTextGap(int)
	 */
	public void setIconTextGap(int iconTextGap) {
		label.setIconTextGap(iconTextGap);
	}
	
	
}
