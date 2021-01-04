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
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GraphNode {
	
	private static Color defaultNodeColor = LeerdomeinGraphPanel.colorBlue4;
	private static Color defaultTextColor = LeerdomeinGraphPanel.colorBlue1;
	private static int defaultFontSize = 16;
	private static Font defaultFont = new Font("SansSerif", Font.PLAIN, defaultFontSize);
	
	private FontMetrics fm;
	private Font font = defaultFont;
	
	private String ID;
	private String subdomein;
	private String description;
	//private String label;
	private Point location;
	private Point tempLocation;
	private int size = 16;
	private Color nodeColor = defaultNodeColor;
	private Color nodeBorderColor = LeerdomeinGraphPanel.colorBlue2;
	private Color textColor = defaultTextColor;
	
	private Map<String, Map<String,Set<Integer>>> methodeInfo;
	//private Map<String, String> methodeInfoString;
	private ArrayList<String> methodeCodes;
	
	private int textLength;
	private int textHeight;
	
	private boolean blur;
	private boolean selected;
	
	private Double succesFailScore = null;
	
	private Color succesColor = new Color(0,200,0);
	private Color halfSuccesColor = new Color(180,240,180);
	private Color failColor = new Color(200,0,0);
	private Color halfFailColor = new Color(255,150,150);
	
	private boolean visible = true;
	
	
	public GraphNode(String ID, String subdomein, String description) {
		this.ID = ID;
		this.subdomein = subdomein;
		this.description = description;
		//setFont(defaultFont);
	}
	
	public GraphNode(String ID, String subdomein, String description, int x, int y) {
		this.ID = ID;
		this.subdomein = subdomein;
		this.description = description;
		location = new Point(x,y);
		//setFont(defaultFont);
	}
	
	public GraphNode(String ID, String subdomein, String description, Point p) {
		this.ID = ID;
		this.subdomein = subdomein;
		this.description = description;
		location = p;
		//setFont(defaultFont);
	}
	
	public GraphNode(int x, int y) {
		this.ID = "0";
		this.subdomein = "";
		this.description = "";
		location = new Point(x,y);
		//setFont(defaultFont);
	}
	
//	public GraphNode(Point p) {
//		this.ID = "0";
//		this.subdomein = "";
//		this.description = "";
//		location = new Point(p);
//		//setFont(defaultFont);
//	}
	
	public void setSuccesFailScore (Double succesFailScore) {
		this.succesFailScore = succesFailScore;
	}
	
	public Double getSuccesFailScore() {
		return succesFailScore;
	}
	
	public String getID() {
		return ID;
	}
	
	public Point getLocation() {
		return location; 
	}
	
	public Point getTempLocation() {
		return tempLocation;
	}
	
	public Point getLocationOnPanel(Point origin, double factor) {
		if(location==null)
			return null;
		int x = origin.x + (int)((location.x)*factor);
		int y = origin.y + (int)((location.y )*factor);
		return new Point(x,y);
	}
	
	public Color getSuccesFailColor() {
		if(succesFailScore == null)
			return nodeColor;
		if(succesFailScore < 25) 
			return failColor;
		if(succesFailScore <= 45) 
			return halfFailColor;
		if(succesFailScore > 45 && succesFailScore < 55)
			return nodeColor;
		if(succesFailScore < 75 && succesFailScore >= 55) 
			return halfSuccesColor;
		else 
			return succesColor;
		
	}
	
	public Color getEdgeSuccesFailColor() {
		if(succesFailScore == null)
			return null;
		if(succesFailScore < 25) 
			return null;
		if(succesFailScore <= 45) 
			return null;
		if(succesFailScore > 45 && succesFailScore < 55)
			return null;
		if(succesFailScore < 75 && succesFailScore >= 55) 
			return halfSuccesColor;
		else 
			return succesColor;
		
	}
	
	public String getSubdomein() {
		return subdomein;
	}
	
	public String getDescription() {
		return description;
	}
	
	public static boolean hasSameChapterCode(GraphNode node1, GraphNode node2) {
		return hasSameChapterCode(node1, node2, null);
	}
	
	public static boolean hasSameChapterCode(GraphNode node1, GraphNode node2, String methode) {
		Map<String, Map<String,Set<Integer>>> info1 = node1.getMethodeInfo();
		Map<String, Map<String,Set<Integer>>> info2 = node2.getMethodeInfo();
		if(info1==null || info2==null)
			return false;

		
		Set<String> infoset = new HashSet<>(info1.keySet());
		infoset.retainAll(info2.keySet()); // retainall == doorsnede
		if (methode != null) infoset.retainAll(Collections.singleton(methode));
		for(String methodeName : infoset) {
		  Map<String,Set<Integer>> leerjaren1 = info1.get(methodeName);
		  Map<String,Set<Integer>> leerjaren2 = info2.get(methodeName);
		  Set<String> leerjarenset = new HashSet<>(leerjaren1.keySet());
		  leerjarenset.retainAll(leerjaren2.keySet());
		  for(String leerjaarName: leerjarenset) {
            Set<Integer> hoofdstukken1 = leerjaren1.get(leerjaarName);
            Set<Integer> hoofdstukken2 = leerjaren2.get(leerjaarName);
            if (hoofdstukken1.stream().anyMatch(hoofdstukken2::contains)) return true;
		  }		  
		}
		
		
//		for (String methodeName1 : info1.keySet()) {
//			if(methode != null && methodeName1.equals(methode)) {
//				Map<String,Set<Integer>> leerjaren1 = info1.get(methodeName1);
//				if(info2.containsKey(methodeName1)) {
//					Map<String,Set<Integer>> leerjaren2 = info2.get(methodeName1);
//					for (String leerjaarName1 : leerjaren1.keySet()){
//						if(leerjaren2.containsKey(leerjaarName1)) {
//							Set<Integer> hoofdstukken1 = leerjaren1.get(leerjaarName1);
//							Set<Integer> hoofdstukken2 = leerjaren2.get(leerjaarName1);
//							for (Integer i1 : hoofdstukken1){
//								for (Integer i2 : hoofdstukken2){
//									if(i1.intValue() == i2.intValue())
//										return true;
//								}
//							}
//						}
//					}
//				}
//			}
//		}
		return false;
	}
	

	public void setLocation(int x, int y) {
		location = new Point(x,y);
	}
	
	public void setLocation(Point p) {
		location = p;
	}
	
	public Map<String, Map<String,Set<Integer>>> getMethodeInfo() {
		return methodeInfo;
	}
	
	public void setMethodeInfo(Map<String, Map<String,Set<Integer>>> methodeInfo) {
		this.methodeInfo = methodeInfo;
		
		this.methodeCodes = new ArrayList<String>();
		for (String methodeName : methodeInfo.keySet()) {
			Map<String,Set<Integer>> leerjaren = methodeInfo.get(methodeName);
			for (String leerjaarName : leerjaren.keySet()){
				Set<Integer> hoofdstukken = 	leerjaren.get(leerjaarName);
				for (Integer i : hoofdstukken){
					methodeCodes.add(methodeName + "-" + leerjaarName + "-" + i);
				}
			}
		}
	}
	
	public boolean hasMethodCode(String code) {
		if(methodeCodes==null || code == null)
			return false;
		return methodeCodes.contains(code);
	}
	public boolean hasBookCode(String code) {
		if(methodeCodes==null)
			return false;
		for(String methodeCode : methodeCodes) {
			if(code!=null && code.equals(methodeCode.substring(0,methodeCode.lastIndexOf("-"))))
				return true;
		}
		return false;
	}
	
	public ArrayList<String> getMethodeCodes() {
		return methodeCodes;
	}
	
	public void setTempLocation(Point p) {
		tempLocation = p;
	}
	
	public Font getFont() {
		return font;
	}
	public void setFont(Font font) {
		this.font = font;
	}
	
	public boolean contains(int x, int y) {
		if(location==null)
			return false;
		Rectangle r = new Rectangle(location.x-size/2, location.y-size/2, size, size);
		if(tempLocation!=null)
			r = new Rectangle(tempLocation.x-size/2, tempLocation.y-size/2, size, size);
		return r.contains(x,y);
	}
	
	public void paint(Graphics gr, Point origin, double factor) {
		if(!visible || location==null || factor<0.15)
			return;
		Graphics2D g = (Graphics2D)gr;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		//g.setFont(defaultFont.deriveFont((int)(defaultFontSize*factor)));
		g.setFont(new Font("SansSerif", Font.PLAIN, (int)(defaultFontSize*factor)));
		if(selected)
			g.setFont(new Font("SansSerif", Font.BOLD, (int)(defaultFontSize*factor)));
		fm = g.getFontMetrics();
			
		String space = "";
		if(subdomein!=null && !"".equals(subdomein))
			space = " - ";
		String label = this.subdomein + space + this.description;
		int x = origin.x + (int)((location.x)*factor);
		int y = origin.y + (int)((location.y )*factor);
		if(tempLocation!=null) {
			x = tempLocation.x;
			y = tempLocation.y;
		}
		
		textLength = fm.stringWidth(label);
		textHeight = fm.getAscent();
		
		int size = (int)(this.size*factor);
		
		g.setColor(nodeColor);
		if(succesFailScore!=null) {
			g.setColor(getSuccesFailColor());
			if(!nodeColor.equals(getSuccesFailColor())) {
				g.setColor(new Color(g.getColor().getRed(), g.getColor().getGreen(), g.getColor().getBlue(), 60));
				if(blur)
					g.setColor(new Color(g.getColor().getRed(), g.getColor().getGreen(), g.getColor().getBlue(), 10));
				g.fillOval(x-3*size/2, y-3*size/2+textHeight/6, 3*size, 3*size);
				g.setColor(getSuccesFailColor());
			}
		}
		if(blur)
			g.setColor(new Color(g.getColor().getRed(), g.getColor().getGreen(), g.getColor().getBlue(), 30));
		g.fillOval(x-size/2, y-size/2+textHeight/6, size, size);
		
		g.setColor(nodeBorderColor);
		g.setStroke(new BasicStroke(2f*(float)factor));
		if(blur)
			g.setColor(new Color(g.getColor().getRed(), g.getColor().getGreen(), g.getColor().getBlue(), 30));
		if(selected) {
			g.setColor(textColor);
			g.drawOval(x-size/2-1, y-size/2+textHeight/6-1, size+2, size+2);
		}
		g.drawOval(x-size/2, y-size/2+textHeight/6, size, size);
		
		g.setColor(textColor);
		if(blur)
			g.setColor(new Color(textColor.getRed(), textColor.getGreen(), textColor.getBlue(), 30));
		g.drawString(label, x-textLength/2, y+textHeight/2);
	}
	
	public Rectangle getTextBB() {
		if(location==null)
			return new Rectangle(0,0,0,0);
//		if(tempLocation != null) {
//			return new Rectangle(tempLocation.x-textLength/2 , tempLocation.y-textHeight/2+3 , textLength, textHeight);
//		}
		return new Rectangle(location.x-textLength/2 , location.y-textHeight/2+3 , textLength, textHeight);
	}
	
	public void setBlur(boolean b) {
		blur = b;
	}
	
	public void setSelected(boolean b) {
		selected = b;
	}
	
	public boolean getBlur() {
		return blur;
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setVisible(boolean b) {
		visible = b;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
}
