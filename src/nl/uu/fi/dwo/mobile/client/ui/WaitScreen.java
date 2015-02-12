package nl.uu.fi.dwo.mobile.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class WaitScreen extends PopupPanel {

	private static WaitScreen _instance;
	
	public static WaitScreen instance() {
		if (_instance == null) _instance = new WaitScreen();
		return _instance;
	}
	
	
	private static WaitScreenUiBinder uiBinder = GWT
			.create(WaitScreenUiBinder.class);

	interface WaitScreenUiBinder extends UiBinder<Widget, WaitScreen> {
	}

	private WaitScreen() {
		super(false);
		setWidget(uiBinder.createAndBindUi(this));
		addStyleDependentName("WaitScreen");
	}

	
	public void w() {
		setPixelSize(Window.getClientWidth(), Window.getClientHeight());
		setPopupPositionAndShow(new PopupPanel.PositionCallback() {
	          public void setPosition(int offsetWidth, int offsetHeight) {
	            int left = 0; // (Window.getClientWidth() - offsetWidth) / 2;
	            int top = 0; //(Window.getClientHeight() - offsetHeight) / 3;
	            setPopupPosition(left, top);
	          }
	        });		
	}
	
	
	
}
