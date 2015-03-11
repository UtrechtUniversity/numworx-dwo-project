package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.utils.PopupFacade;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.FrameElement;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Widget;

public class MC2View extends Composite implements InteractionView, LoadHandler {

	public static final String CROSS_WIDGET_ID = "crossWidgetId";

	private static native JavaScriptObject createIframe(String id, int width, int height, String locale, String relay)
	/*-{
		return $win.createIframe(id, width, height, locale, relay)
	}-*/;
	
	private static native JavaScriptObject getIframe(JavaScriptObject o) /*-{
		return o.getIframe();
	}-*/;
	
	private static native void init(JavaScriptObject inner, int width, int height, String launchdata,
			String randomVars) /*-{ 
				inner.init(width, height, launchdata, randomVars);
			}-*/;

	
	int width;
	int height;
	boolean volledigeBreedte;
	String locale;
	String id;
	String relay;
	ObjectMap innerMap;
	Map randomVars;
	PopupFacade facade;
	IFrame frame;
	private HandlerRegistration loadhandler;
	private OpdrNavIF comRoot;
	private JavaScriptObject innerView;
	
	
	public MC2View(HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		ObjectMap launchdata = JSONUtilities.wrapMap(h);
		String id = launchdata.getString(CROSS_WIDGET_ID);
		String locale = StubView.getLocale();
		InlineHTML html = new InlineHTML();
		html.getElement().setId(id);
		initWidget(html);
		init(id, launchdata, randomVarWaarden);
	}

	private static class IFrame extends Frame {

		public IFrame(JavaScriptObject node) {
			super(Element.as(node));
		}
	}
	
	private void initFrame() {
		JavaScriptObject container = 
		createIframe(id, width, height, locale, relay);
		JavaScriptObject node = getIframe(container);
		frame = new IFrame( node );
		loadhandler = frame.addLoadHandler(this);
	}
	
	
	private void init(String id, ObjectMap outermap,
			HashMap randomVarWaarden) {
		this.id = id;
		innerMap = outermap.getObjectMap("interactiePanelLaunchState");
		randomVars = randomVarWaarden;
		width = 400; if(outermap.containsKey("breedte")) width = outermap.getInt("breedte");
		height =400; if(outermap.containsKey("hoogte")) height = outermap.getInt("hoogte");
		volledigeBreedte  = outermap.getBoolean("volledigeBreedte", false);
		facade = new PopupFacade(outermap);
		if(!volledigeBreedte)
			initFrame();
	}

	public Widget asWidget() {
		return facade.wrap(this);
	}


	@Override
	public int getAsHoogte() {
		return 0;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public void setAsHoogte(int ashoogte) {
	}

	@Override
	public HashMap<String, Object> getState() {
		// TODO Auto-generated method stub
		return new HashMap<>();
	}

	@Override
	public void setState(HashMap<String, Object> h) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getScore() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Boolean isCorrect() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void kijkNa() {
		// TODO Auto-generated method stub

	}

	@Override
	public void zetNagekeken(boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		this.comRoot = comRoot;
	}

	@Override
	public void zetVolledigeBreedte(int breedte) {
		if(volledigeBreedte) {
			this.width = breedte;
			initFrame();
		}
	}

	@Override
	public void onLoad(LoadEvent event) {
		loadhandler.removeHandler();
		JavaScriptObject w = getContentWindow(frame.getElement());
		if (w != null)
		{
			innerView = getApplet(w, this);
			if(innerView != null)
				publish(innerView);
		}
	}

	private static native JavaScriptObject getContentWindow(com.google.gwt.user.client.Element frame) /*={
		return frame.contentWindow;
	}-*/;

	public static native JavaScriptObject getApplet(JavaScriptObject wnd, MC2View view) /*-{
	wnd.outer = view;
	wnd.publish = function(o, viewer) {
		return viewer.@nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.MC2View::publish(Ljava/lang/Object;)(o)
	}
	wnd.getMode = function(viewer) {
		return viewer.@nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.MC2View::getMode()()
	}
	wnd.getLearnerName = function (viewer) {
		return viewer.@nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.MC2View::getLearnerName()()
	}
	wnd.getLearnerId = function (viewer) {
		return viewer.@nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.MC2View::getLearnerId()()
	}
	wnd.getUUID = function (viewer) {
		return viewer.@nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.MC2View::getUUID()()
	}
	wnd.getBackground = function (viewer) {
		return viewer.@nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.MC2View::getBackgroundAsString()()
	}	
	return wnd.inner;
}-*/;


	private void publish(Object inner) { 
		innerView = (JavaScriptObject) inner;
		try {
			init(innerView, width, height, JSONUtilities.toJSONObject(innerMap).toString(), 
					JSONUtilities.toJSONObject(randomVars).toString()); 

//			if(pendingState != null) {
//				setState(inner, pendingState);
//				pendingState = null;
//			} 
		} catch(Exception e) {
			Logger.getLogger("MC2View").log(Level.SEVERE,"init "+ e);
		}
		

	}
	
	/**
	 * Delegate pattern.
	 */
	public int getMode() {
		return comRoot.getMode();
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
	 * @see nl.uu.fi.dwo.interaction.client.OpdrNavIF#getUnitId()
	 */
	public String getUUID() {
		return comRoot.getUUID();
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

	
}
