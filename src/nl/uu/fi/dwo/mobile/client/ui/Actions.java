package nl.uu.fi.dwo.mobile.client.ui;

import com.google.gwt.user.client.Window.Location;

public enum Actions {
  showMainNav, hideMainNav, isMainNavVisible;
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
    sendParent(command);
  }

  public String getCommand() {
    return command;
  }

  public static boolean isAvailable() {
    return "none".equals(Location.getParameter("header"));
  }
}
