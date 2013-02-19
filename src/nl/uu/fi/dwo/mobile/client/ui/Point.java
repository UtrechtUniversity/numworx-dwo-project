package nl.uu.fi.dwo.mobile.client.ui;

public class Point {
	
	int x; int y;
	
	public Point(int x, int y) {
		this.x = x; this.y = y;
	}
	
	public DoublePoint getDoublePoint() {
		double x = this.x;
		double y = this.y;
		return new DoublePoint(x,y);
	}
}
