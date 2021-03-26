package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ListBox;

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

class StudentResultsGraph extends LayoutPanel implements MouseMoveHandler, MouseUpHandler, MouseDownHandler, MouseOutHandler {

	private class Book implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			String b = book.getText();
			String m = GETALENRUIMTE;
			if (methodeBtn.getSelectedIndex() == 2) m = MODERNEWISKUNDE;
			doFilter(Collections.singletonMap(m, Collections.singletonMap(b, Collections.emptySet())));
		}

	}

	static final String GETALENRUIMTE = "Getal&Ruimte";
	static final String MODERNEWISKUNDE = "Moderne Wiskunde";

	Map<String, Map<String,Set<Integer>>> filter;
	
	private class MethodeChange implements ChangeHandler, ClickHandler {

		@Override
		public void onChange(ChangeEvent event) {
			GWT.log("Methode change");
			int index = methodeBtn.getSelectedIndex();
			Map<String, Map<String,Set<Integer>>> filter = null;
			switch(index) {
			default:
			case 0: filter = Collections.emptyMap(); break;
			case 1: filter = Collections.singletonMap(GETALENRUIMTE, Collections.emptyMap()); break;
			case 2: filter = Collections.singletonMap(MODERNEWISKUNDE, Collections.emptyMap());	break;				
			}
			doFilter(filter);
		}

		@Override
		public void onClick(ClickEvent event) {
			GWT.log("Methode click");
			onChange(null);
			event.preventDefault();
		}

	}


	private class Voorkennis implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			// TODO Auto-generated method stub

		}

	}

	float factor = 1.0f;
	class Zoom implements ClickHandler {

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
			Collection<List<Node>> nodes = map.values();
			
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
				factor = Math.max(r.getWidth()/imagewidth, r.getHeight()/imageheight); //
				r.setWidth(imagewidth*factor);
				r.setHeight(imageheight*factor);
				getSvgElement().setViewBox(r);
				resize(factor);
			}
		}

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

	private static int defaultFontSize = 16;
	private static String defaultFont = "Ubuntu";

	private ColorStyle succesColor = new ColorStyle(0,200,0);
	private ColorStyle halfSuccesColor = new ColorStyle(180,240,180);
	private ColorStyle failColor = new ColorStyle(200,0,0);
	private ColorStyle halfFailColor = new ColorStyle(255,150,150);
	private ColorStyle defaultEdgeColor = colorBlue4;
	
	private OMSVGDocument doc;
	private SVGImage image;
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
			line.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "30");
			g.appendChild(line);
// triangle
			float x = (x1+x2)/2;
			float y = (y1+y2)/2;
			float dx = x1 - x2;
			float dy = y1 - y2;
			float len = (float) Math.hypot(dx, dy) / 90;
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
	
	
	
	class Edge extends AbstractEdge {
		final Node from, to;

		Edge(Node from, Node to) {
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

	abstract class AbstractNode {
		boolean blur;
		boolean visible = true;
		final OMSVGGElement g = doc.createSVGGElement();
		OMSVGCircleElement circle;
		OMSVGTextElement text;
		ColorStyle nodeColor = defaultNodeColor;
		ColorStyle nodeBorderColor = colorBlue2;
		ColorStyle textColor = defaultTextColor;
		ColorStyle edgeColor;
		float cx,cy;				
		float r;


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

		boolean isBlur() {
			return blur;
		}
		void setBlur(boolean blur) {
			this.blur = blur;
			colorize();
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
 			text.getStyle().setSVGProperty(SVGConstants.CSS_TEXT_ANCHOR_PROPERTY, "middle");
 			text.getStyle().setSVGProperty(SVGConstants.CSS_DOMINANT_BASELINE_PROPERTY, "central");
 			text.getStyle().setSVGProperty(SVGConstants.CSS_FONT_FAMILY_PROPERTY, defaultFont);
 			text.getStyle().setSVGProperty(SVGConstants.CSS_FONT_SIZE_PROPERTY, Integer.toString(defaultFontSize*44));
 			colorize();
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
			doFilter(info);
		}
		
	}
	
	class ChapterNode extends AbstractNode implements ClickHandler {
		final private DomStudentModelMethodInfo info;
		private List<Node> list = new ArrayList<>();
		

		ChapterNode(DomStudentModelMethodInfo info) {
			this.info = new DomStudentModelMethodInfo(info);
			this.info.setX(0);
			this.info.setY(0);
			r = 150f;
			textColor = white;
			nodeBorderColor = nodeColor;
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
			text = doc.createSVGTextElement(cx, cy, unitType, "H" + info.getChapter());
			text.addClickHandler(this);
 			text.getStyle().setSVGProperty(SVGConstants.CSS_TEXT_ANCHOR_PROPERTY, "middle");
 			text.getStyle().setSVGProperty(SVGConstants.CSS_DOMINANT_BASELINE_PROPERTY, "central");
 			text.getStyle().setSVGProperty(SVGConstants.CSS_FONT_FAMILY_PROPERTY, defaultFont);
 			text.getStyle().setSVGProperty(SVGConstants.CSS_FONT_SIZE_PROPERTY, Integer.toString(defaultFontSize*4));
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
			doFilter(info);
		}
	}
	
	class Node extends AbstractNode {
		final private DomStudentModelMethodInfo info;
		final private DomStudentModelObj obj;
		private OMSVGRectElement rect;
		
		Node(DomStudentModelObj obj, DomStudentModelMethodInfo info, String parent) {
			this.obj = obj;
			r = 15;
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
 			if(invalid()) return;
			circle = doc.createSVGCircleElement(cx, cy, r);
			short unitType = OMSVGLength.SVG_LENGTHTYPE_NUMBER;
			text = doc.createSVGTextElement(cx, cy, unitType, parent + obj.getInfo().getTitle().get(lang));
		}
		
		Node(DomStudentModelObj obj, DomStudentModelMethodInfo info) {
			this(obj,info, "");
		}
		
		OMSVGGElement build() {
			if (invalid()) {
				super.setVisible(false);
				return g;
			}
			
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

		private boolean invalid() {
			return Float.isNaN(cx)||Float.isNaN(cy);
		}

		
		Stream<Edge> edges() {
			List<String> voorkennis = obj.getInfo().getVoorkennis();
			if (voorkennis == null|| invalid()) return Stream.empty();
			return voorkennis.stream()
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
				nodeColor = new ColorStyle(0xFFFFFF);
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
		void colorize() {
			if (!invalid())
				super.colorize();
		}
		
	}
	
	private Map<String, List<Node>> map;
	private Map<String, ChapterNode> chapters;
	private Map<String, BookNode> books;
	private Set<Edge> edges;
	private Set<ChapterEdge> chapterEdges;
	
	private Button zoomFitBtn, zoomInBtn, zoomOutBtn, voorkennisBtn;
	private DockLayoutPanel title;
	private ListBox methodeBtn;
	private Button book;
	private Label  chapter;

	@Inject StudentResultsGraph() {
		getElement().getStyle().setMarginLeft(20, Unit.PX);
		getElement().getStyle().setMarginRight(22, Unit.PX);
		
		doc = OMSVGParser.currentDocument();
		image = new SVGImage();
		image.setSvgElement(doc.createSVGSVGElement());
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
		title = new DockLayoutPanel(Unit.EM);
		title.getElement().getStyle().setBackgroundColor("#1B75BB");
		add(title);
		methodeBtn = new ListBox();
		methodeBtn.addItem("Alle leerdoelen");
		methodeBtn.addItem("Getal & Ruimte");
		methodeBtn.addItem("Moderne Wiskunde");
		methodeBtn.setStylePrimaryName("graph-ListBox");
		
		title.addWest(methodeBtn, 10);
		
		book = new Button("1HV");
		book.setStylePrimaryName("dwo-Button");
		Label prebook = new Label(" > ");
		Style style = prebook.getElement().getStyle();
		style.setPaddingTop(0.2, Unit.EM);
		style.setColor("white");
		style.setTextAlign(TextAlign.CENTER);
		style.setFontSize(20, Unit.PX);
		Label postbook = new Label(" > ");
		style = postbook.getElement().getStyle();
		style.setPaddingTop(0.2, Unit.EM);
		style.setColor("white");
		style.setTextAlign(TextAlign.CENTER);
		style.setFontSize(20, Unit.PX);
		chapter = new Label("h1");
		style = chapter.getElement().getStyle();
		style.setPaddingTop(0.2, Unit.EM);
		style.setColor("white");
		style.setFontSize(20, Unit.PX);
		title.addWest(prebook, 3);
		title.addWest(book, 10);
		title.addWest(postbook, 3);
		title.add(chapter);
		
		setWidgetLeftRight(title, 0, Unit.EM, 0, Unit.EM);
		setWidgetTopHeight(title, 0, Unit.EM, 2, Unit.EM);
		
		
		lang = LocaleInfo.getCurrentLocale().getLocaleName();
		map = new HashMap<>();
		chapters = new HashMap<>();
		books = new HashMap<>();
		edges = Collections.emptySet();
		chapterEdges = Collections.emptySet();
		getElement().getStyle().setBackgroundColor(colorGray3.getColor());
		
		image.addMouseMoveHandler(this);
		image.addMouseUpHandler(this);
		image.addMouseDownHandler(this);
		image.addMouseOutHandler(this);
		
		zoomFitBtn.addClickHandler(new ZoomFit());
		zoomOutBtn.addClickHandler(new Zoom(true));
		zoomInBtn.addClickHandler(new Zoom(false));
		voorkennisBtn.addClickHandler(new Voorkennis());
		MethodeChange handler = new MethodeChange();
		methodeBtn.addChangeHandler(handler);
		methodeBtn.addClickHandler(handler);
		book.addClickHandler(new Book());
	}

	private void doFilter(DomStudentModelMethodInfo info) {
		if (info.getChapter() == null) {
			doFilter(Collections.singletonMap(info.getMethod(), Collections.singletonMap(info.getBook(), Collections.emptySet())));
		} else {
			doFilter(Collections.singletonMap(info.getMethod(), Collections.singletonMap(info.getBook(), Collections.singleton(info.getChapter()))));	
		}
	}

	public void doFilter(Map<String, Map<String, Set<Integer>>> f) {
		if (f == null) return;
		filter = f;
		if (f.isEmpty()) {
			for(List<Node> n: map.values()) for(Node node: n) node.setVisible(true);
		    methodeBtn.setSelectedIndex(0);
		    edges.forEach(Edge::setVisible);
		    chapters.values().forEach(ChapterNode::setVisible);
		    chapterEdges.forEach(ChapterEdge::setVisible);
		    books.values().forEach(BookNode::setVisible);
		    book.setText("");
		    chapter.setText("");
		    return;
		} else if (f.size() == 1) {
			String key = f.keySet().iterator().next();
			int index = 0;
			if (GETALENRUIMTE.equals(key)) index = 1;
			if (MODERNEWISKUNDE.equals(key)) index = 2;
			methodeBtn.setSelectedIndex(index);
			if (f.get(key).size() == 1) {
				book.setText(f.get(key).keySet().iterator().next());
				Set<Integer> chapters = f.get(key).get(book.getText());
				if (chapters.size() == 1) {
					chapter.setText("h" + chapters.iterator().next());
				} else {
					chapter.setText("");
				}			
			} else {
				chapter.setText("");
				book.setText("");
			}
		} else 
		{
			methodeBtn.setSelectedIndex(0);
			book.setText("");
		}
		Iterator<Node> i =  nodeStream().iterator();
		while (i.hasNext()) {
			Node n = i.next();
			boolean ok = StudentResultsPresenter.inFilter(f, n.info);
			n.setVisible(ok);
		}
	    edges.forEach(Edge::setVisible);
	    chapters.values().forEach(ChapterNode::setVisible);
	    chapterEdges.forEach(ChapterEdge::setVisible);
	    books.values().forEach(BookNode::setVisible);
	}

	private OMSVGSVGElement getSvgElement() {
		return image.getSvgElement();
	}

	public void setModelScore(DomStudentModelContext4Student item, Promise<DomStudentModelDataScore> score) {
		map.clear();
		chapters.clear();
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
		chapterEdges = edges.stream().map( e -> {
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
			map.put( id, methodInfo.stream().map(info -> new Node(obj, info, p))
					.filter(t -> !t.invalid())
					.collect(Collectors.toList()));
			return true;
		}
		for (DomStudentModelObj leaf : obj.getObjectives()) setModel(leaf, obj.getInfo());
		return false;
	}

	private String parentOf(DomStudentModelContextInfo info) {
		String parent = info.getTitle().get(lang);
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
		float x = event.getClientX();
		float y = event.getClientY();
		OMSVGPoint point = image.getSvgElement().createSVGPoint(x, y);
		OMSVGMatrix ctm = image.getSvgElement().getScreenCTM().inverse();
		point = point.matrixTransform(ctm);
		float sx = point.getX();
		float sy = point.getY();
		// blur nodes and edges: find node, focus on node and edges.
		Optional<Node> find = nodeStream().filter(node -> node.contains(sx, sy)).findAny();
		if (find.isPresent()) {
			Node node = find.get();
			Set<Edge> set = edges.stream().filter(edge -> edge.to == node).collect(Collectors.toSet());
			Set<Node> nodes = set.stream().map(t -> t.from).collect(Collectors.toSet());
			nodes.add(node);
			edges.forEach(e -> e.setBlur(!set.contains(e)));
			nodeStream().forEach(n -> n.setBlur(!nodes.contains(n)));			
		} else {
			edges.forEach(e -> e.setBlur(false));
			nodeStream().forEach(n -> n.setBlur(false));
		}		
	}

	private Stream<Node> nodeStream() {
		return map.values().stream().flatMap(List::stream);
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
		mouseMove(event);
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.LayoutPanel#onResize()
	 */
	@Override
	public void onResize() {
		super.onResize();
		resize(factor);
	}

	int imagewidth, imageheight;
	private void resize(float f) {
		if (imagewidth != getOffsetWidth() || imageheight != getOffsetHeight() || f != factor) {
			setWidgetTopHeight(image, 2, Unit.EM, imageheight = getOffsetHeight(), Unit.PX);
			setWidgetLeftWidth(image, 0, Unit.PX, imagewidth = getOffsetWidth(), Unit.PX);
			factor = f;
			OMSVGRect rect = getSvgElement().createSVGRect(0, 0, imagewidth*factor, imageheight*factor);
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
			
			getSvgElement().setViewBox(rect);
		} else {
			LOG.info("break recursion");
		}
	}

	
	
}
