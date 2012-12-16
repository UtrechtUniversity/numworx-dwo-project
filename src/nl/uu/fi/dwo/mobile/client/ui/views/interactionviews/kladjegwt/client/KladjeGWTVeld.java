package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.kladjegwt.client;

import java.io.Serializable;
import java.util.Vector;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.canvas.dom.client.CanvasPixelArray;

import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;

import com.google.gwt.user.client.ui.Panel;
/*import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;*/
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
//import com.google.gwt.dom.client.Touch;

import com.googlecode.mgwt.dom.client.event.touch.Touch;
import com.googlecode.mgwt.dom.client.event.touch.TouchMoveHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchEndHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchCancelEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchEndEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchMoveEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartEvent;
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;



public class KladjeGWTVeld 
{
	public Canvas kladjeGWTCanvas;
	public Context2d gIm;
	TouchPanel touchPanel = new TouchPanel();
	
	static double NZERO = 1e-5d;
	
	boolean lijnen = false;
	boolean ruitjes = false;
	
	int lineDistance = 20;
	int gridSize = 20;
	
	CssColor lijnenKleur = CssColor.make(190, 190, 190);
	CssColor ruitjesKleur = CssColor.make(190, 190, 190);
	
	CssColor selectieColor = CssColor.make(0, 0, 255);
		
	CssColor zwart = CssColor.make(0, 0, 0);
	CssColor drawingColor = CssColor.make(0, 0, 0);
	int bgRed = 255;
	int bgGreen = 255;
	int bgBlue = 255;
	CssColor backgroundColor = CssColor.make(bgRed, bgGreen, bgBlue);
	
//	Color[] kleuren = {Color.black, Color.lightGray, Color.red, Color.orange,
//	           Color.green, Color.cyan, Color.blue, Color.magenta};

	final int tekenen = 0;
	final int gummen = 1;
	final int lijnTekenen = 2;
	final int rechthoekTekenen = 3;
	final int cirkelTekenen = 4;
	final int tekstTekenen = 5;
	final int selecteren = 6;
	int mouseMode = tekenen;
	boolean mouseDown;
	int startX, startY;
	//boolean shiftPressed = false;
	
	Vector draggPoints = new Vector();
	Vector gumPunten = new Vector();

	Point figuurStart = null;
	Point lijnEinde = null;
	Rectangle tekenRechthoek = null;
	Rectangle tekstRechthoek = null;
	Rectangle selecteerRechthoek = null;
	Rectangle wisRechthoek = null;

	//ColorBytes[][] pixels = null;
	ImageData pixels = null;

	int breedte, hoogte;
	int gumGrootte = 7; // oneven

//Image offScreen = null;
//Graphics offGraphics = null;

	int maxHistories = 5;
	int numHistories = 0;
	Vector[] histories = new Vector[maxHistories + 1];

//Cursor selectCursor = null;
	boolean sleepSelectie = false;
	Vector sleepPoints = new Vector();
	ImageData sleepRectangleData = null;

//Cursor textCursor = null;
//JTextField tekstVeld;
//Font tekstFont;
//Font tekenTekstFont;
//FontMetrics tekstFM;
	int tekstBreedte = 50;
	int tekstHoogte;
	boolean sleepTekst;
	int tekstRand = 5;
	String tekstString = "";
	int tekstX = 0;
	int tekstY = 0;
	
	boolean noUpdate = true; 
	
	public KladjeGWTVeld(int w, int h)
	{	
		breedte = w;
		hoogte = h;
		kladjeGWTCanvas = Canvas.createIfSupported();
		kladjeGWTCanvas.setWidth(w + "px");
		kladjeGWTCanvas.setHeight(h + "px");
		kladjeGWTCanvas.setCoordinateSpaceWidth(w);
		kladjeGWTCanvas.setCoordinateSpaceHeight(h);
		
		MouseHandler mouseHandler = new MouseHandler();
		kladjeGWTCanvas.addMouseDownHandler(mouseHandler);
		kladjeGWTCanvas.addMouseMoveHandler(mouseHandler);
		kladjeGWTCanvas.addMouseUpHandler(mouseHandler);
		
		touchPanel.getElement().getStyle().setWidth(breedte, Unit.PX);
		touchPanel.getElement().getStyle().setHeight(hoogte, Unit.PX);
		touchPanel.add(kladjeGWTCanvas);
		
		
		TouchHandler touchHandler = new TouchHandler();
		touchPanel.addTouchStartHandler(touchHandler);
		touchPanel.addTouchMoveHandler(touchHandler);
		touchPanel.addTouchEndHandler(touchHandler);
		
	}
	
	public Panel getAsPanel()
	{
		return touchPanel;
	}
	
	public Canvas getCanvas()
	{
		return kladjeGWTCanvas;
	}
	
	public void initContext2d() 
	{
		gIm = kladjeGWTCanvas.getContext2d();
		
	}
	
	void addToHistory()
	{
		Vector stateVector = getState();
		
//System.out.println("add " + stateVector.size());		
		
		histories[numHistories] = stateVector;
		numHistories++;
		if (numHistories > maxHistories)
		{	for (int i = 0; i < numHistories - 1; i++)
			{	histories[i] = histories[i + 1];
			}
			numHistories--;
		}		
	}
	
	public Vector getFromHistory()
	{	if (numHistories <= 1)
			return null;
		numHistories--;
		return histories[numHistories - 1];
	}

	public Vector copyRectangle(Rectangle r)
	{
		
		Vector rVector = new Vector();
/*		
		for (int hCnt = r.x; hCnt < Math.min(breedte, r.x + r.width); hCnt++)
			for (int vCnt = r.y; vCnt < Math.min(hoogte, r.y + r.height); vCnt++)
			{
				Color c = pixels[hCnt][vCnt].makeColor();
				if (!c.equals(backgroundColor))
				{
					ColorBytes newCB = new ColorBytes(pixels[hCnt][vCnt].x, pixels[hCnt][vCnt].y, 
							                          pixels[hCnt][vCnt].red, pixels[hCnt][vCnt].green, pixels[hCnt][vCnt].blue);
					//stateVector.addElement(pixels[hCnt][vCnt]);
					rVector.addElement(newCB);
				}
			}
		
*/		
		return rVector;
	}
	
	public ImageData copyRectangleData(Rectangle r)
	{
		if ((r.width <= 0) || (r.height <= 0))
			return null;
		else
			return gIm.getImageData(r.x + 1, r.y + 1, r.width - 2, r.height - 2);
	}
	
	public void wisRectangle(Rectangle r)
	{
		
		wisRechthoek = new Rectangle(r);
		
		//gIm.setFillStyle(backgroundColor);
		//gIm.fillRect(r.x, r.y, r.width, r.height);
		
		
		
/*		
		int cnt = 0;
		for (int xCnt = r.x; xCnt < Math.min(breedte, r.x + r.width); xCnt++)
			for (int yCnt = r.y; yCnt < Math.min(hoogte, r.y + r.height); yCnt++)
			{
				pixels[xCnt][yCnt].zetColor(backgroundColor);
				cnt++;
			}
		
//System.out.println("wisrect = " + cnt);
 		
 */
	}
	
	public Vector getState()
	{
		Vector stateVector = new Vector();
		
		if (pixels == null)
			return stateVector;

/*		
//		CanvasPixelArray cpa = pixels.getData();
		
		for (int hCnt = 0; hCnt < breedte; hCnt++)
			for (int vCnt = 0; vCnt < hoogte; vCnt++)
			{
//				int red = cpa.get(vCnt * breedte * 4 + hCnt);
//				int blue = cpa.get(vCnt * breedte * 4 + hCnt + 1);
//				int green = cpa.get(vCnt * breedte * 4 + hCnt + 2);
					
				int red = pixels.getRedAt(hCnt, vCnt);
				int green = pixels.getGreenAt(hCnt, vCnt);
				int blue = pixels.getBlueAt(hCnt, vCnt);
				
				boolean isBG = (red == bgRed) && (green == bgGreen) && (blue == bgBlue);
		
				if (!isBG)
				{	ColorBytes newCB = new ColorBytes(hCnt, vCnt, red, green, blue); 
					//stateVector.addElement(newCB);
				}

			}
System.out.println("kladjeGWTVeld getState " + stateVector.size());
*/ 		

		return stateVector;
	}

	public void setState(Vector stateVector)
	{
		
//System.out.println("kladjeVeld setState " + stateVector.size());


		int cnt = 0;
		for (int pCnt = 0; pCnt < stateVector.size(); pCnt++)
		{
			ColorBytes cb = (ColorBytes) stateVector.elementAt(pCnt);
			if ((cb.x >= 0) && (cb.x < breedte) && (cb.y >= 0) && (cb.y < hoogte))
			{
				if (pixels != null)
				{
					pixels.setRedAt(cb.red, cb.x, cb.y);
					pixels.setGreenAt(cb.green, cb.x, cb.y);
					pixels.setBlueAt(cb.blue, cb.x, cb.y);
				}
				
			}
			
/*			
			if ((cb.x < breedte) && (cb.y < hoogte))
			{	pixels[cb.x][cb.y] = cb;
				Color c = pixels[cb.x][cb.y].makeColor();
				if (!c.equals(backgroundColor))
					cnt++;
			}
*/			
		}
		
//System.out.println("kladjeVeld pp " + cnt);		
		paint();
	}
	
	void undo()
	{
		wis();
		Vector lastState = getFromHistory();
		if (lastState != null)
		{	setState(lastState);
		}

		paint();
	}
	
	void wis()
	{	if ((mouseMode == selecteren) && (selecteerRechthoek != null))
		{
			wisRectangle(selecteerRechthoek);
		}
		else
		{
			pixels = null;
			
		}	
		paint();
	}
	
	public void paint()
	{
		paint(gIm, true);
	}
	
	public void paint(boolean metDecoratie)
	{
		paint(gIm, metDecoratie);
	}

	public void paint(Context2d g, boolean metDecoratie)
	{
		
		g.setLineWidth(1.0d);

/*		
		g.setFillStyle(backgroundColor);
		g.fillRect(0, 0, breedte, hoogte);
*/		
		
		g.clearRect(0, 0, breedte, hoogte);
	
		
		
//		g.setStrokeStyle(zwart);
//		g.strokeRect(0, 0, breedte, hoogte);
		
		
		if (metDecoratie && lijnen)
		{
			
			g.setStrokeStyle(lijnenKleur);
			int steps = hoogte / lineDistance;
			for (int lCnt = 1; lCnt <= steps; lCnt++)
			{
				g.beginPath();
				g.moveTo(0, lCnt * lineDistance);
				g.lineTo(breedte - 1, lCnt * lineDistance);
				g.stroke();
				
				//g.drawLine(0, lCnt * lineDistance, getSize().width - 1, lCnt * lineDistance);
			}
			
		}
		if (metDecoratie && ruitjes)
		{
			g.setStrokeStyle(ruitjesKleur);
			int vSteps = hoogte / lineDistance;
			for (int vCnt = 1; vCnt <= vSteps; vCnt++)
			{
				g.beginPath();
				g.moveTo(0, vCnt * lineDistance);
				g.lineTo(breedte - 1, vCnt * lineDistance);
				g.stroke();
				//g.drawLine(0, vCnt * lineDistance, getSize().width - 1, vCnt * lineDistance);
			}
			int hSteps = breedte / lineDistance;
			for (int hCnt = 1; hCnt <= hSteps; hCnt++)
			{
				g.beginPath();
				g.moveTo(hCnt * lineDistance, 0);
				g.lineTo(hCnt * lineDistance, hoogte - 1);
				g.stroke();
				//g.drawLine(hCnt * lineDistance, 0, hCnt * lineDistance, getSize().height - 5);
			}

//System.out.println("ruitjes");
//System.out.println("lw = " + g.getLineWidth());
//System.out.println("ss = " + g.getStrokeStyle().toString());
		}
		

// tijdelijk		
		g.setStrokeStyle(zwart);
		g.strokeRect(0, 0, breedte, hoogte);
//System.out.println("outline");		
		
		g.setLineWidth(1.5d); 
		tekenProgramma(g);

	}
	
	void tekenProgramma(Context2d g)
	{
		
		if (pixels != null)
		{
	
			g.putImageData(pixels, 0, 0);
//System.out.println("imageData");			
		}
		
		if (wisRechthoek != null)
		{
			g.setFillStyle(backgroundColor);
			g.fillRect(wisRechthoek.x, wisRechthoek.y, wisRechthoek.width, wisRechthoek.height);
			wisRechthoek = null;
			updatePixelArray();
			if (pixels != null)
			{	g.putImageData(pixels, 0, 0);
			}
		}
		g.setStrokeStyle(drawingColor);		
		
		if (draggPoints.size() == 1)
		{	Point p = (Point) draggPoints.elementAt(0);
			//g.moveTo(p.x, p.y);
			//g.lineTo(p.x, p.y);
			g.strokeRect(p.x, p.y, 1, 1);
		}
		if (draggPoints.size() > 1)
		{	Point p1 = (Point) draggPoints.elementAt(0);
			g.moveTo(p1.x, p1.y);
			g.beginPath();
			for (int pCnt = 1; pCnt < draggPoints.size(); pCnt++)
			{	Point p2 = (Point) draggPoints.elementAt(pCnt);
				g.lineTo(p2.x, p2.y);
				//g.drawLine(p1.x, p1.y, p2.x, p2.y);
				p1 = p2;
			}
			g.stroke();
			
		}

		for (int pCnt = 0; pCnt < gumPunten.size(); pCnt++)
		{
			Point p = (Point) gumPunten.elementAt(pCnt);
			g.setFillStyle(backgroundColor);
			g.fillRect(p.x - gumGrootte / 2, p.y - gumGrootte / 2, gumGrootte, gumGrootte);
			
		}
		
		g.setStrokeStyle(drawingColor);
		
		if ((mouseMode == lijnTekenen) && (figuurStart != null) && (lijnEinde != null))
		{	
			
			g.beginPath();
			g.moveTo(figuurStart.x, figuurStart.y);
			g.lineTo(lijnEinde.x, lijnEinde.y);
			g.stroke();
//System.out.println("lijn");		
//System.out.println("lw = " + g.getLineWidth());
//System.out.println("ss = " + g.getStrokeStyle().toString());

//			g.drawLine(figuurStart.x, figuurStart.y, lijnEinde.x, lijnEinde.y);
			
		}
		
		if ((mouseMode == rechthoekTekenen) && (tekenRechthoek != null))
		{
//System.out.println("mm = rh && trh not null");			
			g.strokeRect(tekenRechthoek.x, tekenRechthoek.y, tekenRechthoek.width, tekenRechthoek.height);
		}
		
		if ((mouseMode == cirkelTekenen) && (tekenRechthoek != null))
		{
//System.out.println("mm = ci && trh not null");			
			double centerX = tekenRechthoek.x + tekenRechthoek.width / 2;
			double centerY = tekenRechthoek.y + tekenRechthoek.height / 2;
			int steps = 35;
			double angleStep = 2 * Math.PI / steps;
			
			g.moveTo(centerX + tekenRechthoek.width / 2, centerY + tekenRechthoek.height / 2);
			g.beginPath();
			for (int pCnt = 0; pCnt < steps; pCnt++)
			{
				g.lineTo(centerX + (tekenRechthoek.width / 2) * Math.cos(pCnt * angleStep),
						 centerY - (tekenRechthoek.height / 2) * Math.sin(pCnt * angleStep));
			}
			g.closePath();
			g.stroke();
			
//			g.beginPath();
//			g.arc(centerX, centerY, tekenRechthoek.height / 2, 0, 2 * Math.PI);
			//g.stroke();
			
//			g.drawOval(tekenRechthoek.x, tekenRechthoek.y, tekenRechthoek.width, tekenRechthoek.height);
		}
		
		// alleen op het scherm
		if ((mouseMode == tekstTekenen) && (tekstRechthoek != null))
		{
			
//System.out.println("drawing trh at " + tekstRechthoek.x + "," + tekstRechthoek.y);
/*
			Graphics2D g2D = (Graphics2D) g;
			float[] dash = new float[2];
			dash[0] = 1;
			dash[1] = 3;
			g2D.setStroke(new BasicStroke(1.2f, 2, 0, 10.0f, dash, 0.0f));
			g.setColor(Color.black);
			g.drawRect(tekstRechthoek.x, tekstRechthoek.y, tekstRechthoek.width, tekstRechthoek.height);
			g2D.setStroke(new BasicStroke(1.5f, 2, 0, 10.0f, null, 0.0f));
*/			
		}

		// alleen op het scherm
		if ((mouseMode == selecteren) && (selecteerRechthoek != null) && !sleepSelectie)
		{
			g.setLineWidth(0.8d);
			g.setStrokeStyle(selectieColor);
			g.beginPath();
			g.strokeRect(selecteerRechthoek.x, selecteerRechthoek.y, 
					     selecteerRechthoek.width, selecteerRechthoek.height);
			
			
		}
		

		if ((mouseMode == selecteren) && (selecteerRechthoek != null) && sleepSelectie)
		{
		
			if (mouseDown)
			{	g.setLineWidth(0.8d);
				g.setStrokeStyle(selectieColor);
				g.beginPath();
				g.strokeRect(selecteerRechthoek.x, selecteerRechthoek.y, 
					     	 selecteerRechthoek.width, selecteerRechthoek.height);
			}	
			if (sleepRectangleData != null)
			{
				g.putImageData(sleepRectangleData, selecteerRechthoek.x + 1, selecteerRechthoek.y + 1);
			}

			
			
		}
	}

	
	void updatePixelArray()
	{	
		pixels = gIm.getImageData(0, 0, breedte, hoogte);
		
	}	
	
	void gumPunt(int x, int y, Context2d g)
	{
		
/*		
		String cString = backgroundColor.toString().substring(4, backgroundColor.toString().length() - 1);
		String[] kleurenStr = StringUtils.split(cString, ",");

		int bgBlue =  Integer.parseInt(kleurenStr[2]);
		int bgGreen = Integer.parseInt(kleurenStr[1]);
		int bgRed =   Integer.parseInt(kleurenStr[0]);
		
		for (int xCnt = x - gumGrootte / 2; xCnt <= x + gumGrootte / 2; xCnt++)
			for (int yCnt = y - gumGrootte / 2; yCnt <= y + gumGrootte / 2; yCnt++)
			{
				if ((xCnt >= 0) && (xCnt < pixels.getWidth()) &&
					(yCnt >= 0) && (yCnt < pixels.getHeight()))
				{	pixels.setRedAt(bgRed, xCnt, yCnt);
					pixels.setGreenAt(bgGreen, xCnt, yCnt);
					pixels.setBlueAt(bgBlue, xCnt, yCnt);
					//pixels[xCnt][yCnt].zetColor(backgroundColor);
				
				}
				
			}
		
*/		
		g.setFillStyle(backgroundColor);
		g.fillRect(x - gumGrootte / 2, y - gumGrootte / 2, gumGrootte, gumGrootte);

	}
	
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{
		if (mouseMode == tekenen)
		{
			mouseDown = true;
			draggPoints.addElement(new Point(eventX, eventY));
			paint();
		}
		else if (mouseMode == gummen)
		{
			mouseDown = true;
			//gumPunt(e.getX(), e.getY(), gIm);
			gumPunten.addElement(new Point(eventX, eventY));
			paint();
		}
		else if ((mouseMode == lijnTekenen) ||
				 (mouseMode == rechthoekTekenen) ||
				 (mouseMode == cirkelTekenen))
		{
			mouseDown = true;
			figuurStart = new Point(eventX, eventY);
			paint();
		}
		else if (mouseMode == tekstTekenen)
		{
			mouseDown = true;
/*				
			if ((tekstRechthoek != null) && tekstRechthoek.contains(e.getX(), e.getY()))
			{
				sleepTekst = true;
				startX = e.getX();
				startY = e.getY();
			}
			else
			{
				sleepTekst = false;
				//if (tekstVeld.isVisible())
				//{
					hideTekstVeld(false);
				//}
				tekstVeld.setLocation(e.getX(), e.getY());
				tekstVeld.setText("");
				tekstVeld.setVisible(true);
				tekstVeld.requestFocus();
				tekstRechthoek = new Rectangle(e.getX() - tekstRand, e.getY() - tekstRand, tekstBreedte + 2 * tekstRand - 2, 
						                                           tekstHoogte + 2 * tekstRand - 2);
			}
*/			
			paint();
			
		}
		else if (mouseMode == selecteren)
		{
			mouseDown = true;
			
			if ((selecteerRechthoek != null) && selecteerRechthoek.contains(eventX, eventY))
			{
				
				sleepSelectie = true;
				startX = eventX;
				startY = eventY;

				sleepRectangleData = copyRectangleData(selecteerRechthoek);
				wisRectangle(selecteerRechthoek);
			 
				paint();
		
			}
			else
			{
				sleepSelectie = false;
				figuurStart = new Point(eventX, eventY);
				selecteerRechthoek = null;
				paint();
			}

		}
		
	}
	
	public void mouseMoveTouchMoveAction(int eventX, int eventY, boolean shiftPressed)
	{
		if (mouseMode == tekenen)
		{
			draggPoints.addElement(new Point(eventX, eventY));
			paint();
		}
		else if (mouseMode == gummen)
		{
			//gumPunt(e.getX(), e.getY(), gIm);
			gumPunten.addElement(new Point(eventX, eventY));
			paint();
		}
		else if (mouseMode == lijnTekenen)
		{
			
			if (shiftPressed)
			{
				if ((eventX > figuurStart.x) && (eventY > figuurStart.y))
				{	
					double xZijde = (double) eventX - figuurStart.x;
					double yZijde = (double) eventY - figuurStart.y;
					int min = Math.min(eventX - figuurStart.x, eventY - figuurStart.y);
					if (yZijde > xZijde - NZERO)
					{
						if (xZijde < yZijde / 2 + NZERO)
						{
							lijnEinde = new Point(figuurStart.x, eventY);
						}
						else
						{
							lijnEinde = new Point(figuurStart.x + min, figuurStart.y + min);
						}
					}
					else
					{
						if (yZijde > xZijde / 2 - NZERO)
						{
							lijnEinde = new Point(figuurStart.x + min, figuurStart.y + min);
						}
						else
						{
							lijnEinde = new Point(eventX, figuurStart.y);
						}
					}

				}	
				else if ((eventX > figuurStart.x) && (eventY < figuurStart.y))
				{	
					double xZijde = (double) eventX - figuurStart.x;
					double yZijde = (double) figuurStart.y - eventY;
					int min = Math.min(eventX - figuurStart.x, figuurStart.y - eventY);
					if (yZijde > xZijde + NZERO)
					{
						if (xZijde < yZijde / 2 + NZERO)
						{
							lijnEinde = new Point(figuurStart.x, eventY);
						}
						else
						{
							lijnEinde = new Point(figuurStart.x + min, figuurStart.y - min);
						}
					}
					else
					{
						if (yZijde > xZijde / 2 - NZERO)
						{
							lijnEinde = new Point(figuurStart.x + min, figuurStart.y - min);
						}
						else
						{
							lijnEinde = new Point(eventX, figuurStart.y);
						}
					}



				}	
				else if ((eventX < figuurStart.x) && (eventY > figuurStart.y))
				{	
					double xZijde = (double) figuurStart.x - eventX;
					double yZijde = (double) eventY - figuurStart.y;
					int min = Math.min(figuurStart.x - eventX, eventY - figuurStart.y);
					if (yZijde > xZijde + NZERO)
					{
						if (xZijde < yZijde / 2 + NZERO)
						{
							lijnEinde = new Point(figuurStart.x, eventY);
						}
						else
						{
							lijnEinde = new Point(figuurStart.x - min, figuurStart.y + min);
						}
					}
					else
					{
						if (yZijde > xZijde / 2 - NZERO)
						{
							lijnEinde = new Point(figuurStart.x - min, figuurStart.y + min);
						}
						else
						{
							lijnEinde = new Point(eventX, figuurStart.y);
						}
					}
					
					
				}	
				else if ((eventX < figuurStart.x) && (eventY < figuurStart.y))
				{	
					int xZijde = figuurStart.x - eventX;
					int yZijde = figuurStart.y - eventY;
					int min = Math.min(figuurStart.x - eventX, figuurStart.y - eventY);
					if (yZijde > xZijde + NZERO)
					{
						if (xZijde < yZijde / 2 + NZERO)
						{
							lijnEinde = new Point(figuurStart.x, eventY);
						}
						else
						{
							lijnEinde = new Point(figuurStart.x - min, figuurStart.y - min);
						}
					}
					else
					{
						if (yZijde > xZijde / 2 - NZERO)
						{
							lijnEinde = new Point(figuurStart.x - min, figuurStart.y - min);
						}
						else
						{
							lijnEinde = new Point(eventX, figuurStart.y);
						}
					}
					        
				}
				
			}
			else
			{	
				lijnEinde = new Point(eventX, eventY);
			}
			
			paint();
			
		} // lijnTekenen
		else if ((mouseMode == rechthoekTekenen) || (mouseMode == cirkelTekenen))
		{
			
			if (figuurStart != null)
			{
				
				if (shiftPressed)
				{
//System.out.println("ShiftDown");
					if ((eventX > figuurStart.x) && (eventY > figuurStart.y))
					{	
						int zijde = Math.min(eventX - figuurStart.x, eventY - figuurStart.y);
						tekenRechthoek = new Rectangle(figuurStart.x, figuurStart.y, zijde, zijde); 

					}	
					else if ((eventX > figuurStart.x) && (eventY < figuurStart.y))
					{	
						int zijde = Math.min(eventX - figuurStart.x, figuurStart.y - eventY);
						tekenRechthoek = new Rectangle(figuurStart.x, eventY, zijde, zijde); 

					}	
					else if ((eventX < figuurStart.x) && (eventY > figuurStart.y))
					{	
						int zijde = Math.min(figuurStart.x - eventX, eventY - figuurStart.y);
						tekenRechthoek = new Rectangle(eventX, figuurStart.y, zijde, zijde);
						
					}	
					else if ((eventX < figuurStart.x) && (eventY < figuurStart.y))
					{	
						int zijde = Math.min(figuurStart.x - eventX, figuurStart.y - eventY);
						tekenRechthoek = new Rectangle(eventX, eventY, zijde, zijde); 
						        
					}
				}
				else
				{	
					if ((eventX > figuurStart.x) && (eventY > figuurStart.y))
					{	tekenRechthoek = new Rectangle(figuurStart.x, figuurStart.y, 
						                           	   eventX - figuurStart.x, eventY - figuurStart.y); 
					}	
					else if ((eventX > figuurStart.x) && (eventY < figuurStart.y))
					{	tekenRechthoek = new Rectangle(figuurStart.x, eventY, 
							                           eventX - figuurStart.x, figuurStart.y - eventY); 
					}	
					else if ((eventX < figuurStart.x) && (eventY > figuurStart.y))
					{	tekenRechthoek = new Rectangle(eventX, figuurStart.y, 
												       figuurStart.x - eventX, eventY - figuurStart.y); 
					}	
					else if ((eventX < figuurStart.x) && (eventY < figuurStart.y))
					{	tekenRechthoek = new Rectangle(eventX, eventY, 
												       figuurStart.x - eventX, figuurStart.y - eventY); 
					}
				}
				
				paint();
			}	
			
		} //rechthoek && cirkel
		else if (mouseMode == tekstTekenen)
		{
/*				

			if (sleepTekst) // verplaats de tekstRechthoek en het tekstVeld!!
			{	
			
				int dx = e.getX() - startX;
				int dy = e.getY() - startY;
				tekstRechthoek.translate(dx, dy);
				tekstVeld.setLocation(tekstVeld.getLocation().x + dx, tekstVeld.getLocation().y + dy);
				
				startX = e.getX();
				startY = e.getY();
			
			}
*/				
			paint();
		}
		else if (mouseMode == selecteren)
		{
		
			if (sleepSelectie) // verplaats de selecteerRechthoek met inhoud!!
			{
				
				int dx = eventX - startX;
				int dy = eventY - startY;
				
				selecteerRechthoek.translate(dx, dy);

				startX = eventX;
				startY = eventY;
				paint();
				
				
			}
			else // sleepSelectie, vorm de selecteerRechthoek
			{	
				if ((eventX > figuurStart.x) && (eventY > figuurStart.y))
				{	selecteerRechthoek = new Rectangle(figuurStart.x, figuurStart.y, 
						eventX - figuurStart.x, eventY - figuurStart.y); 
				}	
				else if ((eventX > figuurStart.x) && (eventY < figuurStart.y))
				{	selecteerRechthoek = new Rectangle(figuurStart.x, eventY, 
						eventX - figuurStart.x, figuurStart.y - eventY); 
				}	
				else if ((eventX < figuurStart.x) && (eventY > figuurStart.y))
				{	selecteerRechthoek = new Rectangle(eventX, figuurStart.y, 
				       figuurStart.x - eventX, eventY - figuurStart.y); 
				}	
				else if ((eventX < figuurStart.x) && (eventY < figuurStart.y))
				{	selecteerRechthoek = new Rectangle(eventX, eventY, 
				       figuurStart.x - eventX, figuurStart.y - eventY); 
				}
				paint();
			}
			
		}
		
		
	}
	
	public void mouseUpTouchEndAction()
	{
		if (mouseMode == tekenen)
		{	
			updatePixelArray();
			draggPoints.removeAllElements();
			addToHistory();
		}	
		else if (mouseMode == gummen)
		{
			updatePixelArray();
			gumPunten.removeAllElements();
			addToHistory();

		}
		else if ((mouseMode == lijnTekenen) ||
				 (mouseMode == rechthoekTekenen) ||
				 (mouseMode == cirkelTekenen))
		{
			updatePixelArray();
			figuurStart = null;
			lijnEinde = null;
			tekenRechthoek = null;
			addToHistory();
			
		}
		else if (mouseMode == tekstTekenen)
		{
			
		}
		else if (mouseMode == selecteren)
		{
			mouseDown = false;

			if (sleepSelectie)
			{	
				paint();
				updatePixelArray();
				
/*					
				for (int pCnt = 0; pCnt < sleepPoints.size(); pCnt++)
				{	ColorBytes cb = (ColorBytes) sleepPoints.elementAt(pCnt);
					if ((cb.x < breedte) && (cb.y < hoogte))
						pixels[cb.x][cb.y] = cb;
				}
*/					
//				sleepPoints.removeAllElements();
				
				addToHistory();
				sleepSelectie = false;
				paint();
			}
			
		}
		
	}
	
	//class MLMML extends MouseAdapter implements MouseMotionListener
	class MouseHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler
	{
		
		//public void mousePressed(MouseEvent e)
		public void onMouseDown(MouseDownEvent e)
		{
			int eventX = e.getX();
			int eventY = e.getY();
			
			mouseDownTouchStartAction(eventX, eventY);
			
/*			
			if (mouseMode == tekenen)
			{
				mouseDown = true;
				draggPoints.addElement(new Point(e.getX(), e.getY()));
				paint();
			}
			else if (mouseMode == gummen)
			{
				mouseDown = true;
				//gumPunt(e.getX(), e.getY(), gIm);
				gumPunten.addElement(new Point(e.getX(), e.getY()));
				paint();
			}
			else if ((mouseMode == lijnTekenen) ||
					 (mouseMode == rechthoekTekenen) ||
					 (mouseMode == cirkelTekenen))
			{
				mouseDown = true;
				figuurStart = new Point(e.getX(), e.getY());
				paint();
			}
			else if (mouseMode == tekstTekenen)
			{
				mouseDown = true;
				
				paint();
				
			}
			else if (mouseMode == selecteren)
			{
				mouseDown = true;
				
				if ((selecteerRechthoek != null) && selecteerRechthoek.contains(e.getX(), e.getY()))
				{
					
					sleepSelectie = true;
					startX = e.getX();
					startY = e.getY();

					sleepRectangleData = copyRectangleData(selecteerRechthoek);
					wisRectangle(selecteerRechthoek);
				 
					paint();
			
				}
				else
				{
					sleepSelectie = false;
					figuurStart = new Point(e.getX(), e.getY());
					selecteerRechthoek = null;
					paint();
				}

			}
*/			
		}
		
		//public void mouseDragged(MouseEvent e)
		public void onMouseMove(MouseMoveEvent e)	
		{
			
			if (!mouseDown)
				return;

			int eventX = e.getX();
			int eventY = e.getY();
			boolean shiftPressed = e.isShiftKeyDown();
			
			mouseMoveTouchMoveAction(eventX, eventY, shiftPressed);
			
/*			
			if (mouseMode == tekenen)
			{
				draggPoints.addElement(new Point(e.getX(), e.getY()));
				paint();
			}
			else if (mouseMode == gummen)
			{
				//gumPunt(e.getX(), e.getY(), gIm);
				gumPunten.addElement(new Point(e.getX(), e.getY()));
				paint();
			}
			else if (mouseMode == lijnTekenen)
			{
				
				if (e.isShiftKeyDown())
				{
					if ((e.getX() > figuurStart.x) && (e.getY() > figuurStart.y))
					{	
						double xZijde = (double) e.getX() - figuurStart.x;
						double yZijde = (double) e.getY() - figuurStart.y;
						int min = Math.min(e.getX() - figuurStart.x, e.getY() - figuurStart.y);
						if (yZijde > xZijde - NZERO)
						{
							if (xZijde < yZijde / 2 + NZERO)
							{
								lijnEinde = new Point(figuurStart.x, e.getY());
							}
							else
							{
								lijnEinde = new Point(figuurStart.x + min, figuurStart.y + min);
							}
						}
						else
						{
							if (yZijde > xZijde / 2 - NZERO)
							{
								lijnEinde = new Point(figuurStart.x + min, figuurStart.y + min);
							}
							else
							{
								lijnEinde = new Point(e.getX(), figuurStart.y);
							}
						}

					}	
					else if ((e.getX() > figuurStart.x) && (e.getY() < figuurStart.y))
					{	
						double xZijde = (double) e.getX() - figuurStart.x;
						double yZijde = (double) figuurStart.y - e.getY();
						int min = Math.min(e.getX() - figuurStart.x, figuurStart.y - e.getY());
						if (yZijde > xZijde + NZERO)
						{
							if (xZijde < yZijde / 2 + NZERO)
							{
								lijnEinde = new Point(figuurStart.x, e.getY());
							}
							else
							{
								lijnEinde = new Point(figuurStart.x + min, figuurStart.y - min);
							}
						}
						else
						{
							if (yZijde > xZijde / 2 - NZERO)
							{
								lijnEinde = new Point(figuurStart.x + min, figuurStart.y - min);
							}
							else
							{
								lijnEinde = new Point(e.getX(), figuurStart.y);
							}
						}

 

					}	
					else if ((e.getX() < figuurStart.x) && (e.getY() > figuurStart.y))
					{	
						double xZijde = (double) figuurStart.x - e.getX();
						double yZijde = (double) e.getY() - figuurStart.y;
						int min = Math.min(figuurStart.x - e.getX(), e.getY() - figuurStart.y);
						if (yZijde > xZijde + NZERO)
						{
							if (xZijde < yZijde / 2 + NZERO)
							{
								lijnEinde = new Point(figuurStart.x, e.getY());
							}
							else
							{
								lijnEinde = new Point(figuurStart.x - min, figuurStart.y + min);
							}
						}
						else
						{
							if (yZijde > xZijde / 2 - NZERO)
							{
								lijnEinde = new Point(figuurStart.x - min, figuurStart.y + min);
							}
							else
							{
								lijnEinde = new Point(e.getX(), figuurStart.y);
							}
						}
						
						
					}	
					else if ((e.getX() < figuurStart.x) && (e.getY() < figuurStart.y))
					{	
						int xZijde = figuurStart.x - e.getX();
						int yZijde = figuurStart.y - e.getY();
						int min = Math.min(figuurStart.x - e.getX(), figuurStart.y - e.getY());
						if (yZijde > xZijde + NZERO)
						{
							if (xZijde < yZijde / 2 + NZERO)
							{
								lijnEinde = new Point(figuurStart.x, e.getY());
							}
							else
							{
								lijnEinde = new Point(figuurStart.x - min, figuurStart.y - min);
							}
						}
						else
						{
							if (yZijde > xZijde / 2 - NZERO)
							{
								lijnEinde = new Point(figuurStart.x - min, figuurStart.y - min);
							}
							else
							{
								lijnEinde = new Point(e.getX(), figuurStart.y);
							}
						}
						        
					}
					
				}
				else
				{	
					lijnEinde = new Point(e.getX(), e.getY());
				}
				
				paint();
				
			} // lijnTekenen
			else if ((mouseMode == rechthoekTekenen) || (mouseMode == cirkelTekenen))
			{
				
				if (figuurStart != null)
				{
					
					if (e.isShiftKeyDown())
					{
//System.out.println("ShiftDown");
						if ((e.getX() > figuurStart.x) && (e.getY() > figuurStart.y))
						{	
							int zijde = Math.min(e.getX() - figuurStart.x, e.getY() - figuurStart.y);
							tekenRechthoek = new Rectangle(figuurStart.x, figuurStart.y, zijde, zijde); 

						}	
						else if ((e.getX() > figuurStart.x) && (e.getY() < figuurStart.y))
						{	
							int zijde = Math.min(e.getX() - figuurStart.x, figuurStart.y - e.getY());
							tekenRechthoek = new Rectangle(figuurStart.x, e.getY(), zijde, zijde); 
 
						}	
						else if ((e.getX() < figuurStart.x) && (e.getY() > figuurStart.y))
						{	
							int zijde = Math.min(figuurStart.x - e.getX(), e.getY() - figuurStart.y);
							tekenRechthoek = new Rectangle(e.getX(), figuurStart.y, zijde, zijde);
							
						}	
						else if ((e.getX() < figuurStart.x) && (e.getY() < figuurStart.y))
						{	
							int zijde = Math.min(figuurStart.x - e.getX(), figuurStart.y - e.getY());
							tekenRechthoek = new Rectangle(e.getX(), e.getY(), zijde, zijde); 
							        
						}
					}
					else
					{	
						if ((e.getX() > figuurStart.x) && (e.getY() > figuurStart.y))
						{	tekenRechthoek = new Rectangle(figuurStart.x, figuurStart.y, 
							                           	   e.getX() - figuurStart.x, e.getY() - figuurStart.y); 
						}	
						else if ((e.getX() > figuurStart.x) && (e.getY() < figuurStart.y))
						{	tekenRechthoek = new Rectangle(figuurStart.x, e.getY(), 
								                           e.getX() - figuurStart.x, figuurStart.y - e.getY()); 
						}	
						else if ((e.getX() < figuurStart.x) && (e.getY() > figuurStart.y))
						{	tekenRechthoek = new Rectangle(e.getX(), figuurStart.y, 
													       figuurStart.x - e.getX(), e.getY() - figuurStart.y); 
						}	
						else if ((e.getX() < figuurStart.x) && (e.getY() < figuurStart.y))
						{	tekenRechthoek = new Rectangle(e.getX(), e.getY(), 
													       figuurStart.x - e.getX(), figuurStart.y - e.getY()); 
						}
					}
					
					paint();
				}	
				
			} //rechthoek && cirkel
			else if (mouseMode == tekstTekenen)
			{

				paint();
			}
			else if (mouseMode == selecteren)
			{
			
				if (sleepSelectie) // verplaats de selecteerRechthoek met inhoud!!
				{
					
					int dx = e.getX() - startX;
					int dy = e.getY() - startY;
					
					selecteerRechthoek.translate(dx, dy);

					startX = e.getX();
					startY = e.getY();
					paint();
					
					
				}
				else // sleepSelectie, vorm de selecteerRechthoek
				{	
					if ((e.getX() > figuurStart.x) && (e.getY() > figuurStart.y))
					{	selecteerRechthoek = new Rectangle(figuurStart.x, figuurStart.y, 
							e.getX() - figuurStart.x, e.getY() - figuurStart.y); 
					}	
					else if ((e.getX() > figuurStart.x) && (e.getY() < figuurStart.y))
					{	selecteerRechthoek = new Rectangle(figuurStart.x, e.getY(), 
					        e.getX() - figuurStart.x, figuurStart.y - e.getY()); 
					}	
					else if ((e.getX() < figuurStart.x) && (e.getY() > figuurStart.y))
					{	selecteerRechthoek = new Rectangle(e.getX(), figuurStart.y, 
					       figuurStart.x - e.getX(), e.getY() - figuurStart.y); 
					}	
					else if ((e.getX() < figuurStart.x) && (e.getY() < figuurStart.y))
					{	selecteerRechthoek = new Rectangle(e.getX(), e.getY(), 
					       figuurStart.x - e.getX(), figuurStart.y - e.getY()); 
					}
					paint();
				}
				
			}
			
*/			
			
		} // onMouseMove
		
		//public void mouseReleased(MouseEvent e)
		public void onMouseUp(MouseUpEvent e)	
		{
			mouseDown = false;
		
			mouseUpTouchEndAction();

/*			
			if (mouseMode == tekenen)
			{	
				updatePixelArray();
				draggPoints.removeAllElements();
				addToHistory();
			}	
			else if (mouseMode == gummen)
			{
				updatePixelArray();
				gumPunten.removeAllElements();
				addToHistory();

			}
			else if ((mouseMode == lijnTekenen) ||
					 (mouseMode == rechthoekTekenen) ||
					 (mouseMode == cirkelTekenen))
			{
				updatePixelArray();
				figuurStart = null;
				lijnEinde = null;
				tekenRechthoek = null;
				addToHistory();
				
			}
			else if (mouseMode == tekstTekenen)
			{
				
			}
			else if (mouseMode == selecteren)
			{
				mouseDown = false;

				if (sleepSelectie)
				{	
					paint();
					updatePixelArray();
					
					
					addToHistory();
					sleepSelectie = false;
					paint();
				}
				
			}
*/
		}
/*		
		public void mouseMoved(MouseEvent e)
		{
			if (mouseMode == tekstTekenen)
			{
				if ((tekstRechthoek != null) && tekstRechthoek.contains(e.getX(), e.getY()))
				{
					setCursor(new Cursor(Cursor.MOVE_CURSOR));
				}
				else
				{
					if (textCursor != null)
						setCursor(textCursor);
					else
						setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
				
			}
			
			if (mouseMode == selecteren)
			{
				
				
				if ((selecteerRechthoek != null) && selecteerRechthoek.contains(e.getX(), e.getY()))
				{
					setCursor(new Cursor(Cursor.MOVE_CURSOR));
				}
				else
				{
					if (selectCursor != null)
						setCursor(selectCursor);
					else
						setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
				
			}
				
		}
*/
/*
		void translateColorBytes(ColorBytes cb, int dx, int dy)
		{
			cb.x += dx;
			cb.y += dy;
		}
*/		
	} //MLMML

	class TouchHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{
		
		public void onTouchStart(TouchStartEvent e)
		{
			e.preventDefault();
			e.stopPropagation();
			
			if (e.touches().length() > 0) 
			{
				Touch touch = e.touches().get(0);
				Widget sender = (Widget) e.getSource();
			    Element elem = sender.getElement();
				int eventX = touch.getPageX() - kladjeGWTCanvas.getAbsoluteLeft();//touch.getRelativeX(elem);
				int eventY = touch.getPageY() - kladjeGWTCanvas.getAbsoluteTop();//touch.getRelativeY(elem);
				
				mouseDownTouchStartAction(eventX, eventY);
				
		    }
			e.preventDefault();
			e.stopPropagation();
		}
		public void onTouchMove(TouchMoveEvent e)
		{
			e.preventDefault();
			e.stopPropagation();
			
			if (e.touches().length() > 0) 
			{
				Touch touch = e.touches().get(0);
				Widget sender = (Widget) e.getSource();
			    Element elem = sender.getElement();
				int eventX = touch.getPageX() - kladjeGWTCanvas.getAbsoluteLeft();//touch.getRelativeX(elem);
				int eventY = touch.getPageY() - kladjeGWTCanvas.getAbsoluteTop();//touch.getRelativeY(elem);
				//boolean shiftPressed = e.isShiftKeyDown();
				
				mouseMoveTouchMoveAction(eventX, eventY, false);
				
		    }
			e.preventDefault();
			e.stopPropagation();
			
		}
		public void onTouchEnd(TouchEndEvent e)
		{
			mouseUpTouchEndAction();
		}

	}

}

class ColorBytes implements Serializable
{	
	int x, y;
	byte red, green, blue;
	
	public ColorBytes(int x, int y, int r, int g, int b)
	{	
		this.x = x;
		this.y = y;
		
		red = new Integer(r).byteValue(); 
		green = new Integer(g).byteValue();
		blue = new Integer(b).byteValue();;
	}
	
	public ColorBytes(int x, int y, CssColor c)
	{
		
		this.x = x;
		this.y = y;
		
		String cString = c.toString().substring(4, c.toString().length() - 1);
		String[] kleurenStr = StringUtils.split(cString, ",");

		int intBlue =  Integer.parseInt(kleurenStr[2]);
		int intGreen = Integer.parseInt(kleurenStr[1]);
		int intRed =   Integer.parseInt(kleurenStr[0]);
		
		red = new Integer(intRed).byteValue(); 
		green = new Integer(intGreen).byteValue();
		blue = new Integer(intBlue).byteValue();;
	}

	public CssColor makeColor()
	{	int intRed = red;
		if (intRed < 0)
			intRed += 256;
		int intGreen = green;
		if (intGreen < 0)
			intGreen += 256;
		int intBlue = blue;
		if (intBlue < 0)
			intBlue += 256;
		
		return CssColor.make(intRed, intGreen, intBlue);
	}
	
	public void zetColor(CssColor c)
	{
		
		String cString = c.toString().substring(4, c.toString().length() - 1);
		String[] kleurenStr = StringUtils.split(cString, ",");

		int intBlue =  Integer.parseInt(kleurenStr[2]);
		int intGreen = Integer.parseInt(kleurenStr[1]);
		int intRed =   Integer.parseInt(kleurenStr[0]);
		
		red = new Integer(intRed).byteValue(); 
		green = new Integer(intGreen).byteValue();
		blue = new Integer(intBlue).byteValue();;
		
	}
}
class Point
{
	int x; int y;
	
	public Point(int x, int y)
	{
		this.x = x; this.y = y;
	}
}

class Rectangle
{
	int x; int y; int width; int height;
	
	public Rectangle(int x, int y, int w, int h)
	{
		this.x = x; this.y = y;
		width = w; height = h;
	}
	
	public Rectangle(Rectangle r)
	{
		x = r.x;
		y = r.y;
		width = r.width;
		height = r.height;
	}
	
	public boolean contains(int px, int py)
	{
		return (px >= x) && (px <= (x + width)) &&
		       (py >= y) && (py <= (y + height));
	}
	
	public void translate(int dx, int dy)
	{
		x += dx;
		y += dy;
	}
}