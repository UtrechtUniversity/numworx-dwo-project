package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import java.awt.Color;
import java.awt.Font;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;
import org.vectomatic.dom.svg.OMNode;
import org.vectomatic.dom.svg.OMNodeList;
import org.vectomatic.dom.svg.OMSVGAnimatedRect;
import org.vectomatic.dom.svg.OMSVGCircleElement;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGImageElement;
import org.vectomatic.dom.svg.OMSVGLength;
import org.vectomatic.dom.svg.OMSVGLineElement;
import org.vectomatic.dom.svg.OMSVGMatrix;
import org.vectomatic.dom.svg.OMSVGPathElement;
import org.vectomatic.dom.svg.OMSVGPathSegList;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.dom.svg.OMSVGRect;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.OMSVGStyle;
import org.vectomatic.dom.svg.OMSVGTextElement;
import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.utils.OMSVGParser;
import org.vectomatic.dom.svg.utils.SVGConstants;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.Composite;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategoryScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;

class StudentResultsGraph extends Composite implements MouseMoveHandler, MouseUpHandler, MouseDownHandler, MouseOutHandler {

	static ColorStyle colorBlue1 = new ColorStyle(49, 71, 112);
	static ColorStyle colorBlue4 = new ColorStyle(180,195,228);
	static ColorStyle colorBlue2 = new ColorStyle(38, 115, 182);
	static ColorStyle colorGray3 = new ColorStyle(237, 239, 241);

	private static ColorStyle defaultNodeColor = colorBlue4;
	private static ColorStyle defaultTextColor = colorBlue1;
	private static ColorStyle defaultRectColor = new ColorStyle( colorGray3.getRGB()&0xFFFFFF | (186<<24));
	private static ColorStyle defaultRectBorderColor = new ColorStyle( colorGray3.getRGB()&0xFFFFFF | (90<<24));

	private static int defaultFontSize = 16;
	private static String defaultFont = "SansSerif";

	private ColorStyle succesColor = new ColorStyle(0,200,0);
	private ColorStyle halfSuccesColor = new ColorStyle(180,240,180);
	private ColorStyle failColor = new ColorStyle(200,0,0);
	private ColorStyle halfFailColor = new ColorStyle(255,150,150);
	private ColorStyle defaultEdgeColor = colorBlue4;
	
	private OMSVGDocument doc;
	private SVGImage image;
	private String lang;
	
	private OMSVGPoint start;
	
	class Edge {
		final Node from, to;
		final OMSVGGElement g = doc.createSVGGElement();
		boolean blur;

		ColorStyle edgeColor = defaultEdgeColor;

		Edge(Node from, Node to) {
			this.from = from;
			this.to = to;
		}

		// opacity: 30/255
		ColorStyle blur(ColorStyle org) {
			if(!blur) return org;
			return new ColorStyle( org.getRGB()&0xFFFFFF | (30<<24));
		}
		
		OMSVGGElement build() {
			float x1 = from.cx;
			float y1 = from.cy;
			float x2 = to.cx;
			float y2 = to.cy;
			OMSVGLineElement line = doc.createSVGLineElement(x1, y1, x2, y2);
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
			g.appendChild(path);
			colorize();
			return g;
		}

		public void setBlur(boolean b) {
			blur = b;
			colorize();
		}

		private void colorize() {
			String color = blur(edgeColor).getColor();
			OMSVGStyle style = g.getStyle();
			style.setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, color);
			style.setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, color);		
		}
		
		void setSuccesFailColor() {
			OMSVGStyle style = g.getStyle();
			if (to.getEdgeColor() != null) 
			{
				edgeColor = to.getEdgeColor();
				style.setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "5");
			}
			else 
			{
				edgeColor = defaultEdgeColor;
				style.setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "1.3");
			}
			colorize();
		}
	}
	
	
	class Node {

		final private DomStudentModelObj obj;
		final OMSVGGElement g = doc.createSVGGElement();
		final float cx,cy;
		private boolean blur;
				
		private ColorStyle nodeColor = defaultNodeColor;
		private ColorStyle nodeBorderColor = colorBlue2;
		private ColorStyle textColor = defaultTextColor;
		private ColorStyle edgeColor;
		private OMSVGCircleElement circle;
		private OMSVGTextElement text;
		private OMSVGRectElement rect;
		private	float r = 15;

		void setBlur(boolean blur) {
			this.blur = blur;
			colorize();
		}

		boolean isBlur() {
			return blur;
		}
		
		boolean contains(float x, float y) {
			float dx = x - cx;
			float dy = y - cy;
			return Math.abs(dx) <= r && Math.abs(dy) <= r;
		}

		// opacity: 30/255
		ColorStyle blur(ColorStyle org) {
			if(!blur) return org;
			return new ColorStyle( org.getRGB()&0xFFFFFF | (30<<24));
		}
		
		Node(DomStudentModelObj obj, String parent) {
			this.obj = obj;
 			cx = obj.getInfo().getX().floatValue();
 			cy = obj.getInfo().getY().floatValue();
			circle = doc.createSVGCircleElement(cx, cy, r);
			short unitType = OMSVGLength.SVG_LENGTHTYPE_NUMBER;
			text = doc.createSVGTextElement(cx, cy, unitType, parent + obj.getInfo().getTitle().get(lang));
		}
		
		Node(DomStudentModelObj obj) {
			this(obj,"");
		}
		
		OMSVGGElement build() {
 			g.appendChild(circle);
 			text.getStyle().setSVGProperty(SVGConstants.CSS_TEXT_ANCHOR_PROPERTY, "middle");
 			text.getStyle().setSVGProperty(SVGConstants.CSS_DOMINANT_BASELINE_PROPERTY, "central");
 			text.getStyle().setSVGProperty(SVGConstants.CSS_FONT_FAMILY_PROPERTY, defaultFont);
 			text.getStyle().setSVGProperty(SVGConstants.CSS_FONT_SIZE_PROPERTY, Integer.toString(defaultFontSize));
 			g.appendChild(text);
 			OMSVGRect box = text.getBBox();
 			rect = doc.createSVGRectElement(box);
 			rect.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "6");
 			rect.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, defaultRectColor.getColor());
 			rect.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, defaultRectBorderColor.getColor());
			g.insertBefore(rect, circle);
			colorize();
			return g;
		}

		private void colorize() {
			String color = blur(nodeColor).getColor();
			circle.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, color);
			circle.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, blur(nodeBorderColor).getColor());
 			color = blur(textColor).getColor();
 			text.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, color);

		}
		
		Stream<Edge> edges() {
			List<String> voorkennis = obj.getInfo().getVoorkennis();			
			return voorkennis.stream().map( key -> new Edge( map.get(key), this) );
		}

		public void setScore(DomStudentModelScore<?> cat) {
			double succesFailScore = cat.getScore() * 100;
			edgeColor = null;
			if(succesFailScore < 25) 
				nodeColor = failColor;
			else if(succesFailScore <= 45) 
				nodeColor = halfFailColor;
			else if(succesFailScore > 45 && succesFailScore < 55)
				nodeColor = new ColorStyle(0xFFFFFF);
			else if(succesFailScore < 75 && succesFailScore >= 55) 
				edgeColor = nodeColor = halfSuccesColor;
			else 
				edgeColor = nodeColor = succesColor;			
		}

		ColorStyle getEdgeColor() {
			return edgeColor;
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
		getElement().getStyle().setBackgroundColor(colorGray3.getColor());
		
		image.addMouseMoveHandler(this);
		image.addMouseUpHandler(this);
		image.addMouseDownHandler(this);
		image.addMouseOutHandler(this);
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
		
		score.then(this::withScore);
		
	}

	private Promise<DomStudentModelDataScore> withScore(Promise<DomStudentModelDataScore> p) {
		DomStudentModelDataScore s = p.getValue();
		DomStudentModelStructureScore score = s.getDomStudentModelStructureScore();
		for(DomStudentModelCategoryScore cat : score.getCategories()) {
			withScore(cat);
		}
		edges.forEach(Edge::setSuccesFailColor);
		return p;
	}	
	
	private void withScore(DomStudentModelScore<?> cat) {
		List<? extends DomStudentModelScore> children = cat.getChildren();
		if (children != null) {
			for( DomStudentModelScore<?> obj : children) { withScore(obj); }
		}
		String id = cat.getId();
		Node n = map.get(id);
		if (n != null) n.setScore(cat);
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

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if (start == null)
			mouseMove(event);
		else
			mouseDrag(event);
	}

	private void mouseMove(MouseEvent<?> event) {
		float x = event.getClientX();
		float y = event.getClientY();
		OMSVGPoint point = image.getSvgElement().createSVGPoint(x, y);
		OMSVGMatrix ctm = image.getSvgElement().getScreenCTM().inverse();
		point = point.matrixTransform(ctm);
		float sx = point.getX();
		float sy = point.getY();
		// blur nodes and edges: find node, focus on node and edges.
		Optional<Node> find = map.values().stream().filter(node -> node.contains(sx, sy)).findAny();
		if (find.isPresent()) {
			Node node = find.get();
			Set<Edge> set = edges.stream().filter(edge -> edge.to == node).collect(Collectors.toSet());
			Set<Node> nodes = set.stream().map(t -> t.from).collect(Collectors.toSet());
			nodes.add(node);
			edges.forEach(e -> e.setBlur(!set.contains(e)));
			map.values().forEach(n -> n.setBlur(!nodes.contains(n)));			
		} else {
			edges.forEach(e -> e.setBlur(false));
			map.values().forEach(n -> n.setBlur(false));
		}		
	}

	private void mouseDrag(MouseEvent<?> event) {
		float x = event.getClientX();
		float y = event.getClientY();
		float dx = x - start.getX();
		float dy = y - start.getY();
		OMSVGMatrix ctm = image.getSvgElement().getScreenCTM();
		dx = dx / ctm.getA();
		dy = dy / ctm.getA(); // is dat zo? E?
		OMSVGRect viewbox = image.getSvgElement().getViewBox().getBaseVal();
		viewbox.setX(viewbox.getX()-dx);
		viewbox.setY(viewbox.getY()-dy);
		image.getSvgElement().setViewBox(viewbox);
		start = image.getSvgElement().createSVGPoint(x, y);
	}
	
	
	@Override
	public void onMouseUp(MouseUpEvent event) {
		mouseDrag(event);
		start = null;
		mouseMove(event);
		
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		start = null;
		mouseMove(event);
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		start = image.getSvgElement().createSVGPoint(event.getClientX(), event.getClientY());
		
	}

	
	
}
