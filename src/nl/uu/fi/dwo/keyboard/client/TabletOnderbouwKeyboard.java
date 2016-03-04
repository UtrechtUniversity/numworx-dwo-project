/**
 * 
 */
package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author wim
 *
 */
public class TabletOnderbouwKeyboard extends AbstractKeyboard {

	private static final int HEIGHT = 208;
	
	private static TabletOnderbouwKeyboardUiBinder uiBinder = GWT
			.create(TabletOnderbouwKeyboardUiBinder.class);

	interface TabletOnderbouwKeyboardUiBinder extends
			UiBinder<Widget, TabletOnderbouwKeyboard> {
	}

	@UiField FKey t1_2, t2_2;
	
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
	public TabletOnderbouwKeyboard() {
		initWidget(uiBinder.createAndBindUi(this));
		setPixelSize(882,HEIGHT);
	}

	public TabletOnderbouwKeyboard(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	int getKeyboardHeight() {
		return HEIGHT;
	}

	DuoKeys duoLT;
	@UiHandler("t1_2") void onT1_2(ClickEvent e) {
		hideAll();
		if(duoLT == null) {
			duoLT = new DuoKeys();
			duoLT.setOrg('<');
			duoLT.setAlt('≤');
		}
		duoLT.setEditor(getEditor());
		int x = t1_2.getAbsoluteLeft();
		int y = t1_2.getAbsoluteTop();
		duoLT.showDuo(x, y);
	}

	private void hideAll() {
		if(duoGT != null && duoGT.isDuoShown()) duoGT.hideDuo();
		if(duoGR != null && duoGR.isDuoShown()) duoGR.hideDuo();
		if(duoLT != null && duoLT.isDuoShown()) duoLT.hideDuo();
	}

	DuoKeys duoGT;
	@UiHandler("t2_2") void onT2_2(ClickEvent e) {
		hideAll();
		if(duoGT == null) {
			duoGT = new DuoKeys();
			duoGT.setOrg('>');
			duoGT.setAlt('≥');
		}
		duoGT.setEditor(getEditor());
		int x = t2_2.getAbsoluteLeft();
		int y = t2_2.getAbsoluteTop();
		duoGT.showDuo(x, y);
	}
	
	DuoKeys duoGR;
	@UiField FKey t3_5;
	@UiHandler("t3_5") void onT3_5(ClickEvent e) {
		hideAll();
		if(duoGR == null) {
			duoGR = new DuoKeys();
			duoGR.org.removeStyleName("bg-purple");
			duoGR.alt.removeStyleName("bg-purple");
			duoGR.setOrg('°');
			duoGR.setAlt('∠');
		}
		duoGR.setEditor(getEditor());
		int x = t3_5.getAbsoluteLeft();
		int y = t3_5.getAbsoluteTop();
		duoGR.showDuo(x, y);
	}


}
