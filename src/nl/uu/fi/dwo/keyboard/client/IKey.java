package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiConstructor;

public class IKey extends FKey {

	public @UiConstructor IKey(ImageResource resource) {
		super(resource);
	}

	@Override
	public void setHTML(String html) {
		if(html.isEmpty())
			image.setResource(DWOTabletKeyboardFactory.resources.empty());
		super.setHTML(html);
	}
}
