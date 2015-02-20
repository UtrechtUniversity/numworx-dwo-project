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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author wim
 *
 */
public class DesktopKeyboard extends Composite {

	private static DesktopKeyboardUiBinder uiBinder = GWT
			.create(DesktopKeyboardUiBinder.class);

	interface DesktopKeyboardUiBinder extends UiBinder<Widget, DesktopKeyboard> {
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
	public DesktopKeyboard() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	Key c1, c2, c3, c4, c5;
	@UiField
	Key c6, c7, c8, c9, c10;
	@UiField
	Key c11, c12, c13, c14, c15;
	@UiField
	Key c16, c17, c18, c19, c20;
	@UiField
	Key c21, c22, c23, c24, c25;
	@UiField
	Key c26, c27, c28, c29, c30, c31;

	@UiHandler(value={"c1", "c2"})
	void onClick(ClickEvent e) {
		System.out.println(e.getSource());
	}

}
