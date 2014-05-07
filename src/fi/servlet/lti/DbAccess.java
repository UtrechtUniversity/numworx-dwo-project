package fi.servlet.lti;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DbAccess {
    private static final String DWO_SAML_ORGANIZATION_ID = "dwoSAMLOrganizationID";
    private static final String DWO_SAML_ORGANIZATION = "dwoSAMLOrganization";    
	private static final String DWO_SAML_USER_ID = "dwoSAMLUserID";
	
	public void setCookie(HttpServletRequest request, HttpServletResponse response) {
// persoonsgegevens:
		String user_id = request.getParameter("user_id");
		String context_id = request.getParameter("context_id");
	    String oauth_consumer_key = request.getParameter("oauth_consumer_key");
		
		String name_full = request.getParameter("lis_person_name_full");
		String name_given = request.getParameter("lis_person_name_given");
		String name_family = request.getParameter("lis_person_name_family");
		String email = request.getParameter("lis_person_contact_email_primary");
	    String organization = oauth_consumer_key;
	    
	    organization = request.getParameter("tool_consumer_instance_description");
	    if(organization == null)organization = request.getParameter("context_title");
	    if(organization == null)organization = request.getParameter("context_label");
	    if(organization == null)organization = request.getParameter("resource_link_title");
	    if(organization == null)organization = oauth_consumer_key;
	    
		Cookie user  = new Cookie(DWO_SAML_USER_ID, user_id);
		Cookie orgid = new Cookie(DWO_SAML_ORGANIZATION_ID, oauth_consumer_key);
		Cookie org   = new Cookie(DWO_SAML_ORGANIZATION, organization);
		response.addCookie(user);
		response.addCookie(orgid);
		response.addCookie(org);
	}
	
}
