/**
 * 
 */
package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.client.constants.NumberConstants;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.recognizer.longtap.LongTapEvent;

import fi.wiskopdr.text.TextConstants;
import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;

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
	@UiField TextConstants rb;

	private boolean premium;

	@UiField FKey t3_6, t4_6, t5_6, t6_6;
	@UiField TDKey t6_7;

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

	@UiHandler({"t0","t1","t2","t3","t4","t5","t6","t7","t8","t9"})
	void onT(ClickEvent e) {
		doInsert(e);
	}
	@UiHandler("t1_1") 
	void onTx(ClickEvent e) {
		getEditor().insert('*');
	}
	@UiHandler({"t1_2","t1_3","t1_4","t1_5","t1_6"})
	void onT1(ClickEvent e) {
		doInsert(e);
	}
	@UiHandler("tx") void onX(ClickEvent e) { getEditor().insert('x'); }
	@UiHandler({"ty","tp","tq","te", "tpi", "tinf", "t6_8"})
	void onT2(ClickEvent e) {
		doInsert(e);
	}
	@UiHandler("tof")
	void onTof(ClickEvent e) {
		getEditor().insert(" " + rb.ofLabel() + " ");
	}
	@UiHandler("t3_1") void onT3_1(ClickEvent e) { getEditor().wortel(); }
	@UiHandler("t3_2") void onT3_2(ClickEvent e) { getEditor().macht(); }
	@UiHandler("t3_3") void onT3_3(ClickEvent e) { getEditor().kwadraat(); }
	@UiHandler("t3_4") void onT3_4(ClickEvent e) { getEditor().breuk(); }
	@UiHandler("t3_5") void onT3_5(ClickEvent e) { getEditor().haakjes(); }
	@UiHandler({"t3_6","t6_7"}) void onVector(ClickEvent e) { /* TODO vector */ }

	@UiHandler("t4_1") void onT4_1(ClickEvent e) { getEditor().ndewortel(); }
	@UiHandler("t4_2") void onT_2(ClickEvent e) { getEditor().ndelog(); }
	@UiHandler("t4_3") void onT4_3(ClickEvent e) { getEditor().abs(); }
	@UiHandler("t4_4") void onT4_4(ClickEvent e) { getEditor().subscript(); }
	@UiHandler("t4_5") void onT4_5(ClickEvent e) { getEditor().bin(); }
	@UiHandler("t4_6") void onMatrix(ClickEvent e) { /* TODO matrix */ }
	
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
	@UiHandler("t6_6") void onT6_6(ClickEvent e) { getEditor().stelsel();} // TODO deze okay?
	
	@UiHandler("tsin") void onSin(ClickEvent e) { getEditor().insert("sin"); }
	@UiHandler({"tasin","tcos","tacos", "ttan", "tatan", "tlog","tln"})
	void onTmath(ClickEvent e) { doInsert(e); }
	
	@UiHandler({"t8_1", "t8_2", "t8_3", "t8_4", "t8_5", "t8_6", "t8_7", "t8_8"})
	void onHaakje(ClickEvent e) { doInsert(e); }
	
	@UiHandler("t9_1") void onLeft(ClickEvent e) { getEditor().cursorToLeft(); }
	@UiHandler("t9_2") void onRight(ClickEvent e) { getEditor().cursorToRight(); }

	@UiHandler("t10_2") void on10_2(ClickEvent e) { getEditor().insert('≈'); };
	@UiHandler("t10_3") void onLT(ClickEvent e) { getEditor().insert('<'); }
	@UiHandler("t10_4") void onGT(ClickEvent e) { getEditor().insert('>'); }
	@UiHandler({"t10_1", "t10_5", "t10_6"})
	void onT10(ClickEvent e) { doInsert(e); }

	@Override
	public void blur() {
		getDelegate().blur();
	}

	@Override
	void switchABC() {
		getDelegate().switchABC();
	}

	@Override
	void switchGreek() {
		getDelegate().switchGreek();
	}

	@Override
	void switchHand() {
		getDelegate().switchHand();
	}

	@Override
	public void setEditor(FormuleEditorIF formuleEditor) {
		pad.setEditor(formuleEditor);
		super.setEditor(formuleEditor);
	}	
	
	@Override
	void setEnterImage(DataResource resource) {
		pad.setEnterImage(resource);
	}

	protected void disableKey(TDKey key) {
		key.setHTML("");key.addStyleName("disabled");
		key.getParent().removeStyleName(style.haak());
	}

	@Override
	public void setPremium(boolean premium) {
		this.premium = premium;
		if (!premium) {
			disableKey(t3_6);
			disableKey(t4_6);
			disableKey(t5_6);
			disableKey(t6_7);
			disableKey(t6_6);
		}
	}

	@Override
	boolean isPremium() {
		return this.premium;
	}

	@UiField TKey tx;
	private Multikey mx;

	private boolean hasLongTap = true;
// of oncontext?
	@UiHandler("tx") void longOnTX(LongTapEvent ev) {
		if (mx == null) {
			mx = new Multikey();
			mx.setKeys('x','y','p','q');
			mx.setVarStyle();
		}
		int px = tx.getAbsoluteLeft();
		int py = tx.getAbsoluteTop();
		mx.setEditor(getEditor());
		if (hasLongTap ) mx.show(px, py); else onX(null);
	}
	@UiField TKey t10_2, t10_3;
	private Multikey m10_2, m10_3;
	@UiHandler("t10_2") void LongOnT10_2(LongTapEvent ev) {
		if (m10_2 == null) {
			m10_2 = new Multikey();
			m10_2.setKeys('≠', '≈');
		}
		int px = t10_2.getAbsoluteLeft();
		int py = t10_2.getAbsoluteTop();
		m10_2.setEditor(getEditor());
		if (hasLongTap ) m10_2.show(px, py); else on10_2(null);
	}
	@UiHandler("t10_3") void longOnT10_3(LongTapEvent ev) {
		if (m10_3 == null) {
			m10_3 = new Multikey();
			m10_3.setKeys('<', '>', '≤', '≥','≈','≠' );
		}
		int px = t10_3.getAbsoluteLeft();
		int py = t10_3.getAbsoluteTop();
		m10_3.setEditor(getEditor());
		if (hasLongTap ) m10_3.show(px, py); else onLT(null);
	}
}
