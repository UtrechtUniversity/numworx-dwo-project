<%@page import="java.util.logging.Logger"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="fi.servlet.dwomaccess.Subnet" %>
<%@ page import="java.util.*" %>
<%@ include file="/dwo/saml_util.jsp" %>
<%@ include file='/dwo/toets_util.jsp' %>
<!doctype html>
<!-- The DOCTYPE declaration above will set the     -->
<!-- browser's rendering engine into                -->
<!-- "Standards Mode". Replacing this declaration   -->
<!-- with a "Quirks Mode" doctype is not supported. -->
<%
	String requestHash = request.getHeader("X-SafeExamBrowser-RequestHash");
	String configHash  = request.getHeader("X-SafeExamBrowser-ConfigKeyHash");
	String host = request.getRemoteAddr();
	String server = request.getHeader("host");

	if(requestHash == null && needSEB ||  !Subnet.netMatchRange(IPRANGE, host) )
	{
		Logger.getLogger("toets.jsp").severe(request.getRequestURL() +  " host = " + host);
		response.sendError(HttpServletResponse.SC_NOT_FOUND);
		return;
	}
	boolean failed = true;
	String[] hashes = { "ccbb7f46b416704eeecfd7cb96c0a51c517c311ca232150ec4411968795053f3", // mac 2.1.2
						"6d2b53bcb6cae8826b8c5fd71afeb97c2b8c4e7f5d75526ca17066b96461c904",  // win 2.1.7
						System.getProperty("SEB_TOETS_MAC", ""),
						System.getProperty("SEB_TOETS_WIN", "")
	
	};
	for(String hash : hashes) {
		StringTokenizer st = new StringTokenizer(hash, ",");
		while(st.hasMoreTokens()) {
			hash = st.nextToken();
			if(hash.equals(requestHash)|| hash.equals(configHash)) failed = false;
		}
	}
	String id = request.getParameter("id");
	if(failed && needSEB && id == null)
	{
		Logger.getLogger("toets.jsp").severe(request.getRequestURL() +" hash = " + requestHash + " " + configHash);
		response.sendError(HttpServletResponse.SC_FORBIDDEN);
		return;
	}
	String dwo_env = System.getProperty("DWO_ENV", "app");
	String player = "/wiskunde-actief/exam/toetsinner.jsp";
	String query = "?id=" + id;
	String hash = "";
%>
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
	   	<link type="text/css" rel="stylesheet" href="/dwo/oauth2client/OAuth2Client.css">
	    <script>
	        token = "/dwo/oauth2/entree";
	    	endpoint = "<%=player%>"
	    	search = "<%=query%>"
	    	hash= "<%=hash%>"
	    	clientId = "f9af29c4-cfc5-11ea-87d0-0242ac130003";
	    	<%
				// criterium?
	    		String with = request.getParameter("with"); with = "entree";
	    	    String code = request.getParameter("code");
	    		if (with == null && code == null) {
	    	%>
	    		clientId = "";
	    	<% }
	    	%>
	    </script>
     	<script type="text/javascript" src="/dwo/oauth2client/oauth2client/oauth2client.nocache.js"></script>
  </head>
  <body id="main">
  </body>
</html>
