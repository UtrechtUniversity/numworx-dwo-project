package fi.writemathgwt.client;

public class DoublePoint 
{
	
	private double x; private double y;
	
	public DoublePoint(double x, double y) 
	{
		this.setX(x); this.setY(y);
	}
	
	public Point getPoint() 
	{
		int x = (int)Math.rint(this.getX());
		int y = (int)Math.rint(this.getY());
		return new Point(x,y);
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
}
