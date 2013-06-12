package nl.uu.fi.dwo.mobile.touchtest;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartHandler;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;

import nl.uu.fi.dwo.mobile.client.ui.views.LoginView;

public class LoginViewTest implements LoginView {
	public class Handler implements TouchStartHandler {

		private int hits;

		@Override
		public void onTouchStart(TouchStartEvent event) {
			hits++;
			label.setText(hits + " hits");

		}

	}

	private Label label;
	private LayoutPanel panel;

	@Override
	public Widget asWidget() {
		return panel;
	}

	
	public void init() {
		panel = new LayoutPanel();
		Button btn = new Button("oops");
		label = new Label("hits");
		panel.add(label); panel.add(btn);
		panel.add(new Label("via LoginView"));
		btn.addTouchStartHandler(new Handler());
	}

	public LoginViewTest() {
		super();
		init();
	}


	@Override
	public void setupModule(Presenter presenter) {
		// TODO Auto-generated method stub
		
	}

	
}
