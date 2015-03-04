package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.SimplePanel;

public class FKey extends Composite implements HasClickHandlers, HasHTML {

	HTML panel;
	
	public FKey() {
		panel = new HTML();
		initWidget(panel);
	}

	@Override
	public String getText() {
		return panel.getText();
	}

	@Override
	public void setText(String text) {		
		panel.setText(text);
	}

	@Override
	public String getHTML() {
		return panel.getHTML();
	}

	@Override
	public void setHTML(String html) {
		panel.setHTML(html);
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		//return addDomHandler(new MouseHandler(handler, this), MouseDownEvent.getType());
		return panel.addClickHandler(handler);
	}

}
