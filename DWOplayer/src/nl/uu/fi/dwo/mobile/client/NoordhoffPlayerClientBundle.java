package nl.uu.fi.dwo.mobile.client;

import com.google.gwt.resources.client.ClientBundle;
import com.googlecode.mgwt.ui.client.theme.base.HeaderCss;

public interface NoordhoffPlayerClientBundle extends ClientBundle {
	@Source("nl/uu/fi/dwo/mobile/client/resources/imgbutton.css")
	ImgButton imgbutton();
	@Source("nl/uu/fi/dwo/mobile/client/resources/txtbutton.css")
	TxtButton txtbutton();
	
	@Source("nl/uu/fi/dwo/mobile/client/resources/header.css")
	HeaderCss headercss();

}
