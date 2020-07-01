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
import nl.uu.fi.dwo.mobile.client.template.TemplateBasicConstants;
import nl.uu.fi.dwo.mobile.client.template.TemplateConstants;
import nl.uu.fi.dwo.mobile.client.template.TemplateCss;
import nl.uu.fi.dwo.mobile.client.template.TemplateNumworxConstants;
import nl.uu.fi.dwo.mobile.client.template.TemplateUUTestConstants;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.TabletActivityMapper;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
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
public abstract class DWOplayer
{
	public static final boolean JSON = true;
	public static int PROFILE_ID = 77;
	
	public static final DWOplayerClientBundle DWO_BUNDLE = GWT.create(DWOplayerClientBundle.class);
	public static final DWOplayerParameters PARAMETERS = GWT.create(DWOplayerParameters.class);
	private static DWOplayer instance;
	
	private static TemplateCss templateCss;
	public static TemplateConstants templateConstants;
	
	private static final String templateBasisName = "TemplateBasis";
	private static final String templateNumworxName = "TemplateNumworx";
	private static final String templateUUTestName = "TemplateUUTest";
	private static String templateName = templateBasisName;
	
	
	private static native int getDwoProfileID() /*-{
		return $wnd.DWO_PROFILE_ID
	}-*/;
	
	void setDwoProfileID() {
		try {
			int n = getDwoProfileID();
			log("Profile = " + n);
			if(n > 0) 
				PROFILE_ID = n;
		} catch( Throwable _oops_) {}
	}
	
	
	DWOplayer() {
		super();
		instance = this;
		setDwoProfileID();
	}

	public static final String PREFIX = PARAMETERS.getLaunchData();
	
//	public static int count;
	
//	static void goTree() {
//		if(--count <= 0)
//			clientfactory.getPlaceController().goTo(new TreeModulePlace("0"));		
//	}
//
//	static void goFlat() {
//		if(--count <= 0)
//			clientfactory.getPlaceController().goTo(new FlatModulePlace());		
//	}
	
	private static HashMap<String, String> resources = new HashMap<String, String>();


	public static Text_nl rb = new Text_nl();
	
	@Deprecated
	public static boolean withUser() {
		return clientfactory.withUser();
	}	
	
	@Deprecated
	private static Deferred<DomDwoProfileFull> deferredProfile;
	@Deprecated
	public static Promise<DomDwoProfileFull> dwoProfile; // NEVER NULL
	public static ClientFactory clientfactory;

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

	void initProfile() {
		Success<DomDwoProfileFull, DomDwoProfileFull> getProfileCallback = new Success<DomDwoProfileFull, DomDwoProfileFull>() {

			@Override
			public Promise<DomDwoProfileFull> call(Promise<DomDwoProfileFull> promise)
					throws Exception {
				SelectModuleItem r = SelectModuleItem.ROOT;
				DomDwoProfileFull p = promise.getValue();
				r.setName(p.getDwoProfileDescription());
				r.setDescription(p.getDwoProfileText());
				return promise;
			}};
		dwoProfile = dwoProfile.then(getProfileCallback);
		deferredProfile.resolveWith(clientfactory.getRPCHandler().getDwoProfile());
		
	}

	//Sets up the GWT and MGWT settings needed to run the application
	public void setupDWOPlayer()
	{
		//MGWT Settings//
		ViewPort viewport = new MGWTSettings.ViewPort();
		viewport.setTargetDensity(DENSITY.MEDIUM);
		viewport.setUserScaleAble(false).setMinimumScale(1.0).setMaximumScale(1.0);
		if("true".equals(Window.Location.getParameter("responsive")))
			viewport.setWidthToDeviceWidth();
		else
			viewport.setWidth(1024);
		MGWTSettings settings = new MGWTSettings();
		settings.setViewPort(viewport);
		settings.setAddGlosToIcon(true);
		settings.setFullscreen(true);
		settings.setPreventScrolling(true);
		MGWT.applySettings(settings);

		//GWT Settings//
		clientfactory = createClientFactory();
		final PlaceHistoryHandler historyHandler = clientfactory.getPlaceHistoryHandler();

		historyHandler.handleCurrentHistory();
	}

	protected abstract ClientFactory createClientFactory();

	void createTabletDisplay(ClientFactory clientfactory, TabletActivityMapper appActivityMapper)
	{
//		AnimatableDisplay display = GWT.create(AnimatableDisplay.class);
//		TabletActivityMapper appActivityMapper = new TabletActivityMapper(clientfactory);
//		TabletAnimationMapper animationMapper = new TabletAnimationMapper();
//		AnimatingActivityManager activityMapper = new AnimatingActivityManager(appActivityMapper, animationMapper, clientfactory.getEventBus());
//		activityMapper.setDisplay(display);
//		
//		display.asWidget().addStyleName("RootPanel");
		
		SimplePanel display = new SimpleLayoutPanel();
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
	
	public static TemplateCss templateCss() {
		if(templateCss!=null)
			return templateCss;
		else 
			return DWO_BUNDLE.templatebasiccss();
	}
	
	public static void setTemplateCss(String name) {
		if(templateNumworxName.equals(name)) {
			templateCss = DWO_BUNDLE.templatenumworxcss();
			templateConstants = new TemplateNumworxConstants();
		}
		else if(templateUUTestName.equals(name)) {
			templateCss = DWO_BUNDLE.templateuutestcss();
			templateConstants = new TemplateUUTestConstants();
		}
		else {
			templateCss = DWO_BUNDLE.templatebasiccss();
			templateConstants = new TemplateBasicConstants();
		}
	}
	
	private void setupResources()
	{
//		resources.put("SelectionColor", "#88f");
		DWO_BUNDLE.dwoplayercss().ensureInjected();
		DWO_BUNDLE.templatenumworxcss().ensureInjected();
		DWO_BUNDLE.templatebasiccss().ensureInjected();
		DWO_BUNDLE.templateuutestcss().ensureInjected();
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
	private static void insertFlat(List<Map<String, Object>> result) {
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

	public static boolean isPremium() {
		try { 
			return clientfactory.isPremium();
		} catch (Throwable t){
			Logger.getLogger("DWOplayer").log(Level.INFO,"isPremium", t);
			return false;
		}
	}

}
