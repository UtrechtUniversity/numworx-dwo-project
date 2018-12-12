package nl.uu.fi.dwo.register.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

import fi.dwo.gwt.lib.rest.util.Dwo2ExceptionGWTTranslator;
import nl.uu.fi.dwo.rest.dom.entities.DomSamlUser;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

public class Register implements EntryPoint, Command {

	static final String DWO_SAML_ORGANIZATION_ID = "dwoSAMLOrganizationID";
	static final String DWO_SAML_USER_ID = "dwoSAMLUserID";
	
	private RegisterPanel content;
	private String newURL = "/";
	
	private static native boolean getFree() /*-{
	  return $wnd.free
    }-*/;

	public void onModuleLoad() {
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionGWTTranslator());
        String next = Window.Location.getParameter("next");
        if(next != null)
        	newURL = next;
        boolean free = getFree();
		content = new RegisterPanel(free);
		RegisterController controller = content.getController();
		controller.setNext(this);
		String user_id = Cookies.getCookie(DWO_SAML_USER_ID);
		String org_id = Cookies.getCookie(DWO_SAML_ORGANIZATION_ID);
		if (user_id != null && org_id != null) {	
			DomSamlUser samlUser = new DomSamlUser();
			samlUser.setSamlOrgId(org_id);
			samlUser.setSamlUserId(user_id);
			controller.setSamlUser(samlUser);
		}
		
		RootPanel.get().add(content);
		
	}


	@Override
	public void execute() {
		Window.Location.assign(newURL);
	}

}
