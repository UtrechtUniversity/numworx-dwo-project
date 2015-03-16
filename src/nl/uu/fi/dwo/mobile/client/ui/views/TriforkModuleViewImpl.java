package nl.uu.fi.dwo.mobile.client.ui.views;

import com.google.gwt.user.client.Window;

public class TriforkModuleViewImpl extends ViewModuleViewImpl implements
		ViewModuleView {

	public TriforkModuleViewImpl() {
		super(false);
	}

	public void zetMaat() {
		int contentHeight = Window.getClientHeight() - extraHeight;
		Window.addResizeHandler(new Resizer());
		sb.zetMaat();
		sb.setScrollPanel(contentScrollPanel, contentHeight);	
	}

}
