package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.resources.client.DataResource;
import com.google.gwt.uibinder.client.UiConstructor;

public class DKey extends FKey {

	public @UiConstructor DKey(DataResource resource) {
		super(resource);
	}

	@Override
	public void setHTML(String html) {
		if(html.isEmpty())
			image.setResource(DWOTabletKeyboardFactory.resources.empty());
		super.setHTML(html);
	}
}
