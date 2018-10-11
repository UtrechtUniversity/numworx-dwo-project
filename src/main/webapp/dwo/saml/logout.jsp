<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%
	String DWO_SAML_USER_ID = "dwoSAMLUserID";
	String DWO_SAML_AUTH_TOKEN = "dwoSAMLAuthToken";
	Cookie user = new Cookie(DWO_SAML_USER_ID, null);
	user.setMaxAge(0);
	user.setPath("/");
	response.addCookie(user);
	Cookie token = new Cookie(DWO_SAML_AUTH_TOKEN, null);
	token.setMaxAge(0);
	token.setPath("/");
	response.addCookie(token);
	
%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Logout</title>
</head>
<body>
<a id='logout' href="/Shibboleth.sso/Logout?return=/" target="_parent" >Main menu</a>
</body>
</html>