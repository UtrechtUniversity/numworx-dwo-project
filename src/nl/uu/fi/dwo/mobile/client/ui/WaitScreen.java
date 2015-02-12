package nl.uu.fi.dwo.mobile.client.ui;

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

public class WaitScreen extends PopupPanel {

	private static WaitScreenUiBinder uiBinder = GWT
			.create(WaitScreenUiBinder.class);

	interface WaitScreenUiBinder extends UiBinder<Widget, WaitScreen> {
	}

	public WaitScreen() {
		super(false);
		setWidget(uiBinder.createAndBindUi(this));
		getElement().getStyle().setOpacity(0.5);
	}

	
	public void w() {
		setPixelSize(Window.getClientWidth(), Window.getClientHeight());
		setPopupPositionAndShow(new PopupPanel.PositionCallback() {
	          public void setPosition(int offsetWidth, int offsetHeight) {
	            int left = (Window.getClientWidth() - offsetWidth) / 2;
	            int top = (Window.getClientHeight() - offsetHeight) / 3;
	            setPopupPosition(left, top);
	          }
	        });		
	}
	
	
	
}
