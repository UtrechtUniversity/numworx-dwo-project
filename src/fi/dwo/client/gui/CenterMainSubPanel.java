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

import javax.swing.JPanel;

import fi.dwo.client.domain.DwoHelper;

/**
 * This is the main-centerpanel. 
 * It contains the menu and the main panel (e.g. the overview of the courses).
 * @author M.J.B. Kupers
 *  
 */
public class CenterMainSubPanel extends JPanel {
	
    public CenterMainSubPanel(LayoutManager lm) {
        super(lm);
        setDoubleBuffered(false);
        setOpaque(!GuiConstants.GUI_IMAGE_BG);
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
    
    public void paintComponent(Graphics g) {
		super.paintComponent(g);
    	if(!GuiConstants.GUI_IMAGE_BG) {
            double facRed = (double)(255 - GuiConstants.MAIN_BACKGROUND.getRed())/10;
            double facGreen = (double)(255 - GuiConstants.MAIN_BACKGROUND.getGreen())/10;
            double facBlue = (double)(255 - GuiConstants.MAIN_BACKGROUND.getBlue())/10;
            for (int i = 0; i < 10; i++) {
                g.setColor(new Color((int) (255 - facRed * i), (int) (255 - facGreen * i), (int) (255 - facBlue * i)));
                g.fillRect(151-1, 1 + i * 20, 10, 20);
            }
            g.setColor(Color.black);
            g.drawLine(150-1, 0, 150-1, 200);
            g.drawLine(161-1, 0, 161-1, 445);
    	}
    }
    
}