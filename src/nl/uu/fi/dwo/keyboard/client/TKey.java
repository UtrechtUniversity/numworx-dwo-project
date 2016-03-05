package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHTML;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.dom.client.recognizer.longtap.HasLongTapHandlers;
import com.googlecode.mgwt.dom.client.recognizer.longtap.LongTapHandler;
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;

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

	
	private ClickEvent click;

	@Override
	public HandlerRegistration addClickHandler(final ClickHandler handler) {
		return panel.addTapHandler(new TapHandler() {
			
			@Override
			public void onTap(TapEvent event) {
				handler.onClick(click);
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
		w.addMouseOverHandler(this);
		w.addMouseOutHandler(this);
		panel.add(w);
	}

	private TouchPanel panel;
	
	@UiConstructor
	public TKey() {
		panel = new TouchPanel();
		initWidget(panel);
		click = new FKey.MyClickEvent(this);
		setStyleName("kbd-Key");
	}

	@Override
	public HandlerRegistration addLongTapHandler(LongTapHandler handler) {
		return panel.addLongTapHandler(handler);
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
