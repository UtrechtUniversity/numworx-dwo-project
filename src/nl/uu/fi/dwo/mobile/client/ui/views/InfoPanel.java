package nl.uu.fi.dwo.mobile.client.ui.views;

import nl.uu.fi.dwo.mobile.DWOplayer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimplePanel;

public class InfoPanel extends ResizeComposite {

	private static InfoPanelUiBinder uiBinder = GWT
			.create(InfoPanelUiBinder.class);

	interface InfoPanelUiBinder extends UiBinder<DockLayoutPanel, InfoPanel> {
	}

	private PopupPanel parent;
	public InfoPanel(PopupPanel popup) {
		DockLayoutPanel root;
		pfx = DWOplayer.PARAMETERS.getResource("");
		initWidget(root = uiBinder.createAndBindUi(this));
		root.forceLayout();
		parent = popup;
	}

	@UiField HasClickHandlers closeBtn;
	@UiField Label title;
	@UiField SimplePanel description;
	@UiField String pfx;
	
	@UiHandler("closeBtn")
	void close(ClickEvent e) {
		parent.hide();
	}
	
	void setName(String string) {
		title.setText(string);
	}
	
	void setDescription(IsWidget widget) {
		description.setWidget(widget);
	}
	
}
