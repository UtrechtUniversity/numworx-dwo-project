package nl.uu.fi.dwo.interaction.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.user.client.ui.Panel;
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;

public class TekstComponent {

	protected Canvas canvas;
	protected Context2d ctx;
	//public int height;
	//public int width;
	
	protected CssColor color;

	protected boolean selected = false;
	
	//private FormuleFont font;
	
	//public int x = 0;
	//public int y = 0;
	
	private int ashoogte = 0;
	
	private String tekst;
	
	public TekstComponent(FormuleFont fm, String tekst, int width, int height)
	{
		//font = fm;
		canvas = Canvas.createIfSupported();
		//canvas.setSize(width + "px", height + "px");
		canvas.setCoordinateSpaceHeight(height);
		canvas.setCoordinateSpaceWidth(width);
		ctx = canvas.getContext2d();
		String fontString = fm.toString();
		if(fontString.contains("SansSerif"))
			fontString.replace("SansSerif", "sans-serif");
		if(fontString.contains("Dialog"))
			fontString.replace("Dialog", "Arial");
		ctx.setFont(fontString);
		ashoogte = fm.getAscent();
		this.tekst = tekst;
	}
	
//	public int getHeight()
//	{
//		return height;
//	}

//	public void setHeight(int height)
//	{
//		this.height = height;
//	}

//	public int getWidth()
//	{
//		return canvas.getCoordinateSpaceWidth();
//	}

//	public void setWidth(int width)
//	{
//		this.width = width;
//	}

	/*
	public int getX()
	{
		return x;
	}
	
	public void setX(int x)
	{
		this.x = x;
	}

	public int getY()
	{
		return y;
	}

	public void setY(int y)
	{
		this.y = y;
	}
	*/

	public void paint()
	{
		paintObject();
	}
	
	public void paintObject()
	{
		//ctx.setFillStyle("red");
		//ctx.fillRect(0, 0, width, height);
		//ctx.setFillStyle(color.toString());
		ctx.fillText(tekst, 0, ashoogte);
	}
	
	/**
	 * Ashoogte is the middle draw position of the element
	 */

	/*
	public void setAsHoogte(int ashoogte)
	{
		this.ashoogte = ashoogte;
	}

	public int getAsHoogte()
	{
		return this.ashoogte;
	}
	*/

	/**
	 * Position the object should be drawn
	 * 
	 * @param x
	 * @param y
	 */
	/*
	public void setPosition(int x, int y)
	{
		this.x = x;
		this.y = y;
	}*/

	public void setColor(CssColor c)
	{
		color = c;
		ctx.setFillStyle(color.toString());
	}
	
	public CssColor getColor()
	{
		return color;
	}
	
//	public FormuleFont getFont()
//	{
//		return font;
//	}
//	
//	public void setFont(FormuleFont fm)
//	{
//		font = fm;
//	}
	
	public Panel getAsPanel()
	{
		//FocusPanel sp = new FocusPanel();
		TouchPanel sp = new TouchPanel();
		sp.add(this.canvas);
		return sp;
	}

	public Canvas getCanvas()
	{
		return this.canvas;
	}
	
	/*
	//nodig?
	public void draw(Context2d ctx, int x, int y)
	{
		ctx.drawImage(this.canvas.getCanvasElement(), x, y);
	}

	//nodig?
	public void draw(Context2d ctx)
	{
		ctx.drawImage(this.canvas.getCanvasElement(), this.x, this.y);
	}*/
}
