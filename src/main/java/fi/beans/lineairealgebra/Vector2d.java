package fi.beans.lineairealgebra;

public class Vector2d implements java.io.Serializable {
	
	private static final long serialVersionUID = 478565903937391913L;

	private double x;
	private double y;
	
	public Vector2d() {
		x = 0.0;
		y = 0.0;
	}
	
	public Vector2d(double x, double y) {
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
	
	public void scale(double s) {
		this.x *= s;
		this.y *= s;
	}
	
	public double length() {
		return Math.sqrt(this.x * this.x + this.y *this.y);
	}
	
	public void normalize() {
		double norm = Math.sqrt(this.x * this.x + this.y *this.y);
		if (norm > 0) {
			this.x /= norm;
			this.y /= norm;
		}
	}

}
