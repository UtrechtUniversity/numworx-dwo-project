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

public class GraphNode extends AbstractNode implements GNode {

	final static float dash1[] = { 5.0f, 5.0f };
	final static BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1,
			0.0f);



	private Map<String, Map<String, Set<Integer>>> methodeInfo;
	// private Map<String, String> methodeInfoString;
	// @NotNull



	
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


	public Double getSuccesFailScore() {
		return succesFailScore;
	}




	public boolean inside(Rectangle r, Point origin, double factor) {
		for (String code : getMethodeCodes()) {
			Point location = getLocationOnPanel(code, origin, factor);
			if (location != null && r.contains(location))
				return true;
		}
		return false;
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


	public Collection<DomStudentModelMethodInfo> getMethodeInfos() {
		if (methodeInfo == null || methodeInfo.isEmpty())
			return null;
		return super.getMethodeInfos();
	}


	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}



	@Override
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

}
