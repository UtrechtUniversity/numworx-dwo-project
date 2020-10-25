package fi.dwo.dwojapplet.gui.domainmodel.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;

public class GraphEdge {

	private GraphNode source;
	private GraphNode target;
	private int arrowSize = 5;
	
	private boolean blur = false;
	
	private Color edgeColor = LeerdomeinGraphPanel.colorBlue4;
	
	public GraphEdge(GraphNode source, GraphNode target) {
		this.source = source;
		this.target = target;
	}
	
	public boolean contains(int x, int y) {
		if(target==null || source==null || target.getLocation()==null || source.getLocation()==null)
			return false;
		float a = arrowSize;
		float x0 = source.getLocation().x;
		float x1 = target.getLocation().x;
		float y0 = source.getLocation().y;
		float y1 = target.getLocation().y;
		float mx = (x0 + x1)/2;
		float my = (y0 + y1)/2;
		float dm1 = (float)Math.sqrt((x1-mx)*(x1-mx)+(y1-my)*(y1-my));
		float px = mx+2*a*(x1-mx)/dm1;
		float py = my+2*a*(y1-my)/dm1;
		float qx = mx-a*(y1-my)/dm1;
		float qy = my+a*(x1-mx)/dm1;
		float rx = mx+a*(y1-my)/dm1;
		float ry = my-a*(x1-mx)/dm1;
		Polygon pol = new Polygon();
		pol.addPoint((int)px, (int)py);
		pol.addPoint((int)qx, (int)qy);
		pol.addPoint((int)rx, (int)ry);
		return pol.contains(x, y);
	}
	
	public void paint(Graphics gr, Point origin, double factor) {
		if(target==null || source==null || target.getLocation()==null || source.getLocation()==null)
			return;
		Graphics2D g = (Graphics2D)gr;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		float a = (float)(arrowSize*factor);
		float x0 = origin.x + (float)((source.getLocation().x)*factor);
		float x1 = origin.x + (float)((target.getLocation().x)*factor);
		float y0 = origin.y + (float)((source.getLocation().y)*factor);
		float y1 = origin.y + (float)((target.getLocation().y)*factor);
		float mx = (x0 + x1)/2;
		float my = (y0 + y1)/2;
		float dm1 = (float)Math.sqrt((x1-mx)*(x1-mx)+(y1-my)*(y1-my));
		float px = mx+2*a*(x1-mx)/dm1;
		float py = my+2*a*(y1-my)/dm1;
		float qx = mx-a*(y1-my)/dm1;
		float qy = my+a*(x1-mx)/dm1;
		float rx = mx+a*(y1-my)/dm1;
		float ry = my-a*(x1-mx)/dm1;
		
		g.setPaint(edgeColor);
		if(blur)
			g.setPaint(new Color(edgeColor.getRed(), edgeColor.getGreen(), edgeColor.getBlue(), 30));
		
        g.setStroke(new BasicStroke(1.3f*(float)factor));
		GeneralPath path = new GeneralPath();
		path.moveTo(x0,y0);
		path.lineTo(x1,y1);
		path.closePath();
		g.draw(path);
		
		GeneralPath arrow = new GeneralPath();
		arrow.moveTo(px,py);
		arrow.lineTo(rx,ry);
		arrow.lineTo(qx,qy);
		arrow.lineTo(px,py);
		arrow.closePath();
		g.fill(arrow);
	}
	
	public GraphNode getSource() {
		return source;
	}
	
	public GraphNode getTarget() {
		return target;
	}
	
	public void setBlur(boolean b) {
		blur = b;
	}
	
	public boolean getBlur() {
		return blur;
	}
	
}
