// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\CourseIcon.java

package fi.dwo.client.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import fi.beans.tooltip.ToolTipIF;
import fi.beans.tooltip.ToolTipManager;
import fi.beans.tekstobjects.TekstArea;
import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.DwoHelper;

/**
 * This class is a panel witch shows a icon of the course.
 * If the course has no icon specified, the default FI icon is showed.
 * @author M.J.B. Kupers
 *  
 */
public class CourseIcon extends Panel implements CourseIconIF, MouseListener, ToolTipIF {

    private Course course;

    private Vector actionListeners = new Vector();

    private Image courseLogo;
    
    private Color textColor;
    
    private String toolTip;
    
    private TekstArea textArea;

    /**
     * Creates a new CourseIcon. This indicates an image of the course and the
     * name. It is clickable what generates an ActionEvent.
     * 
     * @param course The Course wherefrom the Icon must created.
     */
    public CourseIcon(Course course) {
        super();
        this.setLayout(null);
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        this.course = course;
        //this.setSize(195, 100);
        this.setSize(120, 120);
        addMouseListener(this);
        courseLogo = course.getCourseLogo();
        MediaTracker tr = new MediaTracker(this);
        tr.addImage(courseLogo, 0);
        try {
            tr.waitForAll();
        } catch (Exception e) {
        }
        
        

        textColor = Color.black;
        FontMetrics fm = getFontMetrics(GuiConstants.NORMAL_TEXT);
        int length = fm.stringWidth(course.getName());
        
        if(length < courseLogo.getWidth(this)) {
            length = courseLogo.getWidth(this);
        }
        //this.setSize(length, courseLogo.getHeight(this) + fm.getHeight() + 30);
        //this.setToolTip(course.getDescription());
        
        textArea = new TekstArea();
		textArea.setBounds(0,60,120,60);
		textArea.addMouseListener(this);
		textArea.setAllignment(TekstArea.CENTER);
		textArea.setText(course.getName());
		add(textArea);

    }

    /**
     * Paints the CourseIcon and the CourseName.
     * 
     * @param g The graphics context to use for painting.
     */
    public void paint(Graphics g) {
        /* We must paint the components for correct mouse behaviour */
        super.paint(g);
        FontMetrics fm = getFontMetrics(GuiConstants.NORMAL_TEXT);
        int length = fm.stringWidth(course.getName());

        g.setColor(textColor);
        int pos = this.getSize().height - fm.getHeight() - 5;
        g.drawString(course.getName(), (this.getSize().width / 2)
                - (length / 2), pos);
        //g.drawImage(courseLogo, (this.getSize().width / 2)
        //        - (courseLogo.getWidth(this) / 2), pos - 15 - courseLogo.getHeight(this), null);
		g.drawImage(courseLogo, this.getSize().width / 2 - courseLogo.getWidth(this) / 2 , 0, null);
    }

    /**
     * Returns the current Course.
     * 
     * @return The current Course.
     * @see fi.dwo.client.gui.CourseIconIF#getCourse()
     */
    public Course getCourse() {
        return course;
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
     * the text will be highlighted.
     * 
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent arg0) {
        textColor = GuiConstants.RED_COLOR;
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
        textColor = Color.black;
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        repaint();
    }

    /**
     * Invoked when a mouse button has been pressed on the CourseIcon.
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
    
    
    public void setFont(Font f) {
        super.setFont(f);
        FontMetrics fm = getFontMetrics(GuiConstants.NORMAL_TEXT);
        int length = fm.stringWidth(course.getName());
        
        if(length < courseLogo.getWidth(this)) {
            length = courseLogo.getWidth(this);
        }
        this.setSize(length, courseLogo.getHeight(this) + fm.getHeight() + 15);
    }
    
    
    public Dimension getMinimumSize() {
        return this.getSize();
    }
    public Dimension getPreferredSize() {
        return this.getSize();
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