/*
 * Created on Feb 28, 2005
 *
 */
package fi.dwo.client.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Image;

import fi.dwo.client.domain.DwoHelper;

/**
 * This is the main-centerpanel. 
 * It contains the menu and the main panel (e.g. the overview of the courses).
 * @author M.J.B. Kupers
 *  
 */
public class CenterMainSubPanel extends BorderedPanel {

    /**
     * Creates a new CenterMainSubPanel with the specified LayoutManager. The
     * Panel has a gradient overlay on the left side (from white to blue).
     * 
     * @param lm The LayoutManager to set.
     */
	private Image guiImage;
	
    public CenterMainSubPanel(LayoutManager lm) {
        super(lm);
        this.setLayout(null);
        if(GuiConstants.GUI_IMAGE_BG)guiImage = DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_IMAGE_COURSE); 
    }

    /**
     * Creates a new CenterMainSubPanel with the specified LayoutManager. The
     * Panel has a gradient overlay on the left side (from white to blue).
     * 
     * @param lm The LayoutManager to set.
     * @param borders The borders to show.
     */
    public CenterMainSubPanel(LayoutManager lm, int borders) {
        super(lm, borders);
        this.setLayout(null);
        if(GuiConstants.GUI_IMAGE_BG)guiImage = DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_IMAGE_COURSE); 
    }

    public void setGuiImage(Image image)
    {
    	guiImage = image;
    }
    
    /**
     * Paints a gradient overlay from white to blue and paints the parent
     * component.
     * 
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
    /*public void paint(Graphics g) {
        super.paint(g);

        
        double facRed = (double)(255 - GuiConstants.MAIN_BACKGROUND.getRed())/10;
        double facGreen = (double)(255 - GuiConstants.MAIN_BACKGROUND.getGreen())/10;
        double facBlue = (double)(255 - GuiConstants.MAIN_BACKGROUND.getBlue())/10;
        for (int i = 0; i < 10; i++) {
            g.setColor(new Color((int) (255 - facRed * i), (int) (255 - facGreen * i), (int) (255 - facBlue * i)));
            g.fillRect(151, 1 + i * 20, 10, 20);
        }

       
        g.setColor(Color.black);
        g.drawLine(150, 0, 150, 200);
        g.drawLine(161, 0, 161, 445);
    }*/
    
    public void paint(Graphics g) {
    	if(GuiConstants.GUI_IMAGE_BG) {
	       	Point p = DwoHelper.getComponentLocation(this);
	       	g.drawImage(guiImage,-p.x,-p.y,null);
	       	super.paint(g);
    	} 
    	else {
    		super.paint(g);

            
            double facRed = (double)(255 - GuiConstants.MAIN_BACKGROUND.getRed())/10;
            double facGreen = (double)(255 - GuiConstants.MAIN_BACKGROUND.getGreen())/10;
            double facBlue = (double)(255 - GuiConstants.MAIN_BACKGROUND.getBlue())/10;
            for (int i = 0; i < 10; i++) {
                g.setColor(new Color((int) (255 - facRed * i), (int) (255 - facGreen * i), (int) (255 - facBlue * i)));
                g.fillRect(151, 1 + i * 20, 10, 20);
            }

           
            g.setColor(Color.black);
            g.drawLine(150, 0, 150, 200);
            g.drawLine(161, 0, 161, 445);
    	}
    	
    }
    
}