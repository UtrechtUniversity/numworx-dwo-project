package fi.writemathgwt.client;

public class Rectangle 
{

	public int x; 
	public int y; 
	public int width; 
	public int height;
	
	public Rectangle(int x, int y, int w, int h) 
	{
		this.x = x; this.y = y;
		width = w; height = h;
	}
	
	public Rectangle(Rectangle r) 
	{
		x = r.x;
		y = r.y;
		width = r.width;
		height = r.height;
	}
	
	public boolean contains(int px, int py) 
	{
		return (px >= x) && (px <= (x + width)) &&
		       (py >= y) && (py <= (y + height));
	}
	
	public boolean contains(Point p) 
	{
		return (p.x >= x) && (p.x <= (x + width)) &&
		       (p.y >= y) && (p.y <= (y + height));
	}
}
