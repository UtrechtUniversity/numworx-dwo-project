package fi.writemathgwt.client.engine;

import fi.writemathgwt.client.engine.Point;

public class DoublePoint {

	public double x; 
	public double y;
	
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
	
	public void translate(double dx, double dy) {
		x+=dx;
		y+=dy;
	}
	
	public void scale(double cx, double cy, double factor) {
		x = cx+(x-cx)*factor;
		y = cy+(y-cy)*factor;
	}
}
