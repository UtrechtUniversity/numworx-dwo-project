/**
 * 
 */
package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author peterboon
 *
 */
public class DWOTabletKeyboardUpperResponsive extends AbstractKeyboard {

	private static final int HEIGHT = 4*37+15;
	
	private static DWOTabletKeyboardABCResponsiveUiBinder uiBinder = GWT
			.create(DWOTabletKeyboardABCResponsiveUiBinder.class);

	interface DWOTabletKeyboardABCResponsiveUiBinder extends UiBinder<Widget, DWOTabletKeyboardUpperResponsive> {
	}

	/**
	 * Because this class has a default constructor, it can
	 * be used as a binder template. In other words, it can be used in other
	 * *.ui.xml files as follows:
	 * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
	  *   xmlns:g="urn:import:**user's package**">
	 *  <g:**UserClassName**>Hello!</g:**UserClassName>
	 * </ui:UiBinder>
	 * Note that depending on the widget that is used, it may be necessary to
	 * implement HasHTML instead of HasText.
	 */
	public DWOTabletKeyboardUpperResponsive() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public int getKeyboardHeight() {
		return HEIGHT;
	}

	@UiHandler({"ta","tb","tc", "td", "te", "tf", "tg", "th", "ti", "tj", "tk", "tl", "tm"} )
	void onTAM(ClickEvent e) { doInsert(e); }
	
	@UiHandler({"tn","to","tp", "tq", "tr", "ts", "tt", "tu", "tv", "tw", "tx", "ty", "tz"} )
	void onTNZ(ClickEvent e) { doInsert(e); }
	
	@UiHandler("t1_11") void onBackspace(ClickEvent e) { backspace(); } 
	@UiHandler("t2_10") void onEnter(ClickEvent e) { enter(); }

	@UiHandler({"t3_9", "t3_10"}) void onTKomma(ClickEvent e) { doInsert(e); } 
	
	@UiHandler({"t3_1", "t3_11"}) void onShift(ClickEvent e) { switchLower(); }
	
	@UiHandler("t4_1") void onT123(ClickEvent e) { switch123(); }
	@UiHandler("t4_2") void onTalfa(ClickEvent e) {switchGreek();}
	@UiHandler("t4_3") void onspace(ClickEvent e) { getEditor().insert(' '); }
	@UiHandler("t4_4") void onhand(ClickEvent e) { switchHand(); }
	
	@UiHandler("t4_5") void onblur(ClickEvent e) { blur(); }
	
	@Override
	void switch123() {
		getDelegate().switch123();
	}

	@Override
	void switchHand() {
		getDelegate().switchHand();
	}

	@Override
	void switchLower() {
		getDelegate().switchLower();
	}

	@UiField DKey t2_10;
	@Override
	void setEnterImage(DataResource resource) {
		t2_10.image.setUrl(resource.getSafeUri());
	}

	@Override
	void switchGreek() {
		getDelegate().switchGreek();
	}

	@Override
	public void blur() {
		getDelegate().blur();
	}
	
	
	
}
