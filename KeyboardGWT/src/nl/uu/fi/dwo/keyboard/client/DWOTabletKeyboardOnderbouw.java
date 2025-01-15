/**
 * 
 */
package nl.uu.fi.dwo.keyboard.client;

import java.util.function.Consumer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.client.constants.NumberConstants;
import com.google.gwt.resources.client.DataResource;
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

import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.keyboard.client.AbstractKeyboard.HasHeight;
import nl.uu.fi.dwo.keyboard.client.tap.LongTapEvent;

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
	@UiField DWOkeyboardBundle resources;
	@UiField(provided=true)
	fi.wiskopdr.text.TextConstants rb = fi.wiskopdr.text.Text.constants;
	@UiField(provided=true)
	NumberConstants nc;
	private HasHeight scroll;
	private int extra;
	private boolean hasLongTap;
	
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
		LocaleInfo currentLocale = LocaleInfo.getCurrentLocale();
		nc = currentLocale.getNumberConstants();
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
		hasLongTap = width < ResponsiveCSS.SMALL;
		root.setStyleName(style.extrasmall(), extra);
		int old = HEIGHT;
		if (extra) {
			HEIGHT = 4*37+15;
		} else {
			HEIGHT = 3*37+15;
		}
		if (old != HEIGHT) {
			setPixelSize(-1, HEIGHT);
			scroll.setHeight(this.extra-HEIGHT);
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
	
	@UiHandler({"t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7", "t8", "t9"	} )
	void insert(ClickEvent e) {
		doInsert(e);
	}
	@UiHandler({"ty", "tp", "tq", "ta", "tb", "tpi"} )
	void insertx(ClickEvent e) {
		doInsert(e);
	}
	@UiHandler("tx") void onTX(ClickEvent e) {
		getEditor().insert('x');
	}
	
	
	@UiHandler("t7_1") void inserlt(ClickEvent e) { getEditor().insert('<');}
	@UiHandler("t7_2") void insergt(ClickEvent e) { getEditor().insert('>');}
	@UiHandler("t2_2") void insertdec(ClickEvent e) { doInsert(e); } // decimal comma/point
	@UiHandler("t3_1") void insertstar(ClickEvent e) { getEditor().insert('*');}
	@UiHandler("t3_4") void insertmin(ClickEvent e) { getEditor().insert('-'); }
	@UiHandler({"t2_3", "t3_2", "t3_3", "t4_2", "t5_8", "t5_9"} )
	void insert0(ClickEvent e) {
		doInsert(e);
	}	
	@UiHandler({ "t7_3", "t7_4", "t7_5","t8_1", "t8_2","t9_1", "t9_2"} )
	void inserty(ClickEvent e) {
		doInsert(e);
	}
	@UiHandler( "t10_4") void insertopen(ClickEvent e) { getEditor().insert('〈');}
	@UiHandler( {"t10_2", "t10_3", "t10_1", "t10_5"} )
	void insertz(ClickEvent e) {
		doInsert(e);
	}
	@UiHandler("t10_6") void insertarrow(ClickEvent e) { getEditor().insert('→'); }
	
	@UiHandler("t11_1") void onT11_1(ClickEvent e) {getEditor().cursorToLeft(); }
	@UiHandler("t11_2") void onT11_2(ClickEvent e) {getEditor().cursorToRight();}

	@UiHandler("t5_1") void onT3_1(ClickEvent e) {getEditor().wortel();}
	@UiHandler({"t5_2","t5_2a"}) void onT3_2(ClickEvent e) {getEditor().macht();}
	@UiHandler("t5_3") void onT3_3(ClickEvent e) {getEditor().kwadraat();}
	@UiHandler("t5_4") void onT3_4(ClickEvent e) {getEditor().breuk();}
	@UiHandler("t5_5") void onT3_5(ClickEvent e) {getEditor().haakjes();}
	@UiHandler("t5_6") void onT3_6(ClickEvent e) {getEditor().ndewortel();}
	@UiHandler("t5_7") void onT3_11(ClickEvent e) {getEditor().subscript();}
	@UiHandler("t4_1") void onT4_14(ClickEvent e) {getEditor().insert(" "+rb.ofLabel()+" ");}

	@UiHandler("t12_1") void onT1_16(ClickEvent e) {switchGreek();}
	@UiHandler("t11_3") void onT1_17(ClickEvent e) {backspace();}

	@UiHandler("t12_2") void onT2_16(ClickEvent e) {switch123();}
	@UiHandler("t13_1") void onT2_17(ClickEvent e) {enter();}

	@UiHandler("t12_3") void onT3_16(ClickEvent e) {switchABC();}

	@UiHandler("t12_4") void onT4_16(ClickEvent e) {switchHand();}
	@UiHandler("t13_2") void onT4_17(ClickEvent e) {blur();}

	@Override
	public void blur() {
		getDelegate().blur();
	}
	@Override
	void switchABC() {
		getDelegate().switchABC();
	}
	@Override
	void switch123() {
//		getDelegate().switch123();
	}
	@Override
	void switchHand() {
		getDelegate().switchHand();
	}
	@Override
	void switchGreek() {
		getDelegate().switchGreek();
	}
	
	@UiField DKey t13_1;
	void setEnterImage(DataResource resource) {
	    t13_1.image.setUrl(resource.getSafeUri());
	}
	@UiField TKey tx;
	private Multikey mx;
// of oncontext?
	@UiHandler("tx") void longOnTX(LongTapEvent ev) {
		if (mx == null) {
			mx = new Multikey();
			mx.setKeys('x','y','p','q','a','b');
			mx.setVarStyle();
		}
		int px = tx.getAbsoluteLeft();
		int py = tx.getAbsoluteTop();
		mx.setEditor(getEditor());
		if (hasLongTap) mx.show(px, py); else onTX(null);
	}

	@UiField TKey t7_1;
	private Multikey m7_1;
	@UiHandler("t7_1") void longOnLT(LongTapEvent ev) {
		GWT.log("long tap on '<'");
		if (m7_1 == null) {
			m7_1 = new Multikey();
			m7_1.setKeys('<', '>', '≤', '≥');
		}
		int px = t7_1.getAbsoluteLeft();
		int py = t7_1.getAbsoluteTop();
		m7_1.setEditor(getEditor());
		if (hasLongTap) m7_1.show(px, py); else inserlt(null);
	}
	
	@UiField TKey t10_4;
	private Multikey m10_4;
	@UiHandler("t10_4") void longOnT10_4(LongTapEvent ev) {
		GWT.log("long tap on '〈'");
		if (m10_4 == null) {
			m10_4 = new Multikey();
			m10_4.setAltStyle();
			m10_4.setKeys('〈', '〉', '[', ']');
		}
		int px = t10_4.getAbsoluteLeft();
		int py = t10_4.getAbsoluteTop();
		m10_4.setEditor(getEditor());
		if (hasLongTap) m10_4.show(px, py); else insertopen(null);
	}
	
	@UiField TKey t10_6;
	private Multikey m10_6;
	@UiHandler("t10_6") void longOnT10_6(LongTapEvent ev) {
		GWT.log("long tap on '→'");
		if (m10_6 == null) {
			m10_6 = new Multikey();
			m10_6.setAltStyle();
			m10_6.setKeys('→', '←');
		}
		int px = t10_6.getAbsoluteLeft();
		int py = t10_6.getAbsoluteTop();
		m10_6.setEditor(getEditor());
		if (hasLongTap) m10_6.show(px, py); else insertarrow(null);
	}

	@UiField TKey t5_4;
	private Multikey m5_4;
	@SuppressWarnings("unchecked")
	@UiHandler("t5_4") void longOnT5_4(LongTapEvent ev) {
		GWT.log("long tap on breuk");
		if (m5_4 == null) {
			m5_4 = new Multikey();
			DataResource[] keys = { resources.breuk_svg(), resources.wortel_svg(), resources.macht_svg(), resources.kwadraat_svg(), resources.haakjes_svg(), resources.ndewortel_svg(), resources.subscript_svg() };
			Consumer<FormuleEditorIF> actions[] = new Consumer[keys.length];
			actions[0] = FormuleEditorIF::breuk;
			actions[1] = FormuleEditorIF::wortel;
			actions[2] = FormuleEditorIF::macht;
			actions[3] = FormuleEditorIF::kwadraat;
			actions[4] = FormuleEditorIF::haakjes;
			actions[5] = FormuleEditorIF::ndewortel;
			actions[6] = FormuleEditorIF::subscript;
			m5_4.setKeys(keys, actions);
		}
		int px = t5_4.getAbsoluteLeft();
		int py = t5_4.getAbsoluteTop();
		m5_4.setEditor(getEditor());
		if (hasLongTap) m5_4.show(px, py); else onT3_4(null);
		
	}
}
