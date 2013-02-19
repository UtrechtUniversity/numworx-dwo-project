package nl.uu.fi.dwo.mobile.client.ui;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;


import nl.uu.fi.dwo.mobile.client.ui.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.mobile.client.ui.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.mobile.client.ui.formuleobjects.FormuleTeken;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.googlecode.mgwt.dom.client.event.touch.Touch;
import com.googlecode.mgwt.dom.client.event.touch.TouchEndEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchEndHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchMoveEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchMoveHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartHandler;
import com.googlecode.mgwt.ui.client.widget.touch.TouchDelegate;
import com.googlecode.mgwt.ui.client.widget.touch.TouchWidgetMobileImpl;



public class DigitsPanel extends HorizontalPanel 
{
	private FormuleKeyboard kb;
	ArrayList<WriteObject> writeObjects;
	ArrayList<Point> points;
	WriteObject lastObject;
	
	private Canvas writePanelCanvas;
	private Context2d g;
	
	private CssColor ruitjesKleur = CssColor.make(190, 190, 190);
	private CssColor zwart = CssColor.make(0, 0, 0);
	private CssColor drawingColor = CssColor.make(0, 0, 0);
	private int gridSize = 20;
	
	private int width = 700;
	private int height = 238;
	
	private FormuleViewer formuleViewer;
	private Panel formulePanel;
	
	
	public DigitsPanel(FormuleKeyboard formuleKeyboard) {
		this.kb = formuleKeyboard;
		
		//this.width = width;
		//this.height = height;
		setSize("100%", "250px");
		
		WriteObject.initSamples();
		writeObjects = new ArrayList<WriteObject>();
		points = new ArrayList<Point>();
		
		writePanelCanvas = Canvas.createIfSupported();
		writePanelCanvas.setWidth(100 + "pct");
		writePanelCanvas.setHeight(height + "px");
		writePanelCanvas.setCoordinateSpaceWidth(width);
		writePanelCanvas.setCoordinateSpaceHeight(height);
		writePanelCanvas.getElement().getStyle().setMarginTop(6,Unit.PX);
		add(writePanelCanvas);
		
		initContext2d();
		
		MouseHandler mouseHandler = new MouseHandler();
		writePanelCanvas.addMouseDownHandler(mouseHandler);
		writePanelCanvas.addMouseMoveHandler(mouseHandler);
		writePanelCanvas.addMouseUpHandler(mouseHandler);
					
		TouchWidgetMobileImpl twmi = new TouchWidgetMobileImpl();
		
		MGWTTouchHandler touchHandler = new MGWTTouchHandler();
		twmi.addTouchStartHandler(writePanelCanvas,touchHandler);
		twmi.addTouchMoveHandler(writePanelCanvas,touchHandler);
		twmi.addTouchEndHandler(writePanelCanvas,touchHandler);
		
		//formuleViewer = new FormuleViewer("$f@");
		//formulePanel = formuleViewer.getAsPanel();
		//formulePanel.setWidth(width + "px");
		//formulePanel.setHeight(200 + "px");
		
		TouchButton b = FormuleKeyBoardButtons.getButton("apply", formuleKeyboard);
		
		b.setWidth("30px");
		b.setHeight("16px");
		add(b);
		
		paint();
		
	}
	
	public FormuleViewer getFormuleViewer() {
		return formuleViewer;
	}
	
	public Canvas getCanvas() {
		return writePanelCanvas;
	}
	
	public void initContext2d() {
		g = writePanelCanvas.getContext2d();
	}
	
	public void paint() {
		paintComponent(g);
	}
	
	public void paintComponent(Context2d g) {
		g.setLineWidth(1.0d);
		g.clearRect(0, 0, width, height);
		
		g.setFillStyle(CssColor.make(240, 240, 240));
		g.fillRect(0, 0, width, height);
		
		if (true) {
			g.setStrokeStyle(ruitjesKleur);
			int vSteps = height / gridSize;
			for (int vCnt = 1; vCnt <= vSteps; vCnt++) {
				g.beginPath();
				g.moveTo(0, vCnt * gridSize);
				g.lineTo(width - 1, vCnt * gridSize);
				g.stroke();
			}
			int hSteps = width / gridSize;
			for (int hCnt = 1; hCnt <= hSteps; hCnt++) {
				g.beginPath();
				g.moveTo(hCnt * gridSize, 0);
				g.lineTo(hCnt * gridSize, height - 1);
				g.stroke();
			}
		}
		
		g.setStrokeStyle(zwart);
		for(int i = 0 ; i < writeObjects.size() ; i++) {
			writeObjects.get(i).draw(g);
		}
		
		if(points.size() > 0) {
			g.beginPath();
			g.moveTo(points.get(0).x, points.get(0).y);
			for(int j = 1 ; j <points.size() ; j++) {
				g.lineTo(points.get(j).x, points.get(j).y);
			}
			g.stroke();
		}
	}
	
	public String parseFormule() {
		ArrayList<WriteObject> writeObjectsToDo = new ArrayList<WriteObject>();
		for(int i=0 ; i<writeObjects.size() ; i++){
			WriteObject wo = writeObjects.get(i);
			writeObjectsToDo.add(wo);
		}
		//return "$f" + parseBox(new Rectangle(0, 0, width, height), writeObjectsToDo, null) + "@";
		return parseBox(new Rectangle(0, 0, width, height), writeObjectsToDo, null);
	}
	
	public String parseBox(Rectangle box, ArrayList<WriteObject> writeObjectsToDo, WriteObject lastWriteObject ) {
		String string = "";
		
		ArrayList<WriteObject> writeObjectsToDoNow = new ArrayList<WriteObject>();
		for(int i=0 ; i<writeObjectsToDo.size() ; i++){
			WriteObject wo = writeObjectsToDo.get(i);
			if(box.contains(wo.getBoxMid().x, wo.getBoxMid().y))
					writeObjectsToDoNow.add(wo);
		}
		
		WriteObject nextWriteObject = null;
		
		int telr = 0;
		while(writeObjectsToDoNow.size() > 0 && telr<1000) {
			telr++;
			int minX = width;
			for(int i=0 ; i<writeObjectsToDoNow.size() ; i++){
				WriteObject wo = writeObjectsToDoNow.get(i);
				if(wo.getBox().x < minX) {
					nextWriteObject = wo;
					minX = wo.getBox().x;
				}
			}
			
			//breuk
			if(isBreuk(nextWriteObject, writeObjectsToDoNow)) {
				int x = nextWriteObject.getBox().x;
				int yt = nextWriteObject.getBox().y - nextWriteObject.getBox().width;
				int yn = nextWriteObject.getBox().y + nextWriteObject.getBox().height;
				int w = nextWriteObject.getBox().width;
				int h = nextWriteObject.getBox().width;
				
				writeObjectsToDoNow.remove(nextWriteObject);
				writeObjectsToDo.remove(nextWriteObject);
				
				String teller = parseBox(new Rectangle(x,yt,w,h), writeObjectsToDoNow, null);
				String noemer = parseBox(new Rectangle(x,yn,w,h), writeObjectsToDoNow, null);
				string = string + "$b" + teller + "$n" + noemer + "@@";
				
				writeObjectsToDoNow = removeAllInBox(writeObjectsToDoNow,new Rectangle(x,yt,w,h));
				writeObjectsToDoNow = removeAllInBox(writeObjectsToDoNow,new Rectangle(x,yn,w,h));
			}
			
			//wortel
			else if(nextWriteObject.getTeken().equals("sqrt")) {
				int x = nextWriteObject.getBox().x+10;
				int y = nextWriteObject.getBox().y;
				int w = nextWriteObject.getBox().width-10;
				int h = nextWriteObject.getBox().height;
				
				writeObjectsToDoNow.remove(nextWriteObject);
				writeObjectsToDo.remove(nextWriteObject);
				String operand = parseBox(new Rectangle(x,y,w,h), writeObjectsToDoNow, null);
				string = string + "$w" + operand + "@";
				
				writeObjectsToDoNow = removeAllInBox(writeObjectsToDoNow,new Rectangle(x,y,w,h));
				
			}
			
			//macht 
			else if(lastWriteObject != null && nextWriteObject.getBoxMid().y + nextWriteObject.getBox().height/2 < lastWriteObject.getBoxMid().y ){
				int x = nextWriteObject.getBox().x + nextWriteObject.getBox().width;
				int y = nextWriteObject.getBox().y;
				int w = 5*nextWriteObject.getBox().width;
				int h = nextWriteObject.getBox().height;
				
				String restMacht = parseBox(new Rectangle(x,y,w,h), writeObjectsToDoNow, nextWriteObject);
				string = string + "$m" +nextWriteObject.getTeken() + restMacht + "@";
				writeObjectsToDoNow.remove(nextWriteObject);
				writeObjectsToDo.remove(nextWriteObject);
				
			} 
			
			else {
				writeObjectsToDoNow.remove(nextWriteObject);
				writeObjectsToDo.remove(nextWriteObject);
				String teken = nextWriteObject.getTeken();
				if(teken.indexOf("_")>0)
					teken = teken.substring(0,teken.indexOf("_"));
				string = string + teken;
			}
			lastWriteObject = nextWriteObject;
			
		}
		return string;
	}
	
	private boolean isBreuk(WriteObject wo, ArrayList<WriteObject> writeObjectsToDo) {
		boolean breuk = false;
		if(!wo.getTeken().equals("-")) return false;
		int x = wo.getBox().x;
		int y = wo.getBox().y - wo.getBox().width;
		int w = wo.getBox().width;
		int h = wo.getBox().width;
		Rectangle box = new Rectangle(x,y,w,h);
		for(int i=0 ; i<writeObjectsToDo.size() ; i++){
			WriteObject writeObject = writeObjectsToDo.get(i);
			if(box.contains(writeObject.getBoxMid().x, writeObject.getBoxMid().y))
				breuk = true;
		}
		return breuk;
	}
	
	private  ArrayList<WriteObject> removeAllInBox(ArrayList<WriteObject> wo, Rectangle box) {
		ArrayList<WriteObject> woNew = new ArrayList<WriteObject>();
		for(int i=0 ; i<wo.size() ; i++){
			if(!box.contains(wo.get(i).getBoxMid()))
				woNew.add(wo.get(i));
		}
		return woNew;
	}
	
	private void addWriteObject() {
		WriteObject wo = new WriteObject(points);
		
		if(wo.getTeken().equals("null")) {
		}
		//wis of gum
		else if(wo.getTeken().equals("back")) {
			int x = wo.getBox().x;
			int y = wo.getBoxMid().y - wo.getBox().width/2;
			int w = wo.getBox().width;
			int h = wo.getBox().width;
			Rectangle box = new Rectangle(x, y, w, h);
			writeObjects = removeAllInBox(writeObjects,box);
		}
		else {
			WriteObject woTwoStroke = tryTwoStroke(lastObject, wo);
			if(wo!=woTwoStroke) {
				writeObjects.remove(lastObject);
				wo = woTwoStroke;
			}
			lastObject = wo;
			writeObjects.add(wo);
			
		}
		points.clear();
		paint();
		//formuleViewer = new FormuleViewer(parseFormule());
		//eigenaar.setChanged();
		
		String text = parseFormule();
		FormuleEditor editor = kb.getEditor();
		if (editor != null)
		{
			editor.clearAll();
			editor.insert(text);
			//editor.addElement(new FormuleTeken(editor.getCurrentRegel(), '1'));
		}
		
	}
	
	private WriteObject tryTwoStroke(WriteObject woLast, WriteObject wo) {
		if(woLast==null) return wo;
		Rectangle boxLast = woLast.getBox();
		Rectangle box = wo.getBox();
		
		// + 
		if(woLast.getTeken().equals("1") && wo.getTeken().equals("-")) {
			int diam = (boxLast.height + box.width)/2;
			if(distance(woLast.getBoxMid(), wo.getBoxMid()) < diam/4)
				return new WriteObject("+",mergePoints(woLast.getPoints(), wo.getPoints()));
		}
		// 5
		else if((woLast.getTeken().equals("5") || woLast.getTeken().equals("b"))  && wo.getTeken().equals("-")) {
			int diam = (boxLast.height + box.width)/2;
			if(Math.abs(woLast.getBoxMid().x - wo.getBoxMid().x) < diam && Math.abs(boxLast.y-box.y) < diam/2)
				return new WriteObject("5",mergePoints(woLast.getPoints(), wo.getPoints()));
		}
		// x
		else if(woLast.getTeken().equals(")")  && wo.getTeken().equals("(")) {
			int diam = (boxLast.height + box.height)/2;
			if(Math.abs(woLast.getBox().x + woLast.getBox().width - wo.getBoxMid().x) < diam/2 && Math.abs(boxLast.y-box.y) < diam/2)
				return new WriteObject("x",mergePoints(woLast.getPoints(), wo.getPoints()));
		}
		else if(woLast.getTeken().equals("/")  && wo.getTeken().equals("\\")) {
			int diam = (boxLast.height + box.height)/2;
			if(distance(woLast.getBoxMid(), wo.getBoxMid()) < diam/4)
				return new WriteObject("x",mergePoints(woLast.getPoints(), wo.getPoints()));
		}
		else if(woLast.getTeken().equals("\\")  && wo.getTeken().equals("/")) {
			int diam = (boxLast.height + box.height)/2;
			if(distance(woLast.getBoxMid(), wo.getBoxMid()) < diam/4)
				return new WriteObject("x",mergePoints(woLast.getPoints(), wo.getPoints()));
		}
		// 7
		else if((woLast.getTeken().equals("7"))  && wo.getTeken().equals("-")) {
			if(Math.abs(woLast.getBoxMid().x - wo.getBoxMid().x) < woLast.getBox().width/2  &&  Math.abs(woLast.getBoxMid().y - wo.getBoxMid().y) < woLast.getBox().height/2)
				return new WriteObject("7",mergePoints(woLast.getPoints(), wo.getPoints()));
		}
		// =
		else if((woLast.getTeken().equals("-"))  && wo.getTeken().equals("-")) {
			if(Math.abs(woLast.getBoxMid().x - wo.getBoxMid().x) < woLast.getBox().width/2 && Math.abs(woLast.getBoxMid().y - wo.getBoxMid().y) < woLast.getBox().width/2 )
				return new WriteObject("=",mergePoints(woLast.getPoints(), wo.getPoints()));
		}
		
		return wo;
	}
	
	private ArrayList<Point> mergePoints(ArrayList<Point> p1, ArrayList<Point> p2) {
		for(int i=0 ; i<p2.size() ; i++)
			p1.add(p2.get(i));
		return p1;
	}
	
	private double distance(Point p1, Point p2) {
		return Math.sqrt(1.0*(p1.x-p2.x)*(p1.x-p2.x) + (p1.y-p2.y)*(p1.y-p2.y));
	}
	
	public void wis() {
		writeObjects.clear();
		paint();
	}
	
	public void back() {
		writeObjects.remove(writeObjects.size()-1);
		paint();
	}
	
	
	
	class MouseHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler {
		
		boolean mouseOn = false;
		
		public void onMouseDown(MouseDownEvent e) {
			e.stopPropagation();
			mouseOn = true;
			Point p = new Point(e.getX(), e.getY());
			points.add(p);
		}
		
		public void onMouseMove(MouseMoveEvent e) {
			e.stopPropagation();
			if(mouseOn) {
				Point p = new Point(e.getX(), e.getY());
				points.add(p);
				paint();
			}
		} 
		
		public void onMouseUp(MouseUpEvent e) {	
			e.stopPropagation();
			mouseOn = false;
			Point p = new Point(e.getX(), e.getY());
			points.add(p);
			paint();
			addWriteObject();
		}
	} 

	class MGWTTouchHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{
		
		public void onTouchStart(TouchStartEvent e) {
			e.preventDefault();
			e.stopPropagation();
			
			if (e.getTouches().length() > 0) {
				Touch touch = e.getTouches().get(0);
				
				int eventX = touch.getPageX() - writePanelCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - writePanelCanvas.getAbsoluteTop();				
				
				Point p = new Point(eventX,eventY);
				points.add(p);
			}
			e.preventDefault();
			e.stopPropagation();
		}
		
		public void onTouchMove(TouchMoveEvent e) {
			e.preventDefault();
			e.stopPropagation();
			
			if (e.getTouches().length() > 0) {
				Touch touch = e.getTouches().get(0);
				
				int eventX = touch.getPageX() - writePanelCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - writePanelCanvas.getAbsoluteTop();				
			    
				Point p = new Point(eventX,eventY);
				points.add(p);
				paint();
			}
			e.preventDefault();
			e.stopPropagation();
		}
		
		public void onTouchEnd(TouchEndEvent e) {
			e.stopPropagation();
			if (e.getTouches().length() > 0) {
				Touch touch = e.getTouches().get(0);
				int eventX = touch.getPageX() - writePanelCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - writePanelCanvas.getAbsoluteTop();
				Point p = new Point(eventX,eventY);
				points.add(p);
			}
				//paint();
				addWriteObject();
			//}
		}
	}
}


