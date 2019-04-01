package nl.uu.fi.dwo.mobile;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public class WidgetPlayer implements EntryPoint {

	@Override
	public void onModuleLoad() {
		String id = Window.Location.getParameter("id");
		RootPanel.get().add(new Label("Work in Progress id="+id));
	}

}
