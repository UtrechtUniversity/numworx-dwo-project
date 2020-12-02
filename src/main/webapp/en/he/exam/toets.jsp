<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="fi.servlet.dwomaccess.Subnet" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.logging.*" %>
<%@ include file='/dwo/toets_util.jsp' %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Safe Exam Browser</title>
    <link type="text/css" rel="stylesheet" href="/dwo/oauth2client/OAuth2Client.css">
</head>
<body>
<%
	String requestHash = request.getHeader("X-SafeExamBrowser-RequestHash");
	String host = request.getRemoteAddr();
	String server = request.getHeader("host");
	if ( ! Subnet.netMatchRange(IPRANGE, host) ) {
		Logger.getLogger("toets.jsp").severe(request.getRequestURL() +  " wrong host = " + host);
		response.sendError(HttpServletResponse.SC_NOT_FOUND);
		return;
	}
	else if ( requestHash == null ) {
		Logger.getLogger("toets.jsp").severe(request.getRequestURL() +  " no seb host = " + host);
		response.sendError(HttpServletResponse.SC_NOT_FOUND);
		return;
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
%>
</body>
</html>