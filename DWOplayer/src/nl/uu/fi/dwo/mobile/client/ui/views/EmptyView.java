package nl.uu.fi.dwo.mobile.client.ui.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;

public class EmptyView extends Composite {

	interface EmptyViewUiBinder extends UiBinder<Label, EmptyView> {
	}
	private static EmptyViewUiBinder uiBinder = GWT.create(EmptyViewUiBinder.class);

	private Label root;
	
	private void init() {
		root = uiBinder.createAndBindUi(this);
		initWidget(root);
	}
	
	
	public EmptyView() {
		init();
	}

	public EmptyView(String text) {
		init();
		root.setText(text);
	}



}
