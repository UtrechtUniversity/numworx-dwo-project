package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.DWOplayerCss;
import nl.uu.fi.dwo.mobile.client.sco.CorrectieFacade;
import nl.uu.fi.dwo.mobile.client.sco.DWOLogger;
import nl.uu.fi.dwo.mobile.client.ui.ImageTextButton;
import nl.uu.fi.dwo.mobile.client.ui.views.XMLView;
import nl.uu.fi.dwo.mobile.utils.PopupFacade;
import nl.uu.fi.dwo.mobile.utils.Review;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import fi.wiskopdr.text.Text;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GeogebraView implements InteractionView, LoadHandler, CBookEventListener, AttachEvent.Handler
{
	public static Logger LOG = Logger.getLogger("GeogebraView");
	public static final String ACTION_CORRECT = "action.correct";
	public static final String ACTION_FALSE = "action.false";
	public static final String ACTION_FALSE2 = "action.false_2";

	private static final CBookEvent EVENT_CORRECT = new CBookEvent(ACTION_CORRECT); 
	private static final CBookEvent EVENT_FALSE = new CBookEvent(ACTION_FALSE); 
	private static final CBookEvent EVENT_FALSE2 = new CBookEvent(ACTION_FALSE2); 

	private static final String KIJK_NA = "<span>" + Text.constants.nakijkKnopLabel() + "\u00A0</span>";
	private static final int KIJK_NA_HEIGHT = 30;

	private static DWOplayerCss css = DWOplayer.DWO_BUNDLE.dwoplayercss();

	private SimplePanel mainPanel;
	private Object ggbApplet;
	private Frame frame;
	private Button btn;
	private String ggb;
	private Boolean correct;
	private boolean bewaarOptie, nakijken, check;
	private boolean checkExternal;
	private boolean border;
	private int score, scoreMax;
	private PopupFacade facade;
	private int width;
	private int height;
	private int barHeight;
	private boolean volledigeBreedte;
	private boolean ingevuld;
	private boolean nagekeken;
	private String pendingState;
	private ObjectMap randomVars;
	/**
	 * De nakijkknop. Plaatje wordt niet meer op de knop getoond.
	 */
	private ImageTextButton checkBtn;
	/**
	 * Het panel waar de nakijkknop op komt en feedback-vinkjes/kruisje.
	 */
	VerticalPanel kijkNaPanel;
	private HTML checkLbl;
	private String kijkNa = KIJK_NA;
	private HasHTML checkWidget;
	private int aantalExistingObjects;
	private boolean nakijkenGemaakteObjecten;
	private String[] geogebraCheckObjects;
	private int[] geogebraCheckScores;
	private OpdrNavIF comRoot;
	private String dir;
	private boolean bigdata;
	private boolean editable = true;
	private String filename;
	
	private static final String RESOURCE = "https://mc2-resource.appspot.com/dav/Unit/";
	private String ggbFile;
	private Map<String, Object> geogebraParams = new HashMap<String,Object>();
	private int attemptsCount;
	private int errorCount;
    private int foutStraf = 2;
	private CorrectieFacade correctie;
	/**
	 * Houdt bij of er iets veranderd is, t.b.v. errorCount.
	 */
	private boolean changed = false;
	
	/**
	 * Wordt gebruikt om tijdens het nakijken geen onnodige ggbLog() te doen.
	 * Deze zet namelijk changed op true en haalt de feedbackimmages weg.
	 */
	private boolean processingDWOCheck = false;
	
	public native static Object getGgbWindow(Element frame) /*-{
		return frame.contentWindow;
	}-*/;
	
	public static native int getObjectNumber(Object ggb) /*-{
		return ggb.getObjectNumber()
	}-*/; 
	

	public String install(Object o)
	{
		ggbApplet = o;
		setRandomVars();
		if(o != null) aantalExistingObjects = getObjectNumber(o);
		setPendingState();
		return ggb;
	}

	private void ggbLog(String action, String name, String definition,
			String value, String type)
	{
		if (processingDWOCheck)
			return;
		
		//if(changed) return; TODO uitzoeken of dit okay is (Sylvia)
		
		changed = true;
		// er is iets veranderd, dus vinkje/kruis weg
		setVisibleFeedbackImages(false, false, false);
		
		if (dwologger != null)
		{
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("event", action);
			result.put("id", name);
			Map<String,String> state = new HashMap<String,String>();
			state.put("definition", definition);
			state.put("value", value);
			state.put("position", "");
			result.put("state", state);
			result.put("type", type);
			dwologger.getLogger().log(result);
		}
	}	
	
	public static native Object getApplet(Object wnd, GeogebraView view) /*-{
		wnd.viewer = view;
		wnd.ggbLog  = function (viewer, action,name, definition, value, type) {
			viewer.@nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.GeogebraView::ggbLog(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(action,name, definition, value, type)
		}
		wnd.install = function(o, viewer) {
			return viewer.@nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.GeogebraView::install(Ljava/lang/Object;)(o)
		}
		return wnd.ggbApplet;

	}-*/;

	public GeogebraView()
	{
		super();
		mainPanel = new SimplePanel();
		mainPanel.setStylePrimaryName("GeogebraView");
	}

	public GeogebraView(HashMap<String, Object> launchdata, String[] randomVarNamen, HashMap<String,?> randomVarWaarden)
	{
		this();
		if(!randomVarWaarden.isEmpty()) 
			randomVars = JSONUtilities.wrapMap(randomVarWaarden);
		init(launchdata);
	}

	private GeogebraView init(HashMap<String, Object> launchData)
	{
		ObjectMap json = JSONUtilities.wrapMap(launchData);
		facade = new PopupFacade(json);
		String randje = "#FFFFFF";
		if(json.getBoolean("border", false))
			randje = "#000000";
		geogebraParams.put("borderColor", randje);
		ObjectMap ggbMap = json.getObjectMap("interactiePanelLaunchState");
		ObjectMap object = ggbMap.getObjectMap("ggbFile"); // java:ByteArray struct
		bigdata = ggbMap.getBoolean("file", false);
		if (!bigdata && object != null) 
		{
			String string = object.getString("string");
			ggbFile = string != null ? string.toString() : "";
		}
		object = ggbMap.getObjectMap("geogebraParams");
		if( object instanceof Map)
		{
			Set<String> keys = object.keySet();
			for(String key: keys) {
				geogebraParams.put(key, object.getString(key));
			}
		}
		bewaarOptie = ggbMap.containsKey("bewaarOptie") && ggbMap.getBoolean("bewaarOptie");
		nakijken    = ggbMap.containsKey("nakijken") &&  ggbMap.getBoolean("nakijken");
		checkExternal = ggbMap.containsKey("checkExternal") &&  ggbMap.getBoolean("checkExternal");
		border 		= ggbMap.containsKey("border") && ggbMap.getBoolean("border");
		check       = (!ggbMap.containsKey("check")) || ggbMap.getBoolean("check"); // default is true
		nakijkenGemaakteObjecten = ggbMap.containsKey("nakijkenGemaakteObjecten") && ggbMap.getBoolean("nakijkenGemaakteObjecten");
		if (ggbMap.containsKey("geogebraCheckObjects"))
			geogebraCheckObjects = ggbMap.getStringArray("geogebraCheckObjects");
		if (ggbMap.containsKey("geogebraCheckScores"))
			geogebraCheckScores  = ggbMap.getIntArray("geogebraCheckScores");		
		
		if (ggbMap.containsKey("scoreMax")) 
			scoreMax = ggbMap.getInt("scoreMax");
				
		frame = new Frame(DWOplayer.PARAMETERS.getStubView() + "GeoGebra.html?locale=" + StubView.getLocale());
		frame.setStylePrimaryName("StubView");
		frame.addStyleDependentName("borderless");
		frame.addAttachHandler(this);
		if (bigdata)
			filename = ggbMap.getString("fileUrl");
		
		createGgbParams();

		barHeight = 0;
		if ("true".equals(geogebraParams.get("showMenuBar")))
			barHeight += 33;
		if ("true".equals(geogebraParams.get("showToolBar")))
			barHeight += 9; //57;
		if ("true".equals(geogebraParams.get("showAlgebraInput")))
			barHeight += 25 + 8 /*mac*/;
		width = 400;
		if (json.containsKey("breedte"))
			width = json.getInt("breedte");
		height = 400;
		if (json.containsKey("hoogte"))
			height = json.getInt("hoogte");
		volledigeBreedte = json.containsKey("volledigeBreedte") && json.getBoolean("volledigeBreedte");
		
		//if(!volledigeBreedte) //als volledigeBreedte dan wordt initFrame gedaan in zetVolledigeBreedte.
		//initFrame();
		if (ggbMap.getBoolean("logOption", false))
		{
			dwologger = new DWOLogger();
			dwologger.setMaxScore(scoreMax);
			dwologger.setLogID(ggbMap.getString("logID"));
			dwologger.setClassName("fi.wiskopdr.Geogebra4Panel");
		}
		return this;

	}
	
	private void fireEvent(CBookEvent event) 
	{

		LOG.info("fire + " + event.getCommand() + " s: " + event.getSource());
		DWOplayer.clientfactory.getEventBus().fireEventFromSource(event, this); // Why?
		if (this.comRoot != null)
			this.comRoot.fireEvent(event);
	}

	/**
	 * Bepaal ggb string en barheight.
	 */
	private void createGgbParams() {
	// normal		
		if(!bigdata)
			ggb = "data-param-ggbbase64='" + ggbFile + "'";
	// big data	
		else
			ggb = "data-param-filename='" + RESOURCE + dir + filename + "'";
		StringBuilder params = new StringBuilder();
		for(Map.Entry<String, Object> entry: geogebraParams.entrySet())
		{
			params.append( "data-param-" + entry.getKey() + "='" + entry.getValue() + "' ");
		}
		
		params.append( "data-param-language='" + StubView.getLocale() + "' ");
		params.append(ggb);
		ggb = params.toString();
	}

	private void initFrame() {
		int height = this.height;
		int width  = this.width;
		if(nakijken)
			height -= KIJK_NA_HEIGHT; // button size?
		
		frame.setPixelSize(width, height);
		//height -= 57 + 2; // toolbar aftrekken? ja dus!
		height -= 2;
		width  -= 2;  // 1 pixel border
		height -= barHeight;
		ggb += " data-param-width='" + width + "' data-param-height='" + height + "'"; // geeft een scrollbar
		frame.addLoadHandler(this);
		
		
		if (border)
		{	mainPanel.getElement().getStyle().setBorderColor("gray");
			mainPanel.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
			mainPanel.getElement().getStyle().setBorderWidth(1, Unit.PX);
		}
		
		if (nakijken)
		{
			checkBtn = new ImageTextButton(KIJK_NA, new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					onCheck();
				}} );
			checkWidget = checkBtn;
			checkLbl = new HTML("\u00A0");
			checkBtn.getElement().getStyle().setPaddingBottom(0, Unit.PX);
			checkBtn.getElement().getStyle().setPaddingTop(0,Unit.PX);
			AbsolutePanel hp = new AbsolutePanel();
			hp.setPixelSize(this.width, this.height);
			hp.add(frame, 0, 0);
			kijkNaPanel = new VerticalPanel();
			kijkNaPanel.setPixelSize(100, KIJK_NA_HEIGHT);
			kijkNaPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			if (!checkExternal)
			{
				kijkNaPanel.add(checkBtn);
			}
			kijkNaPanel.add(checkLbl);
			checkLbl.setVisible(false);
			if (comRoot != null && comRoot.getMode() > 1) 
			{
				checkBtn.removeFromParent();
				checkWidget = checkLbl;
				checkLbl.setVisible(true);
				kijkNa = "\u00A0";
			}
			hp.add(kijkNaPanel, (width - 70) / 2, this.height - KIJK_NA_HEIGHT);
			
			kijkNaPanel.setStylePrimaryName(css.kijknapanel());
			mainPanel.setWidget(hp);
			
			setVisibleFeedbackImages(false, false, false);
		}
		else 
			mainPanel.setWidget(frame);
	}

	void onCheck()
	{
		if (!editable)
			return;
		kijkNa(true); // Feedback in view
		attemptsCount++;
		setAttempt();
		comRoot.setChanged(Boolean.FALSE.equals(correct)); // deze roept een ggbLog aan die img reset
		
		// moet hier ook, anders komt het gele vinkje niet...
		zetNagekeken(true);
		
		setCheckImg(); // set feedBack vinkje/kruis
	}

	private DWOLogger dwologger;
	private int mode;
	private boolean review;
	private void setAttempt() {
		if(dwologger != null) {
			Map<String,Object> parameters = new HashMap<String,Object>();
			parameters.put("response", "");
			parameters.put("score", Collections.singletonMap("raw", score));
			Boolean correct = isCorrect();
			if(correct != null)
				parameters.put("success", correct);
			dwologger.log(parameters);
		}
	}

	/**
	 * Toon rood kruisje/groen vinkje.
	 */
	private void setCheckImg()
	{
		if (checkWidget == null)
			return;
		
		if (Boolean.TRUE.equals(correct))
		{
			setVisibleFeedbackImages(true, false, false);
		}
		else if (Boolean.FALSE.equals(correct))
		{
			setVisibleFeedbackImages(false, false, true);
		}
		else if (nagekeken) // correct is dan null; by design! 
		{
			setVisibleFeedbackImages(false, true, false);
		}
		else
		{
			checkWidget.setHTML(kijkNa);
			setVisibleFeedbackImages(false, false, false);
		}
	}

	@Override
	public HashMap<String, Object> getState()
	{
		if (facade.hasState())
		{
			HashMap<String, Object> state = facade.getState();
			if (state == null)
				state = new HashMap<>();
			state.put("nagekeken", Boolean.valueOf(nagekeken)); // deze kan
																// veranderen
																// als de popup
																// dicht is
			return state;
		}
		boolean feedback;
		// bij zelftoets nooit feedback, bij oefenen altijd en bij eindtoets
		// alleen als nagekeken.
		feedback = mode == OpdrNavIF.OEFENEN || mode == OpdrNavIF.OEFENEN_STRAFPUNTEN
			|| (mode == OpdrNavIF.EINDTOETS && nagekeken);
		kijkNa(feedback, false);
		HashMap<String, Object> map = new HashMap<String, Object>();
		if (bewaarOptie && pendingState != null)
			map.put("state", pendingState);
		if (bewaarOptie && ggbApplet != null)
		{
			map.put("state", getXML(ggbApplet));
		}
		map.put("nagekeken", Boolean.valueOf(nagekeken));
		map.put("ingevuld", Boolean.valueOf(ingevuld));
		map.put("attemptsCount", Integer.valueOf(attemptsCount));
		map.put("errorCount", Integer.valueOf(errorCount));
		if(correctie != null) correctie.correctie(map);
		return wrap(map);
	}

	private HashMap<String, Object> wrap(HashMap<String, Object> map) {
		if(map == null) map = new HashMap<String, Object>();
		map.put("STUBVIEW_score", String.valueOf(getScore()));
		map.put("STUBVIEW_correct", String.valueOf(isCorrect()));
		return map;
	}

	private static native String getXML(Object ggb)
	/*-{
		return ggb.getXML();
	}-*/;
	
	private static native String setXML(Object ggb, String xml)
	/*-{
		ggb.setXML(xml);
	}-*/;

	@Override
	public void setState(HashMap<String, Object> h)
	{
		facade.setPopupState(h);
		if (h == null)
			h = new HashMap<String, Object>(); // Never NULL, komt voor!
												// setStateNull()
		if (h.containsKey("STUBVIEW_score"))
			score = Integer.parseInt(h.get("STUBVIEW_score").toString());
		if (h.containsKey("STUBVIEW_correct"))
			correct = StubView.toBoolean(h.get("STUBVIEW_correct").toString());
		String xml = (String) h.get("state");
		if (bewaarOptie && xml != null)
		{
			if (ggbApplet != null)
			{
				pendingState = null;
				setXML(ggbApplet, xml);
			}
			else
				this.pendingState = xml;
		}
		else
		{
			this.pendingState = null;
		}
		CorrectieFacade.showReview(h, getWidget());
		ObjectMap wrap = JSONUtilities.wrapMap(h);
		ingevuld = wrap.getBoolean("ingevuld", false);
		nagekeken = wrap.getBoolean("nagekeken", false);
		if (wrap.containsKey("attempsCount"))
			attemptsCount = wrap.getInt("attemptsCount");
		if (wrap.containsKey("errorCount"))
			errorCount = wrap.getInt("errorCount");

		boolean feedback = mode == OpdrNavIF.OEFENEN || mode == OpdrNavIF.OEFENEN_STRAFPUNTEN || nagekeken|| review;
		if (feedback)
			setCheckImg(); // geen Kijkna nodig, want we hebben alle variabelen
							// hersteld, behalve de groene elementen.
		else if (checkWidget != null)
		{
			setVisibleFeedbackImages(false, false, false);
		}
		correctie = CorrectieFacade.get(h, this, getWidget(), scoreMax, mode);
	}

	/**
	 * Wordt aangeroepen bij zelftoets en extern nakijken met checkbutton.
	 */
	public void kijkNa()
	{
		kijkNa(true);
		
		zetNagekeken(true);
		
		attemptsCount++;
		setAttempt();

		comRoot.setChanged(Boolean.FALSE.equals(correct)); // deze roept een ggbLog aan die img reset

		setCheckImg(); // set feedback vinkje/kruis
	}

	/**
	 * Verhoog de error count als er iets veranderd is.
	 * 
	 */
	void verhoogErrorCount()
	{
		if (isChanged())
			errorCount++;
		setChanged(false);
	}
	
	private boolean isChanged()
	{
		return changed;
	}

	private void setChanged(boolean b)
	{
		changed = b;
	}

	private void kijkNa(boolean feedback) {
		kijkNa(feedback, true);
	}
	private void kijkNa(boolean feedback, boolean fire) // getState/kijkna
	{
		if (nakijken && ggbApplet != null)
		{
			processingDWOCheck = true;
			
			if (nakijkenGemaakteObjecten)
			{
				int length = getObjectNumber(ggbApplet) - aantalExistingObjects;
				if (length <= 0)
				{
					setCorrect(false);
					return;
				}
				int checkLength = geogebraCheckObjects.length;
				int checkStart = 0;
				String[] checkObjects = new String[checkLength];
				System.arraycopy(geogebraCheckObjects, 0, checkObjects, 0, checkLength);
				score = 0;
				int matches = 0;
				for (int i = 0; i < length; i++)
				{
					String objectName = getObjectName(ggbApplet, i + aantalExistingObjects);
					String valueString = getValueString(ggbApplet, objectName);
					String objectString;
					objectString = valueString.replace(objectName + "(x)", "");
					objectString = objectString.replace(objectName, "");
					objectString = objectString.replace(" ", "");
					objectString = objectString.substring(1);
					boolean match = false;

					for (int j = checkStart; j < checkLength; j++)
					{
						if (checkObjects[j] == null)
							continue;
						match = checkObjects[j].equals(objectString);
						if (!match && !"boolean".equals(getObjectType(ggbApplet, objectName)))
						{
							evalCommand(ggbApplet, "checkDWO=" + checkObjects[j] + "==" + objectName); // roept ggbLog() aan
							match = 1.0 == getValue(ggbApplet, "checkDWO");
						}
						if (match)
						{
							if (feedback)
							{
								// feedback alleen in oefenen of (toets + nagekeken).
								setColor(ggbApplet, objectName, 0, 180, 0); // groen; roept ggbLog() aan
							}
							score += geogebraCheckScores[j];
							checkObjects[j] = null; // used!
							matches++;
							// Optimalisatie, maak checkObjects array kleiner
							// als mogelijk, maar met behoud van indexen.
							if (j == checkStart)
								checkStart++;
							else if (j == checkLength - 1)
								checkLength = j;
							break;
						}
					}
				}

				if (matches > 0 && matches < checkObjects.length)
				{
					setCorrect(null); // halfgoed, gele vink
				}
				else
					setCorrect(matches != 0);

				if (feedback)
				{
					// feedback alleen in oefenen of (toets + nagekeken).
					setVisibleFeedbackImages();
				}
			}
			else
			{
				double val = getValue(ggbApplet, "checkDWO");
				setCorrect(val == 1.0);
			}
			// nagekeken = true;
			
			// reset
			processingDWOCheck = false;
		}
	
		if (fire && (isCorrect() == null || !isCorrect())) // halfgoed of fout, alleen als fire true (isChanged() kan onverwacht true zijn ivm ggbLog() en timing 
			verhoogErrorCount();

		if (feedback && nakijken && fire)
		{
			// cross widget communicatie
			if (correct == null) // halfgoed
			{
				if (errorCount > 1)
					fireEvent(EVENT_FALSE2);
				else
					fireEvent(EVENT_FALSE);
			}
			else
			{
				if (correct)
					fireEvent(EVENT_CORRECT);
				else if (!correct && errorCount > 1)
					fireEvent(EVENT_FALSE2);
				else if (!correct)
					fireEvent(EVENT_FALSE);
			}
		}
	}

	/**
	 * Get array of booleans indicating whether 
	 * goed, half or fout feedback image is showing.
	 * 
	 * @return
	 */
	private boolean[] getOldSettingsImage()
	{
		boolean[] array = {false, false, false};
		
		if (kijkNaPanel.getStyleName().contains(css.goed()))
			array[0] = true;
		if (kijkNaPanel.getStyleName().contains(css.half()))
			array[1] = true;
		if (kijkNaPanel.getStyleName().contains(css.fout()))
			array[2] = true;
		
		return array;
	}

	private void setCorrect(Boolean b)
	{
		if (b == null)
		{ // score = 0; // score is gezet door KijkNa(boolean)
			correct = null;
		}
		else if (b)
		{
			score = scoreMax;
			correct = Boolean.TRUE;
		}
		else
		{
			score = 0;
			correct = Boolean.FALSE;
		}
	}
	
	@Override
	public int getScore()
	{
		if (mode == OpdrNavIF.OEFENEN_STRAFPUNTEN)
			return Math.max(0, score - errorCount * foutStraf);

		return score;
	}
	
	@Override
	public int[][] getScoreObjectives()
	{
		return null;
	}

	/* 
	 * Retourneert
	 * - true: als correct of als niet moet worden nagekeken 
	 * - false: als fout
	 * - null: als halfgoed
	 */
	@Override
	public Boolean isCorrect()
	{
		if (nakijken)
			return correct;
		return Boolean.TRUE;
	}
	
	public void zetNagekeken(boolean b) {
		//if (ingevuld)
			nagekeken = b;
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		this.comRoot = comRoot;
		dir = comRoot.getUUID();
		if(dir != null) dir = dir.replace('-', '/')+'/'; else dir ="";
		mode = comRoot.getMode();
		review = Review.isReview(comRoot);
		if (nakijken & mode > 1 && checkBtn != null && !review) {
			// haal checkbutton weg, insert een label
			checkBtn.removeFromParent();
			checkWidget = checkLbl;
			checkLbl.setVisible(true);
			kijkNa = "\u00A0";
		}
		if (bigdata)
		{
			createGgbParams();
		}
		if (dwologger != null)
			dwologger.setCommunicationRoot(comRoot);
	}

	@Override
	public Widget asWidget()
	{
		return facade.wrap(this);
	}

	private Widget widget;
	public Widget getWidget() 
	{   if (widget == null) widget = CorrectieFacade.wrap(mainPanel);
		return widget;
	}
	
	@Override
	public void onLoad(LoadEvent event)
	{
		Object w = getGgbWindow(frame.getElement());
		if (w != null)
		{
			ggbApplet = getApplet(w, this);
			setRandomVars();
			if (ggbApplet != null)
				aantalExistingObjects = getObjectNumber(ggbApplet);
			setPendingState();
		}
	}

	private void setPendingState() {
		if(pendingState != null && ggbApplet != null) {
			setXML(ggbApplet, pendingState); // TODO kan je hier een "kijkna(true)" doen?
			pendingState = null;
		}
	}
	
	private void setRandomVars() {
		if(ggbApplet != null && randomVars != null) {
			Set<String> keys = randomVars.keySet();
			for (String key : keys) {
				double value = randomVars.getDouble(key);
				key = "dwo_" + key.replace("?(", "").replace(")", "");
				setValue(ggbApplet, key, value);
			}
		}
	}
	
	private static native void setValue(Object ggb, String key, double value) /*-{
		ggb.setValue(key, value);
	}-*/;
	
	private static native double getValue(Object ggb, String key) /*-{
		return ggb.getValue(key);
	}-*/;
	
	private static native String getValueString(Object ggb, String key) /*-{
		return ggb.getValueString(key);
	}-*/;
	private static native String getObjectName(Object ggb, int i) /*-{
		return ggb.getObjectName(i);
	}-*/;
	private static native String getObjectType(Object ggb, String name) /*-{
		return ggb.getObjectType(name);
	}-*/;
	private static native boolean evalCommand(Object ggb, String name) /*-{
		return ggb.evalCommand(name);
	}-*/;

	private static native void setColor(Object ggb, String name, int red, int green, int blue) /*-{
		ggb.setColor(name, red, green, blue);
	}-*/;
	
	private native static int execute(Object js) /*-{
		return js.execute();
	}-*/;

	private native static void reset(Object js) /*-{
		return js.reset();
	}-*/;
	
	@Override
	public int getAsHoogte() {
		return facade.wrapAsHoogte(XMLView.getDefaultFontSize());
		//was: return facade.wrapAsHoogte(0), maar dan komt Geogebra te laag in de regel te staan.
	}

	@Override
	public int getHeight() {
		return facade.wrapHeight(height);
	}

	@Override
	public int getWidth() {
		return facade.wrapWidth(width);
	}
	
	public void zetVolledigeBreedte(int breedte)
	{   int oldwidth = width;
		if(volledigeBreedte)
		{
			width = breedte;
			//initFrame();
		}
		if(width != oldwidth || frame.getParent() == null) // Do not init if same width.
		  initFrame();
	}

	@Override
	public void setAsHoogte(int ashoogte) {
		//this.ashoogte = ashoogte;
	}

	@Override
	public void acceptCBookEvent(CBookEvent event)
	{
		if ("action.setNotEditable".equals(event.getCommand()))
		{
			editable = true;
			mainPanel.setStyleDependentName("readonly", !editable);
			frame.setStyleDependentName("readonly", !editable);
		}
	}

	/**
	 * Zet de feedback images zichtbaar adhv de meegegeven booleans.
	 * 
	 * @param vinkjeGroen
	 * @param vinkjeGeel
	 * @param kruisRood
	 */
	void setVisibleFeedbackImages(boolean vinkjeGroen, boolean vinkjeGeel, boolean kruisRood)
	{
		if (nakijken && kijkNaPanel != null)
		{
			kijkNaPanel.setStyleName(css.goed(), vinkjeGroen);
			kijkNaPanel.setStyleName(css.half(), vinkjeGeel);
			kijkNaPanel.setStyleName(css.fout(), kruisRood);
		}
	}

	/**
	 * Zet de feedback images zichtbaar adhv de waarde van correct.
	 */
	void setVisibleFeedbackImages()
	{
		if (nakijken && kijkNaPanel != null)
		{
			boolean vinkjeGroen = correct != null && isCorrect();
			boolean vinkjeGeel = correct == null;
			boolean kruisRood = correct != null && !isCorrect();
			
			kijkNaPanel.setStyleName(css.goed(), vinkjeGroen);
			kijkNaPanel.setStyleName(css.half(), vinkjeGeel);
			kijkNaPanel.setStyleName(css.fout(), kruisRood);
		}
	}

  @Override
  public void onAttachOrDetach(AttachEvent event) {
    LOG.severe("AttachEvent " + event.isAttached());
  }
}
