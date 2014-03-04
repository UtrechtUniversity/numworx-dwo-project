package nl.uu.fi.dwo.formule.client.formuleobjects;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.FormuleFontChanges;
import nl.uu.fi.dwo.interaction.client.touch.TouchPanel;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.user.client.ui.Panel;

/**
 * Base element of a formula
 * 
 * @author Danny Hendrix
 * 
 */
public abstract class FormuleElement
{
	protected Canvas canvas;
	protected Context2d ctx;
	public int height;
	public int width;
	
	protected CssColor color;

	private boolean changed = false;
	protected boolean selected = false;

	//font is the FormuleFont set from method, fm a copy (if changes) of font with the changes applied
	protected FormuleFont fm;
	private FormuleFont font;
	private FormuleFontChanges fontchanges;
	private boolean fontchangesapplied = true;

	public int x = 0;
	public int y = 0;

	protected boolean current = false;

	//protected Vector<FormuleElement> children = new Vector<FormuleElement>();
	//we need the parent, when a element changes the parent(s) should be redrawn
	protected FormuleElement parent = null;

	private int ashoogte = 0;

	protected FormuleHolder holder;

	/**
	 * Constructor
	 * 
	 * @param holder
	 */

	public FormuleElement(FormuleHolder holder)
	{
		if (holder instanceof FormuleEditor)
		{
			FormuleHolder editor = (FormuleHolder) holder;
			if (editor.getCurrentRegel() != null)
			{
				initWithParent(editor.getCurrentRegel());
				return;
			}
		}
		this.holder = holder;

		this.font = holder.getFont();
		this.color = holder.getColor();
		init();
	}

	public FormuleElement(FormuleElement parent)
	{
		initWithParent(parent);
	}

	private void initWithParent(FormuleElement parent)
	{
		this.holder = parent.holder;
		this.parent = parent;

		this.font = parent.getFont();
		this.color = parent.getColor();
		init();
	}

	private void init()
	{
		canvas = Canvas.createIfSupported();
		ctx = canvas.getContext2d();
		this.fm = font;
		//this.color = color;
		//System.out.println("this.color = CssColor.make(0, 0, 0)");
	}

	/**
	 * Is this element a number (used in formuleRegel to define fontsize)
	 */
	public boolean isNumber()
	{
		return false;
	}

	/**
	 * Position and dimensions
	 * 
	 * @return
	 */
	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public int getX()
	{
		return x;
	}

	public int getAbsoluteX()
	{
		int x = this.getX();
		if (this.parent != null)
			x += this.parent.getAbsoluteX();
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

	/**
	 * Ashoogte is the middle draw position of the element
	 */

	public void setAsHoogte(int ashoogte)
	{
		this.ashoogte = ashoogte;
	}

	public int getAsHoogte()
	{
		return this.ashoogte;
	}

	/**
	 * Position the object should be drawn
	 * 
	 * @param x
	 * @param y
	 */
	public void setPosition(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public void setSize(int w, int h)
	{
		width = w;
		height = h;

		//change the canvas dimensions
		this.canvas.setCoordinateSpaceHeight(h);
		this.canvas.setCoordinateSpaceWidth(w);

		this.setChanged(true);
	}
	
	public boolean setColor(CssColor c)
	{
		if(color == null || color != c)
		{	color = c;
			this.setChanged(true);
			return(true);
		}
		
		return(false);
	}
	
	public CssColor getColor()
	{
		return color;
	}

	/**
	 * Font
	 * 
	 * @return
	 */
	public FormuleFont getFont()
	{
		return fm;
	}

	public boolean setFont(FormuleFont fm)
	{	if (this.font == null || fm.toString() != this.font.toString() || fm.toString() != this.fm.toString() || fontchangesapplied == false)
		{	fontchangesapplied = true;
			this.setChanged(true);
			this.font = fm;
			if (this.fontchanges != null)
				this.fm = FormuleFont.createFromChanges(fm, fontchanges);
			else
				this.fm = fm;
			return true;
		}
		return false;
	}

	//if the text should always be small/italic etc, use this font to create a new font when font is changed
	public void setFontChanges(FormuleFontChanges font)
	{
		fontchanges = font;
		fontchangesapplied = false;
		//apply to font
		//fm = FormuleFont.createFromChanges(fm, font);
		this.setFont(fm);
		this.setChanged(true);
	}

	public FormuleFontChanges getFontChanges()
	{
		return this.fontchanges;
	}

	/**
	 * Should the object be repainted?
	 * 
	 * @return
	 */
	public boolean isChanged()
	{
		return this.changed;
	}

	public void setChanged(boolean b)
	{
		if (this.changed == b)
			return;
		if (b == false)
		{
			this.changed = false;
			return;
		}
		//if the object changed, the parent will be changed aswell (width,height etc)
		this.changed = true;
		if (this.parent != null)
			parent.setChanged(true);
	}

	/**
	 * Get the element as a html div element
	 * 
	 * @return
	 */
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

	/**
	 * Change the position to the x, y position the user presses
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	@Deprecated
	//use selection instead
	public FormuleElement setCurrentElementAt(int x, int y)
	{
		if (holder instanceof FormuleEditor)
			((FormuleHolder) holder).setCurrentElement(this);
		return this;
	}

	/**
	 * Paint the element
	 */
	public void paint()
	{
		if (this.changed == true)
			this.paintObject();
	}

	public void paintObject()
	{
		//this method should only draw on it's own canvas
	}

	/**
	 * dreaw on parent canvas
	 * 
	 * @param ctx
	 * @param x
	 * @param y
	 */
	
	public void draw(Context2d ctx, int x, int y)
	{
		ctx.drawImage(this.canvas.getCanvasElement(), x, y);
	}

	public void draw(Context2d ctx)
	{
		ctx.drawImage(this.canvas.getCanvasElement(), this.x, this.y);
	}
	

	/**
	 * Draw line
	 * 
	 * @param ctx
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	//should not be used because it creates a new path for every line, draw a path instead
	@Deprecated
	protected void drawline(Context2d ctx, int x, int y, int w, int h)
	{
		ctx.beginPath();
		ctx.moveTo(x, y);
		ctx.lineTo(w, h);
		ctx.stroke();
	}

	/**
	 * Current element
	 * 
	 * @return
	 */
	//is this the current element (if it is, draw the cursor)
	public boolean isCurrent()
	{
		return this.current;
	}

	public void setCurrent(boolean c)
	{
		if (this.current == c)
			return;
		this.current = c;
		this.setChanged(true);
	}

	protected void drawCursor()
	{
		this.drawCursor(width);
	}

	protected void drawCursor(int x)
	{
		if (this.isCurrent() == false)
			return;

		ctx.setLineWidth(2);
		ctx.setStrokeStyle("#00f");

		if (x - 1 < 0)
			x += 2;
		ctx.beginPath();
		ctx.moveTo(x - 1, 2);
		ctx.lineTo(x - 1, height - 2);
		ctx.stroke();
	}

	/**
	 * This should return the element that will be the current element. In the
	 * "wortelvak" we have a child element. When the object is created set the
	 * current item to the child item.
	 * 
	 * @return
	 */
	public FormuleElement getCurrentOnNew()
	{
		return this;
	}

	public FormuleElement getCurrentOnNewOnSelection()
	{
		return this;
	}

	/**
	 * All elements should have a parent except for the main element.
	 */
	/*
	public void setParent(FormuleElement e)
	{
		this.parent = e;
		this.setFont(e.getFont());
	}
	*/
	public FormuleElement getParent()
	{
		return this.parent;
	}

	public FormuleRegel getRegelParent()
	{
		if (this.parent != null)
			return this.parent.getRegelParent();
		return null;
	}

	public void vulVak(String s)
	{

	}

	public FormuleHolder getHolder()
	{
		return this.holder;
	}

	/**
	 * Selection
	 */
	public void setSelected(boolean b)
	{
		if (selected == b)
			return;

		selected = b;
		this.setChanged(true);
	}

	public boolean isSelected()
	{
		return this.selected;
	}

	public FormuleRegel selection(int selectionStartX, int selectionStartY, int selectionEndX, int selectionEndY)
	{
		return null;
	}

	/**
	 * ToString
	 */
	@Override
	public String toString()
	{
		return null;
	}
}
