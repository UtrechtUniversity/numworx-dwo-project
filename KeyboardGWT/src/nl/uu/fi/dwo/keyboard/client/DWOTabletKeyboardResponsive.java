/**
 * 
 */
package nl.uu.fi.dwo.keyboard.client;

import java.util.function.Consumer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.client.constants.NumberConstants;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;

import fi.wiskopdr.text.TextConstants;
import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.keyboard.client.tap.LongTapEvent;

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
	@UiField DWOkeyboardBundle resources;

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
		
        initStelselMenu(this::processStelselDimension);
		initVectorMenu(this::processVectorDimension);
		initMatrixMenu(this::processMatrixDimension);
	}

	final static private int HEIGHT = 4*37+15;

	@Override
	public int getKeyboardHeight() {
		return HEIGHT;
	}

	@Override
	public void onResize() {
		int w = getOffsetWidth() + combinedWidth();
		hasLongTap = w < ResponsiveCSS.SMALL;
		setStyleName(style.normal(), !hasLongTap);
		setStyleName(style.small(), hasLongTap && w >= ResponsiveCSS.EXTRASMALL);
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
	@UiHandler("t1_6") void onTmin(ClickEvent e) { 
		getEditor().insert('-'); }
	@UiHandler({"t1_2","t1_3","t1_4","t1_5"})
	void onT1(ClickEvent e) {
		doInsert(e);
	}
	@UiHandler("tx") void onX(ClickEvent e) { getEditor().insert('x'); }
	@UiHandler("tpi")void onPi(ClickEvent e){ getEditor().insert('\u03C0'); }
	@UiHandler({"ty","tp","tq","te", "tinf", "t6_8","ta","tb","tn","tk","tee"})
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
	@UiHandler("t3_6") void onVector3_6(ClickEvent e) {
		if (isPremium())
			vectorDimensionDialog.showRelativeTo(t3_6);
	}
	@UiHandler("t6_7") void onVector(ClickEvent e) {
		if (isPremium())
			vectorDimensionDialog.showRelativeTo(t6_7);
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
	
	@UiHandler("tsin") void onSin(ClickEvent e) { getEditor().insert("sin"); }
	@UiHandler({"tcos", "ttan",  "tlog","tln"})
	void onTmath(ClickEvent e) { doInsert(e); }
	@UiHandler("tasin") void onTasin(ClickEvent e) { getEditor().insert("arcsin"); }
	@UiHandler("tacos") void onTacos(ClickEvent e) { getEditor().insert("arccos"); }
	@UiHandler("tatan") void onTatan(ClickEvent e) { getEditor().insert("arctan"); }
	
	
	@UiHandler({"t8_1", "t8_2", "t8_3", "t8_4", "t8_6", "t8_7"})
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
			mx.setKeys('x','y','p','q', 'a', 'b', 'n','k');
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
	
	@UiField TKey tsin;
	private Multikey msin;
	@UiHandler("tsin") void longOnTsin(LongTapEvent e) {
		if (msin == null) {
			msin = new Multikey();
			msin.setFunStyle();
			msin.setKeys("sin","cos","tan","log","ln", "asin", "acos", "atan");
		}
		int px = tsin.getAbsoluteLeft()-3*37;
		int py = tsin.getAbsoluteTop();
		msin.setEditor(getEditor());
		if (hasLongTap ) msin.show(px, py); else onSin(null);
	}
	@UiField TKey tpi;
	private Multikey mpi;
	@UiHandler("tpi") void longOnPi(LongTapEvent e) {
		if (mpi == null) {
			mpi = new Multikey();
			FKey k = mpi.addKey('\u03C0');
			k.getElement().getStyle().setFontStyle(FontStyle.ITALIC);
			k = mpi.addKey('e');
			k.getElement().getStyle().setFontStyle(FontStyle.NORMAL);
		}
		int px = tpi.getAbsoluteLeft();
		int py = tpi.getAbsoluteTop();
		mpi.setEditor(getEditor());
		if (hasLongTap ) mpi.show(px, py); else onPi(null);
		
	}
	
	
	@UiField TKey t3_4;
	private Multikey m3_4;
	@UiHandler("t3_4") void longTapT3_4(LongTapEvent ev) {
		if (m3_4 == null) {
			m3_4 = new Multikey();
			DataResource[] keys = { resources.breuk_svg(), resources.wortel_svg(), resources.macht_svg(), resources.kwadraat_svg(), resources.haakjes_svg(), resources.bin_svg() };
			@SuppressWarnings("unchecked")
			Consumer<FormuleEditorIF> actions[] = new Consumer[keys.length];
			actions[0] = FormuleEditorIF::breuk;
			actions[1] = FormuleEditorIF::wortel;
			actions[2] = FormuleEditorIF::macht;
			actions[3] = FormuleEditorIF::kwadraat;
			actions[4] = FormuleEditorIF::haakjes;
			actions[5] = FormuleEditorIF::bin;
			m3_4.setKeys(keys, actions);
		}
		int px = t3_4.getAbsoluteLeft()-2*37;
		int py = t3_4.getAbsoluteTop();
		m3_4.setEditor(getEditor());
		if (hasLongTap ) m3_4.show(px, py); else onT3_4(null);		
	}
	
	@UiField TKey t4_1;
	private Multikey m4_1;
	@UiHandler("t4_1") void longTapT4_1(LongTapEvent e) {
		if (m4_1 == null) {
			m4_1 = new Multikey();
			DataResource[] keys = { resources.ndewortel_svg(), resources.ndelog_svg(), resources.abs_svg(), resources.subscript_svg(), resources.bin_svg(), resources.conjug_svg()};
			Consumer<FormuleEditorIF> actions[] = new Consumer[keys.length];
			actions[0] = FormuleEditorIF::ndewortel;
			actions[1] = FormuleEditorIF::ndelog;
			actions[2] = FormuleEditorIF::abs;
			actions[3] = FormuleEditorIF::subscript;
			actions[4] = FormuleEditorIF::bin;
			actions[5] = FormuleEditorIF::conjug;
			m4_1.setKeys(keys, actions);
		}
		int px = t4_1.getAbsoluteLeft()-2*37;
		int py = t4_1.getAbsoluteTop();
		m4_1.setEditor(getEditor());
		if (hasLongTap ) m4_1.show(px, py); else onT4_1(null);		
	}

	@UiField TKey t5_1;
	private Multikey m5_1;
	@UiHandler("t5_1") void longTapT5_1(LongTapEvent e) {
		if (m5_1 == null) {
			m5_1 = new Multikey();
			DataResource[] keys = { resources.integraal_svg(), resources.prv_svg(), resources.primitieve_svg(), resources.sigma_svg(), resources.diff_svg(), resources.partialdiff_svg()};
			@SuppressWarnings("unchecked")
			Consumer<FormuleEditorIF> actions[] = new Consumer[keys.length];
			actions[0] = FormuleEditorIF::integraal;
			actions[1] = FormuleEditorIF::prv;
			actions[2] = FormuleEditorIF::primitieve;
			actions[3] = FormuleEditorIF::sigma;
			actions[4] = FormuleEditorIF::diff;
			actions[5] = FormuleEditorIF::diff_partial;
			m5_1.setKeys(keys, actions);
		}
		int px = t5_1.getAbsoluteLeft()-2*37;
		int py = t5_1.getAbsoluteTop();
		m5_1.setEditor(getEditor());
		if (hasLongTap ) m5_1.show(px, py); else onT5_1(null);		
	}

	@UiField TKey t5_4;
	private Multikey m5_4;
	@UiHandler("t5_4") void longTapT5_4(LongTapEvent e) {
		if (m5_4 == null) {
			m5_4 = new Multikey();
			DataResource[] keys = { resources.diff_svg(), resources.partialdiff_svg()};
			@SuppressWarnings("unchecked")
			Consumer<FormuleEditorIF> actions[] = new Consumer[keys.length];
			actions[0] = FormuleEditorIF::diff;
			actions[1] = FormuleEditorIF::diff_partial;
			m5_4.setKeys(keys, actions);
		}
		int px = t5_4.getAbsoluteLeft();
		int py = t5_4.getAbsoluteTop();
		m5_4.setEditor(getEditor());
		if (hasLongTap ) m5_4.show(px, py); else onT5_4(null);		
	}

	@UiField TKey t6_1;
	private Multikey m6_1;
	@UiHandler("t6_1") void longTapT6_1(LongTapEvent e) {
		if (m6_1 == null) {
			m6_1 = new Multikey();
			DataResource[] keys = { resources.limiet0_svg(), resources.limiet1_svg(), resources.limiet2_svg()};
			@SuppressWarnings("unchecked")
			Consumer<FormuleEditorIF> actions[] = new Consumer[keys.length];
			actions[0] = FormuleEditorIF::limiet0;
			actions[1] = FormuleEditorIF::limiet1;
			actions[2] = FormuleEditorIF::limiet2;
			m6_1.setKeys(keys, actions);
			m6_1.addKey('∞');
		}
		int px = t6_1.getAbsoluteLeft();
		int py = t6_1.getAbsoluteTop();
		m6_1.setEditor(getEditor());
		if (hasLongTap ) m6_1.show(px, py); else onT6_1(null);		
	}
	
	@UiField TKey t8_5, t8_8;
	private Multikey m8_5, m8_8;
	@UiHandler("t8_5") void onT8_5(ClickEvent e) { getEditor().insert('〈');}
	@UiHandler("t8_5") void longOnT8_5(LongTapEvent ev) {
		if (m8_5 == null) {
			m8_5 = new Multikey();
			m8_5.setAltStyle();
			m8_5.setKeys('〈', '〉', '[', ']' );
		}
		int px = t8_5.getAbsoluteLeft();
		int py = t8_5.getAbsoluteTop();
		m8_5.setEditor(getEditor());
		if (hasLongTap ) m8_5.show(px, py); else onT8_5(null);
	}
	@UiHandler("t8_8") void onT8_8(ClickEvent e) { getEditor().insert('→');}
	@UiHandler("t8_8") void longOnT8_8(LongTapEvent ev) {
		if (m8_8 == null) {
			m8_8 = new Multikey();
			m8_8.setAltStyle();
			m8_8.setKeys('→', '←' );
		}
		int px = t8_8.getAbsoluteLeft();
		int py = t8_8.getAbsoluteTop();
		m8_8.setEditor(getEditor());
		if (hasLongTap ) m8_8.show(px, py); else onT8_8(null);
	}
	private Multikey m6_7;
	@UiHandler("t6_7") void longOnT6_7(LongTapEvent ev) {
		if (m6_7 == null) {
			m6_7 = new Multikey();
			DataResource[] keys = { resources.vector_svg(), resources.matrix_svg(), resources.vectornotatie_svg(), resources.stelsel_svg()};
			@SuppressWarnings("unchecked")
			Consumer<FormuleEditorIF> actions[] = new Consumer[keys.length];
			if (isPremium()) {
				actions[0] = editor -> onVector(null);
				actions[1] = editor -> matrixDimensionDialog.showRelativeTo(t6_7);
				actions[2] = FormuleEditorIF::vectornotatie;
				actions[3] = editor -> stelselDimensionDialog.showRelativeTo(t6_7);
			} else {
				actions[0] = actions[1] = actions[2] = actions[3] = editor -> {};
			}
			m6_7.setKeys(keys, actions);
		}
		int px = t6_7.getAbsoluteLeft()-1*37;
		int py = t6_7.getAbsoluteTop();
		m6_7.setEditor(getEditor());
		if (hasLongTap) m6_7.show(px, py); else onVector(null);		
	}
}
