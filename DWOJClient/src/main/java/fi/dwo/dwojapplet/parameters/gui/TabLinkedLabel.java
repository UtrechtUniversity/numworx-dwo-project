/*
 * Created on Apr 27, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package fi.dwo.dwojapplet.parameters.gui;

import fi.dwo.dwojapplet.gui.LinkedLabel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

/**
 * @author M.J.B. Kupers
 *
 */
public class TabLinkedLabel extends LinkedLabel {

    private Color deselectedColor;

    private boolean isSelected;

    private Color selectedColor;

    /**
     * @param s
     */
    public TabLinkedLabel(String s) {
        super(s);
        isSelected = false;
        setContentAreaFilled(true);
        setFocusPainted(false);
    }

    /**
     * @return Returns the deselectedColor.
     */
    public Color getDeselectedColor() {
        return deselectedColor;
    }

    /**
     * Returns the <i>current</i> size as the minimum size.
     *
     * @return 
     * @see java.awt.Component#getMinimumSize()
     */
    public Dimension getMinimumSize() {
        return super.getSize();
    }

    /**
     * Returns the <i>current</i> size as the preferred size.
     *
     * @return 
     * @see java.awt.Component#getPreferredSize()
     */
    public Dimension getPreferredSize() {
        return super.getSize();
    }

    /**
     * @return Returns the selectedColor.
     */
    public Color getSelectedColor() {
        return selectedColor;
    }

    /**
     * @return Returns the isSelected.
     */
    public boolean isSelected() {
        return isSelected;
    }

    /* (non-Javadoc)
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
    public void paint(Graphics g) {
        if (isSelected) {
            this.setBackground(selectedColor);
        } else {
            this.setBackground(deselectedColor);
        }
        super.paint(g);

        if (isSelected) {
            g.setColor(Color.black);
            g.drawLine(0, 0, this.getSize().width - 1, 0); //top line
            g.drawLine(0, this.getSize().height - 1, this.getSize().width - 1, this.getSize().height - 1); //bottom line
            g.drawLine(0, 0, 0, this.getSize().height - 1); // left-line
        }
    }

    /**
     * @param deselectedColor The deselectedColor to set.
     */
    public void setDeselectedColor(Color deselectedColor) {
        this.deselectedColor = deselectedColor;
    }

    /**
     * @param isSelected The isSelected to set.
     */
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
        this.repaint();
    }

    /**
     * @param selectedColor The selectedColor to set.
     */
    public void setSelectedColor(Color selectedColor) {
        this.selectedColor = selectedColor;
    }

}
