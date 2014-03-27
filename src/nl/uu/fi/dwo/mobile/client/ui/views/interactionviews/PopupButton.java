package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.HashMap;
import java.util.Map;

import nl.uu.fi.dwo.interaction.client.InteractionView;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ButtonBase;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DialogBox.Caption;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;

public class PopupButton extends Composite implements ClickHandler {

	ButtonBase btn;
	IsWidget content;
	DialogBox box;
	InteractionView view;
	HashMap<String,Object> state;
	
	public PopupButton(IsWidget content) {
		this(content, new Image("images/resources/appletknop.gif"), null);
	}
	
	public PopupButton(StubView view) {
		this(view.getWidget(),new Image("images/resources/appletknop.gif"), view);
	}

	public PopupButton(IsWidget content, Image image, InteractionView view) {
		Image img = image;
		btn = new PushButton(img);
		btn.getElement().getStyle().setPadding(0, Style.Unit.PX);
		btn.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		btn.addClickHandler(this);
		this.content = content;
		this.view = view;
		initWidget(btn);
	}

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
		box.show();		
	}

	public void hide() {
		if(box != null) box.hide();
	}


}
