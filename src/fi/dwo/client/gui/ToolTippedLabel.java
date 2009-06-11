/*
 * Created on Mar 17, 2005
 *
 */
package fi.dwo.client.gui;

import java.awt.Component;
import java.awt.Label;

import javax.swing.JLabel;

import fi.beans.tooltip.ToolTipIF;
import fi.beans.tooltip.ToolTipManager;

/**
 * A AWT Label with a ToolTip.
 * @author M.J.B. Kupers
 *
 */
public class ToolTippedLabel extends JLabel implements ToolTipIF {
    
    private String toolTip;

    /**
     */
    public ToolTippedLabel() {
        super();
    }

    /**
     * @param text
     */
    public ToolTippedLabel(String text) {
        super(text);
    }

    /**
     * @param text
     * @param alignment
     */
    public ToolTippedLabel(String text, int alignment) {
        super(text, alignment);
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
