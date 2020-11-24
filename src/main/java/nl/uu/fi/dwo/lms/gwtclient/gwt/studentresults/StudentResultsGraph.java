package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;
import org.vectomatic.dom.svg.OMNode;
import org.vectomatic.dom.svg.OMNodeList;
import org.vectomatic.dom.svg.OMSVGCircleElement;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGImageElement;
import org.vectomatic.dom.svg.OMSVGLength;
import org.vectomatic.dom.svg.OMSVGLineElement;
import org.vectomatic.dom.svg.OMSVGPathElement;
import org.vectomatic.dom.svg.OMSVGPathSegList;
import org.vectomatic.dom.svg.OMSVGRect;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.OMSVGScriptElement;
import org.vectomatic.dom.svg.OMSVGStyle;
import org.vectomatic.dom.svg.OMSVGTextElement;
import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.utils.OMSVGParser;
import org.vectomatic.dom.svg.utils.SVGConstants;

import com.google.googlejavaformat.Doc;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.Composite;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;

class StudentResultsGraph extends Composite {

	private OMSVGDocument doc;
	private SVGImage image;
	private String lang;
	
	class Edge {
		final Node from, to;
		final OMSVGGElement g = doc.createSVGGElement();

		Edge(Node from, Node to) {
			this.from = from;
			this.to = to;
		}
		
		OMSVGGElement build() {
			float x1 = from.cx;
			float y1 = from.cy;
			float x2 = to.cx;
			float y2 = to.cy;
			String color = "black";
			OMSVGLineElement line = doc.createSVGLineElement(x1, y1, x2, y2);
			g.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, color);
			g.appendChild(line);
// triangle
			float x = (x1+x2)/2;
			float y = (y1+y2)/2;
			float dx = x1 - x2;
			float dy = y1 - y2;
			float len = (float) Math.hypot(dx, dy) / 15;
			dx /= len;
			dy /= len;
			OMSVGPathElement path = doc.createSVGPathElement();
			OMSVGPathSegList points = path.getPathSegList();
			points.appendItem(path.createSVGPathSegMovetoAbs(x, y));		
			points.appendItem(path.createSVGPathSegLinetoAbs(x + dx + dy/2, y + dy -dx/2));
			points.appendItem(path.createSVGPathSegLinetoAbs(x + dx - dy/2, y + dy +dx/2));
			points.appendItem(path.createSVGPathSegClosePath());
			OMSVGStyle style = g.getStyle();
			style.setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, color);
			g.appendChild(path);
			return g;
		}
	}
	
	
	class Node {

		final private DomStudentModelObj obj;
		final OMSVGGElement g = doc.createSVGGElement();
		final float cx,cy;
		
		Node(DomStudentModelObj obj) {
			this.obj = obj;
 			cx = obj.getInfo().getX().floatValue();
 			cy = obj.getInfo().getY().floatValue();
		}
		
		OMSVGGElement build() {
 			float r = 15;
			OMSVGCircleElement circle = doc.createSVGCircleElement(cx, cy, r);
 			String color = "blue";
			circle.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, color);
			circle.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, "black");
 			g.appendChild(circle);
 			color = "black";
			short unitType = OMSVGLength.SVG_LENGTHTYPE_NUMBER;
			OMSVGTextElement text = doc.createSVGTextElement(cx, cy, unitType, obj.getInfo().getTitle().get(lang));
 			text.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, color);
 			text.getStyle().setSVGProperty(SVGConstants.CSS_TEXT_ANCHOR_PROPERTY, "middle");
 			text.getStyle().setSVGProperty(SVGConstants.CSS_DOMINANT_BASELINE_PROPERTY, "central");
 			g.appendChild(text);
 			OMSVGRect box = text.getBBox();
 			OMSVGRectElement rect = doc.createSVGRectElement(box);
 			rect.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, "wheat");
 			rect.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, "wheat");
 			rect.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "6");
 			g.insertBefore(rect, circle);
			return g;
		}
		
		Stream<Edge> edges() {
			List<String> voorkennis = obj.getInfo().getVoorkennis();			
			return voorkennis.stream().map( key -> new Edge( map.get(key), this) );
		}
		
	}
	
	private Map<String, Node> map;
	private Set<Edge> edges;

	@Inject StudentResultsGraph() {
		doc = OMSVGParser.currentDocument();
		image = new SVGImage();
		image.setSvgElement(doc.createSVGSVGElement());
		getSvgElement().setViewBox(0, 0, 500, 500);
		initWidget(image);
		lang = LocaleInfo.getCurrentLocale().getLocaleName();
		map = new HashMap<>();
		edges = Collections.emptySet();
	}

	private OMSVGSVGElement getSvgElement() {
		return image.getSvgElement();
	}

	public void setModelScore(DomStudentModelContext item, Promise<DomStudentModelDataScore> score) {
		map.clear();
		setModel(item.getModelStructure());
		OMSVGSVGElement svg = getSvgElement();
		while(svg.getChildNodes().getLength()>0)
		svg.removeChild(svg.getChildNodes().getItem(0));
		
		edges = map.values().stream().flatMap(Node::edges).collect(Collectors.toSet());
		
		edges.stream().map(Edge::build).forEach(t -> svg.appendChild(t));
		map.values().forEach(t -> svg.appendChild(t.g));
		map.values().forEach(Node::build);
	}

	private void setModel(DomStudentModelStructure modelStructure) {
		for(DomStudentModelCategory cat: modelStructure.getCategories()) {
			setModel(cat);
		}
		
	}

	private void setModel(DomStudentModelCategory cat) {
		for (DomStudentModelObj obj: cat.getObjectives()) {
			setModel(obj);			
		}
	}

	private boolean setModel(DomStudentModelObj obj) {
		if (obj.getObjectives() == null) {
			String id = obj.getInfo().getId();
			map.put(id, new Node(obj));
			return true;
		}
		for (DomStudentModelObj leaf : obj.getObjectives()) setModel(leaf);
		return false;
	}

}
