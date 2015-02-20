/*
 * Created on Feb 25, 2005
 *
 */
package fi.dwo.dwojapplet.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Panel;

/**
 * A panel with a border. The user can define the location of the borders
 * (NORTH, SOUTH, EAST, WEST).
 *
 * @author M.J.B. Kupers
 * @deprecated gebruik JPanel en setBorder
 */
public class BorderedPanel extends Panel {

    /**
     * Draws no borders.
     */
    public final static int NONE = 0;

    /**
     * Draws a border on the top of the panel.
     */
    public final static int NORTH = 1;

    /**
     * Draws a border on the bottom of the panel.
     */
    public final static int SOUTH = 2;

    /**
     * Draws a border on the right of the panel.
     */
    public final static int EAST = 4;

    /**
     * Draws a border on the left of the panel.
     */
    public final static int WEST = 8;

    /**
     * Draws a border on all sides of the panel.
     */
    public final static int ALL = NORTH | SOUTH | EAST | WEST;

    private int borders = ALL;

    private Color borderColor = Color.black;

    /**
     * Creates a new BorderedPanel with borders on all sides
     */
    public BorderedPanel() {
        super();
    }

    /**
     * Creates a new BorderedPanel with borders on all sides with the specified
     * LayoutManager.
     *
     * @param lm The LayoutManager to set.
     */
    public BorderedPanel(LayoutManager lm) {
        super(lm);
        this.insets().bottom = 2;
        this.insets().left = 2;
        this.insets().top = 2;
        this.insets().right = 2;
    }

    /**
     * Creates a new BorderedPanel with the specified borders and the specified
     * LayoutManager.
     *
     * @param lm The LayoutManager to set.
     * @param borders The borders to show.
     */
    public BorderedPanel(LayoutManager lm, int borders) {
        super(lm);
        this.insets().bottom = 2;
        this.insets().left = 2;
        this.insets().top = 2;
        this.insets().right = 2;
        this.borders = borders;
    }

    /**
     * Returns the current border color.
     *
     * @return The current border color.
     */
    public Color getBorderColor() {
        return borderColor;
    }

    /**
     * Sets the current border color.
     *
     * @param borderColor The borderColor to set.
     */
    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    /**
     * Paints the panel and his borders.
     *
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(borderColor);

        if ((borders & NORTH) > 0) {
            g.drawLine(0, 0, this.getSize().width - 1, 0);
        }
        if ((borders & SOUTH) > 0) {
            g.drawLine(0, this.getSize().height - 1, this.getSize().width - 1, this.getSize().height - 1);
        }
        if ((borders & EAST) > 0) {
            g.drawLine(this.getSize().width - 1, 0, this.getSize().width - 1, this.getSize().height - 1);
        }
        if ((borders & WEST) > 0) {
            g.drawLine(0, 0, 0, this.getSize().height - 1);
        }
    }

    /**
     * Returns the current borders.
     *
     * @return The current borders.
     */
    public int getBorders() {
        return borders;
    }

    /**
     * Sets the current Borders.
     *
     * @param borders The borders to set.
     */
    public void setBorders(int borders) {
        this.borders = borders;
    }

    /**
     * Determines the insets of this BorderedPanel, which indicate the size of
     * the borders.
     * <p>
     * @return the insets of this BorderedPanel.
     * @see Insets
     * @see LayoutManager
     * @since JDK1.1
     */
    @Override
    public Insets getInsets() {
        int top = 0;
        int bottom = 0;
        int left = 0;
        int right = 0;
        if ((borders & NORTH) > 0) {
            top = 1;
        }
        if ((borders & SOUTH) > 0) {
            bottom = 1;
        }
        if ((borders & EAST) > 0) {
            right = 1;
        }
        if ((borders & WEST) > 0) {
            left = 1;
        }
        return new Insets(top, left, bottom, right);
    }
}
