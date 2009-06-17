package fi.dwo.client.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import sun.awt.VerticalBagLayout;

public class HeaderPanel extends JLabel
{
	
	private JLabel label;

	public HeaderPanel(String string) {
		super(string); 
	    Font f = GuiConstants.HEADER_TEXT;
	    final boolean ibg = GuiConstants.GUI_IMAGE_BG;
		if(ibg)
	    	f = new Font(f.getName(), f.getStyle(), 26);
		setFont(f);
	    setOpaque(!ibg);
	    setHorizontalAlignment(/*ibg?SwingConstants.LEFT :*/ SwingConstants.CENTER);
	    setVerticalAlignment(ibg?SwingConstants.BOTTOM: SwingConstants.CENTER);
	    setBackground(GuiConstants.MAIN_BACKGROUND);
	    if (!ibg)
	    	setBorder(BorderFactory.createLineBorder(Color.BLACK));
	    
	}

}
