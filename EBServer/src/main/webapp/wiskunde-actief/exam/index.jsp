<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="fi.servlet.dwomaccess.Subnet" %>
<%@ include file='/dwo/toets_util.jsp' %>
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
	String host = request.getRemoteAddr();
	String server = request.getHeader("host");
	  String leerling = request.getParameter("id");
	  String id = "";
	  try { 
	    id = "?id=" + Long.parseLong(leerling);
	  } catch(Exception e) {
	 	leerling = "leerling";
	  }
	if ( ! Subnet.netMatchRange(IPRANGE, host) ) {
%>
	Het apparaat op dit adres <%=host %> is niet toegestaan voor toetsen. Gebruik een beveiligd apparaat.
<%	  
	}
	else if ( needSEB ) {
%>
<a href='sebs://<%=server %>/wiskunde-actief/exam/<%=leerling %>.seb'>Start de beveiligde <strong>toets</strong> omgeving</a>
<%    if (System.getProperty("DWO_ENV", "").contains("test")) { %>
<br>SEB QR Code: <img 
	style='vertical-align: middle'
	src='/dwo/qrcode.png?qr=sebs://<%=server %>/wiskunde-actief/exam/<%=leerling %>.seb' >
<%    }
	} else {
%>
	  <a target='_top' href='/wiskunde-actief/exam/toets.jsp<%=id%>'>Start de beveiligde <strong>exam</strong> omgeving</a>
<%	  
	}

	if ( needSEB ) {
%>

<h1>Installeren</h1>
<ul>
	<li><a href='https://cdn.dwo.nl/downloads/SEB_3.1.1.250_SetupBundle.exe'>Safe Exam Browser Windows (3.1.1)</a></li>
	<li><a href='https://cdn.dwo.nl/downloads/SafeExamBrowser-2.2.1.dmg'>Safe Exam Browser MacOs (2.2.1)</a></li>
</ul>
Let op, alleen deze versies werken correct.
<% } %>
</body>
</html>