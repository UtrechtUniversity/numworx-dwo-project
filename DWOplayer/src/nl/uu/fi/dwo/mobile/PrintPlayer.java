package nl.uu.fi.dwo.mobile;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.web.bindery.event.shared.EventBus;
//import com.googlecode.mgwt.ui.client.MGWT;
//import com.googlecode.mgwt.ui.client.MGWTSettings;
//import com.googlecode.mgwt.ui.client.MGWTSettings.ViewPort;

import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.interaction.client.keyboard.FocusOnTouch;
import nl.uu.fi.dwo.mobile.client.DWOplayerParameters;
import nl.uu.fi.dwo.mobile.client.dagger.DaggerPrintPlayerComponent;
import nl.uu.fi.dwo.mobile.client.dagger.PrintPlayerComponent;
import nl.uu.fi.dwo.mobile.client.sco.Memento;
import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;
import nl.uu.fi.dwo.mobile.client.ui.OpdrNav;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderPrint;
import nl.uu.fi.dwo.mobile.client.ui.views.PrintSeparator;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleViewImpl;


/**
 * FIXME Deze heeft een referentie aan com.googlecode.mgwt.ui.client.widget.main 
 * die moet worden weggewerkt!
 * daar komt ook de .landscape vandaan!@!!!
 */
public class PrintPlayer implements EntryPoint, ValueChangeHandler<String>, ClosingHandler {

	public PrintPlayer() {
		super();
        FocusOnTouch.AREA = false;
	}

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
	private static Logger logger = Logger.getLogger("PrintPlayer");

	@Inject protected ViewModuleViewImpl view;
	@Inject protected EventBus bus;
	private String PREFIX;
	private PrintPlayerComponent component;
	@Inject void setParameters(DWOplayerParameters p) {
	  this.PREFIX = p.getLaunchData();
	}

	private static native String getBase() /*-{
		return $wnd.deploy;
	}-*/;

	private static Element getHead() {
		return Document.get().getElementsByTagName("head").getItem(0);
	}
			
	@Override
	public void onModuleLoad() {
		setupConsole();  // neem console op
	    String  build = "Version " + BUILD.version + "." + BUILD.buildNumber;
	    logger.severe(build);
		
		Promise<String> inject = inject();
		Promise<Void> v = inject.then(p -> {

			MGWTsetup();

			DWOplayer.DWO_BUNDLE.dwoplayercss().ensureInjected();

			zetMaat();

			Scorm2004IF api = view.getApi();
			HeaderPrint header = new HeaderPrint(view.activity.memento());
			RootPanel.get().add(view);
			PromiseCallback<Void> init = new PromiseCallback<>();
			api.Initialize(init); // need some extra async bootstrapping.
			return init.getPromise();
		});
		v = v.then(result -> { 
			HeaderPrint header = new HeaderPrint(view.activity.memento());
			RootPanel.get().add(header);
			RootPanel.get().add(view);
			
			return result;
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
				target = target.substring(0, dot);
			}
			ValueChangeEvent<String> event = new InitialValueChangeEvent(target);
			onValueChange(event);
			return null;
		}, fail -> {
			logger.log(Level.SEVERE, "api.initialize()", fail.getFailure());
		});

}



  protected Promise<String> inject() {

    Scorm2004IF api = GWT.create(Scorm2004IF.class);
    return api.Initialize()
        .recover(p-> { 
        	logger.log(Level.SEVERE, "initialize api", p.getFailure());
        	return "false"; })        
        .then( p -> {
        	logger.log(Level.INFO, "initialize api " + p.getValue() + ".");
        	String abo_type = api.GetValue("dme.abo_type");
        	logger.log(Level.INFO, "aboType =" + abo_type);
		PrintPlayerComponent build = DaggerPrintPlayerComponent.builder()
				.api(api)
				.premium("premium".equals(abo_type))
				.build();
		this.component = build;
		build.inject(this);		
		
		return p;
    });
  }

	protected void zetMaat() {
		view.zetMaat();
	}
/** 
 * Helemaal uitzetten. Deze setup heeft zijeffecten. We doen alles default.
 */
	protected void MGWTsetup() {
//		//MGWT Settings//
//		ViewPort viewport = new MGWTSettings.ViewPort();
//		//viewport.setTargetDensity(DENSITY.MEDIUM);
//		//viewport.setUserScaleAble(false).setMinimumScale(1.0).setMaximumScale(1.0);
//		MGWTSettings settings = new MGWTSettings();
//		settings.setViewPort(viewport);
//		//settings.setAddGlosToIcon(true);
//		settings.setFullscreen(true);
//		settings.setPreventScrolling(false);
//		MGWT.applySettings(settings);
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		if (!(event instanceof InitialValueChangeEvent))
			view.close();
		String value = event.getValue();
		String target = PREFIX + value;
		logger.info(value);
		if(value.startsWith(LAUNCH_DATA))
			setupLaunchData(value);
		else
		{
			view.setUnitId(value);
			view.setupModule(value, target).then(this::checkPremium, this::failure)
			.then(this::nextpages)
			.onResolve(() -> 
			debug("FinishedSetupModule")
			);
		}
	}

	/** check result of needspremium: 
	 *  
	 * @param p resolves to true if premium is needed
	 * @return p
	 */
	private Promise<Boolean> checkPremium(Promise<Boolean> p) {
		if (p.getValue().booleanValue()) {
			view.asWidget().removeFromParent();
			RootPanel.get().add(new Label("Error: need a Premium subscription"));
			view.getApi().Terminate();
		}
		return p;
	}
	
	private Promise<Boolean> nextpages(Promise<Boolean> p) {
		Memento m = component.activity().memento();
		HashMap<String, Object> launchdata = view.launchData;
		OpdrNav on = view.getOpdrNav();
		int max = on.getAantalOpdrachten();
		int cur = m.getCurrentOpdracht()+1;
		while (cur < max) {
			PrintSeparator ps = new PrintSeparator(cur);
			RootPanel.get().add(ps);
			m.setCurrentOpdracht(cur);
			ViewModuleViewImpl other = (ViewModuleViewImpl) component.view();
			RootPanel.get().add(other);
			other.setupView(launchdata);
			cur++;
		}
		PrintSeparator ps = new PrintSeparator(cur);
		RootPanel.get().add(ps);		
	    RootLayoutPanel.get().getElement().getStyle().setDisplay(Display.NONE); // No rootlayoutpanel here.
		return p;
	}
	
	private void failure(Promise<?>p) {
		logger.log(Level.SEVERE, "failure to start", p.getFailure());
		view.getApi().Terminate();
		view.asWidget().removeFromParent();
		RootPanel.get().add(new Label("Error: failure to start " + p.getFailure()));
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
				String target = PREFIX + value;
				view.setupModule(value, target).then(this::checkPremium, this::failure).then(this::nextpages);
			}
		}
		else
		{	final String scoid = value;
			OpdrNav.defer(
			  new Command() {
				public void execute() {
					try {
						checkPremium( Promises.resolved( view.setupView(launchData)) ).then(PrintPlayer.this::nextpages);
					} catch (Throwable e) {
						failure( Promises.failed(e));
					}
				}
			  }
			);
		}
	}


  @Override
  public void onWindowClosing(ClosingEvent event) {
    view.abort();    
  }
	
	
}
