package fi.writemathgwt.client.engine;

public class DoubleRectangle {

	public double x, y, width, height;
	
	public DoubleRectangle(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public DoubleRectangle(DoubleRectangle r1) {
		this.x = r1.x;
		this.y = r1.y;
		this.width = r1.width;
		this.height = r1.height;
	}
	
	public DoubleRectangle(DoubleRectangle r1, DoubleRectangle r2) {
		double xLeft = Math.min(r1.x, r2.x);
		double yTop = Math.min(r1.y, r2.y);
		double xRight = Math.max(r1.x+r1.width, r2.x+r2.width);
		double yBottom = Math.max(r1.y+r1.height,r2.y+r2.height);
		this.x = xLeft;
		this.y = yTop;
		this.width = xRight - xLeft;
		this.height = yBottom - yTop;
	}
	
	public DoubleRectangle(DoubleRectangle r1, DoubleRectangle r2, DoubleRectangle r3) {
		double xLeft = Math.min(r1.x, Math.min(r2.x, r3.x));
		double yTop = Math.min(r1.y, Math.min(r2.y, r3.y));
		double xRight = Math.max(r1.x+r1.width, Math.max(r2.x+r2.width , r3.x+r3.width));
		double yBottom = Math.max(r1.y+r1.height, Math.max(r2.y+r2.height , r3.y+r3.height));
		this.x = xLeft;
		this.y = yTop;
		this.width = xRight - xLeft;
		this.height = yBottom - yTop;
	}
	
	
	public boolean contains(double px, double py) {
		return (px >= x) && (px <= (x + width)) &&
			       (py >= y) && (py <= (y + height));
	}
	
	public boolean contains(DoublePoint p) {
		return (p.x >= x) && (p.x <= (x + width)) &&
			       (p.y >= y) && (p.y <= (y + height));
	}
	
	public boolean contains(DoubleRectangle r, double marginX, double marginY) {
		return (r.x - x > marginX) && ( (x + width) - (r.x+r.width) > marginX) &&
			       (r.y - y > marginY) && ((y + height) - (r.y+r.height) > marginY);
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

