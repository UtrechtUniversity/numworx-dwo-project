package nl.uu.fi.dwo.mobile.utils;

import com.google.gwt.user.client.ui.Image;

import nl.uu.fi.dwo.mobile.DWOplayer;

public class ImageUtils {

	private ImageUtils() {}
	
	
	public static Image newImage(String resource) {
		return new Image(DWOplayer.PARAMETERS.getResource(resource));
	}

}
