/*
 * Created on Feb 28, 2005
 *
 */
package fi.dwo.client.gui;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JButton;

/**
 * This Class is an Label witch you can add ActionListeners. It also shows a
 * MouseHand on mouse over and highlighted the text.
 * 
 * @author M.J.B. Kupers
 *  
 */
public class LinkedLabel extends JButton  {
    
    private Color mouseoverColor = GuiConstants.RED_COLOR;
    private Color defForeground = Color.black;
    

    /**
     * Creates a new LinkedLabel with the specified text.
     * 
     * @param s The caption of the label.
     */
    public LinkedLabel(String s) {
        super(s);
        setBorder(null);
        setBorderPainted(false);
        setContentAreaFilled(false);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
    }
    
    public void setNewForeground(Color c){
    	defForeground = c;
    	setForeground(c);
    }

    private void mouseEntered(MouseEvent arg0) {
        this.setForeground(mouseoverColor);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        repaint();
    }
    
    private void mouseReleased(MouseEvent e) {
    	setCursor(Cursor.getDefaultCursor());
    	mouseExited(e);
    }
    
    /**
     * Invoked when the mouse exits the CourseIcon. The Default Cursor is showed
     * and the text will be displayed normal.
     * 
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    private void mouseExited(MouseEvent arg0) {
        this.setForeground(defForeground);
        repaint();

    }

    protected void processMouseEvent(MouseEvent e) {
		super.processMouseEvent(e);
		switch(e.getID()) {
		case MouseEvent.MOUSE_ENTERED:
			mouseEntered(e); break;
		case MouseEvent.MOUSE_EXITED:
			mouseExited(e); break;
		case MouseEvent.MOUSE_RELEASED:
			mouseReleased(e); break;
		}
	}

	/**
     * Returns the current mouseovercolor. The mouseovercolor is showed when the
     * mouse is above the label.
     * 
     * @return The current mouseovercolor.
     */
    public Color getMouseoverColor() {
        return mouseoverColor;
    }

    /**
     * Sets the current mouseovercolor. The mouseovercolor is showed when the
     * mouse is above the label.
     * 
     * @param mouseoverColor The mouseovercolor to set.
     */
    public void setMouseoverColor(Color mouseoverColor) {
        this.mouseoverColor = mouseoverColor;
    }
}