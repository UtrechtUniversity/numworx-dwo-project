/*
 * Created on Mar 17, 2005
 *
 */
package fi.beans.tooltip;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Manages all the ToolTips in the system. 
 * An instance must be created once, by the parent container of the system.<br>
 * For an example of use, see the fi.beans.tooltip package description.
 * @see fi.beans.tooltip tooltip package for example use
 * 
 * @author M.J.B. Kupers
 *
 */
public class ToolTipManager implements MouseListener, MouseMotionListener {
    
    private static ToolTipManager _instance;
    private Container window;
    private ToolTipData toolTipData;
    private ToolTipThread toolTipThread;
    
    private Color toolTipBackground;
    private Color toolTipForeground;
    private Font toolTipFont;
    
    

    /**
     * Creates a new instance of a ToolTipManager.
     * @param window The parent window where all the components with tooltips will be part of.
     */
    public ToolTipManager(Container window) {
        if(_instance == null ) {
	        this.window = window;
	        toolTipBackground = null;
	        toolTipForeground = null;
	        toolTipFont = null;
	        _instance = this;
        }
    }
    
    /**
     * Returns the created instance of the ToolTipManager.
     * The instance must be created with the ToolTipManager Constructor.
     * @return The created instance of the ToolTipManager.
     */
    protected static ToolTipManager instance() {
        return _instance;
    }
    
    /**
     * Shows the tooltip.
     * The tooltip is showed with the options specified in toolTipData.
     *
     */
    protected void showToolTip() {
        if(toolTipData != null) {
            String tip = toolTipData.toolTipIF.getToolTip();
            
            if((tip != null) && (!tip.equals(""))) {
		        toolTipData.toolTipCanvas = new ToolTipCanvas(tip);

		        if(toolTipBackground != null) {
		            toolTipData.toolTipCanvas.setBackground(toolTipBackground);
		        }
		        if(toolTipForeground != null) {
		            toolTipData.toolTipCanvas.setForeground(toolTipForeground);
		        }
		        if(toolTipFont!= null) {
		            toolTipData.toolTipCanvas.setFont(toolTipFont);
		        }

	            checkToolTipOffScreen();
		        toolTipData.toolTipCanvas.setLocation(toolTipData.toolTipX, toolTipData.toolTipY);
		        toolTipData.toolTipCanvas.setVisible(false);
		        
		        window.add(toolTipData.toolTipCanvas, 0);
		        
		        toolTipData.toolTipCanvas.repaint();
		        toolTipData.toolTipCanvas.setVisible(true);
            }
        }
    }
    
    /**
     * Hides the tooltip.
     * The ToolTipData is set to null also.
     *
     */
    protected void hideToolTip() {
        if((toolTipData != null) && (toolTipData.toolTipCanvas != null)) {
	       	window.remove(toolTipData.toolTipCanvas);
		        
        }
        toolTipData = null;
    }
    
    /**
     * Removes the tooltip.
     *
     */
    private void prepareToolTip() {
        if((toolTipData != null) && (toolTipData.toolTipCanvas != null)) {
            window.remove(toolTipData.toolTipCanvas);
        }
        
        toolTipData = null;
    }
    
    /**
     * Register a component as a tooltip.
     * An instance of ToolTipManager must be created by the system, before calling this method.
     * @param component The component to add a tooltip.
     */
    public static void registerComponent(ToolTipIF component) {
        Component c = component.getComponent();
        c.removeMouseListener(instance());
        c.addMouseListener(instance());
        c.removeMouseMotionListener(instance());
        c.addMouseMotionListener(instance());
    }

    /**
     * Invoked when the mouse has been clicked on a registered tooltipcomponent.
     * The tooltip is removed and the toolTipThread is stopped.
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent e) {
        if(toolTipThread != null) {
            toolTipThread.stop();
        }
        hideToolTip();
        
        toolTipThread = null;
    }
    
    /**
     * Reads the tooltipdata and checks if the tooltip 'runs' out of the screen.
     * If so, the x or y position of the tooltipdata are changed.
     *
     */
    private void checkToolTipOffScreen() {
        if((toolTipData != null) && (toolTipData.toolTipCanvas != null)  && window.isShowing()) {
            /* Tooltip runs out of the window */
            Point windowLocation = window.getLocationOnScreen();
            if(toolTipData.toolTipY + toolTipData.toolTipCanvas.getSize().height + 5 > windowLocation.y + window.getSize().height) {
                Component c = toolTipData.toolTipIF.getComponent();
                toolTipData.toolTipY -= 40;
            }            
            if(toolTipData.toolTipX + toolTipData.toolTipCanvas.getSize().width + 5 > windowLocation.x + window.getSize().width) {
                Component c = toolTipData.toolTipIF.getComponent();
                toolTipData.toolTipX = window.getSize().width - toolTipData.toolTipCanvas.getSize().width - 5;
            }            
        }
    }
    
    /**
     * Sets the location of the tooltip.
     * @param e The mousedata where the tooltip must appear.
     */
    private void setToolTipLocation(MouseEvent e) {
        if((toolTipData != null) && window.isShowing()) {
	        if(toolTipData.toolTipCanvas == null) {
	            Point windowLocation = window.getLocationOnScreen();
		        toolTipData.toolTipX = e.getX() + toolTipData.toolTipIF.getComponent().getLocationOnScreen().x - windowLocation.x;
		        toolTipData.toolTipY = e.getY() + toolTipData.toolTipIF.getComponent().getLocationOnScreen().y - windowLocation.y + 25;
	        }
	        if(toolTipData.toolTipCanvas != null) {
	            checkToolTipOffScreen();
	            toolTipData.toolTipCanvas.setLocation(toolTipData.toolTipX, toolTipData.toolTipY);
	        }
        }        
    }

    /**
     * Invoked when the mouse enters a registered tooltipcomponent.
     * The tooltipdata will be set and a thread is started to show and hide the tooltip.
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent e) {
        if(e.getSource() instanceof ToolTipIF) {
	        prepareToolTip();
	        toolTipData = new ToolTipData();
	        toolTipData.toolTipIF = (ToolTipIF) e.getSource();
	        setToolTipLocation(e);
	        toolTipThread = new ToolTipThread(this);
        }
        
        
    }

    /**
     * Invoked when the mouse exits a registered tooltipcomponent.
     * The tooltipThread is stopped, and the tooltip is hidden.  
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent e) {
        if(toolTipThread != null) {
            toolTipThread.stop();
        }
        hideToolTip();
    }

    /**
     * Invoked when a mouse button has been pressed on a registered tooltipcomponent.
     * The tooltipThread is stopped, and the tooltip is hidden.  
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent e) {
        if(toolTipThread != null) {
            toolTipThread.stop();
        }
        hideToolTip();
        toolTipThread = null;
    }

    /**
     * Invoked when a mouse button has been released on a registered tooltipcomponent.
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent e) {
    }

    /**
     * Invoked when a mouse button is pressed on a registered tooltipcomponent and then dragged.
     * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
     */
    public void mouseDragged(MouseEvent e) {
    }

    /**
     * Invoked when the mouse button has been moved on a registered tooltipcomponent.
     * The tooltips moved with the mouse.
     * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
     */
    public void mouseMoved(MouseEvent e) {
        setToolTipLocation(e);
    }

    /**
     * Returns the ToolTipData of the current ToolTip.
     * @return The ToolTipData of the current ToolTip.
     */
    protected ToolTipData getToolTipData() {
        return toolTipData;
    }

    /**
     * Sets the ToolTipData of the current ToolTip.
     * @param toolTipData The ToolTipData to set.
     */
    protected void setToolTipData(ToolTipData toolTipData) {
        this.toolTipData = toolTipData;
    }
    
    /**
     * Returns the current background of the tooltips.
     * If no background is set, null is returned.
     * @return The current background of the tooltips.
     */
    public Color getToolTipBackground() {
        return toolTipBackground;
    }
    
    /**
     * Sets the current background of the tooltips to show.
     * If null, the default background is used. 
     * @param toolTipBackground The current background to set.
     */
    public void setToolTipBackground(Color toolTipBackground) {
        this.toolTipBackground = toolTipBackground;
    }

    /**
     * Returns the current font of the tooltips.
     * If no font is set, null is returned.
     * @return The current font of the tooltips.
     */
    public Font getToolTipFont() {
        return toolTipFont;
    }

    /**
     * Sets the current font of the tooltips to show.
     * If null, the default font is used. 
     * @param toolTipFont The current font to set.
     */
    public void setToolTipFont(Font toolTipFont) {
        this.toolTipFont = toolTipFont;
    }

    /**
     * Returns the current foreground of the tooltips.
     * If no foreground is set, null is returned.
     * @return The current foreground of the tooltips.
     */
    public Color getToolTipForeground() {
        return toolTipForeground;
    }

    /**
     * Sets the current foreground of the tooltips to show.
     * If null, the default foreground is used. 
     * @param toolTipForeground The current foreground to set.
     */
    public void setToolTipForeground(Color toolTipForeground) {
        this.toolTipForeground = toolTipForeground;
    }
}
