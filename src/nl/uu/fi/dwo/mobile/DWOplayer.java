package nl.uu.fi.dwo.mobile;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nl.uu.fi.dwo.mobile.client.sco.SCORM_DWOmAccess;
import nl.uu.fi.dwo.mobile.client.sco.SCORM_guest;
import nl.uu.fi.dwo.mobile.client.ui.AppPlaceHistoryMapper;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactoryImpl;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem.Type;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.TabletActivityMapper;
import nl.uu.fi.dwo.mobile.client.ui.TabletAnimationMapper;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.mgwt.mvp.client.AnimatableDisplay;
import com.googlecode.mgwt.mvp.client.AnimatingActivityManager;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.MGWTSettings.ViewPort;
import com.googlecode.mgwt.ui.client.MGWTSettings.ViewPort.DENSITY;

import fi.wiskopdr.text.Text_nl;

/**
 * Main class (entry point) Sets up the DWO player.
 * 
 * @author Evertson Croes, Danny Hendrix
 * 
 */
public class DWOplayer implements EntryPoint
{
	public static final String PREFIX = "http://ws-dev.fisme.science.uu.nl/DWOmAccess/getLaunchData?s=";
	
	private Place defaultPlace = new LoginPlace(); // new SelectModulePlace("select");

	public static final AsyncCallback<List<Map<String,Object>>> GETCOURSES_CALLBACK = new AsyncCallback<List<Map<String,Object>>>() {
	
		@Override
		public void onFailure(Throwable caught) {
			Window.alert(caught.toString());
		}
	
		@Override
		public void onSuccess(List<Map<String,Object>> result) {
			SelectModuleItemHolder.clear(); // FIXME hier leegmaken of elders?
			for (Iterator<Map<String, Object>> iterator = result.iterator(); iterator.hasNext();) {
				Map<String, Object> map = (Map<String, Object>) iterator.next();
				SelectModuleItem item = new SelectModuleItem(map, SelectModuleItem.Type.MODULE);
				SelectModuleItemHolder.insert(item);
			}
			
			clientfactory.getPlaceController().goTo(new TreeModulePlace("0"));
		}
		
	};
	//private Place defaultPlace = new SelectModulePlace("Home");
	private static HashMap<String, String> resources = new HashMap<String, String>();
	//public static Locale language = new Locale ("nl", "");
	public static Text_nl rb = new Text_nl();

	public static Map<String, Object> profiledata = null;
	public static ClientFactory clientfactory;
	public static SCORM_guest api;

	/**
	 * This is the entry point method.
	 */
	public void start()
	{
		setupResources();
		setupDWOPlayer();
	}

	//Sets up the GWT and MGWT settings needed to run the application
	public void setupDWOPlayer()
	{
		//MGWT Settings//
		ViewPort viewport = new MGWTSettings.ViewPort();
		viewport.setTargetDensity(DENSITY.MEDIUM);
		viewport.setUserScaleAble(false).setMinimumScale(1.0).setMaximumScale(1.0);
		MGWTSettings settings = new MGWTSettings();
		settings.setViewPort(viewport);
		settings.setAddGlosToIcon(true);
		settings.setFullscreen(true);
		settings.setPreventScrolling(true);
		MGWT.applySettings(settings);

		//GWT Settings//
		clientfactory = new ClientFactoryImpl();
		AppPlaceHistoryMapper historyMapper = GWT.create(AppPlaceHistoryMapper.class);
		final PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
		historyHandler.register(clientfactory.getPlaceController(), clientfactory.getEventBus(), defaultPlace);
		createTabletDisplay(clientfactory);
		historyHandler.handleCurrentHistory();
	}

	private void createTabletDisplay(ClientFactory clientfactory)
	{
		AnimatableDisplay display = GWT.create(AnimatableDisplay.class);
		TabletActivityMapper appActivityMapper = new TabletActivityMapper(clientfactory);
		TabletAnimationMapper animationMapper = new TabletAnimationMapper();
		AnimatingActivityManager activityMapper = new AnimatingActivityManager(appActivityMapper, animationMapper, clientfactory.getEventBus());
		activityMapper.setDisplay(display);
		RootPanel.get().add(display);
	}

	@Override
	public void onModuleLoad()
	{
		Timer t = new Timer()
		{
			@Override
			public void run()
			{
				start();
			}
		};
		t.schedule(1);

	}

	private void setupResources()
	{
		resources.put("SelectionColor", "#88f");
	}

	public static String getResource(String resource)
	{
		return resources.get(resource);
	}

	//Used for debugging
	public static void log(String log)
	{
		System.out.println(log);
	}

	public static void gotoCourses() {
		if(profiledata == null)
			clientfactory.getEntryView().setApi(api = new SCORM_guest());
		else
		{	int userID = ((Integer) profiledata.get("userID")).intValue();
			clientfactory.getEntryView().setApi(api = new SCORM_DWOmAccess(userID));
		}
		clientfactory.getRPCHandler().getCourses(profiledata, GETCOURSES_CALLBACK);
	}
}
