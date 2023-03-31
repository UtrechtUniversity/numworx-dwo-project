package nl.uu.fi.dwo.mobile.client.ui.views;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

public class ConfirmView extends Composite implements HasText {

	private static ConfirmViewUiBinder uiBinder = GWT.create(ConfirmViewUiBinder.class);

	interface ConfirmViewUiBinder extends UiBinder<Widget, ConfirmView> {
	}

	@Inject public ConfirmView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField InlineLabel message;
	@UiField Button ok, cancel;

	public ConfirmView(String message) {
		initWidget(uiBinder.createAndBindUi(this));
		this.message.setText(message);
	}

	public HasClickHandlers getConfirm() {
		return ok;
	}
	public HasClickHandlers getCancel() {
		return cancel;
	}
		
	public void setText(String text) {
		message.setText(text);
	}

	public String getText() {
		return message.getText();
	}

}
