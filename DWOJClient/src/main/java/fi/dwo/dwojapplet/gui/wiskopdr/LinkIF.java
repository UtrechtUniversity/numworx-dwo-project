package fi.dwo.dwojapplet.gui.wiskopdr;

import java.applet.AppletContext;

public interface LinkIF {

	Object getJSObject();

	boolean gotoScoNr(String rest);

	AppletContext getAppletContext();

	void setJSObject(Object window);

}
