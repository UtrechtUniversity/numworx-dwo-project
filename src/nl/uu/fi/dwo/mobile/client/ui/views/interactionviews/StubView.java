package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
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
public class StubView extends SimplePanel implements InteractionView, LoadHandler, OpdrNavIF {

	private Frame frame;
	private Object innerView;
	private HashMap innerMap;
	private OpdrNavIF comRoot = this;
	private HashMap randomVars;

	public StubView(String html, HashMap<String, Object> launchdata, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		init(html, launchdata, randomVarNamen, randomVarWaarden);
	}
	
	private void init(String html, HashMap<String, Object> launchData,
			String[] randomVarNamen, HashMap randomVarWaarden) {
		innerMap = (HashMap) launchData.get("interactiePanelLaunchState");
		randomVars = randomVarWaarden;
		
		frame = new Frame(html);
		Object width = launchData.get("breedte");
		if (width == null)
			width = "400";
		Object height = launchData.get("hoogte");
		if (height == null)
			height = "400";
		frame.setSize(width + "px", height + "px");
		frame.addLoadHandler(this);
		setWidget(frame);
	}

	@Override
	public HashMap<String, Object> getState() {
		String jso = getState(innerView);
		if(jso != null)
		{
			JSONObject js = JSONParser.parseLenient(jso).isObject();
			return JSONUtilities.fromJSONObject(js);
		}
		return null;
	}

	@Override
	public void setState(HashMap<String, Object> h) {
		JSONValue object = JSONUtilities.toJSONObject(h);
		setState(innerView, object.toString());
	}

	@Override
	public int getScore() {
		return getScore(innerView);
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
			init(inner, JSONUtilities.toJSONObject(innerMap).toString(), 
					JSONUtilities.toJSONObject(inits).isObject().getJavaScriptObject()); // FIXME ook toString?
			} catch(Exception e) {
				GWT.log("init", e);
		}

// VANAF HIER TESTEN
		final int score = getScore();
		GWT.log("score is " + score);
		try {
			HashMap<String, Object> o = getState();
			GWT.log("state " + o);			
			setState(o);
		} catch (Exception e) {
			GWT.log("state", e);
		}
		
		try {
			final boolean correct = isCorrect();
			GWT.log("correct " + correct);
		} catch (Exception e) {
			GWT.log("correct", e);
		}
		
	}
	
	
	private native void init(Object inner, String launchdata,
			JavaScriptObject randomVars) /*-{ 
				inner.init(launchdata, randomVars);
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
		return wnd.inner;
	}-*/;

	@Override
	public void setChanged() {
	}


	
	
}
