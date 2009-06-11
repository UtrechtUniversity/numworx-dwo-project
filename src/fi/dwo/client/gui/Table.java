/*
 * Created on Mar 2, 2005
 *
 */
package fi.dwo.client.gui;

import java.awt.Color;
import java.awt.Panel;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Label;
import java.awt.ScrollPane;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Vector;

/**
 * Table is a user-interface component that presents data in a two-dimensional
 * table format. The number of columns is definied with the constructor and
 * cannot be changed.
 * 
 * @author M.J.B. Kupers
 * @version 1.0
 */
public class Table extends Panel implements ComponentListener {
    
    private Color borderColor = GuiConstants.MAIN_BACKGROUND;
    //private Color borderColor = Color.black;

    private Vector cells;

    private Vector heights;

    private int horizontalSpacing = 3;


    private int nrColumns;

    private int nrRows;

    private ScrollPane sp;

    private TableData tableData;

    private Label title;

    private int verticalSpacing = 3;

    private Vector widths;
    
    private int nrHeaderRows = 1;
    private int nrHeaderCols = 0;
    private boolean showHeaderLines = false;
    private boolean showBorder = false;
    
    private boolean reorderLayout = false;
    
    private Color componentBackground;
    
    private boolean showScrollBars;
    
    private Panel fillPanelEast, fillPanelSouth, fillPanelSouthEast;

    /**
     * Creates a new instance of a Table Object. The number of columns can only
     * be specified with the constructor.
     * 
     * @param nrColumns The number of columns of the table.
     */
    public Table(int nrColumns) {
        //super(null, NONE);
        super(null);
        this.nrColumns = nrColumns;

        tableData = new TableData();

        nrRows = 0;
        heights = new Vector();
        widths = new Vector();
        cells = new Vector();

        /* Initialize widths array */
        for (int i = 0; i < nrColumns; i++) {
            widths.addElement(new Integer(0));
        }

        title = new Label("");
        title.setFont(GuiConstants.RED_TEXT);
        FontMetrics fm = title.getFontMetrics(title.getFont());
        title.setLocation(0, 0);
        title.setSize(this.getSize().width, fm.getHeight() + 2);
        this.add(title);
        
        sp = new ScrollPane();
        //sp.setLocation(0, fm.getHeight() + 4);
        sp.setLocation(-3, fm.getHeight());
        tableData.setLocation(0, 0);
        sp.add(tableData);
        this.add(sp);
        showScrollBars = true;
        sp.setVisible(false);
        tableData.setVisible(false);
        componentBackground = getBackground();
        
        fillPanelEast = new Panel();
        fillPanelEast.setBackground(GuiConstants.MAIN_BACKGROUND);
        this.add(fillPanelEast,0);
        
        fillPanelSouth = new Panel();
        fillPanelSouth.setBackground(GuiConstants.MAIN_BACKGROUND);
        this.add(fillPanelSouth,0);

        this.addComponentListener(this);
    }

    /**
     * Adds an empty row to the table.
     */
    public void addRow() {
        TableCell tc;
        for (int i = 0; i < nrColumns; i++) {
            tc = new TableCell();

            tableData.add(tc);
            tc.setBackground(componentBackground);
            cells.addElement(tc);

            if (tc.getSize().width > ((Integer) widths.elementAt(i)).intValue()) {
                widths.removeElementAt(i);
                widths.insertElementAt(new Integer(tc.getSize().width), i);
            }
        }

        heights.addElement(new Integer(0));

        nrRows++;
        repaint();
    }

    /**
     * Adds the specified components to a new row at the end of the table.
     * The component sizes are recalced.
     * 
     * @param data The components to add.
     */
    public void addRow(Component[] data) {
        addRow(data, true);
    }

    /**
     * Adds the specified components to a new row at the end of the table.
     * 
     * @param data The components to add.
     * @param relayout If true, the component sizes are recalced.
     */
    public void addRow(Component[] data, boolean relayout) {
        addRow(data, TableCell.WEST, relayout);
    }

    /**
     * Adds the specified components to a new row at the end of the table.
     * The component sizes are recalced.
     * 
     * @param data The components to add.
     * @param alignment The alignment of the components in the column.
     */
    public void addRow(Component[] data, String alignment) {
        addRow(data, alignment, true);
    }
    /**
     * Adds the specified components to a new row at the end of the table.
     * 
     * @param data The components to add.
     * @param alignment The alignment of the components in the column.
     * @param relayout If true, the component sizes are recalced.
     */
    public void addRow(Component[] data, String alignment, boolean relayout) {
        int i;
        int maxHeight = 0;
        TableCell tc;
        for (i = 0; i < data.length; i++) {

            tc = new TableCell(data[i], alignment);
            tc.setBackground(componentBackground);

            if (data[i].getSize().height > maxHeight) {
                maxHeight = data[i].getSize().height;
            }

            if (data[i].getSize().width > ((Integer) widths.elementAt(i)).intValue()) {
                widths.removeElementAt(i);
                widths.insertElementAt(new Integer(data[i].getSize().width), i);
            }

            cells.addElement(tc);
            tc.setVisible(false);
            tableData.add(tc);
            tc.setVisible(true);
        }

        /* If we've got empty rows, fill it with empty cells */
        if (data.length < nrColumns) {
            for (i = data.length; i < nrColumns; i++) {
                tc = new TableCell();
                tc.setBackground(componentBackground);

                tc.setVisible(false);
                tableData.add(tc);
                tc.setVisible(true);
                cells.addElement(tc);

                if (tc.getSize().width > ((Integer) widths.elementAt(i)).intValue()) {
                    widths.removeElementAt(i);
                    widths.insertElementAt(new Integer(tc.getSize().width), i);
                }
            }
        }

        heights.addElement(new Integer(maxHeight));
        nrRows++;

        reorderLayout = true;
        if(relayout) {
	        repaint();
        }
    }

    /**
     * Invoked when the component has been made invisible.
     * 
     * @param e The eventdata.
     * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
     */
    public void componentHidden(ComponentEvent e) {
    }

    /**
     * Invoked when the component's position changes.
     * 
     * @param e The eventdata.
     * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
     */
    public void componentMoved(ComponentEvent e) {
    }

    /**
     * Invoked when the component's size changes.
     * 
     * @param e The eventdata.
     * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
     */
    public void componentResized(ComponentEvent e) {
        if (e.getComponent() == this) {
            FontMetrics fm = title.getFontMetrics(title.getFont());
            title.setSize(this.getSize().width, fm.getHeight() + 2);
            repaint();
        }

    }

    /**
     * Invoked when the component has been made visible.
     * 
     * @param e The eventdata.
     * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
     */
    public void componentShown(ComponentEvent e) {
    }

    public Color getBorderColor() {
        return borderColor;
    }

    /**
     * Returns the item on the specified column and row.
     * 
     * @param col The column of the component.
     * @param row The row of the component.
     * @return The component at the specified location.
     */
    public Component getItem(int col, int row) {
        return ((TableCell) cells.elementAt((row * nrColumns) + col)).getComponent();
    }

    /**
     * Returns the number of columns.
     * 
     * @return The number of columns.
     */
    public int getNrColums() {
        return nrColumns;
    }

    /**
     * Returns the number of rows.
     * 
     * @return The number of rows
     */
    public int getNrRows() {
        return nrRows;
    }

    /**
     * Returns the title of the table.
     * 
     * @return The title of the table.
     */
    public String getTitle() {
        return title.getText();
    }

    /**
     * Calulates the total required height of the table.
     * 
     * @return The total required height of the table.
     */
    public int getTotalHeight() {
        int i;
        int totalHeight = 0;
        for (i = 0; i < heights.size(); i++) {
            totalHeight += ((Integer) heights.elementAt(i)).intValue();
        }
        return totalHeight + horizontalSpacing * (1 + heights.size()) - 1;
    }

    /**
     * Calulates the total required width of the table.
     * 
     * @return The total required width of the table.
     */
    public int getTotalWidth() {
        int i;
        int totalWidth = 0;
        for (i = 0; i < widths.size(); i++) {
            totalWidth += ((Integer) widths.elementAt(i)).intValue();
        }
        return totalWidth + verticalSpacing * (1 + widths.size()) - 1;
    }

    /**
     * Paints the object.
     * 
     * @param g The graphics context to use for painting.
     */
    public void paint(Graphics g) {
        if(reorderLayout) {
            reorderLayout();
        }
        reorderLayout = false;
        super.paint(g);
    }

    /**
     * Remove the row that contains the specified component.
     * 
     * @param com The component wherefrom the row must be removed.
     */
    public void removeRow(Component com) {
        int i = 0;
        while ((i < cells.size())
                && (((TableCell) cells.elementAt(i)).getComponent() != com)) {
            i++;
        }

        if (i < cells.size()) {
            removeRow(i / nrColumns);
        }
        
    }

    /**
     * Removes the row on the specified index. <br>
     * pre: <code>0 <= row < getNrRows()</code>.
     * 
     * @param row the row to remove.
     */
    public void removeRow(int row) {
        int itemnr = nrColumns * row;

        int i = nrColumns * row;

        while (i < nrColumns * row + nrColumns) {
            Component com = tableData.getComponent(itemnr);
            cells.removeElement(com);
            com.setVisible(false);
            tableData.remove(com);
            com.setVisible(true);
            i++;
        }

        heights.removeElementAt(row);

        nrRows--;

        reorderLayout = true;
        repaint();

    }
    
    /**
     * Removes all the rows of the table.
     *
     */
    public void removeAllRows() {
        tableData.removeAll();

        nrRows = 0;
        heights = new Vector();
        widths = new Vector();
        cells = new Vector();

        /* Initialize widths array */
        for (int i = 0; i < nrColumns; i++) {
            widths.addElement(new Integer(0));
        }
        reorderLayout = true;
        repaint();
       
    }

    /**
     * Recalculate and relocate the components on the panel. If the items need
     * to much space, a scrollbar is showed.
     *  
     */
    public void reorderLayout() {
        if(cells.size() == 0) {
            sp.setVisible(false);
            tableData.setVisible(false);
        } else {
            tableData.setVisible(true);

            int height = getTotalHeight() + 1;
	        int width = getTotalWidth() + 1;
	
	        TableCell tc;
	        int cellWidth = 0;
	        int cellHeight = 0;
	
	        //if(showBorder) {
	        //    tableData.setBorders(TableData.ALL);
	        //} else {
	        //    tableData.setBorders(TableData.NONE);                
	        //}
	
	        Insets is = tableData.getInsets();
	        int locationX = is.left;
	        int locationY = is.top;
	        int firstHeight = 0;
	        int firstWidth = 0;
	        for (int i = 0; i < cells.size(); i++) {
	            tc = (TableCell) cells.elementAt(i);
	            cellWidth = ((Integer) widths.elementAt(i % nrColumns)).intValue();
	            cellHeight = ((Integer) heights.elementAt(i / nrColumns)).intValue();
	
	            tc.setSize(cellWidth, cellHeight);
	            tc.setLocation(locationX + horizontalSpacing, locationY + verticalSpacing);
	
	            tc.validate();
	
	            if ((i + 1) % nrColumns == 0) {
	                locationX = is.left;
	                locationY += ((Integer) heights.elementAt(i / nrColumns)).intValue()
	                        + horizontalSpacing;
	            } else {
	                locationX += ((Integer) widths.elementAt(i % nrColumns)).intValue()
	                        + verticalSpacing;
	            }
	            
	            if(i == 0) {
	                firstHeight = cellHeight;
	                firstWidth = cellWidth;
	            }
	        }
	        
	        if ((height + 15 > getSize().height) || (width + 15 > getSize().width)) {
	
	            sp.setSize(getSize().width, getSize().height
	                    - title.getSize().height - 2);
	            if (!showScrollBars) {
	                tableData.setVisible(false);
	                this.remove(tableData);
	                sp.add(tableData);
	                tableData.setLocation(0, 0);
	                tableData.setVisible(true);
	            }
	            sp.setVisible(true);
	            showScrollBars = true;
	            //tableData.setBorders(TableData.NONE);
	            
	        } else {
	            if (showScrollBars) {
	                tableData.setVisible(false);
	                sp.remove(tableData);
	                this.add(tableData);
	                tableData.setLocation(0, title.getSize().height + 2);
	                tableData.setVisible(true);
	            }
	            sp.setVisible(false);
	            showScrollBars = false;
	            
	        }
	
	        tableData.setSize(width, height);
	
	        tableData.clearLines();
	        if(showHeaderLines) {
	            if((nrHeaderRows > 0) && (nrRows >= nrHeaderRows)) {
	                tableData.drawHorizontalLine(firstHeight + 1, 2);
	            }
	            if((nrHeaderCols > 0) && (nrColumns >= nrHeaderCols)) {
	                tableData.drawVerticalLine(firstWidth + 1, 2);
	            }
	        }
	        
	        sp.validate();
	        if(sp.isVisible()) {
		        Insets insets = sp.getInsets();
		        if(sp.getSize().width -insets.left - insets.right - 1 > width) {
		            //sp.setSize(width + insets.left + insets.right, sp.getSize().height);
		        }
		        
		        if(sp.getSize().height -insets.top - insets.bottom - 1 > height) {
		            //sp.setSize(sp.getSize().width, height + insets.top + insets.bottom);
		        }
		        fillPanelEast.setBounds(width + insets.left + insets.right-20, 0,  sp.getSize().width-(width + insets.left + insets.right), sp.getSize().height+15);
		        fillPanelSouth.setBounds(0, height + insets.top + insets.bottom, sp.getSize().width+15, sp.getSize().height-(height + insets.top + insets.bottom));
		            
	        }/**/
	        
	        tableData.setSize(width, height);
	        sp.validate();
	        
        }
        
        
    }
    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    /**
     * Sets the horizontal gap of the table
     * 
     * @param hgap the horizontal gap between items
     */
    public void setHgap(int hgap) {
        horizontalSpacing = hgap;
    }

    /**
     * Change the content on the specified location in the new component.
     * 
     * @param col The columnindex to change the component.
     * @param row The rowindex to change the component.
     * @param item The new component to set.
     */
    public void setItem(int col, int row, Component item) {

        int maxHeight = ((Integer) heights.elementAt(row)).intValue();
        int maxWidth = ((Integer) widths.elementAt(col)).intValue();

        if (item.getSize().height > maxHeight) {
            maxHeight = item.getSize().height;
        }

        heights.setElementAt(new Integer(maxHeight), row);

        if (item.getSize().width > maxWidth) {
            maxWidth = item.getSize().width;
        }

        widths.setElementAt(new Integer(maxWidth), col);

        Component com = tableData.getComponent((row * nrColumns) + col);
        cells.removeElement(com);

        tableData.remove(com);

        TableCell tc = new TableCell(item);
        tc.setBackground(componentBackground);

        tableData.add(tc, (row * nrColumns) + col);
        cells.insertElementAt(tc, (row * nrColumns) + col);

        reorderLayout = true;
        repaint();
    }
    
    

    /**
     * Sets the title of the table.
     * 
     * @param ttl The title of the table to set.
     */
    public void setTitle(String ttl) {
        title.setText(ttl);
    }

    /**
     * Sets the vertical gap of the table
     * 
     * @param vgap the vertical gap between items
     */
    public void setVgap(int vgap) {
        verticalSpacing = vgap;
    }
    
    /**
     * Returns the number of header columns.
     * @return The number of header columns.
     */
    public int getNrHeaderCols() {
        return nrHeaderCols;
    }
    
    /**
     * Sets the number of header columns.
     * @param nrHeaderCols The number of header columns.
     */
    public void setNrHeaderCols(int nrHeaderCols) {
        this.nrHeaderCols = nrHeaderCols;
    }
    
    /**
     * Returns the number of header rows.
     * @return The number of header rows.
     */
    public int getNrHeaderRows() {
        return nrHeaderRows;
    }
    
    /**
     * Sets the number of header rows.
     * @param nrHeaderRows The number of header rows.
     */
    public void setNrHeaderRows(int nrHeaderRows) {
        this.nrHeaderRows = nrHeaderRows;
    }
    
    /**
     * Indicates if the header lines are showed.
     * @return true, if the header lines are showed.
     */
    public boolean isShowHeaderLines() {
        return showHeaderLines;
    }
    
    /**
     * Set if the header lines must showed.
     * @param showHeaderLines True if the header lines must showed.
     */
    public void setShowHeaderLines(boolean showHeaderLines) {
        this.showHeaderLines = showHeaderLines;
    }

    /**
     * Indicate that the header lines must showed.
     *
     */
    public void showHeaderLines() {
        this.showHeaderLines = true;
    }

    /**
     * Sets the background of a tablecell.
     * @param c The new backgroundcolor.
     */
    public void setComponentBackground(Color c) {
        componentBackground = c;
        for(int i = 0; i < cells.size(); i++) {
            ((Component) cells.elementAt(i)).setBackground(c);
        }
    }
    
    /**
     * Sets the background of the table data.
     * @param c The new backgroundcolor.
     */
    public void setBackground(Color c) {
        tableData.setBackground(c);
    }
    
    /**
     * Indicate that the border lines must showed.
     *
     */
    public void showBorder() {
        showBorder = true;
    }
    
    /**
     * Indicate that the border lines must be hidden.
     *
     */
    public void hideBorder() {
        showBorder = false;
    }
    
    /**
     * Indicates if the border is showed.
     * @return True if the border is showed.
     */
    public boolean isShowBorder() {
        return showBorder;
    }
}