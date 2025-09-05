<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull" %>
<%
	DomDwoProfileFull profileFull = (DomDwoProfileFull)request.getAttribute("template.profile");
	String url = (String) request.getAttribute("template.url");
	Long id = (Long) request.getAttribute("template.profile.id");
	String rights = profileFull.getDwoProfileRights();
%>
<!DOCTYPE html>
<% 
	int profile = id.intValue();
    String base = url;
    String locale = "nl" /*profileFull.getLocale()*/;
    String title = profileFull.getDwoProfileDescription(); // moet een aparte tekst worden
    boolean responsive = rights.contains("w"); // of zo iets. Width
%>
<%@ include file="/dwo/index_util.jsp" %>
<%
    String width = responsive ? "device-width" : "1078";
    if (responsive) {
    	query += "&responsive=true";
    }
%>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
		<meta name="apple-mobile-web-app-capable" content="yes">
		<meta name="mobile-web-app-capable" content="yes">
		<meta name="viewport" content="width=<%=width%>">
        <title><%=title %></title>
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
