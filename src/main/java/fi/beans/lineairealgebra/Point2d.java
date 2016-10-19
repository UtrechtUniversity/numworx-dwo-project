package fi.beans.lineairealgebra;

import com.google.gwt.json.client.JSONObject;

public class Point2d {
	
	private static final long serialVersionUID = 1L;
	
	private static final String cJsonIdPointXStr = "X";
	private static final String cJsonIdPointYStr = "Y";

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
	
	public Point2d(JSONObject jsonPoint) {
        this.x = jsonPoint.get(cJsonIdPointXStr).isNumber().doubleValue();
        this.y = jsonPoint.get(cJsonIdPointYStr).isNumber().doubleValue();
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
	
	public void setX(double newX) { 
		x = newX;
	}
	
	public void setY(double newY) {
		y = newY;
	}
	
	public Point2d clone() {
		Point2d cloneP = new Point2d(this.x, this.y);
		return cloneP;
	}
	
	public double squareDistance(Point2d p) {
		final double dx = this.x-p.getX();
		final double dy = this.y-p.getY();
		return ( (dx*dx) + (dy*dy) );
	}
	
	public double distance(Point2d p) {
		return ( Math.sqrt(squareDistance(p)) );
	}
	
	@Override
	public String toString() {
		String s = "Point2d("+this.x+","+this.y+")";
		return s;
	}

}
