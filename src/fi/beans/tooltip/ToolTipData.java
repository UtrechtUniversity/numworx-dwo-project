/*
 * Created on Mar 17, 2005
 *
 */
package fi.beans.tooltip;

/**
 * Wrapper class for the data to of a tooltip.
 * @author M.J.B. Kupers
 *
 */
public class ToolTipData {

    /**
     * The horizontal location of the tooltip.
     */
    public int toolTipX;
    
    /**
     * The vertical location of the tooltip.
     */
    public int toolTipY;
    
    /**
     * The ToolTipIF of the ToolTip to cummunicate with.
     */
    public ToolTipIF toolTipIF;
    
    /**
     * The graphical ToolTip.
     */
    public ToolTipCanvas toolTipCanvas;

}
