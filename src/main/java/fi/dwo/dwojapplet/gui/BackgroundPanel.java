package fi.dwo.dwojapplet.gui;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;

import javax.swing.JPanel;

public class BackgroundPanel extends JPanel {

	protected Image guiImage;

	public BackgroundPanel() {
	}

	public BackgroundPanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
	}

	public BackgroundPanel(LayoutManager layout) {
		super(layout);
	}

	public BackgroundPanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
	}

	public void paintComponent(Graphics g) {
	    	validate();
	    	if(GuiConstants.GUI_IMAGE_BG) {
		       	//Point p = DwoHelper.getComponentLocation(this);
		       	g.drawImage(guiImage,0,0,this);
		       	int w = getSize().width;
		       	int h = getSize().height;
		       	int dw = w-800;
		       	int dh = h-600;
		       	int rand = 80;
		       	int strook = 100;
		       	final int H1 = 130;
		       	final int H2 = 141;
		       	//                   dx1             dy1             dx2             dy2
	/*	      */
		       	if(h>H2)
		       	g.drawImage(guiImage,0              ,600-rand-strook,800-rand-strook,h-rand         ,0,600-rand-strook,800-rand-strook,600-rand,null);
		       	g.drawImage(guiImage,800-rand-strook,0              ,w-rand         ,600-rand-strook,800-rand-strook,0,800-rand,600-rand-strook,null);
	// bij inklappen, als h<=... weglaten...
		       	if(h>H1)
		       	g.drawImage(guiImage,0              ,h-rand         ,800-rand-strook,h              ,0,600-rand,800-rand-strook,600,null);
		       	g.drawImage(guiImage,w-rand         ,0              ,w              ,600-rand-strook,800-rand,0,800,600-rand-strook,null);
		       	if(h>H2)
		       	g.drawImage(guiImage,800-rand-strook,600-rand-strook,w-rand         ,h-rand         ,800-rand-strook,600-rand-strook,800-rand,600-rand,null);
		       	if(h>H1)
		       	g.drawImage(guiImage,800-rand-strook,h-rand         ,w-rand         ,h              ,800-rand-strook,600-rand,800-rand,600,null);
		       	if(h>H2)
		       	g.drawImage(guiImage,w-rand         ,600-rand-strook,w              ,h-rand         ,800-rand,600-rand-strook,800,600-rand,null);
		       	if(h>H1)
		       	g.drawImage(guiImage,w-rand         ,h-rand         ,w              ,h              ,800-rand,600-rand,800,600,null);
			    
	    	} else
	    		super.paintComponent(g);
	    }

	public void setGuiImage(Image image) {
		guiImage = image;
	}

}
