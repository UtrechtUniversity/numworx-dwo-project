/**
 * 
 */
package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.keyboard.client.AbstractKeyboard.HasHeight;

/**
 * @author peterboon
 *
 */
public class DWODesktopKeyboardResponsive extends AbstractKeyboard {

	private static DWODesktopKeyboardResponsiveUiBinder uiBinder = GWT
			.create(DWODesktopKeyboardResponsiveUiBinder.class);

	interface DWODesktopKeyboardResponsiveUiBinder extends UiBinder<Widget, DWODesktopKeyboardResponsive> {
	}
	private CombinedState state;
	@UiField ResponsiveCSS style;

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
	public DWODesktopKeyboardResponsive(CombinedState state) {
		initWidget(uiBinder.createAndBindUi(this));
		this.state=state;
        initStelselMenu(this::processStelselDimension);
		initVectorMenu(this::processVectorDimension);
		initMatrixMenu(this::processMatrixDimension);
	}

	private int HEIGHT = 2*37 + 15;
	@Override
	public int getKeyboardHeight() {
		return HEIGHT;
	}

	@Override
	public void onResize() {
		int width = getOffsetWidth() + combinedWidth(); // FIXME PARENT WIDTH
		setStyleName(style.small(), width<ResponsiveCSS.SMALL);
		setStyleName(style.normal(), width>=ResponsiveCSS.SMALL);
		int old = HEIGHT;
		if (width < ResponsiveCSS.SMALL) {
			HEIGHT = 4*37 + 15;
		} else {
			HEIGHT = 2*37 + 15;
		}
		if (old != HEIGHT)
		{	setPixelSize(-1, HEIGHT);
			scroll.setHeight(extra - HEIGHT);
		}
		Combined combined = state.getCombined();
		if (width < ResponsiveCSS.EXTRASMALL) {
			if (combined != Combined.NONE && combined != Combined.TABLET) 
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
	private boolean premium;

	@Override
	public void setScrollPanel(HasHeight w, int h) {
		super.setScrollPanel(w, h);
		scroll = w;
		extra = h;
	}

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

	@UiField FKey t3_6, t4_6, t5_6, t6_6;
	@Override
	public void setPremium(boolean premium) {
		this.premium = premium;
		if (!premium) {
			disableKey(t3_6);
			disableKey(t4_6);
			disableKey(t5_6);
			disableKey(t6_6);
		}
	}

	@Override
	boolean isPremium() {
		return this.premium;
	}
	@UiHandler("t3_1") void onT3_1(ClickEvent e) { getEditor().wortel(); }
	@UiHandler("t3_2") void onT3_2(ClickEvent e) { getEditor().macht(); }
	@UiHandler("t3_3") void onT3_3(ClickEvent e) { getEditor().kwadraat(); }
	@UiHandler("t3_4") void onT3_4(ClickEvent e) { getEditor().breuk(); }
	@UiHandler("t3_5") void onT3_5(ClickEvent e) { getEditor().haakjes(); }
	@UiHandler("t3_6") void onVector3_6(ClickEvent e) {
		if (isPremium())
			vectorDimensionDialog.showRelativeTo(t3_6);
	}

	@UiHandler("t4_1") void onT4_1(ClickEvent e) { getEditor().ndewortel(); }
	@UiHandler("t4_2") void onT_2(ClickEvent e) { getEditor().ndelog(); }
	@UiHandler("t4_3") void onT4_3(ClickEvent e) { getEditor().abs(); }
	@UiHandler("t4_4") void onT4_4(ClickEvent e) { getEditor().subscript(); }
	@UiHandler("t4_5") void onT4_5(ClickEvent e) { getEditor().bin(); }
	@UiHandler("t4_6") void onMatrix(ClickEvent e) { 	  
		if (isPremium())
			matrixDimensionDialog.showRelativeTo(t4_6);
	}
	
	@UiHandler("t5_1") void onT5_1(ClickEvent e) { getEditor().integraal();; }
	@UiHandler("t5_2") void onT5_2(ClickEvent e) { getEditor().prv(); }
	@UiHandler("t5_3") void onT5_3(ClickEvent e) { getEditor().primitieve(); }
	@UiHandler("t5_4") void onT5_4(ClickEvent e) { getEditor().diff(); }
	@UiHandler("t5_5") void onT5_5(ClickEvent e) { getEditor().diff_partial(); }
	@UiHandler("t5_6") void onVectorNotatie(ClickEvent e) { getEditor().vectornotatie(); }

	@UiHandler("t6_1") void onT6_1(ClickEvent e) { getEditor().limiet0(); }
	@UiHandler("t6_2") void onT6_2(ClickEvent e) { getEditor().limiet1(); }
	@UiHandler("t6_3") void onT6_3(ClickEvent e) { getEditor().limiet2(); }
	@UiHandler("t6_4") void onT6_4(ClickEvent e) { getEditor().sigma(); }
	@UiHandler("t6_5") void onT6_5(ClickEvent e) {
		getEditor().conjug(); }
	@UiHandler("t6_6") void onT6_6(ClickEvent e) {
		if (isPremium())
			stelselDimensionDialog.showRelativeTo(t6_6);
	}

	@UiHandler("tpi")  void onPi(ClickEvent e) { getEditor().insert('\u03C0'); }
	@UiHandler("tinf") void onT2(ClickEvent e) { getEditor().insert('∞'); }

	@UiHandler({"t10_1","t10_2", "t10_5", "t10_6"})
	void onT10(ClickEvent e) { doInsert(e); }

	@UiHandler("t12_1") void onT1_16(ClickEvent e) {switchGreek();}
	@UiHandler("t12_2") void onT2_16(ClickEvent e) {switch123();}
	@UiHandler("t12_4") void onT4_16(ClickEvent e) {switchHand();}
	@UiHandler("t13_2") void onT4_17(ClickEvent e) {blur();}

	@UiHandler({"t8_5", "t8_6", "t8_7", "t8_8"})
	void onHaakje(ClickEvent e) { doInsert(e); }

}
