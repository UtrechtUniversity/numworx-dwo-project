package nl.uu.fi.dwo.mobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import nl.uu.fi.dwo.mobile.client.ui.places.SelectModulePlace;
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
	public static final boolean JSON = true;
	public static final String PREFIX = "http://ws-dev.fisme.science.uu.nl/DWOmAccess/get" + (JSON?"JSON":"") + "LaunchData?s=";
	public static final int PROFILE_ID = 77;
	
	private Place defaultPlace = new LoginPlace(); // new SelectModulePlace("select");

	public static int count;
	
	static void goTree() {
		if(--count <= 0)
			clientfactory.getPlaceController().goTo(new TreeModulePlace("0"));		
	}

	static void goFlat() {
		if(--count <= 0)
			clientfactory.getPlaceController().goTo(new SelectModulePlace("0"));		
	}
	
	public static final AsyncCallback<List<Map<String,Object>>> GETCOURSES_CALLBACK_CLASS_FLAT = new AsyncCallback<List<Map<String,Object>>>(){

		@Override
		public void onFailure(Throwable caught) {
			Window.alert(caught.toString());
			goFlat();
			
		}

		@Override
		public void onSuccess(List<Map<String, Object>> result) {
			insertFlat(result);
			goFlat();
		}
		
	};

	public static final AsyncCallback<List<Map<String,Object>>> GETCOURSES_CALLBACK = 
			new AsyncCallback<List<Map<String,Object>>>() {
		@Override
		public void onFailure(Throwable caught) {
			Window.alert(caught.toString());
			goTree();
		}	
		@Override
		public void onSuccess(List<Map<String,Object>> result) {	
			insertFlat(result);
			goTree();
		}		
	};	
	//private Place defaultPlace = new SelectModulePlace("Home");
	private static HashMap<String, String> resources = new HashMap<String, String>();

	public static final AsyncCallback<List<Map<String,Object>>>
	GETCOURSES_CALLBACK_CLASS_TREE = new AsyncCallback<List<Map<String,Object>>>(){

		@Override
		public void onFailure(Throwable caught) {
			Window.alert(caught.toString());
			goTree();
		}

		@Override
		public void onSuccess(List<Map<String, Object>> result) {
			sort(result);
			insertTree(result);
			goTree();
		}
		
		private int getParentID(Map<String,Object> course) {
			try {
				return ((Number) course.get("parentID")).intValue();
			} catch (Exception e) {
				return 0;
			}
		}
		
		private int getID(Map<String, Object> course) {
			try {
				return ((Number) course.get("courseID")).intValue();
			} catch (Exception e) {
				return 0;
			}
		}
		private void sort(List<Map<String,Object>> courses) {
			boolean again;
			if(courses == null || courses.isEmpty())
				return;
			do {
				again = false;
				more:
				for(int i = 0; i < courses.size(); i++ ) {
					Map<String,Object> course = courses.get(i);
					if( getParentID(course) == 0) {
						int j;
						for(j = i-1; j >= 0; j--) {
							if(getParentID(courses.get(j))==0) {
								if(j == i-1)
									break;
								courses.add(j+1, courses.remove(i));
								continue more;
							}
						}
						if(j == -1) {
							courses.add(0, courses.remove(i));
							continue more;
						}
					} else {
						int pid = getParentID(course); int j;
						for(j = i-1; j>=0; j--) {
							if(getParentID(courses.get(j))==pid || getID(courses.get(j)) == pid) {
								if(j == i-1) break;
								courses.add(j+1, courses.remove(i));
								continue more;
							}
						}
						if(j == -1) {
							again = true;
						}
					}
				}
			} while(again);
		}
		
	};

	
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
		initProfile();
	}

	private void initProfile() {
		AsyncCallback<Map<String,Object>> getProfileCallback = new AsyncCallback<Map<String,Object>>() {

			@Override
			public void onFailure(Throwable caught) {				
			}

			@Override
			public void onSuccess(Map<String, Object> result) {
				SelectModuleItem r = SelectModuleItem.ROOT;
				r.setName(result.get("dwoProfileDescription").toString());
				r.setDescription(result.get("dwoProfileText").toString());
				
			}};
		clientfactory.getRPCHandler().getDwoProfile(getProfileCallback );
		
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
		Logger.getLogger("DWOplayer").log(Level.INFO,log);
	}

	public static void gotoCourses() {
		SelectModuleItemHolder.clear(); // hier leegmaken of elders?
		count = 1;
		AsyncCallback<List<Map<String, Object>>> callback = GETCOURSES_CALLBACK;
		if(profiledata == null)
			clientfactory.getEntryView().setApi(api = new SCORM_guest());
		else
		{	int userID = ((Integer) profiledata.get("userID")).intValue();
			clientfactory.getEntryView().setApi(api = new SCORM_DWOmAccess(userID));
			if(!"".equals(profiledata.get("classID")))
			{
				boolean iconizer = Boolean.TRUE.equals(profiledata.get("iconizer"));
				if(iconizer)
					callback = GETCOURSES_CALLBACK_CLASS_TREE;
				else
					callback = GETCOURSES_CALLBACK_CLASS_FLAT;
				clientfactory.getRPCHandler().getCoursesClass(profiledata, callback);
				return;
			}
			if(!"".equals(profiledata.get("schoolID")))
			{
				count = 2;
				clientfactory.getRPCHandler().getCoursesSchool(profiledata, callback);
			}
		
		}
		
		
		clientfactory.getRPCHandler().getCourses(profiledata, callback);
		
	}

	/**
	 * @param result
	 */
	public static void insertFlat(List<Map<String, Object>> result) {
		for (Iterator<Map<String, Object>> iterator = result.iterator(); iterator.hasNext();) {
			Map<String, Object> map = iterator.next();
			SelectModuleItem item = new SelectModuleItem(map, SelectModuleItem.Type.MODULE);
			SelectModuleItemHolder.insert(item);
		}
	}
	public static void insertTree(List<Map<String,Object>> result) {
		for (Iterator<Map<String, Object>> iterator = result.iterator(); iterator.hasNext();) {
			Map<String, Object> map = iterator.next();
			SelectModuleItem item = new SelectModuleItem(map, SelectModuleItem.Type.MODULE);
			Integer parentID = (Integer) map.get("parentID");
			if(parentID != null && parentID.intValue()> 0 )
			{
				SelectModuleItem parent = SelectModuleItemHolder.getItemByID(parentID);
				if( parent != null)
				{
					List<SelectModuleItem> children = parent.getChildren();
					if(children == null)
						parent.setChildren(children = new ArrayList<SelectModuleItem>());
					children.add(item);
					item.setParent(parent);
				} 
			} 
			SelectModuleItemHolder.insert(item);
			
		}
	}
}
