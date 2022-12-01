package nl.uu.fi.dwo.lms.chatgwt.util;

public class Notification {
	
	public static Notification INSTANCE = new Notification();

	Notification() {
		
	}
	
	public native void send(String cmd) /*-{
			var domain = $wnd.location.protocol + "//" + $wnd.location.host;
			var iframe = $wnd.parent;
			iframe.postMessage(cmd, domain);
	}-*/;

}
