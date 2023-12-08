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
	String shib = (String) request.getAttribute("Shib-Handler");
	
%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link type="text/css" rel="stylesheet" href="/uuindex.css">
<title>Logout</title>
</head>
<body>
<h1>Uitloggen</h1>
<ul>
	<li>
		<a class='logout' href="<%=shib%>/Logout?return=/" target="_parent" >Naar het hoofdmenu, sessie afsluiten</a>
	</li>
	<li>
		<a class='nologout' href="/" target="_parent" >Naar het hoofdmenu zonder sessie af te sluiten</a>
	</li>
</ul>

<h1>Logout</h1>
<ul>
	<li>
		<a class='logout' href="<%=shib%>/Logout?return=/" target="_parent" >To the main screen, ending your session </a>
	</li>
	<li>
		<a class='nologout' href="/" target="_parent" >To the main screen, keeping your session</a>
	</li>
</ul>
</body>
</html>