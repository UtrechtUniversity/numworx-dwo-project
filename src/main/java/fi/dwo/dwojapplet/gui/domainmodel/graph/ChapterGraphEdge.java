package fi.dwo.dwojapplet.gui.domainmodel.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;

public class ChapterGraphEdge {

	private ChapterGraphNode source;
	private ChapterGraphNode target;
	private int arrowSize = 50;
	
	private Color edgeColor = new Color(222, 229, 240);
	
	public ChapterGraphEdge(ChapterGraphNode source, ChapterGraphNode target) {
		this.source = source;
		this.target = target;
	}
	
	public void paint(Graphics gr, Point origin, double factor) {
		paint(gr, origin, factor, false);
	}
	
	public void paint(Graphics gr, Point origin, double factor, boolean editGraph) {
		if(target==null || source==null || target.getLocation()==null || source.getLocation()==null || source.getLocation().x==0 && source.getLocation().x==0 || target.getLocation().x==0 && target.getLocation().x==0)
			return;
		Graphics2D g = (Graphics2D)gr;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		float a = (float)(arrowSize*factor);
		float x0 = origin.x + (float)((source.getLocation().x)*factor);
		float x1 = origin.x + (float)((target.getLocation().x)*factor);
		float y0 = origin.y + (float)((source.getLocation().y)*factor);
		float y1 = origin.y + (float)((target.getLocation().y)*factor);
//		if(source.getTempLocation()!=null) {
//			x0 = source.getTempLocation().x;
//			y0 = source.getTempLocation().y;
//		}
		float mx = (x0 + x1)/2;
		float my = (y0 + y1)/2;
		float dm1 = (float)Math.sqrt((x1-mx)*(x1-mx)+(y1-my)*(y1-my));
		float px = mx+2*a*(x1-mx)/dm1;
		float py = my+2*a*(y1-my)/dm1;
		float qx = mx-a*(y1-my)/dm1;
		float qy = my+a*(x1-mx)/dm1;
		float rx = mx+a*(y1-my)/dm1;
		float ry = my-a*(x1-mx)/dm1;
		
		float x00 = x0+3*a*(x1-mx)/dm1;
		float y00 = y0+3*a*(y1-my)/dm1;
		float x11 = x1-3*a*(x1-mx)/dm1;
		float y11 = y1-3*a*(y1-my)/dm1;
		
		if(factor<0.15)
			g.setPaint(new Color(120, 150, 202));
		else if(factor<0.22)
			g.setColor(new Color(202, 209, 229));
		else if(factor<0.3)
			g.setColor(new Color(212, 219, 239));
		else 
			g.setPaint(edgeColor);
		
		if(factor>=0.15 && editGraph)
			g.setPaint(new Color(233, 239, 249));
		g.setStroke(new BasicStroke(25f*(float)factor));
//		if(target.getSuccesFailScore()!=null)
//			g.setPaint(target.getEdgeSuccesFailColor());
//		if(!sameChapters)
//			g.setPaint(edgeInterChapColor);
//		if(blur)
//			g.setPaint(new Color(g.getColor().getRed(), g.getColor().getGreen(), g.getColor().getBlue(), 30));
//		
//        g.setStroke(new BasicStroke(1.3f*(float)factor));
//        if(target.getSuccesFailScore()!=null && target.getEdgeSuccesFailColor()!=null)// ||  source.getSuccesFailScore()!=null && source.getSuccesFailScore() < 45)
//       		g.setStroke(new BasicStroke(5f*(float)factor));
		GeneralPath path = new GeneralPath();
		path.moveTo(x00,y00);
		path.lineTo(x11,y11);
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
	
	public ChapterGraphNode getSource() {
		return source;
	}
	
	public ChapterGraphNode getTarget() {
		return target;
	}
}
