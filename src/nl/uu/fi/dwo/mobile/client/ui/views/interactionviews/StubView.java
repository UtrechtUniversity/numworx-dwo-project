package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.sco.Memento;
import nl.uu.fi.dwo.mobile.utils.PopupFacade;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class StubView extends SimplePanel implements InteractionView, LoadHandler, OpdrNavIF, FormuleEditorIF {

	private Frame frame;
	private Object innerView;
	private ObjectMap innerMap;
	private OpdrNavIF comRoot = this;
	private HashMap randomVars;
	private String pendingState;
	private int width;
	private int height;
	private boolean volledigeBreedte;
	private PopupFacade facade;
	private FormuleFont defaultFont = FormuleFont.createFromFontSize(18);
	private HandlerRegistration loadhandler;

	public StubView(String html, HashMap<String, Object> launchdata, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		html = DWOplayer.PARAMETERS.getStubView() + html;
		String locale = getLocale();		
		html += "?locale=" + locale;
		init(html, launchdata, randomVarNamen, randomVarWaarden);
	}

	static String getLocale() {
		String locale = "nl"; // FIXME get Locale from Window?
		String query = Window.Location.getQueryString();
		int k = query.indexOf("locale=");
		if(k > 0)
		{
			query = query.substring(k+7);
			k = query.indexOf('&');
			if(k > 0) query = query.substring(0, k);
			locale = query;
		}
		return locale;
	}
	
	private void init(String html, HashMap<String, Object> launchData,
			String[] randomVarNamen, HashMap randomVarWaarden) {
		ObjectMap outermap = JSONUtilities.wrapMap(launchData);
		innerMap = outermap.getObjectMap("interactiePanelLaunchState");
		randomVars = randomVarWaarden;
		facade = new PopupFacade(launchData);
		frame = new Frame(html);
		frame.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		frame.setStylePrimaryName(".gwt-StubView");
		frame.addStyleDependentName("borderless");
		int width = 400; if(outermap.containsKey("breedte")) width = outermap.getInt("breedte");
		int height =400; if(outermap.containsKey("hoogte")) height = outermap.getInt("hoogte");
		boolean volledigeBreedte = false; if(outermap.containsKey("volledigeBreedte")) volledigeBreedte = outermap.getBoolean("volledigeBreedte");
		
		this.volledigeBreedte = volledigeBreedte;
		this.width = width;
		this.height = height;
		if(!volledigeBreedte ) initFrame();
	}

	
	private void initFrame() {
		frame.setPixelSize(width , height);
		loadhandler = frame.addLoadHandler(this);
		setWidget(frame);
	}

	@Override
	public HashMap<String, Object> getState() {
		if(innerView != null)
		{
			String jso = getState(innerView);
			if(jso != null)
			{
				JSONObject js = JSONParser.parseLenient(jso).isObject();
				//return JSONUtilities.fromJSONObject(js);
				return JSONUtilities.wrapMap(js);
			}
		}
		if(pendingState != null)
		{
			JSONObject js = JSONParser.parseLenient(pendingState).isObject();
			return JSONUtilities.wrapMap(js);
		}
		return null;
	}

	@Override
	public void setState(HashMap<String, Object> h) {
		JSONValue object = JSONUtilities.toJSONObject(h);
		if(innerView != null)
		{
			setState(innerView, object.toString());
			pendingState = null;
			pendingState = object.toString(); // reset komt mogelijk na
		}
		else 
			pendingState = object.toString(); // XXX NPE!
	}

	@Override
	public int getScore() {
		if(innerView != null)
			return getScore(innerView);
		return 0;
	}

	private native static int getScore(Object inner) /*-{
		return inner.getScore();
	}-*/ ;

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
		return inner.kijkNa();
	}-*/;
	
	private native static void zetNagekeken(Object inner, boolean b) /*-{
		inner.zetNagekeken(b);
	}-*/;

	@Override
	public Boolean isCorrect() {
		if(innerView != null) {
			String correct = isCorrect(innerView);
			if(correct == null || "null".equals(correct)) return null;
			return Boolean.valueOf(correct);
		}
		return null;
	}
	
	public void zetNagekeken(boolean b) {
		if(innerView != null)
			zetNagekeken(innerView, b);
	}
	
	public void kijkNa() {
		if(innerView != null)
			kijkNa(innerView);
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		this.comRoot = comRoot;
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
				pendingState = null;
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
			return viewer.@nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.StubView::fireJSEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(event)
		}
		
		wnd.addCBookEventListener = function (command, listener, viewer) {
			return viewer.@nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.StubView::addCBookEventListener(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(command, listener)
		}
		wnd.removeCBookEventListener = function (registration) {
			return @nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.StubView::removeCBookListener(Lcom/google/web/bindery/event/shared/HandlerRegistration;)(registration)
		}
		
		return wnd.inner;
	}-*/;

	public void setFocus(boolean b) {
		comRoot.getKeyboard().setEditor( b ? this : null);
	}
	
	private void fireJSEvent(JavaScriptObject jso) {
		JSONObject value = new JSONObject(jso);
		CBookEvent evt = new CBookEvent(JSONUtilities.wrapMap(value));
		fireEvent(evt);
	}
	
	private static native void acceptCBookEvent(JavaScriptObject jso, JavaScriptObject event) /*-{
		jso.acceptCBookEvent(event)
	}-*/;
	
	
	private HandlerRegistration addCBookEventListener(String command, final JavaScriptObject listener) {
		return comRoot.addCBookEventListener(command, new CBookEventListener() {
			
			@Override
			public void acceptCBookEvent(CBookEvent event) {
				JSONValue ev = JSONUtilities.toJSONObject(event.toObjectMap());
				StubView.acceptCBookEvent(listener, ev.isObject().getJavaScriptObject());
			}
		});
	}
	
	private static void removeCBookListener(HandlerRegistration r) {
		r.removeHandler();
	}
	
	@Override
	public void setChanged(boolean fout) {
		if(comRoot != this)
			comRoot.setChanged(fout);
	}

	public Widget asWidget() {
		return facade.wrap(this);
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
		cursorUp(innerView);
	}
	private static native void cursorDown(Object inner) /*-{
		inner.cursorUp();
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
	public void kopieer() {
		kopieer(innerView);
	}
	private static native void kopieer(Object inner) /*-{
		inner.kopieer();
	}-*/;
	
	@Override
	public void knip() {
		knip(innerView);
	}
	private static native void knip(Object inner) /*-{
		inner.knip();
	}-*/;
	
	@Override
	public void plak() {
		plak(innerView);
	}
	private static native void plak(Object inner) /*-{
		inner.plak();
	}-*/;
	
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
	public int getAsHoogte() {
		return 0;
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
	{
		if(volledigeBreedte)
		{
			width = breedte;
			initFrame();
		}
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
			learnerName = "Guest, Anonymous";
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
		
		
	}
}
