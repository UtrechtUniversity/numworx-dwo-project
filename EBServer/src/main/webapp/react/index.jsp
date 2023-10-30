<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="fi.dwo.server.db.TStamp" %>   
<%@ page import="fi.dwo.server.db.Util" %>
<%@ include file="/dwo/saml_util.jsp" %>
<!DOCTYPE html>
<% 
	int profile = 114;
	long tstamp = TStamp.BOOT;
	String query = request.getQueryString();
	if(Util.illegal(query))
	 	query = "?base=/react/&profile=" + profile + "&t=" + tstamp;
	else 
	  	query = "?base=/react/&profile=" + profile + "&" + query;
	String hash = request.getParameter("hash");
	String player = "/gwtclient/index.html";

	if (!Util.illegal(hash)) // Deeplink
		player = "/dwo/tablet/DWOplayer.jsp";
	else
		hash = "";
%>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
		<meta name="apple-mobile-web-app-capable" content="yes">
		<meta name="viewport" content="width=1024">
        <title>React Modules</title>
    	<link type="text/css" rel="stylesheet" href="/dwo/oauth2client/OAuth2Client.css">
	    <script>
	        token = "/dwo/oauth2/entree";
	    	endpoint = "/gwtclient/index.html"
	    	search = "<%=query%>"
	    	hash= "<%=hash%>"
	    	clientId = "f9af29c4-cfc5-11ea-87d0-0242ac130003";
	    	<%
				// criterium?
	    		String with = request.getParameter("with");
	    	    String code = request.getParameter("code");
	    		if (with == null && code == null) {
	    	%>
	    		clientId = "";
	    	<% }
	    	%>
	    </script>
     	<script type="text/javascript" src="/dwo/oauth2client/oauth2client/oauth2client.nocache.js"></script>
    </head>
    <body>
     </body>
</html>
