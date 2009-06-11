/*
 * Created on Mar 17, 2005
 *
 */
package fi.beans.tooltip;

import java.awt.Component;

/**
 * This interface must be implemented by a component who wants to have a tooltip.
 * @author M.J.B. Kupers
 * @see fi.beans.tooltip tooltip package for example use
 *
 */
public interface ToolTipIF {
    
    
    /**
     * Sets the toolTip of the component.
     * @param toolTip The toolTip to set.
     */
    public void setToolTip(String toolTip);
    
    /**
     * Returns the current toolTip of the component.
     * @return The current toolTip of the component.
     */
    public String getToolTip();
    
    /**
     * Returns the component implementing the ToolTipIF.
     * @return The component implementing the ToolTipIF.
     */
    public Component getComponent();
    

}
