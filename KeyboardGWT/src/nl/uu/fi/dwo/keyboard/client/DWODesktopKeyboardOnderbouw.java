package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
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
	
	private static int HEIGHT = 52; // 10 + 32 + 10
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
			HEIGHT = 89;
		} else {
			HEIGHT = 52;
		}
		if (old != HEIGHT)
		{	setPixelSize(-1, HEIGHT);
			scroll.setHeight(extra - HEIGHT);
		}
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

	@UiHandler("t3_1") void onT3_1(ClickEvent e) {getEditor().wortel();}
	@UiHandler("t3_2") void onT3_2(ClickEvent e) {getEditor().macht();}
	@UiHandler("t3_3") void onT3_3(ClickEvent e) {getEditor().kwadraat();}
	@UiHandler("t3_4") void onT3_4(ClickEvent e) {getEditor().breuk();}
	@UiHandler("t3_5") void onT3_5(ClickEvent e) {getEditor().haakjes();}
	@UiHandler("t3_6") void onT3_6(ClickEvent e) {getEditor().ndewortel();}

	@UiHandler({"t4_8", "t4_11", "t4_12", "t4_13", "t4_14", "t4_15", "t1_5", "t1_6", "t1_7", "t1_8"} )
	void insert(ClickEvent e) {
		doInsert(e);
	}

	@UiHandler("t1_16") void onT3_14(ClickEvent e) {switchGreek();}
	@UiHandler("t4_16") void onT4_16(ClickEvent e) {switchHand(); }
	@UiHandler("t4_17") void onT4_17(ClickEvent e) {blur(); }

	@Override
	public void blur() {
		getDelegate().blur();
	}

	@Override
	void switchGreek() {
		getDelegate().switchGreek();
	}

	@Override
	void switchHand() {
		getDelegate().switchHand();
	}
	
	
}
