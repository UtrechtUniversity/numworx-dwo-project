// Source file: C:\\fi\\dwo\\parameters\\gui\\HelpButton.java

package fi.dwo.parameters.gui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.Vector;

import fi.beans.tooltip.ToolTipIF;
import fi.beans.tooltip.ToolTipManager;
import fi.dwo.parameters.system.TextMapper;

public class DeleteButton extends Panel implements ToolTipIF, MouseListener {

    private String toolTip;
    
    private Image mouseOutImage;
    private Image mouseOverImage;
    private boolean mouseOver = false;

    private Vector actionListeners = new Vector();

    /**
     * @roseuid 425E240E00FA
     */
    public DeleteButton() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        URL url = this.getClass().getResource(ParameterConstants.DELETE_IMAGE1);
        mouseOutImage = toolkit.getImage(url);
        MediaTracker tr = new MediaTracker(this);
        tr.addImage(mouseOutImage, 0);
        try {
            tr.waitForAll();
        } catch (Exception e) {
        }
        
        url = this.getClass().getResource(ParameterConstants.DELETE_IMAGE2);
        mouseOverImage = toolkit.getImage(url);
        tr = new MediaTracker(this);
        tr.addImage(mouseOverImage, 0);
        try {
            tr.waitForAll();
        } catch (Exception e) {
        }
        
        setSize(mouseOutImage.getWidth(this), mouseOutImage.getHeight(this));
        this.setToolTip(TextMapper.getText(TextMapper.TLTP_DELETE_ITEM));
        
        this.addMouseListener(this);
    }
    
    /**
     * Adds the specified action listener to receive action events from this
     * button. Action events occur when a user presses or releases the mouse
     * over this button. If l is null, no exception is thrown and no action is
     * performed.
     * 
     * @param l the action listener.
     * @see fi.dwo.client.gui.CourseIconIF#addActionListener(java.awt.event.ActionListener)
     */
    public void addActionListener(ActionListener l) {
        if (l != null) {
            actionListeners.addElement(l);
        }
    }

    /**
     * Paints the image on the panel and calls the super.paint(g).
     * 
     * @param g The graphics context to use for painting.
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
    public void paint(Graphics g) {
        super.paint(g);
        if(mouseOver) {
            g.drawImage(mouseOverImage, 0, 0, this);
        } else {
            g.drawImage(mouseOutImage, 0, 0, this);            
        }
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

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent e) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent e) {
        mouseOver = true;
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        repaint();
        
    }

    /**
     * Invoked when the mouse exits the HelpButton. The Default Cursor is
     * showed.
     * 
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent e) {
        mouseOver = false;
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        repaint();
        
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent e) {
        mouseOver = false;
        repaint();
        
    }

    /**
     * Invoked when a mouse button has been released on a component.
     * 
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent e) {
        mouseOver = false;
        for (int i = 0; i < actionListeners.size(); i++) {
            ((ActionListener) actionListeners.elementAt(i)).actionPerformed(new ActionEvent(this, 0, ""));
        }
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        repaint();
        
    }

}