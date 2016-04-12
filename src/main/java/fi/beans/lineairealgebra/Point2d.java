package fi.beans.lineairealgebra;

public class Point2d {
//	private static final long serialVersionUID = 478565903937391913L;

	private double x;
	private double y;
	
	public Point2d() {
		x = 0.0;
		y = 0.0;
	}
	
	public Point2d(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public void set(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}

}
