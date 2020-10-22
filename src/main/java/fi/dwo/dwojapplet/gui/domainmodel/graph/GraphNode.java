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
	
	private static Color defaultNodeColor = LeerdomeinGraphPanel.colorBlue3;
	private static Color defaultTextColor = LeerdomeinGraphPanel.colorBlue1;
	private static Font defaultFont = new Font("SansSerif", Font.PLAIN, 16);
	
	private FontMetrics fm;
	private Font font = defaultFont;
	
	private String ID;
	private String subdomein;
	private String description;
	//private String label;
	private Point location;
	private int size = 12;
	private Color nodeColor = defaultNodeColor;
	private Color textColor = defaultTextColor;
	
	private int textLength;
	private int textHeight;
	
	private boolean blur;
	
	public GraphNode(String ID, String subdomein, String description) {
		this.ID = ID;
		this.subdomein = subdomein;
		this.description = description;
		setFont(defaultFont);
	}
	
	public GraphNode(String ID, String subdomein, String description, int x, int y) {
		this.ID = ID;
		this.subdomein = subdomein;
		this.description = description;
		location = new Point(x,y);
		setFont(defaultFont);
	}
	
	public GraphNode(int x, int y) {
		this.ID = "0";
		this.subdomein = "";
		this.description = "";
		location = new Point(x,y);
		setFont(defaultFont);
	}
	
	public String getID() {
		return ID;
	}
	
	public Point getLocation() {
		return location;
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
	
	public void paint(Graphics gr) {
		if(location==null)
			return;
		Graphics2D g = (Graphics2D)gr;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if(fm==null)
			fm = g.getFontMetrics();
		
			
		String space = "";
		if(subdomein!=null && !"".equals(subdomein))
			space = " - ";
		String label = this.subdomein + space + this.description;
		int x = location.x;
		int y = location.y;
		textLength = fm.stringWidth(label);
		textHeight = fm.getAscent();
		
		
		g.setColor(nodeColor);
		if(blur)
			g.setColor(new Color(nodeColor.getRed(), nodeColor.getGreen(), nodeColor.getBlue(), 30));
		g.fillOval(x-size/2, y-size/2, size, size);
		g.setColor(textColor);
		if(blur)
			g.setColor(new Color(textColor.getRed(), textColor.getGreen(), textColor.getBlue(), 30));
		g.drawOval(x-size/2, y-size/2, size, size);
		g.drawString(label, x-textLength/2, y-textHeight);
	}
	
	public Rectangle getTextBB() {
		return new Rectangle(location.x-textLength/2 , location.y-2*textHeight+3 , textLength, textHeight);
	}
	
	public void setBlur(boolean b) {
		blur = b;
	}
	
	public boolean getBlur() {
		return blur;
	}
}
