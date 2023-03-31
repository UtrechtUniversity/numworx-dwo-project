package nl.uu.fi.dwo.mobile.utils;

import com.google.gwt.user.client.ui.PopupPanel;

public class AutoHidePopupPanel extends PopupPanel implements HasHide {

	public AutoHidePopupPanel() {
	}

	public AutoHidePopupPanel(boolean autoHide) {
		super(autoHide);
	}

	public AutoHidePopupPanel(boolean autoHide, boolean modal) {
		super(autoHide, modal);
	}

}
