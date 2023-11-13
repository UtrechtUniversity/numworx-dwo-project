<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="fi.servlet.lti.DbAccess" %>
<%@ page import="fi.dwo.server.db.CookieWrap" %>
<%!
	private static final String DWO_SAML_ORGANIZATION_ID = "dwoSAMLOrganizationID";
	private static final String DWO_SAML_ORGANIZATION = "dwoSAMLOrganization";    
	private static final String DWO_SAML_USER_ID = "dwoSAMLUserID";
	private static final String DWO_SAML_AUTH_TOKEN = "dwoSAMLAuthToken";
	private DbAccess instance;

	private DbAccess getDbAccess() {
		if(instance == null) {
			instance = new DbAccess(getServletContext());
		}
		return instance;
	}
%>
<%
CookieWrap wrap = new CookieWrap(response);
if ("shibboleth".equals(request.getAuthType())) {
  String schoolid = System.getProperty("ENV_ORGID", "385");
  String organization = getDbAccess().getOrganization(schoolid);
  if ( getDbAccess().setUUSAMLCookie(request, wrap, schoolid, organization))
    return;
  
}
%>   