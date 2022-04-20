package nl.uu.fi.dwo.mobile;

import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.mobile.client.DWOplayerClientBundle;
import nl.uu.fi.dwo.mobile.client.DWOplayerParameters;
import nl.uu.fi.dwo.mobile.client.template.TemplateBasicConstants;
import nl.uu.fi.dwo.mobile.client.template.TemplateConstants;
import nl.uu.fi.dwo.mobile.client.template.TemplateCss;
import nl.uu.fi.dwo.mobile.client.template.TemplateNumworxConstants;
import nl.uu.fi.dwo.mobile.client.template.TemplateUUTestConstants;
import nl.uu.fi.dwo.mobile.client.ui.TabletActivityMapper;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderView;
import nl.uu.fi.dwo.mobile.client.ui.views.NavigationView;

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
import com.google.web.bindery.event.shared.EventBus;
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
	public static final boolean RESPONSIVE =  true || "true".equals(Window.Location.getParameter("responsive"));

	public static int PROFILE_ID = 77;
	
	public static final DWOplayerClientBundle DWO_BUNDLE = GWT.create(DWOplayerClientBundle.class);
	
	private static TemplateCss templateCss;
	public static TemplateConstants templateConstants = new TemplateBasicConstants(); // Never null
	
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
		setDwoProfileID();
	}
		
	public static Text_nl rb = new Text_nl();

	/**
	 * This is the entry point method.
	 */
	public void start()
	{
		setupResources();
		setupDWOPlayer();
		initProfile();
	}

	abstract void initProfile();
	
	//Sets up the GWT and MGWT settings needed to run the application
	public void setupDWOPlayer()
	{
		//MGWT Settings//
		ViewPort viewport = new MGWTSettings.ViewPort();
		viewport.setTargetDensity(DENSITY.MEDIUM);
		viewport.setUserScaleAble(false).setMinimumScale(1.0).setMaximumScale(1.0);
		if(RESPONSIVE)
			viewport.setWidthToDeviceWidth();
		else
			viewport.setWidth(1024);
		MGWTSettings settings = new MGWTSettings();
		settings.setViewPort(viewport);
		settings.setAddGlosToIcon(true);
		settings.setFullscreen(true);
		settings.setPreventScrolling(true);
		MGWT.applySettings(settings);

		createClientFactory();
	}

	
	protected void start(PlaceHistoryHandler h) {
		h.handleCurrentHistory();
	}
	
	
	protected abstract void createClientFactory();

	void createTabletDisplay(TabletActivityMapper appActivityMapper, 
			DWOplayerParameters params, NavigationView navigation, HeaderView header, EventBus bus)
	{
		SimplePanel display = new SimpleLayoutPanel();
		ActivityManager activityMapper = new ActivityManager(appActivityMapper, bus);
		activityMapper.setDisplay(display);
		display.asWidget().addStyleName("RootPanel");
		RootLayoutPanel.get().add(header);
		RootLayoutPanel.get().add(navigation);
		RootLayoutPanel.get().add(display);
		navigation.setDisplay(display);
		header.setDisplay(display,navigation);
		header.hide();
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
		{ 	DWO_BUNDLE.templatebasiccss().ensureInjected();
			return DWO_BUNDLE.templatebasiccss();
		}
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
		templateCss.ensureInjected();
	}
	
	private void setupResources()
	{
//		resources.put("SelectionColor", "#88f");
		DWO_BUNDLE.dwoplayercss().ensureInjected();
//		DWO_BUNDLE.templatenumworxcss().ensureInjected();
//		DWO_BUNDLE.templatebasiccss().ensureInjected();
//		DWO_BUNDLE.templateuutestcss().ensureInjected();
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


	public static long timezone = 0L;
	
	
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
