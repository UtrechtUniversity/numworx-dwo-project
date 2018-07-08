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
	
	public boolean contains(DoubleRectangle r) {
		return (r.x >= x) && (r.x+r.width <= (x + width)) &&
			       (r.y >= y) && (r.y+r.height <= (y + height));
	}
	
	public void translate(double dx, double dy) {
		this.x += dx;
		this.y += dy;
	}
	
	public void scale(double cx, double cy, double factor) {
		x = cx+(x-cx)*factor;
		y = cy+(y-cy)*factor;
		width*=factor;
		height*=factor;
	}
	
	public double getDiagonal() {
		return Math.sqrt(width*width+height*height);
	}
}

