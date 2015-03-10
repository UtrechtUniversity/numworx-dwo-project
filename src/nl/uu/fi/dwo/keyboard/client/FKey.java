package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.TouchEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHTML;

public class FKey extends Composite implements HasClickHandlers, HasHTML, MouseOverHandler, MouseOutHandler, ClickHandler {

	private static final String HOVER = "hover";
	HTML panel;
	
	public FKey() {
		panel = new HTML();
		initWidget(panel);
		setStyleName("kbd-Key");
		panel.addMouseOverHandler(this);
		panel.addMouseOutHandler(this);
		if(TouchEvent.isSupported())
		{
			panel.addClickHandler(this);
		}
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
		return panel.addClickHandler(handler);
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		panel.removeStyleName(HOVER);	
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		panel.addStyleName(HOVER);	
	}

	@Override
	public void onClick(ClickEvent event) {
		panel.removeStyleName(HOVER);
	}

}
