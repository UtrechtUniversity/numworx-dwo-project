/**
 * 
 */
package nl.uu.fi.dwo.keyboard.client;

import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author wim
 *
 */
class DuoKeys extends Composite {

	private static DuoKeysUiBinder uiBinder = GWT.create(DuoKeysUiBinder.class);

	interface DuoKeysUiBinder extends UiBinder<Widget, DuoKeys> {
	}

	@UiField FKey org,alt;
	@UiField KeyboardCSS style;
	private char orgChar, altChar;
	private int offset = 20;
	
	private FormuleEditorIF formuleEditor;
	private PopupPanel popup;
	
	private FormuleEditorIF getEditor() {
		return formuleEditor;
	}
	
	void setEditor(FormuleEditorIF editor) {
		formuleEditor = editor;
	}
	
	/**
	 * Because this class has a default constructor, it can
	 * be used as a binder template. In other words, it can be used in other
	 * *.ui.xml files as follows:
	 * &lt;ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
	 *   xmlns:g="urn:import:**user's package**"&gt;
	 *  &lt;g:**UserClassName**&gt;Hello!&lt;/g:**UserClassName&gt;
	 * &lt;/ui:UiBinder&gt;
	 * Note that depending on the widget that is used, it may be necessary to
	 * implement HasHTML instead of HasText.
	 */
	DuoKeys() {
		initWidget(uiBinder.createAndBindUi(this));
		popup = new PopupPanel(true);
		popup.setStyleName("duokeys");
		popup.setGlassEnabled(false);
		popup.setWidget(this);
	}
	
	DuoKeys(boolean small) {
	  this();
	  if(small)
	  {
	    top.addStyleName(style.small());
	    offset = 13;
	  }
	}
	
    @UiField FlowPanel top;
//	@UiHandler("top")
//	void onClickTop(MouseUpEvent e) { hideDuo(); }
	
	@UiHandler("alt")
	void onClickAlt(ClickEvent e) {
		getEditor().insert(altChar);
		hideDuo();
	}
	@UiHandler("org")
	void onClickOrg(ClickEvent e) {
		getEditor().insert(orgChar);
		hideDuo();
	}

	public char getOrg() {
		return orgChar;
	}

	public void setOrg(char orgChar) {
		this.orgChar = orgChar;
		String orgStr;
		if(orgChar == '<')
			orgStr = "&lt;";
		else 
			orgStr = String.valueOf(orgChar);
		org.setHTML("<p>"+orgStr+"</p>");
	}

	public char getAlt() {
		return altChar;
	}

	public void setAlt(char altChar) {
		this.altChar = altChar;
		alt.setHTML("<p>"+altChar+"</p>");
	}

	void showDuo(final int x, final int y) {
		org.onMouseOut(null);
		alt.onMouseOut(null);
		popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				popup.setPopupPosition(x-offset, y-offset);
			}
		});
	}
	
	void hideDuo() {
		popup.hide();
	}

	public boolean isDuoShown() {
		return popup.isShowing();
	}

}
