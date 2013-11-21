package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

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

public class PopupButton extends Composite implements ClickHandler {

	ButtonBase btn;
	IsWidget content;
	DialogBox box;
	
	public PopupButton(IsWidget content) {
		this(content, new Image("images/resources/appletknop.gif"));
	}

	public PopupButton(IsWidget content, Image image) {
		Image img = image;
		btn = new PushButton(img);
		btn.addClickHandler(this);
		this.content = content;
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
					box.hide();
				}});
			p.add(closeBtn);
			p.add(content);
			box.setWidget(p);
		}	
		box.show();		
	}

	public void hide() {
		if(box != null) box.hide();
	}


}
