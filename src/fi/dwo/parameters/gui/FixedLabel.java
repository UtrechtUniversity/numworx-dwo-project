/*
 * Created on Apr 20, 2005
 *
 */
package fi.dwo.parameters.gui;


import java.awt.Dimension;

import javax.swing.JLabel;

/**
 * @author M.J.B. Kupers
 *
 * This class is the same as a label, but the prefered size, and the minimum size
 * are equal to the current size (so the size can't be fucked up by the layoutmanager).
 */
public class FixedLabel extends JLabel {

    
    /**
     * @throws java.awt.HeadlessException
     */
    public FixedLabel() {
        super();
    }
    /**
     * @param text
     */
    public FixedLabel(String text) {
        super(text);
    }
    /**
     * @param text
     * @param alignment
     */
    public FixedLabel(String text, int alignment) {
        super(text, alignment);
    }
    /**
     * Returns the <i>current</i> size as the minimum size.
     * @see java.awt.Component#getMinimumSize()
     */
    public Dimension getMinimumSize() {
        return super.getSize();
    }
    public Dimension getMaximumSize() {
        return super.getSize();
    }

    /**
     * Returns the <i>current</i> size as the preferred size.
     * @see java.awt.Component#getPreferredSize()
     */
    public Dimension getPreferredSize() {
        return super.getSize();
    }
}
