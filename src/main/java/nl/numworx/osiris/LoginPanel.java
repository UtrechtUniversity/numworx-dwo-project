package nl.numworx.osiris;

import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.JPanel;

import org.osgi.util.promise.Promise;

import nl.numworx.samllogin.SamlLoginPanel;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.PublicUserManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public class LoginPanel extends JPanel {

	private static final String DWO_SAML_ORGANIZATION_ID = "dwoSAMLOrganizationID";
    private static final String DWO_SAML_USER_ID = "dwoSAMLUserID";

	Main main;
	SamlLoginPanel panel;
	Promise<DomUserFullwLoginContext> complete;
	URL base;
	
	public LoginPanel(Main main, String url, URL base) {
		this.main = main;
		panel = new SamlLoginPanel(url);
		add(panel);
		complete = panel.getPromise().map(this::toLogin);
		this.base = base;
	}

	public DomUserFullwLoginContext toLogin(Properties p) {
		StoredRestManager.getInstance().setBasicAuthString(null, null, null);
		StoredRestManager.getInstance().getAuthenticator().setServerUrlPath(base);
	    String samlUserID = p.getProperty(DWO_SAML_USER_ID);
	    String samlOrgID = p.getProperty(DWO_SAML_ORGANIZATION_ID);
	    String authToken = p.getProperty("dwoSAMLAuthToken");
	       try {
			DomUserFullwLoginContext user = PublicUserManager.samlLogin(samlUserID, samlOrgID, authToken);
			return user;
	       } catch (Dwo2Exception e) {
			throw new IllegalArgumentException(e);
		}
		
	}

}
