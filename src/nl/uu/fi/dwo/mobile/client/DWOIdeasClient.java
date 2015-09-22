package nl.uu.fi.dwo.mobile.client;

import com.google.gwt.user.client.Window;

import nl.uu.fi.dwo.ideas.client.IdeasClient;
import nl.uu.fi.dwo.mobile.DWOplayer;

public class DWOIdeasClient extends IdeasClient {

	public DWOIdeasClient() {
		super(Window.Location.getProtocol() + "//" + DWOplayer.PARAMETERS.getHost(), IdeasClient.DEFAULT);
	}
	
}
