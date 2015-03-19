package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.utils.PopupFacade;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Widget;

public class MC2View extends Composite implements InteractionView {

	private static final Logger LOGGER = java.util.logging.Logger.getLogger("MC2View");
	public static final String CROSS_WIDGET_ID = "crossWidgetId";

	private static native JavaScriptObject createIframe(String id, int width, int height, String locale, String relay)
	/*-{
		return $wnd.createIframe(id, width, height, locale, relay)
	}-*/;
	
	private static native void setBootstrap(String id, MC2View diz) /*-{
		$wnd.setBootstrap(id, function(xwid, data) {
			diz.@nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.MC2View::doOpenAjaxEvent(Ljava/lang/String;Ljava/lang/Object;)
			(xwid, data);
		})
		
	}-*/;
	
	
	private void doOpenAjaxEvent(String topic, Object data) {
		LOGGER.info("topic:" + topic + " ,data:" + data);
	}
	
	private static native JavaScriptObject getIframe(JavaScriptObject o) /*-{
		return o.getIframe();
	}-*/;
	
	private static native void init(JavaScriptObject inner, int width, int height, String launchdata,
			String randomVars, String action) /*-{ 
				inner.init(width, height, launchdata, randomVars, action);
			}-*/;

	
	private void onBootstrap() {
		onLoadApplet();
		if(innerView == null) {
			LOGGER.info("waiting for iframe");
			Timer t = new Timer() {

				@Override
				public void run() {
					onBootstrap();				
				}};
				t.schedule(200);
		}
	}
	
	private int width;
	private int height;
	private boolean volledigeBreedte;
	private String locale;
	private String id;
	private String relay;
	private ObjectMap innerMap;
	private Map randomVars;
	private PopupFacade facade;
	private IFrame frame;
	private OpdrNavIF comRoot;
	private JavaScriptObject innerView;
	
	
	public MC2View(HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		ObjectMap launchdata = JSONUtilities.wrapMap(h);
		String id = launchdata.getString(CROSS_WIDGET_ID);
		locale = StubView.getLocale();
		relay = getAction(launchdata.getObjectMap("interactiePanelLaunchState").getString("className"));
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
		JavaScriptObject container = createIframe(id, width, height, locale, relay);
		JavaScriptObject node = getIframe(container);
		frame = new IFrame( node );

		Timer t = new Timer()
		{
			@Override
			public void run()
			{
				onBootstrap();
			}
		};
		t.schedule(100);
	}
	
	
	private void init(String id, ObjectMap outermap,
			HashMap randomVarWaarden) {
		this.id = id;
		innerMap = outermap.getObjectMap("interactiePanelLaunchState");
		if(randomVarWaarden != null)
			randomVars = randomVarWaarden;
		else
			randomVars = Collections.EMPTY_MAP;
		width  = 400; if(outermap.containsKey("breedte")) width = outermap.getInt("breedte");
		height = 400; if(outermap.containsKey("hoogte")) height = outermap.getInt("hoogte");
		volledigeBreedte  = outermap.getBoolean("volledigeBreedte", false);
		facade = new PopupFacade(outermap);
	}

	public Widget asWidget() {
		Widget wrap = facade.wrap(this);
		wrap.addAttachHandler(new Handler() {
			
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				initFrame();
			}
		});
		return wrap;
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
		}
	}


	private void onLoadApplet() {
		JavaScriptObject w = getContentWindow(frame.getElement());
		if (w != null)
		{
			innerView = getApplet(w, this);
			if(innerView != null)
				publish(innerView);
		}
	}

	private static native JavaScriptObject getContentWindow(JavaScriptObject frame) /*-{
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

	static final Map<String,String> actionMap = new HashMap<String,String>();
	
	static {
		actionMap.put("de.cinderella.CindyWidget", "http://cinderella.de/services/widget");
		actionMap.put("org.cbook.mediaman.MediaMan", "http://mc2-mediaman.appspot.com/");
		actionMap.put("maltsample.maltWidget", "http://www.talent.gr/malt/");
		actionMap.put("widgetsample.SampleWidget", "http://mc2-jssample.appspot.com/Jssample.jsp");
	}
	
	String getAction(String name) 
	{
		String action = actionMap.get(name);
		if(action ==  null)
			return "https://ws.fisme.science.uu.nl/DWOmAccess/lti/widget.jsp";
		return action;
	}


	private void publish(Object inner) { 
		innerView = (JavaScriptObject) inner;
		try {
			setBootstrap(getUUID(), this);
			init(innerView, width, height, JSONUtilities.toJSONObject(innerMap).toString(), 
					JSONUtilities.toJSONObject(randomVars).toString(), relay); 
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
