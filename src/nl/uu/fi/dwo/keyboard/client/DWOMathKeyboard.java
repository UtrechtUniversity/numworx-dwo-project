/**
 * 
 */
package nl.uu.fi.dwo.keyboard.client;

import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author peterboon
 *
 */
public class DWOMathKeyboard extends AbstractKeyboard {
	
	private static TabletKeyboardUiBinder uiBinder = GWT
			.create(TabletKeyboardUiBinder.class);

	static int HEIGHT = 166;

	interface TabletKeyboardUiBinder extends UiBinder<Widget, DWOMathKeyboard> {
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
	public DWOMathKeyboard() {
		pad = new DWOTabletKeyboardPad();
		pad.setDelegate(this);
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@Override
	public void setEditor(FormuleEditorIF formuleEditor) {
		super.setEditor(formuleEditor);
		pad.setEditor(formuleEditor);
	}



	@UiField(provided=true)
	DWOTabletKeyboardPad pad;
	@UiField
	FKey t1_1,t1_2,t1_3,t1_4, t1_5,t1_6,t1_7,t1_8, t1_9,t1_10,t1_11,t1_12, t1_13,t1_14,t1_15;
	@UiField
	FKey t2_1,t2_2,t2_3,t2_4, t2_5,t2_6,t2_7,t2_8, t2_9,t2_10,t2_11,t2_12, t2_13,t2_14,t2_15;
	@UiField
	FKey t3_1,t3_2,t3_3,t3_4, t3_5,t3_6,t3_7,t3_8, t3_9,t3_10,t3_11,t3_12, t3_13,t3_14,t3_15;
	@UiField
	FKey t4_1,t4_2,t4_3,t4_4, t4_5,t4_6,t4_7,t4_8, t4_9,t4_10,t4_11,t4_12, t4_13,t4_14,t4_15;

	@UiHandler({
		"t1_1","t1_2","t1_3","t1_4","t1_5","t1_6","t1_7","t1_8","t1_9","t1_10","t1_11","t1_12","t1_13","t1_14","t1_15",
		"t2_1","t2_2","t2_3","t2_4","t2_5","t2_6","t2_7","t2_8","t2_9","t2_10","t2_11","t2_12","t2_13","t2_14","t2_15",
		"t3_1","t3_2","t3_3","t3_4","t3_5","t3_6","t3_7","t3_8","t3_9","t3_10","t3_11","t3_12","t3_13","t3_14","t3_15",
		"t4_1","t4_2","t4_3","t4_4","t4_5","t4_6","t4_7","t4_8","t4_9","t4_10","t4_11","t4_12","t4_13","t4_14","t4_15",
		} )
	void insert(ClickEvent e) {
		doInsert(e);
	}

	@Override
	public void blur() {
		getDelegate().blur();
	}

	@Override
	void switchABC() {
		getDelegate().switchABC();
	}
	
	@Override
	void switch123() {
		getDelegate().switch123();
	}

	@Override
	void switchHand() {
		getDelegate().switchHand();
	}
	
	DWOMathKeyboard init() {
		pad.t1_16.addStyleName("is-active");
		return this;
	}

	@Override
	int getKeyboardHeight() {
		return HEIGHT;
	}
}
