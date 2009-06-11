// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\CourseIconIF.java

package fi.dwo.client.gui;

import java.awt.event.ActionListener;

import fi.dwo.client.domain.Course;

/**
 * 
 * @author M.J.B. Kupers
 *  
 */
public interface CourseIconIF {

    /**
     * Returns the current Course.
     * 
     * @return The current Course.
     */
    public Course getCourse();

    /**
     * Adds the specified action listener to receive action events from this
     * button. Action events occur when a user presses or releases the mouse
     * over this button. If l is null, no exception is thrown and no action is
     * performed.
     * 
     * @param l the action listener.
     */
    public void addActionListener(ActionListener l);
}