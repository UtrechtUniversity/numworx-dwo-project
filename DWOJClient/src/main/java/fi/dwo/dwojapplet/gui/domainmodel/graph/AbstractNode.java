package fi.dwo.dwojapplet.gui.domainmodel.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import fi.dwo.dwojapplet.gui.domainmodel.LeerdomeinEditPanel2;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelMethodInfo;

abstract class AbstractNode implements GNode {

	static Color defaultNodeColor = LeerdomeinGraphPanel.colorBlue4;
	static Color defaultKennenNodeColor = new Color(255,255,150);
	static Color defaultKennenNodeBorderColor = new Color(255,200,150);
	static Color defaultTextColor = LeerdomeinGraphPanel.colorBlue1;
	static int defaultFontSize = 16;
	static Font defaultFont = new Font("SansSerif", Font.PLAIN, defaultFontSize);

	FontMetrics fm;
	Font font = defaultFont;
	int textLength;
	int textHeight;

	String ID;
	String subdomein;
	String description;
	// private String label;

	Point tempLocation;
	int size = LeerdomeinEditPanel2.DEFAULT_NODE_SIZE; //Node size
	Color nodeColor = defaultNodeColor;
	Color nodeBorderColor = LeerdomeinGraphPanel.colorBlue2;
	Color textColor = defaultTextColor;
	Color edgeColor = LeerdomeinGraphPanel.colorBlue4;

	Double succesFailScore = null;

	Color succesColor = new Color(0, 200, 0);
	Color halfSuccesColor = new Color(180, 240, 180);
	Color failColor = new Color(200, 0, 0);
	Color halfFailColor = new Color(255, 150, 150);

	
	boolean blur;
	final Set<String> selected = new HashSet<>();

	
	final Set<String> visible = new HashSet<>();
	Map<String, DomStudentModelMethodInfo> methodeInfos = Collections.emptyMap(); // Never null

	public boolean isVisible() {
		return !visible.isEmpty();
	}

	public boolean isVisible(String code) {
		return visible.contains(code);
	}

	public Set<String> getVisibleSet() {
		return visible;
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

	public Set<String> getMethodeCodes() {
		return methodeInfos.keySet();
	}

	public Collection<DomStudentModelMethodInfo> getMethodeInfos() {
		return methodeInfos.values().stream().filter(t -> t.getMethod() != null).collect(Collectors.toList());
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

	public void setLocation(String code, int ex, int ey) {
		DomStudentModelMethodInfo info = methodeInfos.get(code);
		if (info != null) {
			info.setX(ex);
			info.setY(ey);
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
	
	public void setBlur(boolean b) {
		blur = b;
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

	public String getID() {
		return ID;
	}

	public Point getTempLocation() {
		return tempLocation;
	}
	public void setTempLocation(Point p) {
		tempLocation = p;
	}
	public Point getLocationOnPanel(String code, Point origin, double factor) {
		Point location = getLocation(code);
		if (location == null)
			return null;
		int x = origin.x + (int) ((location.x) * factor);
		int y = origin.y + (int) ((location.y) * factor);
		return new Point(x, y);
	}

	public synchronized void selectInside(Rectangle r, Point origin, double factor) {
		selected.clear();
		for (String code : visible) {
			Point location = getLocationOnPanel(code, origin, factor);
			if (location != null && r.contains(location))
				selected.add(code);
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
	public void setSuccesFailScore(Double succesFailScore) {
		this.succesFailScore = succesFailScore;
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
//			return methodeInfos.values().stream()
//					.anyMatch(t -> code.equals(t.getMethod() + "-" + t.getBook() + "-" + t.getChapter()));
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
		public String getDescription() {
			return description;
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

		public String getVariant(String code) {
			DomStudentModelMethodInfo info = methodeInfos.get(code);
			if (info != null) {
				return info.getVariant();
			}
			return null;
		}

}
