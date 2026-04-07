<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="fi.servlet.dwomaccess.Subnet" %>
<%@ include file='/dwo/toets_util2.jsp' %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Safe Exam Browser</title>
</head>
<body>
<h1>Starten</h1>

<p>
<%
	if ( ! Subnet.netMatchRange(IPRANGE, host) ) {
%>
	Het apparaat op dit adres <%=host %> is niet toegestaan voor toetsen. Gebruik een beveiligd apparaat.
<%	  
	}
	else if ( needSEB ) {
%>
<a href='sebs://<%=server%>/react/exam/<%=leerling %>.seb<%=code%>'>Start de beveiligde <strong>exam</strong> omgeving</a>
<%
	} else {
%>
	  <a href='toets.jsp<%=id%><%=code%>' target="_top">Start de beveiligde <strong>exam</strong> omgeving</a>
<%	  
	}

	if ( needSEB ) {
%>
<h1>Installeren</h1>
<ul>
	<li><a href='https://cdn.dwo.nl/downloads/SEB_3.10.1.864_SetupBundle.exe'>Safe Exam Browser Windows (3.10.1)</a></li>
	<li><a href='https://cdn.dwo.nl/downloads/SafeExamBrowser-2.2.1.dmg'>Safe Exam Browser MacOs (2.2.1)</a></li>
</ul>
Let op, alleen deze versies werken correct.
<% } %>
</body>
</html>