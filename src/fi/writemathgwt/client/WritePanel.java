package fi.writemathgwt.client;


import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JButton;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.Context2d.LineCap;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.vaadin.pointerevents.client.PointerCancelEvent;
import com.vaadin.pointerevents.client.PointerCancelHandler;
import com.vaadin.pointerevents.client.PointerDownEvent;
import com.vaadin.pointerevents.client.PointerDownHandler;
import com.vaadin.pointerevents.client.PointerEvent;
import com.vaadin.pointerevents.client.PointerMoveEvent;
import com.vaadin.pointerevents.client.PointerMoveHandler;
import com.vaadin.pointerevents.client.PointerUpEvent;
import com.vaadin.pointerevents.client.PointerUpHandler;

import fi.writemathgwt.client.engine.DoublePoint;
import fi.writemathgwt.client.engine.Point;
import fi.writemathgwt.client.engine.Stroke;
import fi.writemathgwt.client.engine.StrokeContainer;
import fi.writemathgwt.client.engine.formula.FormuleRoot;
import fi.writemathgwt.client.engine.formula.Samples20;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class WritePanel extends LayoutPanel { //HorizontalPanel
	private static Logger logger = Logger.getLogger("WritePanel");
	
	public static boolean analyserOn = false;
	String checkerBooleans = "";
	
	WritePanelHolder eigenaar;
	ArrayList<Point> points;
	ArrayList<DoublePoint> doublePoints;
	
	private Canvas writePanelCanvas;
	private Context2d g;
	
	private CssColor ruitjesKleur = CssColor.make(190, 190, 190);
	private CssColor strokeColor = CssColor.make(42, 71, 113);
	private int gridSize = 20;
	
	private static int defaultWidth = 680;
	private static int defaultHeight = 538;
	
	private int width;
	private int height;
	
	private int panelShiftX, panelShiftY;

	private StrokeContainer strokeContainer = new StrokeContainer();
	
	PushButton correctieButton;
	CorrectiePanel correctiePanel;
	
	int tekenSet = 1;
	
	public WritePanel(WritePanelHolder eigenaar, int tekenS) {
		this(defaultWidth, defaultHeight, eigenaar, tekenS);
	}
	
	public WritePanel(int width, int height, WritePanelHolder eigenaar, int tekenS) {
		this.eigenaar = eigenaar;
		
		this.width = width;
		this.height = height;
		
		tekenSet = tekenS;
		
		Element body = Document.get().getBody();
		body.setAttribute("oncontextmenu", "return false;");
		
		setSize("100%", height + "px");
		
		points = new ArrayList<Point>();
		doublePoints = new ArrayList<DoublePoint>();

		writePanelCanvas = Canvas.createIfSupported();
		writePanelCanvas.setWidth(width + "px");
		writePanelCanvas.setHeight(height + "px");
		writePanelCanvas.setCoordinateSpaceWidth(width);
		writePanelCanvas.setCoordinateSpaceHeight(height);
		add(writePanelCanvas);
		setWidgetLeftWidth(writePanelCanvas, 0, Style.Unit.PX, width, Style.Unit.PX);
		setWidgetTopHeight(writePanelCanvas, 0, Style.Unit.PX, height, Style.Unit.PX);

		correctieButton = new PushButton("C");
		correctieButton.addStyleName("pushbutton");
		//add(correctieButton);
		//setWidgetLeftWidth(correctieButton, width - 30, Style.Unit.PX, 30, Style.Unit.PX);
		//setWidgetTopHeight(correctieButton, 6, Style.Unit.PX, 20, Style.Unit.PX);
		//correctieButton.addClickHandler(new CBL());
		
		initContext2d();
		
		MouseHandler mouseHandler = new MouseHandler();
		writePanelCanvas.addMouseDownHandler(mouseHandler);
		writePanelCanvas.addMouseMoveHandler(mouseHandler);
		writePanelCanvas.addMouseUpHandler(mouseHandler);
					
		if (TouchStartEvent.isSupported()) { 
			MGWTTouchHandler touchHandler = new MGWTTouchHandler();
			writePanelCanvas.addTouchStartHandler(touchHandler);
			writePanelCanvas.addTouchMoveHandler(touchHandler);
			writePanelCanvas.addTouchEndHandler(touchHandler);
		}
		
		WritePanelPointerHandler pointerHandler = new WritePanelPointerHandler();
		(writePanelCanvas.asWidget()).addDomHandler((PointerMoveHandler)pointerHandler, PointerMoveEvent.getType()); 
		(writePanelCanvas.asWidget()).addDomHandler((PointerUpHandler)pointerHandler, PointerUpEvent.getType()); 
		(writePanelCanvas.asWidget()).addDomHandler((PointerDownHandler)pointerHandler, PointerDownEvent.getType());
		paint();
	}
	
	private void addStroke() {
		double[] x = new double [doublePoints.size()];
		double[] y = new double [doublePoints.size()];
		for(int i=0 ; i<x.length ; i++) {
			x[i] = doublePoints.get(i).x;
			y[i] = doublePoints.get(i).y;
		}
		strokeContainer.addStroke(new Stroke(x,y));
		//strokeContainer.addStroke(new Stroke(points));
		points.clear();
		doublePoints.clear();
		paint();
		eigenaar.writePanelChanged();
	}
	
	public void setTekenSet(int num) {
		
	}

	public Canvas getCanvas() {
		return writePanelCanvas;
	}
	
	public void initContext2d() {
		g = writePanelCanvas.getContext2d();
	}
	
	public void paint() {
		paintComponent(g, true);
	}
	
	public void paintLastSegment() {
		g.setStrokeStyle(CssColor.make(80, 80, 80));
		g.setLineWidth(2.0d);
		g.setLineCap(LineCap.ROUND);
		
		DoublePoint p1 = doublePoints.get(doublePoints.size()-2);
		DoublePoint p2 = doublePoints.get(doublePoints.size()-1);
		g.beginPath();
		g.moveTo(p1.x+panelShiftX, p1.y+panelShiftY);
		g.lineTo(p2.x+panelShiftX, p2.y+panelShiftY);
		g.closePath();
		g.stroke();
	}
	
	public void paint(boolean refresh) {
		paintComponent(g, refresh);
	}
	
	public void paintComponent(Context2d g, boolean refresh) {
		g.setLineWidth(0.5d);
		
		if(refresh) {
		g.clearRect(0, 0, width, height);
		g.setFillStyle(CssColor.make(240, 240, 240));
		g.fillRect(0, 0, width, height);
		g.fill();
		
		if (true) {
			g.setStrokeStyle(ruitjesKleur);
			int vSteps = height / gridSize;
			for (int vCnt = -panelShiftY/gridSize; vCnt <= vSteps+1-panelShiftY/gridSize; vCnt++) {
				g.beginPath();
				g.moveTo(0, vCnt * gridSize + panelShiftY);
				g.lineTo(width - 1, vCnt * gridSize + panelShiftY);
				g.stroke();
			}
			int hSteps = width / gridSize;
			for (int hCnt = -panelShiftX/gridSize; hCnt <= hSteps+1-panelShiftX/gridSize; hCnt++) {
				g.beginPath();
				g.moveTo(hCnt * gridSize + panelShiftX, 0);
				g.lineTo(hCnt * gridSize + panelShiftX, height - 1);
				g.stroke();
			}
		}
		g.setLineWidth(2.0d);
		g.setStrokeStyle(strokeColor);
		ArrayList<Stroke> strokes = strokeContainer.getStrokes();
		for(int i = 0 ; i < strokes.size() ; i++) {
			Stroke stroke = strokes.get(i);
			g.beginPath();
			double x0 = (int)stroke.getParsePoints().get(0).x + panelShiftX;
			double y0 = (int)stroke.getParsePoints().get(0).y + panelShiftY;
			g.moveTo(x0, y0);
			if(stroke.getParsePointsbox().width>3 ||  stroke.getParsePointsbox().height>3) {
				for(int j = 1 ; j < stroke.getParsePoints().size() ; j++) {
					double x = stroke.getParsePoints().get(j).x + panelShiftX;
					double y = stroke.getParsePoints().get(j).y + panelShiftY;
					g.lineTo(x, y);
				}
				g.moveTo(x0, y0);
				g.closePath();
				g.stroke();
			}
			else {
				g.arc(x0, y0, 1.5, 0, 1.5* Math.PI);
				g.closePath();
				g.stroke();
			}
		}
	}
		//doublePoints = averageSmooth(doublePoints);
		
		
//		if (doublePoints.size() > 0) {
//			g.beginPath();
//			g.moveTo(8*doublePoints.get(0).x+panelShiftX, 8*doublePoints.get(0).y+panelShiftY-100);
//			for(int j = 1 ; j <doublePoints.size() ; j++) {
//				g.lineTo(8*doublePoints.get(j).x+panelShiftX, 8*doublePoints.get(j).y+panelShiftY-100);
//			}
//			g.moveTo(8*doublePoints.get(0).x+panelShiftX, 8*doublePoints.get(0).y+panelShiftY-100);
//			g.closePath();
//			g.stroke();
//		}
//		doublePoints = averageSmooth(doublePoints);
//		if (doublePoints.size() > 0) {
//			g.beginPath();
//			g.moveTo(8*doublePoints.get(0).x+panelShiftX+120, 8*doublePoints.get(0).y+panelShiftY-100);
//			for(int j = 1 ; j <doublePoints.size() ; j++) {
//				g.lineTo(8*doublePoints.get(j).x+panelShiftX+120, 8*doublePoints.get(j).y+panelShiftY-100);
//			}
//			g.moveTo(8*doublePoints.get(0).x+panelShiftX+120, 8*doublePoints.get(0).y+panelShiftY-100);
//			g.closePath();
//			g.stroke();
//		}
//		
//		if (points.size() > 0) {
//			g.beginPath();
//			g.moveTo(8*points.get(0).x+panelShiftX+240, 8*points.get(0).y+panelShiftY-100);
//			for(int j = 1 ; j <points.size() ; j++) {
//				g.lineTo(8*points.get(j).x+panelShiftX+240, 8*points.get(j).y+panelShiftY-100);
//			}
//			g.moveTo(8*points.get(0).x+panelShiftX+240, 8*points.get(0).y+panelShiftY-100);
//			g.closePath();
//			g.stroke();
//		}
//		doublePoints = averageSmoothInt(points);
//		if (points.size() > 0) {
//			g.beginPath();
//			g.moveTo(8*doublePoints.get(0).x+panelShiftX+360, 8*doublePoints.get(0).y+panelShiftY-100);
//			for(int j = 1 ; j <doublePoints.size() ; j++) {
//				g.lineTo(8*doublePoints.get(j).x+panelShiftX+360, 8*doublePoints.get(j).y+panelShiftY-100);
//			}
//			g.moveTo(8*doublePoints.get(0).x+panelShiftX+360, 8*doublePoints.get(0).y+panelShiftY-100);
//			g.closePath();
//			g.stroke();
//		}
	}
	
	public ArrayList<DoublePoint> averageSmoothInt(ArrayList<Point> points) {
		ArrayList<DoublePoint> pointsNew = new ArrayList<DoublePoint>();
		for (int i = 0; i < points.size(); i++) {
			DoublePoint doublePoint = new DoublePoint(points.get(i).x , points.get(i).y);
			pointsNew.add(doublePoint);
		}
		return averageSmooth(pointsNew);
	}
	
	public ArrayList<DoublePoint> averageSmooth(ArrayList<DoublePoint> doublePoints) {	
		if (doublePoints.size() < 5) 
			return doublePoints;
		ArrayList<DoublePoint> pointsNew = new ArrayList<DoublePoint>();
		pointsNew.add(doublePoints.get(0));		
		pointsNew.add(doublePoints.get(1));
		for (int i = 2; i < doublePoints.size() - 2; i++) {
			DoublePoint pOld0 = doublePoints.get(i-2);
			DoublePoint pOld1 = doublePoints.get(i-1);
			DoublePoint pOld2 = doublePoints.get(i);
			DoublePoint pOld3 = doublePoints.get(i+1);
			DoublePoint pOld4 = doublePoints.get(i+2);
			
			DoublePoint smoothedPoint = new DoublePoint(pOld0.getX()/5 + pOld1.getX()/5 + pOld2.getX()/5 + pOld3.getX()/5 + pOld4.getX()/5,
														pOld0.getY()/5 + pOld1.getY()/5 + pOld2.getY()/5 + pOld3.getY()/5 + pOld4.getY()/5);
			pointsNew.add(smoothedPoint);
		}
		pointsNew.add(doublePoints.get(doublePoints.size() - 1));
		return pointsNew;
	}
	
	public String parseFormule() {
		return strokeContainer.getFormulaString();
	}
	
	public void readFormula(String s) {
		strokeContainer.averageHeight = 30;
		// trim $f...@
		if (s.length() >= 3) {	
			String begin = s.substring(0,2);
			if (begin.equals("$f"))	{	
				s = s.substring(2);
				s = s.substring(0,s.length()-1);
			}
		}
		if (s.length() == 0) {
			wis(); // Wim: wis als s leeg is.
			strokeContainer.wis();
			return;
		}
		
		int LTEIndex = s.indexOf("<=");
		if (LTEIndex >= 0) {	
			String s1 = s.substring(0,LTEIndex);
			String s2 = s.substring(LTEIndex + 2);
			s = s1 + '\u2264' + s2;
		}

		int GTEIndex = s.indexOf(">=");
		if (GTEIndex >= 0) {	
			String s1 = s.substring(0,GTEIndex);
			String s2 = s.substring(GTEIndex + 2);
			s = s1 + '\u2265' + s2;
		}
		wis();
		strokeContainer.wis();
		// make root
		FormuleRoot formuleRoot = new FormuleRoot(strokeContainer);
		// create formula tree
		formuleRoot.vulVak(s);
		// bepaal afmeting
		formuleRoot.findSizes(Samples20.mediumWidth, Samples20.mediumHeight);
		// als e.e.a. niet past, andere "font" nemen		
		
		// bepaal locaties onderdelen
		formuleRoot.setPositions();
		// convert the formulatree to WriteObjects
		formuleRoot.convertToWriteObjects();
		strokeContainer.makeParseArea();
		paint();
		String formule = parseFormule();
		eigenaar.writePanelChanged();
	}

	public void wis() {	
		resetPanelShift();
		strokeContainer.wis();
		paint();
	}

	private void resetPanelShift() {
		panelShiftX = 0;
		panelShiftY = 0;
	}
	
	private Point toWorldCoordinates(Point p) {
		Point pWorld = new Point(p.getX()-panelShiftX, p.getY()-panelShiftY);
		return pWorld;
	}
	private DoublePoint toWorldCoordinates(DoublePoint p) {
		DoublePoint pWorld = new DoublePoint(p.getX()-panelShiftX, p.getY()-panelShiftY);
		return pWorld;
	}
	
	private Point toScreenCoordinates(Point p){
		Point pScreen = new Point(p.getX()+panelShiftX, p.getY()+panelShiftY);
		return pScreen;
	}
	
	private DoublePoint toScreenCoordinates(DoublePoint p){
		DoublePoint pScreen = new DoublePoint(p.getX()+panelShiftX, p.getY()+panelShiftY);
		return pScreen;
	}
	
	//OK
	class MouseHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler {
		
		boolean mouseOnRight = false;
		boolean mouseOnLeft = false;
		
		Point shiftReference;
		
		public void onMouseDown(MouseDownEvent e) {
			e.preventDefault();
			e.stopPropagation();
			if(hasPointerEventSupport)
				return;
			if (e.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
				mouseOnRight = true;
				shiftReference = new Point(e.getX(), e.getY());
			}
			if (e.getNativeButton() == NativeEvent.BUTTON_LEFT) {
				mouseOnLeft = true;
				Point p = toWorldCoordinates(new Point(e.getX(), e.getY()));
				points.add(p);
				DoublePoint dp = toWorldCoordinates(new DoublePoint(e.getX(), e.getY()));
				doublePoints.add(dp);
			}
		}
		
		public void onMouseMove(MouseMoveEvent e) {
			e.preventDefault();
			e.stopPropagation();
			if(hasPointerEventSupport)
				return;
			if(mouseOnLeft) {
				Point p = toWorldCoordinates(new Point(e.getX(), e.getY()));
				points.add(p);
				DoublePoint lastPoint = toScreenCoordinates(doublePoints.get(doublePoints.size()-1));
				double xD = 1.0/3*(lastPoint.x + 2*e.getX());
				double yD = 1.0/3*(lastPoint.y + 2*e.getY());
				DoublePoint dp = toWorldCoordinates(new DoublePoint(xD, yD));
				double dx = xD-lastPoint.x;
				double dy = yD-lastPoint.y;
				//if(dx*dx+dy*dy>1)
					doublePoints.add(dp);
				paintLastSegment();
			}
			if (mouseOnRight) {
				panelShiftX += e.getX()-shiftReference.getX();
				panelShiftY += e.getY()-shiftReference.getY();
				shiftReference = new Point(e.getX(), e.getY());
				paint();
			}
		} 
		
		public void onMouseUp(MouseUpEvent e) {	
			e.preventDefault();
			e.stopPropagation();
			if(hasPointerEventSupport)
				return;
			if (mouseOnLeft) {
				mouseOnLeft = false;
//				Point p = toWorldCoordinates(new Point(e.getX(), e.getY()));
//				points.add(p);
//				DoublePoint dp = toWorldCoordinates(new DoublePoint(e.getX(), e.getY()));
//				doublePoints.add(dp);
				addStroke();
			}
			if (mouseOnRight) {
				mouseOnRight = false;
				panelShiftX += e.getX()-shiftReference.getX();
				panelShiftY += e.getY()-shiftReference.getY();
				paint();
			}
		}
	} 

	class MGWTTouchHandler implements TouchStartHandler, TouchEndHandler, TouchMoveHandler {
		
		Point shiftReference;
		boolean moving = false;
		boolean writing = false;
		
		public void onTouchStart(TouchStartEvent e) {
			e.preventDefault();
			e.stopPropagation();

			if(hasPointerEventSupport)
				return;
			
			Touch touch = e.getTouches().get(0);
			int eventX = touch.getPageX() - writePanelCanvas.getAbsoluteLeft();
			int eventY = touch.getPageY() - writePanelCanvas.getAbsoluteTop();		
			
			if ( (e.getTouches().length() == 1) && !moving ) {
				writing = true;
				Point p = toWorldCoordinates(new Point(eventX, eventY));
				points.add(p);
				DoublePoint dp = toWorldCoordinates(new DoublePoint(eventX, eventY));
				doublePoints.add(dp);
				paint();
			}
			if ( (e.getTouches().length() == 2) ) {
				moving = true;
				writing = false;
				points.clear();
				doublePoints.clear();
				shiftReference = new Point(eventX, eventY);
				paint();
			}			
			if ( (e.getTouches().length() > 2) ) {
				moving = false;
				writing = false;
				points.clear();
				doublePoints.clear();
				paint();
			}			

		}
		
		public void onTouchMove(TouchMoveEvent e) 
		{
			e.preventDefault();
			e.stopPropagation();
			
			if(hasPointerEventSupport)
				return;
			
			Touch touch = e.getTouches().get(0);
			int eventX = touch.getPageX() - writePanelCanvas.getAbsoluteLeft();
			int eventY = touch.getPageY() - writePanelCanvas.getAbsoluteTop();		

			if ( writing ) {
				Point p = toWorldCoordinates(new Point(eventX, eventY));
				points.add(p);
				DoublePoint lastPoint = toScreenCoordinates(doublePoints.get(doublePoints.size()-1));
				double xD = 0.5*(lastPoint.x + eventX);
				double yD = 0.5*(lastPoint.y + eventY);
				DoublePoint dp = toWorldCoordinates(new DoublePoint(xD, yD));
				doublePoints.add(dp);
				paintLastSegment();
			}
			
			if (( moving ) && (e.getTouches().length()==2)){
				panelShiftX += eventX-shiftReference.getX();
				panelShiftY += eventY-shiftReference.getY();
				shiftReference = new Point(eventX, eventY);
				paint();
			}
		}
		
		public void onTouchEnd(TouchEndEvent e) {
			e.stopPropagation();
			e.preventDefault();

			if(hasPointerEventSupport)
				return;
			
			if (!points.isEmpty() && writing) {
				addStroke();
			}
			
			if (e.getTouches().length() < 1 ) {
				writing = false;
				moving = false;
			}	
			//logger.info("touchEnd");
			paint();
		}
		
	}
	
	private boolean hasPointerEventSupport = false;
	
	class WritePanelPointerHandler implements PointerUpHandler, PointerDownHandler, PointerMoveHandler, PointerCancelHandler
	{
		Point shiftReference;
		boolean moving = false;
		boolean writing = false;
		
		int lastTouchX = 0;
		int lastTouchY = 0;
		
		int touchCount = 0;
		
		
		@Override
		public void onPointerCancel(PointerCancelEvent e) {
			e.stopPropagation();
			e.preventDefault();
			
			touchCount--;
			
			
		}

		@Override
		public void onPointerMove(PointerMoveEvent e) {
			e.stopPropagation();
			e.preventDefault();
			
			int eventX = e.getRelativeX(writePanelCanvas.getElement());
			int eventY = e.getRelativeY(writePanelCanvas.getElement());
			
			if(touchCount==1 && writing) {	
				
					Point p = toWorldCoordinates(new Point(eventX, eventY));
					points.add(p);
					DoublePoint lastPoint = toScreenCoordinates(doublePoints.get(doublePoints.size()-1));
					double xD = 0.5*(lastPoint.x + eventX);
					double yD = 0.5*(lastPoint.y + eventY);
					DoublePoint dp = toWorldCoordinates(new DoublePoint(xD, yD));
					doublePoints.add(dp);
					paintLastSegment();
			}
			if(touchCount==2 && moving) {	
					int d=(lastTouchX-eventX)*(lastTouchX-eventX)+(lastTouchY-eventY)*(lastTouchY-eventY);
					if(d<2000) {
						panelShiftX += eventX-shiftReference.getX();
						panelShiftY += eventY-shiftReference.getY();
						shiftReference = new Point(eventX, eventY);
						paint();
						lastTouchX = eventX;
						lastTouchY = eventY;
					}
			}
			
			e.preventDefault();
			e.stopPropagation();
			
		}

		@Override
		public void onPointerDown(PointerDownEvent e) {
			e.stopPropagation();
			e.preventDefault();
			
			touchCount++;
			
			hasPointerEventSupport = true;
			
			int eventX = e.getRelativeX(writePanelCanvas.getElement());
			int eventY = e.getRelativeY(writePanelCanvas.getElement());
			lastTouchX = eventX;
			lastTouchY = eventY;
				
			if(touchCount==1 &&!moving ) {
				writing = true;
				Point p = toWorldCoordinates(new Point(eventX, eventY));
				points.add(p);
				DoublePoint dp = toWorldCoordinates(new DoublePoint(eventX, eventY));
				doublePoints.add(dp);
				//paint();
			}
			else if(touchCount==2) {
				moving = true;
				writing = false;
				points.clear();
				doublePoints.clear();
				shiftReference = new Point(eventX, eventY);
				paint();
			}
				
			
			e.preventDefault();
			e.stopPropagation();
		}

		@Override
		public void onPointerUp(PointerUpEvent e) {
			e.stopPropagation();
			e.preventDefault();
			
			touchCount--;
			
			if (!points.isEmpty() && writing) {
				addStroke();
			}
			
			if (touchCount < 1 ) {
				writing = false;
				moving = false;
			}
			
			e.preventDefault();
			e.stopPropagation();
			
		}
	}
	
    class CBL implements ClickHandler {
    	
    	public void onClick(ClickEvent e) {
    		if (e.getSource() == correctieButton) {
    			if (correctiePanel == null) {
    				correctiePanel = new CorrectiePanel("","","","","","");
    				add(correctiePanel);
    				setWidgetLeftWidth(correctiePanel, width - 30, Style.Unit.PX, 30, Style.Unit.PX);
    				setWidgetTopHeight(correctiePanel, 26, Style.Unit.PX, 120, Style.Unit.PX);

    				correctiePanel.t1Button.addClickHandler(new CBL());
    				correctiePanel.t2Button.addClickHandler(new CBL());
    				correctiePanel.t3Button.addClickHandler(new CBL());
    				correctiePanel.t4Button.addClickHandler(new CBL());
    				correctiePanel.t5Button.addClickHandler(new CBL());
    				correctiePanel.t6Button.addClickHandler(new CBL());

    				paint();
    			}
    			else {	
    				correctiePanel.zetTekens("","","","","","");
    				correctiePanel.setVisible(!correctiePanel.isVisible());
    			}
    		}
    		if ((correctiePanel != null) && (e.getSource() == correctiePanel.t1Button))	{
    			correctiePanel.setVisible(false);
    			//implement action
    			eigenaar.writePanelChanged();
    		}
    		if ((correctiePanel != null) && (e.getSource() == correctiePanel.t2Button))	{
    			correctiePanel.setVisible(false);
    			//implement action
    			eigenaar.writePanelChanged();
    		}
    		if ((correctiePanel != null) && (e.getSource() == correctiePanel.t3Button))	{
    			correctiePanel.setVisible(false);
    			//implement action
    			eigenaar.writePanelChanged();
    		}
    		if ((correctiePanel != null) && (e.getSource() == correctiePanel.t4Button))	{
    			correctiePanel.setVisible(false);
    			//implement action
    			eigenaar.writePanelChanged();

    		}
    		if ((correctiePanel != null) && (e.getSource() == correctiePanel.t3Button))	{
    			correctiePanel.setVisible(false);
    			//implement action
    			eigenaar.writePanelChanged();
    		}
    		if ((correctiePanel != null) && (e.getSource() == correctiePanel.t4Button))	{
    			correctiePanel.setVisible(false);
    			//implement action
    			eigenaar.writePanelChanged();
    		}
    	}
    }

	@Override
	public void setPixelSize(int width, int height) {
		super.setPixelSize(width, height);
		if (width != -1) {
			this.width = width;
			writePanelCanvas.setPixelSize(width, -1);
			writePanelCanvas.setCoordinateSpaceWidth(width);
			setWidgetLeftWidth(writePanelCanvas, 0, Unit.PX, width, Unit.PX);
			initContext2d();
			paint(); // In animatie frame....
		}
	}
    
    
}


