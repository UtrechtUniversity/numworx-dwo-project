package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.utils.Logging;
import nl.uu.fi.dwo.mobile.utils.PopupFacade;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Widget;

public class MC2View extends Composite implements InteractionView {

	private static final Logger LOGGER = java.util.logging.Logger.getLogger("MC2View");
	public static final String CROSS_WIDGET_ID = "crossWidgetId";
	private static final String LOG_ID = "logID";

	private static native JavaScriptObject createIframe(String id, int width, int height, String locale, String relay)
	/*-{
		return $wnd.createIframe(id, width, height, locale, relay)
	}-*/;
	
	private static native void removeIframe(JavaScriptObject fr)
	/*-{
	 	$wnd.removeIframe(fr)
	}-*/;
	
	private static native void setBootstrap(String id, MC2View diz) /*-{
		$wnd.setBootstrap(id, function(xwid, data) {
			diz.@nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.MC2View::doOpenAjaxEvent(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)
			(xwid, data);
		})
		
	}-*/;
	
	
	private Logging latransport = DWOplayer.PARAMETERS.getLogging();
	
	private void doOpenAjaxEvent(String topic, JavaScriptObject data) {
		LOGGER.info("topic:" + topic + " ,data:" + data + " " + getUUID());
		
		if(topic.endsWith(".logOption"))
		{
			JSONObject json = new JSONObject(data);
			json = json.get("parameters").isObject();
			latransport.log(JSONUtilities.wrapMap(json));
			return;
		}
		if(topic.endsWith(".changed") || topic.endsWith(".checked"))
		{
			JSONObject json = new JSONObject(data);
			json = json.get("parameters").isObject();
			ObjectMap map = JSONUtilities.wrapMap(json);
			score = map.getInt("score");
			String status = map.getString("success_status");
			if ("PASSED".equalsIgnoreCase(status))
				correct = Boolean.TRUE;
			else if ("FAILED".equalsIgnoreCase(status))
				correct = Boolean.FALSE;
			else 
				correct = null;
			comRoot.setChanged(Boolean.FALSE.equals(correct));
			return;
		}
		
		
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
	private JavaScriptObject container;
	private int score;
	private Boolean correct;
	private int scoreMax;
	private boolean teltmee;
	private boolean[][] logObjectives;
	
	
	public MC2View(HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		ObjectMap launchdata = JSONUtilities.wrapMap(h);
		String id = launchdata.getString(CROSS_WIDGET_ID);
		locale = StubView.getLocale();
		ObjectMap launchState = launchdata.getObjectMap("interactiePanelLaunchState");
		latransport.setLogID(launchState.getString(LOG_ID));
		relay = getAction(launchState.getString("className"));
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
		container = createIframe(id, width, height, locale, relay);
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

		if(innerMap.containsKey("scoreMax"))
			scoreMax = innerMap.getInt("scoreMax");
		correct = null;
		teltmee = true;
		if(innerMap.containsKey("teltmee")) 
			teltmee = innerMap.containsKey("teltmee");
		if(innerMap.containsKey("logObjectives")) 
		{
			ObjectList logObjectivesList = ( innerMap.getObjectList("logObjectives") );
			logObjectives = new boolean[logObjectivesList.size()][];
			for(int i = 0; i < logObjectivesList.size(); i++)
			{	logObjectives[i] = logObjectivesList.getBooleanArray(i);
			}
		}

		facade = new PopupFacade(outermap);
	}

	public Widget asWidget() {
		Widget wrap = facade.wrap(this);
		this.addAttachHandler(new Handler() {
			
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				if( event.isAttached())
					initFrame();
				else 
				{
					LOGGER.info("detach MC2view");
					removeIframe(container);
				}
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
		HashMap<String, Object> result = new HashMap<String,Object>();
		// TODO fill from state[xwid]?
		return wrap(result);
	}

	private HashMap<String, Object> wrap(HashMap<String, Object> map) {
		if(map == null) map = new HashMap<String, Object>();
		map.put("STUBVIEW_score", String.valueOf(getScore()));
		map.put("STUBVIEW_correct", String.valueOf(isCorrect()));
		return map;
	}

	@Override
	public void setState(HashMap<String, Object> h) {
		if(h.containsKey("STUBVIEW_score"))
			score = Integer.parseInt(h.remove("STUBVIEW_score").toString());
		if(h.containsKey("STUBVIEW_correct"))
			correct = StubView.toBoolean(h.remove("STUBVIEW_correct").toString());
	}

	@Override
	public int getScore() {
		return score;
	}
	
	@Override
	public int[][] getScoreObjectives() {
		if(innerView != null && logObjectives != null)
		{
			int score = getScore();
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

	@Override
	public Boolean isCorrect() {
		if(scoreMax == 0 || !teltmee)
			return Boolean.TRUE;
		return correct;
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
		latransport.setCommunicationRoot(comRoot);
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
	wnd.getLessonMode = function (viewer) {
		return viewer.@nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.MC2View::getLessonModeAsString()()
	}
	wnd.getRole = function (viewer) {
		return viewer.@nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.MC2View::getRoleAsString()()
	}
	return wnd.inner;
}-*/;

	static final Dictionary actionMap = Dictionary.getDictionary("mc2");
	
	
	String getAction(String name) 
	{
		latransport.setClassName(name);
		try {
			String action = actionMap.get(name);
			if(action !=  null)
				return action;
		} catch (MissingResourceException e) {
			LOGGER.log(Level.INFO, null, e);
		}
		return "http://ws.fisme.science.uu.nl/DWOmAccess/lti/widget.jsp";
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
			LOGGER.log(Level.SEVERE,"init "+ e);
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

	public String getLessonModeAsString() {
		return comRoot.getLessonMode().toString();
	}
	
	public String getRoleAsString() {
		return comRoot.getRole().toString();
	}
	
}
