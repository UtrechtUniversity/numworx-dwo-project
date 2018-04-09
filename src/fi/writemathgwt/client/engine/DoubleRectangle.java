package fi.writemathgwt.client.engine;

public class DoubleRectangle {

	public double x, y, width, height;
	
	public DoubleRectangle(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public boolean contains(double px, double py) {
		return (px >= x) && (px <= (x + width)) &&
			       (py >= y) && (py <= (y + height));
	}
	
	public boolean contains(DoublePoint p) {
		return (p.x >= x) && (p.x <= (x + width)) &&
			       (p.y >= y) && (p.y <= (y + height));
	}
}

