package fi.dwo.dwojapplet.gui.domainmodel;

import java.net.URL;

import fi.beans.mainframe.AppletContext;
import fi.beans.mainframe.AppletStub;
import fi.dwo.dwojapplet.domain.DwoHelper;

public class DummyStub implements AppletStub {

	@Override
	public boolean isActive() {
		return true;
	}

	@Override
	public URL getDocumentBase() {
		return DwoHelper.getApplet().getDocumentBase();
	}

	@Override
	public URL getCodeBase() {
		return DwoHelper.getApplet().getCodeBase();
	}

	@Override
	public String getParameter(String name) {
		// TODO Auto-generated method stub
		return DwoHelper.getApplet().getParameter(name);
	}

	@Override
	public void appletResize(int width, int height) {
	}

	@Override
	public AppletContext getAppletContext() {
		return DwoHelper.getApplet().getAppletContext();
	}

}
