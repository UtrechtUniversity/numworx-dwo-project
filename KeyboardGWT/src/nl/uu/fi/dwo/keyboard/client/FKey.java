package nl.uu.fi.dwo.keyboard.client;

import nl.uu.fi.dwo.interaction.client.keyboard.FocusOnTouch;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasAllTouchHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasMouseUpHandlers;
import com.google.gwt.event.dom.client.HasTouchEndHandlers;
import com.google.gwt.event.dom.client.HasTouchStartHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.Image;

public class FKey extends Composite implements HasClickHandlers, HasHTML, MouseOverHandler, MouseOutHandler, ClickHandler, HasTouchStartHandlers, HasTouchEndHandlers, TouchEndHandler, MouseUpHandler {

	static class MyClickEvent extends ClickEvent {

		public MyClickEvent(Object source) {
			super();
			setSource(source);
		}

	}

	static final String HOVER = "hover";
	
	static ClickHandler KEEP_FOCUS = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			FocusOnTouch.focus();
		}
	};
	
	
	HTML panel;
	Image image;
	HasClickHandlers click;
	HasAllTouchHandlers touches;
	HasMouseUpHandlers mouses;
	
	@UiConstructor
	public FKey() {
		panel = new HTML();
		click = panel;
		touches = panel;
		mouses = panel;
		initWidget(panel);
		setStyleName("kbd-Key");
		panel.addMouseOverHandler(this);
		panel.addMouseOutHandler(this);
		if(TouchEvent.isSupported())
		{
			panel.addClickHandler(this);
		} else
			panel.addClickHandler(KEEP_FOCUS);
	}

	private FKey(Image image) {
      panel = new HTML();
      this.image = image;
      click = image;
      touches = image;
      mouses = image;
      initWidget(image);
      image.addMouseOverHandler(this);
      image.addMouseOutHandler(this);
      if(TouchEvent.isSupported())
      {
          image.addClickHandler(this);
      } else
          image.addClickHandler(KEEP_FOCUS);
	  
	}
	
	FKey(ImageResource resource) {
		this(new Image(resource));
	}
	
	FKey(DataResource resource) {
      this(new Image(resource.getSafeUri()));
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

	private ClickHandler listener;
	private MyClickEvent event; 
	
	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		if(TouchEvent.isSupported())
		{   listener = handler;
			event = new MyClickEvent(touches);
			return 
//					new LegacyHandlerWrapper(HandlerRegistrations.compose(
//							touches.addTouchEndHandler(this),
//							mouses.addMouseUpHandler(this)
//					));
					mouses.addMouseUpHandler(this); // you always get mouseup events.
			
		}
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

	@Override
	public HandlerRegistration addTouchStartHandler(TouchStartHandler handler) {
		return touches.addTouchStartHandler(handler);
	}

	@Override
	public HandlerRegistration addTouchEndHandler(TouchEndHandler handler) {
		return touches.addTouchEndHandler(handler);
	}

	@Override
	public void onTouchEnd(TouchEndEvent event) {
		if(listener != null) {
			listener.onClick(this.event);
		}
		event.preventDefault();
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		if(listener != null) {
			listener.onClick(this.event);
		}
		event.preventDefault();
		
	}

}
