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

	private CombinedState state;
	
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
	public DWOTabletKeyboardResponsive(CombinedState s) {
		state = s;
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

	@Override
	public void onResize() {
		int w = getOffsetWidth() + combinedWidth();
		setStyleName(style.normal(), w >= ResponsiveCSS.SMALL);
		setStyleName(style.small(), w < ResponsiveCSS.SMALL && w >= ResponsiveCSS.EXTRASMALL);
		setStyleName(style.extrasmall(), w < ResponsiveCSS.EXTRASMALL);
		Combined combined = state.getCombined();
		
		if (w < ResponsiveCSS.EXTRASMALL && combined != Combined.NONE) {
			state.setCombined(Combined.NONE);
		} else if (combined == Combined.NONE && w >= ResponsiveCSS.EXTRASMALL) {
			state.setCombined(Combined.TABLET_ACTIVE); // of soft
		}

	}

	private int combinedWidth() {
		if (state.getCombined() != Combined.NONE) return state.getWidth();
		return 0;
	}

}
