package nl.uu.fi.dwo.mobile;

import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleViewImpl;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.logging.client.HasWidgetsLogHandler;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.MGWTSettings.ViewPort;
import com.googlecode.mgwt.ui.client.MGWTSettings.ViewPort.DENSITY;

public class TriforkPlayer implements EntryPoint {

	private static Logger logger = Logger.getLogger("TriforkPlayer");
	private ViewModuleViewImpl view;

	@Override
	public void onModuleLoad() {
		VerticalPanel customLogArea = null;
try {
		if( LogConfiguration.loggingIsEnabled())
		{	customLogArea = new VerticalPanel();
			Logger.getLogger("").addHandler(new HasWidgetsLogHandler(customLogArea));
		}
		logger.severe(getTarget());
		
		MGWTsetup();
		view = new ViewModuleViewImpl(false).initialize();
		view.zetMaatTrifork();
		//RootPanel.get().add(view);
		if( LogConfiguration.loggingIsEnabled()) {
			RootPanel.get().add(customLogArea);
		}
		Style style = view.asWidget().getElement().getStyle();
		style.setMargin(0, Unit.PX);
		style.setBackgroundColor("white");
		
		RootPanel.get().add(view);

		Scorm2004IF api = view.getApi();
		AsyncCallback<Void> callback = new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				logger.log(Level.SEVERE, "api.initialize()", caught);
			}

			@Override
			public void onSuccess(Void result) {
				String target = getTarget();	
				view.setupModule("Cito item", target);
			}
			
		};
		api.Initialize(callback); // need some async bootstrapping.	
} catch(Throwable error) {
		RootPanel.get().add(new Label(error.toString()));
		if(customLogArea != null) {
			RootPanel.get().add(customLogArea);
			logger.log(Level.SEVERE, "on module load", error);
		}
}

}

	private static native String getTarget()/*-{
		return $wnd.launchData
	}-*/;

	private void MGWTsetup() {
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


//	public void onValueChange(ValueChangeEvent<String> event) {
//		
//		String value = event.getValue();
//		String target = value;
//		logger.info(value);
//		view.setupModule(value, target);
//	}

	
}
