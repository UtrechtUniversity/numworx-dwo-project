/*
 * Created on Feb 25, 2005
 *
 */
package fi.dwo.client.gui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;

import fi.beans.tooltip.ToolTipIF;
import fi.beans.tooltip.ToolTipManager;

/**
 * A simple panel with an image on it. The panel has the size of the image.
 * 
 * @author M.J.B. Kupers
 *  
 */
public class ImagePanel extends Panel implements ToolTipIF {
    private Image image;
    private String toolTip;

    /**
     * Creates a new instance of ImagePanel. The panel has the same size as the
     * image.
     * 
     * @param i The images contained by the panel.
     */
    public ImagePanel(Image i) {
        image = i;
        setSize(image.getWidth(this), image.getHeight(this));
    }

    /**
     * Paints the image on the panel and calls the super.paint(g).
     * 
     * @param g The graphics context to use for painting.
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
    public void paint(Graphics g) {
        super.paint(g);
        g.drawImage(image, 0, 0, this);
    }

    /**
     * Sets the tooltip of this component.
     * @param toolTip The tooltip to set.
     * @see fi.beans.tooltip.ToolTipIF#setToolTip(java.lang.String)
     */
    public void setToolTip(String toolTip) {
        this.toolTip = toolTip;
        ToolTipManager.registerComponent(this);
    }

    /**
     * Returns the tooltip of this component.
     * @return The tooltip of this component. 
     * @see fi.beans.tooltip.ToolTipIF#getToolTip()
     */
    public String getToolTip() {
        return toolTip;
    }

    /**
     * Returns this component.
     * @return This component.
     * @see fi.beans.tooltip.ToolTipIF#getComponent()
     */
    public Component getComponent() {
        return this;
    }
}