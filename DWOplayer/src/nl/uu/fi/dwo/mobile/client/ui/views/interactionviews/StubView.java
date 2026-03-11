package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.interaction.client.FormuleClipboardIF;
import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Role;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.JSONObjectMapImpl;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.interaction.client.keyboard.EnterType;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.sco.CorrectieFacade;
import nl.uu.fi.dwo.mobile.client.ui.ActivityInterface;
import nl.uu.fi.dwo.mobile.client.ui.TekstElementWithFont;
import nl.uu.fi.dwo.mobile.client.ui.TimedBarrier;
import nl.uu.fi.dwo.mobile.client.ui.views.AnchorContext;
import nl.uu.fi.dwo.mobile.utils.Logging;
import nl.uu.fi.dwo.mobile.utils.PopupFacade;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class StubView extends SimplePanel implements InteractionView, LoadHandler, OpdrNavIF, FormuleEditorIF, AttachEvent.Handler, CBookEventListener, TekstElementWithFont {

	public void pause() {
		comRoot.pause();
	}

	private static native void frameFactory(JavaScriptObject element, String url) /*-{
		try {
			$wnd.frameFactory(element, url);
		} catch(e) {
		
		}
	}-*/;
	
	
	public void unpause() {
		comRoot.unpause();
	}

	private Frame frame;
	private Object innerView;
	private ObjectMap innerMap;
	private OpdrNavIF comRoot = this;
	private HashMap<String, Number> randomVars;
	private String pendingState;
	private int width, fullwidth;
	private int height;
	private boolean volledigeBreedte, hasFullWidth;
	private PopupFacade facade;
	private static FormuleFont defaultFont = FormuleFont.createFromFontSize(18);
	private HandlerRegistration loadhandler;
	private boolean[][] logObjectives;
	private int scoreMax, score;
	private boolean teltmee = true;
	private boolean checkDocent;
	private Boolean correct;
	private CorrectieFacade correctie;
	private Logging logging;
	private AnchorContext aContext;
	private TimedBarrier barrier;
	
	public void setAContext(AnchorContext aContext) {
		this.aContext = aContext;
	}

	@Override
	public void onAttachOrDetach(AttachEvent event) {
		boolean detach = !event.isAttached();
		if( detach )
		{
			if (innerView != null) {
				getState0(); // last chance to fill lastResort en correct/score
			}
			innerView = null;
			barrier.cancel();
		}
		else 
		{
			loadhandler = frame.addLoadHandler(this);
			barrier.start(1000);
		}
		
	}
	private HandlerRegistration detachhandler; 

	public StubView(ActivityInterface activity, String html, HashMap<String, Object> launchdata, String[] randomVarNamen, HashMap<String,Number> randomVarWaarden)
	{
		this.activity = activity;
		this.barrier = activity.barrier();
		html = activity.getStubView() + html;
		String locale = getLocale();		
		html += "?locale=" + locale;
		html += "&profile=" + DWOplayer.PROFILE_ID;
		init(html, launchdata, randomVarNamen, randomVarWaarden);
	}

	public static String getLocale() {
		String locale;
		locale = LocaleInfo.getCurrentLocale().getLocaleName();
		String query = Window.Location.getParameter("locale");
		if(query != null && !query.isEmpty()) {
			locale = query;
		} else if("default".equals(locale)) // no default please.
			return "nl";
		return locale;
	}
	
	private void init(String html, HashMap<String, Object> launchData,
			String[] randomVarNamen, HashMap<String, Number> randomVarWaarden) {
		ObjectMap outermap = JSONUtilities.wrapMap(launchData);
		innerMap = outermap.getObjectMap("interactiePanelLaunchState");
		randomVars = randomVarWaarden;
		facade = new PopupFacade(outermap, activity);
		frame = new Frame(html);
		frameFactory(frame.getElement(), html);
		frame.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		frame.setStylePrimaryName("StubView");
		frame.addStyleDependentName("borderless");
		int width = 400; if(outermap.containsKey("breedte")) width = outermap.getInt("breedte");
		int height =400; if(outermap.containsKey("hoogte")) height = outermap.getInt("hoogte");
		boolean volledigeBreedte = false; if(outermap.containsKey("volledigeBreedte")) volledigeBreedte = outermap.getBoolean("volledigeBreedte");
		
		this.volledigeBreedte = volledigeBreedte;
		hasFullWidth = volledigeBreedte && ! facade.isPopup();
		this.width = width;
		this.height = height;
		this.fullwidth = width;
		if(!volledigeBreedte ) initFrame();
		if(innerMap.containsKey("logObjectives")) 
		{
			ObjectList logObjectivesList = ( innerMap.getObjectList("logObjectives") );
			logObjectives = new boolean[logObjectivesList.size()][];
			for(int i = 0; i < logObjectivesList.size(); i++)
			{	logObjectives[i] = logObjectivesList.getBooleanArray(i);
			}
		}
		if(innerMap.containsKey("scoreMax"))
			scoreMax = innerMap.getInt("scoreMax");
		correct = null;
		teltmee = true;
		if(innerMap.containsKey("teltmee")) 
			teltmee = innerMap.getBoolean("teltmee");
		int soortVak = outermap.getInt("soortInteractiePanel");
		if (innerMap.containsKey("checkDocent")) checkDocent = innerMap.getBoolean("checkDocent");
		logging = activity.logBuilder().setClassName(className(soortVak)).setLaunchData(innerMap).build();
	}
	private String className(int s) {
		switch(s) {
			default: return "nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.StubView";
			case 6:  return "fi.nabouwenaanzichten.NabouwenAanzichten";
			case 26: return "fi.tekenveelvlakopdr.TekenVeelvlakOpdr";
			case 41: return "fi.kladje.Kladje";
			case 45: return "fi.graphtool.GraphTool";
			case 57: return "nl.numworx.geodefiner.GeoDefiner";
			case 61: return "fi.mathscratch.MathScratch";
			case 62: return "nl.numworx.uploadwidget.UploadWidget";
			case 69: return "nl.numworx.aimodel.AIModel";
		}
	}
	
	private void initFrame() {
		frame.setPixelSize(width , height);
		//loadhandler = frame.addLoadHandler(this);
		detachhandler = frame.addAttachHandler(this);
		setWidget(frame);
	}

	@Override
	public HashMap<String, Object> getState() {
		if (facade.hasState()) 
			return facade.getState();
		return getState0();
	}

	private HashMap<String, Object> lastResort;
	private final ActivityInterface activity;
	private HashMap<String, Object> getState0() {
		if(innerView != null)
		{
			String jso = getState(innerView); // FIXME innerview := null als frame hides or disappears
			if(jso != null)
			{
				JSONObject js = JSONParser.parseLenient(jso).isObject();
				//return JSONUtilities.fromJSONObject(js);
				return lastResort = wrap(JSONUtilities.wrapMap(js));
			}
		}
		if(pendingState != null)
		{
			JSONObject js = JSONParser.parseLenient(pendingState).isObject();
			return lastResort = wrap(JSONUtilities.wrapMap(js));
		}
		
		if (lastResort != null) 
			return lastResort;
		
		HashMap<String,Object> map = new HashMap<String,Object>();
		return wrap(map);
	}

	private HashMap<String, Object> wrap(HashMap<String, Object> map) {
		if(map == null) map = new HashMap<String, Object>();
		if(correctie != null) correctie.correctie(map);
		map.put("STUBVIEW_score", String.valueOf(getScore()));
		map.put("STUBVIEW_correct", String.valueOf(isCorrect()));
		return map;
	}

	@Override
	public void setState(HashMap<String, Object> h) {
		facade.setPopupState(h);
		boolean isNull = h == null;
		if(isNull)
			h = new HashMap<String, Object>(); // Never NULL, komt voor!
		lastResort = h;
		if(h.containsKey("STUBVIEW_score"))
			score = Integer.parseInt(h.get("STUBVIEW_score").toString());
		if(h.containsKey("STUBVIEW_correct"))
			correct = toBoolean(h.get("STUBVIEW_correct").toString());
		AcceptsOneWidget cmd = widget instanceof AcceptsOneWidget ? (AcceptsOneWidget) widget : null;
		CorrectieFacade.showReview(h, cmd, this, scoreMax, activity);
		JSONValue object = JSONUtilities.toJSONObject(h);
		if(innerView != null)
		{
			setState(innerView, object.toString());
			barrier.cancel();
			pendingState = null;
			pendingState = object.toString(); // reset komt mogelijk na
		}
		else 
			pendingState = object.toString(); // XXX NPE!
		if (!isNull)
			correctie = CorrectieFacade.get(h, this, widget, scoreMax, comRoot, logging, activity,checkDocent);
	}

	@Override
	public int getScore() {
		if(innerView != null)
			return score = getScore(innerView);
		return score;
	}
	
	@Override
	public int[][] getScoreObjectives() {
		if(innerView != null && logObjectives != null)
		{
			int score = getScore(innerView);
			// verdeel score over objectives
			int[][] scoreObjectives = new int[logObjectives.length][];
			for(int i = 0; i < scoreObjectives.length; i++)
			{	scoreObjectives[i] = new int[logObjectives[i].length];
				for(int j = 0; j < scoreObjectives[i].length; j++)
					if(logObjectives[i][j])
						scoreObjectives[i][j] = score;
			}
			return scoreObjectives;
		}
		return null;
	}

	private native static int getScore(Object inner) /*-{
		return inner.getScore();
	}-*/ ;
	
	private native static String editorToString(Object inner) /*-{
		return inner.editorToString();
	}-*/;
	
	private native static String getState(Object inner) /*-{
		return inner.getState();
	}-*/;
	
	private native static void setState(Object inner, String state) /*-{
		inner.setState(state);
	}-*/;
	
	private native static String isCorrect(Object inner) /*-{
		return inner.isCorrect();
	}-*/;
	
	private native static void kijkNa(Object inner) /*-{
		inner.kijkNa();
	}-*/;
	
	private native static void zetNagekeken(Object inner, boolean b) /*-{
		inner.setNagekeken(b);
	}-*/;

	private native static int getConstantHeight0(Object inner) /*-{
		return inner.getConstantHeight();
	}-*/;

	private native static int getConstantWidth0(Object inner) /*-{
		return inner.getConstantWidth();
	}-*/;
	
	private int getConstantHeight() {
		if (innerView == null) return 0; // komt veel voor
		try {
			return getConstantHeight0(innerView);
		} catch(Throwable t) {
			return 0; // De default;
		}
	}
	private int getConstantWidth() {
		if (innerView == null) return 0; // komt veel voor
		try {
			return getConstantWidth0(innerView);
		} catch(Throwable t) {
			return 0; // De default;
		}
	}
	
	
	@Override
	public Boolean isCorrect() {
		if(innerView != null) {
			String correct = isCorrect(innerView);
			return this.correct = toBoolean(correct);
		}
		if(scoreMax == 0 || !teltmee)
			return Boolean.TRUE;
		return correct; // FIXME betere voorspelling maken.
	}

	static Boolean toBoolean(String value) {
		if(value == null || "null".equals(value)) return null;
		return Boolean.valueOf(value);
	}
	
	Boolean nagekekenPending;
	
	public void zetNagekeken(boolean b) {
		if(innerView != null)
		{	nagekekenPending = null;
			zetNagekeken(innerView, b);
		}
		else
			nagekekenPending = Boolean.valueOf(b);
	}
	
	public void kijkNa() {
		if(innerView != null)
			kijkNa(innerView);
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		this.comRoot = comRoot;
		if (logging != null) 
			logging.setCommunicationRoot(comRoot);
	}

	/**
	 * Delegate pattern.
	 */
	public int getMode() {
		return comRoot.getMode();
	}
	
	private void publish(Object inner) {
		
		innerView = inner;
		try {
			HashMap<String, Object> inits = new HashMap<String,Object>();			
			inits.putAll(randomVars);
			init(inner, width, height, JSONUtilities.toJSONObject(innerMap).toString(), 
					JSONUtilities.toJSONObject(inits).isObject().getJavaScriptObject()); // FIXME ook toString?

			if(pendingState != null) {
				setState(inner, pendingState);
				barrier.cancel();
				pendingState = null;
			} 
			if (nagekekenPending != null) {
				zetNagekeken(nagekekenPending.booleanValue());
			}
		} catch(Exception e) {
			Logger.getLogger("StubView").log(Level.SEVERE,"init "+ e);
		}
		
				
	}
	
	
	private static native void init(Object inner, int width, int height, String launchdata,
			JavaScriptObject randomVars) /*-{ 
				inner.init(width, height, launchdata, randomVars);
			}-*/;

	@Override
	public void onLoad(LoadEvent event) {
		loadhandler.removeHandler();
		Object w = getContentWindow(frame.getElement());
		if (w != null)
		{
			innerView = getApplet(w, this);
			if(innerView != null)
				publish(innerView);
		}
	}
	
	private native static Object getContentWindow(Element frame) /*-{
		return frame.contentWindow;
	}-*/;

	public static native Object getApplet(Object wnd, StubView view) /*-{
		wnd.outer = view;
		wnd.publish = function(o, viewer) {
			return viewer.@nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.StubView::publish(Ljava/lang/Object;)(o)
		}
		wnd.setChanged = function(b, viewer) {
			viewer.@nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.StubView::comRoot.@nl.uu.fi.dwo.interaction.client.OpdrNavIF::setChanged(Z)(b);
		}
		wnd.setFocus = function(b, viewer) {
			viewer.@nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.StubView::setFocus(Z)(b)
		}
		wnd.setFocus2 = function(b, soft, viewer) {
			viewer.@nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.StubView::setFocus(ZZ)(b,soft)
		}
		wnd.getMode = function(viewer) {
			return viewer.@nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.StubView::getMode()()
		}
		wnd.getLearnerName = function (viewer) {
			return viewer.@nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.StubView::getLearnerName()()
		}
		wnd.getLearnerId = function (viewer) {
			return viewer.@nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.StubView::getLearnerId()()
		}
		wnd.getUUID = function (viewer) {
			return viewer.@nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.StubView::getUUID()()
		}
		wnd.getBackground = function (viewer) {
			return viewer.@nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.StubView::getBackgroundAsString()()
		}
		
		wnd.fireEvent = function (event, viewer) {
			if ( typeof event === 'string' )
				event = JSON.parse(event)
			return viewer.@nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.StubView::fireJSEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(event)
		}
		
		wnd.addCBookEventListener = function (command, listener, viewer) {
			return viewer.@nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.StubView::addCBookEventListener(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(command, listener)
		}
		wnd.removeCBookEventListener = function (registration) {
			return @nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.StubView::removeCBookListener(Lcom/google/web/bindery/event/shared/HandlerRegistration;)(registration)
		}
		wnd.setEnterType = function(type, viewer) {
			return viewer.@nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.StubView::setEnterType(Ljava/lang/String;)(type)
		}
		wnd.getConfiguration = function(viewer) {
			return viewer.@nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.StubView::getConfiguration0()()
		}
		wnd.getContext = function(viewer) {
			return viewer.@nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.StubView::getContext0()()
		}
		wnd.tickle = function() {
			view.@nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.StubView::tickle()()
		}
		wnd.setVisited = function(viewer) {
			viewer.@nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.StubView::setVisited()()
		}
				
		return wnd.inner;
	}-*/;

	private void tickle() {
		activity.tickle();
	}
	
	
	private void setFocus(boolean b, boolean soft) {
		final FormuleKeyboardIF kb = comRoot.getKeyboard();
		kb.setEditor( b ? this : null);
		
		// extra parameter 'soft' of hard focus
		if (b) {
			if(soft) kb.softFocus();
			else kb.focus();
		} else 
			kb.blur();
	}
	
	private void setFocus(boolean b) {
		setFocus(b,true);
	}
	
	private void setEnterType(String type) {
		try {
			EnterType e = EnterType.valueOf(type);
			comRoot.getKeyboard().setEnterType(e);
		} catch (Exception e) {
			GWT.log("setEnterType " + type, e);
		}
	}
	
	private void fireJSEvent(JavaScriptObject jso) {
		JSONObject value = new JSONObject(jso);
		CBookEvent evt = new CBookEvent(JSONUtilities.wrapMap(value));
		fireEvent(evt);
		Timer t = new Timer()
		{
			@Override
			public void run()
			{
			}
		};
		t.schedule(1);

	}
	
	private static native void acceptCBookEvent(JavaScriptObject jso, String event) /*-{
		jso(event)
	}-*/;
	
	CBookEventListener setNotEditableListener;
	private boolean seal;
	
	private HandlerRegistration addCBookEventListener(String command, final JavaScriptObject listener) {
		CBookEventListener javalistener = new CBookEventListener() {
			
			@Override
			public void acceptCBookEvent(CBookEvent event) {
				JSONValue ev = JSONUtilities.toJSONObject(event.toObjectMap());
				StubView.acceptCBookEvent(listener, ev.toString());
			}
		};
		if("action.setNotEditable".equals(command))
		{
			setNotEditableListener = javalistener;
		    if (seal) 
		    {	seal = false;
				frame.setStyleName(DWOplayer.DWO_BUNDLE.dwoplayercss().StubView_readonly(), false);
				javalistener.acceptCBookEvent(new CBookEvent("action.setNotEditable"));
		    }
		}
		return comRoot.addCBookEventListener(command, javalistener);
	}
	
	private static void removeCBookListener(HandlerRegistration r) {
		r.removeHandler();
	}
	
	@Override
	public void setChanged(boolean fout) {
		if(comRoot != this)
			comRoot.setChanged(fout);
	}

	private Widget widget;
	public Widget asWidget() {
	    if(widget == null)
	      widget = CorrectieFacade.wrap(frame, activity);
		return facade.wrap(widget, this);
	}

	@Override
	public FormuleKeyboardIF getKeyboard() {
		if(comRoot!=this)
			return comRoot.getKeyboard();
		return null;
	}

	@Override
	public void clearAll() {
		if(innerView != null)
			clearAll(innerView);
	}

	private static native void clearAll(Object inner)/*-{
		inner.clearAll();
	}-*/;

	@Override
	public void insert(String text) {
		if(innerView != null)
			insert(text, innerView);		
	}
	
	private static native void insert(String text, Object inner) /*-{
		inner.insert(text);
	}-*/;
	
	public static void createDefaultFont(int size) {
		defaultFont = FormuleFont.createFromFontSize(size);
	}
	
	
	@Override
	public FormuleFont getDefaultFont() {
		return defaultFont;
	}

	@Override
	public void setFont(FormuleFont font) {
	}

	@Override
	public void setCurrentElementRepaint() {
	}

	@Override
	public void enter() {
		if(innerView != null)
			enter(innerView);
	}
	private static native void enter(Object inner) /*-{
		inner.enter();
	}-*/;
	
	
	@Override
	public void removeCurrentElement() {
		backspace(innerView);
	}
	private static native void backspace(Object inner) /*-{
		inner.backspace();
	}-*/;
	
	@Override
	public void removeNextElement() {
		removeNextElement(innerView);
	}
	
	private static native void removeNextElement(Object inner) /*-{
		inner.removeNextElement();
	}-*/;

	@Override
	public void cursorToLeft() {
		cursorToLeft(innerView);
	}
	private static native void cursorToLeft(Object inner) /*-{
		inner.cursorToLeft();
	}-*/;

	@Override
	public void cursorToRight() {
		cursorToRight(innerView);
	}
	private static native void cursorToRight(Object inner) /*-{
		inner.cursorToRight();
	}-*/;
	
	@Override
	public void cursorToLeftShift() {
		cursorToRight(innerView);
	}
	private static native void cursorToLeftShift(Object inner) /*-{
		inner.cursorToLeftShift();
	}-*/;
	
	@Override
	public void cursorToRightShift() {
		cursorToRight(innerView);
	}
	private static native void cursorToRightShift(Object inner) /*-{
		inner.cursorToRightShift();
	}-*/;

	@Override
	public void cursorUp() {
		cursorUp(innerView);
	}
	private static native void cursorUp(Object inner) /*-{
		inner.cursorUp();
	}-*/;
	
	@Override
	public void cursorDown() {
		cursorDown(innerView);
	}
	private static native void cursorDown(Object inner) /*-{
		inner.cursorDown();
	}-*/;
	
	@Override
	public void insert(char charAt) {
		insert(String.valueOf(charAt));
	}

	@Override
	public String getSelectionString() {
		return "";
	}
	
	
	@Override
	public void kopieer(FormuleClipboardIF clip) {
		String s = kopieer(innerView);
		if (s != null) clip.setClipboard(s);
	}
	private static native String kopieer(Object inner) /*-{
		return inner.kopieer();
	}-*/;
	
	@Override
	public void knip(FormuleClipboardIF clip) {
		String s = knip(innerView);
		if (s != null) clip.setClipboard(s);
	}
	private static native String knip(Object inner) /*-{
		return inner.knip();
	}-*/;
	
	@Override
	public void plak(FormuleClipboardIF clip) {
		insert(clip.getClipboard());
	}

//	private static native void plak(Object inner) /*-{
//		inner.plak();
//	}-*/;
	
	@Override
	public void macht() {
		insert("$m@"); 
	}

	@Override
	public void wortel() {
		insert("$w@");
	}

	@Override
	public void breuk() {
		insert("$b$n@@");
	}

	@Override
	public void kwadraat() {
		insert("$m2@");
	}

	@Override
	public void ndewortel() {
		insert("$W$n@@");
	}

	@Override
	public void haakjes() {
		insert("$h@");
	}

	@Override
	public void integraal() {
		insert("$i$n$k$l@@@@");
	}

	@Override
	public void prv() {
		insert("$q$n$k$l@@@@");
	}

	@Override
	public void ndelog() {
		insert("$L$n@@");
	}

	@Override
	public void abs() {
		insert("$r@");
	}

	@Override
	public void subscript() {
		insert("$s@");
	}

	@Override
	public void bin() {
		insert("$y$n@@");
	}

	@Override
	public void diff() {
		insert("$d$n@@");
	}

	@Override
	public void diff_partial() {
		insert("$D$n@@");
	}
	
	@Override
	public void limiet0() {
		insert("$T$n$k$l@@@@");
	}

	@Override
	public void limiet1() {
		insert("$T$n$k$l@@@@");
	}

	@Override
	public void limiet2() {
		insert("$T$n$k$l@@@@");
	}

	@Override
	public void primitieve() {
		insert("$P$n@@");
	}

	@Override
	public void conjug() {
		insert("$c@");
	}

	@Override
	public void sigma() {
		insert("$S$n$k$l@@@@");
	}
	
    @Override
    public void stelsel() {
        insert("$Q@");
    }
    
    @Override
    public void stelsel(int aantalRijen)
    {
        insert("$Q@");
    }
    
	@Override
	public void vectornotatie()
	{
		insert("$z@");
	}

	@Override
	public void vector()
	{
		insert("$Y@");
	}

	@Override
	public void vector(int aantalRijen)
	{
		insert("$Y@");
	}

	@Override
	public void matrix()
	{
		insert("$M@");
	}

	@Override
	public void matrix(int aantalRijen, int aantalKolommen)
	{
		insert("$M@");
	}

	@Override
	public int getAsHoogte() {
		return facade.wrapAsHoogte(defaultFont.getAscent());
	}

	@Override
	public int getHeight() {
		if (hasFullWidth) return fullheight();
		return facade.wrapHeight(height);
	}

	@Override
	public int getWidth() {
		if (hasFullWidth) return fullwidth;
		return facade.wrapWidth(width);
	}
	
  public void zetVolledigeBreedte(int breedte) {
    if (volledigeBreedte) {
      volledigeBreedte = false; // one-shot, kan maar één keer worden aangeroepen.
      width = fullwidth = breedte;
      initFrame();
    } else if (hasFullWidth && breedte != fullwidth) {
    	fullwidth = breedte;
    	frame.setPixelSize(fullwidth, fullheight());
    }
  }

	private int fullheight() {
		int ch = getConstantHeight();
		int cw = getConstantWidth();
		return ch + (height-ch) * (fullwidth-cw) / (width-cw);
	}

	@Override
	public void setAsHoogte(int ashoogte) {
		//this.ashoogte = ashoogte;
	}

	/**
	 * @return
	 * @see nl.uu.fi.dwo.interaction.client.OpdrNavIF#getLearnerId()
	 */
	public String getLearnerId() {
		String learnerId = comRoot.getLearnerId();
		if(learnerId == null || learnerId.isEmpty())
			learnerId = "guest";
		return learnerId;
	}

	/**
	 * @return
	 * @see nl.uu.fi.dwo.interaction.client.OpdrNavIF#getLearnerName()
	 */
	public String getLearnerName() {
		String learnerName = comRoot.getLearnerName();
		if(learnerName == null || learnerName.isEmpty())
			learnerName = "guest";
		return learnerName;
	}

	/**
	 * @return
	 * @see nl.uu.fi.dwo.interaction.client.OpdrNavIF#getBackground()
	 */
	public CssColor getBackground() {
		return comRoot.getBackground();
	}

	public String getBackgroundAsString() {
		return getBackground().value();
	}

	/**
	 * @return
	 * @see nl.uu.fi.dwo.interaction.client.OpdrNavIF#getUnitId()
	 */
	public String getUUID() {
		return comRoot.getUUID();
	}

	@Override
	public HandlerRegistration addCBookEventListener(
			String command, CBookEventListener listener) {
		return comRoot.addCBookEventListener(command, listener);
	}

	@Override
	public void fireEvent(CBookEvent event) {
		if (event.getCommand().equals("logOption") && logging != null) {
			logging.log(event.getParameters());
			return;
		}
		if (aContext != null && event.getCommand().equals("gotoPlace")) {
			aContext.gotoPlace(event.getMessage());
			return;
		}
		if (event.getCommand().equals("resize")) {
			Number w = (Number) event.getParameter("width");
			Number h = (Number) event.getParameter("height");
			if (w == null) w = Integer.valueOf(-1);
			if (h == null) h = Integer.valueOf(-1);
			pasAanWH(w.intValue(),h.intValue());
			
		}
		if(comRoot != this)
			comRoot.fireEvent(event);		
	}

	@Override
	public FormuleClipboardIF getFormuleClipboard() {
		if(comRoot!=this)
			return comRoot.getFormuleClipboard();
		return null;
	}

	@Override
	public LessonMode getLessonMode() {
		return comRoot.getLessonMode(); // TODO sent lessonMode.name to 'Stub' 
	}

	public Role getRole() {
		if(comRoot==this) return ROLE_LEARNER;
		return comRoot.getRole();
	}

	@Override
	public boolean hasListeners(String command) {
		if(comRoot == this) return false;
		return comRoot.hasListeners(command);
	}

	@Override
	public void tab() {
		try {
			tab(innerView);
		} catch(Exception not_implemented) {	
		}
	}

	private static native void tab(Object innerView)/*-{ innerView.tab() }-*/;
	private static native void shiftTab(Object innerView)/*-{ innerView.shiftTab() }-*/;
	private static native void selectAll(Object innerView)/*-{ innerView.selectAll() }-*/;

	@Override
	public void shiftTab() {
		try {
			shiftTab(innerView);
		} catch(Exception not_implemented) {	
		}
	}

	@Override 
	public String toString() {
		if(innerView != null) 
			try {
				return editorToString(innerView);
			} catch(Throwable e) {GWT.log("toString", e);}
		return "";
	}
	
	public ObjectMap getConfiguration() {
		if(comRoot!=this && comRoot!=null)
		{
			ObjectMap cfg = comRoot.getConfiguration();
			if (regel != null) {
				HashMap copy = new HashMap();
				for(String key: cfg.keySet()) {
					copy.put(key, cfg.get(key));
				}
				copy.put("fgColor", regel.getColor().value());
				copy.put("fontSize", regel.getFont().getFontSize());
				copy.put("fontName", regel.getFont().getFont());
				cfg = JSONUtilities.wrapMap(copy);
			}			
			return cfg;
		}
		return null;
	}

	public JavaScriptObject getConfiguration0() {
		ObjectMap map = getConfiguration();
		if(map instanceof JSONObjectMapImpl) {
			return ((JSONObjectMapImpl) map).unwrap().getJavaScriptObject();
		} else if (map instanceof Map) {
			return JSONUtilities.toJSONObject(map).isObject().getJavaScriptObject();
		}
		else {
			return null;
		}
	}
	
	@Override
	public void acceptCBookEvent(CBookEvent event) {
		if("action.setNotEditable".equals(event.getCommand())) {
			if(setNotEditableListener != null) {
				setNotEditableListener.acceptCBookEvent(event);
			} else {
				seal = true;
				frame.setStyleName(DWOplayer.DWO_BUNDLE.dwoplayercss().StubView_readonly(), true);
			}
		}
	}

	@Override
	public ObjectMap getContext() {
		if(comRoot != this && comRoot != null) {
			return comRoot.getContext();
		}
		JSONObject object = new JSONObject();
		object.put("premium", JSONBoolean.getInstance(activity.isPremium())); //werkt altijd!
		return JSONUtilities.wrapMap(object);
	}

	public JavaScriptObject getContext0() {
		ObjectMap map = getContext(); // Never null!
		return JSONUtilities.toJSONObject(map).isObject().getJavaScriptObject();		
	}

	@Override public void setVisited() {
		if (comRoot != this && comRoot != null) {
			comRoot.setVisited();
		}
	}

	@Override
	public void selectAll() {
		try {
			selectAll(innerView);
		} catch(Exception not_implmented) {}
	}

	@Override
	public void insertcp(int codepoint) {
		insert("$Z" + codepoint + "@");
	}

	@Override
	public void setFontSize(int font_size) {
	}

	@Override
	public void setFontName(String font_name) {
	}

	@Override
	public void setFontStyle(int font_style) {
	}

	private TekstRegel regel;
	private CssColor fgColor;
	@Override
	public void setParentRegel(TekstRegel regel) {
		this.regel = regel;
		fgColor = regel.getColor();
	}
	
	protected void pasAanWH(int w, int h) {
		if (regel != null && visibleChain()) {
				if (w != -1 && w != getWidth()) {
					if (hasFullWidth) fullwidth = w;
				    width = w;
				} else {
					w = -1;
				}
				if (h != -1 && h != getHeight()) {
					height = h;
					if (hasFullWidth) h = getHeight();
				} else {
					h = -1;
				}
				frame.setPixelSize(w, h);
				if(w != -1 || h != -1)
					regel.resize();
		}
	}

	private boolean visibleChain() {
		if (frame.isAttached() ) {
			Widget w = frame;
			Widget root = RootLayoutPanel.get();
			while (w != root && w != null) {
				if (!w.isVisible()) return false;
				w = w.getParent();
			}
			return true;
		} 
		return false;
	}

	
}
