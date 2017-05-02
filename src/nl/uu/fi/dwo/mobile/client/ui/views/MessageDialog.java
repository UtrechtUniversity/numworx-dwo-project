package nl.uu.fi.dwo.mobile.client.ui.views;

import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Float;

import fi.wiskopdr.text.Text;

public class MessageDialog {

	private class Handler implements ClickHandler {
		Integer value;
		
		Handler(Integer value) {
			this.value = value;
		}
		
		@Override
		public void onClick(ClickEvent event) {
			box.hide();
			defer.resolve(value);
		}
	}


	public static final int CANCEL = 0;
	public static final int OK = 1;
	public static final int YES = 1;
	public static final int NO = 0;
	
	private Deferred<Integer> defer;
	private DialogBox box;
	private VerticalPanel contents;
	private FlowPanel buttons;
	
	public MessageDialog() {
		box = new DialogBox(true, true);
		box.addStyleName("messageDialog");
		box.getElement().getStyle().setZIndex(10);
		buttons = new FlowPanel();
		buttons.setStylePrimaryName("buttons");
	    box.addCloseHandler(new CloseHandler<PopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if(event.isAutoClosed()) 
					defer.resolve(CANCEL);				
			}
		});
	    contents = new VerticalPanel();
	}
	
	public void addNo() {
		Button cancel = new Button(Text.constants.neeTekst());
		cancel.addStyleName("no");
		cancel.addClickHandler(new Handler(NO));
	    cancel.getElement().getStyle().setFloat(Float.RIGHT);
	    cancel.getElement().getStyle().setPaddingRight(20, Style.Unit.PX);
		buttons.add(cancel);
	}

	public void addYes() {
		Button ok = new Button(Text.constants.jaTekst());
		ok.addStyleName("yes");
		ok.addClickHandler(new Handler(YES));
		ok.getElement().getStyle().setPaddingLeft(20, Style.Unit.PX);
		buttons.add(ok);
	}

	public void addOk() {
		Button ok = new Button(nl.uu.fi.dwo.mobile.client.text.Text.constants.ok());
		ok.addStyleName("ok");
		ok.addClickHandler(new Handler(OK));
		buttons.add(ok);
		
	}
	
	public void addLine(IsWidget line) {
		contents.add(line);
	}

	public Promise<Integer> showDialog() {
		buttons.removeFromParent();
		defer = new Deferred<Integer>();
		contents.add(buttons);
	    box.setWidget(contents);
	    box.center();
	    return defer.getPromise();
	}
	
	public static Promise<Integer> alert(String message) {
		MessageDialog dlg = new MessageDialog();
		dlg.addLine(new Label(message)); // ????
		dlg.addOk();
		return dlg.showDialog();
	}
	
}
