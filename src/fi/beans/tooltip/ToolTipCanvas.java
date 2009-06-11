/*
 * Created on Mar 17, 2005
 *
 */
package fi.beans.tooltip;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import fi.beans.stringutils.StringUtils;

/**
 * The graphical ToolTip.
 * @author M.J.B. Kupers
 *
 */
public class ToolTipCanvas extends Canvas {
    
    private String text[];
    
    private static final int LINE_SPACE = 2;
    
    

    /**
     * Creates a new ToolTipCanvas with the specified text.
     * If the text is a multi-line text, a multi-line tooltip is showen.
     * @param text The text to show.
     */
    public ToolTipCanvas(String text) {
        super();
        setMultiLineText(text);
        
        this.setBackground(new Color(255,255,225));
        this.setForeground(Color.black);
        this.setFont(new Font("Verdana", Font.PLAIN, 10));
		this.setSize(calcSize());
        
    }
    
    /**
     * Paints the tooltip.
     * 
     * @param g The graphics context to use for painting.
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
    public void paint(Graphics g) {
		super.paint(g);
		g.setColor(this.getBackground());
		g.fillRect(0, 0, getSize().width-1, getSize().height-1);

		int posY = this.getFont().getSize() + 2;
		
		
		g.setFont(this.getFont());
		g.setColor(this.getForeground());
		for(int i = 0; i < text.length; i++) {
		    g.drawString(text[i], 5, posY);
		    posY += this.getFont().getSize() + LINE_SPACE;
		}
		g.drawRect(0, 0, getSize().width-1, getSize().height-1);
    }


    /**
     * Sets the font of the tooltip.
     * The size of the tooltip is been recalced.
     * @param f the font to become this component's font;
     * 		if this parameter is <code>null</code> then this
     *		component will inherit the font of its parent.
     */
    public void setFont(Font f) {
        super.setFont(f);
		this.setSize(calcSize());
    }
    
    /**
     * Splits the msg on a newline char, and sets the resultarray on the <code>text</code> internal variable.
     * @param msg
     */
    private void setMultiLineText(String msg) {
        text = StringUtils.split(msg, "\n");
    }
    
    /**
     * Calculates the size of the tooltip with the current font and the current (multiline) text.
     * @return The dimension for the tooltip with the current font and the current (multiline) text.
     */
    private Dimension calcSize() {
        Dimension d = new Dimension();
		FontMetrics fm = getFontMetrics(this.getFont());
		d.width = 0;
		int width;
		
		for(int i = 0; i < text.length; i++) {
		    width = fm.stringWidth(text[i]);
		    if(width > d.width) {
		        d.width = width;
		    }
		}
        d.width += 10;
        d.height = ((getFont().getSize() + LINE_SPACE) * text.length) + 4;
        return d;
    }
}
