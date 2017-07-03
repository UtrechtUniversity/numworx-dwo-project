package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditorTouchHandler;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleRegel;
import nl.uu.fi.dwo.interaction.client.FacetAware;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.StateLess;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.sco.DWOLogger;
import nl.uu.fi.dwo.mobile.client.sco.ShareFacade;
import nl.uu.fi.dwo.mobile.client.ui.OpdrNav;
import nl.uu.fi.dwo.mobile.client.ui.views.AnchorView;
import nl.uu.fi.dwo.mobile.client.ui.views.AnchorView.AnchorContext;
import nl.uu.fi.dwo.mobile.client.ui.views.ImageView;
import nl.uu.fi.dwo.mobile.client.ui.views.XMLView;
import nl.uu.fi.dwo.mobile.utils.Connector;
import nl.uu.fi.dwo.mobile.utils.PopupFacade;
import nl.uu.fi.dwo.mobile.utils.PopupFacade.PopupListener;
import nl.uu.fi.dwo.mobile.utils.TekstBuffer;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;

import fi.wiskopdr.AntwoordVakChecker;
import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.expressies.Aftrekking;
import fi.wiskopdr.expressies.BasisExpressie;
import fi.wiskopdr.expressies.Expressie;
import fi.wiskopdr.expressies.Optelling;
import fi.wiskopdr.expressies.Vergelijking;
import fi.wiskopdr.expressies.VergelijkingMeerv;




public class TekstVakPanel implements InteractionViewWithMisconceptions, FacetAware, PopupListener, CBookEventListener
{
	public static final String TVP_KLAPUIT = "action.unfold";
	public static final String TVP_KLAPIN = "action.fold";
	public static final String TVP_SELECT = "action.select";
	public static final String TVP_DESELECT = "action.deselect";
	
	private static final CBookEvent KLAPUIT_EVENT = new CBookEvent(TVP_KLAPUIT); 
	private static final CBookEvent KLAPIN_EVENT = new CBookEvent(TVP_KLAPIN); 
	private static final CBookEvent SELECT_EVENT = new CBookEvent(TVP_SELECT); 
	private static final CBookEvent DESELECT_EVENT = new CBookEvent(TVP_DESELECT);
	
	public static Map<String,Map<String,Object>> styles;
	
	private boolean hoofdPanel;

	private boolean callOut = false;
	private boolean callOutDrag = false;
	int callOutMargeX0 = 15;
	int callOutMargeY0 = 15;
	int callOutMargeX1 = 5;
	int callOutMargeY1 = 5;
	int callOutPointX = 0;
	int callOutPointY = 0;
	
	/**
	 * @return the hoofdPanel
	 */
	public boolean isHoofdPanel() {
		return hoofdPanel;
	}

	/**
	 * @param hoofdPanel the hoofdPanel to set
	 */
	public void setHoofdPanel(boolean hoofdPanel) {
		this.hoofdPanel = hoofdPanel;
	}

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
	private String font_name = "Arial";
	private FormuleKeyboardIF kb = null;
	private OpdrNavIF comRoot = null;
	private int breedte = 600;
	private int hoogte = 250;
	//ObjectMap launchState;
	private ObjectMap instellingen;
	private LayoutPanel mainPanel2 = null;
	private Grid mainPanel = null;
	private LayoutPanel callOutPanel = null;
	//private LayoutPanel randPanel = null;
	//private LayoutPanel[][] tekstVakken = null;
	private TekstVak[][] tekstVakken = null;
	//private FlowPanel[][] tekstVakken = null;
	String[] randomVarNamen = null;
	HashMap<String, Number> randomVarWaarden = null;
	
	private TekstVak parent = null;
	private int mode = OpdrNav.OEFENEN;
	
	private ArrayList<Object> interactionViewObjects = new ArrayList<Object>();

	List<Double> breedtes = null;
	List<Double> hoogtes = null;
	List<Double> minHoogtes = null;
	List<Double> uitklapHoogtes = null;
	int cellSpaceColumn = 0;
	int cellSpaceRow = 0;
	int cellMarge = 0;
	int bovenMarge = 0;
	int ronding = 0;
	int hoek = 0;
	CssColor bgColor = CssColor.make(255, 255, 255);
	CssColor fgColor = CssColor.make(0, 0, 0);
	CssColor randColor = CssColor.make(150, 150, 150);
	CssColor selectieColor = CssColor.make(255, 128, 0);
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
	private Image crosshair = null;
	private int sleepdoelMarge = 10;
	private boolean sleepSnap = false;
	private boolean pasAanH = false;
	private boolean pasAanB = false;
	private boolean vulHoogte = false;
	
	private boolean selectable;
	private boolean sleepbaar;
	private boolean selected;
	private String checkExpressieString = "$f1@";
	private boolean defaultBijNull;
	private int ipId = 0;
	private int interlinie = 0;
	private boolean colorSelection;
	private boolean zwevend;
	
	private Point[] doelPosities;
	private TekstVakPanel[] sleepObjecten;

	private boolean relocate = false;
	private int startSleepX;
	private int startSleepY;
	
	private int locationX, locationY;
	private int startX, startY;
	
	String[][][] randomteksten = new String[1][][];
	ObjectList randomIpLaunchdata;
	//ObjectMap[][] randomIpLaunchdata;
	private boolean random = false;
	private int aantalRandom = 1;
	private String randomVar = "a";

	private boolean inklapbaar, ingeklapt, checkUitklapVak;
	private int inklapKnopPos;
	//private String knopImageString1, knopImageString2;
	private ImageView knopImageView1, knopImageView2;
	private Image goedKrulImage;
	private ToggleButton klapUitButton;
	private LayoutPanel klapUitPanel;
	private int klapUitPanelWidth, klapUitPanelHeight;
	private TekstVakContext container;
	private Object queuedObject;
	
	private MouseHandler mouseHandler;
	private TouchHandler touchHandler;
	
	private static boolean fontOvererving;
	private boolean anderFont = false;
	
	private boolean isLink = false;
	private ObjectList linkUrls;
	private AnchorView.AnchorContext anchorContext;
	
	
	private FlowPanel klikPanel;
	private boolean aftrekPopup;
	private boolean popupUsed;
	private int puntenAftrekPopup;
	private boolean logOption;
	
	private boolean visible = true;
	private boolean zichtbaarNaNakijken;
	private boolean nagekeken;
	private boolean bgColorZichtbaar;
	
	private String styleString;
	private boolean doorzochtDoorTab = false;
	
	
	static CssColor getColor(ObjectMap map, String key, int r, int g, int b) {
		ObjectMap colorMap = map != null && map.containsKey(key) ? map.getObjectMap(key) : null ;
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
	
	//Hiermee maak je het basispanel dat alle componenten van een pagina bevat.
	public TekstVakPanel(int breedte, int hoogte, String[] randomVarNamen, HashMap<String, Number> randomVarWaarden)
	{
		this.randomVarNamen = randomVarNamen;
		this.randomVarWaarden = randomVarWaarden;
		
		//niet nodig waarschijnlijk
		facade = new PopupFacade((ObjectMap)null);
		facade.setPopupListener(this);
		mainPanel2 = new LayoutPanel(); 
		mainPanel2.setStylePrimaryName("tekstvakpanel");
		
		setCurrentSize(breedte, hoogte);
		pasAanH = true;
		
		mainPanel = new Grid(1, 1);
		mainPanel.getElement().getStyle().setProperty("borderSpacing", cellSpaceColumn + "px " + cellSpaceRow + "px");
		mainPanel.getElement().getStyle().setProperty("margin", (-cellSpaceRow) + "px " + (-cellSpaceColumn) + "px");
		
		tekstVakken = new TekstVak[1][1];	
		tekstVakken[0][0] = new TekstVak(this, 0, 0);
		tekstVakken[0][0].setSize(breedte, hoogte);
		tekstVakken[0][0].setColor(fgColor);
		
		tekstVakken[0][0].setFontStyle(font_style);
		tekstVakken[0][0].setFontName(font_name);
		tekstVakken[0][0].setFontSize(font_size);
		//tekstVakken[i][j].setRonding(ronding);
		//tekstVakken[i][j].setCentering(centerH, centerV);
		tekstVakken[0][0].setPasHoogteBreedteAan(pasAanH, pasAanB);
		//tekstVakken[i][j].setTekstVakBreedte(tekstVakBreedte);
		//tekstVakken[i][j].setMarges(bovenMarge, cellMarge);
		//tekstVakken[i][j].setInterlinie(interlinie);
		
		mainPanel.setWidget(0, 0, tekstVakken[0][0]);
		
//		randPanel = new LayoutPanel();
//		randPanel.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
//		randPanel.getElement().getStyle().setBorderColor(randColor.toString());
//		randPanel.getElement().getStyle().setBorderWidth(randDikte, Unit.PX);
//		randPanel.getElement().getStyle().setProperty("borderRadius", (ronding / 2) + "px");
//		mainPanel2.add(randPanel);
//		mainPanel2.setWidgetLeftRight(randPanel, 0, Style.Unit.PX, 0, Style.Unit.PX);
//		mainPanel2.setWidgetTopBottom(randPanel, 0, Style.Unit.PX, 0, Style.Unit.PX);

		mainPanel2.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		mainPanel2.getElement().getStyle().setBorderColor(randColor.toString());
		mainPanel2.getElement().getStyle().setBorderWidth(randDikte, Unit.PX);
		mainPanel2.getElement().getStyle().setProperty("borderRadius", (ronding / 2) + "px");
		
		mainPanel2.add(mainPanel);
		mainPanel2.setWidgetLeftRight(mainPanel, 0, Style.Unit.PX, 0, Style.Unit.PX);
		mainPanel2.setWidgetTopBottom(mainPanel, 0, Style.Unit.PX, 0, Style.Unit.PX);
		
		
		breedtes = Arrays.asList((double) breedte);
		hoogtes = Arrays.asList((double) hoogte);
	}

	public TekstVakPanel(HashMap<String, Object> hh, String[] randomVarNamen, HashMap<String,Number> randomVarWaarden, AnchorView.AnchorContext context)
	{
		this(hh, randomVarNamen, randomVarWaarden);
		this.anchorContext = context;
	}
	
	public TekstVakPanel(HashMap<String, Object> hh, String[] randomVarNamen, HashMap<String, Number> randomVarWaarden)
	{
		this.randomVarNamen = randomVarNamen;
		this.randomVarWaarden = randomVarWaarden;
		ObjectMap h = JSONUtilities.wrapMap(hh);
		facade = new PopupFacade(h);
		facade.setPopupListener(this);
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
		bgColorZichtbaar = false;
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
		int selectieColor_red = 255;
		int selectieColor_green = 128;
		int selectieColor_blue = 0;
		//boolean anderFont = false;

		if (launchState.containsKey("breedtes") )
		{
			breedtes = launchState.getDoubleList("breedtes");
			if(!breedtes.isEmpty() && Math.round(breedtes.get(0).doubleValue()) > breedte)
				breedte = (int) Math.round(breedtes.get(0).doubleValue());
		}
		else
			breedtes = (Arrays.asList(600.0));
		if (launchState.containsKey("hoogtes") )
		{
			hoogtes = launchState.getDoubleList("hoogtes");
			if(!hoogtes.isEmpty() && Math.round(hoogtes.get(0).doubleValue()) > hoogte)
				hoogte = (int) Math.round(hoogtes.get(0).doubleValue());
		}
		else
			hoogtes = (Arrays.asList(250.0));
		
		minHoogtes = new ArrayList<Double>(hoogtes);
		
		aftrekPopup = launchState.getBoolean("aftrekPopup", false);
		if (launchState.containsKey("puntenAftrekPopup"))
			puntenAftrekPopup = launchState.getInt("puntenAftrekPopup");
		logOption = launchState.getBoolean("logOption", false);
		if(logOption) {
			dwologger = new DWOLogger();
			dwologger.setLogID(launchState.getString("logID"));
			dwologger.setMaxScore(0);
			dwologger.setLogIDLabel(launchState.getString("logIDLabel"));
			dwologger.setClassName("fi.wiskopdr.TekstVakPanel");			
		}
		
		ObjectMap style = null;
		if(launchState.containsKey("styleString")) 
			styleString = (String)launchState.getString("styleString");
		if(TekstVakPanel.styles!=null && styleString!=null)
		{	if(TekstVakPanel.styles.containsKey(styleString)) 
				style = (ObjectMap)(TekstVakPanel.styles.get(styleString));
		}	
		//System.out.println("Style: "+ (style!=null ? style.toString() : "null"));
		if(style!=null)	
		{	if(style.containsKey("randZichtbaar")) randZichtbaar = style.getBoolean("randZichtbaar");
			if(style.containsKey("bgColorZichtbaar")) bgColorZichtbaar = style.getBoolean("bgColorZichtbaar");
			if (style.containsKey("bgColor_red") ) bgColor_red = style.getInt("bgColor_red");
			if (style.containsKey("bgColor_green"))	bgColor_green = style.getInt("bgColor_green");
			if (style.containsKey("bgColor_blue")) bgColor_blue = style.getInt("bgColor_blue");
			if (style.containsKey("fgColor_red")) fgColor_red = style.getInt("fgColor_red");
			if (style.containsKey("fgColor_green")) fgColor_green = style.getInt("fgColor_green");
			if (style.containsKey("fgColor_blue")) fgColor_blue = style.getInt("fgColor_blue");
			if (style.containsKey("randColor_red")) randColor_red = style.getInt("randColor_red");
			if (style.containsKey("randColor_green")) randColor_green = style.getInt("randColor_green");
			if (style.containsKey("randColor_blue")) randColor_blue = style.getInt("randColor_blue");
			if(style.containsKey("tableBorders")) tableBorders = style.getBoolean("tableBorders");
			if(style.containsKey("cellMarge")) cellMarge = style.getInt("cellMarge");
			if(style.containsKey("bovenMarge")) bovenMarge = style.getInt("bovenMarge");
		    if(style.containsKey("ronding")) ronding = style.getInt("ronding");
		    if (style.containsKey("anderFont"))	anderFont =  style.getBoolean("anderFont");
		    if (style.containsKey("font_size")) font_size = style.getInt("font_size");
			if (style.containsKey("font_style")) font_style = style.getInt("font_style");
			if (style.containsKey("font_name")) font_name = style.getString("font_name");
			if(style.containsKey("font")) {
				ObjectMap m = style.getObjectMap("font");
				font_size = m.getInt("size");
				font_style = m.getInt("style");
				font_name = m.getString("family");
				if(font_name.equals("SansSerif"))
					font_name = "sans-serif";
				if(font_name.equals("Dialog"))
					font_name = "Arial";
			}	
		    
			if(style.containsKey("hoek")) hoek = style.getInt("hoek");
			if(style.containsKey("interlinie")) interlinie = style.getInt("interlinie");
			if(style.containsKey("cellSpaceColumn")) cellSpaceColumn =style.getInt("cellSpaceColumn");
			if(style.containsKey("cellSpaceRow")) cellSpaceRow = style.getInt("cellSpaceRow");
			if(style.containsKey("randDikte")) randDikte = style.getInt("randDikte");
			
			bgColor = getColor(style, "bgColor", bgColor_red, bgColor_green, bgColor_blue);
			if(anderFont)
			{	fgColor = getColor(style, "fgColor",fgColor_red, fgColor_green, fgColor_blue);
			
			}
			randColor = getColor(style, "randColor",randColor_red, randColor_green, randColor_blue);
			
			tableBorders = style.getBoolean("tableBorders",tableBorders);
			centerV = style.getBoolean("centerV",centerV);
			centerH = style.getBoolean("centerH",centerH);
			pasAanH = style.getBoolean("pasAanH",pasAanH);
			pasAanB = style.getBoolean("pasAanB",pasAanB);
		}
		else 
		{
			if (launchState.containsKey("cellSpaceColumn") )
				cellSpaceColumn = launchState.getInt("cellSpaceColumn");
			if (launchState.containsKey("cellSpaceRow") )
				cellSpaceRow = launchState.getInt("cellSpaceRow");
			if (launchState.containsKey("cellMarge"))
				cellMarge = launchState.getInt("cellMarge");
			if (launchState.containsKey("interlinie"))
				interlinie = launchState.getInt("interlinie");
			if (launchState.containsKey("bovenMarge") )
				bovenMarge = launchState.getInt("bovenMarge");
			if (launchState.containsKey("ronding"))
				ronding = launchState.getInt("ronding");
			if (launchState.containsKey("hoek"))
				hoek = launchState.getInt("hoek");
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
			if (launchState.containsKey("font_name"))
				font_name = launchState.getString("font_name");
			if(launchState.containsKey("font")) {
				ObjectMap m = launchState.getObjectMap("font");
				font_size = m.getInt("size");
				font_style = m.getInt("style");
				font_name = m.getString("family");
				if(font_name.equals("SansSerif"))
					font_name = "sans-serif";
				if(font_name.equals("Dialog"))
					font_name = "Arial";
			}
			if(launchState.containsKey("anderFont"))
				anderFont = launchState.getBoolean("anderFont");
			
			bgColor = getColor(launchState, "bgColor", bgColor_red, bgColor_green, bgColor_blue);
			if(anderFont)
			{	fgColor = getColor(launchState, "fgColor",fgColor_red, fgColor_green, fgColor_blue);
			
			}
			randColor = getColor(launchState, "randColor",randColor_red, randColor_green, randColor_blue);
			
			tableBorders = launchState.getBoolean("tableBorders",tableBorders);
			centerV = launchState.getBoolean("centerV",centerV);
			centerH = launchState.getBoolean("centerH",centerH);
			pasAanH = launchState.getBoolean("pasAanH",pasAanH);
			pasAanB = launchState.getBoolean("pasAanB",pasAanB);
		}
		
		selectable = launchState.getBoolean("selectable",selectable); 
		sleepbaar = launchState.getBoolean("sleepbaar", sleepbaar);
		sleepdoel = launchState.getBoolean("sleepdoel",sleepdoel);
		sleepHandle = launchState.getBoolean("sleepHandle",sleepHandle);
		if (launchState.containsKey("checkExpressieString"))
			checkExpressieString = launchState.getString("checkExpressieString");
		try
		{
			checkExpressieString = FormuleParser.randomizeString(checkExpressieString, randomVarNamen, randomVarWaarden);
		}
		catch (Exception e)
		{
			checkExpressieString = "$f???@";

		}
		visible = launchState.getBoolean("visible", true);
		if(!visible)
		{	hoogte = 0;
			breedte = 0;
		}
		zichtbaarNaNakijken = launchState.getBoolean("zichtbaarNaNakijken", false);
		
		defaultBijNull = launchState.getBoolean("defaultBijNull",defaultBijNull);
		if (launchState.containsKey("ipId"))
			ipId = launchState.getInt("ipId");
		colorSelection = launchState.getBoolean("colorSelection",colorSelection);
		if (launchState.containsKey("selectieColor_red") )
			selectieColor_red = launchState.getInt("selectieColor_red");
		if (launchState.containsKey("selectieColor_green"))
			selectieColor_green = launchState.getInt("selectieColor_green");
		if (launchState.containsKey("selectieColor_blue"))
			selectieColor_blue = launchState.getInt("selectieColor_blue");
		selectieColor = getColor(launchState, "selectieColor",selectieColor_red, selectieColor_green, selectieColor_blue);
		if (launchState.containsKey("selected"))
			selected = launchState.getBoolean("selected");
		
		zwevend = launchState.getBoolean("zwevend",zwevend);
		if (launchState.containsKey("locationX"))
			locationX = launchState.getInt("locationX");
		if (launchState.containsKey("locationY"))
			locationY = launchState.getInt("locationY");
// klap schaats
		inklapbaar = launchState.getBoolean("inklapbaar", inklapbaar);
		checkUitklapVak = launchState.getBoolean("checkUitklapVak", checkUitklapVak);
//java.util.logging.Logger.getLogger("TekstVakPanel").info("check uitklapvak = " + checkUitklapVak);
		ingeklapt = launchState.getBoolean("ingeklapt", ingeklapt);
		if( launchState.containsKey("inklapKnopPos"))
			inklapKnopPos = launchState.getInt("inklapKnopPos");
		else inklapKnopPos = RIGHT;
		if (launchState.containsKey("uitklapHoogtes"))
			uitklapHoogtes = launchState.getDoubleList("uitklapHoogtes");
		if (launchState.containsKey("knopImageString1"))
			knopImageView1 = new ImageView(launchState.getString("knopImageString1"));
		if (launchState.containsKey("knopImageString2"))
			knopImageView2 = new ImageView(launchState.getString("knopImageString2"));
// launchState never null!
		
		// call out
		if (launchState.containsKey("callOut"))
			callOut = launchState.getBoolean("callOut", callOut);
		if (launchState.containsKey("callOutMargeX0"))
			callOutMargeX0 = launchState.getInt("callOutMargeX0");
		if (launchState.containsKey("callOutMargeY0"))
			callOutMargeY0 = launchState.getInt("callOutMargeY0");
		if (launchState.containsKey("callOutMargeX1"))
			callOutMargeX1 = launchState.getInt("callOutMargeX1");
		if (launchState.containsKey("callOutMargeY1"))
			callOutMargeY1 = launchState.getInt("callOutMargeY1");
		if (launchState.containsKey("callOutPointX"))
			callOutPointX = launchState.getInt("callOutPointX");
		if (launchState.containsKey("callOutPointY"))
			callOutPointY = launchState.getInt("callOutPointY");
		
		vulHoogte = launchState.getBoolean("vulHoogte", vulHoogte);
		
// link feature of tekstVakPanel.		
		isLink = launchState.getBoolean("isLink", false);
		if( isLink && launchState.containsKey("linkUrls"))
		{
			linkUrls = launchState.getObjectList("linkUrls");
		}
		
		if(launchState.containsKey("random"))
			random = launchState.getBoolean("random");
		if(launchState.containsKey("aantalRandom"))
			aantalRandom = launchState.getInt("aantalRandom");
		if(launchState.containsKey("randomIpLaunchdata")) //FIXME grote of kleine D?
		{
			randomIpLaunchdata = launchState.getObjectList("randomIpLaunchdata");
		}
		
		//FIXME: Wim, hoe krijg ik de randomteksten (een String[][][]) en vooral de Hashtable randomIpLaunchData uit de launchState?
		
		if(launchState.containsKey("randomVar"))
			randomVar = launchState.getString("randomVar");

// FIXME overleg met Peter		
//		if(ingeklapt) for(int i = 0; i < hoogtes.size(); i++) {
//			ingeklapt = false;
//			if(hoogtes.get(i).intValue() < 0)
//			{
//				hoogtes.set(i, new Double(100) ); // How to calculate this?
//				hoogte += 100;
//			}
//		}
		
		if (callOut)
		{
			resizeForCallOut();
		}
		
		randDikte = randZichtbaar ? randDikte : 0; 

		mainPanel2 = new LayoutPanel(); 
		if(!callOut)
			mainPanel2.setStylePrimaryName("tekstvakpanel");
		
		setCurrentSize(breedte, hoogte);
		
		mouseHandler = new MouseHandler();
		mainPanel2.addDomHandler(mouseHandler, MouseDownEvent.getType());
		mainPanel2.addDomHandler(mouseHandler, MouseMoveEvent.getType());
		mainPanel2.addDomHandler(mouseHandler, MouseUpEvent.getType());
		//mainPanel2.addDomHandler(mouseHandler, MouseOutEvent.getType());
		touchHandler = new TouchHandler();
		mainPanel2.addDomHandler(touchHandler, TouchStartEvent.getType());
		mainPanel2.addDomHandler(touchHandler, TouchMoveEvent.getType());
		mainPanel2.addDomHandler(touchHandler, TouchEndEvent.getType());
		
//		randPanel = new LayoutPanel();
//		if(bgColorZichtbaar)
//			randPanel.getElement().getStyle().setBackgroundColor(bgColor.toString());
//		randPanel.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
//		randPanel.getElement().getStyle().setBorderColor(randColor.toString());
//		randPanel.getElement().getStyle().setBorderWidth(randDikte, Unit.PX);
//		randPanel.getElement().getStyle().setProperty("borderRadius", (ronding / 2) + "px");
//		
		if(bgColorZichtbaar)
			mainPanel2.getElement().getStyle().setBackgroundColor(bgColor.toString());
		mainPanel2.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		mainPanel2.getElement().getStyle().setBorderColor(randColor.toString());
		mainPanel2.getElement().getStyle().setBorderWidth(randDikte, Unit.PX);
		mainPanel2.getElement().getStyle().setProperty("borderRadius", (ronding / 2) + "px");
		
		
//		if (callOut)
//		{
//			mainPanel2.setPixelSize(breedte - callOutMargeX0 - callOutMargeX1 - randDikte, hoogte - callOutMargeY0 - callOutMargeY1 - randDikte);
//		}
		
		horizontalBorders = new LayoutPanel[hoogtes.size() - 1];
		verticalBorders = new LayoutPanel[breedtes.size() - 1];
		
		for(int i = 0; i < hoogtes.size() - 1; i++)
		{
			horizontalBorders[i] = new LayoutPanel();
			//horizontalBorders[i].setPixelSize(breedte, 1);
			horizontalBorders[i].getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
			horizontalBorders[i].getElement().getStyle().setBorderColor(randColor.toString());
			mainPanel2.add(horizontalBorders[i]);
			if(!tableBorders)
				horizontalBorders[i].setVisible(false);
		}
		for(int i = 0; i < breedtes.size() - 1; i++)
		{
			verticalBorders[i] = new LayoutPanel();
			//verticalBorders[i].setPixelSize(1 , hoogte);
			verticalBorders[i].getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
			verticalBorders[i].getElement().getStyle().setBorderColor(randColor.toString());
			mainPanel2.add(verticalBorders[i]);
			if(!tableBorders)
				verticalBorders[i].setVisible(false);
			
		}
		
		plaatsTabelRanden();
		
		if (callOut)
		{
			// hier moet als eerste een canvas met punttekening op mainPanel2 worden gezet
			callOutPanel = new LayoutPanel();
			callOutPanel.setStylePrimaryName("tekstvakpanel");
			Canvas callOutCanvas = Canvas.createIfSupported();
			setUpCallOutCanvas(callOutCanvas);
			callOutPanel.add(callOutCanvas);
			callOutPanel.setPixelSize(breedte, hoogte);
			callOutPanel.add(mainPanel2);
			
			callOutPanel.setWidgetLeftRight(mainPanel2, callOutMargeX0, Style.Unit.PX, callOutMargeX1, Style.Unit.PX);
			callOutPanel.setWidgetTopBottom(mainPanel2, callOutMargeY0, Style.Unit.PX, callOutMargeY1, Style.Unit.PX);
			
//			mainPanel2.add(callOutCanvas);
//			mainPanel2.setWidgetLeftRight(callOutCanvas, 0, Style.Unit.PX, 0, Style.Unit.PX);
//			mainPanel2.setWidgetTopBottom(callOutCanvas, 0, Style.Unit.PX, 0, Style.Unit.PX);
		}
		

		int callOutExtraIndex = 0;
//		if (callOut)
//		{
//			// for call out create an extra row and column to account for callOutMargeX0 and callOutMargeY0
//			callOutExtraIndex = 1;
//		}
		
		mainPanel = new Grid(hoogtes.size() + callOutExtraIndex, breedtes.size() + callOutExtraIndex);
		mainPanel.getElement().getStyle().setProperty("borderSpacing", "" + cellSpaceColumn + "px " + cellSpaceRow + "px");
		mainPanel.getElement().getStyle().setProperty("margin", "" + (-cellSpaceRow - randDikte) + "px " + (-cellSpaceColumn - randDikte) + "px");
		
//		if (callOut)
//		{
//			//mainPanel.setPixelSize(breedte - callOutMargeX0 - callOutMargeX1, hoogte - callOutMargeY0 - callOutMargeY1 );

			// add an extra 'fill' panel to create the callOutMargeX0 and callOutMargeY0
//			LayoutPanel callOutFillPanel = new LayoutPanel();
//			LayoutPanel callOutFillPanel2 = new LayoutPanel();
//			callOutFillPanel.setPixelSize(callOutMargeX0, callOutMargeY0);
//			callOutFillPanel2.setPixelSize(callOutMargeX0, callOutMargeY0);
//			mainPanel.setWidget(0, 0, callOutFillPanel);
//			mainPanel.setWidget(0, 1, callOutFillPanel2);
//		}
		
		tekstVakken = new TekstVak[hoogtes.size()][breedtes.size()];
		for (int i = 0; i < hoogtes.size(); i++)
		{
			for (int j = 0; j < breedtes.size(); j++)
			{	
				double tekstVakBreedte =  breedtes.get(j).doubleValue() - 2 * cellMarge;
				double tekstVakHoogte = hoogtes.get(i).doubleValue() - 2 * bovenMarge;
				
				if( tekstVakBreedte < 0) tekstVakBreedte = 0;
				if( tekstVakHoogte < 0) tekstVakHoogte = 0;
				
				tekstVakken[i][j] = new TekstVak(this, i, j);
				int th = (int) (Math.round(hoogtes.get(i).doubleValue()));
				tekstVakken[i][j].setSize((int) (Math.round(breedtes.get(j).doubleValue())), th);
				tekstVakken[i][j].setVisible(th>0);
				//tekstVakken[i][j].setPixelSize(breedtes.get(j).intValue(), hoogtes.get(i).intValue());
				tekstVakken[i][j].setColor(fgColor);
				tekstVakken[i][j].setFontStyle(font_style);
				tekstVakken[i][j].setFontName(font_name);
				tekstVakken[i][j].setFontSize(font_size);
				tekstVakken[i][j].setRonding(ronding);
				tekstVakken[i][j].setCentering(centerH, centerV);
				tekstVakken[i][j].setPasHoogteBreedteAan(pasAanH, pasAanB);
				tekstVakken[i][j].setTekstVakBreedte(tekstVakBreedte);
				tekstVakken[i][j].setMarges(bovenMarge, cellMarge);
				tekstVakken[i][j].setInterlinie(interlinie);
				
				mainPanel.setWidget(i + callOutExtraIndex, j + callOutExtraIndex, tekstVakken[i][j]);
			}
		}
		//mainPanel2.add(randPanel);
		//TODO: fix borders for callout (maybe using callOutPanel?)
//		if (callOut)
//		{
//			// take the margins into account
//			mainPanel2.setWidgetLeftRight(randPanel, callOutMargeX0 + cellSpaceColumn - randDikte, Style.Unit.PX, 0, Style.Unit.PX);
//			mainPanel2.setWidgetTopBottom(randPanel, callOutMargeY0 + cellSpaceRow - randDikte, Style.Unit.PX, 0, Style.Unit.PX);
//		}
//		else
//		{
//			mainPanel2.setWidgetLeftRight(randPanel, 0, Style.Unit.PX, 0, Style.Unit.PX);
//			mainPanel2.setWidgetTopBottom(randPanel, 0, Style.Unit.PX, 0, Style.Unit.PX);
//		}
//		
		
		mainPanel2.add(mainPanel);
		mainPanel2.setWidgetLeftRight(mainPanel, 0, Style.Unit.PX, 0, Style.Unit.PX);
		mainPanel2.setWidgetTopBottom(mainPanel, 0, Style.Unit.PX, 0, Style.Unit.PX);
		
		if(sleepbaar && sleepHandle)
		{	crosshair = new Image(DWOplayer.DWO_BUNDLE.crosshair().getSafeUri());
			mainPanel2.add(crosshair);
			mainPanel2.setWidgetLeftWidth(crosshair, 0, Style.Unit.PX, 20, Style.Unit.PX);
			mainPanel2.setWidgetTopHeight(crosshair, 0, Style.Unit.PX, 20, Style.Unit.PX);
		}
		if(hoek != 0)
		{	//voor Chrome, Firefox
			mainPanel2.getElement().getStyle().setProperty("transform", "rotate(" + hoek + "deg)");
			//voor Safari
			mainPanel2.getElement().getStyle().setProperty("WebkitTransform", "rotate(" + hoek + "deg)");
			
		}
		
		if(selected)
			setSelected(selected);
		
// pas bij zet opdracht
//		if(inklapbaar)
//		{	initieerKlapUitButton(ingeklapt);
//		}

	}

	/**
	 * Set up the call out canvas with the callout drawing.
	 * 
	 * @param callOutCanvas
	 */
	private void setUpCallOutCanvas(Canvas callOutCanvas)
	{
		Context2d ctx = callOutCanvas.getContext2d();
		
		callOutCanvas.setPixelSize(breedte, hoogte);
		callOutCanvas.setCoordinateSpaceWidth(breedte);
		callOutCanvas.setCoordinateSpaceHeight(hoogte);
	
		ctx.clearRect(0, 0, breedte, hoogte);
		
		// draw the call out shape
		ctx.beginPath();
		ctx.setFillStyle(randColor);
		ctx.setStrokeStyle(randColor);
		
		
		int xm = callOutMargeX0 + breedtes.get(0).intValue() / 2;
		int ym = callOutMargeY0 + hoogtes.get(0).intValue() / 2;

		double x = xm - callOutPointX;
		double y = ym - callOutPointY;
		if (x == 0)
			x += 0.000001;

		double rx = 1.0 / Math.sqrt(1.0 + y * y / (x * x));
		double ry = 1.0 * y / x / Math.sqrt(1.0 + y * y / (x * x));

		ctx.moveTo(callOutPointX, callOutPointY);
		ctx.lineTo((int) (xm + 5 * ry), (int) (ym - 5 * rx));
		ctx.lineTo((int) (xm - 5 * ry), (int) (ym + 5 * rx));
		ctx.lineTo(callOutPointX, callOutPointY);
		ctx.stroke();
		ctx.fill();
	}

	/**
	 * Resize hoogtes en breedtes voor callout, d.w.z. trek de marges er vanaf.
	 */
	private void resizeForCallOut()
	{
		if (hoogtes.size() == 1)
			hoogtes.set(0, (double) hoogte - callOutMargeY0 - callOutMargeY1 - 2 * randDikte);
		if (breedtes.size() == 1)
			breedtes.set(0, (double) breedte - callOutMargeX0 - callOutMargeX1 - 2 * randDikte);
	}
	
//	public void setTableBounds()
//	{
//		int b = breedte;
//		int h = hoogte;
//		for (int i = 0; i < hoogtes.size(); i++)
//		{
//			for (int j = 0; j < breedtes.size(); j++)
//			{
//				tekstVakken[i][j].setSize(breedtes.get(j).intValue(), hoogtes.get(i).intValue());
//				
//			}
//		}
//	}
	
	public TekstVakPanel(int breedte, int hoogte, String[] randomVarNamen,
			HashMap randomVarWaarden, AnchorContext anchorContext) {
		this(breedte, hoogte, randomVarNamen, randomVarWaarden);
		this.anchorContext = anchorContext;
	}

	public void plaatsTabelRanden()
	{
		double hoogteCum = -0.5 - cellSpaceRow / 2 - randDikte;
		double breedteCum = -0.5 - cellSpaceColumn / 2 - randDikte;
		
		for (int i = 0; i < hoogtes.size() - 1; i++)
		{	hoogteCum += ((Number) hoogtes.get(i)).doubleValue() + cellSpaceRow;
			mainPanel2.setWidgetLeftRight(horizontalBorders[i], 0, Style.Unit.PX, 0, Style.Unit.PX);
			//randPanel.setWidgetTopHeight(horizontalBorders[i], Math.round(hoogteCum), Style.Unit.PX, 1, Style.Unit.PX);
			mainPanel2.setWidgetTopHeight(horizontalBorders[i], (int) Math.round(hoogteCum), Style.Unit.PX, 1, Style.Unit.PX);
		}
		for (int i = 0; i < breedtes.size() - 1; i++)
		{
			breedteCum += breedtes.get(i).doubleValue() + cellSpaceColumn;
			//randPanel.setWidgetLeftWidth(verticalBorders[i], Math.round(breedteCum), Style.Unit.PX, 1, Style.Unit.PX);
			mainPanel2.setWidgetLeftWidth(verticalBorders[i], (int) Math.round(breedteCum), Style.Unit.PX, 1, Style.Unit.PX);
			mainPanel2.setWidgetTopBottom(verticalBorders[i], 0, Style.Unit.PX, 0, Style.Unit.PX);
			
		}
	}

//	public void zetInstellingen(Map<String, Object> instellingen)
//	{
//		this.instellingen = instellingen;
//		//if(instellingen.get("fontSize") != null)
//		//	font_size = ((Number) instellingen.get("fontSize")).intValue();
//	}
	
	public void zetInstellingen(ObjectMap instellingen2)
	{
		this.instellingen = instellingen2;
		if((!anderFont && !fontOvererving) || isHoofdPanel())
		{	if(instellingen2.containsKey("fontSize") )
				font_size =  instellingen2.getInt("fontSize");
			if(instellingen2.containsKey("fontName"))
			{	font_name = instellingen2.getString("fontName");
				if(font_name.equals("SansSerif"))
					font_name = "sans-serif";
			}
			font_style = 0;
			for (int i = 0; i < hoogtes.size(); i++)
			{
				for (int j = 0; j < breedtes.size(); j++)
				{	tekstVakken[i][j].setFontStyle(font_style);
					tekstVakken[i][j].setFontName(font_name);
					tekstVakken[i][j].setFontSize(font_size);
				}
			}
		}
		
		
	}

	public void setKeyboard(FormuleKeyboardIF kb)
	{
		this.kb = kb;
	}
	
	public FormuleKeyboardIF getKeyboard()
	{
		return kb;
	}

	public void setContainer(TekstVakContext container) {
		this.container = container;
	}
	
	//private int width, height;
	
	//public int getCurrentWidth() {
	//	return width;
	//}
	
	//public int getCurrentHeight() {
	//	return height;
	//}
	
	public void setCurrentSize(int w, int h)
	{
		int oldHeight = hoogte;
		mainPanel2.setPixelSize(w - 2 * randDikte, h - 2 * randDikte);
		if (w >= 0)
			breedte = w;
		if (h >= 0)
			hoogte = h;
		if (container != null)
			container.doLayout(this, hoogte - oldHeight);
	}
	
	public void doLayout(TekstVakPanel child, int delta, int rij, int kolom) {
		//System.out.println("child dolayout " + child + " pos " + rij + " " + kolom + " + delta " + delta);
//		int cch = child.getHeight();
//		int tekstGrootte = child.font_size;
//
		//System.out.println("doLayout rij " + rij + " en kolom " + kolom);
		TekstVak myVak = tekstVakken[rij][kolom];
		int n = myVak.getAantalRegels();
		for(int i = 0; i < n; i++) 
		{	myVak.getRegelVak(i).hervulRegel();
			
		}
		//	myVak.getRegelVak(i).resize();
		
		
//		com.google.gwt.user.client.Element element = child.getAsPanel().getElement();
//		element.getStyle().setProperty("verticalAlign", (tekstGrootte - cch + 1) + "px");
		//if(pasAanH)
		//{
//			int cellHoogte = hoogtes.get(rij).intValue();
//			int c0 = Math.max(cellHoogte, minHoogtes.get(rij).intValue());
//			cellHoogte += delta;
//			hoogtes.set(rij, Double.valueOf(cellHoogte));
//			cellHoogte = Math.max(cellHoogte, minHoogtes.get(rij).intValue());
//			for(int j = 0; j < breedtes.size(); j ++)
//			{
//				tekstVakken[rij][j].setPixelSize(-1, (int) cellHoogte);
//			}
//			delta = cellHoogte - c0;
//			System.out.println("new size = " + breedte + "x" + "(" + hoogte + "+ " + delta + ")");
//			//mainPanel2.setPixelSize(width, height += delta);
//						setCurrentSize(-1, hoogte + delta);
		//resize();
		//}
		
	}
	
	public void zetOpdracht(HashMap<String, Object> interactiePanelLaunchState)
	{
		String randVarString = "";
		ArrayList<Object> opdrachtObjects = new ArrayList<Object>();
		List<Object> opdrachtGegevens = JSONUtilities.toArrayList( interactiePanelLaunchState.get("interactiePanelLaunchData") );

		if (random)
		{
			for (int i = 0; i < randomVarNamen.length; i++)
			{
				if(randomVar.equals(randomVarNamen[i]))
				{
					int tabNummer = ((Number) randomVarWaarden.get(randomVar)).intValue() - 1;
					if (tabNummer < aantalRandom && tabNummer > -1)
					{
						//teksten = randomteksten[tabNummer];
						//opdrachtGegevens = JSONUtilities.toArrayList(randomIpLaunchdata[tabNummer]);
						opdrachtGegevens = randomIpLaunchdata.getList(tabNummer);
					}
					break;
				}
			}
		}
		
		TekstBuffer tb = new TekstBuffer(randomVarNamen, randomVarWaarden, anchorContext);
		int[] volleBreedtes = new int[breedtes.size()];
		for (int j = 0; j < breedtes.size(); j++)
		{	volleBreedtes[j] =  (int) (breedtes.get(j).doubleValue() - 2 * cellMarge);
		}
		tb.zetVolleBreedtes(volleBreedtes);
		
		int aantalVakken = 0;
		for (int i = 0; i < hoogtes.size(); i++)
		{
			for (int j = 0; j < breedtes.size(); j++)
			{
				opdrachtObjects = tb.convertTekst(interactiePanelLaunchState, i, j);
				xWidgetMap.putAll(tb.getXWidgetMap());
				Connector.calculateSubscriptions(xWidgetMap.values());
				if(queuedObject != null && i == 1 && j == 0)
				{
					opdrachtObjects.clear();
					opdrachtObjects.add(queuedObject);
					queuedObject = null;
				}
				
				tekstVakken[i][j].zetOpdrachtObjects(opdrachtObjects);
				
				//eerst zorgen dat alle opdrachtObjects goed geïnitialiseerd zijn, daarna zet je ze netjes in het tekstvak neer.
				//voor goede initialisatie van sleepopdr etc is het wel nodig dat de opdrachtobjects al aan het tekstvak zijn toegevoegd.
				for (int k = 0; k < opdrachtObjects.size(); k++)
				{
					Object currentObject = opdrachtObjects.get(k);
					final Object orgObject = currentObject;
// FIXME general unwrap decorator pattern.
					if(currentObject instanceof ShareFacade)
					{
						currentObject = ((ShareFacade) currentObject).unwrap();
					}
					
					
					if (currentObject instanceof InteractionView)
					{
						OpdrNavIF comRoot2 = comRoot;
						Connector connector = find(currentObject);
						comRoot2 = new OpdrNavContext(comRoot,connector, this.bgColorZichtbaar ? bgColor : comRoot.getBackground());
						((InteractionView) orgObject).setCommunicationRoot(comRoot2);
						if(! (currentObject instanceof StateLess))
						{	interactionViewObjects.add(orgObject);
						}
						
						if(currentObject instanceof CheckValueUnit)
						{
							ArrayList<Object> lijst = geefInteractionViews(k, i, j, opdrachtObjects);
							
							int aantalValueObjects = ((CheckValueUnit) currentObject).getAantalValueObjects();
							TekstVakPanel[] waardeObjecten = new TekstVakPanel[aantalValueObjects];
							
							for(int l = 0; l < aantalValueObjects; l++)
							{	waardeObjecten[l] = zoekTekstVakPanel(l+1, lijst);
							
							}
							((CheckValueUnit) currentObject).zetWaardeObjecten(waardeObjecten);
						}
						else if(currentObject instanceof CheckSelectieUnit)
						{
							ArrayList<Object> lijst = geefInteractionViews(k, i, j, opdrachtObjects);
							
							
							int aantalSelectieObjecten = ((CheckSelectieUnit) currentObject).getAantalSelectieObjecten();
							TekstVakPanel[] selectieObjecten = new TekstVakPanel[aantalSelectieObjecten];
							for(int l = 0; l < aantalSelectieObjecten; l++)
								selectieObjecten[l] = zoekTekstVakPanel(l+1, lijst);
							
							((CheckSelectieUnit) currentObject).zetSelectieObjecten(selectieObjecten);
						}
						else if(currentObject instanceof CheckSleepUnit)
						{	
							ArrayList<Object> lijst = geefInteractionViews(k, i, j, opdrachtObjects);
							
							int aantalSleepObjects = ((CheckSleepUnit) currentObject).getAantalSleepObjects();
							
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
							ArrayList<Object> lijst = geefInteractionViews(k, i, j, opdrachtObjects);
							((CheckButton) currentObject).zetNakijkObjecten(lijst);
						}
					}

					if (currentObject instanceof TekstVakPanel)
					{
						Object launchData = opdrachtGegevens.get(aantalVakken);
						//Als opdrachtGegevens direct uit XMLView komen, zitten er eerst 5 lege entries.
						if(opdrachtGegevens.get(0) == null || isHoofdPanel())
						{	launchData = opdrachtGegevens.get(aantalVakken + 5);
						}
						aantalVakken++;
						
						HashMap<String, Object> launchState = (HashMap<String, Object>) ((HashMap<String, Object>) launchData).get("interactiePanelLaunchState");
						TekstVakPanel tekstVakChild = (TekstVakPanel) currentObject;
						tekstVakChild.setParent(tekstVakken[i][j]);
						tekstVakChild.zetInstellingen(instellingen);
						tekstVakChild.setKeyboard(kb);
						tekstVakChild.zetOpdracht(launchState);
						tekstVakChild.setContainer(new TekstVakContext(i,j));
						xWidgetMap.putAll(tekstVakChild.xWidgetMap);
						Connector.calculateSubscriptions(xWidgetMap.values());
					}
					else if (currentObject instanceof FormuleEditorWithAnswer)
					{
						aantalVakken++;
						FormuleEditorWithAnswer formuleEditorWithAnswer = (FormuleEditorWithAnswer) currentObject;
						formuleEditorWithAnswer.zetInstellingen(instellingen);
						if(i == 0 && j == 0)
							queuedObject = formuleEditorWithAnswer.getUitwerking(this);
						else 
						{
							formuleEditorWithAnswer.getUitwerking(null);
						}
						//formuleEditorWithAnswer.paint(); Verplaatst naar TekstVak.setObjects.
						
					}
					else if (currentObject instanceof FormuleEditorWithAnswer.FormuleEditorPopup)
					{
						((FormuleEditorWithSteps) currentObject).zetInstellingen(instellingen);
					}
					else if (currentObject instanceof FormuleEditorWithSteps)
					{
						aantalVakken++;
						((FormuleEditorWithSteps) currentObject).zetInstellingen(instellingen);
					}
					else if (currentObject instanceof FormuleViewer)
					{
						if(this.isSleepbaar())
							((FormuleViewer) currentObject).setSelectable(false);
					}
					else if (currentObject instanceof SymboolPanel)
					{
						aantalVakken++;
						((SymboolPanel) currentObject).zetVolledigeHoogte(tekstVakken[i][j].hoogte);
					}
					else if (currentObject instanceof InteractionView)
					{
						aantalVakken++;
					}
				}
				//setObjects(opdrachtObjects, i, j);
				tekstVakken[i][j].setObjects(opdrachtObjects);
			}
			
		}
		Connector.calculateSubscriptions(xWidgetMap.values());
	
		if (selectable || (sleepbaar && !sleepHandle))
			zetKlikPanel();

		if(inklapbaar)
		{	initieerKlapUitButton(ingeklapt);
			//hierin gebeurt ook resize();
		}
//		else
//			resize();
		
		//resize gebeurt in setVisibility.
		setVisibility(visible);
		

	}
	
	private Connector find(Object currentObject) {
		return getXWidgetMap().get(currentObject);
	}

	public ArrayList<Object> geefInteractionViews(int k, int row, int column, ArrayList<Object> opdrachtObjects)
	{
		ArrayList<Object> lijst = new ArrayList<Object>();
		
		boolean interactionAanwezig = false;
		
		for(int i = 0; i < opdrachtObjects.size(); i++)
		{
			Object currentObject = opdrachtObjects.get(i);
			if(i != k && currentObject instanceof InteractionView)
			{	interactionAanwezig = true;
				break;
			}
		}
		
		if(interactionAanwezig) //in dezelfde cel als de knop zitten andere interactie-objecten. Die geef je terug.
			lijst = opdrachtObjects;
		else if(row == 0 && column==0 && zwevend) //geval knop in zwevend tekstvak
		{
			lijst = parent.getOpdrachtObjects();
		}
		else  //objecten uit hele tekstvakpanel (tot nu toe) geven.
			lijst = interactionViewObjects;
		
		return lijst;
	}

	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		this.comRoot = comRoot;
		if(comRoot != null)
			mode = comRoot.getMode();
		if (dwologger != null) dwologger.setCommunicationRoot(comRoot);
		comRoot.addCBookEventListener(ACTION_SETVISIBLE, this);
		comRoot.addCBookEventListener(ACTION_SETNOTVISIBLE, this);
		comRoot.addCBookEventListener(TVP_SELECT, this);
		comRoot.addCBookEventListener(TVP_DESELECT, this);
		comRoot.addCBookEventListener(TVP_KLAPUIT, this);
		comRoot.addCBookEventListener(TVP_KLAPIN, this);
	}

	public HashMap<String, Object> getState()
	{
		// Onderstaand if-statement is nodig voor tonen vinkjes in popuptekstvak in nagekeken zelf/eindtoets 
//		if (facade.hasState() && facade.getState() != null)
//		{
//			HashMap<String, Object> state = facade.getState();
//			if (state != null)
//				state.put("nagekeken", nagekeken); // alleen op topnivo
//			return state; 
//			// hier mis ik op het cruciale moment de getState() als een vak in de popup gewijzigd is na nakijken
//			// en dan opnieuw toets wordt nagekeken 
//			// Als geen getState() gebeurt, blijft isVeranderdNaNakijken = true en wordt geen vinkje getoond na openen popup
//		}

//		if (facade.hasState()) 
//			return facade.getState();
		
		// Nu: ten behoeve van issues met isVeranderdNaNakijken altijd een volledige getState()
		
		HashMap<String, Object> h = new HashMap<String, Object>();
		ArrayList<Object> states = new ArrayList<Object>();
		for (int i = 0; i < interactionViewObjects.size(); i++)
		{
			Object currentObject = interactionViewObjects.get(i);
			states.add(((InteractionView) currentObject).getState());
		}
		h.put("hoogtes", hoogtes);
		h.put("interactiePanelStates", states);
		h.put("selected", new Boolean(selected));
		h.put("ingeklapt", new Boolean(ingeklapt));
		h.put("popupUsed", Boolean.valueOf(popupUsed));
		h.put("nagekeken", Boolean.valueOf(nagekeken));
		h.put("visible", new Boolean(visible));
		if(zwevend)
		{	h.put("locationX", new Integer(locationX));
			h.put("locationY", new Integer(locationY));
		}
		if (dwologger != null) {
			dwologger.updateLog(buildLogParameters());
		}
		
		return h;
	}
	
	public void getResponses( List<String> responses) {
		for(Object view: interactionViewObjects) {
			if(view instanceof FacetAware) {
				((FacetAware) view).getResponses(responses);
			}
		}		
	}
	
	

	public void setState(HashMap<String, Object> h)
	{
		facade.setPopupState(h);
		if(h == null || h.isEmpty()) 
		{
			setStateNull();
			return;
		}

		ObjectMap map = JSONUtilities.wrapMap(h);
		boolean ingeklapt = this.ingeklapt;
		if (map.containsKey("hoogtes") )
		{
			List<Double> hoogtesState = map.getDoubleList("hoogtes");
			if(hoogtesState != null && hoogtesState.size() == hoogtes.size())
				hoogtes = hoogtesState;
			else
				Logger.getLogger("TekstVakPanel").severe("hoogtes <> hoogteState");
			if(!hoogtes.isEmpty() && Math.round(hoogtes.get(0).doubleValue()) > hoogte)
				hoogte = (int) Math.round(hoogtes.get(0).doubleValue());
		}
		if(map.containsKey("visible"))
			visible = map.getBoolean("visible");
		if(!visible)
		{	hoogte = 0;
			breedte = 0;
		}

		// hier hoogtes en breedtes aanpassen voor callout
		if (callOut)
		{
			resizeForCallOut();
		}
		
		List<Object> states = JSONUtilities.toArrayList(h.get("interactiePanelStates"));
		int size = interactionViewObjects.size();
		if(size != states.size())
			Logger.getLogger("TextVakPanel").severe("sizes " + size + " " + states.size());
		size = Math.min(size, states.size()); // XXX komt voor dat niet alle states bewaard zijn
		for (int i = 0; i < size; i++)
		{
			Object currentObject = interactionViewObjects.get(i);
			if(currentObject instanceof InteractionView) {
				HashMap<String, Object> state = (HashMap<String, Object>) states.get(i);
				((InteractionView) currentObject).setState(state);
			}
		}
		if(map.containsKey("selected"))
			selected = map.getBoolean("selected");
		popupUsed = map.getBoolean("popupUsed", false);
		nagekeken = map.getBoolean("nagekeken", false);
		if(map.containsKey("ingeklapt"))
			ingeklapt = map.getBoolean("ingeklapt");
		if(map.containsKey("locationX"))
			locationX = map.getInt("locationX");
		if(map.containsKey("locationY"))
			locationY = map.getInt("locationY");
		if (!callOut && parent != null && zwevend)
		{	
			parent.setWidgetLeftWidth(this.asWidget(), locationX, Style.Unit.PX, breedte, Style.Unit.PX);
			parent.setWidgetTopHeight(this.asWidget(), locationY, Style.Unit.PX, hoogte, Style.Unit.PX);
		}
		setSelected(selected);
		
		if(inklapbaar && ( ingeklapt != this.ingeklapt))
		{	
			klapUitButton.setDown(ingeklapt);
			klapUitAction();
			
			
		}
		if(inklapbaar && checkUitklapVak)
		{
			goedKrulImage.setVisible(isKlapvakCorrect());
		}
		//resize();
		
		//resize gebeurt in setVisibility;
		if(zichtbaarNaNakijken)
			setVisibility(nagekeken);
		else
			setVisibility(visible);
		
	}
/**
 *  Always set state to something. Pick up shared state.
 */
	private void setStateNull() {
		int size = interactionViewObjects.size();
		for (int i = 0; i < size; i++)
		{
			Object currentObject = interactionViewObjects.get(i);
			if(currentObject instanceof InteractionView) {
				HashMap<String, Object> state = null;
				((InteractionView) currentObject).setState(state);
			}
		}
	}

	public int getScore()
	{
		int score = 0;
		for (int i = 0; i < interactionViewObjects.size(); i++)
		{
			Object currentObject = interactionViewObjects.get(i);
			score += ((InteractionView) currentObject).getScore();
		}
		if (aftrekPopup && popupUsed)
			score = score - puntenAftrekPopup;
		return score;
	}
	
	public int[][] getScoreObjectives()
	{
		int[][] scoreObjectives = new int[XMLView.objectives.length][];
		for (int i = 0; i < XMLView.objectives.length; i++)
			scoreObjectives[i] = new int[XMLView.objectives[i].length];
		for (int i = 0; i < interactionViewObjects.size(); i++)
		{
			Object currentObject = interactionViewObjects.get(i);
			int[][] scoreObj = ((InteractionView) currentObject).getScoreObjectives();
			for (int j = 0; scoreObj != null && j < XMLView.objectives.length && j < scoreObj.length; j++)
			{	for (int k = 0; scoreObj[j] != null && k < XMLView.objectives[j].length && k < scoreObj[j].length; k++)
				{
					scoreObjectives[j][k] += scoreObj[j][k];
					//terug als aftrek voor popup geimplementeerd:
					if (aftrekPopup && popupUsed)
						scoreObjectives[j][k] -= puntenAftrekPopup;
				}
			}
		}
		return scoreObjectives;
	}
	
	public int[][] getPossibleMisconceptions()
	{
		int[][] totalPossibleMisconceptions = new int[XMLView.misconceptions.length][];
		for(int i = 0; i < XMLView.misconceptions.length; i++)
			totalPossibleMisconceptions[i] = new int[XMLView.misconceptions[i].length];
		for(int i = 0; i < interactionViewObjects.size(); i++)
		{
			Object currentObject = interactionViewObjects.get(i);
			if(currentObject instanceof InteractionViewWithMisconceptions)
			{
				int[][] possibleMisconceptions = ((InteractionViewWithMisconceptions) currentObject).getPossibleMisconceptions();
				for (int j = 0; possibleMisconceptions != null && j < XMLView.misconceptions.length && j < possibleMisconceptions.length; j++)
				{	for (int k = 0; possibleMisconceptions[j] != null && k < XMLView.misconceptions[j].length && k < possibleMisconceptions[j].length; k++)
					{
						totalPossibleMisconceptions[j][k] += possibleMisconceptions[j][k];
					}
				}
			}
		}
		return totalPossibleMisconceptions;
	}
	
	public int[][] getMeasuredMisconceptions()
	{
		int[][] totalMeasuredMisconceptions = new int[XMLView.misconceptions.length][];
		for(int i = 0; i < XMLView.misconceptions.length; i++)
			totalMeasuredMisconceptions[i] = new int[XMLView.misconceptions[i].length];
		for(int i = 0; i < interactionViewObjects.size(); i++)
		{
			Object currentObject = interactionViewObjects.get(i);
			if(currentObject instanceof InteractionViewWithMisconceptions)
			{
				int[][] measuredMisconceptions = ((InteractionViewWithMisconceptions) currentObject).getMeasuredMisconceptions();
				for (int j = 0; measuredMisconceptions != null && j < XMLView.misconceptions.length && j < measuredMisconceptions.length; j++)
				{	for (int k = 0; measuredMisconceptions[j] != null && k < XMLView.misconceptions[j].length && k < measuredMisconceptions[j].length; k++)
					{
						totalMeasuredMisconceptions[j][k] += measuredMisconceptions[j][k];
					}
				}
			}
		}
		return totalMeasuredMisconceptions;
	}

	
	/*
	 * if all  true  return true
	 * if some null  return null
	 * if some false return false
	 * @see nl.uu.fi.dwo.interaction.client.InteractionView#isCorrect()
	 */
	public Boolean isCorrect()
	{
		Boolean correct = Boolean.TRUE;
		for (int i = 0; i < interactionViewObjects.size(); i++)
		{
			Object currentObject = interactionViewObjects.get(i);
			Boolean check = ((InteractionView) currentObject).isCorrect();
			if (check == null)
				correct = null;
			if (Boolean.FALSE.equals(check))
				return check;
		}
		return correct;
	}

	public void zetNagekeken(boolean b)
	{
		nagekeken = b;
		if(zichtbaarNaNakijken)
			setVisibility(b);
		
		for (Object object : interactionViewObjects) {
			if(object instanceof InteractionView)
				((InteractionView) object).zetNagekeken(b);
		}
	}
	
	public void setParent(TekstVak panel)
	{
		parent = panel;
		if(fontOvererving && !anderFont && parent != null && parent.getTekstVakParent() != null)
		{	
			CssColor fgColorOvererving = parent.getTekstVakParent().fgColor;
			int fontSizeOvererving = parent.getTekstVakParent().font_size;
			int fontStyleOvererving = parent.getTekstVakParent().font_style;
			String fontNameOvererving = parent.getTekstVakParent().font_name;
			
			fgColor = fgColorOvererving;
			font_size = fontSizeOvererving;
			font_style = fontStyleOvererving;
			font_name = fontNameOvererving;
			
			for (int i = 0; i < hoogtes.size(); i++)
			{	
				for (int j = 0; j < breedtes.size(); j++)
				{	tekstVakken[i][j].setColor(fgColorOvererving);
					tekstVakken[i][j].setFontStyle(fontStyleOvererving);
					tekstVakken[i][j].setFontName(fontNameOvererving);
					tekstVakken[i][j].setFontSize(fontSizeOvererving);
				}
			}
		}
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
		if(callOut)
			return callOutPanel;
		else 
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
		return facade.wrap(getAsPanel(), this);
	}
	
	public boolean isPopup()
	{
		return facade.isPopup();
	}
	
	public boolean isInklapbaar()
	{
		return inklapbaar;
	}
	
	public boolean isZwevend()
	{
		return zwevend;
	}
	
	public boolean isSleepbaar()
	{
		return sleepbaar;
	}
	
	public boolean isMouseDown()
	{
		return mouseHandler.isMouseDown();
	}
	
	public void setMouseDown(boolean b)
	{
		mouseHandler.setMouseDown(b);
	}
	
	public Point geefLocatie()
	{
		return new Point(locationX, locationY);
	}
	
	
	
	public void zetLocatie(double x, double y) 
	{
		locationX = (int) x;
		locationY = (int) y;
		
		if(parent != null && zwevend)
		{	parent.remove(this.asWidget());
			parent.add(this.asWidget());
			parent.setWidgetLeftWidth(this.asWidget(), locationX, Style.Unit.PX, breedte, Style.Unit.PX);
			parent.setWidgetTopHeight(this.asWidget(), locationY, Style.Unit.PX, hoogte, Style.Unit.PX);
		}
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
	
	public void zetKlikPanel()
	{
		if (klikPanel == null)
		{
			klikPanel = new FlowPanel();
			//klikPanel.setBounds(0, 0, getSize().width, getSize().height);
			//klikPanel.setOpaque(false);
			//klikPanel.addMouseListener(this);
			mainPanel2.add(klikPanel);
			mainPanel2.setWidgetLeftRight(klikPanel, 0, Style.Unit.PX, 0, Style.Unit.PX);
			mainPanel2.setWidgetTopBottom(klikPanel, 0, Style.Unit.PX, 0, Style.Unit.PX);
		}

		mainPanel2.setWidgetLeftRight(klikPanel, 0, Style.Unit.PX, 0, Style.Unit.PX);
		mainPanel2.setWidgetTopBottom(klikPanel, 0, Style.Unit.PX, 0, Style.Unit.PX);
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
				//randPanel.getElement().getStyle().setBorderColor(selectieColor.toString());
				if(bgColorZichtbaar)
					mainPanel2.getElement().getStyle().setBackgroundColor(selectieColor.toString());
				else {
					mainPanel2.getElement().getStyle().setBorderColor(selectieColor.toString());
					int borderWidth = (int) Math.round(Math.min(new Double(hoogte) / 2, new Double(breedte) / 2));
					mainPanel2.getElement().getStyle().setBorderWidth(borderWidth, Unit.PX);
					mainPanel2.getElement().getStyle().setOpacity(0.4); 
					mainPanel2.setPixelSize(breedte - 2 * borderWidth, hoogte - 2 * borderWidth);					
				}
					
				//
				
			}
			else
			{	mainPanel2.getElement().getStyle().setBorderColor(grijs.toString());
				mainPanel2.getElement().getStyle().setBorderWidth(5, Unit.PX);
				mainPanel2.setWidgetLeftRight(mainPanel, randDikte - 5, Style.Unit.PX, randDikte - 5, Style.Unit.PX);
				mainPanel2.setWidgetTopBottom(mainPanel, randDikte - 5, Style.Unit.PX, randDikte - 5, Style.Unit.PX);
			}
		
		}
		else
		{
			if(colorSelection)
			{
				if(bgColorZichtbaar)
					mainPanel2.getElement().getStyle().setBackgroundColor(bgColor.toString());
				else {
					mainPanel2.getElement().getStyle().setBorderColor(randColor.toString());
					mainPanel2.getElement().getStyle().setBorderWidth(randDikte, Unit.PX);
					mainPanel2.getElement().getStyle().setOpacity(1);
					mainPanel2.setPixelSize(breedte - 2 * randDikte, hoogte - 2 * randDikte);
				}
					
			}
			else
			{
				mainPanel2.getElement().getStyle().setBorderColor(randColor.toString());
				//randPanel.getElement().getStyle().setOpacity(1);
				mainPanel2.getElement().getStyle().setBorderWidth(randDikte, Unit.PX);
				mainPanel2.setWidgetLeftRight(mainPanel, 0, Style.Unit.PX, 0, Style.Unit.PX);
				mainPanel2.setWidgetTopBottom(mainPanel, 0, Style.Unit.PX, 0, Style.Unit.PX);
			}
			
			
			
		}
	}
	
	public void resize()
	{
		// Allereerst zorgen dat ashoogtes op alle regels over de gehele regel
		// gelijk zijn.

		for (int i = 0; i < hoogtes.size(); i++)
		{
			int asHoogte = 0;
			for (int j = 0; j < breedtes.size(); j++)
			{
				if (tekstVakken[i][j].getAsHoogte() > asHoogte)
				{
					asHoogte = tekstVakken[i][j].getAsHoogte();
				}

			}
			for (int j = 0; j < breedtes.size(); j++)
			{
				tekstVakken[i][j].setAshoogte(asHoogte);
				// tekstVakken[i][j].setRegelHoogte(regelHoogte);
			}
		}

		// kijken of pasAanH en of pasAanB true zijn; anders zijn we klaar.
		// NEE, want resize gebeurt ook bij in/uitklappen van vak binnen
		// tekstvakpanel; dan veranderen maten ook.
		// if(!pasAanH && !pasAanB)
		// return;

		int[] ashoogtes = new int[hoogtes.size()];
		for (int i = 0; i < hoogtes.size(); i++) 	// ashoogtes vullen, voor het
													// geval de hoogte niet
													// wordt aangepast.
		{
			int ashoogte = tekstVakken[i][0].getAsHoogte();
			// volgens mij is dit hieronder niet nodig, je hebt net gezorgd dat
			// de ashoogtes op de hele regel gelijk zijn.
			for (int j = 1; j < breedtes.size(); j++)
			{
				if (tekstVakken[i][j].getAsHoogte() > ashoogte)
					ashoogte = tekstVakken[i][j].getAsHoogte();
			}
			ashoogtes[i] = ashoogte;
		}
		int totaleHoogte = hoogte;
		int totaleBreedte = breedte;

		if (pasAanH && !vulHoogte)
			totaleHoogte = 0;
		for (int i = 0; i < hoogtes.size(); i++)
		{
			if (i > 0 && inklapbaar && ingeklapt)
				break;

			int h1 = 0;
			int h2 = 0;
			for (int j = 0; j < breedtes.size(); j++)
			{
				// opnieuw alles plaatsen qua hoogte; zoals het tekstvak het
				// zelf zou doen als hem geen hoogte was opgelegd.
				if (pasAanH)
					tekstVakken[i][j].pasHoogteAanInhoudAan(true);
				else
					for (int k = 0; k < tekstVakken[i][j].getAantalRegels(); k++)
					{
						tekstVakken[i][j].getRegelVak(k).bepaalAshoogte();
					}
				int hoogte = tekstVakken[i][j].hoogte;
				int ash = tekstVakken[i][j].getAsHoogte();
				if (!tekstVakken[i][j].isVisible())
				{
					hoogte = ash = 0;
				}

				if (ash > h1)
					h1 = ash;
				if (hoogte - ash > h2)
					h2 = hoogte - ash;
			}

			if (pasAanH && !vulHoogte && !callOut)
			{
				hoogtes.set(i, new Double(h1 + h2));
				totaleHoogte += h1 + h2 + cellSpaceRow;
			}
			ashoogtes[i] = h1;
		}
		if (pasAanH && !vulHoogte)
			totaleHoogte -= cellSpaceRow;

		if (pasAanB)
		{
			totaleBreedte = 0;
			for (int j = 0; j < breedtes.size(); j++)
			{
				int breedte = 0;
				for (int i = 0; i < hoogtes.size(); i++)
				{ 
					// opnieuw alles plaatsen qua breedte, zoals het tekstvak het
					// zelf zou doen als hem geen breedte was opgelegd.
					// tekstVakken[i][j].

					if (tekstVakken[i][j].getInhoudBreedte() > breedte)
						breedte = tekstVakken[i][j].getInhoudBreedte();
				}
				breedte += 2 * cellMarge;
				if (!callOut)
				{
					breedtes.set(j, new Double(breedte));
				}
				totaleBreedte += breedte + cellSpaceColumn;
			}
			totaleBreedte -= cellSpaceColumn;
		}
		if (!visible)
		{
			totaleHoogte = 0;
			totaleBreedte = 0;
		}

		if (!callOut)
		{
			setCurrentSize(totaleBreedte, totaleHoogte);
		}
		
		plaatsTabelRanden();

		if (callOut)
		{
			mainPanel2.setPixelSize(breedtes.get(0).intValue(), hoogtes.get(0).intValue());
			//resizeRandPanel();
			//TODO: replace by resizeCallOutPanel?
		}

		for (int i = 0; i < hoogtes.size(); i++)
		{
			for (int j = 0; j < breedtes.size(); j++)
			{
				if (i == 0 || !(inklapbaar && ingeklapt))
				{
					tekstVakken[i][j].setSize((int) Math.round(breedtes.get(j).doubleValue()),
						(int) Math.round(hoogtes.get(i).doubleValue()));
					tekstVakken[i][j].setAshoogte(ashoogtes[i]);
				}
			}
		}

		if (parent != null)
		{
			if (!callOut && isZwevend() && this.getAsPanel().isAttached())
			{
				parent.setWidgetLeftWidth(this.getAsPanel(), this.getLocationX(), Style.Unit.PX, totaleBreedte,
					Style.Unit.PX);
				parent.setWidgetTopHeight(this.getAsPanel(), this.getLocationY(), Style.Unit.PX, totaleHoogte,
					Style.Unit.PX);
			}
			if (!vulHoogte)
				parent.resize();

		}

		// eventueel opvullen hoogtes in tekstvakken regelen en hoogtes symbolen
		// instellen
		corrigeerOpvulHoogtes();
		vulSymboolHoogtes();
	}
	
	/**
	 * Resize randpanel with the current breedtes and hoogtes.
	 */
//	private void resizeRandPanel()
//	{
//		randPanel.setPixelSize(breedtes.get(0).intValue(), hoogtes.get(0).intValue());
//	}

	public boolean tabFocus(TekstVak source, boolean up)
	{
		if(source == null)
			doorzochtDoorTab = false;
		int startRij = 0;
		int startKolom = 0;
		boolean focusVerlegd = false; 
		boolean laatsteVak = false;
		//if source outside TekstVakPanel: start searching at 0.
		//if source inside TekstVakPanel: start searching at next TekstVak
		//Volgorde: hele rij doorzoeken, dan pas naar volgende rij. 
		if(up)
		{
			for(int i = 0; i < tekstVakken.length; i++)
			{
				for(int j = 0; j < tekstVakken[i].length; j++)
				if(tekstVakken[i][j].equals(source))
				{	if(j < tekstVakken[i].length - 1)
					{	startRij = i;
						startKolom = j + 1;
					}
					else if(i < tekstVakken.length - 1)
					{
						startRij = i + 1;
					}
					else 	//laatste tekstVak van het tekstVakPanel, dus meteen door naar omvattende tekstVak van het tekstVakPanel
					{
						laatsteVak = true;
					}
					break;
				}
			}
		}
		
		if(!laatsteVak)
		{
			//binnen tekstVakPanel verder zoeken.
			//startRij apart behandelen, omdat je daar in startKolom begint en niet in kolom 0. 
			for(int j = startKolom; j < tekstVakken[startRij].length; j++)
			{
				focusVerlegd = tekstVakken[startRij][j].tabFocus(this, false);
				if(focusVerlegd)
					return true;
			}
			if(startRij < tekstVakken.length - 1)
			{	for(int i = startRij + 1; i < tekstVakken.length; i++)
				{
					for(int j = 0; j < tekstVakken[i].length; j++)
					{
						focusVerlegd = tekstVakken[i][j].tabFocus(this, false);
						if(focusVerlegd)
							return true;
					}
				}
			}
		}
		
		//als omliggende tekstvak bestaat: doorgeven naar omliggende tekstvak
		if(parent != null)
			return parent.tabFocus(this, up);
		//Anders: helemaal aan het begin van dit tekstvak verder zoeken. 
		else if(!doorzochtDoorTab)
		{	doorzochtDoorTab = true;
			for(int i = 0; i < tekstVakken.length; i++)
			{
				for(int j = 0; j < tekstVakken[i].length; j++)
				{
					focusVerlegd = tekstVakken[i][j].tabFocus(this, false);
					if(focusVerlegd)
						return true;
				}
			}
			return false;
		}
		else
			return true;
	}
	
	public boolean shiftTabFocus(TekstVak source, boolean up)
	{
		int startRij = tekstVakken.length - 1;
		int startKolom = 0;
		if(tekstVakken.length > 0)
			startKolom = tekstVakken[tekstVakken.length - 1].length - 1;
		boolean focusVerlegd = false; 
		boolean eersteVak = false;
		//if source outside TekstVakPanel: start searching at size.
		//if source inside TekstVakPanel: start searching at previous TekstVak
		//Volgorde: hele rij doorzoeken van rechts naar links (hoog kolomnr naar laagkolomnr), 
		//dan pas naar vorige rij. 
		if(up)
		{
			for(int i = tekstVakken.length - 1; i >= 0; i--)
			{
				for(int j = tekstVakken[i].length - 1; j >= 0; j--)
				if(tekstVakken[i][j].equals(source))
				{	if(j > 0)
					{	startRij = i;
						startKolom = j - 1;
					}
					else if(i > 0)
					{
						startRij = i - 1;
					}
					else 	//laatste tekstVak van het tekstVakPanel, dus meteen door naar omvattende tekstVak van het tekstVakPanel
					{
						eersteVak = true;
					}
					break;
				}
			}
		}
		
		if(!eersteVak)
		{
			//binnen tekstVakPanel verder zoeken.
			//startRij apart behandelen, omdat je daar in startKolom begint en niet in laatste kolom. 
			for(int j = startKolom; j >= 0; j--)
			{
				focusVerlegd = tekstVakken[startRij][j].shiftTabFocus(this, false);
				if(focusVerlegd)
					return true;
			}
			if(startRij > 0)
			{	for(int i = startRij - 1; i >= 0; i--)
				{
					for(int j = tekstVakken[i].length - 1; j >= 0; j--)
					{
						focusVerlegd = tekstVakken[i][j].shiftTabFocus(this, false);
						if(focusVerlegd)
							return true;
					}
				}
			}
		}
		
		//als omliggende tekstvak bestaat: doorgeven naar omliggende tekstvak
		if(parent != null)
			return parent.shiftTabFocus(this, up);
		//anders: helemaal aan het eind van dit tekstvak verder zoeken. 
		else
		{	for(int i = tekstVakken.length - 1; i >= 0; i--)
			{
				for(int j = tekstVakken[i].length - 1; j >= 0; j--)
				{
					focusVerlegd = tekstVakken[i][j].shiftTabFocus(this, false);
					if(focusVerlegd)
						return true;
				}
			}
			return false;
		}
		
		
	}
	
	public void corrigeerOpvulHoogtes()
	{
		for(int i = 0; i < hoogtes.size(); i++)
		{	for(int j = 0; j < breedtes.size(); j++)
			{	tekstVakken[i][j].corrigeerOpvulHoogte();
			}
		}
	}
	
	public void vulSymboolHoogtes()
	{
		for(int i = 0; i < hoogtes.size(); i++)
		{
			for(int j = 0; j < breedtes.size(); j++)
			{
				tekstVakken[i][j].vulSymboolHoogtes();
			}
		}
	}
	
	/*
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
	}*/
	
	public void zetGoedFout(boolean b)
	{
//		if (b)
//		{
//			randPanel.getElement().getStyle().setBorderColor(CssColor.make(50, 225, 50).toString());
//			randPanel.getElement().getStyle().setBorderWidth(5, Unit.PX);
//		}
//		else
//		{	randPanel.getElement().getStyle().setBorderColor(CssColor.make(225, 50, 50).toString());
//			randPanel.getElement().getStyle().setBorderWidth(5, Unit.PX);
//		}
		if(interactionViewObjects.size() > 0)
		{	Object firstObject = interactionViewObjects.get(0);
			if(firstObject instanceof FormuleEditorWithAnswer)
				((FormuleEditorWithAnswer) firstObject).zetGoedFoutCheckWaarde(b?AntwoordVakChecker.GOED:AntwoordVakChecker.FOUT);
		}
	}
	
	public void wisGoedFout()
	{
		if(interactionViewObjects.size() > 0)
		{	Object firstObject = interactionViewObjects.get(0);
			if(firstObject instanceof FormuleEditorWithAnswer)
				((FormuleEditorWithAnswer) firstObject).resetimg();
		}
	}

	public void wisGoedFoutSleep()
	{
		mainPanel2.getElement().getStyle().setBorderColor(randColor.toString());
		mainPanel2.getElement().getStyle().setOpacity(1);
		mainPanel2.getElement().getStyle().setBorderWidth(randDikte, Unit.PX);
		//mainPanel2.setPixelSize(breedte - 2 * randDikte, hoogte - 2 * randDikte);
		mainPanel2.setWidgetLeftRight(mainPanel, 0, Style.Unit.PX, 0, Style.Unit.PX);
		
		if(sleepbaar && sleepHandle)
		{	mainPanel2.setWidgetLeftWidth(crosshair, 0, Style.Unit.PX, 20, Style.Unit.PX);
			mainPanel2.setWidgetTopHeight(crosshair, 0, Style.Unit.PX, 20, Style.Unit.PX);
		}
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
					sleepObjecten[i].zetGoedFoutSleep(b);
					break;
				}
			}
		}
		
		if(!sleepdoel)
		{
			if (b)
			{
				mainPanel2.getElement().getStyle().setBorderColor(CssColor.make(50, 225, 50).toString()); // groen
				
			}
			else
			{	mainPanel2.getElement().getStyle().setBorderColor(CssColor.make(225, 50, 50).toString()); // rood
				
			}
			mainPanel2.getElement().getStyle().setBorderWidth(5, Unit.PX);
			mainPanel2.setWidgetLeftRight(mainPanel, randDikte - 5, Style.Unit.PX, randDikte - 5, Style.Unit.PX);
			mainPanel2.setWidgetTopBottom(mainPanel, randDikte - 5, Style.Unit.PX, randDikte - 5, Style.Unit.PX);
			if(sleepbaar && sleepHandle)
			{	mainPanel2.setWidgetLeftWidth(crosshair, - 5, Style.Unit.PX, 20, Style.Unit.PX);
				mainPanel2.setWidgetTopHeight(crosshair, - 5, Style.Unit.PX, 20, Style.Unit.PX);
			}
		}
	}
	
	public static void zetFontOvererving(boolean b)
	{	fontOvererving = b;
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
			{	
				FormuleEditorWithAnswer fewa = (FormuleEditorWithAnswer) object;
				FormuleRegel fr = fewa.getMainRegel();
				if (fr != null && fewa.isVergelijkingVak())
				{
					String string = fr.toString();
					VergelijkingMeerv vgm = FormuleParser.parseVergelijking("$f" + string + "@");
					Vergelijking vg = null;
					if (vgm != null)
						vg = vgm.geefVergelijking(0);
					Expressie e = null;
					if (vg != null)
						e = new Aftrekking(vg.geefExpLinks(), vg.geefExpRechts());
					if (e != null)
						return "$f" + e.toString() + "@";
				}
				else if(fr != null)
				{
					String string = fr.toString();
					return "$f" + string + "@";
				}	
			}
		}
			
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
		comRoot.pause();
		for(int i = 0; i < interactionViewObjects.size(); i++)
		{	Object object = interactionViewObjects.get(i);
			if(object instanceof FormuleEditorWithAnswer)
			{	FormuleEditorWithAnswer object2 = (FormuleEditorWithAnswer) object;
				object2.kijkNa(false, false, false);
				juist = object2.isCorrectStrikt() != null && object2.isCorrectStrikt().booleanValue();
			}
		}
		comRoot.unpause();
		return juist;
	}
	
	public boolean ipObjectIsIngevuld()
	{
		boolean ingevuld = false;
		comRoot.pause();
		for(int i = 0; i < interactionViewObjects.size(); i++)
		{	Object object = interactionViewObjects.get(i);
			if(object instanceof FormuleEditorWithAnswer)
			{	FormuleEditorWithAnswer object2 = (FormuleEditorWithAnswer) object;
				object2.kijkNa(false, false, false);
				ingevuld = ingevuld || object2.isIngevuld();
			}
		}
		comRoot.unpause();
		return ingevuld;
	}
	
	public boolean ipObjectIsChanged()
	{
		boolean changed = false;
		for(int i = 0; i < interactionViewObjects.size(); i++)
		{	Object object = interactionViewObjects.get(i);
			if(object instanceof FormuleEditorWithAnswer)
			{	FormuleEditorWithAnswer object2 = (FormuleEditorWithAnswer) object;
				changed = changed || object2.isChanged();
			}
		}
		return changed;
	}
	
	public void setChanged(boolean b)
	{
		for(int i = 0; i < interactionViewObjects.size(); i++)
		{	Object object = interactionViewObjects.get(i);
			if(object instanceof FormuleEditorWithAnswer)
			{	FormuleEditorWithAnswer object2 = (FormuleEditorWithAnswer) object;
				if(!b || object2.wordtGecheckt())
					object2.setChanged(b);
			}
		}
	}

	
	public Expressie geefObjectWaarde()
	{
		String ipExpString = getIpExpString();
		Expressie waarde = FormuleParser.geefExpressie(ipExpString);
		if("$f@".equals(ipExpString) && defaultBijNull)
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

	public int geefOpgevuldeHoogte()
	{	return tekstVakken[0][0].geefRestHoogte();
	}
	
	public boolean vulHoogteMogelijk()
	{
		return vulHoogte;
	}
	
	public void corrigeerRestHoogte(int restHoogte)
	{
		setCurrentSize(breedte, Math.max(0, hoogte + restHoogte));
	}
		
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{
		if(selectable && !sleepbaar)
		{
			selected = !selected;
			setSelected(selected);
			if(selected)
				fireEvent(SELECT_EVENT);
			else
				fireEvent(DESELECT_EVENT);
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
		
		if(parent != null && zwevend)
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
	{	if(sleepbaar)
		{	//hier zorgen dat de pagina wordt versleept?
		locationX = eventX - startX;
		locationY = eventY - startY;
		if(parent != null && zwevend)
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
		} else {
// Werk dit? FIXME naar de link api.		
		if(isLink && linkUrls != null) {
			String link = linkUrls.getString(0);
			if(anchorContext == null || !link.startsWith("goto:"))
				Window.open(link, "_blank", "");
			else
				anchorContext.gotoUrl(link);
		}}
	}
	
	public TekstVakPanel findMouseDownObject()
	{
		for(int i = 0; i < interactionViewObjects.size(); i++)
		{
			Object object = interactionViewObjects.get(i);
			if(object instanceof TekstVakPanel)
			{
				TekstVakPanel tvp = (TekstVakPanel) object;
				if(tvp.isSleepbaar() && tvp.isMouseDown())
					return tvp;
				else 
				{
					TekstVakPanel tvp2 = tvp.findMouseDownObject();
					if(tvp2 != null)
						return tvp2;
				}
			}
		}
		return null;
	}
	
	class MouseHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler, MouseOutHandler
	{   
		private boolean mouseDown = false;
		
		public void onMouseDown(MouseDownEvent e)
		{
			if(!editable) return;
			if(sleepbaar && sleepHandle && (e.getX() > 20 || e.getY() > 20) )
				return;
			e.stopPropagation();
			int eventX = e.getClientX();
			int eventY = e.getClientY();
			
			mouseDown = true;
			mouseDownTouchStartAction(eventX, eventY);
		}
		
		public void onMouseMove(MouseMoveEvent e)	
		{	// prevent scrolling
//			if(sleepbaar && sleepHandle && (e.getX() > 20 || e.getY() > 20) )
//			{	mouseDown = false;
//				return;
//			}
			e.stopPropagation(); 
			if(!editable) return;
			int eventX = e.getClientX();
			int eventY = e.getClientY();
			
			if (!mouseDown)
			{	//Kijken of zich binnen huidige tekstvakpanel een object bevindt dat momenteel wordt gesleept
				for(int i = 0; i < interactionViewObjects.size(); i++)
				{
					Object object = interactionViewObjects.get(i);
					if(object instanceof TekstVakPanel && ((TekstVakPanel) object).isSleepbaar() && ((TekstVakPanel) object).isMouseDown())
					{	((TekstVakPanel) object).mouseMoveTouchMoveAction(eventX, eventY);
						return;
					}
				}
				TekstVak localParent = parent;
				while(localParent != null)
				{
					TekstVakPanel tekstVakParent = localParent.getTekstVakParent();
					if(tekstVakParent.isSleepbaar() && tekstVakParent.isMouseDown())
					{	tekstVakParent.mouseMoveTouchMoveAction(eventX, eventY);
						return;
					}
					for(int i = 0; i < tekstVakParent.interactionViewObjects.size(); i++)
					{
						Object object = tekstVakParent.interactionViewObjects.get(i);
						if(object instanceof TekstVakPanel && ((TekstVakPanel) object).isSleepbaar() && ((TekstVakPanel) object).isMouseDown())
						{	((TekstVakPanel) object).mouseMoveTouchMoveAction(eventX, eventY);
							return;
						}
					}
					localParent = tekstVakParent.parent;
				}
				return;
			}

			if(sleepbaar && mouseDown)
			{	e.preventDefault();
				mouseMoveTouchMoveAction(eventX, eventY);
			}
			
			
		} // onMouseMove
		
		public void onMouseUp(MouseUpEvent e)	
		{	// prevent scrolling
			if(sleepbaar && sleepHandle && (e.getX() > 20 || e.getY() > 20) )
			{	mouseDown = false;
				return;
			}
			
			e.stopPropagation();
			if(!editable) return;
			int eventX = e.getClientX();
			int eventY = e.getClientY();
			
			if (!mouseDown)
				{	
					TekstVakPanel object = findMouseDownObject();
					if(object != null)
					{	object.setMouseDown(false);
						object.mouseUpTouchEndAction(eventX, eventY);
					}
					else if(parent != null && zwevend)
					{
						object = parent.getTekstVakParent().findMouseDownObject();
						if(object != null)
						{	object.setMouseDown(false);
							object.mouseUpTouchEndAction(eventX, eventY);
						}
					}
				}
			mouseDown = false;
			mouseUpTouchEndAction(eventX,eventY);

		}
		
		public boolean isMouseDown()
		{
			return mouseDown;
		}
		
		public void setMouseDown(boolean b)
		{
			mouseDown = b;
		}

		@Override
		public void onMouseOut(MouseOutEvent e) {
			
			e.stopPropagation();
			if(!editable) return;
			int eventX = e.getClientX();
			int eventY = e.getClientY();
			
			if (!mouseDown)
			{	for(int i = 0; i < interactionViewObjects.size(); i++)
				{
					Object object = interactionViewObjects.get(i);
					if(object instanceof TekstVakPanel && ((TekstVakPanel) object).isSleepbaar() && ((TekstVakPanel) object).isMouseDown())
					{	((TekstVakPanel) object).setMouseDown(false);
						((TekstVakPanel) object).mouseUpTouchEndAction(eventX, eventY);
						break;
					}
				}
				if(parent != null && zwevend)
				{
					TekstVakPanel tekstVakParent = parent.getTekstVakParent();
					for(int i = 0; i < tekstVakParent.interactionViewObjects.size(); i++)
					{
						Object object = tekstVakParent.interactionViewObjects.get(i);
						if(object instanceof TekstVakPanel && ((TekstVakPanel) object).isSleepbaar() && ((TekstVakPanel) object).isMouseDown())
						{	((TekstVakPanel) object).setMouseDown(false);
							((TekstVakPanel) object).mouseUpTouchEndAction(eventX, eventY);
							break;
						}
					}
				}
			}
		}

	} //MouseHandler
	
	class TouchHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{
		
		public void onTouchStart(TouchStartEvent e)
		{
			e.stopPropagation();
			if(!editable) return;
			
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
			if(!editable) return;
			
			if(e.getTouches().length() == 0)
				return;
			
			Touch touch = e.getTouches().get(0);
			
//			if(sleepbaar && sleepHandle && (touch.getPageX() - getAsPanel().getAbsoluteLeft() > 20 || 
//					touch.getPageY() - getAsPanel().getAbsoluteTop() > 20))
//			{	e.preventDefault();
//				return;
//			}
		
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
			if(!editable) return;
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
	
	public int getFirstRowMinHeight(TekstVak tv)
	{
		if(tv!=tekstVakken[0][0] || !pasAanH) 
			return 0;
		int minHeight = 2*bovenMarge;
		if(inklapbaar && klapUitButton!=null ) 
		{	
			if(knopImageView1 != null)
				minHeight = minHeight + knopImageView1.getHeight();
			else if(view1 != null) 
				minHeight += view1.getHeight();
		}
		return minHeight;
	}
	
	/**
	 * Deze methode wordt aangeroepen na klik van gebruiker om vak uit te klappen 
	 * en vanuit setState().
	 */
	void klapUitAction() {
		
		int delta;
		//System.out.println("delta = " + delta);
		if( ingeklapt = ! ingeklapt) {
			for(int i = 1; i < tekstVakken.length; i++)
			{
				for(int j = 0; j < tekstVakken[i].length; j ++)
				{
					tekstVakken[i][j].setVisible(false);
					//tekstVakken[i][j].setPixelSize(-1, 0);
				}
			}
			setCurrentSize(breedte,  (int) Math.round(hoogtes.get(0).doubleValue()) );
			//this.hoogte = hoogtes.get(0).intValue();
			if(parent != null) parent.resize();
			
		} 
		else {
			double hoogte = hoogtes.get(0);
			
			for(int i = 1; i < tekstVakken.length; i++)
			{
				int h = (int) Math.round(hoogtes.get(i).doubleValue());
				if(h <= 0 ) {
					if (uitklapHoogtes != null && uitklapHoogtes.size() > i) h = (int) Math.round(uitklapHoogtes.get(i).doubleValue());
					else h = 100;
					hoogtes.set(i, Double.valueOf(h));
				}
				hoogte += hoogtes.get(i) + cellSpaceRow;
				mainPanel2.setPixelSize(-1, (int)hoogte - 2 * randDikte);
				for(int j = 0; j < tekstVakken[i].length; j ++)
				{
					tekstVakken[i][j].setVisible(true);
					tekstVakken[i][j].setPixelSize(-1, h);
					tekstVakken[i][j].hoogte = h;
					tekstVakken[i][j].resize();
					delta = tekstVakken[i][j].hoogte-h;
					if(delta>0)
					{
						hoogte += delta;
						h += delta;
					}
					//tekstVakken[i][j].setPixelSize(breedtes.get(j).intValue(), h);
				}
			}
			this.hoogte = (int)hoogte;
			
			setCurrentSize( breedte, this.hoogte);
			
			
			setPopupUsed();
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {

				@Override
				public void execute() {
					if (tekstVakken.length >= 2)
						tekstVakken[1][0].getElement().scrollIntoView();
				}
			});
		}
	}

	void setPopupUsed() {
		if(aftrekPopup && !popupUsed)
		{
			popupUsed = true;
			setAttempt();
			comRoot.setChanged(false);
		}
	}

	public HandlerRegistration addCBookEventListener(CBookEventListener listener) {
		return DWOplayer.clientfactory.getEventBus().addHandlerToSource(CBookEvent.TYPE, this, listener);
	}

	private void fireEvent(CBookEvent event) {
		DWOplayer.clientfactory.getEventBus().fireEventFromSource(event, this);
		comRoot.fireEvent(event);
	}
	
	private static final String ACTION_SETVISIBLE = "action.setVisible";
	private static final String ACTION_SETNOTVISIBLE = "action.setNotVisible";
	@Override
	public void acceptCBookEvent(CBookEvent event) {
		String command = event.getCommand();
		if(ACTION_SETVISIBLE.equals(command)) {
			setVisibility(true);
		}
		else if(ACTION_SETNOTVISIBLE.equals(command)) {
			setVisibility(false);
		}
		else if(TVP_SELECT.equals(command)) {
			if(!selected) {
				setSelected(true);
				//fireEvent(SELECT_EVENT);
			}
		}
		else if(TVP_DESELECT.equals(command)) {
			if(selected) {
				setSelected(false);
				//fireEvent(DESELECT_EVENT);
			}
		}
		else if(TVP_KLAPUIT.equals(command)) {
			if(TekstVakPanel.this.ingeklapt) {
				klapUitAction();
				klapUitButton.setDown(false);
			}
		}
		else if(TVP_KLAPIN.equals(command)) {
			if(!TekstVakPanel.this.ingeklapt) {
				klapUitAction();
				klapUitButton.setDown(true);
			}
		} else if ("action.setNotEditable".equals(command)) {
			seal(event);
		}
		
	}

	private void seal(CBookEvent event) {
		for (Object object : interactionViewObjects)
		{
			if (object instanceof CBookEventListener)
				((CBookEventListener) object).acceptCBookEvent(event);
		}
	}

	// visible (default) or hidden.
	private void setVisibility(boolean b) {
		visible = b;
		Element elem = getAsPanel().getElement();
		Style style = elem.getStyle();
		if(b)
		{	
			style.clearVisibility();
			double hoogteDouble = 0;
			for(int i = 0; i < hoogtes.size(); i++)
			{	hoogteDouble += hoogtes.get(i).doubleValue();
				hoogteDouble += cellSpaceRow;
			}
			if(hoogtes.size() > 0)
				hoogteDouble -= cellSpaceRow;
			hoogte = (int) Math.round(hoogteDouble);
			double breedteDouble = 0;
			for(int j = 0; j < breedtes.size(); j++)
			{	breedteDouble += breedtes.get(j).doubleValue();
				breedteDouble += cellSpaceColumn;
			}
			if(breedtes.size() > 0)
				breedteDouble -= cellSpaceColumn;
			breedte = (int) Math.round(breedteDouble);
			
			if (callOut)
			{
				hoogte = hoogte + callOutMargeY0 + callOutMargeY1;
				breedte = breedte + callOutMargeX0 + callOutMargeX1;
			}
			setCurrentSize( breedte, hoogte);
		}
		else
		{	style.setVisibility(Visibility.HIDDEN); 
			setCurrentSize( 0, 0);
		}
		resize();
//		if(parent!=null)
//			parent.resize();
	}
	private static final int LEFT = 0;
	private static final int RIGHT = 1;
	private static final int MIDDLE = 2;
	private static final int NONE = 3;
	
	private Image view1; 
	
	private void initieerKlapUitButton (boolean ingeklapt)
	{
		klapUitPanel = new LayoutPanel();
		Image view1, view2;
		final int inklapKnopPos = this.inklapKnopPos;
		if(knopImageView1 != null && knopImageView1.exists()) 
		{
			view1 = this.view1 = knopImageView1.getImage();
		} 
		else
		{
			view1 = this.view1 = new Image(DWOplayer.DWO_BUNDLE.klapuit1().getSafeUri());
//			if(checkUitklapVak)
//				this.view1goed = new Image(DWOplayer.DWO_BUNDLE.klapuit1().getSafeUri());
//			else 
//				this.view1goed = this.view1;
//			
			
//			if(checkUitklapVak && isKlapvakCorrect())
//				view1 = this.view1goed;
//			else
//				view1 = this.view1;		
		}
		if(knopImageView2 != null && knopImageView2.exists()) {
			view2 = knopImageView2.getImage();
		} else {
			view2 = new Image(DWOplayer.DWO_BUNDLE.klapuit2().getSafeUri());
		}
		
		//In deze implementatie ga ik er voorlopig vanuit dat view1 en view2 dezelfde maat hebben.
		final int breedtePanel = (checkUitklapVak && !isNoordhoff())?view1.getWidth() + 20:view1.getWidth();
		int hoogteKnop = view1.getHeight();
		
		klapUitPanel.setPixelSize(breedtePanel, hoogteKnop);
		
		final Image masterView = ingeklapt ? view2 : view1;
		goedKrulImage = new Image(FormuleHolder.FORMULE_BUNDLE.mw_vinkje_groen().getSafeUri());
		goedKrulImage.setVisible(false);
		masterView.getElement().getStyle().setProperty("verticalAlign", Math.round(hoogtes.get(0).doubleValue()) + "px");
		klapUitButton = new ToggleButton(view2, view1,
				new ClickHandler() {
			public void onClick(ClickEvent event) {
				if( ! TekstVakPanel.this.ingeklapt && checkUitklapVak )
				{
					klapUitButton.getDownFace().setImage(TekstVakPanel.this.view1);
					goedKrulImage.setVisible(isKlapvakCorrect());
				
					
				}
				klapUitAction();
				if(TekstVakPanel.this.ingeklapt)
					fireEvent(KLAPIN_EVENT);
				else
					fireEvent(KLAPUIT_EVENT);
				
				// t.b.v. updaten kleur bol
				HashMap<String, Object> state;
				state = getState();
				setState(state);

				// na setState() is het goede antwoord in FEWA gezet
				comRoot.setChanged(false);

			}});
		
		klapUitButton.setStylePrimaryName("inklapButton");
		//klapUitButton.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE)
		//masterView.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
		klapUitButton.setDown(ingeklapt);
		klapUitPanel.add(klapUitButton);
		klapUitPanel.setWidgetLeftRight(klapUitButton, 0, Style.Unit.PX, (checkUitklapVak && !isNoordhoff())?20:0, Style.Unit.PX);
		klapUitPanel.setWidgetTopBottom(klapUitButton, 0 , Style.Unit.PX, 0, Style.Unit.PX);
		if(checkUitklapVak)
		{
			klapUitPanel.add(goedKrulImage);
			if(isNoordhoff())
			{
				klapUitPanel.setWidgetRightWidth(goedKrulImage, 9, Style.Unit.PX, 16, Style.Unit.PX);
				klapUitPanel.setWidgetTopBottom(goedKrulImage, 9, Style.Unit.PX, 0, Style.Unit.PX);
			}
			else
			{
				klapUitPanel.setWidgetRightWidth(goedKrulImage, 0, Style.Unit.PX, 16, Style.Unit.PX);
				klapUitPanel.setWidgetTopBottom(goedKrulImage, 0, Style.Unit.PX, 0, Style.Unit.PX);
			}
			
		}
		
		int pos;
// Links of rechts kunnen we aan!
		switch(inklapKnopPos) {
		case LEFT: pos = 0; break;
		default:
		case MIDDLE:
		case RIGHT: pos = breedtes.size()-1; break;
		case NONE: {
				klapUitButton.setVisible(false);
				pos=0;
			}
		}
		
		final TekstVak layoutPanel = tekstVakken[0][pos];
		
		//final Widget widget = layoutPanel.getWidget(0);
		
// Wat met de positie van widgets
		LoadHandler handler = new LoadHandler() {

			@Override
			public void onLoad(LoadEvent event) {
				//int width = masterView.getWidth();
				//int width = klapUitPanel.getOffsetWidth();
				int height = masterView.getHeight();
				
				//setSizeUitklapButton(breedtePanel, hoogtes.get(0).intValue());
				//klapUitButton.setPixelSize(width, hoogtes.get(0).intValue());
				
				setPositionUitklapButton(layoutPanel, breedtePanel, height);
				
			}
		};
// preinitialize width/height?
		if (knopImageView1 != null && knopImageView1.getWidth() > 0 && knopImageView1.getHeight() > 0)
		{
			//layoutPanel.insert(klapUitButton,0);
			layoutPanel.insert(klapUitPanel, 0);
			int width = knopImageView1.getWidth();
// neem max.
			width = Math.max(width, knopImageView2.getWidth());
			
			int height = knopImageView1.getHeight();
			//setSizeUitklapButton(breedtePanel, Math.max(height, hoogtes.get(0).intValue()));
			
			//klapUitButton.setPixelSize(width, hoogtes.get(0).intValue());		
			//setPositionUitklapButton(layoutPanel, inklapKnopPos, width, height);
			setPositionUitklapButton(layoutPanel, breedtePanel, height);
		}
		else 
		if (masterView.getWidth() > 0)
		{
			//layoutPanel.insert(klapUitButton,0);
			layoutPanel.insert(klapUitPanel, 0);
			handler.onLoad(null);
		}
		else 
		{	masterView.addLoadHandler(handler);
			//layoutPanel.insert(klapUitButton,0);
			layoutPanel.insert(klapUitPanel, 0);
		}
		layoutPanel.resize();
	}


	private boolean isKlapvakCorrect() {
		
		boolean vakinhoudCorrect = true;
		//Vector v = parent.getOpdrachtObjects();
		//ArrayList<Object> opdrObjects = parent.getOpdrachtObjects();
		//ArrayList<Object> opdrObjects = parent.getOpdrachtObjects();
		comRoot.pause();
		for(int i = 0; i < interactionViewObjects.size(); i++)
		{
			Object object = interactionViewObjects.get(i);
			if(object instanceof InteractionView)
			{
				if(mode == OpdrNav.OEFENEN || mode == OpdrNav.OEFENEN_STRAFPUNTEN)
					((InteractionView) object).kijkNa();
				if(((InteractionView) object).isCorrect() == null)
					vakinhoudCorrect = false;
				else
					vakinhoudCorrect = vakinhoudCorrect && ((InteractionView) object).isCorrect().booleanValue();
			}
		}
		comRoot.unpause();
		return vakinhoudCorrect;
		
	}
	
	public void setPositionUitklapButton(TekstVak layoutPanel, int width, int height)
	{
		klapUitPanelWidth = width;
		klapUitPanelHeight = height;
		switch(inklapKnopPos) {
		case LEFT:
				//layoutPanel.setWidgetLeftRight(widget, width, Unit.PX, 0, Unit.PX);
				layoutPanel.setWidgetLeftWidth(klapUitPanel, 1, Unit.PX, width, Unit.PX);
				layoutPanel.setWidgetTopHeight(klapUitPanel, (layoutPanel.hoogte-height)/2, Unit.PX, height, Unit.PX);
				layoutPanel.zetUitklapKnopLinks(width);
				break;
		case MIDDLE: // FIXME werkt nog van geen meter!
			layoutPanel.setWidgetLeftWidth(klapUitPanel, layoutPanel.getRegelBreedte(), Unit.PX, width, Unit.PX);
			layoutPanel.setWidgetTopHeight(klapUitPanel, (layoutPanel.hoogte-height)/2, Unit.PX, height, Unit.PX);
			
			break;
		case RIGHT:
		default:
			//layoutPanel.setWidgetLeftRight(widget, 0, Unit.PX, width, Unit.PX);
			layoutPanel.setWidgetRightWidth(klapUitPanel, 1, Unit.PX, width, Unit.PX);
			layoutPanel.setWidgetTopHeight(klapUitPanel, (layoutPanel.hoogte-height)/2, Unit.PX, height, Unit.PX);
		}
	}
	
	public void setPositionUitklapButton(TekstVak layoutPanel)
	{
		if(klapUitPanel == null || klapUitPanel.getParent() != layoutPanel)
			return;
			
		switch(inklapKnopPos) {
		case LEFT:
				//layoutPanel.setWidgetLeftRight(widget, width, Unit.PX, 0, Unit.PX);
				layoutPanel.setWidgetLeftWidth(klapUitPanel, 1, Unit.PX, klapUitPanelWidth, Unit.PX);
				layoutPanel.setWidgetTopHeight(klapUitPanel, (layoutPanel.hoogte-klapUitPanelHeight)/2, Unit.PX, klapUitPanelHeight, Unit.PX);
				layoutPanel.zetUitklapKnopLinks(klapUitPanelWidth);
				break;
		case MIDDLE: // FIXME werkt nog van geen meter!
			layoutPanel.setWidgetLeftWidth(klapUitPanel, layoutPanel.getRegelBreedte(), Unit.PX, klapUitPanelWidth, Unit.PX);
			layoutPanel.setWidgetTopHeight(klapUitPanel, (layoutPanel.hoogte-klapUitPanelHeight)/2, Unit.PX, klapUitPanelHeight, Unit.PX);
			
			break;
		case RIGHT:
		default:
			//layoutPanel.setWidgetLeftRight(widget, 0, Unit.PX, width, Unit.PX);
			layoutPanel.setWidgetRightWidth(klapUitPanel, 1, Unit.PX, klapUitPanelWidth, Unit.PX);
			layoutPanel.setWidgetTopHeight(klapUitPanel, (layoutPanel.hoogte-klapUitPanelHeight)/2, Unit.PX, klapUitPanelHeight, Unit.PX);
		}
	}
	
	/*
	public void setSizeUitklapButton(int breedte, int hoogte)
	{
		klapUitPanel.setPixelSize(breedte, hoogte);
		
	}
	*/

	/**
	 * Kijk de componenten op het popup-tekstvak na
	 * als de popup wordt geopend. Als een component
	 * isVeranderdNaNakijken moet geen vinkje worden
	 * getoond.
	 */
	public void kijkNaOnShow() 
	{
		comRoot.pause();
		for (Object object : interactionViewObjects)
		{
			if (object instanceof InteractionView)
			{
				HashMap<String, Object> state = ((InteractionView) object).getState();
				ObjectMap map = JSONUtilities.wrapMap(state);
				boolean isVeranderdNaNakijken = false;
				if (map.containsKey("isVeranderdNaNakijken"))
				{
					isVeranderdNaNakijken = map.getBoolean("isVeranderdNaNakijken");
				}
				if (!isVeranderdNaNakijken)
				{
					// alleen nakijken en feedback tonen als het antwoord niet is veranderd na nakijken
					((InteractionView) object).kijkNa();
				}
			}
		}
		comRoot.unpause();
	}

	@Override
	public void kijkNa()
	{
		comRoot.pause();
		for (Object object : interactionViewObjects)
		{
			if (object instanceof InteractionView)
				((InteractionView) object).kijkNa();
		}
		comRoot.unpause();
	}

	@Override
	public int getAsHoogte()
	{
		int ashoogte = tekstVakken[0][0].getAsHoogte();
		for (int j = 1; j < breedtes.size(); j++)
		{
			if (tekstVakken[0][j].getAsHoogte() > ashoogte)
				ashoogte = tekstVakken[0][j].getAsHoogte();
		}

		return facade.wrapAsHoogte(ashoogte);
	}

	@Override
	public int getHeight()
	{
		return facade.wrapHeight(hoogte);
	}

	@Override
	public int getWidth()
	{
		return facade.wrapWidth(breedte);
	}
	
	public void zetVolledigeBreedte(int breedte){
	}

	@Override
	public void setAsHoogte(int ashoogte) {
		
		for(int i = 0; i < breedtes.size(); i++)
		{
			tekstVakken[0][i].setAshoogte(ashoogte);
		}
	}
	private Map<InteractionView,Connector> xWidgetMap = new HashMap<InteractionView, Connector>();
	public Map<InteractionView, Connector> getXWidgetMap() {
		return xWidgetMap;
	}
	
	public static boolean isNoordhoff()
	{
		String dependentName = DWOplayer.PARAMETERS.keyboardStyle();
		return "noordhoff".equals(dependentName);
	}
	
	DWOLogger dwologger;
	private boolean editable = true;
	
	private void setAttempt() {
		if(dwologger != null) {
			Map<String, Object> parameters = buildLogParameters();
			dwologger.log(parameters);
		}
	}

	private Map<String, Object> buildLogParameters() {
		Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put("response", fi.wiskopdr.text.Text.constants.jaTekst());
		parameters.put("score", Collections.singletonMap("raw", -this.puntenAftrekPopup));
		return parameters;
	}
	
	public void setCallOut(boolean b)
	{
		if (b && tekstVakken.length == 1 && tekstVakken[0].length == 1)
			callOut = true;
		else
			callOut = false;
	}

	@Override
	public void onShow() 
	{
		boolean nagekeken = this.nagekeken; // voor eindtoets even opslaan, want setState() zet hem weer op false.
		
		HashMap<String, Object> state = facade.getPopupState();
		if (state != null) 
			setState(state);
		else 
			setStateNull();
		
		if (nagekeken)
		{
			zetNagekeken(nagekeken);
			if (nagekeken)
				kijkNaOnShow();
		}
		setPopupUsed();
	}

	@Override
	public void onHide() {
		facade.setPopupState(getState());
	}

	void setEditable(boolean editable) {
		this.editable = editable;	
	}
	
}
