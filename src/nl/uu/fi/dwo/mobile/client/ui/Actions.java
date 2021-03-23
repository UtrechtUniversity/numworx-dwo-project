package nl.uu.fi.dwo.mobile.client.ui;

import com.google.gwt.user.client.Window.Location;

public enum Actions {
  showMainNav, hideMainNav, isMainNavVisible, RESULTS, KNOWLEDGE, PERSONS, SCHOOLCLASSES, ORGANISATION, ARROWUP, TRAIL, LOGOUT, EXAM, MAYBELOGOUT, CLOSING;
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
}
