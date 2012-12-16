package fi.dwo.client.gui.action;

import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;

import fi.dwo.client.domain.DwoHelper;

public class LogoutURLAction extends LogoutAction {

	/**
	 * @param url
	 */
	public LogoutURLAction(String url) {
		super();
		this.url = url;
	}

	String url;
		
	public void actionPerformed(ActionEvent arg0) {
		super.actionPerformed(arg0);
		URL u = DwoHelper.getApplet().getDocumentBase();
		try {
			u = new URL(u, url);
		} catch (MalformedURLException e) {
		}
		DwoHelper.getApplet().getAppletContext().showDocument(u);
		
	}

}
