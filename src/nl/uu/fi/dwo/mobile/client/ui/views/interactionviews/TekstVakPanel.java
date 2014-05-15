package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.hamcrest.core.Is;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditorTouchHandler;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleRegel;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.interaction.client.touch.TouchPanel;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.FormuleKeyboard;
import nl.uu.fi.dwo.mobile.client.ui.views.AnchorView;
import nl.uu.fi.dwo.mobile.client.ui.views.ImageView;
import nl.uu.fi.dwo.mobile.utils.PopupFacade;
import nl.uu.fi.dwo.mobile.utils.TekstBuffer;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
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
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.expressies.Aftrekking;
import fi.wiskopdr.expressies.BasisExpressie;
import fi.wiskopdr.expressies.Expressie;
import fi.wiskopdr.expressies.Optelling;
import fi.wiskopdr.expressies.Vergelijking;
import fi.wiskopdr.expressies.VergelijkingMeerv;


public class TekstVakPanel implements InteractionView
{
	class TekstVakContext {

		private int rij,kolom;
		public TekstVakContext(int rij, int kolom) {
			this.rij = rij;
			this.kolom = kolom;
		}

		public void doLayout(TekstVakPanel tekstVakPanel, int i) {
			TekstVakPanel.this.doLayout(tekstVakPanel, i, rij, kolom);
		}
		
	}
	
	
	private int font_size = 12;
	private int font_style = 0;
	private FormuleKeyboard kb = null;
	private OpdrNavIF comRoot = null;
	private int breedte = 600;
	private int hoogte = 250;
	//ObjectMap launchState;
	private Map<String, Object> instellingen;
	private LayoutPanel mainPanel2 = null;
	private Grid mainPanel = null;
	private LayoutPanel randPanel = null;
	//private LayoutPanel[][] tekstVakken = null;
	private TekstVak[][] tekstVakken = null;
	//private FlowPanel[][] tekstVakken = null;
	String[] randomVarNamen = null;
	HashMap<String, Object> randomVarWaarden = null;
	
	private TekstVak parent = null;
	
	ArrayList<Object> interactionViewObjects = new ArrayList<Object>();

	List<Double> breedtes = null;
	List<Double> hoogtes = null;
	List<Double> minHoogtes = null;
	List<Double> uitklapHoogtes = null;
	int cellSpaceColumn = 0;
	int cellSpaceRow = 0;
	int cellMarge = 0;
	int bovenMarge = 0;
	CssColor bgColor = CssColor.make(255, 255, 255);
	CssColor fgColor = CssColor.make(0, 0, 0);
	CssColor randColor = CssColor.make(150, 150, 150);
	CssColor selectionColor = CssColor.make(255, 128, 0);
	CssColor grijs = CssColor.make(128, 128, 128);
	int randDikte = 0;
	private boolean popup;
	//private boolean tableBorders;
	private LayoutPanel[] horizontalBorders;
	private LayoutPanel[] verticalBorders;
	//private Canvas tabelRandenCanvas;
	private boolean centerV = false;
	
	private boolean sleepdoel = false;
	private boolean sleepHandle = false;
	private int sleepdoelMarge = 10;
	private boolean sleepSnap = false;
	private boolean pasAanH = false;
	
	private boolean selectable;
	private boolean sleepbaar;
	private boolean selected;
	private String checkExpressieString = "$f1@";
	private boolean defaultBijNull;
	private int ipId = 0;
	private boolean colorSelection;
	private boolean zwevend;
	
	private Point[] doelPosities;
	private TekstVakPanel[] sleepObjecten;

	private boolean relocate = false;
	private int startSleepX;
	private int startSleepY;
	
	private int locationX, locationY;
	private int startX, startY;

	private boolean inklapbaar, ingeklapt;
	private int inklapKnopPos;
	private String knopImageString1, knopImageString2;
	private ToggleButton klapUitButton;
	private TekstVakContext container;
	
	static CssColor getColor(ObjectMap map, String key, int r, int g, int b) {
		ObjectMap colorMap = map != null  ? map.getObjectMap(key) : null ;
		if(colorMap != null) {
			r = colorMap.getInt("red");
					//((Number)colorMap.get("red")).intValue();
			g = colorMap.getInt("green");
					//((Number)colorMap.get("green")).intValue();
			b = colorMap.getInt("blue");
					//((Number)colorMap.get("blue")).intValue();
		}
		return CssColor.make(r, g, b);
	}
	
	
	
	
	public TekstVakPanel(HashMap<String, Object> hh, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		this.randomVarNamen = randomVarNamen;
		this.randomVarWaarden = randomVarWaarden;
		facade = new PopupFacade(hh);
		ObjectMap h = JSONUtilities.wrapMap(hh);
		ObjectMap launchState = null;
		if (h != null && h.containsKey("breedte") )
			breedte = h.getInt("breedte");
		if (h != null && h.containsKey("hoogte"))
			hoogte = h.getInt("hoogte");
		if (h != null && h.containsKey("interactiePanelLaunchState"))
			launchState =  h.getObjectMap("interactiePanelLaunchState");

		if(launchState == null)
		{
			launchState = JSONUtilities.wrapMap(Collections.<String, Object> emptyMap());
		}
		//System.out.println("launchState: " + launchState);
		boolean bgColorZichtbaar = false;
		boolean randZichtbaar = false;
		boolean tableBorders = false;
		//boolean centerV = false;
		boolean centerH = false;

		int bgColor_red = 255;
		int bgColor_green = 255;
		int bgColor_blue = 255;
		int fgColor_red = 0;
		int fgColor_green = 0;
		int fgColor_blue = 0;
		int randColor_red = 0;
		int randColor_green = 0;
		int randColor_blue = 0;

		int ronding = 0;

		if (launchState.containsKey("breedtes") )
		{
			breedtes = launchState.getDoubleList("breedtes");
			if(!breedtes.isEmpty() && breedtes.get(0).intValue() > breedte)
				breedte = breedtes.get(0).intValue();
		}
		else
			breedtes = (Arrays.asList(600.0));
		if (launchState.containsKey("hoogtes") )
		{
			hoogtes = launchState.getDoubleList("hoogtes");
			if(!hoogtes.isEmpty() && hoogtes.get(0).intValue() > hoogte)
				hoogte = hoogtes.get(0).intValue();
		}
		else
			hoogtes = (Arrays.asList(250.0));
		minHoogtes = new ArrayList<Double>(hoogtes);
		if (launchState.containsKey("cellSpaceColumn") )
			cellSpaceColumn = launchState.getInt("cellSpaceColumn");
		if (launchState.containsKey("cellSpaceRow") )
			cellSpaceRow = launchState.getInt("cellSpaceRow");
		if (launchState.containsKey("cellMarge"))
			cellMarge = launchState.getInt("cellMarge");
		
		if (launchState.containsKey("bovenMarge") )
			bovenMarge = launchState.getInt("bovenMarge");
		if (launchState.containsKey("ronding"))
			ronding = launchState.getInt("ronding");
		if (launchState.containsKey("bgColorZichtbaar") )
			bgColorZichtbaar = launchState.getBoolean("bgColorZichtbaar");
		if (launchState.containsKey("bgColor_red") )
			bgColor_red = launchState.getInt("bgColor_red");
		if (launchState.containsKey("bgColor_green"))
			bgColor_green = launchState.getInt("bgColor_green");
		if (launchState.containsKey("bgColor_blue"))
			bgColor_blue = launchState.getInt("bgColor_blue");
		if (launchState.containsKey("fgColor_red"))
			fgColor_red = launchState.getInt("fgColor_red");
		if (launchState.containsKey("fgColor_green"))
			fgColor_green = launchState.getInt("fgColor_green");
		if (launchState.containsKey("fgColor_blue"))
			fgColor_blue = launchState.getInt("fgColor_blue");
		if (launchState.containsKey("randZichtbaar"))
			randZichtbaar = launchState.getBoolean("randZichtbaar");
		if (launchState.containsKey("randColor_red"))
			randColor_red = launchState.getInt("randColor_red");
		if (launchState.containsKey("randColor_green"))
			randColor_green = launchState.getInt("randColor_green");
		if (launchState.containsKey("randColor_blue"))
			randColor_blue = launchState.getInt("randColor_blue");
		if (launchState.containsKey("randDikte"))
			randDikte = launchState.getInt("randDikte");
		if (launchState.containsKey("font_size"))
			font_size = launchState.getInt("font_size");
		if (launchState.containsKey("font_style"))
			font_style = launchState.getInt("font_style");
		
		if(launchState.containsKey("font")) {
			ObjectMap m = launchState.getObjectMap("font");
			font_size = m.getInt("size");
			font_style = m.getInt("style");
		}
		
		if (launchState.containsKey("selectable"))
			selectable = launchState.getBoolean("selectable"); 
		if (launchState.containsKey("sleepbaar"))
			sleepbaar = launchState.getBoolean("sleepbaar");
		if (launchState.containsKey("sleepdoel"))
			sleepdoel = launchState.getBoolean("sleepdoel");
		if (launchState.containsKey("sleepHandle"))
			sleepHandle = launchState.getBoolean("sleepHandle");
		if (launchState.containsKey("checkExpressieString"))
			checkExpressieString = launchState.getString("checkExpressieString");
		if (launchState.containsKey("defaultBijNull"))
			defaultBijNull = launchState.getBoolean("defaultBijNull");
		if (launchState.containsKey("ipId"))
			ipId = launchState.getInt("ipId");
		if (launchState.containsKey("colorSelection"))
			colorSelection = launchState.getBoolean("colorSelection");
		if (launchState.containsKey("tableBorders"))
			tableBorders = launchState.getBoolean("tableBorders");
		if (launchState.containsKey("centerV"))
			centerV = launchState.getBoolean("centerV");
		if (launchState.containsKey("centerH"))
			centerH = launchState.getBoolean("centerH");
		if (launchState.containsKey("zwevend"))
			zwevend = launchState.getBoolean("zwevend");
		if (launchState.containsKey("locationX"))
			locationX = launchState.getInt("locationX");
		if (launchState.containsKey("locationY"))
			locationY = launchState.getInt("locationY");
// klap schaats
		if( launchState.containsKey("inklapbaar"))
			inklapbaar = launchState.getBoolean("inklapbaar");
		if( launchState.containsKey("inklapKnopPos"))
			inklapKnopPos = launchState.getInt("inklapKnopPos");
		else inklapKnopPos = RIGHT;
		if( launchState.containsKey("ingeklapt"))
			ingeklapt = launchState.getBoolean("ingeklapt");
		if( launchState.containsKey("uitklapHoogtes"))
			uitklapHoogtes = launchState.getDoubleList("uitklapHoogtes");
		if( launchState.containsKey("knopImageString1"))
			knopImageString1 = launchState.getString("knopImageString1");
		if( launchState.containsKey("knopImageString2"))
			knopImageString2 = launchState.getString("knopImageString2");
// launchState never null!
		if( launchState.containsKey("pasAanH"))
			pasAanH = launchState.getBoolean("pasAanH");

// FIXME overleg met Peter		
//		if(ingeklapt) for(int i = 0; i < hoogtes.size(); i++) {
//			ingeklapt = false;
//			if(hoogtes.get(i).intValue() < 0)
//			{
//				hoogtes.set(i, new Double(100) ); // How to calculate this?
//				hoogte += 100;
//			}
//		}
		
		bgColor = getColor(launchState, "bgColor", bgColor_red, bgColor_green, bgColor_blue);
		fgColor = getColor(launchState, "fgColor",fgColor_red, fgColor_green, fgColor_blue);
		randColor = getColor(launchState, "randColor",randColor_red, randColor_green, randColor_blue);
		randDikte = randZichtbaar ? randDikte : 0; 

		mainPanel2 = new LayoutPanel();
		
		setCurrentSize(breedte, hoogte);
		
		MouseHandler mouseHandler = new MouseHandler();
		mainPanel2.addDomHandler(mouseHandler, MouseDownEvent.getType());
		mainPanel2.addDomHandler(mouseHandler, MouseMoveEvent.getType());
		mainPanel2.addDomHandler(mouseHandler, MouseUpEvent.getType());
		TouchHandler touchHandler = new TouchHandler();
		mainPanel2.addDomHandler(touchHandler, TouchStartEvent.getType());
		mainPanel2.addDomHandler(touchHandler, TouchMoveEvent.getType());
		mainPanel2.addDomHandler(touchHandler, TouchEndEvent.getType());
		
		
		randPanel = new LayoutPanel();
		if(bgColorZichtbaar)
			randPanel.getElement().getStyle().setBackgroundColor(bgColor.toString());
		randPanel.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		randPanel.getElement().getStyle().setBorderColor(randColor.toString());
		randPanel.getElement().getStyle().setBorderWidth(randDikte, Unit.PX);
		randPanel.getElement().getStyle().setProperty("borderRadius", (ronding / 2) + "px");
		
		//tabelranden
		double hoogteCum = -0.5 - cellSpaceRow / 2;
		double breedteCum = -0.5 - cellSpaceColumn / 2;
		//if ("GR".equals(WiskOpdr.deployVariant))
		//	g.setColor(new Color(70, 116, 183));
		horizontalBorders = new LayoutPanel[hoogtes.size() - 1];
		verticalBorders = new LayoutPanel[breedtes.size() - 1];
		for (int i = 0; i < hoogtes.size() - 1; i++)
		{	horizontalBorders[i] = new LayoutPanel();
			horizontalBorders[i].setPixelSize(breedte, 1);
			horizontalBorders[i].getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
			horizontalBorders[i].getElement().getStyle().setBorderColor(randColor.toString());
			hoogteCum += ((Number) hoogtes.get(i)).intValue() + cellSpaceRow;
			randPanel.add(horizontalBorders[i]);
			randPanel.setWidgetLeftRight(horizontalBorders[i], 0, Style.Unit.PX, 0, Style.Unit.PX);
			randPanel.setWidgetTopHeight(horizontalBorders[i], Math.round(hoogteCum), Style.Unit.PX, 1, Style.Unit.PX);
			if(!tableBorders)
				horizontalBorders[i].setVisible(false);
			
		}
		for (int i = 0; i < breedtes.size() - 1; i++)
		{
			verticalBorders[i] = new LayoutPanel();
			verticalBorders[i].setPixelSize(1 , hoogte);
			verticalBorders[i].getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
			verticalBorders[i].getElement().getStyle().setBorderColor(randColor.toString());
			breedteCum += breedtes.get(i).intValue() + cellSpaceColumn;
			randPanel.add(verticalBorders[i]);
			randPanel.setWidgetLeftWidth(verticalBorders[i], Math.round(breedteCum), Style.Unit.PX, 1, Style.Unit.PX);
			randPanel.setWidgetTopBottom(verticalBorders[i], 0, Style.Unit.PX, 0, Style.Unit.PX);
			if(!tableBorders)
				verticalBorders[i].setVisible(false);
			
		}
		
		mainPanel = new Grid(hoogtes.size(), breedtes.size());
		mainPanel.getElement().getStyle().setProperty("borderSpacing", "" + cellSpaceColumn + "px " + cellSpaceRow + "px");
		mainPanel.getElement().getStyle().setProperty("margin", "" + (-cellSpaceRow) + "px " + (-cellSpaceColumn) + "px");
		
		tekstVakken = new TekstVak[hoogtes.size()][breedtes.size()];	
		for (int i = 0; i < hoogtes.size(); i++)
		{
			for (int j = 0; j < breedtes.size(); j++)
			{	double tekstVakBreedte =  breedtes.get(j).doubleValue() - 2 * cellMarge;
				double tekstVakHoogte = hoogtes.get(i).doubleValue() - 2 * bovenMarge;
				
				
				if( tekstVakBreedte < 0) tekstVakBreedte = 0;
				if( tekstVakHoogte < 0) tekstVakHoogte = 0;
				
				tekstVakken[i][j] = new TekstVak(this, i, j);
				tekstVakken[i][j].setSize(breedtes.get(j).intValue(), hoogtes.get(i).intValue());
				//tekstVakken[i][j].setPixelSize(breedtes.get(j).intValue(), hoogtes.get(i).intValue());
				tekstVakken[i][j].setColor(fgColor);
				tekstVakken[i][j].setFontSize(font_size);
				tekstVakken[i][j].setFontStyle(font_style);
				tekstVakken[i][j].setRonding(ronding);
				tekstVakken[i][j].setCentering(centerH, centerV);
				tekstVakken[i][j].setTekstVakBreedte(tekstVakBreedte);
				tekstVakken[i][j].setMarges(bovenMarge, cellMarge);
				
				mainPanel.setWidget(i, j, tekstVakken[i][j]);
				
			}
		}
		mainPanel2.add(randPanel);
		mainPanel2.setWidgetLeftRight(randPanel, 0, Style.Unit.PX, 0, Style.Unit.PX);
		mainPanel2.setWidgetTopBottom(randPanel, 0, Style.Unit.PX, 0, Style.Unit.PX);
		
		
		mainPanel2.add(mainPanel);
		mainPanel2.setWidgetLeftRight(mainPanel, 0, Style.Unit.PX, 0, Style.Unit.PX);
		mainPanel2.setWidgetTopBottom(mainPanel, 0, Style.Unit.PX, 0, Style.Unit.PX);
		
		if(sleepbaar && sleepHandle)
		{	Image ic = new Image(DWOplayer.DWO_BUNDLE.crosshair());
			mainPanel2.add(ic);
			mainPanel2.setWidgetLeftWidth(ic, 0, Style.Unit.PX, 20, Style.Unit.PX);
			mainPanel2.setWidgetTopHeight(ic, 0, Style.Unit.PX, 20, Style.Unit.PX);
		}

//		if(inklapbaar)
//		{	initieerKlapUitButton(ingeklapt);
//		}

	}
	
	public void setTableBounds()
	{
		int b = breedte;
		int h = hoogte;
		for (int i = 0; i < hoogtes.size(); i++)
		{
			for (int j = 0; j < breedtes.size(); j++)
			{
				tekstVakken[i][j].setSize(breedtes.get(j).intValue(), hoogtes.get(i).intValue());
				
			}
		}
	}

	public void zetInstellingen(Map<String, Object> instellingen)
	{
		this.instellingen = instellingen;
		//if(instellingen.get("fontSize") != null)
		//	font_size = ((Number) instellingen.get("fontSize")).intValue();
	}

	public void setKeyboard(FormuleKeyboard kb)
	{
		this.kb = kb;
	}
	
	public FormuleKeyboard getKeyboard()
	{
		return kb;
	}

	public void setContainer(TekstVakContext container) {
		this.container = container;
	}
	
	private int width, height;
	
	public int getCurrentWidth() {
		return width;
	}
	
	public int getCurrentHeight() {
		return height;
	}
	
	public void setCurrentSize(int w, int h) {
		System.out.println(this + " size " + w + "x" + h);
		int oldHeight = height;
		mainPanel2.setPixelSize(w, h);
		if(w >= 0) width = w;
		if(h >= 0) height = h;
		if(container != null)
			container.doLayout(this, height - oldHeight);
	}
	
	public void doLayout(TekstVakPanel child, int delta, int rij, int kolom) {
		System.out.println("child dolayout " + child + " pos " + rij + " " + kolom + " + delta " + delta);
		int cch = child.getCurrentHeight();
		int tekstGrootte = child.font_size;
		
		
		com.google.gwt.user.client.Element element = child.getAsPanel().getElement();
		element.getStyle().setProperty("verticalAlign", (tekstGrootte - cch + 1) + "px");
		if(pasAanH)
		{
			int cellHoogte = hoogtes.get(rij).intValue();
			int c0 = Math.max(cellHoogte, minHoogtes.get(rij).intValue());
			cellHoogte += delta;
			hoogtes.set(rij, Double.valueOf(cellHoogte));
			cellHoogte = Math.max(cellHoogte, minHoogtes.get(rij).intValue());
			for(int j = 0; j < breedtes.size(); j ++)
			{
				tekstVakken[rij][j].setPixelSize(-1, (int) cellHoogte);
			}
			delta = cellHoogte - c0;
			System.out.println("new size = " + width + "x" + "(" + height + "+ " + delta + ")");
			//mainPanel2.setPixelSize(width, height += delta);
						setCurrentSize(-1, height + delta);
		}
		
	}
	
	public void zetOpdracht(HashMap<String, Object> interactiePanelLaunchState)
	{
		String randVarString = "";
		ArrayList<Object> opdrachtObjects = new ArrayList<Object>();
		List<Object> opdrachtGegevens = JSONUtilities.toArrayList( interactiePanelLaunchState.get("interactiePanelLaunchData") );

		TekstBuffer tb = new TekstBuffer(randomVarNamen, randomVarWaarden);
		int aantalVakken = 0;
		for (int i = 0; i < hoogtes.size(); i++)
		{
			for (int j = 0; j < breedtes.size(); j++)
			{
				opdrachtObjects = tb.convertTekst(interactiePanelLaunchState, i, j);
				tekstVakken[i][j].zetOpdrachtObjects(opdrachtObjects);
				
				//eerst zorgen dat alle opdrachtObjects goed geïnitialiseerd zijn, daarna zet je ze netjes in het tekstvak neer.
				//voor goede initialisatie van sleepopdr etc is het wel nodig dat de opdrachtobjects al aan het tekstvak zijn toegevoegd.
				for (int k = 0; k < opdrachtObjects.size(); k++)
				{
					Object currentObject = opdrachtObjects.get(k);
					if (currentObject instanceof InteractionView)
					{ 	((InteractionView) currentObject).setCommunicationRoot(comRoot);
						interactionViewObjects.add(currentObject);
						
						if(currentObject instanceof CheckValueUnit)
						{
							//bepalen waar tekstvakken gezocht moeten worden.
							ArrayList<Object> lijst = new ArrayList<Object>();
							if(k == 0 && i == 0 && j==0 && zwevend)
							{
								lijst = parent.getOpdrachtObjects();
							}
							else if (k == 0)
								lijst = interactionViewObjects;
							else
								lijst = opdrachtObjects;
							
							int aantalValueObjects = ((CheckValueUnit) currentObject).getAantalValueObjects();
							TekstVakPanel[] waardeObjecten = new TekstVakPanel[aantalValueObjects];
							
							for(int l = 0; l < aantalValueObjects; l++)
							{	waardeObjecten[l] = zoekTekstVakPanel(l+1, lijst);
							
							}
							((CheckValueUnit) currentObject).zetWaardeObjecten(waardeObjecten);
						}
						else if(currentObject instanceof CheckSelectieUnit)
						{
							//bepalen waar tekstvakken gezocht moeten worden.
							ArrayList<Object> lijst = new ArrayList<Object>();
							if(k == 0 && i == 0 && j==0)
							{
								lijst = parent.getOpdrachtObjects();
								
							}
							else if (k == 0)
								lijst = interactionViewObjects;
							else
								lijst = opdrachtObjects;
							
							
							int aantalSelectieObjecten = ((CheckSelectieUnit) currentObject).getAantalSelectieObjecten();
							TekstVakPanel[] selectieObjecten = new TekstVakPanel[aantalSelectieObjecten];
							for(int l = 0; l < aantalSelectieObjecten; l++)
								selectieObjecten[l] = zoekTekstVakPanel(l+1, lijst);
							
							((CheckSelectieUnit) currentObject).zetSelectieObjecten(selectieObjecten);
						}
						else if(currentObject instanceof CheckSleepUnit)
						{	//bepalen waar tekstvakken gezocht moeten worden.
							ArrayList<Object> lijst = new ArrayList<Object>();
							if(k == 0 && i == 0 && j==0)
							{
								lijst = parent.getOpdrachtObjects();
								
							}
							else if (k == 0)
								lijst = interactionViewObjects;
							else
								lijst = opdrachtObjects;
							
							
							int aantalSleepObjects = ((CheckSleepUnit) currentObject).getAantalSleepObjects();
							System.out.println("checkSleepUnit: aantalSleepObjects = " + aantalSleepObjects);
						
							TekstVakPanel[] sleepObjecten = new TekstVakPanel[aantalSleepObjects];
							for(int l = 0; l < aantalSleepObjects; l++)
								sleepObjecten[l] = zoekTekstVakPanel(l+1, lijst);
							
							int aantalDoelObjects = ((CheckSleepUnit) currentObject).getAantalDoelObjects();
							TekstVakPanel[] doelObjecten = new TekstVakPanel[aantalDoelObjects];
							for(int l = 0; l < aantalDoelObjects; l++)
								doelObjecten[l] = zoekTekstVakPanel(-(l+1), lijst);
							((CheckSleepUnit) currentObject).zetSleepDoelObjecten(sleepObjecten, doelObjecten);
						}
						else if(currentObject instanceof CheckButton)
						{
							ArrayList<Object> lijst = new ArrayList<Object>();
							if(k == 0 && i == 0 && j==0)
							{
								lijst = parent.getOpdrachtObjects();
								
							}
							else if (k == 0)
								lijst = interactionViewObjects;
							else
								lijst = opdrachtObjects;
							((CheckButton) currentObject).zetNakijkObjecten(lijst);
						}
					}

					if (currentObject instanceof TekstVakPanel)
					{
						Object launchData = opdrachtGegevens.get(aantalVakken);
						aantalVakken++;
						HashMap<String, Object> launchState = (HashMap<String, Object>) ((HashMap<String, Object>) launchData).get("interactiePanelLaunchState");
						TekstVakPanel tekstVakChild = (TekstVakPanel) currentObject;
						if(tekstVakChild.isZwevend())
							tekstVakChild.setParent(tekstVakken[i][j]);
						tekstVakChild.zetInstellingen(instellingen);
						tekstVakChild.setKeyboard(kb);
						tekstVakChild.zetOpdracht(launchState);
						tekstVakChild.setContainer(new TekstVakContext(i,j));
						
					}
					else if (currentObject instanceof FormuleEditorWithAnswer)
					{
						aantalVakken++;
						((FormuleEditorWithAnswer) currentObject).zetInstellingen(instellingen);
					}
					else if (currentObject instanceof FormuleEditorWithSteps)
					{
						aantalVakken++;
						((FormuleEditorWithSteps) currentObject).zetInstellingen(instellingen);
					}
					else if (currentObject.getClass().getName().equals("fi.nabouwenaanzichtengwt.client.NabouwenAanzichtenGWT"))
					{
						aantalVakken++;
					}
					else if (currentObject.getClass().getName().equals("fi.kladjegwt.client.KladjeGWT"))
					{
						aantalVakken++;
					}
					else if (currentObject instanceof StubView)
					{
						aantalVakken++;
					}
				}
				//setObjects(opdrachtObjects, i, j);
				tekstVakken[i][j].setObjects(opdrachtObjects);
			}
			//Nu de hele regel gevuld is kunnen de ashoogtes gelijk worden gesteld, zodat de hele rij netjes is uitgelijnd.
			int asHoogte = 0;
			for(int j = 0; j < breedtes.size(); j++)
			{
				if(tekstVakken[i][j].getAsHoogte() > asHoogte)
				{
					
					asHoogte = tekstVakken[i][j].getAsHoogte();
					
				}
				
			}
			for(int j = 0; j < breedtes.size(); j++)
			{	tekstVakken[i][j].setAshoogte(asHoogte);
				//tekstVakken[i][j].setRegelHoogte(regelHoogte);
			}
		}

		if(inklapbaar)
		{	initieerKlapUitButton(ingeklapt);
		}

	}

	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		this.comRoot = comRoot;
	}

	public HashMap<String, Object> getState()
	{
		HashMap<String, Object> h = new HashMap<String, Object>();
		ArrayList<Object> states = new ArrayList<Object>();
		for (int i = 0; i < interactionViewObjects.size(); i++)
		{
			Object currentObject = interactionViewObjects.get(i);
			states.add(((InteractionView) currentObject).getState());
		}
		h.put("interactiePanelStates", states);
		h.put("selected", new Boolean(selected));
		if(zwevend)
		{	h.put("locationX", new Integer(locationX));
			h.put("locationY", new Integer(locationY));
		}
		return h;
	}

	public void setState(HashMap<String, Object> h)
	{
		List<Object> states = JSONUtilities.toArrayList(h.get("interactiePanelStates"));
		for (int i = 0; i < interactionViewObjects.size(); i++)
		{
			Object currentObject = interactionViewObjects.get(i);
			HashMap<String, Object> state = (HashMap<String, Object>) states.get(i);
			((InteractionView) currentObject).setState(state);
		}
		if(h.containsKey("selected"))
			selected = ((Boolean) h.get("selected")).booleanValue();
		if(h.containsKey("locationX"))
			locationX = ((Integer) h.get("locationX")).intValue();
		if(h.containsKey("locationY"))
			locationY = ((Integer) h.get("locationY")).intValue();
		
		if(parent != null)
		{	parent.setWidgetLeftWidth(this.asWidget(), locationX, Style.Unit.PX, breedte, Style.Unit.PX);
			parent.setWidgetTopHeight(this.asWidget(), locationY, Style.Unit.PX, hoogte, Style.Unit.PX);
		}
		setSelected(selected);
	}

	public int getScore()
	{
		int score = 0;
		for (int i = 0; i < interactionViewObjects.size(); i++)
		{
			Object currentObject = interactionViewObjects.get(i);
			score += ((InteractionView) currentObject).getScore();
		}
		return score;
	}

	public boolean isCorrect()
	{
		boolean correct = true;
		for (int i = 0; i < interactionViewObjects.size(); i++)
		{
			Object currentObject = interactionViewObjects.get(i);
			correct = correct && ((InteractionView) currentObject).isCorrect();
		}
		return correct;
	}

	
	public void setParent(TekstVak panel)
	{
		parent = panel;
	}
	
	public Panel getPanelElement(final FormuleHolder editor)
	{
		FlowPanel fp = new FlowPanel();
		editor.paint();

		final Panel p = editor.getAsPanel();
		if (p instanceof TouchPanel)
		{
			TouchPanel tp = (TouchPanel) p;
		}

		fp.add(p);
		return p;
	}

	public Panel getAsPanel()
	{
		return mainPanel2;
	}

	public void addFormulePanelListeners(final TouchPanel tp, final FormuleHolder editor)
	{
		tp.addTouchHandler(new FormuleEditorTouchHandler(editor));
	}

	private PopupFacade facade;
	@Override
	public Widget asWidget()
	{
		return facade.wrap(getAsPanel());
	}
	
	public boolean isZwevend()
	{
		return zwevend;
	}
	
	public Point geefLocatie()
	{
		return new Point(locationX, locationY);
	}
	
	
	
	public void zetLocatie(double x, double y) 
	{
		locationX = (int) x;
		locationY = (int) y;
		
	}
	
	public void setStartSleep(int x, int y)
	{
		startSleepX = x;
		startSleepY = y;
	}

	public void setStartSleep()
	{
		startSleepX = locationX;
		startSleepY = locationY;
	}

	public Point getStartSleep()
	{
		return new Point(startSleepX, startSleepY);
	}

	public void zetSleepDoelPosities(Point[] doelPosities)
	{
		this.doelPosities = doelPosities;
	}

	public void zetSleepObjecten(TekstVakPanel[] sleepObjecten)
	{
		this.sleepObjecten = sleepObjecten;
	}

	public Point[] geefSleepDoelPosities()
	{
		return doelPosities;
	}

	public void zetSleepSnap(boolean sleepSnap)
	{
		this.sleepSnap = sleepSnap;
	}

	public boolean geefSleepSnap()
	{
		return sleepSnap;
	}

	public void zetSleepdoelMarge(int sleepdoelMarge)
	{
		this.sleepdoelMarge = sleepdoelMarge;
	}

	public int geefSleepdoelMarge()
	{
		return sleepdoelMarge;
	}
	
	public void setRelocate(boolean relocate)
	{
		this.relocate = relocate;
	}
	
	public void setSelected(boolean b)
	{ 
		selected = b;
		if (selected)
		{
			if (colorSelection)
			{	
				randPanel.getElement().getStyle().setBorderColor(selectionColor.toString());
				randPanel.getElement().getStyle().setOpacity(0.4); 
				randPanel.getElement().getStyle().setBorderWidth(400, Unit.PX);
			}
			else
			{	randPanel.getElement().getStyle().setBorderColor(grijs.toString());
				randPanel.getElement().getStyle().setBorderWidth(5, Unit.PX);
			}
		
			
		
		}
		else
		{
			randPanel.getElement().getStyle().setBorderColor(randColor.toString());
			randPanel.getElement().getStyle().setOpacity(1);
			randPanel.getElement().getStyle().setBorderWidth(randDikte, Unit.PX);
			

		}
		
		
	}
	
	
	public Vector geefInteractiePanels()
	{
		Vector v = new Vector();
		for (int i = 0; i < hoogtes.size(); i++)
		{
			for (int j = 0; j < breedtes.size(); j++)
			{
				//for(int k = 0; k < tekstVakken[i][j].getFlowPanel().getWidgetCount(); k++)
				//	if(tekstVakken[i][j].getFlowPanel().getWidget(k) != null)
				//		v.add(tekstVakken[i][j].getFlowPanel().getWidget(k));
				for(int k = 0; k < tekstVakken[i][j].getWidgetCount(); k++)
					if(tekstVakken[i][j].getWidget(k) != null)
						v.add(tekstVakken[i][j].getWidget(k));
				
				
			}
		}
		return v;
	}
	
	public void zetGoedFout(boolean b)
	{
		if (b)
		{
			randPanel.getElement().getStyle().setBorderColor(CssColor.make(50, 225, 50).toString());
			randPanel.getElement().getStyle().setBorderWidth(5, Unit.PX);
		}
		else
		{	randPanel.getElement().getStyle().setBorderColor(CssColor.make(225, 50, 50).toString());
			randPanel.getElement().getStyle().setBorderWidth(5, Unit.PX);
		}
		
	}

	public void wisGoedFout()
	{
		randPanel.getElement().getStyle().setBorderColor(randColor.toString());
		randPanel.getElement().getStyle().setOpacity(1);
		randPanel.getElement().getStyle().setBorderWidth(randDikte, Unit.PX);
	}

	public void zetGoedFoutSleep(boolean b)
	{
		if (sleepObjecten != null && sleepdoel)
		{
			for (int i = 0; i < sleepObjecten.length; i++)
			{
				int dx = (int) Math.abs(geefLocatie().getX() - sleepObjecten[i].geefLocatie().getX());
				int dy = (int) Math.abs(geefLocatie().getY() - sleepObjecten[i].geefLocatie().getY());
				int marge = sleepObjecten[i].geefSleepdoelMarge();
				if (dx < Math.min(1, marge) && dy < Math.min(1, marge))
				{
					sleepObjecten[i].zetGoedFout(b);
					break;
				}
			}
		}
	}
	
	public int getIpId()
	{
		return ipId;
	}
	
	public int getLocationX()
	{
		return locationX;
	}
	
	public int getLocationY()
	{
		return locationY;
	}
	
	public int getFontSize()
	{
		return font_size;
	}
	
	public int getHoogte()
	{
		return hoogte;
	}
	
	public int getBreedte()
	{
		return breedte;
	}
	
	public String getIpExpString()
	{
		for(int i = 0; i < interactionViewObjects.size(); i++)
		{	Object object = interactionViewObjects.get(i);
			if(object instanceof FormuleEditorWithAnswer)
			{	FormuleRegel fr = ((FormuleEditorWithAnswer) object).getCurrentRegel();
				if (fr != null)
				{
					String string = fr.toString();
					return "$f" + string + "@";
				}
				
			}
		}
			//Als er een formulevak inzit: expressie uit formulevak geven
			//Als er een vergelijkingsvak inzit: 
			/*
			String string = null;
			FormuleVak fv = ((SimpelAntwoordVergelijkingVak) ip).geefFormuleVak();
			if (fv != null)
			{
				string = fv.toString();
				VergelijkingMeerv vgm = FormuleParser.parseVergelijking(string);
				Vergelijking vg = null;
				if (vgm != null)
					vg = vgm.geefVergelijking(0);
				Expressie e = null;
				if (vg != null)
					e = new Aftrekking(vg.geefExpLinks(), vg.geefExpRechts());
				if (e != null)
					return "$f" + e.toString() + "@";
			}
			*/
			
			
			
		
		return checkExpressieString;
	}

	public boolean isIpSelected()
	{
		return selected;
	}
	
	public boolean bevatSleepObject()
	{
		boolean bevat = false;
		if (sleepObjecten != null && sleepdoel)
		{	for (int i = 0; i < sleepObjecten.length; i++)
			{
				int dx = (int) Math.abs(geefLocatie().getX() - sleepObjecten[i].geefLocatie().getX());
				int dy = (int) Math.abs(geefLocatie().getY() - sleepObjecten[i].geefLocatie().getY());
				int marge = sleepObjecten[i].geefSleepdoelMarge();
				if (dx < Math.min(1, marge) && dy < Math.min(1, marge))
				{
					bevat = true;
					break;
				}
			}
		}
		return bevat;
	}

	public Expressie geefSleepObjectWaarde()
	{
		Expressie waarde = null;
		if (sleepObjecten != null && sleepdoel)
		{
			for (int i = 0; i < sleepObjecten.length; i++)
			{
				int dx = (int) Math.abs(geefLocatie().getX() - sleepObjecten[i].geefLocatie().getX());
				int dy = (int) Math.abs(geefLocatie().getY() - sleepObjecten[i].geefLocatie().getY());
				int marge = sleepObjecten[i].geefSleepdoelMarge();
				if (dx < Math.min(1, marge) && dy < Math.min(1, marge))
				{
					waarde = FormuleParser.geefExpressie(sleepObjecten[i].getIpExpString());
					break;
				}
			}
		}
		return waarde;
	}

	public Expressie geefSleepObjectVerzamelWaarde()
	{
		Expressie waarde = new BasisExpressie(0);
		if (sleepObjecten != null && sleepdoel)
		{
			for (int i = 0; i < sleepObjecten.length; i++)
			{
				int x = (int) (sleepObjecten[i].geefLocatie().getX() - geefLocatie().getX());
				int y = (int) (sleepObjecten[i].geefLocatie().getY() - geefLocatie().getY());
				int b = sleepObjecten[i].breedte;
				int h = sleepObjecten[i].hoogte;
				boolean binnen = x >= 0 && y >= 0 && x + b <= breedte && y + h <= hoogte;
						
				if (binnen)
				{
					waarde = new Optelling(waarde, FormuleParser.geefExpressie(sleepObjecten[i].getIpExpString()));
				}
			}
		}
		return waarde;
	}

	public boolean ipObjectIsCorrect()
	{
		boolean juist = false;
		
		for(int i = 0; i < interactionViewObjects.size(); i++)
		{	Object object = interactionViewObjects.get(i);
			if(object instanceof FormuleEditorWithAnswer)
			{	FormuleEditorWithAnswer object2 = (FormuleEditorWithAnswer) object;
				object2.kijkNa();
				juist = object2.isCorrect();
			}
		}
		
		return juist;
	}

	
	public Expressie geefObjectWaarde()
	{
		Expressie waarde = FormuleParser.geefExpressie(getIpExpString());
		if("$f@".equals(getIpExpString()) && defaultBijNull)
			waarde = FormuleParser.geefExpressie(checkExpressieString);
		return waarde;
	}
	
	public TekstVakPanel zoekTekstVakPanel(int id, ArrayList<Object> lijst)
	{
		//deze methode moet op die hieronder gaan lijken (en dan moet die hieronder weer weg)
		//interactionViewObjects.size() == 1: De klaar-knop is het enige element in zijn cel.
		//nee, klopt niet; de interactionViewObjects zouden alles moeten zijn in het gehele tekstvak?
		//maar dan zou het al moeten werken als de klaar-knop in een andere cel staat... (als die cel maar later komt in de telling..)
	//	if(interactionViewObjects.size() == 1 && zwevend)
	//	{
	//		TekstVakPanel panel =
	//	}
		
		
		for(int i = 0; i < lijst.size(); i++)
		{
			if(lijst.get(i) instanceof TekstVakPanel)
			{	TekstVakPanel panel = (TekstVakPanel) lijst.get(i);
				if(id == panel.ipId)
					return panel;
				else if(panel.ipId == 0)
				{
					TekstVakPanel panel2 = panel.zoekTekstVakPanel(id, panel.interactionViewObjects);
					if(panel2 != null)
						return panel2;
				}
			}
		}
		return null;
		
		
		
		/*
		for(int i = 0; i < interactionViewObjects.size(); i++)
		{
			if(interactionViewObjects.get(i) instanceof TekstVakPanel)
			{	TekstVakPanel panel = (TekstVakPanel) interactionViewObjects.get(i);
				if(id == panel.ipId)
					return panel;
			}
		}
		return null;
		*/
	}
	
	public boolean objectNullWaarde()
	{
		return "".equals(getIpExpString()) || "$f@".equals(getIpExpString());
	}

	
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{
		if(selectable)
		{
			selected = !selected;
			setSelected(selected);
			return;
		}
		
		if(!sleepbaar)
		{
			return;
		}
		
		startX = eventX - locationX;
		startY = eventY - locationY;
	}
	
	public void mouseMoveTouchMoveAction(int eventX, int eventY)
	{
		locationX = eventX - startX;
		locationY = eventY - startY;
		
		if(parent != null)
		{	locationX = Math.max(locationX, 0);
			locationX = Math.min(locationX, parent.getOffsetWidth() - breedte);
			locationY = Math.max(locationY, 0);
			locationY = Math.min(locationY, parent.getOffsetHeight() - hoogte);
	
			parent.remove(this.asWidget());
			parent.add(this.asWidget());
			parent.setWidgetLeftWidth(this.asWidget(), locationX, Style.Unit.PX, breedte, Style.Unit.PX);
			parent.setWidgetTopHeight(this.asWidget(), locationY, Style.Unit.PX, hoogte, Style.Unit.PX);
		}
		
		
	}
	
	public void mouseUpTouchEndAction(int eventX, int eventY)
	{	if(!sleepbaar)
		{	//hier zorgen dat de pagina wordt versleept?
			return;
		}
		locationX = eventX - startX;
		locationY = eventY - startY;
		if(parent != null)
		{	locationX = Math.max(locationX, 0);
			locationX = Math.min(locationX, parent.getOffsetWidth() - breedte);
			locationY = Math.max(locationY, 0);
			locationY = Math.min(locationY, parent.getOffsetHeight() - hoogte);

			if(sleepSnap) 
			{
				if(doelPosities != null) 
				{	boolean snapped = false;
					for(int i=0 ; i<doelPosities.length ; i++)
					{	int dx = (int) Math.abs(locationX - doelPosities[i].getX());
						int dy = (int) Math.abs(locationY - doelPosities[i].getY());
						if(sleepSnap && dx < sleepdoelMarge && dy < sleepdoelMarge) 
						{	locationX = (int) doelPosities[i].getX();
							locationY = (int) doelPosities[i].getY();
							snapped = true;
							break;
						}
					}
					if(!snapped && relocate)
					{	locationX = startSleepX;
						locationY = startSleepY;
					}
				}
			}
			parent.remove(this.asWidget());
			parent.add(this.asWidget());
			parent.setWidgetLeftWidth(this.asWidget(), locationX, Style.Unit.PX, breedte, Style.Unit.PX);
			parent.setWidgetTopHeight(this.asWidget(), locationY, Style.Unit.PX, hoogte, Style.Unit.PX);
		}
	}
	
	class MouseHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler
	{   
		boolean mouseDown = false;
		
		public void onMouseDown(MouseDownEvent e)
		{
			if(sleepbaar && sleepHandle && (e.getX() > 20 || e.getY() > 20) )
				return;
			e.stopPropagation();
			
			int eventX = e.getClientX();
			int eventY = e.getClientY();
			
			mouseDown = true;
			
			mouseDownTouchStartAction(eventX, eventY);
			
		}
		
		public void onMouseMove(MouseMoveEvent e)	
		{
			
			// prevent scrolling
			if(sleepbaar && sleepHandle && (e.getX() > 20 || e.getY() > 20) )
			{	mouseDown = false;
				return;
			}
			
			e.stopPropagation(); 
			
			if (!mouseDown)
				return;

			int eventX = e.getClientX();
			int eventY = e.getClientY();
			
			if(sleepbaar)
				mouseMoveTouchMoveAction(eventX, eventY);
			
		} // onMouseMove
		
		public void onMouseUp(MouseUpEvent e)	
		{	
			// prevent scrolling
			if(sleepbaar && sleepHandle && (e.getX() > 20 || e.getY() > 20) )
			{	mouseDown = false;
				return;
			}
			
			e.stopPropagation();
			
			mouseDown = false;

			int eventX = e.getClientX();
			int eventY = e.getClientY();
			
			mouseUpTouchEndAction(eventX,eventY);

		}

	} //MouseHandler
	
	class TouchHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{
		
		public void onTouchStart(TouchStartEvent e)
		{
			e.stopPropagation();
			
			if(e.getTouches().length() == 0)
			{	return;
			}
			
			Touch touch = e.getTouches().get(0);
			
			if(sleepbaar && sleepHandle && (touch.getPageX() - getAsPanel().getAbsoluteLeft() > 20 || 
					touch.getPageY() - getAsPanel().getAbsoluteTop() > 20))
			{	e.preventDefault();
				return;
			}
			
			
			int eventX = touch.getClientX();
			int eventY = touch.getClientY();
			mouseDownTouchStartAction(eventX, eventY);
			
			
		}
		public void onTouchMove(TouchMoveEvent e)
		{
			
			e.stopPropagation();
			
			if(e.getTouches().length() == 0)
				return;
			
			Touch touch = e.getTouches().get(0);
			
			if(sleepbaar && sleepHandle && (touch.getPageX() - getAsPanel().getAbsoluteLeft() > 20 || 
					touch.getPageY() - getAsPanel().getAbsoluteTop() > 20))
			{	e.preventDefault();
				return;
			}
		
			int eventX = touch.getClientX();
			int eventY = touch.getClientY();
			
			if(sleepbaar)
			{	e.preventDefault();
				mouseMoveTouchMoveAction(eventX, eventY);
			}
				
		}
		public void onTouchEnd(TouchEndEvent e)
		{
			e.stopPropagation();
			if(sleepbaar || selectable)
				e.preventDefault();
			
			if(sleepbaar && sleepHandle)
			{	e.preventDefault();
				return;
			}
			int eventX = locationX + startX;
			int eventY = locationY + startY;
			mouseUpTouchEndAction(eventX, eventY);
			
			  
		}

	}
	
	void klapUitAction() {
		System.out.println("klapUitAction");
		int delta = hoogte-hoogtes.get(0).intValue();
		System.out.println("delta = " + delta);
		if( ingeklapt = ! ingeklapt) {
			System.out.println("inklappen!");
			GWT.log("inklappen!");
			for(int i = 1; i < tekstVakken.length; i++)
			{
				for(int j = 0; j < tekstVakken[i].length; j ++)
				{
					tekstVakken[i][j].setVisible(false);
					//tekstVakken[i][j].setPixelSize(-1, 0);
				}
			}
			setCurrentSize(breedte,  hoogtes.get(0).intValue() );
		} 
		else {
			double hoogte = hoogtes.get(0);
			
			for(int i = 1; i < tekstVakken.length; i++)
			{
				int h = hoogtes.get(i).intValue();
				if(h <= 0 ) {
					if (uitklapHoogtes != null && uitklapHoogtes.size() > i) h = uitklapHoogtes.get(i).intValue();
					else h = 100;
					hoogtes.set(i, Double.valueOf(h));
				}
				for(int j = 0; j < tekstVakken[i].length; j ++)
				{
					tekstVakken[i][j].setVisible(true);
					tekstVakken[i][j].setPixelSize(-1, h);
					//tekstVakken[i][j].setPixelSize(breedtes.get(j).intValue(), h);
				}
				hoogte += hoogtes.get(i);
			}
			GWT.log("uitklappen " + hoogte);
			this.hoogte = (int)hoogte;
			
			setCurrentSize( breedte, this.hoogte);
		}
	}
	
	private static final int LEFT = 0;
	private static final int RIGHT = 1;
	private static final int MIDDLE = 2;
	
	private void initieerKlapUitButton (boolean ingeklapt)
	{
		Image view1, view2;
		final int inklapKnopPos = this.inklapKnopPos;
		if(knopImageString1 != null && !knopImageString1.isEmpty()) {
			view1 = new ImageView(knopImageString1).getImage();
		} else
			view1 = new Image(DWOplayer.DWO_BUNDLE.klapuit1());		
		if(knopImageString2 != null && !knopImageString2.isEmpty()) {
			view2 = new ImageView(knopImageString2).getImage();
		} else {
			view2 = new Image(DWOplayer.DWO_BUNDLE.klapuit2());
		}
		final Image masterView = ingeklapt ? view2 : view1;
		masterView.getElement().getStyle().setProperty("verticalAlign", hoogtes.get(0).intValue() + "px");
		klapUitButton = new ToggleButton(view2, view1);
		klapUitButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				klapUitAction();				
			}});
		
		klapUitButton.setStylePrimaryName("inklapButton");
		//klapUitButton.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE)
		//masterView.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
		klapUitButton.setDown(ingeklapt);
		int pos;
// Links of rechts kunnen we aan!
		switch(inklapKnopPos) {
		case LEFT: pos = 0; break;
		default:
		case MIDDLE:
		case RIGHT: pos = pos = breedtes.size()-1;
		}
		
		final TekstVak layoutPanel = tekstVakken[0][pos];
		
		//final Widget widget = layoutPanel.getWidget(0);
		
// Wat met de positie van widgets
		layoutPanel.insert(klapUitButton,0);
		LoadHandler handler = new LoadHandler() {

			@Override
			public void onLoad(LoadEvent event) {
				int width = masterView.getWidth();
				int height = masterView.getHeight();
				klapUitButton.setPixelSize(width, hoogtes.get(0).intValue());
				
				setPositionUitklapButton(layoutPanel, inklapKnopPos, width, height);
				
			}
		};
		if (masterView.getWidth() > 0) handler.onLoad(null);
		else masterView.addLoadHandler(handler);
	}


	public void setPositionUitklapButton(TekstVak layoutPanel, int inklapKnopPos, int width, int height)
	{
		switch(inklapKnopPos) {
		case LEFT:
				//layoutPanel.setWidgetLeftRight(widget, width, Unit.PX, 0, Unit.PX);
				layoutPanel.setWidgetLeftWidth(klapUitButton, 0, Unit.PX, width, Unit.PX);
				layoutPanel.setWidgetTopHeight(klapUitButton, (hoogtes.get(0).intValue()-height)/2, Unit.PX, height, Unit.PX);
				break;
		case MIDDLE: // FIXME werkt nog van geen meter!
			layoutPanel.setWidgetLeftWidth(klapUitButton, layoutPanel.getRegelBreedte(), Unit.PX, width, Unit.PX);
			layoutPanel.setWidgetTopHeight(klapUitButton, (hoogtes.get(0).intValue()-height)/2, Unit.PX, height, Unit.PX);
			
			break;
		case RIGHT:
		default:
			//layoutPanel.setWidgetLeftRight(widget, 0, Unit.PX, width, Unit.PX);
			layoutPanel.setWidgetRightWidth(klapUitButton, 0, Unit.PX, width, Unit.PX);
			layoutPanel.setWidgetTopHeight(klapUitButton, (hoogtes.get(0).intValue()-height)/2, Unit.PX, height, Unit.PX);
		}
	}


	@Override
	public void kijkNa() {
		for (Object object : interactionViewObjects) {
			if(object instanceof InteractionView)
				((InteractionView) object).kijkNa();
		}
		
	}

	@Override
	public int getAsHoogte() {
		return tekstVakken[0][0].getAsHoogte();
	}

	@Override
	public int getHeight() {
		return hoogte;
	}

	@Override
	public int getWidth() {
		return breedte;
	}

	@Override
	public void setAsHoogte(int ashoogte) {
		for(int i = 0; i < breedtes.size(); i++)
		{
			tekstVakken[0][i].setAshoogte(ashoogte);
		}
	}
}
