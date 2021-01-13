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
import java.util.Map;
import java.util.Set;

public class ChapterGraphNode {
	
	private static HashMap<String, String> hfstDescriptionsMap;
	public static String[] hfstCodes = {
			"Getal&Ruimte-1HV-1",
			"Getal&Ruimte-1HV-2",
			"Getal&Ruimte-1HV-3",
			"Getal&Ruimte-1HV-4",
			"Getal&Ruimte-1HV-5",
			"Getal&Ruimte-1HV-6",
			"Getal&Ruimte-1HV-7",
			"Getal&Ruimte-1HV-8",
			"Getal&Ruimte-1HV-9",
			"Getal&Ruimte-2HV-1",
			"Getal&Ruimte-2HV-2",
			"Getal&Ruimte-2HV-3",
			"Getal&Ruimte-2HV-4",
			"Getal&Ruimte-2HV-5",
			"Getal&Ruimte-2HV-6",
			"Getal&Ruimte-2HV-7",
			"Getal&Ruimte-2HV-8",
			"Getal&Ruimte-3V-1",
			"Getal&Ruimte-3V-2",
			"Getal&Ruimte-3V-3",
			"Getal&Ruimte-3V-4",
			"Getal&Ruimte-3V-5",
			"Getal&Ruimte-3V-6",
			"Getal&Ruimte-3V-7",
			"Getal&Ruimte-3V-8",
			"Getal&Ruimte-3V-9"
	};
	public static String[] hfstDescriptions = {
			"H1 - Figuren",
			"H2 - Getallen en formules",
			"H3 - Assenstelsels en grafieken",
			"H4 - Hoeken en symmetrie",
			"H5 - Rekenen",
			"H6 - Formules en letters",
			"H7 - Vlakke figuren",
			"H8 - Herleiden en machten",
			"H9 - Meten",
			"H1 - Rekenen met letters",
			"H2 - Vlakke meetkunde",
			"H3 - Lineaire formules en vergelijkingen",
			"H4 - Kwadraten en wortels",
			"H5 - De stelling van Pythagoras",
			"H6 - Procenten en diagrammen",
			"H7 - Kwadratische vergelijkingen",
			"H8 - Inhoud en vergroten",
			"H1 - Lineaire problemen",
			"H2 - Gelijkvormigheid",
			"H3 - Kwadratische problemen",
			"H4 - Statistiek en procenten",
			"H5 - Vergelijkingen en ongelijkheden",
			"H6 - Vaardigheden en vergelijkingen",
			"H7 - Goniometrie",
			"H8 - Allerlei verbanden",
			"H9 - Spreiding tellen en kans"
			
	};
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
	
	private ArrayList<GraphNode> graphNodes = new ArrayList<GraphNode>();
	private ArrayList<GraphNode> voorkennisNodes = new ArrayList<GraphNode>();
	private ArrayList<GraphEdge> voorkennisEdges = new ArrayList<GraphEdge>();
	
	private boolean visible = true;
	
	public static String getChapterDescription(String hfstCode) {
		return hfstDescriptionsMap.get(hfstCode);
	}
	
	public ChapterGraphNode(String hfstCode, ArrayList<GraphNode> graphNodes, ArrayList<GraphEdge> graphEdges) {
		if(hfstDescriptionsMap==null) {
			hfstDescriptionsMap = new HashMap<String, String>();
			for(int i=0 ; i<hfstCodes.length ; i++) {
				hfstDescriptionsMap.put(hfstCodes[i], hfstDescriptions[i]);
			}
		}
		this.hfstCode = hfstCode;
		makeLocation(graphNodes);
		setVoorkennis(graphEdges);
	}
	
	public void makeLocation(ArrayList<GraphNode> graphNodes) {
		int hfstCumX = 0;
		int hfstCumY = 0;
		int hfstCount = 0;
		
		for (GraphNode node : graphNodes) {
			if(node.isVisible() && node.hasMethodCode(hfstCode)) {
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
		return hfstCode.contains(bookCode);
	}
	
	
	
	public void setVoorkennis(ArrayList<GraphEdge> graphEdges) {
		for (GraphEdge edge : graphEdges) {
			if(edge.getTarget().hasMethodCode(hfstCode) && !edge.getSource().hasMethodCode(hfstCode)) {
				if(!voorkennisNodes.contains(edge.getSource()))
					voorkennisNodes.add(edge.getSource());
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
			label = label.substring(0, label.indexOf(" - "));
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
