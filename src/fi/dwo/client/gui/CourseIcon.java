// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\CourseIcon.java

package fi.dwo.client.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.print.attribute.standard.JobHoldUntil;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

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
public class CourseIcon extends JButton implements CourseIconIF {

    private Course course;

    private Image courseLogo;
    
    private Color textColor;
    
    /**
     * Creates a new CourseIcon. This indicates an image of the course and the
     * name. It is clickable what generates an ActionEvent.
     * 
     * @param course The Course wherefrom the Icon must created.
     */
    public CourseIcon(Course course) {
        super();
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        this.setBorder(null);
        setBorderPainted(false);
        this.setContentAreaFilled(false);
        this.setFocusPainted(false);
        this.course = course;
        this.setSize(120, 120);
        courseLogo = course.getCourseLogo();
        MediaTracker tr = new MediaTracker(this);
        tr.addImage(courseLogo, 0);
        try {
            tr.waitForAll();
        } catch (Exception e) {
        }
        ImageIcon icon = new ImageIcon(courseLogo) {
			public int getIconHeight() {
				return 60;
			} } ;

        textColor = Color.black;
        FontMetrics fm = getFontMetrics(GuiConstants.NORMAL_TEXT);
        int length = fm.stringWidth(course.getName());
        
        if(length < courseLogo.getWidth(this)) {
            length = courseLogo.getWidth(this);
        }
        //this.setSize(length, courseLogo.getHeight(this) + fm.getHeight() + 30);
        //this.setToolTip(course.getDescription());
        
        //textArea = new JLabel();
        setIcon(icon);
        setVerticalTextPosition(JLabel.BOTTOM);
        setHorizontalTextPosition(JLabel.CENTER);
        setVerticalAlignment(JLabel.TOP);
        setHorizontalAlignment(JLabel.CENTER);
// Font Okay?
        setFont(new Font("SansSerif", Font.PLAIN, 13));
		setText("<html><center>"+ course.getName() + "</center></html>");
    }

    /**
     * Paints the CourseIcon and the CourseName.
     * 
     * @param g The graphics context to use for painting.
     */
    public void paintx(Graphics g) {
        /* We must paint the components for correct mouse behaviour */
        super.paint(g);
        FontMetrics fm = getFontMetrics(GuiConstants.NORMAL_TEXT);
        int length = fm.stringWidth(course.getName());

        g.setColor(textColor);
        int pos = this.getSize().height - fm.getHeight() - 5;
//        g.drawString(course.getName(), (this.getSize().width / 2)
//                - (length / 2), pos);
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
    
    /* (non-Javadoc)
	 * @see javax.swing.JComponent#processMouseMotionEvent(java.awt.event.MouseEvent)
	 */
	protected void processMouseEvent(MouseEvent e) {
		super.processMouseEvent(e);
		if(e.getID()== MouseEvent.MOUSE_ENTERED)
		{
			setForeground(GuiConstants.RED_COLOR);
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		} else if (e.getID() == MouseEvent.MOUSE_EXITED)
		{
			setForeground(Color.black);
			setCursor(Cursor.getDefaultCursor());
		}
	}

// Waar is deze voor nodig?        
//    public void setFont(Font f) {
//        super.setFont(f);
//        if(course == null)
//        	return;
//        FontMetrics fm = getFontMetrics(GuiConstants.NORMAL_TEXT);
//        int length = fm.stringWidth(course.getName());
//        
//        if(length < courseLogo.getWidth(this)) {
//            length = courseLogo.getWidth(this);
//        }
//        this.setSize(length, courseLogo.getHeight(this) + fm.getHeight() + 15);
//    }
    
    
    public Dimension getMinimumSize() {
        return new Dimension(120,120);
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
        setToolTipText(toolTip);
    }

    /**
     * Returns the tooltip of this component.
     * @return The tooltip of this component. 
     * @see fi.beans.tooltip.ToolTipIF#getToolTip()
     */
    public String getToolTip() {
        return getToolTipText();
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