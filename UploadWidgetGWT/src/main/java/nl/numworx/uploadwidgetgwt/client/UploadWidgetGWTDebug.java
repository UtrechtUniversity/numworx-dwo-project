package nl.numworx.uploadwidgetgwt.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class UploadWidgetGWTDebug extends UploadWidgetGWT {

	@Override
	public void onModuleLoad() {
		int width = 300;
		int height = 100;
		
		Map<String, Object> launchdata = new HashMap<>();
		
		
		init(width, height, launchdata, Collections.emptyMap());
		
		Widget w = asWidget();
		
		RootLayoutPanel root = RootLayoutPanel.get();
		root.add(w);
		root.setWidgetTopHeight(w, 0, Unit.PX, height, Unit.PX);
		root.setWidgetLeftWidth(w, 0, Unit.PX, width, Unit.PX);
		
	}

}
