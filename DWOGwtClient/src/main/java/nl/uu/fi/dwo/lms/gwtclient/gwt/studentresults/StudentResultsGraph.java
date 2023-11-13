package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;
import org.vectomatic.dom.svg.OMSVGCircleElement;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGGElement;
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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
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
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel.StudentModelPresenter;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategoryScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelMethodInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;

public class StudentResultsGraph extends LayoutPanel implements MouseMoveHandler, MouseUpHandler, MouseDownHandler, MouseOutHandler, ContextMenuHandler {

	
	private static final int TITLE_HEIGHT = 32;

	protected static final StudentResultsGraphBundle bundle = GWT.create(StudentResultsGraphBundle.class);

	static final double XLARGE = 0.3;
	static final double LARGE =  0.21;
	static final double SMALL =  0.15;
	static final double XSMALL = 0.05;
	
// range size 0.. 2	
	private String cssSize(double size) {
		StudentResultsGraphCSS css = bundle.css();
		if (size > XLARGE) return css.xlarge();
		if (size > LARGE) return css.large();
		if (size < XSMALL) return css.xsmall();
		if (size < SMALL) return css.small();
		return css.normal();
	}
		
	Map<String, Map<String,Set<Integer>>> filter;
	
	private class FilterConsumer implements Consumer<Map<String, Map<String, Set<Integer>>>> {

		@Override
		public void accept(Map<String, Map<String, Set<Integer>>> t) {
			doFilter(t);
			zoomFit();			
		}		
	}
		
	static final float  VOORKENNIS_HEIGHT = 192f; // 12em
	boolean inVoorkennis, inVoorkennisTree;
	Collection<Edge> voorkennisEdges = Collections.emptySet();
	
	public class Voorkennis implements ClickHandler {

		public OMSVGPoint create(float x, float y) {
			return image.getSvgElement().createSVGPoint(x, y);
		}
		
		
		public Iterable<OMSVGPoint> maakVoorkennisPosities(float x, float y, float width, float height, float f) {
			ArrayList<OMSVGPoint> posities = new ArrayList<OMSVGPoint>();
			float centerx = x + width/2;
			float centery = y + height/2;
			height /= 2;
			posities.add(create(centerx, centery));
			
			posities.add(create(x      +f*250, y+height-f*40));
			posities.add(create(x+width-f*250, y+height+f*40));
			posities.add(create(x      +f*200, y+height+f*40));
			posities.add(create(x+width-f*200, y+height-f*40));
			
			posities.add(create(x      +f*280, y+height-f*20));
			posities.add(create(x+width-f*280, y+height+f*20));
			posities.add(create(x      +f*230, y+height+f*20));
			posities.add(create(x+width-f*230, y+height-f*20));
			
			posities.add(create(x      +f*310, y+height-f*60));
			posities.add(create(x+width-f*310, y+height+f*60));
			posities.add(create(x      +f*310, y+height+f*60));
			posities.add(create(x+width-f*310, y+height-f*60));
			return posities;
			
		}

		
		
		
		
		@Override
		public void onClick(ClickEvent event) {
			setWidgetTopHeight(title, 12, Unit.EM, 2, Unit.EM);
			setWidgetTopHeight(zoomFitBtn, 3+12, Unit.EM, 2, Unit.EM);
			setWidgetTopHeight(zoomInBtn, 6+12, Unit.EM, 2, Unit.EM);
			setWidgetTopHeight(zoomOutBtn, 9+12, Unit.EM, 2, Unit.EM);
			setWidgetTopHeight(voorkennisBtn, 3+12, Unit.EM, 2, Unit.EM);
			setWidgetVisible(voorkennistitle, true);
			setVoorKennisVisible(false);
			OMSVGRect viewbox = image.getSvgElement().getViewBox().getBaseVal();
			float dy = VOORKENNIS_HEIGHT;
			OMSVGMatrix ctm = image.getSvgElement().getScreenCTM();
			dy /= ctm.getA();
			viewbox.setY(viewbox.getY()-dy);
			image.getSvgElement().setViewBox(viewbox);
			inVoorkennis = true;
			Set<String> voorkennisIds = 
			nodeStream().filter(Node::isVisible).flatMap(t -> t.obj.getInfo().getVoorkennis().stream())
			.map(StudentResultsGraph::strip)
			.collect(Collectors.toSet());
			voorkennisIds.removeAll(nodeStream().filter(Node::isVisible).map(n -> n.obj.getInfo().getId()).collect(Collectors.toSet()));
			Set<String> methodes = filter.keySet();
			LOG.info("aantal = " + voorkennisIds.size());
			float centerx = viewbox.getCenterX();
			float centery = viewbox.getY() + dy/2;
			Iterator<OMSVGPoint> points = maakVoorkennisPosities(viewbox.getX(), viewbox.getY(), viewbox.getWidth(), dy, 1f/ctm.getA()).iterator();
			for(String id: voorkennisIds) {
				List<Node> list = map.get(id);
				if (list != null && !list.stream().anyMatch(Node::isVisible)) {
					list.stream()
					.filter(n -> methodes.contains(n.info.getMethod()))
					.findAny().ifPresent(n -> {
					n.setVisible(true);
					n.setVoorkennis(true);
					if (points.hasNext()) {
						OMSVGPoint p  = points.next();
						n.moveTo(p.getX(), p.getY());
					} else
						n.moveTo(centerx, centery);
				});
			}}
			voorkennisEdges = edges.stream().map(Edge::withVoorkennis)
					.filter(Objects::nonNull)
					.collect(Collectors.toList());
			LOG.info("voorkennis edges = " + voorkennisEdges.size());
		}
	}
	public class VerbergVoorkennis implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			verbergVoorkennis();
			doFilter(filter);
		}
		
	}
	

	float factor = 1.0f;
	public class Zoom implements ClickHandler {

		final boolean out;
		public Zoom(boolean b) {
			out = b;
		}

		@Override
		public void onClick(ClickEvent event) {
			if (out) resize(factor * 1.2f);
			else resize(factor / 1.2f);
		}

	}

	static final private Logger LOG = Logger.getLogger("StudentResultsGraph");
	
	public class ZoomFit implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			LOG.info("Zoom Fit");
			zoomFit();
		}

	}

	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {		  
		    Map<Object, Boolean> seen = new ConcurrentHashMap<>(); 
		    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null; 
		}

	static ColorStyle white  = new ColorStyle(255,255,255);
	static ColorStyle colorBlue1 = new ColorStyle(49, 71, 112);
	static ColorStyle colorBlue4 = new ColorStyle(180,195,228);
	static ColorStyle colorBlue2 = new ColorStyle(38, 115, 182);
	static ColorStyle colorGray3 = new ColorStyle(237, 239, 241);

	private static ColorStyle defaultNodeColor = colorBlue4;
	private static ColorStyle defaultTextColor = colorBlue1;
	private static ColorStyle defaultRectColor = new ColorStyle( colorGray3.getRGB()&0xFFFFFF | (186<<24));
	private static ColorStyle defaultRectBorderColor = new ColorStyle( colorGray3.getRGB()&0xFFFFFF | (90<<24));
	private static ColorStyle kennenNodeColor = new ColorStyle(255,255,150);
	private static ColorStyle kennenNodeBorderColor = new ColorStyle(255,200,150);

	private static int defaultFontSize = 16;
	private static String defaultFont = "Ubuntu";

	private static final ColorStyle succesColor = new ColorStyle(0,200,0);
	private static final ColorStyle halfSuccesColor = new ColorStyle(180,240,180);
	private static final ColorStyle failColor = new ColorStyle(200,0,0);
	private static final ColorStyle halfFailColor = new ColorStyle(255,150,150);
	private static final ColorStyle defaultEdgeColor = colorBlue4;
	
	private OMSVGDocument doc;
	protected SVGImage image;
	private String lang;
	
	private OMSVGPoint start;
	private static final String HIDDEN_NODE = "hidden-node";
	
	abstract class AbstractEdge {
		final OMSVGGElement g = doc.createSVGGElement();
		boolean blur;

		ColorStyle edgeColor = defaultEdgeColor;
		// opacity: 30/255
		ColorStyle blur(ColorStyle org) {
			if(!blur) return org;
			return new ColorStyle( org.getRGB()&0xFFFFFF | (30<<24));
		}

		void colorize() {
			String color = blur(edgeColor).getColor();
			OMSVGStyle style = g.getStyle();
			style.setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, color);
			style.setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, color);		
		}
		
	}
	
	class ChapterEdge extends AbstractEdge {
		final ChapterNode from, to;
		ChapterEdge(ChapterNode from, ChapterNode to) {
			g.setClassNameBaseVal(bundle.css().chapteredge());
			Objects.requireNonNull(from, "no from");
			Objects.requireNonNull(to, "no to");
			this.from = from;
			this.to = to;
		}
		void setVisible() {
			if (from.isVisible() && to.isVisible()) {
				g.removeClassNameBaseVal(HIDDEN_NODE);
			} else 
				g.addClassNameBaseVal(HIDDEN_NODE);
		}

		OMSVGGElement build() {
			float x1 = from.getCx();
			float y1 = from.getCy();
			float x2 = to.getCx();
			float y2 = to.getCy();
			OMSVGLineElement line = doc.createSVGLineElement(x1, y1, x2, y2);
	//		line.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "30");
			g.appendChild(line);
// triangle
			float x = (x1+x2)/2;
			float y = (y1+y2)/2;
			float dx = x1 - x2;
			float dy = y1 - y2;
			float len = (float) Math.hypot(dx, dy) / 90;
			dx /= len;
			dy /= len;
			x -= dx/2;
			y -= dy/2;
			OMSVGPathElement path = doc.createSVGPathElement();
			OMSVGPathSegList points = path.getPathSegList();
			points.appendItem(path.createSVGPathSegMovetoAbs(x, y));		
			points.appendItem(path.createSVGPathSegLinetoAbs(x + dx + dy/2, y + dy -dx/2));
			points.appendItem(path.createSVGPathSegLinetoAbs(x + dx - dy/2, y + dy +dx/2));
			points.appendItem(path.createSVGPathSegClosePath());
			g.appendChild(path);
			//colorize();
			return g;
		}

		@Override
		public int hashCode() {
			return Objects.hash(from, to);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ChapterEdge other = (ChapterEdge) obj;
			return Objects.equals(from, other.from) && Objects.equals(to, other.to);
		}
	}
	
	class PseudoEdge extends Edge {
		PseudoEdge(Node single) {
			super(single, single);
		}
		
		void reset() {
			from.setVoorkennis(false);
		}
	}
	
	class Edge extends AbstractEdge {
		final Node from, to;

		Edge(Node from, Node to) {
			Objects.requireNonNull(from, "no from");
			Objects.requireNonNull(to, "no to");
			
			this.from = from;
			this.to = to;
			g.setClassNameBaseVal(bundle.css().edge());
		}
		void setVisible() {
			if (isVisible()  ) {
				g.removeClassNameBaseVal(HIDDEN_NODE);
			} else
				setInvisible();
		}

		void setInvisible() {
			g.addClassNameBaseVal(HIDDEN_NODE);
		}

		boolean isVisible() {
			return from.isVisible() && to.isVisible() && ( from.isVoorkennis() || sameChapter() );
		}
		
		boolean sameChapter() {
			String bf = from.info.getBook();
			String bt = to.info.getBook();
			Integer cf = from.info.getChapter();
			Integer ct = to.info.getChapter();
			String mf = from.info.getMethod();
			String mt = to.info.getMethod();
			return Objects.equals(bf,bt) && Objects.equals(cf, ct) && Objects.equals(mf, mt);
		}
		

		Edge withVoorkennis() {
			if (from.isVoorkennis() && to.isVisible() && !to.isVoorkennis()) {
				g.removeChild(g.getFirstChild());
				g.removeChild(g.getFirstChild());
				setVisible();
				build();
				return this;
			}
			return null;
		}
		
		Edge withVoorkennisTree() {
			if (from.isVoorkennis() && to.isVoorkennis() && to.isVisible() && from.isVisible()) {
				blur = false;
				g.removeChild(g.getFirstChild());
				g.removeChild(g.getFirstChild());
				setVisible();
				build();
				return this;
			}
			setInvisible();
			return null;
		}

		void reset() {
			if (from.isVoorkennis()) from.setVoorkennis(false);
			from.setVisible(false);
			if(inVoorkennisTree && to.isVoorkennis()) {
				to.setVoorkennis(false);
				to.setVisible(false);
			}
			g.removeChild(g.getFirstChild());
			g.removeChild(g.getFirstChild());
			build();
		}
		
		OMSVGGElement build() {
			float x1 = from.cx;
			float y1 = from.cy;
			float x2 = to.cx;
			float y2 = to.cy;
			if (from.isVoorkennis()) { x1 = from.tmpx; y1 = from.tmpy; }
			if (to.isVoorkennis()) { x2 = to.tmpx; y2 = to.tmpy; }		
			OMSVGLineElement line = doc.createSVGLineElement(x1, y1, x2, y2);
			g.appendChild(line);
// triangle
			float x = (x1+x2)/2;
			float y = (y1+y2)/2;
			float dx = x1 - x2;
			float dy = y1 - y2;
			float len = (float) Math.hypot(dx, dy) / 6;
			dx /= len;
			dy /= len;
			x -= dx/2;
			y -= dy/2;
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

		
		void setSuccesFailColor() {
			OMSVGStyle style = g.getStyle();
			if (to.getEdgeColor() != null && from.getEdgeColor() != null) // beide succes en/of half
			{
				if (to.getEdgeColor() == succesColor && from.getEdgeColor() == succesColor)
					edgeColor = to.getEdgeColor();
				else
					edgeColor = halfSuccesColor;
				style.setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "5");
			}
			else 
			{
				edgeColor = defaultEdgeColor;
				style.setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "1.3");
			}
			colorize();
		}

		public void move() {
			g.removeChild(g.getFirstChild());
			g.removeChild(g.getFirstChild());
			build();
		}

	}

	abstract class AbstractNode {
		boolean blur;
		boolean visible = true;
		final protected OMSVGGElement g = doc.createSVGGElement();
		OMSVGCircleElement circle;
		OMSVGTextElement text;
		ColorStyle nodeColor = defaultNodeColor;
		ColorStyle nodeBorderColor = colorBlue2;
		ColorStyle textColor = defaultTextColor;
		ColorStyle edgeColor;
		float cx,cy;				
		float r;

		void setClassName(String name) {
			g.setClassNameBaseVal(name);
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

		void colorize() {
			String color = blur(nodeColor).getColor();
			circle.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, color);
			circle.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, blur(nodeBorderColor).getColor());
 			color = blur(textColor).getColor();
 			text.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, color);

		}

		protected boolean isBlur() {
			return blur;
		}

		void setVisible(boolean b) {
			visible = b;
			if (b) 
				g.removeClassNameBaseVal(HIDDEN_NODE);
			else
				g.addClassNameBaseVal(HIDDEN_NODE);
		}
		
		boolean isVisible() {
			return visible;
		}
		
	}
	
	class BookNode extends AbstractNode implements ClickHandler {
		final DomStudentModelMethodInfo info;
		private List<ChapterNode> list = new ArrayList<>();
		public float getCx() {
			return this.cx / list.size();
		}
		public float getCy() {
			return this.cy / list.size();
		}

		BookNode(String method, String book) {
			setClassName(bundle.css().booknode());
			info = new DomStudentModelMethodInfo(method, book, null);
			r = 700f;
			textColor = colorGray3;
			nodeBorderColor = nodeColor = new ColorStyle(222, 229, 240);
		}

		void add(ChapterNode obj) {
			list.add(obj);
			this.cx += obj.getCx();
			this.cy += obj.getCy();
		}

		OMSVGGElement build() {
			float cx = getCx();
			float cy = getCy();
			circle = doc.createSVGCircleElement(cx, cy, r);
			circle.addClickHandler(this);
			short unitType = OMSVGLength.SVG_LENGTHTYPE_NUMBER;
			text = doc.createSVGTextElement(cx, cy, unitType, info.getBook());
			text.addClickHandler(this);
// 			text.getStyle().setSVGProperty(SVGConstants.CSS_TEXT_ANCHOR_PROPERTY, "middle");
// 			text.getStyle().setSVGProperty(SVGConstants.CSS_DOMINANT_BASELINE_PROPERTY, "central");
// 			text.getStyle().setSVGProperty(SVGConstants.CSS_FONT_FAMILY_PROPERTY, defaultFont);
// 			text.getStyle().setSVGProperty(SVGConstants.CSS_FONT_SIZE_PROPERTY, Integer.toString(defaultFontSize*44));
// 			colorize();
 			g.appendChild(circle);
 			g.appendChild(text);
 			return g;
		}
		
		void setVisible() {
			setVisible(list.stream().anyMatch(ChapterNode::isVisible));
		}

		void hide() { setVisible(false); }

		@Override
		public void onClick(ClickEvent event) {
			doFilterFit(info);
		}
		
	}
	
	
	
	
	
	static final double CHAPTER_ZOOM = SMALL;
	class ChapterNode extends AbstractNode implements ClickHandler {
		final private DomStudentModelMethodInfo info;
		private List<Node> list = new ArrayList<>();
		String longText, shortText;
		boolean longer;
		
		void setText(boolean longer) {
			if (longer != this.longer) {
				this.longer = longer;
				String t = longer ? longText: shortText;
				text.getFirstChild().setNodeValue(t);
			}
		}
		
		ChapterNode(DomStudentModelMethodInfo info) {
			setClassName(bundle.css().chapternode());
			this.info = new DomStudentModelMethodInfo(info);
			this.info.setX(0);
			this.info.setY(0);
			r = 150f;
			textColor = white;
			nodeBorderColor = nodeColor = colorBlue4;
			
			shortText = "H" + info.getChapter();
			longText = shortText;
			DomMethod method = title.getMethod();
			if (method != null && method.key().equals(info.getMethod()))
			for (int i = 0; i < method.books.size(); i++) {
				if (info.getBook().equals(method.books.get(i))) {
					int j = info.getChapter() - 1;
					longText = method.chapters.get(i).get(j);
					break;
				}
			}
		}
		
		public float getCx() {
			return this.cx / list.size();
		}
		public float getCy() {
			return this.cy / list.size();
		}

		void add(Node obj) {
			if (obj.invalid()) return;
 			list.add(obj);
			this.cx += obj.cx;
			this.cy += obj.cy;
		}
		
		OMSVGGElement build() {
			float cx = getCx();
			float cy = getCy();
			circle = doc.createSVGCircleElement(cx, cy, r);
			circle.addClickHandler(this);
			short unitType = OMSVGLength.SVG_LENGTHTYPE_NUMBER;
			String t = longer ? longText: shortText;
			text = doc.createSVGTextElement(cx, cy, unitType, t);
			text.addClickHandler(this);
 			colorize();
 			g.appendChild(circle);
 			g.appendChild(text);
 			return g;
		}
		
		void setVisible() {
			setVisible(list.stream().anyMatch(Node::isVisible));
		}
		void hide() {
			setVisible(false);
		}

		@Override
		public void onClick(ClickEvent event) {
			GWT.log("click in " + info.key());
			doFilterFit(info);
		}

		@Override
		void colorize() {
		}
	}

	static String strip(String id) {
		return id.split("/",2)[0];
	}
	
	public class Node extends AbstractNode implements ClickHandler {
		final private DomStudentModelMethodInfo info;
		final protected DomStudentModelObj obj;
		final private boolean kennen;
		private OMSVGRectElement rect;
		private boolean voorkennis;
		private float tmpx, tmpy;
		private String parent = "";
		
//		public String toString() {
//			return "Node[" + obj.getInfo().getId() + "," + obj.getInfo().getTitle().get(lang) + "]";
//		}
		
		void move(float dx, float dy) {
			moveTo(tmpx+dx, tmpy+dy);
		}

		public String uuid() {
			return obj.getInfo().getId();
		}
		
		public void transform(OMSVGMatrix ctm) {
			OMSVGPoint p = getSvgElement().createSVGPoint(tmpx, tmpy);
			p = p.matrixTransform(ctm);
			moveTo(p.getX(), p.getY());
					
		}
		
		void moveTo(float x, float y) {
			if (voorkennis) {
				tmpx = x;
				tmpy = y;				
			} else {
				cx = x;
				cy = y;
			}
			circle.getCx().getBaseVal().setValue(x);
			circle.getCy().getBaseVal().setValue(y);
			text.getX().getBaseVal().getItem(0).setValue(x);
			text.getY().getBaseVal().getItem(0).setValue(y);
			OMSVGRect bbox = text.getBBox();
			rect.getX().getBaseVal().setValue(bbox.getX());
			rect.getY().getBaseVal().setValue(bbox.getY());
			
		}
		
		
		public Node(DomStudentModelObj obj, DomStudentModelMethodInfo info, String parent) {
			setClassName(bundle.css().node());
			this.obj = obj;
			Integer nodeSize = obj.getInfo().getNodeSize();
			r = nodeSize != null ? nodeSize/2 : 12;
			this.info = info;
			float cx,cy;
 			try {
				cx = info.getX().floatValue(); // possible NPE
			} catch (Exception e) {
				cx = Float.NaN;
			}
 			this.cx = cx;
 			try {
				cy = info.getY().floatValue();
			} catch (Exception e) {
				cy = Float.NaN;
			}
 			this.cy = cy;
			String descr = StudentModelPresenter.getTitle(obj.getInfo(),lang);
			kennen = descr.startsWith("W:");
 			if(invalid()) return;
 			this.parent = parent;
			circle = doc.createSVGCircleElement(cx, cy, r);
			short unitType = OMSVGLength.SVG_LENGTHTYPE_NUMBER;
			if (kennen) {
				nodeColor = kennenNodeColor;
				nodeBorderColor = kennenNodeBorderColor;
				g.addClassNameBaseVal(bundle.css().kennen());
			}
			text = doc.createSVGTextElement(cx, cy, unitType, parent + descr);
		}

		public Node addClickHandler() {
			if (!invalid()) {
				text.addClickHandler(this);
				circle.addClickHandler(this);
			}
			return this;
		}
		
		public void setVoorkennis(boolean b) {
			voorkennis = b;
			if (b) {
				moveTo(tmpx, tmpy);
			} else {
				moveTo(cx, cy);
			}
		}
		public boolean isVoorkennis() {
			return voorkennis;
		}
		
		OMSVGGElement build() {
			if (invalid()) {
				super.setVisible(false);
				return g;
			}
			
 			g.appendChild(circle);
 // Needed for bbox calculation
 			text.getStyle().setSVGProperty(SVGConstants.CSS_TEXT_ANCHOR_PROPERTY, "middle");
 			text.getStyle().setSVGProperty(SVGConstants.CSS_DOMINANT_BASELINE_PROPERTY, "central");
 			text.getStyle().setSVGProperty(SVGConstants.CSS_FONT_FAMILY_PROPERTY, defaultFont);
 			text.getStyle().setSVGProperty(SVGConstants.CSS_FONT_SIZE_PROPERTY, Integer.toString(defaultFontSize));
 			g.appendChild(text);
 			OMSVGRect box = text.getBBox();
 			rect = doc.createSVGRectElement(box);
 			rect.addClickHandler(this);
			g.insertBefore(rect, circle);
			colorize();
			return g;
		}

		private boolean invalid() {
			return Float.isNaN(cx)||Float.isNaN(cy);
		}

		
		
		Stream<Edge> edges() {
			List<String> voorkennis = obj.getInfo().getVoorkennis();
			if (voorkennis == null|| invalid()) return Stream.empty();
			return voorkennis.stream()
					.map(StudentResultsGraph::strip)
					.filter(key -> map.containsKey(key))
					.flatMap( key -> 
						map.get(key).stream().map(n -> new Edge(n, this)) );
		}

		public void setScore(DomStudentModelScore<?> cat) {
			double succesFailScore = cat.getScore() * 100;
			edgeColor = null;
			if(succesFailScore < 25) 
				nodeColor = failColor;
			else if(succesFailScore <= 45) 
				nodeColor = halfFailColor;
			else if(succesFailScore > 45 && succesFailScore < 55)
			{
				//nodeColor = new ColorStyle(0xFFFFFF);

				if (kennen) {
					nodeColor = kennenNodeColor;
				} else {
					nodeColor = defaultNodeColor;
				}
			
			}
			else if(succesFailScore < 75 && succesFailScore >= 55) 
				edgeColor = nodeColor = halfSuccesColor;
			else 
				edgeColor = nodeColor = succesColor;			
		}

		ColorStyle getEdgeColor() {
			return edgeColor;
		}

		@Override
		void setVisible(boolean b) {
			if(!invalid())
				super.setVisible(b);
		}

		@Override
		protected void colorize() {
			if (!invalid())
			{
				if (blur) g.addClassNameBaseVal(bundle.css().blur());
				else g.removeClassNameBaseVal(bundle.css().blur());
				super.colorize();
			}
		}

		protected void setBlur(boolean blur) {
			this.blur = blur;
			colorize();
		}
		@Override
		public void onClick(ClickEvent event) {
			hideDescription();
			final int popupWidth = Math.min(436, Window.getClientWidth()-20); // 20 is marge: randjes, etc
			popup = new DialogBox(true, true);
			SafeHtmlBuilder builder = new SafeHtmlBuilder();
			builder.appendEscaped(parent + StudentModelPresenter.getTitle(obj.getInfo(),lang));
			popup.getCaption().setHTML(builder.toSafeHtml());
			DialogBox.Caption cap = popup.getCaption();
			cap.asWidget().addDomHandler(e -> {
				if (e.getX() > popupWidth - 36)
					popup.hide();
			}, ClickEvent.getType());
			popup.setTitle(StudentModelPresenter.getTitle(obj.getInfo(),lang));
			popup.setStyleDependentName("Node", true);
			popup.setGlassEnabled(true);
			popup.setGlassStyleName("score-frame-Glass");
			popup.getElement().getStyle().setZIndex(10000);
			int popupHeight = Math.min(400, Window.getClientHeight()-40); // xx is verticale marge, randjes, titel
			description.get(current, obj.getInfo()).then(p -> {
				Widget w = p.getValue();
				w.addStyleDependentName("Graph");
				w = new ScrollPanel(w);
				popup.add(w);
				w.setPixelSize(popupWidth, popupHeight);
				popup.center();
				return null;
			}, p -> { 
				Widget w = new Label(p.getFailure().toString());
				w.addStyleDependentName("Graph");
				w.setPixelSize(popupWidth, popupHeight);
				popup.add(w);
				popup.center();
			});
		}
		
	}
	
	private DialogBox popup;
	protected Map<String, List<Node>> map;
	private Map<String, ChapterNode> chapters;
	private Map<String, BookNode> books;
	private Set<Edge> edges;
	private Set<ChapterEdge> chapterEdges;
	
	protected Button zoomFitBtn, zoomInBtn, zoomOutBtn, voorkennisBtn, verbergBtn;
	private DockLayoutPanel voorkennistitle;
	protected FilterTitle title;
		
	final private DescriptionPresenter description;
	private DomStudentModelContext4Student current;

	
	@Inject protected StudentResultsGraph(DescriptionPresenter d) {
		description = d;
		
		bundle.css().ensureInjected();
		
		getElement().getStyle().setMarginLeft(20, Unit.PX);
		getElement().getStyle().setMarginRight(22, Unit.PX);
		
		doc = OMSVGParser.currentDocument();
		image = new SVGImage();
		image.setSvgElement(doc.createSVGSVGElement());
		image.setClassNameBaseVal(bundle.css().normal());
		getSvgElement().setViewBox(0, 0, 500, 500);
		
		add(image);
		setWidgetTopHeight(image, 2, Unit.EM, 500, Unit.PX);
		setWidgetLeftWidth(image, 0, Unit.PX, 500, Unit.PX);
		zoomFitBtn = new Button("\u25a2");
		add(zoomFitBtn);
		setWidgetTopHeight(zoomFitBtn, 3, Unit.EM, 2, Unit.EM);
		zoomInBtn = new Button("+");
		add(zoomInBtn);
		setWidgetTopHeight(zoomInBtn, 6, Unit.EM, 2, Unit.EM);
		zoomOutBtn = new Button("-");
		add(zoomOutBtn);
		setWidgetTopHeight(zoomOutBtn, 9, Unit.EM, 2, Unit.EM);
		voorkennisBtn = new Button("Voorkennis");
		add(voorkennisBtn);
		setWidgetTopHeight(voorkennisBtn, 3, Unit.EM, 2, Unit.EM);
		setWidgetRightWidth(zoomFitBtn, 1, Unit.EM, 2, Unit.EM);
		setWidgetRightWidth(zoomInBtn, 1, Unit.EM, 2, Unit.EM);
		setWidgetRightWidth(zoomOutBtn, 1, Unit.EM, 2, Unit.EM);
		setWidgetRightWidth(voorkennisBtn, 4, Unit.EM, 10, Unit.EM);
		
		zoomFitBtn.setStylePrimaryName("graph-Button");
		zoomInBtn.setStylePrimaryName("graph-Button");
		zoomOutBtn.setStylePrimaryName("graph-Button");		
		voorkennisBtn.setStylePrimaryName("dwo-Button");
		initTitle();
		
		voorkennistitle = new DockLayoutPanel(Unit.EM);
		voorkennistitle.getElement().getStyle().setBackgroundColor("#1B75BB");
		add(voorkennistitle);
		
		setWidgetLeftRight(voorkennistitle, 0, Unit.EM, 0, Unit.EM);
		setWidgetTopHeight(voorkennistitle, 0, Unit.EM, 2, Unit.EM);
		setWidgetVisible(voorkennistitle, false);
		verbergBtn = new Button("X");
		verbergBtn.setStylePrimaryName("graph-Button");
		voorkennistitle.addEast(verbergBtn, 3);
		Label vtt = new Label("Voorkennis");
		Style style = vtt.getElement().getStyle();
		style.setPaddingTop(0.2, Unit.EM);
		style.setColor("white");
		style.setTextAlign(TextAlign.CENTER);
		style.setFontSize(20, Unit.PX);
		voorkennistitle.add(vtt);
		
		
		lang = LocaleInfo.getCurrentLocale().getLocaleName();
		map = new HashMap<>();
		chapters = new HashMap<>();
		books = new HashMap<>();
		edges = Collections.emptySet();
		chapterEdges = Collections.emptySet();
		getElement().getStyle().setBackgroundColor(colorGray3.getColor());
		
		initHandlers();

	}

	protected void initTitle() {
		title = createFilterTitle();
		title.setFilter(new FilterConsumer());
		add(title);
		
		
		setWidgetLeftRight(title, 0, Unit.EM, 0, Unit.EM);
		setWidgetTopHeight(title, 0, Unit.EM, 2, Unit.EM);
	}

	protected FilterTitle createFilterTitle() {
		return new FilterTitle(null);
	}

	protected void initHandlers() {
		image.addMouseMoveHandler(this);
		image.addMouseUpHandler(this);
		image.addMouseDownHandler(this);
		image.addMouseOutHandler(this);
		
		zoomFitBtn.addClickHandler(new ZoomFit());
		zoomOutBtn.addClickHandler(new Zoom(true));
		zoomInBtn.addClickHandler(new Zoom(false));
		voorkennisBtn.addClickHandler(new Voorkennis());
		verbergBtn.addClickHandler(new VerbergVoorkennis());
		
		
		addDomHandler(this, ContextMenuEvent.getType());
	}

	PopupPanel popupMenu;
	
	@Override
	public void onContextMenu(ContextMenuEvent event) {
		start = null;
		int x = event.getNativeEvent().getClientX();
		int y = event.getNativeEvent().getClientY();
		LOG.info("on context menu hit "  + x);
		OMSVGPoint point = getSvgElement().createSVGPoint(x, y);
		OMSVGMatrix ctm = getSvgElement().getScreenCTM().inverse();
		point = point.matrixTransform(ctm);
		float sx = point.getX();
		float sy = point.getY();
		// blur nodes and edges: find node, focus on node and edges.
		Optional<Node> find = nodeStream().filter(node -> node.isVisible() && node.contains(sx, sy)).findAny();
		LOG.info(find.toString());	
		if (find.isPresent())
		{
			 {
				popupMenu = new PopupPanel(true, true);
				VerticalPanel vertical = new VerticalPanel();
				popupMenu.getElement().getStyle().setZIndex(9999);
				Label item = new Label("Toon alle voorkennis");
				item.setStylePrimaryName("pseudobutton");
				popupMenu.setWidget(vertical);
				vertical.add(item);
				item.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						nodeStream().forEach(n -> n.setVisible(false));
						chapters.values().forEach(ChapterNode::hide);
						chapterEdges.forEach(ChapterEdge::setVisible); // hide all
						books.values().forEach(BookNode::hide);
						
						List<Set<Node>> voorkennistree = new ArrayList<>();
						Set<Node> to = Stream.of(find.get()).collect(Collectors.toSet());
						String method = find.get().info.getMethod();
						Set<Node> from;
						do { voorkennistree.add(to);
							 from = getVoorkennis(to, method);
							 to = from;
						} while(!to.isEmpty());
						ListIterator<Set<Node>> last = voorkennistree.listIterator(voorkennistree.size());
						while(last.hasPrevious()) {
							Set<Node> items = last.previous();
							ListIterator<Set<Node>>less = voorkennistree.listIterator(last.nextIndex());
							while(less.hasPrevious()) {
								less.previous().removeAll(items);
							}
						}
						
						voorkennistree.stream().flatMap(Set::stream).forEach(n -> {
							n.tmpx = n.cx; n.tmpy = n.cy;
							n.setVoorkennis(true);
							n.setVisible(true);
							n.setBlur(false);
						});
						
						int i,j;
						i = 0;
						int treesize = voorkennistree.size();
						int width = getOffsetWidth();
						int height = getOffsetHeight();
						factor = 0.75f;
						for(Set<Node> set : voorkennistree) {
							j = 0;
							int setsize = set.size();
							for (Node node: set) {
								// i, j, treesize, setsize;
								int x = -20+i%2*40 + 100 + (j+1)*(width-200)/(setsize+1);
								int y = -7*setsize+15*j + (treesize - (i))*(height-50)/(treesize);
								node.moveTo(x, y);
								
								j++;
							}
							i++;
						}
												
						voorkennisEdges = edges.stream()
								.map(Edge::withVoorkennisTree)
								.filter(Objects::nonNull).collect(Collectors.toList());
						inVoorkennis = true; // zonder ..
						inVoorkennisTree = true;
						setVoorKennisVisible(false);
						title.showClose(this::run);
						popupMenu.hide();
						zoomFit();
						popupMenu = null;
						start = null;
						if (voorkennisEdges.isEmpty()) {
							voorkennisEdges.add(new PseudoEdge(find.get()));
						}
						
					}

					
					
					private Set<Node> getVoorkennis(Set<Node> to, String method) {						
						return to.stream()
								.flatMap(node -> edges.stream().filter(e -> e.to == node).map( e-> e.from))
								.filter(n -> Objects.equals(method, n.info.getMethod())) // within same method????
								.filter(distinctByKey(Node::uuid))
								.collect(Collectors.toSet());
					}
					
					public void run() {
						verbergVoorkennis();
						unBlur();
					}
				});
				item = new Label("Toon directe voorkennis");
				item.setStylePrimaryName("pseudobutton");
				vertical.add(item);
				item.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						popupMenu.hide();
						popupMenu = null;
						start = null;
						// blur nodes and edges: find node, focus on node and edges.
//						Optional<Node> find = nodeStream().filter(node -> node.isVisible() && node.contains(sx, sy)).findAny();
//						if (find.isPresent()) {
							LOG.info("ON present " + StudentModelPresenter.getTitle(find.get().obj.getInfo(),lang)  + " at " + x + " , " + y);
							Node node = find.get();
							Set<Edge> set = edges.stream().filter(edge -> edge.to == node).collect(Collectors.toSet());
							Set<Node> nodes = set.stream().map(t -> t.from).collect(Collectors.toSet());
							nodes.add(node);
							edges.forEach(e -> e.setBlur(!set.contains(e)));
							nodeStream().forEach(n -> n.setBlur(!nodes.contains(n)));			
//						} else
						
					} 
				});
			 
				}
			 
			 
			popupMenu.setPopupPosition(event.getNativeEvent().getClientX(),
	                 event.getNativeEvent().getClientY());
	        popupMenu.show();
			
			
		}
		event.preventDefault();
	}

	private void doFilter(DomStudentModelMethodInfo info) {
		if (info.getChapter() == null) {
			doFilter(Collections.singletonMap(info.getMethod(), Collections.singletonMap(info.getBook(), Collections.emptySet())));
		} else {
			doFilter(Collections.singletonMap(info.getMethod(), Collections.singletonMap(info.getBook(), Collections.singleton(info.getChapter()))));	
		}
	}

	public void doFilter(Map<String, Map<String, Set<Integer>>> f) {
		title.accept(f);
		if (f == null) return;
		if (inVoorkennis) {
			verbergVoorkennis();
			unBlur();
		}
		filter = f;
		boolean showchapters = true;
		boolean showbooks = true;
		if (f.isEmpty()) {
			for(List<Node> n: map.values()) for(Node node: n) node.setVisible(true);
		    edges.forEach(Edge::setVisible);
		    chapters.values().forEach(ChapterNode::setVisible);
		    chapterEdges.forEach(ChapterEdge::setVisible);
		    books.values().forEach(BookNode::setVisible);
		    setVoorKennisVisible(false);
		    return;
		} else if (f.size() == 1) {
			String key = f.keySet().iterator().next();
			if (f.get(key).size() == 1) {
				showbooks = false;
				Set<Integer> chapters = f.get(key).values().iterator().next();
				if (chapters.size() == 1) {
					showchapters = false;
				} else {
				}			
			} else {
			}
		} else 
		{
		}
		Iterator<Node> i =  nodeStream().iterator();
		while (i.hasNext()) {
			Node n = i.next();
			boolean ok = StudentResultsPresenter.inFilter(f, n.info, title.getMethod());
			n.setVisible(ok);
		}
		setVoorKennisVisible(!showchapters);
	    edges.forEach(Edge::setVisible);
	    if(showchapters) chapters.values().forEach(ChapterNode::setVisible);
	    else chapters.values().forEach(ChapterNode::hide);
	    chapterEdges.forEach(ChapterEdge::setVisible);
	    if (showbooks) books.values().forEach(BookNode::setVisible);
	    else books.values().forEach(BookNode::hide);
	}

	private OMSVGSVGElement getSvgElement() {
		return image.getSvgElement();
	}

	
	public void clear() {
		hideDescription();
		popup = null;
	}
	
	private boolean nooverlap(Edge e) {
		DomStudentModelMethodInfo toc = e.to.info;
		if (sameChapter(toc, e.from.info)) 
			return false; // shortcut.
		DomStudentModelObj from = e.from.obj;
		List<DomStudentModelMethodInfo> fromInfos = from.getInfo().getMethodInfo();
		for (DomStudentModelMethodInfo info: fromInfos) {
		      if(sameChapter(toc, info)){
		    	  return false;
		      }
		}

		toc = e.from.info;
		fromInfos = e.to.obj.getInfo().getMethodInfo();

		for (DomStudentModelMethodInfo info: fromInfos) {
		      if(sameChapter(toc, info)){
		    	  return false;
		      }
		}

		return true;
	}

	boolean sameChapter(DomStudentModelMethodInfo toc, DomStudentModelMethodInfo info) {
		return info.getBook().equals(toc.getBook()) && info.getMethod().equals(toc.getMethod()) && info.getChapter().equals(toc.getChapter());
	}
	
	
	
	public void setModelScore(DomStudentModelContext4Student item, Promise<DomStudentModelDataScore> score, DomMethod domMethod) {
		this.current = item;
		title.setMethod(domMethod);
		map.clear();
		chapters.clear();
		books.clear();
		clear();
		setModel(item.getModelStructure());
		OMSVGSVGElement svg = getSvgElement();
		while(svg.getChildNodes().getLength()>0)
			svg.removeChild(svg.getChildNodes().getItem(0));
		
		edges = nodeStream().flatMap(Node::edges).collect(Collectors.toSet());
// chapters
		nodeStream()
			.forEach(node -> {
				if (node.info.getMethod() == null) return;
				String key = node.info.key();
				ChapterNode chap = chapters.computeIfAbsent(key, k -> new ChapterNode(node.info));
				chap.add(node);				
			});
		chapterEdges = edges.stream()
				.filter(this::nooverlap)
				.map( e -> {
			DomStudentModelMethodInfo fromInfo = e.from.info;
			String from = fromInfo.key();
			DomStudentModelMethodInfo toInfo = e.to.info;
			String to   = toInfo.key();
			if (from.equals(to) || !Objects.equals(fromInfo.getMethod(), toInfo.getMethod()) || !Objects.equals(toInfo.getBook(), fromInfo.getBook())) return null;
			return new ChapterEdge(chapters.get(from), chapters.get(to));			
		}).filter(Objects::nonNull).collect(Collectors.toSet());
		
		
// books 
		chapters.values().forEach(chp -> {
			String method = chp.info.getMethod();
			String book = chp.info.getBook();
			String key = method + "-" + book;
			BookNode bk = books.computeIfAbsent(key, k -> new BookNode(method, book));
			bk.add(chp);
		});
		
		books.values().stream().map(BookNode::build).forEach(svg::appendChild);

		chapterEdges.stream().map(ChapterEdge::build).forEach(svg::appendChild);
		chapters.values().forEach(chap -> { 
			svg.appendChild(chap.build());
		});
		edges.stream().map(Edge::build).forEach(t -> svg.appendChild(t));
		nodeStream().forEach(t -> {svg.appendChild(t.g); t.build(); }); // THIS ORDER eerst er in hangen, dan pas build.
		doFilter(item.getFilter());
		score.then(this::withScore).onResolve(this::zoomFit);
		
	}

	private Promise<DomStudentModelDataScore> withScore(Promise<DomStudentModelDataScore> p) {
		DomStudentModelDataScore s = p.getValue();
		DomStudentModelStructureScore score = s.getDomStudentModelStructureScore();
		for(DomStudentModelCategoryScore cat : score.getCategories()) {
			withScore(cat);
		}
		edges.forEach(Edge::setSuccesFailColor);
		unBlur();			
		return p;
	}	
	
	private void withScore(DomStudentModelScore<?> cat) {
		List<? extends DomStudentModelScore> children = cat.getChildren();
		if (children != null) {
			for( DomStudentModelScore<?> obj : children) { withScore(obj); }
		}
		String id = cat.getId();
		List<Node> n = map.get(id);
		if (n != null) n.forEach( nn -> nn.setScore(cat));
	}

	private void setModel(DomStudentModelStructure modelStructure) {
		for(DomStudentModelCategory cat: modelStructure.getCategories()) {
			setModel(cat);
		}
		
	}

	private void setModel(DomStudentModelCategory cat) {
		for (DomStudentModelObj obj: cat.getObjectives()) {
			setModel(obj, cat.getInfo());			
		}
	}

	private boolean setModel(DomStudentModelObj obj, DomStudentModelContextInfo parent) {
		if (obj.getObjectives() == null) {
			String id = obj.getInfo().getId();

			if (obj.getInfo().getMethodInfo() == null) {
				obj.getInfo().setMethodInfo(new ArrayList<>());
			}
			List<DomStudentModelMethodInfo> methodInfo = obj.getInfo().getMethodInfo();
			if (methodInfo.isEmpty()) {
				Map<String, Map<String, Set<Integer>>> meth = obj.getInfo().getMethods();
				if (meth.isEmpty()) {
					DomStudentModelMethodInfo m = new DomStudentModelMethodInfo();
					methodInfo.add(m);
					m.setX(obj.getInfo().getX());
					m.setY(obj.getInfo().getY());
				} else {
					for(String m: meth.keySet()) {
						Map<String, Set<Integer>> mm = meth.get(m);
						for (String b: mm.keySet()) {
							Set<Integer> chps = mm.get(b);
							chps.forEach(chp -> {
								DomStudentModelMethodInfo mi = new DomStudentModelMethodInfo(m, b, chp);
								mi.setX(obj.getInfo().getX());
								mi.setY(obj.getInfo().getY());
								methodInfo.add(mi);
							});
						}
					}
				}
 			}
			final String p = parentOf(parent);
			String method = title.getMethod().key();
			if (method == null) {
				DomStudentModelMethodInfo info = new DomStudentModelMethodInfo();
				info.setX(obj.getInfo().getX());
				info.setY(obj.getInfo().getY());
				map.put( id, Collections.singletonList( nodeFactory(obj, p, info)));
			} else {
				map.put( id, methodInfo.stream()
					.filter(info -> Objects.equals(method, info.getMethod()))
					.map(info -> nodeFactory(obj, p, info))
					.filter(t -> !t.invalid())
					.collect(Collectors.toList()));
			}
			return true;
		}
		for (DomStudentModelObj leaf : obj.getObjectives()) setModel(leaf, obj.getInfo());
		return false;
	}

	protected Node nodeFactory(DomStudentModelObj obj, final String p, DomStudentModelMethodInfo info) {
		return new Node(obj, info, p).addClickHandler();
	}
	
	protected Node nodeFactory(DomStudentModelObj obj, DomStudentModelMethodInfo info) {
		return nodeFactory(obj, "", info);
	}

	private String parentOf(DomStudentModelContextInfo info) {
		String parent = StudentModelPresenter.getTitle(info,lang);
		int col = parent.indexOf(':');
		if (col < 0)
			parent = "";
		else
			parent = parent.substring(0, col) + " - ";		
		return parent;
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if (start == null)
			mouseMove(event);
		else
			mouseDrag(event);
	}

	private void mouseMove(MouseEvent<?> event) {
		if (inVoorkennisTree) return; // GEEN BLUR!
		float x = event.getClientX();
		float y = event.getClientY();
		OMSVGPoint point = getSvgElement().createSVGPoint(x, y);
		OMSVGMatrix ctm = getSvgElement().getScreenCTM().inverse();
		point = point.matrixTransform(ctm);
		float sx = point.getX();
		float sy = point.getY();
//		// blur nodes and edges: find node, focus on node and edges.
//		Optional<Node> find = nodeStream().filter(node -> node.isVisible() && node.contains(sx, sy)).findAny();
//		if (find.isPresent()) {
//			LOG.info("ON present " + StudentModelPresenter.getTitle(find.get().obj.getInfo(),lang)  + " at " + x + " , " + y);
//			Node node = find.get();
//			Set<Edge> set = edges.stream().filter(edge -> edge.to == node).collect(Collectors.toSet());
//			Set<Node> nodes = set.stream().map(t -> t.from).collect(Collectors.toSet());
//			nodes.add(node);
//			edges.forEach(e -> e.setBlur(!set.contains(e)));
//			nodeStream().forEach(n -> n.setBlur(!nodes.contains(n)));			
//		} else
		{
			unBlur();
		}		
	}

	private void unBlur() {
		edges.forEach(e -> e.setBlur(false));
		nodeStream().forEach(n -> n.setBlur(false));
	}

	private Stream<Node> nodeStream() {
		return map.values().stream().flatMap(List::stream);
	}

	private void mouseDrag(MouseEvent<?> event) {
		float x = event.getClientX();
		float y = event.getClientY();
		OMSVGMatrix ctm = image.getSvgElement().getScreenCTM();
		float dx = (x - start.getX()) / ctm.getA();
		float dy = (y - start.getY()) / ctm.getD();
		if (dx == 0 && dy == 0) return;
		
		OMSVGRect viewbox = image.getSvgElement().getViewBox().getBaseVal();
		viewbox.setX(viewbox.getX()-dx);
		viewbox.setY(viewbox.getY()-dy);
		image.getSvgElement().setViewBox(viewbox);
		start = image.getSvgElement().createSVGPoint(x, y);
		
		LOG.info("move delta " + dx + ", " + dy +  "ctm=" + ctm.getA() + " , " + ctm.getD());
		if (!inVoorkennisTree) {
			voorkennisEdges.stream().map(t -> t.from).distinct().forEach(t -> t.move(-dx,  -dy));
			voorkennisEdges.forEach(Edge::move);
		}
	}
	
	
	@Override
	public void onMouseUp(MouseUpEvent event) {
		if(start != null) {
			mouseDrag(event);
			start = null;
		}
		mouseMove(event);
		event.preventDefault();
		
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		start = null;
		mouseMove(event);
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		start = image.getSvgElement().createSVGPoint(event.getClientX(), event.getClientY());
		mouseMove(event);
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.LayoutPanel#onResize()
	 */
	@Override
	public void onResize() {
		super.onResize();
		resizer();
	}

	protected void resizer() {
		resize(factor);
	}

	int imagewidth, imageheight;
	private void resize(float f) {
		f = Math.max(f, 0.5f);
		image.setClassNameBaseVal(cssSize(1/f));
		if (imagewidth != getOffsetWidth() || imageheight != getOffsetHeight() || f != factor) {
			LOG.info("factor = " + 1/f);
			imageheight = getOffsetHeight();
			int svgheight = imageheight - TITLE_HEIGHT;
			setWidgetTopHeight(image, 2, Unit.EM, svgheight , Unit.PX);
			setWidgetLeftWidth(image, 0, Unit.PX, imagewidth = getOffsetWidth(), Unit.PX);
			factor = f;
			OMSVGRect rect = getSvgElement().createSVGRect(0, 0, imagewidth*factor, svgheight*factor);
			OMSVGRect baseVal = getSvgElement().getViewBox().getBaseVal();
			float x = baseVal.getX();
			rect.setX(x);
			float y = baseVal.getY();
			rect.setY(y);
// keep center 			
			float offx = rect.getCenterX()-baseVal.getCenterX();
			rect.setX(x - offx);
			float offy = rect.getCenterY()-baseVal.getCenterY();
			rect.setY(y - offy);
			OMSVGMatrix ctm = getSvgElement().getScreenCTM();
			getSvgElement().setViewBox(rect);
			OMSVGMatrix ctmfinal = getSvgElement().getScreenCTM().inverse().multiply(ctm);
			LOG.info("ctm.a = " + ctmfinal.getA());
			if(!inVoorkennisTree) {
				voorkennisEdges.stream().map(t -> t.from).distinct().forEach(n -> n.transform(ctmfinal));
				voorkennisEdges.forEach(Edge::move);
			}
			boolean longer = 1/f < CHAPTER_ZOOM;
			chapters.values().forEach(item -> item.setText(longer));
			
		} else {
			LOG.info("break recursion");
		}
	}

	private void verbergVoorkennis() {
		setWidgetTopHeight(title, 0, Unit.EM, 2, Unit.EM);
		setWidgetTopHeight(zoomFitBtn, 3, Unit.EM, 2, Unit.EM);
		setWidgetTopHeight(zoomInBtn, 6, Unit.EM, 2, Unit.EM);
		setWidgetTopHeight(zoomOutBtn, 9, Unit.EM, 2, Unit.EM);
		setWidgetTopHeight(voorkennisBtn, 3, Unit.EM, 2, Unit.EM);
		setWidgetVisible(voorkennistitle, false);
		setVoorKennisVisible(true);
		OMSVGRect viewbox = image.getSvgElement().getViewBox().getBaseVal();
		float dy = VOORKENNIS_HEIGHT;
		OMSVGMatrix ctm = image.getSvgElement().getScreenCTM();
		dy /= ctm.getA();
		viewbox.setY(viewbox.getY()+dy);
		image.getSvgElement().setViewBox(viewbox);
		inVoorkennis = false;
		voorkennisEdges.forEach(Edge::reset);
		edges.forEach(Edge::setVisible);
		inVoorkennisTree = false;
		voorkennisEdges = Collections.emptySet();
	}

	protected void zoomFit() {
		Collection<List<Node>> nodes = map.values();
		int imagewidth = getOffsetWidth();
		int imageheight = getOffsetHeight() - TITLE_HEIGHT;
		OMSVGRect r = null;
		for(List<Node> nodeList: nodes) 
		  for(Node node: nodeList){
			if (node.isVisible()) {
				OMSVGRectElement rect0 = node.rect;
				if (rect0 == null) continue; // missing sometimes
				OMSVGRect rect = rect0.getBBox();
				if (r == null) {
					r = getSvgElement().createSVGRect(rect); // make copy
				} else {
					r = r.union(rect);
			}}
		}
		if (r != null) 
		{	r = r.inset(-15, -15);
			float factor = Math.max(r.getWidth()/imagewidth, r.getHeight()/imageheight); //
			float deltax = imagewidth*factor - r.getWidth();
			float deltay = imageheight*factor - r.getHeight();
			r.setX(r.getX() - deltax/2);
			r.setY(r.getY() - deltay/2);
			r.setWidth(imagewidth*factor);
			r.setHeight(imageheight*factor);
			getSvgElement().setViewBox(r);
			resize(factor);
		}
	}

	public void hideDescription() {
		if (popup != null) popup.hide();
	}

	public void showDescription() {
		if (popup != null) popup.show();
		
	}

	public void setVoorKennisVisible(boolean visible) {
		setWidgetVisible(voorkennisBtn, visible);
	}

	protected void doFilterFit(DomStudentModelMethodInfo info) {
		doFilter(info);
		zoomFit();
	}

}
