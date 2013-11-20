package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DialogBox.Caption;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PopupButton extends Composite implements ClickHandler {

	Button btn = new Button();
	IsWidget content;
	DialogBox box;
	
	public PopupButton(IsWidget content) {
		btn.addClickHandler(this);
		btn.setText("Btn");
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


}
