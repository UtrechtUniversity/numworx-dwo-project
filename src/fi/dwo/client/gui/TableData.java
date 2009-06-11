/*
 * Created on Mar 7, 2005
 *
 */
package fi.dwo.client.gui;

import java.awt.Color;
import java.awt.Panel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Vector;

/**
 * On the TableData panel will the data of the table been stored.
 * 
 * @author M.J.B. Kupers
 * @version 1.0
 * @see fi.dwo.client.gui.Table
 */
public class TableData extends Panel {
    
    private Vector horizontalLines;
    private Vector verticalLines;
    //private Color lineColor = Color.black;
    private Color lineColor = GuiConstants.MAIN_BACKGROUND;

    /**
     * Creates a new TableData panel with a null-layout.
     */
    public TableData() {
        //super(null, NONE);
        super(null);
        horizontalLines = new Vector();
        verticalLines = new Vector();
    }

    /**
     * Gets the mininimum size of this component. It returns the currentsize,
     * because of we don't want to shrink this component (if it is to large, the
     * Table component shows scrollbars).
     * 
     * @return A dimension object indicating this component's minimum size.
     */
    public Dimension getMinimumSize() {
        return this.getSize();
    }

    /**
     * Gets the preferred size of this component. It returns the currentsize,
     * because of we don't want to shrink this component (if it is to large, the
     * Table component shows scrollbars).
     * 
     * @return A dimension object indicating this component's preferred size.
     */
    public Dimension getPreferredSize() {
        return this.getSize();
    }
    
    public void paint(Graphics g) {
        super.paint(g);
        Point p;
        int width = getSize().width;
        int height = getSize().height;

        g.setColor(lineColor);
        for(int i = 0; i < horizontalLines.size(); i++) {
            p = (Point) horizontalLines.elementAt(i);
            g.fillRect(0, p.y, width, p.x);
        }

        for(int i = 0; i < verticalLines.size(); i++) {
            p = (Point) verticalLines.elementAt(i);
            g.fillRect(p.x, 0, p.y, height);
        }
    }
    
    public void drawHorizontalLine(int y, int pixels) {
        /* Save the data in a point */
        horizontalLines.addElement(new Point(pixels, y));
    }

    public void drawVerticalLine(int x, int pixels) {
        verticalLines.addElement(new Point(x, pixels));
    }
    
    public void clearLines() {
        horizontalLines.removeAllElements();
        verticalLines.removeAllElements();

    }

    public Color getLineColor() {
        return lineColor;
    }
    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
    }
}