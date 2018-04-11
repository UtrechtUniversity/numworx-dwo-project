package fi.writemathgwt.client;

//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.Point;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JButton;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.FillStrokeStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
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
//import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;

import fi.writemathgwt.client.engine.DoublePoint;
import fi.writemathgwt.client.engine.Point;
import fi.writemathgwt.client.engine.Stroke;
import fi.writemathgwt.client.engine.StrokeContainer;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class WritePanel extends LayoutPanel { //HorizontalPanel
	private static Logger logger = Logger.getLogger("WritePanel");
	
	public static boolean analyserOn = false;
	WriteObject objectToAnalyse;
	String checkerBooleans = "";
	
	private static int cPanelAreaMin = -50000;
	private static int cPanelAreaMax = 50000;
	
	private static int cPanelAreaDelta = cPanelAreaMax - cPanelAreaMin;
	
	ArrayList<Rectangle> breukBoxes;

	WritePanelHolder eigenaar;
	ArrayList<WriteObject> writeObjects;
	ArrayList<Point> points;
	WriteObject lastObject, lastLastObject;
	
	private Canvas writePanelCanvas;
	private Context2d g;
	
	private CssColor ruitjesKleur = CssColor.make(190, 190, 190);
	private CssColor zwart = CssColor.make(0, 0, 0);
	private CssColor blauw = CssColor.make(42, 71, 113);
	private CssColor drawingColor = CssColor.make(0, 0, 0);
	private int gridSize = 20;
	
	private static int defaultWidth = 680;
	private static int defaultHeight = 538;
	
	private int width;
	private int height;

	private StrokeContainer strokeContainer = new StrokeContainer();
	
	
	private int panelShiftX, panelShiftY;
	
	//private FormuleViewer formuleViewer;
	//private Panel formulePanel;
	
	private int averageHeight = 30;
	
	PushButton correctieButton;
	CorrectiePanel correctiePanel;
	
	ListBox sampleInspectComboBox;

	int tekenSet = 1;
	private boolean noParse = false;
	
	//OK
	public WritePanel(WritePanelHolder eigenaar, int tekenS) 
	{
		this(defaultWidth, defaultHeight, eigenaar, tekenS);
	}
	
	//OK
	public WritePanel(int width, int height, WritePanelHolder eigenaar, int tekenS) 
	{
		this.eigenaar = eigenaar;
		
		this.width = width;
		this.height = height;
		
		breukBoxes = new ArrayList<Rectangle>();
		
		tekenSet = tekenS;
		
		setSize("100%", height + "px");
		
		WriteObject.initSamples(tekenSet);
		writeObjects = new ArrayList<WriteObject>();
		points = new ArrayList<Point>();

	
		writePanelCanvas = Canvas.createIfSupported();
		writePanelCanvas.setWidth(width + "px");
		writePanelCanvas.setHeight(height + "px");
		writePanelCanvas.setCoordinateSpaceWidth(width);
		writePanelCanvas.setCoordinateSpaceHeight(height);
		//writePanelCanvas.getElement().getStyle().setMarginTop(6,Unit.PX);
		add(writePanelCanvas);
		setWidgetLeftWidth(writePanelCanvas, 0, Style.Unit.PX, width, Style.Unit.PX);
		setWidgetTopHeight(writePanelCanvas, 0, Style.Unit.PX, height, Style.Unit.PX);

		correctieButton = new PushButton("C");
		correctieButton.addStyleName("pushbutton");
		add(correctieButton);
		
		
		//correctieButton.setSize(50,20);
		setWidgetLeftWidth(correctieButton, width - 30, Style.Unit.PX, 30, Style.Unit.PX);
		setWidgetTopHeight(correctieButton, 6, Style.Unit.PX, 20, Style.Unit.PX);
		
		correctieButton.addClickHandler(new CBL());
		
		if(analyserOn) {
		sampleInspectComboBox = new ListBox();
		sampleInspectComboBox.addItem("choose");
		add(sampleInspectComboBox);
		sampleInspectComboBox.addChangeHandler(new ListChangeHandler());
		
		setWidgetLeftWidth(sampleInspectComboBox, width - 150, Style.Unit.PX, 100, Style.Unit.PX);
		setWidgetTopHeight(sampleInspectComboBox, 6, Style.Unit.PX, 20, Style.Unit.PX);
		}
		
		
		initContext2d();
		
		MouseHandler mouseHandler = new MouseHandler();
		writePanelCanvas.addMouseDownHandler(mouseHandler);
		writePanelCanvas.addMouseMoveHandler(mouseHandler);
		writePanelCanvas.addMouseUpHandler(mouseHandler);
					
		if (TouchStartEvent.isSupported()) 
		{ 
			MGWTTouchHandler touchHandler = new MGWTTouchHandler();
			writePanelCanvas.addTouchStartHandler(touchHandler);
			writePanelCanvas.addTouchMoveHandler(touchHandler);
			writePanelCanvas.addTouchEndHandler(touchHandler);
		}
		paint();
	}
	
	public void setNoParse(boolean b) {
		noParse=b;
	}

	public void setTekenSet(int num)
	{
		WriteObject.initSamples(num);
	}
	//OK
	public Canvas getCanvas() 
	{
		return writePanelCanvas;
	}
	
	//OK
	public void initContext2d() 
	{
		g = writePanelCanvas.getContext2d();
	}
	
	//OK
	public void paint() 
	{
		paintComponent(g, true);
	}
	
	public void paint(boolean refresh) 
	{
		paintComponent(g, refresh);
	}
	
	public void tekenRechthoek(Rectangle rechthoek) {
		initContext2d();
		CssColor rechthoekKleur = CssColor.make(240, 0, 0);
		FillStrokeStyle oldStyle = g.getStrokeStyle();
//		logger.info("teken rechthoek [" +  (double) (rechthoek.x+panelShiftX) + ", " +
//				                           (double) (rechthoek.y+panelShiftY) + ", " + 
//				                           (double) (rechthoek.width) + ", " + 
//				                           (double) (rechthoek.height) + "]" );
		g.setStrokeStyle(rechthoekKleur);
		g.rect( (double) (rechthoek.x+panelShiftX), 
				(double) (rechthoek.y+panelShiftY), 
				(double) (rechthoek.width), 
				(double) (rechthoek.height) );
		g.stroke();	
		g.setStrokeStyle(oldStyle);
	}
	
	public void paintComponent(Context2d g, boolean refresh) 
	{
		g.setLineWidth(0.5d);
		
		if(refresh) {
		g.clearRect(0, 0, width, height);
		
		g.setFillStyle(CssColor.make(240, 240, 240));
		g.fillRect(0, 0, width, height);
		g.fill();
		
		if (true) 
		{
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
		g.setStrokeStyle(blauw);
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
//		for(int i = 0 ; i < writeObjects.size() ; i++) {
//			g.setFillStyle(CssColor.make(220, 220, 220));
//			if(analyserOn) {
//				int x = writeObjects.get(i).getBox().x;
//				int y = writeObjects.get(i).getBox().y;
//				int w = writeObjects.get(i).getBox().width;
//				int h = writeObjects.get(i).getBox().height;
//				//g.fillRect(x, y, w, h);
//				g.setFillStyle(CssColor.make(0, 0, 0));
//				g.fillText(""+i, x, y);
//			}
//			writeObjects.get(i).draw(g, panelShiftX, panelShiftY);
//			
//			if(analyserOn) {
//				g.setFillStyle(CssColor.make(255, 255, 255));
//				int bx = 460;
//				int by = 420;
//				int dx = 14;
//				g.fillRect(0, by-180, width, 560);
//				g.setFillStyle(CssColor.make(150, 150, 150));
//				g.setStrokeStyle(ruitjesKleur);
//				for (int k = 0; k < 9; k++) {
//					g.beginPath();
//					g.moveTo(bx, by-180+k*45);
//					g.lineTo(width, by-180+k*45);
//					g.stroke();
//				}
//				for(int j = 0 ; j < writeObjects.get(i).dAngles.size() ; j++) {
//					
//					int xx = bx+dx*j;
//					int ww = dx-5;
//					int barH = writeObjects.get(i).dAngles.get(j).intValue();
//					int yy = by-Math.max(0,barH);
//					int hh = Math.abs(barH);
//					g.fillRect(xx, yy, ww, hh);
//					g.fillText(""+(j+1), bx+dx*j, by+(barH<0?-5:10));
//					
//					g.fillText("dAngle "+(j+1)+" = "+writeObjects.get(i).dAngles.get(j).intValue(), bx-100, by-180+12*(j+1));
//				}
//			}
//			
//		}
	}
		//ArrayList<DoublePoint> smoothPoints = averageSmoothInt(points);
		ArrayList<Point> smoothPoints = points;
		if (smoothPoints.size() > 0) {
			g.beginPath();
			g.moveTo(smoothPoints.get(0).x+panelShiftX, smoothPoints.get(0).y+panelShiftY);
			for(int j = 1 ; j <smoothPoints.size() ; j++) {
				g.lineTo(smoothPoints.get(j).x+panelShiftX, smoothPoints.get(j).y+panelShiftY);
			}
			g.moveTo(smoothPoints.get(0).x+panelShiftX, smoothPoints.get(0).y+panelShiftY);
			g.closePath();
			g.stroke();
			
		}
		
//		if (breukBoxes.size() > 0 ) {
//			logger.info("#Breukboxes = "+ breukBoxes.size());
//		}
//		for (int i=0; i<breukBoxes.size(); i++) {
//			Rectangle box = breukBoxes.get(i);
//			logger.info("Box "+ i + " = ["+box.x + ", "+ box.y+ ", "+ box.width +", "+ box.height + "]");
//			tekenRechthoek(breukBoxes.get(i));			
//		}
		
		//g.scale(5.0,5.0);
		//g.translate(300,0);
		
		//object vergroot
		if(analyserOn && objectToAnalyse!=null && !objectToAnalyse.isTwoStrokeObject() && !objectToAnalyse.isThreeStrokeObject()) {
			
			int bx=20;
			int by=250;
			double factor = 300.0 / Math.max(objectToAnalyse.getParsingBox().width,objectToAnalyse.getParsingBox().height);
			double x = bx - objectToAnalyse.getParsingBox().x;
			double y = by - objectToAnalyse.getParsingBox().y;
			objectToAnalyse.draw(g,(int)x, (int)y, factor);
			
			{
				double w =(factor*objectToAnalyse.getParsingBox().width);
				double h =(factor*objectToAnalyse.getParsingBox().height);
				g.setStrokeStyle(ruitjesKleur);
				for (int i = 0; i < 11; i++) {
					g.beginPath();
					g.moveTo(bx+0, by+i*h/10);
					g.lineTo(bx+w, by+i*h/10);
					g.stroke();
				}
				for (int i = 0; i < 11; i++) {
					g.beginPath();
					g.moveTo(bx+i*w/10 , by);
					g.lineTo(bx+i*w/10 , by+h);
					g.stroke();
				}
			}
			
			g.fillText(checkerBooleans, 300, by);
		}
				
		//g.scale(0.2,0.2);
		//g.translate(-300,0);
		
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
	
	
	public String parseFormule_() {	
		
		ArrayList<WriteObject> writeObjectsToDo = new ArrayList<WriteObject>();
		// make a compact deep copy
		for (int i = 0; i < writeObjects.size(); i++) {	
			WriteObject wo = new WriteObject(writeObjects.get(i));
			writeObjectsToDo.add(wo);
		}
		// bubble sort op links-positie
		boolean swapped = true;
		while (swapped)
		{	swapped = false;
			for (int i = 1; i < writeObjectsToDo.size(); i++)
			{	WriteObject wo1 = (WriteObject) writeObjectsToDo.get(i-1);
				WriteObject wo2 = (WriteObject) writeObjectsToDo.get(i);
				if (wo1.getBox().x > wo2.getBox().x)
				{	writeObjectsToDo.set(i-1, wo2);
					writeObjectsToDo.set(i, wo1);
					swapped = true;
				}
			}
		}
		// haal de breukstrepen er even uit
		ArrayList<WriteObject> breukStrepen = new ArrayList<WriteObject>();
		for (int i = 0; i < writeObjectsToDo.size(); i++)
		{	WriteObject wo = writeObjectsToDo.get(i);
			if (wo.getTeken().equals("-"))
				breukStrepen.add(wo);
		}
		
		// bubble sort op lengte breukstreep
		// langste vooraan
		swapped = true;
		while (swapped)
		{	swapped = false;
			for (int i = 1; i < breukStrepen.size(); i++)
			{	WriteObject wo1 = (WriteObject) breukStrepen.get(i-1);
				WriteObject wo2 = (WriteObject) breukStrepen.get(i);
				if (wo1.getBox().width < wo2.getBox().width)
				{	breukStrepen.set(i-1, wo2);
					breukStrepen.set(i, wo1);
					swapped = true;
				}
			}
		}

		// label de objecten die breukstreep zijn, dit initialiseeert ook de teller- en noemer boxes,
		// en labelt alle objecten die in een teller(box) of in een noemer(box) voorkomen, maar zodanig
		// dat een object alleen teller of (exclusief) noemer kan zijn en dat van de meest geneste
		//(kortste) breukstreep
		for (int i = 0; i < breukStrepen.size(); i++)
		{	WriteObject wo = (WriteObject) breukStrepen.get(i);
			isBreuk(wo, writeObjectsToDo);
		}
		
		// haal de wortels er even uit
		ArrayList<WriteObject> wortels = new ArrayList<WriteObject>();
		for (int i = 0; i < writeObjectsToDo.size(); i++)
		{	WriteObject wo = writeObjectsToDo.get(i);
			if (wo.getTeken().equals("sqrt"))
				wortels.add(wo);
		}
		// bubble sort op lengte wortel
		// langste vooraan
		swapped = true;
		while (swapped)
		{	swapped = false;
			for (int i = 1; i < wortels.size(); i++)
			{	WriteObject wo1 = (WriteObject) wortels.get(i-1);
				WriteObject wo2 = (WriteObject) wortels.get(i);
				if (wo1.getBox().width < wo2.getBox().width)
				{	wortels.set(i-1, wo2);
					wortels.set(i, wo1);
					swapped = true;
				}
			}
		}
		
		// correcties bij wortels
		// maak de wortelboxen
		for (int v = 0; v < wortels.size(); v++)
		{	WriteObject wo = wortels.get(v);
			int x = wo.getBox().x + 10;
			int y = wo.getBox().y;
			int w = wo.getBox().width - 10;
			int h = wo.getBox().height;
			Rectangle wBox = new Rectangle(x,y,w,h);
			wo.wortelBox = wBox;
		}	
		
		for (int i = 0; i < wortels.size(); i++)
		{	WriteObject wo = wortels.get(i);
			Rectangle wBox = wo.wortelBox; 

			// objecten binnen de wortelBox, NB wo zit daar ook bij!!
			// en ook wortels die wo bevatten!!
			ArrayList<WriteObject> objectsInside = writeObjectsInBox(writeObjectsToDo, wBox);
			
			// 1) object niet wo en geen wortel: object zit onder wo
			// 2) object niet wo en wortel die kleiner(!) is dan wo: object zit onder wo  
			// 3) objecten in een geneste wortel worden herlabeld omdat de wortels van groot naar 
			// klein verwerkt worden
			for (int k = 0; k < objectsInside.size(); k++)
			{	WriteObject oi = objectsInside.get(k);
				if ((oi != wo) && !oi.getTeken().equals("sqrt"))
				{	oi.isOnderWortel = wo;
				}
				else if ((oi != wo) && oi.getTeken().equals("sqrt"))
				{
					if (oi.wortelBox.width < wo.wortelBox.width)
						oi.isOnderWortel = wo;
				}
			}
			
			for (int j = 0; j < breukStrepen.size(); j++)
			{	WriteObject bs = (WriteObject) breukStrepen.get(j);
				// breukstreep binnen de wortel
				// a) mag de wortel niet als teller/noemer hebben
				// b) objecten buiten de wortel mogen geen teller of noemer zijn van deze breukstreep
				// c) aanpassen teller- en noemerbox hoeft niet: bij uitvoeren van de wortel
				// bevat writeObjectsToDoNow alleen objecten in de wortelbox
				if (bs.isBreuk && wBox.contains(bs.getBoxMid().x, bs.getBoxMid().y)) {	
					ArrayList<WriteObject> tellerObjects = writeObjectsInBox(writeObjectsToDo, bs.tellerBox);
					ArrayList<WriteObject> noemerObjects = writeObjectsInBox(writeObjectsToDo, bs.noemerBox);
						
					int inTellerCnt = tellerObjects.size();
					int inNoemerCnt = noemerObjects.size();
						
					// wortel eruit indien die in de teller zit
					if (tellerObjects.contains(wo))
					{	// NB wo kan al aan de juiste breukstreep zijn toegewezen!
						if ((wo.isTellerVan != null) && (wo.isTellerVan == bs))
							wo.isTellerVan = null;
						tellerObjects.remove(wo);
						inTellerCnt--;
					}
					// wortel eruit indien die in de noemer zit
					if (noemerObjects.contains(wo))
					{	// NB wo kan al aan de juiste breukstreep zijn toegewezen!
						if ((wo.isNoemerVan != null) && (wo.isNoemerVan == bs))
							wo.isNoemerVan = null;
						noemerObjects.remove(wo);
						inNoemerCnt--;
					}
					// objecten buiten de wortel uit de teller van bs
					for (int tCnt = 0; tCnt < tellerObjects.size(); tCnt++)
					{	WriteObject to = tellerObjects.get(tCnt);
						if (!wBox.contains(to.getBoxMid().x, to.getBoxMid().y))
						{
							if ((to.isTellerVan != null) && (to.isTellerVan == bs))
							{	to.isTellerVan = null;
								tellerObjects.remove(wo);
								inTellerCnt--;
							}
						}
					}
					// objecten buiten de wortel uit de noemer van bs
					for (int nCnt = 0; nCnt < noemerObjects.size(); nCnt++)
					{	WriteObject no = noemerObjects.get(nCnt);
						if (!wBox.contains(no.getBoxMid().x, no.getBoxMid().y))
						{
							if ((no.isNoemerVan != null) && (no.isNoemerVan == bs))
							{	no.isNoemerVan = null;
								noemerObjects.remove(wo);
								inNoemerCnt--;
							}
						}
					}
					// check of bs nog breuk is	
					if ((inTellerCnt == 0) && (inNoemerCnt == 0))
					{	bs.isBreuk = false;
						bs.tellerBox = null;
						bs.noemerBox = null;
					}
				} // if bs binnend de wortel
				// breukstreep buiten de wortel, als wo teller of noemer van bs is, dan mag 
				// mag geen object binnen de wortel teller/noemer of noemer zijn van bs
				// maar: als er een breukstreep binnen de wortel is, dan is wo daar teller of noemer van
				// en niet van een breukstreep buiten de wortel
				// maak wo dus teller of noemer van de kleinste breukstreep die wo als
				// teller/noemer bevat (breukstrepen zijn gesorteerd
				// maar: doe dit niet als wo onder een wortel zit die bs al als teller of noemer heeft !!
				// let op dat bs buiten iha voor bs binnen komt omdat iha bs buiten langer is dan bs binnen 
				else if (bs.isBreuk && !wBox.contains(bs.getBoxMid().x, bs.getBoxMid().y))
				{	
					
					boolean tellerCorrection = true;
					// corrigeer niet als wo onder een wortel zit die bs al als teller heeft !!
					// deze bs-allocatie is correct want wo wordt later behandeld dan deze moederwortel
					// omdat de moederwortel groter is dan wo 
					if ((wo.isOnderWortel != null) && 
						(wo.isOnderWortel.isTellerVan != null) && (wo.isOnderWortel.isTellerVan == bs))
						tellerCorrection = false;
					// corrigeer als nodig	
					if (bs.tellerBox.contains(wo.getBoxMid().x, wo.getBoxMid().y) && tellerCorrection)
					{	wo.isTellerVan = bs;
						wo.isNoemerVan = null;
					}
					// corrigeer niet als wo onder een wortel zit die bs al als noemer heeft !!
					// deze bs-allocatie is correct want wo wordt later behandeld dan deze moederwortel
					// omdat de moederwortel groter is dan wo 
					boolean noemerCorrection = true;
					if ((wo.isOnderWortel != null) && 
						(wo.isOnderWortel.isNoemerVan != null) && (wo.isOnderWortel.isNoemerVan == bs))
						noemerCorrection = false;
					// corrigeer als nodig	
					if (bs.noemerBox.contains(wo.getBoxMid().x, wo.getBoxMid().y) && noemerCorrection)
					{	wo.isTellerVan = null;
						wo.isNoemerVan = bs;
					}	

					// indien(!) wo nu teller of noemer is van bs, dan kunnen alle objecten (exclusief wo)
					// geen teller of noemer meer zijn van bs; zet dus tellerVan of noemerVan op null
					if (((wo.isTellerVan != null) && (wo.isTellerVan == bs)) ||
						((wo.isNoemerVan != null) && (wo.isNoemerVan == bs))
					   )
					{	for (int k = 0; k < objectsInside.size(); k++)
						{	WriteObject oi = objectsInside.get(k);
							if (oi != wo)
							{	
								if ((oi.isTellerVan != null) && (oi.isTellerVan == bs))
								{	oi.isTellerVan = null;
								}
								if ((oi.isNoemerVan != null) && (oi.isNoemerVan == bs))
								{	oi.isNoemerVan = null;
								}
							}
						} //for
					}
				} // breukstreep buiten de wortel
					
			} // for breukstrepen
		} // for wortels
		
//      Changed scope of the writePanel, size is now inifinite (as fars as that is possible within the int parameters)
//		return parseBox(new Rectangle(Integer.MIN_VALUE / 2, Integer.MIN_VALUE / 2, Integer.MAX_VALUE, Integer.MAX_VALUE), writeObjectsToDo, null, null);
		return parseBox(new Rectangle(cPanelAreaMin, cPanelAreaMin, cPanelAreaDelta, cPanelAreaDelta), writeObjectsToDo, null, null);
//		return parseBox(new Rectangle(0, 0, width, height), writeObjectsToDo, null, null);
	}
	
	private ArrayList<WriteObject> writeObjectsInBox(ArrayList<WriteObject> wObjects, Rectangle box)
	{
		ArrayList<WriteObject> insideObjects = new ArrayList<WriteObject>();
		for (int i = 0; i < wObjects.size(); i++)
		{	WriteObject wo = wObjects.get(i);
			if (box.contains(wo.getBoxMid().x, wo.getBoxMid().y))
				insideObjects.add(wo);
		}
		return insideObjects;
	}
	
	public void logWriteObjects(String key)
	{
		String codeRules = "";
		for (int i = 0; i < writeObjects.size(); i++)
		{
			String rRule = "";
			WriteObject wo = writeObjects.get(i);
			
			String s = "";
			for (int j = 0; j < wo.points.size(); j++)
			{
				if(j>0)
					s=s+",";
				s=s+wo.points.get(j).getX()+","+wo.points.get(j).getY() ;
			}
			codeRules=codeRules+"int[] sample_"+i+" = {"+s+"}; samples.add(sample_"+i+");\n";
		}
		//if(analyserOn)
			logger.info("// "+key+"\n{\nArrayList<int[]> samples = new ArrayList<int[]>();\n"+codeRules+"refSamples.put(\""+key+"\", samples);\n}");
	}
	
	public void loadRefSamples(String key)
	{
		writeObjects.clear();
		//ReferenceSamples rs = new ReferenceSamples();
		//ArrayList<WriteObject> sampleWriteObjects
		//writeObjects = rs.getReferenceObjects(key);
		String result = "";
		if(analyserOn) {
			sampleInspectComboBox.clear();
			sampleInspectComboBox.addItem(key);
		}
		for(int i=0 ; i<writeObjects.size() ; i++) {
			String teken = writeObjects.get(i).parse(key);
			
			writeObjects.get(i).translate(20+(i%10)*80-writeObjects.get(i).getBox().x , 20+(i/10)*110-writeObjects.get(i).getBox().y );
			result = result+teken;
			if(analyserOn)
				sampleInspectComboBox.addItem(""+i+". :"+teken);
		}
		//eigenaar.writeFormulaObject(result);
		paint();
		
	}
	
	private boolean skipWriteObject(WriteObject wo, WriteObject boxOwner)
	{
		if (wo == null)
			return true;
		
		boolean skipWo1 = wo.isVerwerkt; 
		boolean skipWo2 = false;
		if (boxOwner == null)
			skipWo2 = (wo.isTellerVan != null) ||(wo.isNoemerVan != null);
		else
			skipWo2 = ((wo.isTellerVan != null) && (wo.isTellerVan != boxOwner)) ||
					  ((wo.isNoemerVan != null) && (wo.isNoemerVan != boxOwner));
		boolean skipWo3 = false;
		if (boxOwner == null)
			skipWo3 = wo.isOnderWortel != null;
		else
			skipWo3 = (wo.isOnderWortel != null) && (wo.isOnderWortel != boxOwner);

		return skipWo1 || skipWo2 || skipWo3;
	}
	
	public String parseBox(Rectangle box, ArrayList<WriteObject> writeObjectsToDo, 
						   WriteObject lastWriteObject, WriteObject boxOwner) {
		String string = "";
		Rectangle correctedBox = null;
		
		ArrayList<WriteObject> wortelsInBox = new ArrayList<WriteObject>();
		int x = box.x; 
		int width = box.width;
		for (int i = 0; i < writeObjectsToDo.size(); i++)
		{	WriteObject wo = writeObjectsToDo.get(i);
			if (box.contains(wo.getBoxMid().x, wo.getBoxMid().y) && wo.getTeken().equals("sqrt"))
			{
				x = Math.min(x, wo.wortelBox.x);
				width = Math.max(width, wo.wortelBox.width);
			}
		}
		correctedBox = new Rectangle(x,box.y,width,box.height);
		
		ArrayList<WriteObject> writeObjectsToDoNow = new ArrayList<WriteObject>();
		for (int i = 0; i < writeObjectsToDo.size(); i++) 
		{
			WriteObject wo = writeObjectsToDo.get(i);
			if (correctedBox.contains(wo.getBoxMid().x, wo.getBoxMid().y))
				writeObjectsToDoNow.add(wo);
		}
		
		// vindt het meest linkse object in de box dat nog niet verwerkt is
		// en dat niet in een teller of noemer voorkomt, m.u.v. de teller of noemer 
		// van boxOwner
		WriteObject nextWriteObject = null;
		boolean found = false;
		for (int i = 0; i < writeObjectsToDoNow.size(); i++) {	
			WriteObject wo = writeObjectsToDoNow.get(i);
		
			boolean skipWo = skipWriteObject(wo,boxOwner);
			if (!skipWo && !found) 
			{	nextWriteObject = wo;
				found = true;
			}
		}

		int stopCnt = 0;
// tijdelijk		
		//while (writeObjectsToDoNow.size() > 0)
		while ((nextWriteObject != null) && (stopCnt < 50))
		{
			stopCnt++;
			
			//breuk
			if (nextWriteObject.isBreuk)
			{
				
				String teller = "";
				String noemer = "";
				teller = parseBox(nextWriteObject.tellerBox, writeObjectsToDoNow, null, nextWriteObject);
				noemer = parseBox(nextWriteObject.noemerBox, writeObjectsToDoNow, null, nextWriteObject);
				
				if ((lastWriteObject != null) && isMacht(lastWriteObject, nextWriteObject))
					string = string + processMacht(lastWriteObject, nextWriteObject, 
							"$b" + teller + "$n" + noemer + "@@"); 
				else	
					string = string + "$b" + teller + "$n" + noemer + "@@";
				
				nextWriteObject.isVerwerkt = true;
				
				zetAllInBoxVerwerkt(writeObjectsToDo, nextWriteObject.tellerBox, true);
				zetAllInBoxVerwerkt(writeObjectsToDo, nextWriteObject.noemerBox, true);
			}
			
			//wortel
			else if (nextWriteObject.getTeken().equals("sqrt")) 
			{
				Rectangle wBox = nextWriteObject.wortelBox; 
				
				nextWriteObject.isVerwerkt = true;
				
				removeIsOnderWortel(writeObjectsToDoNow, wBox, nextWriteObject);
				
				String operand = parseBox(wBox, writeObjectsToDoNow, null,nextWriteObject);
				
				if ((lastWriteObject != null) && isMacht(lastWriteObject, nextWriteObject))
					string = string + processMacht(lastWriteObject, nextWriteObject, "$w" + operand + "@"); 
				else	
					string = string + "$w" + operand + "@";

				zetAllInBoxVerwerkt(writeObjectsToDo, wBox, true);
				
			}
			
			//macht 
			else if(lastWriteObject != null && isMacht(lastWriteObject, nextWriteObject))
			{
				// isMacht = false betekent 1) verboden situatie of 
				// 2) nextWriteObject staat niet boven lastWriteObject en 
				// lastWriteObject is geen macht


				if (staatBoven(lastWriteObject, nextWriteObject))
				{
					
					nextWriteObject.isMachtVan = lastWriteObject;
					// open de macht
					string = string + "$m" + nextWriteObject.getTeken();
					nextWriteObject.isVerwerkt = true;
					
				}
				else if (staatNaast(lastWriteObject, nextWriteObject))
				{
					if (lastWriteObject.isMachtVan != null)
					{
						nextWriteObject.isMachtVan = lastWriteObject.isMachtVan;
						string = string + nextWriteObject.getTeken();
						nextWriteObject.isVerwerkt = true;
					}
					// else isMacht = false 
				}
				// staat lager en zou weer bij een eerdere macht kunnen horen
				else // maak dit maar redundant
				{
					
					if ((lastWriteObject.isMachtVan != null) &&
					    staatNaast(lastWriteObject.isMachtVan, nextWriteObject))	
					{
						
						if (lastWriteObject.isMachtVan.isMachtVan == null)
						{
							
							// macht afsluiten
							string = string + "@" + nextWriteObject.getTeken();
							// teken hier afhandelen
							nextWriteObject.isVerwerkt = true;
						}
						else // lastWriteObject.isMachtVan.isMachtVan != null)
						{								

							nextWriteObject.isMachtVan = lastWriteObject.isMachtVan.isMachtVan;
							// macht afsluiten
							string = string + "@" + nextWriteObject.getTeken();
							nextWriteObject.isVerwerkt = true;
						}	
					}
					
					if ((lastWriteObject.isMachtVan != null) && 
						(lastWriteObject.isMachtVan.isMachtVan != null) &&
						staatNaast(lastWriteObject.isMachtVan.isMachtVan, nextWriteObject))
					{
						
						
						if (lastWriteObject.isMachtVan.isMachtVan.isMachtVan == null)
						{
							
							// macht 2 keer (!) afsluiten
							string = string + "@@" + nextWriteObject.getTeken();
							nextWriteObject.isVerwerkt = true;
						}
						else // lastWriteObject.isMachtVan.isMachtVan.isMachtVan != null
						{
							nextWriteObject.isMachtVan = lastWriteObject.isMachtVan.isMachtVan.isMachtVan;
							// macht 2 keer (!) afsluiten
							string = string + "@@" + nextWriteObject.getTeken();
							nextWriteObject.isVerwerkt = true;
							
						}
					}
					
					if ((lastWriteObject.isMachtVan != null) && 
						(lastWriteObject.isMachtVan.isMachtVan != null) &&
						(lastWriteObject.isMachtVan.isMachtVan.isMachtVan != null) &&
						staatNaast(lastWriteObject.isMachtVan.isMachtVan.isMachtVan, nextWriteObject))
						{
							
							if (lastWriteObject.isMachtVan.isMachtVan.isMachtVan.isMachtVan == null)
							{
								
								// macht 3 keer (!) afsluiten
								string = string + "@@@" + nextWriteObject.getTeken();
								// teken hier afhandelen
								nextWriteObject.isVerwerkt = true;
							}
							else // lastWriteObject.isMachtVan.isMachtVan.isMachtVan.isMachtVan != null
							{
								nextWriteObject.isMachtVan = lastWriteObject.isMachtVan.isMachtVan.isMachtVan.isMachtVan;
								// macht 3 keer (!) afsluiten
								string = string + "@@@" + nextWriteObject.getTeken();
								nextWriteObject.isVerwerkt = true;
								
							}
						}
					
					if ((lastWriteObject.isMachtVan != null) && 
						(lastWriteObject.isMachtVan.isMachtVan != null) &&
						(lastWriteObject.isMachtVan.isMachtVan.isMachtVan != null) &&
						(lastWriteObject.isMachtVan.isMachtVan.isMachtVan.isMachtVan != null) &&
						staatNaast(lastWriteObject.isMachtVan.isMachtVan.isMachtVan.isMachtVan, nextWriteObject))
							{
								
								
								if (lastWriteObject.isMachtVan.isMachtVan.isMachtVan.isMachtVan.isMachtVan == null)
								{
									
							
									// macht 4 keer (!) afsluiten
									string = string + "@@@@" + nextWriteObject.getTeken();
									// teken hier afhandelen
									nextWriteObject.isVerwerkt = true;
								}
								else // lastWriteObject.isMachtVan.isMachtVan.isMachtVan.isMachtVan != null
								{
									nextWriteObject.isMachtVan = 
										lastWriteObject.isMachtVan.isMachtVan.isMachtVan.isMachtVan.isMachtVan;
									// macht 4 keer (!) afsluiten
									string = string + "@@@@" + nextWriteObject.getTeken();
									nextWriteObject.isVerwerkt = true;
									
								}
							}
				}
				
			} 
			// punt
			else if (nextWriteObject.getTeken().equals("."))
			{

				int py = nextWriteObject.getBoxMid().y;
				if (lastWriteObject != null)
				{	
					int ly = lastWriteObject.getBox().y;
					if (py > ly + 2 * averageHeight / 3)
						string = string + ".";
					else
						string = string + "*";
				}
				
				nextWriteObject.isVerwerkt = true;
				
			}
			else	
			{

				String teken = "";
				if (!nextWriteObject.isVerwerkt)
				{	
					nextWriteObject.isVerwerkt = true;
					teken = nextWriteObject.getTeken();
				}
				string = string + teken;
			}
			lastWriteObject = nextWriteObject;
			
			// vindt het meest linkse object dat nog niet verwerkt is (if any) 
			nextWriteObject = null;
			//minX = width;
			found = false;
			for (int i = 0; i < writeObjectsToDoNow.size(); i++)
			{	WriteObject wo = writeObjectsToDoNow.get(i);
				
				boolean skipWo = skipWriteObject(wo,boxOwner);
				
				if (!skipWo && !found) 
				{	nextWriteObject = wo;
					found = true;
				}
			}
			if (nextWriteObject == null)
			{
				string = string + sluitMachtenAf(lastWriteObject);
			}
			
		} // while
		
		string = removeHalfObjects(string);
		
		return string;
	}
	
	public String removeHalfObjects(String s)
	{
		String result = new String(s);
		int tHIndex = result.indexOf("tH");
		if (tHIndex >= 0)
		{	String s1 = result.substring(0,tHIndex);
			String s2 = result.substring(tHIndex + 2);
			result = s1 + s2;
		}
		int fourHIndex = result.indexOf("4H");
		if (fourHIndex >= 0)
		{	String s1 = result.substring(0,fourHIndex);
			String s2 = result.substring(fourHIndex + 2);
			result = s1 + s2;
		}
		int fiveHIndex = result.indexOf("5H");
		if (fiveHIndex >= 0)
		{	String s1 = result.substring(0,fiveHIndex);
			String s2 = result.substring(fiveHIndex + 2);
			result = s1 + s2;
		}
		int jHIndex = result.indexOf("jH");
		if (jHIndex >= 0)
		{	String s1 = result.substring(0,jHIndex);
			String s2 = result.substring(jHIndex + 2);
			result = s1 + s2;
		}
		int xHIndex = result.indexOf("xH");
		if (xHIndex >= 0)
		{	String s1 = result.substring(0,xHIndex);
			String s2 = result.substring(xHIndex + 2);
			result = s1 + s2;
		}

		
		
		return result;
	}

	
	private String sluitMachtenAf(WriteObject wo)
	{	String result = ""; 
		if (wo.isMachtVan == null)
			return result;
		else if (wo.isMachtVan.isMachtVan == null)
			return "@";
		else if (wo.isMachtVan.isMachtVan.isMachtVan == null)
			return "@@";
		else if (wo.isMachtVan.isMachtVan.isMachtVan.isMachtVan == null)
			return "@@@";
		else if (wo.isMachtVan.isMachtVan.isMachtVan.isMachtVan.isMachtVan == null)
			return "@@@@";
	
		return result;
	}
	
	private String processMacht(WriteObject lastWriteObject, WriteObject nextWriteObject, String objectString)
	{
		String string = "";
		
		if (staatBoven(lastWriteObject, nextWriteObject))
		{
			nextWriteObject.isMachtVan = lastWriteObject;
			// open de macht
			string = string + "$m" + objectString;
			nextWriteObject.isVerwerkt = true;
			
		}
		else if (staatNaast(lastWriteObject, nextWriteObject))
		{
			if (lastWriteObject.isMachtVan != null)
			{
				nextWriteObject.isMachtVan = lastWriteObject.isMachtVan;
				string = string + objectString;
				nextWriteObject.isVerwerkt = true;
			}
			// else isMacht = false 
		}
		// staat lager en zou weer bij een eerdere macht kunnen horen
		else // maak dit maar redundant
		{
			if ((lastWriteObject.isMachtVan != null) &&
			    staatNaast(lastWriteObject.isMachtVan, nextWriteObject))	
			{
				if (lastWriteObject.isMachtVan.isMachtVan == null)
				{
					// macht afsluiten
					string = string + "@" + objectString;
					// teken hier afhandelen
					nextWriteObject.isVerwerkt = true;
				}
				else // lastWriteObject.isMachtVan.isMachtVan != null)
				{								
					nextWriteObject.isMachtVan = lastWriteObject.isMachtVan.isMachtVan;
					// macht afsluiten
					string = string + "@" + objectString;
					nextWriteObject.isVerwerkt = true;
				}	
			}
			
			if ((lastWriteObject.isMachtVan != null) && 
				(lastWriteObject.isMachtVan.isMachtVan != null) &&
				staatNaast(lastWriteObject.isMachtVan.isMachtVan, nextWriteObject))
			{
				
				if (lastWriteObject.isMachtVan.isMachtVan.isMachtVan == null)
				{
					// macht 2 keer (!) afsluiten
					string = string + "@@" + objectString;
					nextWriteObject.isVerwerkt = true;
				}
				else // lastWriteObject.isMachtVan.isMachtVan.isMachtVan != null
				{
					nextWriteObject.isMachtVan = lastWriteObject.isMachtVan.isMachtVan.isMachtVan;
					// macht 2 keer (!) afsluiten
					string = string + "@@" + objectString;
					nextWriteObject.isVerwerkt = true;
					
				}
			}
			
			if ((lastWriteObject.isMachtVan != null) && 
				(lastWriteObject.isMachtVan.isMachtVan != null) &&
				(lastWriteObject.isMachtVan.isMachtVan.isMachtVan != null) &&
				staatNaast(lastWriteObject.isMachtVan.isMachtVan.isMachtVan, nextWriteObject))
				{
					
					if (lastWriteObject.isMachtVan.isMachtVan.isMachtVan.isMachtVan == null)
					{
						// macht 3 keer (!) afsluiten
						string = string + "@@@" + objectString;
						// teken hier afhandelen
						nextWriteObject.isVerwerkt = true;
					}
					else // lastWriteObject.isMachtVan.isMachtVan.isMachtVan.isMachtVan != null
					{
						nextWriteObject.isMachtVan = lastWriteObject.isMachtVan.isMachtVan.isMachtVan.isMachtVan;
						// macht 3 keer (!) afsluiten
						string = string + "@@@" + objectString;
						nextWriteObject.isVerwerkt = true;
						
					}
				}
			
			if ((lastWriteObject.isMachtVan != null) && 
				(lastWriteObject.isMachtVan.isMachtVan != null) &&
				(lastWriteObject.isMachtVan.isMachtVan.isMachtVan != null) &&
				(lastWriteObject.isMachtVan.isMachtVan.isMachtVan.isMachtVan != null) &&
				staatNaast(lastWriteObject.isMachtVan.isMachtVan.isMachtVan.isMachtVan, nextWriteObject))
					{
						
						
						if (lastWriteObject.isMachtVan.isMachtVan.isMachtVan.isMachtVan.isMachtVan == null)
						{
					
							// macht 4 keer (!) afsluiten
							string = string + "@@@@" + objectString;
							// teken hier afhandelen
							nextWriteObject.isVerwerkt = true;
						}
						else // lastWriteObject.isMachtVan.isMachtVan.isMachtVan.isMachtVan != null
						{
							nextWriteObject.isMachtVan = 
								lastWriteObject.isMachtVan.isMachtVan.isMachtVan.isMachtVan.isMachtVan;
							// macht 4 keer (!) afsluiten
							string = string + "@@@@" + objectString;
							nextWriteObject.isVerwerkt = true;
							
						}
					}
		}
		
		return string;
	}
	
	private boolean isMacht(WriteObject lastWo, WriteObject wo)
	{
		// lastWo^{".","=","+",")","/"} kan/mag niet
		if (staatBoven(lastWo,wo) &&
			(".".equals(wo.getTeken()) || 
			 "=".equals(wo.getTeken()) || 
			 "+".equals(wo.getTeken()) || 
			 ")".equals(wo.getTeken()) || 
			 "/".equals(wo.getTeken()))
		   )
			return false;
		//{".","=","+","-",")","/"}^wo kan niet
		else if (staatBoven(lastWo,wo) &&
				 (".".equals(lastWo.getTeken()) || 
				  "=".equals(lastWo.getTeken()) || 
				  "+".equals(lastWo.getTeken()) || 
				  "-".equals(lastWo.getTeken()) || 
				  "(".equals(lastWo.getTeken()) || 
				  "/".equals(lastWo.getTeken()))
				)
			return false;
		
		boolean isMacht = staatBoven(lastWo,wo) || (lastWo.isMachtVan != null);
		
		
		return isMacht;
	}
	
	private boolean staatBoven(WriteObject lastWo, WriteObject wo)
	{
		return (wo.getBoxMid().y + averageHeight / 2 < lastWo.getBoxMid().y) &&
			   (wo.getBox().x > lastWo.getBoxMid().x);
	}
	
	private boolean staatNaast(WriteObject lastWo, WriteObject wo)
	{	if (wo.getTeken().equals("-"))
			return (wo.getBoxMid().y > lastWo.getBox().y) && 
				   (wo.getBoxMid().y < (lastWo.getBox().y + lastWo.getBox().height)) && 
				   (wo.getBox().x > lastWo.getBoxMid().x);
		else
			return (Math.abs(wo.getBoxMid().y - lastWo.getBoxMid().y) < averageHeight / 3) && 
			   	   (wo.getBox().x > lastWo.getBoxMid().x);
	}
	
	
	private void isBreuk(WriteObject wo, ArrayList<WriteObject> writeObjectsToDo) 
	{
		

		boolean hasTeller = false;
		boolean hasNoemer = false;
		
		// wo moet een (breuk)streep zijn
		if (!"-".equals(wo.getTeken())) 
		{	
			return; // false;
		}
		
		if ((wo.isTellerVan == null) && (wo.isNoemerVan == null)) {	
			// neem alle hoogte tot bovenaan
			int tx = wo.getBox().x;
			int ty= cPanelAreaMin ;
			int tw = wo.getBox().width;
			int th = wo.getBox().y - ty; 
			
			wo.tellerBox = new Rectangle(tx,ty,tw,th);			
			
			// neem alle hoogte tot onderaan
			int nx = wo.getBox().x;
			int ny = wo.getBox().y + wo.getBox().height;
			int nw = wo.getBox().width;
//			int nh = height - ny; //wo.getBox().width;
			int nh = cPanelAreaMax-ny;
			
			wo.noemerBox = new Rectangle(nx,ny,nw,nh);
			
		}
		else if ((wo.isTellerVan != null) && (wo.isNoemerVan == null)) {
			// neem alle hoogte tot bovenaan
//			int tx = wo.getBox().x;
//			int ty = 0; // wo.getBox().y - wo.getBox().width;
//			int tw = wo.getBox().width;
//			int th = wo.getBox().y; //wo.getBox().width;
			
			int tx = wo.getBox().x;
			int ty= cPanelAreaMin ;
			int tw = wo.getBox().width;
			int th = wo.getBox().y - ty; 
			wo.tellerBox = new Rectangle(tx,ty,tw,th);
			
			// pas tussen wo boven wo.isTellerVan
			int nx = wo.getBox().x;
			int ny = wo.getBox().y + wo.getBox().height;
			int nw = wo.getBox().width;
			int nh = wo.isTellerVan.getBox().y - ny; 
			wo.noemerBox = new Rectangle(nx,ny,nw,nh);
		}
		
		else if ((wo.isTellerVan == null) && (wo.isNoemerVan != null)) {	
			
			// pas tussen wo.isNoemerVan boven wo 
			int tx = wo.getBox().x;
			int ty = wo.isNoemerVan.getBox().y + wo.isNoemerVan.getBox().height;
			int tw = wo.getBox().width;
			int th = wo.getBox().y - ty; 
			wo.tellerBox = new Rectangle(tx,ty,tw,th);
			
			// neem alle hoogte tot onderaan
			int nx = wo.getBox().x;
			int ny = wo.getBox().y + wo.getBox().height;
			int nw = wo.getBox().width;
//			int nh = height - ny; //wo.getBox().width;
			int nh = cPanelAreaMax-ny;
			wo.noemerBox = new Rectangle(nx,ny,nw,nh);
		}
		
		for (int i = 0; i < writeObjectsToDo.size(); i++)
		{	WriteObject writeObject = writeObjectsToDo.get(i);
			if (wo.tellerBox.contains(writeObject.getBoxMid().x, writeObject.getBoxMid().y))
			{	hasTeller = true;
				writeObject.isTellerVan = wo;
				writeObject.isNoemerVan = null;
			}
		}

		for (int i = 0; i < writeObjectsToDo.size(); i++)
		{	WriteObject writeObject = writeObjectsToDo.get(i);
			if (wo.noemerBox.contains(writeObject.getBoxMid().x, writeObject.getBoxMid().y))
			{	hasNoemer = true;
				writeObject.isNoemerVan = wo;
				writeObject.isTellerVan = null;
			}
		}		

		if (!hasTeller && !hasNoemer)
		{	
			wo.tellerBox = null;
			wo.noemerBox = null;
		}
		else
		{
			wo.isBreuk = true;
//			breukBoxes.add(wo.tellerBox);
//			breukBoxes.add(wo.noemerBox);
		}
		
	}
	
	private  ArrayList<WriteObject> removeAllInBox(ArrayList<WriteObject> wo, Rectangle box) 
	{
		ArrayList<WriteObject> woNew = new ArrayList<WriteObject>();
		for (int i = 0; i < wo.size(); i++)
		{
			WriteObject awo = wo.get(i);
			boolean wortelLatenStaan = (wo.size() > 1) && awo.getTeken().equals("sqrt") &&
									   (awo.getBox().width > box.width);	
			if (!box.contains(awo.getBoxMid()) || wortelLatenStaan)
				woNew.add(awo);
		}
		return woNew;
	}

	private void removeIsOnderWortel(ArrayList<WriteObject> wo, Rectangle wortelBox, WriteObject wortel)
	{
		for (int i = 0; i < wo.size(); i++)
		{	WriteObject wob = wo.get(i);
			if (wortelBox.contains(wob.getBoxMid()) && 
			   (wob.isOnderWortel != null) && (wob.isOnderWortel == wortel))
			{	wob.isOnderWortel = null;
			}
		
		}
	}
	
	private void zetAllInBoxVerwerkt(ArrayList<WriteObject> wo, Rectangle box, boolean b) 
	{
		for (int i = 0; i < wo.size(); i++)
		{	WriteObject wob = wo.get(i);
			if (box.contains(wob.getBoxMid().x, wob.getBoxMid().y))
				wob.isVerwerkt = b;
		}
	}
	
	// OK
	private void addWriteObject() {
		strokeContainer.addStroke(new Stroke(points));
		points.clear();
		paint();
		eigenaar.writePanelChanged();
	}	
//		ArrayList<Point> newWriteObjectPoints = new ArrayList<Point>();
//		for (int i=0; i<points.size(); i++) {
//			newWriteObjectPoints.add(
//					new Point(points.get(i).getX(), points.get(i).getY()) );
////			newWriteObjectPoints.add(
////					new Point(points.get(i).getX()-panelShiftX, points.get(i).getY()-panelShiftY) );
//		}
////		resetPanelShift();
//
//		WriteObject wo = new WriteObject(newWriteObjectPoints);
//	
//		if ("null".equals(wo.getTeken())) 
//		{
//		}
//		//wis of gum
//		else if ("back".equals(wo.getTeken())) 
//		{
//			boolean isBack = true;
//			if (lastObject != null)
//			{	
//				
////System.out.println("lo not null");				
//				WriteObject woTwoStroke = tryTwoStroke(lastObject, wo);
//				if (wo != woTwoStroke) 
//				{	
////System.out.println("two");					
//					writeObjects.remove(lastObject);
//					wo = woTwoStroke;
//					lastObject = wo;
//					updateAverageHeight(wo);
//					writeObjects.add(wo);
//					isBack = false;
//				}
//			}
////System.out.println("isBack " + isBack);			
//			if (isBack)
//			{
//				int x = wo.getBox().x;
//				//int y = wo.getBoxMid().y - wo.getBox().width / 2;
//				int y = wo.getBoxMid().y - averageHeight ;
//				int w = wo.getBox().width;
//				//int h = wo.getBox().width;
//				int h = 2*averageHeight;
//				Rectangle box = new Rectangle(x, y, w, h);
//				int objectsBefore = writeObjects.size();
////System.out.println("before " + objectsBefore);				
//				writeObjects = removeAllInBox(writeObjects,box);
//				int objectsAfter = writeObjects.size();
////System.out.println("after " + objectsAfter);				
//				if (objectsBefore != objectsAfter)
//				{	lastObject = null;
//					lastLastObject = null;
//				}
//				else
//				{	wo = new WriteObject("-",wo.getIntPoints());
//					lastLastObject = lastObject;
//					lastObject = wo;
//					writeObjects.add(wo);
//				}
//			}
//		}
//		else if (lastLastObject != null && lastObject!=null)
//		{
//			WriteObject woThreeStroke = tryThreeStroke(lastLastObject, lastObject,  wo);
//			if (wo != woThreeStroke) 
//			{
//				writeObjects.remove(lastObject);
//				writeObjects.remove(lastLastObject);
//				//lastLastObject = null;
//				wo = woThreeStroke;
//			}
//			else {
//				WriteObject woTwoStroke = tryTwoStroke(lastObject, wo);
//				if (wo != woTwoStroke) 
//				{
//					writeObjects.remove(lastObject);
//					wo = woTwoStroke;
//				}
//				else
//				{
//					lastLastObject = lastObject;
//				}
//				
//			}
//			lastObject = wo;
//			if(analyserOn)
//				objectToAnalyse = wo;
//			updateAverageHeight(wo);
//			writeObjects.add(wo);
//			
//		}
//		else if (lastObject != null)
//		{
//			WriteObject woTwoStroke = tryTwoStroke(lastObject, wo);
//			if (wo != woTwoStroke) 
//			{
//				writeObjects.remove(lastObject);
//				wo = woTwoStroke;
//			}
//			else
//			{
//				lastLastObject = lastObject;
//			}
//			lastObject = wo;
//			if(analyserOn)
//				objectToAnalyse = wo;
//			updateAverageHeight(wo);
//			writeObjects.add(wo);
//			
//		}
//		else 
//		{
//			lastLastObject = lastObject;
//			lastObject = wo;
//			if(analyserOn)
//				objectToAnalyse = wo;
//			updateAverageHeight(wo);
//			writeObjects.add(wo);
//		}
//		
//		points.clear();
//		paint();
//		
//		//Buiten de DWOplayer:
//		//formuleViewer = new FormuleViewer(parseFormule());
//		eigenaar.writePanelChanged();
//		
//		//Binnen de DWOplayer:
//		//String text = parseFormule();
//		//FormuleEditor editor = kb.getEditor();
//		//if (editor != null)	{
//		//	editor.clearAll();
//		//	editor.insert(text);
//		//}
//	}
	
	protected void addWriteObject(String teken, ArrayList<Point> points) {
	//	logger.info("addWriteObject :: "+ teken + ", #Points = "+ points.size());
		WriteObject wo = new WriteObject(teken, points);
		//lastLastObject = lastObject;
		//lastObject = wo;
		writeObjects.add(wo);
	}
	
	protected void addWriteObject(String teken, ArrayList<Point> points1, ArrayList<Point> points2) {
		WriteObject wo1 = new WriteObject(teken, points1);
		WriteObject wo2 = new WriteObject(teken, points2);
		WriteObject wo = new WriteObject(teken, wo1, wo2);
		//lastLastObject = lastObject;
		//lastObject = wo;
		writeObjects.add(wo);
	}
	
	protected void addWriteObject(String teken, ArrayList<Point> points1, ArrayList<Point> points2, ArrayList<Point> points3) {
		WriteObject wo1 = new WriteObject(teken, points1);
		WriteObject wo2 = new WriteObject(teken, points2);
		WriteObject wo3 = new WriteObject(teken, points3);
		WriteObject wo = new WriteObject(teken, wo1, wo2, wo3);
		//lastLastObject = lastObject;
		//lastObject = wo;
		writeObjects.add(wo);
	}
	
	public void readFormula(String s)
	{
		// trim $f...@
		if (s.length() >= 3)
		{	String begin = s.substring(0,2);
			if (begin.equals("$f"))
			{	s = s.substring(2);
				s = s.substring(0,s.length()-1);
			}
		}
		if (s.length() == 0)
		{
			wis(); // Wim: wis als s leeg is.
			strokeContainer.wis();
			return;
		}
		
		int LTEIndex = s.indexOf("<=");
		if (LTEIndex >= 0)
		{	String s1 = s.substring(0,LTEIndex);
			String s2 = s.substring(LTEIndex + 2);
			s = s1 + '\u2264' + s2;
		}

		int GTEIndex = s.indexOf(">=");
		if (GTEIndex >= 0)
		{	String s1 = s.substring(0,GTEIndex);
			String s2 = s.substring(GTEIndex + 2);
			s = s1 + '\u2265' + s2;
		}

		
//System.out.println("s = " + s);		
		
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
		
//System.out.println("wos = " + writeObjects.size());		
		
		paint();
		
		String formule = parseFormule();
		
		eigenaar.writePanelChanged();
		
		//WriteMath.formuleVak.vulVak(formule);
		//WriteMath.formuleLabel.setText("fs = " + formule);
		
	}

	private void updateAverageHeight(WriteObject wo) 
	{
		if ("-".equals(wo.getTeken())|| 
			".".equals(wo.getTeken())|| 
			"sqrt".equals(wo.getTeken()) || 
			"=".equals(wo.getTeken())) 
			return;
		averageHeight = (writeObjects.size()*averageHeight + wo.getBox().height)/(writeObjects.size()+1);	
	}
	
	private WriteObject tryThreeStroke(WriteObject woLastLast, WriteObject woLast, WriteObject wo) {
		//logger.info("in tryThreeStroke()");
		
		if ( (woLast == null) || ( woLast.isTwoStrokeObject()) || ( woLast.isThreeStrokeObject()) 
				|| (woLastLast == null) || ( woLastLast.isTwoStrokeObject()) || ( woLastLast.isThreeStrokeObject()) ) {
			return wo; 
		}
		
		
		WriteObject result = ThreeStrokeMatcher.findThreeStroke(woLastLast, woLast, wo);
		if(result != null) {
			result.newMatch = true;
			return result;
		}	
		return wo;
	}
	
	private WriteObject tryTwoStroke(WriteObject woLast, WriteObject wo) {
		
		if ( (woLast == null) || ( woLast.isTwoStrokeObject()) || ( woLast.isThreeStrokeObject()) ) {
			return wo; 
		}
		Rectangle boxLast = woLast.getBox();
		Rectangle box = wo.getBox();
		
		WriteObject result = TwoStrokeMatcher.findTwoStroke(woLast, wo);
		if(result != null) {
			result.newMatch = true;
			return result;
		}	
/*		
		
		// + = 1 + -
		if (woLast.getTeken().equals("1") && (wo.getTeken().equals("-") || wo.getTeken().equals("back"))) 
		{
			int diam = (boxLast.height + box.width) / 2;
			if (distance(woLast.getBoxMid(), wo.getBoxMid()) < averageHeight / 3)
//				return new WriteObject("+", mergePoints(woLast.getPoints(), wo.getPoints()));
				return new WriteObject("+", woLast, wo);

		}
		// + = - + 1 
		else if (woLast.getTeken().equals("-") && wo.getTeken().equals("1")) 
		{
			int diam = (boxLast.height + box.width) / 2;
			if (distance(woLast.getBoxMid(), wo.getBoxMid()) < averageHeight / 3)
//				return new WriteObject("+", mergePoints(woLast.getPoints(), wo.getPoints()));
				return new WriteObject("+", woLast, wo);
		}
		// + = / + -
		else if (woLast.getTeken().equals("/") && (wo.getTeken().equals("-") || wo.getTeken().equals("back"))) 
		{
			int diam = (boxLast.height + box.width) / 2;
			if (distance(woLast.getBoxMid(), wo.getBoxMid()) < averageHeight / 3)
//				return new WriteObject("+", mergePoints(woLast.getPoints(), wo.getPoints()));
				return new WriteObject("+", woLast, wo);
		}
		// + = - + / NB in GWT averageHeight / 4
		else if(woLast.getTeken().equals("-") && wo.getTeken().equals("/")) 
		{
			int diam = (boxLast.height + box.width)/ 2;
			if (distance(woLast.getBoxMid(), wo.getBoxMid()) < averageHeight / 3)
//				return new WriteObject("+", mergePoints(woLast.getPoints(), wo.getPoints()));
				return new WriteObject("+", woLast, wo);
		}
		// 5 = 5H of b + - in 
		else if (
				((woLast.isCloseTo("5H")) && 
				 (wo.getTeken().equals("-") || wo.getTeken().equals("back")))) 
		{
			//int diam = (boxLast.height + box.width) / 2;
			//if (Math.abs(woLast.getBoxMid().x - wo.getBoxMid().x) < diam && 
			//	Math.abs(boxLast.y - box.y) < diam / 2)
			if(woLast.hasCloseDistance(10,wo,0,2,0,2))
			    return new WriteObject("5", woLast, wo);
		}
		// 4 = 4H + 1
		else if ( woLast.isCloseTo("tH") && (wo.getTeken().equals("1")|| wo.getTeken().equals("/")) // moet tH niet 4H zijn???
				  //(woLast.getTeken().equals("4H") && wo.getTeken().equals("1")) || 
				  //(woLast.getTeken().equals("tH") && wo.getTeken().equals("1")) ||
 				  //(woLast.getTeken().equals("<") && wo.getTeken().equals("1")) || 
 				  //(woLast.getTeken().equals("4H") && wo.getTeken().equals("/")) || 
				  //(woLast.getTeken().equals("tH") && wo.getTeken().equals("/")) ||
 				  //(woLast.getTeken().equals("<") && wo.getTeken().equals("/"))
					
				)
		{
			int diam = (boxLast.width + box.height) / 2;
			

//			if ( (box.x+box.width > (boxLast.x + 0.40 *boxLast.width)) && (box.x+box.width < (boxLast.x + 1.15 *boxLast.width)) &&
// 				 (box.y > (boxLast.y - 0.05 *boxLast.height)) && (box.y < (boxLast.y + 0.95 *boxLast.height))
//			   ) 
			if(woLast.hasCloseDistance(10,wo,15,20,5,15)) {
				return new WriteObject("4", woLast, wo);
			} 
		}
		// x = ) + (
		else if ( ( woLast.getTeken().equals(")") || woLast.getTeken().equals("xH") ) && 
				    ( wo.getTeken().equals("(") || wo.getTeken().equals("c") || wo.getTeken().equals("4H") ) 
			    ) {
			int avgWidth = (boxLast.width + box.width) / 2;
			int diam = (boxLast.height + box.height) / 2;
			if (Math.abs(woLast.getBox().x + woLast.getBox().width - wo.getBox().x) < avgWidth / 4 && 
				Math.abs(boxLast.y - box.y) < diam / 2)
//				return new WriteObject("x", mergePoints(woLast.getPoints(), wo.getPoints()));
				return new WriteObject("x", woLast, wo);
		}
		else if ( ( woLast.getTeken().equals("(") || woLast.getTeken().equals("c") || woLast.getTeken().equals("4H") ) && 
  				  ( wo.getTeken().equals(")")     || wo.getTeken().equals("xH") ) 
		        ) {
		int diam = (boxLast.height + box.height) / 2;
		if (Math.abs(wo.getBox().x + wo.getBox().width - woLast.getBox().x) < diam / 4 && 
			Math.abs(boxLast.y - box.y) < diam / 2)
//			return new WriteObject("x", mergePoints(woLast.getPoints(), wo.getPoints()));
			return new WriteObject("x", woLast, wo);
	}
		// x = / + \
		else if ( ( woLast.getTeken().equals("/")  && wo.getTeken().equals("\\") )  || 
				  ( woLast.getTeken().equals("/")  && wo.getTeken().equals("1") ) ||
				  ( woLast.getTeken().equals("1")  && wo.getTeken().equals("\\") )
				) { 
			int diam = (boxLast.height + box.height)/2;
			if (distance(woLast.getBoxMid(), wo.getBoxMid()) < diam / 3)
//				return new WriteObject("x", mergePoints(woLast.getPoints(), wo.getPoints()));
				return new WriteObject("x", woLast, wo);
		}
		// x = \ + / of y = \ (klein) + /
		else if ( (woLast.getTeken().equals("\\")  && wo.getTeken().equals("/")) ||
				  (woLast.getTeken().equals("\\")  && wo.getTeken().equals("1")) ||
				  (woLast.getTeken().equals("1")  && wo.getTeken().equals("/"))
				) {
//			if (Math.abs(boxLast.x - box.x) < averageHeight / 4 &&
//					Math.abs(boxLast.y - box.y) < averageHeight / 4 &&	
//					boxLast.height < 2 * box.height / 3)
			if (Math.abs(boxLast.x - box.x) < box.height / 2 &&
					Math.abs(boxLast.y - box.y) < averageHeight / 4 &&	
					boxLast.height < 2 * box.height / 3) {		
//				return new WriteObject("y", mergePoints(woLast.getPoints(),wo.getPoints()));
				return new WriteObject("y", woLast, wo);
			}
			else
			{
				int diam = (boxLast.height + box.height) / 2;
				if (distance(woLast.getBoxMid(), wo.getBoxMid()) < diam / 3)
//					return new WriteObject("x", mergePoints(woLast.getPoints(), wo.getPoints()));
					return new WriteObject("x", woLast, wo);
			}
		}
		
		// =
		else if (((woLast.getTeken().equals("-")) && wo.getTeken().equals("-")) ||
				//((woLast.getTeken().equals("back")) && wo.getTeken().equals("back")))
				 ((woLast.getTeken().equals("-")) && wo.getTeken().equals("back")))
		{
			if (Math.abs(woLast.getBoxMid().x - wo.getBoxMid().x) < averageHeight / 3 && 
			    Math.abs(woLast.getBoxMid().y - wo.getBoxMid().y) < 2 * averageHeight / 3 )
//				return new WriteObject("=", mergePoints(woLast.getPoints(),wo.getPoints()));
				return new WriteObject("=", woLast, wo);
		}
		
		// 7 met extra streepje
		else if ((woLast.getTeken().equals("7")) && (wo.getTeken().equals("-") || wo.getTeken().equals("back"))) 
		{
			if (Math.abs(woLast.getBoxMid().x - wo.getBoxMid().x) < woLast.getBox().width / 2  &&  
				Math.abs(woLast.getBoxMid().y - wo.getBoxMid().y) < woLast.getBox().height / 2)
//				return new WriteObject("7", mergePoints(woLast.getPoints(), wo.getPoints()));
				return new WriteObject("7", woLast, wo);
		}
		
		
		
		
	
		// >= = > + /
		else if(woLast.getTeken().equals(" > ") && wo.getTeken().equals("/")) 
		{			
			if (Math.abs(woLast.getBoxMid().x - wo.getBoxMid().x) < averageHeight / 3 && 
				Math.abs(woLast.getBoxMid().y - wo.getBoxMid().y) < 2 * averageHeight / 3 )
//					return new WriteObject("\u2265 ", mergePoints(woLast.getPoints(),wo.getPoints()));		
					return new WriteObject(" \u2265 ", woLast, wo);
		}

		// >= = > + -
		else if(woLast.getTeken().equals(" > ") && wo.getTeken().equals("-")) 
		{			
			if (Math.abs(woLast.getBoxMid().x - wo.getBoxMid().x) < averageHeight / 3 && 
				Math.abs(woLast.getBoxMid().y - wo.getBoxMid().y) < averageHeight)
//					return new WriteObject(" \u2265 ", mergePoints(woLast.getPoints(),wo.getPoints()));		
					return new WriteObject(" \u2265 ", woLast, wo);
		}

		// <= = < + \
		else if(woLast.getTeken().equals(" < ") && wo.getTeken().equals("\\")) 
		{			
			if (Math.abs(woLast.getBoxMid().x - wo.getBoxMid().x) < averageHeight / 3 && 
				Math.abs(woLast.getBoxMid().y - wo.getBoxMid().y) < 2 * averageHeight / 3 )
//					return new WriteObject(" \u2264 ", mergePoints(woLast.getPoints(),wo.getPoints()));		
					return new WriteObject(" \u2264 ", woLast, wo);
		}

		// <= = < + -
		else if(woLast.getTeken().equals(" < ") && wo.getTeken().equals("-")) 
		{			
			if (Math.abs(woLast.getBoxMid().x - wo.getBoxMid().x) < averageHeight / 3 && 
				Math.abs(woLast.getBoxMid().y - wo.getBoxMid().y) < averageHeight)
//					return new WriteObject(" \u2264 ", mergePoints(woLast.getPoints(),wo.getPoints()));		
					return new WriteObject(" \u2264 ", woLast, wo);
		}
		
		// f = f(zonder streepje) + -
		else if(woLast.getTeken().equals("fH") && wo.getTeken().equals("-")) 
		{			
			if (Math.abs(woLast.getBoxMid().x - wo.getBoxMid().x) < averageHeight / 3 && 
				Math.abs(woLast.getBoxMid().y - wo.getBoxMid().y) < averageHeight)
//					return new WriteObject("f", mergePoints(woLast.getPoints(),wo.getPoints()));	
					return new WriteObject("f", woLast, wo);
		}
		
		// t = tH + -
		else if(woLast.getTeken().equals("tH") && wo.getTeken().equals("-")) 
		{			
			if (Math.abs(woLast.getBoxMid().x - wo.getBoxMid().x) < averageHeight / 3 && 
				Math.abs(woLast.getBoxMid().y - wo.getBoxMid().y) < averageHeight)
//					return new WriteObject("t", mergePoints(woLast.getPoints(),wo.getPoints()));		
					return new WriteObject("t", woLast, wo);
		}
		
		// j = jH + .
		else if(woLast.getTeken().equals("jH") && wo.getTeken().equals(".")) 
		{			
			if (Math.abs(woLast.getBoxMid().x - wo.getBoxMid().x) < averageHeight / 3 && 
				Math.abs(woLast.getBoxMid().y - wo.getBoxMid().y) > averageHeight / 2)
//					return new WriteObject("j", mergePoints2(woLast.getPoints(),wo.getPoints()));		
					return new WriteObject("j", woLast, wo);
		}
		*/
		// p = 1 + pH
		else if(woLast.getTeken().equals("1") && wo.getTeken().equals("pH")) 
		{			
			if (Math.abs(woLast.getBoxMid().x - wo.getBoxMid().x) < averageHeight / 3 && 
				Math.abs(woLast.getBoxMid().y - wo.getBoxMid().y) < averageHeight)
//					return new WriteObject("p", mergePoints(woLast.getPoints(),wo.getPoints()));		
					return new WriteObject("p", woLast, wo);
		}
		// i = tH + .
		else if(woLast.getTeken().equals("tH") && wo.getTeken().equals(".")) 
		{			
			if (Math.abs(woLast.getBoxMid().x - wo.getBoxMid().x) < averageHeight / 3 && 
				Math.abs(woLast.getBoxMid().y - wo.getBoxMid().y) > averageHeight / 2)
//					return new WriteObject("i", mergePoints2(woLast.getPoints(),wo.getPoints()));		
					return new WriteObject("i", woLast, wo);
		}
		
		// k = 1 + <
		else if(woLast.getTeken().equals("1") && wo.getTeken().equals(" < ")) 
		{		
//System.out.println("try2 k");

			if (Math.abs(woLast.getBoxMid().x - wo.getBoxMid().x) < averageHeight / 2 && 
				Math.abs(woLast.getBoxMid().y - wo.getBoxMid().y) < averageHeight / 2)
//					return new WriteObject("k", mergePoints(woLast.getPoints(),wo.getPoints()));		
					return new WriteObject("k", woLast, wo);
		}

		return wo;
	}
	
	//OK
	private ArrayList<Point> mergePoints(ArrayList<Point> p1, ArrayList<Point> p2) 
	{	for (int i = 0; i < p2.size(); i++)
			p1.add(p2.get(i));
		return p1;
	}
	
	//OK
	private ArrayList<Point> mergePoints(ArrayList<Point> p1, ArrayList<Point> p2, boolean reverseFirst) {
		if (!reverseFirst)
			return mergePoints(p1,p2);
		ArrayList<Point> result = new ArrayList<Point>();
		for (int i = p1.size() - 1; i >= 0; i--)
			result.add(p1.get(i));
		for (int i = 0; i < p2.size(); i++)
			result.add(p2.get(i));
		return result;
	}
	
	private ArrayList<Point> mergePoints2(ArrayList<Point> p1, ArrayList<Point> p2)
	{
		for (int i = 0; i < p2.size(); i++)
			p1.add(p2.get(i));
		Point pu = p2.get(p2.size() - 1);
		Point pu1 = new Point(pu.x+1,pu.y);
		Point pu2 = new Point(pu.x,pu.y-1);
		Point pu3 = new Point(pu.x+1,pu.y-1);
		
		p1.add(pu1);
		p1.add(pu2);
		p1.add(pu3);		
		
		return p1;
	}

	
	//OK
	private double distance(Point p1, Point p2) 
	{
		return Math.sqrt(1.0*(p1.x-p2.x)*(p1.x-p2.x) + (p1.y-p2.y)*(p1.y-p2.y));
	}
	
	//OK
	public void wis() 
	{	
		writeObjects.clear();
		lastObject = null;
		lastLastObject = null;
		resetPanelShift();
		strokeContainer.wis();
		paint();
	}

	//OK
	public void back() 
	{	if (writeObjects.size() == 0)
			return;
		
		writeObjects.remove(writeObjects.size() - 1);
		if (writeObjects.size() > 1) {
			lastLastObject = lastObject;
			lastObject = writeObjects.get(writeObjects.size()-1);
		}
		else if (writeObjects.size() > 0) {
			lastLastObject = null;
			lastObject = writeObjects.get(writeObjects.size()-1);
		}
		else
			lastObject = null;	
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
	
	private Point toScreenCoordinates(Point p){
		Point pScreen = new Point(p.getX()+panelShiftX, p.getY()+panelShiftY);
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
			if (e.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
//				logger.info("BUTTON RIGHT");
				mouseOnRight = true;
				shiftReference = new Point(e.getX(), e.getY());
			}
			if (e.getNativeButton() == NativeEvent.BUTTON_LEFT) {
//				logger.info("BUTTON LEFT");
				mouseOnLeft = true;
				Point p = toWorldCoordinates(new Point(e.getX(), e.getY()));
				points.add(p);
			}
		}
		
		public void onMouseMove(MouseMoveEvent e) {
			e.preventDefault();
			e.stopPropagation();
			
			if(mouseOnLeft) {
				Point p = toWorldCoordinates(new Point(e.getX(), e.getY()));
				points.add(p);
				paint();
			}
			if (mouseOnRight) {
				panelShiftX += e.getX()-shiftReference.getX();
				panelShiftY += e.getY()-shiftReference.getY();
				shiftReference = new Point(e.getX(), e.getY());
				paint();
			}
		} 
		
		public void onMouseUp(MouseUpEvent e) 
		{	
			e.preventDefault();
			e.stopPropagation();
			if (mouseOnLeft) {
				mouseOnLeft = false;
				Point p = toWorldCoordinates(new Point(e.getX(), e.getY()));
				points.add(p);
				paint();
				
				
				addWriteObject();
// 				// Move current points as stroke to Strokes
//				Stroke stroke = new Stroke(aPoints);
//				aStrokes.add(stroke);
//				aPoints = new ArrayList<Point>();
			}
			if (mouseOnRight) {
				mouseOnRight = false;
				panelShiftX += e.getX()-shiftReference.getX();
				panelShiftY += e.getY()-shiftReference.getY();
				paint();
			}
		}

		
//		boolean mouseOn = false;
//		
//		public void onMouseDown(MouseDownEvent e) {
//			e.preventDefault();
//			e.stopPropagation();
//			mouseOn = true;
//			Point p = new Point(e.getX(), e.getY());
//			points.add(p);
//		}
//		
//		public void onMouseMove(MouseMoveEvent e) {
//			e.preventDefault();
//			e.stopPropagation();
//			if(mouseOn) 
//			{
//				Point p = new Point(e.getX(), e.getY());
//				points.add(p);
//				paint();
//			}
//		} 
//		
//		public void onMouseUp(MouseUpEvent e) 
//		{	
//			e.preventDefault();
//			e.stopPropagation();
//			mouseOn = false;
//			Point p = new Point(e.getX(), e.getY());
//			points.add(p);
//			//paint();
//			addWriteObject();
//		}
	} 

	//OK
	class MGWTTouchHandler implements TouchStartHandler, TouchEndHandler, TouchMoveHandler {
		Point shiftReference;
		boolean moving = false;
		boolean writing = false;
		public void onTouchStart(TouchStartEvent e) {
			e.preventDefault();
			e.stopPropagation();

			Touch touch = e.getTouches().get(0);
			int eventX = touch.getPageX() - writePanelCanvas.getAbsoluteLeft();
			int eventY = touch.getPageY() - writePanelCanvas.getAbsoluteTop();		
			
			if ( (e.getTouches().length() == 1) && !moving ) {
				writing = true;
				Point p = toWorldCoordinates(new Point(eventX, eventY));
				points.add(p);
				paint();
			}
			
			if ( (e.getTouches().length() == 2) ) {
				moving = true;
				writing = false;
				points.clear();
				shiftReference = new Point(eventX, eventY);
				paint();
			}			
			
			if ( (e.getTouches().length() > 2) ) {
				moving = false;
				writing = false;
				points.clear();
				paint();
			}			

		}
		
		public void onTouchMove(TouchMoveEvent e) 
		{
			e.preventDefault();
			e.stopPropagation();
			
			Touch touch = e.getTouches().get(0);
			int eventX = touch.getPageX() - writePanelCanvas.getAbsoluteLeft();
			int eventY = touch.getPageY() - writePanelCanvas.getAbsoluteTop();		

			if ( writing ) {
				Point p = toWorldCoordinates(new Point(eventX, eventY));
				points.add(p);
				paint();
			}
			
			if (( moving ) && (e.getTouches().length()==2)){
				panelShiftX += eventX-shiftReference.getX();
				panelShiftY += eventY-shiftReference.getY();
				shiftReference = new Point(eventX, eventY);
				paint();
			}
		}
		
		public void onTouchEnd(TouchEndEvent e) {
			Touch touch = null;
			int eventX = 0;
			int eventY = 0;
			
			e.stopPropagation();
			e.preventDefault();

			if (e.getTouches().length() > 0 ) {
				touch = e.getTouches().get(0);
				eventX = touch.getPageX() - writePanelCanvas.getAbsoluteLeft();
				eventY = touch.getPageY() - writePanelCanvas.getAbsoluteTop();
				
					
			}
			
			if (!points.isEmpty() && writing) {
//				if(noParse) {
//					ArrayList<Point> newWriteObjectPoints = new ArrayList<Point>();
//					for (int i=0; i<points.size(); i++) {
//						newWriteObjectPoints.add(
//								new Point(points.get(i).getX(), points.get(i).getY()) );
////						newWriteObjectPoints.add(
////								new Point(points.get(i).getX()-panelShiftX, points.get(i).getY()-panelShiftY) );
//					}
////					resetPanelShift();
//
//					WriteObject wo = new WriteObject(true,newWriteObjectPoints);
//					writeObjects.add(wo);
//				}
//				else
					addWriteObject();
			}
			
			if (e.getTouches().length() < 1 ) {
				writing = false;
				moving = false;
			}	

			paint();
		}
		
//		public void onTouchStart(TouchStartEvent e) 
//		{
//			e.preventDefault();
//			e.stopPropagation();
//			
//			if (e.getTouches().length() > 0) 
//			{
//				Touch touch = e.getTouches().get(0);
//				
//				int eventX = touch.getPageX() - writePanelCanvas.getAbsoluteLeft();
//				int eventY = touch.getPageY() - writePanelCanvas.getAbsoluteTop();				
//				
//				Point p = new Point(eventX,eventY);
//				points.add(p);
//			}
//			e.preventDefault();
//			e.stopPropagation();
//		}
//		
//		public void onTouchMove(TouchMoveEvent e) 
//		{
//			e.preventDefault();
//			e.stopPropagation();
//			
//			if (e.getTouches().length() > 0) 
//			{
//				Touch touch = e.getTouches().get(0);
//				
//				int eventX = touch.getPageX() - writePanelCanvas.getAbsoluteLeft();
//				int eventY = touch.getPageY() - writePanelCanvas.getAbsoluteTop();				
//			    
//				Point p = new Point(eventX,eventY);
//				points.add(p);
//				paint();
//			}
//			e.preventDefault();
//			e.stopPropagation();
//		}
//		
//		public void onTouchEnd(TouchEndEvent e) 
//		{
//			e.stopPropagation();
//			if (e.getTouches().length() > 0) 
//			{
//				Touch touch = e.getTouches().get(0);
//				int eventX = touch.getPageX() - writePanelCanvas.getAbsoluteLeft();
//				int eventY = touch.getPageY() - writePanelCanvas.getAbsoluteTop();
//				Point p = new Point(eventX,eventY);
//				points.add(p);
//			}
//			//paint();
//			addWriteObject();
//			
//		}
	}
	
    class CBL implements ClickHandler
    {
    	public void onClick(ClickEvent e)
    	{
    		if (e.getSource() == correctieButton)
    		{
    			
//System.out.println("CBL aP");

    			if (correctiePanel == null)
    			{
    				if (lastObject != null)
    					correctiePanel = new CorrectiePanel(lastObject.getTeken1(),lastObject.getTeken2(),
    														lastObject.getTeken3(),lastObject.getTeken4(),
    														lastObject.getTeken5(),lastObject.getTeken6());
    				
    				else
    					correctiePanel = new CorrectiePanel("","","","","","");
    				//correctiePanel.setLocation(400, 100);
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
    			else
    			{	
    				if (lastObject != null)
    					correctiePanel.zetTekens(lastObject.getTeken1(),lastObject.getTeken2(),
    											 lastObject.getTeken3(),lastObject.getTeken4(),
												 lastObject.getTeken5(),lastObject.getTeken6());
    				else
    					correctiePanel.zetTekens("","","","","","");
    				
    				//correctiePanel.setVisible(true);
    				correctiePanel.setVisible(!correctiePanel.isVisible());
    			}
    		}
    		if ((correctiePanel != null) && (e.getSource() == correctiePanel.t1Button))
    		{
    			correctiePanel.setVisible(false);
    			if ((lastObject != null) && !lastObject.getTekenRaw().equals(lastObject.getTeken1()))
    				lastObject.zetTeken(lastObject.getTeken1());
    			//produceAction("");
    			eigenaar.writePanelChanged();
    			
    		}
    		if ((correctiePanel != null) && (e.getSource() == correctiePanel.t2Button))
    		{
    			correctiePanel.setVisible(false);
    			if ((lastObject != null) && !lastObject.getTekenRaw().equals(lastObject.getTeken2()))
    				lastObject.zetTeken(lastObject.getTeken2());
    			//produceAction("");
    			eigenaar.writePanelChanged();

    		}
    		if ((correctiePanel != null) && (e.getSource() == correctiePanel.t3Button))
    		{
    			correctiePanel.setVisible(false);
    			if ((lastObject != null) && !lastObject.getTekenRaw().equals(lastObject.getTeken3()))
    				lastObject.zetTeken(lastObject.getTeken3());
    			//produceAction("");
    			eigenaar.writePanelChanged();
    			
    		}
    		if ((correctiePanel != null) && (e.getSource() == correctiePanel.t4Button))
    		{
    			correctiePanel.setVisible(false);
    			if ((lastObject != null) && !lastObject.getTekenRaw().equals(lastObject.getTeken4()))
    				lastObject.zetTeken(lastObject.getTeken4());
    			//produceAction("");
    			eigenaar.writePanelChanged();

    		}
    		if ((correctiePanel != null) && (e.getSource() == correctiePanel.t3Button))
    		{
    			correctiePanel.setVisible(false);
    			if ((lastObject != null) && !lastObject.getTekenRaw().equals(lastObject.getTeken5()))
    				lastObject.zetTeken(lastObject.getTeken5());
    			//produceAction("");
    			eigenaar.writePanelChanged();
    			
    		}
    		if ((correctiePanel != null) && (e.getSource() == correctiePanel.t4Button))
    		{
    			correctiePanel.setVisible(false);
    			if ((lastObject != null) && !lastObject.getTekenRaw().equals(lastObject.getTeken6()))
    				lastObject.zetTeken(lastObject.getTeken6());
    			//produceAction("");
    			eigenaar.writePanelChanged();

    		}
    		//if ((correctiePanel != null) && (e.getSource() == correctiePanel.closeButton))
    		//{
    		//	correctiePanel.setVisible(false);
    		//}

    	}
    }
    
    class ListChangeHandler implements ChangeHandler
	{
		@Override
		public void onChange(ChangeEvent e)
		{
			if (e.getSource() == sampleInspectComboBox)
			{
				int selectedindex = sampleInspectComboBox.getSelectedIndex();
				if (selectedindex != 0)
				{	objectToAnalyse = writeObjects.get(selectedindex-1);
					String key = sampleInspectComboBox.getItemText(0);
					String teken = objectToAnalyse.parse(key);
					if(!key.equals(teken)) {
						checkerBooleans = ""+OneStrokeChecker.check(objectToAnalyse,key);
						checkerBooleans = checkerBooleans + teken+":\n";
						checkerBooleans = checkerBooleans + OneStrokeChecker.getCheckerBooleans();
					}
					
					logger.info(checkerBooleans);
					
					paint();
				}
				

			}
			
		}
	}

}


