package fi.dwo.dwojapplet.gui.action;

import fi.dwo.dwojapplet.domain.DwoHelper;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;

public class LogoutURLAction extends LogoutAction {

    /**
     * @param url
     */
    public LogoutURLAction(String url) {
        super();
        this.url = url;
    }

    String url;

    @Override
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
