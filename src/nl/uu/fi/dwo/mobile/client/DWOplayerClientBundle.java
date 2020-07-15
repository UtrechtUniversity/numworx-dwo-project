package nl.uu.fi.dwo.mobile.client;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.DataResource.MimeType;
import com.google.gwt.resources.client.ImageResource;
import com.googlecode.mgwt.ui.client.theme.base.ButtonCss;
import com.googlecode.mgwt.ui.client.theme.base.HeaderCss;

import nl.uu.fi.dwo.mobile.client.template.TemplateBasicCss;
import nl.uu.fi.dwo.mobile.client.template.TemplateNumworxCss;
import nl.uu.fi.dwo.mobile.client.template.TemplateUUTestCss;

public interface DWOplayerClientBundle extends ClientBundle {

	@Source("nl/uu/fi/dwo/mobile/client/resources/foutkruis.gif")
	ImageResource foutkruis();
	
	@Source("nl/uu/fi/dwo/mobile/client/resources/mw_kruisje_rood.png")
	ImageResource mw_kruisje_rood();

	@Source("nl/uu/fi/dwo/mobile/client/resources/pijlterug.gif")
	ImageResource pijlterug();
	
	@Source("nl/uu/fi/dwo/mobile/client/resources/pijlcopy.gif")
	ImageResource pijlcopy();
	
	@Source("nl/uu/fi/dwo/mobile/client/resources/closebutton.png")
	ImageResource closebutton();
	
	@Source("nl/uu/fi/dwo/mobile/client/resources/pijldown.gif")
	ImageResource pijldown();
	
	@Source("nl/uu/fi/dwo/mobile/client/resources/uitwerkingknop.svg")
	SVGResource uitwerkingsknop();
	
	@Source("nl/uu/fi/dwo/mobile/client/resources/abcknop.png")
	ImageResource abcknop();
	
	@Source("nl/uu/fi/dwo/mobile/client/resources/formuleachtergrond.png")
	ImageResource formuleachtergrondknop();
	
	@Source("nl/uu/fi/dwo/mobile/client/resources/appletknop.gif")
	ImageResource appletknop();

	@Source("nl/uu/fi/dwo/mobile/client/resources/crosshair.gif")
	ImageResource crosshair();

	@Source("nl/uu/fi/dwo/mobile/client/resources/klapuit1.png")
	ImageResource klapuit1();

	@Source("nl/uu/fi/dwo/mobile/client/resources/klapuit1goed.png")
	ImageResource klapuit1goed();

	@Source("nl/uu/fi/dwo/mobile/client/resources/klapuit2.png")
	ImageResource klapuit2();

	@Source("nl/uu/fi/dwo/mobile/client/resources/reload.png")
	ImageResource reload();
	
	
	@Source("nl/uu/fi/dwo/mobile/client/resources/imgbutton.css")
	ImgButton imgbutton();
	@Source("nl/uu/fi/dwo/mobile/client/resources/txtbutton.css")
	TxtButton txtbutton();
	
	@Source("nl/uu/fi/dwo/mobile/client/resources/header.css")
	HeaderCss headercss();
	
	@Source("nl/uu/fi/dwo/mobile/client/resources/menu-icon.png")
	ImageResource menuIcon();
	
	@Source("nl/uu/fi/dwo/mobile/client/resources/arrow-right-big.png")
	ImageResource arrowRightBig();
	
	@Source("nl/uu/fi/dwo/mobile/client/resources/vorigeknop.gif")
	ImageResource vorigeknop();
	
	@Source("nl/uu/fi/dwo/mobile/client/resources/volgendeknop.gif")
	ImageResource volgendeknop();

	@Source("nl/uu/fi/dwo/mobile/client/resources/rmknop.gif")
	ImageResource rmknop();

	@Source("nl/uu/fi/dwo/mobile/client/resources/DWOplayer.css")
	DWOplayerCss dwoplayercss();

	@Source("nl/uu/fi/dwo/mobile/client/resources/TemplateNumworx.css")
	TemplateNumworxCss templatenumworxcss();
	
	@Source("nl/uu/fi/dwo/mobile/client/resources/TemplateBasic.css")
	TemplateBasicCss templatebasiccss();
	
	@Source("nl/uu/fi/dwo/mobile/client/resources/TemplateUUTest.css")
	TemplateUUTestCss templateuutestcss();

	@Source("nl/uu/fi/dwo/mobile/client/resources/knop-indicatie-goed.png")
	ImageResource goed();

	@Source("nl/uu/fi/dwo/mobile/client/resources/knop-indicatie-magdoor.png")
	ImageResource half();

	@Source("nl/uu/fi/dwo/mobile/client/resources/knop-indicatie-fout.png")
	ImageResource fout();
 
	@Source("nl/uu/fi/dwo/mobile/client/resources/correctie.png")
    ImageResource correctie();
    @Source("nl/uu/fi/dwo/mobile/client/resources/corrected.png")
    ImageResource corrected();
	
	@Source("nl/uu/fi/dwo/mobile/client/resources/goed-numworx.svg")
	@MimeType("image/svg+xml")
	DataResource goednumworx();
	
	@Source("nl/uu/fi/dwo/mobile/client/resources/kb/tablet.png")
	ImageResource tablet();
	@Source("nl/uu/fi/dwo/mobile/client/resources/kb/tablet_active.png")
	ImageResource tablet_active();
	@Source("nl/uu/fi/dwo/mobile/client/resources/kb/desktop_active.png")
	ImageResource desktop_active();

	
	
}
