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
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.Image;

public class FKey extends Composite implements HasClickHandlers, HasHTML, MouseOverHandler, MouseOutHandler, ClickHandler {

	private static final String HOVER = "hover";
	HTML panel;
	Image image;
	HasClickHandlers click;
	
	@UiConstructor
	public FKey() {
		panel = new HTML();
		click = panel;
		initWidget(panel);
		setStyleName("kbd-Key");
		panel.addMouseOverHandler(this);
		panel.addMouseOutHandler(this);
		if(TouchEvent.isSupported())
		{
			panel.addClickHandler(this);
		}
	}

	FKey(ImageResource resource) {
		panel = new HTML();
		image = new Image(resource);
		click = image;
		initWidget(image);
		image.addMouseOverHandler(this);
		image.addMouseOutHandler(this);
		if(TouchEvent.isSupported())
		{
			image.addClickHandler(this);
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
		return click.addClickHandler(handler);
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		getWidget().removeStyleName(HOVER);	
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		getWidget().addStyleName(HOVER);	
	}

	@Override
	public void onClick(ClickEvent event) {
		getWidget().removeStyleName(HOVER);
	}

}
