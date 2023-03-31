package nl.uu.fi.dwo.mobile.client;

import com.google.gwt.core.shared.GWT;
import nl.uu.fi.dwo.ideas.client.IdeasClient;

public class DWOIdeasClient extends IdeasClient {

	static native String casServer() /*-{
		return $wnd.casServer
	}-*/;
	
	
	private static int ENDPOINT = IdeasClient.DEFAULT;
	private static String BASE = "";

	static {
		try {
			String casServer = casServer();
			if(casServer != null && !casServer.isEmpty())
			{ ENDPOINT = IdeasClient.NONE;
			  BASE = casServer;
			}
		} catch (Exception e) { 
			GWT.log("ideas client", e);
		}
	}
	
	public DWOIdeasClient() {
		super(BASE, ENDPOINT);
	}
	
}
