/**
 * 
 */
package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.client.constants.NumberConstants;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author peterboon
 *
 */
public class DWOTabletKeyboardResponsive extends AbstractKeyboard {

	private static DWOTabletKeyboardResponsiveUiBinder uiBinder = GWT.create(DWOTabletKeyboardResponsiveUiBinder.class);

	interface DWOTabletKeyboardResponsiveUiBinder extends UiBinder<Widget, DWOTabletKeyboardResponsive> {
	}

	@UiField(provided=true) NumberConstants nc;
	@UiField ResponsiveCSS style;
	@UiField DWOTabletKeyboardPad pad;

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
	public DWOTabletKeyboardResponsive() {
		LocaleInfo currentLocale = LocaleInfo.getCurrentLocale();
		nc = currentLocale.getNumberConstants();
		initWidget(uiBinder.createAndBindUi(this));
		pad.setDelegate(this);
	}

	final static private int HEIGHT = 4*37+15;

	@Override
	public int getKeyboardHeight() {
		return HEIGHT;
	}

}
