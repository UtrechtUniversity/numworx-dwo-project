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
import nl.uu.fi.dwo.mobile.utils.PopupFacade;

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

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class StubView extends SimplePanel implements InteractionView, LoadHandler, OpdrNavIF, FormuleEditorIF {

	private Frame frame;
	private Object innerView;
	private HashMap innerMap;
	private OpdrNavIF comRoot = this;
	private HashMap randomVars;
	private String pendingState;
	private int width;
	private int height;
	private PopupFacade facade;
	private FormuleFont defaultFont = FormuleFont.createFromFontSize(18);

	public StubView(String html, HashMap<String, Object> launchdata, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		init(html, launchdata, randomVarNamen, randomVarWaarden);
	}
	
	private void init(String html, HashMap<String, Object> launchData,
			String[] randomVarNamen, HashMap randomVarWaarden) {
		innerMap = (HashMap) launchData.get("interactiePanelLaunchState");
		randomVars = randomVarWaarden;
		facade = new PopupFacade(launchData);
		frame = new Frame(html);
		frame.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		frame.setStylePrimaryName(".gwt-StubView");
		frame.addStyleDependentName("borderless");
		Number width = ((Number)launchData.get("breedte"));
		if (width == null)
			width = 400;
		Number height = (Number) launchData.get("hoogte");
		if (height == null)
			height = 400;
		frame.setSize(width + "px", height + "px");
		this.width = width.intValue();
		this.height = height.intValue();
		frame.addLoadHandler(this);
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
	
	private native static boolean isCorrect(Object inner) /*-{
		return inner.isCorrect();
	}-*/;

	@Override
	public boolean isCorrect() {
		if(innerView != null)
			return isCorrect(innerView);
		return false;
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		this.comRoot = comRoot;
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
			Logger.getLogger("StubView").log(Level.SEVERE,"init", e);
		}
		
				
	}
	
	
	private static native void init(Object inner, int width, int height, String launchdata,
			JavaScriptObject randomVars) /*-{ 
				inner.init(width, height, launchdata, randomVars);
			}-*/;

	@Override
	public void onLoad(LoadEvent event) {
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
		wnd.setChanged = function(viewer) {
			viewer.@nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.StubView::comRoot.@nl.uu.fi.dwo.interaction.client.OpdrNavIF::setChanged()();
		}
	
		wnd.setFocus = function(b, viewer) {
			viewer.@nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.StubView::setFocus(Z)(b)
		}
		
		return wnd.inner;
	}-*/;

	public void setFocus(boolean b) {
		comRoot.getKeyboard().setEditor( b ? this : null);
	}
	
	
	@Override
	public void setChanged() {
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
	public void insert(char charAt) {
		insert(String.valueOf(charAt));
	}

	@Override
	public String getSelectionString() {
		return "";
	}

	@Override
	public void macht() {
		insert("$m@"); // TODO evenzo voor de rest...
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
	
}
