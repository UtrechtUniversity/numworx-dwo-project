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
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import fi.dwo.dwojapplet.gui.domainmodel.LeerdomeinEditPanel2;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelMethodInfo;

public class GraphNode {

	final static float dash1[] = { 5.0f, 5.0f };
	final static BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1,
			0.0f);

	static final String NOTFOUND = "NOTFOUND";
	static final String NULLKEY = "null-null-null";
	private static Color defaultNodeColor = LeerdomeinGraphPanel.colorBlue4;
	private static Color defaultKennenNodeColor = new Color(255,255,150);
	private static Color defaultKennenNodeBorderColor = new Color(255,200,150);
	private static Color defaultTextColor = LeerdomeinGraphPanel.colorBlue1;
	private static int defaultFontSize = 16;
	private static Font defaultFont = new Font("SansSerif", Font.PLAIN, defaultFontSize);

	private FontMetrics fm;
	private Font font = defaultFont;

	private String ID;
	private String subdomein;
	private String description;
	// private String label;

	private Point tempLocation;
	private int size = LeerdomeinEditPanel2.DEFAULT_NODE_SIZE; //Node size
	private Color nodeColor = defaultNodeColor;
	private Color nodeBorderColor = LeerdomeinGraphPanel.colorBlue2;
	private Color textColor = defaultTextColor;
	private Color edgeColor = LeerdomeinGraphPanel.colorBlue4;

	private Map<String, Map<String, Set<Integer>>> methodeInfo;
	// private Map<String, String> methodeInfoString;
	// @NotNull
	private Map<String, DomStudentModelMethodInfo> methodeInfos = Collections.emptyMap(); // Never null

	private int textLength;
	private int textHeight;

	private boolean blur;
	private final Set<String> selected = new HashSet<>();
	private final Set<String> visible = new HashSet<>();

	private Double succesFailScore = null;

	private Color succesColor = new Color(0, 200, 0);
	private Color halfSuccesColor = new Color(180, 240, 180);
	private Color failColor = new Color(200, 0, 0);
	private Color halfFailColor = new Color(255, 150, 150);
	
	private boolean kennenLeerdoel;

	// private boolean visible = true;

	public GraphNode(String ID, String subdomein, String description) {
		this.ID = ID;
		this.subdomein = subdomein;
		this.description = description;
		kennenLeerdoel = description.startsWith("W:");
		if(kennenLeerdoel) {
			nodeColor = defaultKennenNodeColor;
			nodeBorderColor = defaultKennenNodeBorderColor;
		}
		// setFont(defaultFont);
	}

//	public GraphNode(String ID, String subdomein, String description, DomStudentModelMethodInfo info) {
//		this.ID = ID;
//		this.subdomein = subdomein;
//		this.description = description;
//		kennenLeerdoel = description.startsWith("W:");
//		if(kennenLeerdoel)
//			nodeColor = defaultKennenNodeColor;
//		methodeInfos = Collections.singletonMap(info.key(), info);
//	}

	GraphNode(int x, int y) {
	    visible.add(NULLKEY);
	    methodeInfos = Collections.singletonMap(NULLKEY, new DomStudentModelMethodInfo());
	    setLocation(NULLKEY, x, y);
	}
    GraphNode(int x, int y, String code) {
      visible.add(code);
      methodeInfos = Collections.singletonMap(code, new DomStudentModelMethodInfo());
      setLocation(code, x, y);
  }

	GraphNode(String ID, String subdomain, String description, int x, int y) {
		this(ID, subdomain, description);
		setLocation(x, y);
	}

	public void setSuccesFailScore(Double succesFailScore) {
		this.succesFailScore = succesFailScore;
	}

	public Double getSuccesFailScore() {
		return succesFailScore;
	}

	public String getID() {
		return ID;
	}

	public Point getLocation(String key) {
		DomStudentModelMethodInfo info = methodeInfos.get(key);
		if (info != null && info.getX() != null && info.getY() != null) {
			return new Point(info.getX(), info.getY());
		} else {
			// System.err.println("node zonder locatie " + key + " " + subdomein + " " +
			// description);
		}
		return null;
	}

	public Point getTempLocation() {
		return tempLocation;
	}

	public Point getLocationOnPanel(String code, Point origin, double factor) {
		Point location = getLocation(code);
		if (location == null)
			return null;
		int x = origin.x + (int) ((location.x) * factor);
		int y = origin.y + (int) ((location.y) * factor);
		return new Point(x, y);
	}

	public boolean inside(Rectangle r, Point origin, double factor) {
		for (String code : getMethodeCodes()) {
			Point location = getLocationOnPanel(code, origin, factor);
			if (location != null && r.contains(location))
				return true;
		}
		return false;
	}

	public synchronized void selectInside(Rectangle r, Point origin, double factor) {
		selected.clear();
		for (String code : visible) {
			Point location = getLocationOnPanel(code, origin, factor);
			if (location != null && r.contains(location))
				selected.add(code);
		}
	}

	public synchronized void selectAround(int x, int y) {
	     selected.clear();	     
	     for (String code : visible) {
           Point location = getLocation(code);
           if (location == null)
               continue;
           Rectangle r = new Rectangle(location.x - size / 2, location.y - size / 2, size, size);
           if (r.contains(x, y))
               selected.add(code);
       }

	}
	
	
	public Color getSuccesFailColor() {
		if (succesFailScore == null)
			return nodeColor;
		if (succesFailScore < 25)
			return failColor;
		if (succesFailScore <= 45)
			return halfFailColor;
		if (succesFailScore > 45 && succesFailScore < 55)
			return nodeColor;
		if (succesFailScore < 75 && succesFailScore >= 55)
			return halfSuccesColor;
		else
			return succesColor;

	}

	public Color getEdgeSuccesFailColor() {
		if (succesFailScore == null)
			return null;
		if (succesFailScore < 25)
			return null;
		if (succesFailScore <= 45)
			return null;
		if (succesFailScore > 45 && succesFailScore < 55)
			return null;
		if (succesFailScore < 75 && succesFailScore >= 55)
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

//	public static boolean hasSameChapterCode(GraphNode node1, GraphNode node2) {
//		return hasSameChapterCode(node1, node2, null);
//	}

	public static boolean hasSameChapterCode(GraphNode node1, GraphNode node2, String methode) {
	    if (methode == null) return true; // Geen methode -> altijd same chapter
		Map<String, Map<String, Set<Integer>>> info1 = node1.getMethodeInfo();
		Map<String, Map<String, Set<Integer>>> info2 = node2.getMethodeInfo();
		if (info1 == null || info2 == null)
			return false;

		Set<String> infoset = new HashSet<>(info1.keySet());
		infoset.retainAll(info2.keySet()); // retainall == doorsnede
		infoset.retainAll(Collections.singleton(methode));
		for (String methodeName : infoset) {
			Map<String, Set<Integer>> leerjaren1 = info1.get(methodeName);
			Map<String, Set<Integer>> leerjaren2 = info2.get(methodeName);
			Set<String> leerjarenset = new HashSet<>(leerjaren1.keySet());
			leerjarenset.retainAll(leerjaren2.keySet());
			for (String leerjaarName : leerjarenset) {
				Set<Integer> hoofdstukken1 = leerjaren1.get(leerjaarName);
				Set<Integer> hoofdstukken2 = leerjaren2.get(leerjaarName);
				if (hoofdstukken1.stream().anyMatch(hoofdstukken2::contains))
					return true;
			}
		}

		// for (String methodeName1 : info1.keySet()) {
		// if(methode != null && methodeName1.equals(methode)) {
		// Map<String,Set<Integer>> leerjaren1 = info1.get(methodeName1);
		// if(info2.containsKey(methodeName1)) {
		// Map<String,Set<Integer>> leerjaren2 = info2.get(methodeName1);
		// for (String leerjaarName1 : leerjaren1.keySet()){
		// if(leerjaren2.containsKey(leerjaarName1)) {
		// Set<Integer> hoofdstukken1 = leerjaren1.get(leerjaarName1);
		// Set<Integer> hoofdstukken2 = leerjaren2.get(leerjaarName1);
		// for (Integer i1 : hoofdstukken1){
		// for (Integer i2 : hoofdstukken2){
		// if(i1.intValue() == i2.intValue())
		// return true;
		// }
		// }
		// }
		// }
		// }
		// }
		// }
		return false;
	}

	public void setLocation(int x, int y) {
	  if (tempLocation != null) tempLocation.setLocation(x, y);
	  else
      for (String code : selected) {
        setLocation(code, x, y);
        break;
      }
	}

	public void setLocation(Point p) {
	  if (p == null) {
	    Iterator<String> iter = selected.iterator();
	    if (iter.hasNext()) {
	      String code = iter.next(); iter.remove();
	      DomStudentModelMethodInfo m = methodeInfos.get(code);
	      m.setX(null);m.setY(null);
	      visible.remove(code);
	    }
	  } else {
		setLocation(p.x, p.y);
	  }
	}

	public Map<String, Map<String, Set<Integer>>> getMethodeInfo() {
		return methodeInfo;
	}

	public void setMethodeInfo(Map<String, Map<String, Set<Integer>>> methodeInfo) {
		this.methodeInfo = methodeInfo;
		this.methodeInfos = extractInfos(methodeInfo);
	}

    public static Map<String, DomStudentModelMethodInfo> extractInfos(Map<String, Map<String, Set<Integer>>> methodeInfo) {
        Map<String, DomStudentModelMethodInfo> methodeInfos = new HashMap<>();
		DomStudentModelMethodInfo nul = new DomStudentModelMethodInfo();
        methodeInfos.put(nul.key(), nul); // null position
		for (String methodeName : methodeInfo.keySet()) {
			Map<String, Set<Integer>> leerjaren = methodeInfo.get(methodeName);
			for (String leerjaarName : leerjaren.keySet()) {
				Set<Integer> hoofdstukken = leerjaren.get(leerjaarName);
				for (Integer i : hoofdstukken) {
					final DomStudentModelMethodInfo info = new DomStudentModelMethodInfo(methodeName, leerjaarName, i);
					methodeInfos.put(info.key(), info);
				}
			}
		}
		return methodeInfos;
    }

	public boolean hasMethodCode(String code) {
		if (code == null)
			return false;
		return methodeInfos.values().stream().anyMatch(t -> code.startsWith(Objects.toString(t.getMethod(), "null")));
	}

	public boolean hasBookCode(String code) {
		return methodeInfos.values().stream().anyMatch(t -> code.equals(t.getMethod() + "-" + t.getBook()));
	}

	public boolean hasChapterCode(String code) {
	    return methodeInfos.containsKey(code);	  
//		return methodeInfos.values().stream()
//				.anyMatch(t -> code.equals(t.getMethod() + "-" + t.getBook() + "-" + t.getChapter()));
	}

	// public boolean hasChapterCode(String methode, String book, Set<Integer>
	// chapters) {
	// for(Integer chapter : chapters) {
	// String code = methode + "-" + book + "-" + chapter;
	// if(methodeInfos.values().stream().anyMatch(t -> code.equals(t.getMethod() +
	// "-" +t.getBook()+"-"+t.getChapter())))
	// return true;
	// }
	// return false;
	// }

	public boolean hasChapterCode(Map<String, DomStudentModelMethodInfo> filterInfo) {
		for (String chapter : filterInfo.keySet()) {
			if (methodeInfos.keySet().contains(chapter))
				return true;
		}
		return false;
	}

	public Collection<DomStudentModelMethodInfo> getMethodeInfos() {
		if (methodeInfo == null || methodeInfo.isEmpty())
			return null;
		return methodeInfos.values().stream().filter(t -> t.getMethod() != null).collect(Collectors.toList());
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

		if (tempLocation != null)
			return new Rectangle(tempLocation.x - size / 2, tempLocation.y - size / 2, size, size).contains(x, y);
		for (String code : visible) {
			Point location = getLocation(code);
			if (location == null)
				continue;
			Rectangle r = new Rectangle(location.x - size / 2, location.y - size / 2, size, size);
			if (r.contains(x, y))
				return true;
		}
		return false;
	}
	
	public void paint(Graphics gr, Point origin, double factor) {
		paint(gr,origin,factor,true);
	}

	public void paint(Graphics gr, Point origin, double factor, boolean connectInstances) {
		
		if (!isVisible() || factor < 0.15)
			return;
		Graphics2D g = (Graphics2D) gr;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		boolean nodeInstancesOverlap = false;
		if(connectInstances) {
			for (String v1code : visible) {
				if (v1code == null)
					continue;
				for (String v2code : visible) {
					if (v1code.compareTo(v2code) < 0 && tempLocation == null) {
						Point location0 = getLocation(v1code);
						if (location0 == null) continue;
						int x0 = origin.x + (int) ((location0.x) * factor);
						int y0 = origin.y + (int) ((location0.y) * factor);
						Point location1 = getLocation(v2code);
						if (location1 == null) continue;
						int x1 = origin.x + (int) ((location1.x) * factor);
						int y1 = origin.y + (int) ((location1.y) * factor);
						if (Math.abs(x0 - x1) < 1 && Math.abs(y0 - y1) < 1)
							nodeInstancesOverlap = true;
						// BasicStroke dashed = new BasicStroke(1.3f*(float)factor,
						// BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
						CompositeStroke compStroke = new CompositeStroke(new BasicStroke(2f * (float) factor),
								new BasicStroke(0.3f * (float) factor));
						ZigzagStroke zzStroke = new ZigzagStroke(new BasicStroke(1f * (float) factor),
								5.3f * (float) factor, 5.3f * (float) factor);
						g.setStroke(zzStroke);
						// g.setPaint(nodeBorderColor);
						g.setPaint(edgeColor);
						GeneralPath path = new GeneralPath();
						path.moveTo(x0, y0);
						path.lineTo(x1, y1);
						path.moveTo(x0, y0);
						path.closePath();
						g.draw(path);
					}
				}
			}
		}

		for (String code : getMethodeCodes()) {
			if (visible.contains(code)) {
				Point location = getLocation(code);
				if (location == null) continue;
				// g.setFont(defaultFont.deriveFont((int)(defaultFontSize*factor)));
				g.setFont(new Font("SansSerif", Font.PLAIN, (int) (defaultFontSize * factor)));
				if (selected.contains(code))
					g.setFont(new Font("SansSerif", Font.BOLD, (int) (defaultFontSize * factor)));
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
				g.fillOval(x - size / 2, y - size / 2 + textHeight / 6, size, size);

				g.setColor(nodeBorderColor);
				g.setStroke(new BasicStroke(2f * (float) factor));
				if (blur)
					g.setColor(new Color(g.getColor().getRed(), g.getColor().getGreen(), g.getColor().getBlue(), 30));
				if (selected.contains(code)) {
					g.setColor(textColor);
					g.drawOval(x - size / 2 - 1, y - size / 2 + textHeight / 6 - 1, size + 2, size + 2);
				}
				g.drawOval(x - size / 2, y - size / 2 + textHeight / 6, size, size);

				g.setColor(textColor);
				if (blur)
					g.setColor(new Color(textColor.getRed(), textColor.getGreen(), textColor.getBlue(), 30));
				g.drawString(label, x - textLength / 2, y + textHeight / 2);

				if (tempLocation != null)
					break;
			}
		}
	}

	public Rectangle getTextBB(String code) {
		Point location = getLocation(code);
		if (location == null)
			return new Rectangle(0, 0, 0, 0);
		// if(tempLocation != null) {
		// return new Rectangle(tempLocation.x-textLength/2 ,
		// tempLocation.y-textHeight/2+3 , textLength, textHeight);
		// }
		return new Rectangle(location.x - textLength / 2, location.y - textHeight / 2 + 3, textLength, textHeight);
	}

	public void setBlur(boolean b) {
		blur = b;
	}

	public void setSelected(boolean b) {
		if (b)
			selected.addAll(getMethodeCodes());
		else
			selected.clear();
	}

	public void setSelected(String code, boolean b) {
		for (String methodeCode : getMethodeCodes())
			if (methodeCode.startsWith(code)) {
				if (b) {
					// visible.clear();
					selected.add(methodeCode);
				} else
					selected.remove(methodeCode);
			}
	}

	public void setSelected(Map<String, DomStudentModelMethodInfo> filterInfo, boolean b) {
		for (String methodeCode : getMethodeCodes()) {
			if (filterInfo.containsKey(methodeCode)) {
				if (b) {
					selected.add(methodeCode);
				} else
					selected.remove(methodeCode);
			}
		}
	}

	public boolean getBlur() {
		return blur;
	}

	public boolean isSelected() {
		return !selected.isEmpty();
	}

	public boolean isSelected(String code) {
		return selected.contains(code);
	}

	public void setVisible(boolean b) {
		if (b)
			visible.addAll(getMethodeCodes());
		else
			visible.clear();
	}

	public void setVisible(String code, boolean b) {
		for (String methodeCode : getMethodeCodes())
			if (methodeCode.startsWith(code)) {
				if (b) {
					// visible.clear();
					visible.add(methodeCode);
				} else
					visible.remove(methodeCode);
			}
	}

	public void setVisible(Map<String, DomStudentModelMethodInfo> filterInfo, boolean b) {
		for (String methodeCode : getMethodeCodes()) {
			if (filterInfo.containsKey(methodeCode)) {
				if (b) {
					visible.add(methodeCode);
				} else
					visible.remove(methodeCode);
			}
		}
	}

	public boolean isVisible() {
		return !visible.isEmpty();
	}

	public boolean isVisible(String code) {
		return visible.contains(code);
	}

	public Set<String> getVisibleSet() {
		return visible;
	}

	private int compare(DomStudentModelMethodInfo a, DomStudentModelMethodInfo b) {
	  String ma = Objects.toString(a.getMethod(), "");
	  String mb = Objects.toString(b.getMethod(), "");
	  int result = ma.compareTo(mb);
	  if (result != 0) return result;
	  String ba = Objects.toString(a.getBook(), "");
	  String bb = Objects.toString(b.getBook(), "");
	  result = ba.compareTo(bb);
	  if (result != 0) return result;
	  Integer ca = a.getChapter(); if (ca == null) ca = Integer.valueOf(0);
	  Integer cb = b.getChapter(); if (cb == null) cb = Integer.valueOf(0);
	  return ca.compareTo(cb);
	}
	
	
	public void setMethodeInfos(List<DomStudentModelMethodInfo> infos) {
		if (infos != null) {
		    
			for (DomStudentModelMethodInfo info : infos) {
				String key = info.key();
				methodeInfos.computeIfPresent(key, (k, v) -> info);
			}
			infos = new ArrayList<DomStudentModelMethodInfo>(methodeInfos.values());
			Collections.sort(infos, this::compare);
			int size = infos.size();
			DomStudentModelMethodInfo first = null, last = null;
			for(int i = 0; i < size; i ++ ) {
			  DomStudentModelMethodInfo item = infos.get(i);
			  if (first != null && !Objects.equals(first.getMethod(),item.getMethod())) first = null;
			  if (item.getX() == null || item.getY() == null) {
			    last = first;
			    for (int j = i+1; j < size; j++) {
			      DomStudentModelMethodInfo other = infos.get(j);
			      if (!Objects.equals(item.getMethod(), other.getMethod())) break;
			      if (other.getX() != null && other.getY() != null) last = other;
			    }
			    if (last != null) {
			      item.setX(last.getX());
			      item.setY(last.getY());
			      first = item;
			    }
			  } else {
			    first = item;
			  }			  
			}
		}

	}

	public Set<String> getMethodeCodes() {
		return methodeInfos.keySet();
	}

	public String search(int x, int y) {
		for (String code : visible) {
			Point location = getLocation(code);
			if (location==null) 
				continue;
			Rectangle r = new Rectangle(location.x - size / 2, location.y - size / 2, size, size);
			if (r.contains(x, y))
				return code;
		}
		return NOTFOUND;
	}

	public void setLocation(String code, int ex, int ey) {
		DomStudentModelMethodInfo info = methodeInfos.get(code);
		if (info != null) {
			info.setX(ex);
			info.setY(ey);
		}
	}
	
	public String getVariant(String code) {
		DomStudentModelMethodInfo info = methodeInfos.get(code);
		if (info != null) {
			return info.getVariant();
		}
		return null;
	}

	public void move(int dx, int dy) {
		for (String code : selected) {
			Point location = getLocation(code);
			setLocation(code, location.x + dx, location.y + dy);
		}

	}
	
	public void setSelectionOnGrid() {
		for (String code : selected) {
			Point location = getLocation(code);
			int locx = location.x;
			int locy = location.y;
			int corrx = locx<0 ? -15 : 15;
			int corry = locy<0 ? -15 : 15;
			setLocation(code, corrx+locx/30*30, corry+locy/30*30);
		}

	}

	public Integer getSize() {
      return Integer.valueOf(size);
    }
  
    public void setSize(Integer size) {
      if (size == null) size = LeerdomeinEditPanel2.DEFAULT_NODE_SIZE;
      this.size = size.intValue();
    }

    public class CompositeStroke implements Stroke {
		private Stroke stroke1, stroke2;

		public CompositeStroke(Stroke stroke1, Stroke stroke2) {
			this.stroke1 = stroke1;
			this.stroke2 = stroke2;
		}

		@Override
		public Shape createStrokedShape(Shape shape) {
			return stroke2.createStrokedShape(stroke1.createStrokedShape(shape));
		}
	}

	public class ZigzagStroke implements Stroke {
		private float amplitude = 10.0f;
		private float wavelength = 10.0f;
		private Stroke stroke;
		private static final float FLATNESS = 1;

		public ZigzagStroke(Stroke stroke, float amplitude, float wavelength) {
			this.stroke = stroke;
			this.amplitude = amplitude;
			this.wavelength = wavelength;
		}

		public Shape createStrokedShape(Shape shape) {
			GeneralPath result = new GeneralPath();
			PathIterator it = new FlatteningPathIterator(shape.getPathIterator(null), FLATNESS);
			float points[] = new float[6];
			float moveX = 0, moveY = 0;
			float lastX = 0, lastY = 0;
			float thisX = 0, thisY = 0;
			int type = 0;
			boolean first = false;
			float next = 0;
			int phase = 0;

			float factor = 1;

			while (!it.isDone()) {
				type = it.currentSegment(points);
				switch (type) {
				case PathIterator.SEG_MOVETO:
					moveX = lastX = points[0];
					moveY = lastY = points[1];
					result.moveTo(moveX, moveY);
					first = true;
					next = wavelength / 2;
					break;

				case PathIterator.SEG_CLOSE:
					points[0] = moveX;
					points[1] = moveY;
					// Fall into....

				case PathIterator.SEG_LINETO:
					thisX = points[0];
					thisY = points[1];
					float dx = thisX - lastX;
					float dy = thisY - lastY;
					float distance = (float) Math.sqrt(dx * dx + dy * dy);
					if (distance >= next) {
						float r = 1.0f / distance;
						float angle = (float) Math.atan2(dy, dx);
						while (distance >= next) {
							float x = lastX + next * dx * r;
							float y = lastY + next * dy * r;
							float tx = amplitude * dy * r;
							float ty = amplitude * dx * r;
							if ((phase & 1) == 0)
								result.lineTo(x + amplitude * dy * r, y - amplitude * dx * r);
							else
								result.lineTo(x - amplitude * dy * r, y + amplitude * dx * r);
							next += wavelength;
							phase++;
						}
					}
					next -= distance;
					first = false;
					lastX = thisX;
					lastY = thisY;
					if (type == PathIterator.SEG_CLOSE)
						result.closePath();
					break;
				}
				it.next();
			}

			return stroke.createStrokedShape(result);
		}
	}

	public void mergeMethodeInfos(Collection<DomStudentModelMethodInfo> infos) {
		if (infos != null) {
			for ( DomStudentModelMethodInfo i : infos) {
				DomStudentModelMethodInfo o = methodeInfos.get(i.key());
				if (o != null) {
					o.setVariant(i.getVariant()); // XXX merge all fields, except key and x,y, voor nu alleen variant
				} else {
					methodeInfos.put(i.key(), i); // do not forget
				}
			}
		}
		
	}
}
