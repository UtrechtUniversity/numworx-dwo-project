/*
 * Created on Mar 7, 2005
 *
 */
package fi.dwo.client.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

/**
 * This Class is an ImagePanel witch you can add ActionListeners. It also shows
 * a MouseHand on mouse over.
 * 
 * @author M.J.B. Kupers
 *  
 */
public class LinkedImagePanel extends ImagePanel implements MouseListener {

    private Vector actionListeners = new Vector();

    /**
     * Creates a new LinkedImagePanel with the specified image.
     * 
     * @param i The image to add to the panel.
     */
    public LinkedImagePanel(Image i) {
        super(i);
        addMouseListener(this);
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
     * Invoked when the mouse has been clicked on the LinkedImage. The
     * ActionListeners are invoked.
     * 
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent arg0) {
    }

    /**
     * Invoked when the mouse enters the LinkedImage. A Hand Cursor is showed.
     * 
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent arg0) {
        this.setForeground(GuiConstants.RED_COLOR);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        repaint();
    }

    /**
     * Invoked when the mouse exits the LinkedImage. The Default Cursor is
     * showed.
     * 
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent arg0) {
        this.setForeground(Color.black);
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        repaint();

    }

    /**
     * Invoked when a mouse button has been pressed on the Panel.
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
        for (int i = 0; i < actionListeners.size(); i++) {
            ((ActionListener) actionListeners.elementAt(i)).actionPerformed(new ActionEvent(this, 0, ""));
        }
    }
}