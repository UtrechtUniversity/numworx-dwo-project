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


}
