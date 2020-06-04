<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="fi.servlet.lti.DbAccess" %>
<%@ page import="fi.dwo.server.db.CookieWrap" %>
<%!
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
  if ("entree".equals(schoolid))
  {
    if ( getDbAccess().setEntreeCookie(request, wrap))
      return;
  } else {
  	String organization = getDbAccess().getOrganization(schoolid);
  	if ( getDbAccess().setUUSAMLCookie(request, wrap, schoolid, organization))
    	return;
  }
  String extras = "<param name='logoutURL' value='/dwo/saml/logout.jsp' >\n";
  request.setAttribute("extras", extras);
  
}
%>   