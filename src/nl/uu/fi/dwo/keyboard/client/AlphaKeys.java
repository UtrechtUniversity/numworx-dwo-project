package nl.uu.fi.dwo.keyboard.client;

import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class AlphaKeys extends Composite {

	private static AlphaKeysUiBinder uiBinder = GWT
			.create(AlphaKeysUiBinder.class);

	interface AlphaKeysUiBinder extends UiBinder<Widget, AlphaKeys> {
	}

	private FormuleEditorIF formuleEditor;
	private PopupPanel popup;
	
	private FormuleEditorIF getEditor() {
		return formuleEditor;
	}
	
	void setEditor(FormuleEditorIF editor) {
		formuleEditor = editor;
	}

	public AlphaKeys() {
		initWidget(uiBinder.createAndBindUi(this));
		popup = new PopupPanel(false, false);
		popup.setStyleName("alphakeys");
		popup.setGlassEnabled(false);
		popup.setWidget(this);
	}
	
	void showAlfa(final int x, final int y) {
		popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				popup.setPopupPosition(x, y);
			}
		});
	}
	
	void hideAlpha() {
		popup.hide();
	}
	
	void hideOnClick() {
	 if ( false ) 			// Op verzoek van Nathalie de Weerd van Noordhoff
		hideAlpha();
	}
	
	boolean isAlphaShown() {
		return popup.isShowing();
	}
	
	@UiField
	FKey c1,c2,c3,c4,c5,c6;

	public AlphaKeys(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
	}
	@UiHandler("c1")
	void onC1(ClickEvent e) {
		hideOnClick();
		getEditor().insert('α');
	}

	@UiHandler("c2")
	void onC2(ClickEvent e) {
		hideOnClick();
		getEditor().insert('β');
	}
	@UiHandler("c3")
	void onC3(ClickEvent e) {
		hideOnClick();
		getEditor().insert('γ');
	}
	@UiHandler("c4")
	void onC4(ClickEvent e) {
		hideOnClick();
		getEditor().insert('λ');
	}

	@UiHandler("c5")
	void onC5(ClickEvent e) {
		hideOnClick();
		getEditor().insert('μ');
	}
	@UiHandler("c6")
	void onC6(ClickEvent e) {
		hideOnClick();
		getEditor().insert('σ');
	}

}
