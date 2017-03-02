package fi.writemathgwt.client;

public class Point 
{
	
	int x; int y;
	
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
}
