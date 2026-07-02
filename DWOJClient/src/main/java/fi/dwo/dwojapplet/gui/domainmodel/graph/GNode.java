package fi.dwo.dwojapplet.gui.domainmodel.graph;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelMethodInfo;

interface GNode {

	static final String NOTFOUND = "NOTFOUND";
	static final String NULLKEY = "null-null-null";

	
	void paint(Graphics gr, Point origin, double factor, boolean connectInstances);
	default void paint(Graphics gr, Point origin, double factor) {
		paint(gr,origin,factor,true);
	}

	Point getLocation(String key);
	default Stream<Point> getLocationStream(String key) { return Stream.of(getLocation(key)); }
	boolean getBlur();
	boolean isVisible();
	Object getTempLocation();
	Set<String> getVisibleSet();
	Rectangle getTextBB(String code);
	boolean isVisible(String hfstCode);
	boolean hasMethodCode(String hfstCode);
	void setVisible(boolean b);
	void setTempLocation(Point object);
	void setVisible(String methodeCode, boolean b);
	boolean hasBookCode(String bookCode);
	boolean hasChapterCode(Map<String, DomStudentModelMethodInfo> filterInfo);
	void setVisible(Map<String, DomStudentModelMethodInfo> filterInfo, boolean b);
	boolean hasChapterCode(String hfstCode);
	boolean contains(int ex, int ey);
	void setBlur(boolean b);
	String getDescription();
	String search(int ex, int ey);
	String getVariant(String code);
	String getID();
	void mergeMethodeInfos(Collection<DomStudentModelMethodInfo> methodeInfos);
	Collection<DomStudentModelMethodInfo> getMethodeInfos();
	void setSuccesFailScore(Double score);
	void selectAround(int ex, int ey);
	void selectInside(Rectangle rectangle, Point origin, double factor);
	void setSelectionOnGrid();
	void setSelected(boolean b);
	boolean isSelected();
	void move(int dx, int dy);
	void setLocation(String activeCode, int ex, int ey);
	void setLocation(Point object);
	void setSelected(String t, boolean b);
	Collection<String> getMethodeCodes();
	void setLocation(int ex, int ey);

}
