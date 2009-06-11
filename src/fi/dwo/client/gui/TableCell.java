/*
 * Created on Mar 7, 2005
 *
 */
package fi.dwo.client.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Panel;

/**
 * The table cell will be used to lay-out the Table component.
 * 
 * @author M.J.B. Kupers
 * @version 1.0
 * @see fi.dwo.client.gui.Table
 */
public class TableCell extends Panel {

    private Component component;

    private int x;

    private int y;

    /**
     * The north layout constraint (top of TableCell).
     */
    public final static String NORTH = BorderLayout.NORTH;

    /**
     * The south layout constraint (bottom of TableCell).
     */
    public final static String SOUTH = BorderLayout.SOUTH;

    /**
     * The east layout constraint (right side of TableCell).
     */
    public final static String EAST = BorderLayout.EAST;

    /**
     * The west layout constraint (left side of TableCell).
     */
    public final static String WEST = BorderLayout.WEST;

    /**
     * The center layout constraint (middle of TableCell).
     */
    public final static String CENTER = BorderLayout.CENTER;

    /**
     * Creates a new instance of a TableCell.
     */
    public TableCell() {
        super(new BorderLayout());
    }

    /**
     * Creates a new instance of a TableCell and adds the specified component on
     * layout WEST to the TableCell.
     * 
     * @param c The component to add to the TableCell.
     */
    public TableCell(Component c) {
        super(new BorderLayout());
        this.add(c, BorderLayout.WEST);
        this.component = c;
    }

    /**
     * Creates a new instance of a TableCell and adds the specified component at
     * the specified alignment to the TableCell.
     * 
     * @param c The component to add to the TableCell.
     * @param align The alignment of the component.
     */
    public TableCell(Component c, String align) {
        super(new BorderLayout());
        this.add(c, align);
        this.component = c;
    }

    /**
     * Returns the component contained by the TableCell.
     * 
     * @return The component contained by the TableCell.
     */
    public Component getComponent() {
        return component;
    }

}