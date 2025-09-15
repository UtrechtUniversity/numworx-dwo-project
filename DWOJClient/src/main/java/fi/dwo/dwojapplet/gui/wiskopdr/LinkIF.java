package fi.dwo.dwojapplet.gui.wiskopdr;

import fi.beans.mainframe.AppletContext;

public interface LinkIF {

	Object getJSObject();

	boolean gotoScoNr(String rest);

	AppletContext getAppletContext();

	void setJSObject(Object window);

}
