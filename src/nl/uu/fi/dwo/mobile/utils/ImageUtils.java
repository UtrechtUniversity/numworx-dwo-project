package nl.uu.fi.dwo.mobile.utils;

import com.google.gwt.user.client.ui.Image;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.DWOplayerParameters;

public class ImageUtils {

	private ImageUtils() {}
	
//	@Deprecated
//	public static Image newImage(String resource) {
//		if(resource.startsWith("image"))
//			resource = DWOplayer.PARAMETERS.getResource(resource);
//		return new Image(resource);
//	}


	public static Image newImage(String resource, DWOplayerParameters parameters) {
		if(resource.startsWith("image"))
			resource = parameters.getResource(resource);
		return new Image(resource);
	}

}
