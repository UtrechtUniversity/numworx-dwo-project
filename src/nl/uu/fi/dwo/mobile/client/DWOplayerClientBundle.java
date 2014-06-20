package nl.uu.fi.dwo.mobile.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.googlecode.mgwt.ui.client.theme.base.ButtonCss;
import com.googlecode.mgwt.ui.client.theme.base.HeaderCss;

public interface DWOplayerClientBundle extends ClientBundle {

	@Source("nl/uu/fi/dwo/mobile/client/resources/mw_wortel.gif")
	ImageResource mv_wortel();

	@Source("nl/uu/fi/dwo/mobile/client/resources/foutkruis.gif")
	ImageResource foutkruis();

	@Source("nl/uu/fi/dwo/mobile/client/resources/pijlterug.gif")
	ImageResource pijlterug();
	
	@Source("nl/uu/fi/dwo/mobile/client/resources/pijlcopy.gif")
	ImageResource pijlcopy();

	@Source("nl/uu/fi/dwo/mobile/client/resources/appletknop.gif")
	ImageResource appletknop();

	@Source("nl/uu/fi/dwo/mobile/client/resources/crosshair.gif")
	ImageResource crosshair();

	@Source("nl/uu/fi/dwo/mobile/client/resources/klapuit1.png")
	ImageResource klapuit1();

	@Source("nl/uu/fi/dwo/mobile/client/resources/klapuit2.png")
	ImageResource klapuit2();

	@Source("nl/uu/fi/dwo/mobile/client/resources/reload.png")
	ImageResource reload();
	
	
	@Source("nl/uu/fi/dwo/mobile/client/resources/imgbutton.css")
	ImgButton imgbutton();
	
	@Source("nl/uu/fi/dwo/mobile/client/resources/header.css")
	HeaderCss headercss();
	
	@Source("nl/uu/fi/dwo/mobile/client/resources/menu-icon.png")
	ImageResource menuIcon();
	
	@Source("nl/uu/fi/dwo/mobile/client/resources/arrow-right-big.png")
	ImageResource arrowRightBig();
	
	
}
