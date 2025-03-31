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
<h1>Start</h1>
<p>
<%
	if ( ! Subnet.netMatchRange(IPRANGE, host) ) {
%>
	The device at this address <%=host %> is not allowed for assessments. Use a secured device.
<%	  
	}
	else if ( needSEB ) {
%>
<a href='sebs://<%=server %>/en/exam/<%=leerling%>.seb<%=code%>'>Start the safe <strong>exam</strong> environment</a>
<%
	} else {
%>
	  <a href='/en/exam/toets.jsp<%=id%><%=code%>' target='_top'>Start the safe <strong>exam</strong> environment</a>
<%	  
	}

	if ( needSEB ) {
%>
<h1>Install</h1>
<ul>
	<li><a href='https://cdn.dwo.nl/downloads/SEB_3.1.1.250_SetupBundle.exe'>Safe Exam Browser Windows (3.1.1)</a></li>
	<li><a href='https://cdn.dwo.nl/downloads/SafeExamBrowser-2.2.1.dmg'>Safe Exam Browser MacOs (2.2.1)</a></li>
</ul>
Please note, only these versions work correctly.
<% } %>
</body>
</html>