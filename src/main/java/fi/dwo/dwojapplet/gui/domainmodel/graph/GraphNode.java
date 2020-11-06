package fi.dwo.dwojapplet.gui.domainmodel.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;

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
	
	private int textLength;
	private int textHeight;
	
	private boolean blur;
	private boolean selected;
	
	private Double succesFailScore = null;
	
	private Color succesColor = new Color(0,200,0);
	private Color halfSuccesColor = new Color(180,240,180);
	private Color failColor = new Color(200,0,0);
	private Color halfFailColor = new Color(255,150,150);
	
	
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
	
	public GraphNode(int x, int y) {
		this.ID = "0";
		this.subdomein = "";
		this.description = "";
		location = new Point(x,y);
		//setFont(defaultFont);
	}
	
	public void setSuccesFailScore (double succesFailScore) {
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
		int x = origin.x + (int)((location.x)*factor);
		int y = origin.y + (int)((location.y )*factor);
		return new Point(x,y);
	}
	
	public Color getSuccesFailColor() {
		if(succesFailScore == null)
			return new Color(255,255,255,0);
		if(succesFailScore < 25) 
			return failColor;
		if(succesFailScore <= 45) 
			return halfFailColor;
		if(succesFailScore > 45 && succesFailScore < 55)
			return new Color(255,255,255,0);
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
	

	public void setLocation(int x, int y) {
		location = new Point(x,y);
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
		Rectangle r = new Rectangle(location.x-size/2, location.y-size/2, size, size);
		return r.contains(x,y);
	}
	
	public void paint(Graphics gr, Point origin, double factor) {
		if(location==null)
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
		if(succesFailScore!=null)
			g.setColor(getSuccesFailColor());
		if(blur)
			g.setColor(new Color(g.getColor().getRed(), g.getColor().getGreen(), g.getColor().getBlue(), 30));
		g.fillOval(x-size/2, y-size/2+textHeight/6, size, size);
		g.setColor(nodeBorderColor);
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
}
