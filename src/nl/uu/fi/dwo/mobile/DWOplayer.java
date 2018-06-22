package nl.uu.fi.dwo.mobile;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

import nl.uu.fi.dwo.mobile.client.DWOplayerClientBundle;
import nl.uu.fi.dwo.mobile.client.DWOplayerParameters;
import nl.uu.fi.dwo.mobile.client.sco.SCORM_DWOmAccess;
import nl.uu.fi.dwo.mobile.client.sco.SCORM_guest;
import nl.uu.fi.dwo.mobile.client.ui.AppPlaceHistoryMapper;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactoryImpl;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.TabletActivityMapper;
import nl.uu.fi.dwo.mobile.client.ui.places.FlatModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.dom.client.Text;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.MGWTSettings.ViewPort;
import com.googlecode.mgwt.ui.client.MGWTSettings.ViewPort.DENSITY;

import fi.dwo.gwt.lib.rest.DwoConstants;
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
	public static int PROFILE_ID = 77;
	
	public static final DWOplayerClientBundle DWO_BUNDLE = GWT.create(DWOplayerClientBundle.class);
	public static final DWOplayerParameters PARAMETERS = GWT.create(DWOplayerParameters.class);
	private static DWOplayer instance;
	
	private static native int getDwoProfileID() /*-{
		return $wnd.DWO_PROFILE_ID
	}-*/;
	
	void setDwoProfileID() {
		try {
			int n = getDwoProfileID();
			log("Profile = " + n);
			if(n > 0) 
				PROFILE_ID = n;
		} catch( Throwable _) {}
	}
	
	
	public DWOplayer() {
		super();
		instance = this;
		setDwoProfileID();
	}

	public static final String PREFIX = PARAMETERS.getLaunchData();
	
	Place defaultPlace = new LoginPlace(); // new SelectModulePlace("select");

	public static int count;
	
	static void goTree() {
		if(--count <= 0)
			clientfactory.getPlaceController().goTo(new TreeModulePlace("0"));		
	}

	static void goFlat() {
		if(--count <= 0)
			clientfactory.getPlaceController().goTo(new FlatModulePlace());		
	}
	
//	static final AsyncCallback<List<Map<String,Object>>> GETCOURSES_CALLBACK_CLASS_FLAT = new AsyncCallback<List<Map<String,Object>>>(){
//
//		@Override
//		public void onFailure(Throwable caught) {
//			Window.alert(caught.toString());
//			goFlat();
//			
//		}
//
//		@Override
//		public void onSuccess(List<Map<String, Object>> result) {
//			insertFlat(result);
//			goFlat();
//		}
//		
//	};

//	static final AsyncCallback<List<Map<String,Object>>> GETCOURSES_CALLBACK = 
//			new AsyncCallback<List<Map<String,Object>>>() {
//		@Override
//		public void onFailure(Throwable caught) {
//			Window.alert(caught.toString());
//			goTree();
//		}	
//		@Override
//		public void onSuccess(List<Map<String,Object>> result) {	
//			insertFlat(result);
//			goTree();
//		}		
//	};	
	//private Place defaultPlace = new SelectModulePlace("Home");
	private static HashMap<String, String> resources = new HashMap<String, String>();

//	private static final AsyncCallback<List<Map<String,Object>>>
//	GETCOURSES_CALLBACK_CLASS_TREE = new AsyncCallback<List<Map<String,Object>>>(){
//
//		@Override
//		public void onFailure(Throwable caught) {
//			Logger.getLogger("DWOplayer").log(Level.SEVERE, caught.toString(), caught);
//			Window.alert(caught.toString());
//			goTree();
//		}
//
//		@Override
//		public void onSuccess(List<Map<String, Object>> result) {
//			sort(result);
//			insertTree(result);
//			goTree();
//		}
//		
//		private int getParentID(Map<String,Object> course) {
//			try {
//				return ((Number) course.get("parentID")).intValue();
//			} catch (Exception e) {
//				return 0;
//			}
//		}
//		
//		private int getID(Map<String, Object> course) {
//			try {
//				return ((Number) course.get("courseID")).intValue();
//			} catch (Exception e) {
//				return 0;
//			}
//		}
//		private void sort(List<Map<String,Object>> courses) {
//			boolean again;
//			if(courses == null || courses.isEmpty())
//				return;
//			do {
//				again = false;
//				more:
//				for(int i = 0; i < courses.size(); i++ ) {
//					Map<String,Object> course = courses.get(i);
//					if( getParentID(course) == 0) {
//						int j;
//						for(j = i-1; j >= 0; j--) {
//							if(getParentID(courses.get(j))==0) {
//								if(j == i-1)
//									break;
//								courses.add(j+1, courses.remove(i));
//								continue more;
//							}
//						}
//						if(j == -1) {
//							courses.add(0, courses.remove(i));
//							continue more;
//						}
//					} else {
//						int pid = getParentID(course); int j;
//						for(j = i-1; j>=0; j--) {
//							if(getParentID(courses.get(j))==pid || getID(courses.get(j)) == pid) {
//								if(j == i-1) break;
//								courses.add(j+1, courses.remove(i));
//								continue more;
//							}
//						}
//						if(j == -1) {
//							again = true;
//						}
//					}
//				}
//			} while(again);
//		}
//		
//	};

	
	//public static Locale language = new Locale ("nl", "");
	public static Text_nl rb = new Text_nl();
	
	public static boolean withUser() {
		return clientfactory.withUser();
	}	
	
	private static Deferred<DomDwoProfileFull> deferredProfile;
	public static Promise<DomDwoProfileFull> dwoProfile; // NEVER NULL
	public static ClientFactory clientfactory;
	//public static SCORM_guest api;

	/**
	 * This is the entry point method.
	 */
	public void start()
	{
		//Logger.getLogger("DWOplayer").log(Level.WARNING, "Version " + BUILD.version + ", build " + BUILD.buildNumber);
		deferredProfile = new Deferred<DomDwoProfileFull>();
		dwoProfile = deferredProfile.getPromise();
		setupResources();
		setupDWOPlayer();
		initProfile();
	}

	protected void initProfile() {
		Success<DomDwoProfileFull, Void> getProfileCallback = new Success<DomDwoProfileFull, Void>() {

			@Override
			public Promise<Void> call(Promise<DomDwoProfileFull> promise)
					throws Exception {
				SelectModuleItem r = SelectModuleItem.ROOT;
				DomDwoProfileFull p = promise.getValue();
				r.setName(p.getDwoProfileDescription());
				r.setDescription(p.getDwoProfileText());
				return null;
			}};
		dwoProfile.then(getProfileCallback);
		deferredProfile.resolveWith(clientfactory.getRPCHandler().getDwoProfile());
		
	}

	//Sets up the GWT and MGWT settings needed to run the application
	public void setupDWOPlayer()
	{
		//MGWT Settings//
		ViewPort viewport = new MGWTSettings.ViewPort();
		viewport.setTargetDensity(DENSITY.MEDIUM);
		viewport.setUserScaleAble(false).setMinimumScale(1.0).setMaximumScale(1.0);
		viewport.setWidth(1024);
		MGWTSettings settings = new MGWTSettings();
		settings.setViewPort(viewport);
		settings.setAddGlosToIcon(true);
		settings.setFullscreen(true);
		settings.setPreventScrolling(true);
		MGWT.applySettings(settings);

		inject();
		//GWT Settings//
		clientfactory = createClientFactory();
		AppPlaceHistoryMapper historyMapper = GWT.create(AppPlaceHistoryMapper.class);
		final PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
		historyHandler.register(clientfactory.getPlaceController(), clientfactory.getEventBus(), defaultPlace);
		createTabletDisplay(clientfactory);
		historyHandler.handleCurrentHistory();
	}

	protected ClientFactory createClientFactory() {
		return null; //new ClientFactoryImpl();
	}

	private void createTabletDisplay(ClientFactory clientfactory)
	{
//		AnimatableDisplay display = GWT.create(AnimatableDisplay.class);
//		TabletActivityMapper appActivityMapper = new TabletActivityMapper(clientfactory);
//		TabletAnimationMapper animationMapper = new TabletAnimationMapper();
//		AnimatingActivityManager activityMapper = new AnimatingActivityManager(appActivityMapper, animationMapper, clientfactory.getEventBus());
//		activityMapper.setDisplay(display);
//		
//		display.asWidget().addStyleName("RootPanel");
		
		SimplePanel display = new SimpleLayoutPanel();
		TabletActivityMapper appActivityMapper = new TabletActivityMapper(clientfactory);
		ActivityManager activityMapper = new ActivityManager(appActivityMapper, clientfactory.getEventBus());
		activityMapper.setDisplay(display);
		display.asWidget().addStyleName("RootPanel");
		RootLayoutPanel.get().add(clientfactory.getHeaderView());
		RootLayoutPanel.get().add(clientfactory.getNavigationView());
		RootLayoutPanel.get().add(display);
		clientfactory.getNavigationView().setDisplay(display);
		clientfactory.getHeaderView().setDisplay(display,clientfactory.getNavigationView());
		clientfactory.getHeaderView().hide();
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

	protected void inject() {
	  //nl.uu.fi.dwo.mobile.client.dagger.DaggerPlayerComponent.create().inject(this);
	}
	
	private void setupResources()
	{
//		resources.put("SelectionColor", "#88f");
		DWO_BUNDLE.dwoplayercss().ensureInjected();
	}

//	public static String getResource(String resource)
//	{
//		return resources.get(resource);
//	}

	//Used for debugging
	public static void log(String log)
	{
		Logger.getLogger("DWOplayer").log(Level.INFO,log);
	}

	public static void gotoCourses() {
		instance.gotoCourses_impl();
	}
	
	protected void gotoCourses_impl() {
//		SelectModuleItemHolder.clear(); // hier leegmaken of elders?
//		count = 1;
//		AsyncCallback<List<Map<String, Object>>> callback = GETCOURSES_CALLBACK;
//		if(!DWOplayer.withUser())
//			api = clientfactory.setupAPI();
//		else
//		{	
//			api = clientfactory.setupAPI();
//			if(!"".equals(clientfactory.getClassID()))
//			{
//				boolean iconizer = clientfactory.isIconizer(); 
//						
//				if(iconizer)
//					callback = GETCOURSES_CALLBACK_CLASS_TREE;
//				else
//					callback = GETCOURSES_CALLBACK_CLASS_FLAT;
//				clientfactory.getRPCHandler().getCoursesClass(clientfactory.getClassID(), callback);
//				return;
//			}
//// DONE als je wel student bent, maar niet in een klas zit, krijg je ook dit te zien!!!! FIXME voor MC2
//			if(!"".equals(clientfactory.getSchoolID()) 
//					&&  RoleType.STUDENT != clientfactory.getRoleType() )
//			{
//				count = 2;
//				clientfactory.getRPCHandler().getCoursesSchool(clientfactory.getSchoolID(), callback);
//			}
//		
//		}
//
//		final AsyncCallback<List<Map<String, Object>>> callback_final = callback;
//				clientfactory.getRPCHandler().getCourses(callback_final);
//		
	}

	public static long timezone = 0L;
	
	/**
	 * @param result
	 */
	public static void insertFlat(List<Map<String, Object>> result) {
		long now = System.currentTimeMillis() + timezone;
		for (Iterator<Map<String, Object>> iterator = result.iterator(); iterator.hasNext();) {
			Map<String, Object> map = iterator.next();
			Object o = map.get("notBefore");
            if (o instanceof Date) {
                if (now < ((Date) o).getTime()) {
                    continue;
                }
            }
            o = map.get("notAfter");
            if (o instanceof Date) {
                if (now > ((Date) o).getTime()) {
                    continue;
                }
            }

            SelectModuleItem item = new SelectModuleItem(map, SelectModuleItem.Type.MODULE);
			SelectModuleItemHolder.insert(item);
		}
	}

	private static void insertTree(List<Map<String,Object>> result) {
		long now = System.currentTimeMillis() + timezone;
		for (Iterator<Map<String, Object>> iterator = result.iterator(); iterator.hasNext();) {
			Map<String, Object> map = iterator.next();
			
			Object o = map.get("notBefore");
            if (o instanceof Date) {
                if (now < ((Date) o).getTime()) {
                    continue;
                }
            }
            o = map.get("notAfter");
            if (o instanceof Date) {
                if (now > ((Date) o).getTime()) {
                    continue;
                }
            }
						
			SelectModuleItem item = new SelectModuleItem(map, SelectModuleItem.Type.MODULE);
			if (Boolean.TRUE.equals (map.get("withChildren")))
			{
				List<SelectModuleItem> children = item.getChildren();
				if(children == null)
					item.setChildren(children = new ArrayList<SelectModuleItem>());
			}
				
			Integer parentID = (Integer) map.get("parentID"); // FIXME voor MC Squared type parentID?
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
	
	public static void insertCSS(String value) {
		String href = DwoConstants.constants.server() + 
				"public/scoData/get/" + value + "/style.css";
		insertStylesheet(href);
	}

	public static void insertInlineCss(String data) {
		removeStyle();
		LinkElement link = Document.get().createLinkElement();
		link.setRel("stylesheet");
		link.setType("text/css");
		link.setInnerText(data);
		Element head = getHead();
		style = link;
		head.appendChild(link);
		
	}

	private static LinkElement style;
	static void insertStylesheet(String href) {
		removeStyle();
		LinkElement link = Document.get().createLinkElement();
		link.setRel("stylesheet");
		link.setType("text/css");
		link.setHref(href);
		Element head = getHead();
		style = link;
		head.appendChild(link);
	}

	private static Element getHead() {
		return Document.get().getElementsByTagName("head").getItem(0);
	}
	public static void removeStyle() {
		if(style != null) {
			LinkElement link = style; style = null;
			Element head = getHead();
			head.removeChild(link);
		}
	}

}
