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
	private boolean sameChapters = true;
	
	private Color edgeColor = LeerdomeinGraphPanel.colorBlue4;
	private Color edgeInterChapColor = new Color(238,209,180);//230,210,210);
	
	public GraphEdge(GraphNode source, GraphNode target) {
		this.source = source;
		this.target = target;
		sameChapters = GraphNode.hasSameChapterCode(source, target, "Getal&Ruimte");
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
		if(target==null || source==null || target.getLocation()==null || source.getLocation()==null  || !target.isVisible() || !source.isVisible() ||factor<0.15 
				||source.getTempLocation()!=null && target.getTempLocation()!=null)
			return;
		Graphics2D g = (Graphics2D)gr;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		float a = (float)(arrowSize*factor);
		float x0 = origin.x + (float)((source.getLocation().x)*factor);
		float x1 = origin.x + (float)((target.getLocation().x)*factor);
		float y0 = origin.y + (float)((source.getLocation().y)*factor);
		float y1 = origin.y + (float)((target.getLocation().y)*factor);
		if(source.getTempLocation()!=null) {
			x0 = source.getTempLocation().x;
			y0 = source.getTempLocation().y;
		}
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
		if(target.getSuccesFailScore()!=null)
			g.setPaint(target.getEdgeSuccesFailColor());
//		if((target.getSuccesFailScore()==null || target.getSuccesFailScore() < 45)) // &&  source.getSuccesFailScore()!=null && source.getSuccesFailScore() < 45)
//			g.setPaint(source.getSuccesFailColor());
		if(!sameChapters && target.getMethodeInfo()!=null)
			g.setPaint(edgeInterChapColor);
		if(blur)
			g.setPaint(new Color(g.getColor().getRed(), g.getColor().getGreen(), g.getColor().getBlue(), 30));
		
        g.setStroke(new BasicStroke(1.3f*(float)factor));
        if(target.getSuccesFailScore()!=null && target.getEdgeSuccesFailColor()!=null)// ||  source.getSuccesFailScore()!=null && source.getSuccesFailScore() < 45)
       		g.setStroke(new BasicStroke(5f*(float)factor));
//        if(getLength()>600 || source.getTempLocation()!=null) {
//        		g.setStroke(new BasicStroke(1.3f*(float)factor, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5,5}, 5));
//        		if(target.getSuccesFailScore()!=null && target.getEdgeSuccesFailColor()!=null)
//        			g.setStroke(new BasicStroke(5f*(float)factor, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5,5}, 5));
//        		
//        }
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
	
	public int getLength() {
		if(target==null || source==null || target.getLocation()==null || source.getLocation()==null)
			return 0;
		float x0 = source.getLocation().x;
		float y0 = source.getLocation().y;
		float x1 = target.getLocation().x;
		float y1 = target.getLocation().y;
		return (int)Math.sqrt((x1-x0)*(x1-x0)+(y1-y0)*(y1-y0));
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
