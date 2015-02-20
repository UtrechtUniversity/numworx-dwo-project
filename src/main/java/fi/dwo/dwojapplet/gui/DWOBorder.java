package fi.dwo.dwojapplet.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.border.AbstractBorder;
/**
 * Dit is een border die de 'standaard' DWO look-and-feel genereerd.
 * Te weten, een afgeronde rechthoek met schaduw.
 * De vorm van de rechthoek wordt bepaald door de opgegeven image.
 * @author wim
 *
 */
public class DWOBorder extends AbstractBorder {

	public DWOBorder(Image image, Insets insets, int[] r)
	{
		this(image, insets, r[0], r[1], r[2], r[3], r[4], r[5]);
	}
	
	
	public DWOBorder(Image image, Insets insets, int w1, int w2, int w, int h1,
			int h2, int h) {
		super();
		this.image = image;
		if(insets != null)
			this.insets = insets;
		this.w1 = w1;
		this.w2 = w2;
		this.w = w;
		this.h1 = h1;
		this.h2 = h2;
		this.h = h;
	}

	private Image image;
	static final Insets DEFAULT_INSETS = new Insets(5,5,5,5);
	private Insets insets = DEFAULT_INSETS;
	private int w1, w2, w; // splitpoints hor
	private int h1, h2, h; // splitpoints ver
	private Color background = GuiConstants.MAIN_BACKGROUND;
	
	
        @Override
	public void paintBorder(Component c, Graphics g, int x, int y,
			int width, int height) {
		if(image != null)
		{
// a ninepatch:			
			int x1 = x + w1;
			int x2 = x + width - (w-w2);
			int x3 = x + width;
			int y1 = y + h1;
			int y2 = y + height - (h-h2);
			int y3 = y + height;
			
			
/*topleft*/  g.drawImage(image, x,  y, x1, y1, 0, 0, w1, h1, c);
/*topmiddle*/g.drawImage(image, x1, y, x2, y1, w1, 0, w2, h1, c);
/*topright*/ g.drawImage(image, x2, y, x3, y1, w2, 0, w, h1, c);
/*middleleft*/  g.drawImage(image, x, y1, x1, y2, 0, h1, w1, h2, c);
/*middlemiddle*/g.drawImage(image, x1, y1, x2, y2, w1, h1, w2, h2, c);
/*middleright*/	g.drawImage(image, x2, y1, x3, y2, w2, h1, w, h2, c);
/*botleft*/	 g.drawImage(image, x, y2, x1, y3, 0, h2, w1, h, c);
/*botmiddle*/g.drawImage(image, x1,y2, x2, y3, w1, h2, w2, h, c);
/*botright*/ g.drawImage(image, x2, y2, x3, y3, w2, h2, w, h, c);

		} else {
			g.setColor(background);
			g.fillRect(x, y, width, height);
		}
	}

        @Override
	public Insets getBorderInsets(Component c, Insets insets) {
		insets.bottom = this.insets.bottom;
		insets.top    = this.insets.top;
		insets.left   = this.insets.left;
		insets.right  = this.insets.right;
		return insets;
	}

        @Override
	public Insets getBorderInsets(Component c) {
		return (Insets) insets.clone();
	}

        @Override
	public boolean isBorderOpaque() {
		return true;
	}

	public Color getBackground() {
		return background;
	}

	public void setBackground(Color background) {
		this.background = background;
	}
	
}


