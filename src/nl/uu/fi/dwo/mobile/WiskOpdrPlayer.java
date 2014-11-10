package nl.uu.fi.dwo.mobile;

import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;
import nl.uu.fi.dwo.mobile.client.ui.DummyClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.OpdrNav;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleViewImpl;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.logging.client.HasWidgetsLogHandler;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.MGWTSettings.ViewPort;
import com.googlecode.mgwt.ui.client.MGWTSettings.ViewPort.DENSITY;

public class WiskOpdrPlayer implements EntryPoint, ValueChangeHandler<String> {

	static class InitialValueChangeEvent extends ValueChangeEvent<String> {

		InitialValueChangeEvent(String value) {
			super(value);
		}

	}
	private static final String LAUNCH_DATA = "cmi.launch_data";
	private static Logger logger = Logger.getLogger("WiskOpdrPlayer");
	protected ViewModuleViewImpl view;

	@Override
	public void onModuleLoad() {
//		VerticalPanel customLogArea = null;
//		if( LogConfiguration.loggingIsEnabled())	
//		{
//			customLogArea = new VerticalPanel();
//			Logger.getLogger("").addHandler(new HasWidgetsLogHandler(customLogArea));
//			logger.info("start logging");
//		}
//		if( LogConfiguration.loggingIsEnabled())	
//		{
//			RootPanel.get().add(customLogArea);
//		}

		DummyClientFactory dummyClientFactory = new DummyClientFactory();
		DWOplayer.clientfactory = dummyClientFactory;
		
		MGWTsetup();
		view = createEntryVlew();

		dummyClientFactory.setEntryView(view);

		zetMaat();
		
		Scorm2004IF api = view.getApi();
		RootPanel.get().add(view);
		
		History.addValueChangeHandler(this);
		AsyncCallback<Void> callback = new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				logger.log(Level.SEVERE, "api.initialize()", caught);
			}

			@Override
			public void onSuccess(Void result) {
				String target = History.getToken();		
				ValueChangeEvent<String> event = new InitialValueChangeEvent(target);
				onValueChange(event);
			}
			
		};
		api.Initialize(callback); // need some async bootstrapping.	
		
	}

	/**
	 * Factory method pattern. Initialize clientFactory
	 * @return
	 */
	protected ViewModuleViewImpl createEntryVlew() {
		return new ViewModuleViewImpl(false).initialize();
	}

	protected void zetMaat() {
		view.zetMaat();
	}

	protected void MGWTsetup() {
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
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		if (!(event instanceof InitialValueChangeEvent))
			view.close();
		String value = event.getValue();
		String target = DWOplayer.PREFIX + value;
		logger.info(value);
		if(value.startsWith(LAUNCH_DATA))
			setupLaunchData(value);
		else
		if(DWOplayer.PREFIX == null || value == null || value.equals(""))
			setupOldView();
		else
		{
			view.setUnitId(value);
			view.setupModule(value, target);
		}
	}

	protected void setupLaunchData(String value) {
		Scorm2004IF api = view.getApi();
		api.Initialize();
		int k = value.indexOf(':');
		if(k > 0) {
			value = value.substring(k+1);
			view.setUnitId(value);
		} else 
			view.setUnitId("scoViewNr");
		String launchData = api.GetValue(LAUNCH_DATA);
		if(launchData == null || launchData.isEmpty() )
		{
			if(k > 0) {
				String target = DWOplayer.PREFIX + value;
				view.setupModule(value, target);
			} else
				setupOldView();
		}
		else
		{
			view.setupView(launchData);
		}
	}

	protected void setupOldView() {
		String url = "index.xml";
		String link = "index.xmr"; // reference.
		String path = Window.Location.getPath();
		while( path.startsWith("//")) // XXX Noordhoff path begint met 1 slash teveel
			path = path.substring(1);
		// strip basename
		int slash = path.lastIndexOf('/');
		//if (slash >= 0)
		//	path = path.substring(slash + 1);
		// strip extension
		int dot = path.lastIndexOf('.');
		if (dot > 0)
		{
			path = path.substring(0, dot);
		}
		if (!path.isEmpty())
		{
			url = path + ".xml";
			link = path + ".xmr";
		}
		view.setUnitId("scoViewNr");
		view.preSetupModule(link, url);
	}
	
}
