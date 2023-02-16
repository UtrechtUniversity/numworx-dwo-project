package nl.uu.fi.dwo.lms.chatgwt.util;

public class Notification {
	
	public static Notification INSTANCE = new Notification();
	private String last = "";

	Notification() {
	}
	
	private native void send0(String cmd) /*-{
			var domain = $wnd.location.protocol + "//" + $wnd.location.host;
			var iframe = $wnd.parent;
			iframe.postMessage(cmd, domain);
	}-*/;

	public void send(String cmd) {
		this.last  = cmd;
		send0(cmd);
	}
	
	public String getLast() {
		return last;
	}

}
