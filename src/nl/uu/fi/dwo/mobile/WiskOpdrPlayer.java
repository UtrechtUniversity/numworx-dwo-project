package nl.uu.fi.dwo.mobile;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Provider;

import org.osgi.util.promise.Promise;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.MGWTSettings.ViewPort;
import com.googlecode.mgwt.ui.client.MGWTSettings.ViewPort.DENSITY;

import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.mobile.client.DWOplayerParameters;
import nl.uu.fi.dwo.mobile.client.dagger.DaggerWiskOpdrComponent;
import nl.uu.fi.dwo.mobile.client.dagger.ModuleViewModule;
import nl.uu.fi.dwo.mobile.client.sco.Memento;
import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;
import nl.uu.fi.dwo.mobile.client.sco.WiskOpdrMemento;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.OpdrNav;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleViewImpl;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.CheckButton;
import nl.uu.fi.dwo.mobile.utils.Logging;

public class WiskOpdrPlayer implements EntryPoint, ValueChangeHandler<String>, CBookEventListener, ClosingHandler {

	static class InitialValueChangeEvent extends ValueChangeEvent<String> {

		InitialValueChangeEvent(String value) {
			super(value);
		}

	}
	
	private native void setupConsole() /*-{
		window.console = $wnd.console;
	}-*/;
	
	private native void debug(String message) /*-{
		$wnd.console.debug(message)
	}-*/;
	
	private static final String LAUNCH_DATA = "cmi.launch_data";
	private static Logger logger = Logger.getLogger("WiskOpdrPlayer");

	@Inject protected ViewModuleViewImpl view;
	@Inject void setClientFactory(ClientFactory f) {
	  DWOplayer.clientfactory = f;
	}
	@Inject void setParameters(DWOplayerParameters p) {
	  DWOplayer.PARAMETERS = p;
	  DWOplayer.PREFIX = p.getLaunchData();
	}

	public static Provider<Logging> loggingProvider;
	
	@Inject void setLogging(Provider<Logging> provider) {
	  loggingProvider = provider;
	}
	
	
	@Override
	public void onModuleLoad() {
		setupConsole();  // neem console op
		
	    String  build = "Version " + BUILD.version + "." + BUILD.buildNumber;
	    logger.severe(build);
		
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

		Promise<String> inject = inject();
		Promise<Void> v = inject.then(p -> {

			DWOplayer.clientfactory.getEventBus().addHandler(CBookEvent.TYPE, this);
			MGWTsetup();

			DWOplayer.DWO_BUNDLE.dwoplayercss().ensureInjected();

			zetMaat();

			Scorm2004IF api = view.getApi();
			RootLayoutPanel.get().add(view);
			PromiseCallback<Void> init = new PromiseCallback<>();
			api.Initialize(init); // need some extra async bootstrapping.
			return init.getPromise();
		});
		v.then(result ->
		{
			Scorm2004IF api = view.getApi();
			String target = Window.Location.getHash();
			if (target.startsWith("#"))
				target = target.substring(1);
			int dot = target.lastIndexOf('.');
			int colon = target.lastIndexOf(':');
			if (dot > 0 && dot > colon) {
				String location = target.substring(dot + 1);
				target = target.substring(0, dot);
				api.SetValue(Memento.LOCATION, location);
			}
			ValueChangeEvent<String> event = new InitialValueChangeEvent(target);
			onValueChange(event);
			return null;
		}, fail -> {
			logger.log(Level.SEVERE, "api.initialize()", fail.getFailure());
		});

}



  protected Promise<String> inject() {
//    DummyClientFactory dummyClientFactory = new DummyClientFactory();
//    DWOplayer.clientfactory = dummyClientFactory;
//    view = createEntryView(false);
//    dummyClientFactory.setEntryView(view);

    Scorm2004IF api = GWT.create(Scorm2004IF.class);
    return api.Initialize()
        .recover(p-> { 
        	logger.log(Level.SEVERE, "initialize api", p.getFailure());
        	return "false"; })        
        .then( p -> {
        	logger.log(Level.INFO, "initialize api " + p.getValue() + ".");
        	String abo_type = api.GetValue("dme.abo_type");
        	logger.log(Level.INFO, "aboType =" + abo_type);
		DaggerWiskOpdrComponent.builder()
			.api(api)
			.premium("premium".equals(abo_type))
			.moduleView(new ModuleViewModuleImpl())
			.build()
			.inject(this);
		return p;
    });
  }

	/**
	 * Factory method pattern. Initialize clientFactory
	 * @return
	 */
	
	class ModuleViewModuleImpl extends ModuleViewModule {
	    boolean header = false;

	  ModuleViewModuleImpl() {
      }


      ModuleViewModuleImpl(boolean header) {
        this.header = header;
      }


	@Override
    protected ViewModuleViewImpl getViewModuleView(Scorm2004IF api, ClientFactory clientFactory) {
		logger.info("getViewModuleView " + clientFactory.isPremium());
// FIXME Smelly code. clientfactory injected early.
		DWOplayer.clientfactory = clientFactory;
		return createEntryView(header, api);
    }
	  
	}
		
	protected final ViewModuleViewImpl createEntryView(boolean header, Scorm2004IF api) {
		return new ViewModuleViewImpl(header, api) {
			@Override
			protected Memento createMemento() {
				return new WiskOpdrMemento(getApi(), this, studentModel); // terminate at close, no "almost" close
			} } 
		.initialize();
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
			DWOplayer.insertCSS(value);
			view.setupModule(value, target).onResolve(() -> 
			debug("FinishedSetupModule")
			);
		}
	}

	protected void setupLaunchData(String value) {
		Scorm2004IF api = view.getApi();
		
		int k = value.indexOf(':');
		if(k > 0) {
			value = value.substring(k+1);
			view.setUnitId(value);
		} else 
			view.setUnitId("scoViewNr");
		final String launchData = api.GetValue(LAUNCH_DATA);
		if(launchData == null || launchData.isEmpty() )
		{
			logger.severe("launchdata empty + " + launchData);
			if(k > 0) {
				String target = DWOplayer.PREFIX + value;
				DWOplayer.insertCSS(value);
				view.setupModule(value, target);
			} else
				setupOldView();
		}
		else
		{
			OpdrNav.defer(
			  new Command() {
				public void execute() {
					view.setupView(launchData);
				}
			  }
			);
		}
	}

	protected void setupOldView() {
		String url = "index.xml";
//		String link = "index.xmr"; // reference.
		String path = Window.Location.getPath();
		while( path.startsWith("//")) // XXX Noordhoff path begint met 1 slash teveel
			path = path.substring(1);
		// strip basename
		//int slash = path.lastIndexOf('/');
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
//			link = path + ".xmr";
		}
		view.setUnitId("scoViewNr");
		view.preSetupModule(url);
	}

  public static String ACTION_NEXT_ASSET = "actionNextAsset";
  @Override
  public void acceptCBookEvent(CBookEvent event) {
    if (CheckButton.ACTION_NEXT_PAGE.equals(event.getCommand()))
    {
        if (! view.nextPageAction())
          DWOplayer.clientfactory.getEventBus().fireEvent(new CBookEvent(ACTION_NEXT_ASSET));
    }
    
  }


  @Override
  public void onWindowClosing(ClosingEvent event) {
    view.abort();    
  }
	
	
}
