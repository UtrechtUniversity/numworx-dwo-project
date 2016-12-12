package fi.writemathgwt.client;

//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.Point;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JButton;

//import javax.swing.JButton;


import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style.Unit;
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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;


public class WritePanel extends LayoutPanel { //HorizontalPanel
	private static Logger logger = Logger.getLogger("WritePanel");

	WritePanelHolder eigenaar;
	ArrayList<WriteObject> writeObjects;
	ArrayList<Point> points;
	WriteObject lastObject;
	
	private Canvas writePanelCanvas;
	private Context2d g;
	
	private CssColor ruitjesKleur = CssColor.make(190, 190, 190);
	private CssColor zwart = CssColor.make(0, 0, 0);
	private CssColor drawingColor = CssColor.make(0, 0, 0);
	private int gridSize = 20;
	
	private static int defaultWidth = 680;
	private static int defaultHeight = 238;
	
	private int width;
	private int height;
	
	//private FormuleViewer formuleViewer;
	//private Panel formulePanel;
	
	private int averageHeight = 30;
	
	PushButton correctieButton;
	CorrectiePanel correctiePanel;

	int tekenSet = 1;
	
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
		
		tekenSet = tekenS;
		
		setSize("100%", "250px");
		
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
		paintComponent(g);
	}
	
	public void paintComponent(Context2d g) 
	{
		g.setLineWidth(1.0d);
		g.clearRect(0, 0, width, height);
		
		g.setFillStyle(CssColor.make(240, 240, 240));
		g.fillRect(0, 0, width, height);
		
		if (true) 
		{
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
		for(int i = 0 ; i < writeObjects.size() ; i++) 
		{
			writeObjects.get(i).draw(g);
		}
		
		if (points.size() > 0) 
		{
			g.beginPath();
			g.moveTo(points.get(0).x, points.get(0).y);
			for(int j = 1 ; j <points.size() ; j++) 
			{
				g.lineTo(points.get(j).x, points.get(j).y);
			}
			g.stroke();
		}
	}
	
	public String parseFormule() 
	{
		
		ArrayList<WriteObject> writeObjectsToDo = new ArrayList<WriteObject>();
		// make a compact deep copy
		for (int i = 0; i < writeObjects.size(); i++)
		{	WriteObject wo = new WriteObject(writeObjects.get(i));
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
				if (bs.isBreuk && wBox.contains(bs.getBoxMid().x, bs.getBoxMid().y))
				{	

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

		
		
		//Buiten de DWOplayer:
		return parseBox(new Rectangle(0, 0, width, height), writeObjectsToDo, null, null);
		
		//Binnen de DWOplayer:
		//return parseBox(new Rectangle(0, 0, width, height), writeObjectsToDo, null, null);
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
						   WriteObject lastWriteObject, WriteObject boxOwner) 
	{
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
		for (int i = 0; i < writeObjectsToDoNow.size(); i++)
		{	WriteObject wo = writeObjectsToDoNow.get(i);
		
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
		
		if ((wo.isTellerVan == null) && (wo.isNoemerVan == null))
		{	// neem alle hoogte tot bovenaan
			int tx = wo.getBox().x;
			int ty = 0; // wo.getBox().y - wo.getBox().width;
			int tw = wo.getBox().width;
			int th = wo.getBox().y; //wo.getBox().width;
			
			wo.tellerBox = new Rectangle(tx,ty,tw,th);			
			
			// neem alle hoogte tot onderaan
			int nx = wo.getBox().x;
			int ny = wo.getBox().y + wo.getBox().height;
			int nw = wo.getBox().width;
			int nh = height - ny; //wo.getBox().width;
			
			wo.noemerBox = new Rectangle(nx,ny,nw,nh);
			
		}
		else if ((wo.isTellerVan != null) && (wo.isNoemerVan == null))
		{
			// neem alle hoogte tot bovenaan
			int tx = wo.getBox().x;
			int ty = 0; // wo.getBox().y - wo.getBox().width;
			int tw = wo.getBox().width;
			int th = wo.getBox().y; //wo.getBox().width;
			wo.tellerBox = new Rectangle(tx,ty,tw,th);
			
			// pas tussen wo boven wo.isTellerVan
			int nx = wo.getBox().x;
			int ny = wo.getBox().y + wo.getBox().height;
			int nw = wo.getBox().width;
			int nh = wo.isTellerVan.getBox().y - ny; 
			wo.noemerBox = new Rectangle(nx,ny,nw,nh);
			
		}
		
		else if ((wo.isTellerVan == null) && (wo.isNoemerVan != null))
		{	
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
			int nh = height - ny; //wo.getBox().width;
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
	private void addWriteObject() 
	{
		WriteObject wo = new WriteObject(points);
		
		if ("null".equals(wo.getTeken())) 
		{
		}
		//wis of gum
		else if ("back".equals(wo.getTeken())) 
		{
			boolean isBack = true;
			if (lastObject != null)
			{	
				
//System.out.println("lo not null");				
				WriteObject woTwoStroke = tryTwoStroke(lastObject, wo);
				if (wo != woTwoStroke) 
				{	
//System.out.println("two");					
					writeObjects.remove(lastObject);
					wo = woTwoStroke;
					lastObject = wo;
					updateAverageHeight(wo);
					writeObjects.add(wo);
					isBack = false;
				}
			}
//System.out.println("isBack " + isBack);			
			if (isBack)
			{
				int x = wo.getBox().x;
				int y = wo.getBoxMid().y - wo.getBox().width / 2;
				int w = wo.getBox().width;
				int h = wo.getBox().width;
				Rectangle box = new Rectangle(x, y, w, h);
				int objectsBefore = writeObjects.size();
//System.out.println("before " + objectsBefore);				
				writeObjects = removeAllInBox(writeObjects,box);
				int objectsAfter = writeObjects.size();
//System.out.println("after " + objectsAfter);				
				if (objectsBefore != objectsAfter)
				{	lastObject = null;
				}
				else
				{	lastObject = wo;
					writeObjects.add(wo);
				}
			}
		}
		else if (lastObject != null)
		{
			WriteObject woTwoStroke = tryTwoStroke(lastObject, wo);
			if (wo != woTwoStroke) 
			{
				writeObjects.remove(lastObject);
				wo = woTwoStroke;
			}
			lastObject = wo;
			updateAverageHeight(wo);
			writeObjects.add(wo);
			
		}
		else 
		{
			lastObject = wo;
			updateAverageHeight(wo);
			writeObjects.add(wo);
		}
		
		points.clear();
		paint();
		
		//Buiten de DWOplayer:
		//formuleViewer = new FormuleViewer(parseFormule());
		eigenaar.writePanelChanged();
		
		//Binnen de DWOplayer:
		//String text = parseFormule();
		//FormuleEditor editor = kb.getEditor();
		//if (editor != null)	{
		//	editor.clearAll();
		//	editor.insert(text);
		//}
	}
	
	protected void addWriteObject(String teken, ArrayList<Point> points)
	{
		
//System.out.println("addWriteObject " + teken + " " + points.size());
		
		WriteObject wo = new WriteObject(teken, points);
		lastObject = wo;
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
		// make root
		FormuleRoot formuleRoot = new FormuleRoot(this);
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
	
	private WriteObject tryTwoStroke(WriteObject woLast, WriteObject wo) {
		
		logger.info("T2S :: last = "+ woLast +", this = "+ wo); 
		if ( (woLast == null) || ( woLast.isTwoStrokeObject()) ) {
			return wo; 
		}
		Rectangle boxLast = woLast.getBox();
		Rectangle box = wo.getBox();
		
		// + = 1 + -
		if (woLast.getTeken().equals("1") && (wo.getTeken().equals("-") || wo.getTeken().equals("back"))) 
		{
			int diam = (boxLast.height + box.width) / 2;
			if (distance(woLast.getBoxMid(), wo.getBoxMid()) < averageHeight / 3)
				return new WriteObject("+", mergePoints(woLast.getPoints(), wo.getPoints()));
		}
		// + = - + 1 
		else if (woLast.getTeken().equals("-") && wo.getTeken().equals("1")) 
		{
			int diam = (boxLast.height + box.width) / 2;
			if (distance(woLast.getBoxMid(), wo.getBoxMid()) < averageHeight / 3)
				return new WriteObject("+", mergePoints(woLast.getPoints(), wo.getPoints()));
		}
		// + = / + -
		else if (woLast.getTeken().equals("/") && (wo.getTeken().equals("-") || wo.getTeken().equals("back"))) 
		{
			int diam = (boxLast.height + box.width) / 2;
			if (distance(woLast.getBoxMid(), wo.getBoxMid()) < averageHeight / 3)
				return new WriteObject("+", mergePoints(woLast.getPoints(), wo.getPoints()));
		}
		// + = - + / NB in GWT averageHeight / 4
		else if(woLast.getTeken().equals("-") && wo.getTeken().equals("/")) 
		{
			int diam = (boxLast.height + box.width)/ 2;
			if (distance(woLast.getBoxMid(), wo.getBoxMid()) < averageHeight / 3)
				return new WriteObject("+", mergePoints(woLast.getPoints(), wo.getPoints()));
		}
		// 5 = 5H of b + - in 
		else if (
				((woLast.getTeken().equals("5H") || woLast.getTeken().equals("b")) && 
				 (wo.getTeken().equals("-") || wo.getTeken().equals("back")))) 
		{
			int diam = (boxLast.height + box.width) / 2;
			if (Math.abs(woLast.getBoxMid().x - wo.getBoxMid().x) < diam && 
				Math.abs(boxLast.y - box.y) < diam / 2)
				return new WriteObject("5", mergePoints(woLast.getPoints(), wo.getPoints(),true));
		}
		// 4 = 4H + 1
		else if ( (woLast.getTeken().equals("4H") && wo.getTeken().equals("1")) || 
				  (woLast.getTeken().equals("tH") && wo.getTeken().equals("1")) ||
 				  (woLast.getTeken().equals("<") && wo.getTeken().equals("1")) || 
 				  (woLast.getTeken().equals("4H") && wo.getTeken().equals("/")) || 
				  (woLast.getTeken().equals("tH") && wo.getTeken().equals("/")) ||
 				  (woLast.getTeken().equals("<") && wo.getTeken().equals("/"))
				)
		{
			int diam = (boxLast.width + box.height) / 2;

/* Old merge - criteria 4 */
//			if (Math.abs(woLast.getBoxMid().x - wo.getBoxMid().x) < (diam / 3)  && 
//				Math.abs(boxLast.y - box.y) < diam ) {
/* end of old merge criteria 4 */			

			if ( (box.x+box.width > (boxLast.x + 0.40 *boxLast.width)) && (box.x+box.width < (boxLast.x + 1.15 *boxLast.width)) &&
 				 (box.y > (boxLast.y - 0.05 *boxLast.height)) && (box.y < (boxLast.y + 0.95 *boxLast.height))
			   ) {
					return new WriteObject("4",mergePoints(woLast.getPoints(), wo.getPoints(),true));	
			} 
		}
		// x = ) + (
		else if ( ( woLast.getTeken().equals(")") || woLast.getTeken().equals("xH") ) && 
				    ( wo.getTeken().equals("(") || wo.getTeken().equals("c") || wo.getTeken().equals("4H") ) 
			    ) {
			int avgWidth = (boxLast.width + box.width) / 2;
			int diam = (boxLast.height + box.height) / 2;
			logger.info("diam :: "+ diam);
			logger.info("Crit 1 :: "+ Math.abs(woLast.getBox().x + woLast.getBox().width - wo.getBoxMid().x));
			if (Math.abs(woLast.getBox().x + woLast.getBox().width - wo.getBox().x) < avgWidth / 4 && 
				Math.abs(boxLast.y - box.y) < diam / 2)
				return new WriteObject("x", mergePoints(woLast.getPoints(), wo.getPoints()));
		}
		else if ( ( woLast.getTeken().equals("(") || woLast.getTeken().equals("c") || woLast.getTeken().equals("4H") ) && 
  				  ( wo.getTeken().equals(")")     || wo.getTeken().equals("xH") ) 
		        ) {
		int diam = (boxLast.height + box.height) / 2;
		if (Math.abs(wo.getBox().x + wo.getBox().width - woLast.getBox().x) < diam / 4 && 
			Math.abs(boxLast.y - box.y) < diam / 2)
			return new WriteObject("x", mergePoints(woLast.getPoints(), wo.getPoints()));
	}
		// x = / + \
		else if ( ( woLast.getTeken().equals("/")  && wo.getTeken().equals("\\") )  || 
				  ( woLast.getTeken().equals("/")  && wo.getTeken().equals("1") ) ||
				  ( woLast.getTeken().equals("1")  && wo.getTeken().equals("\\") )
				) { 
			int diam = (boxLast.height + box.height)/2;
			if (distance(woLast.getBoxMid(), wo.getBoxMid()) < diam / 3)
				return new WriteObject("x", mergePoints(woLast.getPoints(), wo.getPoints()));
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
					boxLast.height < 2 * box.height / 3)
			{		return new WriteObject("y", mergePoints(woLast.getPoints(),wo.getPoints()));
			}
			else
			{
				int diam = (boxLast.height + box.height) / 2;
				if (distance(woLast.getBoxMid(), wo.getBoxMid()) < diam / 3)
					return new WriteObject("x", mergePoints(woLast.getPoints(), wo.getPoints()));
			}
		}
		
		// 7 met extra streepje
		else if ((woLast.getTeken().equals("7")) && (wo.getTeken().equals("-") || wo.getTeken().equals("back"))) 
		{
			if (Math.abs(woLast.getBoxMid().x - wo.getBoxMid().x) < woLast.getBox().width / 2  &&  
				Math.abs(woLast.getBoxMid().y - wo.getBoxMid().y) < woLast.getBox().height / 2)
				return new WriteObject("7", mergePoints(woLast.getPoints(), wo.getPoints()));
		}
		
		// =
		else if (((woLast.getTeken().equals("-")) && wo.getTeken().equals("-")) ||
				 ((woLast.getTeken().equals("back")) && wo.getTeken().equals("back")))
		{
			if (Math.abs(woLast.getBoxMid().x - wo.getBoxMid().x) < averageHeight / 3 && 
			    Math.abs(woLast.getBoxMid().y - wo.getBoxMid().y) < 2 * averageHeight / 3 )
				return new WriteObject("=", mergePoints(woLast.getPoints(),wo.getPoints()));
		}
	
		// >= = > + /
		else if(woLast.getTeken().equals(" > ") && wo.getTeken().equals("/")) 
		{			
			if (Math.abs(woLast.getBoxMid().x - wo.getBoxMid().x) < averageHeight / 3 && 
				Math.abs(woLast.getBoxMid().y - wo.getBoxMid().y) < 2 * averageHeight / 3 )
					return new WriteObject(" \u2265 ", mergePoints(woLast.getPoints(),wo.getPoints()));		
		}

		// >= = > + -
		else if(woLast.getTeken().equals(" > ") && wo.getTeken().equals("-")) 
		{			
			if (Math.abs(woLast.getBoxMid().x - wo.getBoxMid().x) < averageHeight / 3 && 
				Math.abs(woLast.getBoxMid().y - wo.getBoxMid().y) < averageHeight)
					return new WriteObject(" \u2265 ", mergePoints(woLast.getPoints(),wo.getPoints()));		
		}

		// <= = < + \
		else if(woLast.getTeken().equals(" < ") && wo.getTeken().equals("\\")) 
		{			
			if (Math.abs(woLast.getBoxMid().x - wo.getBoxMid().x) < averageHeight / 3 && 
				Math.abs(woLast.getBoxMid().y - wo.getBoxMid().y) < 2 * averageHeight / 3 )
					return new WriteObject(" \u2264 ", mergePoints(woLast.getPoints(),wo.getPoints()));		
		}

		// <= = < + -
		else if(woLast.getTeken().equals(" < ") && wo.getTeken().equals("-")) 
		{			
			if (Math.abs(woLast.getBoxMid().x - wo.getBoxMid().x) < averageHeight / 3 && 
				Math.abs(woLast.getBoxMid().y - wo.getBoxMid().y) < averageHeight)
					return new WriteObject(" \u2264 ", mergePoints(woLast.getPoints(),wo.getPoints()));		
		}
		// f = f(zonder streepje) + -
		else if(woLast.getTeken().equals("f") && wo.getTeken().equals("-")) 
		{			
			if (Math.abs(woLast.getBoxMid().x - wo.getBoxMid().x) < averageHeight / 3 && 
				Math.abs(woLast.getBoxMid().y - wo.getBoxMid().y) < averageHeight)
					return new WriteObject("f", mergePoints(woLast.getPoints(),wo.getPoints()));		
		}
		// t = tH + -
		else if(woLast.getTeken().equals("tH") && wo.getTeken().equals("-")) 
		{			
			if (Math.abs(woLast.getBoxMid().x - wo.getBoxMid().x) < averageHeight / 3 && 
				Math.abs(woLast.getBoxMid().y - wo.getBoxMid().y) < averageHeight)
					return new WriteObject("t", mergePoints(woLast.getPoints(),wo.getPoints()));		
		}
		// p = 1 + pH
		else if(woLast.getTeken().equals("1") && wo.getTeken().equals("pH")) 
		{			
			if (Math.abs(woLast.getBoxMid().x - wo.getBoxMid().x) < averageHeight / 3 && 
				Math.abs(woLast.getBoxMid().y - wo.getBoxMid().y) < averageHeight)
					return new WriteObject("p", mergePoints(woLast.getPoints(),wo.getPoints()));		
		}
		// i = tH + .
		else if(woLast.getTeken().equals("tH") && wo.getTeken().equals(".")) 
		{			
			if (Math.abs(woLast.getBoxMid().x - wo.getBoxMid().x) < averageHeight / 3 && 
				Math.abs(woLast.getBoxMid().y - wo.getBoxMid().y) > averageHeight / 2)
					return new WriteObject("i", mergePoints2(woLast.getPoints(),wo.getPoints()));		
		}
		// j = jH + .
		else if(woLast.getTeken().equals("jH") && wo.getTeken().equals(".")) 
		{			
			if (Math.abs(woLast.getBoxMid().x - wo.getBoxMid().x) < averageHeight / 3 && 
				Math.abs(woLast.getBoxMid().y - wo.getBoxMid().y) > averageHeight / 2)
					return new WriteObject("j", mergePoints2(woLast.getPoints(),wo.getPoints()));		
		}
		// k = 1 + <
		else if(woLast.getTeken().equals("1") && wo.getTeken().equals(" < ")) 
		{		
//System.out.println("try2 k");

			if (Math.abs(woLast.getBoxMid().x - wo.getBoxMid().x) < averageHeight / 2 && 
				Math.abs(woLast.getBoxMid().y - wo.getBoxMid().y) < averageHeight / 2)
					return new WriteObject("k", mergePoints(woLast.getPoints(),wo.getPoints()));		
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
	private ArrayList<Point> mergePoints(ArrayList<Point> p1, ArrayList<Point> p2, boolean reverseFirst) 
	{	if (!reverseFirst)
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
		paint();
	}

	//OK
	public void back() 
	{	if (writeObjects.size() == 0)
			return;
		
		writeObjects.remove(writeObjects.size() - 1);
		if (writeObjects.size() > 0)
			lastObject = writeObjects.get(writeObjects.size()-1);
		else
			lastObject = null;	
		paint();
	}
	
	//OK
	class MouseHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler 
	{
		
		boolean mouseOn = false;
		
		public void onMouseDown(MouseDownEvent e) {
			e.preventDefault();
			e.stopPropagation();
			mouseOn = true;
			Point p = new Point(e.getX(), e.getY());
			points.add(p);
		}
		
		public void onMouseMove(MouseMoveEvent e) {
			e.preventDefault();
			e.stopPropagation();
			if(mouseOn) 
			{
				Point p = new Point(e.getX(), e.getY());
				points.add(p);
				paint();
			}
		} 
		
		public void onMouseUp(MouseUpEvent e) 
		{	
			e.preventDefault();
			e.stopPropagation();
			mouseOn = false;
			Point p = new Point(e.getX(), e.getY());
			points.add(p);
			//paint();
			addWriteObject();
		}
	} 

	//OK
	class MGWTTouchHandler implements TouchStartHandler, TouchEndHandler, TouchMoveHandler
	{
		
		public void onTouchStart(TouchStartEvent e) 
		{
			e.preventDefault();
			e.stopPropagation();
			
			if (e.getTouches().length() > 0) 
			{
				Touch touch = e.getTouches().get(0);
				
				int eventX = touch.getPageX() - writePanelCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - writePanelCanvas.getAbsoluteTop();				
				
				Point p = new Point(eventX,eventY);
				points.add(p);
			}
			e.preventDefault();
			e.stopPropagation();
		}
		
		public void onTouchMove(TouchMoveEvent e) 
		{
			e.preventDefault();
			e.stopPropagation();
			
			if (e.getTouches().length() > 0) 
			{
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
		
		public void onTouchEnd(TouchEndEvent e) 
		{
			e.stopPropagation();
			if (e.getTouches().length() > 0) 
			{
				Touch touch = e.getTouches().get(0);
				int eventX = touch.getPageX() - writePanelCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - writePanelCanvas.getAbsoluteTop();
				Point p = new Point(eventX,eventY);
				points.add(p);
			}
			//paint();
			addWriteObject();
			
		}
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
    														lastObject.getTeken3(),lastObject.getTeken4());
    				else
    					correctiePanel = new CorrectiePanel("","","","");
    				//correctiePanel.setLocation(400, 100);
    				add(correctiePanel);
    				setWidgetLeftWidth(correctiePanel, width - 30, Style.Unit.PX, 30, Style.Unit.PX);
    				setWidgetTopHeight(correctiePanel, 26, Style.Unit.PX, 80, Style.Unit.PX);

    				correctiePanel.t1Button.addClickHandler(new CBL());
    				correctiePanel.t2Button.addClickHandler(new CBL());
    				correctiePanel.t3Button.addClickHandler(new CBL());
    				correctiePanel.t4Button.addClickHandler(new CBL());

    				paint();
    			}
    			else
    			{	
    				if (lastObject != null)
    					correctiePanel.zetTekens(lastObject.getTeken1(),lastObject.getTeken2(),
												 lastObject.getTeken3(),lastObject.getTeken4());
    				else
    					correctiePanel.zetTekens("","","","");
    				
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
    		//if ((correctiePanel != null) && (e.getSource() == correctiePanel.closeButton))
    		//{
    		//	correctiePanel.setVisible(false);
    		//}

    	}
    }

}


