package nl.uu.fi.dwo.mobile;

import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleViewImpl;

import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.MGWTSettings.ViewPort;
import com.googlecode.mgwt.ui.client.MGWTSettings.ViewPort.DENSITY;

public class NoordhoffPlayer extends WiskOpdrPlayer {

	protected void MGWTsetup()
	{
		ViewPort viewport = new MGWTSettings.ViewPort();
		viewport.setTargetDensity(DENSITY.MEDIUM);
		viewport.setUserScaleAble(false).setMinimumScale(1.0).setMaximumScale(1.0);
		MGWTSettings settings = new MGWTSettings();
		settings.setViewPort(viewport);
		settings.setAddGlosToIcon(true);
		settings.setFullscreen(false);
		settings.setPreventScrolling(true);
		MGWT.applySettings(settings);
	}
	
	@Override
	protected void zetMaat() {
		view.zetMaatNoordhoff();
	}

	@Override
	protected ViewModuleViewImpl createEntryVlew() {
		return new ViewModuleViewImpl(true).initialize();
	}
	
}
