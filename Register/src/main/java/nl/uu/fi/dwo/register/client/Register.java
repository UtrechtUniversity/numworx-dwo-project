package nl.uu.fi.dwo.register.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

import fi.dwo.gwt.lib.rest.util.Dwo2ExceptionGWTTranslator;
import nl.uu.fi.dwo.rest.dom.entities.DomSamlUser;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

import static nl.uu.fi.dwo.register.client.RegisterPanel.getCookieOnce;

public class Register implements EntryPoint, Command {

	static final String DWO_SAML_ORGANIZATION_ID = "dwoSAMLOrganizationID";
	static final String DWO_SAML_USER_ID = "dwoSAMLUserID";
	
	private RegisterPanel content;
	private String newURL = "/", cancelURL;
	
	private static native boolean getFree() /*-{
	  return $wnd.free
    }-*/;

	private static native boolean getSAML() /*-{
		return "saml" == $wnd.dwo_env
	}-*/;
	
	
	
	public void onModuleLoad() {
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionGWTTranslator());
        String next = Window.Location.getParameter("next");
        if (next == null) {
        	next = getCookieOnce("next");
        }
        String cancel = Window.Location.getParameter("cancel");
        if (cancel == null) {
        	cancel = getCookieOnce("cancel");
        }
        if(next != null)
        	newURL = next;
        if (cancel != null) {
        	cancelURL = cancel;
        } else
        	cancelURL = newURL;
        
        boolean free = getFree();
		String user_id = getCookieOnce(DWO_SAML_USER_ID);
		String org_id = getCookieOnce(DWO_SAML_ORGANIZATION_ID);
        boolean saml = getSAML();
        
        saml = saml || user_id != null && org_id != null;
        
		content = new RegisterPanel(free, saml);
		RegisterController controller = content.getController();
		controller.setNext(this);
		controller.setCancel(new Command() {

			@Override
			public void execute() {
				Window.Location.assign(cancelURL);
				
			}});
		if (saml && user_id != null && org_id != null) {	
			DomSamlUser samlUser = new DomSamlUser();
			samlUser.setSamlOrgId(org_id);
			samlUser.setSamlUserId(user_id);
			controller.setSamlUser(samlUser);
			controller.setSchoolClass(content.getSchoolClass());
		}
		
		RootPanel.get().add(content);
		
	}


	@Override
	public void execute() {
		Window.Location.assign(newURL);
	}

	
}
