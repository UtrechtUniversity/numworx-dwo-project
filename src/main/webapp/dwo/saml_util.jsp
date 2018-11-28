<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="fi.servlet.lti.DbAccess" %>
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
if ("shibboleth".equals(request.getAuthType())) {
  String schoolid = System.getProperty("ENV_ORGID", "385");
  if ("entree".equals(schoolid))
  {
    if ( getDbAccess().setEntreeCookie(request, response))
      return;
  } else {
  	String organization = getDbAccess().getOrganization(schoolid);
  	if ( getDbAccess().setUUSAMLCookie(request, response, schoolid, organization))
    	return;
  }
  String extras = "<param name='logoutURL' value='/dwo/saml/logout.jsp' >\n";
  request.setAttribute("extras", extras);
  
}
%>   