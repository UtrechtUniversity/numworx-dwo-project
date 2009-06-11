/*
 * Created on Feb 28, 2005
 *
 */
package fi.dwo.client.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

/**
 * This Class is an Label witch you can add ActionListeners. It also shows a
 * MouseHand on mouse over and highlighted the text.
 * 
 * @author M.J.B. Kupers
 *  
 */
public class LinkedLabel extends ToolTippedLabel implements MouseListener {
    private Vector actionListeners = new Vector();

    private Color mouseoverColor = GuiConstants.RED_COLOR;
    private Color defForeground = Color.black;
    

    /**
     * Creates a new LinkedLabel with the specified text.
     * 
     * @param s The caption of the label.
     */
    public LinkedLabel(String s) {
        super(s);
        addMouseListener(this);
    }
    
    public void setNewForeground(Color c){
    	defForeground = c;
    	setForeground(c);
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
     * Invoked when the mouse has been clicked on the CourseIcon. The
     * ActionListeners are invoked.
     * 
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent arg0) {
    }

    /**
     * Invoked when the mouse enters the CourseIcon. A Hand Cursor is showed and
     * the text is highlighted.
     * 
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent arg0) {
        this.setForeground(mouseoverColor);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        repaint();
    }

    /**
     * Invoked when the mouse exits the CourseIcon. The Default Cursor is showed
     * and the text will be displayed normal.
     * 
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent arg0) {
        this.setForeground(defForeground);
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        repaint();

    }

    /**
     * Invoked when a mouse button has been pressed on the Label.
     * 
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent arg0) {
    }

    /**
     * Invoked when a mouse button has been released on a component.
     * 
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent arg0) {
        this.setForeground(defForeground);
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        for (int i = 0; i < actionListeners.size(); i++) {
            ((ActionListener) actionListeners.elementAt(i)).actionPerformed(new ActionEvent(this, 0, ""));
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