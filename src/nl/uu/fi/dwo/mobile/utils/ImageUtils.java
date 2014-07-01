package nl.uu.fi.dwo.mobile.utils;

import com.google.gwt.user.client.ui.Image;

import nl.uu.fi.dwo.mobile.DWOplayer;

public class ImageUtils {

	private ImageUtils() {}
	
	
	public static Image newImage(String resource) {
		if(resource.startsWith("image"))
			resource = DWOplayer.PARAMETERS.getResource(resource);
		return new Image(resource);
	}

}
