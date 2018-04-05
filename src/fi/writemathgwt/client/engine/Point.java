package fi.writemathgwt.client.engine;


public class Point 
{
	
	public int x; 
	public int y;
	
	public Point(int x, int y) 
	{
		this.x = x; this.y = y;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public DoublePoint getDoublePoint() 
	{
		double x = this.x;
		double y = this.y;
		return new DoublePoint(x,y);
	}
	
	public void translate(int dx, int dy) {
		x+=dx;
		y+=dy;
	}
}