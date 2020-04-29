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
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.keyboard.client.AbstractKeyboard.HasHeight;

/**
 * @author peterboon
 *
 */
public class DWOTabletKeyboardOnderbouw extends AbstractKeyboard  implements RequiresResize {

	private static DWOTabletKeyboardOnderbouwUiBinder uiBinder = GWT.create(DWOTabletKeyboardOnderbouwUiBinder.class);

	interface DWOTabletKeyboardOnderbouwUiBinder extends UiBinder<Widget, DWOTabletKeyboardOnderbouw> {
	}

	private CombinedState state;
	@UiField ResponsiveCSS style;
	private HasHeight scroll;
	private int extra;
	
	public DWOTabletKeyboardOnderbouw(CombinedState s) {
		this();
		state = s;
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
	private DWOTabletKeyboardOnderbouw() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	private static int HEIGHT = 135;

	@Override
	public int getKeyboardHeight() {
		return HEIGHT;
	}

	private int combinedWidth() {
		return state.getCombined() == Combined.NONE ? 0 : state.getWidth();
	}

	@Override
	public void onResize() {
		Widget root = getWidget();
		int width = root.getOffsetWidth() + combinedWidth();
		root.setStyleName(style.small(), width<ResponsiveCSS.SMALL && width >= ResponsiveCSS.EXTRASMALL);
		root.setStyleName(style.normal(), width>=ResponsiveCSS.SMALL);
		boolean extra = width < ResponsiveCSS.EXTRASMALL;
		root.setStyleName(style.extrasmall(), extra);
		int old = HEIGHT;
		if (extra) {
			HEIGHT = 4*37+15;
		} else {
			HEIGHT = 3*37+15;
		}
		if (old != HEIGHT) {
			scroll.setHeight(HEIGHT);
		}
		Combined combined = state.getCombined();
		if (extra) {
			if (combined != Combined.NONE) 
				state.setCombined(Combined.NONE);
		} else {
			if (combined == Combined.NONE)
				state.setCombined(Combined.TABLET_ACTIVE);
		}
		
	}
	@Override
	public void setScrollPanel(HasHeight w, int h) {
		super.setScrollPanel(w, h);
		scroll = w;
		extra = h;
	}
}
