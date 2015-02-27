/**
 * 
 */
package nl.uu.fi.dwo.keyboard.client;

import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author wim
 *
 */
public class DesktopKeyboard extends AbstractKeyboard {

	@UiField ResourceBase res = ResourceBase.INSTANCE;
	
	
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

	@UiHandler("c1")
	void onC1(ClickEvent e) {
		getEditor().wortel();
	}

	@UiHandler("c2")
	void onC2(ClickEvent e) {
		getEditor().macht();
	}
	@UiHandler("c3")
	void onC3(ClickEvent e) {
		getEditor().kwadraat();
	}
	@UiHandler("c4")
	void onC4(ClickEvent e) {
		getEditor().breuk();
	}

	@UiHandler("c5")
	void onC5(ClickEvent e) {
		getEditor().haakjes();
	}
	@UiHandler("c6")
	void onC6(ClickEvent e) {
		getEditor().ndewortel();
	}

	@UiHandler("c7")
	void onC7(ClickEvent e) {
		getEditor().integraal();;
	}

	@UiHandler("c8")
	void onC8(ClickEvent e) {
		getEditor().prv();
	}
	@UiHandler("c9")
	void onC9(ClickEvent e) {
		getEditor().ndelog();
	}
	@UiHandler("c10")
	void onC10(ClickEvent e) {
		getEditor().abs();
	}

	@UiHandler("c11")
	void onC11(ClickEvent e) {
		getEditor().subscript();
	}
	@UiHandler("c12")
	void onC12(ClickEvent e) {
		getEditor().bin() ;
	}
	@UiHandler("c13")
	void onC13(ClickEvent e) {
		getEditor().insert('[');
	}
	@UiHandler("c14")
	void onC14(ClickEvent e) {
		getEditor().insert(']');
	}

	@UiHandler("c15")
	void onC15(ClickEvent e) {
		getEditor().insert('(');
	}
	@UiHandler("c16")
	void onC16(ClickEvent e) {
		getEditor().insert(')');
	}

	@UiHandler("c17")
	void onC17(ClickEvent e) {
		getEditor().insert('\u2190'); // TODO u code
	}

	@UiHandler("c18")
	void onC18(ClickEvent e) {
		getEditor().insert('\u2192'); // TODO u code
	}
	@UiHandler("c19")
	void onC19(ClickEvent e) {
		getEditor().insert(" of ");
	}
	@UiHandler("c20")
	void onC20(ClickEvent e) {
		getEditor().insert("\u2227");
	}

	@UiHandler("c21")
	void onC21(ClickEvent e) {
		getEditor().insert('\u2248');
	}
	@UiHandler("c22")
	void onC22(ClickEvent e) {
		getEditor().insert('\u2264');
	}
	@UiHandler("c23")
	void onC23(ClickEvent e) {
		getEditor().insert('\u2265');
	}
	@UiHandler("c24")
	void onC24(ClickEvent e) {
		getEditor().insert("ln");
	}

	@UiHandler("c25")
	void onC25(ClickEvent e) {
		getEditor().primitieve();
	}
	@UiHandler("c26")
	void onC26(ClickEvent e) {
		getEditor().ndelog();
	}

	@UiHandler("c27")
	void onC27(ClickEvent e) {
		getEditor().limiet0(); // TODO u code
	}

	@UiHandler("c28")
	void onC28(ClickEvent e) {
		getEditor().limiet2(); // TODO u code
	}
	@UiHandler("c29")
	void onC29(ClickEvent e) {
		getEditor().limiet1();
	}
	@UiHandler("c30")
	void onC30(ClickEvent e) {
		getEditor().diff();
	}

	@UiHandler("c31")
	void onC31(ClickEvent e) {
		getEditor().insert('∞');
	}

	private int getKeyboardHeight() {
		return 44;
	}

	private Widget scrollPanel; 
	private int origHeight = 426 - 40;
	private int origDelta = 0;
	
	void resizeScrollPanel(int size) {
		origDelta = size;
		if(scrollPanel != null)
			scrollPanel.setPixelSize(-1, origHeight - size);
	}
	public void setScrollPanel(Widget w, int h) {
		scrollPanel = w;
		origHeight = h;
		if(scrollPanel != null) scrollPanel.setPixelSize(-1, origHeight - origDelta);
	}

	@Override
	public void focus() {
		resizeScrollPanel(getKeyboardHeight());
		super.focus();
	}

	@Override
	public void blur() {
		resizeScrollPanel(0);
		super.blur();
	}

}
