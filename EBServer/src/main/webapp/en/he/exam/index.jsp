<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="fi.servlet.dwomaccess.Subnet" %>
<%@ include file='/dwo/toets_util.jsp' %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Safe Exam Browser</title>
    <link type="text/css" rel="stylesheet" href="/dwo/oauth2client/OAuth2Client.css">
</head>
<body>
<h1>Start</h1>

<p>
<%
	String host = request.getRemoteAddr();
	String server = request.getHeader("host");
	if ( ! Subnet.netMatchRange(IPRANGE, host) ) {
%>
	The device at this address <%=host %> is not allowed for assessments. Use a secured device.
<%	  
	}
	else if ( needSEB ) {
%>
<a href='sebs://<%=server%>/en/he/exam/leerling.seb'>Start de beveiligde <strong>exam</strong> omgeving</a>
<%
	} else {
String clientId = "";
if ("shibboleth".equals(request.getAuthType())) clientId = "f9af29c4-cfc5-11ea-87d0-0242ac130003";
%>
    <script>
    	endpoint = "/ho/en/exam/toets.jsp"
    	search = ""
    	hash= ""
        clientId = "<%=clientId%>"
        token="/dwo/saml/uulogin"
    </script>
    <script type="text/javascript" src="/dwo/oauth2client/oauth2client/oauth2client.nocache.js"></script>
<%	  
	}

	if ( needSEB ) {
%>
<h1>Installeren</h1>
<ul>
	<li><a href='https://cdn.dwo.nl/downloads/SEB_3.1.1.250_SetupBundle.exe'>Safe Exam Browser Windows (3.1.1)</a></li>
	<li><a href='https://cdn.dwo.nl/downloads/SafeExamBrowser-2.2.1.dmg'>Safe Exam Browser MacOs (2.2.1)</a></li>
</ul>
Please note, only these versions work correctly.
<% } %>
</body>
</html>