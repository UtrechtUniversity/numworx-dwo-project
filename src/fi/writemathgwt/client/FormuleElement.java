package fi.writemathgwt.client;

//import java.awt.*;

public abstract class FormuleElement 
{
	public int x = 0;
	public int y = 0;
	public int height;
	public int width;
	public int ascent = 0;

// nodig?	
	private boolean changed = false;
	protected boolean selected = false;
	private boolean fontchangesapplied = true;
	protected boolean current = false;

	protected FormuleElement parent = null;
	
// nodig?	
	private int ashoogte = 0;

	public FormuleElement(FormuleElement parent)
	{	
		if (parent != null)
			initWithParent(parent);
	}

	private void initWithParent(FormuleElement parent)
	{	this.parent = parent;
		init();
	}

	public FormuleElement findRoot()
	{
		FormuleElement root = this;
		if (parent != null)
		{	root = parent.findRoot();
		}
		return root;
	}
	
	private void init()
	{
	}

	// redefine
	public boolean isNumber()
	{	return false;
	}

	// redefine
	public void setAscent()
	{	;
	}

	// redefine
	public void setDescent()
	{	;
	}

	public int getHeight()
	{	return height;
	}

	public void setHeight(int height)
	{	this.height = height;
	}

	public int getWidth()
	{	return width; 
	}

	public void setWidth(int width)
	{	this.width = width;
	}

	public int getX()
	{	return x;
	}

// klopt niet als alles op een canvas	
	public int getAbsoluteX()
	{
		int x = this.getX();
		if (this.parent != null)
			x += this.parent.getAbsoluteX();
		return x;
	}

	public void setX(int x)
	{	this.x = x;
	}

	public int getY()
	{	return y;
	}

	public void setY(int y)
	{	this.y = y;
	}

	//Ashoogte is the middle draw position of the element
	public void setAsHoogte(int ashoogte)
	{	this.ashoogte = ashoogte;
	}

	public int getAsHoogte()
	{	return this.ashoogte;
	}

	// redefine
	public void findSize(int leafWidth, int leafHeight)
	{
//System.out.println("FE findSizes w = " + width + " h = " + height);		
	}
	
	// redefine
	public void setPosition(int xPos, int yPos)
	{	x = xPos;
		y = yPos;
//System.out.println("FE setPosition x = " + x + " y = " + y);		
	}

	protected int minW = 0;
	protected int minH = 0;
	
// nodig?	
	public void setSize(int w, int h)
	{
		
		w = Math.max(w, minW);
		h = Math.max(h, minH);

		width = w;
		height = h;
		
		this.setChanged(true);
	}
	
	public boolean isChanged()
	{	return this.changed;
	}

	public void setChanged(boolean b)
	{
		if (this.changed == b)
			return;
		if (b == false)
		{	this.changed = false;
			return;
		}
		//if the object changed, the parent will be changed aswell (width,height etc)
		this.changed = true;
		if (this.parent != null)
			parent.setChanged(true);
	}

	// redefine
	public void convertToWriteObject()
	{
//System.out.println("FE convertToWriteObject");		
	}
	
	public void paint()
	{
		//if (this.changed == true)
		//	this.paintObject();
	}

	public void paintObject()
	{
		//this method should only draw on it's own canvas
	}

	public boolean isCurrent()
	{	return this.current;
	}

	public void setCurrent(boolean c)
	{
		if (this.current == c)
			return;
		this.current = c;
		this.setChanged(true);
	}
	/**
	 * This should return the element that will be the current element. In the
	 * "wortelvak" we have a child element. When the object is created set the
	 * current item to the child item.
	 * 
	 * @return
	 */
	public FormuleElement getCurrentOnNew()
	{	return this;
	}

	public FormuleElement getCurrentOnNewOnSelection()
	{	return this;
	}

	public void setParent(FormuleElement e)
	{	this.parent = e;
	}
	
	public FormuleElement getParent()
	{	return this.parent;
	}

	public FormuleRegel getRegelParent()
	{
		if (this.parent != null)
			return this.parent.getRegelParent();
		return null;
	}

	// redefine
	public void vulVak(String s)
	{
	}

	public void setSelected(boolean b)
	{
		if (selected == b)
			return;
		selected = b;
		this.setChanged(true);
	}

	public boolean isSelected()
	{	return this.selected;
	}
	
	// redefine
	public String toString()
	{
		return null;
	}
}
