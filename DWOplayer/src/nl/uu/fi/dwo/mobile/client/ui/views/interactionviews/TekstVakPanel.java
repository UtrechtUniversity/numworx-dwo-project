package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleRegel;
import nl.uu.fi.dwo.ideas.client.AbstractRule;
import nl.uu.fi.dwo.ideas.client.IdeasIF;
import nl.uu.fi.dwo.ideas.client.RuleCallback;
import nl.uu.fi.dwo.ideas.client.RuleIF;
import nl.uu.fi.dwo.interaction.client.FacetAware;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.StateLess;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.sco.ShareFacade;
import nl.uu.fi.dwo.mobile.client.ui.Actions;
import nl.uu.fi.dwo.mobile.client.ui.ActivityInterface;
import nl.uu.fi.dwo.mobile.client.ui.HasResize;
import nl.uu.fi.dwo.mobile.client.ui.OpdrNav;
import nl.uu.fi.dwo.mobile.client.ui.views.AnchorContext;
import nl.uu.fi.dwo.mobile.client.ui.views.ImageView;
import nl.uu.fi.dwo.mobile.client.ui.views.XMLView;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.reviewvak.ReviewActivity;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.samengesteldestappen.SamengesteldeStappenPanel;
import nl.uu.fi.dwo.mobile.utils.Connector;
import nl.uu.fi.dwo.mobile.utils.LogBuilder;
import nl.uu.fi.dwo.mobile.utils.Logging;
import nl.uu.fi.dwo.mobile.utils.PopupFacade;
import nl.uu.fi.dwo.mobile.utils.PopupFacade.PopupListener;
import nl.uu.fi.dwo.mobile.utils.TekstBuffer;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Cursor;
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
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.vaadin.pointerevents.client.PointerCancelEvent;
import com.vaadin.pointerevents.client.PointerCancelHandler;
import com.vaadin.pointerevents.client.PointerDownEvent;
import com.vaadin.pointerevents.client.PointerDownHandler;
import com.vaadin.pointerevents.client.PointerMoveEvent;
import com.vaadin.pointerevents.client.PointerMoveHandler;
import com.vaadin.pointerevents.client.PointerUpEvent;
import com.vaadin.pointerevents.client.PointerUpHandler;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;
import fi.wiskopdr.AntwoordVakChecker;
import fi.wiskopdr.AntwoordVergelijkingVakChecker;
import fi.wiskopdr.AntwoordFormuleVakChecker;
import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.expressies.Aftrekking;
import fi.wiskopdr.expressies.BasisExpressie;
import fi.wiskopdr.expressies.Expressie;
import fi.wiskopdr.expressies.Optelling;
import fi.wiskopdr.expressies.VergelijkingMeerv;
import fi.wiskopdr.expressies.Vergelijking;

public class TekstVakPanel extends Composite implements InteractionViewWithMisconceptions, FacetAware, PopupListener, CBookEventListener, RequiresResize
{
	private final static Logger logger = Logger.getLogger("TekstVakPanel");
	
	private final boolean RESPONSIVE = DWOplayer.RESPONSIVE;
    private static final Logger LOG = logger;
	public static final String TVP_KLAPUIT = "action.unfold";
	public static final String TVP_KLAPIN = "action.fold";
	public static final String TVP_SELECT = "action.select";
	public static final String TVP_DESELECT = "action.deselect";
	public static final String TVP_CLICK = "action.click";
	public static final String TVP_POPUP = "action.popup";
	
	private static final CBookEvent KLAPUIT_EVENT = new CBookEvent(TVP_KLAPUIT); 
	private static final CBookEvent KLAPIN_EVENT = new CBookEvent(TVP_KLAPIN); 
	private static final CBookEvent SELECT_EVENT = new CBookEvent(TVP_SELECT); 
	private static final CBookEvent DESELECT_EVENT = new CBookEvent(TVP_DESELECT);
	private static final CBookEvent CLICK_EVENT = new CBookEvent(TVP_CLICK);
	private static final CBookEvent POPUP_EVENT = new CBookEvent(TVP_POPUP);

	/**
	 * Forget the style in the styling.
	 * Helaas nog niet goed. 
	 * Geen style.css in course description en studentmodel description.
	 * Alleen bij sco's launchdata
	 */
	public static final boolean FORGET_STYLES = true; 
	
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

	class RuleRecovery implements Function<Promise<?>, RuleIF> {
		@Override
		public RuleIF apply(Promise<?> p) {
			final Throwable t = p.getFailure();
			GWT.log("failure diagnose" , t);
			return new AbstractRule() {

				@Override
				public String getId() {
					return RuleIF.EXCEPTION;
				}

				@Override
				public String getExpr() {
					return t.toString();
				}

				@Override
				public boolean isException() {
					return true;
				}
			 };
		}
	}

	class DeferRuleCallback implements RuleCallback {
		private final Deferred<RuleIF> defer;

		DeferRuleCallback(Deferred<RuleIF> defer) {
			this.defer = defer;
		}

		@Override
		public void onSuccess(RuleIF result) {
			defer.resolve(result);
		}

		@Override
		public void onFailure(Throwable caught) {
			defer.fail(caught);
		}
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
	private int orgBreedte = 600;
	private int hoogte = 250;
	private boolean volledigeBreedte=false;
	/**
	 * Breedte onthouden voor invisible callout-tekstvak
	 */
	private int breedte_oud = 600;
	/**
	 * Hoogte onthouden voor invisible callout-tekstvak
	 */
	private int hoogte_oud = 250;
	//ObjectMap launchState;
	private ObjectMap instellingen;
	private LayoutPanel mainPanel2 = null;
	private Grid mainPanel = null;
	private LayoutPanel callOutPanel = null;
	private Canvas callOutCanvas = null;
	//private LayoutPanel randPanel = null;
	//private LayoutPanel[][] tekstVakken = null;
	protected TekstVak[][] tekstVakken = null;
	//private FlowPanel[][] tekstVakken = null;
	String[] randomVarNamen = null;
	HashMap<String, Number> randomVarWaarden = null;
	
	TekstVak parent = null;
	private HasResize parentStappen = null;
	private int mode = OpdrNav.OEFENEN;
	
	protected ArrayList<Object> interactionViewObjects = new ArrayList<Object>();

	List<Double> breedtes = null;
	protected List<Double> hoogtes = null;
	List<Double> minHoogtes = null;
	List<Double> uitklapHoogtes = null;
	int cellSpaceColumn = 0;
	int cellSpaceRow = 0;
	int cellMarge = 0;
	int bovenMarge = 0;
	int ronding = 0;
	int hoek = 0;
	protected int kolom = -1;
	CssColor bgColor = CssColor.make(255, 255, 255);
	CssColor fgColor = CssColor.make(0, 0, 0);
	CssColor randColor = CssColor.make(150, 150, 150);
	CssColor selectieColor = CssColor.make(255, 128, 0);
	CssColor grijs = CssColor.make(128, 128, 128);
	int randDikte, randDikte0;
	private boolean popup;
	//private boolean tableBorders;
	private LayoutPanel[] horizontalBorders;
	private LayoutPanel[] verticalBorders;
	//private Canvas tabelRandenCanvas;
	private boolean centerV = false;
	
	private boolean sleepdoel = false;
	private boolean sleepHandle = false;
	private boolean inactive = false;
	private Image crosshair = null;
	private int sleepdoelMarge = 10;
	private boolean sleepSnap = false;
	private boolean pasAanH = false;
	private boolean pasAanB = false;
	private boolean vulHoogte = false;
	
	private boolean selectable;
	private boolean sleepbaar;
	private boolean sleepveld;
	private boolean draaibaar;
	private boolean selected;
	private String checkExpressieString = "$f1@";
	private boolean defaultBijNull;
	private int ipId = 0;
	private int interlinie = 0;
	private boolean colorSelection;
	private boolean zwevend;
	
	private Point[] doelPosities;
	private TekstVakPanel[] sleepObjecten;
	private TekstVakPanel[] sleepDoelen;

	private boolean relocate = false;
	private int startSleepX;
	private int startSleepY;
	
	private int locationX, locationY;
	private int startX, startY;
	private double startHoek;
	private double draaihoek;
	
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
	private Image goedKrulImage, foutKruisImage, goedKrulHalfImage, feedbackImage;
	private ToggleButton klapUitButton;
	private LayoutPanel klapUitPanel;
	private int klapUitPanelWidth, klapUitPanelHeight;
	private TekstVakContext container;
	private Object queuedObject;
	
	private MouseHandler mouseHandler;
	private TouchHandler touchHandler;
	private PointerHandler pointerHandler;
	
	private static boolean fontOvererving;
	private boolean anderFont = false;
	
	private boolean isLink = false;
	private ObjectList linkUrls;
	private AnchorContext anchorContext;
	
	
	private FlowPanel klikPanel;
	private boolean aftrekPopup;
	private boolean popupUsed;
	private int puntenAftrekPopup;
	private boolean logOption;
	
	private boolean visible = true, layerVisible = true;
	private boolean zichtbaarNaNakijken;
	private boolean nagekeken;
	private boolean bgColorZichtbaar;
	private int layerNr;
	
	private String styleString;
	private boolean doorzochtDoorTab = false;
	private boolean randomPositioned = false; //Bij randomized meerkeuzeopdrachten kan de tabsequence anders worden
	
	private int stapNr = 0;
	private List<Object> stappen = null;
	
	private boolean ideasStatistiek = false;
	private boolean backButton = false;
	private boolean hintButton = false;
	private List<Tupel> lastAnswers = null;
	private ArrayList<Tupel> initialStatistiekState = null;
	private int goedHalfFoutStatistiek = AntwoordVakChecker.GEEN;
	private String feedbackStatistiek = "";
	private TekstVak feedbackPanel = null;
	int feedbackPanelHeight = 0;
	private boolean responsive;
	private int responsiveToggleWidth = 800;
	private int responsiveMinWidth = 400;
	private int responsiveMaxWidth = 980;
	private int responsiveConstant = 0;
	private double responsiveFactor = 0;
	private String logID;
	private boolean fullScreenOption;
	private FullScreenButton fsBtn;
	private final ActivityInterface activity;
	
	
	static CssColor getColor(ObjectMap map, String key, int r, int g, int b) {
		ObjectMap colorMap = map != null && map.containsKey(key) ? map.getObjectMap(key) : null ;
		if(colorMap != null) {
			r = colorMap.getInt("red");
			g = colorMap.getInt("green");
			b = colorMap.getInt("blue");
		}
		return CssColor.make(r, g, b);
	}
	
	public boolean hasStyle() {
		return styleString != null;
	}
	
	//Hiermee maak je het basispanel dat alle componenten van een pagina bevat.
	public TekstVakPanel(ActivityInterface a, int breedte, int hoogte, String[] randomVarNamen, HashMap<String, Number> randomVarWaarden)
	{
		activity = a;
		this.randomVarNamen = randomVarNamen;
		this.randomVarWaarden = randomVarWaarden;
		this.volledigeBreedte = true;
		
		//niet nodig waarschijnlijk
		facade = new PopupFacade((ObjectMap)null, a);
		facade.setPopupListener(this);
		mainPanel2 = new LayoutPanel(); 
		mainPanel2.setStylePrimaryName("tekstvakpanel");
		
		setCurrentSize(breedte, hoogte);
		pasAanH = true;
		
		mainPanel = new Grid(1, 1);
		mainPanel.getElement().getStyle().setProperty("borderSpacing", cellSpaceColumn + "px " + cellSpaceRow + "px");
		mainPanel.getElement().getStyle().setProperty("margin", (-cellSpaceRow) + "px " + (-cellSpaceColumn) + "px");
		
		
		tekstVakken = new TekstVak[1][1];	
		tekstVakken[0][0] = createTekstVak(0, 0);
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
		
		initWidget();
	}

//	public TekstVakPanel(ActivityComponent a, HashMap<String, Object> hh, String[] randomVarNamen, HashMap<String,Number> randomVarWaarden, AnchorContext context)
//	{
//		this(a, hh, randomVarNamen, randomVarWaarden);
//		this.anchorContext = context;
//	}
	
	String getLogID() { 
		if (logID != null)
			return logID  +"/" + comRoot.getUUID();
		else
			return comRoot.getUUID();
	}
	
	public TekstVakPanel(ActivityInterface a, HashMap<String, Object> hh, String[] randomVarNamen, HashMap<String, Number> randomVarWaarden, int volleBreedte)
	{
		activity = a;
		this.randomVarNamen = randomVarNamen;
		this.randomVarWaarden = randomVarWaarden;
		ObjectMap h = JSONUtilities.wrapMap(hh);
		facade = new PopupFacade(h, activity);
		facade.setPopupListener(this);
		ObjectMap launchState = null;
		if (h != null && h.containsKey("breedte") )
			breedte = h.getInt("breedte");
		
		if (h != null && h.containsKey("hoogte"))
			hoogte = h.getInt("hoogte");
		
		if (h != null && h.containsKey("volledigeBreedte"))
			volledigeBreedte = h.getBoolean("volledigeBreedte");
		
		if (volledigeBreedte && volleBreedte > 0)
			breedte = volleBreedte;
		
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
			if(!breedtes.isEmpty() && Math.round(breedtes.get(0).doubleValue()) > breedte && !volledigeBreedte)
				breedte = (int) Math.round(breedtes.get(0).doubleValue());
// XXX weet nog niet in andere gevallen	
			if (volledigeBreedte && breedtes.size() == 1) {
				breedtes.set(0, Double.valueOf(breedte));
			}
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
			LogBuilder builder = activity.logBuilder()
				.setLogOption(true)
				.setLogID(launchState.getString("logID"))
				.setMaxScore(0)
				.setLogIDLabel(launchState.getString("logIDLabel"))
				.setClassName("fi.wiskopdr.TekstVakPanel");
			dwologger = builder.build();
			logID = builder.getLogID();
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
			
			draaihoek = (double)hoek;
			
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
			
			if( FORGET_STYLES) {
				style = null;
				styleString = null;
			}
			
			
		}
		else 
		{
			styleString = null;
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
			
			draaihoek = (double)hoek;
			
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
		draaibaar = launchState.getBoolean("draaibaar", draaibaar);
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
		if (launchState.containsKey("layerNr")) {
			layerNr = launchState.getInt("layerNr");
		} else
			layerNr = 0;
		
		if (launchState.containsKey("responsive"))
			responsive = launchState.getBoolean("responsive");
		if (responsive && launchState.containsKey("responsiveToggleWidth"))
			responsiveToggleWidth = launchState.getInt("responsiveToggleWidth");
		if (responsive && launchState.containsKey("responsiveMaxWidth"))
			responsiveMaxWidth = launchState.getInt("responsiveMaxWidth");
		if (responsive && launchState.containsKey("responsiveMinWidth"))
			responsiveMinWidth = launchState.getInt("responsiveMinWidth");
		if (responsive && launchState.containsKey("responsiveFactor"))
			responsiveFactor = launchState.getDouble("responsiveFactor");
		if (responsive && launchState.containsKey("responsiveConstant"))
			responsiveConstant = launchState.getInt("responsiveConstant");
		
		visible = launchState.getBoolean("visible", true);
		if(!visible)
		{	
			hoogte_oud = hoogte;
			breedte_oud = breedte;
			hoogte = 0;
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
			knopImageView1 = new ImageView(launchState.getString("knopImageString1"), activity);
		if (launchState.containsKey("knopImageString2"))
			knopImageView2 = new ImageView(launchState.getString("knopImageString2"), activity);
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
		if(launchState.containsKey("ideasStatistiek"))
			ideasStatistiek = launchState.getBoolean("ideasStatistiek");
		if(ideasStatistiek)
		{ 	goedKrulImage = new Image(FormuleHolder.FORMULE_BUNDLE.mw_vinkje_groen().getSafeUri());
			goedKrulHalfImage = new Image(FormuleHolder.FORMULE_BUNDLE.mw_vinkje_geel().getSafeUri());
			foutKruisImage = new Image(FormuleHolder.FORMULE_BUNDLE.mw_kruisje_rood().getSafeUri());
		}
		if(launchState.containsKey("backButton"))
			backButton = launchState.getBoolean("backButton");
		if(launchState.containsKey("hintButton"))
			hintButton = launchState.getBoolean("hintButton");
		fullScreenOption = launchState.getBoolean("fullScreenOption", fullScreenOption);
		
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
		
		randDikte0 = randDikte = randZichtbaar ? randDikte : 0; 

		mainPanel2 = new LayoutPanel(); 
		if(!callOut)
			mainPanel2.setStylePrimaryName("tekstvakpanel");
		if (style != null) {
			mainPanel2.addStyleName(styleString + "-main2");
		}
		
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
		
		pointerHandler = new PointerHandler();
		mainPanel2.addDomHandler((PointerMoveHandler)pointerHandler, PointerMoveEvent.getType()); 
		mainPanel2.addDomHandler((PointerUpHandler)pointerHandler, PointerUpEvent.getType()); 
		mainPanel2.addDomHandler((PointerDownHandler)pointerHandler, PointerDownEvent.getType()); 

		if (isLink) {
			mainPanel2.getElement().getStyle().setCursor(Cursor.POINTER);
		}
		
		
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
			if (style != null) {
				horizontalBorders[i].addStyleName(styleString + "-border");
			}
			horizontalBorders[i].getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
			horizontalBorders[i].getElement().getStyle().setBorderColor(randColor.toString());
			mainPanel2.add(horizontalBorders[i]);
			if(!tableBorders)
				horizontalBorders[i].setVisible(false);
		}
		for(int i = 0; i < breedtes.size() - 1; i++)
		{
			verticalBorders[i] = new LayoutPanel();
			if (style != null) {
				verticalBorders[i].addStyleName(styleString + "-border");
			}
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
			callOutCanvas = Canvas.createIfSupported();
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
		
		mainPanel = new Grid(hoogtes.size(), breedtes.size());
		if (style != null) {
			mainPanel.addStyleName(styleString + "-main");
		}  else {
			mainPanel.getElement().getStyle().setProperty("borderSpacing", "" + cellSpaceColumn + "px " + cellSpaceRow + "px");
			mainPanel.getElement().getStyle().setProperty("margin", "" + (-cellSpaceRow - randDikte) + "px " + (-cellSpaceColumn - randDikte) + "px");
		}
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
				if( tekstVakBreedte < 0) tekstVakBreedte = 0;
				
				tekstVakken[i][j] = createTekstVak(i, j);
				if (style != null) {
					tekstVakken[i][j].addStyleName(styleString + "-tekstregel");
				} else {
					tekstVakken[i][j].setColor(fgColor);
				}
				int th = (int) (Math.round(hoogtes.get(i).doubleValue()));
				tekstVakken[i][j].setSize((int) (Math.round(breedtes.get(j).doubleValue())), th);
				tekstVakken[i][j].setVisible(th>0);
				//tekstVakken[i][j].setPixelSize(breedtes.get(j).intValue(), hoogtes.get(i).intValue());
				tekstVakken[i][j].setFontStyle(font_style);
				tekstVakken[i][j].setFontName(font_name);
				tekstVakken[i][j].setFontSize(font_size);
				tekstVakken[i][j].setRonding(ronding);
				tekstVakken[i][j].setCentering(centerH, centerV);
				tekstVakken[i][j].setPasHoogteBreedteAan(pasAanH, pasAanB);
				tekstVakken[i][j].setTekstVakBreedte(tekstVakBreedte);
				tekstVakken[i][j].setMarges(bovenMarge, cellMarge);
				tekstVakken[i][j].setInterlinie(interlinie);
				
				mainPanel.setWidget(i, j, tekstVakken[i][j]);
			}
		}
		
		
		
		mainPanel2.add(mainPanel);
		mainPanel2.setWidgetLeftRight(mainPanel, 0, Style.Unit.PX, 0, Style.Unit.PX);
		mainPanel2.setWidgetTopBottom(mainPanel, 0, Style.Unit.PX, 0, Style.Unit.PX);
		
		if(sleepbaar && sleepHandle)
		{	crosshair = new Image(DWOplayer.DWO_BUNDLE.crosshair().getSafeUri());
			crosshair.getElement().getStyle().setProperty("WebkitUserDrag", "none");
			mainPanel2.add(crosshair);
			mainPanel2.setWidgetLeftWidth(crosshair, 0, Style.Unit.PX, 20, Style.Unit.PX);
			mainPanel2.setWidgetTopHeight(crosshair, 0, Style.Unit.PX, 20, Style.Unit.PX);
			
			
		}
		if(sleepbaar || draaibaar) {
			mainPanel2.getElement().getStyle().setProperty("touchAction", "none");
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
		if (fullScreenOption) {
		  fsBtn = new FullScreenButton("open");
		  mainPanel2.add(fsBtn);
		  mainPanel2.setWidgetRightWidth(fsBtn, 17, Unit.PX, fsBtn.getWidth(), Unit.PX);
		  mainPanel2.setWidgetTopHeight(fsBtn, 0, Unit.PX, fsBtn.getHeight(), Unit.PX);
		  fsBtn.addButtonListener(this::zoomunzoomAction);
		}
		
		initWidget();
	}

	protected TekstVak createTekstVak(int i, int j) {
		return new TekstVak(this, i, j);
	}

	private void zoomAction() {
	  if(!fullScreenOption || "close".equals(fsBtn.getText())) return;
      orgBreedte = breedte;
      if (responsive) {
        zoomKolom = 0;
        zoomRij = 0;
      }
      int width = getWindowWidth();
      parent.zoom1(this, width);
      fsBtn.setText("close");
	}
	
	private void zoomunzoomAction(Object btn) {
	  if ("open".equals(fsBtn.getText()))
	    zoomAction();
	  else
	    unzoomAction();
	}
	
	private void unzoomAction() {
      if(!fullScreenOption|| "open".equals(fsBtn.getText())) return;
      if (responsive) {
        zoomKolom = zoomRij = null;
      }
      zetVolledigeBreedte1(orgBreedte);
      parent.unzoom(this);
      
      fsBtn.setText("open");
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
	 * Draw the call out canvas with the callout drawing.
	 * 
	 * @param callOutCanvas
	 */
	private void drawCallOutCanvas(Canvas callOutCanvas)
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
		if (hoogtes.size() == 1) // hoogte en breedte kan hier 0 zijn als de callout invisible is
		{
			if (hoogte <= 0)
				hoogtes.set(0, (double) hoogte_oud - callOutMargeY0 - callOutMargeY1 - 2 * randDikte);
			else
				hoogtes.set(0, (double) hoogte - callOutMargeY0 - callOutMargeY1 - 2 * randDikte);
		}
		if (breedtes.size() == 1)
		{
			if (breedte <= 0)
				breedtes.set(0, (double) breedte_oud - callOutMargeX0 - callOutMargeX1 - 2 * randDikte);
			else
				breedtes.set(0, (double) breedte - callOutMargeX0 - callOutMargeX1 - 2 * randDikte);
		}
	}
		
	public TekstVakPanel(ActivityInterface a, int breedte, int hoogte, String[] randomVarNamen,
			HashMap randomVarWaarden, AnchorContext anchorContext) {
		this(a, breedte, hoogte, randomVarNamen, randomVarWaarden);
		this.anchorContext = anchorContext;
	}

	public TekstVakPanel(ActivityInterface a, HashMap<String, Object> hh,
			String[] randomVarNamen, HashMap<String, Number> randomVarWaarden, AnchorContext anchorContext, int vollebreedte) {
		this(a, hh, randomVarNamen, randomVarWaarden, vollebreedte);
		this.anchorContext = anchorContext;
		ObjectMap launch = JSONUtilities.wrapMap(hh);
		launch = launch.getObjectMap("interactiePanelLaunchState");
		if (launch.getBoolean("isAnchor", false)) {
			Element element = getElement();
			String anchor = launch.getString("anchor");
			anchorContext.addElement(anchor, element);
		}
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
		if (layerNr > 0 && instellingen.containsKey("layerVisible")) {
			boolean[] layers = instellingen.getBooleanArray("layerVisible");
			if (layerNr-1 < layers.length)
			{	layerVisible = layers[layerNr-1];
				if (!layerVisible && visible) {
					hoogte_oud = hoogte;
					breedte_oud = breedte;
					hoogte = 0;
					breedte = 0;
					setCurrentSize(0, 0);
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

		if (w > 30000) {
			GWT.log("veels te groot " + w);
		}
		
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
	}
	private ReviewActivity reviewActivity;

	private LessonMode lessonmode;
	
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
		ActivityInterface a = activity;
		if (zichtbaarNaNakijken && a.isReview() && maxScore(interactiePanelLaunchState)) {
			LOG.warning("Hier komen rubrics");
			a = reviewActivity = new ReviewActivity(a,this, !opdrachtGegevens.isEmpty(), JSONUtilities.wrapMap(interactiePanelLaunchState));
		}
		TekstBuffer tb = new TekstBuffer(a, randomVarNamen, randomVarWaarden, anchorContext);
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
				tekstVakken[i][j].zetOpdrachtObjects(opdrachtObjects, opdrachtObjects); // opdrachtObjects wordt al gebruikt next line
								
				//eerst zorgen dat alle opdrachtObjects goed geïnitialiseerd zijn, daarna zet je ze netjes in het tekstvak neer.
				//voor goede initialisatie van sleepopdr etc is het wel nodig dat de opdrachtobjects al aan het tekstvak zijn toegevoegd.
				aantalVakken = initialiseerObjects(opdrachtObjects, opdrachtGegevens, i, j, aantalVakken);
				
				//setObjects(opdrachtObjects, i, j);
				tekstVakken[i][j].setObjects(opdrachtObjects);
			}
			
		}
		Connector.calculateSubscriptions(xWidgetMap.values());
		//Connector c = find(this);
		for(Connector c : xWidgetMap.values())
		{
			if(c != null && c.subscriptions != null && c.subscriptions.containsKey("text.content"))
			{	
				if(c.v instanceof TekstVakPanel)
				{
					((TekstVakPanel) c.v).initialiseerStappen();
				}
			}
		}
		
		
		
	
		if (selectable || (sleepbaar && !sleepHandle)) //
			zetKlikPanel();

		if(inklapbaar)
		{	initieerKlapUitButton(ingeklapt);
			//hierin gebeurt ook resize();
		}
//		else
//			resize();
		
		//resize gebeurt in setVisibility.
		setVisibility(visible);
		if(ideasStatistiek)
			initialiseIdeasStatistiek();
	}
	
	private boolean maxScore(HashMap<String, Object> interactiePanelLaunchState) {
		Object max = interactiePanelLaunchState.get("scoreMax");
		return !(Integer.valueOf(0).equals(max));
	}

	public void initialiseIdeasStatistiek(ArrayList<Tupel> initialState)
	{
		//Make lists out of separate list elements
		ListIterator<Tupel> initIt = initialState.listIterator();
		ArrayList<Tupel> allListElements = new ArrayList<Tupel>();
		while(initIt.hasNext())
		{
			Tupel t = initIt.next();
			if(t.name.contains("."))
			{
				allListElements.add(t);
				initIt.remove();
			}
		}
		while(!allListElements.isEmpty())
		{
			Tupel firstTupel = allListElements.get(0);
			String firstName = firstTupel.name.substring(0, firstTupel.name.indexOf("."));
			ArrayList<Tupel> singleListElements = new ArrayList<Tupel>();
			ListIterator<Tupel> it = allListElements.listIterator();
			while(it.hasNext())
			{
				Tupel t = it.next();
				String name = t.name.substring(0, t.name.indexOf("."));
				if(name.equals(firstName))
				{
					singleListElements.add(t);
					it.remove();
				}
			}
			singleListElements.sort(new NameComparator());
			String sep = ";";
			String bracketL = "[";
			String bracketR = "]";
					
			String antwoord = "";
			if(firstTupel.name.substring(firstTupel.name.indexOf(".") + 1).contains(".")) //two dimensional list
			{
				antwoord = bracketL + bracketL;
				ListIterator<Tupel> singleIt = singleListElements.listIterator();
				while(singleIt.hasNext())
				{
					Tupel t = singleIt.next();
					String number = t.name.substring(t.name.indexOf(".") + 1);
					if(number.equals("1.1"))
						antwoord = antwoord + t.value;
					else if(number.substring(number.indexOf(".") + 1).equals("1"))
						antwoord = antwoord + bracketR + sep + bracketL + t.value;
					else
						antwoord = antwoord + sep + t.value;
				}
				antwoord = antwoord + bracketR + bracketR;
			}
			else //one dimensional list
			{
				antwoord = bracketL;
				ListIterator<Tupel> singleIt = singleListElements.listIterator();
				while(singleIt.hasNext())
				{
					Tupel t = singleIt.next();
					String number = t.name.substring(t.name.indexOf(".") + 1);
					if(number.equals("1"))
						antwoord = antwoord + t.value;
					else
						antwoord = antwoord + sep + t.value;
				}
				antwoord = antwoord + bracketR;
			}
			Tupel newTupel = new Tupel(firstTupel.name.substring(0, firstTupel.name.indexOf(".")), firstTupel.type, antwoord);
			initialState.add(newTupel);
		}
		
		initialStatistiekState = initialState;
	}
	
	private void initialiseIdeasStatistiek()
	{
		ArrayList<Tupel> initialState = new ArrayList<Tupel>();
		//1. Find the big AntwoordKeuzeVak containing all steps
		AntwoordKeuzeVak akv = null;
		//isIdeasStatistiek is checked for the box containing all steps.
		//The AntwoordKeuzeVak for choosing the steps is situated in the parent of this box.
		for(int i = 0; i < interactionViewObjects.size(); i++)
		{
			if(interactionViewObjects.get(i) instanceof AntwoordKeuzeVak)
			{	akv = (AntwoordKeuzeVak) interactionViewObjects.get(i);
				break;
			}
		}
		
		if(akv == null)
			return;
		
		for(int i = 0; i < akv.getAnswerModels().length; i++)
		{
			if(akv.getAnswerModels()[i].containsKey("feedback"))
			{
				try{
					ObjectMap feedbackMap = akv.getAnswerModels()[i].getObjectMap("feedback");
//					
//					TekstVakPanel tvp = new TekstVakPanel((HashMap<String, Object>) feedbackMap, null, null);
//					tvp.zetInstellingen(instellingen);
//					tvp.setKeyboard(kb);
//					final Object orgObject = tvp;
//					OpdrNavIF comRoot2 = comRoot;
//					Connector connector = find(tvp);
//					comRoot2 = new OpdrNavContext(comRoot,connector, this.bgColorZichtbaar ? bgColor : comRoot.getBackground());
//					((InteractionView) orgObject).setCommunicationRoot(comRoot2);
//					HashMap<String, Object> launchState = (HashMap<String, Object>) ((HashMap<String, Object>) feedbackMap).get("interactiePanelLaunchState");
//					tvp.zetOpdracht(launchState);
					initialState.addAll(getAnswerModels(feedbackMap));
				}
				catch(Exception e)
				{
					//Situation back; can be ignored in this method
				}
			}
			
		}
		initialiseIdeasStatistiek(initialState);
		
	}
	
	class NameComparator implements Comparator<Tupel> {
	    @Override
	    public int compare(Tupel a, Tupel b) {
	        return a.name.compareToIgnoreCase(b.name);
	    }
	}

	
	private ArrayList<Tupel> getAnswerModels()
	{
		//HashMap<String, Object> map = new HashMap<String, Object>();
				ArrayList<Tupel> answerModels = new ArrayList<Tupel>();
				String type = "initial"; //Indicates that this is the initial (author's) answer
				MapperConstants constants = new MapperConstants(); //map for collecting answers from AntwoordKeuzeVak
				for(int i = 0; i < tekstVakken.length; i++)
				{
					for(int j = 0; j < tekstVakken[i].length; j++)
					{
						ArrayList<Object> opdrObjects = tekstVakken[i][j].getOpdrachtObjects();
						for(int k = 0; k < opdrObjects.size(); k++)
						{
							Object object = opdrObjects.get(k);
							//first check: is it a TekstVakPanel? Then request answers from this TekstVakPanel.
							if(object instanceof TekstVakPanel)
							{	
								ArrayList<Tupel> answers2 = ((TekstVakPanel) object).getAnswerModels();
								answerModels.addAll(answers2);
							}
							else
							{
								String logIDLabel = "";
								String antwoord = "";
								if(object instanceof FormuleEditorWithAnswer)
								{
									FormuleEditorWithAnswer fewa = (FormuleEditorWithAnswer) object;
									logIDLabel = fewa.getLogIDLabel();
									//Expressie e = FormuleParser.parse("$f" + fewa.toString() + "@");
									if(fewa.isVergelijkingVak())
									{
										VergelijkingMeerv vgl = ((AntwoordVergelijkingVakChecker)fewa.getAvChecker()).getDesiredSolution();
										if(vgl != null)
											antwoord = vgl.toStringStrikt();
									}
									else
									{
										Expressie[] juisteAntwoorden = ((AntwoordFormuleVakChecker) fewa.getAvChecker()).getJuisteAntwoorden();
										if(juisteAntwoorden != null && juisteAntwoorden.length > 0)
										{
											//for now: assume that only the first possible answer is relevant
											Expressie e = juisteAntwoorden[0];
											if(e != null)
												antwoord = e.toStringStrikt();
										}
									}
								}
								else if(object instanceof FormuleEditorWithSteps)
								{
									FormuleEditorWithSteps fews = (FormuleEditorWithSteps) object;
									logIDLabel = fews.getLogIDLabel();
									if(fews.isVergelijkingVak())
									{
										VergelijkingMeerv vgl = ((AntwoordVergelijkingVakChecker) fews.getAvChecker()).getDesiredSolution();
										if(vgl != null)
											antwoord = vgl.toStringStrikt();
									}
									else
									{
										Expressie[] juisteAntwoorden = ((AntwoordFormuleVakChecker) fews.getAvChecker()).getJuisteAntwoorden();
										if(juisteAntwoorden != null && juisteAntwoorden.length > 0)
										{
											//for now: assume that only the first possible answer is relevant
											Expressie e = juisteAntwoorden[0];
											if(e != null)
											antwoord = e.toStringStrikt();
										}
									}
								}
								else if(object instanceof AntwoordKeuzeVak)
								{
									AntwoordKeuzeVak akv = (AntwoordKeuzeVak) object;
									logIDLabel = akv.getLogIDLabel();
									String juisteAntwoord = akv.getAntwoordString().trim();
									//if selection is an expression: convert to stringStrikt for merging with other components
									//else: select key from map
									if(juisteAntwoord.startsWith("$f") && juisteAntwoord.endsWith("@"))
									{
										Expressie e = FormuleParser.geefExpressie(juisteAntwoord);
										if(e != null)
										{	antwoord = e.toStringStrikt();
											
										}
										else
										{	antwoord = juisteAntwoord.substring(2, juisteAntwoord.length() - 1);
											
										}
									}
									else
										antwoord = getFromMap(constants, logIDLabel, juisteAntwoord);
								}
								else if(object instanceof AntwoordTekstVak)
								{
									AntwoordTekstVak atv = (AntwoordTekstVak) object;
									logIDLabel = atv.getLogIDLabel();
									Expressie expr = FormuleParser.geefExpressie(atv.getAntwoordString());
									if(expr != null)
										antwoord = expr.toStringStrikt();
								}
								if(!logIDLabel.equals("") && !antwoord.equals(""))
								{
									//Does answers already contain tupel with same logIDLabel? Then merge answers into equation or inequality
									String lastLabel = "";
									Tupel lastTupel = null;
									if(answerModels.size() > 0)
									{
										lastTupel = answerModels.get(answerModels.size() - 1);
										lastLabel = lastTupel.name;
									}
									if(lastTupel != null && lastLabel.equals(logIDLabel))
									{
										String lastAnswer = lastTupel.value;
										String[] vergTekens = { "=", ">", "<", "\u2264", "\u2265", "\u2248", "\u2260" };
										boolean pasted = false;
										for(int m = 0; m < vergTekens.length && !pasted; m++)
										{
											if(lastAnswer.endsWith(vergTekens[m]) || antwoord.equals(vergTekens[m]))
											{
												lastAnswer = lastAnswer + antwoord;
												pasted = true;
											}
										}
										if(!pasted)
										{	lastAnswer = lastAnswer + "=" + antwoord;
										}
										
										lastTupel.value = lastAnswer; // this also adjusts the value in the answers arraylist
									}
									else			
										answerModels.add(new Tupel(logIDLabel, type, antwoord));
								}
							}
						}
					}
				}
				return answerModels;
	}
	
	public ArrayList<Tupel> getAnswerModels(ObjectMap map)
	{
		TekstVakPanel tvp = new TekstVakPanel(activity, (HashMap<String, Object>) map, randomVarNamen, randomVarWaarden, map.getInt("breedte"));
		tvp.zetInstellingen(instellingen);
		tvp.setKeyboard(kb);
		final Object orgObject = tvp;
		OpdrNavIF comRoot2 = comRoot;
		Connector connector = find(tvp);
		comRoot2 = new OpdrNavContext(comRoot,connector, bgColorZichtbaar ? bgColor : comRoot.getBackground());
		((InteractionView) orgObject).setCommunicationRoot(comRoot2);
		HashMap<String, Object> launchState = (HashMap<String, Object>) ((HashMap<String, Object>) map).get("interactiePanelLaunchState");
		tvp.zetOpdracht(launchState);
		return tvp.getAnswerModels();
	}
	
	public ArrayList<Tupel> getAnswerModelsNew(ArrayList<Object> list)
	{
		maakStapNieuw(list, randomVarNamen, randomVarWaarden);
		return getAnswerModels();
		
	}
	
	
	
	
	private int initialiseerObjects(ArrayList<Object> opdrachtObjects, List<Object> opdrachtGegevens, int rij, int kolom, int aantalVakken)
	{
		for (int k = 0; k < opdrachtObjects.size(); k++)
		{
			Object currentObject = opdrachtObjects.get(k);
			final Object orgObject = currentObject;
//FIXME general unwrap decorator pattern.
			if(currentObject instanceof ShareFacade)
			{
				currentObject = ((ShareFacade) currentObject).unwrap();
			}
			
			
			if (currentObject instanceof InteractionView)
			{
				OpdrNavIF comRoot2 = comRoot;
				Connector connector = find(currentObject);
				comRoot2 = new OpdrNavContext(comRoot,connector, this.bgColorZichtbaar ? bgColor : comRoot.getBackground());

if (zichtbaarNaNakijken && activity.isReview() && reviewActivity != null) {
	LOG.warning("Hier review Opdrnav");
	comRoot2 = reviewActivity.wrap(comRoot2);
}
				
				((InteractionView) orgObject).setCommunicationRoot(comRoot2);
				if(! (currentObject instanceof StateLess))
				{	interactionViewObjects.add(orgObject);
				}
				
				if(currentObject instanceof CheckValueUnit)
				{
					ArrayList<Object> lijst = geefInteractionViews(k, rij, kolom, opdrachtObjects);
					
					int aantalValueObjects = ((CheckValueUnit) currentObject).getAantalValueObjects();
					TekstVakPanel[] waardeObjecten = new TekstVakPanel[aantalValueObjects];
					
					for(int l = 0; l < aantalValueObjects; l++)
					{	waardeObjecten[l] = zoekTekstVakPanel(l+1, lijst);
					
					}
					((CheckValueUnit) currentObject).zetWaardeObjecten(waardeObjecten);
				}
				else if(currentObject instanceof CheckSelectieUnit)
				{
					ArrayList<Object> lijst = geefInteractionViews(k, rij, kolom, opdrachtObjects);
					
					
					int aantalSelectieObjecten = ((CheckSelectieUnit) currentObject).getAantalSelectieObjecten();
					TekstVakPanel[] selectieObjecten = new TekstVakPanel[aantalSelectieObjecten];
					for(int l = 0; l < aantalSelectieObjecten; l++)
						selectieObjecten[l] = zoekTekstVakPanel(l+1, lijst);
					
					((CheckSelectieUnit) currentObject).zetSelectieObjecten(selectieObjecten);
				}
				else if(currentObject instanceof CheckSleepUnit)
				{	
					ArrayList<Object> lijst = geefInteractionViews(k, rij, kolom, opdrachtObjects);
					
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
					ArrayList<Object> lijst = geefInteractionViews(k, rij, kolom, opdrachtObjects);
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
				tekstVakChild.setParent(tekstVakken[rij][kolom]);
				tekstVakChild.zetInstellingen(instellingen);
				tekstVakChild.setKeyboard(kb);
				tekstVakChild.zetOpdracht(launchState);
				tekstVakChild.setContainer(new TekstVakContext(rij,kolom));
				xWidgetMap.putAll(tekstVakChild.xWidgetMap);
				Connector.calculateSubscriptions(xWidgetMap.values());
				
			}
			else if (currentObject instanceof FormuleEditorWithAnswer)
			{
				aantalVakken++;
				FormuleEditorWithAnswer formuleEditorWithAnswer = (FormuleEditorWithAnswer) currentObject;
				formuleEditorWithAnswer.zetInstellingen(instellingen);
				if(rij == 0 && kolom == 0)
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
				TekstVak tv = tekstVakken[rij][kolom];
				((SymboolPanel) currentObject).zetVolledigeHoogte(tv.hoogte-2*tv.bovenMarge);
			}
			else if (currentObject instanceof SamengesteldeStappenPanel)
			{
				SamengesteldeStappenPanel panel = (SamengesteldeStappenPanel) currentObject;
				panel.zetInstellingen(instellingen);
				panel.setKeyboard(kb);
				//panel.setParent(tekstVakken[rij][kolom]);
			}
			else if (currentObject instanceof InteractionView)
			{
				aantalVakken++;
			}
		}
		return aantalVakken;
	}
	
	private Connector find(Object currentObject) {
		return getXWidgetMap().get(currentObject);
	}
	
	public void initialiseerStappen()
	{
		stappen = new ArrayList<Object>();
		setKolom(breedtes.size()-1);
		resize();
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
		mode = comRoot.getMode();
		lessonmode = comRoot.getLessonMode();
		if (dwologger != null) dwologger.setCommunicationRoot(comRoot);
		comRoot.addCBookEventListener(ACTION_SETVISIBLE, this);
		comRoot.addCBookEventListener(ACTION_SETNOTVISIBLE, this);
		comRoot.addCBookEventListener(TEXT_CONTENT, this);
		comRoot.addCBookEventListener(TVP_SELECT, this);
		comRoot.addCBookEventListener(TVP_DESELECT, this);
		comRoot.addCBookEventListener(TVP_KLAPUIT, this);
		comRoot.addCBookEventListener(TVP_KLAPIN, this);
		comRoot.addCBookEventListener("action.zoom", this);
		comRoot.addCBookEventListener("action.unzoom", this);
		comRoot.addCBookEventListener("action.setActive", this);
		comRoot.addCBookEventListener("action.setInactive", this);
		comRoot.addCBookEventListener("double.xcoordinate", this);
		comRoot.addCBookEventListener("double.ycoordinate", this);
		comRoot.addCBookEventListener("double.rotation", this);
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
		h.put("selected", Boolean.valueOf(selected));
		h.put("inactive", new Boolean(inactive));
		h.put("ingeklapt", new Boolean(ingeklapt));
		h.put("popupUsed", Boolean.valueOf(popupUsed));
		h.put("nagekeken", Boolean.valueOf(nagekeken));
		h.put("visible", new Boolean(visible));
		if(stappen!=null)
		{	
			h.put("stappen", stappen);
			h.put("stapNr", new Integer(stapNr));
		}
		h.put("goedHalfFoutStatistiek", new Integer(goedHalfFoutStatistiek));
		h.put("feedbackStatistiek", feedbackStatistiek);
		
		if(zwevend)
		{	h.put("locationX", new Integer(locationX));
			h.put("locationY", new Integer(locationY));
		}
		if (dwologger != null && !ideasStatistiek) {
			dwologger.updateLog(buildLogParameters());
		}
		if (reviewActivity != null) {
			int score0 = getScore0();
			if (score0 == 0 && isFout()) score0 = -1; // marker 
			reviewActivity.getState(h, score0);
		}
		return h;
	}

	
	private boolean isFout() {
		for (int i = 0; i < interactionViewObjects.size(); i++)
		{
			Object currentObject = interactionViewObjects.get(i);
			Boolean check = ((InteractionView) currentObject).isCorrect();
			if (Boolean.FALSE.equals(check))
				return true;
		}
		return false;
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
			if ( !layerVisible || zichtbaarNaNakijken) {
				visible = false;
				setVisibility(false);
			} 
			return;
		}

		ObjectMap map = JSONUtilities.wrapMap(h);

		if (reviewActivity != null) map = reviewActivity.setState(map);
		else if (zichtbaarNaNakijken && activity.isEindtoetsVerzegeld()) map = ReviewActivity.wrap(map);

		boolean ingeklapt = this.ingeklapt;
		if (map.containsKey("hoogtes") )
		{
			List<Double> hoogtesState = map.getDoubleList("hoogtes");
			if(hoogtesState != null && hoogtesState.size() == hoogtes.size())
				hoogtes = hoogtesState;
			else
				LOG.severe("hoogtes <> hoogteState");
			if(!hoogtes.isEmpty() && Math.round(hoogtes.get(0).doubleValue()) > hoogte)
				hoogte = (int) Math.round(hoogtes.get(0).doubleValue());
		}
		boolean shouldVisible = visible;
		if(map.containsKey("visible"))
			shouldVisible = map.getBoolean("visible");
		if(!layerVisible) shouldVisible = false;
		if(!shouldVisible)
		{	hoogte = 0;
			breedte = 0;
		}

		if (h.containsKey("stappen"))
		{	List<Object> stappenList = JSONUtilities.toArrayList(h.get("stappen")); // read-only origineel
			stappen = new ArrayList<Object> (stappenList); // read/write kopie
		}
		if (h.containsKey("stapNr"))
			stapNr = map.getInt("stapNr");
		if(stappen!=null)
		{
			//Fix for steps that have been saved, but are outside range of tekstVakken
			stapNr = Math.min(stapNr, tekstVakken.length);
			for(int i = stappen.size() - 1; i >= stapNr; i--)
			{
				stappen.remove(i);
			}
			
			for(int i = 0; i < stapNr; i++)
			{	
				//old situation, before SamengesteldeStappenPanel
				try {
					addTekstVakPanel((HashMap<String, Object>) stappen.get(i), randomVarNamen, randomVarWaarden, i, kolom);
				}
				//new situation, with SamengesteldeStappenPanel
				catch(Exception e) {
					
					Object stepContents = stappen.get(i);
					if(stepContents.getClass().isArray())
					{
						Object[] stepContentsArray = (Object[]) stepContents;
						ArrayList<Object> stepContentsList = new ArrayList<Object> ();
						for(int j = 0; j < stepContentsArray.length; j++)
							stepContentsList.add(stepContentsArray[j]);
						addStepContents(stepContentsList, randomVarNamen, randomVarWaarden, i, kolom);
					}
					else
					{
						addStepContents((ArrayList<Object>) stappen.get(i), randomVarNamen, randomVarWaarden, i, kolom);	
					}
					
				}
			}
			
		}
		//Feedback laatste stap hypothesetoetsen terugzetten
		if(h.containsKey("goedHalfFoutStatistiek"))
			goedHalfFoutStatistiek = map.getInt("goedHalfFoutStatistiek");
		if(h.containsKey("feedbackStatistiek"))
			feedbackStatistiek = map.getString("feedbackStatistiek");
		if(goedHalfFoutStatistiek != AntwoordVakChecker.GEEN || !feedbackStatistiek.equals(""))
			genereerFeedback(goedHalfFoutStatistiek, feedbackStatistiek);
		
		
		// hier hoogtes en breedtes aanpassen voor callout
		if (callOut)
		{
			resizeForCallOut();
		}
		
		ObjectList states = (map.getObjectList("interactiePanelStates"));
		if (states == null)
			states = JSONUtilities.wrapList(Collections.EMPTY_LIST);
		int size = interactionViewObjects.size();
		if(size != states.size())
			Logger.getLogger("TextVakPanel").severe("sizes " + size + " " + states.size());
		size = Math.min(size, states.size()); // XXX komt voor dat niet alle states bewaard zijn
		for (int i = 0; i < size; i++)
		{
			Object currentObject = interactionViewObjects.get(i);
			if(currentObject instanceof InteractionView) {
				HashMap<String, Object> state = (HashMap<String, Object>) states.getMap(i);
				((InteractionView) currentObject).setState(state);
			}
		}
		if(map.containsKey("selected"))
			selected = map.getBoolean("selected");
		if(map.containsKey("inactive"))
			inactive = map.getBoolean("inactive");
		popupUsed = map.getBoolean("popupUsed", false);
		nagekeken = map.getBoolean("nagekeken", false);
		if(map.containsKey("ingeklapt"))
			ingeklapt = map.getBoolean("ingeklapt");
		if(map.containsKey("locationX"))
			locationX = map.getInt("locationX");
		if(map.containsKey("locationY"))
			locationY = map.getInt("locationY");
		if (!callOut && parent != null && zwevend && parent == asWidget().getParent())
		{	
			parent.setWidgetLeftWidth(this.asWidget(), locationX, Style.Unit.PX, breedte, Style.Unit.PX);
			parent.setWidgetTopHeight(this.asWidget(), locationY, Style.Unit.PX, hoogte, Style.Unit.PX);
		}
		setSelected(selected);
		
		if(inactive) {
			if (!activity.isReview()) zetKlikPanel();
		}
		
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
			setVisibility((nagekeken||activity.isEindtoetsVerzegeld())&&layerVisible);
		else
			setVisibility(shouldVisible);

		if (lastEvent != null) {
			boolean small = lastEvent.booleanValue();
			lastEvent = null;
			Scheduler.get().scheduleDeferred(() -> 
				fireLayoutAction(small) // delay after all of setState()
			);
		}
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

	private int getScore0()
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
	
	public int getScore() {
		if (zichtbaarNaNakijken) return 0; // geen score bij deze optie.
		return getScore0();
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
		if(ideasStatistiek)
		{
			if(goedHalfFoutStatistiek == AntwoordVakChecker.GOED)
				return true;
			else if(goedHalfFoutStatistiek == AntwoordVakChecker.GEEN)
				return null;
			else 
				return false;
		}
		
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

		if (zichtbaarNaNakijken && activity.isReview()) {
			int s0 = getScore0();
			return reviewActivity.isCorrect(s0, correct);
		}
		return correct;
	}

	public void zetNagekeken(boolean b)
	{
		nagekeken = b;
		if(zichtbaarNaNakijken)
			setVisibility((b||activity.isEindtoetsVerzegeld())&&layerVisible);
		
		for (Object object : interactionViewObjects) {
			if(object instanceof InteractionView)
				((InteractionView) object).zetNagekeken(b);
		}
	}
	
	public void setParent(TekstVak panel)
	{
		parent = panel;
		if(sleepbaar) {
			TekstVakPanel tekstVakParent = parent.getTekstVakParent();
			tekstVakParent.sleepveld = tekstVakParent.sleepveld || sleepbaar;
			parent.getElement().getStyle().setProperty("touchAction", "none");
			//tekstVakParent.getAsPanel().addDomHandler((PointerMoveHandler)pointerHandler, PointerMoveEvent.getType()); 
			//tekstVakParent.getAsPanel().addDomHandler((PointerUpHandler)pointerHandler, PointerUpEvent.getType()); 
			//tekstVakParent.getAsPanel().addDomHandler((PointerDownHandler)pointerHandler, PointerDownEvent.getType()); 
		}
		if(fontOvererving && !anderFont && parent != null && parent.getTekstVakParent() != null)
		{	
			TekstVakPanel parentVak = parent.getTekstVakParent();
			CssColor fgColorOvererving = parentVak.fgColor;
			int fontSizeOvererving = parentVak.font_size;
			int fontStyleOvererving = parentVak.font_style;
			String fontNameOvererving = parentVak.font_name;
			
			fgColor = fgColorOvererving;
			font_size = fontSizeOvererving;
			font_style = fontStyleOvererving;
			font_name = fontNameOvererving;
			
			for (int i = 0; i < hoogtes.size(); i++)
			{	
				for (int j = 0; j < breedtes.size(); j++)
				{	
					if (!parentVak.hasStyle())
						tekstVakken[i][j].setColor(fgColorOvererving);
					else 
						tekstVakken[i][j].setColor(null); // anders wordt het zwart!
					tekstVakken[i][j].setFontStyle(fontStyleOvererving);
					tekstVakken[i][j].setFontName(fontNameOvererving);
					tekstVakken[i][j].setFontSize(fontSizeOvererving);
				}
			}
		}
		
	}
	
	public Panel getPanelElement(final FormuleHolder editor)
	{
		editor.paint();
		final Panel p = editor.getAsPanel();
		return p;
	}

	public Panel getAsPanel()
	{
		if(callOut)
			return callOutPanel;
		else 
			return mainPanel2;
	}

	private PopupFacade facade;

	private void initWidget() {
	  initWidget(facade.wrap(getAsPanel(), this));
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
	
	public boolean isInactive()
	{
		return inactive;
	}
	
	public TekstVakPanel isInIdeasStatistiek()
	{
		if(ideasStatistiek)
			return this;
		else if(parent != null)
			return parent.getTekstVakParent().isInIdeasStatistiek();
		else
			return null;
	}
	
	public void getHintIdeasStatistiek()
	{
		final TekstVakPanel statPanel = isInIdeasStatistiek();
		final List<Tupel> newAnswers = statPanel.getAllAnswers();
		
		//TODO: ask for hint based on newAnswers: String hint = ideas.getOneFirst(newAnswers);
		Promise<String> promise;
		
		promise = statPanel.getOneHint(newAnswers).map(new Function<RuleIF, String>() {
			private final MapperConstants constants = new MapperConstants();
			@Override
			public String apply(RuleIF t) {
//convert ruleif to string						
				String id = t.getId(); // ideas identifier
				String hint;						 

				if(id.equals("error"))
				{
					String expr = t.getExpr();
					if(expr.equals("no hint available"))
						id = "no hint available";
				}
				hint = constants.getHint(id);//getFromMap(constants, "hint", id); // ID to hint tekst
//toevoegen: reason/expr/..?
				
				return hint;
			}});
		
		promise.then( new Success<String,Void>() {

			@Override
			public Promise<Void> call(Promise<String> resolved)
					throws Exception {
				String hint = resolved.getValue();
				
				statPanel.feedbackImage = null;
				statPanel.vulFeedbackPanelEnVoegToe(hint);
				return null;
			}});
	}
	
	public TekstVakPanel findStappenVak()
	{
		if(stappen != null)
			return this;
		for(int i = 0; i < interactionViewObjects.size(); i++)
		{	Object object = interactionViewObjects.get(i);
			if(object instanceof TekstVakPanel)
			{
				TekstVakPanel stappenVak = ((TekstVakPanel) object).findStappenVak();
				if(stappenVak != null)
					return stappenVak;
			}
		}
		if(parent != null)
			return parent.getTekstVakParent().findStappenVak();
		
		return null;
	}

	public Promise<RuleIF> kijkNaIdeasStatistiek()
	{
		final List<Tupel> newAnswers = getAllAnswers();
		ListIterator<Tupel> iter = newAnswers.listIterator(newAnswers.size());
		while(iter.hasPrevious()) {
			Tupel t = iter.previous();
			if(t.value == null || t.value.isEmpty()) iter.remove();
		}
		
		//lastAnswers should always be a subset of newAnswers
		//but if steps are removed, or if a student has corrected steps, this is not the case. 
		//Therefore: remove all Tupels from lastAnswers that are not in newAnswers
		
		if(lastAnswers != null)
		{
			//Find last tupel with value from newAnswers:
//			Tupel lastTupel = null;
//			ListIterator<Tupel> iterNew = newAnswers.listIterator(newAnswers.size());
//			while(iterNew.hasPrevious())
//			{	Tupel t = iterNew.previous();
//				if(t.value != null && !t.value.isEmpty())
//				{
//					lastTupel = t;
//					break;
//				}
//			}
			
			ListIterator<Tupel> iter2 = lastAnswers.listIterator(lastAnswers.size());
			while(iter2.hasPrevious()) {
				Tupel t = iter2.previous();
				if(!newAnswers.contains(t))// || (lastTupel != null && t.equals(lastTupel)))
					iter2.remove();
			}
		}
		
		
		List<Tupel> oldAnswers = lastAnswers;
		
		//@Wim: maar wat als een student niet iets heeft toegevoegd, maar iets heeft gewijzigd? 
		//Of, wat nu ook kan, meerdere dingen heeft toegevoegd voordat hij op een checkbutton heeft geklikt?
		//Kan onderstaande regel nu niet beter weer weg?
		//if(newAnswers.size() > 0)
		//	oldAnswers = newAnswers.subList(0, newAnswers.size()-1);
		
		//opsturen - Wim
		
		//hier kan nu ook oldAnswers bij in. Bij eerste keer nakijken is oldAnswers nog null. 
		Promise<RuleIF> result = diagnose(oldAnswers, newAnswers)
		.recover(new RuleRecovery())	
		.then( 
				new Success<RuleIF, RuleIF>() {

					@Override
					public Promise<RuleIF> call(Promise<RuleIF> p) throws Exception {
						RuleIF resultaat = p.getValue();
						doeIetsMet(resultaat);						
						return p;
					}
				})
//		.onResolve(
//		
//		new Runnable() {
//			public void run() {
//				//na diagnose:
//				lastAnswers = newAnswers; //antwoorden uit deze ronde nakijken bewaren voor volgende ronde nakijken.
//			}
//		}
//		)
		;
		lastAnswers = newAnswers; //antwoorden uit deze ronde nakijken bewaren voor volgende ronde nakijken.
		return result;
		
		// Zie "doetIetsMet(resultaat)"
		//genereerFeedback(AntwoordVakChecker.GOED, "feedback");
	}

	private void genereerFeedback(int status, String feedbackReason) {
		//feedback terugkrijgen en verwerken
		goedHalfFoutStatistiek = status;
		feedbackStatistiek = feedbackReason;
		
		if(goedHalfFoutStatistiek == AntwoordVakChecker.GOED || goedHalfFoutStatistiek == AntwoordVakChecker.DOOR)
			feedbackImage = goedKrulImage; //request by social sciences: also green tickmark for correct intermediate steps. 
					// Otherwise students think they still need to do something with the answer they already gave, instead of continuing with a new step. 
		else if(goedHalfFoutStatistiek == AntwoordVakChecker.HALF)
			feedbackImage = goedKrulHalfImage; 
		else if(goedHalfFoutStatistiek == AntwoordVakChecker.FOUT)
			feedbackImage = foutKruisImage;
		
		MapperConstants constants = new MapperConstants();
		if(feedbackReason.trim().equals(""))
			feedbackReason = "noFeedback";
		vulFeedbackPanelEnVoegToe(/*getFromMap(constants, "feedback", feedbackReason)*/
								constants.getFeedback(feedbackReason)
				);
		
		comRoot.setChanged(goedHalfFoutStatistiek == AntwoordVakChecker.FOUT);
	}
	
	
private void vulFeedbackPanelEnVoegToe(String feedbackTekst)
{
	if(feedbackPanel == null)
	{
		feedbackPanel = new TekstVak();
		feedbackPanel.setSize(breedte, feedbackPanelHeight);
		feedbackPanel.setFontSize(XMLView.getDefaultFontSize());
		feedbackPanel.setFontName(XMLView.getDefaultFontName());
		feedbackPanel.setColor(CssColor.make("black"));
		feedbackPanel.setMarges(5, 25);
		feedbackPanel.setCentering(false, true);
		feedbackPanel.setPasHoogteBreedteAan(true, false);
		feedbackPanel.getElement().getStyle().setBackgroundColor("#FFFFDD");
	}
	feedbackPanel.removeFromParent();
	
	TekstBuffer b = new TekstBuffer(activity);
	ArrayList<Object> feedbackList = b.convertTekst(feedbackTekst, null, false);
	feedbackPanel.clear();
	feedbackPanel.setSize(breedte, 34);
	feedbackPanel.setObjects(feedbackList);
	feedbackPanel.resize();
		
	if (!feedbackTekst.trim().equals(""))
	{	
		feedbackPanelHeight = feedbackPanel.getHeight();
		
		mainPanel2.add(feedbackPanel);
		mainPanel2.setWidgetLeftRight(feedbackPanel, 0, Style.Unit.PX, 0, Style.Unit.PX);
		mainPanel2.setWidgetBottomHeight(feedbackPanel, 0, Style.Unit.PX, feedbackPanelHeight, Style.Unit.PX); 
		
		if(feedbackImage != null)
		{
			removeFeedbackImage();
			mainPanel2.add(feedbackImage);
			mainPanel2.setWidgetLeftWidth(feedbackImage, 5, Style.Unit.PX, 20, Style.Unit.PX);
			mainPanel2.setWidgetBottomHeight(feedbackImage, feedbackPanelHeight - 20, Style.Unit.PX, 20, Style.Unit.PX);
		}
	}
	else
	{	
		feedbackPanelHeight = 0;
		removeFeedbackImage();
		//feedbackImage.removeFromParent();
		mainPanel2.add(feedbackImage);
		mainPanel2.setWidgetLeftWidth(feedbackImage, 5, Style.Unit.PX, 20, Style.Unit.PX);
		mainPanel2.setWidgetBottomHeight(feedbackImage, 5, Style.Unit.PX, 20, Style.Unit.PX);
	}
	resize();
	
}
	
private void removeFeedbackImage()
{
	if(feedbackImage != null)
		feedbackImage.removeFromParent();
	goedKrulImage.removeFromParent();
	goedKrulHalfImage.removeFromParent();
	foutKruisImage.removeFromParent();
}

/**
 * Bepaal een hint na answers.
 * Aanroep:
 * getOneFirst(getAnswers()).then(new Success<RuleIF, Void>() {...doeietsmetRuleIF(resolved.getValue())...} );
 * @param answers
 * @return de hint als promise
 */
private Promise<RuleIF> getOneFirst(List<Tupel> answers) {
	Deferred<RuleIF> defer = new Deferred<RuleIF>();
	IdeasIF ideas = GWT.create(IdeasIF.class);
	ideas.setStrategie("hypothesis");
	String expr = "[]"; // last time
	
	final TekstVakPanel statPanel = isInIdeasStatistiek();
	//final List<Tupel> newAnswers = statPanel.getAnswers();
	List<Tupel> old = new ArrayList<Tupel>();
	if(statPanel.initialStatistiekState != null) old.addAll(statPanel.initialStatistiekState);
	if(answers != null) old.addAll(answers);
	
	if(!old.isEmpty()) {
		StringBuilder input = new StringBuilder();
		input.append('[');
		for (Tupel tupel : old) {
			if(tupel.value == null || tupel.value.isEmpty()) continue;
			input.append("$C"); // component
			input.append(deGreek(tupel.value)).append("$n").append(CamelCase(tupel.name)).append("$k");
			if("initial".equals(tupel.type)) input.append('0'); else input.append('1');
			input.append("@@@,");
		}
		if(input.length()>2) input.setLength(input.length()-1);
		input.append(']');
		expr = input.toString();
	}
	String prefix = getPrefix(answers);
	ideas.getOneFirst(wrap(expr, prefix), new DeferRuleCallback(defer));
	return defer.getPromise().recover(new RuleRecovery());
}

/**
 * Bepaal een hint na answers.
 * Aanroep:
 * getOneFirst(getAnswers()).then(new Success<RuleIF, Void>() {...doeietsmetRuleIF(resolved.getValue())...} );
 * @param answers
 * @return de hint als promise
 */
private Promise<RuleIF> getOneHint(List<Tupel> answers) {
    Deferred<RuleIF> defer = new Deferred<RuleIF>();
    IdeasIF ideas = GWT.create(IdeasIF.class);
    ideas.setStrategie("hypothesis");
    String expr = "[]"; // last time
    
    final TekstVakPanel statPanel = isInIdeasStatistiek();
    //final List<Tupel> newAnswers = statPanel.getAnswers();
    List<Tupel> old = new ArrayList<Tupel>();
    if(statPanel.initialStatistiekState != null) old.addAll(statPanel.initialStatistiekState);
    if(answers != null) old.addAll(answers);
    
    if(!old.isEmpty()) {
        StringBuilder input = new StringBuilder();
        input.append('[');
        for (Tupel tupel : old) {
            if(tupel.value == null || tupel.value.isEmpty()) continue;
            input.append("$C"); // component
            input.append(deGreek(tupel.value)).append("$n").append(CamelCase(tupel.name)).append("$k");
            if("initial".equals(tupel.type)) input.append('0'); else input.append('1');
            input.append("@@@,");
        }
        if(input.length()>2) input.setLength(input.length()-1);
        input.append(']');
        expr = input.toString();
    }
    String prefix = getPrefix(answers);
    ideas.getOneHint(wrap(expr, prefix), new DeferRuleCallback(defer));
    return defer.getPromise().recover(new RuleRecovery());
}


private Promise<RuleIF> diagnose(List<Tupel> oldAnswers, List<Tupel> newAnswers) {
		Promise<RuleIF> cache = fromCache(oldAnswers, newAnswers);
		if(cache != null) return cache;
		final Deferred<RuleIF> defer = new Deferred<RuleIF>();
		IdeasIF ideas = GWT.create(IdeasIF.class);
		ideas.setStrategie("hypothesis");
		String expr = "[]"; // last time
		List<Tupel> old = new ArrayList<Tupel>();
		if(initialStatistiekState != null) old.addAll(initialStatistiekState);
		if(oldAnswers != null) old.addAll(oldAnswers);
		
		if(!old.isEmpty()) {
			StringBuilder input = new StringBuilder();
			input.append('[');
			for (Tupel tupel : old) {
				if(tupel.value == null || tupel.value.isEmpty()) continue;
				input.append("$C"); // component
				input.append(deGreek(tupel.value)).append("$n").append(CamelCase(tupel.name)).append("$k");
				if("initial".equals(tupel.type)) input.append('0'); else input.append('1');
				input.append("@@@,");
			}
			if(input.length()>2) input.setLength(input.length()-1);
			input.append(']');
			expr = input.toString();
		}
		List<Tupel> intupels = new ArrayList<Tupel>();
		if(initialStatistiekState != null) intupels.addAll(initialStatistiekState);
		intupels.addAll(newAnswers);
		
		StringBuilder input = new StringBuilder();
		input.append('[');
		for (Tupel tupel : intupels) {
			if(tupel.value == null || tupel.value.isEmpty()) continue;
			input.append("$C"); // component
			input.append(deGreek(tupel.value)).append("$n").append(CamelCase(tupel.name)).append("$k");
			if("initial".equals(tupel.type)) input.append('0'); else input.append('1');
			input.append("@@@,");
		}
		if(input.length()>2) input.setLength(input.length()-1);
		input.append(']');
		final String instr = input.toString();
		if(instr.equals(expr)) {
			return toCache( Promises.resolved((RuleIF)new AbstractRule() {

				@Override
				public String getId() {
					return "equal";
				}

				@Override
				public String getExpr() {
					return instr;
				}

				@Override
				public String getName() {
					return "equal";
				}
				@Override
				public boolean isException() {
					return true;
				}

			}), oldAnswers, newAnswers);	
		}

		
		
		RuleCallback callback = new DeferRuleCallback(defer);
GWT.log("expr  = "+ expr);
GWT.log("input = " + instr);
		String prefix = getPrefix(oldAnswers);
GWT.log("prefix = " + prefix);
		ideas.diagnose(wrap(expr, prefix), wrap(instr, ""), callback );
		return toCache( defer.getPromise(), oldAnswers, newAnswers);
	}

private Object deGreek(String value) {
	System.out.println("deGreek: " + value);
	return value
		//task specific replacements
		.replace("$b$a$a$cx@$n$cy@@@$nD$s0@@@$n$w$o$b$ps$sx@$n2@@$nn$sx@@@$n$b$ps$sy@$n2@@$nn$sy@@@@@@@@", 
				"$b$aM1$nM2@@$n$w$o$b$ps1$n2@@$nn1@@$n$b$ps2$n2@@$nn2@@@@@@@")
		//TODO (for later, with Bastiaan) enable replacing x by M1 instead of replacing x by M (etc).
		//Then the above can be replaced by extending the general replacements below. 
		
		//general replacements	
		.replace(",", ".")
		.replace("$sD@", "")
		.replace("$cx@", "M")
		.replace("σ$sM@", "sigmaM")
		.replace("σ$sx@", "sigmaM")
		.replace("$pμ$n0@@", "mu")
		.replace("μ$sX@", "mu")
		.replace("μ", "mu")
		.replace("σ", "sigma")
		.replace("ρ", "rho")
		.replace("z$sc*r*i*t@", "zcrit")
		.replace("t$sc*r*i*t@", "tcrit")
		.replace("r$sc*r*i*t@", "rcrit")
		.replace("F$sc*r*i*t@", "Fcrit")
		.replace("$pX$n(2@$sc*r*i*t@@@", "chicrit")//TO DO: invullen (X^2_crit) -- checken of zo goed geschreven
		.replace("$pX$n2@@", "chisq")
		.replace("$sA@", "1")
		.replace("$sB@", "2")
		.replace("$s1@", "1")
		.replace("$s2@", "2")
		.replace("wel", "true")
		.replace("niet", "false")
		.replace("inside", "true")
		.replace("outside", "false")
		.replace("$vS$nE$sM@@@", "SEM")
		.replace("$vS$nE$sx@@@", "SEM")
		.replace("s$sM@", "SEM")
		.replace("$vS$nD@@", "s")
		.replace("s$sx@", "s")
		.replace("s$sy@", "s")
		.replace("vS$n$bD$n$wn@@", "bs$n$wn") // vervang "SD/sqrt(n)" door "s/sqrt(n)"
		.replace("S$n$bD", "s$n$b1")// vervang "SD/iets" door "s*1/iets" 
	;	
}

private Map<Object, Promise<RuleIF>> promiseCache = new HashMap<Object, Promise<RuleIF>>();

private Promise<RuleIF> toCache(Promise<RuleIF> promise, List<Tupel> oud, List<Tupel> nieuw) {
	Object key = Arrays.asList(oud, nieuw);
	promiseCache.put(key, promise);
	if(!nieuw.equals(oud)) 
		promiseCache.put(nieuw, promise);
	return promise;
}

private Promise<RuleIF> fromCache(List<Tupel> oud, List<Tupel> nieuw) {
	return promiseCache.get(Arrays.asList(oud, nieuw));
}

private String getPrefix(List<Tupel> nieuw) {
	Promise<RuleIF> promise = promiseCache.get(nieuw);
	if(promise != null && promise.isDone() && promise.getFailure() == null && promise.getValue() != null) {
		return promise.getValue().getPrefix();
	}
	return "";
}

private RuleIF wrap(final String expr, final String prefix) {
	return new AbstractRule() {

		@Override
		public String getExpr() {
			return expr;
		}

		@Override
		public String getPrefix() {
			return prefix;
		}
		
	};
}

private Object CamelCase(String name) {
	int i;
	while( (i=name.indexOf('-')) >=0 ) {
		if( i <= name.length()-2) {
		String voor = name.substring(0, i);
		char   title = name.charAt(i+1);
		String na = name.substring(i+2);
		name = voor + (title) + na; // No camelcase
		} else 
			name = name.substring(0, i);
	}
	return name;
}

/** berekening van het reasoner resultaat
 * 	
 * @param resultaat
 */
	protected void doeIetsMet(RuleIF resultaat) {
		boolean klaar = resultaat.isReady();
		boolean fout  = resultaat.isException();
// name: correct similar notequiv 
		String name = resultaat.getName();
// zie ik nog niet ingevuld
		String id = resultaat.getId();
		if(fout) {
			GWT.log("Exception: " + resultaat.getExpr());
		} else 
		{
			GWT.log( klaar + ", " + id + ", " + name);
			int status;
			if (klaar) status = AntwoordVakChecker.GOED;
			else if ("notequiv".equals(name)||"buggy".equals(name))
				status = AntwoordVakChecker.FOUT;
			else status = AntwoordVakChecker.DOOR;
			String feedback = id;
			Map context = resultaat.getContext();
			String reason = context != null ? (String) context.get("reason") : "";
			genereerFeedback(status, feedback + reason);
			if (dwologger != null) {
				Map<String,Object> parameters = new HashMap<>();
				switch(status) {
				case AntwoordVakChecker.FOUT: parameters.put("success", false);
				default:
					parameters.put("score", Collections.singletonMap("raw", 0));
				break;
				case AntwoordVakChecker.GOED: parameters.put("success", true);
					parameters.put("score", Collections.singletonMap("scaled", 1));
				}
				parameters.put("response", resultaat.getExpr());
				parameters.put("feedback", (name + " " + feedback + " " + reason).trim());
				dwologger.log(parameters);
			}
		}
		// voor logging:
		// name, context.reason en expr, status -> success
	}

	public static class Tupel {
	    private String name;
	    private String type; //initial or derived
	    private String value;

	    public Tupel(String name, String type, String value) {
	        super();
	        this.name = name;
	        this.type = type;
	        this.value = value;
	    }

		public Tupel(ObjectMap map) {
			this.name = map.getString("name");
			this.type = map.getString("type");
			this.value = map.getString("value");
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Tupel other = (Tupel) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (type == null) {
				if (other.type != null)
					return false;
			} else if (!type.equals(other.type))
				return false;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}
	    
	}
	
	public ArrayList<Tupel> getAnswers()
	{
		//HashMap<String, Object> map = new HashMap<String, Object>();
		ArrayList<Tupel> answers = new ArrayList<Tupel>();
		String type = "derived"; //Indicates that this is a student answer
		MapperConstants constants = new MapperConstants(); //map for collecting answers from AntwoordKeuzeVak
		for(int i = 0; i < tekstVakken.length; i++)
		{
			for(int j = 0; j < tekstVakken[i].length; j++)
			{
				ArrayList<Object> opdrObjects = tekstVakken[i][j].getOpdrachtObjects();
				for(int k = 0; k < opdrObjects.size(); k++)
				{
					Object object = opdrObjects.get(k);
					//first check: is it a TekstVakPanel? Then request answers from this TekstVakPanel.
					if(object instanceof TekstVakPanel)
					{	
						ArrayList<Tupel> answers2 = ((TekstVakPanel) object).getAnswers();
						answers.addAll(answers2);
					}
					else
					{
						String logIDLabel = "";
						String antwoord = "";
						if(object instanceof FormuleEditorWithAnswer)
						{
							FormuleEditorWithAnswer fewa = (FormuleEditorWithAnswer) object;
							logIDLabel = fewa.getLogIDLabel();
							if(fewa.isVergelijkingVak())
							{
								VergelijkingMeerv vgl = FormuleParser.parseVergelijking("$f" + fewa.toString() + "@");
								if(vgl != null)
									antwoord = vgl.toStringStrikt();
							}
							else
							{
								Expressie e = FormuleParser.geefExpressie("$f" + fewa.toString() + "@");
								if(e != null)
									antwoord = e.toStringStrikt();
							}
						}
						else if(object instanceof FormuleEditorWithSteps)
						{
							FormuleEditorWithSteps fews = (FormuleEditorWithSteps) object;
							logIDLabel = fews.getLogIDLabel();
							String[] regels = fews.geefRegels();
							if(fews.isVergelijkingVak())
							{
								VergelijkingMeerv[] vergelijkingen = new VergelijkingMeerv[regels.length];
								for(int m = 0; m < regels.length; m++)
								{
									vergelijkingen[m] = FormuleParser.parseVergelijking(regels[m]);//$f are @ already added in geefRegels.
								}
								if(vergelijkingen[vergelijkingen.length - 1] != null)
									antwoord = vergelijkingen[vergelijkingen.length - 1].toStringStrikt();
							}
							else
							{
								Expressie[] expressies = new Expressie[regels.length];
								for(int m = 0; m < regels.length; m++)
								{
									expressies[m] = FormuleParser.geefExpressie(regels[m]);//$f are @ already added in geefRegels.
								}
								if(expressies[expressies.length - 1] != null)
									antwoord = expressies[expressies.length - 1].toStringStrikt();
							}
						}
						else if(object instanceof AntwoordKeuzeVak)
						{
							AntwoordKeuzeVak akv = (AntwoordKeuzeVak) object;
							logIDLabel = akv.getLogIDLabel();
							String selection = akv.getSelectedItem().trim();
							//if selection is an expression: convert to stringStrikt for merging with other components
							//else: select key from map
							if(selection.startsWith("$f") && selection.endsWith("@"))
							{
								Expressie e = FormuleParser.geefExpressie(selection);
								if(e != null)
								{	antwoord = e.toStringStrikt();
									
								}
								else
								{	antwoord = selection.substring(2, selection.length() - 1);
									
								}
							}
							else
								antwoord = getFromMap(constants, logIDLabel, selection);
						}
						else if(object instanceof AntwoordTekstVak)
						{
							AntwoordTekstVak atv = (AntwoordTekstVak) object;
							logIDLabel = atv.getLogIDLabel();
							Expressie expr = FormuleParser.geefExpressie(atv.getText());
							if(expr != null)
								antwoord = expr.toStringStrikt();
						}
						if(!logIDLabel.equals(""))
						{
							//does answers already contain tupel with same logIDLabel? Then merge answers into one equation or inequality
							String lastLabel = "";
							Tupel lastTupel = null;
							if(answers.size() > 0)
							{
								lastTupel = answers.get(answers.size() - 1);
								lastLabel = lastTupel.name;
							}
							if(lastTupel != null && lastLabel.equals(logIDLabel))
							{
								String lastAnswer = lastTupel.value;
								String[] vergTekens = { "=", ">", "<", "\u2264", "\u2265", "\u2248", "\u2260" };
								boolean pasted = false;
								for(int m = 0; m < vergTekens.length && !pasted; m++)
								{
									if(lastAnswer.endsWith(vergTekens[m]) || antwoord.equals(vergTekens[m]))
									{
										lastAnswer = lastAnswer + antwoord;
										pasted = true;
									}
								}
								if(!pasted && !(lastAnswer.isEmpty()||antwoord.isEmpty())) // geen = als een van beide leeg is.
								{	lastAnswer = lastAnswer + "=" + antwoord;
								}
								
								lastTupel.value = lastAnswer; // this also adjusts the value in the answers arraylist
							}	
							else			
								answers.add(new Tupel(logIDLabel, type, antwoord));
						}
					}
					
				}
			}
		}
		return answers;
	}
	
	public ArrayList<Tupel> getAllAnswers()
	{
		ArrayList<Tupel> answers = getAnswers();
		//Make lists out of separate list elements
		ListIterator<Tupel> initIt = answers.listIterator();
		ArrayList<Tupel> allListElements = new ArrayList<Tupel>();
		while(initIt.hasNext())
		{
			Tupel t = initIt.next();
			if(t.name.contains("."))
			{
				allListElements.add(t);
				initIt.remove();
			}
		}
		while(!allListElements.isEmpty())
		{
			Tupel firstTupel = allListElements.get(0);
			String firstName = firstTupel.name.substring(0, firstTupel.name.indexOf("."));
			ArrayList<Tupel> singleListElements = new ArrayList<Tupel>();
			ListIterator<Tupel> it = allListElements.listIterator();
			while(it.hasNext())
			{
				Tupel t = it.next();
				String name = t.name.substring(0, t.name.indexOf("."));
				if(name.equals(firstName))
				{
					singleListElements.add(t);
					it.remove();
				}
			}
			singleListElements.sort(new NameComparator());
			String sep = ";";
			String bracketL = "[";
			String bracketR = "]";
			String antwoord = "";
			if(firstTupel.name.substring(firstTupel.name.indexOf(".") + 1).contains(".")) //two dimensional list
			{
				antwoord = bracketL + bracketL;
				ListIterator<Tupel> singleIt = singleListElements.listIterator();
				while(singleIt.hasNext())
				{
					Tupel t = singleIt.next();
					String number = t.name.substring(t.name.indexOf(".") + 1);
					if(number.equals("1.1"))
						antwoord = antwoord + t.value;
					else if(number.substring(number.indexOf(".") + 1).equals("1"))
						antwoord = antwoord + bracketR + sep + bracketL + t.value;
					else
						antwoord = antwoord + sep + t.value;
				}
				antwoord = antwoord + bracketR + bracketR;
			}
			else //one dimensional list
			{
				antwoord = bracketL;
				ListIterator<Tupel> singleIt = singleListElements.listIterator();
				while(singleIt.hasNext())
				{
					Tupel t = singleIt.next();
					String number = t.name.substring(t.name.indexOf(".") + 1);
					if(number.equals("1"))
						antwoord = antwoord + t.value;
					else
						antwoord = antwoord + sep + t.value;
				}
				antwoord = antwoord + bracketR;
			}
			if(!antwoord.contains("[;") && !antwoord.contains(";;") && !antwoord.contains(";]")) //make sure it is a complete list
			{
				Tupel newTupel = new Tupel(firstTupel.name.substring(0, firstTupel.name.indexOf(".")), firstTupel.type, antwoord);
				answers.add(newTupel);
			}
		}
		
		return answers;
	}
	
	public String getFromMap(MapperConstants constants, String label, String selectie)
	{
		Map m = constants.getMap(label);
		if(m != null && m.containsKey(selectie)) {
			String result = m.get(selectie).toString();
			if (result != null) 
			{
				return result;
			}
		}
		return selectie;
	}
	
	public boolean isMouseDown()
	{
		if(this.hasPointerEventSupport)
			return pointerHandler.isMouseDown();
		return mouseHandler.isMouseDown();
	}
	
	public void setMouseDown(boolean b)
	{
		if(this.hasPointerEventSupport)
			pointerHandler.setMouseDown(b);
		else
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
		Widget divparent = asWidget().getParent();
		if(parent == divparent && parent != null && zwevend)
		{	parent.remove(this.asWidget());
			parent.add(this.asWidget()); // herplaats aan de top.
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

	public TekstVakPanel[] geefSleepDoelen()
	{
		return sleepDoelen;
	}

	public void zetSleepDoelen(TekstVakPanel[] sleepDoelen)
	{
		this.sleepDoelen = sleepDoelen;
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
	public void verwijderKlikPanel()
	{
		if (klikPanel != null && klikPanel.getParent()==mainPanel2)
		{	mainPanel2.remove(klikPanel);
			klikPanel=null;
		}
	}
	
	
	public void setRelocate(boolean relocate)
	{
		this.relocate = relocate;
	}
	
	public void setSelected(boolean b)
	{ 
		selected = b;
		Style style = mainPanel2.getElement().getStyle();
		if (selected)
		{
			if (colorSelection)
			{	
				//randPanel.getElement().getStyle().setBorderColor(selectieColor.toString());
				if(bgColorZichtbaar)
					style.setBackgroundColor(selectieColor.toString());
				else {
					style.setBackgroundColor(selectieColor.toString());
					style.setBorderColor(selectieColor.toString());
					//int borderWidth = (int) Math.round(Math.min(new Double(hoogte) / 2, new Double(breedte) / 2));
					//style.setBorderWidth(borderWidth, Unit.PX);
					//style.setOpacity(0.4); 
					//mainPanel2.setPixelSize(breedte - 2 * borderWidth, hoogte - 2 * borderWidth);					
				}
					
				//
				
			}
			else
			{	style.setBorderColor(grijs.toString());
				style.setBorderWidth(randDikte = 5, Unit.PX);
				int extra = randDikte0 - randDikte;
				mainPanel2.setWidgetLeftRight(mainPanel, extra, Style.Unit.PX, extra, Style.Unit.PX);
				mainPanel2.setWidgetTopBottom(mainPanel, extra, Style.Unit.PX, extra, Style.Unit.PX);
// does not work, after setstate position with setHeight
				mainPanel2.setPixelSize(breedte - 2 * randDikte, hoogte - 2 * randDikte);

			}
		
		}
		else
		{
			if(colorSelection)
			{
				if(bgColorZichtbaar)
					style.setBackgroundColor(bgColor.toString());
				else {
					style.clearBackgroundColor();
					style.setBorderColor(randColor.toString());
//					style.setBorderWidth(randDikte, Unit.PX);
//					style.setOpacity(1);
				}
					
			}
			else
			{
				style.setBorderColor(randColor.toString());
				//randPanel.getElement().getStyle().setOpacity(1);
				style.setBorderWidth(randDikte = randDikte0, Unit.PX);
				mainPanel2.setWidgetLeftRight(mainPanel, 0, Style.Unit.PX, 0, Style.Unit.PX);
				mainPanel2.setWidgetTopBottom(mainPanel, 0, Style.Unit.PX, 0, Style.Unit.PX);
				mainPanel2.setPixelSize(breedte - 2 * randDikte, hoogte - 2 * randDikte);
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
				if (tekstVakken[i][j].pasAanH)
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
			if(stappen!=null && (i>stapNr-1 || stapNr==0))
			{	totaleHoogte = (totaleHoogte==0) ? cellSpaceRow : totaleHoogte;
				break;
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
		if (!visible || !layerVisible)
		{
			totaleHoogte = 0;
			totaleBreedte = 0;
		}

		if (!callOut)
		{
			setCurrentSize(totaleBreedte, totaleHoogte + feedbackPanelHeight);
		}
		
		plaatsTabelRanden();

		if (callOut)
		{
			mainPanel2.setPixelSize(breedtes.get(0).intValue(), hoogtes.get(0).intValue());
			//resizeRandPanel();
			//TODO: replace by resizeCallOutPanel?
		}

		if (zoomKolom != null) { // breedtes is niet in effect.
		  int i = zoomRij.intValue();
		  int j = zoomKolom.intValue();
          tekstVakken[i][j].setSize( breedte, hoogte);
          tekstVakken[i][j].setAshoogte(ashoogtes[i]);	  
		} else {
		
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
		}
		if (parent != null)
		{
			if (!callOut && isZwevend() && this.getAsPanel().isAttached())
			{
			  Widget p = getAsPanel().getParent();
			  if (p == parent) {  
			  
				parent.setWidgetLeftWidth(this.getAsPanel(), this.getLocationX(), Style.Unit.PX, totaleBreedte,
					Style.Unit.PX);
				parent.setWidgetTopHeight(this.getAsPanel(), this.getLocationY(), Style.Unit.PX, totaleHoogte,
					Style.Unit.PX);
			  } else {
			    LOG.severe("parent mismatch");
			    if (p instanceof LayoutPanel) {
	               ((LayoutPanel) p).setWidgetLeftWidth(this.getAsPanel(), this.getLocationX(), Style.Unit.PX, totaleBreedte,
	                    Style.Unit.PX);
	                ((LayoutPanel) p).setWidgetTopHeight(this.getAsPanel(), this.getLocationY(), Style.Unit.PX, totaleHoogte,
	                    Style.Unit.PX);
		      
			    }
			  }
			}
			if (!vulHoogte)
				parent.resize();

		}

		// eventueel opvullen hoogtes in tekstvakken regelen en hoogtes symbolen
		// instellen
		corrigeerOpvulHoogtes();
		vulSymboolHoogtes();
		if(parentStappen != null)
			parentStappen.resize();
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
		
		if(!laatsteVak && !inactive)
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
		else if(inactive)
		{ return false;
		}
		
		
		//als omliggende tekstvak bestaat: doorgeven naar omliggende tekstvak
		if(parent != null &&!randomPositioned)
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
					{	doorzochtDoorTab = false;
						return true;
					}
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
		
		if(!eersteVak && !inactive)
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
		else if(inactive)
		{ return false;
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
			mainPanel2.getElement().getStyle().clearWidth();
			mainPanel2.getElement().getStyle().clearHeight();
			
			mainPanel2.setWidgetLeftRight(mainPanel, randDikte - 5, Style.Unit.PX, randDikte - 5, Style.Unit.PX);
			mainPanel2.setWidgetTopBottom(mainPanel, randDikte - 5, Style.Unit.PX, randDikte - 5, Style.Unit.PX);
			if(sleepbaar && sleepHandle)
			{	mainPanel2.setWidgetLeftWidth(crosshair, -5, Style.Unit.PX, 20, Style.Unit.PX);
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
		Expressie waarde = new BasisExpressie(0);
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
		for (int i = 0; i < interactionViewObjects.size(); i++)
		{
			Object object = interactionViewObjects.get(i);
			if (object instanceof FormuleEditorWithAnswer)
			{
				FormuleEditorWithAnswer object2 = (FormuleEditorWithAnswer) object;
				changed = changed || object2.isChanged();
			}
		}
		return changed;
	}
	
	public void ipObjectResetFeedbackImage()
	{
		for (int i = 0; i < interactionViewObjects.size(); i++)
		{
			Object object = interactionViewObjects.get(i);
			if (object instanceof FormuleEditorWithAnswer)
			{
				FormuleEditorWithAnswer object2 = (FormuleEditorWithAnswer) object;
				object2.resetimg();
			}
		}
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
		if(comRoot.hasListeners(TVP_CLICK)) {
			fireEvent(CLICK_EVENT);
		}
		
		if(selectable && !sleepbaar && !sealed)
		{
			selected = !selected;
			setSelected(selected);
			//adviseMe();
			if(selected)
				fireEvent(SELECT_EVENT);
			else
				fireEvent(DESELECT_EVENT);
			return;
		}
		
		if(backButton)
		{
			if(isInIdeasStatistiek() != null)
				isInIdeasStatistiek().backAction();
			else if(findStappenVak() != null)
				findStappenVak().backAction();
		}

		if(hintButton)
		{
			if(isInIdeasStatistiek() != null)
			{
				getHintIdeasStatistiek();
				return;
			}
		}
		
		if(!sleepbaar && !draaibaar)
		{
			return;
		}
		
		startX = eventX - locationX;
		startY = eventY - locationY;
		
		int dx = eventX - locationX - parent.getAbsoluteLeft() - breedte/2;
		int dy = eventY - locationY - parent.getAbsoluteTop() - hoogte/2;
		startHoek = Math.atan2(dy, dx);
	}
	
	public void mouseMoveTouchMoveAction(int eventX, int eventY)
	{
		if(sleepbaar)
		{
			int mx = startX - parent.getAbsoluteLeft() - breedte/2;
			int my = startY - parent.getAbsoluteTop() - hoogte/2;
			if(!draaibaar || mx*mx+my*my<900) { //alleen versleepbaar indien aangepakt in het midden
				locationX = eventX - startX;
				locationY = eventY - startY;
				
				if(parent != null && zwevend)
				{	
					if(!draaibaar) { // meer sleepruimte indien draaibaar
						locationX = Math.max(locationX, 0);
						locationX = Math.min(locationX, parent.getOffsetWidth() - breedte);
						locationY = Math.max(locationY, 0);
						locationY = Math.min(locationY, parent.getOffsetHeight() - hoogte);
					}
					
					parent.remove(this.asWidget());
					parent.add(this.asWidget());
					parent.setWidgetLeftWidth(this.asWidget(), locationX, Style.Unit.PX, breedte, Style.Unit.PX);
					parent.setWidgetTopHeight(this.asWidget(), locationY, Style.Unit.PX, hoogte, Style.Unit.PX);
					
					{	Map<String,Object> mapx = new HashMap<String,Object>();
						mapx.put("name", "xcoordinate");
						mapx.put("value", locationX);
						fireEvent(new CBookEvent(this,"double.xcoordinate",mapx));
					}
					
					{	Map<String,Object> mapy = new HashMap<String,Object>();
						mapy.put("name", "ycoordinate");
						mapy.put("value", locationY);
						fireEvent(new CBookEvent(this,"double.ycoordinate",mapy));
					}
					LOG.info("zetLocatie");
					
				}
			}
		}
		if(draaibaar && parent != null && zwevend)
		{
			int dx = eventX - locationX - parent.getAbsoluteLeft() - breedte/2;
			int dy = eventY - locationY - parent.getAbsoluteTop() - hoogte/2;
			
			double dhoek = Math.atan2(dy,dx)-startHoek;
			draaihoek = draaihoek + dhoek*180.0/Math.PI;
			
			
			int mx = startX - parent.getAbsoluteLeft() - breedte/2;
			int my = startY - parent.getAbsoluteTop() - hoogte/2;
			if(mx*mx+my*my>900) {
				mainPanel2.getElement().getStyle().setProperty("transform", "rotate(" + (draaihoek) + "deg)");
				mainPanel2.getElement().getStyle().setProperty("WebkitTransform", "rotate(" + (draaihoek) + "deg)");
				startHoek = startHoek+dhoek;
				{	Map<String,Object> mapy = new HashMap<String,Object>();
					mapy.put("name", "rotation");
					mapy.put("value", draaihoek);
					fireEvent(new CBookEvent(this,"double.rotation",mapy));
				}
			}
		}
		
	}
	
	public void mouseUpTouchEndAction(int eventX, int eventY)
	{
		if (sleepbaar && !draaibaar)
		{
			//hier zorgen dat de pagina wordt versleept?
			locationX = eventX - startX;
			locationY = eventY - startY;
			if (parent != null && zwevend)
			{
				locationX = Math.max(locationX, 0);
				locationX = Math.min(locationX, parent.getOffsetWidth() - breedte);
				locationY = Math.max(locationY, 0);
				locationY = Math.min(locationY, parent.getOffsetHeight() - hoogte);
	
				if (sleepSnap) 
				{
					if (doelPosities != null) 
					{
						boolean snapped = false;
						for (int i = 0; i < doelPosities.length ; i++)
						{
							int dx = (int) Math.abs(locationX - doelPosities[i].getX());
							int dy = (int) Math.abs(locationY - doelPosities[i].getY());
							
							boolean in = isBinnen(sleepDoelen[i]);

							if (sleepSnap)
							{
								if (in)
								{
									snapped = true;
									break;
								}
								else if (!in && isBinnenMarge(sleepDoelen[i])) // check of erbuiten valt maar binnen de marge
								{
									Point p = findLocationWithin(sleepDoelen[i]);
									locationX = (int) p.getX();
									locationY = (int) p.getY();
									
									snapped = true;
									break;
								}
							}
						}
						if (!snapped && relocate)
						{
							locationX = startSleepX;
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
		else
		{
// Werk dit? FIXME naar de link api.		
			if (isLink && linkUrls != null)
			{
				String link = linkUrls.getString(0);
				if (link.startsWith(Actions.PROTO)) {
					Actions.goTo(link);
				} else
				if (link.startsWith("#") && anchorContext != null) {
					anchorContext.gotoPlace(link.substring(1));
				} else
				if (anchorContext == null || !link.startsWith("goto:"))
					Window.open(link, "_blank", "");
				else
					anchorContext.gotoUrl(link);
			}
		}
	}
	
	/**
	 * Bepaal de locatie zodat this (sleepobject) binnen het gegeven sleepdoel valt.
	 *  
	 * @param sleepDoel
	 * @return
	 */
	private Point findLocationWithin(TekstVakPanel sleepDoel)
	{
		Point p = null;
		int pointX = -1;
		int pointY = -1;
		
		int sleepObjectX = getLocationX();
		int sleepObjectY = getLocationY();
		int sleepObjectBreedte = getWidth();
		int sleepObjectHoogte = getHeight();

		int sleepDoelX = sleepDoel.getLocationX();
		int sleepDoelY = sleepDoel.getLocationY();
		int sleepDoelBreedte = sleepDoel.getWidth();
		int sleepDoelHoogte = sleepDoel.getHeight();

		// check x coordinate
		if (sleepObjectX < sleepDoelX)
			pointX = Math.min(sleepObjectX + sleepdoelMarge, sleepDoelX); // nooit groter dan sleepDoelX
		else if (sleepObjectX + sleepObjectBreedte > sleepDoelX + sleepDoelBreedte)
			pointX = Math.max(sleepObjectX - sleepdoelMarge, sleepDoelX); // nooit kleiner dan sleepDoelX
		else
			pointX = sleepObjectX;
		
		// check y coordinate
		if (sleepObjectY < sleepDoelY)
			pointY = Math.min(sleepObjectY + sleepdoelMarge, sleepDoelY); // nooit groter dan sleepObjectY
		else if (sleepObjectY + sleepObjectHoogte > sleepDoelY + sleepDoelHoogte)
			pointY = Math.max(sleepObjectY - sleepdoelMarge, sleepDoelY); // nooit kleiner dan sleepDoelY
		else
			pointY = sleepObjectY;
		
		p = new Point(pointX, pointY);
		
		return p;
	}

	/**
	 * True als this (sleepobject) binnen de marge van het gegeven sleepdoel valt.
	 * 
	 * @param sleepDoel
	 * @return
	 */
	private boolean isBinnenMarge(TekstVakPanel sleepDoel)
	{
		boolean isBinnenMarge = false;
		int sleepObjectX = getLocationX();
		int sleepObjectY = getLocationY();
		int sleepObjectBreedte = getWidth();
		int sleepObjectHoogte = getHeight();

		TekstVakPanel sleepDoelVak = (TekstVakPanel) sleepDoel;
		int sleepDoelX = sleepDoelVak.getLocationX();
		int sleepDoelY = sleepDoelVak.getLocationY();
		int sleepDoelBreedte = sleepDoelVak.getWidth();
		int sleepDoelHoogte = sleepDoelVak.getHeight();
		
		if (sleepObjectX > sleepDoelX - sleepdoelMarge // check x-coordinaat
			&& sleepObjectX < (sleepDoelX + sleepDoelBreedte - sleepObjectBreedte + sleepdoelMarge)
			&& sleepObjectY > sleepDoelY - sleepdoelMarge // check y-coordinaat
			&& sleepObjectY < sleepDoelY + sleepDoelHoogte - sleepObjectHoogte + sleepdoelMarge)
		{
			isBinnenMarge = true;
		}
		
		return isBinnenMarge;
	}

	/**
	 * True als this (sleepobject) binnen het gegeven sleepdoel valt.
	 * 
	 * @param sleepDoel
	 * @return
	 */
	private boolean isBinnen(TekstVakPanel sleepDoel)
	{
		boolean isBinnen = false;
		int sleepObjectX = getLocationX();
		int sleepObjectY = getLocationY();
		int sleepObjectBreedte = getWidth();
		int sleepObjectHoogte = getHeight();

		int sleepDoelX = sleepDoel.getLocationX();
		int sleepDoelY = sleepDoel.getLocationY();
		int sleepDoelBreedte = sleepDoel.getWidth();
		int sleepDoelHoogte = sleepDoel.getHeight();
		
		if (sleepObjectX > sleepDoelX // check x-coordinaat
			&& sleepObjectX < (sleepDoelX + sleepDoelBreedte - sleepObjectBreedte)
			&& sleepObjectY > sleepDoelY // check y-coordinaat
			&& sleepObjectY < sleepDoelY + sleepDoelHoogte - sleepObjectHoogte)
		{
			isBinnen = true;
		}

		return isBinnen;
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
			//e.preventDefault();
			//e.stopPropagation();
			if(hasPointerEventSupport)
				return;
			
			
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
		{	
			//e.preventDefault();
			//e.stopPropagation();
			
			if(hasPointerEventSupport)
				return;
			// prevent scrolling
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

			if((sleepveld||sleepbaar||draaibaar) && mouseDown)
			{	e.preventDefault();
				mouseMoveTouchMoveAction(eventX, eventY);
			}
			
			
		} // onMouseMove
		
		public void onMouseUp(MouseUpEvent e)	
		{	
			//e.preventDefault();
			//e.stopPropagation();
			
			if(hasPointerEventSupport)
				return;
			// prevent scrolling
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
			//e.preventDefault();
			//e.stopPropagation();
			
			if(hasPointerEventSupport)
				return;
			
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
	
	boolean hasPointerEventSupport = false;
	
	class PointerHandler implements PointerDownHandler, PointerMoveHandler, PointerUpHandler, PointerCancelHandler {

		private boolean mouseDown = false;
		
		@Override
		public void onPointerUp(PointerUpEvent event) {
			//event.preventDefault();
			event.stopPropagation();
			//LOG.info("pointerUp");
			if(!editable) return;
			if(sleepbaar || selectable) {
				event.preventDefault();
				//event.stopPropagation();
			}
			int eventX = locationX + startX;
			int eventY = locationY + startY;
			
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
			
			mouseUpTouchEndAction(eventX, eventY);	
			
		}

		@Override
		public void onPointerMove(PointerMoveEvent event) {
			//event.preventDefault();
			event.stopPropagation();
			
			if(!editable) return;
			
			int eventX = event.getClientX();
			int eventY = event.getClientY();
			
			if(sleepveld || sleepbaar && mouseDown)
			{	event.preventDefault();
				//event.stopPropagation();
				mouseMoveTouchMoveAction(eventX, eventY);
			}
			
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
			
		}

		@Override
		public void onPointerDown(PointerDownEvent event) {
			//event.preventDefault();
			event.stopPropagation();
			hasPointerEventSupport = true;
		//LOG.info("pointerDown");
			int ex = event.getRelativeX(getAsPanel().getElement());
			int ey = event.getRelativeY(getAsPanel().getElement());
			
			if(sleepbaar && sleepHandle && (ex > 20 || ey > 20))
			{	event.preventDefault();
				return;
			}
			mouseDown = true;
			
			int eventX = event.getClientX();
			int eventY = event.getClientY();
			mouseDownTouchStartAction(eventX, eventY);	
			
		}

		@Override
		public void onPointerCancel(PointerCancelEvent event) {
			event.preventDefault();
			event.stopPropagation();
			LOG.info("pointerCancel");
		}
	
		public boolean isMouseDown()
		{
			return mouseDown;
		}
		
		public void setMouseDown(boolean b)
		{
			mouseDown = b;
		}
		
	}
	class TouchHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{
		
		public void onTouchStart(TouchStartEvent e)
		{
			//e.preventDefault();
			e.stopPropagation();
			if(hasPointerEventSupport)
				return;
			
			if(!editable) return;
			
			if(e.getTouches().length() == 0)
			{	return;
			}
			
			Touch touch = e.getTouches().get(0);
			
			if((sleepbaar && sleepHandle && (touch.getPageX() - getAsPanel().getAbsoluteLeft() > 20 || 
					touch.getPageY() - getAsPanel().getAbsoluteTop() > 20)))
			{	e.preventDefault();
				return;
			}
			
			
			int eventX = touch.getClientX();
			int eventY = touch.getClientY();
			mouseDownTouchStartAction(eventX, eventY);
			
			
		}
		public void onTouchMove(TouchMoveEvent e)
		{
			//e.preventDefault();
			e.stopPropagation();
			if(hasPointerEventSupport)
				return;
			
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
			
			if(sleepveld||sleepbaar)
			{	e.preventDefault();
				mouseMoveTouchMoveAction(eventX, eventY);
			}
				
		}
		public void onTouchEnd(TouchEndEvent e)
		{
			LOG.info("TekstVakPanel.TouchHandler.onTouchEnd()");
			//e.preventDefault();
			e.stopPropagation();
			if(hasPointerEventSupport)
				return;
			
			Touch touch = e.getTouches().get(0);
			
			
			if(!editable) return;
			if(sleepbaar || selectable)
				e.preventDefault();
			
//			if(sleepbaar && sleepHandle && (touch.getPageX() - getAsPanel().getAbsoluteLeft() > 20 || 
//					touch.getPageY() - getAsPanel().getAbsoluteTop() > 20))
//			{	e.preventDefault();
//				return;
//			}
			int eventX = locationX + startX;
			int eventY = locationY + startY;
			mouseUpTouchEndAction(eventX, eventY);
			
			  
		}

	}
	
	public int getFirstRowMinHeight(TekstVak tv)
	{
		if (tv!=tekstVakken[0][0] || !pasAanH) 
			return 0;
		int minHeight = 2*bovenMarge;
		if (inklapbaar && klapUitButton!=null ) 
		{	
			if (knopImageView1 != null)
				minHeight = minHeight + DWOplayer.DWO_BUNDLE.klapuit1().getHeight();
			else if (view1 != null) 
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
	
	void setPopupOpened() {
		fireEvent(POPUP_EVENT);
	}

	public HandlerRegistration addCBookEventListener(CBookEventListener listener) {
		return activity.getEventBus().addHandlerToSource(CBookEvent.TYPE, this, listener);
	}

	private void fireEvent(CBookEvent event) {
		activity.getEventBus().fireEventFromSource(event, this);
		comRoot.fireEvent(event);
	}
	
	private static final String ACTION_SETVISIBLE = "action.setVisible";
	private static final String ACTION_SETNOTVISIBLE = "action.setNotVisible";
	private static final String TEXT_CONTENT = "text.content";
	
	@Override
	public void acceptCBookEvent(CBookEvent event) {
		String command = event.getCommand();
		LOG.info("accept " + command + " s:" + event.getSource() + " id:" + getLogID());
		if(ACTION_SETVISIBLE.equals(command)) {
			setVisibility(layerVisible);
		}
		else if(ACTION_SETNOTVISIBLE.equals(command)) {
			setVisibility(false);
		}
		else if(TEXT_CONTENT.equals(command)) {
			if(isInIdeasStatistiek().equals(null)) {
				Map map = (Map)event.getParameters();
				if(map!=null) {
					String contentString = ((String)map.get("content"));
					ArrayList<Object> objectArray = new ArrayList<Object>();
					objectArray.add(contentString);
					tekstVakken[0][0].clearRegels();
					tekstVakken[0][0].setObjects(objectArray);
					LOG.info("was here");
				}
				return;
			}
			
			
			
			if(stappen == null)
				stappen = new ArrayList<Object>(); //ObjectMap[hoogtes.size()];
			Map map = (Map)event.getParameters();
			
			if(map!=null)
			{	ObjectMap objectMap = JSONUtilities.wrapMap(map);
				
				//Heeft omliggende (statistiek)TekstVakPanel nog een feedbackPanel? Dan weghalen.
				removeFeedback();
							
				try{
					String contentString = ((String)map.get("content"));
					if(contentString.startsWith("back"))
					{	
						backAction();
						return;
					}
				}
				catch(Exception e){
					ObjectMap contentMap = objectMap.getObjectMap("content");
					maakStap(contentMap, randomVarNamen, randomVarWaarden);
					
				}
			}
		}
		else if(TVP_SELECT.equals(command)) {
			if(!selected && !sealed) {
				setSelected(true);
				//fireEvent(SELECT_EVENT);
			}
		}
		else if(TVP_DESELECT.equals(command)) {
			if(selected && !sealed) {
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
			if (!activity.isReview()) seal(event);
		} else if ("action.zoom".equals(command)) {
		      zoomAction();
		} else if ("action.unzoom".equals(command)) {
		    unzoomAction();
		} else if ("action.setActive".equals(command)) {
		     inactive = false;
		     verwijderKlikPanel();
		} else if ("action.setInactive".equals(command)) {
			inactive = true;
			if (!activity.isReview()) zetKlikPanel();
			comRoot.getKeyboard().setEditor(null);
			comRoot.getKeyboard().blur();
			logger.info("inactive");
		}
		else if("double.xcoordinate".equals(command)) {
			Map map = (Map)event.getParameters();
			ObjectMap objectMap = JSONUtilities.wrapMap(map);
			if(map!=null) {
				int xcoordinate = objectMap.getInt("value");
				zetLocatie(xcoordinate, this.getLocationY());
			}
		}
		else if("double.ycoordinate".equals(command)) {
			Map map = (Map)event.getParameters();
			ObjectMap objectMap = JSONUtilities.wrapMap(map);
			if(map!=null) {
				int ycoordinate = objectMap.getInt("value");
				zetLocatie(this.getLocationX(), ycoordinate);
			}
		}
		else if("double.rotation".equals(command)) {
			Map map = (Map)event.getParameters();
			ObjectMap objectMap = JSONUtilities.wrapMap(map);
			if(map!=null) {
				draaihoek = objectMap.getInt("value");
				logger.info("draaihoek = "+draaihoek);
				mainPanel2.getElement().getStyle().setProperty("transform", "rotate(" + (draaihoek) + "deg)");
				mainPanel2.getElement().getStyle().setProperty("WebkitTransform", "rotate(" + (draaihoek) + "deg)");
			}
		}
		
	}

	
	public void removeFeedback()
	{
		TekstVakPanel statPanel = isInIdeasStatistiek();
		if(statPanel != null && statPanel.feedbackPanel != null)
		{	statPanel.feedbackPanel.removeFromParent();
			statPanel.feedbackPanelHeight = 0;
			statPanel.removeFeedbackImage();
		}
		//feedbackPanelHeight = 0;
	}
	
	private boolean sealed; // no selects/
	private void seal(CBookEvent event) {
// no seal?
		this.sealed = true;
		for (Object object : interactionViewObjects)
		{
			if (object instanceof CBookEventListener)
				((CBookEventListener) object).acceptCBookEvent(event);
		}
	}
	
	public boolean forwardStep(int index,ArrayList<Object> stepContents, String[] randomVarNamen, HashMap<String, Number> randomVarWaarden) {
	  int size = hoogtes.size();
      List<Object> opdrObjects = tekstVakken[size-1][kolom].getOpdrachtObjects();
	  interactionViewObjects.removeAll(opdrObjects);
      tekstVakken[size-1][kolom].clear();
      ArrayList<Object> empty = new ArrayList<Object>();
      tekstVakken[size-1][kolom].zetOpdrachtObjects(empty, empty);
	  mainPanel.removeRow(size-1);
	  mainPanel.insertRow(index);

      TekstVak[] safe = tekstVakken[size-1];
      System.arraycopy(tekstVakken, index, tekstVakken, index+1, size-index-1);
      tekstVakken[index] = safe;
      for (int i = 0; i < safe.length; i++ ) mainPanel.setWidget(index, i, safe[i]);
      Double h = hoogtes.get(size-1);
      for(int i = index; i < size; i++) {
        Double x = hoogtes.get(i);
        hoogtes.set(i, h);
        h = x;
      }
      stappen.add(index, stepContents==null ? empty : stepContents);
      stapNr++;

      if(stepContents != null) 
      {   addStepContents( stepContents, randomVarNamen, randomVarWaarden, index, kolom);
          setVisibility(layerVisible);
      }
	  
	  return true;
	}
	
	
	
	
	public boolean backStep(int index) {
	  //if (index == stapNr-1) return backAction();
	  List<Object> opdrObjects = tekstVakken[index][kolom].getOpdrachtObjects();
	  for(Object o : opdrObjects) interactionViewObjects.remove(o);
	  tekstVakken[index][kolom].clear();
      ArrayList<Object> empty = new ArrayList<Object>();
      tekstVakken[index][kolom].zetOpdrachtObjects(empty, empty);
// move tekstVakken[index] to bottom
      mainPanel.removeRow(index);
      int size = hoogtes.size();
      mainPanel.insertRow(size-1);
      TekstVak[] safe = tekstVakken[index];
      System.arraycopy(tekstVakken, index+1, tekstVakken, index, size-index-1);
      tekstVakken[size-1] = safe;
      for (int i = 0; i < safe.length; i++ ) mainPanel.setWidget(size-1, i, safe[i]);

      Double h = hoogtes.get(index);
      for(int i = size-1; i >= index; i--) {
        Double x = hoogtes.get(i);
        hoogtes.set(i, h);
        h = x;
      }
      stappen.remove(index);
      stapNr--;
      resize();
      goedHalfFoutStatistiek = AntwoordVakChecker.GEEN;
	  return true;
	}
	
	
	
	public boolean backAction()
	{
		//back has to be performed on this TekstVakPanel
		if(stappen != null && stappen.size() > 0)
		{
			ArrayList<Object> opdrObjects = tekstVakken[stapNr-1][kolom].getOpdrachtObjects();
			interactionViewObjects.removeAll(opdrObjects);
			tekstVakken[stapNr-1][kolom].clear();
			ArrayList<Object> empty = new ArrayList<Object>();
            tekstVakken[stapNr-1][kolom].zetOpdrachtObjects(empty, empty);
			if(stapNr > 0)	
				stapNr--;
			if(stappen.size() > stapNr)
				stappen.remove(stapNr);
			resize();
			goedHalfFoutStatistiek = AntwoordVakChecker.GEEN;
			return true;
		}
		else //back has to be performed on TekstVakPanel containing stappen, somewhere in this TekstVakPanel
		{
			if(feedbackPanel != null)
			{	feedbackPanel.removeFromParent();
				feedbackPanelHeight = 0;
				removeFeedbackImage();
			}
			
			for(int i = 0; i < interactionViewObjects.size(); i++)
			{
				Object object = interactionViewObjects.get(i);
				if(object instanceof TekstVakPanel)
				{
					if(((TekstVakPanel) object).backAction())
						return true;
				}
			}
			return false;
		}
			
	}
	
	public void maakStap(ObjectMap contentMap, String[] randomVarNamen, HashMap<String, Number> randomVarWaarden)
	{
		//Heeft omliggende (statistiek)TekstVakPanel nog een feedbackPanel? Dan weghalen.
		TekstVakPanel statPanel = isInIdeasStatistiek();
		if(statPanel != null && statPanel.feedbackPanel != null)
		{	statPanel.feedbackPanel.removeFromParent();
			statPanel.feedbackPanelHeight = 0;
			statPanel.removeFeedbackImage();
		}
		
		if(stappen.size() > stapNr)
			stappen.remove(stapNr);
		if(isFull())
			return;
		stappen.add(contentMap);
		stapNr++;
		
		if(contentMap != null) 
		{	addTekstVakPanel((HashMap<String, Object>) contentMap, randomVarNamen, randomVarWaarden, stapNr - 1, kolom);
			setVisibility(layerVisible);
		}
	}
	
	public void maakStapNieuw(ArrayList<Object> stepContents, String[] randomVarNamen, HashMap<String, Number> randomVarWaarden) //randomwaarden nodig? kennen we die niet al?
	{
		//Heeft omliggende (statistiek)TekstVakPanel nog een feedbackPanel? Dan weghalen.
		TekstVakPanel statPanel = isInIdeasStatistiek();
		if(statPanel != null && statPanel.feedbackPanel != null)
		{	statPanel.feedbackPanel.removeFromParent();
			statPanel.feedbackPanelHeight = 0;
			statPanel.removeFeedbackImage();
		}
		
		if(stappen.size() > stapNr)
			stappen.remove(stapNr);
		if(isFull())
			return;
		stappen.add(stepContents);
		stapNr++;
		
		if(stepContents != null)
		{
			addStepContents(stepContents, randomVarNamen, randomVarWaarden, stapNr - 1, kolom);
			setVisibility(layerVisible);
		}
	}

  public boolean isFull() {
    return stapNr >= tekstVakken.length;
  }

	// visible (default) or hidden.
	private void setVisibility(boolean b) {
		boolean visibilityChanged = false;
		if(visible != b)
			visibilityChanged = true;
		visible = b;
		facade.setVisibility(b);
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
				
				if (b && (callOutPanel.getOffsetWidth() == 0 || callOutPanel.getOffsetHeight() == 0))
					callOutPanel.setPixelSize(breedte + 2 * randDikte, hoogte + 2 * randDikte); // als callOutPanel eerst onzichtbaar was, moet de size gezet worden 
				
				drawCallOutCanvas(callOutCanvas);
			}
			setCurrentSize( breedte, hoogte);
		}
		else
		{	style.setVisibility(Visibility.HIDDEN); 
			setCurrentSize( 0, 0);
		}
		if(visibilityChanged)
		{
			//Objects may have to move to next TextRegel, so clear parent and setObjects again. 
		    parent.reLayout();
		}
		resize();
	}
	
	private void addTekstVakPanel(HashMap<String, Object> contentMap, String[] randomVarNamen, HashMap<String, Number> randomVarWaarden, int row, int column)
	{
		TekstVakPanel tvp = new TekstVakPanel(activity, contentMap, randomVarNamen, randomVarWaarden, JSONUtilities.wrapMap(contentMap).getInt("breedte"));
		tvp.setParent(tekstVakken[row][column]);
		tvp.zetInstellingen(instellingen);
		tvp.setKeyboard(kb);
		final Object orgObject = tvp;
		OpdrNavIF comRoot2 = comRoot;
		Connector connector = find(tvp);
		comRoot2 = new OpdrNavContext(comRoot,connector, this.bgColorZichtbaar ? bgColor : comRoot.getBackground());
		((InteractionView) orgObject).setCommunicationRoot(comRoot2);
		if(! (tvp instanceof StateLess))
		{	interactionViewObjects.add(orgObject);
		}
							
		HashMap<String, Object> launchState = (HashMap<String, Object>) (contentMap.get("interactiePanelLaunchState"));
		tvp.zetOpdracht(launchState);
		tvp.setContainer(new TekstVakContext(row, column));
		xWidgetMap.putAll(tvp.xWidgetMap);
		Connector.calculateSubscriptions(xWidgetMap.values());
		ArrayList<Object> list = new ArrayList<Object>();
		list.add(tvp);
		tekstVakken[row][column].zetOpdrachtObjects(list,list);
		tekstVakken[row][column].setObjects(list);
		
		//setVisibility should not yet been done in setState, so leave out of this method and add separately in maakStap. 
		//setVisibility(layerVisible);
	}
	
	private void addStepContents(ArrayList<Object> stepContents, String[] randomVarNamen, HashMap<String, Number> randomVarWaarden, int row, int column)
	{
		TekstBuffer tb = new TekstBuffer(activity, randomVarNamen, randomVarWaarden, null);
		int[] breedtes = new int[1];
		breedtes[0] = (int) tekstVakken[row][column].tekstVakBreedte;
		tb.zetVolleBreedtes(breedtes);
		ArrayList<Object> opdrachtObjects = new ArrayList<Object>();
		ArrayList<Object> opdrachtGegevens = new ArrayList<Object>();
		
		for(int i = 0; i < stepContents.size(); i++)
		{
			Object object = stepContents.get(i);
			if(object instanceof String)
			{
				String objectNoBreaks = ((String) object).replaceAll("\n", " \n ");
				String[] result = objectNoBreaks.split("\n");
				for(int j = 0; j < result.length; j++)
					opdrachtObjects.add(result[j]);
			}
			else if(object instanceof ObjectMap)
			{
				Object vak = tb.getVak0((HashMap<String, Object>) object);
				opdrachtObjects.add(vak);
				opdrachtGegevens.add(object);
			}
		}
		
		tekstVakken[row][column].zetOpdrachtObjects(opdrachtObjects, opdrachtObjects);
		initialiseerObjects(opdrachtObjects, opdrachtGegevens, row, column, 0);
		tekstVakken[row][column].setObjects(opdrachtObjects);
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
		int breedteView1, hoogteView1, hoogteView2, hoogteMasterView;
		final int inklapKnopPos = this.inklapKnopPos;
		
		
		if (knopImageView1 != null && knopImageView1.exists()) 
		{
			// eigen plaatje gekozen voor in- en uitklappen
			view1 = this.view1 = knopImageView1.getImage();
			breedteView1 = view1.getWidth();
			hoogteView1 = view1.getHeight();
		} 
		else
		{
			view1 = this.view1 = new Image(DWOplayer.DWO_BUNDLE.klapuit1().getSafeUri());
			breedteView1 = DWOplayer.DWO_BUNDLE.klapuit1().getWidth();
			hoogteView1 = DWOplayer.DWO_BUNDLE.klapuit1().getHeight();
		}

		if (knopImageView2 != null && knopImageView2.exists())
		{
			// eigen plaatje gekozen voor in- en uitklappen
			view2 = knopImageView2.getImage();
			hoogteView2 = view2.getHeight();
		}
		else
		{
			view2 = new Image(DWOplayer.DWO_BUNDLE.klapuit2().getSafeUri());
			hoogteView2 = DWOplayer.DWO_BUNDLE.klapuit2().getHeight();
		}
		
		//In deze implementatie ga ik er voorlopig vanuit dat view1 en view2 dezelfde maat hebben.
		final int breedtePanel = (checkUitklapVak && !isNoordhoff()) ? breedteView1 + 20 : breedteView1;
		int hoogteKnop = hoogteView1;
		
		klapUitPanel.setPixelSize(breedtePanel, hoogteKnop);
		
		final Image masterView = ingeklapt ? view2 : view1;
		
		hoogteMasterView = ingeklapt ? hoogteView2 : hoogteView1;
		
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
				int height = hoogteMasterView;
				
				//setSizeUitklapButton(breedtePanel, hoogtes.get(0).intValue());
				//klapUitButton.setPixelSize(width, hoogtes.get(0).intValue());
				
				setPositionUitklapButton(layoutPanel, breedtePanel, height);
				
			}
		};
		
		// determine height
		int height = 0;
		
		if (knopImageView1 != null)
		{
			height = knopImageView1.getHeight();
		}
		else if (DWOplayer.DWO_BUNDLE.klapuit1().getHeight() > 0)
		{
			height = DWOplayer.DWO_BUNDLE.klapuit1().getHeight();
		}
		
		if (height > 0)
		{
			layoutPanel.insert(klapUitPanel, 0);

			setPositionUitklapButton(layoutPanel, breedtePanel, height);
		}
		else if (masterView.getWidth() > 0)
		{
			//layoutPanel.insert(klapUitButton,0);
			layoutPanel.insert(klapUitPanel, 0);
			handler.onLoad(null);
		}
		else 
		{
			masterView.addLoadHandler(handler);
			//layoutPanel.insert(klapUitButton,0);
			layoutPanel.insert(klapUitPanel, 0);
		}
		layoutPanel.resize();
	}


	private boolean isKlapvakCorrect() {
		
		boolean vakinhoudCorrect = true;
// https://numworx.atlassian.net/browse/DWOWIDGET-172
// er is geen check op zelftoets gedaan en zichtbaar
// bij eindtoets nooooooooit feedback, tenzij "verzegeld".
		if ((mode == OpdrNavIF.EINDTOETS || mode == OpdrNavIF.ZELFTOETS) && lessonmode == LessonMode.normal)
			return false;
		
		
		
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
		if(!RESPONSIVE && zoomKolom == null) {
			return;
		}
		zetVolledigeBreedte0alt(breedte);
	}

	
	Boolean lastEvent = null;
	private void fireLayoutAction(boolean small) {
		if (lastEvent != null && small == lastEvent.booleanValue()) return;
		final CBookEvent ev = new CBookEvent(small ? "action.smalllayout": "action.widelayout");
		Scheduler.get().scheduleDeferred(() -> comRoot.fireEvent(ev));
		lastEvent = Boolean.valueOf(small);
	}
	
	
	private void zetVolledigeBreedte0alt(int breedte) {
		
			// 1. deze kolom is uitgezoemd:
			if (zoomKolom != null) {

				if (breedte != this.breedte) {
					tekstVakken[zoomRij][zoomKolom].setSize(breedte, tekstVakken[zoomRij][zoomKolom].getHeight());
					this.breedte = breedte;
					tekstVakken[zoomRij][zoomKolom].reLayout(); 
				}
				return;
			}
			// 2. dit is een response tekstvakpanel met 1 tekstvak
			if (responsive && RESPONSIVE && visible && layerVisible) {
				//zetVolledigeBreedte1(breedte); // boekhouding....
				int w;	 			
			    w = (int)Math.round(responsiveFactor*breedte + responsiveConstant);
			    boolean small = w < responsiveMinWidth;
				if (small ) {
			    		w = breedte;			    		
			    }
			    else {
			    		w = w-1; // correctie voor afrondingen naar boven
			    }
			    if(w > responsiveMaxWidth)
			    		w = responsiveMaxWidth;						
//				if(Math.abs(this.breedte - w)>1 ) {
//					tekstVakken[0][0].setSize(w, tekstVakken[0][0].getHeight());
//					this.breedte = w;
//					breedtes.set(0,(double)w);
//					
//					tekstVakken[0][0].reLayout();
//				}
			    zetVolledigeBreedte1(w);
				fireLayoutAction(small);
				return;
			}
		if (volledigeBreedte && breedtes != null) {		// alle andere gevallen
			zetVolledigeBreedte1(breedte);
		}
	}
	
	
	
	
	
	
	
	
	private void zetVolledigeBreedte0(int breedte) {
    if(volledigeBreedte && breedtes!=null) {
/*
 * Onderstaande is niet goed als we zoomen. Dan is maar één kolom zichtbaar en de rest niet.
 */
		zetVolledigeBreedte2(breedte);
	}
		if(responsive && RESPONSIVE ) {
			int w = breedte;
 			
			    w = (int)Math.round(responsiveFactor*breedte + responsiveConstant);
			    boolean small = responsiveFactor*breedte + responsiveConstant< responsiveMinWidth || zoomKolom != null;
				if(small ) {
			    		w = breedte;
			    }
			    else {
			    		w = w-1; // correctie voor afrondingen naar boven
			    }
			    		
			    if(w > responsiveMaxWidth && zoomKolom == null)
			    		w = responsiveMaxWidth;
			
//			int w = breedte;
//			if(breedte>responsiveToggleWidth) 
//				w = breedte/2;
			
			
			if(Math.abs(this.breedte - w)>1 ) {
				tekstVakken[0][0].setSize(w, tekstVakken[0][0].getHeight());
				this.breedte = w;
				breedtes.set(0,(double)w);
				
				tekstVakken[0][0].reLayout();
			}
			fireLayoutAction(small);
		}
  }

  void zetVolledigeBreedte2(int breedte) {
    if (zoomKolom == null) {	
			zetVolledigeBreedte1(breedte);
		} else {
			tekstVakken[zoomRij][zoomKolom].setSize(breedte, tekstVakken[zoomRij][zoomKolom].getHeight());
			this.breedte = breedte;
			tekstVakken[zoomRij][zoomKolom].reLayout();
		}
  }

  private void zetVolledigeBreedte1(int breedte) {
	int huidigebreedte = visible && layerVisible ? this.breedte : this.breedte_oud;
	if(Math.abs(huidigebreedte - breedte)<2)
	{	LOG.info("zetVolledigebreedte weinig verschil");
		return;
	}
	int aantalKolommen = breedtes.size();
	int teVerdelenBreedte = huidigebreedte - (aantalKolommen-1)*cellSpaceColumn;
    double factor = 1.0*(breedte-(aantalKolommen-1)*cellSpaceColumn)/teVerdelenBreedte;
    double restbreedte = breedte-(aantalKolommen-1)*cellSpaceColumn;				
    double[] newBreedtes = new double[breedtes.size()];
    for(int i=0 ; i<aantalKolommen ; i++) {
    	if(i==aantalKolommen-1) {
    		for(int j=0 ; j<hoogtes.size() ; j++) 
    			tekstVakken[j][i].setSize((int)restbreedte, tekstVakken[j][i].getHeight());
    		newBreedtes[i] = restbreedte;		
    	}
    	else {
    		for(int j=0 ; j<hoogtes.size() ; j++)
    			tekstVakken[j][i].setSize((int)(breedtes.get(i)*factor), tekstVakken[j][i].getHeight());
    		newBreedtes[i] = breedtes.get(i)*factor;
    		restbreedte = restbreedte-breedtes.get(i)*factor;
    	}
    }
    restbreedte = breedte;
    for(int i=0 ; i<aantalKolommen ; i++) {
    	breedtes.set(i,newBreedtes[i]);
    }
    boolean veranderd = Math.abs(this.breedte - breedte)>1;
    if (visible && layerVisible)
    	this.breedte = breedte;
    else
    	this.breedte_oud = breedte;
    if(veranderd)
	    for(int i=0 ; i<aantalKolommen ; i++) {
	    	for(int j=0 ; j<hoogtes.size() ; j++) {
	    		tekstVakken[j][i].reLayout();
	    	}
	    }
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
	
	public boolean isNoordhoff()
	{		
		return activity.isNoordhoff();
	}
	
	private Logging dwologger;
	private boolean editable = true;
	
	private void setAttempt() {
		if(dwologger != null && !ideasStatistiek) {
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
	
	public void setRandomPositioned(boolean b) {
		randomPositioned = b;
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
		setPopupOpened();
	}

	@Override
	public void onHide() {
		facade.setPopupState(getState());
	}

	void setEditable(boolean editable) {
		this.editable = editable;	
	}
	
	private class SelectRule extends AbstractRule {
		private final String id2;
		private final String math;
		final private Map<String, String> context;

		private SelectRule(String id2, String math, Map<String, String> c) {
			this.id2 = id2;
			this.math = math;
			this.context = c;
		}

		@Override
		public String getExpr() {
			return math;
		}

		@Override
		public String getId() {
			return id2;
		}

		@Override
		public Map<String,String> getContext() {
			return context;
		}
		
	}

	RuleIF getSelectRule(String base, Map<String, String> context) {
		String id = base + (char) ('a'-1 + this.getIpId());
		String math = String.valueOf(this.isIpSelected());
		String label = this.getIpExpString();
		Map<String,String> c = new HashMap<>(context);
		c.put("label", label);
		SelectRule r = new SelectRule(id, math,c);
		return r;
	}

//	private void adviseMe() {
//		Optional<DwoGlobalVars> vars = activity.vars();
//		if (vars.isPresent() && vars.get().withUser() && logOption && comRoot.getLessonMode() == LessonMode.normal) {
//			String id = logID;
//			if(! id.startsWith("adviseMe:")) 
//				return;
//			String[] split = id.split(":");
//			String userid = vars.get().getUserID().toString();
//			String classid;
//			try {
//				classid = vars.get().getCurrentSchoolClass().getId().getIdString();
//			} catch (Exception e) {
//				classid = "";
//			}
//			String exerciseid = split[1];
//			String id2 = split[2];
//			Map<String,String> context = new HashMap<>();
//			context.put("userid", userid);
//			context.put("groupid", classid);
//			context.put("language", StubView.getLocale());
//			RuleIF[] math = new RuleIF[] { getSelectRule(id2, context) };
//			PromiseCallback<RuleIF> defer = new PromiseCallback<>();
//			WiskOpdr.ideas.adviseMe(math, exerciseid, defer );
//			activity.agent().addBarrier(defer.getPromise());
//			defer.getPromise().onResolve(() -> { 
//				Promise<RuleIF> p = defer.getPromise();
//				Throwable t = p.getFailure();
//				if ( t != null) {
//					LOG.log(Level.SEVERE, "adviseMe", t);
//				} else {
//					RuleIF r = p.getValue();
//					if ( r.isException()) {
//						LOG.severe(r.getExpr());
//					} else {
//						LOG.info(r.getExpr());
//					}
//				}
//			} );
//		}
//	}

	public TekstVak geefTekstVak(int row, int col)
	{
		return(tekstVakken[row][col]);
	}
	
	public void setParentStappen(HasResize panel)
	{
		parentStappen = panel;
	}

	private Integer zoomKolom;
	private Integer zoomRij;
	
	public void zoom(TekstVak vak, int rij, int kolom) {
		for(int i = 0; i < tekstVakken.length; i++)
		{
			for(int j = 0; j < tekstVakken[i].length; j ++)
			{
				if (i != rij || j != kolom)
					tekstVakken[i][j].setVisible(false);				
			}
		}
		Style style = mainPanel.getElement().getStyle();
        style.clearMargin();
		style.setProperty("borderSpacing", "0px 0px");
		style.clearBorderWidth();
		zoomKolom = Integer.valueOf(kolom);
		zoomRij   = Integer.valueOf(rij);
       // mainPanel.getElement().getStyle().setProperty("margin", (-0 - randDikte) + "px " + (-0 - randDikte) + "px");

		setCurrentSize(Math.round(breedtes.get(kolom).floatValue()),  Math.round(hoogtes.get(rij).floatValue()) );
		if(parent != null) {
			parent.zoom(this);
		} else {
//		  int marge = instellingen.getInt("margeRechts") + instellingen.getInt("margeLinks");
//		  zetVolledigeBreedte0(Window.getClientWidth()-marge);
		}
	}

	public void unzoom(TekstVak tekstVak, int rij, int kolom) {
		for(int i = 0; i < tekstVakken.length; i++)
		{
			for(int j = 0; j < tekstVakken[i].length; j ++)
			{
				if (i != rij || j != kolom)
					tekstVakken[i][j].setVisible(true);				
			}
		}
		zoomKolom = null;
		float breedte = breedtes.stream().collect(Collectors.summingDouble(Double::doubleValue)).floatValue();
	    if (breedtes.size()>1) breedte += cellSpaceColumn * (breedtes.size()-1);
		float hoogte =  hoogtes.stream().collect(Collectors.summingDouble(Double::doubleValue)).floatValue();
		if (hoogtes.size()>1) hoogte += cellSpaceRow * (hoogtes.size()-1);

		Style style = mainPanel.getElement().getStyle();
        style.setProperty("borderSpacing", cellSpaceColumn + "px " + cellSpaceRow + "px");
        style.setProperty("margin", (-cellSpaceRow - randDikte) + "px " + (-cellSpaceColumn - randDikte) + "px");
        style.setBorderWidth(randDikte, Unit.PX);
		
		setCurrentSize(Math.round(breedte), Math.round(hoogte));
// zet ook de tekstvakken op maat
		for (int i = 0; i < hoogtes.size(); i++)
		{
			for (int j = 0; j < breedtes.size(); j++)
			{
				if (i == 0 || !(inklapbaar && ingeklapt))
				{
					tekstVakken[i][j].setSize((int) Math.round(breedtes.get(j).doubleValue()),
						(int) Math.round(hoogtes.get(i).doubleValue()));
				}
			}
		}

		
		
		
		
		if(parent != null) {
			parent.unzoom(this);
		} else {
          int width = getWindowWidth();
          zetVolledigeBreedte0alt(width);
		}
		  
		
	}

  public int getWindowWidth() {
    int marge = instellingen.getInt("margeRechts") + instellingen.getInt("margeLinks");
    int width = Window.getClientWidth()-marge;
    return width;
  }

  private ResizeHandler resizeHandler;
  @Override
  public void onResize() {
//    GWT.log("On Resize called");
    if (resizeHandler != null) {
      resizeHandler.onResize(null);
    }
  }

  public HandlerRegistration addResizeHandler(ResizeHandler resize) {
    resizeHandler = resize;
    return () -> { if (resizeHandler == resize) resizeHandler = null; };
  }

  public int getRowOf(Widget vak) {
    if (vak != null)
      for (int row = 0; row < tekstVakken.length; row++) {
        for (int col = 0; col <tekstVakken[row].length; col++) {
          if (vak == tekstVakken[row][col]) return row;
        }
      }    
    return -1;
  }

  public void setKolom(int kolom) {
    this.kolom = kolom;
  }
	
  public void setDwologger(Logging dwologger) {
	this.dwologger = dwologger;
  }
	
}
