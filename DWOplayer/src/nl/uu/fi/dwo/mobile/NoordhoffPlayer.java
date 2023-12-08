package nl.uu.fi.dwo.mobile;

import org.osgi.util.promise.Promise;

import com.google.gwt.core.client.GWT;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.MGWTSettings.ViewPort;
import nl.uu.fi.dwo.mobile.client.dagger.DaggerWiskOpdrComponent;
import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;

public class NoordhoffPlayer extends WiskOpdrPlayer {

	protected void MGWTsetup()
	{
		ViewPort viewport = new MGWTSettings.ViewPort();
		//viewport.setTargetDensity(DENSITY.MEDIUM);
		viewport.setUserScaleAble(false).setMinimumScale(1.0).setMaximumScale(1.0);
		MGWTSettings settings = new MGWTSettings();
		settings.setViewPort(viewport);
		//settings.setAddGlosToIcon(true);
		settings.setFullscreen(false);
		settings.setPreventScrolling(true);
		MGWT.applySettings(settings);
	}
	
	@Override
	protected void zetMaat() {
		view.zetMaatNoordhoff();
	}

	  protected Promise<String> inject() {
	    Scorm2004IF api = GWT.create(Scorm2004IF.class);
	    return api.Initialize().then( p -> {
			DaggerWiskOpdrComponent.builder().api(api).moduleView(new ModuleViewModuleImpl(true)).premium(true).build().inject(this);
			return p;
	    });
	  }

	
}
