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

public class BookGraphNode {
	
	public static HashMap<String, String> bookDescriptionsMap;
	public static String[] bookCodes = {
			"Getal&Ruimte-1HV",
			"Getal&Ruimte-2HV",
			"Getal&Ruimte-3V"
			
	};
	public static String[] bookDescriptions = {
			"1HV",
			"2HV",
			"3V"
	};
	private static Color defaultNodeColor = LeerdomeinGraphPanel.colorBlue4;
	private static Color defaultTextColor = new Color(120, 150, 202, 35);
	private static int defaultFontSize = 640;
	private static Font defaultFont = new Font("SansSerif", Font.PLAIN, defaultFontSize);
	
	private Color nodeColor = defaultNodeColor;
	private Color nodeBorderColor = LeerdomeinGraphPanel.colorBlue2;
	
	private String bookCode;
	private Point location = new Point(0,0);
	
	private int size = 1400;
	
	private FontMetrics fm;
	private Font font = defaultFont;
	
	private ArrayList<ChapterGraphNode> chapterNodes = new ArrayList<ChapterGraphNode>();
	
	public static String getBookDescription(String bookCode) {
		return bookDescriptionsMap.get(bookCode);
	}
	
	public BookGraphNode(String bookCode, ArrayList<ChapterGraphNode> chapterGraphNodes, ArrayList<GraphEdge> ChapterGraphEdges) {
		if(bookDescriptionsMap==null) {
			bookDescriptionsMap = new HashMap<String, String>();
			for(int i=0 ; i<bookCodes.length ; i++) {
				bookDescriptionsMap.put(bookCodes[i], bookDescriptions[i]);
			}
		}
		this.bookCode = bookCode;
		makeLocation(chapterGraphNodes);
	}
	
	public void makeLocation(ArrayList<ChapterGraphNode> chapterNodes) {
		int hfstCumX = 0;
		int hfstCumY = 0;
		int hfstCount = 0;
		
		for (ChapterGraphNode node : chapterNodes) {
			System.out.println("hasBookCode "+bookCode +" "+node.hasBookCode(bookCode));
			if(node.isVisible() && node.hasBookCode(bookCode)) {
				hfstCumX+=node.getLocation().x;
				hfstCumY+=node.getLocation().y;
				hfstCount+=1;
				this.chapterNodes.add(node);
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
	
	
	
	public void setVoorkennis(ArrayList<ChapterGraphEdge> chapterGraphEdges) {
//		for (ChapterGraphEdge edge : chapterGraphEdges) {
//			if(edge.getTarget().hasMethodCode(bookCode) && !edge.getSource().hasMethodCode(bookCode)) {
//				if(!voorkennisNodes.contains(edge.getSource()))
//					voorkennisNodes.add(edge.getSource());
//				if(!voorkennisEdges.contains(edge))
//					voorkennisEdges.add(edge);
//			}
//		}
		
	}
	
//	public ArrayList<GraphNode> getVoorkennisNodes() {
//		return voorkennisNodes;
//	}
//	
//	public ArrayList<GraphEdge> getVoorkennisEdges() {
//		return voorkennisEdges;
//	}
	
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
		
		if(factor<0.05) {
			g.setFont(new Font("SansSerif", Font.PLAIN, (int)(defaultFontSize*factor)));
			FontMetrics fm = g.getFontMetrics();
			g.setColor(nodeColor);
			g.fillOval(rx-size/2, ry-size/2, size, size);
			g.setColor(nodeBorderColor);
			g.drawOval(rx-size/2, ry-size/2, size, size);
			String label = bookDescriptionsMap.get(bookCode);
			int textLength = fm.stringWidth(label);
			int textHeight = fm.getAscent();
			g.setColor(LeerdomeinGraphPanel.colorBlue1);
			g.drawString(label, rx-textLength/2, ry+textHeight/2);
		}
		else {
			g.setFont(new Font("SansSerif", Font.BOLD,(int)(640*factor)));
			FontMetrics fm = g.getFontMetrics();
			g.setColor(new Color(222, 229, 240));
			if(editGraph)
				g.setColor(new Color(233, 239, 249));
			g.fillOval(rx-size/2, ry-size/2, size, size);
			String label = bookDescriptionsMap.get(bookCode);
			label = label.substring(label.indexOf(" ")+1);
			int textLength = fm.stringWidth(label);
			int textHeight = fm.getAscent();
			g.setColor(LeerdomeinGraphPanel.colorGray3);
			if(editGraph)
				g.setColor(Color.white);
			g.drawString(label, rx-textLength/2, ry+textHeight/2);
		}
		
		
		
		
		
	}	
	
	public Point getLocation() {
		return location;
	}
	
	public boolean contains(int x, int y) {
		if(location==null)
			return false;
		Rectangle r = new Rectangle(location.x-size/2, location.y-size/2, size, size);
		return r.contains(x,y);
	}
	
	public String getBookCode() {
		return bookCode;
	}
	
	public String getBookDescription() {
		return bookDescriptionsMap.get(bookCode);
	}

}
