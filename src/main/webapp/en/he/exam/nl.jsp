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
<h1>Starten</h1>

<p>
<%
	String host = request.getRemoteAddr();
	String server = request.getHeader("host");
	if ( ! Subnet.netMatchRange(IPRANGE, host) ) {
%>
	Het apparaat op dit adres <%=host %> is niet toegestaan voor toetsen. Gebruik een beveiligd apparaat.
<%	  
	}
	else if ( needSEB ) {
%>
<a href='sebs://<%=server%>/ho/exam/leerling.seb'>Start de beveiligde <strong>exam</strong> omgeving</a>
<%
	} else {
%>
    <script>
    	endpoint = "/ho/en/exam/toets_nl.jsp"
    	search = ""
    	hash= ""
        clientId = "f9af29c4-cfc5-11ea-87d0-0242ac130003"
        token="/dwo/saml/uulogin"
    </script>
    <script type="text/javascript" src="/dwo/oauth2client/oauth2client/oauth2client.nocache.js"></script>
<%	  
	}

	if ( needSEB ) {
%>
<h1>Installeren</h1>
<ul>
	<li><a href='https://www.dwo.nl/downloads/SEB_3.0.1.163_SetupBundle.exe'>Safe Exam Browser Windows (3.0.1)</a></li>
	<li><a href='https://www.dwo.nl/downloads/SafeExamBrowser-2.1.2.dmg'>Safe Exam Browser MacOs (2.1.2)</a></li>
</ul>
Let op, alleen deze versies werken correct.
<% } %>
</body>
</html>