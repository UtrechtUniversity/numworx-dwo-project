package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.i18n.shared.Localizable;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.DataResource.MimeType;

public interface DWOkeyboardBundle extends ClientBundle, Localizable {
  
    // SVG resources
  
    @Source("nl/uu/fi/dwo/keyboard/client/resources/wortel.svg")
    @MimeType("image/svg+xml")
    DataResource wortel_svg();
    @Source("nl/uu/fi/dwo/keyboard/client/resources/diff.svg")
    @MimeType("image/svg+xml")
    DataResource diff_svg();
    @Source("nl/uu/fi/dwo/keyboard/client/resources/macht.svg")
    @MimeType("image/svg+xml")
    DataResource macht_svg();
    @Source("nl/uu/fi/dwo/keyboard/client/resources/limiet0.svg")
    @MimeType("image/svg+xml")
    DataResource limiet0_svg();
    @Source("nl/uu/fi/dwo/keyboard/client/resources/limiet1.svg")
    @MimeType("image/svg+xml")
    DataResource limiet1_svg();
    @Source("nl/uu/fi/dwo/keyboard/client/resources/limiet2.svg")
    @MimeType("image/svg+xml")
    DataResource limiet2_svg();
    
    @Source("nl/uu/fi/dwo/keyboard/client/resources/bin.svg")
    @MimeType("image/svg+xml")
    DataResource bin_svg();
    @Source("nl/uu/fi/dwo/keyboard/client/resources/breuk.svg")
    @MimeType("image/svg+xml")
    DataResource breuk_svg();
    @Source("nl/uu/fi/dwo/keyboard/client/resources/conjug.svg")
    @MimeType("image/svg+xml")
    DataResource conjug_svg();
    @Source("nl/uu/fi/dwo/keyboard/client/resources/haakjes.svg")
    @MimeType("image/svg+xml")
    DataResource haakjes_svg();
    @Source("nl/uu/fi/dwo/keyboard/client/resources/abs.svg")
    @MimeType("image/svg+xml")
    DataResource abs_svg();
    @Source("nl/uu/fi/dwo/keyboard/client/resources/integraal.svg")
    @MimeType("image/svg+xml")
    DataResource integraal_svg();
    @Source("nl/uu/fi/dwo/keyboard/client/resources/kwadraat.svg")
    @MimeType("image/svg+xml")
    DataResource kwadraat_svg();
    
    @Source("nl/uu/fi/dwo/keyboard/client/resources/ndelog.svg")
    @MimeType("image/svg+xml")
    DataResource ndelog_svg();
    @Source("nl/uu/fi/dwo/keyboard/client/resources/ndewortel.svg")
    @MimeType("image/svg+xml")
    DataResource ndewortel_svg();
    
    @Source("nl/uu/fi/dwo/keyboard/client/resources/partialdiff.svg")
    @MimeType("image/svg+xml")
    DataResource partialdiff_svg();
    
    @Source("nl/uu/fi/dwo/keyboard/client/resources/primitive.svg")
    @MimeType("image/svg+xml")
    DataResource primitieve_svg();

    @Source("nl/uu/fi/dwo/keyboard/client/resources/prv.svg")
    @MimeType("image/svg+xml")
    DataResource prv_svg();

    @Source("nl/uu/fi/dwo/keyboard/client/resources/sigma.svg")
    @MimeType("image/svg+xml")
    DataResource sigma_svg();
 
    @Source("nl/uu/fi/dwo/keyboard/client/resources/subscript.svg")
    @MimeType("image/svg+xml")
    DataResource subscript_svg();

    @Source("nl/uu/fi/dwo/keyboard/client/resources/keyboard.svg")
    @MimeType("image/svg+xml")
    DataResource VVV_svg();
    
    @Source("nl/uu/fi/dwo/keyboard/client/resources/backspace.svg")
    @MimeType("image/svg+xml")
    DataResource backspace_svg();
    
    @Source("nl/uu/fi/dwo/keyboard/client/resources/check-vink.svg")
    @MimeType("image/svg+xml")
    DataResource apply_svg();
    @Source("nl/uu/fi/dwo/keyboard/client/resources/gesture.svg")
    @MimeType("image/svg+xml")
    DataResource hand_svg();
  
    @Source("nl/uu/fi/dwo/keyboard/client/resources/enter.svg")
    @MimeType("image/svg+xml")
    DataResource enter_svg();
    
    @Source("nl/uu/fi/dwo/keyboard/client/resources/matrix.svg")
    @MimeType("image/svg+xml")
    DataResource matrix_svg();
    
    @Source("nl/uu/fi/dwo/keyboard/client/resources/vector.svg")
    @MimeType("image/svg+xml")
    DataResource vector_svg();
    
    @Source("nl/uu/fi/dwo/keyboard/client/resources/vectornotatie.svg")
    @MimeType("image/svg+xml")
    DataResource vectornotatie_svg();
    
    @Source("nl/uu/fi/dwo/keyboard/client/resources/stelsel.svg")
    @MimeType("image/svg+xml")
    DataResource stelsel_svg();
   
    
    
	@Source("nl/uu/fi/dwo/keyboard/client/resources/mw_breuk.gif")
	ImageResource breuk();

	@Source("nl/uu/fi/dwo/keyboard/client/resources/mw_kwadraat.gif")
	ImageResource kwadraat();
	
	@Source("nl/uu/fi/dwo/keyboard/client/resources/mw_macht.gif")
	ImageResource macht();

	@Source("nl/uu/fi/dwo/keyboard/client/resources/mw_ndewortel.gif")
	ImageResource ndewortel();

	@Source("nl/uu/fi/dwo/keyboard/client/resources/mw_wortel.gif")
	ImageResource wortel();

	@Source("nl/uu/fi/dwo/keyboard/client/resources/keyboardremovebutton.png")
	ImageResource VVV();

	@Source("nl/uu/fi/dwo/keyboard/client/resources/mw_abs.gif")
	ImageResource abs();
	@Source("nl/uu/fi/dwo/keyboard/client/resources/mw_bin.gif")
	ImageResource bin();
	@Source("nl/uu/fi/dwo/keyboard/client/resources/mw_diff.gif")
	ImageResource diff();
	@Source("nl/uu/fi/dwo/keyboard/client/resources/mw_haakjes.gif")
	ImageResource haakjes();
	@Source("nl/uu/fi/dwo/keyboard/client/resources/mw_integraal.gif")
	ImageResource integraal();
	@Source("nl/uu/fi/dwo/keyboard/client/resources/mw_limiet0.gif")
	ImageResource limiet0();
	@Source("nl/uu/fi/dwo/keyboard/client/resources/mw_limiet1.gif")
	ImageResource limiet1();
	@Source("nl/uu/fi/dwo/keyboard/client/resources/mw_limiet2.gif")
	ImageResource limiet2();
	@Source("nl/uu/fi/dwo/keyboard/client/resources/mw_ndelog.gif")
	ImageResource ndelog();
	@Source("nl/uu/fi/dwo/keyboard/client/resources/mw_primitieve.gif")
	ImageResource primitieve();
	@Source("nl/uu/fi/dwo/keyboard/client/resources/mw_prv.gif")
	ImageResource prv();
	@Source("nl/uu/fi/dwo/keyboard/client/resources/mw_subscript.gif")
	ImageResource subscript();
	@Source("nl/uu/fi/dwo/keyboard/client/resources/mw_sigma.gif")
	ImageResource sigma();
	@Source("nl/uu/fi/dwo/keyboard/client/resources/mw_partialdiff.gif")
	ImageResource partialdiff();
	@Source("nl/uu/fi/dwo/keyboard/client/resources/mw_conjug.gif")
	ImageResource conjug();
	
	@Source("nl/uu/fi/dwo/keyboard/client/resources/apply.png")
	ImageResource apply();
	@Source("nl/uu/fi/dwo/keyboard/client/resources/enter.png")
	ImageResource enter();
	@Source("nl/uu/fi/dwo/keyboard/client/resources/BackSpaceIcon.png")
	ImageResource backspace();
	@Source("nl/uu/fi/dwo/keyboard/client/resources/hand.png")
	ImageResource hand();
	@Source("nl/uu/fi/dwo/keyboard/client/resources/empty.png")
	ImageResource empty();

	@Source("nl/uu/fi/dwo/keyboard/client/resources/vinkje.png")
	ImageResource vinkje();
	
	@Source("nl/uu/fi/dwo/keyboard/client/resources/vector.png")
	ImageResource vector();
	@Source("nl/uu/fi/dwo/keyboard/client/resources/matrix.png")
	ImageResource matrix();
	@Source("nl/uu/fi/dwo/keyboard/client/resources/vectornotatie.png")
	ImageResource vectornotatie();
	@Source("nl/uu/fi/dwo/keyboard/client/resources/stelsel.png")
	ImageResource stelsel();
	
	@Source("nl/uu/fi/dwo/keyboard/client/resources/Multikey.css")
	MultikeyCss multikeycss();
	
}
