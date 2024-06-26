<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<% 
	int profile = 112;
    String base = "/wiskunde-actief/";
    String locale = "nl";  
%>
<%@ include file="/dwo/index_util.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
		<meta name="apple-mobile-web-app-capable" content="yes">
		<meta name="viewport" content="width=1078">
        <title>Wiskunde Actief</title>
	   	<link type="text/css" rel="stylesheet" href="/dwo/oauth2client/OAuth2Client.css">
	    <script>
	        token = "/dwo/oauth2/entree";
	    	endpoint = "<%=player%>"
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
