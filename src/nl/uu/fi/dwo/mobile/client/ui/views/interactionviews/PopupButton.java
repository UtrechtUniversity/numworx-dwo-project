package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.HashMap;
import java.util.logging.Logger;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.mobile.DWOplayer;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ButtonBase;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Touch;

public class PopupButton extends Composite implements ClickHandler, TouchStartHandler, MouseDownHandler {

	ButtonBase btn;
	IsWidget content;
	DialogBox box;
	InteractionView view;
	HashMap<String,Object> state;
	
	public PopupButton(IsWidget content) {
		this(content, new Image(DWOplayer.DWO_BUNDLE.appletknop()), null);
	}
	
	public PopupButton(StubView view) {
		this(view.getWidget(),new Image(DWOplayer.DWO_BUNDLE.appletknop()), view);
	}

	public PopupButton(IsWidget content, Image image, InteractionView view) {
		Image img = image;
		btn = new PushButton(img);
		btn.getElement().getStyle().setPadding(0, Style.Unit.PX);
		btn.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		btn.addClickHandler(this);
		btn.addTouchStartHandler(this);
		btn.addMouseDownHandler(this);
		this.content = content;
		this.view = view;
		initWidget(btn);
	}

	private int clientX,clientY;
	private Logger logger = Logger.getLogger("PopupButton");
	@Override
	public void onClick(ClickEvent event) {
		if(box == null) {
			box = new DialogBox(false,false);			
			VerticalPanel p = new VerticalPanel();
			Button closeBtn = new Button("[x]");
			closeBtn.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if(view != null)
						state = view.getState();
					box.hide();
				}});
			p.add(closeBtn);
			p.add(content);
			box.setWidget(p);
		}
		if(!box.isShowing() && view != null && state != null)
			view.setState(state);
		if(!box.isShowing() )
				box.showRelativeTo(this);		
	}

	public void hide() {
		if(box != null) box.hide();
	}

	@Override
	public void onTouchStart(TouchStartEvent event) {
		Touch touch = event.getChangedTouches().get(0);
		clientX = touch.getClientX();
		clientY = touch.getClientY();
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		clientX = event.getClientX();
		clientY = event.getClientY();
	}

	
	

}
