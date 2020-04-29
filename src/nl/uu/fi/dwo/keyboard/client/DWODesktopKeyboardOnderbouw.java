package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

public class DWODesktopKeyboardOnderbouw extends AbstractKeyboard implements RequiresResize {

	private static DWODesktopKeyboardOnderbouwUiBinder uiBinder = GWT.create(DWODesktopKeyboardOnderbouwUiBinder.class);

	interface DWODesktopKeyboardOnderbouwUiBinder extends UiBinder<Widget, DWODesktopKeyboardOnderbouw> {
	}
	
	@UiField ResponsiveCSS style;
	
	void insert(char ch) {
		getEditor().insert(ch);
	}

	public DWODesktopKeyboardOnderbouw(CombinedState state) {
		initWidget(uiBinder.createAndBindUi(this));
		this.state = state;
	}
	
	private static int HEIGHT = 90;
	public int getKeyboardHeight() {
		return HEIGHT;
	}

	@Override
	public void onResize() {
		Widget root = getWidget();
		int width = root.getOffsetWidth() + combinedWidth(); // FIXME PARENT WIDTH
		root.setStyleName(style.small(), width<ResponsiveCSS.SMALL);
		root.setStyleName(style.normal(), width>=ResponsiveCSS.SMALL);
		int old = HEIGHT;
		if (width < ResponsiveCSS.SMALL) {
			HEIGHT = 90;
		} else {
			HEIGHT = 45;
		}
		if (old != HEIGHT)
			scroll.setHeight(HEIGHT);
		Combined combined = state.getCombined();
		if (width < ResponsiveCSS.EXTRASMALL) {
			if (combined == Combined.DESKTOP_ACTIVE) 
				state.setCombined(Combined.NONE);
		} else {
			if (combined == Combined.NONE)
				state.setCombined(Combined.DESKTOP_ACTIVE);
		}
	}

	private int combinedWidth() {
		return state.getCombined() == Combined.NONE ? 0 : state.getWidth();
	}

	int extra;
	HasHeight scroll;

	@Override
	public void setScrollPanel(HasHeight w, int h) {
		super.setScrollPanel(w, h);
		scroll = w;
		extra = h;
	}
	
	CombinedState state;
	
}
