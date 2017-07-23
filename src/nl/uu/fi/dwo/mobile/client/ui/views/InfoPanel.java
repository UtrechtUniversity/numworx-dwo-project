package nl.uu.fi.dwo.mobile.client.ui.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;

public class InfoPanel extends ResizeComposite {

	private static InfoPanelUiBinder uiBinder = GWT
			.create(InfoPanelUiBinder.class);

	interface InfoPanelUiBinder extends UiBinder<DockLayoutPanel, InfoPanel> {
	}

	public InfoPanel() {
		DockLayoutPanel root;
		initWidget(root = uiBinder.createAndBindUi(this));
		root.forceLayout();
	}

}
