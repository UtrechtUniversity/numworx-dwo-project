package nl.uu.fi.dwo.mobile.client.ui;

import javax.ws.rs.core.Link;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window.Location;

public enum Actions {
  showMainNav, hideMainNav, isMainNavVisible, RESULTS, KNOWLEDGE, PERSONS, SCHOOLCLASSES, ORGANISATION, ARROWUP, TRAIL, LOGOUT, EXAM, MAYBELOGOUT, CLOSING, LOGINNEEDED, INITED, RETOUR;
  private String command;

  Actions() {
    this.command = name();
  }

  private static native void sendParent(String cmd) /*-{
		var domain = $wnd.location.protocol + "//" + $wnd.location.host;
		var iframe = $wnd.parent;
		iframe.postMessage(cmd, domain);
  }-*/;

  public void execute() {
	java.util.logging.Logger.getLogger(Actions.class.getName()).fine("execute " + command);
    sendParent(command);
  }

  public String getCommand() {
    return command;
  }

  /**
   * For TRAIL, add arguments.
   * jsonarray of jsonobject("title", "message")
   * @param extra argument
   */
  public void execute(String extra) {
    java.util.logging.Logger.getLogger(Actions.class.getName()).fine("execute " + command);
    String command;
    if (extra == null || extra.isEmpty())
      command = name();
    else 
      command = name() + ":" + extra;
    sendParent(command);
  }
  
  /**
   * true if embedded in gwtclient.
   * header is gehalveerd.
   * @return
   */
  public static boolean isAvailable() {
    return "none".equals(Location.getParameter("header"));
  }
  
  
  public static class Handler implements ClickHandler {
		
	private String link;

	@Override
	public void onClick(ClickEvent event) {
		event.stopPropagation();
		event.preventDefault();
		goTo(link);		
	}
	public Handler(String href) {
		this.link = href;
	}
  }

public static final String PROTO = "action:";
private static final int ACTION_PROTO_LENGTH = PROTO.length();
  
public static void goTo(String link) {
	if (Actions.isAvailable() && link.startsWith(PROTO)) {
		sendParent(link.substring(ACTION_PROTO_LENGTH));
	}
}

}
