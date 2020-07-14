<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="fi.servlet.dwomaccess.Subnet" %>
<%@page import="java.util.Base64"%>
<%@ include file='/dwo/toets_util.jsp' %>
<%@ include file='/dwo/saml_util.jsp' %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Safe Exam Browser</title>
</head>
<body onload='loading()' >
<h1>Starten</h1>

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
<a href='sebs://<%=server %>/ho/en/exam/leerling.seb'>Start the safe <strong>exam</strong> environment</a>
	<script> function loading() { } </script>
<%
	} else {
		
		String query = "";
		Cookie[] cookies = wrap.getCookies();
		String u = null, o = null, t = null;
		for(Cookie c : cookies) {
			if (DWO_SAML_ORGANIZATION_ID.equals(c.getName())) o = c.getValue();
			else if (DWO_SAML_USER_ID.equals(c.getName())) u = c.getValue();
			else if (DWO_SAML_AUTH_TOKEN.equals(c.getName())) t = c.getValue();
		}
		if (o != null && u != null && t != null) {
		      t = "3\f" + u + '\f' + o + '\f' + t;
		      query += "?a=" + Base64.getEncoder().encodeToString(t.getBytes());
		}

%>
	  <a id='toets' href='/ho/en/exam/toets.jsp<%=query%>'>Start the safe <strong>exam</strong> environment</a>
	  <script>
	  	function loading() {
	  		document.getElementById('toets').click();
	  	}
	  
	  </script>
<%	  
	}

	if ( needSEB ) {
%>
<h1>Install</h1>
<ul>
	<li><a href='https://www.dwo.nl/downloads/SafeExamBrowserInstaller.exe'>Safe Exam Browser Windows (2.1.7)</a></li>
	<li><a href='https://www.dwo.nl/downloads/SafeExamBrowser-2.1.2.dmg'>Safe Exam Browser MacOs (2.1.2)</a></li>
</ul>
Please note, only these versions work correctly.
<% } %>
</body>
</html>