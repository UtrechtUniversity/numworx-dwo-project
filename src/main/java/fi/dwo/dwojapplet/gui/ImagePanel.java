/*
 * Created on Feb 25, 2005
 *
 */
package fi.dwo.dwojapplet.gui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import fi.beans.tooltip.ToolTipIF;
import fi.beans.tooltip.ToolTipManager;

/**
 * A simple panel with an image on it. The panel has the size of the image.
 * 
 * @author M.J.B. Kupers
 *  
 */
public class ImagePanel extends JLabel {
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
        setIcon(new ImageIcon(i));
        setSize(image.getWidth(this), image.getHeight(this));
    }

    /**
     * Sets the tooltip of this component.
     * @param toolTip The tooltip to set.
     * @see fi.beans.tooltip.ToolTipIF#setToolTip(java.lang.String)
     */
    public void setToolTip(String toolTip) {
        this.toolTip = toolTip;
        setToolTipText(toolTip);
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