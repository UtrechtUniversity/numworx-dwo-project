package nl.uu.fi.dwo.keyboard.client;

import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.Image;

import nl.uu.fi.dwo.keyboard.client.tap.HasLongTapHandlers;
import nl.uu.fi.dwo.keyboard.client.tap.LongTapEvent;
import nl.uu.fi.dwo.keyboard.client.tap.LongTapHandler;
import nl.uu.fi.dwo.keyboard.client.tap.TapEvent;
import nl.uu.fi.dwo.keyboard.client.tap.TapHandler;
import nl.uu.fi.dwo.keyboard.client.tap.TouchHandler;
import nl.uu.fi.dwo.keyboard.client.tap.TouchPanel;

public class TKey extends Composite implements HasHTML, HasClickHandlers, HasLongTapHandlers, MouseOverHandler, MouseOutHandler {

	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setText(String text) {
		// TODO Auto-generated method stub

	}
	boolean fire;
	Logger logger = Logger.getLogger("TKey");
	
	class PreventDefault implements TouchHandler  {

		@Override
		public void onTouchStart(TouchStartEvent event) {
			fine("TouchStart");
			fire = true;
			event.preventDefault();
		}

		@Override
		public void onTouchMove(TouchMoveEvent event) {
			fine("TouchMove");
			event.preventDefault();
		}

		@Override
		public void onTouchEnd(TouchEndEvent event) {
			fine("TouchEnd");
			event.preventDefault();
		}

		@Override
		public void onTouchCancel(TouchCancelEvent event) {
			fine("TouchCanceled");
			event.preventDefault();
		}
		
	}
	
	final PreventDefault nodefault = new PreventDefault();
	
	private ClickEvent click;

	private void fine(String cmd) {
		logger.fine(cmd  + " " + html);
	}
	
	@Override
	public HandlerRegistration addClickHandler(final ClickHandler handler) {
		if (false)
			return panel.addClickHandler(handler);
		
		
		return panel.addTapHandler(new TapHandler() {
			
			@Override
			public void onTap(TapEvent event) {
				fine("Tap " + fire);
				if(fire) handler.onClick(click);
			}
		});
	}

	private String html;
	@Override
	public String getHTML() {
		return html;
	}

	@Override
	public void setHTML(String html) {
		this.html = html;
		panel.clear();
		HTML w = new HTML(html);
		if(!TouchEvent.isSupported())
		{
			w.addMouseOverHandler(this);
			w.addMouseOutHandler(this);
		}
		panel.add(w);
		
		
	}

	private TouchPanel panel;
	
	@UiConstructor
	public TKey() {
		panel = new TouchPanel();
		initWidget(panel);
		click = new FKey.MyClickEvent(this);
		setStyleName("kbd-Key");
		panel.addTouchHandler(nodefault);
	}

	public TKey(DataResource resource) {
		this();
		panel.add(new Image(resource.getSafeUri()));
	}

	@Override
	public HandlerRegistration addLongTapHandler(final LongTapHandler handler) {
		return panel.addLongTapHandler(new LongTapHandler() {

			@Override
			public void onLongTap(LongTapEvent event) {
				fine("LongTap");
				fire = false;
				handler.onLongTap(event);
				
			}
			
		});
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		getWidget().addStyleName(FKey.HOVER);	
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		getWidget().removeStyleName(FKey.HOVER);	
	}

}
