package fi.dwo.dwojapplet.gui.domainmodel.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fi.dwo.dwojapplet.gui.domainmodel.methods.MethodsProperties;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class ChapterGraphNode {
	
	private static final String DASH = " - ";
  public  static Map<String, String> hfstDescriptionsMap;
	private static Color defaultNodeColor = LeerdomeinGraphPanel.colorBlue4;
	private static Color defaultTextColor = new Color(120, 150, 202, 35);
	private static int defaultFontSize = 130;
	private static Font defaultFont = new Font("SansSerif", Font.PLAIN, defaultFontSize);
	
	private Color nodeColor = defaultNodeColor;
	private Color nodeColor2 = new Color(222, 229, 240);
	private Color nodeBorderColor = LeerdomeinGraphPanel.colorBlue2;
	
	private String hfstCode;
	private Point location = new Point(0,0);
	
	private int size = 300;
	
	private FontMetrics fm;
	private Font font = defaultFont;
	
	private ArrayList<GNode> graphNodes = new ArrayList<GNode>();
	private ArrayList<GraphNode> voorkennisNodes = new ArrayList<GraphNode>();
	private ArrayList<GraphEdge> voorkennisEdges = new ArrayList<GraphEdge>();
	
	private boolean visible = true;
	
	public static String getChapterDescription(String hfstCode) {
		return hfstDescriptionsMap.get(hfstCode);
	}
	
	public ChapterGraphNode(String hfstCode, List<GNode> graphNodes2, ArrayList<GraphEdge> graphEdges, PersistenceId activeMethod) {
		if(hfstDescriptionsMap==null) {
			hfstDescriptionsMap = new HashMap<String, String>();
			hfstDescriptionsMap.putAll(MethodsProperties.instance().getDescriptionsMap(activeMethod));
		}
		this.hfstCode = hfstCode;
		makeLocation(graphNodes2);
		setVoorkennis(graphEdges);
	}
	
	public void makeLocation(List<GNode> graphNodes2) {
		int hfstCumX = 0;
		int hfstCumY = 0;
		int hfstCount = 0;
		
		for (GNode node : graphNodes2) {
			if(node.isVisible(hfstCode) && node.hasMethodCode(hfstCode)) {
				final Point loc = node.getLocation(hfstCode);
				if (loc == null) continue; // NPE check
                hfstCumX+=loc.x;
				hfstCumY+=loc.y;
				hfstCount+=1;
				this.graphNodes.add(node);
			}
		}
		
		if(hfstCount>0) {
			location.x = hfstCumX/hfstCount;
			location.y = hfstCumY/hfstCount;
		}
		else {
			location.x = 0;
			location.y = 0;
		}
	}
	
	public boolean hasBookCode(String bookCode) {
		return hfstCode.startsWith(bookCode);
	}
	
	public ArrayList<GNode> getGraphNodes() {
		return graphNodes;
	}
	
	
	
	public void setVoorkennis(ArrayList<GraphEdge> graphEdges) {
		for (GraphEdge edge : graphEdges) {
			if(edge.getTarget().hasChapterCode(hfstCode) && !edge.getSource().hasChapterCode(hfstCode)) {
				if(!voorkennisNodes.contains(edge.getSource()) && edge.getSource().hasMethodCode(hfstCode))
					voorkennisNodes.add((GraphNode) edge.getSource());
				if(!voorkennisEdges.contains(edge))
					voorkennisEdges.add(edge);
			}
		}
		
	}
	
	public ArrayList<GraphNode> getVoorkennisNodes() {
		return voorkennisNodes;
	}
	
	public ArrayList<GraphEdge> getVoorkennisEdges() {
		return voorkennisEdges;
	}
	
	public void paint(Graphics gr, Point origin, double factor) {
		paint(gr, origin, factor, false);
	}
	
	public void paint(Graphics gr, Point origin, double factor, boolean editGraph) {
		if(location==null || location.x==0 && location.y==0)
			return;
		Graphics2D g = (Graphics2D)gr;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		
		
		int size = (int)(this.size*factor);
		int rx = (int)(origin.x+(location.x)*factor);
		int ry = (int)(origin.y+(location.y)*factor);
		
		if(factor<0.15) {
			g.setFont(new Font("SansSerif", Font.PLAIN, (int)(defaultFontSize*factor)));
			FontMetrics fm = g.getFontMetrics();
			g.setColor(nodeColor);
			g.fillOval(rx-size/2, ry-size/2, size, size);
			g.setColor(nodeBorderColor);
			g.setStroke(new BasicStroke(10f*(float)factor));
			g.drawOval(rx-size/2, ry-size/2, size, size);
			String label = hfstDescriptionsMap.get(hfstCode);
			int textLength = fm.stringWidth(label);
			int textHeight = fm.getAscent();
			g.setColor(LeerdomeinGraphPanel.colorBlue1);
			g.drawString(label, rx-textLength/2, ry+textHeight/2);
		}
		else {
			g.setFont(new Font("SansSerif", Font.BOLD,(int)(160*factor)));
			FontMetrics fm = g.getFontMetrics();
			g.setColor(new Color(222, 229, 240));
			if(factor<0.3)
				g.setColor(new Color(212, 219, 239));
			if(factor<0.22)
				g.setColor(new Color(202, 209, 229));
			if(editGraph)
				g.setColor(new Color(233, 239, 249));
			g.fillOval(rx-size/2, ry-size/2, size, size);
			String label = hfstDescriptionsMap.get(hfstCode);
			int dash = label.indexOf(DASH);
			if (dash > 0)
              label = label.substring(0, dash);
			int textLength = fm.stringWidth(label);
			int textHeight = fm.getAscent();
			g.setColor(LeerdomeinGraphPanel.colorGray3);
			if(editGraph)
				g.setColor(Color.white);
			g.drawString(label, rx-textLength/2, ry+textHeight/2);
		}
		
		
		
		
		
	}	
	
//	private Color getNodeColor(double factor) {
//		if(factor<0.15)
//			return nodeColor;
//		if(factor>0.3)
//			return nodeColor2;
//		else
//			return
//	}
	
	public Point getLocation() {
		return location;
	}
	
	public boolean contains(int x, int y) {
		if(location==null || location.x==0 && location.y==0)
			return false;
		Rectangle r = new Rectangle(location.x-size/2, location.y-size/2, size, size);
		return r.contains(x,y);
	}
	
	public String getHfstCode() {
		return hfstCode;
	}
	
	public String getHfstDescription() {
		return hfstDescriptionsMap.get(hfstCode);
	}
	
	public String getMethodCode() {
		return hfstCode.substring(0,hfstCode.indexOf("-"));
	}
	
	public String getBookCode() {
		return hfstCode.substring(0,hfstCode.lastIndexOf("-"));
	}
	
	public String getBookDescription() {
		return BookGraphNode.bookDescriptionsMap.get(getBookCode());
	}
	
	public boolean isVisible() {
		return visible;
	}

}
