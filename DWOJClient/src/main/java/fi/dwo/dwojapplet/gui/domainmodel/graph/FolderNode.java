package fi.dwo.dwojapplet.gui.domainmodel.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelMethodInfo;

public class FolderNode extends AbstractNode implements GNode {


	
	private static final Rectangle EMPTY_RECT = new Rectangle();
	private static final double SIZE = 2.0;
	List<Point> hull = Collections.emptyList();
	final Collection<GNode> folder = new ArrayList<>();
	boolean collaps;
	Shape last = EMPTY_RECT;
	
	@Override
	public void paint(Graphics gr, Point origin, double factor, boolean connectInstances) {
		if (!isVisible() || factor < 0.15)
			return;
		Graphics2D g = (Graphics2D) gr;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		boolean nodeInstancesOverlap = false;

		for (String code : getMethodeCodes()) {
			if (visible.contains(code)) {
				Point location = getLocation(code);
				if (location == null) continue;
				// g.setFont(defaultFont.deriveFont((int)(defaultFontSize*factor)));
				g.setFont(new Font("SansSerif", Font.PLAIN, (int) (defaultFontSize * SIZE * factor )));
				if (selected.contains(code))
					g.setFont(new Font("SansSerif", Font.BOLD, (int) (defaultFontSize * SIZE * factor)));
				fm = g.getFontMetrics();

				String space = "";
				if (subdomein != null && !"".equals(subdomein))
					space = " - ";
				String label = this.subdomein + space + this.description;
				int x = origin.x + (int) ((location.x) * factor);
				int y = origin.y + (int) ((location.y) * factor);
				if (tempLocation != null) {
					x = tempLocation.x;
					y = tempLocation.y;
				}

				textLength = fm.stringWidth(label);
				textHeight = fm.getAscent();

				int size = (int) (this.size * factor);

				g.setColor(nodeColor);
				if (nodeInstancesOverlap)
					g.setColor(Color.red);
				if (succesFailScore != null) {
					g.setColor(getSuccesFailColor());
					if (!nodeColor.equals(getSuccesFailColor())) {
						g.setColor(
								new Color(g.getColor().getRed(), g.getColor().getGreen(), g.getColor().getBlue(), 60));
						if (blur)
							g.setColor(new Color(g.getColor().getRed(), g.getColor().getGreen(), g.getColor().getBlue(),
									10));
						g.fillOval(x - 3 * size / 2, y - 3 * size / 2 + textHeight / 6, 3 * size, 3 * size);
						g.setColor(getSuccesFailColor());
					}
				}
				if (blur)
					g.setColor(new Color(g.getColor().getRed(), g.getColor().getGreen(), g.getColor().getBlue(), 30));

				// was oval met rand, wordt polygon
				
				//g.fillOval(x - size / 2, y - size / 2 + textHeight / 6, size, size);
				
				Polygon poly = new Polygon();
				for(Point p: hull) poly.addPoint(origin.x + (int) ((p.x) * factor), origin.y + (int) ((p.y) * factor));
				Rectangle rect = new Rectangle(x-textLength/2, y-textHeight / 2, textLength, textHeight);
				
				rect.grow(3, 3);
				Area path = new Area(rect);
				path.add(new Area(poly));
				BasicStroke stroke = new BasicStroke((float) (size * factor),BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
				Shape filler = stroke.createStrokedShape(poly);
				path.add(new Area(filler));
				Shape shape = path;
				last = shape.getBounds(); // contains
				g.fill(shape);
				g.setColor(nodeBorderColor);
				g.setStroke(new BasicStroke(2f * (float) factor));
				if (blur)
					g.setColor(new Color(g.getColor().getRed(), g.getColor().getGreen(), g.getColor().getBlue(), 30));
				if (selected.contains(code)) {
					g.setColor(textColor);
					g.setStroke(new BasicStroke(4f*(float) factor));
				}
				g.draw(shape);
				g.setColor(textColor);
				if (blur)
					g.setColor(new Color(textColor.getRed(), textColor.getGreen(), textColor.getBlue(), 30));
				g.drawString(label, x - textLength / 2, y + textHeight / 2);

				if (tempLocation != null)
					break;
			}
		}
	}
		
		
	public FolderNode(Collection<? extends GNode> folder) {
		this(folder, NULLKEY);
	}


	public FolderNode(Collection<? extends GNode> folder, String key) {
		this.folder.addAll(folder);
	    visible.add(key);
	    methodeInfos = Collections.singletonMap(key, new DomStudentModelMethodInfo());
	    calculateHull(key);
	}


	public void expand() {
		collaps = false;
		last = EMPTY_RECT;
		for (GNode n: folder)
			if (n instanceof FolderNode) ((FolderNode) n).show(); else
			n.setVisible(methodeInfos,true);
		setVisible(methodeInfos,false);
	}


	public void collapse() {
		collaps = true;
		for (GNode n: folder) 
			if (n instanceof FolderNode) ((FolderNode) n).hide(); else
			n.setVisible(methodeInfos,false);
		setVisible(methodeInfos,true);
	}
	
	public void hide() {
		setVisible(methodeInfos, false);
		last = EMPTY_RECT;
		for (GNode n: folder) {
			if (n instanceof FolderNode) ((FolderNode) n).hide();
			else
				n.setVisible(methodeInfos, false);
		}
	}
	
	public void show() { 
		if (collaps) {
			setVisible(methodeInfos, true);
		} else 
			for (GNode n: folder) {
				if (n instanceof FolderNode) ((FolderNode) n).show();
				else n.setVisible(methodeInfos, true);
			}
	}


	public void add(FolderNode fn) {
		if (folder.add(fn)) {
			String key = NULLKEY; // voorlopig!
		    calculateHull(key);	
		}
	}


	private void calculateHull(String key) {
		List<Point> points = folder.stream().flatMap(n -> n.getLocationStream(key))
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
		hull = ConvexHull.computeHull(points);
		int x = hull.stream().collect(Collectors.averagingDouble(Point::getX)).intValue();
		int y = hull.stream().collect(Collectors.averagingDouble(Point::getY)).intValue();
		setLocation(key, x, y);
	}

	public Stream<Point> getLocationStream(String key) {
		return folder.stream().flatMap(n -> n.getLocationStream(key));
	}


	@Override
	public Object getSuccesFailScore() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Paint getEdgeSuccesFailColor() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Map<String, Map<String, Set<Integer>>> getMethodeInfo() {
		return null;
	}


	@Override
	public boolean contains(int x, int y) {
		return collaps && last.contains(x, y);
	}


	@Override
	public String search(int x, int y) {
		return NULLKEY;
	}

}
