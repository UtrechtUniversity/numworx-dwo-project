package fi.writemathgwt.client;

public class DoublePoint 
{
	
	double x; double y;
	
	public DoublePoint(double x, double y) 
	{
		this.x = x; this.y = y;
	}
	
	public Point getPoint() 
	{
		int x = (int)Math.rint(this.x);
		int y = (int)Math.rint(this.y);
		return new Point(x,y);
	}
}
